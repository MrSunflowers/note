package com.atguigu.gulimall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.constant.order.OrderConstant;
import com.atguigu.common.constant.ware.WareOrderTaskConstant;
import com.atguigu.common.exception.NoStockException;
import com.atguigu.common.to.mq.StockDetailTO;
import com.atguigu.common.to.mq.StockLockedTO;
import com.atguigu.common.to.order.OrderTO;
import com.atguigu.common.to.product.SkuInfoTO;
import com.atguigu.common.to.ware.SkuHasStockTO;
import com.atguigu.common.to.ware.WareSkuLockTO;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.common.utils.R;
import com.atguigu.common.vo.ware.OrderItemVO;
import com.atguigu.gulimall.ware.agent.ProductAgentService;
import com.atguigu.gulimall.ware.dao.WareSkuDao;
import com.atguigu.gulimall.ware.entity.WareOrderTaskDetailEntity;
import com.atguigu.gulimall.ware.entity.WareOrderTaskEntity;
import com.atguigu.gulimall.ware.entity.WareSkuEntity;
import com.atguigu.gulimall.ware.feign.OrderFeignService;
import com.atguigu.gulimall.ware.service.WareSkuService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    ProductAgentService productAgentService;
    @Autowired
    WareOrderTaskServiceImpl orderTaskService;
    @Autowired
    WareOrderTaskDetailServiceImpl orderTaskDetailService;
    @Autowired
    OrderFeignService orderFeignService;
    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 查询sku库存（模糊条件：skuId、wareId）
     */
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        /**
         * skuId:1
         * wareId:2
         */
        QueryWrapper<WareSkuEntity> queryWrapper = new QueryWrapper<>();
        String skuId = (String) params.get("skuId");
        if (!StringUtils.isEmpty(skuId)) {
            queryWrapper.eq("sku_id", skuId);
        }

        String wareId = (String) params.get("wareId");
        if (!StringUtils.isEmpty(wareId)) {
            queryWrapper.eq("ware_id", wareId);
        }


        IPage<WareSkuEntity> page = this.page(new Query<WareSkuEntity>().getPage(params), queryWrapper);

        return new PageUtils(page);
    }

    /**
     * 库存入库
     */
    @Transactional
    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        Integer count = baseMapper.selectCount(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
        if (count > 0) {
            // 当前仓库有当前商品库存，更新库存
            baseMapper.addStock(skuId, wareId, skuNum);
        } else {
            // 当前仓库无当前商品库存，新增记录
            WareSkuEntity wareSku = new WareSkuEntity();
            wareSku.setSkuId(skuId);
            wareSku.setWareId(wareId);
            wareSku.setStock(skuNum);
            wareSku.setStockLocked(0);
            // 冗余字段
            SkuInfoTO skuInfoTO = productAgentService.info(skuId);
            if (skuInfoTO != null) {
                wareSku.setSkuName(skuInfoTO.getSkuName());
            }
            baseMapper.insert(wareSku);
        }
    }

    /**
     * 查询sku是否有库存
     */
    @Override
    public List<SkuHasStockTO> getSkusHasStock(List<Long> skuIds) {
        return skuIds.stream().map(skuId -> {
            SkuHasStockTO stock = new SkuHasStockTO();
            // 查询当前sku总库存量
            Long count = baseMapper.getSkusStock(skuId);
            stock.setSkuId(skuId);
            stock.setHasStock(count == null ? false : count > 0);
            return stock;
        }).collect(Collectors.toList());
    }

    /**
     * 库存锁定，sql执行锁定锁定
     *
     * @param lockTO
     * @return 锁定结果
     * @Transactional(rollbackFor = NoStockException.class)：指定的异常出现会导致回滚
     * 未指定异常，任何运行时异常都会导致回滚，可以省略rollbackFor
     */
    @Transactional
    @Override
    public Boolean orderLockStock(WareSkuLockTO lockTO) {
        // 按照收货地址找到就近仓库，锁定库存（暂未实现）
        // 采用方案：获取每项商品在哪些仓库有库存，轮询尝试锁定，任一商品锁定失败回滚

        // 1.往库存工作单存储当前锁定（本地事务表）
        WareOrderTaskEntity taskEntity = new WareOrderTaskEntity();
        taskEntity.setOrderSn(lockTO.getOrderSn());
        orderTaskService.save(taskEntity);

        // 2.封装待锁定库存项Map
        Map<Long, OrderItemVO> lockItemMap = lockTO.getLocks().stream().collect(Collectors.toMap(key -> key.getSkuId(), val -> val));
        // 3.查询（库存 - 库存锁定 >= 待锁定库存数）的仓库
        List<WareSkuEntity> wareEntities = baseMapper.selectListHasSkuStock(lockItemMap.keySet()).stream().filter(entity -> entity.getStock() - entity.getStockLocked() >= lockItemMap.get(entity.getSkuId()).getCount()).collect(Collectors.toList());
        // 判断是否查询到仓库
        if (CollectionUtils.isEmpty(wareEntities)) {
            // 匹配失败，所有商品项没有库存
            Set<Long> skuIds = lockItemMap.keySet();
            throw new NoStockException(skuIds);
        }
        // 将查询出的仓库数据封装成Map，key:skuId  val:wareId
        Map<Long, List<WareSkuEntity>> wareMap = wareEntities.stream().collect(Collectors.groupingBy(key -> key.getSkuId()));
        // 4.判断是否为每一个商品项至少匹配了一个仓库
        List<WareOrderTaskDetailEntity> taskDetails = new ArrayList<>();// 库存锁定工作单详情
        Map<Long, StockLockedTO> lockedMessageMap = new HashMap<>();// 库存锁定工作单消息
        if (wareMap.size() < lockTO.getLocks().size()) {
            // 匹配失败，部分商品没有库存
            Set<Long> skuIds = lockItemMap.keySet();
            skuIds.removeAll(wareMap.keySet());// 求商品项差集
            throw new NoStockException(skuIds);
        } else {
            // 所有商品都存在有库存的仓库
            // 5.锁定库存
            for (Map.Entry<Long, List<WareSkuEntity>> entry : wareMap.entrySet()) {
                Boolean skuStocked = false;
                Long skuId = entry.getKey();// 商品
                OrderItemVO item = lockItemMap.get(skuId);
                Integer count = item.getCount();// 待锁定个数
                List<WareSkuEntity> hasStockWares = entry.getValue();// 有足够库存的仓库
                for (WareSkuEntity ware : hasStockWares) {
                    Long num = baseMapper.lockSkuStock(skuId, ware.getWareId(), count);
                    if (num == 1) {
                        // 锁定成功，跳出循环
                        skuStocked = true;
                        // 创建库存锁定工作单详情（每一件商品锁定详情）
                        WareOrderTaskDetailEntity taskDetail = new WareOrderTaskDetailEntity(null, skuId,
                                item.getTitle(), count, taskEntity.getId(), ware.getWareId(),
                                WareOrderTaskConstant.LockStatusEnum.LOCKED.getCode());
                        taskDetails.add(taskDetail);
                        // 创建库存锁定工作单消息（每一件商品一条消息）
                        StockDetailTO detailMessage = new StockDetailTO();
                        BeanUtils.copyProperties(taskDetail, detailMessage);
                        StockLockedTO lockedMessage = new StockLockedTO(taskEntity.getId(), detailMessage);
                        lockedMessageMap.put(skuId, lockedMessage);
                        break;
                    }
                }
                if (!skuStocked) {
                    // 匹配失败，当前商品所有仓库都未锁定成功
                    throw new NoStockException(skuId);
                }
            }
        }

        // 6.往库存工作单详情存储当前锁定（本地事务表）
        orderTaskDetailService.saveBatch(taskDetails);

        // 7.发送消息
        for (WareOrderTaskDetailEntity taskDetail : taskDetails) {
            StockLockedTO message = lockedMessageMap.get(taskDetail.getSkuId());
            message.getDetail().setId(taskDetail.getId());// 存储库存详情ID
            rabbitTemplate.convertAndSend("stock-event-exchange", "stock.locked", message);
        }
        return true;
    }

    /**
     * 库存解锁
     */
    @Override
    public void unLockStock(StockLockedTO locked) throws Exception {
        StockDetailTO taskDetailTO = locked.getDetail();// 库存工作单详情TO
        WareOrderTaskDetailEntity taskDetail = orderTaskDetailService.getById(taskDetailTO.getId());// 库存工作单详情Entity
        if (taskDetail != null) {
            // 1.工作单未回滚，需要解锁
            WareOrderTaskEntity task = orderTaskService.getById(locked.getId());// 库存工作单Entity
            R r = orderFeignService.getOrderByOrderSn(task.getOrderSn());// 订单Entity
            if (r.getCode() == 0) {
                // 订单数据返回成功
                OrderTO order = r.getData(new TypeReference<OrderTO>() {
                });
                if (order == null || OrderConstant.OrderStatusEnum.CANCLED.getCode().equals(order.getStatus())) {
                    // 2.订单已回滚 || 订单未回滚已取消状态
                    if (WareOrderTaskConstant.LockStatusEnum.LOCKED.getCode().equals(taskDetail.getLockStatus())) {
                        // 订单已锁定状态，需要解锁（消息确认）
                        unLockStock(taskDetailTO.getSkuId(), taskDetailTO.getWareId(), taskDetailTO.getSkuNum(), taskDetailTO.getId());
                    } else {
                        // 订单其他状态，不可解锁（消息确认）
                    }
                }
            } else {
                // 订单远程调用失败（消息重新入队）
                throw new Exception();
            }
        } else {
            // 3.无库存锁定工作单记录，已回滚，无需解锁（消息确认）
        }
    }

    /**
     * 库存解锁
     * 订单解锁触发，防止库存解锁消息优先于订单解锁消息到期，导致库存无法解锁
     */
    @Transactional
    @Override
    public void unLockStock(OrderTO order) {
        String orderSn = order.getOrderSn();// 订单号
        // 1.根据订单号查询库存锁定工作单
        WareOrderTaskEntity task = orderTaskService.getOrderTaskByOrderSn(orderSn);
        // 2.按照工作单查询未解锁的库存，进行解锁
        List<WareOrderTaskDetailEntity> taskDetails = orderTaskDetailService.list(new QueryWrapper<WareOrderTaskDetailEntity>()
                .eq("task_id", task.getId())
                .eq("lock_status", WareOrderTaskConstant.LockStatusEnum.LOCKED.getCode()));// 并发问题
        // 3.解锁库存
        for (WareOrderTaskDetailEntity taskDetail : taskDetails) {
            unLockStock(taskDetail.getSkuId(), taskDetail.getWareId(), taskDetail.getSkuNum(), taskDetail.getId());
        }
    }

    /**
     * 库存解锁
     * 1.sql执行释放锁定
     * 2.更新库存工作单状态为已解锁
     *
     * @param skuId
     * @param wareId
     * @param count
     */
    public void unLockStock(Long skuId, Long wareId, Integer count, Long taskDetailId) {
        // 1.库存解锁
        baseMapper.unLockStock(skuId, wareId, count);

        // 2.更新工作单的状态 已解锁
        WareOrderTaskDetailEntity taskDetail = new WareOrderTaskDetailEntity();
        taskDetail.setId(taskDetailId);
        taskDetail.setLockStatus(WareOrderTaskConstant.LockStatusEnum.UNLOCKED.getCode());
        orderTaskDetailService.updateById(taskDetail);
    }
}