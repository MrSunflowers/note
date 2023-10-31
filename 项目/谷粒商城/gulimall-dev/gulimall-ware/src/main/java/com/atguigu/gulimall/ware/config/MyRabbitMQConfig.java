package com.atguigu.gulimall.ware.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

/**
 * 创建队列，交换机，延时队列，绑定关系 的configuration
 * 1.Broker中的Queue、Exchange、Binding不存在的情况下，会自动创建（在RabbitMQ），不会重复创建覆盖
 * 2.懒加载，只有第一次使用的时候才会创建（例如监听队列）
 */
@Configuration
public class MyRabbitMQConfig {

    ///**
    // * 用于首次创建队列、交换机、绑定关系的监听
    // * @param message
    // */
    //@RabbitListener(queues = "stock.release.stock.queue")
    //public void handle(Message message) {
    //}

    /**
     * 交换机
     * Topic，可以绑定多个队列
     */
    @Bean
    public Exchange stockEventExchange() {
        //String name, boolean durable, boolean autoDelete, Map<String, Object> arguments
        return new TopicExchange("stock-event-exchange", true, false);
    }

    /**
     * 死信队列
     * 释放库存
     */
    @Bean
    public Queue stockReleaseStockQueue() {
        //String name, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
        return new Queue("stock.release.stock.queue", true, false, false);
    }

    /**
     * 延时队列
     * 锁定库存
     */
    @Bean
    public Queue stockDelay() {
        HashMap<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", "stock-event-exchange");
        arguments.put("x-dead-letter-routing-key", "stock.release");
        // 消息过期时间 1.5分钟
        arguments.put("x-message-ttl", 90000);
        return new Queue("stock.delay.queue", true, false, false, arguments);
    }

    /**
     * 绑定：交换机与死信队列
     * 释放库存
     */
    @Bean
    public Binding stockLocked() {
        //String destination, DestinationType destinationType, String exchange, String routingKey,
        // 			Map<String, Object> arguments
        return new Binding("stock.release.stock.queue",
                Binding.DestinationType.QUEUE,
                "stock-event-exchange",
                "stock.release.#",
                null);
    }

    /**
     * 绑定：交换机与延时队列
     * 锁定库存
     */
    @Bean
    public Binding stockLockedBinding() {
        return new Binding("stock.delay.queue",
                Binding.DestinationType.QUEUE,
                "stock-event-exchange",
                "stock.locked",
                null);
    }
}
