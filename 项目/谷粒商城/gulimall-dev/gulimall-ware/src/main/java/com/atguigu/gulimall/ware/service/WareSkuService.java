package com.atguigu.gulimall.ware.service;

import com.atguigu.common.to.mq.StockLockedTO;
import com.atguigu.common.to.order.OrderTO;
import com.atguigu.common.to.ware.SkuHasStockTO;
import com.atguigu.common.to.ware.WareSkuLockTO;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author wanzenghui
 * @email lemon_wan@aliyun.com
 * @date 2021-09-02 22:59:35
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    /**
     * 查询sku库存（模糊条件：skuId、wareId）
     */
    PageUtils queryPage(Map<String, Object> params);

    /**
     * 采购成功，库存需求sku入库
     * @param skuId  商品ID
     * @param wareId 仓库ID
     * @param skuNum 商品数量
     */
    void addStock(Long skuId, Long wareId, Integer skuNum);

    /**
     * 查询sku是否有库存
     */
    List<SkuHasStockTO> getSkusHasStock(List<Long> skuIds);

    /**
     * 库存锁定
     * sql执行增加锁定
     */
    Boolean orderLockStock(WareSkuLockTO lockTO);

    /**
     * 库存解锁
     * 供监听死信队列方法调用
     */
    void unLockStock(StockLockedTO locked) throws Exception;

    /**
     * 库存解锁
     * 订单解锁触发
     */
    void unLockStock(OrderTO order);
}

