PostgreSQL 是一个功能强大的开源关系型数据库管理系统，以其稳定性、可靠性和高性能而闻名。

PostgreSQL 9.3 文档 https://www.rockdata.net/zh-cn/docs/9.3/index.html

# 数据类型

## 数值类型

| **特性**             | **MySQL**                                                                 | **PostgreSQL**                                                           |
|-----------------------|---------------------------------------------------------------------------|---------------------------------------------------------------------------|
| **整数类型**         |                                                                           |                                                                           |
| `TINYINT`            | 1 字节，范围：-128 到 127 或 0 到 255（无符号）                          | 不支持（可使用 `SMALLINT` 代替）                                         |
| `SMALLINT`           | 2 字节，范围：-32,768 到 32,767 或 0 到 65,535（无符号）                 | 2 字节，范围：-32,768 到 32,767 或 0 到 65,535（无符号）                 |
| `MEDIUMINT`          | 3 字节，范围：-8,388,608 到 8,388,607 或 0 到 16,777,215（无符号）       | 不支持（可使用 `INTEGER` 或 `BIGINT` 代替）                             |
| `INT` 或 `INTEGER`   | 4 字节，范围：-2,147,483,648 到 2,147,483,647 或 0 到 4,294,967,295（无符号） | 4 字节，范围：-2,147,483,648 到 2,147,483,647 或 0 到 4,294,967,295（无符号） |
| `BIGINT`             | 8 字节，范围：-9,223,372,036,854,775,808 到 9,223,372,036,854,775,807 或 0 到 18,446,744,073,709,551,615（无符号） | 8 字节，范围：-9,223,372,036,854,775,808 到 9,223,372,036,854,775,807 或 0 到 18,446,744,073,709,551,615（无符号） |
| **定点类型**         |                                                                           |                                                                           |
| `DECIMAL` 或 `NUMERIC` | 可变精度和规模，存储为字符串，精确数值存储                             | `NUMERIC` 和 `DECIMAL` 相同，精确数值存储，精度和规模可指定             |
| **浮点类型**         |                                                                           |                                                                           |
| `FLOAT`              | 4 字节，单精度浮点数                                                     | 4 字节，单精度浮点数                                                     |
| `DOUBLE`             | 8 字节，双精度浮点数                                                     | 8 字节，双精度浮点数                                                     |
| `REAL`               | 4 字节（默认），单精度浮点数（取决于 SQL 模式）                          | 4 字节，单精度浮点数（与 `FLOAT(24)` 相同）                             |
| **位类型**           |                                                                           |                                                                           |
| `BIT`                | 可变长度，范围：1 到 64 位                                               | 可变长度，范围：1 到 83886080 位                                         |
| `BOOL` 或 `BOOLEAN`  | `TINYINT(1)` 的别名，0 表示 false，非零表示 true                         | `BOOLEAN` 是 `SMALLINT` 的别名，0 表示 false，1 表示 true               |
| **序列类型**         |                                                                           |                                                                           |
| `SERIAL`             | `BIGINT UNSIGNED NOT NULL AUTO_INCREMENT UNIQUE` 的别名                 | `SERIAL` 是 `INTEGER` 的别名，`BIGSERIAL` 是 `BIGINT` 的别名           |
| `BIGSERIAL`         | `BIGINT UNSIGNED NOT NULL AUTO_INCREMENT UNIQUE` 的别名                 | `BIGSERIAL` 是 `BIGINT` 的别名                                           |
| **货币类型**         | 不支持（通常使用 `DECIMAL` 或 `NUMERIC`）                               | `MONEY` 类型，存储金额，精度为 2 位小数                                 |
| **范围类型**         | 不支持（可使用 `CHECK` 约束或应用层处理）                                | 支持范围类型，如 `int4range`, `int8range`, `numrange` 等               |

详细说明：

1. **整数类型**：
   - MySQL 提供了多种整数类型，如 `TINYINT`, `SMALLINT`, `MEDIUMINT`, `INT`, `BIGINT`，每种类型有不同的存储大小和数值范围。
   - PostgreSQL 的整数类型与 MySQL 类似，但不支持 `MEDIUMINT`，需要使用 `INTEGER` 或 `BIGINT` 代替。

2. **定点类型**：
   - MySQL 和 PostgreSQL 都支持 `DECIMAL` 和 `NUMERIC` 类型，用于存储精确的数值数据。
   - 在 MySQL 中，`DECIMAL` 和 `NUMERIC` 是相同的类型，存储为字符串格式，以保持精度。

3. **浮点类型**：
   - 两种数据库都支持 `FLOAT` 和 `DOUBLE` 类型，用于存储浮点数。
   - MySQL 的 `REAL` 类型默认是单精度浮点数，但具体行为取决于 SQL 模式。

4. **位类型**：
   - MySQL 的 `BIT` 类型用于存储位数据，范围为 1 到 64 位。
   - PostgreSQL 的 `BIT` 类型支持更广泛的位长度，范围为 1 到 83886080 位。

5. **布尔类型**：
   - MySQL 使用 `BOOL` 或 `BOOLEAN` 作为 `TINYINT(1)` 的别名，0 表示 false，非零表示 true。
   - PostgreSQL 的 `BOOLEAN` 是 `SMALLINT` 的别名，0 表示 false，1 表示 true。

6. **序列类型**：
   - MySQL 使用 `SERIAL` 和 `BIGSERIAL` 作为 `BIGINT UNSIGNED NOT NULL AUTO_INCREMENT UNIQUE` 的别名。
   - PostgreSQL 的 `SERIAL` 是 `INTEGER` 的别名，`BIGSERIAL` 是 `BIGINT` 的别名。PostgreSQL 中可以使用 SERIAL 或 BIGSERIAL 数据类型来实现 MySQL 的主键自增效果。

7. **货币类型**：
   - MySQL 不支持专门的货币类型，通常使用 `DECIMAL` 或 `NUMERIC` 来存储金额。
   - PostgreSQL 提供了 `MONEY` 类型，用于存储货币金额，精度为 2 位小数。

8. **范围类型**：
   - MySQL 不支持范围类型，需要使用 `CHECK` 约束或应用层处理。
   - PostgreSQL 支持多种范围类型，如 `int4range`, `int8range`, `numrange` 等。

### 创建自增列

在 PostgreSQL 中，可以使用 `SERIAL` 或 `BIGSERIAL` 数据类型来实现类似于 MySQL 中主键自增（auto-increment）的效果。虽然 `SERIAL` 和 `BIGSERIAL` 并不是真正的数据类型，而是 PostgreSQL 提供的一种便捷的语法，用于创建一个带有自增序列的列。

1. 使用 `SERIAL` 和 `BIGSERIAL`

- **`SERIAL`**:
  - 相当于 `INTEGER` 类型。
  - 默认情况下，`SERIAL` 会创建一个从 1 开始的序列，每次插入时自动递增 1。
  - 适用于大多数需要自增主键的场景。

- **`BIGSERIAL`**:
  - 相当于 `BIGINT` 类型。
  - 适用于需要更大范围的主键值的情况。
  - 同样会创建一个从 1 开始的序列，每次插入时自动递增 1。

2. 创建表的示例

以下是如何在 PostgreSQL 中使用 `SERIAL` 和 `BIGSERIAL` 创建带有自增主键的表的示例：

```sql
-- 使用 SERIAL 创建自增主键
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 使用 BIGSERIAL 创建自增主键
CREATE TABLE orders (
    order_id BIGSERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    total_amount NUMERIC(10, 2) NOT NULL,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

3. 插入数据

在插入数据时，不需要为自增列指定值，PostgreSQL 会自动为其生成：

```sql
INSERT INTO users (username, email) VALUES ('alice', 'alice@example.com');
INSERT INTO users (username, email) VALUES ('bob', 'bob@example.com');

INSERT INTO orders (user_id, total_amount) VALUES (1, 99.99);
INSERT INTO orders (user_id, total_amount) VALUES (2, 149.49);
```

4. 查看序列

每个 `SERIAL` 或 `BIGSERIAL` 列都会自动创建一个序列，可以通过 `\d` 命令查看表的结构：

```sql
\d users
```

输出示例：

```
                                      Table "public.users"
   Column   |          Type          | Collation | Nullable |               Default                
------------+------------------------+-----------+----------+-------------------------------------
 id         | integer                |           | not null | nextval('users_id_seq'::regclass)
 username   | character varying(50)  |           | not null | 
 email      | character varying(100) |           | not null | 
 created_at | timestamp without time zone |           |          | CURRENT_TIMESTAMP
Indexes:
    "users_pkey" PRIMARY KEY, btree (id)
```

5. 序列的详细说明

- **序列（Sequence）**: PostgreSQL 使用序列来生成自增值。序列是一个独立的数据库对象，可以被多个表或多个列共享。
- **序列的创建**: 当使用 `SERIAL` 或 `BIGSERIAL` 时，PostgreSQL 会自动创建一个序列，并将其设置为该列的默认值。
- **序列的修改**: 可以使用 `ALTER SEQUENCE` 命令来修改序列的属性，例如起始值、步长等。

6. 注意事项

- **唯一性**: `SERIAL` 和 `BIGSERIAL` 生成的序列值是唯一的，因此非常适合作为主键。
- **并发性**: PostgreSQL 的序列机制在高并发环境下表现良好，能够保证自增值的唯一性和顺序性。
- **性能**: 使用序列作为自增主键可以提高插入性能，因为不需要在插入时进行额外的检查或锁定。

7. 替代方案

虽然 `SERIAL` 和 `BIGSERIAL` 是最常用的方法，但 PostgreSQL 还提供了其他方式来实现自增主键，例如使用 `GENERATED` 关键字：

```sql
CREATE TABLE products (
    product_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price NUMERIC(10, 2) NOT NULL
);
```

这种方法在 PostgreSQL 10 及以上版本中可用，提供了更现代和灵活的方式来处理自增主键。

## 字符类型

| **特性**             | **MySQL**                                                                 | **PostgreSQL**                                                                 |
|----------------------|---------------------------------------------------------------------------|-------------------------------------------------------------------------------|
| **CHAR 类型**        | - 固定长度字符串。<br>- 长度范围：1 到 255 个字符。<br>- 不足长度时用空格填充。 | - 固定长度字符串。<br>- 长度范围：1 到 10485760 个字符。<br>- 不足长度时用空格填充。 |
| **VARCHAR 类型**     | - 可变长度字符串。<br>- 长度范围：1 到 65535 个字符。<br>- 存储实际字符长度，不足长度时不填充。 | - 可变长度字符串。<br>- 长度范围：1 到 10485760 个字符。<br>- 存储实际字符长度，不足长度时不填充。 |
| **TEXT 类型**        | - 可变长度文本。<br>- 长度范围：最大 4GB。<br>- 适用于存储大量文本数据。 | - 可变长度文本。<br>- 长度范围：最大 1GB。<br>- 适用于存储大量文本数据。 |
| **ENUM 类型**        | - 枚举类型。<br>- 允许定义一组预定义的字符串值。<br>- 存储为整数，内部映射到字符串。 | - 不直接支持 ENUM 类型。<br>- 可以使用 CHECK 约束或自定义类型实现类似功能。 |
| **SET 类型**        | - 集合类型。<br>- 允许存储零个或多个预定义的字符串值。<br>- 存储为位字段。 | - 不直接支持 SET 类型。<br>- 可以使用数组类型或自定义类型实现类似功能。 |
| **BLOB 类型**        | - 二进制大对象。<br>- 适用于存储二进制数据，如图像、音频等。 | - 二进制大对象。<br>- 类似于 MySQL 的 BLOB 类型。 |
| **TINYTEXT 类型**    | - 可变长度文本。<br>- 最大长度 255 字节。 | - 不直接支持 TINYTEXT 类型。<br>- 可以使用 TEXT 类型代替。 |
| **MEDIUMTEXT 类型**  | - 可变长度文本。<br>- 最大长度 16MB。 | - 不直接支持 MEDIUMTEXT 类型。<br>- 可以使用 TEXT 类型代替。 |
| **LONGTEXT 类型**    | - 可变长度文本。<br>- 最大长度 4GB。 | - 不直接支持 LONGTEXT 类型。<br>- 可以使用 TEXT 类型代替。 |
| **存储方式**         | - CHAR 和 VARCHAR 类型存储为定长或变长字符串，具体取决于类型和内容。 | - CHAR 和 VARCHAR 类型存储为变长字符串，TEXT 类型也存储为变长字符串。 |
| **字符集支持**       | - 支持多种字符集，包括 UTF-8、UTF-16 等。 | - 支持多种字符集，包括 UTF-8、UTF-16 等。 |
| **性能**             | - CHAR 类型在固定长度字符串操作上性能较好。<br>- VARCHAR 和 TEXT 类型在处理可变长度字符串时性能较好。 | - CHAR 和 VARCHAR 类型在处理固定和可变长度字符串时性能较好。<br>- TEXT 类型在处理大量文本数据时性能较好。 |
| **默认值**           | - 可以为 CHAR 和 VARCHAR 类型设置默认值。 | - 可以为 CHAR、VARCHAR 和 TEXT 类型设置默认值。 |
| **索引支持**         | - CHAR 和 VARCHAR 类型支持索引。 | - CHAR、VARCHAR 和 TEXT 类型支持索引。 |
| **约束支持**         | - 支持多种约束，如 NOT NULL、UNIQUE 等。 | - 支持多种约束，如 NOT NULL、UNIQUE、CHECK 等。 |

- **MySQL** 和 **PostgreSQL** 在字符类型上有很多相似之处，但也有一些差异。
- **PostgreSQL** 的字符类型长度范围通常更大，尤其是 TEXT 类型。
- **MySQL** 提供了更多的专用文本类型，如 TINYTEXT、MEDIUMTEXT 和 LONGTEXT，而 **PostgreSQL** 则主要使用 TEXT 类型。
- **PostgreSQL** 不直接支持 ENUM 和 SET 类型，但可以通过其他方式实现类似功能。
- 两者在字符集支持和索引方面都有良好的支持，但在具体实现和性能上可能会有所不同。

## 时间类型

| **功能**           | **MySQL 数据类型** | **PostgreSQL 数据类型** | **描述**                                                                 |
|---------------------|--------------------|-------------------------|--------------------------------------------------------------------------|
| 日期                 | `DATE`             | `DATE`                  | 存储日期（年、月、日），范围大致相同。                                   |
| 时间                 | `TIME`             | `TIME`                  | 存储时间（小时、分钟、秒），范围和精度略有不同。                         |
| 日期和时间           | `DATETIME`         | `TIMESTAMP`, `TIMESTAMP WITHOUT TIME ZONE` | MySQL 的 `DATETIME` 和 PostgreSQL 的 `TIMESTAMP` 都存储日期和时间，但 PostgreSQL 的 `TIMESTAMP` 可以包含时区信息。 |
| 带时区的日期和时间 | 不支持             | `TIMESTAMP WITH TIME ZONE` | PostgreSQL 特有的类型，允许存储带有时区信息的日期和时间。                 |
| 年份                 | `YEAR`             | `YEAR`（通过 `INTEGER` 或 `DATE` 实现） | MySQL 提供了专门的 `YEAR` 类型，而 PostgreSQL 通常使用 `INTEGER` 或 `DATE` 来表示年份。 |
| 微秒级精度          | `DATETIME(6)`, `TIMESTAMP(6)` | `TIME`, `TIMESTAMP`（默认支持微秒） | PostgreSQL 默认支持微秒级精度，而 MySQL 需要指定精度。                   |
| 自动初始化和更新   | `DEFAULT CURRENT_TIMESTAMP`, `ON UPDATE CURRENT_TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP`, `DEFAULT NOW()`, `USING` 表达式 | 两者都支持自动初始化和更新，但语法略有不同。                           |
| 时区处理            | 仅存储和检索时区信息 | `TIMESTAMP WITH TIME ZONE` 自动转换时区 | PostgreSQL 提供了更强大的时区处理功能。                                 |

1. **DATE**:
   - **MySQL**: 存储日期，范围从 '1000-01-01' 到 '9999-12-31'。
   - **PostgreSQL**: 同样存储日期，范围从 '4713 BC' 到 '5874897 AD'。

2. **TIME**:
   - **MySQL**: 存储时间，范围从 '-838:59:59' 到 '838:59:59'，支持微秒。
   - **PostgreSQL**: 存储时间，范围从 '00:00:00' 到 '24:00:00'，默认支持微秒。

3. **DATETIME vs TIMESTAMP**:
   - **MySQL**: `DATETIME` 存储日期和时间，范围从 '1000-01-01 00:00:00' 到 '9999-12-31 23:59:59'。`TIMESTAMP` 存储日期和时间，范围从 '1970-01-01 00:00:01' UTC 到 '2038-01-19 03:14:07' UTC。
   - **PostgreSQL**: `TIMESTAMP` 存储日期和时间，范围从 '4713 BC' 到 '5874897 AD'。`TIMESTAMP WITH TIME ZONE` 存储带有时区信息的日期和时间。

4. **YEAR**:
   - **MySQL**: 提供专门的 `YEAR` 类型，存储年份，范围从 1901 到 2155。
   - **PostgreSQL**: 通常使用 `INTEGER` 或 `DATE` 类型来表示年份。

5. **微秒级精度**:
   - **MySQL**: 需要在类型后指定精度，例如 `DATETIME(6)`。
   - **PostgreSQL**: 默认支持微秒级精度。

6. **自动初始化和更新**:
   - **MySQL**: 使用 `DEFAULT CURRENT_TIMESTAMP` 和 `ON UPDATE CURRENT_TIMESTAMP`。
   - **PostgreSQL**: 使用 `DEFAULT CURRENT_TIMESTAMP`, `DEFAULT NOW()`, 或 `USING` 表达式。

7. **时区处理**:
   - **MySQL**: 仅存储和检索时区信息，不进行自动转换。
   - **PostgreSQL**: `TIMESTAMP WITH TIME ZONE` 会自动将输入的时间转换为 UTC 存储，并在检索时转换为会话的时区。

- **MySQL** 提供了简单直接的时间/日期类型，适合大多数基本需求。
- **PostgreSQL** 提供了更丰富和强大的时间/日期类型，特别是在时区和精度处理方面更为灵活和强大。

## 其他类型

PostgreSQL 支持极其丰富的数据类型，例如 json 类型、xml 类型、几何类型等等，甚至可以自定义数据类型，详情可参考文章开头的文档。

# 序列

在数据库管理系统中，序列（Sequence）是一种用于生成唯一数字值的数据库对象，常用于生成主键或需要唯一标识符的场景。PostgreSQL 和 Oracle 都支持序列，但它们在实现细节和使用方式上存在一些差异。以下是对 PostgreSQL 和 Oracle 中序列的详细对比：

1. 创建序列

**PostgreSQL**

在 PostgreSQL 中，可以使用 `CREATE SEQUENCE` 语句来创建序列：

```sql
CREATE SEQUENCE my_sequence
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 1000000
    CACHE 1
    NO CYCLE;
```

- **常用参数**：
  - `START WITH`: 序列的起始值。
  - `INCREMENT BY`: 序列的步长。
  - `MINVALUE` 和 `MAXVALUE`: 序列的最小值和最大值。
  - `CACHE`: 预分配并缓存的序列值数量。
  - `CYCLE`: 是否允许序列在达到最大值后循环。

**Oracle**

在 Oracle 中，同样使用 `CREATE SEQUENCE` 语句来创建序列：

```sql
CREATE SEQUENCE my_sequence
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 1000000
    CACHE 20
    NOCYCLE;
```

- **常用参数**：
  - `START WITH`: 序列的起始值。
  - `INCREMENT BY`: 序列的步长。
  - `MINVALUE` 和 `MAXVALUE`: 序列的最小值和最大值。
  - `CACHE`: 预分配并缓存的序列值数量。
  - `CYCLE`: 是否允许序列在达到最大值后循环。

2. 获取序列的下一个值

**PostgreSQL**

在 PostgreSQL 中，可以使用 `nextval` 函数来获取序列的下一个值：

```sql
SELECT nextval('my_sequence');
```

或者在插入数据时使用 `DEFAULT` 关键字：

```sql
INSERT INTO my_table (id, data) VALUES (DEFAULT, 'example');
```

**Oracle**

在 Oracle 中，同样使用 `nextval` 来获取序列的下一个值：

```sql
SELECT my_sequence.nextval FROM dual;
```

或者在插入数据时使用序列的 `nextval`：

```sql
INSERT INTO my_table (id, data) VALUES (my_sequence.nextval, 'example');
```

3. 序列的缓存机制

**PostgreSQL**

PostgreSQL 的序列缓存机制允许数据库预分配一定数量的序列值，以提高性能。`CACHE` 参数指定了预分配的数量。例如，`CACHE 1` 表示不缓存，每次获取值时都从磁盘读取。默认情况下，PostgreSQL 的序列缓存为 1（即不缓存），但可以通过 CACHE 参数进行调整。

**Oracle**

Oracle 也支持序列缓存，`CACHE` 参数指定了预分配的数量。默认情况下，Oracle 的序列缓存为 20。缓存可以提高序列的性能，但在大规模并发环境下，可能会导致序列值的不连续性。例如，数据库实例崩溃或重启时，内存中的缓存序列值会丢失。或者，当序列的缓存值为 50 时，如果数据库在分配了 30 个序列值后崩溃，那么接下来的序列值将从 31 开始，而不是 51。

4. 序列的持久性

**PostgreSQL**

PostgreSQL 的序列是持久化的，序列的当前值会保存在系统表中，如 pg_class 和 pg_sequence。即使数据库重启，序列值也会保持不变。这些值是持久化的，并且在数据库重启后会被正确读取和使用。

**Oracle**

Oracle 的序列也是持久化的，序列的当前值保存在数据字典中，数据库重启后，序列值会从磁盘中恢复。Oracle 通过控制文件和序列文件来跟踪序列的当前值，即使在缓存丢失的情况下，Oracle 也会从控制文件中获取最新的序列值，并继续递增，从而避免重复发号。

5. 序列的使用方式

**PostgreSQL**

在 PostgreSQL 中，序列通常与表的列关联，通过 `SERIAL` 或 `BIGSERIAL` 数据类型自动创建序列并将其设置为列的默认值。例如：

```sql
CREATE TABLE my_table (
    id SERIAL PRIMARY KEY,
    data VARCHAR(100)
);
```

**Oracle**

在 Oracle 中，序列是独立的数据库对象，通常在插入数据时显式地使用序列的 `nextval`。例如：

```sql
CREATE TABLE my_table (
    id NUMBER PRIMARY KEY,
    data VARCHAR2(100)
);

INSERT INTO my_table (id, data) VALUES (my_sequence.nextval, 'example');
```

6. 序列的权限管理

**PostgreSQL**

在 PostgreSQL 中，序列是作为表的一部分存在的，权限管理通常与表一起进行。可以通过 `GRANT` 和 `REVOKE` 语句来控制对序列的访问权限。

**Oracle**

在 Oracle 中，序列是独立的数据库对象，权限管理是独立的。可以使用 `GRANT` 和 `REVOKE` 语句来控制对序列的访问权限。

7. 序列的修改

**PostgreSQL**

在 PostgreSQL 中，可以使用 `ALTER SEQUENCE` 语句来修改序列的属性。例如：

```sql
ALTER SEQUENCE my_sequence INCREMENT BY 10;
```

**Oracle**

在 Oracle 中，同样使用 `ALTER SEQUENCE` 语句来修改序列的属性。例如：

```sql
ALTER SEQUENCE my_sequence INCREMENT BY 10;
```

8. 序列的删除

**PostgreSQL**

在 PostgreSQL 中，可以使用 `DROP SEQUENCE` 语句来删除序列：

```sql
DROP SEQUENCE my_sequence;
```

**Oracle**

在 Oracle 中，同样使用 `DROP SEQUENCE` 语句来删除序列：

```sql
DROP SEQUENCE my_sequence;
```

虽然 PostgreSQL 和 Oracle 都支持序列，并且在基本功能上有许多相似之处，但在实现细节和使用方式上存在一些差异：

1. **创建和修改**: 两者都使用 `CREATE SEQUENCE` 和 `ALTER SEQUENCE` 语句，但 PostgreSQL 提供了 `SERIAL` 和 `BIGSERIAL` 数据类型，简化了与表的关联。
2. **获取值**: 两者都使用 `nextval`，但 Oracle 需要使用 `FROM dual`。
3. **缓存机制**: 两者都支持缓存，但 Oracle 的默认缓存较大。
4. **使用方式**: PostgreSQL 通常将序列与表列关联，而 Oracle 的序列是独立的，需要在插入时显式使用。
5. **权限管理**: 两者都支持权限管理，但 PostgreSQL 的序列权限与表权限相关联，而 Oracle 的序列权限是独立的。

# 索引

PostgreSQL 支持多种类型的索引，每种索引适用于不同的查询场景。以下是 PostgreSQL 中常见的索引类型及其特点：

1. B-tree 索引
- **适用场景**：B-tree 索引是 PostgreSQL 默认的索引类型，适用于大多数查询场景。它适用于范围查询、排序操作（ORDER BY）、等值查询（=）以及使用比较运算符（如 >, <, >=, <=）的查询。
- **特点**：B-tree 索引支持高效的查找、插入和删除操作，适用于高基数的列。**B-tree 索引依然遵循最左匹配原则**。

2. Hash 索引
- **适用场景**：Hash 索引适用于简单的等值查询（=）。
- **特点**：Hash 索引在等值查询上比 B-tree 索引更快，但在范围查询、排序和部分匹配查询中不如 B-tree 索引高效。需要注意的是，Hash 索引在 PostgreSQL 10 之前的版本中不支持 WAL（Write-Ahead Logging），因此在故障恢复时可能会出现问题。

3. GiST（Generalized Search Tree）索引
- **适用场景**：GiST 索引适用于需要支持复杂查询的数据类型，如几何数据、文本搜索、范围类型等。
- **特点**：GiST 索引是一个通用的索引框架，支持多种索引策略，如范围查询、邻近查询（kNN）、部分匹配等。它允许用户自定义索引操作符和索引策略。

4. GIN（Generalized Inverted Index）索引
- **适用场景**：GIN 索引适用于需要处理数组、JSONB、全文搜索等复杂数据类型的查询。
- **特点**：GIN 索引特别适合处理包含多个键值的数据类型，如数组和 JSONB。它支持高效的包含查询（@>）、存在查询（?）等操作。

5. SP-GiST（Space-Partitioned GiST）索引
- **适用场景**：SP-GiST 索引适用于需要空间分区策略的数据类型，如几何数据、范围类型、IP 地址等。
- **特点**：SP-GiST 索引通过空间分区来提高查询效率，适用于高维数据和复杂查询。

6. BRIN（Block Range Index）索引
- **适用场景**：BRIN 索引适用于大规模数据且数据具有物理上连续的特性，如时间序列数据、地理空间数据等。
- **特点**：BRIN 索引通过记录数据块的范围来提供索引功能，索引体积小，维护成本低，但查询性能不如 B-tree 索引。它适用于数据在磁盘上具有良好局部性的情况。

7. Partial 索引
- **适用场景**：Partial 索引适用于只对表中的部分行进行索引的场景。
- **特点**：Partial 索引通过在创建索引时使用 WHERE 子句来限制索引的范围，从而减少索引的大小，提高查询效率。

8. Expression 索引
- **适用场景**：Expression 索引适用于需要对表中的表达式进行索引的场景。
- **特点**：Expression 索引允许用户对列的表达式进行索引，而不是对单独的列进行索引。这在需要对计算结果进行快速查询时非常有用。

9. Covering 索引（INCLUDE 子句）
- **适用场景**：Covering 索引适用于查询只需要索引中的列而不需要访问表数据的场景。
- **特点**：通过在索引中包含额外的列（使用 INCLUDE 子句），可以减少查询时对表数据的访问，提高查询性能。

## 回表与索引覆盖

PostgreSQL 中索引依旧存在回表过程与索引覆盖特性，只是回表过程使用的是 TID 而非主键 ID 来指向完整数据。

是的，PostgreSQL 的回表过程是通过 **tuple identifier (TID)** 而不是主键 ID 来实现的。以下是对这一过程的详细解释：

1. TID 的定义和作用

**Tuple Identifier (TID)** 是 PostgreSQL 中用于唯一标识表中每一行的物理位置的标识符。TID 包含两个部分：

- **块号（Block Number）**：标识数据文件中的数据块（也称为页）。
- **元组索引（Tuple Index）**：标识块内具体元组的位置。

TID 直接指向表中行的物理位置，因此它是一个低级别的指针，用于快速定位和访问表中的数据行。

2. 回表过程的具体实现

当 PostgreSQL 使用索引进行查询时，索引扫描会返回满足条件的行的 TID 列表。然后，数据库使用这些 TID 来访问表中的实际数据行。这个过程就是回表（**table access by index rowid**），具体步骤如下：

1. **索引扫描**：
   - 查询使用索引来定位满足条件的行。
   - 索引扫描返回一组 TID，这些 TID 对应于满足条件的行。

2. **回表访问**：
   - 数据库使用返回的 TID 来访问表中的实际数据行。
   - 通过 TID，数据库可以直接定位到数据文件中的具体位置，而无需扫描整个表。

3. 与主键 ID 的区别

虽然主键 ID 也可以用于唯一标识表中的行，但在回表过程中，PostgreSQL 使用的是 TID 而不是主键 ID，原因如下：

- **物理定位**：
  - TID 直接指向行的物理位置，提供更快的访问速度。
  - 主键 ID 需要通过索引查找来定位行，可能涉及额外的查找步骤。

- **独立性**：
  - TID 是 PostgreSQL 内部用于物理存储的标识符，不依赖于表的逻辑结构（如主键）。
  - 主键 ID 是逻辑上的标识符，可能随着数据的变化而变化。

4. 性能考虑

使用 TID 进行回表访问具有以下性能优势：

- **直接访问**：
  - TID 提供了对数据行的直接物理访问，避免了通过主键或其他索引进行二次查找的开销。

- **减少 I/O**：
  - 由于 TID 直接指向数据行的位置，数据库可以更有效地进行磁盘 I/O 操作，减少不必要的读取。

5. 示例

假设有一个表 `employees`，包含列 `id`（主键）, `name`, `age`，并且在 `age` 列上创建了索引。

```sql
SELECT name, age FROM employees WHERE age > 30;
```

在这个查询中，PostgreSQL 会执行以下步骤：

1. **索引扫描**：
   - 使用 `age` 上的索引扫描来定位所有 `age > 30` 的行。
   - 返回一组 TID，对应于满足条件的行。

2. **回表访问**：
   - 使用返回的 TID 来访问 `employees` 表中的实际数据行。
   - 获取 `name` 和 `age` 的值。

具体使用可参考文章顶部的官方文档。

# 并发控制与锁

PostgreSQL 默认为读已提交隔离级别

## MVCC

PostgreSQL利用多版本并发控制(MVCC)来维护数据的一致性。 这就意味着当检索数据时，每个事务看到的都只是一小段时间之前的数据快照(一个数据库版本)， 而不是数据的当前状态。这样，通过对每个数据库会话提供事务隔离，就避免了一个事务看到由其它并发事务的更新相同数据行而导致的不一致的数据。MVCC通过避开传统数据库系统封锁的方法，最大限度地减少锁竞争以允许合理的多用户环境中的性能。

## 锁

以下是 PostgreSQL 中主要的锁类型及其用途：

1. **行级锁（Row-Level Locks）**
   - **FOR UPDATE**: 当一个事务执行 `SELECT ... FOR UPDATE` 时，它会锁定查询返回的行，防止其他事务对这些行进行修改或删除，直到当前事务结束。
   - **FOR NO KEY UPDATE**: 类似于 `FOR UPDATE`，但锁的强度较低，适用于不涉及外键的更新操作。
   - **FOR SHARE**: 锁定查询返回的行，但允许其他事务读取这些行。阻止其他事务对这些行进行修改或删除。
   - **FOR KEY SHARE**: 类似于 `FOR SHARE`，但锁的强度较低，允许其他事务对这些行进行非冲突的更新。

2. **表级锁（Table-Level Locks）**
   PostgreSQL 提供了多种表级锁，用于控制对整个表的并发访问：
   - **ACCESS SHARE**: 允许读取表数据，但不允许修改表结构或数据。
   - **ROW SHARE**: 允许并发读取和修改行，但不允许对表结构进行修改。
   - **ROW EXCLUSIVE**: 允许并发修改行，但不允许对表结构进行修改。
   - **SHARE UPDATE EXCLUSIVE**: 允许并发读取，但不允许对表结构进行修改或进行排他性操作。
   - **SHARE**: 允许并发读取，但不允许修改表数据或表结构。
   - **SHARE ROW EXCLUSIVE**: 类似于 `SHARE`，但不允许其他事务获取 `SHARE` 或 `SHARE ROW EXCLUSIVE` 锁。
   - **EXCLUSIVE**: 允许并发读取，但不允许任何形式的修改或排他性操作。
   - **ACCESS EXCLUSIVE**: 完全排他性锁，阻止任何其他事务对表进行任何操作，包括读取。

3. **页级锁（Page-Level Locks）**
   - PostgreSQL 在某些情况下会使用页级锁来控制对数据页的访问，例如在 VACUUM 操作期间。

4. **咨询锁（Advisory Locks）**
   - 咨询锁是应用程序级别的锁，PostgreSQL 不会自动管理这些锁。应用程序可以显式地请求和释放咨询锁，用于实现自定义的并发控制逻辑。

5. **死锁（Deadlocks）**
   - 当两个或多个事务互相持有对方所需的锁时，就会发生死锁。PostgreSQL 会自动检测死锁并回滚其中一个事务以打破死锁。

6. **锁的获取和释放**
   - PostgreSQL 使用两阶段锁协议来管理锁的获取和释放。锁在事务开始时获取，并在事务结束时释放。
   - 锁的获取是自动的，数据库会根据执行的 SQL 语句自动决定需要获取的锁类型。

7. **锁的监控**
   - PostgreSQL 提供了多种系统视图和函数来监控锁的使用情况，例如 `pg_locks`、`pg_stat_activity` 等。

示例
```sql
-- 获取行级锁
BEGIN;
SELECT * FROM employees WHERE id = 1 FOR UPDATE;
-- 其他事务无法修改或删除 id = 1 的行，直到当前事务结束
COMMIT;

-- 获取表级锁
BEGIN;
LOCK TABLE employees IN SHARE MODE;
-- 其他事务可以读取表数据，但无法修改表数据或表结构
COMMIT;
```

与PostgreSQL中其它锁一样， 可以在pg_locks系统视图中查看当前被会话持有的所有咨询锁。

https://www.rockdata.net/zh-cn/docs/9.3/view-pg-locks.html

咨询锁和普通锁存储在共享内存池中，其中大小由max_locks_per_transaction和 max_connections配置参数决定。 千万不要耗尽这些内存，否则服务器将不能再获取任何新锁。 因此服务器可以获得的咨询锁数量是有限的，根据服务器的配置不同， 这个限制可能是几万到几十万个。

## 锁和索引

尽管PostgreSQL提供对表数据访问的非阻塞的读/写， 但并非所有PostgreSQL里实现的索引访问模式都能够进行非阻塞读/写。

目前，B-tree 索引为并发应用提供了最好的性能。因为它还有比 Hash 索引更多的特性，在那些需要对标量数据进行索引的并发应用中，我们建议使用 B-tree 索引类型。在处理非标量类型数据的时候，B-tree 就没什么用了，应该使用 GiST, SP-GiST 或 GIN 索引。

# 执行计划

























# 数据存储结构

PostgreSQL 采用一种更为统一和模块化的架构，没有像 MySQL 那样明确的“存储引擎”概念。

1. **统一的存储引擎**：
   - PostgreSQL 使用的是一种称为 **Heap** 的存储引擎，所有数据都存储在堆表中。这种设计使得 PostgreSQL 在处理复杂查询和事务时具有一致的性能和功能。

2. **可插拔的存储接口**：
   - 虽然 PostgreSQL 没有像 MySQL 那样的多种存储引擎，但它提供了 **可插拔的存储接口**，允许开发者创建自定义的存储方案。例如，**cstore_fdw** 是一个用于列式存储的扩展，类似于列式存储引擎。
   - **zheap** 是一个正在开发中的项目，旨在提供一种新的存储格式，以减少更新操作带来的膨胀问题。

3. **表空间（Tablespaces）**：
   - PostgreSQL 支持表空间，允许用户将表和索引存储在不同的物理位置。这提供了类似于存储引擎的灵活性，但仍然基于统一的存储引擎架构。

4. **事务和并发控制**：
   - PostgreSQL 内置了对事务和并发控制的强大支持，类似于 MySQL 的 InnoDB 引擎。它支持多版本并发控制（MVCC），确保在高并发环境下数据的完整性和一致性。

5. **扩展性**：
   - PostgreSQL 提供了丰富的扩展和插件，可以增强其功能。例如，**pg_stat_statements** 用于性能监控，**hstore** 和 **jsonb** 用于存储半结构化数据。
























# 参考资料
1. [PostgreSQL 官方文档](https://www.postgresql.org/docs/)
2. [MySQL 官方文档](https://dev.mysql.com/doc/)



























