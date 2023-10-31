package com.atguigu.gulimall.order;

import com.atguigu.common.entity.order.OrderEntity;
import com.atguigu.common.entity.order.OrderReturnReasonEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.UUID;

@Slf4j
@SpringBootTest
class GulimallOrderApplicationTests {

//    @Autowired
//    AmqpAdmin amqpAdmin;
//
    @Autowired
RabbitTemplate rabbitTemplate;
//
//    @Test
//    void createExchange() {
//        // 创建交换机
//        // String name, boolean durable, boolean autoDelete
//        DirectExchange exchange = new DirectExchange("hello-java-exchange", true, false);
//        amqpAdmin.declareExchange(exchange);
//        log.info("Exchange创建[{}]成功", "hello-java-exchange");
//    }
//
//    @Test
//    void createQueue() {
//        // 创建队列
//        // String name, boolean durable, boolean exclusive, boolean autoDelete
//        // exclusive：是否排他，true：只有一个连接可以使用此队列，其他连接无法连上此队列
//        Queue queue = new Queue("hello-java-queue", true, false, false);
//        amqpAdmin.declareQueue(queue);
//        log.info("Queue创建[{}]成功", "hello-java-queue");
//    }
//
//    @Test
//    void createBinding() {
//        // 创建绑定，交换机绑定目的地
//        // String destination：目的地name
//        // DestinationType destinationType：目的地类型【queue或exchange（路由）】
//        // String exchange：待绑定交换机
//        // String routingKey：路由键
//        Binding bind = new Binding("hello-java-queue", Binding.DestinationType.QUEUE,"hello-java-exchange", "hello.java", null);
//        amqpAdmin.declareBinding(bind);
//        log.info("Binding创建[{}]成功", "hello-java-binding");
//    }
//
//    @Test
//    void sendMsg() {
//        // 消息对象，可以是任意类型，类必须实现serializable，消息会以序列化的方式写入流中（如果使用JSON序列化器，则不需要类实现Serializable）
//        OrderReturnReasonEntity message = new OrderReturnReasonEntity();// 退货原因
//        message.setId(1L);
//        message.setCreateTime(new Date());
//        message.setName("哈哈");
//        // 消息ID
//        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
//        rabbitTemplate.convertAndSend("hello-java-exchange", "hello.java", message, correlationData);
//    }

//    @Test
    void sendMsg2() {
        // 该测试案例向同一队列发送了两个不同类型的消息对象
        // 消息一：
        OrderReturnReasonEntity message1 = new OrderReturnReasonEntity();// 退货原因
        message1.setId(1L);
        message1.setCreateTime(new Date());
        message1.setName("哈哈");
        CorrelationData correlationData1 = new CorrelationData(UUID.randomUUID().toString());// 消息ID
        rabbitTemplate.convertAndSend("hello-java-exchange", "hello.java", message1, correlationData1);

        // 消息二：
        OrderEntity message2 = new OrderEntity();// 退货原因
        message2.setId(1L);
        message2.setCreateTime(new Date());
        message2.setOrderSn("哈哈");
        CorrelationData correlationData2 = new CorrelationData(UUID.randomUUID().toString());// 消息ID
        rabbitTemplate.convertAndSend("hello-java-exchange", "hello.java", message2, correlationData2);
    }
}
