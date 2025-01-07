# mysql 服务器硬件选型

在 MySQL 中，执行一条 sql 时只允许一个 CPU 核心参与运算，在 5.7 以上版本才进行了优化

由此可得：

- CPU 尽量选择 64 位的，且一定要配套 64 位的操作系统
- 高并发应用中，核心数量比频率更重要
- 复杂 sql 多的应用核心频率比数量更重要

InnoDB 默认情况下会将索引与数据全部加载至内存，以加快查询速度，最优的情况就是选用大内存来存储数据，在数据量过大时可将热点数据单独部署一份用于提升性能

# InnoDB 存储结构与表的垂直拆分

在 InnoDB 存储引擎中，存储的每条数据称为 row ，由多个 row 组成的称为页 page ，每个页的存储容量默认为 16KB，用于存储连续的行，存储页的结构称为区 extent ，一个区的默认大小为 1MB ，即一个区最多可以装载 64 个连续的页。

表空间是一个逻辑容器，包含多个段（Segment），每个段由一个或多个区（Extent）组成。

1. 行（Row）
- **定义**: 每一条记录称为一行（row），包含表中定义的所有列的数据。
- **存储**: 行数据在页中以紧凑的方式存储，包括实际的数据值和一些额外的元数据，如变长字段长度列表和 NULL 值列表等。

2. 页（Page）
- **定义**: 页是 InnoDB 存储引擎中数据存储的基本单位，多个行（rows）被组织在一个页中。
- **默认大小**: 每个页的默认大小为 16KB，这意味着一个页可以存储大约 8000 字节的数据，具体大小可能会因为行格式和存储方式的不同而略有变化。
- **存储结构**: 
  - **文件头（File Header）**: 包含页的通用信息，如校验和、页面类型等。
  - **页头（Page Header）**: 存储页的元数据，如记录数、空闲空间等。
  - **用户记录（User Records）**: 实际存储的行数据。
  - **空闲空间（Free Space）**: 页中尚未使用的空间，用于插入新记录。
  - **页目录（Page Directory）**: 包含页中记录的相对位置信息，用于快速查找记录。

3. 区（Extent）
- **定义**: 区是比页大一级的存储结构，用于管理连续的页。
- **默认大小**: 一个区的默认大小为 1MB，包含 64 个连续的页（每个页 16KB）。
- **作用**: 区用于提高数据存储的连续性和访问效率，减少磁盘随机访问的开销。

4. 表空间（Tablespace）
- **定义**: 表空间是一个逻辑容器，包含多个段（Segment），每个段由一个或多个区（Extent）组成。
- **结构**: 表空间是 InnoDB 存储引擎中最高级别的存储结构，负责管理所有数据、索引和日志信息。

在 InnoDB 1.0 后，在页存储中引入了数据压缩技术，使得一个页中可以存储更多行数据，但由此页带来一个问题，即数据有压缩就有解压缩，这意味着每次跨页查询时，数据库引擎需要花费额外的 CPU 资源来进行解压缩操作，所以在检索数据时，需要尽可能少的跨页查询，将大宽表拆分成小表就是为了在每个页中存储更多的行数据，减少跨页检索。

## 表的垂直拆分

假设现在有一张系统执行日志表，如果需要通过执行时间查询执行日志，这个时间字段又由于各种原因没有建立索引，那么本身一条数据占用很大空间，又需要全表扫描，可见其性能之慢，但如果将其拆分为两个小表，将检索信息等小字段存入一张小表，利用 InnoDB 的页存储特点，可以减少扫描页的数量，然后根据检索到的 ID 回查详情表，会快很多。

即垂直拆表需要满足两个条件

- 表数据量可能超过百万
- 表中含有非常大的字段

# 慢 SQL 日志查询

## MySQL 慢查询日志查询指南

MySQL 的慢查询日志（Slow Query Log）是一种用于记录执行时间超过设定阈值的 SQL 语句的日志功能。通过分析这些日志，可以帮助识别和优化数据库中的性能瓶颈。以下是关于如何配置、查看和分析 MySQL 慢查询日志的详细指南：

### 1. 开启慢查询日志

- **配置文件设置**：
  - 打开 MySQL 的配置文件（通常是 `my.cnf` 或 `my.ini`）。
  - 添加或修改以下参数：
    ```ini
    [mysqld]
    slow_query_log = 1
    slow_query_log_file = /var/log/mysql/slow-query.log
    long_query_time = 2
    ```
    - `slow_query_log`：启用（1）或禁用（0）慢查询日志。
    - `slow_query_log_file`：指定慢查询日志文件的路径。
    - `long_query_time`：设置慢查询的时间阈值（单位为秒），超过此时间的查询将被记录。

- **动态设置**：
  - 使用以下命令动态开启慢查询日志：
    ```sql
    SET GLOBAL slow_query_log = 'ON';
    SET GLOBAL long_query_time = 2;
    ```
    - 注意：动态设置在 MySQL 重启后会失效，需在配置文件中进行持久化。

### 2. 查看慢查询日志

- **日志文件**：
  - 慢查询日志会记录在指定的文件中（如 `/var/log/mysql/slow-query.log`）。可以使用文本编辑器或命令行工具（如 `tail`, `less`）查看日志内容。

- **日志格式**：
  - 日志中包含的信息包括：
    - 查询时间（Query_time）
    - 锁定时间（Lock_time）
    - 返回的行数（Rows_sent）
    - 扫描的行数（Rows_examined）
    - 具体的 SQL 语句。

### 3. 分析慢查询日志

- **使用内置工具**：
  - `mysqldumpslow`：这是一个 MySQL 自带的工具，可以对慢查询日志进行汇总和分析。例如：
    ```bash
    mysqldumpslow -s t -t 10 /var/log/mysql/slow-query.log
    ```
    - `-s t`：按查询时间排序。
    - `-t 10`：显示前 10 条记录。

- **使用第三方工具**：
  - `pt-query-digest`（Percona Toolkit 的一部分）：提供更详细的分析功能。
    ```bash
    pt-query-digest /var/log/mysql/slow-query.log --limit 10
    ```
    - 这将显示执行时间最长的前 10 条 SQL 语句及其统计信息。

### 4. 优化慢查询

- **添加索引**：
  - 对于频繁查询的列，添加适当的索引可以显著提高查询性能。
  - 示例：
    ```sql
    ALTER TABLE employees ADD INDEX idx_first_name (first_name);
    ```

- **减少查询范围**：
  - 仅选择需要的列，避免使用 `SELECT *`。

- **使用 EXPLAIN**：
  - 使用 `EXPLAIN` 分析查询计划，识别潜在的性能瓶颈。

- **优化表结构**：
  - 根据需要调整表结构，如使用更合适的数据类型或归档旧数据。


# mysql 集群搭建方式

MySQL 默认的主从同步方案为异步同步，这个非常坑，会导致数据库数据不一致，**在没有显示声明的时候用的就是这个模式**

## 读写分离集群

主体结构：由主节点 master 与子节点 node 组成，子节点利用 MySQL 自带的主从同步机制，即binlog 日志同步。其中写入操作只在主节点进行，并同步给子节点，读取操作可在任一节点进行，这是 MySQL 自带的简单集群模式。

在应用中增加一层中间件，可以是 MyCat 或其他数据库分片中间件，主要作用是根据提交的 SQL 行为，如 insert，update，delete 语句将路由至主节点执行，select 查询则路由至从节点执行。

故障转移：利用 MHA 中间件实现，在主节点挂掉之后自动选举提升从节点为主节点。

适用于互联网等读多写少的场景。

## 数据分片集群模式

在数据量越来越大，单表数据常常过千万级别时，可采用数据分片集群模式，即分库分表。

分片中间件实现 SQL 路由，如 MyCat。

适用于十亿至几十亿数据量的数据存储，当数据超出几十亿的时候，就需要考虑采用非关系型数据库来处理。

分片算法：

基于范围的分片，如时间范围，优点是集群扩展简单，缺点是易造成流量倾斜
基于Hash算法分片，主要有Hash取模和一致性Hash算法，优点是数据分布均匀，缺点是集群难以扩展

## 利用 canal 中间件实现 MySQL ES 双写

原理：canal 伪装成一个 MySQL 从库节点，监听主库同步过来的 binglog 日志，根据不同的日志来触发执行一段对应的 Java 代码，来实现不同数据库间的数据同步。

一般可将数据变更消息发往 kafka 或其他 mq，等待消费即可。

# mysql 主从复制架构下先插再查的延迟问题

1. 利用集群本身的全同步功能，在数据同步给每节点之前是不会对更新语句做返回的。
2. 利用中间件的强制路由功能，在更新语句执行后，强制下一次查询路由至主库进行查询，这个可以在中间件中配置。

# 哈希分片下的数据迁移问题

一般来讲，在使用哈希分片来存储数据时，最简单的方式就是使用数据的 ID 对分片数量取模，最终的到数据应该存储的分片位置，但是这种方式在进行数据库集群扩容时会有大量数据需要迁移。

**哈希分片**和**一致性哈希算法**在分布式系统中的数据存储和负载均衡中扮演着重要角色。以下是对这两种方法的详细解释，特别是针对**一致性哈希算法**如何解决传统哈希分片在数据库集群扩容时面临的问题。

## **一、传统哈希分片方法**

**1.1 基本原理**
在传统的哈希分片方法中，通常使用数据的唯一标识符（如ID）通过哈希函数生成一个哈希值，然后将这个哈希值对分片数量（例如数据库节点数量）取模，最终得到数据应该存储的分片位置。

公式如下：
```
ShardIndex = Hash(ID) % NumberOfShards
```

**1.2 优点**
- **简单易实现**：算法简单，易于理解和实现。
- **均匀分布**：在理想情况下，数据能够均匀地分布在各个分片上，避免热点问题。

**1.3 缺点**
- **扩容困难**：当集群需要扩容（例如增加新的分片节点）时，几乎所有的数据都需要重新计算哈希值并迁移到新的分片上。这是因为分片数量发生了变化，导致取模的结果也发生了变化。
- **数据迁移量大**：扩容时，大量的数据需要迁移，这会导致系统性能下降，甚至在迁移过程中出现服务不可用的情况。

## **二、一致性哈希算法**

**2.1 基本原理**
一致性哈希算法（Consistent Hashing）是一种特殊的哈希算法，旨在解决传统哈希分片在扩容和缩容时需要大量数据迁移的问题。其核心思想是将数据和节点都映射到一个哈希环（Hash Ring）上，通过在环上寻找顺时针方向最近的节点来存储或查找数据。

**2.2 哈希环**
一致性哈希算法将哈希值空间组织成一个环，通常范围是0到2^32-1。所有的节点和数据都通过哈希函数映射到这个环上的某个位置。

**2.3 数据存储与查找**
- **数据存储**：将数据通过哈希函数映射到环上的某个位置，然后顺时针寻找第一个节点，将数据存储在该节点上。
- **数据查找**：同样地，将数据ID映射到环上，顺时针寻找第一个节点，从该节点获取数据。

**2.4 虚拟节点（Virtual Nodes）**
为了解决节点在环上分布不均匀的问题，一致性哈希算法引入了虚拟节点的概念。每个物理节点在环上对应多个虚拟节点（通常为多个副本），这样可以更均匀地分布数据和负载。

**2.5 优点**
- **减少数据迁移**：当集群扩容或缩容时，只有部分数据需要迁移，而不是全部数据。例如，增加一个新的节点时，只会影响环上新增节点逆时针方向到下一个节点之间的数据。
- **更好的负载均衡**：通过引入虚拟节点，可以更均匀地分布数据和负载，避免数据倾斜和热点问题。
- **高可用性**：由于每个节点在环上对应多个虚拟节点，即使某个节点故障，其数据也可以由其他虚拟节点承担，提高系统的可用性。

**2.6 缺点**
- **实现复杂度**：相比传统哈希分片，一致性哈希算法的实现更为复杂。
- **虚拟节点的管理**：需要管理和维护虚拟节点，增加了系统的复杂度。

## **三、一致性哈希算法在扩容时的优势**

**3.1 数据迁移量减少**
在传统哈希分片方法中，扩容时所有数据都需要重新计算哈希值并迁移。而在一致性哈希算法中，扩容时只有部分数据需要迁移。例如，增加一个新的节点时，只会影响环上新增节点逆时针方向到下一个节点之间的数据。

**3.2 动态调整**
一致性哈希算法允许动态调整集群规模，而不需要停机或大规模数据迁移。这对于需要高可用性和可扩展性的系统尤为重要。

**3.3 负载均衡**
通过引入虚拟节点，一致性哈希算法可以实现更好的负载均衡，避免数据倾斜和热点问题。

## **四、实际应用中的优化**

**4.1 虚拟节点的数量**
虚拟节点的数量需要根据实际需求进行配置。过多的虚拟节点会增加管理和存储的负担，而过少的虚拟节点可能导致数据分布不均匀。

**4.2 哈希函数的选择**
选择合适的哈希函数也是关键。常见的哈希函数有 MD5、SHA-1、SHA-256 等。选择哈希函数时需要考虑哈希值的均匀性和计算速度。

**4.3 数据备份与恢复**
在一致性哈希算法中，数据备份和恢复机制也需要特别设计。例如，可以使用多副本策略，将数据存储在多个虚拟节点上，以提高数据的可靠性和可用性。

**总结**

一致性哈希算法通过将数据和节点映射到一个哈希环上，并在环上寻找顺时针方向最近的节点进行数据存储和查找，解决了传统哈希分片在扩容时需要大量数据迁移的问题。它具有减少数据迁移量、更好的负载均衡和高可用性等优点，但也存在实现复杂度较高的缺点。

在实际应用中，一致性哈希算法被广泛应用于分布式缓存（如 Redis Cluster）、数据库分片（如 MongoDB Sharding）、分布式文件系统（如 Ceph）等场景。如果您正在设计或优化一个分布式系统，一致性哈希算法是一个值得考虑的选择。

# MySQL 高可用 MHA 架构方案

https://zhuanlan.zhihu.com/p/132508138

# MySQL 高可用 MGR 架构

# join 关联相关

原则上在高性能应用中应明令禁止三表以上的 join 查询，需要 join 的字段数据类型必须绝对一致，且关联字段必须有索引。

原因：

- myCat 不支持两张表以上的关联查询
- MySQL 自身设计缺陷导致多表关联查询性能差，尤其在两张表以上的关联查询，sql 优化器会有意想不到的错误优化。

解决方案

- 分步查询
- 反范式表冗余设计
- 引入银行的 ETL 数据集市解决方案，ETL 即数据的导出，加工，导入，数据银行每天会有日终跑批，用于处理当天产生的数据，这种数据处理称为 T＋1 即第二天才可以看到第一天的数据，使用 ETL 技术可以产生各种中间数据，使用中间数据利用倒排技术筛选出需要的数据

# sql 优化基础

sql 优化中要特别注意因隐式类型转换导致的索引失效问题。

## 索引选择性陷阱

在 MySQL 优化器优化查询语句时，如果索引命中的数据量过大，比如超过了全表的 80％ ，则 sql 优化器会放弃使用索引转而扫描全表，比如查询手机号码为 1 开头的，`select ＊ from table where mobel like '1%';` 因为大部分手机号码都是 1 开头的，导致索引最左匹配到大量数据，导致优化器放弃使用索引。

这种情况应增加其他强制匹配条件以缩小索引命中数据量或使用ES等查询框架检索

## 降低区间范围比较的计算精度

区间范围比较（特别是索引列比较）要有明确的边界，降低比较时的计算精度。即用 >=、<=代替>、<。

示例：

1. 数值比较 A>1 替换为 A>=1.00001
2. 时间 A>2019-09-01 00:00:00 替换为 A>=2019-09-01 00:00:00+00001
3. 日期 A>2019-09-01 替换为 A>=2019-09-02

## 执行计划

**EXPLAIN 和 EXPLAIN EXTENDED**

在MySQL中，`EXPLAIN`和`EXPLAIN EXTENDED`是用于分析查询执行计划的两个关键字。它们可以帮助开发者理解MySQL如何执行查询，并提供优化查询性能的建议。**`EXPLAIN EXTENDED` 在 5.7 以上版本已不再支持**。

### EXPLAIN

**功能**：
- `EXPLAIN`用于获取MySQL查询的执行计划信息。它显示MySQL如何处理查询，包括表连接的顺序、使用的索引、估计的行数等。

**使用场景**：
- 优化查询性能：通过查看执行计划，开发者可以识别查询中的瓶颈，如全表扫描或低效的索引使用。
- 理解查询执行逻辑：了解MySQL如何选择执行路径，尤其是在涉及多个表和复杂连接时。

**输出信息**：
- `id`: 查询中每个SELECT的唯一标识符。
- `select_type`: 查询的类型（如SIMPLE、PRIMARY、UNION等）。
- `table`: 查询涉及的表名。
- `type`: 访问类型（如const, ref, range, index, ALL等），从好到坏排序。
- `possible_keys`: 可能使用的索引。
- `key`: 实际使用的索引。
- `key_len`: 索引使用的字节数。
- `ref`: 与索引比较的列。
- `rows`: 估计扫描的行数。
- `filtered`: 过滤后的数据百分比。
- `Extra`: 附加信息（如Using index, Using filesort等）。

### EXPLAIN EXTENDED

**功能**：
- `EXPLAIN EXTENDED`在`EXPLAIN`的基础上提供更详细的执行计划信息。它不仅显示执行计划，还可以通过`SHOW WARNINGS`命令查看扩展的查询重写信息。

**使用场景**：
- 更深入的分析：适用于需要更详细了解查询内部处理过程的情况，例如查看MySQL如何重写查询或优化查询逻辑。
- 调试复杂查询：帮助开发者理解MySQL如何处理复杂的子查询或联合查询。

**输出信息**：
- 除了`EXPLAIN`的所有信息外，`EXPLAIN EXTENDED`还允许使用`SHOW WARNINGS`查看更详细的执行计划，包括查询重写后的SQL语句。

### 以下是对`EXPLAIN`输出信息的详细解读：

1. **id**
- **含义**: 查询中每个SELECT语句的唯一标识符。如果查询中包含子查询或联合查询，`id`可以帮助理解各个部分的执行顺序。
- **示例**: 
  - `id`为1的SELECT语句是主查询。
  - `id`为2的SELECT语句是子查询。

2. **select_type**
- **含义**: 查询的类型，描述SELECT语句的类型。
- **常见类型**:
  - `SIMPLE`: 简单查询，不包含子查询或联合查询。
  - `PRIMARY`: 主查询，在包含子查询的查询中，主查询的`select_type`为`PRIMARY`。
  - `SUBQUERY`: 子查询。
  - `DERIVED`: 派生表（FROM子句中的子查询）。
  - `UNION`: 联合查询中的第二个或后续SELECT语句。
  - `UNION RESULT`: 联合查询的结果集。

3. **table**
- **含义**: 查询涉及的表名。如果查询中使用了别名，这里显示的是别名。
- **示例**: `employees`或`e`（如果使用了别名）。

4. **type**
- **含义**: 访问类型，表示MySQL如何访问表中的数据。访问类型从好到坏排序，越靠前的访问类型性能越好。
- **常见类型**:
  - `system`: 表中只有一行数据。
  - `const`: 表中最多只有一行匹配，查询速度非常快。
  - `eq_ref`: 使用唯一索引进行连接。
  - `ref`: 使用非唯一索引或前缀索引进行连接。
  - `range`: 使用索引进行范围扫描。
  - `index`: 全索引扫描。
  - `ALL`: 全表扫描，性能最差。

5. **possible_keys**
- **含义**: 可能使用的索引列表。MySQL在查询优化过程中会考虑这些索引，但不一定使用。
- **示例**: `PRIMARY, idx_employee_id`。

6. **key**
- **含义**: 实际使用的索引。如果为NULL，表示没有使用索引。
- **示例**: `idx_employee_id`。

7. **key_len**
- **含义**: 使用的索引的字节长度。可以通过`key_len`判断索引的使用情况，例如是否使用了复合索引的所有列。
- **示例**: `4`（表示使用了4个字节的索引）。

8. **ref**
- **含义**: 与索引比较的列或常量。显示哪些列或常量被用来与索引进行比较。
- **示例**: `const`或`employees.employee_id`。

9. **rows**
- **含义**: 估计扫描的行数。表示MySQL估计在执行查询时需要扫描的行数。
- **示例**: `100`。

10. **filtered**
- **含义**: 过滤后的数据百分比。表示通过索引或其他条件过滤后的数据占总数据的百分比。
- **示例**: `10.00`（表示过滤后的数据占总数据的10%）。

11. **Extra**
- **含义**: 附加信息，提供关于查询执行的额外细节，**主要优化的位置**。

以下是 `Extra` 列中常见的几种信息及其解释：

Using filesort
- **含义**：MySQL 对查询结果进行了外部排序，而不是利用索引进行排序。这种情况通常发生在查询包含 `ORDER BY` 子句，但排序的字段没有索引，或者排序顺序与索引顺序不一致。
- **影响**：使用 `filesort` 会导致额外的磁盘或内存排序操作，影响查询性能。文件排序会极大影响查询性能，**这是主要需要优化的项**。

Using temporary
- **含义**：MySQL 使用临时表来存储中间结果。这通常发生在查询包含 `GROUP BY`、`DISTINCT` 或 `ORDER BY` 子句，且无法通过索引优化时。临时表的特性是如果内存缓存够用则使用内存缓存，否则自动创建 MyISAM 引擎表，导致 IO 变差。**这是主要需要优化的项**，通常发生在关联查询时。
- **影响**：使用临时表会增加查询的开销，尤其是在处理大数据量时。

Using index
- **含义**：查询使用了覆盖索引，这意味着查询所需的所有列都包含在索引中，MySQL 不需要访问表的数据行即可返回结果。
- **影响**：这种操作非常高效，因为减少了磁盘 I/O 操作。

Using where
- **含义**：查询使用了 `WHERE` 子句进行过滤。即使使用了索引，MySQL 仍然需要应用 `WHERE` 条件来筛选数据。这意味着需要在底层挨着数据块一块一块的扫描。性能非常差,**这是主要需要优化的项**。
- **影响**：如果 `WHERE` 条件无法通过索引优化，可能会导致全表扫描。

Using join buffer
- **含义**：MySQL 使用连接缓冲区来执行连接操作。这通常发生在连接的两个表没有合适的索引时。
- **影响**：使用连接缓冲区可以提高连接效率，但过多的连接缓冲区使用可能表示需要优化索引。

Impossible WHERE
- **含义**：查询的 `WHERE` 子句条件始终为假，无法返回任何行。
- **影响**：这种查询通常不会返回任何结果，可能需要检查查询条件。

Select tables optimized away
- **含义**：查询优化器能够通过索引直接获取结果，而无需执行实际的表扫描。例如，使用 `MIN()` 或 `MAX()` 函数时，如果相关列有索引，MySQL 可以直接通过索引获取结果。

Distinct
- **含义**：查询使用了 `DISTINCT` 操作，MySQL 在找到第一个匹配的行后停止搜索其他行。

Using index condition
- **含义**：查询使用了索引条件下推（Index Condition Pushdown）优化，MySQL 在索引层面应用 `WHERE` 条件，减少回表操作。

Using MRR (Multi-Range Read)
- **含义**：查询使用了多范围读取优化，MySQL 按顺序读取索引中的多个范围，以提高缓存命中率。在 MySQL 5.6 以后加入的优化措施，能很大程度优化回表查询的效率，查询通过二级索引检索到主键值，然后通过主键值回表。如果以二级索引的顺序回表，对应的主键就是乱序的，回表时候就是大量的随机IO。MRR则在内存中将命中的二级索引进行了重新排序（以主键列排序），通过主键列回表则是顺序IO，极大的提高了性能。

Range checked for each record
- **含义**：MySQL 在每一行记录上检查是否可以使用索引进行范围扫描。这种情况通常发生在查询条件复杂且无法通过单一索引优化时。

Using join buffer (Batched Key Access)
- **含义**：查询使用了批量键访问连接缓冲区，MySQL 通过批量处理键访问来提高连接效率。

### 示例一

假设有以下查询和`EXPLAIN`输出：

```sql
EXPLAIN SELECT e.name, d.department_name FROM employees e JOIN departments d ON e.department_id = d.id WHERE e.salary > 5000;
```

**输出**:

| id | select_type | table | type | possible_keys | key | key_len | ref | rows | filtered | Extra |
|----|-------------|-------|------|---------------|-----|---------|-----|------|----------|-------|
| 1  | SIMPLE      | e     | range| idx_salary    | idx_salary | 4      | NULL| 100  | 10.00    | Using index condition |
| 1  | SIMPLE      | d     | eq_ref| PRIMARY       | PRIMARY    | 4      | e.department_id | 1    | 100.00   | NULL  |

**解读**:
- `id`为1，表示这是一个简单查询。
- `select_type`为`SIMPLE`，表示没有子查询或联合查询。
- `table`为`e`和`d`，表示查询涉及`employees`和`departments`表。
- `type`为`range`和`eq_ref`，表示`employees`表使用范围扫描，`departments`表使用唯一索引进行连接。
- `possible_keys`和`key`表示可能使用的索引和实际使用的索引。
- `rows`为100和1，表示估计扫描的行数。
- `Extra`为`Using index condition`，表示使用索引条件进行过滤。

## 关联查询

### NLJ

### MySQL 关联查询中的 NLJ（Nested Loop Join）

**NLJ（Nested Loop Join，嵌套循环连接）** 是 MySQL 中一种基础的表连接算法，用于在执行多表关联查询时处理表之间的连接操作。NLJ 通过嵌套循环的方式逐行比较两个表中的数据，以找到满足连接条件的记录。尽管 NLJ 是最基本的连接算法，但在某些情况下，它仍然是一种有效且常用的连接方式。

---

### 1. NLJ 的基本原理

**嵌套循环连接（NLJ）** 的工作原理类似于嵌套的 `for` 循环。具体步骤如下：

1. **外层循环**：遍历第一个表（通常称为“驱动表”或“外部表”）的每一行。
2. **内层循环**：对于外层循环中的每一行，遍历第二个表（通常称为“被驱动表”或“内部表”）的每一行。
3. **匹配条件**：检查每一对来自两个表的行是否满足连接条件（例如，`JOIN` 条件）。
4. **输出结果**：如果满足条件，则将两行数据组合起来并输出。

**示例**：
假设有两个表 `employees` 和 `departments`，我们希望查询每个员工所在的部门：

```sql
SELECT employees.name, departments.department_name
FROM employees
JOIN departments ON employees.department_id = departments.id;
```

在 NLJ 中，MySQL 会执行以下操作：
- 遍历 `employees` 表的每一行。
- 对于 `employees` 表中的每一行，遍历 `departments` 表的每一行。
- 如果 `employees.department_id` 等于 `departments.id`，则将两行数据组合并返回。

---

### 2. NLJ 的优缺点

#### 优点：
- **实现简单**：NLJ 是最基础的连接算法，逻辑简单，易于实现。
- **适用于小表连接**：当连接的两个表都很小，或者其中一个表非常小（可以完全加载到内存中）时，NLJ 的性能表现良好。
- **不需要额外的内存或索引支持**：NLJ 不依赖于索引，可以在没有合适索引的情况下使用。

#### 缺点：
- **性能问题**：当连接的表很大时，NLJ 的性能会急剧下降，因为它需要进行笛卡尔积级别的比较（时间复杂度为 O(n*m)）。
- **无法利用索引优化**：NLJ 通常不会利用索引进行优化，除非在某些特殊情况下（如索引嵌套循环连接，详见下文）。

---

### 3. NLJ 的变种

在 MySQL 中，NLJ 有几种常见的变种，用于优化特定场景下的连接操作：

#### a. 索引嵌套循环连接（Index Nested Loop Join，INLJ）
- **原理**：如果连接条件中的列有索引，MySQL 可以利用索引来加速内层循环的查找过程。
- **优点**：通过索引查找，NLJ 的性能可以显著提高，因为索引查找的时间复杂度为 O(log n)，而不是 O(n)。
- **适用场景**：当连接条件中的列有索引时，MySQL 会自动选择 INLJ。

#### b. 块嵌套循环连接（Block Nested Loop Join，BNLJ）
- **原理**：BNLJ 通过将外部表的数据分成块，并使用连接缓冲区来减少内层循环的扫描次数。
- **优点**：BNLJ 可以减少 I/O 操作，提高连接效率，尤其是在外部表较大时。
- **适用场景**：当连接的两个表都较大且没有合适的索引时，MySQL 可能会选择 BNLJ。

---

### 4. 如何优化 NLJ

- **创建合适的索引**：为连接条件中的列创建索引，可以将 NLJ 优化为索引嵌套循环连接（INLJ），从而显著提高性能。
- **减少连接表的大小**：尽量减少驱动表和被驱动表的数据量，例如，通过在连接前过滤数据。
- **使用覆盖索引**：如果查询的列都包含在索引中，可以避免回表操作，进一步提高性能。

---

### 5. 示例

假设有以下两个表：

```sql
CREATE TABLE employees (
    id INT PRIMARY KEY,
    name VARCHAR(100),
    department_id INT,
    INDEX (department_id)
);

CREATE TABLE departments (
    id INT PRIMARY KEY,
    department_name VARCHAR(100)
);
```

执行以下查询：

```sql
SELECT employees.name, departments.department_name
FROM employees
JOIN departments ON employees.department_id = departments.id;
```

- **使用 NLJ**：MySQL 会遍历 `employees` 表的每一行，并使用 `department_id` 在 `departments` 表中查找匹配的部门。
- **优化为 INLJ**：由于 `employees.department_id` 有索引，MySQL 会使用索引查找来加速内层循环的查找过程，从而提高查询性能。

---

NLJ 是 MySQL 中最基本的表连接算法，适用于小表或某些特定场景。在大多数情况下，MySQL 会自动选择最合适的连接算法（如 INLJ、BNLJ）来优化查询性能。为了提高 NLJ 的效率，开发者可以通过创建合适的索引、减少数据量等方法进行优化。

## 关联查询的执行效率与选择

**在关联字段必须加索引，效率提升无数倍，只加筛选字段索引一点用都没有**，尽量使用 union all,少用 union ，union 去重是基于临时表，临时表的特性是如果内存缓存够用则使用内存缓存，否则自动创建 MyISAM 引擎表，导致 IO 变差。

在MySQL中，关联查询（JOIN）是一种常见的操作，用于从多个表中检索相关数据。常见的JOIN类型包括内连接（INNER JOIN）、左连接（LEFT JOIN）等。以下是对这些JOIN类型的执行效率及其选择的详细分析：

### 内连接（INNER JOIN）

**特点**:
- **定义**: 只返回两个表中满足连接条件的行。
- **执行效率**: 通常是JOIN类型中效率最高的，因为它只处理匹配的行，减少了需要处理的数据量。
- **适用场景**: 当需要两个表中都存在匹配的数据时使用。例如，获取客户及其订单信息。

### 左连接（LEFT JOIN）

**特点**:
- **定义**: 返回左表中的所有行，以及右表中满足连接条件的行。如果右表中没有匹配的行，则结果中右表的列将填充为NULL。
- **执行效率**: 通常比INNER JOIN效率稍低，因为它需要扫描左表的所有行，并尝试匹配右表中的行。
- **适用场景**: 当需要保留左表中的所有记录，即使右表中没有匹配的数据时使用。例如，获取所有客户及其可能的订单信息。

### 选择与优化建议

- **选择合适的JOIN类型**:
  - **INNER JOIN**适用于需要两个表中都存在匹配数据的场景，通常性能更优。
  - **LEFT JOIN**适用于需要保留左表中所有记录的场景，即使右表中没有匹配数据。

- **优化建议**:
  - **使用索引**: 在JOIN列上创建索引可以显著提高JOIN操作的性能，尤其是对于大型表。
  - **限制返回的列**: 只选择需要的列，避免使用SELECT *，这可以减少数据传输和处理的开销。
  - **避免不必要的JOIN**: 尽量减少JOIN的次数和复杂度，使用子查询或视图来简化查询逻辑。
  - **选择合适的驱动表**: 在多表JOIN中，选择数据量较小的表作为驱动表可以提高查询效率。

- **其他考虑因素**:
  - **数据量**: 当数据量较大时，JOIN操作可能会成为性能瓶颈。可以通过分区表或使用缓存机制来缓解。
  - **服务器资源**: 确保数据库服务器有足够的内存和CPU资源来处理复杂的JOIN操作。

### 关联查询的计算过程

1. **连接类型**:
   - **内连接（INNER JOIN）**: 只返回两个表中满足连接条件的行。
   - **左连接（LEFT JOIN）**: 返回左表中的所有行，以及右表中满足连接条件的行。如果右表中没有匹配的行，则结果中右表的列将填充为NULL。
   - **右连接（RIGHT JOIN）**: 返回右表中的所有行，以及左表中满足连接条件的行。如果左表中没有匹配的行，则结果中左表的列将填充为NULL。

2. **执行步骤**:
   - **驱动表选择**: MySQL通常会选择结果集较小的表作为驱动表（驱动表是指在连接操作中首先被扫描的表）。这可以通过使用`EXPLAIN`语句来确认，驱动表是`EXPLAIN`输出中的第一个表。
   - **连接算法**:
     - **嵌套循环连接（Nested-Loop Join）**: MySQL常用的基本连接算法。它通过嵌套循环的方式逐行匹配连接条件。
     - **块嵌套循环连接（Block Nested-Loop Join）**: 通过使用缓冲区（Join Buffer）来减少内层循环的读取次数，适用于没有索引的连接。
     - **索引嵌套循环连接（Index Nested-Loop Join）**: 利用索引加速连接操作，适用于有索引的连接。

3. **连接顺序**:
   - MySQL不会简单地按照SQL语句中表的顺序进行连接，而是根据优化器的决策选择最优的连接顺序。优化器会考虑表的连接条件和统计信息来决定连接顺序。

### 驱动表的选择

- **选择原则**: 在选择驱动表时，MySQL会优先选择结果集较小的表作为驱动表。这是因为较小的结果集可以减少连接操作的计算量，提高查询效率。
- **影响条件**: 
  - 表的大小（行数）。
  - 连接条件的过滤效果。
  - 是否存在合适的索引。
- **优化建议**:
  - 使用`EXPLAIN`语句查看执行计划，确认驱动表的选择。
  - 在连接列上创建适当的索引，以优化连接操作。
  - 尽量减少连接表的行数，例如通过使用子查询或视图来预先过滤数据。

## 索引覆盖

### 分页查询优化

常见的 MySQL 分页查询语句如下

```sql
select * from employees limit 10000,10;
```

上面表示从 employees 表中取出从 10001 行开始的 10 行记录，但是实际上 MySQL 是先查出10010行记录，然后再把前面的10000行记录删掉。可想而知，如果数据量特别大的情况下，这种方式的效率会很低。

从执行计划也可以看出上述 SQL 是扫描全表的 ALL

优化方案是利用索引覆盖，来指定一个索引让 MySQL 使用，例如使用主键索引

优化后的 SQL

```sql
EXPLAIN select * from employees e inner join (select id from employees limit 90000,5) ed on e.id = ed.id;
```

从执行计划上可以看到会先执行子查询，而子查询因为查询的列只有主键id，不需要提取其他数据，所以会走覆盖索引，直接使用主键索引来获取主键返回。然后再根据id查外面的查询。因为分页的话一页一般都是10条数据，数据量很小，所以外查询的效率也并不低。

同时 INNER JOIN 是 JOIN 类型中效率最高的，因为它只处理匹配的行，减少了需要处理的数据量。

### 排序优化

对于需要排序的分页查询，排序字段必须创建索引，优化思路和上面的一样，只是使用的索引从主键索引换到了排序字段的索引，因为 InnoDB 的索引结构上既有索引字段，也有回表用的主键字段。

优化前的 SQL

```sql
EXPLAIN select * from employees ORDER BY name limit 90000,5;
```

优化后的 SQL

```sql
EXPLAIN select * from employees e inner join (select id from employees order by name limit 90000,5) ed on e.id = ed.id;
```

当主键有序或排序字段唯一时，也可利用排序字段的有序性来优化

主键有序:

```sql
select * from employees e where e.id >= (select id from employees by name limit 90000,1) limit 5;
```

排序字段唯一:

```sql
select * from employees e where e.name >= (select name from employees by name limit 90000,1) limit 5;
```

# 在分库分表结构下，数据查询方案

## 基因法

**基因法**（Gene Method）是一种在分库分表（Sharding）架构中用于数据分布和查询优化的技术。其核心思想是将数据的关键信息（如用户ID）的一部分作为“基因”，并将其嵌入到其他相关数据的标识符（如订单ID）中，从而实现数据的有效分布和快速定位。

基因法主要用于解决以下问题：

1. **数据分布**：确保相关数据（如同一个用户的所有订单）分布在同一个数据库或数据表中。
2. **查询优化**：通过基因信息快速定位数据所在的数据库或数据表，避免全表扫描。

基因法的核心思想是利用取模运算的特性。具体来说，假设被除数是2的n次方，那么取模运算的结果（即余数）一定等于被除数二进制表示的最后n位。例如：

- 189 % 16 = 13
- 189 的二进制表示为：10111101
- 13 的二进制表示为：1101
- 189 的二进制最后四位（1101）与 13 的二进制相同

因此，基因法通过以下步骤实现：

1. **提取基因**：从用户ID中提取最后n位作为“基因”。
2. **嵌入基因**：将提取的基因嵌入到订单ID的后n位中。
3. **数据分布**：根据基因值对数据进行分库或分表。

基因法的具体实现

假设我们需要将用户的所有订单存放在同一个表中，并且能够通过用户ID或订单ID进行查询。以下是基因法的具体实现步骤：

1. **分库分表配置**：
   - 假设有16个数据库，使用用户ID的最后4位作为基因。
   - 例如，用户ID为189，其二进制为10111101，最后4位为1101，对应的十进制为13。因此，用户数据将存放在第14个数据库中（因为索引从1开始）。

2. **订单ID生成**：
   - 使用分布式ID生成算法（如雪花算法）生成订单ID。
   - 在生成订单ID时，将用户ID的基因嵌入到订单ID的最后4位中。
   - 例如，订单ID的前60位由分布式ID生成算法生成，最后4位嵌入用户ID的基因。

3. **数据查询**：
   - 通过用户ID查询时，先提取用户ID的基因，然后根据基因值定位到具体的数据库。
   - 通过订单ID查询时，直接提取订单ID的最后4位作为基因，然后定位到具体的数据库。

优点

1. **查询速度快**：通过基因信息可以快速定位数据所在的数据库或数据表，避免全表扫描。
2. **数据分布均匀**：基因法利用取模运算的特性，可以实现数据的均匀分布。

缺点

1. **扩容极其困难**：一旦需要增加数据库数量，原有数据需要重新分布和迁移。
2. **生成重复订单ID的风险**：在高并发环境下，基因法可能会导致订单ID重复。例如，雪花算法在同一毫秒内生成的订单ID可能因为基因相同而重复。

为了解决基因法的缺点，可以考虑以下改进措施：

1. **使用全局索引**：将基因信息存储在全局索引中，通过全局索引进行查询路由。
2. **改造ID生成算法**：在ID生成算法中预留足够的位用于基因嵌入，避免重复ID的产生。例如，改造雪花算法，增加基因位。
3. **动态扩容策略**：设计动态扩容策略，减少数据迁移的复杂性。

基因法是一种有效的分库分表技术，通过将基因信息嵌入到数据标识符中，实现数据的有效分布和快速查询。然而，在实际应用中，需要根据具体业务需求和数据规模，权衡基因法的优缺点，并进行相应的优化和改进。

## 全局索引法

**全局索引法**（Global Indexing）是一种在分库分表（Sharding）架构中用于优化查询性能的技术。其核心思想是通过在全局范围内维护一个索引，使得非分片键的查询能够快速定位到数据所在的分片，从而避免全库或全表扫描，提高查询效率。

在分布式数据库架构下，当业务需要通过非分片键进行查询时，传统的分库分表策略会导致查询性能下降。例如，在一个订单表中，如果分片键是用户ID（`o_custkey`），但业务需要通过订单ID（`o_orderkey`）进行查询时，如果订单ID不是分片键，那么需要查询所有分片才能得到最终结果。这种情况下，全局索引法可以显著提升查询性能。

全局索引法的实现主要分为以下几个步骤：

1. **创建全局索引表**：
   - 创建一个独立的索引表，表中包含非分片键和分片键。例如，在一个订单表中，索引表可以包含`o_orderkey`和`o_custkey`两个字段。
   - 索引表的分片键可以设置为非分片键本身，这样可以确保每个非分片键对应的记录只存在于一个分片中。

   ```sql
   CREATE TABLE idx_orderkey_custkey (
       o_orderkey INT,
       o_custkey INT,
       PRIMARY KEY (o_orderkey)
   );
   ```

2. **数据插入时维护索引**：
   - 在数据插入时，不仅需要在原表中插入数据，还需要同时在索引表中插入相应的索引记录。
   - 例如，当插入一条订单记录时，需要在订单表中插入订单数据，并在索引表中插入`o_orderkey`和`o_custkey`的对应关系。

3. **查询时使用全局索引**：
   - 当需要通过非分片键进行查询时，首先在全局索引表中查询对应的分片键。
   - 例如，通过`o_orderkey`查询订单详情时，首先在索引表中查询`o_orderkey`对应的`o_custkey`，然后根据`o_custkey`定位到具体的分片，最后在原表中查询订单详情。

   ```sql
   # Step 1: 查询索引表获取分片键
   SELECT o_custkey FROM idx_orderkey_custkey WHERE o_orderkey = 1;

   # Step 2: 根据分片键查询原表
   SELECT * FROM orders WHERE o_custkey = ? AND o_orderkey = 1;
   ```

优点

1. **查询速度快**：通过全局索引表，可以快速定位到数据所在的分片，避免全表扫描。
2. **灵活性高**：全局索引表可以根据需要灵活设计，支持多种非分片键的查询。

缺点

1. **存储空间增加**：需要额外的存储空间来维护全局索引表。
2. **维护复杂性增加**：在数据插入、更新和删除时，需要同时维护索引表，增加了系统的维护复杂性。
3. **性能开销**：在数据量非常大的情况下，索引表的查询和更新可能会带来性能开销。

全局索引法的优化策略

1. **使用分布式缓存**：
   - 将全局索引表的数据缓存到分布式缓存系统中（如Redis、Memcached），以减少数据库查询次数，提高查询性能。

2. **设计高效的索引表**：
   - 索引表的设计应尽量简洁，只包含必要的字段，避免冗余数据。
   - 可以考虑将索引表也进行分库分表，根据非分片键进行分片，以实现更高的查询效率。

3. **避免频繁的索引更新**：
   - 在数据更新频繁的场景下，尽量减少对索引表的频繁更新，可以通过批量更新或异步更新的方式优化性能。

全局索引法是一种有效的分库分表优化技术，通过在全局范围内维护索引，使得非分片键的查询能够快速定位到数据所在的分片，从而提高查询效率。然而，全局索引法也需要在存储空间和系统维护复杂性上进行权衡。在实际应用中，需要根据具体的业务需求和数据规模，选择合适的全局索引策略和优化方法。

# JSON 与 虚拟列 

MySQL 从 5.7 版本开始支持 JSON 数据类型，并在后续版本中不断增强其功能。同时，MySQL 提供了虚拟列（Generated Columns）的概念，允许基于其他列的数据自动计算生成新列。将 JSON 数据与虚拟列结合使用，可以极大地提升数据库的灵活性和查询效率。以下将详细介绍 MySQL 中的 JSON 数据类型、虚拟列以及两者结合的应用场景和最佳实践。

## JSON 数据类型

MySQL 5.7 及以上版本引入了原生的 JSON 数据类型，用于存储 JSON 格式的数据。与存储 JSON 字符串相比，原生 JSON 数据类型具有以下优势：

- **自动验证 JSON 格式**：存储时自动检查 JSON 数据的有效性，避免存储无效的 JSON 字符串。
- **优化存储结构**：MySQL 会以优化的二进制格式存储 JSON 数据，提升存储和访问效率。
- **支持丰富的 JSON 函数**：提供大量内置函数用于操作 JSON 数据，如查询、修改、创建等。

### 常用 JSON 函数

以下是一些常用的 MySQL JSON 函数：

- **创建 JSON 对象/数组**

  ```sql
  -- 创建 JSON 对象
  JSON_OBJECT('key1', 'value1', 'key2', 'value2')
  
  -- 创建 JSON 数组
  JSON_ARRAY('value1', 'value2', 'value3')
  ```

- **查询 JSON 数据**

  ```sql
  -- 从指定的 json 列中提取 json 某个字段的值
  JSON_EXTRACT(json_column, '$.path.to.element') 
  -- 简写方式
  json_column->'$.path.to.element'
  
  -- 获取指定路径的值并转换为特定类型
  JSON_UNQUOTE(json_column->'$.path.to.element') 
  -- 或者使用 ->> 操作符
  json_column->>'$.path.to.element'
  ```

- **修改 JSON 数据**

  ```sql
  -- 插入或更新 JSON 对象中的值
  JSON_SET(json_column, '$.path.to.element', 'new_value')
  
  -- 插入 JSON 对象中的值（如果路径不存在）
  JSON_INSERT(json_column, '$.path.to.element', 'new_value')
  
  -- 删除 JSON 对象中的值
  JSON_REMOVE(json_column, '$.path.to.element')
  ```

- **其他常用函数**

  ```sql
  -- 判断 JSON 数据是否为特定类型
  JSON_TYPE(json_column) = 'OBJECT' -- 或 'ARRAY', 'STRING', 'INTEGER', 'BOOLEAN', 'NULL'
  
  -- 获取 JSON 数组的长度
  JSON_LENGTH(json_column)
  
  -- 获取 JSON 对象的所有键
  JSON_KEYS(json_column)
  
  -- 合并两个 JSON 对象
  JSON_MERGE(json_column1, json_column2)
  ```

### JSON 数据类型的限制

- **不支持索引**：直接对 JSON 列创建索引是无效的，无法利用索引加速查询。
- **查询性能较低**：由于 JSON 数据存储为二进制格式，查询时需要进行解析，导致查询性能不如传统关系型数据。
- **存储空间较大**：相比于存储相同数据的传统列，JSON 列可能占用更多存储空间。

## 虚拟列（Generated Columns）

虚拟列是 MySQL 中的一种特殊列，其值由表达式自动计算生成，而不是直接存储在表中。虚拟列分为两种类型：

- **虚拟（Virtual）列**：在查询时动态计算，不占用存储空间。
- **存储（Stored）列**：在数据插入或更新时计算并存储，类似于普通列，占用存储空间。

语法示例：

```sql
CREATE TABLE example (
    id INT PRIMARY KEY,
    data JSON,
    -- 虚拟列
    json_value VARCHAR(255) GENERATED ALWAYS AS (data->>'$.path.to.element') VIRTUAL,
    -- 存储列
    json_value_stored VARCHAR(255) GENERATED ALWAYS AS (data->>'$.path.to.element') STORED
);
```

虚拟列的优势

- **简化查询语句**：可以将复杂的计算逻辑封装在虚拟列中，简化查询语句。
- **提高查询性能**：对于存储列，可以创建索引，从而加速查询。
- **增强数据完整性**：可以基于其他列的数据进行约束检查，确保数据的一致性。

虚拟列的限制

- **不支持存储函数**：虚拟列的表达式中不能使用存储函数，只能使用内置函数和操作符。
- **不支持触发器**：虚拟列不支持触发器，无法在数据变化时触发特定操作。
- **存储列占用存储空间**：存储列会占用额外的存储空间，需要权衡使用。

## JSON 与 虚拟列结合使用

将 JSON 数据与虚拟列结合使用，可以充分发挥两者的优势，弥补各自的不足。以下是一些常见的应用场景：

### 提取 JSON 数据并创建索引

由于 JSON 列无法直接创建索引，可以通过虚拟列提取 JSON 数据中的特定字段，并为其创建索引，从而加速查询。

**示例：**

假设有一个 `users` 表，其中包含一个 `profile` JSON 列：

```sql
CREATE TABLE users (
    id INT PRIMARY KEY,
    profile JSON
);
```

要查询 `profile` 中 `age` 字段大于 30 的用户，可以先创建虚拟列并为其创建索引：

```sql
ALTER TABLE users 
ADD COLUMN age INT GENERATED ALWAYS AS (profile->>'$.age') STORED,
ADD INDEX idx_age (age);
```

然后执行查询：

```sql
SELECT * FROM users WHERE age > 30;
```

**优势：**

- 避免使用 `JSON_EXTRACT` 函数进行查询，提升查询性能。
- 利用索引加速查询。

### 简化查询语句

使用虚拟列可以将复杂的 JSON 查询逻辑封装起来，简化查询语句。

**示例：**

查询用户的姓名和邮箱：

```sql
SELECT 
    (profile->>'$.name') AS name,
    (profile->>'$.email') AS email
FROM users;
```

可以创建虚拟列：

```sql
ALTER TABLE users 
ADD COLUMN name VARCHAR(255) GENERATED ALWAYS AS (profile->>'$.name') VIRTUAL,
ADD COLUMN email VARCHAR(255) GENERATED ALWAYS AS (profile->>'$.email') VIRTUAL;
```

然后简化查询：

```sql
SELECT name, email FROM users;
```

### 增强数据完整性

使用虚拟列可以对 JSON 数据进行约束检查，确保数据的一致性。

**示例：**

确保 `profile` 中的 `age` 字段为正整数：

```sql
ALTER TABLE users 
ADD COLUMN age INT GENERATED ALWAYS AS (profile->>'$.age') STORED,
ADD CHECK (age > 0);
```

## 最佳实践

1. **选择合适的列类型**：对于需要频繁查询和操作的 JSON 字段，考虑将其提取为虚拟列并创建索引。
2. **避免过度使用 JSON**：虽然 JSON 提供了灵活性，但过度使用可能导致数据冗余和查询复杂化。对于结构化数据，建议使用传统的关系型列。
3. **合理使用虚拟列**：虚拟列可以简化查询，但过多的虚拟列会增加维护难度。应根据实际需求合理设计虚拟列。
4. **利用存储列优化性能**：对于需要频繁访问的 JSON 字段，可以将其提取为存储列并创建索引，以提升查询性能。
5. **结合使用 JSON 函数和虚拟列**：灵活运用 JSON 函数和虚拟列，可以实现复杂的数据操作和查询需求。

## 示例

**场景：** 存储用户信息，其中包含地址信息。

**表结构：**

```sql
CREATE TABLE users (
    id INT PRIMARY KEY,
    name VARCHAR(255),
    address JSON
);
```

**插入数据：**

```sql
INSERT INTO users (id, name, address) VALUES 
(1, 'Alice', JSON_OBJECT('street', '123 Main St', 'city', 'New York', 'zip', '10001')),
(2, 'Bob', JSON_OBJECT('street', '456 Maple Ave', 'city', 'Los Angeles', 'zip', '90001'));
```

**创建虚拟列并创建索引：**

```sql
ALTER TABLE users 
ADD COLUMN city VARCHAR(255) GENERATED ALWAYS AS (address->>'$.city') STORED,
ADD INDEX idx_city (city);
```

**查询用户所在城市为 "New York" 的用户：**

```sql
SELECT * FROM users WHERE city = 'New York';
```

**结果：**

```
+----+-------+--------------------------------------------------------------------+
| id | name  | address                                                            |
+----+-------+--------------------------------------------------------------------+
|  1 | Alice | {"street": "123 Main St", "city": "New York", "zip": "10001"}      |
+----+-------+--------------------------------------------------------------------+
```

MySQL 的 JSON 数据类型和虚拟列为开发者提供了强大的工具来处理半结构化和结构化数据。通过合理地结合使用 JSON 和虚拟列，可以实现高效的数据存储、查询和操作。然而，在实际应用中，需要根据具体需求权衡使用 JSON 和传统关系型列的优缺点，并结合虚拟列的优势进行优化设计。

## 参考资料

- [MySQL 官方文档 - JSON 函数](https://dev.mysql.com/doc/refman/8.0/en/json-function-reference.html)
- [MySQL 官方文档 - Generated Columns](https://dev.mysql.com/doc/refman/8.0/en/create-table-generated-columns.html) 

# 窗口函数

窗口函数（Window Functions）是 SQL 中一种强大的工具，用于在查询结果集上进行复杂的计算和分析。与传统的聚合函数不同，窗口函数不会将结果集缩减为一行，而是对每一行都生成一个结果，同时考虑与该行相关的其他行（即“窗口”）。MySQL 从 8.0 版本开始支持窗口函数，这为数据分析和复杂查询提供了极大的便利。

本文将详细介绍 MySQL 窗口函数的概念、语法、常用函数以及实际应用示例。

## 1. 窗口函数概述

窗口函数允许在查询结果集的每一行上执行计算，同时可以访问与当前行相关的一组行（即“窗口”）。这使得窗口函数非常适合用于以下场景：

- **排名和排序**：如计算行号、排名、百分比排名等。
- **聚合分析**：如计算累计和、移动平均等。
- **比较和差异分析**：如计算当前行与前一行的差异等。

## 2. 窗口函数的基本语法

窗口函数的语法结构如下：

```sql
SELECT
    column1,
    column2,
    window_function() OVER (
        [PARTITION BY partition_expression]
        [ORDER BY order_expression]
        [frame_clause]
    ) AS alias
FROM
    table_name;
```

### 组成部分说明：

- **window_function()**：具体的窗口函数，如 `ROW_NUMBER()`, `RANK()`, `SUM()` 等。
- **OVER 子句**：定义窗口的范围和规则。
  - **PARTITION BY**：将结果集划分为多个分区，窗口函数在每个分区上独立计算。
  - **ORDER BY**：定义分区内的排序顺序，影响窗口函数的行为。
  - **frame_clause**（可选）：定义窗口的框架范围，用于滑动窗口等高级功能。

## 3. 常用窗口函数

### 3.1 排名函数

- **ROW_NUMBER()**：为分区内的每一行分配一个唯一的顺序号。
  
  ```sql
  SELECT
      employee_id,
      salary,
      ROW_NUMBER() OVER (ORDER BY salary DESC) AS salary_rank
  FROM
      employees;
  ```

- **RANK()**：为分区内的每一行分配一个排名，如果存在相同的值，则排名相同，后续排名会跳过。
  
  ```sql
  SELECT
      employee_id,
      salary,
      RANK() OVER (ORDER BY salary DESC) AS salary_rank
  FROM
      employees;
  ```

- **DENSE_RANK()**：与 `RANK()` 类似，但即使存在相同的值，后续排名也不会跳过。
  
  ```sql
  SELECT
      employee_id,
      salary,
      DENSE_RANK() OVER (ORDER BY salary DESC) AS salary_rank
  FROM
      employees;
  ```

### 3.2 聚合函数

- **SUM() OVER()**：计算累计总和。
  
  ```sql
  SELECT
      order_id,
      amount,
      SUM(amount) OVER (ORDER BY order_date ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS running_total
  FROM
      orders;
  ```

- **AVG() OVER()**：计算移动平均。
  
  ```sql
  SELECT
      order_id,
      amount,
      AVG(amount) OVER (ORDER BY order_date ROWS BETWEEN 2 PRECEDING AND CURRENT ROW) AS moving_average
  FROM
      orders;
  ```

- **COUNT() OVER()**：计算分区内的行数。
  
  ```sql
  SELECT
      department_id,
      employee_id,
      COUNT(*) OVER (PARTITION BY department_id) AS employee_count
  FROM
      employees;
  ```

### 3.3 偏移函数

- **LAG()**：获取当前行之前的某一行数据。
  
  ```sql
  SELECT
      order_id,
      order_date,
      amount,
      LAG(amount) OVER (ORDER BY order_date) AS previous_amount
  FROM
      orders;
  ```

- **LEAD()**：获取当前行之后的某一行数据。
  
  ```sql
  SELECT
      order_id,
      order_date,
      amount,
      LEAD(amount) OVER (ORDER BY order_date) AS next_amount
  FROM
      orders;
  ```

- **FIRST_VALUE()** 和 **LAST_VALUE()**：获取窗口内的第一个和最后一个值。
  
  ```sql
  SELECT
      order_id,
      order_date,
      amount,
      FIRST_VALUE(amount) OVER (ORDER BY order_date ROWS BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING) AS first_amount,
      LAST_VALUE(amount) OVER (ORDER BY order_date ROWS BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING) AS last_amount
  FROM
      orders;
  ```

### 3.4 其他函数

- **NTILE()**：将分区内的行分配到指定数量的桶中。
  
  ```sql
  SELECT
      employee_id,
      salary,
      NTILE(4) OVER (ORDER BY salary DESC) AS quartile
  FROM
      employees;
  ```

- **CUME_DIST()**：计算累计分布值。
  
  ```sql
  SELECT
      employee_id,
      salary,
      CUME_DIST() OVER (ORDER BY salary DESC) AS cumulative_distribution
  FROM
      employees;
  ```

## 4. 窗口函数的实际应用示例

### 4.1 计算累计总和

假设有一个销售订单表 `orders`，包含 `order_id`, `order_date`, `amount` 等字段。我们希望计算每个订单的累计销售总额。

```sql
SELECT
    order_id,
    order_date,
    amount,
    SUM(amount) OVER (ORDER BY order_date ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS running_total
FROM
    orders;
```

### 4.2 计算移动平均

计算每个订单前后两天的平均销售额。

```sql
SELECT
    order_id,
    order_date,
    amount,
    AVG(amount) OVER (ORDER BY order_date ROWS BETWEEN 2 PRECEDING AND CURRENT ROW) AS moving_average
FROM
    orders;
```

### 4.3 排名与分位数

将员工按薪水排名，并分配到不同的分位数中。

```sql
SELECT
    employee_id,
    salary,
    RANK() OVER (ORDER BY salary DESC) AS salary_rank,
    NTILE(5) OVER (ORDER BY salary DESC) AS salary_quintile
FROM
    employees;
```

### 4.4 偏移分析

比较当前订单金额与前一个订单金额的差异。

```sql
SELECT
    order_id,
    order_date,
    amount,
    LAG(amount) OVER (ORDER BY order_date) AS previous_amount,
    amount - LAG(amount) OVER (ORDER BY order_date) AS difference
FROM
    orders;
```

## 5. 窗口函数的高级用法

### 5.1 分区与排序

通过 `PARTITION BY` 和 `ORDER BY` 的结合，可以实现更复杂的分析。例如，计算每个部门的累计销售额。

```sql
SELECT
    department_id,
    employee_id,
    salary,
    SUM(salary) OVER (PARTITION BY department_id ORDER BY employee_id) AS department_running_total
FROM
    employees;
```

### 5.2 滑动窗口

使用 `ROWS BETWEEN` 子句定义窗口的框架范围，实现滑动窗口。例如，计算每个订单前后两天的销售额总和。

```sql
SELECT
    order_id,
    order_date,
    amount,
    SUM(amount) OVER (ORDER BY order_date ROWS BETWEEN 1 PRECEDING AND 1 FOLLOWING) AS sliding_sum
FROM
    orders;
```

### 5.3 窗口框架

窗口框架定义了窗口函数在当前行周围的作用范围。可以使用以下方式定义：

- **ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW**：从分区的第一行到当前行。
- **ROWS BETWEEN CURRENT ROW AND UNBOUNDED FOLLOWING**：从当前行到分区的最后一行。
- **ROWS BETWEEN 2 PRECEDING AND 2 FOLLOWING**：当前行前后两行。

```sql
SELECT
    order_id,
    order_date,
    amount,
    AVG(amount) OVER (ORDER BY order_date ROWS BETWEEN 2 PRECEDING AND 2 FOLLOWING) AS average_amount
FROM
    orders;
```

## 6. 注意事项

1. **性能考虑**：窗口函数在处理大数据集时可能会影响性能，特别是在没有适当索引的情况下。因此，在使用窗口函数时，应确保相关列有适当的索引。
2. **MySQL 版本**：窗口函数在 MySQL 8.0 及以上版本中可用。如果使用较低版本，可以考虑升级或使用其他替代方法（如子查询或自连接）。
3. **窗口函数顺序**：窗口函数在 `SELECT` 语句中的执行顺序通常在 `ORDER BY` 之前，但在 `WHERE`, `GROUP BY` 和 `HAVING` 之后。因此，无法在窗口函数中使用 `WHERE` 子句中的别名。







