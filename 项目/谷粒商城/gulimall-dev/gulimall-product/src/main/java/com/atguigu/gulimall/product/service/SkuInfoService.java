package com.atguigu.gulimall.product.service;

import com.atguigu.common.entity.product.SkuInfoEntity;
import com.atguigu.common.entity.product.SpuInfoEntity;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.vo.product.SkuItemVO;
import com.atguigu.common.vo.product.Skus;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * sku信息
 *
 * @author wanzenghui
 * @email lemon_wan@aliyun.com
 * @date 2021-09-02 22:58:35
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
    /**
     * sku检索
     */
    PageUtils queryPageByCondition(Map<String, Object> params);

    /**
     * 新增sku信息
     */
    void saveSkuInfo(SpuInfoEntity spuInfo, List<Skus> skus);

    /**
     * 查询spuId对应的所有sku信息
     */
    List<SkuInfoEntity> getSkusBySpuId(Long spuId);

    /**
     * 查询skuId商品信息，封装VO返回
     */
    SkuItemVO item(Long skuId) throws ExecutionException, InterruptedException;

    /**
     * 根据集合查询
     */
    List<SkuInfoEntity> getByIds(Collection<Long> skuIds);
}

