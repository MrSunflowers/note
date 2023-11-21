package com.atguigu.gulimall.product.service;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.vo.product.SpuSaveVO;
import com.atguigu.common.entity.product.SpuInfoEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * spu信息
 *
 * @author wanzenghui
 * @email lemon_wan@aliyun.com
 * @date 2021-09-02 22:58:35
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
    /**
     * 分页查询
     */
    PageUtils queryPageByCondition(Map<String, Object> params);

    /**
     * 发布商品
     */
    void saveSpuInfo(SpuSaveVO vo);

    /**
     * 新增spuInfoEntity
     */
    void saveBaseSpuInfo(SpuInfoEntity spuInfo);

    /**
     * spu商品上架
     */
    void up(Long spuId);

    /**
     * 根据SkuId查询SPU信息
     */
    SpuInfoEntity getBySkuId(Long skuId);
}

