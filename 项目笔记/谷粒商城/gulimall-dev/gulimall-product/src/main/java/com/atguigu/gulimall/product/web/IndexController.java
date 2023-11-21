package com.atguigu.gulimall.product.web;

import com.atguigu.common.vo.product.Catalog2VO;
import com.atguigu.common.entity.product.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author: wanzenghui
 * @Date: 2021/10/26 22:01
 * 首页页面跳转
 */
@Controller
public class IndexController {

    @Autowired
    CategoryService categoryService;
    @Autowired
    RedissonClient redissonClient;

    @GetMapping({"/", "index.html"})
    public String indexPage(Model model) {

        // 查询所有1级分类
        List<CategoryEntity> categoryEntitys = categoryService.getLevel1Categorys();

        //
        model.addAttribute("categorys", categoryEntitys);


        // 解析器自动拼装classpath:/templates/  + index +  .html =》 classpath:/templates/index.html
        // classpath表示类路径，编译前是resources文件夹，编译后resources文件夹内的文件会统一存放至classes文件夹内
        return "index";
    }

    /**
     * 查询三级分类
     * {
     *     "一级分类ID": [
     *         {
     *             "catalog1Id": "一级分类ID",
     *             "id": "二级分类ID",
     *             "name": "二级分类名",
     *             "catalog3List":[
     *                 {
     *                     "catalog2Id": "二级分类ID",
     *                     "id": "三级分类ID",
     *                     "name": "三级分类名"
     *                 }
     *             ]
     *         }
     *     ],
     *     "一级分类ID": [],
     *     "一级分类ID": []
     * }
     */
    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String, List<Catalog2VO>> getCatalogJson() {
        Map<String, List<Catalog2VO>> map = categoryService.getCatalogJsonWithSpringCache();
        return map;
    }

    /**
     * 测试redisson实现分布式锁
     */
    @ResponseBody
    @GetMapping("/testRedisson")
    public String test() {
        // 1.获取锁
        RLock lock = redissonClient.getLock("redisson_lock");

        // 2.加锁
        // 1）锁自动续期+30S，业务超长情况下（看门狗）
        // 2）如果线程宕机，看门狗不会自动续期，锁会自动过期
        // 3）unlock使用lua脚本释放锁，不会出现误删锁
        lock.lock(10, TimeUnit.SECONDS);

        try {
            // 加锁成功，执行业务
            System.out.println("加锁成功，执行业务..." + Thread.currentThread().getId());
            Thread.sleep(30000);
        } catch (Exception e) {

        } finally {
            // 3.解锁
            System.out.println("解锁..." + Thread.currentThread().getId());
            lock.unlock();
        }
        return "testRedisson";
    }

    /**
     * 测试redisson实现分布式锁
     */
    @ResponseBody
    @GetMapping("/testRedisson2")
    public String test2() {
        // 1.获取锁
        RReadWriteLock lock = redissonClient.getReadWriteLock("redisson_lock");

        // 2.加锁
        // 1）锁自动续期+30S，业务超长情况下（看门狗）
        // 2）如果线程宕机，看门狗不会自动续期，锁会自动过期
        // 3）unlock使用lua脚本释放锁，不会出现误删锁
        RLock readLock = lock.readLock();
        readLock.lock(10, TimeUnit.SECONDS);
        try {
            // 加锁成功，执行业务
            System.out.println("加锁成功，执行业务..." + Thread.currentThread().getId());
            Thread.sleep(30000);
        } catch (Exception e) {

        } finally {
            // 3.解锁
            System.out.println("解锁..." + Thread.currentThread().getId());
            readLock.unlock();
        }
        return "testRedisson";
    }

}