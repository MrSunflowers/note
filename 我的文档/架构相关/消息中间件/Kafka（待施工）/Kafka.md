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

## Topic 创建流程

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

### 客户端拦截器

生产者API在数据准备好发送给Kafka服务器之前，允许我们对生产的数据进行统一的处理，比如校验，整合数据等等。这些处理我们是可以通过Kafka提供的拦截器完成。因为拦截器不是生产者必须配置的功能，所以大家可以根据实际的情况自行选择使用。

但是要注意，这里的拦截器是可以配置多个的。执行时，会按照声明顺序执行完一个后，再执行下一个。并且某一个拦截器如果出现异常，只会跳出当前拦截器逻辑，并不会影响后续拦截器的处理。所以开发时，需要将拦截器的这种处理方法考虑进去。

`org.apache.kafka.clients.producer.ProducerInterceptor` 是 Apache Kafka 客户端库中用于生产者（Producer）的一个接口。它允许开发者在消息被发送到 Kafka 集群之前，对消息进行拦截、修改或监控。`ProducerInterceptor` 提供了一种机制，可以在消息的生命周期中插入自定义的逻辑，从而实现诸如消息过滤、转换、审计、监控等功能。

`ProducerInterceptor` 接口主要包含以下方法：

1. **`onSend(ProducerRecord<K, V> record)`**:
   - **描述**：在消息被序列化并分配分区之前调用。
   - **参数**：`ProducerRecord<K, V> record` 表示即将发送的消息记录。
   - **返回值**：可以返回修改后的 `ProducerRecord`，或者返回 `null` 来过滤掉该消息。
   - **用途**：用于修改消息内容、添加或修改消息头，或根据业务逻辑决定是否发送消息。

2. **`onAcknowledgement(RecordMetadata metadata, Exception exception)`**:
   - **描述**：在消息被 Kafka 集群确认（acknowledged）后调用，无论消息发送成功还是失败。
   - **参数**：
     - `RecordMetadata metadata`：表示消息的元数据，如主题、分区、偏移量等。
     - `Exception exception`：如果消息发送失败，这里会包含异常信息；否则为 `null`。
   - **用途**：用于记录消息发送的结果，进行性能监控、错误处理或审计。

3. **`close()`**:
   - **描述**：在拦截器关闭时调用，用于释放资源或执行清理操作。
   - **用途**：确保拦截器在关闭时能够正确地释放资源，避免资源泄漏。

以下是一个简单的 `ProducerInterceptor` 实现示例，用于在消息发送前添加一个时间戳到消息头中：

```java
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.Headers;

import java.util.Map;

public class TimestampInterceptor implements ProducerInterceptor<String, String> {

    @Override
    public void configure(Map<String, ?> configs) {
        // 配置参数（如果有的话）
    }

    @Override
    public ProducerRecord<String, String> onSend(ProducerRecord<String, String> record) {
        Headers headers = record.headers();
        headers.add("timestamp", String.valueOf(System.currentTimeMillis()).getBytes());
        return record;
    }

    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
        if (exception == null) {
            // 消息发送成功
            System.out.println("Message sent successfully to " + metadata.topic() + "-" + metadata.partition());
        } else {
            // 消息发送失败
            System.err.println("Failed to send message: " + exception.getMessage());
        }
    }

    @Override
    public void close() {
        // 清理资源（如果有的话）
    }
}
```

在 Kafka 生产者的配置中，可以通过 `interceptor.classes` 属性来配置拦截器。例如：

```java
Properties props = new Properties();
props.put("bootstrap.servers", "localhost:9092");
props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
props.put("interceptor.classes", "com.example.TimestampInterceptor");

Producer<String, String> producer = new KafkaProducer<>(props);
```

1. **性能影响**：拦截器中的逻辑可能会影响消息发送的性能，尤其是在高吞吐量的场景下。因此，应尽量保持拦截器的逻辑简洁高效。
2. **线程安全**：拦截器可能会被多个线程同时调用，因此实现时需要注意线程安全问题。
3. **异常处理**：在 `onSend` 和 `onAcknowledgement` 方法中，应妥善处理异常，避免因拦截器导致的生产者崩溃。

### 异步发送

Kafka发送数据时，底层的实现类似于生产者消费者模式。对应的，底层会由主线程代码作为生产者向缓冲区中放数据，而数据发送线程会从缓冲区中获取数据进行发送。Broker接收到数据后进行后续处理。

如果Kafka通过主线程代码将一条数据放入到缓冲区后，无需等待数据的后续发送过程，就直接发送一下条数据的场合，我们就称之为异步发送。

### 同步发送

如果Kafka通过主线程代码将一条数据放入到缓冲区后，需等待数据的后续发送操作的应答状态，才能发送一下条数据的场合，我们就称之为同步发送。所以这里的所谓同步，就是生产数据的线程需要等待发送线程的应答（响应）结果。

代码实现上，采用的是JDK1.5增加的JUC并发编程的Future接口的get方法实现。

### 指定分区(队列)发送

Kafka中Topic是对数据逻辑上的分类，而Partition才是数据真正存储的物理位置。所以在生产数据时，如果只是指定Topic的名称，其实Kafka是不知道将数据发送到哪一个Broker节点的。我们可以在构建数据传递Topic参数的同时，也可以指定数据存储的分区编号。

指定分区传递数据是没有任何问题的。Kafka会进行基本简单的校验，比如是否为空，是否小于0之类的，但是你指定的分区是否存在就无法判断了，所以需要从Kafka中获取集群元数据信息，此时可能会因为长时间获取不到元数据信息而出现超时异常。所以如果不能确定分区编号范围的情况，不指定分区也是一个不错的选择。

### 未指定分区发送

如果不指定分区，Kafka会根据集群元数据中的主题分区来通过算法来计算分区编号并设定：

- 如果指定了分区，直接使用
- 如果指定了自己的分区器，通过分区器计算分区编号，如果有效，直接使用
- 如果指定了数据Key，且使用Key选择分区的场合，采用murmur2非加密散列算法（类似于hash）计算数据Key序列化后的值的散列值，然后对主题分区数量模运算取余，最后的结果就是分区编号
- 如果未指定数据Key，或不使用Key选择分区，那么Kafka会采用优化后的粘性分区策略进行分区选择：
  - 没有分区数据加载状态信息时，会从分区列表中随机选择一个分区。
  - 如果存在分区数据加载状态信息时，根据分区数据队列加载状态，通过随机数获取一个权重值
  - 根据这个权重值在队列加载状态中进行二分查找法，查找权重值的索引值
  - 将这个索引值加1就是当前设定的分区。

增加数据后，会根据当前粘性分区中生产的数据量进行判断，是不是需要切换其他的分区。判断的标准就是大于等于批次大小（16K）的2倍，或大于一个批次大小（16K）且需要切换。如果满足条件，下一条数据就会放置到其他分区。

### 分区器

在某些场合中，指定的数据我们是需要根据自身的业务逻辑发往指定的分区的。所以需要自己定义分区编号规则，而不是采用Kafka自动设置就显得尤其必要了。Kafka早期版本中提供了两个分区器，不过在当前kafka版本中已经不推荐使用了。

配置分区器类

首先我们需要创建一个类，然后实现Kafka提供的分区类接口Partitioner，接下来重写方法。这里我们只关注partition方法即可，因为此方法的返回结果就是需要的分区编号。

```java
package com.atguigu.test;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

import java.util.Map;

/**
 * TODO 自定义分区器实现步骤：
 *      1. 实现Partitioner接口
 *      2. 重写方法
 *         partition : 返回分区编号，从0开始
 *         close
 *         configure
 */
public class KafkaPartitionerMock implements Partitioner {
    /**
     * 分区算法 - 根据业务自行定义即可
     * @param topic The topic name
     * @param key The key to partition on (or null if no key)
     * @param keyBytes The serialized key to partition on( or null if no key)
     * @param value The value to partition on or null
     * @param valueBytes The serialized value to partition on or null
     * @param cluster The current cluster metadata
     * @return 分区编号，从0开始
     */
    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        return 0;
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> configs) {

    }
}
```

## 可靠消息

对于生产者发送的数据，我们有的时候是不关心数据是否已经发送成功的，我们只要发送就可以了。在这种场景中，消息可能会因为某些故障或问题导致丢失，我们将这种情况称之为消息不可靠。虽然消息数据可能会丢失，但是在某些需要高吞吐，低可靠的系统场景中，这种方式也是可以接受的，甚至是必须的。

但是在更多的场景中，我们是需要确定数据是否已经发送成功了且Kafka正确接收到数据的，也就是要保证数据不丢失，这就是所谓的消息可靠性保证。

而这个确定的过程一般是通过Kafka给我们返回的响应确认结果（Acknowledgement）来决定的，这里的响应确认结果我们也可以简称为ACK应答。根据场景，Kafka提供了3种应答处理，可以通过配置对象进行配置

### ACK = 0

当生产数据时，生产者对象将数据通过网络客户端将数据发送到网络数据流中的时候，Kafka就对当前的数据请求进行了响应（确认应答），如果是同步发送数据，此时就可以发送下一条数据了。如果是异步发送数据，回调方法就会被触发。

这种应答方式，数据已经走网络给Kafka发送了，但这其实并不能保证Kafka能正确地接收到数据，在传输过程中如果网络出现了问题，那么数据就丢失了。也就是说这种应答确认的方式，数据的可靠性是无法保证的。不过相反，因为无需等待Kafka服务节点的确认，通信效率倒是比较高的，也就是系统吞吐量会非常高。

### ACK = 1

当生产数据时，Kafka Leader副本将数据接收到并写入到了日志文件后，就会对当前的数据请求进行响应（确认应答），如果是同步发送数据，此时就可以发送下一条数据了。如果是异步发送数据，回调方法就会被触发。

这种应答方式，数据已经存储到了分区Leader副本中，那么数据相对来讲就比较安全了，也就是可靠性比较高。之所以说相对来讲比较安全，就是因为现在只有一个节点存储了数据，而数据并没有来得及进行备份到follower副本，那么一旦当前存储数据的broker节点出现了故障，数据也依然会丢失。

### ACK = -1 (默认)

当生产数据时，Kafka Leader副本和Follower副本都已经将数据接收到并写入到了日志文件后，再对当前的数据请求进行响应（确认应答），如果是同步发送数据，此时就可以发送下一条数据了。如果是异步发送数据，回调方法就会被触发。

这种应答方式，数据已经同时存储到了分区Leader副本和follower副本中，那么数据已经非常安全了，可靠性也是最高的。此时，如果Leader副本出现了故障，那么follower副本能够开始起作用，因为数据已经存储了，所以数据不会丢失。

不过这里需要注意，如果假设我们的分区有5个**follower**副本，编号为 1，2，3，4，5

但是此时只有3个副本处于和Leader副本之间处于数据同步状态，那么此时分区就存在一个同步副本列表，我们称之为In Syn Replica，简称为ISR。此时，Kafka只要保证ISR中所有的4个副本接收到了数据，就可以对数据请求进行响应了。无需5个副本全部收到数据。

## 消息重发

由于网络或服务节点的故障，Kafka在传输数据时，可能会导致数据丢失，所以我们才会设置ACK应答机制，尽可能提高数据的可靠性。但其实在某些场景中，数据的丢失并不是真正地丢失，而是“虚假丢失”，比如咱们将ACK应答设置为1，也就是说一旦Leader副本将数据写入文件后，Kafka就可以对请求进行响应了。

此时，如果假设由于网络故障的原因，Kafka并没有成功将ACK应答信息发送给Producer，那么此时对于Producer来讲，以为kafka没有收到数据，所以就会一直等待响应，一旦超过某个时间阈值，就会发生超时错误，也就是说在Kafka Producer眼里，数据已经丢了

所以在这种情况下，kafka Producer会尝试对超时的请求数据进行重试(**retry**)操作。通过重试操作尝试将数据再次发送给Kafka。

如果此时发送成功，那么Kafka就又收到了数据，而这两条数据是一样的，也就是说，导致了数据的重复。

## 数据乱序

数据重试(**retry**)功能除了可能会导致数据重复以外，还可能会导致数据乱序。假设我们需要将编号为1，2，3的三条连续数据发送给Kafka。每条数据会对应于一个连接请求

此时，如果第一个数据的请求出现了故障，而第二个数据和第三个数据的请求正常，那么Broker就收到了第二个数据和第三个数据，并进行了应答。

为了保证数据的可靠性，此时，Kafka Producer 会将第一条数据重新放回到缓冲区的第一个。进行重试操作

如果重试成功，Broker收到第一条数据，你会发现。数据的顺序已经被打乱了。

## 数据幂等性

为了解决Kafka传输数据时，所产生的数据重复和乱序问题，Kafka引入了幂等性操作，所谓的幂等性，就是Producer同样的一条数据，无论向Kafka发送多少次，kafka都只会存储一条。注意，这里的同样的一条数据，指的不是内容一致的数据，而是指的不断重试的数据。

默认幂等性是不起作用的，所以如果想要使用幂等性操作，只需要在生产者对象的配置中开启幂等性配置即可

| **配置项**                                | **配置值** | **说明**                                         |
| ----------------------------------------- | ---------- | ------------------------------------------------ |
| **enable.idempotence**                    | true       | 开启幂等性                                       |
| **max.in.flight.requests.per.connection** | 小于等于5  | 每个连接的在途请求数，不能大于5，取值范围为[1,5] |
| **acks**                                  | all(-1)    | 确认应答，固定值，不能修改                       |
| **retries**                               | >0         | 重试次数，推荐使用Int最大值                      |

kafka是如何实现数据的幂等性操作呢，我们这里简单说一下流程：

开启幂等性后，为了保证数据不会重复，那么就需要给每一个请求批次的数据增加唯一性标识，kafka中，这个标识采用的是连续的序列号数字。

具体来说，每个生产者初始化时会被 Broker 分配一个Producer ID（PID），然后每条消息会有一个序列号（Sequence Number）。当生产者发送消息时，Broker 会检查这个 PID 和序列号，如果已经处理过这个序列号的消息，就会拒绝重复的消息，从而保证不会重复写入。

PID是在生产者初始化时由Broker分配的，而序列号是针对每个分区和每个PID递增的。也就是说，每个分区维护自己的序列号，这样即使同一个生产者发送到不同的分区，也能保证各自的顺序和唯一性。

在 Broker 中会给每一个分区记录生产者的生产状态：采用队列的方式缓存最近的 5 个批次数据。队列中的数据按照 seqnum 进行升序排列。这里的数字 5 是经过压力测试，均衡空间效率和时间效率所得到的值，所以为固定值，无法配置且不能修改。

如果 Borker 当前新的请求批次数据在缓存的 5 个旧的批次中存在相同的，那么说明有重复，当前批次数据不做任何处理。

如果 Broker 当前的请求批次数据在缓存中没有相同的，那么判断当前新的请求批次的序列号是否为缓存的最后一个批次的序列号加 1，如果是，说明是连续的，顺序没乱。那么继续，如果不是，那么说明数据已经乱了，发生异常。

Broker 根据异常返回响应，通知 Producer 进行重试。Producer 重试前，需要在缓冲区中将数据重新排序，保证正确的顺序后。再进行重试即可。

如果请求批次不重复，且有序，那么更新缓冲区中的批次数据。将当前的批次放置再队列的结尾，将队列的第一个移除，保证队列中缓冲的数据最多5个。

从上面的流程可以看出，Kafka 的幂等性是通过消耗时间和性能的方式提升了数据传输的有序和去重，在一些对数据敏感的业务中是十分重要的。但是通过原理，咱们也能明白，这种幂等性还是有缺陷的：

- 幂等性的 producer 仅做到单分区上的幂等性，即单分区消息有序不重复，多分区无法保证幂等性。
- 只能保持生产者单个会话的幂等性，无法实现跨会话的幂等性，也就是说如果一个 producer 挂掉再重启，那么重启前和重启后的 producer 对象会被当成两个独立的生产者，从而获取两个不同的独立的生产者 ID，导致 broker 端无法获取之前的状态信息，所以无法实现跨会话的幂等。要想解决这个问题，可以采用后续的事务功能。

## 数据事务

Kafka 的生产者事务（Transactional Producer）功能允许在多个主题和分区上以原子性的方式发送一组消息。这意味着这些消息要么全部成功发送，要么全部失败，从而确保数据的一致性和完整性。事务功能在 Kafka 0.11 版本中引入，并在后续版本中得到了增强。

为了实现事务，Kafka 引入了事务协调器（TransactionCoodinator）负责事务的处理，每个 Kafka 集群中都有一个事务协调器，负责管理事务的状态。事务协调器是一个特殊的 Kafka broker，负责跟踪事务的开始、提交和中止。

具体来说，生产者在初始化时，需要配置一个唯一的事务ID，类似于下面这样

```java
// TODO 配置事务ID
configMap.put( ProducerConfig.TRANSACTIONAL_ID_CONFIG, "my-tx-id");

```

### 初始化事务

生产者在开始一个事务之前，需要先调用 initTransactions() 方法。

首先，`initTransactions()` 方法会检查生产者是否已经配置了事务 ID（即 `TRANSACTIONAL_ID_CONFIG`）。如果没有配置事务 ID，调用 `initTransactions()` 会抛出异常，因为事务功能依赖于唯一的事务 ID。

接下来，生产者会尝试与 Kafka 集群中的事务协调器建立连接。事务协调器是一个特殊的 Kafka broker，负责管理事务的状态。

1. **查找事务协调器**：
   - 生产者会向 Kafka 集群发送一个查找请求，以确定哪个 broker 是当前事务 ID 的事务协调器。
   - Kafka 使用内部的主题 `__transaction_state` 来存储事务状态信息，事务协调器负责管理这个主题。

2. **建立连接**：
   - 一旦确定了事务协调器，生产者会与其建立连接，以便进行后续的事务操作。

3. **注册事务 ID**

生产者会向事务协调器发送一个注册请求，通知事务协调器一个新的事务 ID 正在被使用。如果事务 ID 已经被其他生产者使用，当前生产者会抛出异常，提示事务 ID 冲突。

- **事务日志更新**：
  - 事务协调器会在 `__transaction_state` 主题中创建一个新的事务状态记录，标记该事务 ID 为“正在初始化”。

4. **初始化事务状态**

事务协调器会为该事务 ID 初始化一个事务状态，包括：

- **事务超时时间**：设置事务的超时时间（默认为 60 秒）。
- **事务状态**：标记事务状态为“正在初始化”。

5. **等待事务协调器响应**

生产者会等待事务协调器的响应，确认事务 ID 已被成功注册和初始化。如果在指定的时间内未收到响应，生产者会抛出超时异常。










示例代码

```java
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import java.util.Properties;

public class TransactionalProducerExample {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "my-tx-id"); // 设置事务 ID
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");   // 启用幂等性

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        try {
            producer.initTransactions(); // 初始化事务
            producer.beginTransaction(); // 开始事务

            // 发送消息
            producer.send(new ProducerRecord<>("topic1", "key1", "value1"));
            producer.send(new ProducerRecord<>("topic2", "key2", "value2"));

            producer.commitTransaction(); // 提交事务
        } catch (Exception e) {
            producer.abortTransaction(); // 中止事务
            e.printStackTrace();
        } finally {
            producer.close(); // 关闭生产者
        }
    }
}
```






事务日志（Transaction Log）：事务协调器使用一个内部的主题（__transaction_state）来存储事务的状态信息。这个主题类似于消费者偏移量主题，用于持久化事务的状态。







，所有的事务逻辑包括分派 PID 等都是由 TransactionCoodinator 负责实施的。TransactionCoodinator 会将事务状态持久化到该主题中。




对于幂等性的缺陷，kafka 可以采用事务的方式解决跨会话的幂等性。基本的原理就是通过事务功能管理生产者 ID，保证事务开启后，生产者对象总能获取一致的生产者 ID。



事务基本的实现思路就是通过配置的事务ID，将生产者ID进行绑定，然后存储在Kafka专门管理事务的内部主题 __transaction_state中，而内部主题的操作是由事务协调器（TransactionCoodinator）对象完成的，这个协调器对象有点类似于咱们数据发送时的那个副本Leader。其实这种设计是很巧妙的，因为kafka将事务ID和生产者ID看成了消息数据，然后将数据发送到一个内部主题中。这样，使用事务处理的流程和咱们自己发送数据的流程是很像的。接下来，我们就把这两个流程简单做一个对比。





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









