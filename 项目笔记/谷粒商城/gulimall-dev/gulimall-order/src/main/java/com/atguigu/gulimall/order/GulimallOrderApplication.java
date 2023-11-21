package com.atguigu.gulimall.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

// 开启动态代理，使用aspectj作动态代理
@EnableAspectJAutoProxy(exposeProxy = true)
// 开启rabbit监听（不监听可以不加）
@EnableRabbit
// 开启SpringSession
@EnableRedisHttpSession
// 开启服务注册功能
@EnableDiscoveryClient
// 开启feign
@EnableFeignClients
// 扫描mapper
@MapperScan("com.atguigu.gulimall.order.dao")
// 扫描组件
@ComponentScan(basePackages = {"com.atguigu.common.utils", "com.atguigu.gulimall.order"})
// springboot启动类注解
@SpringBootApplication
public class GulimallOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallOrderApplication.class, args);
    }

}
