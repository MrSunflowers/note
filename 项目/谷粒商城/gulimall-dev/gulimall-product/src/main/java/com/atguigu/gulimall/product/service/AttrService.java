package com.atguigu.gulimall.product.service;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.vo.product.AttrGroupRelationVO;
import com.atguigu.common.vo.product.AttrRespVO;
import com.atguigu.common.vo.product.AttrVO;
import com.atguigu.common.entity.product.AttrEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author wanzenghui
 * @email lemon_wan@aliyun.com
 * @date 2021-09-02 22:58:35
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAttr(AttrVO attr);

    PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String type);

    AttrRespVO getAttrInfo(Long attrId);

    void updateAttr(AttrVO attr);

    /**
     * 查询当前分组关联的所有基本属性
     */
    List<AttrEntity> getRelationAttr(Long attrgroupId);

    /**
     * 删除关联关系
     */
    void deleteRelation(AttrGroupRelationVO[] vos);

    /**
     * 查询与当前分组没有关联关系的基本属性
     */
    PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId);

    /**
     * 根据ids查询
     */
    List<AttrEntity> getBatchIds(List<Long> attrIds);

    /**
     * 查询允许被检索的属性ID集合
     */
    List<Long> selectSearchAttrIds(List<Long> attrIds);
}

