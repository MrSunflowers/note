package com.atguigu.gulimall.product.service.impl;

import com.atguigu.common.constant.product.AttrConstant;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.common.vo.product.*;
import com.atguigu.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.atguigu.gulimall.product.dao.AttrDao;
import com.atguigu.gulimall.product.dao.AttrGroupDao;
import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.common.entity.product.AttrAttrgroupRelationEntity;
import com.atguigu.common.entity.product.AttrEntity;
import com.atguigu.common.entity.product.AttrGroupEntity;
import com.atguigu.common.entity.product.CategoryEntity;
import com.atguigu.gulimall.product.service.AttrService;
import com.atguigu.gulimall.product.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    AttrAttrgroupRelationDao relationDao;
    @Autowired
    CategoryDao categoryDao;
    @Autowired
    AttrGroupDao attrGroupDao;
    @Autowired
    CategoryService categoryService;
    @Autowired
    AttrAttrgroupRelationDao attrAttrgroupRelationDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveAttr(AttrVO attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        // 保存基本数据
        this.save(attrEntity);
        if (AttrConstant.AttrEnum.ATTR_TYPE_BASE.getCode() == attrEntity.getAttrType() &&
                !StringUtils.isEmpty(attr.getAttrGroupId())) {
            // 基本规格，保存关联关系
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrGroupId(attr.getAttrGroupId());// 分组ID
            relationEntity.setAttrId(attrEntity.getAttrId());// 属性ID
            relationDao.insert(relationEntity);
        }
    }

    @Override
    public PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String type) {
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>().
                eq("attr_type", "base".equalsIgnoreCase(type) ?
                        AttrConstant.AttrEnum.ATTR_TYPE_BASE.getCode() :
                        AttrConstant.AttrEnum.ATTR_TYPE_SALE.getCode());
        if (catelogId != 0) {
            queryWrapper.eq("catelog_id", catelogId);
        }
        // 关键字
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.and(wrapper -> wrapper.eq("attr_id", key).or().like("attr_name", key));
        }
        // 分页查询
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), queryWrapper);
        // 封装vo冗余数据
        List<AttrRespVO> attrRespVos = page.getRecords().stream().map(attr -> {
            AttrRespVO attrRespVo = new AttrRespVO();
            BeanUtils.copyProperties(attr, attrRespVo);

            // 查询设置分类名
            CategoryEntity categoryEntity = categoryDao.selectById(attr.getCatelogId());
            attrRespVo.setCatelogName(categoryEntity.getName());

            if (AttrConstant.AttrEnum.ATTR_TYPE_BASE.getCode() == attr.getAttrType()) {
                // 基本规格，查询设置分组名
                AttrAttrgroupRelationEntity relationEntity = relationDao.selectOne(
                        new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));
                if (relationEntity != null && !StringUtils.isEmpty(relationEntity.getAttrGroupId())) {
                    AttrGroupEntity groupEntity = attrGroupDao.selectById(relationEntity.getAttrGroupId());
                    attrRespVo.setGroupName(groupEntity.getAttrGroupName());
                }
            }
            return attrRespVo;
        }).collect(Collectors.toList());
        // 封装回参
        PageUtils pageUtils = new PageUtils(page);
        pageUtils.setList(attrRespVos);
        return pageUtils;
    }

    @Cacheable(value = "attr", key = "'attrinfo:'+#root.args[0]")// attrinfo:attrId
    @Override
    public AttrRespVO getAttrInfo(Long attrId) {
        AttrEntity attrEntity = this.getById(attrId);
        AttrRespVO respVo = new AttrRespVO();
        BeanUtils.copyProperties(attrEntity, respVo);

        if (attrEntity.getAttrType().equals(AttrConstant.AttrEnum.ATTR_TYPE_BASE.getCode())) {
            // 基本规格，1.查询设置分组ID
            AttrAttrgroupRelationEntity relationEntity = relationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId));
            if (relationEntity != null) {
                respVo.setAttrGroupId(relationEntity.getAttrGroupId());
                // 查询分组名
                AttrGroupEntity groupEntity = attrGroupDao.selectById(relationEntity.getAttrGroupId());
                if (groupEntity != null) {
                    respVo.setGroupName(groupEntity.getAttrGroupName());
                }
            }
        }
        // 2.查询设置分类path
        Long[] catelogPath = categoryService.findCatelogPath(attrEntity.getCatelogId());
        respVo.setCatelogPath(catelogPath);

        return respVo;
    }

    @Transactional
    @Override
    public void updateAttr(AttrVO attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        // 修改属性
        this.updateById(attrEntity);

        // 查询属性
        AttrEntity _attr = this.getById(attrEntity.getAttrId());
        if (AttrConstant.AttrEnum.ATTR_TYPE_BASE.getCode() == _attr.getAttrType()) {
            // 基本规格，修改分组关联
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrId(attr.getAttrId());
            relationEntity.setAttrGroupId(attr.getAttrGroupId());
            int count = relationDao.update(relationEntity, new UpdateWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));
            if (count == 0) {
                // 修改数据为空，新增分组关联
                relationDao.insert(relationEntity);
            }
        }
    }

    /**
     * 显示属性分组 关联的 所有属性
     */
    @Override
    public List<AttrEntity> getRelationAttr(Long attrgroupId) {
        // 根据分组id查询所有关联关系
        List<AttrAttrgroupRelationEntity> relationEntities = attrAttrgroupRelationDao.
                selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrgroupId));
        // 封装基本属性id
        List<Long> attrIds = relationEntities.stream().map((attr) -> {
            return attr.getAttrId();
        }).collect(Collectors.toList());
        // 根据id查询基本属性集合
        if (CollectionUtils.isEmpty(attrIds)) {
            return new ArrayList<>();
        }
        // 非空查询返回
        List<AttrEntity> attrEntities = this.listByIds(attrIds);
        return attrEntities;
    }

    /**
     * 删除所有关联数据
     */
    @Override
    public void deleteRelation(AttrGroupRelationVO[] vos) {
        List<AttrAttrgroupRelationEntity> entities = Arrays.stream(vos).map((item) -> {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(item, relationEntity);
            return relationEntity;
        }).collect(Collectors.toList());
        attrAttrgroupRelationDao.deleteBatchRelation(entities);
    }

    /**
     * 获取当前分组没有关联的所有属性
     * 1、条件是当前分类下的所有属性分组
     * 2、没有被分组关联的属性【包括自己关联的】
     */
    @Override
    public PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId) {
        // 1、当前分组只能关联所属分类下的所有属性
        // 先查出分类ID
        AttrGroupEntity groupEntity = attrGroupDao.selectById(attrgroupId);
        Long catelogId = groupEntity.getCatelogId();
        // 2、只能显示当前分类下没有被关联的属性
        // 2.1）查出所属分类下的所有分组
        List<AttrGroupEntity> groups = attrGroupDao.selectList(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        List<Long> groupIds = groups.stream().map((item) -> {
            return item.getAttrGroupId();
        }).collect(Collectors.toList());
        // 2.2）查出这些分组已经关联的属性
        List<AttrAttrgroupRelationEntity> relationEntities = new ArrayList<AttrAttrgroupRelationEntity>();
        if (!CollectionUtils.isEmpty(groupIds)) {
            relationEntities = attrAttrgroupRelationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().in("attr_group_id", groupIds));
        }
        List<Long> attrIds = relationEntities.stream().map((item) -> {
            return item.getAttrId();
        }).collect(Collectors.toList());

        // 2.3）从当前分类下的所有属性里 剔除已关联的属性
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>().eq("catelog_id", catelogId).eq("attr_type", AttrConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
        if (!CollectionUtils.isEmpty(attrIds)) {
            queryWrapper.notIn("attr_id", attrIds);
        }
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.and((w) -> {
                w.eq("attr_id", key).or().like("attr_name", key);
            });
        }
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), queryWrapper);
        PageUtils pageUtils = new PageUtils(page);
        return pageUtils;
    }

    @Override
    public List<AttrEntity> getBatchIds(List<Long> attrIds) {
        return this.baseMapper.selectBatchIds(attrIds);
    }

    /**
     * 查询允许被检索的属性ID集合
     */
    @Override
    public List<Long> selectSearchAttrIds(List<Long> attrIds) {
        return baseMapper.selectSearchAttrIds(attrIds);
    }


}