# 关系型数据库完整性约束

六种主流关系型数据库的完整性约束支持情况如下：

| 数据库 | 非空约束 | 唯一约束 | 主键约束 | 外键约束 | 检查约束 | 默认值 |
| ------ | -------- | -------- | -------- | -------- | -------- | -------- |
| Oracle | ✔ | ✔ | ✔ | ✔ | ✔ | ✔ |
| MySQL | ✔ | ✔ | ✔ | ✔ | ✘ | ✔ |
| SQL Server | ✔ | ✔ | ✔ | ✔ | ✔ | ✔ |
| PostgreSQL | ✔ | ✔ | ✔ | ✔ | ✔ | ✔ |
| Db2 | ✔ | ✔ | ✔ | ✔ | ✔ | ✔ |
| SQLite | ✔ | ✔ | ✔ | ✔ | ✔ | ✔ |

* MySQL 只有 InnoDB 引擎支持外键约束，不支持检查约束

# SQL 发展历史

![image-20230719224448837](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202307192244011.png)


* SQL 语言规范只是一个规范，各个数据库的实现并不一定相同

![image-20230719224528907](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202307192245015.png)

![image-20230719224543423](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202307192245591.png)

