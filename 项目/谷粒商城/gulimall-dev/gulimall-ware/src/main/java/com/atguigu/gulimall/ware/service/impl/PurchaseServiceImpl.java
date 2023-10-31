package com.atguigu.gulimall.ware.service.impl;

import com.atguigu.common.constant.ware.PurchaseConstant;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.common.vo.ware.MergeVO;
import com.atguigu.common.vo.ware.PurchaseDoneVO;
import com.atguigu.common.vo.ware.PurchaseItemDoneVO;
import com.atguigu.gulimall.ware.dao.PurchaseDao;
import com.atguigu.gulimall.ware.entity.PurchaseDetailEntity;
import com.atguigu.gulimall.ware.entity.PurchaseEntity;
import com.atguigu.gulimall.ware.service.PurchaseDetailService;
import com.atguigu.gulimall.ware.service.PurchaseService;
import com.atguigu.gulimall.ware.service.WareSkuService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    PurchaseDetailService purchaseDetailService;
    @Autowired
    WareSkuService wareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 查询未领取的采购单
     */
    @Override
    public PageUtils queryPageUnreceive(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>().eq("status", PurchaseConstant.PurchaseStatusEnum.CREATED.getCode()).
                        or().eq("status", PurchaseConstant.PurchaseStatusEnum.ASSIGNED.getCode())
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void mergePurchase(MergeVO mergeVo) {
        Long purchaseId = mergeVo.getPurchaseId();
        if (StringUtils.isEmpty(purchaseId)) {
            // 新增采购单
            PurchaseEntity purchase = new PurchaseEntity();
            purchase.setStatus(PurchaseConstant.PurchaseStatusEnum.CREATED.getCode());
            Date now = new Date();
            purchase.setCreateTime(now);
            purchase.setUpdateTime(now);
            save(purchase);// 保存
            purchaseId = purchase.getId();
        } else {
            // 采购单已存在
            // TODO 校验采购单状态 新建或已分配

        }
        // 整合采购需求
        List<Long> items = mergeVo.getItems();
        Long finalPurchaseId = purchaseId;
        List<PurchaseDetailEntity> collect = items.stream().map(id -> {
            PurchaseDetailEntity detail = new PurchaseDetailEntity();
            detail.setId(id);
            detail.setPurchaseId(finalPurchaseId);
            detail.setStatus(PurchaseConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());// 分配至采购单
            return detail;
        }).collect(Collectors.toList());
        purchaseDetailService.updateBatchById(collect);

        // 修改更新时间
        PurchaseEntity purchase = new PurchaseEntity();
        purchase.setId(purchaseId);
        purchase.setUpdateTime(new Date());
        updateById(purchase);

    }

    /**
     * 领取采购单
     * @param ids 采购单ID
     */
    @Transactional
    @Override
    public void received(List<Long> ids) {
        // 校验：新建或已分配状态(未领取)
        List<PurchaseEntity> collect = baseMapper.selectBatchIds(ids).stream(
        ).filter(purchase -> PurchaseConstant.PurchaseStatusEnum.CREATED.getCode() == purchase.getStatus() ||
                PurchaseConstant.PurchaseStatusEnum.ASSIGNED.getCode() == purchase.getStatus()
        ).map(purchase -> {
            // 修改采购单状态，已领取
            purchase.setStatus(PurchaseConstant.PurchaseStatusEnum.RECEIVE.getCode());
            return purchase;
        }).collect(Collectors.toList());
        updateBatchById(collect);

        // 修改采购需求的状态
        // 查询采购需求
        List<PurchaseDetailEntity> details = new ArrayList<>();
        collect.forEach(purchase -> {
            List<PurchaseDetailEntity> entities = purchaseDetailService.listDetailByPurchaseId(purchase.getId());
            details.addAll(entities);
        });
        List<PurchaseDetailEntity> detailEntities = details.stream().map(detail -> {
            PurchaseDetailEntity entity = new PurchaseDetailEntity();
            entity.setId(detail.getId());
            entity.setStatus(PurchaseConstant.PurchaseDetailStatusEnum.BUYING.getCode());
            return entity;
        }).collect(Collectors.toList());
        purchaseDetailService.updateBatchById(detailEntities);
    }

    /**
     * 完成采购，入库
     */
    @Transactional
    @Override
    public void done(PurchaseDoneVO doneVo) {
        // 1.改变采购需求状态
        boolean flag = true;// 采购单状态标记
        List<PurchaseDetailEntity> purchaseDetails = new ArrayList<>();
        List<PurchaseDetailEntity> finishPurchaseDetails = new ArrayList<>();
        for (PurchaseItemDoneVO item : doneVo.getItems()) {
            PurchaseDetailEntity purchaseDetail = new PurchaseDetailEntity();
            purchaseDetail.setId(item.getItemId());
            if (PurchaseConstant.PurchaseDetailStatusEnum.HASERROR.getCode() == item.getStatus()) {
                // 采购失败
                flag = false;
                purchaseDetail.setStatus(item.getStatus());
            } else {
                // 采购成功
                purchaseDetail.setStatus(PurchaseConstant.PurchaseDetailStatusEnum.FINISH.getCode());
                finishPurchaseDetails.add(purchaseDetail);
            }
            purchaseDetails.add(purchaseDetail);
        }
        purchaseDetailService.updateBatchById(purchaseDetails);

        // 2.改变采购单状态，所有采购需求完成则采购单状态已完成，任一采购需求未完成则采购单状态异常
        PurchaseEntity purchase = new PurchaseEntity();
        purchase.setId(doneVo.getId());
        purchase.setStatus(flag ? PurchaseConstant.PurchaseStatusEnum.FINISH.getCode() :
                PurchaseConstant.PurchaseStatusEnum.HASERROR.getCode());
        purchase.setUpdateTime(new Date());
        updateById(purchase);

        // 3.采购成功的采购需求，进行入库操作
        if (!CollectionUtils.isEmpty(finishPurchaseDetails)) {
            // 查询采供成功的采购需求数据
            finishPurchaseDetails = purchaseDetailService.listByIds(finishPurchaseDetails.stream().
                    map(item -> item.getId()).collect(Collectors.toList()));
            // TODO 优化，skuName查询封装成一次feign调用
            for (PurchaseDetailEntity item : finishPurchaseDetails) {
                // 添加库存（这里不存在部分库存入库的情况）
                wareSkuService.addStock(item.getSkuId(), item.getWareId(), item.getSkuNum());
            }
        }
    }
}