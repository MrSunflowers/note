[TOC]

# JMS

Java Message Service（JMS）是Java平台中用于消息传递的API。它类似于 JDBC，允许应用程序之间通过消息传递进行异步通信。JMS提供了一种标准化的方式来创建、发送、接收和读取消息，从而实现松耦合的、可靠的、异步的通信。

JMS 规定了两种消息传递模式

1. **点对点模式（Point-to-Point）**：
   - 消息被发送到一个队列中，一个消息只能被一个消费者接收。
   - 适用于需要确保消息被处理的场景。

2. **发布/订阅模式（Publish/Subscribe）**：
   - 消息被发布到一个主题中，多个消费者可以订阅该主题并接收消息。
   - 适用于广播消息给多个接收者的场景。

基本使用步骤类似于 JDBC，有一套固定的规范：

1. **创建连接工厂（ConnectionFactory）**：
   - 用于创建与消息中间件的连接。

2. **创建连接（Connection）**：
   - 使用连接工厂创建连接。

3. **创建会话（Session）**：
   - 使用连接创建会话，指定事务和确认模式。

4. **创建目的地（Destination）**：
   - 创建队列或主题作为消息的目的地。

5. **创建消息生产者或消费者**：
   - 根据需要创建消息生产者发送消息，或创建消息消费者接收消息。

6. **发送或接收消息**：
   - 使用消息生产者发送消息，或使用消息消费者接收和处理消息。

7. **关闭资源**：
   - 关闭会话、连接等资源。

以下是一个简单的JMS点对点消息传递示例：

```java
import javax.jms.*;
import javax.naming.InitialContext;

public class JMSExample {
    public static void main(String[] args) {
        try {
            // 初始化JNDI上下文
            InitialContext ctx = new InitialContext();

            // 查找连接工厂和目的地
            ConnectionFactory connectionFactory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
            Destination queue = (Destination) ctx.lookup("myQueue");

            // 创建连接和会话
            Connection connection = connectionFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // 创建消息生产者
            MessageProducer producer = session.createProducer(queue);

            // 创建消息
            TextMessage message = session.createTextMessage("Hello, JMS!");

            // 发送消息
            producer.send(message);
            System.out.println("Message sent: " + message.getText());

            // 关闭资源
            producer.close();
            session.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

# Kafka

Apache Kafka 是一个分布式的流处理平台和消息系统，它并没有完全实现 Java Message Service（JMS）的相关协议和规范。尽管 Kafka 和 JMS 都用于消息传递，但 Kafka 仅支持发布/订阅一种消息传递模式，其更专注与实现超高吞吐场景。

Kafka 主要特点是基于 Pull 的模式来处理消息消费, 追求高吞吐量, 一开始的目的就是用于日志收集和传输, 适合产生大量数据的互联网服务的数据收集业务。大型公司建议可以选用, 如果有日志采集功能, 肯定是首选 kafka 了。

大数据的杀手锏, 谈到大数据领域内的消息传输, 则绕不开 Kafka, 这款为大数据而生的消息中间件, 以其百万级 TPS 的吞吐量名声大噪, 迅速成为大数据领域的宠儿, 在数据采集、传输、存储的过程中发挥着举足轻重的作用。目前已经被 LinkedIn, Uber, Twitter, Netflix 等大公司所采纳。

优点: 性能卓越, 单机写入TPS约在百万条/秒, 最大的优点, 就是吞吐量高。时效性ms级可用性非常高, kafka是分布式的, 一个数据多个副本, 少数机器宕机, 不会丢失数据, 不会导致不可用, 消费者采用Pull方式获取消息, 消息有序, 通过控制能够保证所有消息被消费且仅被消费一次，有优秀的第三方 Kafka Web 管理界面 Kafka-Manager；在日志领域比较成熟, 被多家公司和多个开源项目使用；功能支持：功能较为简单, 主要支持简单的MQ功能, 在大数据领域的实时计算以及日志采集被大规模使用

缺点：Kafka 单机超过 64 个分区, Load 会发生明显的 CPU 飙高现象, 分区越多, load 越高, 发送消息响应时间变长, 使用短轮询方式, 实时性取决于轮询间隔时间, **消费失败不支持重试；支持消息顺序, 但是一台代理宕机后, 就会产生消息乱序**, 社区更新较慢；

# 安装手册

## Kafka 与 ZooKeeper

Kafka 软件依赖 ZooKeeper 来实现协调调度，其实，Kafka 作为一个独立的分布式消息传输系统，还需要第三方软件进行节点间的协调调度，不能实现自我管理，无形中就导致 Kafka 和其他软件之间形成了耦合性，制约了 Kafka 软件的发展，所以从 Kafka 2.8.X 版本开始，Kafka 就尝试增加了 Raft 算法实现节点间的协调管理，来代替 ZooKeeper。不过 Kafka 官方不推荐此方式应用在生产环境中，计划在 Kafka 4.X 版本中完全移除 ZooKeeper，让我们拭目以待。

本文安装手册基于 Kafka_2.12_3.6.1 版本编写，Kafka 是基于 Scala 语言和 Java 语言开发，其中 2.12 代表 Scala 语言版本，3.6.1 为 Kafka 版本，该版本可运行于 Java 8 环境中，但官方建议运行在 Java 11 版本，未来 Kafka 4.X 版本会完全弃用 Java 8。

Kafka 的绝大数代码都是Scala语言编写的，而 Scala 语言本身就是基于 Java 语言开发的，并且由于 Kafka 内置了 Scala 语言包，所以 Kafka 是可以直接运行在JVM上的，无需安装其他软件。

注意事项：2.8.0版本前，必须安装对应的 zookeeper，2.8.0 以后可以选择性安装 zookeeper。

由于当前版本软件内部依然依赖 zookeeper 进行多节点协调调度，所以启动 Kafka 软件之前，需要先启动 ZooKeeper 软件。不过因为 Kafka 软件本身内置了ZooKeeper，用脚本命令启动即可。

下载软件安装包：kafka_2.12-3.6.1.tgz，下载地址：https://kafka.apache.org/downloads

## 目录结构

解压后

| 目录         | 说明   |
| ----------- | --------------------------- |
| bin         | linux系统下可执行脚本文件   |
| bin/windows | windows系统下可执行脚本文件 |
| config      | 配置文件                    |
| libs        | 依赖类库                    |
| licenses    | 许可信息                    |
| site-docs   | 文档                        |
| logs        | 服务日志                    |

## windows 环境







# Kafka Java API 示例

## 生产者

创建生产者 maven 项目，并添加依赖

```xml
<dependencies>
    <dependency>
        <groupId>org.apache.kafka</groupId>
        <artifactId>kafka-clients</artifactId>
        <version>3.6.1</version>
    </dependency>
</dependencies>
```

示例方法

```java
package com.atguigu.kafka.test;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import java.util.HashMap;
import java.util.Map;
public class KafkaProducerTest {
    public static void main(String[] args) {
        // TODO 配置属性集合
        Map<String, Object> configMap = new HashMap<>();
        // TODO 配置属性：Kafka服务器集群地址
        configMap.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        // TODO 配置属性：Kafka生产的数据为KV对，所以在生产数据进行传输前需要分别对K,V进行对应的序列化操作
        configMap.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");
        configMap.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");
        // TODO 创建Kafka生产者对象，建立Kafka连接
        //      构造对象时，需要传递配置参数
        KafkaProducer<String, String> producer = new KafkaProducer<>(configMap);
        // TODO 准备数据,定义泛型
        //      构造对象时需要传递 【Topic主题名称】，【Key】，【Value】三个参数
        ProducerRecord<String, String> record = new ProducerRecord<String, String>(
                "test", "key1", "value1"
        );
        // TODO 生产（发送）数据
        producer.send(record);
        // TODO 关闭生产者连接
        producer.close();
    }
}
```

## 消费者

```java
package com.atguigu.kafka.test;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
public class KafkaConsumerTest {
    public static void main(String[] args) {
        // TODO 配置属性集合
        Map<String, Object> configMap = new HashMap<String, Object>();
        // TODO 配置属性：Kafka集群地址
        configMap.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        // TODO 配置属性: Kafka传输的数据为KV对，所以需要对获取的数据分别进行反序列化
        configMap.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
        configMap.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
        // TODO 配置属性: 读取数据的位置 ，取值为earliest（最早），latest（最晚）
        configMap.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");
        // TODO 配置属性: 消费者组
        configMap.put("group.id", "atguigu");
        // TODO 配置属性: 自动提交偏移量
        configMap.put("enable.auto.commit", "true");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(configMap);
        // TODO 消费者订阅指定主题的数据
        consumer.subscribe(Collections.singletonList("test"));
        while ( true ) {
            // TODO 每隔100毫秒，抓取一次数据
            ConsumerRecords<String, String> records =
                consumer.poll(Duration.ofMillis(100));
            // TODO 打印抓取的数据
            for (ConsumerRecord<String, String> record : records) {
                System.out.println("K = " + record.key() + ", V = " + record.value());
            }
        }
    }
}
```

# Kafka 集群概述

## Broker

使用Kafka前，我们都会启动Kafka服务进程，这里的Kafka服务进程我们一般会称之为Kafka Broker或Kafka Server。因为Kafka是分布式消息系统，所以在实际的生产环境中，是需要多个服务进程形成集群提供消息服务的。所以每一个服务节点都是一个broker，而且在Kafka集群中，为了区分不同的服务节点，每一个broker都应该有一个不重复的全局ID，称之为broker.id，这个ID可以在kafka软件的配置文件server.properties中进行配置。

```properties
############################# Server Basics #############################

# The id of the broker. This must be set to a unique integer for each broker
# 集群ID
broker.id=0
```

咱们的Kafka集群中每一个节点都有自己的ID，整数且唯一。

| 配置项     | kafka-broker1 | kafka-broker2 | kafka-broker3 |
| --------- | ------------- | ------------- | ------------- |
| broker.id | 1             | 2             | 3             |

## Controller

Kafka是分布式消息传输系统，所以存在多个Broker服务节点，但是它的软件架构采用的是分布式系统中比较常见的**主从（Master - Slave）架构**，也就是说需要从多个Broker中找到一个用于管理整个Kafka集群的Master节点，这个节点，我们就称之为Controller。它是Apache Kafka的核心组件非常重要。它的主要作用是在Apache Zookeeper的帮助下管理和协调控制整个Kafka集群。

如果在运行过程中，Controller 节点出现了故障，那么Kafka会依托于ZooKeeper软件选举其他的节点作为新的Controller，让Kafka集群实现高可用。

### Controller 节点的选举算法

1. 第一次启动Kafka集群时，会同时启动多个Broker节点，每一个Broker节点就会连接ZooKeeper，并尝试创建一个临时节点 `/controller`
2. 因为ZooKeeper中一个节点不允许重复创建，所以多个Broker节点，最终只能有一个Broker节点可以创建成功，那么这个创建成功的Broker节点就会自动作为Kafka集群控制器节点，用于管理整个Kafka集群。
3. 没有选举成功的其他Slave节点会创建Node监听器，用于监听 `/controller`节点的状态变化。
4. 一旦Controller节点出现故障或挂掉了，那么对应的ZooKeeper客户端连接就会中断。ZooKeeper中的 `/controller` 节点就会自动被删除，而其他的那些Slave节点因为增加了监听器，所以当监听到 `/controller` 节点被删除后，就会马上向ZooKeeper发出创建 `/controller` 节点的请求，一旦创建成功，那么该Broker就变成了新的Controller节点了。

# 主题 Topic

Topic主题是Kafka中消息的逻辑分类，但是这个分类不应该是固定的，而是应该由外部的业务场景进行定义（注意：Kafka中其实是有两个固定的，用于记录消费者偏移量和事务处理的主题），所以Kafka提供了相应的指令和客户端进行主题操作。

在 Kafka 中，并没有实现原生的 P2P 消息发送方式，也没有交换机的概念，所有的消息都是通过发布订阅模式来传递的。

为了对消费者订阅的消息进行区分，所以对消息在逻辑上进行了分类，这个分类我们称之为主题：Topic。消息的生产者必须将消息数据发送到某一个主题，而消费者必须从某一个主题中获取消息，并且消费者可以同时消费一个或多个主题的数据。Kafka集群中可以存放多个主题的消息数据。

为了防止主题的名称和监控指标的名称产生冲突，官方推荐主题的名称中不要同时包含下划线和点。

## 分区：Partition

Kafka消息传输采用发布、订阅模式，所以消息生产者必须将数据发送到一个主题，假如发送给这个主题的数据非常多，那么主题所在broker节点的负载和吞吐量就会受到极大的考验，甚至有可能因为热点问题引起broker节点故障，导致服务不可用。一个好的方案就是将一个主题从物理上分成几块，然后将不同的数据块均匀地分配到不同的broker节点上，这样就可以缓解单节点的负载问题。这个主题的分块我们称之为：分区partition。默认情况下，topic主题创建时分区数量为1，也就是一块分区，可以指定参数`--partitions`改变。Kafka的分区解决了单一主题topic线性扩展的问题，也解决了负载均衡的问题。

topic主题的每个分区都会用一个编号进行标记，一般是从0开始的连续整数数字。Partition分区是物理上的概念，也就意味着会以数据文件的方式真实存在。每个topic包含一个或多个partition，每个partition都是一个有序的队列。partition中每条消息都会分配一个有序的ID，称之为偏移量：Offset

分区也可以理解为队列。

## 分区副本：Replication

分布式系统出现错误是比较常见的，只要保证集群内部依然存在可用的服务节点即可，当然效率会有所降低，不过只要能保证系统可用就可以了。咱们Kafka的topic也存在类似的问题，也就是说，如果一个topic划分了多个分区partition，那么这些分区就会均匀地分布在不同的broker节点上，一旦某一个broker节点出现了问题，那么在这个节点上的分区就会出现问题，那么Topic的数据就不完整了。所以一般情况下，为了防止出现数据丢失的情况，我们会给分区数据设定多个备份，这里的备份，我们称之为：分区副本Replication。

Kafka支持多副本，使得主题topic可以做到更多容错性，牺牲性能与空间去换取更高的可靠性。

注意：这里不能将多个备份放置在同一个broker中，因为一旦出现故障，多个副本就都不能用了，那么副本的意义就没有了。

## 分区副本类型：Leader & Follower

假设我们有一份文件，一般情况下，我们对副本的理解应该是有一个正式的完整文件，然后这个文件的备份，我们称之为副本。但是在Kafka中，不是这样的，所有的文件都称之为副本，只不过会选择其中的一个文件作为主文件，称之为：Leader(主导)副本，其他的文件作为备份文件，称之为：Follower（追随）副本。在Kafka中，这里的文件就是分区，每一个分区都可以存在1个或多个副本，只有Leader副本才能进行数据的读写，Follower副本只做备份使用。

## 日志 Log

Kafka 最开始的应用场景就是日志场景或MQ场景，更多的扮演着一个日志传输和存储系统，这是Kafka立家之本。所以Kafka接收到的消息数据最终都是存储在log日志文件中的，底层存储数据的文件的扩展名就是log。

主题创建后，会创建对应的分区数据Log日志。并打开文件连接通道，随时准备写入数据。

## Topic 的创建

创建主题Topic的方式有很多种：命令行，工具，客户端 API，自动创建。在 server.properties 文件中配置参数 auto.create.topics.enable=true 时，如果访问的主题不存在，那么 Kafka 就会自动创建主题。

一般来讲，新增主题操作只有 Kafka 管理员才有权限，其余一律不允许私自创建，因为只有管理员才了解集群的运行情况。

新增主题时，当没有配置分区和副本参数，所以当前主题分区数量为默认值1，编号为0，副本为1，编号为所在broker的ID值。为了方便集群的管理，创建topic时，会同时在ZK中增加子节点，记录主题相关配置信息。

`/config/topics` 节点中会增加first-topic节点。
`/brokers/topics` 节点中会增加first-topic节点以及相应的子节点。

| **节点**                               | **节点类型** | **数据名称**                                                 | **数据值** | **说明**                                                |
| -------------------------------------- | ------------ | ------------------------------------------------------------ | ---------- | ------------------------------------------------------- |
| /topics/first-topic                    | 持久类型     | removing_replicas                                            | 无         |                                                         |
| partitions                             | {"0":[3]}    | 分区配置                                                     |            |                                                         |
| topic_id                               | 随机字符串   |                                                              |            |                                                         |
| adding_replicas                        | 无           |                                                              |            |                                                         |
| version                                | 3            |                                                              |            |                                                         |
| /topics/first-topic/partitions         | 持久类型     |                                                              |            | 主题分区节点，每个主题都应该设置分区，保存在该节点      |
| /topics/first-topic/partitions/0       | 持久类型     |                                                              |            | 主题分区副本节点，因为当前主题只有一个分区，所以编号为0 |
| /topics/first-topic/partitions/0/state | 持久类型     | controller_epoch                                             | 7          | 主题分区副本状态节点                                    |
| leader                                 | 3            | Leader副本所在的broker  Id                                   |            |                                                         |
| version                                | 1            |                                                              |            |                                                         |
| leader_epoch                           | 0            |                                                              |            |                                                         |
| isr                                    | [3]          | 副本同步列表，因为当前只有一个副本，所以副本中只有一个副本编号 |            |                                                         |

## 数据存储位置

主题创建后，需要找到一个用于存储分区数据的位置，根据上面ZooKeeper存储的节点配置信息可以知道，当前主题的分区数量为1，副本数量为1，那么数据存储的位置就是副本所在的broker节点，

路径中的00000000000000000000.log文件就是真正存储消息数据的文件，文件名称中的0表示当前文件的起始偏移量为0，目录下的`.index`文件和`.timeindex`文件都是数据索引文件，用于快速定位数据。只不过index文件采用偏移量的方式进行定位，而timeindex是采用时间戳的方式。

## 主题创建流程

以命令行提交创建指令为例

### 指令解析

- 通过命令行提交指令，指令中会包含操作类型（--create）、topic的名称（--topic）、主题分区数量（--partitions）、主题分区副本数量（--replication-facotr）、副本分配策略（--replica-assignment）等参数。
- 指令会提交到客户端进行处理，客户端获取指令后，会首先对指令参数进行校验。
  - 操作类型取值：create、list、alter、describe、delete，只能存在一个。
  - 分区数量为大于1的整数。
  - 主题是否已经存在
  - 分区副本数量大于1且小于Short.MaxValue，一般取值小于等于Broker数量。
- 将参数封装主题对象（NewTopic）。
- 创建通信对象，设定请求标记（CREATE_TOPICS），查找Controller，通过通信对象向Controller发起创建主题的网络请求。

### Controller接收创建主题请求

- Controller节点接收到网络请求（Acceptor），并将请求数据封装成请求对象放置在队列（requestQueue）中。
- 请求控制器（KafkaRequestHandler）周期性从队列中获取请求对象（BaseRequest）。
- 将请求对象转发给请求处理器（KafkaApis），根据请求对象的类型调用创建主题的方法。

### 创建主题 & 分区/分区副本分配算法

1. 请求处理器（KafkaApis）校验主题参数

- 如果分区数量没有设置，那么会采用Kafka启动时加载的配置项：num.partitions（默认值为1）
- 如果副本数量没有设置，那么会采用Kafka启动时记载的配置项：default.replication.factor（默认值为1）

2. 创建主题

在创建主题时，如果使用了replica-assignment参数，那么就按照指定的方案来进行分区副本的创建；如果没有指定replica-assignment参数，那么就按照Kafka内部逻辑来分配，内部逻辑按照机架信息分为两种策略：【未指定机架信息】和【指定机架信息】。当前课程中采用的是【未指定机架信息】副本分配策略：

- 分区起始索引设置0
- 轮询所有分区，计算每一个分区的所有副本位置：
  - 副本起始索引 = （分区编号 + 随机值） %  BrokerID列表长度。
  - 其他副本索引 = 。。。随机值（基本算法为使用随机值执行多次模运算）
```
##################################################################
# 假设 
#     当前分区编号 : 0
#     BrokerID列表 :【1，2，3，4】
#     副本数量 : 4
#     随机值（BrokerID列表长度）: 2
#     副本分配间隔随机值（BrokerID列表长度）: 2
##################################################################
# 第一个副本索引：（分区编号 + 随机值）% BrokerID列表长度 =（0 + 2）% 4 = 2
# 第一个副本所在BrokerID : 3

# 第二个副本索引（第一个副本索引 + （1 +（副本分配间隔 + 0）% （BrokerID列表长度 - 1））） % BrokerID列表长度 = （2 +（1+（2+0）%3））% 4 = 1
# 第二个副本所在BrokerID：2

# 第三个副本索引：（第一个副本索引 + （1 +（副本分配间隔 + 1）% （BrokerID列表长度 - 1））） % BrokerID列表长度 = （2 +（1+（2+1）%3））% 4 = 3
# 第三个副本所在BrokerID：4

# 第四个副本索引：（第一个副本索引 + （1 +（副本分配间隔 + 2）% （BrokerID列表长度 - 1））） % BrokerID列表长度 = （2 +（1+（2+2）%3））% 4 = 0
# 第四个副本所在BrokerID：1

# 最终分区0的副本所在的Broker节点列表为【3，2，4，1】
# 其他分区采用同样算法
```
- 通过索引位置获取副本节点ID
- 保存分区以及对应的副本ID列表。

3. 通过ZK客户端在ZK端创建节点

- 在 /config/topics节点下，增加当前主题节点，节点类型为持久类型。
- 在 /brokers/topics节点下，增加当前主题及相关节点，节点类型为持久类型。

4. 增加监听器

- Controller节点启动后，会在/brokers/topics节点增加监听器，一旦节点发生变化，会触发相应的功能：
  - 获取需要新增的主题信息
  - 更新当前Controller节点保存的主题状态数据
  - 更新分区状态机的状态为：NewPartition
  - 更新副本状态机的状态：NewReplica
  - 更新分区状态机的状态为：OnlinePartition，从正常的副本列表中的获取第一个作为分区的Leader副本，所有的副本作为分区的同步副本列表，我们称之为ISR( In-Sync Replica)。在ZK路径/brokers/topics/主题名上增加分区节点/partitions，及状态/state节点。
  - 更新副本状态机的状态：OnlineReplica

5. 更新缓存

- Controller节点向主题的各个分区副本所属Broker节点发送LeaderAndIsrRequest请求，向所有的Broker发送UPDATE_METADATA请求，更新自身的缓存
  - Controller向分区所属的Broker发送请求
  - Broker节点接收到请求后，根据分区状态信息，设定当前的副本为Leader或Follower，并创建底层的数据存储文件目录和空的数据文件。

文件目录名：主题名 + 分区编号

| **文件名**                 | **说明**                     |
| -------------------------- | ---------------------------- |
| 0000000000000000.log       | 数据文件，用于存储传输的小心 |
| 0000000000000000.index     | 索引文件，用于定位数据       |
| 0000000000000000.timeindex | 时间索引文件，用于定位数据   |

# 消息生产者 Producer

## 环境准备

这里我们采用Java代码通过Kafka Producer API的方式生产数据。

创建Map类型的配置对象，根据场景增加相应的配置属性

| **参数名**                            | **参数作用**                                                 | **类型** | **默认值** | **推荐值**                                  |
| ------------------------------------- | ------------------------------------------------------------ | -------- | ---------- | ------------------------------------------- |
| bootstrap.servers                     | 集群地址，格式为：  brokerIP1:端口号,brokerIP2:端口号        | 必须     |            |                                             |
| key.serializer                        | 对生产数据Key进行序列化的类完整名称                          | 必须     |            | Kafka提供的字符串序列化类：StringSerializer |
| value.serializer                      | 对生产数据Value进行序列化的类完整名称                        | 必须     |            | Kafka提供的字符串序列化类：StringSerializer |
| interceptor.classes                   | 拦截器类名，多个用逗号隔开                                   | 可选     |            |                                             |
| batch.size                            | 数据批次字节大小。此大小会和数据最大估计值进行比较，取大值。  估值=61+21+（keySize+1+valueSize+1+1） | 可选     | 16K        |                                             |
| retries                               | 重试次数                                                     | 可选     | 整型最大值 | 0或整型最大值                               |
| request.timeout.ms                    | 请求超时时间                                                 | 可选     | 30s        |                                             |
| linger.ms                             | 数据批次在缓冲区中停留时间                                   | 可选     |            |                                             |
| acks                                  | 请求应答类型：all(-1),  0, 1                                 | 可选     | all(-1)    | 根据数据场景进行设置                        |
| retry.backoff.ms                      | 两次重试之间的时间间隔                                       | 可选     | 100ms      |                                             |
| buffer.memory                         | 数据收集器缓冲区内存大小                                     | 可选     | 32M        | 64M                                         |
| max.in.flight.requests.per.connection | 每个节点连接的最大同时处理请求的数量                         | 可选     | 5          | 小于等于5                                   |
| enable.idempotence                    | 幂等性，                                                     | 可选     | true       | 根据数据场景进行设置                        |
| partitioner.ignore.keys               | 是否放弃使用数据key选择分区                                  | 可选     | false      |                                             |
| partitioner.class                     | 分区器类名                                                   | 可选     | null       |                                             |

## 创建待发送数据

在kafka中传递的数据我们称之为消息（message）或记录(record)，所以Kafka发送数据前，需要将待发送的数据封装为指定的数据模型：

相关属性必须在构建数据模型时指定，其中主题和value的值是必须要传递的。如果配置中开启了自动创建主题，那么Topic主题可以不存在。value就是我们需要真正传递的数据了，而Key可以用于数据的分区定位。

## 创建生产者对象，发送生产的数据

根据前面提供的配置信息创建生产者对象，通过这个生产者对象向Kafka服务器节点发送数据，而具体的发送是由生产者对象创建时，内部构建的多个组件实现的，多个组件的关系有点类似于生产者消费者模式。

### 数据生产者

-数据生产者（KafkaProducer）：生产者对象，用于对我们的数据进行必要的转换和处理，将处理后的数据放入到数据收集器中，类似于生产者消费者模式下的生产者。这里我们简单介绍一下内部的数据转换处理：

- 如果配置拦截器栈（interceptor.classes），那么将数据进行拦截处理。某一个拦截器出现异常并不会影响后续的拦截器处理。
- 因为发送的数据为KV数据，所以需要根据配置信息中的序列化对象对数据中Key和Value分别进行序列化处理。
- 计算数据所发送的分区位置。
- 将数据追加到数据收集器中。

### 数据收集器

数据收集器（RecordAccumulator）：用于收集，转换我们产生的数据，类似于生产者消费者模式下的缓冲区。为了优化数据的传输，Kafka并不是生产一条数据就向Broker发送一条数据，而是通过合并单条消息，进行批量（批次）发送，提高吞吐量，减少带宽消耗。

- 默认情况下，一个发送批次的数据容量为16K，这个可以通过参数batch.size进行改善。
- 批次是和分区进行绑定的。也就是说发往同一个分区的数据会进行合并，形成一个批次。
- 如果当前批次能容纳数据，那么直接将数据追加到批次中即可，如果不能容纳数据，那么会产生新的批次放入到当前分区的批次队列中，这个队列使用的是Java的双端队列Deque。旧的批次关闭不再接收新的数据，等待发送

### 数据发送器

数据发送器（Sender）：线程对象，用于从收集器对象中获取数据，向服务节点发送。类似于生产者消费者模式下的消费者。因为是线程对象，所以启动后会不断轮询获取数据收集器中已经关闭的批次数据。对批次进行整合后再发送到Broker节点中

- 因为数据真正发送的地方是Broker节点，不是分区。所以需要将从数据收集器中收集到的批次数据按照可用Broker节点重新组合成List集合。
- 将组合后的<节点，List<批次>>的数据封装成客户端请求（请求键为：Produce）发送到网络客户端对象的缓冲区，由网络客户端对象通过网络发送给Broker节点。
- Broker节点获取客户端请求，并根据请求键进行后续的数据处理：向分区中增加数据。

## 发送消息

























# 核心概念

- 消息生产者 Producer
- 消息 record
- 消息中间件服务器 Broker
- 消息主题 Topic
- 消息消费者组
- 消息消费者 Consumer

在 Kafka 中，并没有实现原生的 P2P 消息发送方式，也没有交换机的概念，所有的消息都是通过发布订阅模式来传递的。





# 高吞吐设计理论基础

kafka 利用一系列的优化措施来提高极限情况的吞吐量，在极限情况下的数据处理吞吐可达到 TB/小时 级别，其中涉及到的技术非常值得研究。

## 顺序读写

kafka 以文件的形式来记录接收的消息，源源不断的消息将被记录称为一个个的文件

将消息写入磁盘的过程是吞吐的瓶颈所在，而在顺序写入的模式下，机械硬盘与固态硬盘的差距并不大，kafka 利用此特性来想磁盘写入数据

当消息被消费后数据并不立即进行删除操作，而是当消息所在的文件都被消费时进行统一批量删除

## 页缓存

kafka 不再使用 JVM 内存来缓存收到的消息，原因是为了避免在 GC 时的延迟。转而使用操作系统的页缓存来进行消息缓存。

## 零拷贝

略

## 批量处理









