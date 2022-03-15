# SqlSessionFactoryBuilder

&emsp;&emsp;SqlSessionFactoryBuilder 用于构建 SqlSessionFactory，这个类可以被实例化、使用和丢弃，一旦创建了 SqlSessionFactory，就不再需要它了。 因此 **SqlSessionFactoryBuilder 实例的最佳作用域是方法作用域（也就是局部方法变量）**。 你可以**重用 SqlSessionFactoryBuilder 来创建多个 SqlSessionFactory 实例**，但最好还是不要一直保留着它，以保证所有的 XML 解析资源可以被释放给更重要的事情。

# SqlSessionFactory

&emsp;&emsp;每个基于 MyBatis 的应用都是以一个 SqlSessionFactory 的实例为核心的。SqlSessionFactory 的实例可以通过 SqlSessionFactoryBuilder 获得。而 SqlSessionFactoryBuilder 则可以从 **XML 配置文件或一个预先配置的 Configuration** 实例来构建出 SqlSessionFactory 实例。

&emsp;&emsp;SqlSessionFactory（简单看为数据库连接池） **一旦被创建就应该在应用的运行期间一直存在**，没有任何理由丢弃它或重新创建另一个实例。使用 SqlSessionFactory 的最佳实践是在应用运行期间不要重复创建多次，多次重建 SqlSessionFactory 被视为一种代码“坏习惯”。因此 SqlSessionFactory 的最佳作用域是应用作用域。 有很多方法可以做到，最简单的就是使用单例模式或者静态单例模式。

**使用 XML 配置文件构建 SqlSessionFactory**

&emsp;&emsp;从 XML 文件中构建 SqlSessionFactory 的实例非常简单，建议使用类路径下的资源文件进行配置。 但也可以使用**任意的输入流（InputStream）实例**，比如用文件路径字符串或 `file:// URL` 构造的输入流。MyBatis 包含一个名叫 **Resources** 的工具类，它包含一些实用方法，使得从类路径或其它位置加载资源文件更加容易。

```java
String resource = "org/mybatis/example/mybatis-config.xml";
InputStream inputStream = Resources.getResourceAsStream(resource);
SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
```

&emsp;&emsp;XML 配置文件中包含了对 MyBatis 系统的核心设置，包括获取数据库连接实例的数据源（DataSource）以及决定事务作用域和控制方式的事务管理器（TransactionManager）。

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <environments default="development">
        <environment id="development">
            <transactionManager type="JDBC"/>
            <dataSource type="POOLED">
                <property name="driver" value="com.mysql.cj.jdbc.Driver"/>
                <property name="url" value="jdbc:mysql://101.43.208.215:3306/mydb?useSSL=true&amp;characterEncoding=UTF-8&amp;serverTimezone=UTC"/>
                <property name="username" value="root"/>
                <property name="password" value="root"/>
            </dataSource>
        </environment>
        <environment id="test">
            <transactionManager type="JDBC"/>
            <dataSource type="POOLED">
                <property name="driver" value="com.mysql.cj.jdbc.Driver"/>
                <property name="url" value="jdbc:mysql://101.43.208.215:3306/mysql"/>
                <property name="username" value="root"/>
                <property name="password" value="root"/>
            </dataSource>
        </environment>
    </environments>
</configuration>
```

&emsp;&emsp;当然，还有很多可以在 XML 文件中配置的选项，上面的示例仅罗列了最关键的部分。 注意 XML 头部的声明，它用来验证 XML 文档的正确性。environment 元素体中包含了事务管理和连接池的配置。

**使用 java 方式构建 SqlSessionFactory**

&emsp;&emsp;如果你更愿意直接从 Java 代码而不是 XML 文件中创建配置，或者想要创建你自己的配置建造器，MyBatis 也提供了完整的配置类，提供了所有与 XML 文件等价的配置项。详细配置见官方文档。

# SqlSession

&emsp;&emsp;既然有了 SqlSessionFactory，顾名思义，我们可以从中获得 SqlSession 的实例。**SqlSession 提供了在数据库执行 SQL 命令所需的所有方法**。

&emsp;&emsp;**每个线程都应该有它自己的 SqlSession 实例。SqlSession 的实例不是线程安全的，因此是不能被共享的，所以它的最佳的作用域是请求或方法作用域。<font color = red>绝对不能将 SqlSession 实例的引用放在一个类的静态域，甚至一个类的实例变量也不行。也绝不能将 SqlSession 实例的引用放在任何类型的托管作用域中，比如 Servlet 框架中的 HttpSession。</font> 如果你现在正在使用一种 Web 框架，考虑将 SqlSession 放在一个和 HTTP 请求相似的作用域中。 换句话说，每次收到 HTTP 请求，就可以打开一个 SqlSession，返回一个响应后，就关闭它。 这个关闭操作很重要，为了确保每次都能执行关闭操作，你应该把这个关闭操作放到 finally 块中。 **

&emsp;&emsp;**SqlSessionFactoryBuilder、SqlSessionFactory 、 SqlSession 和 SQL Mapper 是 mybatis 的四大核心组件。**

 mybatis 的使用包括四个步骤
 1. pojo ：与数据库表相对应的实体类
 2. Mapper 接口 ：规定操作对应 pojo 的增删改查方法，**方法不可被重载**。
 3. Mapper.xml 配置文件 ：提供对应 Mapper 接口的相关配置信息
 4. mybatis-config.xml ：mybatis 的配置文件，提供对数据源和注册 Mapper.xml 的地方

 实现详情见对应项目 MybatisDemo.mybatis-01

# 线程安全问题

- 对于使用依赖注入框架：依赖注入框架可以创建线程安全的、基于事务的 SqlSession 和映射器，并将它们直接注入到你的 bean 中，因此可以直接忽略它们的生命周期。

# $和#的区别

**$:原样的拼在SQL中,你写什么,最后SQL就是拼成什么**

比如mapper中这样定义

insert into user (name,age) values (${name},${age})

运行----->

**insert into user (name,age) values (admin,18)** 

有SQL注入的风险.

**\#:通过占位符的方式定义参数**

比如mapper中这样定义

insert into user (name,age) values (#{name},#{age})

运行--->

**insert into user (name,age) values (?,?)** 

$: 直接去对象中寻找对应的属性get方法,如果找不到就直接报错了。

\#：先去寻找对象的对应的属性的get方法,如果没有,就是传入参数的值.

# 事务管理器（transactionManager）

在 MyBatis 中有**两种类型**的事务管理器（也就是 type="[JDBC|MANAGED]"）：

- JDBC – 这个配置直接使用了 JDBC 的提交和回滚设施，它依赖从数据源获得的连接来管理事务作用域。
- MANAGED – 这个配置几乎没做什么。它从不提交或回滚一个连接，而是让容器来管理事务的整个生命周期（比如 JEE 应用服务器的上下文）。 默认情况下它会关闭连接。然而一些容器并不希望连接被关闭，因此需要将 closeConnection 属性设置为 false 来阻止默认的关闭行为。

MyBatis 中默认的事务管理器为 JDBC 默认的数据源策略为 POOLED 数据库连接池

# mybatis 中的代理





# 缓存

## 一级缓存

本地缓存，无法关闭，可以被禁用，存储在 sqlSession 中

**一级缓存失效场景：**

1). sqlSession 不同

2). sqlSession 相同，查询 sql 不同

3). sqlSession 相同，两次查询之间进行了增删改操作

4). sqlSession 相同，手动清除了缓存

## 二级缓存

全局缓存，基于 namespace 实现，一个 mapper 文件对应一个二级缓存，底层由 Map 存储，一个 namespace 对应一个 Map 

工作机制

1. sqlSession 在查询数据时，会将数据存放在一级缓存
2. 当 sqlSession 关闭，**一级缓存中的数据将被保存至二级缓存**
3. 不同的 namespace 查出的数据会放在自己对应的 Map 中

缓存配置：

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203142223381.png)

**使用只读缓存时，POJO 需要实现序列化接口**

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203142238999.png)

**查找顺序：先查找二级缓存，再查找一级缓存，这样是为了解决一级缓存存在的脏读问题，但是当访问两个不同的Mapper时也会存在脏读，解决方法可以把两个Mapper放到一个nameSpace下**

# 使用过程中 oracle 与 mysql 的区别

1. mybatis 默认将 null 值映射为 JDBC_TYPE.ORTHER 类型，oracle 不认，而 mysql 可以。显示设置映射关系为 JDBC_TYPE.NULL : 全局 or 放值时

2. mysql 有主键自增，oracle 使用序列实现，配置select key标签

3. 批量插入语法：

   mysql：

   1. 可以在连接数据库URL增加参数使，mysql 支持一次执行多条 sql

   2. 可以使用 `insert values ()()()` 的形式批量插入

   oracle：

   1. 可以使用 `begin insert values(); insert values(); end;` 存储过程实现
   2. 通过子查询实现![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203142142401.png)

4. mybatis 的内置参数：
   1. 数据库别名![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203142150771.png)
   2. 传入参数本身![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203142153996.png)

# 整合第三方缓存

原理：三方缓存通过实现 mybatis 的 Cache 接口来实现整合

[MyBatis 与各种环境的整合实现的中间包与文档](https://github.com/mybatis)

整合 EHcache

[mybatis-ehcache – MyBatis Ehcache | Reference Documentation](http://mybatis.org/ehcache-cache/)

