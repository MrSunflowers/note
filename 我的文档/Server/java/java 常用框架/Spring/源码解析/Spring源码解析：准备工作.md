# Spring源码解析：准备工作



## 源代码的获取

​		Spring 源代码在 GitHub 上，在 Github 主页搜索框搜索 Spring 找到 `spring-projects/spring-framework` 复制其 HTTPS 连接，使用 Git clone 到本地。

找一个空文件夹，右键选项 git bash here，点击这个选项，启动git的命令行程序

```bash
git config --global user.name "youname" 设置密码
git config --global user.email "aa@qq.com" 设置邮箱
git init   初始化一个空的git本地仓库
```


开始 clone

```bash
git clone https://github.com/spring-projects/spring-framework.git
```

网络不稳定出错的话尝试

```bash
git config --global http.sslVerify "false"
```
执行git命令脚本：修改设置，解除ssl验证，再次执行 clone 操作即可。


使用 IDEA 打开 clone 的项目，等待构建完成即可，初次构建时间可能比较长，主要都是在下载构建需要的环境。

```bash
BUILD SUCCESSFUL in 21s
```

构建成功，至此项目构建完成。

## 测试案例

来看一个最简单的Spring使用案例。

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

​		直接使用 BeanFactory 作为容器对于 Spring 的使用来说并不多见，因为在企业的应用中大多数都会使用的是 ApplicationContext。

【简略流程】

![](https://note.youdao.com/yws/res/4958/WEBRESOURCEc989742ce27efad2afc4adba2f2197a3)


项目乱码：https://blog.csdn.net/qq_42721085/article/details/116650391

BeanPostProcessors

增强器