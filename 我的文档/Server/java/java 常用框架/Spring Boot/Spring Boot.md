# SpringBoot 简介

- Java8及以上
- Maven 3.3及以上

## Spring 5 与 SpringBoot 2

&emsp;&emsp;从 Spring 5 开始，Spring 从底层基于 java 8 的新特性进行了大量升级改造，实现了对于响应式编程的底层支持，也就是说，从 Spring 5 开始，编写 web 项目被拆分为了两个技术栈分支，一个是以 spring、springMVC 以及 mybatis 为代表的传统开发模式。另一个是基于响应式开发的响应式 web 应用技术栈，它可以像 node 一样，通过占用少量的线程资源，实现高并发需求。这也是 SpringBoot 1 与 SpringBoot 2 最大的区别。

![Spring的两种技术栈](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203212051258.png)

&emsp;&emsp;SpringBoot 的存在意义就是约定大于配置，用内部大量的默认配置，将整个 Spring 技术体系整合，例如像 Spring Session 分布式 Session 共享解决方案、Spring Security 安全、Spring AMQP 消息队列等等 Spring 的生态技术全部集成于一体而免于配置，从而快速进行应用的开发。

- 创建独立Spring应用
- 内嵌web服务器
- 自动starter依赖，简化构建配置
- 自动配置Spring以及第三方功能
- 提供生产级别的监控、健康检查及外部化配置
- 无代码生成、无需编写XML

> SpringBoot是整合Spring技术栈的一站式框架
> SpringBoot是简化Spring技术栈的快速开发脚手架

## 微服务

- 微服务是一种架构风格
- 一个应用拆分为一组小型服务
- 每个服务运行在自己的进程内，也就是可独立部署和升级
- 服务之间使用轻量级HTTP交互
- 服务围绕业务功能拆分
- 可以由全自动部署机制独立部署
- 去中心化，服务自治。服务可以使用不同的语言、不同的存储技术

## 分布式

SpringBoot + SpringCloud

- 远程调用
- 服务发现
- 负载均衡
- 服务容错
- 配置管理
- 服务监控
- 链路追踪
- 日志管理
- 任务调度

## 云原生

原生应用如何上云。 Cloud Native

上云的困难
- 服务自愈
- 弹性伸缩
- 服务隔离
- 自动化部署
- 灰度发布
- 流量治理

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203212100549.png)

## 官网文档架构

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203212058414.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203212058062.png)

# 示例程序

## maven 全局设置

```xml
<mirrors>
      <mirror>
        <id>nexus-aliyun</id>
        <mirrorOf>central</mirrorOf>
        <name>Nexus aliyun</name>
        <url>https://maven.aliyun.com/nexus/content/groups/public</url>
      </mirror>
  </mirrors>
 
  <profiles>
         <profile>
              <id>jdk-1.8</id>
              <activation>
                <activeByDefault>true</activeByDefault>
                <jdk>1.8</jdk>
              </activation>
              <properties>
                <maven.compiler.source>1.8</maven.compiler.source>
                <maven.compiler.target>1.8</maven.compiler.target>
                <maven.compiler.compilerVersion>1.8</maven.compiler.compilerVersion>
              </properties>
         </profile>
  </profiles>
```

## 创建项目

配置pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>

<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.6.4</version>
  </parent>

  <groupId>org.example</groupId>
  <artifactId>SpringBootDemo</artifactId>
  <version>1.0-SNAPSHOT</version>
  <packaging>war</packaging>

  <name>SpringBootDemo Maven Webapp</name>

  <properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <maven.compiler.source>1.8</maven.compiler.source>
    <maven.compiler.target>1.8</maven.compiler.target>
  </properties>

  <dependencies>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
  </dependencies>

</project>
```

## 启动类

&emsp;&emsp;To finish our application, we need to create a single Java file. By default, Maven compiles sources from `src/main/java`, so you need to create that directory structure and then add a file named `src/main/java/MyApplication.java` to contain the following code:

```java
@RestController
@EnableAutoConfiguration
//这里默认扫描当前类所在的包
public class MyApplication {

    @RequestMapping("/")
    String home() {
        return "Hello World!";
    }

    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }

}
```

## 配置文件

&emsp;&emsp;默认加载 resources 文件夹下的 application.properties 文件

```properties
server.port=8888
```

[配置项索引](https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html#appendix.application-properties)

## 部署

&emsp;&emsp;SpringBoot 提供一种简便的部署方式，通过将整个 Spring 项目环境打包为可执行 jar 包，然后在命令行直接启动，不再需要部署至服务器，因为环境中已经包含了所需的服务。

在 pom 文件添加 maven 插件，此时 pom 文件不再需要指示项目打包方式。

```
<build>
    <plugins>
      <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
      </plugin>
    </plugins>
  </build>
```

&emsp;&emsp;通过 `maven package` 打包 `java -jar` 运行即可。
