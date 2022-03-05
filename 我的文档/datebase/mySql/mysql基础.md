# 数据库操作和存储引擎

## 1 数据库一般操作(linux)

**启动 mysql 服务**

```shell
service mysql start
```

**查看启动状态**

```shell
service mysql status
```

**停止 mysql 服务**

```shell
service mysql stop
```

**登录 mysql**

```shell
mysql -u root -p
```

**查看数据库服务器存在哪些数据库**

```sql
show databases;
```

**使用指定的数据库**

```sql
use database_name;
```

**查看指定的数据库中有哪些表**

```sql
show tables from database_name;
```

**创建数据库**

```sql
create database database_name;
```

**删除数据库**

```sql
drop database database_name;
```

## 2 数据库对象

存储，管理和使用数据的不同结构形式，如：表、视图、存储过程、函数、触发器、事件等。

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203051908326.png)

## 3 原始数据库介绍

- **information_schema** : 存储数据库对象信息，如：用户表信息，列信息，权限，字符，分区等信息。
- **performance_schema** : 存储数据库服务器性能参数信息。
- **mysql** : 存储数据库用户权限信息。
- **sys** : 系统配置信息。

