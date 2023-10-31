package com.atguigu.gulimall.seckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.constant.seckill.SeckillConstant;
import com.atguigu.common.to.mq.SeckillOrderTO;
import com.atguigu.common.to.seckill.SeckillSessionWithSkusTO;
import com.atguigu.common.to.seckill.SeckillSkuRedisTO;
import com.atguigu.common.to.seckill.SkuInfoTO;
import com.atguigu.common.utils.R;
import com.atguigu.common.vo.auth.MemberResponseVO;
import com.atguigu.common.vo.product.SeckillSkuVO;
import com.atguigu.gulimall.seckill.feign.CouponFeignService;
import com.atguigu.gulimall.seckill.feign.ProductFeignService;
import com.atguigu.gulimall.seckill.interceptor.LoginUserInterceptor;
import com.atguigu.gulimall.seckill.service.SeckillService;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.atguigu.common.constant.seckill.SeckillConstant.SECKILL_CHARE_KEY;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: wanzenghui
 * @createTime: 2020-07-09 19:29
 **/

@Slf4j
@Service
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    CouponFeignService couponFeignService;
    @Autowired
    ProductFeignService productFeignService;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    RedissonClient redissonClient;

    /**
     * 上架最近三天需要秒杀的商品
     */
    @Override
    public void uploadSeckillSkuLatest3Days() {
        // 1.查询最近三天需要参加秒杀的场次+商品
        R lates3DaySession = couponFeignService.getLates3DaySession();
        if (lates3DaySession.getCode() == 0) {
            // 获取场次
            List<SeckillSessionWithSkusTO> sessions = lates3DaySession.getData("data", new TypeReference<List<SeckillSessionWithSkusTO>>() {
            });
            // 2.上架场次信息
            saveSessionInfos(sessions);
            // 3.上架商品信息
            saveSessionSkuInfo(sessions);
        }

    }

    /**
     * 上架场次
     */
    private void saveSessionInfos(List<SeckillSessionWithSkusTO> sessions) {
        if (!CollectionUtils.isEmpty(sessions)) {
            sessions.stream().forEach(session -> {
                // 1.遍历场次
                long startTime = session.getStartTime().getTime();// 场次开始时间戳
                long endTime = session.getEndTime().getTime();// 场次结束时间戳
                String key = SeckillConstant.SESSION_CACHE_PREFIX + startTime + "_" + endTime;// 场次的key

                // 2.判断场次是否已上架（幂等性）
                Boolean hasKey = redisTemplate.hasKey(key);
                if (!hasKey) {
                    // 未上架
                    // 3.封装场次信息
                    List<String> skuIds = session.getRelationSkus().stream()
                            .map(item -> item.getPromotionSessionId() + "_" + item.getSkuId().toString())
                            .collect(Collectors.toList());// skuId集合
                    // 4.上架
                    redisTemplate.opsForList().leftPushAll(key, skuIds);
                }
            });
        }
    }

    /**
     * 上架商品信息
     */
    private void saveSessionSkuInfo(List<SeckillSessionWithSkusTO> sessions) {
        if (!CollectionUtils.isEmpty(sessions)) {
            // 查询所有商品信息
            List<Long> skuIds = new ArrayList<>();
            sessions.stream().forEach(session -> {
                List<Long> ids = session.getRelationSkus().stream().map(SeckillSkuVO::getSkuId).collect(Collectors.toList());
                skuIds.addAll(ids);
            });
            R info = productFeignService.getSkuInfos(skuIds);
            if (info.getCode() == 0) {
                // 将查询结果封装成Map集合
                Map<Long, SkuInfoTO> skuMap = info.getData(new TypeReference<List<SkuInfoTO>>() {
                }).stream().collect(Collectors.toMap(SkuInfoTO::getSkuId, val -> val));
                // 绑定秒杀商品hash
                BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(SeckillConstant.SECKILL_CHARE_KEY);
                // 1.遍历场次
                sessions.stream().forEach(session -> {
                    // 2.遍历商品
                    session.getRelationSkus().stream().forEach(seckillSku -> {
                        // 判断商品是否已上架（幂等性）
                        String skuKey = seckillSku.getPromotionSessionId().toString() + "_" + seckillSku.getSkuId().toString();// 商品的key（需要添加场次ID前缀，同一款商品可能场次不同）
                        if (!operations.hasKey(skuKey)) {
                            // 未上架
                            // 3.封装商品信息
                            SeckillSkuRedisTO redisTo = new SeckillSkuRedisTO();// 存储到redis的对象
                            SkuInfoTO sku = skuMap.get(seckillSku.getSkuId());
                            BeanUtils.copyProperties(seckillSku, redisTo);// 商品秒杀信息
                            redisTo.setSkuInfo(sku);// 商品详细信息
                            redisTo.setStartTime(session.getStartTime().getTime());// 秒杀开始时间
                            redisTo.setEndTime(session.getEndTime().getTime());// 秒杀结束时间
                            // 商品随机码：用户参与秒杀时，请求需要带上随机码（防止恶意攻击）
                            String token = UUID.randomUUID().toString().replace("-", "");// 商品随机码（随机码只会在秒杀开始时暴露）
                            redisTo.setRandomCode(token);// 设置商品随机码

                            // 4.上架商品（序列化成json格式存入Redis中）
                            String jsonString = JSONObject.toJSONString(redisTo);
                            operations.put(skuKey, jsonString);

                            // 5.上架商品的分布式信号量，key：商品随机码 值：库存（限流）
                            RSemaphore semaphore = redissonClient.getSemaphore(SeckillConstant.SKU_STOCK_SEMAPHORE + token);
                            // 信号量（扣减成功才进行后续操作，否则快速返回）
                            semaphore.trySetPermits(seckillSku.getSeckillCount());
                        }
                    });
                });
            }
        }
    }

    /**
     * 获取到当前可以参加秒杀商品的信息
     */
    //@SentinelResource(value = "getCurrentSeckillSkusResource", blockHandler = "blockHandler")
    @Override
    public List<SeckillSkuRedisTO> getCurrentSeckillSkus() {
        //try (Entry entry = SphU.entry("seckillSkus")) {
        // 1.查询当前时间所属的秒杀场次
        long currentTime = System.currentTimeMillis();// 当前时间
        // 查询所有秒杀场次的key
        Set<String> keys = redisTemplate.keys(SeckillConstant.SESSION_CACHE_PREFIX + "*");// keys seckill:sessions:*
        for (String key : keys) {
            //seckill:sessions:1594396764000_1594453242000
            String replace = key.replace(SeckillConstant.SESSION_CACHE_PREFIX, "");// 截取时间，去掉前缀
            String[] time = replace.split("_");
            long startTime = Long.parseLong(time[0]);// 开始时间
            long endTime = Long.parseLong(time[1]);// 截止时间
            // 判断是否处于该场次
            if (currentTime >= startTime && currentTime <= endTime) {
                // 2.查询当前场次信息（查询结果List< sessionId_skuId > ）
                List<String> sessionIdSkuIds = redisTemplate.opsForList().range(key, -100, 100);// 获取list范围内100条数据
                // 获取商品信息
                BoundHashOperations<String, String, String> skuOps = redisTemplate.boundHashOps(SECKILL_CHARE_KEY);
                assert sessionIdSkuIds != null;
                // 根据List< sessionId_skuId >从Map中批量获取商品信息
                List<String> skus = skuOps.multiGet(sessionIdSkuIds);
                if (!CollectionUtils.isEmpty(skus)) {
                    // 将商品信息反序列成对象
                    List<SeckillSkuRedisTO> skuInfos = skus.stream().map(sku -> {
                        SeckillSkuRedisTO skuInfo = JSON.parseObject(sku.toString(), SeckillSkuRedisTO.class);
                        // redisTo.setRandomCode(null);当前秒杀开始需要随机码
                        return skuInfo;
                    }).collect(Collectors.toList());
                    return skuInfos;
                }
                // 3.匹配场次成功，退出循环
                break;
            }
        }
        //} catch (BlockException e) {
        //    log.error("资源被限流{}", e.getMessage());
        //}
        return null;
    }

    /**
     * 根据skuId查询商品当前时间秒杀信息
     *
     * @param skuId
     */
    @Override
    public SeckillSkuRedisTO getSkuSeckilInfo(Long skuId) {
        // 1.匹配查询当前商品的秒杀信息
        BoundHashOperations<String, String, String> skuOps = redisTemplate.boundHashOps(SECKILL_CHARE_KEY);
        // 获取所有商品的key：sessionId_
        Set<String> keys = skuOps.keys();
        if (!CollectionUtils.isEmpty(keys)) {
            String lastIndex = "_" + skuId;
            for (String key : keys) {
                if (key.lastIndexOf(lastIndex) > -1) {
                    // 商品id匹配成功
                    String jsonString = skuOps.get(key);
                    // 进行序列化
                    SeckillSkuRedisTO skuInfo = JSON.parseObject(jsonString, SeckillSkuRedisTO.class);
                    Long currentTime = System.currentTimeMillis();
                    Long endTime = skuInfo.getEndTime();
                    if (currentTime <= endTime) {
                        // 当前时间小于截止时间
                        Long startTime = skuInfo.getStartTime();
                        if (currentTime >= startTime) {
                            // 返回当前正处于秒杀的商品信息
                            return skuInfo;
                        }
                        // 返回预告信息，不返回随机码
                        skuInfo.setRandomCode(null);// 随机码
                        return skuInfo;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 秒杀商品
     * 1.校验登录状态
     * 2.校验秒杀时间
     * 3.校验随机码、场次、商品对应关系
     * 4.校验信号量扣减，校验购物数量是否限购
     * 5.校验是否重复秒杀（幂等性）【秒杀成功SETNX占位  userId_sessionId_skuId】
     * 6.扣减信号量
     * 7.发送消息，创建订单号和订单信息
     * 8.订单模块消费消息，生成订单
     * @param killId    sessionId_skuid
     * @param key   随机码
     * @param num   商品件数
     */
    @Override
    public String kill(String killId, String key, Integer num) throws InterruptedException {
        // TODO 1.拦截器校验登录状态
        long start = System.currentTimeMillis();
        // 获取当前用户信息
        MemberResponseVO user = LoginUserInterceptor.loginUser.get();

        // 获取当前秒杀商品的详细信息
        BoundHashOperations<String, String, String> skuOps = redisTemplate.boundHashOps(SECKILL_CHARE_KEY);
        String jsonString = skuOps.get(killId);// 根据sessionId_skuid获取秒杀商品信息
        if (StringUtils.isEmpty(jsonString)) {
            // 这一步已经默认校验了场次+商品，如果为空表示校验失败
            return null;
        }
        // json反序列化商品信息
        SeckillSkuRedisTO skuInfo = JSON.parseObject(jsonString, SeckillSkuRedisTO.class);
        Long startTime = skuInfo.getStartTime();
        Long endTime = skuInfo.getEndTime();
        long currentTime = System.currentTimeMillis();
        // TODO 2.校验秒杀时间
        if (currentTime >= startTime && currentTime <= endTime) {
            // TODO 3.校验随机码
            String randomCode = skuInfo.getRandomCode();// 随机码
            if (randomCode.equals(key)) {
                // 获取每人限购数量
                Integer seckillLimit = skuInfo.getSeckillLimit();
                // 获取信号量
                String seckillCount = redisTemplate.opsForValue().get(SeckillConstant.SKU_STOCK_SEMAPHORE + randomCode);
                Integer count = Integer.valueOf(seckillCount);
                // TODO 4.校验信号量（库存是否充足）、校验购物数量是否限购
                if (num > 0 && num <= seckillLimit && count > num) {
                    // TODO 5.校验是否重复秒杀（幂等性）【秒杀成功后占位，userId-sessionId-skuId】
                    // SETNX 原子性处理
                    String userKey = SeckillConstant.SECKILL_USER_PREFIX + user.getId() + "_" + killId;
                    // 自动过期时间(活动结束时间 - 当前时间)
                    Long ttl = endTime - currentTime;
                    Boolean isRepeat = redisTemplate.opsForValue().setIfAbsent(userKey, num.toString(), ttl, TimeUnit.MILLISECONDS);
                    if (isRepeat) {
                        // 占位成功
                        // TODO 6.扣减信号量（防止超卖）
                        RSemaphore semaphore = redissonClient.getSemaphore(SeckillConstant.SKU_STOCK_SEMAPHORE + randomCode);
                        boolean isAcquire = semaphore.tryAcquire(num, 100, TimeUnit.MILLISECONDS);
                        if (isAcquire) {
                            // 信号量扣减成功，秒杀成功，快速下单
                            // TODO 7.发送消息，创建订单号和订单信息
                            // 秒杀成功 快速下单 发送消息到 MQ 整个操作时间在 10ms 左右
                            String orderSn = IdWorker.getTimeId();// 订单号
                            SeckillOrderTO order = new SeckillOrderTO();// 订单
                            order.setOrderSn(orderSn);// 订单号
                            order.setMemberId(user.getId());// 用户ID
                            order.setNum(num);// 商品上来给你
                            order.setPromotionSessionId(skuInfo.getPromotionSessionId());// 场次id
                            order.setSkuId(skuInfo.getSkuId());// 商品id
                            order.setSeckillPrice(skuInfo.getSeckillPrice());// 秒杀价格
                            // TODO 需要保证可靠消息，发送者确认+消费者确认（本地事务的形式）
                            rabbitTemplate.convertAndSend("order-event-exchange", "order.seckill.order", order);
                            long end = System.currentTimeMillis();
                            log.info("秒杀成功，耗时..." + (end - start));
                            return orderSn;
                        }
                    }
                }
            }
        }
        long end = System.currentTimeMillis();
        log.info("秒杀失败，耗时..." + (end - start));
        return null;
    }

    //public List<SeckillSkuRedisTo> blockHandler(BlockException e) {
    //    log.error("getCurrentSeckillSkusResource被限流了,{}", e.getMessage());
    //    return null;
    //}
    //



}
