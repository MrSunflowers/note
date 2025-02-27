PostgreSQL 是一个功能强大的开源关系型数据库管理系统，以其稳定性、可靠性和高性能而闻名。

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

PostgreSQL 的序列是持久化的，序列的当前值会保存在系统表中，如 pg_class 和 pg_sequence。即使数据库重启，序列值也会保持不变。这些值是持久化的，并且在数据库重启后会被正确读取和使用

**Oracle**

Oracle 的序列也是持久化的，序列的当前值保存在数据字典中，数据库重启后，序列值会从磁盘中恢复。Oracle 通过控制文件和序列文件来跟踪序列的当前值。即使在缓存丢失的情况下，Oracle 也会从控制文件中获取最新的序列值，并继续递增，从而避免重复发号。

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











