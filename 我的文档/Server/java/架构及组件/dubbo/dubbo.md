[TOC]

# Dubbo

[官方文档](https://dubbo.apache.org/zh/docs/v3.0/references/)

&emsp;&emsp;随着互联网的发展，网站应用的规模不断扩大，常规的垂直应用架构已无法应对，分布式服务架构以及流动计算架构势在必行，急需一个治理系统，确保架构有条不紊的演进。

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202204091749535.png)

- 阶段一 ：**单一应用架构**
  - 当网站流量很小时，只需一个应用，将所有功能都部署在一起，以减少部署节点和成本。此时，用于简化增删改查工作量的数据访问框架 (ORM) 是关键，对应图中 ORM 节点。
  ![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202204091758953.png)
- 阶段二 ：**垂直应用架构**
  - 当访问量逐渐增大，单一应用增加机器带来的加速度越来越小，将应用拆成互不相干的几个应用，以提升效率，此时，用于加速前端页面开发的 Web 框架 (MVC) 是关键，对应图中 MVC 节点。
  ![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202204091758622.png)
- 阶段三 ：**分布式服务架构**
  - 当垂直应用越来越多，应用之间交互不可避免，将核心业务抽取出来，作为独立的服务，逐渐形成稳定的服务中心，使前端应用能更快速的响应多变的市场需求，此时，用于提高业务复用及整合的分布式服务框架 (RPC) 是关键，对应图中 RPC 节点。
  ![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202204091759213.png)
- 阶段四 ：**流动计算架构**
  - 当服务越来越多，容量的评估，小服务资源的浪费等问题逐渐显现，此时需增加一个调度中心基于访问压力实时管理集群容量，提高集群利用率，此时，用于提高机器利用率的资源调度和治理中心 (SOA) 是关键，对应图中 SOA 节点。
  ![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202204091759280.png)

&emsp;&emsp;Dubbo 就是类似于 webservice 的关于系统之间通信的框架，并可以统计和管理服务之间的调用情况（包括服务被谁调用了，调用的次数是如何，以及服务的使用状况）。

&emsp;&emsp;RPC是指**远程过程调用**，是一种进程间通信方式，他是一种技术的思想，而不是规范。它允许程序调用另一个地址空间（通常是共享网络的另一台机器上）的过程或函数，而不用程序员显式编码这个远程调用的细节。即程序员无论是调用本地的还是远程的函数，本质上编写的调用代码基本相同，也就是说两台服务器A，B，一个应用部署在A服务器上，想要调用 B 服务器上应用提供的函数/方法，由于不在一个内存空间，不能直接调用，需要通过网络来表达调用的语义和传达调用的数据。

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202204091801112.png)

&emsp;&emsp;影响 RPC 效率的两个核心模块：**通讯**，**序列化**。dubbo 提供了三大核心能力：
- 面向接口的远程方法调用
- 智能容错和负载均衡
- 服务自动注册和发现

# 基本概念

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202204091804887.png)

- 服务提供者（Provider）：暴露服务的服务提供方，服务提供者在启动时，向注册中心注册自己提供的服务。
- 服务消费者（Consumer）: 调用远程服务的服务消费方，服务消费者在启动时，向注册中心订阅自己所需的服务，服务消费者，从提供者地址列表中，基于软负载均衡算法，选一台提供者进行调用，如果调用失败，再选另一台调用。
- 注册中心（Registry）：注册中心返回服务提供者地址列表给消费者，如果有变更，注册中心将基于长连接推送变更数据给消费者
- 监控中心（Monitor）：服务消费者和提供者，在内存中累计调用次数和调用时间，定时每分钟发送一次统计数据到监控中心

## 调用关系说明

- 服务容器负责启动，加载，运行服务提供者。
- 服务提供者在启动时，向注册中心注册自己提供的服务。
- 服务消费者在启动时，向注册中心订阅自己所需的服务。
- 注册中心返回服务提供者地址列表给消费者，如果有变更，注册中心将基于长连接推送变更数据给消费者。
- 服务消费者，从提供者地址列表中，基于软负载均衡算法，选一台提供者进行调用，如果调用失败，再选另一台调用。
- 服务消费者和提供者，在内存中累计调用次数和调用时间，定时每分钟发送一次统计数据到监控中心。

# 服务的注册与发现

&emsp;&emsp;Dubbo 官方支持 5 中注册方式，分别为：
- **Nacos 注册中心** ：官方推荐的注册中心
- **Zookeeper 注册中心** ：官方推荐的注册中心
- **Multicast 方式注册** ：基于广播形式实现，不需要启动任何中心节点，组播受网络结构限制，只适合小规模应用或开发阶段使用。组播地址段: 224.0.0.0 - 239.255.255.255
- **Redis 注册中心** ： 并没有经过长时间运行的可靠性验证，其稳定性依赖于Redis本身
- **Simple 注册中心**：Simple 注册中心本身就是一个普通的 Dubbo 服务，可以减少第三方依赖，使整体通讯方式一致，不支持集群，可作为自定义注册中心的参考，但不适合直接用于生产环境。

## zookeeper 

&emsp;&emsp;这里使用 zookeeper 作为注册中心。

# 管理控制台

## 启动 dubbo-admin-server

&emsp;&emsp;进入 dubbo 的 github 选择[管理控制台项目](https://github.com/MrSunflowers/dubbo-admin) fork 下来之后打开其中的 `dubbo-admin-server` maven 项目，用 idea 打开。这个是dubbo-admin的后端程序(提供restful接口给前端)；

&emsp;&emsp;配置项目文件 `application.properties` 中 `zookeeper` 的服务器

```properties
admin.registry.address=zookeeper://192.168.1.110:2181
admin.config-center=zookeeper://192.168.1.111:2181
admin.metadata-report.address=zookeeper://192.168.1.112:2181
#防止连接超时导致的连接不上
dubbo.registry.timeout=15000
```

&emsp;&emsp;运行clean 和 package 打包项目，由于 maven 版本问题可能会出问题，将 idea 的 maven 设置为跳过测试，打包完成。运行 `java -jar *.jar` 运行 jar 包。

&emsp;&emsp;更改 zookeeper 驱动版本与服务器一致

```xml
<dependency>
    <groupId>org.apache.zookeeper</groupId>
    <artifactId>zookeeper</artifactId>
    <version>3.7.0</version>
</dependency>
```

&emsp;&emsp;更改运行环境为 java 8 ，启动成功

## 启动 dubbo-admin-ui

&emsp;&emsp;这个模块就是 dubbo-admin 的页面代码，使用了 vue 框架，依赖的数据都是通过 http 请求 dubbo-admin-server 来获取

&emsp;&emsp;修改 `vue.config.js` 请求的服务器端口

```js
const path = require('path');

module.exports = {
  outputDir: "target/dist",
  lintOnSave: "warning",
  devServer: {
    port: 8082,
    historyApiFallback: {
      rewrites: [
        {from: /.*/, to: path.posix.join('/', 'index.html')},
      ],
    },
    publicPath: '/',
    proxy: {
      '/': {
        target: 'http://localhost:8080/',
        changeOrigin: true,
        pathRewrite: {
          '^/': '/'
        }
      }
    }
  },
  configureWebpack: {
    devtool: process.env.NODE_ENV === 'dev' ? 'source-map' : undefined,
    performance: {
      hints: false
    },
    optimization: {
      splitChunks: {
        cacheGroups: {
          reactBase: {
            name: 'braceBase',
            test: (module) => {
              return /brace/.test(module.context);
            },
            chunks: 'initial',
            priority: 10,
          },
          common: {
            name: 'vendor',
            chunks: 'initial',
            priority: 2,
            minChunks: 2,
          },
        }
      }
    }
  }
};

```

&emsp;&emsp;启动 vue 项目

```shell
# 进入到dubbo-admin-ui目录下
# 安装相关依赖
npm install
# 启动vue项目
npm run dev
```

&emsp;&emsp;访问 http://localhost:8082/ 输入密码 root root 进入

# 使用

&emsp;&emsp;为了使项目更加立体，将所有规定的接口和实体类封装为 `core.jar` 包，来供所有的模块依赖和使用，这样也就解决了应用分开但还需要依赖其他模块的接口的问题。

pom文件配置

```xml
<properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <maven.compiler.source>1.8</maven.compiler.source>
    <maven.compiler.target>1.8</maven.compiler.target>
    <curator.version>5.2.1</curator.version>
  </properties>

  <dependencies>
    <dependency>
      <groupId>org.apache.dubbo</groupId>
      <artifactId>dubbo</artifactId>
      <version>3.0.7</version>
    </dependency>
    <dependency>
      <groupId>org.example</groupId>
      <artifactId>core</artifactId>
      <version>1.0-SNAPSHOT</version>
    </dependency>
    <dependency>
      <groupId>org.apache.curator</groupId>
      <artifactId>curator-framework</artifactId>
      <version>${curator.version}</version>
    </dependency>
    <dependency>
      <groupId>org.apache.curator</groupId>
      <artifactId>curator-recipes</artifactId>
      <version>${curator.version}</version>
    </dependency>
    <dependency>
      <groupId>org.apache.curator</groupId>
      <artifactId>curator-x-discovery</artifactId>
      <version>${curator.version}</version>
    </dependency>
  </dependencies>
```

由于使用 zookeeper 作为注册中心，所以需要操作 zookeeper

- dubbo 2.6 以前的版本引入 zkclient 操作 zookeeper 

- dubbo 2.6 及以后的版本引入 curator 操作 zookeeper

zk 客户端根据 dubbo 版本2选1即可

## 普通使用

服务提供者配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:dubbo="http://code.alibabatech.com/schema/dubbo"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://code.alibabatech.com/schema/dubbo http://code.alibabatech.com/schema/dubbo/dubbo.xsd">

    <!--1.应用名称-->
    <dubbo:application name="dao" />
    <!--2.注册中心-->
    <dubbo:registry address="zookeeper://192.168.1.110:2181?backup=192.168.1.111:2181,192.168.1.112:2181" timeout="15000" />
    <!--3.通信协议以及端口-->
    <dubbo:protocol name="dubbo" port="20080" />
    <!--4.暴露服务-->
    <dubbo:service interface="org.example.interfaces.dao.UserDao" ref="userDao" />
    <!--服务的实现-->
    <bean id="userDao" class="org.example.daoImpl.UserDaoImpl" />
</beans>

```

服务消费者配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:dubbo="http://code.alibabatech.com/schema/dubbo"
       xmlns:content="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://code.alibabatech.com/schema/dubbo http://code.alibabatech.com/schema/dubbo/dubbo.xsd http://www.springframework.org/schema/context https://www.springframework.org/schema/context/spring-context.xsd">

    <!--<content:component-scan base-package="org.example.serviceImpl" />-->

    <!--1.应用名称-->
    <dubbo:application name="service" />
    <!--2.注册中心-->
    <dubbo:registry address="zookeeper://192.168.1.110:2181?backup=192.168.1.111:2181,192.168.1.112:2181" timeout="15000" />
    <!--3.通信协议以及端口-->
    <dubbo:protocol name="dubbo" port="20080" />
    <!--4.引用服务-->
    <dubbo:reference  interface="org.example.interfaces.dao.UserDao" id="userDao" />

    <bean name="userServiceImpl" class="org.example.serviceImpl.UserServiceImpl" >
        <property name="userDao" ref="userDao" />
    </bean>
</beans>
```

## 使用注解

**坑坑坑！！！注意 xml 头命名空间**

服务提供者配置

```java
package org.example.daoImpl;
import com.alibaba.dubbo.config.annotation.Service;
import org.example.interfaces.dao.UserDao;
import org.example.interfaces.pojo.User;

@Service
public class UserDaoImpl implements UserDao {
    @Override
    public User getUser() {
        return new User("user",12);
    }
}
```

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:dubbo="http://dubbo.apache.org/schema/dubbo"
       xmlns:content="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://dubbo.apache.org/schema/dubbo
       http://dubbo.apache.org/schema/dubbo/dubbo.xsd
       http://www.springframework.org/schema/context
       https://www.springframework.org/schema/context/spring-context.xsd">

    <content:component-scan base-package="org.example.daoImpl" />
    <dubbo:annotation package="org.example.daoImpl"/>
    <!--1.应用名称-->
    <dubbo:application name="dao" />
    <!--2.注册中心-->
    <dubbo:registry address="zookeeper://192.168.1.110:2181?backup=192.168.1.111:2181,192.168.1.112:2181" timeout="15000" />
    <!--3.通信协议以及端口-->
    <dubbo:protocol name="dubbo" port="20080" />

    <!--4.暴露服务-->
    <!--<dubbo:service interface="org.example.interfaces.dao.UserDao" ref="userDao" />-->
    <!--服务的实现-->
    <!--<bean id="userDao" class="org.example.daoImpl.UserDaoImpl" />-->
</beans>
```

服务消费者配置

```java
package org.example.serviceImpl;

import com.alibaba.dubbo.config.annotation.Reference;
import org.example.interfaces.dao.UserDao;
import org.example.interfaces.pojo.User;
import org.example.interfaces.service.UserService;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service
public class UserServiceImpl implements UserService, Serializable {

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Reference
    UserDao userDao;

    @Override
    public User getUser() {
        return userDao.getUser();
    }
}

```

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:dubbo="http://dubbo.apache.org/schema/dubbo"
       xmlns:content="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://dubbo.apache.org/schema/dubbo
       http://dubbo.apache.org/schema/dubbo/dubbo.xsd
       http://www.springframework.org/schema/context
       https://www.springframework.org/schema/context/spring-context.xsd">

    <content:component-scan base-package="org.example.serviceImpl" />
    <dubbo:annotation package="org.example.serviceImpl"/>

    <!--1.应用名称-->
    <dubbo:application name="service" />
    <!--2.注册中心-->
    <dubbo:registry address="zookeeper://192.168.1.110:2181?backup=192.168.1.111:2181,192.168.1.112:2181" timeout="15000" />
    <!--3.通信协议以及端口-->
    <dubbo:protocol name="dubbo" port="20080" />
    <!--4.引用服务-->
   <!-- <dubbo:reference  interface="org.example.interfaces.dao.UserDao" id="userDao" />

    <bean name="userServiceImpl" class="org.example.serviceImpl.UserServiceImpl" >
        <property name="userDao" ref="userDao" />
    </bean>-->
</beans>
```

## 完全使用注解方式

[官方示例](https://github.com/apache/dubbo-samples/tree/master/dubbo-samples-annotation/src/main/java/org/apache/dubbo/samples/annotation)

## 整合 SpringBoot

[Apache Dubbo Spring Boot Project](https://github.com/apache/dubbo-spring-boot-project)

引入 spring-boot-starter以及dubbo和curator的依赖

```xml
<dependency>
    <groupId>com.alibaba.boot</groupId>
    <artifactId>dubbo-spring-boot-starter</artifactId>
    <version>0.2.0</version>
</dependency>
```

注意starter版本适配：

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202205011639972.png)

配置application.properties

**提供者配置**

```properties
dubbo.application.name=gmall-user
dubbo.registry.protocol=zookeeper
dubbo.registry.address=192.168.67.159:2181
dubbo.scan.base-package=com.atguigu.gmall
dubbo.protocol.name=dubbo
```

application.name就是服务名，不能跟别的dubbo提供端重复

registry.protocol 是指定注册中心协议

registry.address 是注册中心的地址加端口号

protocol.name 是分布式固定是dubbo,不要改。

base-package 注解方式要扫描的包

**消费者配置**

```properties
dubbo.application.name=gmall-order-web
dubbo.registry.protocol=zookeeper
dubbo.registry.address=192.168.67.159:2181
dubbo.scan.base-package=com.atguigu.gmall
dubbo.protocol.name=dubbo
```

**dubbo注解**

@Service、@Reference
如果没有在配置中写dubbo.scan.base-package,还需要使用@EnableDubbo注解

# dubbo配置

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202205011643976.png)

1. JVM 启动 -D 参数优先，这样可以使用户在部署和启动时进行参数重写，比如在启动时需改变协议的端口。
2. XML 次之，如果在 XML 中有配置，则 dubbo.properties 中的相应配置项无效。
3. Properties 最后，相当于缺省值，只有 XML 没有配置时，dubbo.properties 的相应配置项才会生效，通常用于共享公共配置，比如应用名。

## 重试次数

失败自动切换，当出现失败，重试其它服务器，但重试会带来更长延迟。可通过 retries="2" 来设置重试次数(不含第一次)。

```xml
重试次数配置如下：
<dubbo:service retries="2" />
或
<dubbo:reference retries="2" />
或
<dubbo:reference>
    <dubbo:method name="findFoo" retries="2" />
</dubbo:reference>
```

## 超时时间

由于网络或服务端不可靠，会导致调用出现一种不确定的中间状态（超时）。为了避免超时导致客户端资源（线程）挂起耗尽，必须设置超时时间。

**Dubbo消费端**

```xml
全局超时配置
<dubbo:consumer timeout="5000" />

指定接口以及特定方法超时配置
<dubbo:reference interface="com.foo.BarService" timeout="2000">
    <dubbo:method name="sayHello" timeout="3000" />
</dubbo:reference>
```

**Dubbo服务端** 

```xml
<dubbo:provider timeout="5000" />

指定接口以及特定方法超时配置
<dubbo:provider interface="com.foo.BarService" timeout="2000">
    <dubbo:method name="sayHello" timeout="3000" />
</dubbo:provider>
```

## 配置原则

dubbo推荐在Provider上尽量多配置Consumer端属性：

1. 作服务的提供者，比服务使用方更清楚服务性能参数，如调用的超时时间，合理的重试次数，等等
2. 在Provider配置后，Consumer不配置则会使用Provider的配置值，即Provider配置可以作为Consumer的缺省值。否则，Consumer会使用Consumer端的全局设置，这对于Provider不可控的，并且往往是不合理的

配置的覆盖规则：

1. 方法级配置别优于接口级别，即小Scope优先 
2. Consumer端配置 优于 Provider配置 优于 全局配置
3. 最后是Dubbo Hard Code的配置值（见配置文档）

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202205011653122.png)

## 版本号

当一个接口实现，出现不兼容升级时，可以用版本号过渡，版本号不同的服务相互间不引用。可以按照以下的步骤进行版本迁移：

1. 在低压力时间段，先升级一半提供者为新版本
2. 再将所有消费者升级为新版本
3. 然后将剩下的一半提供者升级为新版本

```xml
老版本服务提供者配置：
<dubbo:service interface="com.foo.BarService" version="1.0.0" />

新版本服务提供者配置：
<dubbo:service interface="com.foo.BarService" version="2.0.0" />

老版本服务消费者配置：
<dubbo:reference id="barService" interface="com.foo.BarService" version="1.0.0" />

新版本服务消费者配置：
<dubbo:reference id="barService" interface="com.foo.BarService" version="2.0.0" />

如果不需要区分版本，可以按照以下的方式配置：
<dubbo:reference id="barService" interface="com.foo.BarService" version="*" />
```

# 高可用

## zookeeper宕机与dubbo直连

当 zookeeper 注册中心宕机，还可以消费 dubbo 暴露的服务

1. 监控中心宕掉不影响使用，只是丢失部分采样数据
2. 数据库宕掉后，注册中心仍能通过缓存提供服务列表查询，但不能注册新服务
3. 注册中心对等集群，任意一台宕掉后，将自动切换到另一台
4. **注册中心全部宕掉后，服务提供者和服务消费者仍能通过本地缓存通讯**
5. 服务提供者无状态，任意一台宕掉后，不影响使用
6. 服务提供者全部宕掉后，服务消费者应用将无法使用，并无限次重连等待服务提供者恢复

高可用：通过设计，减少系统不能提供服务的时间

## 集群下dubbo负载均衡配置

在集群负载均衡时，Dubbo 提供了多种均衡策略，缺省为 random 随机调用

**负载均衡策略**

- Random LoadBalance
  随机，按权重设置随机概率。
  在一个截面上碰撞的概率高，但调用量越大分布越均匀，而且按概率使用权重后也比较均匀，有利于动态调整提供者权重。
- RoundRobin LoadBalance
  轮循，按公约后的权重设置轮循比率。
  存在慢的提供者累积请求的问题，比如：第二台机器很慢，但没挂，当请求调到第二台时就卡在那，久而久之，所有请求都卡在调到第二台上。
- LeastActive LoadBalance
  最少活跃调用数，相同活跃数的随机，活跃数指调用前后计数差。
  使慢的提供者收到更少请求，因为越慢的提供者的调用前后计数差会越大。
- ConsistentHash LoadBalance
  一致性 Hash，相同参数的请求总是发到同一提供者。
  当某一台提供者挂时，原本发往该提供者的请求，基于虚拟节点，平摊到其它提供者，不会引起剧烈变动。算法参见：http://en.wikipedia.org/wiki/Consistent_hashing
  缺省只对第一个参数 Hash，如果要修改，请配置 <dubbo:parameter key="hash.arguments" value="0,1" />
  缺省用 160 份虚拟节点，如果要修改，请配置 <dubbo:parameter key="hash.nodes" value="320" />

## 整合hystrix，服务熔断与降级处理

### 服务降级

**当服务器压力剧增的情况下，根据实际业务情况及流量，对一些服务和页面有策略的不处理或换种简单的方式处理，从而释放服务器资源以保证核心交易正常运作或高效运作**

可以通过服务降级功能临时屏蔽某个出错的非关键服务，并定义降级后的返回策略

向注册中心写入动态配置覆盖规则：

```java
RegistryFactory registryFactory = ExtensionLoader.getExtensionLoader(RegistryFactory.class).getAdaptiveExtension();
Registry registry = registryFactory.getRegistry(URL.valueOf("zookeeper://10.20.153.10:2181"));
registry.register(URL.valueOf("override://0.0.0.0/com.foo.BarService?category=configurators&dynamic=false&application=foo&mock=force:return+null"));
```

其中:

- mock=force:return+null 表示消费方对该服务的方法调用都直接返回 null 值，不发起远程调用。用来屏蔽不重要服务不可用时对调用方的影响
- 还可以改为 mock=fail:return+null 表示消费方对该服务的方法调用在失败后，再返回 null 值，不抛异常。用来容忍不重要服务不稳定时对调用方的影响

### 集群容错

在集群调用失败时，Dubbo 提供了多种容错方案，缺省为 failover 重试

**集群容错模式**

- Failover Cluster
  失败自动切换，当出现失败，重试其它服务器。通常用于读操作，但重试会带来更长延迟。可通过 retries="2" 来设置重试次数(不含第一次)。

```xml
重试次数配置如下：
<dubbo:service retries="2" />
或
<dubbo:reference retries="2" />
或
<dubbo:reference>
    <dubbo:method name="findFoo" retries="2" />
</dubbo:reference>
```

- Failfast Cluster
  快速失败，只发起一次调用，失败立即报错。通常用于非幂等性的写操作，比如新增记录。

- Failsafe Cluster
  失败安全，出现异常时，直接忽略。通常用于写入审计日志等操作。

- Failback Cluster
  失败自动恢复，后台记录失败请求，定时重发。通常用于消息通知操作。

- Forking Cluster
  并行调用多个服务器，只要一个成功即返回。通常用于实时性要求较高的读操作，但需要浪费更多服务资源。可通过 forks="2" 来设置最大并行数。

- Broadcast Cluster
  广播调用所有提供者，逐个调用，任意一台报错则报错 [2]。通常用于通知所有提供者更新缓存或日志等本地资源信息。

**集群模式配置**

按照以下示例在服务提供方和消费方配置集群模式

```xml
<dubbo:service cluster="failsafe" />
或
<dubbo:reference cluster="failsafe" />
```

### 整合hystrix

Hystrix 旨在通过控制那些访问远程系统、服务和第三方库的节点，从而对延迟和故障提供更强大的容错能力。Hystrix具备拥有回退机制和断路器功能的线程和信号隔离，请求缓存和请求打包，以及监控和配置等功能

**配置spring-cloud-starter-netflix-hystrix**

spring boot官方提供了对hystrix的集成，直接在pom.xml里加入依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
    <version>1.4.4.RELEASE</version>
</dependency>
```

然后在Application类上增加@EnableHystrix来启用hystrix starter

```java
@SpringBootApplication
@EnableHystrix
public class ProviderApplication {
```

**配置Provider端**

在Dubbo的Provider上增加@HystrixCommand配置，这样子调用就会经过Hystrix代理

```java
@Service(version = "1.0.0")
public class HelloServiceImpl implements HelloService {
    @HystrixCommand(commandProperties = {
     @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "10"),
     @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000") })
    @Override
    public String sayHello(String name) {
        // System.out.println("async provider received: " + name);
        // return "annotation: hello, " + name;
        throw new RuntimeException("Exception to show hystrix enabled.");
    }
}
```

**配置Consumer端**

对于Consumer端，则可以增加一层method调用，并在method上配置@HystrixCommand。当调用出错时，会走到fallbackMethod = "reliable"的调用里

```java
 @Reference(version = "1.0.0")
    private HelloService demoService;

    @HystrixCommand(fallbackMethod = "reliable")
    public String doSayHello(String name) {
        return demoService.sayHello(name);
    }
    public String reliable(String name) {
        return "hystrix fallback value";
    }
```

# dubbo原理

## RPC原理

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202205011708337.png)

```
一次完整的RPC调用流程（同步调用，异步另说）如下： 
1）服务消费方（client）调用以本地调用方式调用服务； 
2）client stub接收到调用后负责将方法、参数等组装成能够进行网络传输的消息体； 
3）client stub找到服务地址，并将消息发送到服务端； 
4）server stub收到消息后进行解码； 
5）server stub根据解码结果调用本地的服务； 
6）本地服务执行并将结果返回给server stub； 
7）server stub将返回结果打包成消息并发送至消费方； 
8）client stub接收到消息，并进行解码； 
9）服务消费方得到最终结果。
RPC框架的目标就是要2~8这些步骤都封装起来，这些细节对用户来说是透明的，不可见的。
```

## netty通信原理

Netty是一个异步事件驱动的网络应用程序框架， 用于快速开发可维护的高性能协议服务器和客户端。它极大地简化并简化了TCP和UDP套接字服务器等网络编程

见dox文档
