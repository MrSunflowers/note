# 元数据字段

Elasticsearch 中，每个文档除了你自己定义的字段（如 title, content），还有一些由系统自动生成和维护的字段，它们以 _开头，即元数据字段，例如 _id, _index, _source。

## 预聚合与 _doc_count

预聚合（Pre-aggregation）指的是在数据被存储到Elasticsearch之前，已经对原始数据进行了某种形式的汇总。例如，我们可能不会存储每一条销售记录，而是存储每天的总销售额。这样，每个文档不再是代表一个单独的销售记录，而是代表一天内所有销售记录的汇总。这种做法的好处是可以大大减少存储的数据量，并提高查询性能（因为只需要处理更少的文档）。但是，这也带来了一个问题：在计算文档数量时，如果我们想得到原始销售记录的数量（即每天的销售次数），那么直接计算文档数就会得到错误的结果（因为现在一个文档代表多次销售）。

为了解决这个问题，Elasticsearch引入了_doc_count字段。在预聚合的文档中，我们可以通过_doc_count字段来指定这个文档代表了多少个原始文档。这样，在进行聚合查询时，Elasticsearch就会使用_doc_count的值来计数，而不是简单地将文档数加1。

假设我们有一个电商平台，原始数据是每一笔订单。如果我们存储原始数据，那么每笔订单存一个文档，像这样：
文档1：
```json
{
"order_id": 1,
"date": "2024-01-01",
"amount": 100
}
```
文档2：
```json
{
"order_id": 2,
"date": "2024-01-01",
"amount": 200
}
```
如果我们按照日期进行聚合，那么2024-01-01这个日期的文档数量是2，总销售额是300。
但是，如果我们使用预聚合，我们可能只存储每天的总销售额，那么2024-01-01这一天只有一个文档：
预聚合文档：
```json
{
"date": "2024-01-01",
"total_amount": 300
}
```
这时，如果我们按照日期进行聚合，那么2024-01-01这个日期的文档数量是1，而实际上原始订单数量是2。这就会导致我们在计算订单数量时出错。
为了解决这个问题，我们在预聚合文档中加入_doc_count字段，用来表示这个文档代表了2个原始订单：
预聚合文档（带_doc_count）：
```json
{
"date": "2024-01-01",
"total_amount": 300,
"_doc_count": 2
}
```
这样，当我们按照日期进行聚合时，Elasticsearch会使用_doc_count字段的值来计算文档数量，所以2024-01-01这个日期的文档数量就是2，总销售额是300，这就和原始数据的结果一致了。

下面我们用具体的操作步骤和查询来演示。

创建索引并映射_doc_count字段（注意：_doc_count是一个元数据字段，不需要在映射中显式定义，但为了明确，我们可以说明一下）
插入带有_doc_count的文档
进行聚合查询
步骤：
步骤1：创建索引（可以不用显式映射_doc_count，因为它是元数据字段，但我们可以通过映射来禁止它，不过默认是启用的）
我们创建一个名为daily_sales的索引，并插入一个文档：
注意：在Elasticsearch 9.0+中，我们可以这样操作：
插入文档：
```json
POST /daily_sales/_doc/1
{
  "date": "2024-01-01",
  "total_amount": 300,
  "_doc_count": 2
}
```
步骤2：进行聚合查询
我们按照日期进行分桶，并计算每个日期的文档数量（即原始订单数量）和总销售额。
查询语句：
```json
GET /daily_sales/_search
{
  "size": 0,
  "aggs": {
    "sales_by_date": {
      "terms": {
        "field": "date"
      },
      "aggs": {
        "total_amount": {
          "sum": {
            "field": "total_amount"
          }
        }
      }
    }
  }
}
```
返回结果中，2024-01-01这个桶的文档计数（doc_count）应该是2，总销售额是300。
注意：在上面的例子中，我们使用了terms聚合，但是注意，我们的日期字段是"date"，并且我们只插入了一个文档，所以这个桶的doc_count应该是2（因为我们在文档中指定了_doc_count为2）。
实际返回的结果应该是这样的：
```json
{
  "aggregations": {
    "sales_by_date": {
      "buckets": [
        {
          "key": "2024-01-01",
          "doc_count": 2,
          "total_amount": {
            "value": 300
          }
        }
      ]
    }
  }
}
```
这样，我们就通过_doc_count字段正确地表示了原始订单数量。

使用注意事项与最佳实践
限制条件
- 必须是正整数：_doc_count不能是 0 或负数
- 单值字段：不能是数组，例如 "_doc_count": [3, 5]是非法的
- 索引行为：默认情况下 _doc_count不会被索引，因为它只用于聚合
- 更新限制：如果文档已存在，更新 _doc_count需要完整的文档替换

## exists 查询与 _field_names

_field_names 字段过去用于索引文档中每个包含非 null 值的字段的名称。exists查询曾经使用这个字段来查找那些“在特定字段上存在（或不存在）任何非 null 值”的文档。

对于一个文档 {"title": "Elastic", "tags": null, "body": "Search"}，早期版本会在这个文档的 _field_names字段中存储值 ["title", "body"]（因为tags是null，不包含在内）。

当执行 {"query": {"exists": {"field": "title"}}}时，查询引擎可以直接去 _field_names这个专用索引里查找包含 "title"的文档列表，速度非常快。这是为 exists查询专门建立的“反向索引”。

随着 Elasticsearch 发展，一些其他机制变得更强大、更通用，_field_names 的作用也被弱化，以下两种存储结构将替代 _field_names 的作用：

1. Doc Values： 一种列式存储结构，默认对所有支持聚合、排序的字段启用。它按字段存储所有文档的值，非常适合用来判断一个字段在某文档是否有值（只需查看该文档在该列是否有记录即可）。
2. Norms： 存储用于计算相关性分数（_score）的长度因子。如果一个字段有 norms，它必然会被索引，系统也可以通过其他方式判断其存在性。

这意味着，在现代 Elasticsearch 中，_field_names 只是一个后备机制，只为那些既不需要做聚合/排序（无doc_values），也不参与相关性评分（无norms）的、非常“轻量”的字段服务。

假设我们有一个用户档案索引，映射如下：
```json
PUT /user_profile
{
  "mappings": {
    "properties": {
      "user_id": {
        "type": "keyword"
        // 默认启用 doc_values， 禁用 norms
      },
      "name": {
        "type": "text"
        // 文本字段默认禁用 doc_values， 启用 norms
      },
      "bio": {
        "type": "text"
        // 同上
      },
      "last_login_ip": {
        "type": "ip",
        "doc_values": false,
        "norms": false
        // 明确禁用两者
      },
      "internal_notes": {
        "type": "keyword",
        "doc_values": false,
        "norms": false
        // 明确禁用两者
      }
    }
  }
}
```
插入一条文档：
```json
POST /user_profile/_doc/1
{
  "user_id": "U123",
  "name": "张三",
  "last_login_ip": "10.0.0.1",
  "internal_notes": "VIP客户"
  // 注意，没有 `bio` 字段
}
```
对于这个文档，_field_names字段会索引什么？
- user_id: 有关键字类型的 doc_values-> 不进入 _field_names。
- name: 有文本类型的 norms-> 不进入 _field_names。
- bio: 字段不存在 -> 肯定不进入 _field_names。
- last_login_ip: doc_values和 norms都禁用了 -> 进入​ _field_names。
- internal_notes: doc_values和 norms都禁用了 -> 进入​ _field_names。
所以，文档1的 _field_names值为：["last_login_ip", "internal_notes"]。

执行 exists 查询时会发生什么？
```json
GET /user_profile/_search
{
  "query": {
    "exists": {
      "field": "user_id"
    }
  }
}
```
对 user_id的查询： 因为 user_id有 doc_values，查询引擎会直接检查其 doc_values结构，发现文档1在该列有值“U123”，因此匹配。不走 _field_names。
```json
GET /user_profile/_search
{
  "query": {
    "exists": {
      "field": "bio"
    }
  }
}
```
对 bio的查询： 因为 bio字段在文档1中不存在，其映射中虽然有 norms，但该文档的 bio列是空的。查询引擎检查其相关结构后，判断为不存在。不走 _field_names。
```json
GET /user_profile/_search
{
  "query": {
    "exists": {
      "field": "last_login_ip"
    }
  }
}
```
对 last_login_ip的查询： 该字段两者都禁用。查询引擎在检查其自身结构无果后，会回退到查询 _field_names 索引，发现文档1的 _field_names包含 "last_login_ip"，因此匹配。

在新版本的 Elasticsearch 中，不再可以禁用 _field_names 字段。**它现在默认启用**，因为它不再像过去那样带来索引开销。

因为现在的 _field_names 开销极低（只索引极少数字段），默认开启的收益（为禁用doc_values/norms的字段提供exists查询支持）远大于其成本。保留一个“禁用”选项只会让配置复杂化，没有实际益处。

## _ignored

Elasticsearch 在尝试索引一个文档时，如果某个字段不符合预设的规则，它可以选择不将这个字段的内容加入到倒排索引中，这样这个字段就变得不可搜索。但为了帮助您诊断问题，Elasticsearch 会将这个字段的名字记录在 _ignored这个特殊的字段里。

这是一个数据诊断和治理工具。想象一下，您正在从外部系统接收海量数据，有些数据可能不规范。与其让整个文档因为一个坏字段而索引失败，不如忽略它并继续处理，同时通过 _ignored字段让您知道哪些字段出了问题，便于后续排查和修复。

我们通过几个具体的场景来理解 _ignored字段的用途。

场景一：字段格式错误 (Malformed Field)

背景：你有一个 products索引，其中 price字段被定义为 float类型。但某个上游系统偶尔会发送错误的数据，比如把价格写成了字符串 "twenty"。

映射与设置：
```json
PUT /products
{
  "mappings": {
    "properties": {
      "name": {"type": "text"},
      "price": {
        "type": "float",
        "ignore_malformed": true  // 关键设置：遇到类型错误时忽略此字段
      }
    }
  }
}
```
索引一个有问题的文档：
```json
POST /products/_doc/1
{
  "name": "T-Shirt",
  "price": "twenty"  // 这是一个字符串，无法转为浮点数
}
```
这个文档会被成功索引，但 price字段的值 "twenty"不会被加入到索引中，即你无法通过 price搜索到这个文档。

此时，Elasticsearch 会将字段名 price记录在这个文档的 _ignored字段中。

查询验证：
```json
GET /products/_search
{
  "query": {
    "term": {
      "_ignored": "price"  // 查找哪些文档的 `price` 字段被忽略了
    }
  }
}
```
这个查询会返回文档 _id: 1，因为它包含被忽略的 price字段。通过这个查询，你就能快速定位到有问题的数据记录。

场景二：Keyword 字段超长

背景：你有一个 logs索引，其中 error_code被定义为 keyword类型。keyword类型适合精确匹配，但为了性能，通常会给一个长度限制。

映射与设置：
```json
PUT /logs
{
  "mappings": {
    "properties": {
      "message": {"type": "text"},
      "error_code": {
        "type": "keyword",
        "ignore_above": 256  // 关键设置：超过256字符的keyword值将被忽略
      }
    }
  }
}
```
索引一个超长的关键字：
```json
POST /logs/_doc/1
{
  "message": "An error occurred",
  "error_code": "VERY_LONG_ERROR_CODE_THAT_EXCEEDS_THE_256_CHARACTER_LIMIT_SET_IN_IGNORE_ABOVE_FOR_THE_KEYWORD_FIELD_SO_THIS_PART_WILL_BE_IGNORED_AND_NOT_INDEXED_FOR_SEARCHING_ALTHOUGH_IT_IS_STILL_STORED_IN_SOURCE_IF_YOU_HAVE_ENABLED_SOURCE"
}
```
这个文档会被索引，但 error_code字段的超长值不会被加入到倒排索引中。

诊断：你可以通过聚合来分析哪些字段最常被忽略。
```json
GET /logs/_search
{
  "size": 0,  // 不关心具体文档，只要聚合结果
  "aggs": {
    "problematic_fields": {
      "terms": {
        "field": "_ignored",  // 对 _ignored 字段做聚合
        "size": 10
      }
    }
  }
}
```
返回结果会类似：
```json
{
  "aggregations" : {
    "problematic_fields" : {
      "buckets" : [
        {
          "key" : "error_code",  // 被忽略的字段名
          "doc_count" : 1        // 有1个文档的此字段被忽略
        }
      ]
    }
  }
}
```
这能帮助你发现 error_code字段的长度设置可能不合理，需要调整业务逻辑或 ignore_above的值。

场景三：文档字段数超限

背景：为了防止映射爆炸，Elasticsearch 默认限制一个文档不能超过 1000 个字段。但有时你会处理一些稀疏的、字段极多的数据（比如宽表）。

映射与设置：
```json
PUT /wide_table
{
  "settings": {
    "index.mapping.total_fields.limit": 5,  // 为了演示，将限制设为极小的5个
    "index.mapping.total_fields.ignore_dynamic_beyond_limit": true  // 关键设置：超过的字段将被忽略
  }
}
```
索引一个“宽”文档（包含6个字段，超过了5个的限制）：
```json
POST /wide_table/_doc/1
{
  "field1": "A",
  "field2": "B",
  "field3": "C",
  "field4": "D",
  "field5": "E",
  "field6": "F"  // 这个字段是第6个，将被忽略
}
```
文档被成功索引，但 field6被忽略。

诊断：使用 exists查询找到所有存在“被忽略字段”的文档。
```json
GET /wide_table/_search
{
  "query": {
    "exists": {
      "field": "_ignored"  // 查找所有有字段被忽略的文档
    }
  }
}
```
这个查询能帮你快速筛选出哪些文档的字段结构“超标”了，以便进行数据清洗或调整索引的字段数量限制。

## _id

每个文档都有一个唯一标识它的 _id。这个字段会被索引，以便可以通过 GET API 或 ids 查询来查找文档。_id可以在索引文档时手动指定，也可以由 Elasticsearch 自动生成一个唯一的 ID。此字段在映射中不可配置。

_id是一个系统级的元数据字段，其行为和存储方式由 Elasticsearch 核心引擎严格控制，以保证其唯一性和检索性能的绝对可靠。不允许用户修改它的数据类型、分析器（它默认是不分词的）等属性。这是设计上的约束，不是功能缺失。

_id 字段不能用于聚合、排序和脚本操作。如果确实需要对 _id 字段进行排序或聚合，建议将 _id字段的内容复制到另一个启用了 doc_values​ 的字段中。

_id 的大小被限制在 512 字节以内，更大的值将被拒绝。

_id 是文档在 Elasticsearch 中的“身份证号”或“主键”。它的核心作用是唯一标识一个文档。想象一下，你有一个用户表，每个用户都需要一个唯一的用户ID，_id就是 Elasticsearch 文档世界里的这个 ID。

## 跨索引查询与 _index

_index 是 Elasticsearch 文档的元数据字段，用于标识文档所属的索引名称。当进行跨多索引查询时，可以通过此字段精准筛选特定索引的文档。

跨索引综合查询示例

```json
GET index_1,index_2/_search
{
  "query": {
    "terms": {
      "_index": ["index_1", "index_2"]  // 只查询这两个索引
    }
  },
  "aggs": {
    "indices": {
      "terms": {
        "field": "_index",  // 按索引名称聚合
        "size": 10
      }
    }
  },
  "sort": [
    {
      "_index": {
        "order": "asc"  // 按索引名升序排序
      }
    }
  ],
  "script_fields": {
    "index_name": {
      "script": {
        "lang": "painless",
        "source": "doc['_index']"  // 在脚本中访问索引名
      }
    }
  }
}
```

四大应用场景详解

1. 索引条件查询

```json
// 示例1：精确匹配单个索引
GET */_search
{
  "query": {
    "term": {
      "_index": "logs-2023-10-01"  // 只查询特定日期日志索引
    }
  }
}

// 示例2：匹配多个索引
GET */_search
{
  "query": {
    "terms": {
      "_index": ["index_a", "index_b", "index_c"]
    }
  }
}

// 示例3：通配符匹配
GET */_search
{
  "query": {
    "wildcard": {
      "_index": "logs-2023-*"  // 匹配2023年所有日志索引
    }
  }
}
```
应用场景：

日志分析：_index: "nginx-logs-2023-10-*"查询10月份所有日志
多租户系统：_index: "tenant_*_products"查询所有租户的商品索引

2. 按索引聚合统计

```json
GET index_1,index_2,index_3/_search
{
  "size": 0,  // 不返回具体文档
  "aggs": {
    "per_index_stats": {
      "terms": {
        "field": "_index",
        "size": 100
      },
      "aggs": {
        "avg_price": {
          "avg": {"field": "price"}
        }
      }
    }
  }
}
```
返回结果示例：

```json
{
  "aggregations": {
    "per_index_stats": {
      "buckets": [
        {
          "key": "index_1",        // 索引名
          "doc_count": 1500,        // 文档数量
          "avg_price": {"value": 299.99}
        },
        {
          "key": "index_2",
          "doc_count": 2300,
          "avg_price": {"value": 450.50}
        }
      ]
    }
  }
}
```

典型用例：

统计各索引的文档数量分布
对比不同业务模块的数据量
监控各分片索引的数据均衡性

3. 跨索引排序

```json
GET logs_prod,logs_test/_search
{
  "sort": [
    {
      "_index": {  // 先按索引名排序
        "order": "desc"
      }
    },
    {
      "@timestamp": {  // 再按时间排序
        "order": "asc"
      }
    }
  ]
}
```
排序规则：

索引名按字典序排列
常用于：prod环境日志优先显示

4. 脚本中动态访问

```json
GET */_search
{
  "script_fields": {
    "full_identifier": {
      "script": """
        // 组合索引名和文档ID
        return doc['_index'].value + '#' + doc['_id'].value
      """
    },
    "env_type": {
      "script": """
        // 根据索引名判断环境类型
        String index = doc['_index'].value;
        if (index.contains('_prod_')) {
          return 'production';
        } else if (index.contains('_test_')) {
          return 'testing';
        } else {
          return 'development';
        }
      """
    }
  }
}
```

重要技术特性

1. 虚拟字段特性

```json
// 以下查询可行
{
  "query": {
    "term": { "_index": "logs" }           // ✅ 支持
  }
}

{
  "query": {
    "match": { "_index": "logs" }          // ✅ 支持（转为term查询）
  }
}

{
  "query": {
    "wildcard": { "_index": "log*" }       // ✅ 支持
  }
}

// 以下查询不可用
{
  "query": {
    "regexp": { "_index": "log.*" }        // ❌ 不支持正则
  }
}

{
  "query": {
    "fuzzy": { "_index": "logs" }          // ❌ 不支持模糊查询
  }
}
```

底层原理：
- _index是虚拟字段，不会实际写入 Lucene 倒排索引
- 查询时 Elasticsearch 在查询规划阶段处理索引过滤
- 因此只支持精确匹配、通配符等简单查询

2. 支持索引别名

```json
// 创建别名
PUT /real_index/_alias/my_alias

// 通过别名查询（实际查询 real_index）
GET */_search
{
  "query": {
    "term": {
      "_index": "my_alias"  // ✅ 支持别名
    }
  }
}
```

3. 跨集群查询支持

```json
// 查询远程集群数据
GET */_search
{
  "query": {
    "term": {
      "_index": "cluster1:logs_2023"  // 远程集群索引
    }
  }
}

// 通配符查询远程集群
GET */_search
{
  "query": {
    "wildcard": {
      "_index": "cluster_*:index_3"  // 匹配所有集群的 index_3
    }
  }
}
```

特别注意：

```json
// ❌ 错误写法：缺失分隔符
{
  "query": {
    "wildcard": {
      "_index": "cluster*index_1"  // 只匹配本地索引！
    }
  }
}

// ✅ 正确写法：包含分隔符
{
  "query": {
    "wildcard": {
      "_index": "cluster_*:index_1"  // 正确匹配远程索引
    }
  }
}
```

实战应用案例

案例1：多环境日志检索系统

```json
// 同时查询开发、测试、生产环境日志
GET logs_dev_*,logs_test_*,logs_prod_*/_search
{
  "query": {
    "bool": {
      "must": [
        { "match": { "message": "error" } }
      ],
      "filter": [
        {
          "terms": {
            "_index": [  // 限定只查生产和测试环境
              "logs_prod_2023-10-01",
              "logs_test_2023-10-01"
            ]
          }
        }
      ]
    }
  },
  "sort": [
    {
      "_index": {  // 生产环境日志优先
        "order": "desc"
      }
    }
  ]
}
```

案例2：索引生命周期监控

```json
// 监控各索引健康状态
GET _all/_search?size=0
{
  "aggs": {
    "index_distribution": {
      "terms": {
        "field": "_index",
        "size": 1000
      },
      "aggs": {
        "doc_count": { "value_count": { "field": "_id" } },
        "avg_size": { "avg": { "script": "params._source.toString().length()" } }
      }
    }
  }
}
```















```json

```




























