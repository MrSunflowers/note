package com.atguigu.gulimall.ware.listener;

import com.atguigu.common.to.mq.StockLockedTO;
import com.atguigu.common.to.order.OrderTO;
import com.atguigu.gulimall.ware.service.WareSkuService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 解锁库存，监听死信队列
 *
 * @author: wanzenghui
 **/
@Slf4j
@RabbitListener(queues = "stock.release.stock.queue")
@Component
public class StockReleaseListener {

    @Autowired
    private WareSkuService wareSkuService;

    /**
     * 库存解锁（监听死信队列）
     * 场景：
     * 1.下订单成功【需要解锁】(订单过期未支付、被用户手动取消、其他业务调用失败（订单回滚）)
     * 2.下订单失败【无需解锁】(库存锁定失败（库存锁定已回滚，但消息已发出）)
     * <p>
     * 注意：需要开启手动确认，不要删除消息，当前解锁失败需要重复解锁
     */
    @RabbitHandler
    public void handleStockLockedRelease(StockLockedTO locked, Message message, Channel channel) throws IOException {
        log.debug("库存解锁，库存工作单详情ID：" + locked.getDetail().getId());
        //当前消息是否重新派发过来
        // Boolean redelivered = message.getMessageProperties().getRedelivered();
        try {
            // 解锁库存
            wareSkuService.unLockStock(locked);
            // 解锁成功，手动确认
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            // 解锁失败，消息入队
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }

    /**
     * 客户取消订单，监听到消息
     */
    @RabbitHandler
    public void handleOrderCloseRelease(OrderTO orderTo, Message message, Channel channel) throws IOException {
        log.debug("订单关闭准备解锁库存，订单号：" + orderTo.getOrderSn());
        try {
            wareSkuService.unLockStock(orderTo);
            // 手动删除消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            // 解锁失败 将消息重新放回队列，让别人消费
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }
}
