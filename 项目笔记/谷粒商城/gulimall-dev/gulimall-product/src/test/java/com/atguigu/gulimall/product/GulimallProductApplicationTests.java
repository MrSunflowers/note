package com.atguigu.gulimall.product;

import com.atguigu.common.entity.product.BrandEntity;
import com.atguigu.common.vo.product.SkuItemSaleAttrVO;
import com.atguigu.common.vo.product.SpuItemAttrGroupVO;
import com.atguigu.gulimall.product.service.BrandService;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.service.impl.AttrGroupServiceImpl;
import com.atguigu.gulimall.product.service.impl.SkuSaleAttrValueServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@SpringBootTest
class GulimallProductApplicationTests {

    @Autowired
    BrandService brandService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    RedissonClient redissonClient;
    @Autowired
    AttrGroupServiceImpl attrGroupService;
    @Autowired
    SkuSaleAttrValueServiceImpl skuSaleAttrValueService;

    @Test
    void testRegix() {


        String regx = "/^(?i)use.*/gi";
        System.out.println("use database".matches(regx));


    }

//
//    /**
//     * 获取spu下的所有销售属性组合
//     */
//    @Test
//    void testGetSaleAttrBySpuId() {
//        List<SkuItemSaleAttrVO> res = skuSaleAttrValueService.getSaleAttrBySpuId(13l);
//        System.out.println(res.size());
//    }
//
//    /**
//     * 测试查询当前spu对应的所有属性的分组信息以及当前分组下的所有属性对应的值
//     */
//    @Test
//    void testGetAttrGroupWithAttrsBySpuId() {
//        List<SpuItemAttrGroupVO> attrs = attrGroupService.getAttrGroupWithAttrsBySpuId(13l, 225l);
//        System.out.println(attrs.size());
//    }
//
//    /**
//     * 测试redisClient是否注入成功
//     */
//    @Test
//    void testRedisClient() {
//        System.out.println(redissonClient);
//    }
//
//    /**
//     * 测试redis
//     */
//    @Test
//    void testRedis() {
//        // 获取操作对象
//        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
//
//        // 存储
//        ops.set("hello", "world" + UUID.randomUUID());
//
//        // 获取
//        System.out.println(ops.get("hello"));
//    }
//
//    @Test
//    void contextLoads() {
//        BrandEntity entity = new BrandEntity();
//        entity.setName("华为");
//        boolean save = brandService.save(entity);
//        System.out.println("保存成功：" + save);
//    }
//
//    // 查询条件Wrapper，brand_id = 1的，链式编程拼接多个条件
//    @Test
//    void queryPage() {
//        //brandService.queryPage()
//        List<BrandEntity> list = brandService.list(new QueryWrapper<BrandEntity>().eq("name", "华为"));
//        list.forEach((item)->{
//            System.out.println(item);
//        });
//    }
//
//    // 测试查询父路径
//    @Test
//    void findCatelogPath() {
//        log.info("查询父路径{}" + Arrays.toString(categoryService.findCatelogPath(225L)));
//    }
//
//

}
