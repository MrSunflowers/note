package com.atguigu.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.constant.product.CategoryConstant;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.common.vo.product.Catalog2VO;
import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.common.entity.product.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryBrandRelationService;
import com.atguigu.gulimall.product.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    RedissonClient redisson;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 查找三级分类
     */
    @Override
    public List<CategoryEntity> listWithTree() {
        //查出所有分类
        List<CategoryEntity> entities = baseMapper.selectList(null);

        //组装成父子树形结构
        //1级分类
        List<CategoryEntity> level1Menus = entities.stream().
                filter(entity -> entity.getParentCid() == 0).
                map(entity -> {
                    entity.setChildren(getChildren(entity, entities));
                    return entity;
                }).
                sorted((entity1, entity2) ->
                        (entity1.getSort() == null ? 0 : entity1.getSort()) -
                                (entity2.getSort() == null ? 0 : entity2.getSort())).
                collect(Collectors.toList());
        return level1Menus;
    }

    /**
     * 递归查找所有菜单的子菜单
     */
    private List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> all) {
        List<CategoryEntity> children = all.stream().filter(entity -> entity.getParentCid().equals(root.getCatId())).
                map(entity -> {
                    entity.setChildren(getChildren(entity, all));
                    return entity;
                }).sorted((entity1, entity2) ->
                (entity1.getSort() == null ? 0 : entity1.getSort()) -
                        (entity2.getSort() == null ? 0 : entity2.getSort())).
                collect(Collectors.toList());
        return children;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO 检查当前删除的菜单，是否被别的地方引用

        //逻辑删除
        baseMapper.deleteBatchIds(asList);
    }

    /**
     * 根据catelogId查询所有父分类ID
     */
    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        // 递归查询父类
        paths = findParentPath(catelogId, paths);
        // 逆序，父在前
        Collections.reverse(paths);
        return paths.toArray(new Long[paths.size()]);
    }

    /**
     * 递归查找父路径
     */
    private List<Long> findParentPath(Long catelogId, List<Long> paths) {
        paths.add(catelogId);
        CategoryEntity category = this.getById(catelogId);
        if (category.getParentCid() != 0) {
            findParentPath(category.getParentCid(), paths);
        }
        return paths;
    }

    /**
     * 级联更新所有关联表的冗余数据
     * 缓存策略：失效模式，方法执行完删除缓存
     */
    @CacheEvict(value = {"category"}, allEntries = true)
    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        if (!StringUtils.isEmpty(category.getName())) {
            // 更新冗余表
            categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
            // TODO 更新其他冗余表
        }
    }

    /**
     * 查出所有1级分类
     */
    @Cacheable(value = {"category"}, key = "'getLevel1Categorys'", sync = true)
    @Override
    public List<CategoryEntity> getLevel1Categorys() {
        System.out.println("调用了getLevel1Categorys...");
        // 查询父id=0
        return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
    }

    /**
     * 查询三级分类并封装成Map返回
     * 使用SpringCache注解方式简化缓存设置
     */
    @Cacheable(value = {"category"}, key = "'getCatalogJson'", sync = true)
    @Override
    public Map<String, List<Catalog2VO>> getCatalogJsonWithSpringCache() {
        // 未命中缓存
        // 1.double check，占锁成功需要再次检查缓存（springcache使用本地锁）
        // 查询非空即返回
        String catlogJSON = redisTemplate.opsForValue().get("getCatalogJson");
        if (!StringUtils.isEmpty(catlogJSON)) {
            // 查询成功直接返回不需要查询DB
            Map<String, List<Catalog2VO>> result = JSON.parseObject(catlogJSON, new TypeReference<Map<String, List<Catalog2VO>>>() {
            });
            return result;
        }

        // 2.查询所有分类，按照parentCid分组
        Map<Long, List<CategoryEntity>> categoryMap = baseMapper.selectList(null).stream()
                .collect(Collectors.groupingBy(key -> key.getParentCid()));

        // 3.获取1级分类
        List<CategoryEntity> level1Categorys = categoryMap.get(0L);

        // 4.封装数据
        Map<String, List<Catalog2VO>> result = level1Categorys.stream().collect(Collectors.toMap(key -> key.getCatId().toString(), l1Category -> {
            // 5.查询2级分类，并封装成List<Catalog2VO>
            List<Catalog2VO> catalog2VOS = categoryMap.get(l1Category.getCatId())
                    .stream().map(l2Category -> {
                        // 7.查询3级分类，并封装成List<Catalog3VO>
                        List<Catalog2VO.Catalog3Vo> catalog3Vos = categoryMap.get(l2Category.getCatId())
                                .stream().map(l3Category -> {
                                    // 封装3级分类VO
                                    Catalog2VO.Catalog3Vo catalog3Vo = new Catalog2VO.Catalog3Vo(l2Category.getCatId().toString(), l3Category.getCatId().toString(), l3Category.getName());
                                    return catalog3Vo;
                                }).collect(Collectors.toList());
                        // 封装2级分类VO返回
                        Catalog2VO catalog2VO = new Catalog2VO(l1Category.getCatId().toString(), catalog3Vos, l2Category.getCatId().toString(), l2Category.getName());
                        return catalog2VO;
                    }).collect(Collectors.toList());
            return catalog2VOS;
        }));
        return result;
    }

    /**
     * 查询三级分类并封装成Map返回
     * 使用redis客户端实现缓存设置
     */
    @Override
    public Map<String, List<Catalog2VO>> getCatalogJson() {
        // 查询缓存
        String catalogJSON = redisTemplate.opsForValue().get(CategoryConstant.CACHE_KEY_CATALOG_JSON);

        if (StringUtils.isEmpty(catalogJSON)) {
            // 未命中缓存
            // 查询db
            Map<String, List<Catalog2VO>> result = getCatalogJsonFromDBWithRedissonLock();
            return result;
        }

        // 命中缓存
        Map<String, List<Catalog2VO>> result = JSONObject.parseObject(catalogJSON, new TypeReference<Map<String, List<Catalog2VO>>>() {
        });

        return result;
    }

    /**
     * 查询三级分类（本地锁版本）
     * 已废弃
     */
    @Deprecated
    public Map<String, List<Catalog2VO>> getCatalogJsonFromDBWithLocalLock() {
        // 本地锁：synchronized，JUC(lock)，在分布式0情况下，需要使用分布式锁
        synchronized (this) {
            // 得到锁以后还要检查一次，double check
            return getCatalogJsonFromDB();
        }
    }

    /**
     * 查询三级分类（原生版redis分布式锁版本）
     */
    public Map<String, List<Catalog2VO>> getCatalogJsonFromDBWithRedisLock() {
        // 1.抢占分布式锁，同时设置过期时间
        String uuid = UUID.randomUUID().toString();
        // 使用setnx占锁（setIfAbsent）
        Boolean isLock = redisTemplate.opsForValue().setIfAbsent(CategoryConstant.LOCK_KEY_CATALOG_JSON, uuid, 300, TimeUnit.SECONDS);
        if (isLock) {
            // 2.抢占成功
            Map<String, List<Catalog2VO>> result = null;
            try {
                // 查询DB
                // TODO 业务续期（锁过期）【不应该添加业务续期代码】
                return getCatalogJsonFromDB();
            } finally {
                // 3.查询UUID是否是自己，是自己的lock就删除
                // 封装lua脚本（原子操作解锁）
                // 查询+删除（当前值与目标值是否相等，相等执行删除，不等返回0）
                String luaScript = "if redis.call('get',KEYS[1]) == ARGV[1]\n" +
                        "then\n" +
                        "    return redis.call('del',KEYS[1])\n" +
                        "else\n" +
                        "    return 0\n" +
                        "end";
                // 删除锁
                redisTemplate.execute(new DefaultRedisScript<Long>(luaScript, Long.class), Arrays.asList(CategoryConstant.LOCK_KEY_CATALOG_JSON), uuid);
            }
        } else {
            // 4.加锁失败，自旋重试
            // TODO 不应该使用递归，使用while，且固定次数
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return getCatalogJsonFromDBWithRedisLock();
        }
    }

    /**
     * 查询三级分类（redisson分布式锁版本）
     */
    public Map<String, List<Catalog2VO>> getCatalogJsonFromDBWithRedissonLock() {
        // 1.抢占分布式锁，同时设置过期时间
        RLock lock = redisson.getLock(CategoryConstant.LOCK_KEY_CATALOG_JSON);
        lock.lock(30, TimeUnit.SECONDS);
        try {
            // 2.查询DB
            Map<String, List<Catalog2VO>> result = getCatalogJsonFromDB();
            return result;
        } finally {
            // 3.释放锁
            lock.unlock();
        }
    }

    /**
     * 查询三级分类（从数据源DB查询）
     * 加入分布式锁版本代码，double check检查
     */
    public Map<String, List<Catalog2VO>> getCatalogJsonFromDB() {
        // 1.double check，占锁成功需要再次检查缓存
        // 查询非空即返回
        String catlogJSON = redisTemplate.opsForValue().get("catlogJSON");
        if (!StringUtils.isEmpty(catlogJSON)) {
            // 查询成功直接返回不需要查询DB
            Map<String, List<Catalog2VO>> result = JSON.parseObject(catlogJSON, new TypeReference<Map<String, List<Catalog2VO>>>() {
            });
            return result;
        }

        // 2.查询所有分类，按照parentCid分组
        Map<Long, List<CategoryEntity>> categoryMap = baseMapper.selectList(null).stream()
                .collect(Collectors.groupingBy(key -> key.getParentCid()));

        // 3.获取1级分类
        List<CategoryEntity> level1Categorys = categoryMap.get(0L);

        // 4.封装数据
        Map<String, List<Catalog2VO>> result = level1Categorys.stream().collect(Collectors.toMap(key -> key.getCatId().toString(), l1Category -> {
            // 3.查询2级分类，并封装成List<Catalog2VO>
            List<Catalog2VO> catalog2VOS = categoryMap.get(l1Category.getCatId())
                    .stream().map(l2Category -> {
                        // 4.查询3级分类，并封装成List<Catalog3VO>
                        List<Catalog2VO.Catalog3Vo> catalog3Vos = categoryMap.get(l2Category.getCatId())
                                .stream().map(l3Category -> {
                                    // 封装3级分类VO
                                    Catalog2VO.Catalog3Vo catalog3Vo = new Catalog2VO.Catalog3Vo(l2Category.getCatId().toString(), l3Category.getCatId().toString(), l3Category.getName());
                                    return catalog3Vo;
                                }).collect(Collectors.toList());
                        // 封装2级分类VO返回
                        Catalog2VO catalog2VO = new Catalog2VO(l1Category.getCatId().toString(), catalog3Vos, l2Category.getCatId().toString(), l2Category.getName());
                        return catalog2VO;
                    }).collect(Collectors.toList());
            return catalog2VOS;
        }));

        // 5.结果集存入redis
        // 关注锁时序问题，存入redis代码块必须在同步快内执行
        redisTemplate.opsForValue().set(CategoryConstant.CACHE_KEY_CATALOG_JSON,
                JSONObject.toJSONString(result), 1, TimeUnit.DAYS);

        return result;
    }
}