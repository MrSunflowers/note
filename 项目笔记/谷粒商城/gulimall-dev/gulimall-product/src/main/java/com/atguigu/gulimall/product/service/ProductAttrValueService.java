package com.atguigu.gulimall.product.service;

import com.atguigu.common.vo.product.BaseAttrs;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.entity.product.ProductAttrValueEntity;

import java.util.List;
import java.util.Map;

/**
 * spu属性值
 *
 * @author wanzenghui
 * @email lemon_wan@aliyun.com
 * @date 2021-09-02 22:58:35
 */
public interface ProductAttrValueService extends IService<ProductAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 保存spu商品基本属性值
     */
    void saveProductAttrValue(Long spuId, List<BaseAttrs> baseAttrs);

    /**
     * 获取spu规格
     */
    List<ProductAttrValueEntity> baseAttrlistforspu(Long spuId);

    /**
     * 修改商品规格
     */
    void updateSpuAttr(Long spuId, List<ProductAttrValueEntity> entities);
}

