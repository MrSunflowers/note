package com.atguigu.gulimall.product.service;

import com.atguigu.common.vo.product.Catalog2VO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.entity.product.CategoryEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author wanzenghui
 * @email lemon_wan@aliyun.com
 * @date 2021-09-02 22:58:35
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryEntity> listWithTree();

    /**
     * 删除分类
     */
    void removeMenuByIds(List<Long> asList);

    /**
     * 根据catelogId查询所有父分类ID
     */
    Long[] findCatelogPath(Long catelogId);

    /**
     * 级联更新所有关联表的冗余数据
     */
    void updateCascade(CategoryEntity category);

    /**
     * 查出所有1级分类
     */
    List<CategoryEntity> getLevel1Categorys();

    /**
     * 查询三级分类并封装成Map返回
     * 使用SpringCache注解方式简化缓存设置
     */
    Map<String, List<Catalog2VO>> getCatalogJsonWithSpringCache();

    /**
     * 查询三级分类并封装成Map返回
     * 使用redis客户端实现缓存设置
     */
    Map<String, List<Catalog2VO>> getCatalogJson();
}