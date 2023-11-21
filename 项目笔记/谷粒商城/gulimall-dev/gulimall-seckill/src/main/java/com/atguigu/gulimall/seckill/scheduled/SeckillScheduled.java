package com.atguigu.gulimall.seckill.scheduled;

import com.atguigu.common.constant.seckill.SeckillConstant;
import com.atguigu.gulimall.seckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 定时任务
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: wanzenghui
 * @createTime: 2020-07-09 19:22
 */
@Slf4j
@Service
public class SeckillScheduled {

    @Autowired
    SeckillService seckillService;
    @Autowired
    RedissonClient redissonClient;

    /**
     * 秒杀商品定时上架，保证幂等性问题
     *  每天晚上3点，上架最近三天需要秒杀的商品
     *  当天00:00:00 - 23:59:59
     *  明天00:00:00 - 23:59:59
     *  后天00:00:00 - 23:59:59
     */
    @Scheduled(cron = "*/10 * * * * ? ")
    //@Scheduled(cron = "0 0 3 * * ? ")
    public void uploadSeckillSkuLatest3Days() {
        // 重复上架无需处理
        log.info("上架秒杀的商品...");

        // 分布式锁（幂等性）
        RLock lock = redissonClient.getLock(SeckillConstant.UPLOAD_LOCK);
        try {
            lock.lock(10, TimeUnit.SECONDS);
            // 上架最近三天需要秒杀的商品
            seckillService.uploadSeckillSkuLatest3Days();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
