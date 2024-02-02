# Spring源码笔记十一：数据库连接JDBC

Spring 中的 JDBC 连接与直接使用 JDBC 去连接还是有所差别的， Spring 对 JDBC 做了大量封装，消除了冗余代码，使得开发量大大减小。

建表：

```sql
create table `user`
(
    `id`   int(11) not null auto_increment,
    `name` varchar(255) default null,
    `age`  int(11)      default null,
    `sex`  varchar(255) default null,
    primary key (`id`)
) engine = InnoDB
  default charset = utf8mb4
  collate utf8mb4_bin;
```