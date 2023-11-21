package com.atguigu.gulimall.product.service;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.vo.product.AttrGroupWithAttrsVO;
import com.atguigu.common.entity.product.AttrGroupEntity;
import com.atguigu.common.vo.product.SpuItemAttrGroupVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author wanzenghui
 * @email lemon_wan@aliyun.com
 * @date 2021-09-02 22:58:35
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPage(Map<String, Object> params, Long catelogId);

    /**
     * 获取分类下所有分组&关联属性
     */
    List<AttrGroupWithAttrsVO> getAttrGroupWithAttrs(Long catelogId);

    /**
     * 查出当前spu对应的所有属性的分组信息以及当前分组下的所有属性对应的值
     */
    List<SpuItemAttrGroupVO> getAttrGroupWithAttrsBySpuId(Long spuId, Long catalogId);
}

