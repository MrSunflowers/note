[TOC]
# Varnish 简介

Varnish 是一款高性能的开源**反向代理服务器**和 **HTTP 缓存加速服务器**。它主要用于加速 Web 应用程序的性能，通过缓存静态或缓慢变化的内容，减轻后端服务器的负担，从而提高 Web 应用程序的性能和吞吐量。现实中更多用作搭建 CDN 服务器。

官网 https://varnish-cache.org/

https://blog.csdn.net/weixin_47019016/article/details/107707871

技术选型

https://www.cnblogs.com/kevingrace/p/6188123.html

当然其反向代理能力不如 nginx，nginx 的缓存管理能力不如 Varnish，在架构方案中，使用此两者取长补短。Varnish 主要用于缓存静态内容，提高 Web 服务器的响应速度。它专注于减少后端服务器的负载，通过缓存响应内容来加速请求处理。

**主要特点**

- **高性能**：Varnish 被设计成高性能的反向代理缓存服务器，能够处理数千个并发连接。
- **可扩展性**：支持多个缓存服务器集群化，可以通过增加缓存服务器来扩展性能和容量。
- **灵活性**：支持高度自定义的配置选项和 VCL（Varnish Configuration Language）语言扩展，使得可以根据特定的应用程序需求进行调整和优化。
- **可靠性**：提供了多种故障处理和恢复机制，包括健康检查、动态后端服务器池和热备份等。

**工作原理**

Varnish 的工作原理是通过在前端和后端之间建立一个缓存层，将经过代理服务器的请求缓存下来。当同样的请求再次到达服务器时，就直接从缓存中取出数据返回给客户端，而无需再次访问源服务器。

**对比 Squid**

与传统的 Squid 相比，Varnish 具有以下优势：

- **更高的稳定性**：Varnish 的稳定性更高，Squid 服务器发生故障的几率要高于 Varnish。
- **更快的访问速度**：Varnish 采用“Visual Page Cache”技术，所有缓存数据都直接从内存读取，而 Squid 是从硬盘读取，因而 Varnish 在访问速度方面会更快。
- **更多的并发连接**：Varnish 可以支持更多的并发连接，因为 Varnish 的 TCP 连接释放要比 Squid 快。
- **批量清除缓存**：Varnish 可以通过管理端口，使用正则表达式批量的清除部分缓存，而 Squid 是做不到的。

**适用场景**

Varnish 适用于需要高性能和高并发处理的 Web 应用场景，如大型门户网站、电子商务平台、内容管理系统等。它可以作为 CDN 缓存服务器的可选服务之一，帮助企业提高网站的响应速度和用户体验。

# 配置文件与 VCL

VCL（Varnish Configuration Language）是 Varnish 的配置语言，语法简单，功能强大，类似于 C 和 Perl。主要用于配置如何处理请求和内容的缓存策略。

VCL 在执行时会转换成二进制代码。

VCL 文件被分为多个子程序（即配置文件中 sub 修饰的配置代码块），不同的子程序在不同的时间里执行，比如一个子程序在接到请求时执行，另一个子程序在接收到后端服务器传送的文件时执行。

## 配置文件示例

以下是一个完整的 VCL（Varnish Configuration Language）配置文件示例：

```vcl
vcl 4.0;

# 导入默认的 VCL 规则
import std;

# 定义后端服务器
backend default {
    .host = "127.0.0.1";
    .port = "8080";
}

# 处理接收到的请求
sub vcl_recv {
    # 如果请求的方法不是 GET 或 HEAD，则不缓存
    if (req.method != "GET" && req.method != "HEAD") {
        return (pass);
    }

    # 如果请求包含认证信息，则不缓存
    if (req.http.Authorization || req.http.Cookie) {
        return (pass);
    }

    # 可以添加更多自定义规则，例如根据 URL 路径决定是否缓存
    if (req.url ~ "^/admin/") {
        return (pass);
    }

    # 可以添加更多自定义规则，例如根据主机名决定是否缓存
    if (req.http.host ~ "^(www\.)?example\.com$") {
        # 可以添加更多自定义规则
    }
}

# 处理从后端服务器接收到的响应
sub vcl_backend_response {
    # 设置缓存的 TTL（Time-To-Live）
    set beresp.ttl = 1h;

    # 如果响应状态不是 200（成功），则不缓存
    if (beresp.status != 200) {
        set beresp.uncacheable = true;
        set beresp.ttl = 120s;
        return (deliver);
    }

    # 可以添加更多自定义规则，例如根据响应头决定是否缓存
    if (beresp.http.Cache-Control ~ "no-cache" || beresp.http.Pragma ~ "no-cache") {
        set beresp.uncacheable = true;
        set beresp.ttl = 120s;
        return (deliver);
    }
}

# 处理从缓存中检索到的响应
sub vcl_deliver {
    # 可以添加自定义头部信息
    if (obj.hits > 0) {
        set resp.http.X-Cache = "HIT";
    } else {
        set resp.http.X-Cache = "MISS";
    }

    # 删除不必要的头部信息
    unset resp.http.Server;
    unset resp.http.X-Varnish;
}

# 处理请求完成后的清理工作
sub vcl_fini {
    # 可以添加自定义清理逻辑
    return (ok);
}
```

**配置文件说明**

1. **VCL 版本声明**：
   ```vcl
   vcl 4.0;
   ```
   指定使用的 VCL 版本。

2. **导入标准库**：
   ```vcl
   import std;
   ```
   导入 Varnish 标准库，提供额外的功能。

3. **定义后端服务器**：
   ```vcl
   backend default {
       .host = "127.0.0.1";
       .port = "8080";
   }
   ```
   定义默认的后端服务器地址和端口。

4. **`vcl_recv` 子程序**：
   ```vcl
   sub vcl_recv {
       if (req.method != "GET" && req.method != "HEAD") {
           return (pass);
       }

       if (req.http.Authorization || req.http.Cookie) {
           return (pass);
       }

       if (req.url ~ "^/admin/") {
           return (pass);
       }

       if (req.http.host ~ "^(www\.)?example\.com$") {
           # 自定义规则
       }
   }
   ```
   处理接收到的请求，决定是否缓存。例如，非 GET/HEAD 请求、有认证信息或特定 URL 路径的请求不缓存。

5. **`vcl_backend_response` 子程序**：
   ```vcl
   sub vcl_backend_response {
       set beresp.ttl = 1h;

       if (beresp.status != 200) {
           set beresp.uncacheable = true;
           set beresp.ttl = 120s;
           return (deliver);
       }

       if (beresp.http.Cache-Control ~ "no-cache" || beresp.http.Pragma ~ "no-cache") {
           set beresp.uncacheable = true;
           set beresp.ttl = 120s;
           return (deliver);
       }
   }
   ```
   处理从后端服务器接收到的响应，设置缓存 TTL，处理特定状态码和不缓存的响应头。

6. **`vcl_deliver` 子程序**：
   ```vcl
   sub vcl_deliver {
       if (obj.hits > 0) {
           set resp.http.X-Cache = "HIT";
       } else {
           set resp.http.X-Cache = "MISS";
       }

       unset resp.http.Server;
       unset resp.http.X-Varnish;
   }
   ```
   处理发送给客户端的响应，添加自定义头部信息并删除不必要的头部信息。

7. **`vcl_fini` 子程序**：
   ```vcl
   sub vcl_fini {
       return (ok);
   }
   ```
   处理请求完成后的清理工作。

## VCL（Varnish Configuration Language）详解

### VCL 的执行机制

1. **编译与加载**：
   - VCL 文件由 Varnish 加载并编译成二进制代码。这一过程在 Varnish 启动或 VCL 文件修改后自动完成。

2. **子程序的执行顺序**：
   - VCL 文件包含多个子程序，每个子程序在不同的生命周期阶段执行。以下是常见的子程序及其执行顺序：

     | 子程序名称      | 执行时机                           | 主要用途                           |
     | --------------- | ---------------------------------- | ---------------------------------- |
     | `vcl_recv`      | 接收到客户端请求时                 | 决定是否缓存请求、修改请求头等     |
     | `vcl_hash`      | 计算缓存键时                       | 定义缓存键的生成方式               |
     | `vcl_pass`      | 请求需要绕过缓存时                 | 处理需要传递到后端的请求           |
     | `vcl_purge`     | 收到清除缓存的请求时               | 处理缓存清除逻辑                   |
     | `vcl_backend_fetch` | 向后端服务器发起请求时           | 修改向后端服务器发送的请求         |
     | `vcl_backend_response` | 接收到后端服务器的响应时        | 处理后端响应，决定是否缓存等       |
     | `vcl_deliver`   | 向客户端发送响应时                 | 修改发送给客户端的响应头           |
     | `vcl_fini`      | VCL 加载完成后                     | 执行清理操作                       |

3. **子程序的调用流程**：

   当 Varnish 接收到一个请求时，执行流程大致如下：

   1. **vcl_recv**：处理客户端请求，决定是否缓存、修改请求头等。
   2. **vcl_hash**：计算缓存键，用于后续缓存查找。
   3. **vcl_pass**（如果需要绕过缓存）：处理需要传递到后端的请求。
   4. **vcl_backend_fetch**：向后端服务器发起请求。
   5. **vcl_backend_response**：处理后端响应，决定是否缓存等。Varnish 3 及更早版本为 vcl_fetch 这个名称
   6. **vcl_deliver**：向客户端发送响应。

   如果在任意阶段调用了 `return` 语句，VCL 会跳转到相应的子程序或终止执行。

### VCL 语法结构

1. **子程序定义**：

   ```vcl
   sub <子程序名称> {
       // VCL 代码
   }
   ```

   **示例：**

   ```vcl
   sub vcl_recv {
       // 处理客户端请求
   }

   sub vcl_backend_response {
       // 处理后端响应
   }
   ```

2. **变量与操作**：

   VCL 提供了丰富的内置变量和操作符，用于访问和修改请求、响应和缓存相关的数据。

   - **内置变量**：

     - `req.*`：客户端请求相关变量，如 `req.url`, `req.http.User-Agent` 等。
     - `beresp.*`：后端响应相关变量，如 `beresp.status`, `beresp.http.Content-Type` 等。
     - `obj.*`：缓存对象相关变量。
     - `now`：当前时间。

   - **操作符**：

     - 赋值：`set`
     - 条件判断：`if`, `else`
     - 函数调用：`call <函数名>`

   **示例：**

   ```vcl
   sub vcl_recv {
       if (req.http.User-Agent ~ "Mobile") {
           set req.http.X-Device = "Mobile";
       } else {
           set req.http.X-Device = "Desktop";
       }
   }
   ```

3. **函数**：

   VCL 支持调用内置函数和自定义函数，用于实现复杂的逻辑。

   - **内置函数**：

     - `return (<状态|hash|purge|pass|restart>)`：控制 VCL 执行流程。
     - `synthetic("<字符串>")`：生成自定义响应内容。
     - `hash_data(<字符串>)`：添加数据到缓存键中。

   **示例：**

   ```vcl
   sub vcl_recv {
       if (req.url ~ "^/admin") {
           return (pass);
       }
   }
   ```

4. **注释**：

   - 单行注释：`//`
   - 多行注释：`/* */`

   **示例：**

   ```vcl
   /*
      这是一个多行注释
   */
   sub vcl_recv {
       // 单行注释
       if (req.url ~ "^/admin") {
           return (pass); // 绕过缓存
       }
   }
   ```

### 常见子程序及配置示例

#### 1. vcl_recv

**用途：** 处理客户端请求，决定是否缓存、修改请求头等。

**示例：**

```vcl
sub vcl_recv {
    // 移除私有 Cookie
    unset req.http.Cookie;

    // 移除跟踪参数
    if (req.url ~ "\?") {
        set req.url = regsub(req.url, "\?.*", "");
    }

    // 强制缓存特定路径
    if (req.url ~ "^/images/") {
        return (hash);
    }

    // 绕过缓存特定路径
    if (req.url ~ "^/admin/") {
        return (pass);
    }
}
```

#### 2. vcl_hash

**用途：** 计算缓存键，用于后续缓存查找。

**示例：**

```vcl
sub vcl_hash {
    // 添加 Host 头到缓存键
    hash_data(req.http.Host);

    // 添加请求 URL 到缓存键
    hash_data(req.url);

    // 如果有 Cookie，则添加 Cookie 到缓存键
    if (req.http.Cookie) {
        hash_data(req.http.Cookie);
    }
}
```

#### 3. vcl_backend_response

**用途：** 处理后端响应，决定是否缓存等。

**示例：**

```vcl
sub vcl_backend_response {
    // 设置缓存时间
    set beresp.ttl = 1h;

    // 移除 Set-Cookie 头
    unset beresp.http.Set-Cookie;

    // 缓存特定状态码
    if (beresp.status == 200 || beresp.status == 302) {
        return (deliver);
    } else {
        return (pass);
    }
}
```

#### 4. vcl_deliver

**用途：** 向客户端发送响应。

**示例：**

```vcl
sub vcl_deliver {
    // 添加自定义响应头
    set resp.http.X-Cache = "HIT";
    if (obj.ttl <= 0s) {
        set resp.http.X-Cache = "MISS";
    }

    // 移除 Varnish 添加的 headers
    unset resp.http.Via;
    unset resp.http.X-Varnish;
}
```

### VCL 调试与日志

VCL 支持在子程序中插入调试语句，帮助开发者了解请求处理流程和变量状态。

**示例：**

```vcl
sub vcl_recv {
    // 打印请求 URL
    std.log("URL: " + req.url);

    // 打印请求方法
    std.log("Method: " + req.method);

    // 打印 User-Agent
    std.log("User-Agent: " + req.http.User-Agent);
}
```

## 基本语法介绍

1. **用花括号做界定符，使用分号表示声明结束。注释用 //, #, /* */**：
   - 花括号 `{}` 用于界定代码块。
   - 分号 `;` 用于结束语句。
   - 注释可以使用 `//`（单行注释）、`#`（单行注释）或 `/* */`（多行注释）。

2. **赋值(=), 比较(==), 和一些布尔值(!, &&, ||), !(取反)等类似C语法**：
   - 赋值使用 `=`。
   - 比较使用 `==`。
   - 布尔操作符包括 `!`（取反）、`&&`（逻辑与）、`||`（逻辑或）。

3. **支持正则表达式, ACL匹配使用 ~ 操作**：
   - 正则表达式和 ACL（访问控制列表）匹配使用 `~` 操作符。

4. **不同于C的地方，反斜杠(\)在VCL中没有特殊的含义。只是用来匹配URLs**：
   - 在 VCL 中，反斜杠 `\` 没有特殊含义，仅用于匹配 URLs。

5. **VCL没有用户定义的变量，只能给backend, request, document这些对象的变量赋值。大部分是手工输入的，而且给这些变量分配值的时候，必须有一个VCL兼容的单位**：
   - VCL 不支持用户定义的变量，变量的赋值需要针对特定对象（如 backend, request, document）。

6. **VCL有if, 但是没有循环**：
   - VCL 支持 `if` 条件语句，但不支持循环结构。

7. **可以使用set来给request的header添加值, unset, 或 remove 来删除某个header**：
   - 使用 `set` 可以给请求的 header 添加值。
   - 使用 `unset` 或 `remove` 可以删除某个 header。

### 1. **代码块与语句结束**

- **代码块界定符**：使用花括号 `{}` 来界定代码块，例如子程序的定义。
  
  ```vcl
  sub vcl_recv {
      // 子程序代码
  }
  ```

- **语句结束符**：使用分号 `;` 来表示语句的结束。

  ```vcl
  set req.http.X-Custom-Header = "CustomValue";
  ```

### 2. **注释**

- **单行注释**：使用 `//` 或 `#` 进行单行注释。
  
  ```vcl
  // 这是一个单行注释
  # 这也是单行注释
  ```

- **多行注释**：使用 `/* */` 进行多行注释。
  
  ```vcl
  /*
     这是一个多行注释
     可以跨多行
  */
  ```

### 3. **变量与赋值**

- **内置变量**：VCL 提供了丰富的内置变量，用于访问和修改请求、响应和缓存相关的数据。例如：
  
  - `req.*`：客户端请求相关变量，如 `req.url`, `req.http.User-Agent` 等。
  - `beresp.*`：后端响应相关变量，如 `beresp.status`, `beresp.http.Content-Type` 等。
  - `obj.*`：缓存对象相关变量。
  - `now`：当前时间。

- **赋值操作**：使用 `set` 关键字进行赋值。
  
  ```vcl
  set req.http.X-Custom-Header = "CustomValue";
  set beresp.ttl = 1h;
  ```

- **删除变量**：使用 `unset` 或 `remove` 关键字删除变量。
  
  ```vcl
  unset req.http.Cookie;
  remove resp.http.Server;
  ```

### 4. **条件语句**

- **if 语句**：VCL 支持 `if` 条件语句，用于根据条件执行不同的代码块。
  
  ```vcl
  if (req.method == "GET" || req.method == "HEAD") {
      // 处理 GET 或 HEAD 请求
  } else {
      // 处理其他类型的请求
  }
  ```

- **逻辑操作符**：支持 `==`（等于）、`!=`（不等于）、`&&`（逻辑与）、`||`（逻辑或）、`!`（逻辑非）等。
  
  ```vcl
  if (req.http.User-Agent ~ "Mobile" && req.http.Cookie !~ "session") {
      // 处理移动设备且没有会话 Cookie 的请求
  }
  ```

### 5. **正则表达式与 ACL 匹配**

- **正则表达式匹配**：使用 `~` 操作符进行正则表达式匹配，`!~` 表示不匹配。
  
  ```vcl
  if (req.url ~ "^/admin/") {
      // 处理以 /admin/ 开头的 URL
  }

  if (req.http.User-Agent !~ "MSIE") {
      // 处理非 IE 浏览器的请求
  }
  ```

- **ACL 匹配**：使用 `acl` 关键字定义访问控制列表，并使用 `~` 操作符进行匹配。
  
  ```vcl
  acl localnet {
      "127.0.0.1"/8;
      "192.168.0.0"/16;
  }

  if (client.ip ~ localnet) {
      // 处理本地网络请求
  }
  ```

### 6. **函数调用**

- **内置函数**：VCL 提供了许多内置函数，用于实现复杂的逻辑。例如：
  
  - `return (<action>)`：控制 VCL 执行流程，如 `return (pass)`, `return (hash)`, `return (deliver)` 等。
  - `synthetic("<字符串>")`：生成自定义响应内容。
  - `hash_data(<字符串>)`：添加数据到缓存键中。
  - `std.log("<字符串>")`：记录日志信息。

  ```vcl
  sub vcl_recv {
      if (req.url ~ "^/admin") {
          return (pass);
      }
      std.log("Request URL: " + req.url);
  }

  sub vcl_deliver {
      synthetic("Hello, World!");
  }
  ```

### 7. **循环结构**

- **不支持循环**：VCL 不支持循环结构（如 `for`, `while`），只能使用条件语句和函数调用来处理逻辑。

### 8. **变量作用域**

- **子程序内变量**：变量在子程序内有效，子程序之间不共享变量。
  
  ```vcl
  sub vcl_recv {
      set req.http.X-Custom-Header = "CustomValue";
  }

  sub vcl_deliver {
      // 无法访问 req.http.X-Custom-Header
  }
  ```

### 9. **子程序定义**

- **子程序名称**：常见的子程序名称包括 `vcl_recv`, `vcl_hash`, `vcl_pass`, `vcl_purge`, `vcl_backend_fetch`, `vcl_backend_response`, `vcl_deliver`, `vcl_fini` 等。

  ```vcl
  sub vcl_recv {
      // 处理接收到的请求
  }

  sub vcl_backend_response {
      // 处理从后端服务器接收到的响应
  }
  ```

### 10. **导入其他 VCL 文件**

- **import 语句**：使用 `import` 关键字导入其他 VCL 文件或标准库。
  
  ```vcl
  import std;

  sub vcl_recv {
      std.log("Request received");
  }
  ```

### 11. **示例：完整的 VCL 配置**

以下是一个完整的 VCL 配置示例，展示了上述语法和特性的应用：

```vcl
vcl 4.0;

import std;

backend default {
    .host = "127.0.0.1";
    .port = "8080";
}

acl purge {
    "localhost";
    "192.168.1.0"/24;
}

sub vcl_recv {
    // 移除私有 Cookie
    unset req.http.Cookie;

    // 强制缓存特定路径
    if (req.url ~ "^/images/") {
        return (hash);
    }

    // 绕过缓存特定路径
    if (req.url ~ "^/admin/") {
        return (pass);
    }

    // 处理清除缓存的请求
    if (req.method == "PURGE") {
        if (client.ip ~ purge) {
            return (purge);
        } else {
            return (synth(403, "Forbidden"));
        }
    }
}

sub vcl_hash {
    hash_data(req.url);
    if (req.http.host) {
        hash_data(req.http.host);
    } else {
        hash_data("localhost");
    }
    return (hash);
}

sub vcl_backend_response {
    // 设置缓存时间
    set beresp.ttl = 1h;

    // 移除 Set-Cookie 头
    unset beresp.http.Set-Cookie;
}

sub vcl_deliver {
    // 添加自定义响应头
    set resp.http.X-Cache = "HIT";
    if (obj.hits == 0) {
        set resp.http.X-Cache = "MISS";
    }

    // 删除不必要的头部信息
    unset resp.http.Server;
    unset resp.http.X-Varnish;
}

sub vcl_purge {
    return (synth(200, "Purged"));
}

sub vcl_fini {
    return (ok);
}
```

## Backend

在 VCL（Varnish Configuration Language）中，`backend` 是用于定义 Varnish 如何与后端服务器通信的关键部分。`backend` 定义了 Varnish 从哪里获取内容，以及如何处理与这些服务器的连接。

### Backend 定义语法

定义一个 backend 的基本语法如下：

```vcl
backend <backend_name> {
    .host = "<hostname_or_IP>";
    .port = "<port_number>";
    # 其他可选参数
}
```

- `<backend_name>`：backend 的名称，用于在 VCL 中引用。
- `<hostname_or_IP>`：后端服务器的主机名或 IP 地址。
- `<port_number>`：后端服务器监听的端口号。

### Backend 参数

除了必需的主机名和端口号外，backend 还支持多个可选参数，用于精细控制与后端服务器的通信：

1. **.connect_timeout**：与后端服务器连接超时时间（秒）。
   ```vcl
   .connect_timeout = 1s;
   ```

2. **.first_byte_timeout**：接收后端服务器传输第一个字节的超时时间（秒）。
   ```vcl
   .first_byte_timeout = 5s;
   ```

3. **.between_bytes_timeout**：接收两个连续字节之间的超时时间（秒）。
   ```vcl
   .between_bytes_timeout = 2s;
   ```

4. **.max_connections**：varnish 与后端服务器的最大并发连接数。
   ```vcl
   .max_connections = 200;
   ```

5. **.proxy_header**：指定使用的代理头部版本（1 或 2）。
   ```vcl
   .proxy_header = 2;
   ```

6. **.probe**：健康检查配置，用于检测后端服务器的健康状态。
   ```vcl
   .probe = {
       .url = "/healthcheck";
       .timeout = 1s;
       .interval = 5s;
       .window = 5;
       .threshold = 3;
   }
   ```

### Backend 示例

以下是一个完整的 backend 定义示例：

```vcl
backend default {
    .host = "127.0.0.1";
    .port = "8080";
    .connect_timeout = 1s;
    .first_byte_timeout = 5s;
    .between_bytes_timeout = 2s;
    .max_connections = 200;
    .proxy_header = 2;
    .probe = {
        .url = "/healthcheck";
        .timeout = 1s;
        .interval = 5s;
        .window = 5;
        .threshold = 3;
    }
}
```

### Backend 健康检查

健康检查（probe）是 backend 定义中的一个重要部分，用于定期检查后端服务器的健康状态。默认会自动探测，配置了就用我们自己配置的，配置示例如下：

```vcl
.probe = {
    .url = "/healthcheck";
    .timeout = 1s;
    .interval = 5s;
    .window = 5;
    .threshold = 3; # 至少3次成功的健康检查
}
```

- **.url**：健康检查请求的 URL。
- **.timeout**：健康检查请求的超时时间。
- **.interval**：健康检查请求的间隔时间。
- **.window**：健康检查窗口大小，即最近的检查次数。
- **.threshold**：健康检查阈值，即最近检查中成功的次数。

也可以将 probe 单独拿出来独立定义

```vcl
probe my_probe {
    .url = "/health";
    .timeout = 1s;
    .interval = 5s;
    .window = 5;
    .threshold = 3;
}

backend server1 {
    .host = "192.168.1.10";
    .port = "8080";
    .probe = my_probe;
}

backend server2 {
    .host = "192.168.1.11";
    .port = "8080";
    .probe = my_probe;
}

director my_director round_robin {
    {
        .backend = server1;
    }
    {
        .backend = server2;
    }
}

sub vcl_init {
    new my_director_instance = directors.round_robin();
    my_director_instance.add_backend(server1);
    my_director_instance.add_backend(server2);
}

sub vcl_recv {
    set req.backend_hint = my_director_instance.backend();
}
```

### 多 Backend 配置

你可以在 VCL 中定义多个 backend，并在需要时进行切换：

```vcl
backend server1 {
    .host = "192.168.1.10";
    .port = "8080";
}

backend server2 {
    .host = "192.168.1.11";
    .port = "8080";
}

sub vcl_recv {
    if (req.http.host ~ "example.com") {
        set req.backend_hint = server1;
    } else {
        set req.backend_hint = server2;
    }
}
```

### 根据请求的 HTTP 主机名选择后端服务器

```vcl
backend server1 {
    .host = "192.168.1.10";
    .port = "8080";
}

backend server2 {
    .host = "192.168.1.11";
    .port = "8080";
}

if (req.http.host ~ "^www\.?server1\.com$") {
    set req.backend = server1;
}
```

这行代码的意思是：如果请求的 HTTP 主机名匹配正则表达式 ^www\.?server1\.com$，则执行大括号内的代码。

### 默认的 backend 选择规则

如果只有一个 backend，则不需要显式声明使用的 backend 规则，因为 Varnish 会默认使用唯一的后端服务器。

以下是一个简化的 VCL 配置示例：

```vcl
vcl 4.0;

import std;

backend default {
    .host = "192.168.1.10";
    .port = "8080";
}

sub vcl_recv {
    # 这里可以添加自定义规则，例如缓存策略
    if (req.method != "GET" && req.method != "HEAD") {
        return (pass);
    }

    # 其他自定义规则
}

sub vcl_backend_response {
    # 设置缓存 TTL
    set beresp.ttl = 1h;

    # 其他自定义规则
}

sub vcl_deliver {
    # 添加自定义头部信息
    if (obj.hits > 0) {
        set resp.http.X-Cache = "HIT";
    } else {
        set resp.http.X-Cache = "MISS";
    }

    # 删除不必要的头部信息
    unset resp.http.Server;
    unset resp.http.X-Varnish;
}

sub vcl_fini {
    return (ok);
}
```

**详细解释**

1. **定义唯一的后端服务器**：
   ```vcl
   backend default {
       .host = "192.168.1.10";
       .port = "8080";
   }
   ```
   定义了一个名为 `default` 的后端服务器。由于只有一个后端服务器，因此不需要额外的路由规则。

2. **`vcl_recv` 子程序**：
   ```vcl
   sub vcl_recv {
       if (req.method != "GET" && req.method != "HEAD") {
           return (pass);
       }

       # 其他自定义规则
   }
   ```
   处理接收到的请求，决定是否缓存。例如，非 GET/HEAD 请求不缓存。

3. **`vcl_backend_response` 子程序**：
   ```vcl
   sub vcl_backend_response {
       set beresp.ttl = 1h;

       # 其他自定义规则
   }
   ```
   处理从后端服务器接收到的响应，设置缓存 TTL。

4. **`vcl_deliver` 子程序**：
   ```vcl
   sub vcl_deliver {
       if (obj.hits > 0) {
           set resp.http.X-Cache = "HIT";
       } else {
           set resp.http.X-Cache = "MISS";
       }

       unset resp.http.Server;
       unset resp.http.X-Varnish;
   }
   ```
   处理发送给客户端的响应，添加自定义头部信息并删除不必要的头部信息。

5. **`vcl_fini` 子程序**：
   ```vcl
   sub vcl_fini {
       return (ok);
   }
   ```
   处理请求完成后的清理工作。

## Director

在 Varnish Configuration Language (VCL) 中，`director` 是一种用于定义和管理多个后端服务器的机制。通过 `director`，你可以实现负载均衡、高可用性和故障转移等功能，从而提高系统的可靠性和性能。

`director` 允许你将多个后端服务器组合在一起，可以理解为 director 的逻辑分组或 director 的集群，并根据一定的策略选择其中一个后端服务器来处理请求。这在以下场景中特别有用：

1. **负载均衡**：将流量分配到多个后端服务器，以提高性能和可靠性。
2. **高可用性**：当一个后端服务器不可用时，自动将请求路由到其他可用的后端服务器。
3. **故障转移**：在某个后端服务器出现故障时，自动切换到备用服务器。

### Director 的类型

Varnish 提供了多种类型的 `director`，每种类型有不同的负载均衡策略：

1. **round_robin**：轮询调度，将请求依次分配给每个后端服务器。
2. **random**：随机选择后端服务器。
3. **client**：根据客户端的 IP 地址进行哈希，选择后端服务器。
4. **hash**：根据请求的 URL 或其他指定字段进行哈希，选择后端服务器。
5. **fallback**：定义一个主后端服务器和一个备用后端服务器，当主服务器不可用时，使用备用服务器。

### Director 的定义语法

定义一个 `director` 的基本语法如下：

```vcl
director <director_name> <type> {
    .backend = <backend1>;
    .backend = <backend2>;
    # 其他参数
}
```

- `<director_name>`：director 的名称，用于在 VCL 中引用。
- `<type>`：director 的类型，如 `round_robin`、`random` 等。
- `<backend1>`, `<backend2>`：定义的后端服务器。

### Director 示例

以下是一个完整的 `director` 定义示例，使用 `round_robin` 类型：

```vcl
backend server1 {
    .host = "192.168.1.10";
    .port = "8080";
}

backend server2 {
    .host = "192.168.1.11";
    .port = "8080";
}

director my_director round_robin {
    { .backend = server1; }
    { .backend = server2; }
}

sub vcl_recv {
    # 使用 director 选择后端服务器
    set req.backend_hint = my_director.backend();
}
```

**详细解释**

1. **定义后端服务器**：
   ```vcl
   backend server1 {
       .host = "192.168.1.10";
       .port = "8080";
   }

   backend server2 {
       .host = "192.168.1.11";
       .port = "8080";
   }
   ```
   定义了两个后端服务器 `server1` 和 `server2`。

2. **定义 director**：
   ```vcl
   director my_director round_robin {
       { .backend = server1; }
       { .backend = server2; }
   }
   ```
   定义了一个名为 `my_director` 的 `director`，类型为 `round_robin`，包含 `server1` 和 `server2` 两个后端服务器。

3. **使用 director**：
   ```vcl
   sub vcl_recv {
       set req.backend_hint = my_director.backend();
   }
   ```
   在 `vcl_recv` 子程序中，使用 `my_director` 选择后端服务器。

### Director 类型及参数配置

1. **random（随机）**

   随机选择后端服务器。

   ```vcl
   director my_random random {
       .retries = 5;
       {
           .backend = server1;
           .weight = 7;
       }
       {
           .backend = server2;
           .weight = 3;
       }
   }
   ```

   - **retries**：指定查找可用后端的次数。默认情况下，Director 中的所有后端的 retries 相同。比如上述配置就规定了 server1 与 server2 的重试次数都是 5，如果某个后端在指定的次数内不可用，Varnish 会尝试其他后端。
   - **weight**：权重参数决定了每个后端被选中的概率。

2. **round_robin（轮询）**

   轮询调度，将请求依次分配给每个后端服务器。

   ```vcl
   director my_round_robin round_robin {
       {
           .backend = server1;
       }

       {
           .backend = server2;
       }
   }
   ```

3. **client（客户端）**

   根据客户端的 IP 地址进行哈希，选择后端服务器。

   ```vcl
   director my_client client {
       {
           .backend = server1;
       }

       {
           .backend = server2;
       }
   }
   ```

对于 `client director`，你可以通过设置 VCL 的变量 `client.identity` 来区分客户端。这个值可以从 `session cookie` 或其他类似的值来获取。这意味着你可以根据客户端的不同身份来设置不同的调度策略，从而实现更精细的负载均衡和控制。

例如，你可以根据不同的用户会话或客户端特征，将请求分配到不同的后端服务器。这对于需要会话粘性（session stickiness）或根据客户端特征进行差异化服务的场景非常有用。

以下是一个简单的示例，展示如何在 VCL 中设置 `client.identity` 并使用不同的 `director`：

```vcl
sub vcl_recv {
    // 从 cookie 中获取客户端身份
    if (req.http.Cookie ~ "sessionid=") {
        set client.identity = regsub(req.http.Cookie, ".*sessionid=([^;]*);*.*", "\1");
    }

    // 根据客户端身份选择不同的 director
    if (client.identity == "user1") {
        set req.backend_hint = my_round_robin.backend();
    } else {
        set req.backend_hint = my_random.backend();
    }
}
```

在这个示例中，我们首先从 `Cookie` 中提取 `sessionid`，然后根据 `sessionid` 的值设置 `client.identity`。最后，根据 `client.identity` 的值选择不同的 `director`。

4. **hash（哈希）**

   根据请求的 URL 或其他指定字段进行哈希，选择后端服务器。

   ```vcl
   director my_hash hash {
       {
           .backend = server1;
           .weight = 1;
       }

       {
           .backend = server2;
           .weight = 1;
       }
   }
   ```

对于 `hash director`

默认使用 URL 的 hash 值，可以通过 `req.hash` 获取到。这意味着在使用 `hash director` 时，请求会根据 URL 的哈希值来决定分配到哪个后端服务器。这种方式可以实现会话粘性（session stickiness），确保同一用户的请求总是被分配到同一个后端服务器。

以下是一个示例配置：

```vcl
director my_hash hash {
    {
        .backend = server1;
    }
    {
        .backend = server2;
    }
}

sub vcl_recv {
    set req.backend_hint = my_hash.backend();
}
```

在这个示例中，`my_hash` 使用 URL 的哈希值来决定请求分配到 `server1` 还是 `server2`。

定义哈希函数

**描述**: 定义哈希函数，用于在轮询调度中实现会话粘性（session stickiness）。

**示例**:
```vcl
director my_director hash {
    {
        .backend = server1;
        .hash = {
            "sessionid";
        }
    }
    {
        .backend = server2;
        .hash = {
            "sessionid";
        }
    }
}
```

**实际使用场景**:

- **场景描述**: 确保同一会话的请求被分配到同一个后端服务器，以保持会话状态。
  
- **具体应用**:
  - **会话保持**: 通过哈希 `sessionid`，确保同一用户的请求总是被转发到同一个后端服务器，避免会话丢失。
  - **有状态应用**: 对于需要保持会话状态的应用（如购物车、用户登录状态等），会话粘性是必不可少的。

5. **fallback（故障转移）**

   定义一个主后端服务器和一个备用后端服务器，当主服务器不可用时，使用备用服务器。

   ```vcl
   director my_fallback fallback {
       {
           .backend = server1;
       }

       {
           .backend = server2; // 第一个不可用时才会走
       }
   }
   ```

### 示例配置

以下是一个综合示例，展示如何使用不同的 `director` 类型：

```vcl
backend server1 {
    .host = "192.168.1.10";
    .port = "8080";
}

backend server2 {
    .host = "192.168.1.11";
    .port = "8080";
}

director my_round_robin round_robin {
    { .backend = server1; }
    { .backend = server2; }
}

director my_random random {
    .retries = 5;

    {
        .backend = server1;
        .weight = 7;
    }

    {
        .backend = server2;
        .weight = 3;
    }
}

director my_client client {
    { .backend = server1; }
    { .backend = server2; }
}

director my_hash hash {
    { .backend = server1; .weight = 1; }
    { .backend = server2; .weight = 1; }
}

director my_fallback fallback {
    { .backend = server1; }
    { .backend = server2; }
}

sub vcl_recv {
    # 使用不同的 director
    set req.backend_hint = my_round_robin.backend();# 表示调用 director 对象 my_round_robin 的 backend 方法。通过调用 .backend() 方法，director 会根据其调度策略动态选择合适的后端服务器。例如，对于轮询（round-robin）策略，每次调用 .backend() 都会返回不同的后端服务器（基于轮询顺序）。
    # 或者使用其他 director
    # set req.backend_hint = my_random.backend();
    # set req.backend_hint = my_client.backend();
    # set req.backend_hint = my_hash.backend();
    # set req.backend_hint = my_fallback.backend();
}
```

### 内联 Backend 定义语法

`director` 可以内联定义后端服务器，而无需单独定义每个后端。这种方式可以使配置更加简洁和集中。

在 `director` 中内联定义后端服务器的语法如下：

```vcl
director <director_name> <type> {
    {
        .host = "<hostname_or_IP>";
        .port = "<port_number>";
        # 其他可选参数
    }
    {
        .host = "<hostname_or_IP>";
        .port = "<port_number>";
        # 其他可选参数
    }
    # 其他参数
}
```

以下是一个使用内联后端服务器定义的 `director` 示例：

```vcl
director my_director round_robin {
    {
        .host = "192.168.1.10";
        .port = "8080";
    }
    {
        .host = "192.168.1.11";
        .port = "8080";
    }
}
```

详细解释

1. **定义 director**：
   ```vcl
   director my_director round_robin {
   ```
   定义了一个名为 `my_director` 的 `director`，类型为 `round_robin`。

2. **内联定义后端服务器**：
   ```vcl
   {
       .host = "192.168.1.10";
       .port = "8080";
   }
   {
       .host = "192.168.1.11";
       .port = "8080";
   }
   ```
   在 `director` 中直接定义了多个后端服务器，而无需单独声明每个后端。

3. **使用 director**：
   ```vcl
   sub vcl_recv {
       set req.backend_hint = my_director.backend();
   }
   ```
   在 `vcl_recv` 子程序中，使用 `my_director` 选择后端服务器。

### 综合示例

以下是一个更复杂的示例，展示如何使用内联后端服务器定义多个 `director`：

```vcl
director my_round_robin round_robin {
    {
        .host = "192.168.1.10";
        .port = "8080";
    }
    {
        .host = "192.168.1.11";
        .port = "8080";
    }
}

director my_random random {
    .retries = 5;

    {
        .host = "fs1";
        .weight = 7;
    }

    {
        .host = "fs2";
        .weight = 3;
    }
}

sub vcl_recv {
    # 使用不同的 director
    set req.backend_hint = my_round_robin.backend(); # 表示调用 director 对象 my_round_robin 的 backend 方法。通过调用 .backend() 方法，director 会根据其调度策略动态选择合适的后端服务器。例如，对于轮询（round-robin）策略，每次调用 .backend() 都会返回不同的后端服务器（基于轮询顺序）。
    # 或者使用其他 director
    # set req.backend_hint = my_random.backend();
}
```

### 其他参数

#### 失败重试

**描述**: 定义在选择后端服务器失败时的重试次数。

**示例**:
```vcl
director my_director round_robin {
    .retries = 3;  # 重试3次
    {
        .backend = server1;
    }
    {
        .backend = server2;
    }
}
```

**实际使用场景**:

- **场景描述**: 当某个后端服务器暂时不可用或响应缓慢时，`director` 可以尝试重新连接到其他可用的后端服务器，以提高系统的容错能力。
  
- **具体应用**:
  - **临时故障处理**: 例如，后端服务器 `server1` 正在重启或遇到临时网络问题，`director` 会尝试重试 3 次，如果仍然失败，则选择 `server2`。
  - **负载均衡优化**: 在高负载情况下，某个后端服务器可能因为过载而响应变慢，通过重试机制，可以暂时绕过该服务器，减轻其负担。

#### 负载权重

**描述**: 为每个后端服务器分配权重，影响调度时的选择概率。

**示例**:
```vcl
director my_director round_robin {
    {
        .backend = server1;
        .weight = 3;  # 权重为3
    }
    {
        .backend = server2;
        .weight = 1;  # 权重为1
    }
}
```

**实际使用场景**:

- **场景描述**: 当后端服务器的处理能力不同或资源分配不均时，可以通过权重来平衡负载。
  
- **具体应用**:
  - **异构服务器集群**: 假设 `server1` 的处理能力是 `server2` 的三倍，可以将 `server1` 的权重设为 3，`server2` 的权重设为 1。这样，`server1` 会被选中的概率更高，从而更好地利用其处理能力。
  - **动态负载调整**: 在业务高峰期，某些服务器可能需要承担更多的负载，通过调整权重，可以实现动态负载分配。


#### 集群最低健康状态

**描述**: 定义在选择后端服务器时需要达到的最低健康状态比例。

**示例**:
```vcl
director my_director round_robin {
    .quorum = 75;  # 至少75%的后端服务器健康
    {
        .backend = server1;
    }
    {
        .backend = server2;
    }
}
```

**实际使用场景**:

- **场景描述**: 当多个后端服务器中的一部分出现故障时，`director` 需要决定是否继续使用剩余的健康服务器。
  
- **具体应用**:
  - **高可用性需求**: 假设有 4 个后端服务器，`quorum` 设置为 75%，意味着至少 3 个服务器必须健康才能继续提供服务。如果只有 2 个服务器健康，`director` 会停止转发请求，避免服务降级。
  - **故障恢复**: 当部分服务器恢复健康后，`director` 会自动恢复正常的负载均衡。


#### 自动故障转移

**描述**: 定义在选择后端服务器失败时的行为，如是否继续尝试其他服务器。

**示例**:
```vcl
director my_director round_robin {
    .fall = 1;  # 如果一个后端服务器失败，则继续尝试其他服务器
    {
        .backend = server1;
    }
    {
        .backend = server2;
    }
}
```

**实际使用场景**:

- **场景描述**: 当某个后端服务器失败时，`director` 需要决定是否尝试其他服务器。
  
- **具体应用**:
  - **故障转移**: 如果 `server1` 失败，`director` 会自动将请求转发到 `server2`，确保服务不中断。
  - **高可用性**: 通过设置 `.fall = 1`，可以实现自动故障转移，提高系统的整体可用性。

#### 限制客户端访问权限

**描述**: 定义访问控制列表（ACL），用于限制对后端服务器的访问。

**示例**:
```vcl
acl restricted_ips {
    "192.168.1.0"/24;
}

director my_director round_robin {
    {
        .backend = server1;
        .acl = restricted_ips;
    }
    {
        .backend = server2;
        .acl = restricted_ips;
    }
}
```

**实际使用场景**:

- **场景描述**: 控制哪些客户端可以访问特定的后端服务器。
  
- **具体应用**:
  - **安全控制**: 限制只有来自特定 IP 范围（如 `192.168.1.0/24`）的客户端可以访问 `server1` 和 `server2`。
  - **区域访问控制**: 根据地理位置或网络区域，限制访问权限，提高安全性。


### DNS Director 

DNS Director 的原理结合了 DNS（域名系统）的动态解析能力和负载均衡策略，通过定期查询 DNS 记录来动态选择后端服务器。

1. **DNS 查询**：
   DNS Director 会根据配置中的 DNS 记录，定期向指定的 DNS 服务器发送查询请求，获取最新的 DNS 记录。这些 DNS 记录包含了后端服务器的 IP 地址。

2. **缓存机制**：
   为了减少频繁的 DNS 查询，DNS Director 会根据 DNS 记录的 TTL（生存时间）缓存查询结果。在 TTL 过期之前，DNS Director 会使用缓存中的 IP 地址作为后端服务器列表。TTL 过期后，DNS Director 会重新发起 DNS 查询，获取最新的记录。

3. **负载均衡**：
   获取到最新的 DNS 记录后，DNS Director 会根据配置的负载均衡策略（如轮询、最少连接等），将这些 IP 地址分配给客户端请求。这样可以实现动态负载均衡，确保请求均匀分布到各个后端服务器。

4. **故障转移**：
   如果某个后端服务器不可用，DNS Director 会自动从可用的服务器中选择一个来处理请求。这种机制提高了系统的容错能力和可用性。

以下是一个 DNS director 的配置示例：

```vcl
director my_dns_director dns {
    .list = {
        .host_header = "www.example.com";
        .port = "8080";
        .connection_timeout = 1s;
        "192.168.1.0"/24;
        "192.168.2.0"/24;
    }
    .ttl = 5m;
    .suffix = "internal.example.com";
}
```

1. **director my_dns_director dns**:
   - 定义一个名为 `my_dns_director` 的 DNS director。

2. **.list**:
   - **.host_header**: 设置请求头中的主机名，这里为 `www.example.com`。
   - **.port**: 设置请求的目标端口，这里为 `8080`。
   - **.connection_timeout**: 设置连接超时时间，这里为 `1` 秒。
   - **IP 地址段**: 定义了两个 IP 地址段，`192.168.1.0/24` 和 `192.168.2.0/24`，表示这两个网段内的所有 IP 地址都会作为后端服务器。

3. **.ttl**:
   - 定义 DNS lookup 的缓存时间，这里设置为 `5` 分钟。

4. **.suffix**:
   - 设置内部域名后缀，这里为 `internal.example.com`。

- **动态选择后端服务器**: DNS director会根据DNS查询结果动态选择后端服务器。
- **TTL缓存**: 根据TTL缓存DNS查询结果，在TTL过期后重新查询DNS。
- **连接超时设置**: 可以设置连接超时时间，以确保不会因为某个后端服务器响应过慢而影响整体性能。
- **IP 地址段**: 可以指定多个IP段，表示这些IP段内的所有IP地址都会作为后端服务器。

#### 使用场景

- **动态负载均衡**: 当后端服务器地址经常变化时，可以使用DNS director根据DNS查询结果动态选择后端服务器。
- **高可用性**: 通过TTL缓存和动态选择，可以在后端服务器故障时自动切换到其他可用服务器，提高系统的可用性。

**注意事项**

- **不支持IPv6**: 当前版本的DNS director不支持IPv6。
- **列表项顺序**: `.list` 中的设置项必须在 IP 地址段之前。

#### 使用实例

假设我们有一个网站 `www.example.com`，我们需要配置Varnish以实现动态负载均衡。我们可以使用DNS director来实现这一目标。以下是一个完整的VCL配置示例：

```vcl
vcl 4.0;

import directors;

backend server1 {
    .host = "192.168.1.10";
    .port = "8080";
}

backend server2 {
    .host = "192.168.1.11";
    .port = "8080";
}

director my_dns_director dns {
    .list = {
        .host_header = "www.example.com";
        .port = "8080";
        .connection_timeout = 1s;
        "192.168.1.0"/24;
        "192.168.2.0"/24;
    }
    .ttl = 5m;
    .suffix = "internal.example.com";
}

sub vcl_init {
    new my_director = directors.dns(
        .list = {
            .host_header = "www.example.com";
            .port = "8080";
            .connection_timeout = 1s;
            "192.168.1.0"/24;
            "192.168.2.0"/24;
        },
        .ttl = 5m,
        .suffix = "internal.example.com"
    );
}

sub vcl_recv {
    set req.backend_hint = my_director.backend();
}
```

在这个示例中，我们定义了一个DNS director `my_dns_director`，并在 `vcl_init` 中初始化它。然后在 `vcl_recv` 中使用 `my_director.backend()` 来选择后端服务器。这样可以实现动态负载均衡，根据DNS查询结果选择后端服务器。

## ACL

在 VCL 中，Access Control Lists (ACL) 是一种用于定义 IP 地址列表及其访问权限的机制。通过 ACL，管理员可以控制哪些客户端可以访问特定的资源或执行特定的操作。

ACL（访问控制列表）用于定义一组 IP 地址或子网，以便在 VCL 代码中根据客户端的 IP 地址进行访问控制。ACL 可以用于允许或拒绝来自特定 IP 地址的请求，从而实现安全性和访问控制策略。

### 基本语法

在 VCL 中定义 ACL 的基本语法如下：

```vcl
acl <acl_name> {
    "ip_address1";
    "ip_address2";
    "subnet/mask";
    ...
}
```

- `<acl_name>`：ACL 的名称，用于在 VCL 代码中引用。
- `"ip_address"`：单个 IP 地址，可以是 IPv4 或 IPv6。
- `"subnet/mask"`：子网和掩码，用于定义一个 IP 范围。

### 示例

```vcl
acl internal_ips {
    "192.168.1.0"/24;    # 允许 192.168.1.0/24 子网的 IP
    "10.0.0.1";          # 允许单个 IP 10.0.0.1
    "2001:db8::1";       # 允许单个 IPv6 地址
    "2001:db8::"/32;     # 允许 2001:db8::/32 子网的 IPv6 地址
}
```

#### 无效的主机名

在 Varnish 的 ACL（访问控制列表）中，如果指定了一个主机名，但 Varnish 不能解析这个主机名，那么 Varnish 会将这个主机名匹配到所有地址。这是 Varnish 处理无法解析主机名的一种默认行为。

当你在 Varnish 的 ACL 中使用主机名时，Varnish 会尝试将该主机名解析为 IP 地址。如果解析成功，ACL 将匹配解析后的 IP 地址。然而，如果 Varnish 不能解析这个主机名（例如，由于 DNS 配置问题或主机名不存在），Varnish 将不会拒绝匹配，而是将该主机名视为匹配所有地址。这种行为意味着，任何客户端 IP 地址都会被认为匹配这个 ACL 条目。

假设你有以下 ACL 配置：

```vcl
acl example_acl {
    "nonexistent_host";
    "192.0.2.0"/24;
}
```

在这个例子中，`nonexistent_host` 是一个无法解析的主机名。由于 Varnish 不能解析这个主机名，它会将这个主机名视为匹配所有地址。因此，`example_acl` 将匹配以下地址：
- 任何 IP 地址（因为 `nonexistent_host` 无法解析）
- 192.0.2.0/24 子网内的所有 IP 地址

这种行为可能会导致意想不到的结果，特别是在安全敏感的上下文中。如果你希望严格控制访问权限，确保所有主机名都可以正确解析是非常重要的。否则，未能解析的主机名可能会无意中允许不必要的访问。

为了避免这种情况，可以采取以下措施：
1. **确保 DNS 配置正确**：确保 Varnish 服务器的 DNS 配置正确，能够解析所有需要的主机名。
2. **使用 IP 地址**：尽量在 ACL 中直接使用 IP 地址，而不是主机名，以避免解析问题。
3. **监控和日志**：定期检查 Varnish 的日志，监控无法解析的主机名，并及时修正。

#### 否定标记

在使用 Varnish 的 ACL（访问控制列表）时，否定标记（!）是一个非常强大的工具，用于拒绝匹配特定的主机或 IP 地址。理解如何使用否定标记，可以帮助你更精确地控制哪些客户端可以访问你的服务，哪些需要被拒绝。

在 ACL 中使用否定标记（!）表示拒绝匹配指定的主机或 IP 地址。所有其他没有使用否定标记的主机或 IP 地址将被允许访问。

假设你有以下 ACL 配置：

```vcl
acl deny_acl {
    !"192.0.2.23";
    !"192.0.2.0"/24;
}
```

在这个例子中：
- `"!192.0.2.23"` 表示拒绝 IP 地址 192.0.2.23。
- `"!192.0.2.0"/24` 表示拒绝 192.0.2.0/24 子网内的所有 IP 地址。

因此，任何来自 192.0.2.23 或 192.0.2.0/24 子网内的请求都会被拒绝。所有其他 IP 地址将被允许访问。

否定标记非常适用于以下场景：

1. **禁止特定 IP 地址或子网**：如果你知道某些 IP 地址或子网存在恶意活动，可以使用否定标记来阻止这些地址访问你的服务。
2. **限制访问范围**：通过拒绝特定范围，你可以更好地控制访问权限，只允许可信任的客户端访问。

否定标记可以与其他 ACL 条目结合使用，以实现更复杂的访问控制策略。例如：

```vcl
acl allow_acl {
    "192.168.1.0"/24;
}

acl deny_acl {
    !"192.168.1.10";
}
```

在这个例子中：
- `allow_acl` 允许 192.168.1.0/24 子网内的所有 IP 地址。
- `deny_acl` 拒绝 IP 地址 192.168.1.10。

因此，尽管 192.168.1.10 属于允许的子网，但由于它在 `deny_acl` 中被否定标记拒绝，所以它仍然无法访问。

通过合理使用否定标记，你可以实现更灵活和精细的访问控制策略，提高系统的安全性和管理效率。在配置 ACL 时，确保正确使用否定标记，以避免意外地允许或拒绝不必要的访问。

### 在 VCL 中使用 ACL

ACL 通常在 `vcl_recv` 钩子中使用，用于根据客户端的 IP 地址决定是否允许访问。以下是一些常见的用法示例：

#### 1. 允许特定 IP 访问特定资源

```vcl
acl admin_ips {
    "192.168.1.100";
    "10.0.0.50";
}

sub vcl_recv {
    if (req.url ~ "^/admin/") {
        if (!client.ip ~ admin_ips) {
            return (synth(403, "Forbidden"));
        }
    }
}
```

在这个示例中，只有 `admin_ips` ACL 中定义的 IP 地址可以访问以 `/admin/` 开头的 URL。其他 IP 地址将被拒绝，并返回 403 禁止访问错误。

#### 2. 拒绝特定 IP 访问

```vcl
acl blocked_ips {
    "123.456.789.0"/24;  # 拒绝 123.456.789.0/24 子网的 IP
    "89.45.123.67";      # 拒绝单个 IP 89.45.123.67
}

sub vcl_recv {
    if (client.ip ~ blocked_ips) {
        return (synth(403, "Forbidden"));
    }
}
```

在这个示例中，`blocked_ips` ACL 中定义的 IP 地址将被拒绝访问，并返回 403 禁止访问错误。

#### 3. 结合多个 ACL 进行复杂控制

```vcl
acl whitelist {
    "192.168.1.0"/24;
    "10.0.0.0"/16;
}

acl blacklist {
    "123.456.789.0"/24;
    "89.45.123.67";
}

sub vcl_recv {
    if (req.url ~ "^/secure/") {
        if (client.ip ~ blacklist) {
            return (synth(403, "Forbidden"));
        }
        if (!client.ip ~ whitelist) {
            return (synth(403, "Forbidden"));
        }
    }
}
```

在这个示例中，只有在 `whitelist` ACL 中定义的 IP 地址可以访问以 `/secure/` 开头的 URL，并且这些 IP 地址不能出现在 `blacklist` ACL 中。

### 高级用法

#### 1. 使用 ACL 进行速率限制

虽然 VCL 的 ACL 主要用于访问控制，但结合其他 VCL 功能，可以实现速率限制。例如：

```vcl
acl rate_limit_ips {
    "192.168.1.0"/24;
}

sub vcl_recv {
    if (client.ip ~ rate_limit_ips) {
        # 使用 varnish 变量和计数器实现速率限制
        # 这需要更复杂的 VCL 逻辑
    }
}
```

具体的速率限制实现可能需要结合 Varnish 的计数器、变量和定时器功能。

#### 2. 动态更新 ACL

VCL 的 ACL 通常在配置文件中定义。如果需要动态更新 ACL，可以使用 Varnish 的 `vcl_recv` 钩子结合外部数据源。例如：

```vcl
sub vcl_recv {
    # 从外部源获取 ACL 数据
    # 这里假设有一个函数可以获取当前的 ACL 列表
    # 例如：
    # set req.http.x-acl = get_current_acl();

    # 然后根据 req.http.x-acl 进行判断
    if (client.ip ~ req.http.x-acl) {
        # 允许访问
    } else {
        return (synth(403, "Forbidden"));
    }
}
```

注意：这种动态更新需要自定义 VCL 代码和外部数据源的支持。

### 注意事项

1. **性能影响**：ACL 的使用可能会对性能产生一定影响，特别是在 ACL 列表较大或规则复杂时。因此，建议尽量简化 ACL 规则，并优化 ACL 的使用方式。

2. **安全性**：确保 ACL 列表中的 IP 地址是准确的，避免误配置导致的安全漏洞。同时，定期审查和更新 ACL 列表，以适应不断变化的安全需求。

3. **VCL 版本**：不同版本的 Varnish 可能对 VCL 的语法和功能支持有所不同。在编写和配置 VCL 时，请参考对应版本的官方文档。

VCL 的 ACL 提供了一种强大的方式来控制客户端的访问权限。通过定义和管理 ACL，管理员可以有效地实施访问控制策略，保护 Web 应用程序和资源的安全。结合 VCL 的其他功能，ACL 可以实现复杂的访问控制和流量管理策略。

以下是一些关键点总结：

- **定义 ACL**：使用 `acl` 关键字在 VCL 配置文件中定义 IP 地址和子网。
- **使用 ACL**：在 `vcl_recv` 钩子中根据客户端 IP 地址进行匹配，决定是否允许访问。
- **高级用法**：结合其他 VCL 功能，实现更复杂的访问控制策略，如速率限制和动态 ACL 更新。

通过合理配置和使用 ACL，Varnish 可以显著提升 Web 应用程序的安全性和性能。

# 请求合并（Request Coalescing）

当多个客户端几乎同时请求相同的资源时，Varnish 不会对每个请求都向后端服务器发送请求。相反，Varnish 会执行以下操作：

1. **第一个请求**：
   - 当第一个请求到达时，Varnish 会检查缓存中是否存在该资源的有效版本。
   - 如果缓存中没有该资源，或者缓存的资源已经过期，Varnish 会向后端服务器发送一个请求以获取最新的内容。

2. **后续请求**：
   - 当后续的相同请求到达时，如果 Varnish 已经有一个请求正在等待后端服务器的响应（即第一个请求），它会将这些后续请求挂起（suspend），而不是再向后端服务器发送新的请求。
   - 这些挂起的请求会等待第一个请求完成并返回结果。

3. **响应分发**：
   - 当第一个请求完成后，Varnish 会将获取到的内容存储在缓存中。
   - 然后，Varnish 会将这个内容复制并返回给所有挂起的请求对应的客户端。

这种机制确保了对于相同的资源，Varnish 只会向后端服务器发送一个请求，从而避免了对后端服务器造成过大的压力。

# 缓存机制

在处理请求的同时，Varnish 会将获取到的内容存储在缓存中，以便后续的请求可以直接从缓存中获取，而无需再次访问后端服务器。缓存机制的工作流程如下：

1. **缓存存储**：
   - 当 Varnish 从后端服务器获取到内容后，它会将这些内容存储在内存中，并设置相应的 TTL（生存时间）。
   - 存储的内容包括 HTTP 响应头和响应体。

2. **缓存命中**：
   - 当后续的请求到达时，Varnish 会检查缓存中是否存在该资源的有效版本。
   - 如果缓存中存在有效的资源，Varnish 会直接从缓存中返回内容，而无需访问后端服务器。

3. **缓存过期**：
   - 如果缓存中的资源已经过期，Varnish 会执行请求合并的逻辑，如前文所述，只向后端服务器发送一个请求，并在获取新内容的同时将其他请求挂起。

# Grace 模式

为了进一步优化性能，Varnish 还支持 **Grace 模式**。在 Grace 模式下，即使缓存中的资源已经过期，Varnish 仍然可以在短时间内继续提供旧的内容，同时在后台异步地获取新内容。这样可以进一步减少用户的等待时间，并避免在缓存过期时对后端服务器造成压力。

## 配置示例

以下是一个简单的 VCL 配置示例，展示了如何启用请求合并和缓存机制：

```vcl
vcl 4.0;

backend default {
    .host = "127.0.0.1";
    .port = "8080";
}

# 接收到客户端请求时
sub vcl_recv {
	# 设置请求的 Grace 时间为 15 秒, 在这里设置 req.grace 可以确保在请求的早期阶段就启用了 Grace 模式，从而使 Varnish 在缓存对象过期后的一段时间内继续提供旧的内容。这意味着在缓存对象过期后的 15 秒内，Varnish 仍然可以提供旧的内容。
    set req.grace = 15s;
    return (hash);
}

# 接收到后端服务器的响应时
sub vcl_backend_response {
    # 设置缓存的 TTL
    set beresp.ttl = 5m;
    # 启用 Grace 模式
    set beresp.grace = 2m;
}

sub vcl_deliver {
    # 可选：添加自定义头信息
    set resp.http.X-Cache = "HIT"; # 添加自定义头信息，表示缓存命中。
    if (obj.ttl <= 0s) {
        set resp.http.X-Cache = "MISS"; # 如果缓存对象已经过期，设置头信息为 MISS。
    }
}
```

### 配置解析

```vcl
sub vcl_recv {
    # 设置请求的 Grace 时间为 15 秒, 在这里设置 req.grace 可以确保在请求的早期阶段就启用了 Grace 模式，从而使 Varnish 在缓存对象过期后的一段时间内继续提供旧的内容。这意味着在缓存对象过期后的 15 秒内，Varnish 仍然可以提供旧的内容。
    set req.grace = 15s;
    return (hash);
}
```

在 `vcl_recv` 中，我们设置了请求的 Grace 时间为 15 秒。这意味着即使缓存对象已经过期，Varnish 也能够在接下来的 15 秒内继续提供旧的内容。这对于在高并发情况下减少后端服务器的压力非常有帮助。

```vcl
sub vcl_backend_response {
    # 设置缓存的 TTL
    set beresp.ttl = 5m;
    # 启用 Grace 模式
    set beresp.grace = 2m;
}
```

在 `vcl_backend_response` 中，我们设置了缓存对象的 TTL 为 5 分钟。这意味着缓存中的对象在生成后的 5 分钟内被认为是有效的，可以直接从缓存中提供。

同时，我们启用了 Grace 模式，并将 Grace 时间设置为 2 分钟。这意味着在对象 TTL 过期后的 2 分钟内，Varnish 仍然可以提供旧的内容，同时在后台异步地获取新内容。

```vcl
sub vcl_deliver {
    # 可选：添加自定义头信息
    set resp.http.X-Cache = "HIT"; # 添加自定义头信息，表示缓存命中。
    if (obj.ttl <= 0s) {
        set resp.http.X-Cache = "MISS"; # 如果缓存对象已经过期，设置头信息为 MISS。
    }
}
```

在 `vcl_deliver` 中，我们添加了一个自定义 HTTP 头信息 `X-Cache`，用于指示缓存命中情况。如果缓存命中（即缓存中的对象是有效的），则设置为 "HIT"；如果缓存对象已经过期，则设置为 "MISS"。

### 注意事项

1. **调整 TTL 和 Grace 时间**：
   - **TTL**：根据内容的更新频率和业务需求，调整 TTL 的值。例如，如果内容很少变化，可以将 TTL 设置得长一些；如果内容频繁更新，则需要设置得短一些。
   - **Grace 时间**：Grace 时间应设置得足够长，以便在后台获取新内容时能够覆盖到可能出现的延迟。但是，过长的 Grace 时间可能导致用户看到过期的内容。因此，需要根据实际情况进行权衡。

   ```vcl
   sub vcl_backend_response {
       set beresp.ttl = 10m;    # 调整为 10 分钟
       set beresp.grace = 5m;   # 调整为 5 分钟
   }
   ```

2. **结合 Saint 模式**：
   - 如果后端服务器偶尔会出现错误，可以使用 Saint 模式来避免在短时间内重复请求失败的服务器。

   ```vcl
   sub vcl_backend_response {
       if (beresp.status == 500 || beresp.status == 502 || beresp.status == 503 || beresp.status == 504) {
           set beresp.saintmode = 10s;
           return (restart);
       }
       set beresp.ttl = 5m;
       set beresp.grace = 2m;
   }
   ```

   在上述配置中，如果后端服务器返回 5xx 错误，Varnish 会暂停对该服务器的请求 10 秒，并尝试重新启动请求。

3. **使用 `beresp.keep`**：
   - `beresp.keep` 参数用于设置在缓存中保留对象的时间，以便处理有条件的 GET 请求（如带有 `If-Modified-Since` 或 `If-None-Match` 头）。

   ```vcl
   sub vcl_backend_response {
       set beresp.ttl = 5m;
       set beresp.grace = 2m;
       set beresp.keep = 1m;
   }
   ```

   这样，Varnish 会在缓存中保留对象 1 分钟，以便处理有条件的 GET 请求。

## 详情介绍

**Grace 模式**：
   - 通过设置 `beresp.grace` 参数，可以告诉 Varnish 在对象 TTL（生存时间）过期后的一段时间内继续提供旧的内容。例如，`set beresp.grace = 30m;` 表示对象在 TTL 过期后仍可以在缓存中保留 30 分钟。
   - 这种机制允许 Varnish 在后端服务器负载较高或响应较慢时，继续提供旧的内容，从而减少用户等待时间。

**Keep 模式**：
   - 与 Grace 模式相关的还有 Keep 模式。通过设置 `beresp.keep` 参数，可以告诉 Varnish 在缓存中保留对象的时间。例如，`set beresp.keep = 8m;` 表示对象在 TTL 过期后仍可以在缓存中保留 8 分钟。
   - Keep 模式主要用于构造有条件的 GET 请求（带有 `If-Modified-Since` 和/或 `If-None-Match` 头），允许后端服务器使用 `304 Not Modified` 响应，从而节省带宽和资源。

## 设置 Grace 和 Keep

在 VCL（Varnish Configuration Language）中，可以通过以下方式设置 Grace 和 Keep：

```vcl
sub vcl_backend_response {
    set beresp.grace = 2m;
    set beresp.keep = 8m;
}
```

上述配置表示对象在 TTL 过期后仍可以在缓存中保留 10 分钟（2 分钟的 Grace + 8 分钟的 Keep）。

## Grace 模式的应用场景

1. **避免请求扎堆**：
   - 当多个客户端同时请求相同的资源时，Grace 模式可以避免对后端服务器造成过大的压力，因为它只发送一个请求到后端，其他请求等待并使用旧的内容。

2. **后端服务器故障**：
   - 如果后端服务器出现故障，Grace 模式可以确保 Varnish 继续提供旧的内容，直到后端服务器恢复正常。

## 结合其他模式使用

1. **Saint 模式**：
   - Saint 模式与 Grace 模式类似，但主要用于处理后端服务器返回错误状态码（如 500）的情况。通过设置 `beresp.saintmode`，可以告诉 Varnish 在一定时间内不请求该服务器，并尝试从其他后端服务器获取内容。

   ```vcl
   sub vcl_fetch {
       if (beresp.status == 500) {
           set beresp.saintmode = 10s;
           return(restart);
       }
       set beresp.grace = 5m;
   }
   ```

   在上述配置中，当后端服务器返回 500 错误时，Varnish 会暂停对该服务器的请求 10 秒，并尝试从其他服务器获取内容。如果其他服务器不可用，则会提供缓存中的旧内容。

2. **健康检查**：
   - 通过健康检查，可以监控后端服务器的状态，并根据其健康状况调整 Grace 模式的行为。例如，当后端服务器健康时，可以设置较短的 Grace 时间；当后端服务器不健康时，可以设置较长的 Grace 时间。

   ```vcl
   sub vcl_backend_response {
       set beresp.grace = 24h;
   }

   sub vcl_recv {
       if (std.healthy(req.backend_hint)) {
           set req.grace = 10s;
       }
   }
   ```

   在上述配置中，当后端服务器健康时，Grace 时间被限制为 10 秒；当后端服务器不健康时，Grace 时间被设置为 24 小时。

## 总结

Grace 模式是 Varnish 中一个强大的功能，可以显著提高网站的性能和可靠性。通过合理设置 Grace 和 Keep 参数，并结合其他模式（如 Saint 模式），可以有效地处理高流量和后端服务器故障的情况。以下是一些关键点：

- **Grace 模式**：在对象 TTL 过期后继续提供旧的内容。
- **Keep 模式**：在缓存中保留对象的时间，用于构造有条件的 GET 请求。
- **请求合并**：避免对后端服务器造成过大的压力。
- **Saint 模式**：处理后端服务器返回错误状态码的情况。
- **健康检查**：根据后端服务器的健康状况调整 Grace 模式的行为。

通过这些配置，Varnish 可以更好地应对各种复杂的网络环境和应用需求。