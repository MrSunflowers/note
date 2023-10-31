package com.atguigu.gulimall.product.service.impl;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.gulimall.product.dao.ProductAttrValueDao;
import com.atguigu.common.entity.product.AttrEntity;
import com.atguigu.common.entity.product.ProductAttrValueEntity;
import com.atguigu.gulimall.product.service.AttrService;
import com.atguigu.gulimall.product.service.ProductAttrValueService;
import com.atguigu.common.vo.product.BaseAttrs;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {

    @Autowired
    AttrService attrService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ProductAttrValueEntity> page = this.page(
                new Query<ProductAttrValueEntity>().getPage(params),
                new QueryWrapper<ProductAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveProductAttrValue(Long spuId, List<BaseAttrs> baseAttrs) {
        if (!CollectionUtils.isEmpty(baseAttrs)) {
            // 根据ids查询属性map，用于封装冗余数据
            List<Long> attrIds = baseAttrs.stream().map(baseAttr -> baseAttr.getAttrId()).collect(Collectors.toList());
            Map<Long, AttrEntity> attrMap = attrService.getBatchIds(attrIds).stream().collect(Collectors.toMap(key -> key.getAttrId(), val -> val));
            // 封装
            List<ProductAttrValueEntity> collect = baseAttrs.stream().map(attr -> {
                ProductAttrValueEntity product = new ProductAttrValueEntity();
                product.setAttrId(attr.getAttrId());
                product.setAttrName(attrMap.get(attr.getAttrId()).getAttrName());
                product.setAttrValue(attr.getAttrValues());
                product.setQuickShow(attr.getShowDesc());
                product.setSpuId(spuId);

                return product;
            }).collect(Collectors.toList());
            // 批量保存
            this.saveBatch(collect);
        }
    }

    /**
     * 获取spu规格
     */
    @Override
    public List<ProductAttrValueEntity> baseAttrlistforspu(Long spuId) {
        return this.baseMapper.selectList(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));
    }

    @Transactional
    @Override
    public void updateSpuAttr(Long spuId, List<ProductAttrValueEntity> entities) {
        // 1.删除
        this.baseMapper.delete(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));
        // 2.插入
        List<ProductAttrValueEntity> collect = entities.stream().map(item -> {
            item.setId(null);
            item.setSpuId(spuId);
            return item;
        }).collect(Collectors.toList());
        this.saveBatch(collect);
    }

}