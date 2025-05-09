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

# 基本架构

Varnish 的架构主要分为以下几个部分：

- **Varnish 守护进程（varnishd）**：这是 Varnish 的核心组件，负责处理 HTTP 请求和响应，管理缓存，以及与后端服务器通信。
- **VCL（Varnish Configuration Language）**：VCL 是 Varnish 的配置语言，允许管理员自定义缓存策略和请求处理逻辑。
- **共享内存日志（SHMLog）**：用于记录日志信息，管理员可以通过 `varnishlog` 和 `varnishtest` 等工具进行监控和分析。
- **管理接口（Management Interface）**：允许管理员通过命令行工具（如 `varnishadm`）动态管理 Varnish 实例，如加载新的 VCL 配置、清理缓存等。

# 工作流程

Varnish 的工作流程可以分为以下几个主要阶段：

## 接收客户端请求

当 Varnish 接收来自客户端的 HTTP 请求时，首先会进行一系列的预处理步骤：

1. **解析请求**：Varnish 解析客户端发送的 HTTP 请求，包括请求方法、URL、头部信息等。
2. **VCL 处理**：根据 VCL 配置，Varnish 决定如何处理该请求。这包括是否需要从缓存中获取响应、是否需要将请求转发给后端服务器等。

## 缓存查找

在处理请求时，Varnish 会尝试从缓存中查找匹配的响应：

1. **哈希计算**：Varnish 根据请求的 URL、主机名、请求方法等信息计算一个哈希值，用于快速查找缓存。
2. **缓存命中**：如果找到匹配的缓存条目，并且缓存条目尚未过期，Varnish 会直接从缓存中返回响应给客户端。
3. **缓存未命中或过期**：如果缓存中没有匹配的条目，或者缓存条目已经过期，Varnish 会将请求转发给后端服务器。

## 请求转发到后端服务器

当缓存未命中或缓存条目过期时，Varnish 会将请求转发给后端服务器：

1. **建立连接**：Varnish 与后端服务器建立连接，发送 HTTP 请求。
2. **接收响应**：Varnish 接收后端服务器的响应，包括状态码、头部信息和响应体。
3. **VCL 处理**：在接收到响应后，Varnish 会根据 VCL 配置决定是否缓存该响应、如何修改响应头部信息等。

## 缓存响应

如果 VCL 配置决定缓存响应，Varnish 会将响应存储到内存中：

1. **缓存存储**：Varnish 将响应存储到内存中的缓存结构中，以便后续快速访问。
2. **缓存失效策略**：Varnish 支持多种缓存失效策略，如基于时间的 TTL（Time-To-Live）、基于内容的 ETag/Last-Modified 等。
3. **缓存清理**：Varnish 会根据配置的缓存策略，定期清理过期的缓存条目，以节省内存空间。

## 返回响应给客户端

无论是从缓存中获取的响应还是从后端服务器获取的响应，Varnish 都会将其返回给客户端：

1. **响应发送**：Varnish 将 HTTP 响应发送回客户端。
2. **日志记录**：Varnish 记录相关的日志信息，以便后续的监控和分析。

# VCL 配置详解

VCL（Varnish Configuration Language）是 Varnish 的配置语言，语法简单，功能强大，类似于 C 和 Perl。主要用于配置如何处理请求和内容的缓存策略，同时允许管理员自定义缓存策略和请求处理逻辑。

VCL 在执行时会被编译转换成二进制代码。一个标准的 VCL 配置文件一般由三部分构成

- 后端服务器配置信息：包括 Backend，Director 等
- 资源访问限制配置信息：acl 信息
- 请求流程配置控制：sub 子程序信息

## VCL 基本语法

**用花括号做界定符，使用分号表示声明结束。注释用 //, #, /* */**：
- 花括号 `{}` 用于界定代码块。
- 分号 `;` 用于结束语句。
- 注释可以使用 `//`（单行注释）、`#`（单行注释）或 `/* */`（多行注释）。

**赋值(=), 比较(==), 和一些布尔值(!, &&, ||), !(取反)等类似C语法**：
- 赋值使用 `=`。
- 比较使用 `==`。
- 布尔操作符包括 `!`（取反）、`&&`（逻辑与）、`||`（逻辑或）。

**支持正则表达式, ACL匹配使用 ~ 操作**：
- 正则表达式和 ACL（访问控制列表）匹配使用 `~` 操作符。

**不同于C的地方，反斜杠(\)在VCL中没有特殊的含义。只是用来匹配URLs**：
- 在 VCL 中，反斜杠 `\` 没有特殊含义，仅用于匹配 URLs。

**VCL没有用户定义的变量，只能给backend, request, document这些对象的变量赋值。大部分是手工输入的，而且给这些变量分配值的时候，必须有一个VCL兼容的单位**：
- VCL 不支持用户定义的变量，变量的赋值需要针对特定对象（如 backend, request, document）。

**VCL有if, 但是没有循环**：
- VCL 支持 `if` 条件语句，但不支持循环结构。

**可以使用set来给request的header添加值, unset, 或 remove 来删除某个header**：
- 使用 `set` 可以给请求的 header 添加值。
- 使用 `unset` 或 `remove` 可以删除某个 header。

### 代码块与语句结束

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

### 注释

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

### 变量与赋值

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

### 条件语句

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

### 正则表达式与 ACL 匹配

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

### 函数调用

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

### 循环结构

- **不支持循环**：VCL 不支持循环结构（如 `for`, `while`），只能使用条件语句和函数调用来处理逻辑。

### 变量作用域

- **子程序内变量**：变量在子程序内有效，子程序之间不共享变量。
  
  ```vcl
  sub vcl_recv {
      set req.http.X-Custom-Header = "CustomValue";
  }
  
  sub vcl_deliver {
      // 无法访问 req.http.X-Custom-Header
  }
  ```

### 子程序定义

- **子程序名称**：常见的子程序名称包括 `vcl_recv`, `vcl_hash`, `vcl_pass`, `vcl_purge`, `vcl_backend_fetch`, `vcl_backend_response`, `vcl_deliver`, `vcl_fini` 等。

  ```vcl
  sub vcl_recv {
      // 处理接收到的请求
  }
  
  sub vcl_backend_response {
      // 处理从后端服务器接收到的响应
  }
  ```

### 导入其他 VCL 文件

- **import 语句**：使用 `import` 关键字导入其他 VCL 文件或标准库。
  
  ```vcl
  import std;
  
  sub vcl_recv {
      std.log("Request received");
  }
  ```

### 示例：完整的 VCL 配置

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

### 内置变量

VCL 提供了丰富的内置变量和操作符，用于访问和修改请求、响应和缓存相关的数据。

- `req.*`：客户端请求相关变量，如 `req.url`, `req.http.User-Agent` 等。
- `beresp.*`：后端响应相关变量，如 `beresp.status`, `beresp.http.Content-Type` 等。
- `obj.*`：缓存对象相关变量。
- `now`：当前时间。

### 操作符

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

### 函数

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

## 配置文件示例

以下是一个完整的 VCL（Varnish Configuration Language）配置文件示例：

```vcl
vcl 4.0;

# 导入默认的 VCL 规则
import std;

# 定义后端服务器配置信息
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

## 子程序

在 VCL 中，一个子程序就是一串代码，子程序没有参数，也没有返回值。

### 子程序语法

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

### 子程序的调用

调用一个子程序，使用call关键字，后接子程序名

例

```vcl
call pipe_if_local;
```

### 子程序的调用流程

有很多默认子程序与 Varnish 的工作流程相关，这些子程序会检查和操作 HTTP 头文件和各种请求，决定哪些请求被重用。如果这些子程序没有被定义（没有在VCL中配置），或者没有完成预定的处理而被终止，控制权将被转交给系统默认的子程序（调用系统默认的子程序的实现）。

VCL 文件可以包含多个子程序，每个子程序在请求的不同的生命周期阶段执行。以下是常见的子程序及其执行顺序

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


**子程序的调用流程**：

当 Varnish 接收到一个请求时，执行流程大致如下：

1. **vcl_recv**：处理客户端请求，决定是否缓存、修改请求头等。
2. **vcl_hash**：计算缓存键，用于后续缓存查找。
3. **vcl_pass**（如果需要绕过缓存）：处理需要传递到后端的请求。
4. **vcl_backend_fetch**：向后端服务器发起请求。
5. **vcl_backend_response**：处理后端响应，决定是否缓存等。Varnish 3 及更早版本为 vcl_fetch 这个名称
6. **vcl_deliver**：向客户端发送响应。

如果在任意阶段调用了 `return` 语句，VCL 会跳转到相应的子程序或终止执行。

### request 请求流程

![image-20250106204518408](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202501062045901.png)

这张图展示了VCL（Varnish Configuration Language）的请求处理流程。

1. **vcl_recv**：这是请求处理的起点。Varnish接收客户端的请求。
   - 如果请求需要查找缓存，则进入`vcl_hash`。
   - 如果请求需要直接pipe（管道传输），则进入`vcl_pipe`。

2. **vcl_hash**：在这个阶段，Varnish会根据请求的URL和其他一些因素生成一个哈希值，用于查找缓存对象。
   - 完成后，进入判断缓存是否存在的阶段。

3. **判断缓存是否存在**：
   - 如果缓存存在（yes），则进入`vcl_hit`。
   - 如果缓存不存在（no），则进入`vcl_miss`。

4. **vcl_hit**：缓存命中的处理流程。
   - 可以选择直接传递（pass）对象，进入`vcl_pass`。
   - 或者直接传递对象，进入`vcl_deliver`。

5. **vcl_miss**：缓存未命中的处理流程。
   - 可以选择直接传递（pass）对象，进入`vcl_pass`。
   - 或者从后端服务器获取对象，进入`vcl_fetch`。

6. **vcl_pass**：处理需要传递的对象。
   - 最终会进入`vcl_fetch`从后端服务器获取对象。

7. **vcl_fetch**：从后端服务器获取对象。
   - 获取对象后，可以选择缓存对象（Cache），进入`vcl_deliver`。
   - 或者不缓存对象（Don't cache），直接进入`vcl_deliver`。

8. **vcl_deliver**：将对象交付给客户端。
   - 完成请求的处理流程。

9. **vcl_pipe**：处理需要管道传输的请求。
   - 直到连接关闭，一直保持管道传输。

### Actions 动作

在 VCL（Varnish Configuration Language）中，动作（Actions）是用于控制 HTTP 请求和响应处理流程的关键指令。

#### pass

`pass` 是 VCL（Varnish Configuration Language）中用于控制 HTTP 请求处理流程的一个关键动作。它的主要功能是跳过缓存机制，直接将请求传递给后端服务器，并且不缓存后端服务器的响应。换句话说，`pass` 动作使得请求和响应绕过 Varnish 的缓存，直接进行客户端和服务器之间的传输。

##### 使用位置

一般来说 pass 动作放在 vcl_recv 和 vcl_backend_fetch 子程序中。

**vcl_recv**

`vcl_recv` 是 VCL 中最先处理的子程序之一，用于接收和处理客户端的 HTTP 请求。在这个阶段使用 `pass` 动作有以下几个原因：

1. **早期决策**：
   - 在 `vcl_recv` 中尽早决定是否需要跳过缓存，可以避免不必要的缓存查找和处理，提高效率。如果确定某个请求需要绕过缓存，那么尽早返回 `pass` 可以减少后续的处理步骤。

   ```vcl
   sub vcl_recv {
       if (req.url ~ "^/admin") {
           return (pass);
       }
   }
   ```

2. **减少负载**：
   - 通过在 `vcl_recv` 中使用 `pass`，可以避免后续子程序中进行不必要的缓存检查和可能的缓存存储操作，从而减轻 Varnish 和后端服务器的负载。

3. **精确控制**：
   - 在 `vcl_recv` 中使用 `pass` 可以根据请求的详细信息（如 URL、客户端 IP、请求方法等）进行精确控制，确保只有特定的请求绕过缓存。

**vcl_backend_fetch**

`vcl_backend_fetch` 在从客户端接收请求后，准备向后台服务器发起请求时调用。在这个阶段使用 `pass` 动作有以下原因：

1. **确保传递**：
   - 尽管在 `vcl_recv` 中已经决定使用 `pass`，但在 `vcl_backend_fetch` 中再次确认可以确保请求确实绕过缓存，直接传递到后端服务器。这增加了安全性和可靠性，避免缓存中的任何干扰。

   ```vcl
   sub vcl_backend_fetch {
       if (bereq.url ~ "^/admin") {
           return (pass);
       }
   }
   ```

2. **处理复杂逻辑**：
   - 有些复杂的逻辑可能需要在请求传递给后端服务器前进行处理。在 `vcl_backend_fetch` 中使用 `pass` 可以确保这些逻辑被正确执行，且请求不经过缓存。

3. **调试和日志记录**：
   - 在 `vcl_backend_fetch` 中使用 `pass` 可以帮助进行调试和日志记录，确保请求按照预期绕过缓存，直接传递到后端服务器，便于监控和分析。

##### 使用场景

1. **敏感请求**：
   - 对于涉及敏感信息的请求，比如登录信息、个人数据等，可能不希望这些请求的数据被缓存。这时可以使用 `pass` 动作确保数据直接从后端服务器获取，并直接返回给客户端。
   
   ```vcl
   if (req.url ~ "^/login") {
       return (pass);
   }
   ```

2. **动态内容**：
   - 有些动态生成的内容每次请求可能都会有所不同，这些内容不适合缓存。使用 `pass` 可以确保每次请求都获取最新的数据。
   
   ```vcl
   if (req.url ~ "^/dynamic-data") {
       return (pass);
   }
   ```

3. **特定客户端请求**：
   - 可以针对特定客户端的请求使用 `pass`，例如来自特定 IP 地址的请求，以确保这些请求绕过缓存。
   
   ```vcl
   if (client.ip ~ local_network) {
       return (pass);
   }
   ```

在 VCL 配置文件中，`pass` 动作通常在 `vcl_recv` 子程序中使用。以下是一个典型的例子：

```vcl
sub vcl_recv {
    # 如果请求的URL以 /admin 开头，则使用 pass 动作
    if (req.url ~ "^/admin") {
        return (pass);
    }
}
```

在这个例子中，所有以 `/admin` 开头的请求都会被直接传递给后端服务器，并且不会进行缓存。

##### pass vs cache/pipe

1. **`pass` vs `cache`**：
   - `pass` 会绕过缓存，直接与后端服务器通信，并且不缓存响应。而 `cache`（默认行为）会将响应存储在缓存中，供后续请求使用。
   
2. **`pass` vs `pipe`**：
   - `pipe` 也会绕过缓存，但它的作用是建立一个客户端和服务器之间的管道连接，不进行任何处理。而 `pass` 仍然会进行一些请求头的处理和检查。
   
   ```vcl
   if (req.http.Upgrade ~ "websocket") {
       return (pipe);
   }
   ```

- **性能影响**：使用 `pass` 会增加后端服务器的负载，因为每个 `pass` 请求都需要直接与后端服务器通信，无法利用缓存的优势。因此，应谨慎使用 `pass`，仅在必要时采用。
- **安全性**：确保 `pass` 动作不会绕过必要的安全检查，比如身份验证和授权，否则可能导致安全漏洞。

#### lookup

`lookup` 是 VCL（Varnish Configuration Language）中一个非常重要的动作，用于在缓存中查找请求对应的对象。如果找到匹配的对象，则返回缓存内容，否则将被设置为 pass，且将请求传递给后端服务器。不能在 vcl_backend_fetch 中使用，vcl_backend_fetch 用于在将请求发送到后端服务器之前，对请求进行最后的修改，在这个阶段，请求已经确定需要由后端服务器处理，缓存查找已经完成，因此 lookup 没有意义。

**功能与用途**

1. **缓存查找**：
   - `lookup` 主要用于在 Varnish 的缓存中查找请求对应的对象。如果找到匹配的对象（即缓存命中），则返回缓存的内容。这可以极大地提高响应速度，减少后端服务器的负载。

2. **缓存未命中的处理**：
   - 如果缓存中没有匹配的对象（即缓存未命中），`lookup` 会将请求传递给后端服务器，获取最新的内容。然后，这个内容可以被缓存起来，供后续请求使用。

**使用场景**

- **加速内容交付**：通过在缓存中查找已有的内容，可以大大减少响应时间，提高用户体验。
- **减轻后端服务器压力**：缓存命中可以直接返回内容，减少对后端服务器的请求次数，降低服务器负载。
- **提高可扩展性**：有效的缓存策略使得系统能够处理更多的并发请求，提高整体的可扩展性。


以下是一个简单的示例，展示如何在 VCL 中使用 `lookup` 动作：

```vcl
sub vcl_recv {
    # 设置哈希值，用于缓存查找
    set req.hash = req.url;

    # 执行缓存查找
    return (lookup);
}

sub vcl_hit {
    # 缓存命中，返回缓存内容
    return (deliver);
}

sub vcl_miss {
    # 缓存未命中，继续请求后端服务器
    return (fetch);
}

sub vcl_backend_response {
    # 将从后端服务器获取的内容存入缓存
    return (deliver);
}
```

**详细流程**

1. **`vcl_recv`**：
   - 在请求接收阶段，设置请求的哈希值（通常是 URL），然后调用 `lookup` 动作进行缓存查找。

2. **`vcl_hit`**：
   - 如果缓存命中（即找到了匹配的对象），则执行 `deliver` 动作，将缓存的内容返回给客户端。

3. **`vcl_miss`**：
   - 如果缓存未命中，则继续请求后端服务器，获取最新的内容。

4. **`vcl_backend_response`**：
   - 从后端服务器获取的内容可以通过 `deliver` 动作存入缓存，供后续请求使用。

- **哈希值的设置**：正确设置请求的哈希值非常重要，它决定了缓存查找的匹配规则。通常使用请求的 URL，但也可以根据需要使用其他请求参数。
- **缓存策略**：需要根据具体的应用场景制定合理的缓存策略，避免缓存雪崩、缓存穿透等问题。
- **缓存失效**：合理设置缓存失效时间，确保缓存中的数据不过期，同时又能及时更新。

#### pipe

`pipe` 动作在 VCL（Varnish Configuration Language）中主要用于处理特定类型的流量，特别是那些不适合缓存的请求。与 pass 类似，都需要访问后端服务器，不过当进入 pipe 后，此链接在未关闭前，后续所有的请求都直接发往后端服务器，不经过 Varnish 的处理。

**功能与用途**

1. **建立管道连接**：
   - 当 Varnish 遇到需要使用 `pipe` 动作的请求时，它会在客户端和后端服务器之间建立一个直接的管道连接。这意味着请求和响应数据将直接在客户端和服务器之间传输，Varnish 不再干预或缓存这些数据。

2. **适用于非 HTTP 流量**：
   - `pipe` 常用于处理非 HTTP 协议的流量，例如 WebSocket 连接、SSH、FTP 等。这些协议的流量通常不适合缓存，因为它们可能包含动态生成的内容或需要保持长时间的连接。

3. **绕过缓存**：
   - 对于某些特定的 HTTP 请求，如果希望完全绕过 Varnish 的缓存机制，也可以使用 `pipe`。例如，对于需要实时更新的数据或需要保持会话一致性的请求，`pipe` 是一个理想的选择。

**使用场景**

1. **WebSocket 连接**：
   - WebSocket 连接需要保持长时间的通信通道，并且数据是双向实时传输的。这种情况下，`pipe` 可以确保连接不被 Varnish 缓存，从而保持通信的实时性。
     ```vcl
     if (req.http.Upgrade ~ "websocket") {
         return (pipe);
     }
     ```

2. **文件下载**：
   - 对于非常大的文件下载，如果不想让这些文件占用 Varnish 的缓存空间，可以使用 `pipe` 让文件直接从后端服务器传输到客户端。
     ```vcl
     if (req.url ~ "^/download/") {
         return (pipe);
     }
     ```

3. **实时数据流**：
   - 对于实时数据流，如股票行情、实时日志等，需要保证数据的实时性和连续性，这时可以使用 `pipe`。
     ```vcl
     if (req.url ~ "^/realtime/") {
         return (pipe);
     }
     ```

**实现细节**

- **连接保持**：
  - 使用 `pipe` 后，Varnish 会保持客户端和后端服务器之间的连接，直到连接关闭。这意味着 Varnish 不再对传输的数据进行任何处理或修改。

- **性能考虑**：
  - 虽然 `pipe` 可以绕过缓存，但对于高并发的场景，需要谨慎使用，因为每个 `pipe` 连接都会占用一定的系统资源。如果有大量的 `pipe` 连接，可能会影响 Varnish 的整体性能。

- **日志记录**：
  - 由于 `pipe` 会直接传输数据，Varnish 不会记录这些请求的详细信息。因此，在使用 `pipe` 时，日志记录会有所欠缺，需要通过其他方式监控和分析流量。

以下是一个完整的 VCL 示例，展示如何使用 `pipe` 动作处理 WebSocket 连接：

```vcl
sub vcl_recv {
    if (req.http.Upgrade ~ "websocket") {
        return (pipe);
    }
}

sub vcl_pipe {
    # 保持连接打开，直到客户端或服务器关闭
    return (pipe);
}
```

#### deliver 

在 VCL（Varnish Configuration Language）中，`deliver` 用于将缓存中的对象交付给客户端。

**功能与用途**

- **交付缓存对象**：`deliver` 动作的主要功能是将缓存中找到的对象交付给客户端。这意味着 Varnish 已经成功地从缓存中检索到了请求的资源，并将这个资源返回给用户，而不需要向后端服务器再次发起请求。
  
- **终止处理流程**：当 `deliver` 被调用时，它会终止当前的 VCL 子程序，并将控制权返回给 Varnish，表明缓存对象已经准备好发送给客户端。

**使用场景**

1. **缓存命中**：
   - 当 Varnish 在缓存中找到了请求的资源时，可以使用 `deliver` 将该资源直接返回给客户端，提高响应速度，减少后端服务器的压力。
   - **示例**：
     ```vcl
     sub vcl_recv {
         if (req.url ~ "^/cached/") {
             return (lookup);
         }
     }
     
     sub vcl_hit {
         return (deliver);
     }
     ```

2. **自定义缓存策略**：
   - 通过在不同的子程序中调用 `deliver`，可以实现复杂的缓存策略。例如，可以在特定的条件下决定是否缓存某些请求，或者在缓存命中时执行额外的逻辑。
   - **示例**：
     ```vcl
     sub vcl_backend_response {
         if (beresp.http.X-Cache-Action == "cache") {
             set beresp.uncacheable = false;
         } else {
             set beresp.uncacheable = true;
         }
     }
     
     sub vcl_deliver {
         if (obj.uncacheable) {
             return (deliver);
         }
     }
     ```

**配合其他动作**

- **`deliver` 通常与 `lookup` 动作一起使用**：
  - 当 Varnish 需要在缓存中查找请求的资源时，会调用 `lookup`。如果在缓存中找到匹配的对象，则会进入 `vcl_hit` 子程序，在那里调用 `deliver` 将对象交付给客户端。
  - **示例**：
    ```vcl
    sub vcl_recv {
        return (lookup);
    }
    
    sub vcl_hit {
        return (deliver);
    }
    ```

**注意事项**

- **确保缓存对象的有效性**：
  - 在调用 `deliver` 之前，确保缓存中的对象是有效的和最新的。否则，可能会向客户端返回过期的或不正确的内容。
  
- **处理不可缓存的请求**：
  - 对于标记为不可缓存的请求，不要调用 `deliver`，而是应该将请求传递给后端服务器处理。

以下是一个更完整的 VCL 配置示例，展示如何使用 `deliver` 动作来处理缓存命中：

```vcl
sub vcl_recv {
    if (req.url ~ "^/cached/") {
        return (lookup);
    }
}

sub vcl_hit {
    return (deliver);
}

sub vcl_miss {
    return (fetch);
}

sub vcl_backend_response {
    if (beresp.http.Content-Type ~ "text/html") {
        set beresp.ttl = 300s;
    }
}

sub vcl_deliver {
    if (obj.hits > 0) {
        set resp.http.X-Cache = "HIT";
    } else {
        set resp.http.X-Cache = "MISS";
    }
}
```

在这个示例中：
- 请求被 `vcl_recv` 接收，如果 URL 匹配特定模式，则进行缓存查找。
- 如果缓存命中，进入 `vcl_hit` 子程序并调用 `deliver` 将缓存对象交付给客户端。
- 如果缓存未命中，则从后端服务器获取资源。
- 在 `vcl_deliver` 中，根据缓存命中情况设置自定义 HTTP 头。

#### hit_for_pass

`hit_for_pass` 是一个在 VCL（Varnish Configuration Language）中主要用于处理那些不应该被缓存的对象，或者缓存无效的对象。

**功能与用途**

`hit_for_pass` 的主要功能是标记一个请求，使得未来的相似请求也不再尝试从缓存中获取，而是直接传递给后端服务器。它通常用于处理那些无法缓存或不应该缓存的响应，比如涉及到个人化内容、经常变化的内容或者后端服务器返回某些特定状态码的响应。

**使用场景**

1. **动态内容**：对于一些动态生成的内容，比如用户特定的推荐、广告等，这些内容每次请求都可能不同，因此不适合缓存。
   
2. **认证和授权**：涉及到用户认证和授权的请求，通常需要每次都验证，这类请求不适合缓存。

3. **错误响应**：某些情况下，后端服务器可能会返回一些临时的错误响应（如 503 Service Unavailable），这些响应不适合缓存，因为它们不代表最终状态。

以下是一个使用 `hit_for_pass` 的示例：

```vcl
sub vcl_backend_response {
    # 如果后端返回的状态码是 500 或 503，则使用 hit_for_pass
    if (beresp.status == 500 || beresp.status == 503) {
        set beresp.uncacheable = true;
        return (hit_for_pass);
    }

    # 对于某些特定的内容类型，不缓存
    if (beresp.http.Content-Type ~ "text/event-stream") {
        set beresp.uncacheable = true;
        return (hit_for_pass);
    }
}
```

在这个示例中，我们检查后端响应的状态码。如果状态码是 500 或 503，我们将其标记为不可缓存，并使用 `hit_for_pass` 动作。同样，对于 `text/event-stream` 类型的内容，我们也不进行缓存。

**工作原理**

当 `hit_for_pass` 被调用时，Varnish 会做以下几件事：

1. **标记对象为不可缓存**：将对象标记为不可缓存，这样未来的相似请求就不会尝试从缓存中获取。
   
2. **直接传递请求**：将请求直接传递给后端服务器，而不进行任何缓存相关的操作。

3. **设置 TTL**：设置一个特殊的 TTL（Time-To-Live），确保这个对象在缓存中有一个短暂的存活时间，但不会被重用。如果没有 TTL，每次这样的请求都会直接打到后端服务器，可能会造成后端服务器的压力过大，导致雪崩效应。设置 TTL 可以确保在短时间内，相同的请求不会重复发往后端，而是由 Varnish 直接返回一个标记，告诉其他请求同样的结果。可以确保系统在一段时间内不会重复处理相同的请求，同时也避免缓存中充斥着大量无用的数据。

**注意事项**

- **性能影响**：频繁使用 `hit_for_pass` 可能影响性能，因为它会增加后端服务器的负载，因为每个请求都需要重新处理。
  
- **合理使用**：应该根据具体的业务需求合理使用 `hit_for_pass`，避免不必要的缓存失效。

#### fetch

在 VCL（Varnish Configuration Language）中，`fetch` 动作用于从后端服务器获取请求的对象，并在缓存中存储它。虽然 `fetch` 不是一个直接的 VCL 动作，但它是在 VCL 的处理流程中非常重要的一个步骤，通常在 `vcl_backend_fetch` 和 `vcl_backend_response` 子程序中使用。

**功能与用途**

1. **从后端服务器获取内容**：
   - 当 Varnish 需要获取客户端请求的内容时，它会向后端服务器发送请求，并使用 `fetch` 动作来获取响应内容。这个过程涉及网络请求和数据传输。

2. **缓存对象**：
   - 获取到的内容会被存储在缓存中，以便将来相同的请求可以直接从缓存中获取，而不需要再次访问后端服务器。这大大提高了响应速度，降低了后端服务器的负载。

**使用场景**

- **首次请求**：当客户端请求一个尚未缓存的对象时，Varnish 会使用 `fetch` 从后端服务器获取该对象，并将其存储在缓存中。
- **缓存过期**：如果缓存中的对象已经过期，Varnish 会使用 `fetch` 从后端服务器获取最新的内容，并更新缓存。
- **条件请求**：在某些情况下，Varnish 可能会发送条件请求（例如基于 `If-Modified-Since` 或 `If-None-Match` 头部），如果后端服务器返回新的内容，则使用 `fetch` 更新缓存。

以下是一个典型的 `vcl_backend_response` 子程序示例，展示了如何使用 `fetch` 动作：

```vcl
sub vcl_backend_response {
    # 检查响应状态
    if (beresp.status == 200) {
        # 设置缓存生存时间
        set beresp.ttl = 1h;
    } elseif (beresp.status == 404) {
        # 对于404响应，设置较短的缓存生存时间
        set beresp.ttl = 10m;
    }

    # 返回 fetch 以继续处理
    return (fetch);
}
```

**详细步骤**

1. **发送请求**：
   - Varnish 向后端服务器发送 HTTP 请求，这个请求包含了客户端的原始请求信息。

2. **接收响应**：
   - 后端服务器处理请求并返回 HTTP 响应。Varnish 接收这个响应，并进行进一步的处理。

3. **缓存存储**：
   - 根据 VCL 配置，Varnish 会决定是否将响应内容存储在缓存中。通常会根据响应状态、头部信息等来决定缓存策略。

4. **返回客户端**：
   - 最终，Varnish 将获取到的内容返回给客户端。如果将来有相同的请求，Varnish 可以直接从缓存中获取内容，提高响应速度。

**注意事项**

- **缓存策略**：合理设置缓存策略非常重要，既要保证数据的时效性，又要最大化缓存命中率。
- **错误处理**：在后端服务器返回错误时，需要根据具体情况决定是否进行重试或返回错误信息给客户端。
- **性能优化**：尽量减少不必要的网络请求，通过合理的缓存策略和 `fetch` 动作，可以显著提高系统性能。

#### hash 

进入 hash 模式

#### restart 

在 VCL（Varnish Configuration Language）中，`restart` 是一个特殊的动作，用于重新开始 VCL 程序的处理流程。当执行 `restart` 时，当前的请求处理会被终止，并重新从头开始处理。这在某些需要重新评估请求或处理流程的场景中非常有用。

**使用场景**

1. **重新评估请求**：
   - 在某些复杂的逻辑判断中，可能需要根据后续处理的结果重新评估请求。例如，最初的判断条件可能不充分，需要更多信息来做出最终决定。

2. **修正请求**：
   - 如果在处理过程中发现请求需要修改，可以使用 `restart` 来重新开始处理，以便应用新的修改。

3. **错误处理**：
   - 在某些错误处理场景中，可能需要重新尝试处理请求，特别是在后端服务器暂时不可用或返回错误时。

以下是一个使用 `restart` 的示例，展示了如何在特定条件下重新开始请求处理：

```vcl
sub vcl_recv {
    if (req.http.x-custom-header) {
        # 如果自定义头部存在，重新开始处理流程
        return (restart);
    }
    # 其他处理逻辑
}

sub vcl_restart {
    # 在重新开始处理之前，可以在这里执行一些初始化操作
    set req.http.X-Restarted = "true";
}
```

**注意事项**

1. **避免无限循环**：
   - 使用 `restart` 时需要小心，确保不会导致无限循环。可以通过设置某些标志或限制重启次数来避免这种情况。

2. **性能影响**：
   - 频繁使用 `restart` 可能影响性能，因为每次重启都会重新开始请求处理流程。因此，应谨慎使用，确保只在必要时使用。

3. **初始化操作**：
   - 在 `vcl_restart` 子程序中，可以执行一些初始化操作，以确保重新开始处理时一切正常。

#### ok

表示正常

#### error

表示错误

















5. **retry**：
   - **用途**：重新尝试请求。常用于后端服务器暂时不可用或返回错误时。
   - **示例**：
     ```vcl
     if (beresp.status == 503) {
         return (retry);
     }
     ```

6. **purge**：
   - **用途**：从缓存中删除指定的对象。常用于需要立即更新缓存的场景。
   - **示例**：
     ```vcl
     if (req.method == "PURGE") {
         return (purge);
     }
     ```

7. **synth**：
   - **用途**：生成一个合成响应，而不是从后端服务器获取。常用于返回自定义错误信息或重定向。
   - **示例**：
     ```vcl
     if (req.url ~ "^/forbidden") {
         return (synth(403, "Forbidden"));
     }
     ```

8. **return**：
   - **用途**：返回一个状态，终止当前子程序并返回控制权给调用者。可以用来提前结束处理流程。
   - **示例**：
     ```vcl
     if (req.http.Cookie ~ "logged_in=yes") {
         return (pass);
     }
     ```

- **缓存控制**：通过 `pass`、`pipe` 和 `hit_for_pass` 等动作，可以精细控制哪些请求需要绕过缓存，直接访问后端服务器。
- **错误处理**：使用 `synth` 可以生成自定义的错误响应，提升用户体验。
- **缓存更新**：通过 `purge` 可以即时删除缓存中的旧数据，确保返回给用户的是最新的内容。
- **重试机制**：通过 `retry` 可以处理临时性错误，提高系统的健壮性。

### vcl_init 和 vcl_fini

**作用**
`vcl_init` 和 `vcl_fini` 函数用于在 VCL 加载和卸载时执行初始化和清理操作。

**主要功能**
- **初始化资源**：例如，连接数据库、初始化变量等。
- **清理资源**：例如，关闭连接、释放资源等。

**示例**
```vcl
sub vcl_init {
    # 初始化后端连接
    new my_backend = backend("my_backend", "127.0.0.1", 8080);
}

sub vcl_fini {
    # 清理资源
    my_backend.close();
}
```

#### vcl_init

`vcl_init` 是一个非常重要的 VCL 子程序，它在 VCL 加载时被调用，通常用于初始化操作，尤其是与 VMOD（Varnish 模块）相关的初始化。

1. **初始化 VMOD 模块**：
   - `vcl_init` 最常见的用途是初始化 VMOD 模块。VMOD 是 Varnish 的模块扩展，允许开发者扩展 Varnish 的功能。通过在 `vcl_init` 中调用 VMOD 的初始化函数，可以确保这些模块在 Varnish 启动时正确加载和配置。

2. **设置全局变量**：
   - 在 `vcl_init` 中，可以设置一些全局变量，这些变量可以在整个 VCL 配置中访问和使用。这对于需要在多个子程序中共享的数据非常有用。

3. **配置初始化**：
   - 除了 VMOD，`vcl_init` 也可以用于其他初始化任务，比如配置参数、日志系统等。它提供了一个集中位置来执行所有需要在 Varnish 启动时完成的设置操作。

以下是一个简单的示例，展示如何在 `vcl_init` 中初始化一个 VMOD 模块：

```vcl
import example_module;

sub vcl_init {
    # 初始化 example_module
    new example_obj = example_module.init();

    # 设置全局变量
    set req.http.X-Powered-By = "Varnish " + varnish.version;

    # 其他初始化操作
    call setup_logging;
}

sub setup_logging {
    # 这里可以添加日志系统的初始化代码
}
```

- `vcl_init` 在 VCL 配置文件加载时被调用，且在加载任何客户请求之前执行。这意味着在 `vcl_init` 中进行的任何设置都会在处理任何实际请求之前完成，确保所有模块和配置都已正确初始化。

- `vcl_init` 应该返回一个状态值（实际上不是返回值，而是动作 Actions），通常是 `ok`，表示初始化成功。如果初始化失败，可以返回一个错误状态，这将阻止 Varnish 启动，并记录相应的错误信息。

- 在 `vcl_init` 中执行的任何操作都应该是快速且非阻塞的，因为初始化过程会直接影响 Varnish 的启动时间。
- 尽量避免在 `vcl_init` 中执行复杂或耗时的操作，比如网络请求或大规模数据处理。

### vcl_recv

`vcl_recv` 函数在 Varnish 接收到一个 HTTP 请求后立即被调用。它主要用于决定是否处理该请求、是否从缓存中提供响应、是否进行重定向或是否传递请求到后端服务器。一般以 `error code [reason]`、`return (pass)`,`return (pipe)`,`return (lookup)` 结束

1. **决定是否处理请求**：根据请求的某些条件，决定是否继续处理该请求。
2. **缓存控制**：指定是否从缓存中提供响应，或者是否需要从后端服务器获取新内容。
3. **请求重定向**：根据需要进行 URL 重定向。
4. **请求传递**：将请求直接传递到后端服务器，而不经过缓存。
5. **修改请求**：在将请求传递到后端之前，可以修改请求的头部信息或其他属性。

`vcl_recv` 函数通常以以下几种方式结束：

- **`error code [reason]`**：返回一个错误响应给客户端。例如，`error 404 "Not Found"`。
- **`return (pass)`**：指示 Varnish 不缓存该请求的响应，并直接将其传递到后端服务器。
- **`return (pipe)`**：将请求置于管道模式，Varnish 不再干预后续的数据传输，直接在客户端和后端服务器之间传递数据。
- **`return (lookup)`**：指示 Varnish 从缓存中查找响应。如果缓存命中，则返回缓存的响应；否则，继续传递请求到后端服务器。
- **`return (hash)`**：根据请求生成哈希值，用于缓存查找（Varnish 4 及以上版本）。
- **`return (synth(status, reason))`**：生成一个合成响应（synthetic response），例如重定向或自定义错误页面。

以下是一个示例 `vcl_recv` 配置，展示了如何根据不同的条件处理请求：

```vcl
sub vcl_recv {
    # 移除跟踪参数以提高缓存命中率
    if (req.url ~ "(\?|&)(utm_source|utm_medium|utm_campaign|gclid|cx|ie)=([^&]+)") {
        set req.url = regsuball(req.url, "((\?)?|&)(utm_source|utm_medium|utm_campaign|gclid|cx|ie)=([^&]+)", "\1");
        set req.url = regsub(req.url, "(\?|&)$", "");
    }

    # 处理带有特定 Cookie 的请求，不缓存
    if (req.http.Cookie ~ "user_logged_in") {
        return (pass);
    }

    # 强制 HTTPS
    if (req.http.X-Forwarded-Proto !~ "(?i)https") {
        return (synth(301, "https://" + req.http.Host + req.url));
    }

    # 缓存所有 GET 和 HEAD 请求
    if (req.method != "GET" && req.method != "HEAD") {
        return (pass);
    }

    # 允许缓存静态内容
    if (req.url ~ "\.(css|js|png|gif|jpg|jpeg|ico|svg|woff|woff2|ttf|eot)$") {
        return (lookup);
    }

    # 默认行为：查找缓存
    return (lookup);
}
```

1. **移除跟踪参数**：通过正则表达式匹配并移除 URL 中的跟踪参数（如 `utm_source`），以提高缓存命中率。
2. **处理特定 Cookie**：如果请求中包含 `user_logged_in` 的 Cookie，则不缓存该请求，直接传递到后端服务器。
3. **强制 HTTPS**：如果请求不是通过 HTTPS 发起的，则返回 301 重定向响应，强制使用 HTTPS。
4. **缓存控制**：
   - 对于非 GET 和 HEAD 方法的请求，不缓存，直接传递到后端服务器。
   - 对于静态内容的请求（如 CSS、JS、图片等），尝试从缓存中查找响应。
   - 其他请求默认尝试从缓存中查找响应。

**常见使用场景**

- **缓存策略**：根据 URL 路径、请求方法、Cookie 等条件决定是否缓存响应。
- **安全增强**：移除或修改请求头，防止安全漏洞，如 HTTP 头部注入。
- **性能优化**：通过移除不必要的请求参数或头信息，减少带宽使用和服务器负载。
- **重定向管理**：实现内部或外部的重定向规则。
- **负载均衡**：根据请求的属性，将请求分发到不同的后端服务器。

**注意事项**

- **VCL 版本差异**：不同版本的 Varnish 在 VCL 语法和功能上可能有所不同。例如，Varnish 4 引入了 `return (hash)`，而 Varnish 3 使用 `return (lookup)`。
- **调试与测试**：在修改 `vcl_recv` 配置后，建议使用 Varnish 的调试工具或日志功能进行测试，以确保配置按预期工作。
- **性能影响**：复杂的 VCL 配置可能会影响 Varnish 的性能，特别是在高并发环境下。因此，应尽量保持配置简洁高效。

### vcl_pipe

`vcl_pipe` 是 Varnish 中的一个子程序，用于处理进入 `pipe` 模式的请求。在这个模式下，请求会被直接传递到后端服务器，而不进行任何缓存处理。

- **直接传递请求**：当请求进入 `pipe` 模式时，`vcl_pipe` 子程序被调用，Varnish 将请求直接传递给后端服务器。
- **透明传输数据**：在连接关闭之前，无论是客户端还是后端服务器的数据，都会通过 Varnish 进行透明传输。这意味着 Varnish 不干预数据的传输，只作为一个通道。
- **不适合缓存的请求**：适用于无法缓存的请求，例如长时间的流媒体数据或需要保持连接的请求。
- **保持连接**：当需要保持客户端和服务器之间的连接时，可以使用 `pipe` 模式。

`vcl_pipe` 子程序通常以下列关键字结束：
- **`error code [reason]`**：返回一个错误响应给客户端。例如，`error 502 "Bad Gateway"`。
- **`pipe`**：继续以 `pipe` 模式运行，保持客户端和后端服务器之间的数据传输通道。

以下是一个简单的 `vcl_pipe` 配置示例：

```vcl
sub vcl_pipe {
    # 在这里可以添加自定义逻辑，例如日志记录或请求修改
    return (pipe);
}
```

### vcl_pass

`vcl_pass` 是 Varnish 中的一个子程序，用于处理进入 `pass` 模式的请求。在这个模式下，请求会被直接传递到后端服务器，后端服务器的响应也会被直接传递给客户端，但不会被缓存。

- **直接传递请求**：当请求进入 `pass` 模式时，`vcl_pass` 子程序被调用，Varnish 将请求直接传递给后端服务器。
- **不缓存响应**：后端服务器的响应会被直接传递给客户端，但不会被 Varnish 缓存。
- **后续请求处理**：来自相同客户端的后续请求将按照正常的缓存策略进行处理，不会因为之前的 `pass` 请求而受到影响。

- **不适合缓存的请求**：适用于需要实时处理但不希望缓存的请求，例如需要动态生成内容的请求。
- **调试和测试**：在调试和测试过程中，可以使用 `pass` 模式来绕过缓存，直接查看后端服务器的响应。

`vcl_pass` 子程序通常以下列关键字结束：
- **`error code [reason]`**：返回一个错误响应给客户端。例如，`error 502 "Bad Gateway"`。
- **`pass`**：继续以 `pass` 模式运行，保持客户端和后端服务器之间的数据传输通道。
- **`restart`**：重启当前事务，增加重启计数器的计数。如果重启次数超过 `max_restarts`，Varnish 会发出一个错误。

以下是一个简单的 `vcl_pass` 配置示例：

```vcl
sub vcl_pass {
    # 在这里可以添加自定义逻辑，例如日志记录或请求修改
    return (pass);
}
```

### vcl_hash

**作用**
`vcl_hash` 函数用于定义如何为请求生成哈希键（hash key），这个哈希键用于在缓存中查找对应的对象。Varnish 使用哈希表来快速定位缓存中的对象。

**主要功能**
- **自定义哈希键**：通过添加或修改请求的组成部分来影响哈希键的生成。
- **影响缓存命中率**：通过调整哈希键，可以影响缓存的命中率。

**示例**
```vcl
sub vcl_hash {
    # 默认情况下，Varnish 使用请求的方法和 URL 作为哈希键
    hash_data(req.url);

    # 如果需要，可以根据请求头中的某些信息来调整哈希键
    if (req.http.host) {
        hash_data(req.http.host);
    }

    return (lookup);
}
```

### vcl_hit

**作用**
`vcl_hit` 函数在 Varnish 在缓存中成功找到与请求匹配的缓存对象时被调用。它用于决定如何处理已缓存的响应。

**主要功能**
- **决定是否使用缓存响应**：通过设置 `return (deliver)` 来提供缓存的响应，或 `return (miss)` 来强制重新获取响应。
- **修改缓存的响应**：例如，添加或修改响应头。

**示例**
```vcl
sub vcl_hit {
    # 如果缓存的响应仍然有效，则提供缓存的响应
    if (obj.ttl > 0s) {
        return (deliver);
    }

    # 如果缓存的响应已过期，则重新获取
    return (miss);
}
```

### vcl_miss

**作用**
`vcl_miss` 函数在 Varnish 在缓存中未找到与请求匹配的缓存对象时被调用。它用于决定如何处理未缓存的请求。

**主要功能**
- **决定是否从后端获取响应**：通过设置 `return (fetch)` 来从后端获取响应。
- **修改请求**：例如，添加或修改请求头。

**示例**
```vcl
sub vcl_miss {
    # 从后端获取响应
    return (fetch);
}
```

### vcl_backend_fetch

**作用**
`vcl_backend_fetch` 函数在 Varnish 准备从后端服务器获取响应时被调用。它用于修改发送到后端的请求。

**主要功能**
- **修改后端请求**：例如，添加或修改请求头。
- **决定是否重试请求**：如果后端请求失败，可以设置重试策略。

`vcl_backend_fetch` 是 VCL 的阶段，用于在将请求转发给后端服务器之前进行一些处理。在这个阶段，管理员可以：

- **修改后端请求**：如添加或修改请求头部信息。
- **决定是否转发请求**：如某些特定条件下，可以阻止请求转发给后端服务器。

**示例**
```vcl
sub vcl_backend_fetch {
    # 在发送到后端的请求中添加自定义头
    set bereq.http.X-Forwarded-For = client.ip;

    return (fetch);
}
```

### vcl_backend_response

**作用**
`vcl_backend_response` 函数在 Varnish 从后端服务器接收到响应时被调用。它用于修改从后端获取的响应。

**主要功能**
- **修改响应**：例如，添加或修改响应头。
- **决定是否缓存响应**：通过设置 `return (deliver)` 或 `return (hit_for_pass)` 来控制缓存行为。
- **处理错误响应**：例如，设置重定向或自定义错误页面。

**示例**
```vcl
sub vcl_backend_response {
    # 如果后端响应是 404，则设置自定义错误页面
    if (beresp.status == 404) {
        set beresp.status = 404;
        set beresp.http.Content-Type = "text/html";
        synthetic("<?xml version=\"1.0\" encoding=\"utf-8\"?><!DOCTYPE html><html><body><h1>404 Not Found</h1></body></html>");
        return (deliver);
    }

    # 缓存响应
    return (deliver);
}
```

`vcl_backend_response` 是 VCL 的阶段，用于处理从后端服务器接收到的响应。在这个阶段，管理员可以：

- **修改响应**：如添加或修改响应头部信息。
- **决定是否缓存响应**：如某些特定的响应不需要缓存，可以直接返回给客户端。

```vcl
sub vcl_backend_response {
    # 修改响应头部
    set beresp.http.X-Cache = "HIT";

    # 某些特定响应不缓存
    if (beresp.http.Content-Type ~ "text/html") {
        set beresp.ttl = 300;
    } else {
        set beresp.ttl = 86400;
    }
}
```

### vcl_deliver

**作用**
`vcl_deliver` 函数在 Varnish 准备将响应发送给客户端时被调用。它用于修改最终发送给客户端的响应。

**主要功能**
- **修改响应头**：例如，添加或修改响应头。
- **记录日志**：记录有关请求和响应的信息。
- **添加自定义内容**：例如，添加自定义的 HTTP 头或修改响应内容。

**示例**
```vcl
sub vcl_deliver {
    # 添加自定义响应头
    set resp.http.X-Cache = "HIT";

    # 如果响应是从缓存中提供的，则设置相应的头
    if (obj.hits > 0) {
        set resp.http.X-Cache-Hits = obj.hits;
    }

    return (deliver);
}
```

`vcl_deliver` 是 VCL 的阶段，用于在将响应返回给客户端之前进行一些处理。在这个阶段，管理员可以：

- **修改响应**：如添加或修改响应头部信息。
- **记录日志**：如记录缓存命中情况。

```vcl
sub vcl_deliver {
    # 添加自定义响应头部
    set resp.http.X-Cache = "HIT";

    # 记录日志
    if (obj.hits > 0) {
        set resp.http.X-Cache-Hits = obj.hits;
    }
}
```

### vcl_backend_error

**作用**
`vcl_backend_error` 函数在 Varnish 从后端服务器接收到错误响应时被调用。它用于处理后端错误。

**主要功能**
- **处理错误响应**：例如，设置重定向或自定义错误页面。
- **决定是否重试请求**：如果需要，可以设置重试策略。

**示例**
```vcl
sub vcl_backend_error {
    # 如果后端响应是 5xx 错误，则设置自定义错误页面
    if (beresp.status >= 500 && beresp.status <= 599) {
        set beresp.status = 503;
        set beresp.http.Content-Type = "text/html";
        synthetic("<?xml version=\"1.0\" encoding=\"utf-8\"?><!DOCTYPE html><html><body><h1>Service Unavailable</h1></body></html>");
        return (deliver);
    }

    return (deliver);
}
```

## 变量

由于子程序没有参数，子进程必须通过全局变量来处理。

### 内置数据结构

#### req
`req`对象是在Varnish接收到客户端的HTTP请求时创建的。它包含了请求的所有信息，比如请求的方法（GET、POST等）、请求的URL、请求的头信息等。在VCL的`vcl_recv`阶段，可以对`req`对象进行操作和修改，以决定如何处理请求。主要可以执行以下操作：
- 修改请求的头信息。
- 决定是否要缓存请求。
- 将请求重写或转发到其他后端。

例如，可以通过修改`req.http.User-Agent`来伪装请求的浏览器类型，或者通过`return (pass)`来跳过缓存，直接将请求传递到后端服务器。

#### beresp
`beresp`对象是在Varnish从后端服务器获取到响应后创建的。它包含了后端服务器返回的所有信息，比如响应的状态码、响应的头信息等。在VCL的`vcl_fetch`阶段，可以对`beresp`对象进行操作和修改，以决定如何缓存响应。主要可以执行以下操作：
- 修改响应的头信息。
- 设置响应的TTL（Time-To-Live）。
- 决定是否缓存响应。

例如，可以通过设置`beresp.ttl`来定义缓存的时间长度，或者通过`return (hit_for_pass)`来标记某些响应不需要缓存。

#### obj
`obj`对象表示已经被缓存的响应。它是只读的，意味着不能直接修改`obj`的内容，但可以读取其属性以决定如何处理缓存的响应。主要可以执行以下操作：
- 读取缓存响应的头信息。
- 检查缓存响应的TTL。
- 根据缓存响应的属性决定是否提供缓存的响应。

例如，可以通过检查`obj.ttl`来决定是否提供缓存的响应，或者通过读取`obj.http.Content-Type`来决定如何处理缓存的内容。

### 内置变量

#### 通用变量

- **now**：这是一个表示当前时间的变量。在VCL中，你可以使用这个变量来执行一些基于时间的操作，比如设置TTL（Time-To-Live）或者记录请求的时间戳。

#### 在backend中有效的变量
- **.host**：这个变量用于指定backend的主机名或者IP地址。通过设置这个变量，你可以定义Varnish应该连接到哪个后端服务器来获取内容。
  
  ```vcl
  backend default {
      .host = "127.0.0.1";
      .port = "8080";
  }
  ```

- **.port**：这个变量用于指定backend的服务名或者端口号。结合.host，你可以精确定义Varnish如何连接到后端服务器。

#### 在处理请求时有效的变量
- **client.ip**：这个变量包含了客户端的IP地址。通过这个变量，你可以根据客户端的地理位置或者IP地址段来做出不同的缓存策略。
  
  ```vcl
  if (client.ip ~ "192.168.0.0/16") {
      // 特定的缓存策略
  }
  ```

- **client.identity**：这个变量用于标识客户端，特别是在使用负载均衡的时候。通过这个变量，可以针对不同的客户端应用不同的策略。
  
  ```vcl
  set req.http.X-Client-Id = client.identity;
  ```

- **server.hostname**：这个变量包含了Varnish服务器的主机名。这对于日志记录或者调试非常有帮助，因为你可以通过它来识别是哪一台服务器处理了请求。
  
  ```vcl
  std.log("Handling request on " + server.hostname);
  ```

- **server.identity**：这个变量用于设置Varnish服务器的身份。如果没有通过-i参数传递给varnishd，server.identity将自动为varnishd实例设置名字。
  
  ```vcl
  set server.identity = "cache-01";
  ```

- **server.ip**：这个变量包含了客户端连接到的Varnish服务器的IP地址。这对于识别和处理特定的服务器实例非常有用。
  
  ```vcl
  std.log("Client connected to IP: " + server.ip);
  ```

- **server.port**：这个变量包含了客户端连接到的Varnish服务器的端口号。结合server.ip，你可以全面了解请求的入口点。
  
  ```vcl
  std.log("Client connected on port: " + server.port);
  ```

- **req.request**：这个变量表示HTTP请求的类型，比如“GET”，“HEAD”，“POST”等。你可以根据请求的类型来决定如何处理请求。
  
  ```vcl
  if (req.request == "GET") {
      // 处理GET请求
  }
  ```


req.proto
- **客户端的Http协议**：这个变量包含了客户端使用的HTTP协议版本，比如HTTP/1.0或者HTTP/1.1。通过这个变量，你可以根据不同的协议版本采取不同的处理策略。

  ```vcl
  if (req.proto ~ "HTTP/1.0") {
      // 针对HTTP/1.0请求的特殊处理
  }
  ```

req.url
- **请求的URL**：这个变量包含了客户端请求的完整URL。通过这个变量，可以对不同的URL应用不同的缓存策略。例如，可以针对特定的URL路径设置不同的TTL。

  ```vcl
  if (req.url ~ "^/static/") {
      set beresp.ttl = 1h;
  }
  ```

req.backend
- **使用哪个后端服务器为这个请求提供服务**：这个变量指定了应该使用哪个后端服务器来处理请求。可以根据不同的条件动态地选择后端服务器。

  ```vcl
  if (req.url ~ "^/api/") {
      set req.backend = api_backend;
  } else {
      set req.backend = default_backend;
  }
  ```

req.backend.healthy
- **后端服务器是否健康**：这个变量用来检查后端服务器的健康状态。如果后端服务器不健康，可以采取相应的措施，比如使用备用服务器。

  ```vcl
  if (!req.backend.healthy) {
      set req.backend = backup_backend;
  }
  ```

req.http.header
- **对应的HTTP头**：这个变量用来访问请求中的HTTP头信息。通过这个变量，可以对特定的HTTP头进行操作，比如修改或者检查头信息。这里的header指的是请求头的名称，比如 `req.http.请求头中的某个属性`

  ```vcl
  if (req.http.Cookie ~ "session_id=") {
      // 针对包含session_id的Cookie的处理
  }
  ```

req.hash_always_miss
- **强制本次请求的缓存失效**：如果设置为true，那么即使请求的URL已经被缓存，也会强制从后端服务器获取新的内容。

  ```vcl
  set req.hash_always_miss = true;
  ```

req.hash_ignore_busy
- **当lookup缓存的时候，忽略busy的对象**：这个变量用来忽略那些标记为busy的缓存对象，避免在缓存查找时等待busy对象。

  ```vcl
  set req.hash_ignore_busy = true;
  ```

req.can_gzip
- **设置能使用gzip**：这个变量用来指示是否可以接受gzip压缩的内容。如果设置为true，Varnish会尝试获取gzip压缩的内容。

  ```vcl
  set req.can_gzip = true;
  ```

req.restarts
- **设置最大的重启次数**：这个变量用来设置请求重启的最大次数。当请求处理失败时，可以通过重启来处理。

  ```vcl
  if (req.restarts == 0 && req.request == "GET") {
      // 处理重启逻辑
  }
  ```

req.esi
- **设置是否支持ESI，今后会改变，建议不要使用**：这个变量用来指示是否支持ESI（Edge Side Includes）。ESI是一种用于动态内容的边缘计算技术。

  ```vcl
  set req.esi = false;
  ```

req.esi_level
- **设置ESI的level**：这个变量用来设置ESI处理的层级深度。

  ```vcl
  set req.esi_level = 5;
  ```

req.grace
- **设置对象被保持的时间**：这个变量用来设置缓存对象的grace时间，即使缓存对象过期，也可以在一定时间内继续使用。

  ```vcl
  set req.grace = 15m;
  ```

req.xid
- **请求的唯一id**：这个变量包含了请求的唯一标识符。通过这个变量，可以对特定的请求进行追踪和分析。

  ```vcl
  std.log("Handling request with XID: " + req.xid);
  ```

#### 访问backend时使用的变量

bereq.request
- **请求的类型**：这个变量表示发送到backend的请求类型，比如“GET”，“HEAD”等。通过这个变量，可以根据不同的请求类型采取不同的处理策略。

  ```vcl
  if (bereq.request == "GET") {
      // 针对GET请求的处理
  }
  ```

bereq.url
- **请求的URL**：这个变量包含了发送到backend的请求的完整URL。通过这个变量，可以对不同的URL应用不同的处理策略。

  ```vcl
  if (bereq.url ~ "^/api/") {
      // 针对/api路径的处理
  }
  ```

bereq.proto
- **请求的协议**：这个变量表示发送到backend的请求的协议版本，比如HTTP/1.0或者HTTP/1.1。通过这个变量，可以根据不同的协议版本采取不同的处理策略。

  ```vcl
  if (bereq.proto ~ "HTTP/1.0") {
      // 针对HTTP/1.0协议的处理
  }
  ```

bereq.http.header
- **请求的HTTP头**：这个变量用来访问发送到backend的请求中的HTTP头信息。通过这个变量，可以对特定的HTTP头进行操作，比如修改或者检查头信息。

  ```vcl
  if (bereq.http.Cookie ~ "session_id=") {
      // 针对包含session_id的Cookie的处理
  }
  ```

bereq.connect_timeout
- **等待后端服务器响应的时间**：这个变量设置了Varnish等待backend响应的时间。如果backend在规定时间内没有响应，Varnish会认为请求超时。

  ```vcl
  set bereq.connect_timeout = 5s;
  ```

bereq.first_byte_timeout
- **等待接收第一个字节的等待时间**：这个变量设置了Varnish等待接收backend响应的第一个字节的时间。pipe模式下无效。

  ```vcl
  set bereq.first_byte_timeout = 30s;
  ```

bereq.between_bytes_timeout
- **两次从后端服务器接收到字节的间隔**：这个变量设置了Varnish在接收backend响应时，两次接收字节之间的最大间隔时间。pipe模式下无效。

  ```vcl
  set bereq.between_bytes_timeout = 15s;
  ```

#### 从backend获取响应后但尚未进入缓存时使用的变量

这张图片介绍了在VCL（Varnish Configuration Language）中，从backend获取响应后但尚未进入缓存时使用的变量。这些变量可以在`vcl_fetch`子程序中使用。

beresp.do_stream
- **对缓存直接返回给客户端，不会缓存**：这个变量指示Varnish直接将响应返回给客户端，而不进行缓存。在Varnish 3中，这类响应会被标记为busy。

  ```vcl
  if (beresp.http.content-type ~ "video/mp4") {
      set beresp.do_stream = true;
  }
  ```

beresp.do_esi
- **是否进行ESI处理**：这个变量指示Varnish是否要对响应内容进行ESI（Edge Side Includes）处理。ESI是一种用于动态内容的边缘计算技术。

  ```vcl
  set beresp.do_esi = true;
  ```

beresp.do_gzip
- **是否在存储前Gzip压缩**：这个变量指示Varnish是否要在存储响应之前对内容进行Gzip压缩。

  ```vcl
  set beresp.do_gzip = true;
  ```

beresp.do_gunzip
- **是否在存储前解压缩**：这个变量指示Varnish是否要在存储响应之前对内容进行解压缩。

  ```vcl
  set beresp.do_gunzip = true;
  ```

beresp.http.header
- **HTTP头**：这个变量用于访问从backend获取的HTTP响应头信息。通过这个变量，可以对特定的HTTP头进行操作，比如修改或者检查头信息。

  ```vcl
  if (beresp.http.X-Cache-Status ~ "HIT") {
      // 针对HIT响应的处理
  }
  ```

beresp.proto
- **HTTP的协议**：这个变量表示从backend获取的响应的HTTP协议版本，比如HTTP/1.0或者HTTP/1.1。通过这个变量，可以根据不同的协议版本采取不同的处理策略。

  ```vcl
  if (beresp.proto ~ "HTTP/1.0") {
      // 针对HTTP/1.0协议的处理
  }
  ```

beresp.status
- **HTTP的状态码**：这个变量包含了从backend获取到的HTTP响应的状态码，比如200（成功）、404（未找到）等。通过这个变量，可以根据不同的状态码采取不同的处理策略。

  ```vcl
  if (beresp.status == 200) {
      // 针对200状态码的处理
  }
  ```

beresp.response
- **服务端返回的状态消息**：这个变量包含了从backend获取到的HTTP响应的状态消息，比如“OK”、“Not Found”等。结合状态码，可以更好地理解响应的含义。

  ```vcl
  if (beresp.response ~ "OK") {
      // 针对状态消息为OK的处理
  }
  ```

beresp.ttl
- **对象保存的时间**：这个变量用于设置缓存对象的TTL（Time-To-Live），即对象在缓存中可以保存的时间。通过调整TTL，可以控制缓存的有效期。

  ```vcl
  set beresp.ttl = 1h;  // 设置缓存对象的TTL为1小时
  ```

beresp.grace
- **对象grace保存的时间**：这个变量用于设置缓存对象的grace时间，即使缓存对象过期，也可以在一定时间内继续使用。这对于处理后端服务器短暂不可用的情况非常有用。

  ```vcl
  set beresp.grace = 15m;  // 设置grace时间为15分钟
  ```

beresp.saintmode
- **saint模式持续的时间**：这个变量用于设置saint模式持续的时间。saint模式是一种容错机制，当后端服务器返回错误时，可以在一段时间内避免请求该服务器。

  ```vcl
  set beresp.saintmode = 10m;  // 设置saint模式持续时间为10分钟
  ```

beresp.backend.name
- **response的backend的名字**：这个变量包含了处理请求的后端服务器的名字。通过这个变量，可以识别是哪一个后端服务器返回的响应。

  ```vcl
  std.log("Response from backend: " + beresp.backend.name);
  ```

beresp.backend.ip
- **response的backend的IP地址**：这个变量包含了处理请求的后端服务器的IP地址。结合backend.name，可以更精确地定位后端服务器。

  ```vcl
  std.log("Backend IP: " + beresp.backend.ip);
  ```

beresp.backend.port
- **response的backend的端口**：这个变量包含了处理请求的后端服务器的端口号。结合backend.ip，可以全面了解后端服务器的信息。

  ```vcl
  std.log("Backend port: " + beresp.backend.port);
  ```

beresp.storage
- **强制Varnish保存这个对象**：这个变量用于指定缓存对象的存储方式。通过设置这个变量，可以控制Varnish如何存储缓存对象。

  ```vcl
  set beresp.storage = "malloc, 1G";  // 使用malloc存储，并限制大小为1G
  ```


#### 请求目标被成功地从后端服务器或者缓存中获取后有效的变量

这张图片介绍了在VCL（Varnish Configuration Language）中，当请求目标被成功地从后端服务器或者缓存中获取后有效的变量。即 vcl_deliver

obj.proto
- **返回请求目标的HTTP版本**：这个变量包含了从后端服务器或者缓存中获取到的响应的HTTP版本，比如HTTP/1.0或者HTTP/1.1。通过这个变量，可以根据不同的协议版本采取不同的处理策略。

  ```vcl
  if (obj.proto ~ "HTTP/1.0") {
      // 针对HTTP/1.0协议的处理
  }
  ```

obj.status
- **服务器返回的HTTP状态码**：这个变量包含了从后端服务器或者缓存中获取到的HTTP响应的状态码，比如200（成功）、404（未找到）等。通过这个变量，可以根据不同的状态码采取不同的处理策略。

  ```vcl
  if (obj.status == 200) {
      // 针对200状态码的处理
  }
  ```

obj.response
- **服务器返回的HTTP状态信息**：这个变量包含了从后端服务器或者缓存中获取到的HTTP响应的状态信息，比如“OK”、“Not Found”等。结合状态码，可以更好地理解响应的含义。

  ```vcl
  if (obj.response ~ "OK") {
      // 针对状态消息为OK的处理
  }
  ```

obj.ttl
- **目标的剩余生存时间，以秒为单位**：这个变量用于获取缓存对象的TTL（Time-To-Live），即对象在缓存中可以保存的时间。通过这个变量，可以判断缓存对象是否即将过期。

  ```vcl
  if (obj.ttl < 300) {
      // 缓存对象剩余时间少于5分钟的处理
  }
  ```

obj.lastuse
- **最后一个请求后，过去的时间，以秒为单位**：这个变量包含了自最后一个请求以来所经过的时间，单位为秒。通过这个变量，可以判断缓存对象的活跃程度。

  ```vcl
  if (obj.lastuse > 600) {
      // 缓存对象超过10分钟未使用时的处理
  }
  ```

obj.hits
- **大概的delivered次数，如果为0，表明缓存出错**：这个变量包含了缓存对象被成功交付的次数。如果这个值为0，可能表明缓存对象存在问题。

  ```vcl
  if (obj.hits == 0) {
      // 缓存对象未被成功交付的处理
  }
  ```

obj.grace
- **对象grace的存活时间**：这个变量用于获取缓存对象的grace时间，即即使缓存对象过期，也可以在一定时间内继续使用的时间长度。

  ```vcl
  if (obj.grace > 900) {
      // grace时间超过15分钟的处理
  }
  ```

obj.http.header
- **HTTP头**：这个变量用于访问从后端服务器或者缓存中获取到的HTTP响应头信息。通过这个变量，可以对特定的HTTP头进行操作，比如修改或者检查头信息。

  ```vcl
  if (obj.http.Content-Type ~ "text/html") {
      // 针对Content-Type为text/html的处理
  }
  ```

#### 在目标hash key以后有效的变量
- **req.hash**: 这个变量和缓存中的目标相关，在读出和写入缓存时使用。通过设置req.hash，可以定义缓存的hash key，从而影响缓存的行为。

#### 在准备响应客户端时使用的变量
- **resp.proto**: 这个变量表示准备响应的HTTP协议版本，比如HTTP/1.0或者HTTP/1.1。通过这个变量，可以指定返回给客户端的HTTP版本。

  ```vcl
  set resp.proto = "HTTP/1.1";
  ```

- **resp.status**: 这个变量用于设置返回给客户端的HTTP状态码，比如200（成功）、404（未找到）等。通过这个变量，可以控制HTTP响应的状态。

  ```vcl
  set resp.status = 200;
  ```

- **resp.response**: 这个变量用于设置返回给客户端的HTTP状态信息，比如“OK”、“Not Found”等。结合状态码，可以更好地传达响应的含义。

  ```vcl
  set resp.response = "OK";
  ```

- **resp.http.header**: 这个变量用于设置通信的HTTP头信息。通过这个变量，可以添加、修改或者删除HTTP响应头。

  ```vcl
  set resp.http.Content-Type = "text/html; charset=utf-8";
  ```

## VCL 调试与日志

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

## 常见应用片段

### 设置 HTTP 头

这段代码是用于Varnish Cache的VCL（Varnish Configuration Language）配置的一部分。它的目的是根据客户端的设备类型设置不同的HTTP头部参数。具体来说，这段代码做了以下事情：

1. 定义了一个名为`vcl_recv`的子程序，这个子程序在Varnish收到请求时执行。
2. 使用`if`语句检查请求的`User-Agent`头部是否包含"iPad"、"iPhone"或"Android"。
3. 如果`User-Agent`包含这些字符串中的任何一个，就设置请求的`X-Device`头部为"mobile"，表示这是一个移动设备。
4. 如果`User-Agent`不包含这些字符串，就设置请求的`X-Device`头部为"desktop"，表示这是一个桌面设备。

完整的代码如下：

```vcl
sub vcl_recv {
    if (req.http.User-Agent ~ "iPad" ||
        req.http.User-Agent ~ "iPhone" ||
        req.http.User-Agent ~ "Android") {
        set req.http.X-Device = "mobile";
    } else {
        set req.http.X-Device = "desktop";
    }
}
```

### 取消对路径`/images`的请求中的cookie

这段VCL（Varnish Configuration Language）代码的目的是取消对路径`/images`的请求中的cookie。代码如下：

```vcl
sub vcl_recv {
    if (req.url ~ "^/images") {
        unset req.http.cookie;
    }
}
```

解释：
1. `sub vcl_recv { ... }`：定义一个名为`vcl_recv`的子程序，这个子程序在Varnish收到请求时执行。
2. `if (req.url ~ "^/images") { ... }`：检查请求的URL是否以`/images`开头。如果是，则执行大括号内的代码。
3. `unset req.http.cookie;`：取消请求中的cookie头部。

这样配置后，所有对`/images`路径的请求将不会携带cookie。

### 通过 ACL 控制可以访问的地址

这段VCL（Varnish Configuration Language）代码的作用是通过ACL（访问控制列表）来控制能够执行PURGE请求的IP地址。以下是代码的详细解释：

```vcl
// 定义一个名为local的ACL，控制允许访问的IP地址
acl local {
    "localhost";  // 允许本地主机访问
    "192.168.1.0"/24;  // 允许本地网络中的所有IP地址访问
    !"192.168.1.23";  // 禁止拨号路由器的IP地址访问
}

sub vcl_recv {
    // 如果请求方法是PURGE
    if (req.request == "PURGE") {
        // 检查客户端IP是否在local ACL中
        if (client.ip ~ local) {
            // 如果在local ACL中，则进行缓存查找
            return (lookup);
        }
    }
}

sub vcl_hit {
    // 如果请求方法是PURGE并且缓存命中
    if (req.request == "PURGE") {
        // 将对象的TTL设置为0秒
        set obj.ttl = 0s;
        // 返回200状态码和"Purged."消息
        error 200 "Purged.";
    }
}

sub vcl_miss {
    // 如果请求方法是PURGE并且缓存未命中
    if (req.request == "PURGE") {
        // 返回404状态码和"Not in cache."消息
        error 404 "Not in cache.";
    }
}
```

详细解释：

1. **ACL定义**：
   - `acl local { ... }`：定义一个名为`local`的访问控制列表，包含允许访问的IP地址。
   - `"localhost"`：允许本地主机访问。
   - `"192.168.1.0"/24`：允许本地网络中的所有IP地址访问。
   - `!"192.168.1.23"`：禁止拨号路由器的IP地址访问。

2. **`vcl_recv`子程序**：
   - `if (req.request == "PURGE") { ... }`：检查请求的方法是否为`PURGE`。
   - `if (client.ip ~ local) { ... }`：如果客户端IP在`local` ACL中，则执行缓存查找。

3. **`vcl_hit`子程序**：
   - `if (req.request == "PURGE") { ... }`：如果请求的方法是`PURGE`且缓存命中。
   - `set obj.ttl = 0s;`：将对象的TTL（生存时间）设置为0秒，即删除缓存。
   - `error 200 "Purged.";`：返回200状态码和"Purged."消息。

4. **`vcl_miss`子程序**：
   - `if (req.request == "PURGE") { ... }`：如果请求的方法是`PURGE`且缓存未命中。
   - `error 404 "Not in cache.";`：返回404状态码和"Not in cache."消息。

这段代码的主要目的是通过控制PURGE请求的访问权限，确保只有特定的IP地址可以清除缓存，从而提高安全性。

### 修改从后台服务器返回的对象的TTL

这段VCL（Varnish Configuration Language）代码的目的是修改从后台服务器返回的对象的TTL（生存时间），特别是针对图片文件（PNG、GIF、JPG）。以下是代码的详细解释：

```vcl
sub vcl_fetch {
    if (req.url ~ "\.(png|gif|jpeg|jpg)$") {
        unset beresp.http.set-cookie;
        set beresp.ttl = 1h;
    }
}
```

详细解释：

1. **`sub vcl_fetch { ... }`**：
   - 这行代码定义了一个名为`vcl_fetch`的子程序。`vcl_fetch`是在Varnish从后台服务器成功获取到对象后调用的子程序。在这个子程序中，你可以对从后台服务器返回的对象进行处理和修改。

2. **`if (req.url ~ "\.(png|gif|jpeg|jpg)$") { ... }`**：
   - 这行代码检查请求的URL是否以.png、.gif、.jpeg或.jpg结尾，即判断请求的资源是否为图片文件。
   - 正则表达式`\.(png|gif|jpeg|jpg)$`表示URL以.png、.gif、.jpeg或.jpg结尾。

3. **`unset beresp.http.set-cookie;`**：
   - 这行代码的作用是取消响应中的`set-cookie`头部。`unset`关键字用于删除变量或头部信息。
   - 通过取消`set-cookie`，可以防止图片文件携带不必要的cookie，从而提高缓存效率和性能。

4. **`set beresp.ttl = 1h;`**：
   - 这行代码设置从后台服务器返回的对象的TTL（生存时间）为1小时。
   - `beresp.ttl`是Varnish中的一个变量，用于定义缓存对象的生存时间。通过设置`beresp.ttl = 1h`，你可以控制图片文件在缓存中保存的时间为1小时。

通过这样的配置，你可以确保对图片文件的请求不会携带cookie，并且这些文件会在缓存中保存1小时。这样可以减少不必要的数据传输，提高缓存效率，并减轻后端服务器的负担。这种配置在需要优化静态资源加载性能的场景中非常有用。

### 设置客户端发送的`Accept-Encoding`头，只允许`gzip`和`deflate`两种编码方式，并且优先使用`gzip`

这段VCL（Varnish Configuration Language）代码的目的是设置客户端发送的`Accept-Encoding`头，只允许`gzip`和`deflate`两种编码方式，并且优先使用`gzip`。以下是代码的详细解释：

```vcl
if (req.http.Accept-Encoding) {
    if (req.url ~ "\.(jpg|png|gif|gz|tgz|bz2|tbz|mp3|ogg)$") {
        # No point in compressing these
        remove req.http.Accept-Encoding;
    } elseif (req.http.Accept-Encoding ~ "gzip") {
        set req.http.Accept-Encoding = "gzip";
    } elseif (req.http.Accept-Encoding ~ "deflate") {
        set req.http.Accept-Encoding = "deflate";
    } else {
        # unknown algorithm
        remove req.http.Accept-Encoding;
    }
}
```

详细解释：

1. **`if (req.http.Accept-Encoding) { ... }`**：
   - 这行代码检查请求中是否包含`Accept-Encoding`头。如果存在，则执行大括号内的代码。

2. **`if (req.url ~ "\.(jpg|png|gif|gz|tgz|bz2|tbz|mp3|ogg)$") { ... }`**：
   - 这行代码检查请求的URL是否以指定的文件扩展名结尾，即判断请求的资源是否为图片文件、压缩文件或音频文件。
   - 正则表达式`\.(jpg|png|gif|gz|tgz|bz2|tbz|mp3|ogg)$`表示URL以这些扩展名结尾。
   - 如果匹配，则移除请求中的`Accept-Encoding`头，因为这些类型的文件通常不需要压缩。

3. **`elseif (req.http.Accept-Encoding ~ "gzip") { ... }`**：
   - 这行代码检查`Accept-Encoding`头是否包含`gzip`。如果是，则将`Accept-Encoding`头设置为`gzip`，表示客户端支持gzip压缩。

4. **`elseif (req.http.Accept-Encoding ~ "deflate") { ... }`**：
   - 这行代码检查`Accept-Encoding`头是否包含`deflate`。如果是，则将`Accept-Encoding`头设置为`deflate`，表示客户端支持deflate压缩。

5. **`else { ... }`**：
   - 如果`Accept-Encoding`头既不包含`gzip`也不包含`deflate`，则移除请求中的`Accept-Encoding`头，因为不支持的编码方式将被忽略。

通过这样的配置，你可以确保只有支持的压缩方式（`gzip`和`deflate`）被使用，并且优先使用`gzip`。这有助于优化网络传输和提高页面加载性能。

### 简单的图片防盗链

这段VCL（Varnish Configuration Language）代码的目的是实现一个简单的图片防盗链机制。以下是代码的详细解释：

```vcl
// 简单的图片防盗链：
if (req.http.referer ~ "http://.*") {
    if ( !(req.http.referer ~ "http://.*sishuok\.com"
        || req.http.referer ~ "http://.*google\.com"
        || req.http.referer ~ "http://.*google\.cn"
    )) {
        set req.http.host = "www.sishuok.com";
        set req.url = "/static/images/logo.gif";
    }
}
return (lookup);
```

详细解释：

1. **`if (req.http.referer ~ "http://.*") { ... }`**：
   - 这行代码检查请求的`Referer`头是否以`http://`开头。`~`是匹配操作符，`"http://.*"`表示字符串以`http://`开头，后面跟随任意字符。
   - 如果匹配，则执行大括号内的代码。

2. **`if ( !(req.http.referer ~ "http://.*sishuok\.com" || req.http.referer ~ "http://.*google\.com" || req.http.referer ~ "http://.*google\.cn") ) { ... }`**：
   - 这行代码检查`Referer`头是否不匹配以下三个域名：`sishuok.com`、`google.com`、`google.cn`。
   - 使用`!`表示取反，即如果`Referer`头不匹配这三个域名，则执行大括号内的代码。

3. **`set req.http.host = "www.sishuok.com";`**：
   - 这行代码将请求的主机名设置为`www.sishuok.com`。这意味着即使请求是从其他域名发起的，也会被重定向到`www.sishuok.com`。

4. **`set req.url = "/static/images/logo.gif";`**：
   - 这行代码将请求的URL设置为`/static/images/logo.gif`。这意味着即使请求的是其他资源，也会被重定向到这个特定的图片文件。

5. **`return (lookup);`**：
   - 这行代码表示进行缓存查找。`lookup`是Varnish中的一个动作，用于查找请求的资源是否在缓存中。

通过这样的配置，你可以实现简单的图片防盗链机制。只有来自特定域名（`sishuok.com`、`google.com`、`google.cn`）的请求才能正常访问图片，否则会被重定向到指定的图片文件。这有助于防止其他网站直接引用你的图片资源，从而保护你的图片版权。





# VCL 常用内置函数

在 Varnish 中提供了非常丰富的内置函数供开发者使用。

## hash_data(str)

`hash_data` 函数用于向 Varnish 的哈希表中添加数据，这些数据将用于生成缓存键。缓存键决定了 Varnish 如何区分不同的请求，以及是否可以从缓存中提供响应。通过合理地使用 `hash_data`，可以确保缓存的命中率（hit rate）最大化，同时避免缓存污染（cache pollution）。

### 作用原理

在 Varnish 中，`hash_data(str)` 是一个用于自定义缓存哈希键的内置函数。为了深入理解其工作流程细节，我们需要从 Varnish 的整体缓存机制入手，逐步解析 `hash_data(str)` 在其中的具体作用和执行过程。

#### 1. **缓存哈希键的基本概念**

在 Varnish 中，缓存查找依赖于一个唯一的哈希键（hash key）。这个哈希键用于在内存中快速定位特定的缓存条目。哈希键的生成基于多个因素，包括但不限于：

- **请求的 URL**
- **主机名（Host Header）**
- **请求方法（如 GET、POST）**
- **其他请求头信息**

默认情况下，Varnish 会根据这些基本信息生成一个哈希键。然而，在某些情况下，这些基本信息不足以区分不同的缓存条目，此时就需要使用 `hash_data(str)` 来添加额外的数据。

#### 2. **VCL 中的 `hash` 子程序**

Varnish 使用 VCL（Varnish Configuration Language）来定义缓存策略。在 VCL 中，`vcl_hash` 子程序用于定义如何生成哈希键。以下是一个典型的 `vcl_hash` 示例：

```vcl
sub vcl_hash {
    hash_data(req.url);
    hash_data(req.http.host);
    if (req.http.Cookie) {
        hash_data(req.http.Cookie);
    }
    return (lookup);
}
```

在这个示例中，哈希键由请求的 URL、主机名以及 Cookie 组成。

#### 3. **`hash_data(str)` 的工作流程细节**

当 Varnish 处理一个请求并进入 `vcl_hash` 子程序时，`hash_data(str)` 的执行流程如下：

##### a. **初始化哈希上下文**

1. **创建哈希上下文**：Varnish 首先为当前请求创建一个哈希上下文（hash context），用于存储所有用于生成哈希键的数据。
2. **添加默认数据**：默认情况下，Varnish 会自动将一些基本信息（如请求的 URL 和主机名）添加到哈希上下文中。

##### b. **执行 `hash_data(str)`**

1. **接收字符串参数**：`hash_data(str)` 接收一个字符串参数，该参数可以是任何自定义的数据，如请求头、Cookie 值、URL 参数等。
2. **添加到哈希上下文**：Varnish 将接收到的字符串参数添加到当前的哈希上下文中。这相当于在生成哈希键时引入了一个新的变量。

   ```vcl
   sub vcl_hash {
       hash_data(req.http.X-Custom-Header);
   }
   ```

   在上述示例中，`X-Custom-Header` 的值被添加到哈希上下文中。

3. **处理多个 `hash_data(str)` 调用**：如果在 `vcl_hash` 中多次调用 `hash_data(str)`，每个调用都会将相应的字符串参数添加到哈希上下文中。这意味着哈希键将包含所有通过 `hash_data(str)` 添加的数据。

   ```vcl
   sub vcl_hash {
       hash_data(req.url);
       hash_data(req.http.host);
       hash_data(req.http.X-Custom-Header);
   }
   ```

   在这个例子中，哈希键由 URL、主机名和 `X-Custom-Header` 组成。

##### c. **生成哈希键**

1. **组合数据**：所有通过 `hash_data(str)` 添加的数据与默认的数据（如 URL 和主机名）组合在一起，形成一个完整的字符串。
2. **计算哈希值**：Varnish 使用哈希算法（如 MurmurHash）对这个组合字符串进行计算，生成一个唯一的哈希值。
3. **存储哈希键**：这个哈希值作为缓存查找的键，用于在缓存中定位对应的缓存条目。

##### d. **缓存查找**

1. **查找缓存**：使用生成的哈希键在缓存中查找是否存在对应的缓存条目。
2. **缓存命中或未命中**：
   - **命中**：如果找到对应的缓存条目，Varnish 会直接返回缓存的响应。
   - **未命中**：如果没有找到对应的缓存条目，Varnish 会将请求转发给后端服务器获取响应，并将其存储到缓存中。

#### 4. **影响缓存命中的因素**

使用 `hash_data(str)` 可以显著影响缓存命中率。以下是一些关键点：

- **唯一性**：通过添加不同的数据，`hash_data(str)` 可以使哈希键更加唯一，从而区分不同的缓存条目。例如，基于用户会话 ID 或其他动态参数。
- **粒度控制**：可以控制缓存的粒度，避免不同用户或不同请求之间的缓存冲突。
- **性能考虑**：添加过多的数据可能会增加哈希键的复杂性，影响缓存查找的性能。因此，应根据实际需求合理使用 `hash_data(str)`。

#### 5. **实际应用示例**

假设有一个 Web 应用，其中不同的用户需要不同的缓存响应。我们可以使用 `hash_data(str)` 来基于用户身份生成不同的哈希键：

```vcl
sub vcl_hash {
    hash_data(req.url);
    hash_data(req.http.host);
    if (req.http.X-User-ID) {
        hash_data(req.http.X-User-ID);
    }
    return (lookup);
}
```

在这个示例中：

1. **添加用户 ID**：如果请求头中包含 `X-User-ID`，将其添加到哈希键中。
2. **影响缓存**：这样，具有不同 `X-User-ID` 值的请求将被视为不同的缓存条目，确保每个用户都能获取到正确的缓存响应。

### 基本用法

在 VCL 中，`hash_data` 通常在 `vcl_hash` 子程序中使用。以下是一个基本的示例：

```vcl
sub vcl_hash {
    # 添加请求的 URL 到哈希表
    hash_data(req.url);

    # 如果需要，可以根据请求的其他部分（如主机名、查询参数等）来区分缓存
    hash_data(req.http.host);

    # 例如，根据查询参数中的某些值来区分缓存
    if (req.url ~ "\?") {
        set req.url = regsub(req.url, "\?.+", "");
    }
    hash_data(req.url);

    return (lookup);
}
```

在上述示例中：

1. 首先将请求的 URL 添加到哈希表中。
2. 然后将请求的主机名（`req.http.host`）也添加到哈希表中。
3. 如果 URL 中包含查询参数，可以选择是否根据查询参数来区分缓存。

### 何时使用 `hash_data`

使用 `hash_data` 的主要目的是确保缓存的粒度（granularity）符合应用的需求。以下是一些常见的应用场景：

1. **基于 URL 缓存**：这是最常见的情况，不同的 URL 对应不同的缓存条目。
    ```vcl
    sub vcl_hash {
        hash_data(req.url);
        return (lookup);
    }
    ```

2. **基于主机名和 URL 缓存**：如果同一个 URL 在不同主机名下有不同的内容，可以同时添加主机名和 URL 到哈希表中。
    ```vcl
    sub vcl_hash {
        hash_data(req.http.host);
        hash_data(req.url);
        return (lookup);
    }
    ```

3. **基于查询参数缓存**：有时，URL 中的查询参数会影响内容，可以选择性地添加查询参数到哈希表中。
    ```vcl
    sub vcl_hash {
        hash_data(req.url);
        if (req.url ~ "\?") {
            set req.url = regsub(req.url, "\?.+", "");
        }
        hash_data(req.url);
        return (lookup);
    }
    ```

4. **基于 Cookie 缓存**：如果需要根据用户的特定状态（如登录状态）来缓存内容，可以添加相关的 Cookie 到哈希表中。
    ```vcl
    sub vcl_hash {
        hash_data(req.http.cookie);
        return (lookup);
    }
    ```

### 注意事项

1. **缓存键的唯一性**：确保 `hash_data` 添加的数据能够唯一地标识不同的请求，否则可能会导致缓存命中错误或缓存污染。

2. **性能考虑**：虽然 `hash_data` 可以添加多个数据到哈希表中，但过多的数据会增加哈希表的复杂性，可能影响性能。应根据实际需求选择必要的数据。

3. **避免缓存污染**：例如，如果添加了动态生成的参数（如会话 ID）到哈希表中，可能会导致每个请求都有不同的缓存键，从而降低缓存命中率。应避免将不必要的动态数据添加到哈希表中。

### 高级用法

1. **条件性添加数据**：
    ```vcl
    sub vcl_hash {
        hash_data(req.url);
        if (req.http.host == "www.example.com") {
            hash_data("special");
        }
        return (lookup);
    }
    ```
    在这个例子中，只有当主机名为 `www.example.com` 时，才会添加 `"special"` 到哈希表中，从而实现更细粒度的缓存控制。

2. **使用变量**：
    ```vcl
    sub vcl_hash {
        set req.http.X-Cache-Key = req.url + req.http.host;
        hash_data(req.http.X-Cache-Key);
        return (lookup);
    }
    ```
    通过自定义变量，可以更灵活地控制缓存键的生成。


**参考资料**：

- [Varnish 官方文档 - VCL Reference](https://varnish-cache.org/docs/6.0/reference/vcl.html)
- [Varnish 官方文档 - VCL hash](https://varnish-cache.org/docs/6.0/reference/vcl.html#vcl-hash)

## regsub(str, regex, sub)

`regsub(str, regex, sub)` 用于基于正则表达式对字符串进行替换操作。

### 函数定义

```vcl
regsub(str, regex, sub)
```

- **str**: 要进行替换操作的原始字符串。
- **regex**: 用于匹配的正则表达式。
- **sub**: 用于替换匹配部分的字符串。

### 功能描述

`regsub` 函数会在字符串 `str` 中搜索与正则表达式 `regex` 匹配的部分，并将匹配到的部分替换为 `sub` 指定的字符串。具体来说，`regsub` 只会替换第一个匹配到的部分。如果需要替换所有匹配的部分，可以使用 `regsuball` 函数。

### 实现细节

1. **正则表达式匹配**:
   - `regsub` 使用的是 Perl 兼容的正则表达式（PCRE）。这意味着在编写正则表达式时，可以使用 PCRE 支持的各种语法和特性。
   - 例如，如果需要忽略大小写匹配，可以在正则表达式中使用 `(?i)` 标志：
     ```vcl
     regsub(req.http.host, "(?i)example\\.com", "example.org")
     ```

2. **替换字符串中的特殊字符**:
   - 在 `sub` 字符串中，可以使用以下特殊字符进行替换：
     - `\\0` 或 `\\&`: 替换为整个匹配的字符串。
     - `\\n`: 替换为第 n 个捕获组的匹配内容。例如，`\\1` 替换为第一个捕获组的匹配内容。
   - 例如：
     ```vcl
     regsub("Hello World", "(\\w+) (\\w+)", "\\2, \\1")  // 结果: "World, Hello"
     ```

3. **性能考虑**:
   - 正则表达式匹配和替换操作可能会对性能产生影响，尤其是在高流量的环境中。因此，建议在编写正则表达式时尽量简化，避免复杂的模式匹配，以提高性能。
   - 例如，如果只需要简单的字符串替换，可以使用 `set` 语句配合字符串函数，如 `substr` 或 `replace`，而不是使用正则表达式。

4. **示例**:
   - **示例 1**: 替换 URL 中的部分路径
     ```vcl
     sub vcl_recv {
         set req.url = regsub(req.url, "^/old/path/", "/new/path/");
     }
     ```
     这个例子中，所有以 `/old/path/` 开头的 URL 都会被替换为 `/new/path/`。

   - **示例 2**: 修改请求头中的 User-Agent
     ```vcl
     sub vcl_recv {
         set req.http.User-Agent = regsub(req.http.User-Agent, "MSIE", "Internet Explorer");
     }
     ```
     这个例子中，所有包含 "MSIE" 的 User-Agent 字符串都会被替换为 "Internet Explorer"。

   - **示例 3**: 替换所有匹配项
     ```vcl
     sub vcl_recv {
         set req.url = regsuball(req.url, " ", "_");
     }
     ```
     这个例子中，所有空格都会被替换为下划线。

5. **与 `regsuball` 的区别**:
   - `regsub`: 仅替换第一个匹配到的部分。
   - `regsuball`: 替换所有匹配到的部分。
   - 例如：
     ```vcl
     set str = "foo bar foo bar";
     set str_new = regsub(str, "foo", "baz");  // 结果: "baz bar foo bar"
     set str_new_all = regsuball(str, "foo", "baz"); // 结果: "baz bar baz bar"
     ```

## regsuball(str, regex, sub)

同上替换所有匹配到的部分。

## ban(expression)

`ban` 是 Varnish 提供的一个内置函数，用于**禁止**（ban）缓存中匹配特定条件的对象。简单来说，`ban` 可以用来标记缓存中的某些对象，使其在后续的请求中不再被使用，从而实现缓存内容的过期或删除。

在动态网站或应用中，缓存内容可能会因为数据更新而变得过时。使用 `ban` 可以确保缓存中不再包含过时的内容，从而避免向用户提供错误或过时的信息。

Varnish 提供了两种主要的方法来移除缓存内容：`ban` 和 `purge`。虽然它们都用于移除缓存，但它们的工作方式有所不同：

- **`purge`**：立即移除匹配的缓存对象，并在后续的请求中重新从后端获取数据。`purge` 操作是同步的，会立即生效。
  
- **`ban`**：标记匹配的缓存对象，使其在后续的请求中不再被使用。`ban` 操作是异步的，可能不会立即生效，直到缓存对象被再次访问。

**选择使用 `ban` 还是 `purge` 取决于具体需求**：

- 如果需要立即移除缓存内容并确保后续请求从后端获取最新数据，应该使用 `purge`。
- 如果希望缓存对象在后续的请求中不再被使用，但不需要立即移除，可以使用 `ban`。

#### 4. `ban` 的语法

`ban` 的基本语法如下：

```vcl
ban <expression>
```

其中 `<expression>` 是一个用于匹配缓存对象的表达式，类似于 VCL（Varnish Configuration Language）中的匹配规则。

#### 5. 常用的 `ban` 表达式

以下是一些常用的 `ban` 表达式示例：

- **基于 URL 禁止缓存对象**：

  ```vcl
  ban req.url ~ "^/path/to/resource"
  ```

  该表达式会禁止所有 URL 以 `/path/to/resource` 开头的缓存对象。

- **基于 HTTP 头部禁止缓存对象**：

  ```vcl
  ban req.http.host == "example.com" && req.url ~ "^/images/"
  ```

  该表达式会禁止所有主机为 `example.com` 且 URL 以 `/images/` 开头的缓存对象。

- **基于内容类型禁止缓存对象**：

  ```vcl
  ban req.http.Content-Type ~ "text/html"
  ```

  该表达式会禁止所有内容类型为 `text/html` 的缓存对象。

- **基于时间戳禁止缓存对象**：

  ```vcl
  ban obj.http.Last-Modified < now - 1d
  ```

  该表达式会禁止所有 `Last-Modified` 时间早于当前时间一天前的缓存对象。

#### 6. `ban` 的执行过程

当调用 `ban` 时，Varnish 会执行以下步骤：

1. **解析表达式**：Varnish 会解析传入的 `ban` 表达式，并将其转换为内部使用的格式。
2. **标记缓存对象**：所有匹配 `ban` 表达式的缓存对象会被标记为“被禁止”，使其在后续的请求中不再被使用。
3. **异步处理**：由于 `ban` 是异步的，缓存对象并不会立即被移除。只有当缓存对象被再次访问时，Varnish 才会检查其是否被禁止，并决定是否重新从后端获取数据。

#### 7. 使用 `ban` 的注意事项

- **性能影响**：`ban` 操作可能会对性能产生一定影响，特别是在处理大量缓存对象时。因此，建议在必要时使用 `ban`，并尽量优化 `ban` 表达式以减少匹配范围。
  
- **缓存一致性**：使用 `ban` 可以确保缓存内容的一致性，但需要确保 `ban` 表达式的准确性，以避免误删缓存对象。

- **结合 `purge` 使用**：在某些情况下，结合使用 `ban` 和 `purge` 可以实现更高效的缓存管理。例如，先使用 `ban` 标记需要移除的缓存对象，然后在适当的时候执行 `purge` 操作以彻底移除它们。

#### 8. 示例

假设我们有一个网站，URL 结构如下：

```
https://example.com/products/12345
https://example.com/products/67890
https://example.com/categories/electronics
```

我们希望移除所有 URL 以 `/products/` 开头的缓存对象，可以使用以下 `ban` 命令：

```vcl
ban req.url ~ "^/products/"
```

执行上述命令后，所有以 `/products/` 开头的 URL 缓存对象将被标记为“被禁止”，在后续的请求中，Varnish 将不会使用这些缓存对象，而是从后端重新获取数据。

# Varnish 缓存与 Http 头信息

## Cache-Control

HTTP协议中的`Cache-Control`头字段用于在客户端和服务器之间指定缓存机制，以优化资源加载性能，减少网络带宽消耗，并提高用户体验。

**缓存（Cache）**是存储在客户端或中间代理服务器（如CDN）上的资源的副本，用于在后续请求中快速提供这些资源，而无需每次都从源服务器重新获取。缓存的主要目的是减少延迟、提高响应速度和降低网络负载。

`Cache-Control`是HTTP/1.1中引入的头字段，用于在请求和响应中指定缓存指令。它替代了早期的`Pragma`和`Expires`头字段，提供更灵活和强大的缓存控制机制。

常见的 Cache-Control 指令

- **缓存控制指令分类**
  - **缓存策略指令**：控制缓存的行为和策略。
  - **缓存有效性指令**：指定资源在缓存中的有效时间。

常用的缓存策略指令

1. **public**
   - **含义**：响应可以被任何缓存（如浏览器、代理服务器）缓存。
   - **示例**：`Cache-Control: public`
   - **适用场景**：适用于公开的、可以共享的资源。

2. **private**
   - **含义**：响应只能被单个用户缓存，不能被共享缓存（如代理服务器）缓存。
   - **示例**：`Cache-Control: private`
   - **适用场景**：适用于包含用户特定信息的内容。

3. **no-cache**
   - **含义**：在提供缓存副本之前，必须向源服务器验证资源是否仍然有效。
   - **示例**：`Cache-Control: no-cache`
   - **适用场景**：适用于需要每次都验证资源是否最新的情况。

4. **no-store**
   - **含义**：完全禁止缓存，响应内容不能被存储在任何缓存中。
   - **示例**：`Cache-Control: no-store`
   - **适用场景**：适用于敏感信息，如个人隐私数据。

5. **must-revalidate**
   - **含义**：缓存必须在每次使用缓存副本之前，向源服务器验证其有效性。
   - **示例**：`Cache-Control: must-revalidate`
   - **适用场景**：确保资源是最新的，防止使用过期的资源。

6. **proxy-revalidate**
   - **含义**：类似于`must-revalidate`，但仅适用于共享缓存（如代理服务器）。
   - **示例**：`Cache-Control: proxy-revalidate`

7. **max-age**
   - **含义**：指定资源的最大缓存时间（以秒为单位）。
   - **示例**：`Cache-Control: max-age=3600`
   - **适用场景**：适用于资源在一定时间内保持有效的情况。

8. **s-maxage**
   - **含义**：类似于`max-age`，但仅适用于共享缓存（如代理服务器）。
   - **示例**：`Cache-Control: s-maxage=3600`

缓存有效性指令

1. **immutable**
   - **含义**：指示资源在一定时间内不会改变，客户端不应尝试重新验证资源。
   - **示例**：`Cache-Control: immutable`
   - **适用场景**：适用于资源在短时间内不会变化的情况。

2. **stale-while-revalidate**
   - **含义**：允许缓存使用过期的资源，同时在后台重新验证资源。
   - **示例**：`Cache-Control: stale-while-revalidate=300`
   - **适用场景**：提高响应速度，减少等待时间。

3. **stale-if-error**
   - **含义**：在发生错误时，允许缓存使用过期的资源。
   - **示例**：`Cache-Control: stale-if-error=600`
   - **适用场景**：提高容错能力，确保在服务器故障时仍能提供资源。

### Cache-Control 的工作原理

#### 客户端请求

当客户端发送HTTP请求时，可以在请求头中包含`Cache-Control`指令，以指示服务器和中间缓存服务器如何处理响应。

**示例**：

```http
GET /example.jpg HTTP/1.1
Host: www.example.com
Cache-Control: no-cache
```

上述请求指示服务器和中间缓存服务器在返回响应之前，必须向源服务器验证资源是否仍然有效。

#### 服务器响应

服务器在响应中包含`Cache-Control`指令，以指示客户端和中间缓存服务器如何缓存响应。

**示例**：

```http
HTTP/1.1 200 OK
Content-Type: image/jpeg
Content-Length: 1024
Cache-Control: max-age=3600, public
```

上述响应指示客户端和中间缓存服务器可以将响应缓存3600秒（即1小时），并且响应可以被任何缓存缓存。

### 缓存验证

当缓存的资源过期后，客户端或中间缓存服务器需要向源服务器验证资源是否仍然有效。可以通过以下两种方式实现：

1. **条件请求**：使用`If-Modified-Since`或`If-None-Match`头字段，询问源服务器资源是否已更改。
   - **示例**：

     ```http
     GET /example.jpg HTTP/1.1
     Host: www.example.com
     If-Modified-Since: Wed, 21 Oct 2024 07:28:00 GMT
     ```

2. **ETag**：服务器在响应中包含`ETag`头字段，客户端在后续请求中使用`If-None-Match`头字段验证资源是否已更改。
   - **示例**：

     ```http
     HTTP/1.1 200 OK
     Content-Type: image/jpeg
     Content-Length: 1024
     ETag: "abc123"
     Cache-Control: max-age=3600, public
     ```

     ```http
     GET /example.jpg HTTP/1.1
     Host: www.example.com
     If-None-Match: "abc123"
     ```

     如果资源未更改，服务器返回`304 Not Modified`，客户端继续使用缓存资源。

### 缓存策略的应用场景

#### 1. 公共资源

对于不包含用户特定信息且不经常变化的资源，如CSS、JavaScript、图片等，可以使用`public`和`max-age`指令进行缓存。

**示例**：

```http
Cache-Control: public, max-age=86400
```

#### 2. 私有资源

对于包含用户特定信息且需要保护隐私的资源，如个人资料、订单信息等，可以使用`private`和`no-cache`指令。

**示例**：

```http
Cache-Control: private, no-cache
```

#### 3. 动态内容

对于需要实时更新的动态内容，如实时数据、新闻等，可以使用`no-cache`或`must-revalidate`指令。

**示例**：

```http
Cache-Control: no-cache, must-revalidate
```

#### 4. 敏感信息

对于包含敏感信息且绝对不能被缓存的资源，如密码、个人隐私数据等，可以使用`no-store`指令。

**示例**：

```http
Cache-Control: no-store
```

### 缓存策略的最佳实践

1. **合理设置缓存时间**：根据资源的变化频率，合理设置`max-age`或`s-maxage`的值。
2. **使用版本化资源**：通过在资源URL中添加版本号或哈希值，避免缓存旧资源。
3. **结合使用ETag和Last-Modified**：提高缓存验证的准确性。
4. **避免不必要的缓存**：对于频繁变化或重要的资源，适当设置`no-cache`或`must-revalidate`指令。
5. **考虑安全性**：对于敏感信息，坚决使用`no-store`指令，防止信息泄露。

### 示例

#### 示例1：公共资源

```http
HTTP/1.1 200 OK
Content-Type: application/javascript
Content-Length: 2048
Cache-Control: public, max-age=31536000
```

#### 示例2：私有资源

```http
HTTP/1.1 200 OK
Content-Type: text/html
Content-Length: 1024
Cache-Control: private, no-cache
```

#### 示例3：动态内容

```http
HTTP/1.1 200 OK
Content-Type: application/json
Content-Length: 512
Cache-Control: no-cache, must-revalidate
```

#### 示例4：敏感信息

```http
HTTP/1.1 200 OK
Content-Type: application/json
Content-Length: 256
Cache-Control: no-store
```

### Varnish 中的作用

**Varnish** 在处理缓存时，会优先考虑 `max-age` 参数来计算对象的 TTL（Time To Live，生存时间），即对象在缓存中保留的时间长度。

当 Varnish 收到一个响应时，它会解析 `Cache-Control` 头信息，并提取 `max-age` 参数的值。如果 `max-age` 存在且有效，Varnish 会根据该值来设置对象的 TTL。

**具体步骤如下：**

1. **解析 `Cache-Control` 头信息：**
   - Varnish 首先检查响应中是否包含 `Cache-Control` 头。
   
2. **提取 `max-age` 参数：**
   - 如果存在 `Cache-Control`，Varnish 会进一步查找 `max-age` 参数。
   - 例如，`Cache-Control: public, max-age=600` 中，`max-age` 的值为 600 秒。

3. **计算 TTL：**
   - Varnish 将 `max-age` 的值直接作为对象的 TTL。
   - 例如，`max-age=600` 表示对象在缓存中的生存时间为 600 秒。

4. **处理 `no-cache` 指令：**
   - 如果 `Cache-Control` 中包含 `no-cache` 指令，Varnish 会忽略 `max-age` 参数，并且不对该响应进行缓存。
   - 例如，`Cache-Control: no-cache, max-age=600` 中，尽管 `max-age` 存在，但由于 `no-cache` 的存在，Varnish 不会缓存该响应。

**示例 1：**

```http
HTTP/1.1 200 OK
Content-Type: text/html
Cache-Control: public, max-age=600
```

- **解析：** 响应可以被公共缓存，`max-age` 为 600 秒。
- **行为：** Varnish 将对象的 TTL 设置为 600 秒，并在接下来的 600 秒内从缓存中提供该响应。

**示例 2：**

```http
HTTP/1.1 200 OK
Content-Type: text/html
Cache-Control: no-cache, max-age=600
```

- **解析：** 响应不能被缓存，尽管 `max-age` 为 600 秒。
- **行为：** Varnish 忽略 `max-age` 参数，不缓存该响应，每次请求都会重新从后端获取。

**示例 3：**

```http
HTTP/1.1 200 OK
Content-Type: text/html
Cache-Control: max-age=3600
```

- **解析：** 响应可以被缓存，`max-age` 为 3600 秒。
- **行为：** Varnish 将对象的 TTL 设置为 3600 秒，并在接下来的 3600 秒内从缓存中提供该响应。

在 Varnish 中，`max-age` 的优先级高于其他缓存控制指令（如 `s-maxage`）。如果同时存在多个缓存控制指令，`max-age` 通常会被优先采用，除非有其他特定指令（如 `no-cache`）明确指示不缓存。

## Age

HTTP 请求头中的 `Age` 字段是一个用于指示资源在缓存中已存在时间的响应头。虽然它通常出现在响应头中，但在某些情况下（如使用某些代理或缓存机制）也可能在请求头中出现。

`Age` 字段表示响应自源服务器生成以来经过的时间（以秒为单位）。它用于指示资源在缓存中的“年龄”，帮助客户端和中间缓存了解资源的新鲜程度。

作用:

- **指示资源的新鲜程度**：`Age` 字段告知客户端资源在缓存中已存在的时间，帮助客户端决定是否使用缓存资源。
- **辅助缓存验证**：当客户端接收到带有 `Age` 字段的响应时，可以结合 `Cache-Control` 和 `Expires` 头信息，判断资源是否仍然有效。
- **调试和监控**：服务器和中间缓存可以使用 `Age` 字段进行缓存性能监控和调试。

工作原理

当一个资源被源服务器响应并被缓存存储时，缓存开始计时。随着时间的推移，资源的 `Age` 逐渐增加。

`Age` 的值可以通过以下几种方式计算：

1. **缓存服务器上的时间差**：缓存服务器记录资源被存储的时间，并计算当前时间与存储时间的差值。
2. **经过的中间缓存**：如果资源经过多个缓存服务器，每个缓存服务器都会增加 `Age` 的值。
3. **源服务器提供的 `Date` 头**：如果响应中包含 `Date` 头，缓存服务器可以根据 `Date` 头计算 `Age`。

当缓存服务器向客户端返回响应时，会在响应头中包含 `Age` 字段。例如：

```
HTTP/1.1 200 OK
Date: Sat, 04 Jan 2025 12:00:00 GMT
Age: 300
Cache-Control: max-age=600
Content-Type: text/html
...
```

在上述例子中，`Age: 300` 表示资源在缓存中已存在了 300 秒。

### `Age` 与其他缓存头的关系

#### `Cache-Control`

`Cache-Control` 头提供了更详细的缓存指令，如 `max-age`、`no-cache`、`no-store` 等。`Age` 字段与 `Cache-Control: max-age` 结合使用，可以帮助客户端判断资源是否仍然有效。

例如，如果 `Cache-Control: max-age=600`，而 `Age: 300`，则表示资源还有 300 秒的有效期。

#### `Expires`

`Expires` 头指定资源的过期时间。如果 `Age` 加上当前时间超过 `Expires`，则资源已过期。

#### `Date`

`Date` 头表示响应被源服务器生成的时间。通过 `Date` 和 `Age`，客户端可以计算出资源的实际生成时间。

### 示例

假设客户端向 CDN 发送一个请求，CDN 从缓存中返回响应：

```
HTTP/1.1 200 OK
Date: Sat, 04 Jan 2025 12:00:00 GMT
Age: 120
Cache-Control: max-age=600
Content-Type: application/javascript
...
```

在这个例子中：

- `Age: 120` 表示资源在 CDN 缓存中已存在了 120 秒。
- `Cache-Control: max-age=600` 表示资源还有 480 秒的有效期（600 - 120 = 480）。
- 客户端可以决定是否使用缓存资源，或者在剩余的 480 秒内继续使用缓存。

### 注意事项

- **时间同步**：确保缓存服务器和客户端的系统时间同步，以避免 `Age` 计算错误。
- **多层缓存**：如果资源经过多层缓存，每个缓存服务器都会增加 `Age` 的值。因此，`Age` 的值可能比单层缓存中的实际时间要长。
- **缓存验证**：即使 `Age` 表明资源仍然有效，客户端仍可能选择重新验证资源，以确保其最新性。

### Varnish 中的作用

Varnish 默认情况下会在响应中添加 Age 头信息，无需额外配置。然而，如果你需要自定义 Age 头信息，可以在 VCL（Varnish Configuration Language）中进行配置。例如：

```vcl
sub vcl_deliver {
    # 自定义 Age 头信息
    set resp.http.Age = "自定义值";
}
```

但通常情况下，不需要手动设置 Age 头信息，因为 Varnish 会自动处理。

#### 使用 varnishlog 查看 Age 头信息

`varnishlog` 是 Varnish 提供的一个强大的日志分析工具，可以用于实时监控和调试 Varnish 的行为。通过使用特定的参数，可以筛选出包含 Age 头信息的相关日志。

#### 基本命令

```bash
varnishlog -i TxHeader -I "Age"
```

- **`varnishlog`**：启动日志记录工具。
- **`-i TxHeader`**：过滤日志，只显示与传输头（TxHeader）相关的条目。
- **`-I "Age"`**：进一步过滤，只显示包含 "Age" 字符串的头信息。

运行上述命令后，你可能会看到类似以下的输出：

```
*   TxHeader      Age: 120
```

这表示该请求的响应头中包含 Age 信息，且值为 120 秒，即该对象在 Varnish 缓存中已经保持了 120 秒。

除了 Age 头信息，`varnishlog` 还可以提供更多关于请求和响应的详细信息。例如：

- **ReqStart**：请求开始的时间。
- **ReqURL**：请求的 URL。
- **RespStatus**：响应的状态码。
- **RespHeader**：响应的头信息。

你可以结合这些信息进行更深入的分析。例如：

```bash
varnishlog -g request -q "ReqURL eq '/your-request-url'" -i TxHeader -I "Age"
```

这条命令会筛选出特定 URL 的请求，并显示其 Age 头信息。

## Pragma

**Pragma** 头信息最初在 HTTP/1.0 中引入，用于向客户端或服务器传递特定的指令。虽然在 HTTP/1.1 中 **Cache-Control** 头信息已经取代了 **Pragma** 的许多功能，但 **Pragma: no-cache** 仍然被广泛使用，尤其是在与旧版客户端或服务器通信时。

- **Pragma: no-cache**: 指示缓存服务器不要缓存响应内容，每次请求都应从源服务器获取最新内容。

在默认配置下，**Varnish** 忽略 **Pragma** 头信息。这意味着即使客户端或服务器发送了 **Pragma: no-cache**，Varnish 也不会将其作为缓存决策的依据。这主要是因为 **Pragma** 的定义较为模糊，且在现代 HTTP 协议中 **Cache-Control** 更为明确和强大。

如果你希望 **Varnish** 考虑 **Pragma** 头信息，并根据其指令来决定是否缓存响应内容，可以在 **VCL (Varnish Configuration Language)** 中进行自定义配置。以下是如何在 `vcl_fetch` 阶段增加对 **Pragma: no-cache** 支持的步骤：

```vcl
sub vcl_fetch {
    // 检查响应头中的 Pragma 是否包含 "nocache"
    if (beresp.http.Pragma ~ "nocache") {
        // 如果包含，则使用 pass 指令跳过缓存
        return (pass);
    }

    // 其他缓存逻辑
}
```

**详细解释：**

1. **sub vcl_fetch**: 定义一个在 `vcl_fetch` 阶段执行的子程序。`vcl_fetch` 是 Varnish 在从源服务器获取响应后执行的阶段，用于决定如何处理该响应。

2. **if (beresp.http.Pragma ~ "nocache")**: 检查响应头中的 **Pragma** 是否包含字符串 "nocache"。这里使用了 VCL 的正则表达式匹配操作符 `~`。

3. **return (pass)**: 如果条件满足，则调用 `pass` 指令。这指示 Varnish 不要缓存该响应，而是直接将其传递给客户端，并确保下次请求时再次从源服务器获取。

4. **其他缓存逻辑**: 如果 **Pragma** 不包含 "nocache"，则 Varnish 会根据其他缓存策略（如 **Cache-Control**）来决定是否缓存响应内容。

#### 为什么选择 `vcl_fetch` 而不是 `vcl_recv`

虽然你也可以在 `vcl_recv` 阶段处理 **Pragma** 头信息，但在 `vcl_fetch` 中处理更为合适，因为 **Pragma** 是服务器响应头的一部分，而不是客户端请求头的一部分。以下是 `vcl_recv` 和 `vcl_fetch` 的区别：

- **vcl_recv**: 处理客户端的请求头，用于决定是否从缓存中提供响应。
- **vcl_fetch**: 处理从源服务器获取的响应头，用于决定是否缓存该响应。

因此，在 `vcl_fetch` 中处理 **Pragma** 头信息更为直接和有效。

#### 完整示例

以下是一个完整的 VCL 示例，展示如何在 `vcl_fetch` 中处理 **Pragma: no-cache**：

```c
vcl 4.0;

backend default {
    .host = "127.0.0.1";
    .port = "8080";
}

sub vcl_recv {
    // 其他请求处理逻辑
}

sub vcl_fetch {
    // 检查 Pragma 头信息
    if (beresp.http.Pragma ~ "nocache") {
        // 跳过缓存
        return (pass);
    }

    // 检查 Cache-Control 头信息
    if (beresp.http.Cache-Control ~ "no-cache") {
        return (pass);
    }

    // 其他缓存逻辑
}
```

在这个示例中，除了处理 **Pragma: no-cache** 外，还处理了 **Cache-Control: no-cache**，以确保全面的缓存控制。

## Authorization

**Authorization** 是 HTTP 请求头中的一个字段，用于在客户端向服务器发送请求时，提供认证信息，以证明客户端有权访问所请求的资源。其基本语法如下：

```
Authorization: <type> <credentials>
```

- `<type>`：认证的类型（如 Basic、Digest、Bearer 等）。
- `<credentials>`：具体的认证信息，通常是经过编码或加密的凭证。

Authorization 请求头主要用于以下场景：

- **身份验证**：验证客户端的身份，确保只有授权的用户或服务可以访问资源。
- **权限控制**：根据认证信息决定客户端是否有权限执行特定操作或访问特定资源。
- **会话管理**：在无状态协议（如 HTTP）中，通过令牌（如 JWT）实现会话管理。

---

### 常见认证类型

#### Basic 认证

**Basic 认证** 是最简单的一种认证方式，客户端将用户名和密码通过 Base64 编码后发送给服务器。其基本流程如下：

1. 客户端发送未经认证的请求。
2. 服务器返回 `401 Unauthorized` 状态码，并在响应头中包含 `WWW-Authenticate: Basic realm="realm"`。
3. 客户端将用户名和密码用冒号连接（例如 `username:password`），然后进行 Base64 编码。
4. 客户端重新发送请求，并在 `Authorization` 头中包含编码后的凭证，例如 `Authorization: Basic dXNlcjpwYXNzd29yZA==`。
5. 服务器解码并验证凭证，如果成功则返回资源。

**缺点**：
- 安全性低：凭证以明文形式传输，容易被截获。
- 无法实现单点登出等高级功能。

**适用场景**：
- 仅在 HTTPS 环境下使用，以防止凭证被窃听。
- 对安全性要求不高的内部系统。

#### Digest 认证

**Digest 认证** 是一种改进的认证方式，通过哈希算法对凭证进行加密传输，以提高安全性。其基本流程如下：

1. 客户端发送未经认证的请求。
2. 服务器返回 `401 Unauthorized` 状态码，并在响应头中包含 `WWW-Authenticate: Digest` 信息。
3. 客户端根据服务器提供的随机数（nonce）和其他信息，计算出哈希值。
4. 客户端重新发送请求，并在 `Authorization` 头中包含计算后的哈希值。
5. 服务器验证哈希值，如果成功则返回资源。

**优点**：
- 凭证不会被明文传输，提高了安全性。

**缺点**：
- 实现复杂。
- 仍然存在中间人攻击等风险。

**适用场景**：
- 需要比 Basic 认证更高的安全性，但不需要使用更复杂的认证机制。

#### Bearer 认证（Token 认证）

**Bearer 认证** 是现代应用中常用的认证方式，通常使用 JSON Web Token (JWT) 或 OAuth 2.0 令牌进行认证。其基本流程如下：

1. 客户端通过某种方式（如 OAuth 2.0 授权流程）获取一个访问令牌（Access Token）。
2. 客户端在每个请求的 `Authorization` 头中包含令牌，例如 `Authorization: Bearer <token>`。
3. 服务器验证令牌的有效性，如果有效则返回资源。

**优点**：
- 安全性高：令牌可以包含丰富的声明信息，并且可以设置有效期。
- 灵活性强：可以结合 OAuth 2.0 等协议实现复杂的授权机制。
- 无状态：服务器不需要存储会话信息，减轻服务器负担。

**缺点**：
- 需要妥善管理令牌的生命周期和存储。
- 令牌泄露可能导致安全问题。

**适用场景**：
- 现代 Web 应用和 API 服务。
- 需要跨域认证和授权的场景。

#### 其他认证类型

- **HOBA (HTTP Origin-Bound Authentication)**：基于数字签名的认证机制。
- **Mutual TLS (mTLS)**：双向 TLS 认证，客户端和服务器都需要提供证书。
- **OAuth 2.0**：一种授权框架，常用于第三方应用访问用户资源。
- **OpenID Connect**：基于 OAuth 2.0 的身份验证协议。

### Authorization 的实现步骤

以下是使用 Bearer 认证（Token 认证）的实现步骤：

1. **获取令牌**：
   - 客户端通过 OAuth 2.0 授权流程或直接通过身份验证服务获取访问令牌。

2. **发送请求**：
   - 客户端在每个 HTTP 请求的 `Authorization` 头中包含访问令牌，例如：
     ```
     Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6...
     ```

3. **服务器验证**：
   - 服务器接收到请求后，解析 `Authorization` 头，提取令牌。
   - 验证令牌的签名、有效期和其他声明信息。
   - 如果验证通过，则处理请求；否则，返回 `401 Unauthorized` 或 `403 Forbidden` 状态码。

4. **返回响应**：
   - 服务器根据验证结果返回相应的响应。


### 示例

#### Basic 认证示例

**请求**：
```
GET /api/protected/resource HTTP/1.1
Host: example.com
Authorization: Basic dXNlcjpwYXNzd29yZA==
```

**响应**：
```
HTTP/1.1 200 OK
Content-Type: application/json
...
```

#### Bearer 认证示例

**请求**：
```
GET /api/protected/resource HTTP/1.1
Host: example.com
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6...
```

**响应**：
```
HTTP/1.1 200 OK
Content-Type: application/json
...
```

### Varnish 与 Authorization 头信息处理

**默认情况下，Varnish 在接收到包含 `Authorization` 头的 HTTP 请求时，会将该请求标记为不可缓存（uncacheable），并将其传递给后端服务器进行处理。** 这是因为 `Authorization` 头通常用于携带认证信息（如基本认证、OAuth 令牌等），这些信息是特定于用户的，因此不适合被缓存和共享给其他用户。

**具体原因：**
- **安全性**：缓存包含 `Authorization` 头的响应可能会导致敏感信息泄露，因为不同的用户可能有不同的权限和访问内容。
- **个性化内容**：认证后的响应通常包含个性化数据，这些数据不能被其他用户共享。

**行为表现：**
- 当 Varnish 收到带有 `Authorization` 头的请求时，它会跳过缓存查找，直接将请求转发给后端服务器。
- 响应的内容也不会被缓存，因此后续的相同请求仍会触发后端服务器的响应。


### 通过 `unset` 操作移除 `Authorization` 头

如果你的应用场景允许（例如，某些静态资源不需要认证，或者你希望在特定条件下缓存某些认证后的内容），你可以通过 **VCL（Varnish Configuration Language）** 配置来移除 `Authorization` 头信息，从而改变 Varnish 的默认行为。

**步骤如下：**

#### a. **定义 VCL 配置**

在 Varnish 的配置文件（通常是 `default.vcl`）中，你可以自定义请求和响应的处理逻辑。以下是一个示例配置，展示了如何移除 `Authorization` 头：

```vcl
vcl 4.0;

sub vcl_recv {
    # 移除 Authorization 头信息
    unset req.http.Authorization;
    
    # 其他自定义逻辑，例如：
    # 仅对特定路径进行缓存
    if (req.url ~ "^/static/") {
        return (hash);
    }
}
```

**解释：**
- `sub vcl_recv`：这是 Varnish 接收请求时执行的子程序。
- `unset req.http.Authorization;`：这行代码会移除请求中的 `Authorization` 头。
- `if (req.url ~ "^/static/")`：这是一个示例条件，用于指定哪些请求需要进行缓存处理。

#### b. **重新加载 Varnish 配置**

在修改 VCL 配置后，需要重新加载 Varnish 配置以应用更改：

```bash
sudo systemctl reload varnish
```

#### c. **验证配置**

确保新的配置已生效，可以通过以下命令查看 Varnish 的运行状态：

```bash
sudo varnishadm vcl.show boot
```

### 注意事项与最佳实践

- **安全性优先**：移除 `Authorization` 头信息可能会带来安全风险，尤其是在处理敏感数据时。确保仅在绝对必要的情况下移除该头信息，并且了解其潜在影响。
  
- **缓存策略**：根据应用需求制定合理的缓存策略。例如，可以针对不需要认证的静态资源进行缓存，而对需要认证的动态内容保持不缓存。

- **条件性移除**：在某些情况下，可能只需要在特定条件下移除 `Authorization` 头。例如，仅在请求的 URL 匹配特定模式时移除：

  ```vcl
  sub vcl_recv {
      if (req.url ~ "^/public/") {
          unset req.http.Authorization;
      }
  }
  ```

- **调试与监控**：在修改缓存策略后，密切监控缓存命中率、后端服务器负载以及应用性能，确保新策略不会引入新的问题。

### 示例场景

假设你有一个网站，其中包含以下内容：

- **静态资源**：如图片、CSS、JavaScript 文件，这些资源不需要认证，可以被所有用户访问。
- **动态内容**：如用户个人资料、订单信息，这些内容需要认证。

**优化策略：**

1. **针对静态资源**：
   - **移除 `Authorization` 头**，允许 Varnish 缓存这些资源。
   - **配置缓存策略**，例如设置较长的缓存时间（`Cache-Control: max-age=86400`）。

   ```vcl
   sub vcl_recv {
       if (req.url ~ "^/static/") {
           unset req.http.Authorization;
           return (hash);
       }
   }
   ```

2. **针对动态内容**：
   - **保留 `Authorization` 头**，确保 Varnish 不缓存这些请求，直接转发给后端服务器。

   ```vcl
   sub vcl_recv {
       if (req.url !~ "^/static/") {
           return (pass);
       }
   }
   ```

## Cookies

### Varnish 不会缓存带有 Set-Cookie 头信息的响应

#### **原因分析**
- **会话管理和个性化内容**：Web 服务器通过 **Set-Cookie** 头信息向客户端发送会话标识符或其他个性化数据。这通常意味着服务器生成的内容是针对特定用户的，不能被其他用户共享或重复使用。
  
- **安全性考虑**：如果 Varnish 缓存了带有 **Set-Cookie** 头信息的响应，可能会导致以下问题：
  - **会话劫持**：其他用户可能会收到包含其他用户会话标识符的响应，导致会话被劫持。
  - **数据泄露**：敏感的用户数据可能被错误地展示给其他用户。
  - **功能异常**：例如，购物车内容、用户偏好设置等个性化数据可能无法正确更新或显示。

#### **具体行为**
- 当 Varnish 从后端服务器接收到带有 **Set-Cookie** 头信息的响应时，它会识别该响应是针对特定用户的。
- Varnish 会绕过缓存，直接将响应传递给客户端，而不会将该响应存储在其缓存中。
- 这样可以确保每个用户接收到的响应都是最新的、个性化的，并且不包含其他用户的敏感信息。

### 如果客户端发送了 Cookie 头信息，Varnish 将忽略缓存，直接发给后端

#### **原因分析**
- **用户身份标识**：客户端发送的 **Cookie** 头信息通常包含用户的会话标识符或其他标识信息，用于告诉服务器用户的身份和状态。
  
- **个性化请求处理**：服务器需要根据这些 Cookie 信息来生成特定于用户的内容。例如，显示用户的登录状态、个性化推荐、用户设置等。

- **避免缓存污染**：如果 Varnish 使用包含 Cookie 的请求来查找缓存，可能会导致以下问题：
  - **缓存污染**：不同用户的请求可能会命中同一个缓存条目，导致返回错误的响应。
  - **数据不一致**：用户可能会看到其他用户的数据或状态。

#### **具体行为**
- 当 Varnish 接收到包含 **Cookie** 头信息的客户端请求时，它会识别该请求为需要个性化处理的请求。
- Varnish 会绕过缓存，直接将请求转发给后端服务器。
- 后端服务器根据 **Cookie** 信息生成响应，并返回给 Varnish，Varnish 再将响应传递给客户端。

### **Varnish 的缓存策略总结**

1. **不缓存带有 Set-Cookie 的响应**：确保每个用户接收到的响应都是最新的、个性化的，并且不包含其他用户的敏感信息。
2. **不缓存包含 Cookie 的请求**：确保每个请求都是根据用户的特定状态进行处理的，避免缓存污染和数据不一致。

### **如何优化 Varnish 的缓存策略**

尽管 Varnish 默认不缓存带有 **Set-Cookie** 或 **Cookie** 头信息的请求和响应，但开发者可以通过以下方法优化缓存策略：

- **使用 Vary 头**：通过 **Vary** 头信息，Varnish 可以根据不同的请求头（如 **User-Agent**、**Accept-Language** 等）来区分不同的缓存版本。
  
- **ESI（Edge Side Includes）**：使用 ESI 可以将页面分解为多个片段，其中一些片段可以被缓存，而其他片段则需要动态生成。
  
- **自定义缓存规则**：通过 VCL（Varnish Configuration Language）自定义缓存规则，例如，根据 URL 模式、请求参数等来决定是否缓存响应。

### **示例 VCL 配置**

以下是一个简单的 VCL 配置示例，展示了如何根据 **Cookie** 和 **Set-Cookie** 头信息来决定是否缓存响应：

```vcl
vcl 4.0;

sub vcl_recv {
    # 如果请求中包含 Cookie，则不缓存
    if (req.http.Cookie) {
        return (pass);
    }
}

sub vcl_backend_response {
    # 如果响应中包含 Set-Cookie，则不缓存
    if (beresp.http.Set-Cookie) {
        set beresp.uncacheable = true;
        set beresp.ttl = 0s;
        return (deliver);
    }
}
```

## Vary

`Vary` 头是一个 HTTP 响应头，用于告知缓存服务器和客户端哪些请求头字段会影响响应的内容变化。当缓存服务器接收到一个带有 `Vary` 头的响应时，它会根据 `Vary` 中指定的请求头字段来决定是否可以使用缓存的响应，或者是否需要重新向源服务器请求新的内容。

### 语法

```http
Vary: <header-name>, <header-name>, ...
```

- `<header-name>`：一个或多个请求头字段的名称，如 `Accept-Encoding`、`User-Agent` 等。

### 示例

```http
Vary: Accept-Encoding, User-Agent
```

上述示例表示响应的内容会根据 `Accept-Encoding` 和 `User-Agent` 请求头字段的不同而变化。

### `Vary` 的主要用途

#### 内容协商

内容协商是指服务器根据客户端的请求头（如 `Accept`、`Accept-Language`、`Accept-Encoding` 等）来决定返回不同格式、语言或编码的内容。`Vary` 头用于指示哪些请求头字段参与了内容协商，从而影响缓存的决策。

#### 缓存控制

缓存服务器（如 CDN、浏览器缓存）使用 `Vary` 头来区分不同类型的请求。例如，如果一个响应头中包含 `Vary: Accept-Encoding`，则缓存服务器会根据 `Accept-Encoding` 请求头的不同值来分别缓存不同的响应版本（如 gzip 压缩和未压缩版本）。

### `Vary` 的工作原理

#### 缓存决策

当缓存服务器接收到一个带有 `Vary` 头的响应时，它会根据 `Vary` 中指定的请求头字段来区分不同的请求。例如，如果 `Vary` 头中包含 `Accept-Encoding`，则缓存服务器会为每个不同的 `Accept-Encoding` 值（如 `gzip`, `deflate` 等）分别缓存响应。

#### 缓存命中

当缓存服务器接收到一个新的请求时，它会检查请求头中与 `Vary` 头中指定的字段是否匹配。如果匹配，则可以使用缓存的响应；否则，需要向源服务器重新请求内容。

#### 缓存存储

缓存服务器会为每个不同的 `Vary` 组合存储不同的缓存版本。例如，如果 `Vary` 头中包含 `Accept-Encoding` 和 `User-Agent`，则缓存服务器会根据不同的 `Accept-Encoding` 和 `User-Agent` 组合分别存储不同的缓存版本。

### 常见的 `Vary` 头字段

#### `Accept-Encoding`

指示响应的内容会根据 `Accept-Encoding` 请求头的不同而变化。例如，`Accept-Encoding: gzip, deflate` 表示客户端可以接受 gzip 或 deflate 压缩的响应。

#### `User-Agent`

指示响应的内容会根据 `User-Agent` 请求头的不同而变化。例如，不同的浏览器或设备可能会有不同的 `User-Agent` 值，服务器可能会根据这些值返回不同的内容。

#### `Accept-Language`

指示响应的内容会根据 `Accept-Language` 请求头的不同而变化。例如，客户端可能会根据 `Accept-Language` 请求头指定的语言偏好，服务器可能会返回相应语言的内容。

#### `Accept`

指示响应的内容会根据 `Accept` 请求头的不同而变化。例如，客户端可能会指定不同的媒体类型（如 `text/html`, `application/json` 等），服务器可能会返回相应媒体类型的内容。

### `Vary` 头的使用场景

#### 动态内容

对于动态生成的内容，如果内容会根据某些请求头字段的不同而变化，则需要使用 `Vary` 头。例如，服务器可能会根据 `Accept-Language` 返回不同语言的内容。

#### 压缩内容

对于压缩的内容，如果服务器根据 `Accept-Encoding` 返回不同的压缩版本，则需要使用 `Vary: Accept-Encoding`。

#### 移动设备优化

如果服务器根据 `User-Agent` 返回针对移动设备优化的内容，则需要使用 `Vary: User-Agent`。

### `Vary` 头与缓存策略

#### 缓存失效

当 `Vary` 头中指定的请求头字段发生变化时，缓存服务器需要重新请求内容。例如，如果 `Vary: Accept-Encoding` 且客户端请求头中 `Accept-Encoding` 发生变化，则缓存服务器需要向源服务器重新请求内容。

#### 缓存命中率

合理使用 `Vary` 头可以提高缓存命中率。例如，如果 `Vary` 头中指定的请求头字段变化频繁，则缓存命中率可能会降低。因此，需要根据实际情况合理选择 `Vary` 头中指定的字段。

#### 缓存存储

`Vary` 头会影响缓存存储的大小。例如，如果 `Vary` 头中指定的请求头字段较多，则缓存服务器需要存储更多的缓存版本，可能会增加存储压力。

### 注意事项

#### 合理选择 `Vary` 字段

选择 `Vary` 头中指定的请求头字段时，需要考虑缓存命中率、存储压力以及内容变化频率等因素。例如，如果内容变化频繁，则可以考虑使用 `Vary: Cookie` 或其他变化较少的字段。

#### 避免过度使用 `Vary`

过度使用 `Vary` 头可能会导致缓存命中率降低，增加服务器负载。例如，如果 `Vary` 头中指定的请求头字段较多，则缓存服务器需要存储更多的缓存版本，可能会增加存储压力。

#### 结合其他缓存策略

`Vary` 头应与其他缓存策略（如 `Cache-Control`, `ETag` 等）结合使用，以实现更有效的缓存控制。例如，可以使用 `Cache-Control` 来指定缓存的最大年龄，使用 `ETag` 来实现细粒度的缓存验证。

### 示例

#### 示例 1：基于 `Accept-Encoding` 的内容协商

```http
GET /example HTTP/1.1
Host: www.example.com
Accept-Encoding: gzip, deflate
```

```http
HTTP/1.1 200 OK
Content-Type: text/html
Content-Encoding: gzip
Vary: Accept-Encoding
Content-Length: 1234

[压缩 content ]
```

在上述示例中，服务器根据 `Accept-Encoding` 请求头返回 gzip 压缩的内容，并在 `Vary` 头中指定 `Accept-Encoding`，以指示缓存服务器根据 `Accept-Encoding` 的不同值来区分缓存。

#### 示例 2：基于 `User-Agent` 的内容协商

```http
GET /example HTTP/1.1
Host: www.example.com
User-Agent: Mozilla/5.0 (iPhone; CPU iPhone OS 12_0 like Mac OS X)
```

```http
HTTP/1.1 200 OK
Content-Type: text/html
Vary: User-Agent
Content-Length: 1234

[ 针对移动设备优化的内容 ]
```

在上述示例中，服务器根据 `User-Agent` 请求头返回针对移动设备优化的内容，并在 `Vary` 头中指定 `User-Agent`，以指示缓存服务器根据 `User-Agent` 的不同值来区分缓存。

### Varnish中的Vary头信息详解

在Web缓存技术中，**Varnish** 是一个高性能的开源HTTP加速器，用于加速动态网站和API的响应速度。在Varnish中，**Vary头信息** 是一个非常重要的概念，它指示缓存系统根据某些HTTP头信息的变化来区分不同的HTTP对象版本。下面将详细展开讲讲Vary头信息在Varnish中的具体作用和影响，特别是通过**Accept-Encoding**头信息来理解。

#### 1. **什么是Vary头信息？**

**Vary** 头信息是由Web服务器发送的HTTP响应头，用于指示缓存系统根据哪些请求头信息的变化来区分不同的HTTP对象版本。简单来说，Vary头信息告诉缓存系统哪些请求头会影响响应的内容，因此缓存系统需要为不同的请求头组合分别缓存不同的响应版本。

例如，常见的Vary头信息包括：

- `Vary: Accept-Encoding`
- `Vary: User-Agent`
- `Vary: Accept-Language`
- `Vary: Cookie`

#### 2. **Vary: Accept-Encoding 的作用**

**Accept-Encoding** 是HTTP请求头中的一个字段，用于指示客户端支持的压缩编码类型。常见的编码类型包括：

- `gzip`
- `deflate`
- `br`（Brotli）

当服务器响应中包含 `Vary: Accept-Encoding` 时，这意味着缓存系统需要为每个不同的 `Accept-Encoding` 值缓存不同的响应版本。例如：

- 如果客户端发送 `Accept-Encoding: gzip`，服务器会返回经过gzip压缩的响应，缓存系统会缓存这个版本。
- 如果客户端发送 `Accept-Encoding: deflate`，服务器会返回经过deflate压缩的响应，缓存系统也会缓存这个版本。

因此，Varnish会根据 `Accept-Encoding` 的不同值来区分不同的响应版本，确保每个客户端都能获得其支持的压缩编码版本。

#### 3. **多个不同的Accept-Encoding值**

在某些情况下，客户端可能会发送多个不同的 `Accept-Encoding` 值，例如：

```
Accept-Encoding: deflate, gzip
```

这意味着客户端支持 `deflate` 和 `gzip` 两种编码方式。在这种情况下，Varnish会按照以下步骤处理：

1. **优先级排序**：Varnish会按照客户端发送的顺序优先选择第一个支持的编码方式。
2. **缓存匹配**：Varnish会检查缓存中是否存在与第一个 `Accept-Encoding` 值匹配的响应版本。如果存在，则直接返回该版本。
3. **缓存缺失**：如果缓存中不存在与第一个 `Accept-Encoding` 值匹配的响应版本，Varnish会向服务器请求该响应，并在缓存中存储该版本。

因此，如果客户端发送 `Accept-Encoding: deflate, gzip`，Varnish会首先尝试使用 `deflate` 编码的响应版本。如果缓存中没有 `deflate` 版本的响应，则会使用 `gzip` 版本的响应。

#### 4. **规范Accept-Encoding头信息**

为了减少缓存中的不同版本数量，优化缓存命中率，可以对 `Accept-Encoding` 头信息进行规范处理。例如：

- **限制支持的编码类型**：只允许客户端发送 `gzip` 和 `deflate`，避免其他不常见的编码类型。
- **优先使用gzip**：如果客户端支持 `gzip`，优先使用 `gzip` 编码。
- **缓存命中优化**：通过规范 `Accept-Encoding` 头信息，减少不同版本的数量，提高缓存命中率。

例如，可以将 `Accept-Encoding: deflate, gzip` 规范为 `Accept-Encoding: gzip, deflate`，确保缓存中只存储 `gzip` 和 `deflate` 两种版本。

#### 5. **具体示例**

假设服务器响应中包含以下头信息：

```
Vary: Accept-Encoding
Content-Encoding: gzip
```

并且客户端发送的请求头信息为：

```
Accept-Encoding: gzip, deflate
```

在这种情况下，Varnish会：

1. 识别 `Vary: Accept-Encoding`，知道需要根据 `Accept-Encoding` 的不同值来区分不同的响应版本。
2. 检查缓存中是否存在 `Accept-Encoding: gzip` 的响应版本。
3. 如果存在，则直接返回该版本。
4. 如果不存在，则向服务器请求 `gzip` 编码的响应，并在缓存中存储该版本。

如果客户端发送 `Accept-Encoding: deflate, gzip`，Varnish会：

1. 识别 `Vary: Accept-Encoding`。
2. 检查缓存中是否存在 `Accept-Encoding: deflate` 的响应版本。
3. 如果存在，则返回 `deflate` 版本的响应。
4. 如果不存在，则检查 `Accept-Encoding: gzip` 的版本，并返回 `gzip` 版本的响应。

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

## 配置解析

```vcl
sub vcl_recv {
    # 设置请求的 Grace 时间为 15 秒, 在这里设置 req.grace 可以确保在请求的早期阶段就启用了 Grace 模式，从而使 Varnish 在缓存对象过期后的一段时间内继续提供旧的内容。这意味着在缓存对象过期后的 15 秒内，Varnish 仍然可以提供旧的内容。
    set req.grace = 15s;
    return (hash);
}
```

在 `vcl_recv` 中，我们设置了请求的 Grace 时间为 15 秒。这意味着即使缓存对象已经过期，Varnish 也能够在接下来的 15 秒内继续提供旧的内容。这对于在高并发情况下减少后端服务器的压力非常有帮助。

**注意，这里的 15s 依赖于下面配置的 beresp.grace = 2m，beresp.grace = 2m 的意思是在 TTL 过期后继续在内存中保持 2m，这里的 15s 是指当TTL过期且后端宕机的情况下，继续提供 15s 的服务。**

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

## 注意事项

**调整 TTL 和 Grace 时间**：
   - **TTL**：根据内容的更新频率和业务需求，调整 TTL 的值。例如，如果内容很少变化，可以将 TTL 设置得长一些；如果内容频繁更新，则需要设置得短一些。
   - **Grace 时间**：Grace 时间应设置得足够长，以便在后台获取新内容时能够覆盖到可能出现的延迟。但是，过长的 Grace 时间可能导致用户看到过期的内容。因此，需要根据实际情况进行权衡。

   ```vcl
   sub vcl_backend_response {
       set beresp.ttl = 10m;    # 调整为 10 分钟
       set beresp.grace = 5m;   # 调整为 5 分钟
   }
   ```

## 结合 Saint 模式

**结合 Saint 模式**：
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

   在上述配置中，如果后端服务器返回 5xx 错误，Varnish 会暂停对该服务器的请求 10 秒，并在 10s 后尝试重新启动请求。这或多或少可以算是一个黑名单，restart 被执行时，如果我们有其他后端可以提供该内容，Varnish 会请求它们。当没有其他后端可用，Varnish 就会提供缓存中的旧内容。

**使用 `beresp.keep`**：
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

# 缓存策略

Varnish 支持多种缓存策略，以满足不同的需求：

- **基于时间的缓存策略**：通过设置 TTL（Time-To-Live）来控制缓存的生命周期。
- **基于内容的缓存策略**：通过 ETag 和 Last-Modified 头部信息来控制缓存的有效性。
- **缓存失效策略**：如在收到后端服务器的响应时，设置缓存失效条件。

# 控制台指令  

在启动Varnish的时候，已经通过`-T`的参数指定了管理Varnish的IP地址和端口。现在就可以连接这个IP地址和端口来进行Varnish的管理，有两种连接方式：

第一种：`telnet ip port`的方式，会进入一个纯文本的命令行管理界面。

第二种：`varnishadm -T ip:port`的方式，进入Varnish的命令行管理界面。

这两种方式都是进入Varnish Command Line Interface，简称CLI，在CLI里面可以控制和改变大多数Varnish运行的参数和配置，而无须中断Varnish的服务。

这里主要学习第二种

## varnishadm CLI

语法

```bash
varnishadm [-t timeout] [-S secret_file] [-T address:port] [-n name] [command [...]]
```

- `-t timeout`：等待一个操作完成的时间，单位秒。
- `-S secret_file`：确定一个认证的安全文件。
- `-T address:port`：连接到管理接口的地址和端口，在启动Varnish时指定的。
- `-n name`：连接到管理接口的名字，在启动Varnish时指定的。

后面还可以直接跟要执行的命令，如果不跟命令，就会进入CLI的界面。

假设你已经启动了 Varnish，并且指定了管理接口的地址和端口为 `127.0.0.1:6082`，你可以使用以下命令来连接到 Varnish 的管理接口并执行一些命令：

```bash
varnishadm -T 127.0.0.1:6082 -S /etc/varnish/secret
```

在这个命令中：
- `-T 127.0.0.1:6082` 指定了管理接口的地址和端口。
- `-S /etc/varnish/secret` 指定了认证的安全文件路径。

执行上述命令后，你将进入 Varnish 的命令行管理界面（CLI），可以在其中执行各种管理命令。例如，你可以使用 `help` 命令来查看可用的命令列表：

```bash
help
```

这将列出所有可用的 CLI 命令，例如 `start`、`stop`、`status`、`ban` 等。你可以使用这些命令来管理 Varnish 的运行状态和缓存策略。

例如，要查看 Varnish 的当前状态，可以使用以下命令：

```bash
status
```

如果要清除缓存中所有匹配的 URL，可以使用 `ban` 命令：

```bash
ban req.url ~ .
```

这个命令将清除所有缓存的 URL。

通过这种方式，你可以方便地管理和配置 Varnish，而无需重启服务。

CLI主要能完成如下的功能：

1. 配置：能上传、修改和删除VCL文件
2. 参数：能查看和修改各种参数
3. 清除缓存：可以清除Varnish中的缓存内容
4. 进程管理：可以启动或者停止缓存子进程

## 常用命令

- **backend.list**：这个命令用于列出定义中的所有backend，包括它们的健康状态。通过这个命令，你可以查看当前所有backend的健康状况，便于监控和管理。

- **backend.set_health matcher state**：这个命令用于为某个backend设置健康状态。当你想要把某个backend从使用列表中移出时，这个命令会非常有用。通过设置健康状态，你可以控制Varnish如何处理与该backend相关的请求。

## 配置文件热更新

首先，可以使用 `vcl.list` 命令查看当前已经加载的所有VCL配置文件列表。

```bash
vcl.list
```

这个命令会列出所有已加载的VCL文件及其状态（例如active或available）。

active：当前使用的配置文件
available：可用的配置文件

接下来，可以使用 `vcl.load` 命令加载一个新的VCL配置文件。例如，如果你的新配置文件名为 `newconfig.vcl`，可以使用以下命令加载：

```bash
vcl.load newconfig newconfig.vcl
```

这里，`newconfig` 是你给这个新配置的名称（随便写），`newconfig.vcl` 是VCL文件的路径。

加载完成后，可以使用 `vcl.show` 命令查看新加载的VCL文件内容，以确保其正确加载。

```bash
vcl.show newconfig
```

这个命令会输出 `newconfig` 这个VCL配置文件的内容。

最后，使用 `vcl.use` 命令将新的VCL配置文件设置为当前使用的配置。

```bash
vcl.use newconfig
```

执行这个命令后，Varnish将开始使用 `newconfig` 这个VCL配置文件进行请求处理。

通过以上步骤，你可以实现VCL配置文件的热更新，而无需重启Varnish服务。这种方式非常适合在生产环境中进行配置更改，因为它最大限度地减少了服务中断时间。

以下是整个过程的命令汇总：

```bash
# 查看当前加载的VCL列表
vcl.list

# 加载新的VCL配置文件
vcl.load newconfig newconfig.vcl

# 查看新加载的VCL文件内容
vcl.show newconfig

# 使用新的VCL配置文件
vcl.use newconfig
```

### vcl.discard

**vcl.discard configname**：废弃某个配置。注意，如果这个配置的引用不为0的话，简单地说就是已经使用了，这个命令无效。

这个命令用于从Varnish中移除一个不再需要的VCL配置。但是，如果这个配置当前正在被使用（即引用计数不为0），那么这个命令将不会生效。因此，在使用`vcl.discard`命令之前，需要确保目标配置没有被任何正在运行的Varnish实例引用。

### vcl.inline

`vcl.inline` 命令用于内联创建一个新的VCL配置。一般很少用，都用 vcl.load 会更方便

命令格式如下：
```bash
vcl.inline configname 'vcl_code'
```

其中：
- `configname` 是你要创建的配置的名称。
- `vcl_code` 是实际的VCL代码。

例如，如果你想创建一个名为`myconfig`的VCL配置，可以使用如下命令：
```bash
vcl.inline myconfig 'vcl 4.0;
backend default {
    .host = "127.0.0.1";
    .port = "8080";
}'
```

这个命令会创建一个名为`myconfig`的新配置，其中包含指定的VCL代码。这样，你就可以在Varnish中使用和管理这个新的配置了。

## 清除缓存 ban

使用 `ban` 命令来清除Varnish缓存中的内容。具体内容如下：

- `ban field operator argument [&& field operator argument [...]]`：使得匹配 `ban` 表达式的内容从缓存中清除。

详细说明：

1. **一个 `ban` 表达式包含一到多个条件**，一个条件由一个字段、一个操作符、一个参数构成，多个条件之间可以用 `&&` 来表示逻辑与的关系。

2. **字段可以是任意的VCL变量**，例如：`req.url`, `req.http.host` 或 `obj.http.set-cookie` 等。

3. **操作符有**：表示等于 `=`, 匹配正则表达式 `~`, 大于 `>`, 小于 `<`, 非 `!` 等。

4. **参数可以是一个用双引号引起来的字符串，也可以是正则表达式，或者数字**。数字后面可以跟 `KB`, `MB`, `GB` 或 `TB` 等。

举个例子，如果你想要清除所有URL中包含特定路径的缓存，可以使用如下命令：

```bash
ban req.url ~ "/specific-path"
```

这个命令将会清除所有URL中包含 `/specific-path` 的缓存内容。

其他示例

- 清除请求url完全匹配`/news`的缓存内容：`ban req.url == "/news"`
- 清除请求url不以`.ogg`结尾，同时对象的大小不能大于10MB的缓存内容：`ban req.url !~ ".ogg$" && obj.size > 10MB`
- 清除host为example.com或者www.example.com，同时从backend获得set-cookie头里面包含`userid=1663`的缓存内容：`ban req.http.host ~ "(?i)(www\.)example\.com$" && obj.http.set-cookie ~ "USERID=1663"`

## ban.list

ban.list是Varnish缓存服务器中用于管理和查看ban规则的工具。通过ban.list，可以清晰地看到哪些缓存内容被ban，以及这些ban规则的详细信息。这对于管理和调试缓存策略非常有帮助。

ban.list是一个用于管理缓存ban规则的列表。以下是一些关键点：

1. **缓存内容标记为banned**：
   - 如果缓存的内容比ban旧，那么这个缓存内容会被标记为banned，不能再使用这个缓存内容，需要从backend获取内容。

2. **ban列表更新**：
   - 如果ban的表达式比所有的缓存对象都旧的话，它将从ban的list里面去掉。

3. **ban.list输出的格式**：
   - ban.list输出的格式如下：
     ```plaintext
     0x7fea4fcb0580 1303835108.618863 131G req.http.host ~ www.myhost.com && req.url ~ /some/url
     ```
     - 第一个字段是ban的地址。
     - 第二个字段是时间戳。
     - 第三个字段表示有多少个对象受此ban的影响（通常在debug时才有效）。
     - G表示这个ban已经Gone，也就不再有效了。
     - 后面的内容才是真正的ban的内容。

以下是ban.list输出的一个示例：
```plaintext
0x7fea4fcb0580 1303835108.618863 131G req.http.host ~ www.myhost.com && req.url ~ /some/url
```
- `0x7fea4fcb0580`：这是ban的地址。
- `1303835108.618863`：这是时间戳，表示这个ban规则生效的时间。
- `131G`：表示有131个对象受到这个ban的影响，G表示这个ban已经Gone，不再有效。
- `req.http.host ~ www.myhost.com && req.url ~ /some/url`：这是实际的ban规则，表示当请求的主机名匹配`www.myhost.com`并且请求的URL匹配`/some/url`时，缓存内容会被ban。

## ban.url

ban.url regexp：要ban的url
- 匹配这个规则的url，缓存立即失效，注意一点：在这个url里面host是被忽略的。

## help

help [command]：显示命令的帮助
- 不写命令就显示所有的命令。

## param.set

param.set：设置param的值
- 设置param的值。

## 其他常用命令

1. **param.show [-l] [param]**：
   - 显示param以及他们的值。
   - `-l`选项会带着命令的简短描述。

2. **ping [timestamp]**：
   - 用于ping Varnish的缓存进程，保持连接是活动的。

3. **quit**：
   - 退出CLI管理。

4. **start**：
   - 如果Varnish的缓存进程没有启动的话，启动它。

5. **status**：
   - 检查Varnish的缓存进程的状态。

6. **stop**：
   - 停止Varnish的缓存进程。

7. **storage.list**：
   - 列表显示定义的storage backends。

### VCL命令

1. **vcl.discard configname**：
   - 废弃某个配置。注意，如果这个配置的引用不为0的话，简单地说就是已经使用了，这个命令无效。

2. **vcl.inline configname vcl**：
   - 使用VCL来创建一个新的配置。

3. **vcl.list**：
   - 列出可用的配置，以及参照使用他们的数量。

4. **vcl.load configname filename**：
   - 创建一个新的配置，配置的名字是configname，内容是filename指定的文件内容。

5. **vcl.show configname**：
   - 显示配置的具体内容。

6. **vcl.use configname**：
   - 使用哪一个配置。

### 示例解释

以下是一些命令的实际应用示例：

- **param.show**：
  ```bash
  param.show -l
  ```
  这个命令会显示所有参数及其简短描述。

- **ping**：
  ```bash
  ping
  ```
  这个命令用于ping varnish的缓存进程，确保连接是活动的。

- **quit**：
  ```bash
  quit
  ```
  这个命令用于退出CLI管理界面。

- **start**：
  ```bash
  start
  ```
  这个命令用于启动Varnish缓存进程，如果它还没有启动的话。

- **status**：
  ```bash
  status
  ```
  这个命令用于检查Varnish缓存进程的状态。

- **stop**：
  ```bash
  stop
  ```
  这个命令用于停止Varnish缓存进程。

- **storage.list**：
  ```bash
  storage.list
  ```
  这个命令用于列出所有定义的storage backends。

- **vcl.discard configname**：
  ```bash
  vcl.discard myconfig
  ```
  这个命令用于废弃名为`myconfig`的VCL配置。

- **vcl.inline configname vcl**：
  ```bash
  vcl.inline myconfig "vcl 4.0;"
  ```
  这个命令用于创建一个新的VCL配置，内容为`vcl 4.0;`。

- **vcl.list**：
  ```bash
  vcl.list
  ```
  这个命令用于列出所有可用的VCL配置及其引用数量。

- **vcl.load configname filename**：
  ```bash
  vcl.load myconfig /path/to/config.vcl
  ```
  这个命令用于加载一个VCL配置文件，配置名为`myconfig`，文件路径为`/path/to/config.vcl`。

- **vcl.show configname**：
  ```bash
  vcl.show myconfig
  ```
  这个命令用于显示名为`myconfig`的VCL配置的具体内容。

- **vcl.use configname**：
  ```bash
  vcl.use myconfig
  ```
  这个命令用于使用名为`myconfig`的VCL配置。

# Varnishd 命令和运行期参数

## Varnishd

命令语法
```
varnishd [-a address[:port]] [-b host[:port]] [-d] [-F] [-f config] [-g group] [-h type[,options]] [-i identity] [-l shmlogsize] [-n name] [-P file] [-p param=value] [-s type[,options]] [-T address[:port]] [-t ttl] [-u user] [-V] [-w min[,max[,timeout]]]
```

- `-a address[:port][,address]`
  - 监听指定的IP地址和端口的请求。地址可以是主机名（如“localhost”），或者一个IPv4（如“127.0.0.1”），和IPv6（如“[::1]”）。如果地址没有明确指定，varnishd将监听所有可用的IPv4和IPv6地址。如果端口没有指定，那么varnishd默认监听/etc/services中的HTTP对应的端口。更多的端口和地址使用逗号分隔。
  
- `-b host[:port]`
  - 指定后端服务器的地址和端口，如果没有端口，默认是8080。

- `-C`
  - 编译VCL代码成C语言，然后退出，指定VCL文件用-f参数。

- `-d`
  - 开启debug模式。主进程在前台启动，提供一个CLI界面，用于标准输入输出。子进程必须通过CLI命令启动。如果结束主进程，那么子进程也会结束。

- `-F`
  - 在前台运行。

- `-f config`
  - 使用指定的VCL配置文件代替系统默认的。

- `-g group`
  - 指定Varnishd子进程使用的用户组。

- `-h type[,options]`
  - 指定hash算法。

- `-i identity`
  - 指定Varnishd server的身份。

- `-l shmlogsize`
  - 指定shmlogfile的大小，单位可以使用‘k’和‘m’，默认是80M，不要指定比80M小的值。

- `-n name`
  - 为这个实例指定一个名字。

- `-P file`
  - 指定pidfile，用于保存PID信息。

- `-p param=value`
  - 设置指定参数的值。

- `-S file`
  - 访问管理端口用到的安全认证文件的路径。

- `-s [name=]type[,options]`
  - 使用指定的存储后端，可以多次使用此选项指定不同的存储类型。

- `-T address[:port]`
  - 提供一个管理接口的地址和端口。

- `-t ttl`
  - 给缓存中的内容指定最小的ttl。

- `-u user`
  - 指定运行varnishd子进程的用户。

- `-V`
  - 显示Varnishd的版本，然后退出。

- `-w min[,max[,timeout]]`
  - 指定线程最小和大空闲时间，这是一个设置thread_pool_min、thread_pool_max和thread_pool_timeout的捷径。如果只有一个值被指定，那么thread_pool_min和thread_pool_max都是用这个值。Thread_pool_timeout会失效。

## 内置 Hash 算法

**simple_list**是一种简单的doubly-linked链表。这种数据结构的特点是每个节点都有两个指针，一个指向前一个节点，另一个指向后一个节点。虽然这种数据结构实现简单，但在生产环境中并不推荐使用。原因在于它的查找效率较低，时间复杂度为O(n)，不适合需要频繁查找和插入的场景。

**classic**是一种标准的hash table，也是默认使用的Hash算法。hash table通过将键值对存储在数组的桶（buckets）中，利用哈希函数将键映射到数组索引，从而实现快速查找。其优点是查找、插入和删除的平均时间复杂度为O(1)，非常适合需要高效数据访问的场景。然而，hash table的性能依赖于哈希函数的质量和桶的数量。如果哈希函数设计不当或桶的数量不足，可能会导致大量的哈希冲突，从而影响性能。

**critbit**是一种自适应的树结构，相比传统的B-tree，critbit-tree几乎不用锁，性能更好。critbit-tree是一种基于字典树的变种，适用于需要频繁插入和查找的场景。其特点是每个节点存储一个键值对和一个临界位，通过比较键的临界位来决定分支方向。由于不需要锁机制，critbit-tree在高并发环境下表现出色，能够有效减少锁竞争，提高系统的整体性能。

## 存储类型

### 1. Malloc[, size]
**Malloc**是基于内存的存储方式。`Size`参数指定分配给Varnish的最大内存，默认单位是byte。你可以使用K、M、G、T等单位来指定大小，例如：
- `Malloc,1G`：分配1GB内存给Varnish。
- `Malloc,512M`：分配512MB内存给Varnish。

默认情况下，`Size`没有限制，这意味着Varnish将使用尽可能多的内存。

### 2. file[, path[, size[, granularity]]]
**file**是采用文件来存储对象，然后使用mmap将这些文件映射到内存，这是Varnish缺省的存储方式。以下是各参数的详细说明：
- `path`：指定存储的路径和文件名，默认是/tmp。
- `size`：指定文件的最大尺寸，默认单位是byte。同样可以使用K、M、G、T等单位。例如：
  - `file,/var/lib/varnish,2G`：在/var/lib/varnish目录下创建一个最大2GB的文件。
  - `file,/var/lib/varnish,1T`：在/var/lib/varnish目录下创建一个最大1TB的文件。

还可以使用百分比来表示使用系统空闲空间的百分比，默认是50%。例如：
- `file,/var/lib/varnish,50%`：使用系统空闲空间的50%。

- `granularity`：这个参数指定了间隔的尺寸，默认是字节，可以指定单位，但不能使用%。默认的间隔尺寸是VM的page大小。如果你有太多的小文件，这个值可以小一点。例如：
  - `file,/var/lib/varnish,,4K`：指定间隔尺寸为4KB。

其他注意事项
- 如果文件已经存在，varnish会缩小或放大backing文件到指定的size。
- 如果Varnishd需要创建或者扩大一个文件，它不会去预分配需要的空间，而之前又没有设置好空间的话，可能会产生碎片，影响性能。在分配文件前使用dd命令来创建文件，可以尽量减少碎片。

性能考虑
文件的性能通常取决于设备的写入速度，还有使用上的寻找时间。选择合适的存储类型和配置参数，可以显著提高Varnish的性能和稳定性。

## Transient Storage

如果你将一个Storage的name设置为Transient，它将用来存储临时对象。缺少的Varnish将会使用无限制的malloc来存储它们。这意味着当Varnish需要存储临时对象时，它将使用系统内存（RAM）来进行存储，而不会限制内存的使用量。这种配置适用于需要快速存储和访问临时数据的场景，但要注意可能会占用大量内存资源。


## 运行期参数

运行期参数是指在管理界面，通过`param.show`看到的参数。这些参数有速记标志，以避免重复出现。以下是该标记的含义：

#### experimental
- **含义**：对这个参数，我们没有固定的值来说明好与不好，欢迎观察和反馈这个值。

#### delayed
- **含义**：这个值可以在不工作的时候改变，但是不会立即生效。

#### restart
- **含义**：工作进程会被停止，重新启动。

#### reload
- **含义**：VCL程序会被重新装载。

#### acceptor_sleep_decay
- **默认值**：0.900
- **标志**：experimental
- **说明**：如果我们的文件描述符或者工作线程等资源耗尽，接收器在两次间隔中会休眠，这个参数减少成功接收的休眠时间（0.9 = reduce by 10%）。

#### acceptor_sleep_incr
- **单位**：s
- **默认值**：0.001
- **标志**：experimental
- **说明**：如果我们的文件描述符或者工作线程等资源耗尽，接收器在两次接收间隔中会休眠，这个参数控制休眠的时间。

#### acceptor_sleep_max
- **单位**：s
- **默认值**：0.050
- **标志**：experimental
- **说明**：如果我们的文件描述符或者工作线程等资源耗尽，接收器在两次接收间隔中会休眠，这个参数设置最大的休眠时间。

#### auto_restart
- **单位**：bool
- **默认值**：on
- **说明**：如果子进程宕了，自动重启。

#### ban_dups
- **单位**：bool
- **默认值**：on
- **说明**：发现并去掉重复的bans。

#### ban_lurker_sleep
- **单位**：秒（s）
- **默认值**：0.01
- **说明**：设置ban_lurker线程在成功执行一次ban后的休息时间。默认是1秒，设置0将禁用ban_lurker。

#### between_bytes_timeout
- **单位**：秒（s）
- **默认值**：60
- **说明**：设置在接收数据时，两个字节之间的超时时间。值为0表示不会超时。这个参数不能用在pipe模式中。

#### cc_command
- **默认值**：exec gcc -std=gnu99 -pthread -fpic -shared -Wl,-x -o %o %s
- **说明**：编译C源代码的参数。%s是源文件名，%o是输出文件名。

#### cli_buffer
- **单位**：字节（bytes）
- **默认值**：8192
- **说明**：CLI输入的缓冲区大小。如果使用很大的vcl文件的话，需要加大这个值。注意要使用-p参数使其生效。

#### cli_timeout
- **单位**：秒（seconds）
- **默认值**：10
- **说明**：管理员对CLI请求的超时时间。

#### clock_skew
- **单位**：秒（s）
- **默认值**：10
- **说明**：设置后端服务器和Varnish之间，多少时差是可以接受的。

#### connect_timeout
- **Units**: s  
- **Default**: 0.7  
- **说明**: 设置连接后端服务器默认的超时时间。vcl的配置可以覆盖这个值。

#### critbit_cooloff
- **Units**: s  
- **Default**: 180.0  
- **说明**: 设置critbit hasher在cooloff列表保存deleted objheads的时间。

#### default_grace
- **Units**: seconds  
- **Default**: 10  
- **Flags**: delayed  
- **说明**: 设置grace的缺省时间。varnish将在对象过期后延迟递交，好让其他线程做一个新的拷贝。

#### default_keep
- **Units**: seconds  
- **Default**: 0  
- **Flags**: delayed  
- **说明**: 设置保存一个无用对象的时间。这意味着对象从缓存中删除的时间=ttl+grace+keep。

#### default_ttl
- **Units**: seconds  
- **Default**: 120  
- **说明**: 如果backend和vcl都没有给对象分配ttl的话，这个设置将生效。已经存在缓存中的对象，在它们重新从后台获取数据前不受影响，强制他们生效，可以使用"ban.url"。

#### diag_bitmap
- **Units**: bitmap  
- **Default**: 0  
- **说明**: 设置bitmap控制诊断码，具体可以从文档上查看。

#### expiry_sleep
- **Units**: seconds  
- **Default**: 1  
- **说明**: 设置expiry线程的休息时间。

#### fetch_chunksize
- **Units**: kilobytes  
- **Default**: 128  
- **Flags**: experimental  
- **说明**: 设置fetcher使用的缺省chunksize，这个值应该比多数对象大。

#### fetch_maxchunksize
- **Units**: kilobytes  
- **Default**: 262144  
- **Flags**: experimental  
- **说明**: 分配给存储的最大chunksize，分配过大会引起延迟和碎片。

#### first_byte_timeout
- **Units**: s  
- **Default**: 60  
- **说明**: 从后端获取第一个数据的时间。我们只等待这么长时间，超时就放弃，0表示永不放弃，vcl的配置可以覆盖这个值。这个值在pipe模式无效。

#### group
- **Default**: magic  
- **Flags**: must_restart  
- **说明**: 使用哪个没有特权的组来运行此进程。

#### gzip_level
- **Default**: 6  
- **说明**: Gzip压缩级别：0=debug，1=fast，9=best。

#### gzip_memlevel
- **Default**: 8  
- **说明**: Gzip内存级别1=slow/least，9=fast/most compression。Memory impact is 1=1k，2=2k，...9=256k。

#### gzip_stack_buffer
- **Units**: Bytes  
- **Default**: 32768  
- **Flags**: experimental  
- **说明**: Gzip缓存区的大小。

#### gzip_tmp_space
- **Default**: 0  
- **Flags**: experimental  
- **说明**: 在哪儿给gzip/gunzip分配临时空间，0=malloc; 1=session workspace; 2=thread workspace。如果有很多gzip/gunzip活动，建议使用workspace以减少内存耗费。要知道gzip需要256+KB，gunzip需要32+KB (64+KB if ESI processing)。

#### gzip_window
- **Default**: 15  
- **说明**: Gzip窗口大小8=least, 15=most compression。Memory impact is 8=1k, 9=2k, ... 15=128k。

#### http_gzip_support
- **Units**: bool  
- **Default**: on  
- **Flags**: experimental  
- **说明**: 开启gzip支持，Varnish将会在存储到缓存前压缩对象。

#### http_max_hdr
- **Units**: header lines  
- **Default**: 64  
- **说明**: 可以处理的最大Http Header。

#### http_range_support
- **Units**: bool  
- **Default**: on  
- **说明**: 开启支持HTTP Range headers，以支持多线程断点续传。

#### http_req_hdr_len
- **Units**: bytes  
- **Default**: 8192  
- **说明**: 最大能接受的客户端请求头的大小。

#### http_req_size
- **Units**: bytes  
- **Default**: 32768  
- **说明**: 最大能处理的客户端请求的大小。

#### http_resp_hdr_len
- **Units**: bytes  
- **Default**: 8192  
- **说明**: 最大能处理的从backend返回的响应头的大小。

#### http_resp_size
- **Units**: bytes  
- **Default**: 32768  
- **说明**: 最大能处理的从backend返回的响应的大小。

#### idle_send_timeout
- **Units**: seconds  
- **Default**: 60  
- **Flags**: delayed  
- **说明**: 等待发送数据的时间，如果超时还是没有数据发送，session会关闭。

#### listen_address
- **Default**: :80  
- **Flags**: must_restart  
- **说明**: 监听的地址，可接收的表达：host, host:port, :port。

#### listen_depth
- **Units**: connections  
- **Default**: 1024  
- **Flags**: must_restart  
- **说明**: 监听队列的深度。

#### log_hashstring
- **Units**: bool  
- **Default**: on  
- **说明**: 是否记录这hash内容到共享内存日志。

#### log_local_address
- **Units**: bool  
- **Default**: off  
- **说明**: 日志是否记录本地IP的TCP连接。

#### lru_interval
- **Units**: seconds  
- **Default**: 2  
- **Flags**: experimental  
- **说明**: 目标对象移到LRU列表前的Grace时长。

#### max_esi_depth
- **Units**: levels  
- **Default**: 5  
- **说明**: esi:include进程的最大深度。

#### max_restarts
- **Units**: restarts  
- **Default**: 4  
- **说明**: 一个请求的最大重试次数。

#### nuke_limit
- **Units**: allocations  
- **Default**: 50  
- **Flags**: experimental  
- **说明**: 在空间中保存对象body的最大对象数量。

#### pcre_match_limit
- **Default**: 10000  
- **说明**: 内部调用pcre_exec()的次数限制。

#### pcre_match_limit_recursion
- **Default**: 10000  
- **说明**: 内部递归调用pcre_exec()的次数限制。

#### ping_interval
- **Units**: seconds  
- **Default**: 3  
- **Flags**: must_restart  
- **说明**: 子进程ping主进程的时间间隔，0表示禁止ping。

#### pipe_timeout
- **Units**: seconds  
- **Default**: 60  
- **说明**: PIPE会话的空闲超时时间，如果超时还没有数据发送的话，session会关闭。

#### prefer_ipv6
- **Units**: bool  
- **Default**: off  
- **说明**: 如果后端支持ipv4和ipv6，那么偏好使用ipv6。

#### queue_max
- **Units**: %  
- **Default**: 100  
- **Flags**: experimental  
- **说明**: 允许的队列长度，用百分比表示，设置的是请求队列与worker进程的百分比。

#### rush_exponent
- **Units**: requests per request  
- **Default**: 3  
- **Flags**: experimental  
- **说明**: 为完成一个请求对象准备多少parked request。

#### saintmode_threshold
- **Units**: objects  
- **Default**: 10  
- **Flags**: experimental  
- **说明**: Saint模式在超时前可以容纳的对象数目，0表示禁用Saint模式。

#### send_timeout
- **Units**: seconds  
- **Default**: 600  
- **Flags**: delayed  
- **说明**: 发送的超时时间。

#### sess_timeout
- **Units**: seconds  
- **Default**: 5  
- **说明**: 每个session保持的空闲时间。

#### sess_workspace
- **Units**: bytes  
- **Default**: 65536  
- **Flags**: delayed  
- **说明**: 分配给每个HTTP协议的session的空间大小，最小是1024 bytes。

#### session_linger
- **Units**: ms  
- **Default**: 50  
- **Flags**: experimental  
- **说明**: 一个session的linger（滞留，慢慢消失）的时间。

#### session_max
- **Units**: sessions  
- **Default**: 100000  
- **说明**: session的最大数量。

#### shm_reclen
- **Units**: bytes  
- **Default**: 255  
- **说明**: SHM日志的最大数量，最大是65535 bytes。

#### shm_workspace
- **Units**: bytes  
- **Default**: 8192  
- **Flags**: delayed  
- **说明**: 分配给worker线程中shm日志的空间，最小是4096 bytes。

#### Shortlived
- **Units**: s  
- **Default**: 10.0  
- **说明**: 对象最小的存活时间。如果分配各对象的TTL比这个值小，就保存在瞬时存储里面。

#### syslog_cli_traffic
- **Units**: bool  
- **Default**: on  
- **说明**: 记录CLI的syslog（LOG_INFO）。

#### thread_pool_add_delay
- **Units**: milliseconds  
- **Default**: 2  
- **说明**: 创建新线程前等待的时间，设置太长，会造成工作线程不足，太短会造成工作线程堆积。

#### thread_pool_add_threshold
- **Units**: requests  
- **Default**: 2  
- **Flags**: experimental  
- **说明**: 创建工作线程的阈值，设置太小，会造成工作线程过量，太大会造成线程不足。

#### thread_pool_fail_delay
- **Units**: milliseconds  
- **Default**: 200  
- **Flags**: experimental  
- **说明**: 线程池在一个线程失败，创建新线程前的等待时间。

#### thread_pool_max
- **Units**: threads  
- **Default**: 500  
- **Flags**: delayed, experimental  
- **说明**: 每个线程池能容纳的最大线程数。

#### thread_pool_min
- **Units**: threads  
- **Default**: 5  
- **Flags**: delayed, experimental  
- **说明**: 每个线程池最小的线程数，最小值为2。

#### thread_pool_purge_delay
- **Units**: milliseconds  
- **Default**: 1000  
- **Flags**: delayed, experimental  
- **说明**: 在purge线程间等待的时间，最小值为100。

#### thread_pool_stack
- **Units**: bytes  
- **Default**: -1  
- **Flags**: experimental  
- **说明**: Worker线程栈的大小。

#### thread_pool_timeout
- **Units**: seconds  
- **Default**: 300  
- **Flags**: delayed, experimental  
- **说明**: 线程池中的线程数小于thread_pool_min，在关闭线程池前等待的时间，最小是1秒。

#### thread_pool_workspace
- **Units**: bytes  
- **Default**: 65536  
- **Flags**: delayed  
- **说明**: 分配各工作线程的workspace大小，最小是1024 bytes。

#### thread_pools
- **Units**: pools  
- **Default**: 2  
- **Flags**: delayed, experimental  
- **说明**: 线程池的数量。

#### thread_stats_rate
- **Units**: requests  
- **Default**: 10  
- **Flags**: experimental  
- **说明**: 处理多少个线程，然后统计一次。

#### User
- **Default**: magic  
- **Flags**: must_restart  
- **说明**: 运行进程的用户，通常和group一起配置。

#### vcc_err_unref
- **Units**: bool  
- **Default**: on  
- **说明**: 为引用vcl对象的错误结果。

#### vcl_dir
- **Default**: /usr/local/etc/varnish  
- **说明**: VCL文件的路径和名称。

#### vcl_trace
- **Units**: bool  
- **Default**: off  
- **说明**: 开启跟踪VCL执行情况。

#### vmod_dir
- **Default**: /usr/local/lib/varnish/vmods  
- **说明**: 定义VCL modules的路径。

在32位系统上，有一些默认值，例如：
- `workspace_client`（=16k）
- `thread_pool_workspace`（=16k）
- `http_resp_size`（=8k）
- `http_req_size`（=12k）
- `gzip_stack_buffer`（=4k）
- `thread_pool_stack`（=64k）

可以通过减少这些值以保持虚拟空间。

# 日志操作

在 bin 目录下的 varnishlog 程序。

Varnish将日志记录到共享内存片段，而不是记录到一个普通文件中。这种方式有几个优点：
1. **速度快**：内存访问速度比磁盘快得多，因此记录日志的速度也更快。
2. **节省磁盘空间**：不需要占用磁盘空间，适合高流量网站。

当记录到内存片段的最后处时，会从头开始记录，覆盖老数据。这种循环覆盖的方式确保日志不会无限制增长，从而节省内存资源。

## Varnishlog程序
Varnishlog是一个命令行工具，用于查看Varnish的日志信息。使用方法如下：
1. **启动varnishlog**：在命令行输入`varnishlog`，会看到主进程的基本信息。
2. **刷新浏览器访问页面**：重新加载网页，varnishlog会显示访问的日志信息，帮助开发者了解缓存命中情况和请求处理过程。

## 日志格式说明
日志格式分为几列，每列代表不同的信息：
1. **第一列**：数字，表示具体的请求。相同的数字表示它们属于同一个HTTP事务。
2. **第二列**：标签，表示日志信息的类型。以Rx开头的标签代表Varnish正在接受数据，Tx代表正在发送数据。
3. **第三列**：表示数据的方向。来自或要发送给客户的数据标记为c，来自或要发送给后端的数据标记为b。
4. **第四列**：具体的日志数据，记录了请求或响应的详细信息。

### 示例解析
假设有以下日志条目：
```
11 RxRequest c GET /index.html HTTP/1.1
12 TxResponse b 200 OK
```
- 第一行表示请求编号11，Varnish正在接收来自客户端（c）的GET请求，请求的资源是/index.html，使用HTTP/1.1协议。
- 第二行表示请求编号12，Varnish正在发送响应给后端（b），响应状态码是200，表示请求成功。

## 日志查询过滤

`varnishlog`是一个用于查看Varnish日志的程序，通过不同的选项可以实现对日志信息的筛选和过滤。

### 基本选项
- **-b**：只显示Varnish和后端服务器之间通信的记录条。当你想优化缓存命中率的时候，这个选项非常有用。
- **-c**：和-b类似，只是针对与客户端的通信情况。

标签过滤
- **-i tag**：只有显示带有特定标签的行。例如，`varnishlog -i SessionOpen`将只显示新会话的情况。注意标签是大小写敏感的。

正则表达式过滤
- **-I**：通过正则表达式过滤数据，并显示匹配行。例如，`varnishlog -c -I RxHeader -I Cookie`，将显示所有来自客户端的cookie头信息。

请求ID过滤
- **-o**：根据请求id，将记录条目分组。如果要写到文件里面，使用-w选项。

示例
假设有以下日志条目：
```
11 RxRequest c GET /index.html HTTP/1.1
12 TxResponse b 200 OK
```
- 使用`-b`选项，只会显示Varnish与后端服务器的通信记录，例如`TxResponse b 200 OK`。
- 使用`-c`选项，只会显示Varnish与客户端的通信记录，例如`RxRequest c GET /index.html HTTP/1.1`。
- 使用`-i RxHeader`选项，只会显示包含RxHeader标签的行。
- 使用`-I Cookie`选项，只会显示包含Cookie头的行。
- 使用`-o`选项，可以根据请求id将记录条目分组，例如将所有与请求id为11相关的记录分组到一起。

### 拓展选项

- `-a`：当把日志写入文件时，采用追加的方式，而不是覆盖。
- `-C`：匹配正则表达式的时候，忽略大小写。
- `-D`：以进程方式运行。
- `-d`：启动时处理旧的日志，通常 `varnishlog` 只会在进程写入日志后启动。
- `-k num`：只显示开头的 `num` 个日志记录。
- `-n`：指定 varnish 实例的名字，用来获取日志，默认是主机名。
- `-P file`：记录 PID 的文件。
- `-r file`：从一个文件读取日志，而不是从共享内存读取。
- `-s num`：跳过开始的 `num` 条日志。
- `-u`：无缓冲的输出。
- `-v`：显示版本，然后退出。
- `-w file`：把日志写到一个文件里，而不是显示他们。如果没有 `-a` 参数的话，就会覆盖文件。如果在写文件的时候，收到 `sighup` 的信号，他会创建一个新的文件。
- `-X regex`：排除匹配正则表达式的日志。
- `-x tag`：排除匹配 tag 的日志。

如果 `-o` 选项被指定，需要使用正则表达式和 tag 来制定需要的日志。

## tag

- Backend
- BackendClose
- BackendOpen
- BackendReuse
- BackendXID
- CLI
- ClientAddr
- Debug
- Error
- ExpBan
- ExpKill
- ExpPick
- Hit
- HitPass
- HttpError
- HttpGarbage
- Length
- ObjHeader
- ObjLostHeader
- ObjProtocol
- ObjRequest
- ObjResponse
- ObjStatus
- ObjURL
- ReqEnd
- ReqStart
- RxHeader
- RxLostHeader
- RxProtocol
- RxRequest
- RxResponse
- RxStatus
- RxURL
- SessionClose
- SessionOpen
- StatAddr
- StatSess
- TTL
- TxHeader
- TxLostHeader
- TxProtocol
- TxRequest
- TxResponse
- TxStatus
- TxURL
- VCL_acl
- VCL_call
- VCL_return
- VCL_trace
- WorkThread

示例:

1. **输出日志到一个文件:**
   ```sh
   $ varnishlog -w /var/log/varnish.log
   ```

2. **读取一个日志文件，然后显示首页的请求:**
   ```sh
   $ varnishlog -r /var/log/varnish.log -c -m 'RxURL: /$'
   ```

# Varnish 缓存空间大小配置

给Varnish选择多少内存，是个很艰巨的问题，需要考虑以下事情：

1. 应用的数据集大小
对于门户网站或新闻网站来说，数据集主要包含首页及其相关内容。首页通常包含大量的文字、图片和其他多媒体内容，这些内容是最容易被用户访问的，因此缓存命中率较高。在规划缓存大小时，需要评估首页及其相关内容的总大小，以便合理分配缓存空间。

2. 对象的生成花费
对象的生成花费指的是从后端服务器获取数据并生成缓存对象的资源消耗。如果生成对象的资源消耗较低，可以考虑缓存更多的对象，以提高缓存命中率。但如果资源消耗较高，则需要权衡缓存对象的数量，以避免过度消耗系统资源。

3. 监控LRU活动
LRU（Least Recently Used）算法是缓存管理中常用的算法，用于淘汰最少使用的缓存对象。通过监控n_lru_nuked计数器，可以了解缓存中LRU活动的频率。如果LRU活动频繁，说明缓存空间不足，需要增加缓存大小以减少对象淘汰，提高缓存命中率。

**缓存开销**

缓存任何对象都会带来额外的开销，包括存储开销和管理开销。即使指定了缓存大小，实际使用的内存可能会超过指定值。每个对象的开销大约为1kB，比如缓存一百万张图片的额外开销大概是 1GB，因此在缓存中存在大量小对象时，整体开销会较大，需要特别注意。

# 提高命中率

提高 Varnish 的命中率是优化网站性能的重要手段。以下是一些具体的方法和策略，可以帮助你提高 Varnish 的缓存命中率：

### 1. 自定义缓存策略

#### 使用 Varnish 配置语言 (VCL)
通过自定义 VCL 文件，可以精细控制哪些请求和响应应该被缓存。以下是一些常见的自定义策略：

- **缓存 GET 和 HEAD 请求**：默认情况下，Varnish 只缓存 GET 和 HEAD 请求。确保你的配置没有改变这一点。
  ```vcl
  sub vcl_recv {
      if (req.method != "GET" && req.method != "HEAD") {
          return (pass);
      }
      // 其他缓存逻辑
  }
  ```

- **处理带有 Cookie 的请求**：默认情况下，带有 Cookie 的请求不会被缓存。你可以决定哪些 Cookie 是可以忽略的，从而允许缓存。
  ```vcl
  sub vcl_recv {
      if (req.http.Cookie) {
          if (req.url ~ "^/public/") {
              unset req.http.Cookie;
          } else {
              return (pass);
          }
      }
      // 其他缓存逻辑
  }
  ```

- **处理带有认证信息的请求**：对于需要认证的请求，通常不应该被缓存。
  ```vcl
  sub vcl_recv {
      if (req.http.Authorization || req.http.Cookie ~ "auth") {
          return (pass);
      }
      // 其他缓存逻辑
  }
  ```

### 2. 主动设置对象的 TTL

#### 设置合理的 TTL 值
TTL（Time To Live）决定了缓存对象在缓存中存活的时间。合理设置 TTL 可以显著提高缓存命中率。

- **默认 TTL**：默认情况下，Varnish 设置的 TTL 为 120 秒。你可以根据实际需求调整这个值。
  ```vcl
  sub vcl_backend_response {
      set beresp.ttl = 300s; // 设置 TTL 为 5 分钟
      // 其他缓存逻辑
  }
  ```

- **基于内容类型设置 TTL**：对于静态内容，可以设置较长的 TTL；对于动态内容，可以设置较短的 TTL。
  ```vcl
  sub vcl_backend_response {
      if (bereq.url ~ "\.(css|js|png|jpg|gif)$") {
          set beresp.ttl = 1h; // 1 小时
      } else {
          set beresp.ttl = 5m; // 5 分钟
      }
      // 其他缓存逻辑
  }
  ```

### 3. 忽略 HTTP 头信息

#### 尽量不依赖 HTTP 头信息
虽然 HTTP 头信息（如 Cache-Control）可以控制缓存行为，但过度依赖它们可能会导致缓存命中率降低。通过自定义 VCL，可以忽略某些头信息，从而提高缓存命中率。

```vcl
sub vcl_recv {
    if (req.http.Cache-Control ~ "no-cache") {
        return (pass);
    }
    // 其他缓存逻辑
}
```

### 4. 分析和监控

#### 使用日志和监控工具
通过分析 Varnish 的日志，可以了解哪些请求没有被缓存，以及为什么没有被缓存。

- **常用命令**：
  - `varnishtop -i txurl`：查看请求后端的 URL，帮助识别高频访问的 URL。
  - `varnishstat`：实时监控 Varnish 的状态，包括缓存命中率。
  - `varnishtest`：进行缓存策略的测试和验证。

#### 示例：
```bash
varnishtop -i txurl
```
这个命令可以帮助你识别哪些 URL 被频繁请求，从而优化这些 URL 的缓存策略。

### 5. 其他优化策略

- **合并请求**：尽量合并多个小请求为一个大的请求，减少请求次数。
- **压缩响应**：通过压缩响应内容，可以减少带宽消耗，提高传输速度。
- **使用缓存分级**：对于不同类型的请求，使用不同的缓存策略。例如，静态资源使用长 TTL，动态内容使用短 TTL。

## 通过日志辅助分析

使用 Varnish 的日志功能来辅助分析缓存命中率是一个非常有用的方法。通过日志，你可以深入了解哪些请求没有被缓存，为什么没有被缓存，以及如何优化缓存策略。以下是详细的步骤和示例，帮助你通过日志来提高 Varnish 的缓存命中率。

### 1. 导入 `std` 库

首先，你需要在 VCL 文件的顶部导入 `std` 库，以便使用日志功能。

```vcl
import std;
```

### 2. 配置日志输出

在需要输出日志的地方，使用 `std.log` 函数。例如，如果你想跟踪哪些请求没有命中缓存，可以在 `vcl_miss` 函数中输出日志。

```vcl
sub vcl_miss {
    std.log("URL miss! The URL is: " + req.url);
    return (fetch);
}
```

### 3. 启动 Varnish 并运行 `varnishlog`

启动 Varnish 后，使用 `varnishlog` 工具来跟踪日志输出。

```bash
varnishlog -I log
```

这个命令会实时显示所有包含 "log" 标签的日志信息。

### 4. 高级日志配置

为了更精细地控制日志输出，你可以在 VCL 文件中配置更多的日志信息。例如，记录请求的详细信息、未命中缓存的原因等。

```vcl
import std;

sub vcl_recv {
    // 记录所有进入的请求
    std.log("Received request for: " + req.url);
}

sub vcl_miss {
    // 记录未命中缓存的请求
    std.log("Cache miss for URL: " + req.url);
    // 你可以在这里添加更多的日志信息，例如请求头、客户端 IP 等
    std.log("Client IP: " + client.ip);
    return (fetch);
}

sub vcl_backend_response {
    // 记录后端响应的详细信息
    std.log("Backend response status: " + beresp.status);
    std.log("Backend response TTL: " + beresp.ttl);
}

sub vcl_deliver {
    // 记录发送给客户端的响应信息
    std.log("Response sent to client with status: " + resp.status);
}
```

### 5. 使用 `varnishtop` 和 `varnishstat` 进行实时监控

除了 `varnishlog`，Varnish 还提供了其他工具来帮助你实时监控缓存性能。

- **varnishtop**：显示最频繁的请求。
  ```bash
  varnishtop -i txurl
  ```
  这个命令会显示请求后端的 URL，帮助你识别高频访问的 URL。

- **varnishstat**：显示 Varnish 的实时统计信息，包括缓存命中率、缓存对象数量等。
  ```bash
  varnishstat
  ```

### 6. 分析日志以优化缓存策略

通过分析日志，你可以识别出哪些请求没有被缓存，以及为什么没有被缓存。以下是一些常见的优化策略：

- **识别高频访问的 URL**：通过 `varnishtop` 或日志分析，找出高频访问的 URL，并确保这些 URL 的缓存策略是合理的。
- **检查未命中缓存的原因**：通过 `varnishlog`，查看未命中缓存的请求，分析是否可以通过调整 VCL 配置来缓存这些请求。
- **调整 TTL 值**：根据日志中的 TTL 信息，调整缓存对象的 TTL 值，以平衡缓存命中率和数据新鲜度。

### 示例：分析未命中缓存的请求

假设你在 `vcl_miss` 中记录了未命中缓存的请求：

```vcl
sub vcl_miss {
    std.log("Cache miss for URL: " + req.url);
    return (fetch);
}
```

通过运行 `varnishlog -I log`，你可以看到类似以下的日志输出：

```
Cache miss for URL: /api/data
Client IP: 192.168.1.100
Cache miss for URL: /images/logo.png
Client IP: 192.168.1.101
```

通过分析这些日志，你可以：

- **识别未缓存的 URL**：例如 `/api/data` 可能是一个动态请求，不适合缓存；而 `/images/logo.png` 是一个静态资源，应该被缓存。
- **调整缓存策略**：对于静态资源，确保 VCL 配置允许缓存这些请求，并设置合理的 TTL 值。

