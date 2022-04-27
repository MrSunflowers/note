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

