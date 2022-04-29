# Spring源码笔记：准备工作

## 项目构建

&emsp;&emsp;Spring 源代码在 GitHub 上，在 Github 主页搜索框搜索 Spring 找到 `spring-projects/spring-framework` 复制其 HTTPS 连接，使用 Git clone 到本地，这里用的是 JDK11。

&emsp;&emsp;找一个空文件夹，右键选项 git bash here，点击这个选项，启动git的命令行程序

```bash
git config --global user.name "youname" 设置密码
git config --global user.email "aa@qq.com" 设置邮箱
git init   初始化一个空的git本地仓库
```

&emsp;&emsp;开始 clone

```bash
git clone https://github.com/spring-projects/spring-framework.git
```

&emsp;&emsp;网络不稳定出错的话尝试

```bash
git config --global http.sslVerify "false"
```

&emsp;&emsp;执行git命令脚本：修改设置，解除ssl验证，再次执行 clone 操作即可。

&emsp;&emsp;使用 IDEA 打开 clone 的项目，等待构建完成即可，初次构建时间可能比较长，主要都是在下载构建需要的环境。

```bash
BUILD SUCCESSFUL in 21s
```

&emsp;&emsp;构建成功，项目构建完成。

## 测试案例

&emsp;&emsp;一个最简单的Spring使用案例。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean name="springTestDemoBean" class="com.SpringDemo.SpringTestDemoBean" >
        <property name="name" value="zhangsan" />
    </bean>

</beans>
```

```java
public class main {
    public static void main(String[] args) {
        Resource resource = new ClassPathResource("applicationContext.xml");
        BeanFactory beanFactory = new XmlBeanFactory(resource);
        SpringTestDemoBean springTestDemoBean = (SpringTestDemoBean) beanFactory.getBean(SpringTestDemoBean.class);
        springTestDemoBean.SpringTestDemoBeanTest();

    }
}
```

&emsp;&emsp;直接使用 BeanFactory 作为容器对于 Spring 的使用来说并不多见，因为在企业的应用中大多数都会使用的是 ApplicationContext。

## 解决Spirng源码启动出现乱码问题

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/spring/202111121012927.png)
