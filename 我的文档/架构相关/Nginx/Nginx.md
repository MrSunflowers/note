[TOC]

Nginx 文档 https://nginx.org/en/docs/

# 1.Nginx目录结构

```
[root@localhost ~]# tree /usr/local/nginx
/usr/local/nginx
├── client_body_temp                 # POST 大文件暂存目录
├── conf                             # Nginx所有配置文件的目录
│   ├── fastcgi.conf                 # fastcgi相关参数的配置文件
│   ├── fastcgi.conf.default         # fastcgi.conf的原始备份文件
│   ├── fastcgi_params               # fastcgi的参数文件
│   ├── fastcgi_params.default       
│   ├── koi-utf
│   ├── koi-win
│   ├── mime.types                   # 媒体类型
│   ├── mime.types.default
│   ├── nginx.conf                   #这是Nginx默认的主配置文件，日常使用和修改的文件
│   ├── nginx.conf.default
│   ├── scgi_params                  # scgi相关参数文件
│   ├── scgi_params.default  
│   ├── uwsgi_params                 # uwsgi相关参数文件
│   ├── uwsgi_params.default
│   └── win-utf
├── fastcgi_temp                     # fastcgi临时数据目录
├── html                             # Nginx默认站点目录
│   ├── 50x.html                     # 错误页面优雅替代显示文件，例如出现502错误时会调用此页面
│   └── index.html                   # 默认的首页文件
├── logs                             # Nginx日志目录
│   ├── access.log                   # 访问日志文件
│   ├── error.log                    # 错误日志文件
│   └── nginx.pid                    # pid文件，Nginx进程启动后，会把所有进程的ID号写到此文件
├── proxy_temp                       # 临时目录
├── sbin                             # Nginx 可执行文件目录
│   └── nginx                        # Nginx 二进制可执行程序
├── scgi_temp                        # 临时目录
└── uwsgi_temp                       # 临时目录
```

主要的目录是 conf,html,及 sbin。

- conf 目录放的是核心配置文件
- html 目录放的是静态页面
	- 50x.html 是发生错误展示的页面，index.html 是默认的访问页面。可以在该目录下新建 html，然后在浏览器中访问，例如在该目录下新建 hello.html，内容是 hello，然后访问：http://192.168.8.101/hello.html
- logs文件夹用于存放日志信息
- error.log 存放出错的信息，nginx.pid 存放的是当前 nginx 的 pid。
- sbin 存放的是可执行文件，可以用 ./nginx启动 nginx

# 2.Nginx基本运行原理

Nginx 的进程是使用经典的「Master-Worker」模型, Nginx 在启动后，会有一个 master 进程和多个 worker 进程。master 进程主要用来管理 worker 进程，包含：接收来自外界的信号，向各 worker 进程发送信号，监控 worker 进程的运行状态，当 worker 进程退出后(异常情况下)，会自动重新启动新的 worker 进程。worker 进程主要处理基本的网络事件，多个 worker 进程之间是对等的，他们同等竞争来自客户端的请求，各进程互相之间是独立的。一个请求，只可能在一个 worker 进程中处理，一个 worker 进程，不可能处理其它进程的请求。 worker 进程的个数是可以设置的，一般会设置与机器 cpu 核数一致，这里面的原因与 nginx 的进程模型以及事件处理模型是分不开的。

# 3.Nginx的基本配置文件

刚安装好的 nginx.conf 如下

```nginx
#user  nobody;
worker_processes  1;

#error_log  logs/error.log;
#error_log  logs/error.log  notice;
#error_log  logs/error.log  info;

#pid        logs/nginx.pid;


events {
    worker_connections  1024;
}


http {
    include       mime.types;
    default_type  application/octet-stream;

    #log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
    #                  '$status $body_bytes_sent "$http_referer" '
    #                  '"$http_user_agent" "$http_x_forwarded_for"';

    #access_log  logs/access.log  main;

    sendfile        on;
    #tcp_nopush     on;

    #keepalive_timeout  0;
    keepalive_timeout  65;

    #gzip  on;

    server {
        listen       80;
        server_name  localhost;

        #charset koi8-r;

        #access_log  logs/host.access.log  main;

        location / {
            root   html;
            index  index.html index.htm;
        }

        #error_page  404              /404.html;

        # redirect server error pages to the static page /50x.html
        #
        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   html;
        }

        # proxy the PHP scripts to Apache listening on 127.0.0.1:80
        #
        #location ~ \.php$ {
        #    proxy_pass   http://127.0.0.1;
        #}

        # pass the PHP scripts to FastCGI server listening on 127.0.0.1:9000
        #
        #location ~ \.php$ {
        #    root           html;
        #    fastcgi_pass   127.0.0.1:9000;
        #    fastcgi_index  index.php;
        #    fastcgi_param  SCRIPT_FILENAME  /scripts$fastcgi_script_name;
        #    include        fastcgi_params;
        #}

        # deny access to .htaccess files, if Apache's document root
        # concurs with nginx's one
        #
        #location ~ /\.ht {
        #    deny  all;
        #}
    }


    # another virtual host using mix of IP-, name-, and port-based configuration
    #
    #server {
    #    listen       8000;
    #    listen       somename:8080;
    #    server_name  somename  alias  another.alias;

    #    location / {
    #        root   html;
    #        index  index.html index.htm;
    #    }
    #}


    # HTTPS server
    #
    #server {
    #    listen       443 ssl;
    #    server_name  localhost;

    #    ssl_certificate      cert.pem;
    #    ssl_certificate_key  cert.key;

    #    ssl_session_cache    shared:SSL:1m;
    #    ssl_session_timeout  5m;

    #    ssl_ciphers  HIGH:!aNULL:!MD5;
    #    ssl_prefer_server_ciphers  on;

    #    location / {
    #        root   html;
    #        index  index.html index.htm;
    #    }
    #}

}
```

去掉注释的简单版如下

```nginx
worker_processes  1; #允许进程数量，建议设置为cpu核心数或者auto自动检测，注意Windows服务器上虽然可以启动多个processes，但是实际只会用其中一个

events {
    #单个进程最大连接数（最大连接数=连接数*进程数）
    #根据硬件调整，和前面工作进程配合起来用，尽量大，但是别把cpu跑到100%就行。
    worker_connections  1024;
}


http {
    #文件扩展名与文件类型映射表(是conf目录下的一个文件)
    include       mime.types;
    #默认文件类型，如果mime.types预先定义的类型没匹配上，默认使用二进制流的方式传输
    default_type  application/octet-stream;

    #sendfile指令指定nginx是否调用sendfile 函数（zero copy 方式）来输出文件，对于普通应用，必须设为on。如果用来进行下载等应用磁盘IO重负载应用，可设置为off，以平衡磁盘与网络IO处理速度。
    sendfile        on;
    
     #
	 ，单位是秒
    keepalive_timeout  65;

 #虚拟主机的配置
    server {
    #监听端口
        listen       80;
        #域名，可以有多个，用空格隔开
        server_name  localhost;

	#配置根目录以及默认页面
        location / {
            root   html;
            index  index.html index.htm;
        }

	#出错页面配置
        error_page   500 502 503 504  /50x.html;
        #/50x.html文件所在位置
        location = /50x.html {
            root   html;
        }
        
    }
}
```

# 4.域名解析（理论）

虚拟主机使用特殊的软硬件技术，把一台运行在因特网上的服务器主机分成一台台“虚拟”的主机，每一台虚拟主机都具有独立的域名，具有完整的 Internet 服务器（WWW、FTP、Email等）功能，虚拟主机之间完全独立，并可由用户自行管理，在外界看来，每一台虚拟主机和一台独立的主机完全一样。

域名解析就是域名到 IP 地址的转换过程，IP 地址是网路上标识站点的数字地址，为了简单好记，采用域名来代替 ip 地址标识站点地址，。域名的解析工作由 DNS 服务器完成。

## 域名、dns、ip地址的关系

域名是相对网站来说的，IP是相对网络来说的。当输入一个域名的时候，网页是如何做出反应的？

输入域名---->域名解析服务器（dns）解析成ip地址—>访问IP地址—>完成访问的内容—>返回信息。

Internet上的计算机IP是唯一的，一个IP地址对应一个计算机。

一台计算机上面可以有很多个服务，也就是一个ip地址对应了很多个域名，即一个计算机上有很多网站。

## IP地址和DNS地址的区别

IP地址是指单个主机的唯一IP地址，而DNS服务器地址是用于域名解析的地址。

一个是私网地址，一个是公网地址；

一个作为主机的逻辑标志，一个作为域名解析服务器的访问地址。

### IP地址

IP，就是Internet Protocol的缩写，是一种通信协议，我们用的因特网基本是IP网组成的。

IP地址就是因特网上的某个设备的一个编号。

IP地址一般由网络号，主机号，掩码来组成。

IP网络上有很多路由器，路由器之间转发、通信都是只认这个IP地址，类似什么哪？就好像你寄包裹，你的写上发件人地址，你的姓名，收件人地址，收件人姓名。

这个发件人地址就是你电脑的IP的网络号，你的姓名就是你的主机号。

收件人的地址就是你要访问的IP的网络号，收件人的姓名就是访问IP的主机号。

现在还有了更复杂的IPV6,还有IPV9。

### DNS

我们访问因特网必须知道对端的IP地址，可是我们访问网站一般只知道域名啊，怎么办？

这时候DNS就有用处了，电脑先访问DNS服务器，查找域名对应的IP,于是，你的电脑就知道要发包到IP地址了。

### http 协议

HTTP是一个应用层协议，由请求和响应构成，是一个标准的客户端服务器模型。HTTP是一个无状态的协议。

HTTP 协议通常承载于 TCP 协议之上，有时也承载于 TLS 或 SSL 协议层之上，这个时候，就成了我们常说的 HTTPS。

客户端与服务器的数据交互的流程

1）首先客户机与服务器需要建立TCP连接。只要单击某个超级链接，HTTP的工作即开始。

2）建立连接后，客户机发送一个请求给服务器，请求方式的格式为：统一资源标识符（URL）、协议版本号，后边是MIME信息包括请求修饰符、客户机信息和可能的内容。

3）服务器接到请求后，给予相应的响应信息，其格式为一个状态行，包括信息的协议版本号、一个成功或错误的代码，后边是MIME信息包括服务器信息、实体信息和可能的内容，例如返回一个HTML的文本。

4）客户端接收服务器所返回的信息通过浏览器显示在用户的显示屏上，然后客户机与服务器断开连接。如果在以上过程中的某一步出现错误，那么产生错误的信息将返回到客户端，有显示屏输出。对于用户来说，这些过程是由HTTP自己完成的，用户只要用鼠标点击，等待信息显示就可以了。

# 5.虚拟主机（配置）

**虚拟主机是为了在同一台物理机器上运行多个不同的网站，提高资源利用率引入的技术。**

一般的web服务器一个ip地址的80端口只能正确对应一个网站。web服务器在不使用多个ip地址和端口的情况下，如果需要支持多个相对独立的网站就需要一种机制来分辨同一个ip地址上的不同网站的请求，这就出现了主机头绑定的方法。简单的说就是，将不同的网站空间对应不同的域名，以连接请求中的域名字段来分发和应答正确的对应空间的文件执行结果。举个例子来说，一台服务器ip地址为192.168.8.101，有两个域名和对应的空间在这台服务器上，使用的都是192.168.8.101的80端口来提供服务。如果只是简单的将两个域名A和B的域名记录解析到这个ip地址，那么web服务器在收到任何请求时反馈的都会是同一个网站的信息，这显然达不到要求。接下来我们使用主机头绑定域名A和B到他们对应的空间文件夹C和D。当含有域名A的web请求信息到达192.168.8.101时，web服务器将执行它对应的空间C中的首页文件，并返回给客户端，含有域名B的web请求信息同理，web服务器将执行它对应的空间D中的首页文件，并返回给客户端，所以在使用主机头绑定功能后就不能使用ip地址访问其上的任何网站了，因为请求信息中不存在域名信息，所以会出错。

## 监听不同域名

配置nginx.cfg

```nginx
worker_processes  1; #允许进程数量，建议设置为cpu核心数或者auto自动检测，注意Windows服务器上虽然可以启动多个processes，但是实际只会用其中一个

events {
    #单个进程最大连接数（最大连接数=连接数*进程数）
    #根据硬件调整，和前面工作进程配合起来用，尽量大，但是别把cpu跑到100%就行。
    worker_connections  1024;
}


http {
    #文件扩展名与文件类型映射表(是conf目录下的一个文件)
    include       mime.types;
    #默认文件类型，如果mime.types预先定义的类型没匹配上，默认使用二进制流的方式传输
    default_type  application/octet-stream;

    #sendfile指令指定nginx是否调用sendfile 函数（zero copy 方式）来输出文件，对于普通应用，必须设为on。如果用来进行下载等应用磁盘IO重负载应用，可设置为off，以平衡磁盘与网络IO处理速度。
    sendfile        on;
    
     #长连接超时时间，单位是秒
    keepalive_timeout  65;

 #虚拟主机的配置
    server {
    #监听端口
        listen       80;
        #域名，可以有多个，用空格隔开
        server_name  test80.xzj520520.cn;

	#配置根目录以及默认页面
        location / {
            root   /www/test80;
            index  index.html index.htm;
        }

	#出错页面配置
        error_page   500 502 503 504  /50x.html;
        #/50x.html文件所在位置
        location = /50x.html {
            root   html;
        }
        
    }
    
    
    #虚拟主机的配置
    server {
    #监听端口
        listen       80;
        #域名，可以有多个，用空格隔开
        server_name  test81.xzj520520.cn;

	#配置根目录以及默认页面
        location / {
            root   /www/test81;
            index  index.html index.htm;
        }

	#出错页面配置
        error_page   500 502 503 504  /50x.html;
        #/50x.html文件所在位置
        location = /50x.html {
            root   html;
        }
        
    }
```

使用 `systemctl reload nginx` 重新加载配置

配置单机域名

![ba83f6445941356fb0ca5aa8780a0412](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202311052010852.png)

测试

访问 http://test80.xzj520520.cn/
访问 http://test81.xzj520520.cn/

如果匹配不到会访问第一个站点

## 监听多个端口

修改nginx.conf

```nginx
worker_processes  1; #允许进程数量，建议设置为cpu核心数或者auto自动检测，注意Windows服务器上虽然可以启动多个processes，但是实际只会用其中一个

events {
    #单个进程最大连接数（最大连接数=连接数*进程数）
    #根据硬件调整，和前面工作进程配合起来用，尽量大，但是别把cpu跑到100%就行。
    worker_connections  1024;
}


http {
    #文件扩展名与文件类型映射表(是conf目录下的一个文件)
    include       mime.types;
    #默认文件类型，如果mime.types预先定义的类型没匹配上，默认使用二进制流的方式传输
    default_type  application/octet-stream;

    #sendfile指令指定nginx是否调用sendfile 函数（zero copy 方式）来输出文件，对于普通应用，必须设为on。如果用来进行下载等应用磁盘IO重负载应用，可设置为off，以平衡磁盘与网络IO处理速度。
    sendfile        on;
    
     #长连接超时时间，单位是秒
    keepalive_timeout  65;

 #虚拟主机的配置
    server {
    #监听端口
        listen       80;
        #域名，可以有多个，用空格隔开
        server_name  localhost;

	#配置根目录以及默认页面
        location / {
            root   /www/test80;
            index  index.html index.htm;
        }

	#出错页面配置
        error_page   500 502 503 504  /50x.html;
        #/50x.html文件所在位置
        location = /50x.html {
            root   html;
        }
        
    }
    
    
    #虚拟主机的配置
    server {
    #监听端口
        listen       81;
        #域名，可以有多个，用空格隔开
        server_name  localhost;

	#配置根目录以及默认页面
        location / {
            root   /www/test81;
            index  index.html index.htm;
        }

	#出错页面配置
        error_page   500 502 503 504  /50x.html;
        #/50x.html文件所在位置
        location = /50x.html {
            root   html;
        }
        
    }

}
```

使用 `systemctl reload nginx` 重新加载配置

在如下位置新建test80,test81

![5a29e5e8f40fcef889b4c4d9da622d89](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202311052013445.png)

test80,test81新建index.html

分别访问

http://192.168.8.101:80/

http://192.168.8.101:81/

## 泛域名

所谓“泛域名解析”是指：利用通配符* （星号）来做次级域名以实现所有的次级域名均指向同一IP地址。

好处：

1.可以让域名支持无限的子域名(这也是泛域名解析最大的用途)。

2.防止用户错误输入导致的网站不能访问的问题

3.可以让直接输入网址登陆网站的用户输入简洁的网址即可访问网站

泛域名在实际使用中作用是非常广泛的，比如实现无限二级域名功能，提供免费的url转发，在IDC部门实现自动分配免费网址，在大型企业中实现网址分类管理等等，都发挥了巨大的作用。

## ServerName 配置规则

可以在同一个servername中配置多个域名

### 完整匹配

server中可以配置多个域名，例如：

```nginx
server_name  test81.xzj520520.cn  test82.xzj520520.cn;
```


### 通配符匹配

使用通配符的方式如下：

```nginx
server_name  *.xzj520520.cn;
```

需要注意的是**精确匹配**的优先级大于**通配符匹配**和**正则匹配**。


### 正则匹配

采用正则的匹配方式如下:

![41d0a93f50338db6ed3b62480fc92319](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202311052123608.png)

正则匹配格式，必须以~开头，比如：`server_name ~^www\d+\.example\.net$;`。如果开头没有`~`，则nginx认为是精确匹配。在逻辑上，需要添加`^`和`$`锚定符号。注意，正则匹配格式中`.`为正则元字符，如果需要匹配`.`，则需要反斜线转义。如果正则匹配中含有`{`和`}`则需要双引号引用起来，避免nginx报错，如果没有加双引号，则nginx会报如下错误：`directive "server_name" is not terminated by ";" in ...`。

### 特殊匹配格式

采用正则的匹配方式如下:

```nginx
server_name ""; 匹配Host请求头不存在的情况。
```

### 匹配顺序

```
1. 精确的名字
2. 以*号开头的最长通配符名称，例如 *.example.org
3. 以*号结尾的最长通配符名称，例如 mail.*
4. 第一个匹配的正则表达式（在配置文件中出现的顺序）
```

优化

```
1. 尽量使用精确匹配;
2. 当定义大量server_name时或特别长的server_name时，需要在http级别调整server_names_hash_max_size和server_names_hash_bucket_size，否则nginx将无法启动。
```

在使用 Nginx 作为服务器时，需要注意 `server_names_hash_max_size` 和 `server_names_hash_bucket_size` 这两个指令的配置，尤其是在处理大量的 `server_name` 指令或特别长的 `server_name` 字符串时。

1. **server_names_hash_max_size**: 这个指令用于设置 Nginx 服务器名称哈希表的最大大小。哈希表用于快速查找匹配的 `server_name`。如果 Nginx 启动时检测到这个值太小，无法容纳所有服务器名称，它将无法启动，并会报错提示需要增加这个值。

2. **server_names_hash_bucket_size**: 这个指令用于设置哈希表中每个桶的大小。桶是哈希表中用于存储具有相同哈希值的服务器名称的容器。增加这个值可以减少哈希冲突，提高性能，特别是在 `server_name` 非常多或者很长时。

调整这两个参数时，需要根据你的 `server_name` 数量和长度来决定合适的值。通常，如果 Nginx 报告无法启动并提示需要调整这些参数，你需要根据错误信息中的建议来增加这些值。

例如，如果你收到的错误信息提示 `server_names_hash_max_size` 需要增加，你可以按照如下方式调整你的 Nginx 配置文件（通常是 `/etc/nginx/nginx.conf` 或者 `/etc/nginx/conf.d/default.conf`）：

```nginx
http {
    ...
    server_names_hash_max_size 512;
    server_names_hash_bucket_size 64;
    ...
}
```

在调整这些值之后，需要重新加载或重启 Nginx 以使更改生效。可以使用以下命令之一：

```bash
sudo nginx -s reload  # 重新加载配置文件
sudo systemctl restart nginx  # 如果你使用的是systemd管理Nginx服务
```

请记住，增加这些值会消耗更多的内存，因此需要根据服务器的资源和实际需求来平衡配置。如果不确定如何设置，可以先从较小的值开始，然后根据实际需要逐步调整。

### httpDNS（了解）

[httpDNS](https://juejin.cn/post/7041854828661702663)

### 附录:常用正则表

~* 不区分大小写的匹配（匹配firefox的正则同时匹配FireFox）

!~ 区分大小写不匹配

!~* 不区分大小写不匹配

. 匹配除换行符以外的任意字符

\w 匹配字母或数字或下划线或汉字

\s 匹配任意的空白符

\d 匹配数字

\b 匹配单词的开始或结束

^ 匹配字符串的开始

$ 匹配字符串的结束

*重复零次或更多次前面一个字符

+重复一次或更多次前面一个字符

? 重复零次或一次前面一个字符

{n} 重复n次前面一个字符{n,} 重复n次或更多次

{n,m} 重复n到m次

*? 重复任意次，但尽可能少重复

+? 重复1次或更多次，但尽可能少重复

?? 重复0次或1次，但尽可能少重复{n,m}? 重复n到m次，但尽可能少重复{n,}? 重复n次以上，但尽可能少重复

\W 匹配任意不是字母，数字，下划线，汉字的字符

\S 匹配任意不是空白符的字符

\D 匹配任意非数字的字符

\B 匹配不是单词开头或结束的位置

[^x] 匹配除了x以外的任意字符

[^abc] 匹配除了abc这几个字母以外的任意字符

(exp) 匹配exp,并捕获文本到0…9

(?exp) 匹配exp,并捕获文本到名称为name的组里，也可以写成(?'name’exp)(?:exp) 匹配exp,不捕获匹配的文本，也不给此分组分配组号

(?=exp) 零宽断言,匹配exp前面的位置

(?<=exp) 匹配exp后面的位置

(?!exp) 匹配后面跟的不是exp的位置

(?<!exp) 匹配前面不是exp的位置

(?#comment) 注释,这种类型的分组不对正则表达式的处理产生任何影响，用于提供注释让人阅读

# 6.反向代理（配置）

反向代理方式是指以代理服务器来接受Internet上的连接请求，然后将请求转发给内部网络上的服务器；并将从服务器上得到的结果返回给Internet上请求连接的客户端，此时代理服务器对外就表现为一个服务器。

反向代理服务器通常有两种模型，一种是作为内容服务器的替身，另一种作为内容服务器集群的负载均衡器。

**作内容服务器的替身**

如果您的内容服务器具有必须保持安全的敏感信息，如信用卡号数据库，可在防火墙外部设置一个代理服务器作为内容服务器的替身。当外部客户机尝试访问内容服务器时，会将其送到代理服务器。实际内容位于内容服务器上，在防火墙内部受到安全保护。代理服务器位于防火墙外部，在客户机看来就像是内容服务器。

当客户机向站点提出请求时，请求将转到代理服务器。然后，代理服务器通过防火墙中的特定通路，将客户机的请求发送到内容服务器。内容服务器再通过该通道将结果回传给代理服务器。代理服务器将检索到的信息发送给客户机，好像代理服务器就是实际的内容服务器。如果内容服务器返回错误消息，代理服务器会先行截取该消息并更改标头中列出的任何 URL，然后再将消息发送给客户机。如此可防止外部客户机获取内部内容服务器的重定向 URL。

这样，代理服务器就在安全数据库和可能的恶意攻击之间提供了又一道屏障。与有权访问整个数据库的情况相对比，就算是侥幸攻击成功，作恶者充其量也仅限于访问单个事务中所涉及的信息。未经授权的用户无法访问到真正的内容服务器，因为防火墙通路只允许代理服务器有权进行访问。

**作为内容服务器的负载均衡器**

可以在一个组织内使用多个代理服务器来平衡各 Web 服务器间的网络负载。在此模型中，可以利用代理服务器的高速缓存特性，创建一个用于负载平衡的服务器池。此时，代理服务器可以位于防火墙的任意一侧。如果 Web 服务器每天都会接收大量的请求，则可以使用代理服务器分担 Web 服务器的负载并提高网络访问效率。

对于客户机发往真正服务器的请求，代理服务器起着中间调停者的作用。代理服务器会将所请求的文档存入高速缓存。如果有不止一个代理服务器，DNS 可以采用“轮询法”选择其 IP 地址，随机地为请求选择路由。客户机每次都使用同一个 URL，但请求所采取的路由每次都可能经过不同的代理服务器。

可以使用多个代理服务器来处理对一个高用量内容服务器的请求，这样做的好处是内容服务器可以处理更高的负载，并且比其独自工作时更有效率。在初始启动期间，代理服务器首次从内容服务器检索文档，此后，对内容服务器的请求数会大大下降。

## Nginx的反向代理配置

**proxy_pass**

在默认的配置文件中

```nginx

worker_processes  1;

events {
    worker_connections  1024;
}

http {
    include       mime.types;
    default_type  application/octet-stream;
    sendfile        on;
    keepalive_timeout  65;

    server {
        listen       80;
        server_name  localhost;
        location / {
            root   html;
            index  index.html index.htm;
        }

        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   html;
        }

    }

}
```

server.location 下的 root+index 用来配置静态资源，用来配置前端静态资源使用，proxy_pass 用来配置代理服务器使用，这两个属性二选一

proxy_pass 配置有两种形式

### 单台机器配置语法

```nginx
proxy_pass + 空格 + 完整网址（一般为非 https 协议）/ http:// + 服务器组别名 + 封号结尾
```

示例：

```nginx

worker_processes  1;

events {
    worker_connections  1024;
}


http {
    include       mime.types;
    default_type  application/octet-stream;
    sendfile        on;
    keepalive_timeout  65;

    server {
        listen       80;
        server_name  localhost;
        location / {
            proxy_pass http://www.bilibili.com;
			#proxy_pass http://192.168.1.131:80;
            #root   html;
            #index  index.html index.htm;
        }

        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   html;
        }

    }
}
```

### 多台机器配置语法(负载均衡器)

upstream httpds 服务器组，与 server 同一级别

基本语法

```nginx
upstream 别名 {
    server 192.168.1.131:80;
    server 192.168.1.132:80;
}
```

克隆两个 centos，ip分别设为192.168.1.131，192.168.1.132

示例：

```nginx
worker_processes  1;

events {
    worker_connections  1024;
}


http {
    include       mime.types;
    default_type  application/octet-stream;
    sendfile        on;
    keepalive_timeout  65;

    upstream httpds{
        server 192.168.1.131:80;
        server 192.168.1.132:80;
    }

    server {
        listen       80;
        server_name  localhost;
        location / {
            proxy_pass http://httpds;
            #root   html;
            #index  index.html index.htm;
        }

        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   html;
        }

    }
}
```

默认带有负载均衡功能，默认策略为轮询

## 负载均衡器策略

### 1.轮询

见上述示例

#### 权重 

weight 值越大权重越大，权重是相对的

```nginx
worker_processes  1;

events {
    worker_connections  1024;
}


http {
    include       mime.types;
    default_type  application/octet-stream;
    sendfile        on;
    keepalive_timeout  65;

    upstream httpds{
        server 192.168.1.131:80 weight=8;
        server 192.168.1.132:80 weight=2;
    }

    server {
        listen       80;
        server_name  localhost;
        location / {
            proxy_pass http://httpds;
            #root   html;
            #index  index.html index.htm;
        }

        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   html;
        }

    }
}
```

#### down (宕机) 不参与负载均衡

```nginx
worker_processes  1;

events {
    worker_connections  1024;
}


http {
    include       mime.types;
    default_type  application/octet-stream;
    sendfile        on;
    keepalive_timeout  65;

    upstream httpds{
        server 192.168.1.131:80 down;
        server 192.168.1.132:80 weight=2;
    }

    server {
        listen       80;
        server_name  localhost;
        location / {
            proxy_pass http://httpds;
            #root   html;
            #index  index.html index.htm;
        }

        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   html;
        }

    }
}
```

#### backup 备用服务器，主机不可用时使用

```nginx

worker_processes  1;

events {
    worker_connections  1024;
}


http {
    include       mime.types;
    default_type  application/octet-stream;
    sendfile        on;
    keepalive_timeout  65;

    upstream httpds{
        server 192.168.1.131:80 backup;
        server 192.168.1.132:80 weight=2;
    }

    server {
        listen       80;
        server_name  localhost;
        location / {
            proxy_pass http://httpds;
            #root   html;
            #index  index.html index.htm;
        }

        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   html;
        }

    }
}
```

### 2.Hash

#### IP Hash 

根据客户端的ip地址转发同一台服务器，可以保持会话，但是很少用这种方式去保持会话，例如我们当前正在使用wifi访问，当切换成手机信号访问时，会话就不保持了。

用途：线上**临时紧急扩容**，可以在不改动代码和结构的情况下处理一些紧急生产问题。

以下是一个基本的配置示例，展示如何在 NGINX 中设置 IP Hash 负载均衡：

```nginx
http {
    upstream myapp1 {
        ip_hash;
        server srv1.example.com;
        server srv2.example.com;
        server srv3.example.com;
    }

    server {
        listen 80;

        location / {
            proxy_pass http://myapp1;
        }
    }
}
```

在这个配置中：

- `http` 块是 NGINX 配置的根块。
- `upstream` 块定义了一个名为 `myapp1` 的服务器组。
- `ip_hash` 指令启用了 IP Hash 负载均衡方法。
- `server` 指令列出了后端服务器的地址。这些地址可以是域名或 IP 地址。
- `server` 块定义了 NGINX 用于接收请求的端口和地址，并将请求代理到 `myapp1` 服务器组。

确保在修改配置文件后，使用 `nginx -t` 检查配置文件的正确性，然后使用 `nginx -s reload` 重新加载 NGINX 以应用更改。

请注意，IP Hash 负载均衡方法在某些情况下可能不是最佳选择，比如当后端服务器数量经常变化时，因为 IP Hash 依赖于后端服务器的固定集合。此外，如果后端服务器的性能差异很大，IP Hash 可能会导致某些服务器过载而其他服务器负载不足。在这种情况下，可能需要考虑其他负载均衡策略，如最少连接（least_conn）或基于权重的轮询（weight）。

#### url_hash

根据用户访问的url定向转发请求，不同的url转发到不同的服务器进行处理（定向流量转发），一般用于定向寻找资源，例如100个文件散列在10台机器上，这些文件又非常大，不可能也没必要做完全备份，就可以根据请求文件资源的名称哈希定位到具体的机器上。

要实现基于用户访问的 URL 定向转发请求到不同的服务器，你可以使用 NGINX 的 `map` 指令结合 `proxy_pass` 指令来实现。这种方法允许你根据请求的 URL 或 URL 中的某些参数来决定将请求转发到哪个后端服务器。

以下是一个配置示例，假设你有几台服务器，每台服务器负责处理特定的 URL 路径：

```nginx
http {
    # 定义一个 map 块，根据请求的 URI 来决定转发到哪个服务器
    map $request_uri $target_server {
        ~*^/service1/(.*)$ http://backend1.example.com;
        ~*^/service2/(.*)$ http://backend2.example.com;
        # 可以继续添加更多的匹配规则
    }

    server {
        listen 80;

        location / {
            # 使用 map 指令定义的变量 $target_server 来决定转发的目标
            proxy_pass $target_server;
        }
    }
}
```

在这个配置中：

- `map $request_uri $target_server` 块定义了一个变量 `$target_server`，它根据请求的 URI 来决定值。这里使用了正则表达式来匹配特定的 URL 路径，并将这些路径转发到不同的后端服务器。
- `server` 块定义了 NGINX 监听的端口和地址。
- `location /` 块指定了处理所有请求的方式。`proxy_pass` 指令使用 `$target_server` 变量来决定将请求转发到哪个后端服务器。

请注意，这个配置示例使用了正则表达式来匹配 URL 路径，并根据匹配结果转发到不同的后端服务器。你可以根据实际的服务器和 URL 路径需求来调整正则表达式和映射逻辑。

在实际应用中，你可能需要根据实际的服务器配置调整正则表达式和映射逻辑。此外，确保在修改配置后使用 `nginx -t` 检查配置文件的正确性，然后使用 `nginx -s reload` 重新加载 NGINX 以应用更改。

#### 其他 Hash

例如 $coolie_jsessionid 和 $request_url、文件、请求头等都可以做 Hash

### 3.least_conn

最少连接访问，优先访问连接最少的那一台服务器，这种方式也很少使用，因为连接少，可能是由于该服务器配置较低，刚开始赋予的权重较低。

### 4.fair（需要第三方插件）

根据后端服务器响应时间转发请求，这种方式也很少使用，因为容易造成流量倾斜，给某一台服务器压垮。

### 5.sticky（需要第三方插件）

`nginx-sticky-module-ng` 是一个第三方 NGINX 模块，用于实现基于 **cookie** 的会话粘滞性（session stickiness）。这意味着，一旦用户首次访问服务器，他们将被重定向到特定的后端服务器，并且在会话期间，后续的请求也将被发送到同一服务器，直到会话结束或 cookie 过期。

**配置 `upstream` 块**：
   在 NGINX 配置文件中，使用 `upstream` 块定义后端服务器，并使用 `sticky` 指令来启用会话粘滞性。

   ```nginx
   upstream backend {
       sticky；
       server backend1.example.com;
       server backend2.example.com;
   }
   ```

**配置 `server` 块**：
   在 `server` 块中，将请求代理到 `upstream` 块定义的后端服务器组。

   ```nginx
   server {
       listen 80;
       location / {
           proxy_pass http://backend;
       }
   }
   ```

**原理**

`nginx-sticky-module-ng` 的工作原理是通过在用户的浏览器中设置一个特定的，类似于 jsessionid 的 cookie 的 k-v 来实现的（默认 k 的名字为 route ）。当用户首次访问服务器时，NGINX 会检查是否已经存在一个有效的粘滞 route。如果不存在，NGINX 会生成一个新的 route 并将其发送给用户。这个 route 包含了用于识别后端服务器的信息。

在后续的请求中，用户的浏览器会自动发送这个 route。NGINX 读取 route 中的信息，并根据这些信息将请求转发到之前选定的后端服务器。这样，用户的会话就被“粘滞”在了同一个服务器上，直到 route 过期或被清除。

**注意事项**

- 由于是 NGINX 基于 cookie 实现的，这个特性在静态资源服务器上一样适用，不需要后台来维持会话状态。
- sticky 默认的 cookie 的 key 名称为 ‘route’, 可以通过 `sticky name=名称` 来指定，但不要与现有的 key 冲突，比如 `jsessionid`

## 健康检查

Nginx 的 `upstream` 模块提供了基本的健康检查功能，允许你定义一个简单的健康检查机制来监控后端服务器的状态。以下是如何使用 `upstream` 模块的健康检查功能的基本步骤：

1. **定义 upstream 块**：
   在 Nginx 配置文件中，你需要定义一个 `upstream` 块，其中包含你想要负载均衡的后端服务器列表。

2. **配置健康检查**：
   在 `upstream` 块中，你可以使用 `server` 指令的 `max_fails` 和 `fail_timeout` 参数来配置健康检查。`max_fails` 指定了在 `fail_timeout` 时间内，如果服务器失败的尝试次数达到这个值，Nginx 将认为该服务器不可用。`fail_timeout` 指定了服务器被认为是不可用的时间长度。

   示例配置如下：

   ```nginx
   http {
       upstream myapp {
           server backend1.example.com max_fails=3 fail_timeout=30s;
           server backend2.example.com max_fails=3 fail_timeout=30s;
           # 更多服务器...
       }

       server {
           listen 80;

           location / {
               proxy_pass http://myapp;
           }
       }
   }
   ```

   在这个例子中，如果 `backend1.example.com` 或 `backend2.example.com` 在 30 秒内失败了 3 次，Nginx 将认为该服务器不可用，并在接下来的 30 秒内不再将请求转发到该服务器。

3. **使用 `health_check` 指令**（Nginx 1.19.0 及以上版本）：
   对于 Nginx 1.19.0 及以上版本，你可以使用 `health_check` 指令来启用更高级的健康检查功能。这个指令允许你定义一个特定的路径用于健康检查请求。

   示例配置如下：

   ```nginx
   http {
       upstream myapp {
           server backend1.example.com;
           server backend2.example.com;
           # 更多服务器...

           health_check interval=30s;
       }

       server {
           listen 80;

           location / {
               proxy_pass http://myapp;
           }

           location /healthz {
               health_check;
           }
       }
   }
   ```

   在这个配置中，Nginx 将定期（每 30 秒）向 `myapp` 上游组中的服务器发送请求到 `/healthz` 路径，以检查它们的健康状态。

4. **重启 Nginx**：
   修改配置文件后，需要重启 Nginx 以应用更改。

请注意，`health_check` 指令在 Nginx 的某些版本中可能需要额外的模块支持，如 `ngx_http_upstream_module`。确保你的 Nginx 版本支持你想要使用的健康检查功能。

通过上述步骤，你可以实现 Nginx 的基本健康检查功能，以监控后端服务器的健康状态。对于更复杂的健康检查需求，你可能需要考虑使用外部的健康检查工具或服务。

### 设置请求超时后将请求发往其他服务器

在 Nginx 中，你可以通过配置 `proxy_next_upstream` 指令来实现当请求超时或遇到错误时，将请求转发到其他服务器。这个指令允许你定义在什么情况下请求应该被转发到上游服务器组中的下一个服务器。

以下是一个配置示例，展示了如何设置请求超时后将请求发往其他服务器：

```nginx
http {
    upstream myapp {
        server backend1.example.com;
        server backend2.example.com;
        # 更多服务器...
    }

    server {
        listen 80;

        location / {
            proxy_pass http://myapp;
            proxy_connect_timeout 1s;  # 连接超时时间
            proxy_send_timeout 2s;     # 发送请求超时时间
            proxy_read_timeout 3s;     # 读取响应超时时间

            # 当遇到以下情况时，将请求转发到下一个服务器
            proxy_next_upstream error timeout invalid_header http_500 http_502 http_503 http_504;
        }
    }
}
```

在这个配置中：

- `proxy_connect_timeout` 指定了 Nginx 等待与后端服务器建立连接的超时时间。
- `proxy_send_timeout` 指定了 Nginx 等待发送请求到后端服务器的超时时间。
- `proxy_read_timeout` 指定了 Nginx 等待从后端服务器接收响应的超时时间。
- `proxy_next_upstream` 指令定义了在哪些情况下请求应该被转发到下一个服务器。例如，如果遇到后端服务器错误（`error`）、超时（`timeout`）、无效的响应头（`invalid_header`）或 HTTP 500、502、503、504 错误，请求将被转发到下一个服务器。

通过合理配置这些超时参数和 `proxy_next_upstream` 指令，你可以确保当后端服务器出现问题时，Nginx 能够及时将请求转发到其他健康的服务器，从而提高系统的可用性和稳定性。

请记得在修改配置后重启 Nginx 以应用更改：

```bash
sudo systemctl restart nginx
```

或者使用 Nginx 的命令行工具：

```bash
sudo nginx -s reload
```

确保在实际部署前充分测试配置以验证其行为符合预期。

### 错误处理机制

Nginx 的错误处理机制主要通过配置指令来实现，允许你定义如何处理各种错误情况，包括服务器错误、超时、请求失败等。以下是一些关键的配置指令和概念，它们共同构成了 Nginx 的错误处理机制：

1. **error_page**：
   这个指令允许你为特定的 HTTP 状态码指定一个自定义的错误页面。当客户端请求遇到这些状态码时，Nginx 将返回指定的错误页面。

   示例配置：
   ```nginx
   error_page 404 /404.html;
   error_page 500 502 503 504 /50x.html;
   ```

2. **try_files**：
   `try_files` 指令用于按顺序检查文件的存在性，并将请求转发到第一个找到的文件或最后一个参数指定的 URI。如果所有文件都不存在，可以指定一个错误页面或重定向到另一个 URI。

   示例配置：
   ```nginx
   location / {
       try_files $uri $uri/ /index.php?$query_string;
   }
   ```

3. **proxy_next_upstream** 和 **fastcgi_next_upstream**：
   这些指令用于定义在遇到错误或超时时，请求应该被转发到上游服务器组中的下一个服务器。这有助于实现负载均衡和容错。

   示例配置：
   ```nginx
   location / {
       proxy_pass http://backend;
       proxy_next_upstream error timeout invalid_header http_500 http_502 http_503 http_504;
   }
   ```

4. **return**：
   `return` 指令用于直接返回一个指定的 HTTP 状态码和响应体给客户端。

   示例配置：
   ```nginx
   location /forbidden {
       return 403 "Access is forbidden";
   }
   ```

5. **rewrite** 和 **break**：
   这些指令用于在请求处理过程中修改请求的 URI 或终止当前的处理流程。

   示例配置：
   ```nginx
   location / {
       rewrite ^/admin/(.*) /user/$1 break;
   }
   ```

6. **error_log**：
   通过配置 `error_log` 指令，你可以指定 Nginx 错误日志的路径和级别。错误日志是诊断问题和调试配置的重要工具。

   示例配置：
   ```nginx
   error_log /var/log/nginx/error.log warn;
   ```

7. **access_log**：
   `access_log` 指令用于记录客户端请求的详细信息，包括请求的 URI、状态码、响应时间等。这有助于分析请求模式和性能问题。

   示例配置：
   ```nginx
   access_log /var/log/nginx/access.log combined;
   ```

通过这些配置指令，你可以灵活地定义 Nginx 的错误处理行为，以满足不同的需求和场景。在实际部署时，建议根据应用的具体需求和环境来调整这些配置，以确保最佳的用户体验和系统稳定性。

### rewrite 和 break 指令区别

在 Nginx 中，`rewrite` 和 `break` 指令都用于控制请求处理流程，但它们的作用和使用场景有所不同。

**rewrite 指令**

`rewrite` 指令用于修改请求的 URI。当 Nginx 处理请求时，如果匹配到 `rewrite` 指令，它会根据指令中定义的规则改变请求的 URI，并根据新的 URI 重新开始处理请求。这意味着 `rewrite` 可以用来重定向请求到不同的位置，或者将请求重写为另一个 URI。

`rewrite` 指令可以使用正则表达式，并且可以有多个规则，按照它们在配置文件中出现的顺序依次执行。如果 `rewrite` 规则匹配并执行，Nginx 会根据新的 URI 重新开始处理请求，这可能包括再次匹配 `location` 块和执行其他 `rewrite` 规则。

示例配置：
```nginx
location /oldpath {
    rewrite ^/oldpath/(.*) /newpath/$1 break;
}
```

在这个例子中，任何匹配 `/oldpath` 的请求都会被重写为 `/newpath` 加上原始请求 URI 的路径部分。

**break 指令**

`break` 指令用于终止当前的配置块中的指令执行。当 Nginx 处理请求时，如果遇到 `break` 指令，它会停止执行当前块中的剩余指令，并继续处理请求。`break` 通常用在 `if` 块或 `location` 块中，用于防止指令的进一步执行。

`break` 指令不会改变请求的 URI，它只是停止执行当前块中的指令。这与 `rewrite` 不同，后者会改变请求的 URI 并重新开始处理。

示例配置：
```nginx
location /somepath {
    if ($arg_foo = "bar") {
        rewrite ^ /otherpath break;
    }
}
```

在这个例子中，如果请求参数中包含 `foo=bar`，则请求会被重写到 `/otherpath`，并且 `if` 块中的其他指令不会被执行。

**总结**

- `rewrite` 用于修改请求的 URI 并可能重新开始处理请求。
- `break` 用于停止执行当前块中的剩余指令。

在配置 Nginx 时，根据你的需求选择合适的指令非常重要。`rewrite` 适用于需要改变请求路径的场景，而 `break` 适用于需要立即停止当前块中指令执行的场景。

# 7.动静分离（静态资源前置）

为了提高网站的响应速度，减轻程序服务器（Tomcat，Jboss等）的负载，对于静态资源，如图片、js、css等文件，可以在反向代理服务器中进行缓存，这样浏览器在请求一个静态资源时，代理服务器就可以直接处理，而不用将请求转发给后端服务器。对于用户请求的动态文件，如 servlet、jsp，则转发给 Tomcat，Jboss 服务器处理，这就是动静分离。即动态文件与静态文件的分离。

动静分离可通过 location 对请求 url 进行匹配，将网站静态资源（HTML，JavaScript，CSS，img等文件）与后台应用分开部署，提高用户访问静态代码的速度，降低对后台应用访问。通常将静态资源放到 nginx 中，动态资源转发到 tomcat 服务器中。

nginx 在处理静态资源的并发访问时，实际上是要比 Tomcat 快一些的，但在 java 的技术不断更新的基础上，这个差距在主键减小，包括 Tomcat 使用的 keepalive、epoll 等技术加持下

**动静分离适用于中小型网站**

![image-20231107205341033](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202311072053214.png)

## location 配置

主要是 location 的配置

```nginx
#user  nobody;
worker_processes  1;

events {
    worker_connections  1024;
}

http {
    include       mime.types;
    default_type  application/octet-stream;

    sendfile        on;
    keepalive_timeout  65;


    server {
        listen       80;
        server_name  localhost;
		
	# 所有 80 端口的根目录请求，访问 tomcat 服务器
        location / {
            proxy_pass http://192.168.8.101:8080;
        }
        
	# 涉及到的 images 文件夹下的资源，到本机的 html/images 目录寻找
	# 例如 192.168.8.102:80/images/test.png
	# 精确匹配的优先级要大于模糊匹配，所以这里会被代理过来
	# location 后必须有 / 号 ，
	# 也可以使用正则表达式 例如 ~
        location /images {
            root   html;
            index  index.html index.htm;
        }
        

        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   html;
        }
    }
}
```

### 常见的 Nginx 正则表达式

```
^ ：匹配输入字符串的起始位置
$ ：匹配输入字符串的结束位置
* ：匹配前面的字符零次或多次。如“ol*”能匹配“o”及“ol”、“oll”
+ ：匹配前面的字符一次或多次。如“ol+”能匹配“ol”及“oll”、“olll”，但不能匹配“o”
? ：匹配前面的字符零次或一次，例如“do(es)?”能匹配“do”或者“does”，”?”等效于”{0,1}”
. ：匹配除“\n”之外的任何单个字符，若要匹配包括“\n”在内的任意字符，请使用诸如“[.\n]”之类的模式
\ ：将后面接着的字符标记为一个特殊字符或一个原义字符或一个向后引用。如“\n”匹配一个换行符，而“\$”则匹配“$”
\d ：匹配纯数字
{n} ：重复 n 次
{n,} ：重复 n 次或更多次
{n,m} ：重复 n 到 m 次
[] ：定义匹配的字符范围
[c] ：匹配单个字符 c
[a-z] ：匹配 a-z 小写字母的任意一个
[a-zA-Z0-9] ：匹配所有大小写字母或数字
() ：表达式的开始和结束位置
| ：或运算符  //例(js|img|css)
```

### location 匹配方式及优先级：

在 Nginx 配置中，`location` 块用于定义如何处理特定的请求。根据匹配方式的不同，`location` 可以分为三类：精准匹配、一般匹配和正则匹配。下面详细解释每种类型：

1. **精准匹配（Exact Match）**:
   使用等号 `=` 开头的 `location` 块表示精准匹配。这意味着请求的 URI 必须与指定的路径完全一致才能匹配。一旦匹配成功，Nginx 将立即处理该请求，不会继续检查其他 `location` 块。
   ```nginx
   location = /exact/path {
       # 只有当请求的 URI 完全等于 /exact/path 时，才会匹配这个块
   }
   ```

2. **一般匹配（Prefix Match）**:
   不带等号 `=` 或 `~` 的 `location` 块表示一般前缀匹配。Nginx 会根据请求的 URI 与指定路径的前缀进行匹配。如果有多个匹配，Nginx 会选择最长的匹配项。
   ```nginx
   location /general/path {
       # 匹配任何以 /general/path 开头的请求 URI
   }
   ```

3. **正则匹配（Regular Expression Match）**:
   使用波浪号 `~` 开头的 `location` 块表示正则表达式匹配。这种匹配方式允许使用正则表达式来定义匹配规则，适用于更复杂的匹配需求。正则匹配是区分大小写的。
   ```nginx
   location ~ /regex/path {
       # 使用正则表达式匹配请求 URI
   }
   ```

   如果需要匹配的路径中包含正则表达式的特殊字符（如 `.`、`*` 等），需要使用 `~*` 来表示不区分大小写的正则匹配。
   ```nginx
   location ~* \.(jpg|jpeg|png)$ {
       # 匹配以 .jpg、.jpeg 或 .png 结尾的请求 URI，不区分大小写
   }
   ```

在配置 `location` 块时，Nginx 会根据请求的 URI 按照以下顺序进行匹配：

1. 精准匹配（`=`）
2. 正则匹配（`~` 或 `~*`）
3. 一般匹配（无符号）

一旦找到匹配项，Nginx 就会处理该 `location` 块中的指令。如果多个 `location` 块可以匹配同一个请求，Nginx 会根据上述优先级选择一个进行处理。因此，在配置 `location` 时，需要仔细考虑匹配规则的顺序和优先级，以确保请求能被正确处理。


### location 常用的匹配规则及优先级

1. **精确匹配（`=`）**:
   - 这种匹配方式要求请求的 URI 必须与指定的路径完全一致。一旦精确匹配成功，Nginx 将停止搜索并处理该请求。
   - 例子：`location = /index.html` 仅匹配 `/index.html`。

2. **前缀字符串匹配（`^~`）**:
   - 这种方式用于前缀匹配，但不是正则表达式匹配。如果找到匹配项，Nginx 将停止搜索并处理该请求。
   - 例子：`location ^~ /images/` 会匹配任何以 `/images/` 开头的请求。

3. **区分大小写的正则匹配（`~`）**:
   - 使用正则表达式进行匹配，且匹配是区分大小写的。
   - 例子：`location ~ \.php$` 会匹配所有以 `.php` 结尾的请求。

4. **不区分大小写的正则匹配（`~*`）**:
   - 类似于 `~`，但匹配时不区分大小写。
   - 例子：`location ~* \.css$` 会匹配所有以 `.css` 结尾的请求，无论大小写。

5. **区分大小写的正则匹配取非（`!~`）**:
   - 这是 `~` 的取反，表示不匹配给定的正则表达式。
   - 例子：`location !~ \.php$` 会匹配所有不以 `.php` 结尾的请求。

6. **不区分大小写的正则匹配取非（`!~*`）**:
   - 类似于 `!~`，但匹配时不区分大小写。
   - 例子：`location !~* \.css$` 会匹配所有不以 `.css` 结尾的请求，无论大小写。

**优先级**

Nginx 在处理请求时，会按照以下优先级顺序来匹配 `location` 块：

1. **精确匹配（`=`）**:
   - 如果有精确匹配，Nginx 会立即使用该匹配项处理请求。

2. **前缀字符串匹配（`^~`）**:
   - 如果有 `^~` 匹配，Nginx 会停止搜索并使用该匹配项处理请求。

3. **正则匹配（`~` 或 `~*`）**:
   - Nginx 会按照在配置文件中出现的顺序检查正则表达式匹配项。一旦找到匹配项，就会使用它处理请求。

4. **不带任何修饰的前缀匹配**:
   - 如果前面的匹配都没有成功，Nginx 会使用最长的前缀匹配项处理请求。

5. **通用匹配（`/`）**:
   - 如果没有任何其他匹配项，Nginx 将使用默认的前缀匹配（`/`）来处理请求。

理解这些匹配规则和优先级对于配置 Nginx 以正确响应不同请求非常重要。在实际配置中，应根据实际需求合理安排 `location` 块的顺序和规则，以确保请求能被正确处理。

**注意**：

- 精确匹配： `=` ， 后面的表达式中写的是纯字符串
- 字符串匹配： `^~` 和 无符号匹配 ， 后面的表达式中写的是纯字符串
- 正则匹配： `~` 和 `~*` 和 `!~` 和 `!~*` ， 后面的表达式中写的是正则表达式

### location 的说明

```
 (1）location = / {}
=为精确匹配 / ，主机名后面不能带任何字符串，比如访问 / 和 /data，则 / 匹配，/data 不匹配
再比如 location = /abc，则只匹配/abc ，/abc/或 /abcd不匹配。若 location  /abc，则即匹配/abc 、/abcd/ 同时也匹配 /abc/。

（2）location / {}
因为所有的地址都以 / 开头，所以这条规则将匹配到所有请求 比如访问 / 和 /data, 则 / 匹配， /data 也匹配，
但若后面是正则表达式会和最长字符串优先匹配（最长匹配）

（3）location /documents/ {}
匹配任何以 /documents/ 开头的地址，匹配符合以后，还要继续往下搜索其它 location
只有其它 location后面的正则表达式没有匹配到时，才会采用这一条

（4）location /documents/abc {}
匹配任何以 /documents/abc 开头的地址，匹配符合以后，还要继续往下搜索其它 location
只有其它 location后面的正则表达式没有匹配到时，才会采用这一条

（5）location ^~ /images/ {}
匹配任何以 /images/ 开头的地址，匹配符合以后，停止往下搜索正则，采用这一条

（6）location ~* \.(gif|jpg|jpeg)$ {}
匹配所有以 gif、jpg或jpeg 结尾的请求
然而，所有请求 /images/ 下的图片会被 location ^~ /images/ 处理，因为 ^~ 的优先级更高，所以到达不了这一条正则

（7）location /images/abc {}
最长字符匹配到 /images/abc，优先级最低，继续往下搜索其它 location，会发现 ^~ 和 ~ 存在

（8）location ~ /images/abc {}
匹配以/images/abc 开头的，优先级次之，只有去掉 location ^~ /images/ 才会采用这一条

（9）location /images/abc/1.html {}
匹配/images/abc/1.html 文件，如果和正则 ~ /images/abc/1.html 相比，正则优先级更高

优先级总结：
(location =) > (location 完整路径) > (location ^~ 路径) > (location ~,~* 正则顺序) > (location 部分起始路径) > (location /)
```

**实际网站使用中，至少有三个匹配规则定义**:

第一个必选规则

直接匹配网站根，通过域名访问网站首页比较频繁，使用这个会加速处理，比如说官网。这里是直接转发给后端应用服务器了，也可以是一个静态首页

```nginx
location = / {
    proxy_pass http://127.0.0.1:8080/; 
}
```


第二个必选规则

处理静态文件请求，这是nginx作为http服务器的强项,有两种配置模式，目录匹配或后缀匹配,任选其一或搭配使用

```nginx
location ^~ /static/ {
    root /webroot/static/;
}

location ~* \.(html|gif|jpg|jpeg|png|css|js|ico)$ {
    root /webroot/res/;
}
```

第三个规则

通用规则，用来转发动态请求到后端应用服务器

```nginx
location /api/ {
    proxy_pass http://127.0.0.1:3000/api/
}
```

## URLRewrite

rewrite是实现URL重写的关键指令，根据regex(正则表达式)部分内容，重定向到repacement，结尾是flag标记。

格式：

```
rewrite <regex> <replacement> [flag];
关键字   正则     替代内容       flag标记

关键字:其中关键字 error_1og 不能改变
正则:per1兼容正则表达式语句进行规则匹配
替代内容: 将正则匹配的内容替换成repacementflag
标记: rewrite支持的 flag 标记
rewrite参数的标签段位置:
server,location,if

flag标记说明:
last #本条规则匹配完成后，继续向下匹配新的1ocation URI规则
break #本条规则匹配完成即终止，不再匹配后面的任何规则
redirect #返回302临时重定向，浏览器地址会显示跳转后的URL地址
permanent #返回301永久重定向，浏览器地址栏会显示跳转后的URL地址
```

### 重写指令的基本语法

`rewrite` 指令的基本语法如下：

```nginx
rewrite regex replacement [flag];
```

- `regex`：一个正则表达式，用于匹配请求的 URI。
- `replacement`：当正则表达式匹配成功时，URI 将被替换为这里的值。
- `flag`：可选参数，用于控制重写的行为。常见的标志包括：
  - `last`：完成重写后，开始新的搜索，类似于重新开始处理请求。
  - `break`：停止处理当前的 `rewrite` 规则集，并继续处理当前的请求。
  - `redirect`：返回一个临时的 HTTP 302 重定向响应。
  - `permanent`：返回一个永久的 HTTP 301 重定向响应。

### 一个简单的 URL 重写示例

假设你想要将所有对旧博客文章的请求重定向到新的博客系统，你可以使用如下配置：

```nginx
server {
    listen 80;
    server_name example.com;

    location /old-blog/ {
        rewrite ^/old-blog/(.*)$ /new-blog/$1 permanent;
    }
}
```

在这个例子中，任何以 `/old-blog/` 开头的请求都会被重写为以 `/new-blog/` 开头的路径，并返回一个永久重定向（HTTP 301）。

### 使用 `last` 和 `break` 标志

`last` 和 `break` 标志用于控制重写后的处理流程：

- **last**：重写后，Nginx 会重新开始处理重写后的 URI，就像这个请求刚刚到达一样。这通常用于将请求重定向到不同的 `location` 块。
- **break**：重写后，Nginx 会停止处理当前的 `rewrite` 规则集，并继续处理当前的请求。这通常用于在同一个 `location` 块内完成重写。

### 注意事项

- 确保重写规则不会导致循环重定向，这可能会导致浏览器陷入无限重定向的循环。
- 重写规则应该谨慎使用，以避免破坏搜索引擎优化（SEO）。
- 在生产环境中部署重写规则之前，应该进行充分的测试。

通过合理使用 `rewrite` 指令，你可以灵活地控制 Nginx 的请求处理流程，满足各种复杂的 URL 重写需求。

### URLRewrite 的优缺点

优点：掩藏真实的url以及url中可能暴露的参数，以及隐藏web使用的编程语言，提高安全性便于搜索引擎收录

缺点：降低效率，影响性能。如果项目是内网使用，比如公司内部软件，则没有必要配置。

实例

![image-20231107212706294](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202311072127366.png)

```
# 例如请求 http://127.0.0.1/test.html 真实的地址其实是
# http://127.0.0.1/index.html?testParam=3
rewrite ^/test.html$ /index.html?testParam=3 break;

//也可以用正则表达式的形式：^/([0-9]).html$ 括号代表入参
rewrite ^/([0-9]).html$ /index.html?testParam=$1 break; //$1表示第一个匹配的字符串 

```

使用负载均衡的方式访问：

![image-20231107212800747](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202311072128798.png)

# 8.内网隔离

在真实的线上环境可以利用防火墙配置，使我们的应用服务器只允许 网关 nginx 的 IP 和 端口 可以访问，做到一个内外网分离

假设现在要搭建一个内网环境，内网中存在一个应用服务器 Tomcat ，则 Tomcat 不应该被外网环境直接访问，此时就可以利用 NGINX 服务器和 Linux 的防火墙来实现

在 Tomcat 服务器的 Linux 添加指定端口和ip访问(添加之后记得重新启动防火墙)

```
firewall-cmd --permanent --add-rich-rule="rule family="ipv4" source address="192.168.8.102" port protocol="tcp" port="8080" accept"
```

则此时，Tomcat 服务器就只允许配置的 NGINX 服务器访问

移除规则

```
firewall-cmd --permanent --remove-rich-rule="rule family="ipv4" source address="192.168.8.102" port protocol="tcp" port="8080" accept"
```

重启防火墙

```
firewall-cmd --reload
```

## 查看当前的防火墙规则

要查看当前的 `firewalld` 防火墙规则，你可以使用以下命令：

```bash
firewall-cmd --list-all
```

这个命令会列出所有当前激活的区域（zones）的详细信息，包括规则、服务、端口、接口等。输出结果会显示每个区域的配置，包括默认区域的设置。

输出示例可能如下所示：

```
public (active)
  target: default
  icmp-block-inversion: no
  interfaces: eth0
  sources: 
  services: ssh dhcpv6-client http https
  ports: 8080/tcp
  protocols: 
  forward: no
  sourceports: 
  icmp-blocks: 
  rich rules: 
    rule family="ipv4" source address="192.168.8.102" port protocol="tcp" port="8080" accept
```

在这个示例中，`rich rules` 部分显示了之前添加的规则，允许来自 IP 地址 `192.168.8.102` 的 TCP 流量通过端口 `8080`。

请注意，根据你的系统配置和已应用的规则数量，输出内容可能会有所不同。如果你只想查看特定区域的规则，可以使用 `--zone=<zone_name>` 选项，例如：

```bash
firewall-cmd --list-all --zone=public
```

这将仅显示名为 `public` 的区域的规则。

确保在执行这些命令时你具有足够的权限，通常需要 root 权限。如果你的系统使用的是不同的防火墙管理工具（如 iptables），则需要使用相应的命令和语法来查看规则。

# 9.防盗链

盗链是指服务提供商自己不提供服务的内容，通过技术手段绕过其它有利益的最终用户界面（如广告），直接在自己的网站上向最终用户提供其它服务提供商的服务内容，骗取最终用户的浏览和点击率。受益者不提供资源或提供很少的资源，而真正的服务提供商却得不到任何的收益。

**防盗链即服务器上的资源只允许配置的服务器引用**，不允许其他服务器引用，比如qq空间中的图片只能在qq空间中引用和查看，其他服务器无法查看。

## 防盗链原理

当在访问一个网站页面时，服务器会返回一个 HTML 页面，在 HTML 页面中可能存在有 img、css、js 等资源链接，而在浏览器继续使用当前页面去二次请求时，浏览器会在请求头上添加一个 Referer 字段来标识请求的来源页面，这是一个协议，由浏览器遵守和实现，所以我们可以通过该信息来确认请求来源。

`Referer` 是 HTTP 协议中的一个头部字段，用于标识请求的来源页面。当用户从一个页面通过链接跳转到另一个页面时，浏览器会自动在发起的 HTTP 请求中包含一个 `Referer` 头部，其值为用户当前页面的 URL。这个字段最初被设计用来帮助网站管理员了解用户如何到达他们的网站，以便于分析流量来源和优化网站结构。

**Referer 头部的用途**

1. **流量分析**：网站管理员可以使用 `Referer` 字段来分析用户是从哪些页面跳转到他们的网站的，从而了解哪些外部链接或搜索引擎对网站流量贡献最大。

2. **防盗链**：网站可以利用 `Referer` 字段来防止未授权的资源访问。例如，如果一个图片或视频资源只能从特定页面访问，网站可以通过检查 `Referer` 字段来确保请求是从该页面发起的。

3. **内容保护**：一些内容管理系统（CMS）或在线服务可能使用 `Referer` 字段来限制对某些内容的访问，确保用户是通过合法途径访问内容。

**Referer 头部的限制和问题**

- **隐私问题**：`Referer` 字段可能会暴露用户的浏览历史，这可能涉及隐私问题。出于隐私保护的考虑，一些浏览器允许用户配置是否发送 `Referer` 头部，或者可以限制发送的 `Referer` 信息的详细程度。

- **不总是可靠**：`Referer` 字段依赖于浏览器的正确实现和用户没有修改浏览器设置。在某些情况下，`Referer` 字段可能为空或被篡改，因此不能完全依赖它来保证安全性。

- **可被绕过**：由于 `Referer` 字段不是强制性的，恶意用户可以通过特定的手段（如修改 HTTP 请求或使用某些浏览器插件）绕过 `Referer` 检查。

**Referer 头部的配置和使用**

在服务器端，如 Nginx 或 Apache，可以通过配置规则来检查 `Referer` 头部，以实现防盗链等安全策略。例如，在 Nginx 中，可以使用 `valid_referers` 指令来定义合法的来源，并根据 `Referer` 字段的值来决定是否允许访问资源。

`Referer` 字段是 HTTP 协议中用于标识请求来源的一个头部，它在流量分析、防盗链和内容保护方面有其用途。然而，由于隐私和安全性的考虑，它并不是一个完全可靠的机制，需要与其他安全措施结合使用。

## 防盗链实现

Nginx 防盗链功能的原理主要是通过检查请求头中的 `Referer` 字段来实现的。`Referer` 字段通常包含发起请求的页面的 URL，这个信息可以用来判断请求是否来自合法的来源。以下是 Nginx 防盗链的基本工作原理：

1. **检查 `Referer` 字段**:
   当一个请求到达 Nginx 服务器时，Nginx 会检查请求头中的 `Referer` 字段。这个字段包含了发起请求的页面的 URL。

2. **定义合法来源**:
   在 Nginx 配置中，可以定义一组合法的来源（即允许访问资源的网站）。这些来源通常以域名的形式指定。

3. **拒绝非法请求**:
   如果请求的 `Referer` 字段不匹配预定义的合法来源列表，Nginx 可以拒绝该请求，返回一个错误页面或 HTTP 错误代码（如 403 Forbidden），从而阻止非法访问。

4. **配置示例**:
   在 Nginx 配置文件中，可以使用 `valid_referers` 指令来定义合法的来源，并使用 `if` 语句来检查 `Referer` 字段。以下是一个简单的配置示例：

   ```nginx
   server {
       listen 80;
       server_name example.com;

       location /images/ {
           valid_referers none blocked server_names ~\.example\.com$;
           if ($invalid_referer) {
               return 403;
           }
           # 其他配置，如 proxy_pass 或 root 指令
       }
   }
   ```

   在这个例子中，`valid_referers` 指令定义了三种合法来源：
   - `none`：没有 `Referer` 字段的情况。
   - `blocked`：`Referer` 字段被浏览器或代理服务器阻止的情况。
   - `server_names`：以 `.example.com` 结尾的域名。

   如果 `Referer` 字段不符合这些条件，`$invalid_referer` 变量将被设置为真（true），`if` 语句将触发，返回 403 禁止访问错误。

5. **其他方法**:
   除了检查 `Referer` 字段，还可以使用其他方法来防止资源被盗链，例如：
   - 使用签名 URL，即在资源 URL 中加入一个时间戳或密钥，只有知道密钥的用户才能访问。
   - 通过服务器端验证，例如检查请求是否来自特定的 IP 地址或用户代理（User-Agent）。

Nginx 的防盗链功能是通过简单的配置实现的，可以有效地防止未授权的资源访问，保护网站内容不被非法使用。

示例

在任意的 location 中配置

```nginx
location ^~/images/ {
    valid_referers 192.168.8.102;  #valid_referers 指令，配置是否允许 referer 头部以及允许哪些 referer 访问。192.168.8.102不是ip而是域名（去掉http:// 前缀）
    if ($invalid_referer) {  # 注意这里if后要加空格
        return 403; ## 返回错误码
    }
    
    root   /www/resources;
}
```

valid_referers 解释

可以同时携带多个参数，表示多个 referer 头部都生效。

参数值

- none：允许没有 referer 信息的请求访问，即直接通过url访问。
- blocked：请求头Referer字段不为空（即存在Referer），但是值可以为空（值被代理或者防火墙删除了），并且允许refer不以“http://”或“https://”开头，通俗点说就是允许“http://”或"https//"以外的请求。
- server_names：若 referer 中站点域名与 server_name 中本机域名某个匹配，则允许该请求访问
- 其他字符串类型：检测referer与字符串是否匹配，如果匹配则允许访问，可以采用通配符*
- 正则表达式：若 referer 的值匹配上了正则，就允许访问

invalid_referer 变量

- 允许访问时变量值为空
- 不允许访问时变量值为 1

例

```nginx
server {
    server_name referer.test.com;
    listen 80;

    error_log logs/myerror.log debug;
    root html;
    location / {
        valid_referers none server_names
                       *.test.com www.test.org.cn/nginx/;
        if ($invalid_referer) {
                return 403; # 返回错误码
        }
        return 200 'valid\n';
    }
}

# none：表示没有 referer 的可以访问
# server_names：表示本机 server_name 也就是 referer.test.com 可以访问
# *.test.com：匹配上了正则的可以访问
# www.test.org.cn/nginx/：该页面发起的请求可以访问
```

# 10.配置错误提示页面

在 Nginx 中配置错误提示页面允许你自定义当用户遇到特定类型的 HTTP 错误时看到的页面。这不仅提升了用户体验，还可以提供更清晰的错误信息、帮助用户理解发生了什么问题，以及如何解决。常见的错误提示页面包括 404（未找到页面）、403（禁止访问）、500（服务器内部错误）等。

**配置错误提示页面的步骤**

1. **创建自定义错误页面**:
    首先，你需要创建一个 HTML 文件，这个文件将作为错误提示页面。例如，创建一个名为 `404.html` 的文件，用于 404 错误。

2. **配置 Nginx**:
    在 Nginx 配置文件中，你可以使用 `error_page` 指令来指定当遇到特定错误代码时显示的自定义页面。通常，这些配置位于 `http`、`server` 或 `location` 块中。

    以下是一个配置示例，它告诉 Nginx 当遇到 404 错误时显示 `404.html` 页面：

    ```nginx
    server {
        listen 80;
        server_name example.com;

        # 其他配置...

        error_page 404 /404.html;
        location = /404.html {
            internal;
        }
    }
    ```

    在这个例子中，`error_page 404 /404.html;` 指令告诉 Nginx 当发生 404 错误时，显示位于服务器根目录下的 `404.html` 页面。`location = /404.html` 块确保只有直接访问 `404.html` 时才会显示该页面，而 `internal` 指令确保该页面不能通过 URL 直接访问，只能作为错误页面显示。

3. **重启 Nginx**:
    修改配置文件后，需要重启 Nginx 以使更改生效。可以使用以下命令之一：

    ```bash
    sudo systemctl restart nginx
    # 或者
    sudo nginx -s reload
    ```

**注意事项**

- 确保自定义错误页面位于 Nginx 可以访问的路径下。
- 使用 `internal` 指令可以防止用户直接通过 URL 访问错误页面，确保错误页面只在错误发生时显示。
- 你可以为不同的错误代码配置不同的页面，例如 `error_page 500 502 503 504 /50x.html;`。
- 除了自定义页面，你还可以指定重定向到另一个 URL，例如 `error_page 404 http://example.com/notfound;`。

通过配置自定义错误提示页面，你可以提供更加友好和有用的反馈给用户，同时保持网站的专业形象。

**也可以返回出错图片**

![image-20231109204701059](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202311092047142.png)

# 11.高可用配置，nginx 集群

高可用场景及解决方案

利用 Keepalived 的虚拟 IP 来实现，多台机器使用同一虚拟 IP，此 IP 在多台机器上来回切换，以达到高可用集群的目的

假设 101 为主 nginx，102 为备用机，首先需要修改 101 和 102 的 keepalived.conf 配置

101 的 keepalived.conf 配置，使用 ens33 网卡

```conf
! Configuration File for keepalived

global_defs {
   router_id LB_102 # LB_102 为负载均衡的机器名称，可以随便起名，别重复
}

vrrp_instance VI_102 # VI_102 为实例名称，集群保持一致 {
    state MASTER # MASTER/BACKUP
    interface ens33 # ens33网卡名称
    virtual_router_id 51 # 集群保持一致
    priority 100 # 竞选 MASTER 节点的优先级
    advert_int 1 # 检测存活时间，心跳间隔
    # keepalived 分组名称信息 集群保持一致
    authentication {
        auth_type PASS
        auth_pass 1111
    }
    # 虚拟的IP地址，可填写多个
    virtual_ipaddress { 
        192.168.8.200
    }
}
```

使用 systemctl start keepalived 启动 keepalived ,查看 ip 发现多了虚拟 ip 192.168.8.200

102 的 keepalived.conf 配置,使用 ens33 网卡

```conf
! Configuration File for keepalived

global_defs {
   router_id LB_101
}

vrrp_instance VI_102 {
    state BACKUP
    interface ens33
    virtual_router_id 51
    priority 50
    advert_int 1
    authentication {
        auth_type PASS
        auth_pass 1111
    }
    virtual_ipaddress {
        192.168.8.200
    }
}
```

使用 systemctl start keepalived 启动 keepalived

在 nginx 集群状态下，需要访问虚拟 IP，即 VIP 162.168.8.200：

关闭 102，再次访问 192.168.8.200：

发现可以正常访问

Keepalived 是进程检测，即它只检测自己进程是否存活，不检测其他软件情况。可以编写脚本来检测 Nginx 服务运行情况，当 Nginx 宕机直接杀死本机Keepalived，以达到 IP 漂移切换的目的，所以 Keepalived 可以检测一切软件，如 redis、mysql 等

## 使用脚本检测服务状态

你可以编写一个脚本来检测特定服务（如 Nginx）的状态。如果服务停止运行，脚本可以执行一些操作，比如发送通知或直接杀死 Keepalived 进程，从而触发虚拟IP的漂移。

以下是一个简单的示例脚本，用于检测 Nginx 是否在运行：

```bash
#!/bin/bash

# 检测 Nginx 是否在运行
NGINX_STATUS=$(pgrep -f nginx)

# 如果 Nginx 没有运行（即 $NGINX_STATUS 为空），则杀死 Keepalived
if [ -z "$NGINX_STATUS" ]; then
    # 杀死 Keepalived 进程，这将触发虚拟IP的漂移
    pkill keepalived
    echo "Nginx is down. Killed Keepalived to trigger failover."
else
    echo "Nginx is running."
fi
```

确保此脚本具有执行权限：

```bash
chmod +x /path/to/your/script.sh
```

然后，你可以将此脚本设置为定时任务（使用 `cron`），定期检查 Nginx 的状态。

### 脚本进阶使用

在生产环境下，普通的 NGINX 检测脚本无法满足实际需求，通常还需要故障自动恢复功能，因此，应在上述脚本的基础上进行改造。

使用脚本来检测本机的 NGINX 的服务状态，如果 NGINX 存活且可正常提供服务（即没有无响应或响应超时），则什么也不做，否则杀死 Keepalived 进程，这将触发虚拟IP的漂移

1. 编写脚本

```bash
#!/bin/bash

# 设置超时时间（秒）
TIMEOUT=1

# 检测 NGINX 状态的函数
check_nginx() {
    # 尝试访问 NGINX 的默认页面
    local response=$(curl --max-time $TIMEOUT -s -o /dev/null -w "%{http_code}" http://localhost)
    # 检查 HTTP 状态码
    if [ "$response" -eq 200 ]; then
        echo "NGINX is running and responding properly."
        return 0
    else
        echo "NGINX is not responding properly. Received status code: $response"
        # 杀死 Keepalived 进程，这将触发虚拟IP的漂移
        pkill keepalived
        return 1
    fi
}

# 执行检测
check_nginx
```

保存这个脚本到一个文件中，比如 `check_nginx.sh`，然后给它执行权限：

```bash
chmod 777 check_nginx.sh
```

运行脚本：

```bash
./check_nginx.sh
```

### 示例脚本

```shell
#!/bin/bash

# 设置尝试连接 nginx 的超时时间（秒）
try_conn_timeout=1

# 尝试重启 nginx
try_restart_nginx(){

    if systemctl is-active --quiet nginx; then
        echo "try_restart_nginx : nginx is active"
        return 0
    else
        echo "try_restart_nginx : nginx is die, try restart nginx"
        systemctl restart nginx
    fi

    if systemctl is-active --quiet nginx; then
        echo "try_restart_nginx : nginx restart success"
        return 0
    else
        echo "try_restart_nginx : nginx restart fail"
        return 1
    fi

}

# 尝试重启 keepalived
try_restart_keepalived(){

    # 是否需要重启 0 否 1 是
    local is_need_restart=0

    try_conn_nginx
    # nginx 联通状态
    local nginx_conn_status=$?

    if [ "$nginx_conn_status" -eq 0 ]; then
        # nginx 服务正常
        echo "nginx 状态恢复，可以重启 keepalived"
        is_need_restart=1
    fi

    if [ "$is_need_restart" -eq 0 ]; then
        # nginx 服务正常
        echo "nginx 状态故障，不重启 keepalived"
        return 0
    fi


    if systemctl is-active --quiet keepalived; then
        echo "try_restart_keepalived : keepalived is active"
        return 0
    else
        echo "try_restart_keepalived : keepalived is die, try restart keepalived"
        systemctl restart keepalived
    fi

    if systemctl is-active --quiet keepalived; then
        echo "try_restart_keepalived : keepalived restart success"
        return 0
    else
        echo "try_restart_keepalived : keepalived restart fail"
        return 1
    fi

}

# 尝试访问 NGINX
try_conn_nginx(){
    # 尝试访问 NGINX 的默认页面
    local response
    response=$(curl --max-time $try_conn_timeout -s -o /dev/null -w "%{http_code}" http://localhost)
    # 检查 HTTP 状态码
    if [ "$response" -eq 200 ]; then
        echo "try_conn_nginx : NGINX is running and responding properly. Received status code: $response"
        return 0
    else
        echo "try_conn_nginx : NGINX is not responding properly. Received status code: $response"
        return 1
    fi
}



# 检测 NGINX 状态的函数
check_nginx() {

    try_conn_nginx
    # nginx 联通状态
    local nginx_conn_status=$?

    if [ "$nginx_conn_status" -eq 0 ]; then
        # nginx 服务正常
        return 0
    else
       # 杀死 Keepalived 进程，这将触发虚拟IP的漂移
       pkill keepalived
       # 尝试恢复 nginx 服务
       try_restart_nginx
       # 尝试重启 keepalived
       try_restart_keepalived
    fi

}

# 执行检测
check_nginx
```

## 使用 Keepalived 的脚本钩子功能

使用脚本钩子可以实现复杂的故障转移逻辑和自定义的高可用性行为，但需要仔细设计脚本以确保它们的稳定性和效率。

下面是如何配置 Keepalived 脚本钩子的步骤：

### 步骤 1: 配置 Keepalived

在 Keepalived 的配置文件 `/etc/keepalived/keepalived.conf` 中，添加 `vrrp_script` 部分来指定脚本及其执行频率。以下是一个配置示例：

```conf
! Configuration File for keepalived

global_defs {
   router_id NODE1
}

vrrp_script check_nginx {
    script "/opt/check_nginx.sh"  # 指定脚本路径
    interval 1                    # 每5秒执行一次脚本
}

vrrp_instance VI_1 {
    state MASTER
    interface ens33
    virtual_router_id 51
    priority 100
    advert_int 1
    authentication {
        auth_type PASS
        auth_pass 1111
    }
    virtual_ipaddress {
        192.168.1.200
    }
    track_script {
    	check_nginx				# 你上面定义的名字叫 check_nginx
    }
}
```

在这个配置中，`vrrp_script` 部分定义了一个名为 `check_nginx` 的脚本钩子，它会每 1 秒执行一次指定的脚本。

### 步骤 2: 重启 Keepalived

配置完成后，需要重启 Keepalived 服务以应用更改：

```bash
sudo systemctl restart keepalived
```

或者，如果你的系统不使用 `systemctl`，可以使用以下命令：

```bash
sudo service keepalived restart
```

# 12.https 签名

参考 《凤凰架构》-周志明

## 不安全的 HTTP 协议

当与一个站点进行通讯时，在通讯的过程中，一般会经历多个协议，其由底层到高层的顺序一般为 socket(最底层，即调用网卡发送数据) -> TCP/IP -> HTTP(应用层)

在 HTTP 访问时，客户端与服务器之间没有任何身份确认的过程，数据全部明文传输，“裸奔”在互联网上，所以很容易遭到黑客的攻击。客户端发出的请求很容易被黑客截获，如果此时黑客冒充服务器，则其可返回任意信息给客户端，而不被客户端察觉，所以我们经常会听到一词“劫持”。

所以 HTTP 传输面临的风险有

1. 窃听风险：黑客可以获知通信内容。
2. 篡改风险：黑客可以修改通信内容。
3. 冒充风险：黑客可以冒充他人身份参与通信。

## HTTP 向 HTTPS 演化的过程

第一步：为了防止上述现象的发生，人们想到一个办法：对传输的信息加密（即使黑客截获，也无法破解）

### 对称加密

对称加密算法是应用较早的加密算法，技术成熟。在对称加密算法中，数据发信方将明文（原始数据）和加密密钥（mi yao）一起经过特殊加密算法处理后，使其变成复杂的加密密文发送出去。收信方收到密文后，若想解读原文，则需要使用加密用过的密钥及相同算法的逆算法对密文进行解密，才能使其恢复成可读明文。在对称加密算法中，使用的密钥只有一个，发收信双方都使用这个密钥对数据进行加密和解密，这就要求解密方事先必须知道加密密钥。但此种方式的缺点是：

- 不同的客户端、服务器数量庞大，所以双方都需要维护大量的密钥，维护成本很高
- 因每个客户端、服务器的安全级别不同，密钥极易泄露

### 非对称加密

非对称加密需要两个密钥：公钥 (publickey) 和私钥 (privatekey)。公钥和私钥是一对，如果用公钥对数据加密，那么只能用对应的私钥解密。如果用私钥对数据加密，只能用对应的公钥进行解密。因为加密和解密用的是不同的密钥，所以称为非对称加密。

客户端用公钥对请求内容加密，服务器使用私钥对内容解密，反之亦然，但上述过程也存在缺点：

- 公钥是公开的（也就是黑客也会有公钥），所以私钥加密的信息，如果被黑客截获，其可以使用公钥进行解密，获取其中的内容

鉴于非对称加密的机制，我们可能会有这种思路：服务器先把公钥以明文方式传输给浏览器，之后浏览器向服务器传数据前都先用这个公钥加密好再传，这条数据的安全似乎可以保障了！因为只有服务器有相应的私钥能解开公钥加密的数据。

然而反过来由服务器到浏览器的这条路怎么保障安全？如果服务器用它的私钥加密数据传给浏览器，那么浏览器用公钥可以解密它，而这个公钥是一开始通过明文传输给浏览器的，若这个公钥被中间人劫持到了，那他也能用该公钥解密服务器传来的信息了。所以目前似乎只能保证由浏览器向服务器传输数据的安全性。

### 改良的非对称加密方案

我们已经理解通过一组公钥私钥，可以保证单个方向传输的安全性，那用两组公钥私钥，是否就能保证双向传输都安全了？请看下面的过程：

1. 某网站服务器拥有公钥A与对应的私钥A’；浏览器拥有公钥B与对应的私钥B’。
2. 浏览器把公钥B明文传输给服务器。
3. 服务器把公钥A明文给传输浏览器。
4. 之后浏览器向服务器传输的内容都用公钥A加密，服务器收到后用私钥A’解密。由于只有服务器拥有私钥A’，所以能保证这条数据的安全。
5. 同理，服务器向浏览器传输的内容都用公钥B加密，浏览器收到后用私钥B’解密。同上也可以保证这条数据的安全。

的确可以！抛开这里面仍有的漏洞不谈，HTTPS的加密却没使用这种方案，为什么？很重要的原因是非对称加密算法非常耗时，而对称加密快很多。那我们能不能运用非对称加密的特性解决前面提到的对称加密的漏洞？

### 非对称加密+对称加密

既然非对称加密耗时，那非对称加密+对称加密结合可以吗？而且得尽量减少非对称加密的次数。当然是可以的，且非对称加密、解密各只需用一次即可。
请看一下这个过程：

1. 某网站拥有用于非对称加密的公钥A、私钥A’。
2. 浏览器向网站服务器请求，服务器把公钥A明文给传输浏览器。
3. 浏览器随机生成一个用于对称加密的密钥X，用公钥A加密后传给服务器。
4. 服务器拿到后用私钥A’解密得到密钥X。
5. 这样双方就都拥有密钥X了，且别人无法知道它。之后双方所有数据都通过密钥X加密解密即可。

如果在数据传输过程中，中间人劫持到了数据，此时他的确无法得到浏览器生成的密钥X，这个密钥本身被公钥A加密了，只有服务器才有私钥A’解开它，然而中间人却完全不需要拿到私钥A’就能干坏事了。请看：

1. 某网站有用于非对称加密的公钥A、私钥A’。
2. 浏览器向网站服务器请求，服务器把公钥A明文给传输浏览器。
3. **中间人劫持到公钥A，保存下来，把数据包中的公钥A替换成自己伪造的公钥B（它当然也拥有公钥B对应的私钥B’）。**
4. 浏览器生成一个用于对称加密的密钥X，用公钥B（浏览器无法得知公钥被替换了）加密后传给服务器。
5. **中间人劫持后用私钥B’解密得到密钥X，再用公钥A加密后传给服务器。**
6. 服务器拿到后用私钥A’解密得到密钥X。

这样在双方都不会发现异常的情况下，中间人通过一套“狸猫换太子”的操作，掉包了服务器传来的公钥，进而得到了密钥X。根本原因是浏览器无法确认收到的公钥是不是网站自己的，因为公钥本身是明文传输的，难道还得对公钥的传输进行加密？这似乎变成鸡生蛋、蛋生鸡的问题了。

**如何证明浏览器收到的公钥一定是该网站的公钥**

其实所有证明的源头都是一条或多条不证自明的“公理”（可以回想一下数学上公理），由它推导出一切。比如现实生活中，若想证明某身份证号一定是小明的，可以看他身份证，而身份证是由政府作证的，这里的“公理”就是“政府机构可信”，这也是社会正常运作的前提。

那能不能类似地有个机构充当互联网世界的“公理”呢？让它作为一切证明的源头，给网站颁发一个“身份证”？

它就是CA机构，它是如今互联网世界正常运作的前提，而CA机构颁发的“身份证”就是数字证书。

网站在使用 HTTPS 前，需要向 CA 机构申领一份数字证书，数字证书里含有证书持有者信息、公钥信息等。服务器把证书传输给浏览器，浏览器从证书里获取公钥就行了，证书就如身份证，证明“该公钥对应该网站”。而这里又有一个显而易见的问题，“证书本身的传输过程中，如何防止被篡改”？即如何证明证书本身的真实性？身份证运用了一些防伪技术，而数字证书怎么防伪呢？解决这个问题我们就接近胜利了！

如何放防止数字证书被篡改

我们把证书原本的内容生成一份“签名”，比对证书内容和签名是否一致就能判别是否被篡改。这就是数字证书的“防伪技术”，这里的“签名”就叫数字签名

数字签名的制作过程

1. CA机构拥有非对称加密的私钥和公钥。
2. CA机构对证书明文数据T进行hash。
3. 对hash后的值用私钥加密，得到数字签名S。

明文和数字签名共同组成了数字证书，这样一份数字证书就可以颁发给网站了。
那浏览器拿到服务器传来的数字证书后，如何验证它是不是真的？（有没有被篡改、掉包）

浏览器验证过程

1. 拿到证书，得到明文T，签名S。
2. 用CA机构的公钥对S解密（由于是浏览器信任的机构，所以浏览器保有它的公钥。详情见下文），得到S’。
3. 用证书里指明的hash算法对明文T进行hash得到T’。
4. 显然通过以上步骤，T’应当等于S‘，除非明文或签名被篡改。所以此时比较S’是否等于T’，等于则表明证书可信。

为何么这样可以保证证书可信呢？我们来仔细想一下。

假设中间人篡改了证书的原文，由于他没有CA机构的私钥，所以无法得到此时加密后签名，无法相应地篡改签名。浏览器收到该证书后会发现原文和签名解密后的值不一致，则说明证书已被篡改，证书不可信，从而终止向服务器传输信息，防止信息泄露给中间人。

既然不可能篡改，那整个证书被掉包呢？

假设有另一个网站B也拿到了CA机构认证的证书，它想劫持网站A的信息。于是它成为中间人拦截到了A传给浏览器的证书，然后替换成自己的证书，传给浏览器，之后浏览器就会错误地拿到B的证书里的公钥了，这确实会导致上文“中间人攻击”那里提到的漏洞？

其实这并不会发生，因为证书里包含了网站A的信息，包括域名，浏览器把证书里的域名与自己请求的域名比对一下就知道有没有被掉包了。

## https 证书配置

### 自签名 

# 13.KeepAlive

`KeepAlive` 是 HTTP 协议中的一个特性，它允许在同一个 TCP 连接上进行多个 HTTP 请求和响应。在没有 `KeepAlive` 的情况下，每个 HTTP 请求都需要建立一个新的 TCP 连接，这会增加延迟并消耗更多资源。启用 `KeepAlive` 可以减少连接建立和关闭的开销，提高网站的响应速度和服务器的处理能力。

在 Nginx 中，`KeepAlive` 功能是默认启用的，但可以通过配置文件进行调整。Nginx 的配置文件通常位于 `/etc/nginx/nginx.conf` 或者 `/etc/nginx/sites-available/` 目录下的特定站点配置文件中。

以下是一些与 `KeepAlive` 相关的 Nginx 配置指令：

1. `keepalive_requests`：这个指令用于设置一个 TCP 连接上允许的最大请求数量。默认值通常是 100，意味着一个连接最多可以处理 100 个请求。

2. `keepalive_timeout`：这个指令用于设置服务器在关闭连接之前等待另一个请求的时间。默认值是 65 秒。如果在 `keepalive_timeout` 指定的时间内没有新的请求到达，连接将被关闭。

例如，你可以在 Nginx 配置文件中找到类似下面的配置：

```nginx
server {
    ...
    keepalive_requests 100;
    keepalive_timeout 65;
    ...
}
```

调整这些值可以根据你的服务器负载和性能需求来优化连接管理。例如，如果你的服务器处理的是大量短连接请求，增加 `keepalive_requests` 的值可能有助于减少连接的频繁建立和关闭。如果服务器资源有限，可能需要调整 `keepalive_timeout` 以确保及时释放资源。

需要注意的是，虽然 `KeepAlive` 可以提高性能，但过度使用也可能导致资源占用问题，特别是在高并发情况下。因此，合理配置这些参数对于优化服务器性能至关重要。

在 HTTP 头部（header）中，`Keep-Alive` 是一个与持久连接（Persistent Connections）相关的响应头字段，它用于控制客户端和服务器之间是否应该保持连接打开状态，以便进行后续的请求。这个字段通常由服务器在响应中发送给客户端，指示客户端是否可以重用当前的 TCP 连接来发送更多的请求。

当服务器决定启用持久连接时，它会在响应头中包含 `Keep-Alive` 字段，其值可能如下所示：

```
Keep-Alive: timeout=5, max=100
```

这个例子中，`timeout=5` 表示服务器将在 5 秒后关闭空闲连接，`max=100` 表示一个连接最多可以处理 100 个请求。

然而，需要注意的是，HTTP/1.1 默认就是支持持久连接的，因此在 HTTP/1.1 的响应中，`Keep-Alive` 头部并不是必须的。如果服务器不希望启用持久连接，它可能会发送一个 `Connection: close` 头部，明确告诉客户端在当前请求响应后关闭连接。

对于 HTTP/2 和 HTTP/3，持久连接是默认行为，且这两个版本的 HTTP 协议使用了不同的机制来管理连接，不再使用 `Keep-Alive` 头部。HTTP/2 使用帧和流的概念来管理多个请求和响应，而 HTTP/3 则基于 QUIC 协议，它提供了更高效的连接复用和管理机制。

总结一下，`Keep-Alive` 头部主要用于 HTTP/1.0 和早期的 HTTP/1.1 实现中，用于指示是否保持连接打开。在现代的 HTTP 版本中，持久连接是默认行为，通常不需要显式地在头部中声明。

## 对客户端的常用配置

### 关闭 KeepAlive

在 NGINX 中关闭 KeepAlive 功能，你需要编辑 NGINX 的配置文件。

找到 `http`、`server` 或者 `location` 块中的 `keepalive_timeout` 指令。如果 `keepalive_timeout` 没有被设置，你可以添加它。将其值设置为 `0` 来关闭 `KeepAlive` 功能。例如：

```nginx
keepalive_timeout 0;
```

### 禁止某些浏览器使用 KeepAlive

在 NGINX 中，`keepalive_disable` 指令用于控制哪些客户端可以使用 HTTP 保持连接（Keep-Alive）。保持连接允许在同一个 TCP 连接上发送和接收多个 HTTP 请求/响应，而不是每个请求都建立一个新的连接。这可以减少连接的开销，提高性能。

`keepalive_disable` 指令可以接受以下参数：

- `msie6`：禁用来自旧版 Microsoft Internet Explorer（版本 6 及以下）的保持连接。旧版 IE 在处理 Keep-Alive 时存在问题，因此默认情况下 NGINX 会禁用这些版本的 IE 的保持连接功能。
- `none`：不禁用任何客户端的保持连接。
- `browser`：禁用所有浏览器的保持连接。
- `nginx`：禁用 NGINX 作为客户端时的保持连接。
- `safari`：禁用 Safari 浏览器的保持连接。

你可以根据需要组合使用这些参数。例如，如果你想要禁用所有浏览器的保持连接，可以在配置文件中设置如下：

```nginx
keepalive_disable browser;
```

如果你想要针对特定浏览器或条件进行设置，可以组合使用多个参数，例如：

```nginx
keepalive_disable msie6 safari;
```

这将禁用来自旧版 IE 和 Safari 的保持连接。

请注意，通常情况下，保持连接是被启用的，因为它可以提高服务器的性能。只有在特定情况下，比如当特定浏览器或客户端存在问题时，才需要禁用保持连接。在修改这个设置之前，建议评估是否真的需要这样做，因为禁用保持连接可能会导致服务器性能下降。

`keepalive_disable` 指令通常配置在 NGINX 的 `http`、`server` 或者 `location` 上下文中。具体位置取决于你想要禁用保持连接的范围和条件。下面是一些常见的配置位置和它们的影响：

1. **http 上下文**：如果你想要对所有服务器和位置块应用 `keepalive_disable` 设置，可以将其放在 `http` 块中。这将影响整个 NGINX 服务器。

    ```nginx
    http {
        keepalive_disable browser;  # 禁用所有浏览器的保持连接
        ...
    }
    ```

2. **server 上下文**：如果你只想在特定的虚拟主机或服务器块中禁用保持连接，可以将 `keepalive_disable` 放在 `server` 块中。

    ```nginx
    server {
        listen 80;
        server_name example.com;
        keepalive_disable msie6;  # 仅禁用旧版 IE 的保持连接
        ...
    }
    ```

3. **location 上下文**：如果你只想在特定的路径或位置块中禁用保持连接，可以将 `keepalive_disable` 放在 `location` 块中。

    ```nginx
    server {
        listen 80;
        server_name example.com;
        location / {
            keepalive_disable safari;  # 仅禁用 Safari 的保持连接
            ...
        }
    }
    ```

4. **多个参数组合**：你也可以在同一个上下文中组合使用多个参数，以适应不同的需求。

    ```nginx
    http {
        keepalive_disable msie6 browser;  # 禁用旧版 IE 和所有浏览器的保持连接
        ...
    }
    ```

在修改配置文件后，记得重新加载或重启 NGINX 以使更改生效：

```bash
sudo systemctl reload nginx
```
或者
```bash
sudo systemctl restart nginx
```

选择正确的配置位置取决于你想要禁用保持连接的范围。通常，除非有特定的兼容性问题，否则不建议在 `http` 上下文中全局禁用保持连接，因为这可能会影响服务器的性能。

### 设置 keepalive 超时时间

在 NGINX 中，`keepalive_timeout` 指令用于设置 HTTP 保持连接（Keep-Alive）的超时时间。这意味着，一旦一个 TCP 连接被建立用于多个请求/响应交换，它将在空闲状态下保持打开状态的最长时间。超过这个时间后，如果没有任何新的请求通过该连接，连接将被关闭。

`keepalive_timeout` 的单位通常是秒。例如，如果你设置 `keepalive_timeout` 为 60 秒，那么在没有任何请求通过连接的 60 秒后，连接将被关闭。

下面是一个配置示例：

```nginx
http {
    keepalive_timeout 60;  # 设置保持连接的超时时间为 60 秒
    ...
}
```

或者，如果你想要在特定的 `server` 或 `location` 块中设置不同的超时时间，可以这样配置：

```nginx
server {
    listen 80;
    server_name example.com;

    keepalive_timeout 60;  # 为这个特定的 server 块设置保持连接的超时时间

    location / {
        # 其他配置...
    }
}
```

请注意，`keepalive_timeout` 指令有两个参数：第一个参数是设置客户端响应超时时间，第二个参数（可选）是设置服务器端的超时时间。如果只提供一个参数，它将同时应用于客户端和服务器端。

例如：

```nginx
keepalive_timeout 60 30;  # 客户端超时时间为 60 秒，服务器端超时时间为 30 秒
```

在配置 `keepalive_timeout` 时，需要权衡性能和资源使用。较短的超时时间可以减少服务器资源的占用，但可能会增加建立新连接的频率，从而影响性能。较长的超时时间可以提高性能，但可能会导致资源占用增加，特别是在高流量情况下。通常，60 秒是一个比较合理的默认值，但最佳设置取决于你的具体应用场景和服务器负载。

### 限制 keepalive 保持连接的最大时间 （1.19.10 新功能）

keepalive_time

限制 keepalive 保持连接的最大时间，即无论有没有交互，一个 TCP 连接最长不能超过该值，默认为 1h

### send_timeout

在 NGINX 中，`send_timeout` 指令用于设置服务器向客户端发送响应的超时时间。这个超时时间是指在两个连续的写操作之间，服务器等待客户端接收数据的最长时间。如果在指定的时间内客户端没有读取数据，连接将被关闭。

这个指令通常用于处理客户端在接收数据时可能出现的超时情况，比如客户端网络延迟高、客户端程序异常或客户端意外断开连接等。通过设置合理的 `send_timeout`，可以避免服务器资源被无效的连接长时间占用。

下面是一个配置示例：

```nginx
http {
    send_timeout 30s;   # 设置发送超时时间为 30 秒
    ...
}
```

在这个例子中，如果服务器在发送响应后等待了30秒，但客户端在这段时间内没有读取任何数据，那么服务器将关闭该连接。

`send_timeout` 的设置应根据你的服务器和客户端之间的网络条件以及应用的具体需求来调整。如果网络条件良好，可以设置较短的超时时间以快速释放资源；如果网络条件较差或客户端响应较慢，可能需要设置较长的超时时间。

请注意，`send_timeout` 只影响两个连续写操作之间的等待时间，并不直接控制整个请求-响应周期的超时。对于整个请求处理的超时，通常使用 `proxy_read_timeout`（对于反向代理场景）或 `client_body_timeout` 和 `client_header_timeout`（对于客户端请求体和头部的读取超时）等其他指令。

两次向客户端写操作之间的间隔 如果大于这个时间则关闭连接 默认60s

**此处有坑**，注意如果有耗时的同步操作有可能会丢弃用户连接，比如发送一个生成文件请求，服务器生成文件用了 61 秒，则 61 秒后才开始返回文件，此时连接已被断开，那么文件就无法返回了

该设置表示 Nginx 服务器与客户端连接后，某次会话中服务器等待客户端响应超过10s，就会自动关闭连接。

**注意**：该值不能比 keepalive_timeout 的值小，否则可能没到 keepalive_timeout 规定的超时时间，就因为 send_timeout 规定的值而断开连接了。

### keepalive_requests

单个连接中可并发处理的请求数,默认1000,一般不需要动，足够了

## 对上游服务器的常用配置（对反向代理服务器）

首先需要配置使用 http1.1 协议，以便建立更高效的传输。

在 Upstream 中所配置的上游服务器**默认**都是用短连接，即每次请求都会在完成之后断开

1. **启用 HTTP/1.1**：确保 NGINX 与上游服务器之间的通信使用 HTTP/1.1 协议。这通常在 `upstream` 块中指定。

2. **配置 `keepalive`**：在 `upstream` 块中，你可以使用 `keepalive` 指令来指定 NGINX 可以保持打开的上游服务器连接的最大数量。

3. **设置 `keepalive_timeout`**：此指令用于设置 NGINX 保持上游服务器连接打开的超时时间。

4. **设置 `keepalive_requests`**：此指令用于设置一个 TCP 连接可以处理的最大请求数量。

**示例配置**

以下是一个配置示例，展示了如何在 NGINX 中设置这些参数：

```nginx
http {
    upstream myapp1 {
        server backend1.example.com;
        server backend2.example.com;
        keepalive 32;  # NGINX 向上游服务器的保留连接数
    }

    server {
        listen 80;

        location / {
            proxy_pass http://myapp1;
            proxy_http_version 1.1;  # 确保使用 HTTP/1.1
            proxy_set_header Connection "";  # 前端请求来到 NGINX 后会被清除该属性，并加上 NGINX 自己的，默认 NGINX 加的为 close ，即不支持长连接，所以需要移除 Connection 头，允许持久连接
            keepalive_timeout 60s;  # 设置 NGINX 与上游服务器之间连接的超时时间
            keepalive_requests 1000;  # 设置一个 TCP 连接可以处理的最大并发请求数量
        }
    }
}
```

在这个配置中：

- `keepalive 32;` 表示 NGINX 将保持最多 32 个上游服务器的连接打开。
- `proxy_http_version 1.1;` 确保代理连接使用 HTTP/1.1 协议。
- `proxy_set_header Connection "";` 移除 `Connection` 头，允许持久连接。
- `keepalive_timeout 60s;` 设置连接保持打开的超时时间为 60 秒。
- `keepalive_requests 1000;` 设置一个 TCP 连接可以处理的最大并发请求数量为 1000。

# 反向代理工作流程（重点）

假设现在存在这样一个 NGINX 配置

```nginx
http {
    upstream myapp1 {
        server backend1.example.com;
        server backend2.example.com;
        keepalive 32;  # NGINX 可以保持打开的上游服务器连接的最大数量
    }

    server {
        listen 80;

        location / {
            proxy_pass http://myapp1;
            proxy_http_version 1.1;  # 确保使用 HTTP/1.1
            proxy_set_header Connection "";  # 移除 Connection 头，允许持久连接
            keepalive_timeout 60s;  # 设置 NGINX 与上游服务器之间连接的超时时间
            keepalive_requests 1000;  # 设置一个 TCP 连接可以处理的最大请求数量
        }
    }
}
```

在 nginx 服务器接收到一个客户端发送来的请求时，会先将传输来的二进制流信息解析成为 http 报文，然后通常会经历以下几个阶段

## 处理请求头

### client_header_buffer_size

`client_header_buffer_size` 是 NGINX 中用于设置用于存储客户端请求头的缓冲区大小的指令。这个缓冲区用于读取客户端发送的 HTTP 请求头。正确配置这个缓冲区的大小对于确保 NGINX 能够高效地处理各种大小的请求头非常重要。默认32位8K。 64位平台16K。

作用

1. **请求头缓冲区大小**：
    - `client_header_buffer_size` 指令定义了 NGINX 用于存储客户端请求头的缓冲区的初始大小。
    - 如果请求头较小，NGINX 将使用这个缓冲区来存储整个请求头。

2. **处理大型请求头**：
    - 如果请求头超过了 `client_header_buffer_size` 定义的大小，NGINX 会尝试使用更大的缓冲区来存储请求头。
    - NGINX 提供了 `large_client_header_buffers` 指令，允许定义一组更大的缓冲区，用于处理超出初始缓冲区大小的请求头。

配置示例

```nginx
http {
    ...
    client_header_buffer_size 1k;
    large_client_header_buffers 4 4k;
    ...
}
```

在这个示例中：
- `client_header_buffer_size` 设置为 1KB，这是用于存储请求头的初始缓冲区大小。
- `large_client_header_buffers` 设置为 4 个 4KB 的缓冲区，用于处理较大的请求头。

注意事项

- **缓冲区大小的调整**：如果网站的请求头通常很小，可以将 `client_header_buffer_size` 设置得较小以节省内存。如果网站经常接收大型请求头，可能需要增加这个值。
- **内存使用**：合理配置缓冲区大小可以避免不必要的内存浪费，同时确保 NGINX 能够处理大型请求头。
- **性能影响**：如果请求头过大，超出了 `client_header_buffer_size` 和 `large_client_header_buffers` 的容量，NGINX 将返回 414 Request-URI Too Large 错误。

通过合理配置 `client_header_buffer_size` 和 `large_client_header_buffers`，可以确保 NGINX 有效处理各种大小的客户端请求头，同时优化内存使用和性能。

### client_header_timeout 和 client_body_timeout

接收 client 发来的请求的超时时间，比如客户端发送一个 2KB 的文件传输到一半不动了，就认为超时，如果一直在动则不认为超时

## 处理请求体

在读取 http 的请求体数据时，由于客户端发送来的数据可能很大（比如文件），所以这里会将数据读取到一个缓冲区中，通过 `client_body_buffer_size` 参数来指定缓冲区的大小。

### client_body_buffer_size

在 NGINX 中，`client_body_buffer_size` 指令用于设置读取客户端请求体（即 POST 请求中的数据）时使用的缓冲区大小。这个缓冲区用于暂存客户端发送的数据，直到 NGINX 处理完毕。

**作用**

- **缓冲区大小调整**：通过调整 `client_body_buffer_size`，你可以控制 NGINX 在处理大请求体时的内存使用。如果请求体较大，而缓冲区设置得太小，NGINX 可能无法一次性读取整个请求体，导致需要多次读取操作，这会增加处理请求的延迟。

- **性能优化**：合理设置缓冲区大小可以优化 NGINX 的性能。如果缓冲区设置得过大，可能会导致不必要的内存使用；如果设置得太小，则可能导致频繁的磁盘 I/O 操作，因为 NGINX 可能需要将请求体写入临时文件。

**配置示例**

```nginx
http {
    client_body_buffer_size 16k;   # 设置缓冲区大小为 16KB
    ...
}
```

在这个例子中，缓冲区大小被设置为 16KB。你可以根据实际需要调整这个值，比如对于处理大型文件上传的服务器，可能需要将缓冲区设置得更大。

**注意事项**

- **默认值**：`client_body_buffer_size` 的默认值通常足够处理小到中等大小的请求体。对于大多数情况，不需要修改这个值。

- **临时文件**：如果请求体超过了缓冲区大小，NGINX 会将超出部分写入临时文件。因此，确保服务器的临时文件目录（由 `client_body_temp_path` 指令指定）有足够的空间和良好的性能。

- **监控和调整**：在生产环境中，监控 NGINX 的性能和日志，根据实际的请求体大小和服务器负载情况，适当调整 `client_body_buffer_size`。

在将数据读取到缓冲区以后，NGINX 实际上提供了两种处理方案，需要做出一个选择，即

- 数据完全读取完成后再发送给上游服务器
- 一边读取一边发送给上游服务器

这两种方案的选择通过 `proxy_requset_buffering` 参数来指定

### proxy_requset_buffering

`proxy_request_buffering` 是 NGINX 中的一个配置指令，它控制着 NGINX 在代理请求到后端服务器时是否缓冲请求体。这个参数在 NGINX 的 `http`, `server`, `location` 或者 `if` 上下文中都可以设置。

下面是 `proxy_request_buffering` 参数的两个可能值及其含义：

1. `proxy_request_buffering on;`（默认值）
   - 当设置为 `on` 时，NGINX 会缓冲整个请求体到内存中，或者如果请求体很大，可能会写入到磁盘上的临时文件中，然后再将请求转发给后端服务器。
   - 这种方式的好处是，即使后端服务器响应慢或者暂时不可用，NGINX 也可以保证请求被完整地接收和存储，从而确保数据的完整性。
   - 但是，这可能会消耗较多的内存或磁盘空间，特别是当处理大文件上传时。

2. `proxy_request_buffering off;`
   - 当设置为 `off` 时，NGINX 不会缓冲请求体，而是边接收边转发给后端服务器。
   - 这种方式可以减少内存和磁盘的使用，特别是对于大文件上传来说非常有用，因为它允许后端服务器直接开始处理数据，而无需等待整个请求体被完全接收。
   - 然而，如果后端服务器响应慢或暂时不可用，可能会导致 NGINX 的客户端连接超时，因为请求体没有被完全存储在 NGINX 端。

在配置 `proxy_request_buffering` 时，需要根据实际应用场景和需求来决定使用哪种方式。例如，如果后端服务器非常可靠，且网络连接稳定，可以考虑关闭请求缓冲以提高效率。相反，如果网络连接不稳定或后端服务器可能不可靠，开启缓冲可以提供更好的数据完整性和可靠性保障。

需要注意的是，**关闭请求缓冲可能会影响 NGINX 的错误处理和重试机制**，因为 NGINX 可能无法重新发送请求体给后端服务器。因此，在实际部署时，需要仔细测试和评估配置更改的影响。

这主要体现在以下几个方面：

1. **无法重试非幂等请求**：
   - 当请求缓冲关闭时，NGINX 无法重新发送请求体给后端服务器。如果后端服务器在处理请求时失败，并且请求不是幂等的（即多次执行会产生不同的结果），NGINX 将无法安全地重试该请求。幂等请求的例子包括 GET、PUT、DELETE 等，而非幂等请求的例子是 POST。
   - 这意味着，如果后端服务器响应超时或返回错误，且请求无法重试，客户端可能会收到错误响应，即使问题可能是暂时的。

2. **客户端连接超时问题**：
   - 当请求体很大时，关闭缓冲意味着 NGINX 会边接收客户端数据边转发给后端服务器。如果后端服务器处理速度跟不上客户端发送数据的速度，可能会导致客户端连接超时。
   - 这种情况下，NGINX 无法通过缓冲来缓解客户端和后端服务器之间的速度不匹配问题。

3. **后端服务器的负载管理**：
   - 关闭请求缓冲允许后端服务器直接处理流式数据，这在处理大文件上传等场景下非常有用。然而，这也意味着后端服务器必须能够即时处理数据流，否则可能会导致资源耗尽或性能问题。
   - 如果后端服务器暂时无法处理请求（例如，由于高负载或维护），客户端可能会收到错误响应，因为 NGINX 无法缓冲请求体以等待后端服务器恢复。

4. **日志记录和监控**：
   - 在请求缓冲关闭的情况下，NGINX 的日志记录可能不会包含完整的请求体信息，这可能会影响监控和调试过程。在某些情况下，请求体的详细信息对于诊断问题至关重要。

5. **安全性考虑**：
   - 虽然与错误处理和重试机制不直接相关，但关闭缓冲可能会减少 NGINX 缓冲区中数据的暴露时间，从而在一定程度上提高安全性。然而，这也要求后端服务器能够有效地处理实时数据流，否则可能会引入新的安全风险。

### client_max_body_size

`client_max_body_size` 是 NGINX 中用于限制客户端请求体（即 POST 请求的数据部分）最大允许大小的指令。这个指令对于防止恶意用户上传过大的文件或防止服务器因处理过大的请求体而耗尽资源非常关键。将 size 设置为0将禁用对客户端请求正文大小的检查。

作用

1. **限制请求体大小**：
    - 通过设置 `client_max_body_size`，你可以限制客户端可以发送到服务器的最大数据量。这对于防止服务器因处理过大的请求体而耗尽资源非常有用。
    - 如果客户端尝试发送超过此限制的数据，NGINX 将返回 413 Request Entity Too Large 错误。

2. **防止恶意上传**：
    - 在 Web 应用中，限制请求体大小可以防止恶意用户尝试上传大文件，从而对服务器造成不必要的负担或进行攻击。

3. **优化资源使用**：
    - 合理设置 `client_max_body_size` 可以帮助优化服务器资源的使用，确保服务器不会因处理过大的请求体而影响性能。

配置示例

```nginx
http {
     ...
     client_max_body_size 1m;
     ...
}
```

在这个示例中，`client_max_body_size` 被设置为 1MB。这意味着客户端发送的请求体最大不能超过 1MB。

注意事项

- **客户端错误处理**：当请求体超过 `client_max_body_size` 的限制时，NGINX 会返回 413 错误。在应用层面，你可能需要处理这个错误，并向用户提供适当的反馈。
- **测试和调整**：根据你的应用需求和服务器能力，可能需要调整这个值。例如，如果你的应用需要处理大文件上传，可能需要增加这个限制。
- **安全性**：限制请求体大小是防止某些类型的攻击（如资源耗尽攻击）的一种手段。确保这个值设置得既合理又安全。

通过合理配置 `client_max_body_size`，可以确保 NGINX 服务器在处理客户端请求时既安全又高效，同时避免因处理过大的请求体而影响服务器性能。

### client_body_temp_path

`client_body_temp_path` 是 NGINX 中用于指定存储客户端请求体的临时文件路径的指令。当客户端发送的请求体（例如，文件上传）太大而不能完全存储在内存中时，NGINX 会将请求体的部分或全部内容写入到临时文件中。`client_body_temp_path` 指令允许你定义这些临时文件的存储位置。

作用

1. **定义临时文件存储位置**：
    - 通过设置 `client_body_temp_path`，你可以指定一个或多个目录作为临时文件的存储位置。NGINX 会根据配置将临时文件分散到这些目录中。

2. **性能优化**：
    - 选择合适的目录对于优化磁盘 I/O 性能很重要。理想情况下，应该选择一个读写速度快的磁盘分区来存储临时文件。

3. **避免单点故障**：
    - 通过配置多个目录，可以分散 I/O 负载，避免单个磁盘分区成为瓶颈或发生故障时影响整个服务。

配置示例

```nginx
http {
     ...
     client_body_temp_path /var/nginx/client_body_temp 1 2;
     ...
}
```

在这个示例中，`client_body_temp_path` 指令定义了 `/var/nginx/client_body_temp` 作为临时文件的主目录，并且使用了两个附加级别（1 和 2）。这意味着 NGINX 会根据哈希值将临时文件分散存储在 `/var/nginx/client_body_temp` 目录下的不同子目录中。

注意事项

- **目录权限**：确保 NGINX 进程有权限写入 `client_body_temp_path` 指定的目录。
- **磁盘空间**：监控临时文件目录的磁盘空间使用情况，避免磁盘空间耗尽。
- **安全性**：虽然临时文件通常不会包含敏感信息，但出于安全考虑，最好将它们存放在受保护的目录中。

通过合理配置 `client_body_temp_path`，可以确保 NGINX 在处理大型请求体或高流量请求时，临时文件的存储既高效又安全。


## 向上游服务器发起请求

NGINX 采用了IO多路复用模型来高效地处理客户端请求和向上游服务器发起请求。这种模型允许 NGINX 在单个线程中同时处理成千上万个连接，而不会因为单个连接的延迟而阻塞其他连接的处理。

目前支持IO多路复用的系统调用，有 select，epoll 等等。select 系统调用，是目前几乎在所有的操作系统上都有支持，具有良好跨平台特性。epoll 是在linux 2.6 内核中提出的，是 select 系统调用的 linux 增强版本。
 
在这种模式中，首先不是进行 read/write 系统调动，而是进行 select/epoll 系统调用。当然，这里有一个前提，需要将目标网络连接，提前注册到 select/epoll 的可查询 socket 列表中。然后，才可以开启整个的IO多路复用模型的读/写流程。

### 选择服务器（upstream）

在向服务器发起请求前，首先会在配置的 upstream 块中选择一个服务器地址，默认是以链表的方式轮询选择。

### 向上游服务器发送数据

实际上，NGINX 在处理客户端发来的请求的过程中，具体来说就是在读取请求的请求体时，NGINX 就已经向操作系统中注册了一个 epoll 事件，只需要在操作系统提供的回调函数完成相应的处理即可，比如 TCP 链接建立完成、数据已可写等，所以在请求体读取的过程中就已经异步的建立了和上游服务器的链接，以保证在读取的过程中即可直接发送给上游服务器。这种处理方式使得 NGINX 可以在不阻塞当前线程的情况下，同时处理多个连接和 I/O 事件。这使得 NGINX 能够在读取客户端请求体的同时，高效地与上游服务器进行数据交换。

一些额外操作比如设置请求头 proxy_set_header 等参数也是在这个过程中处理的。

### 上游服务器返回数据

上游服务器返回数据的过程与发送类似，也是通过 epoll 事件来处理的，这就使得 NGINX 在处理请求的过程中一直处于伺机待发的状态，极大的提升了吞吐量。

在读取上游服务器返回的数据时，会涉及到一个数据是否需要缓冲的问题，这个问题主要解决的是上下游服务器速率不一致问题。

一般的，由于上游服务器时存在于内网中，网络传输速率通常非常快，而要将数据相应给用户，则需经过外网，速率就会下降很多。

假设现在服务器响应一个 20GB 大小的文件给客户端

- 如果禁用缓冲，那么 NGINX 将一直保持和上游服务器与客户端的链接，一边读，一边往客户端写，这样的问题就是，这两个链接将会一直保持占用资源，且当上游服务器响应慢或暂时不可用，客户端可能会遇到延迟或连接超时的问题。
- 如果启用缓冲，则 NGINX 将从后端服务器接收到的响应先存储在内存或磁盘上的缓冲区中，然后再发送给客户端。

### proxy_buffering

`proxy_buffering` 是 NGINX 中用于控制代理模块是否启用缓冲机制的指令。它主要用于处理 NGINX 与后端服务器之间的数据传输。具体来说，`proxy_buffering` 的作用包括：

1. **启用缓冲**：
    - 当 `proxy_buffering` 设置为 `on`（默认值），NGINX 会启用缓冲机制，将从后端服务器接收到的响应先存储在内存或磁盘上的缓冲区中，然后再发送给客户端。
    - 这种方式可以减少后端服务器的负载，因为后端服务器只需要发送一次数据给 NGINX，NGINX 再将数据分批发送给客户端。
    - 启用缓冲还可以提高客户端的响应速度，因为数据可以更快地从缓冲区中读取并发送给客户端，而不需要等待后端服务器发送完整个响应。

2. **禁用缓冲**：
    - 当 `proxy_buffering` 设置为 `off`，NGINX 不会启用缓冲机制，而是直接将从后端服务器接收到的数据流式传输给客户端，边接收边转发。
    - 这种方式适用于需要实时传输数据的场景，比如实时视频流或音频流，以及需要后端服务器直接与客户端交互的 WebSockets。
    - 禁用缓冲可以减少内存的使用，因为不需要为每个响应分配缓冲区。然而，这也意味着后端服务器的任何延迟都会直接传递给客户端。

**使用场景**

- **启用缓冲**：适用于大多数 Web 应用场景，特别是当后端服务器响应较慢或不稳定时，启用缓冲可以提高整体性能和用户体验。
- **禁用缓冲**：适用于需要实时数据交互的应用，如实时通信、流媒体服务等。

**注意事项**

- 当禁用缓冲时，如果后端服务器响应慢或暂时不可用，客户端可能会遇到延迟或连接超时的问题。
- 启用缓冲时，如果后端服务器发送大量数据，可能会消耗较多的内存资源，特别是在处理大文件或视频流时。

`proxy_buffering` 的配置需要根据实际应用场景和需求来决定，以平衡性能、资源使用和用户体验。在配置 NGINX 时，应仔细考虑这些因素，以确保系统运行在最佳状态。

### proxy_buffers

`proxy_buffers` 是 NGINX 中用于配置代理缓冲区的指令，它允许你指定用于存储从后端服务器接收到的响应的缓冲区的数量和大小。这个指令对于控制内存使用和响应处理性能非常关键，特别是在处理大型响应或高流量场景时。

1. **缓冲区数量和大小**：
    - `proxy_buffers` 指令允许你设置每个连接的缓冲区数量和每个缓冲区的大小。
    - 例如，`proxy_buffers 8 4k;` 表示为每个连接分配 8 个缓冲区，每个缓冲区大小为 4KB。

2. **优化内存使用**：
    - 通过合理配置缓冲区的数量和大小，可以优化内存使用，避免不必要的内存浪费或溢出。
    - 对于处理大型文件或视频流的服务器，可能需要增加缓冲区的大小或数量以存储更多数据。

### proxy_buffer_size

除了 `proxy_buffers`，还有一个相关的指令是 `proxy_buffer_size`，它用于设置 NGINX 用于存储响应头的缓冲区大小。

- `proxy_buffer_size` 通常设置为小于 `proxy_buffers` 中定义的缓冲区大小，因为响应头通常比响应体小。
- 例如，`proxy_buffer_size 8k;` 表示为响应头分配一个 8KB 的缓冲区。


示例配置

```nginx
http {
    ...
    proxy_buffer_size 8k;
    proxy_buffers 8 4k;
    proxy_busy_buffers_size 16k;
    ...
}
```

在这个示例中，`proxy_buffer_size` 设置为 8KB 用于存储响应头，`proxy_buffers` 设置为 8 个缓冲区，每个缓冲区大小为 4KB，用于存储响应体。`proxy_busy_buffers_size` 指令用于定义 NGINX 可以同时使用的缓冲区的最大大小，这里设置为 16KB。

### proxy_temp_file_write_size

`proxy_temp_file_write_size` 是 NGINX 中的一个指令，用于控制当响应体太大而不能完全存储在内存缓冲区时，写入临时文件的缓冲区大小。这个指令在处理大型文件或高流量时特别有用，因为它允许 NGINX 更有效地管理内存和磁盘 I/O。

1. **临时文件写入控制**：
   - 当从后端服务器接收到的数据量超过了 `proxy_buffers` 和 `proxy_buffer_size` 所定义的内存缓冲区的总容量时，NGINX 会将剩余的数据写入到磁盘上的临时文件中。
   - `proxy_temp_file_write_size` 指令定义了每次写入临时文件的数据量大小。

**配置示例**

```nginx
http {
    ...
    proxy_buffer_size 8k;
    proxy_buffers 8 4k;
    proxy_temp_file_write_size 128k;
    ...
}
```

在这个示例中，`proxy_temp_file_write_size` 设置为 128KB。这意味着每次 NGINX 从后端服务器接收到的数据超过 32KB（8个4KB缓冲区）时，它会将额外的数据以 128KB 的块写入临时文件。

**注意事项**

- 该指令的配置应与 `proxy_temp_path`（指定临时文件存储路径）和 `proxy_max_temp_file_size`（设置临时文件的最大大小）等其他相关指令一起考虑，以确保 NGINX 的代理功能既高效又稳定。

### proxy_temp_path

`proxy_temp_path` 是 NGINX 中用于指定存储临时文件的目录路径的指令。当 NGINX 作为代理服务器处理请求时，如果响应体太大而不能完全存储在内存缓冲区中，它会将部分响应体写入临时文件。`proxy_temp_path` 指令允许你定义这些临时文件的存储位置。

1. **定义临时文件存储位置**：
    - 通过设置 `proxy_temp_path`，你可以指定一个或多个目录作为临时文件的存储位置。NGINX 会根据配置将临时文件分散到这些目录中。

2. **性能优化**：
    - 选择合适的目录对于优化磁盘 I/O 性能很重要。理想情况下，应该选择一个读写速度快的磁盘分区来存储临时文件。

3. **避免单点故障**：
    - 通过配置多个目录，可以分散 I/O 负载，避免单个磁盘分区成为瓶颈或发生故障时影响整个服务。

**配置示例**

```nginx
http {
    ...
    proxy_temp_path /var/nginx/tmp 1 2;
    ...
}
```

在这个示例中，`proxy_temp_path` 指令定义了 `/var/nginx/tmp` 作为临时文件的主目录，并且使用了两个附加级别（1 和 2）。这意味着 NGINX 会根据哈希值将临时文件分散存储在 `/var/nginx/tmp` 目录下的不同子目录中。

**注意事项**

- **目录权限**：确保 NGINX 进程有权限写入 `proxy_temp_path` 指定的目录。
- **磁盘空间**：监控临时文件目录的磁盘空间使用情况，避免磁盘空间耗尽。
- **性能考虑**：选择读写速度快的磁盘分区，以避免成为性能瓶颈。
- **安全性**：虽然临时文件通常不会包含敏感信息，但出于安全考虑，最好将它们存放在受保护的目录中。

通过合理配置 `proxy_temp_path`，可以确保 NGINX 在处理大型响应或高流量请求时，临时文件的存储既高效又安全。

### proxy_max_temp_file_size

`proxy_max_temp_file_size` 是 NGINX 中用于限制单个请求中临时文件的最大大小的指令。当响应体太大，无法完全存储在内存缓冲区时，NGINX 会将部分响应体写入临时文件。`proxy_max_temp_file_size` 指令定义了这个临时文件的最大大小限制。

1. **限制临时文件大小**：
    - 该指令用于设置单个请求中临时文件的最大大小。如果响应体的大小超过了这个限制，NGINX 将不再继续写入临时文件，而是直接将数据发送给客户端（如果启用了 `proxy_buffering`）或者返回错误（如果没有启用 `proxy_buffering`）。

2. **防止磁盘空间耗尽**：
    - 通过限制临时文件的大小，可以防止因单个请求过大而导致磁盘空间被耗尽。

3. **性能和资源管理**：
    - 合理设置 `proxy_max_temp_file_size` 可以帮助管理服务器的磁盘 I/O 负载和资源使用。如果设置得太大，可能会导致磁盘 I/O 性能下降；如果设置得太小，则可能导致频繁的磁盘写入操作。

**配置示例**

```nginx
http {
     ...
     proxy_buffer_size 8k;
     proxy_buffers 8 4k;
     proxy_temp_file_write_size 128k;
     proxy_max_temp_file_size 1024k; # 设置临时文件的最大大小为 1024KB
     ...
}
```

在这个示例中，`proxy_max_temp_file_size` 被设置为 1024KB。这意味着如果响应体的大小超过了 32KB（8个4KB缓冲区）加上 128KB（每次写入临时文件的大小），并且总大小超过了 1024KB，NGINX 将不会继续写入临时文件。

**注意事项**

- `proxy_max_temp_file_size` 的值应根据服务器的磁盘空间和性能进行调整。如果服务器磁盘空间充足且性能良好，可以适当增加这个值。
- 该指令通常与 `proxy_temp_file_write_size` 一起使用，后者控制每次写入临时文件的大小。
- 如果 `proxy_buffering` 被设置为 `off`，则 `proxy_max_temp_file_size` 的设置将不会生效，因为响应将直接从后端服务器传输到客户端，不会使用临时文件。

通过合理配置 `proxy_max_temp_file_size`，可以确保 NGINX 在处理大型响应时既高效又稳定，同时避免对服务器性能和资源造成不必要的影响。

# 反向代理中的容错机制

## proxy_timeout

## 重试机制 proxy_next_upstream

proxy_next_upstream

当后端服务器返回指定的错误时，将请求传递到其他服务器。

error与服务器建立连接，向其传递请求或读取响应头时发生错误;

timeout在与服务器建立连接，向其传递请求或读取响应头时发生超时;

invalid_header服务器返回空的或无效的响应;

http_500服务器返回代码为500的响应;

http_502服务器返回代码为502的响应;

http_503服务器返回代码为503的响应;

http_504服务器返回代码504的响应;

http_403服务器返回代码为403的响应;

http_404服务器返回代码为404的响应;

http_429服务器返回代码为429的响应;

不了解这个机制，在日常开发web服务的时候，就可能会踩坑。

比如有这么一个场景：一个用于导入数据的web页面，上传一个excel，通过读取、处理excel，向数据库中插入数据，处理时间较长（如1分钟），且为同步操作（即处理完成后才返回结果）。暂且不论这种方式的好坏，若nginx配置的响应等待时间（proxy_read_timeout）为30秒，就会触发超时重试，将请求又打到另一台。如果处理中没有考虑到重复数据的场景，就会发生数据多次重复插入！（当然，这种场景，内网可以通过机器名访问该服务器进行操作，就可以绕过nginx了，不过外网就没办法了。）

# 获取客户端真实 IP

在使用 NGINX 作为网关服务器之后，后端应用，比如 java 如果想要通过代码来获取客户的真实 IP 地址就无法获取到了

## X-Real-IP

需要额外模块，不推荐使用

## 使用 setHeader 携带真实 IP

```nginx
proxy_set_header X-Forwarded-For $remote_addr;
```

然后上游服务器就可以通过读取 header 的 X-Forwarded-For 属性的值来获取到客户机的真实 IP 地址，如果有多层 Nginx 那么可以通过追加方式添加 IP 地址，比如 `X-Forwarded-For:IP1,IP2` ，则最前面的 IP1 即客户机的 IP 地址。

# 高效传输

## 零拷贝技术 sendfile

在 Nginx 中，`sendfile` 是一个用于高效文件传输的指令，它允许操作系统直接在内核空间之间传输数据，绕过用户空间，从而减少 CPU 的使用并提高文件传输的效率。`sendfile` 指令通常用于优化静态文件的传输，比如图片、CSS、JavaScript 文件等。

**配置指令**

`sendfile` 指令可以设置为以下值：

1. **on**
   - 开启 `sendfile` 功能。当设置为 `on` 时，Nginx 将使用内核级别的 `sendfile()` 系统调用来传输文件。

2. **off**
   - 关闭 `sendfile` 功能。当设置为 `off` 时，Nginx 将使用传统的用户空间 I/O 来传输文件。

**示例配置**

下面是一个简单的 Nginx 配置示例，展示了如何在 `http`、`server` 或 `location` 块中使用 `sendfile` 指令：

```nginx
http {
    # 全局范围内开启 sendfile
    sendfile on;

    # 其他全局配置...
}

server {
    listen 80;
    server_name example.com;

    # 服务器范围内开启 sendfile
    sendfile on;

    # 配置静态文件路径
    root /var/www/html;

    # 配置 location 块
    location / {
        try_files $uri $uri/ =404;
    }
}
```

在这个配置中，`sendfile on;` 被放置在 `http` 和 `server` 块中，意味着在全局和该服务器配置中都启用了 `sendfile` 功能。这将对所有静态文件传输使用 `sendfile` 优化。

**注意事项**

- `sendfile` 功能在大多数现代操作系统上可用，但其效果可能因系统而异。
- 在某些情况下，如果网络或磁盘 I/O 成为瓶颈，开启 `sendfile` 可能不会带来显著的性能提升。
- 在使用 `sendfile` 时，还应考虑其他相关指令，如 `tcp_nopush` 和 `tcp_nodelay`，它们可以进一步优化网络传输性能。

在修改 Nginx 配置后，记得使用 `nginx -t` 检查配置文件的正确性，并使用 `nginx -s reload` 重新加载配置以使更改生效。

## 压缩传输

在 HTTP（超文本传输协议）中，压缩传输是一种优化技术，用于减少通过网络传输的数据量，从而加快数据传输速度并节省带宽。压缩传输主要通过压缩服务器响应的数据来实现，使得客户端（通常是网页浏览器）接收到的数据量更小，然后在客户端进行解压缩。以下是关于 HTTP 压缩传输的几个关键点：

**常用的压缩技术**

1. **Gzip**：这是最常用的压缩方法之一，它基于 DEFLATE 压缩算法。Gzip 压缩特别适合文本文件，如 HTML、CSS、JavaScript 文件等，可以显著减少文件大小。

2. **Deflate**：这是一种结合了 LZ77 算法和 Huffman 编码的压缩方法。它也常用于 HTTP 压缩，但不如 Gzip 普遍。

3. **Brotli**：这是一种相对较新的压缩算法，旨在提供比 Gzip 更高的压缩率和更快的解压缩速度。它逐渐在现代浏览器和服务器中得到支持。

**HTTP 压缩的工作流程**

1. **客户端请求**：当用户访问网站时，浏览器会向服务器发送 HTTP 请求。请求中通常包含一个 `Accept-Encoding` 头部，告知服务器客户端支持的压缩格式，例如 `Accept-Encoding: gzip, deflate, br`（`br` 表示 Brotli）。

2. **服务器响应**：如果服务器支持请求中提到的压缩方法，并且确定要发送的内容适合压缩，它会将响应的内容进行压缩，并在响应头中通过 `Content-Encoding` 字段告知客户端使用了哪种压缩方法，例如 `Content-Encoding: gzip`。

3. **数据传输**：压缩后的数据被传输到客户端。

4. **客户端解压缩**：浏览器接收到压缩的数据后，根据 `Content-Encoding` 头部的指示，使用相应的解压缩算法对数据进行解压缩，然后继续处理和展示给用户。

**压缩传输的优势**

- **减少传输时间**：通过减少传输的数据量，可以显著减少网页加载时间，特别是在网络条件不佳的情况下。
- **节省带宽**：对于网站运营商来说，减少传输的数据量可以降低带宽使用，节省成本。
- **提升用户体验**：更快的页面加载速度直接提升了用户的浏览体验。

**注意事项**

- 压缩传输对文本文件效果最好，对于已经压缩过的文件（如某些图片格式）或压缩比很小的文件，使用压缩可能不会带来太大收益，有时甚至会增加处理时间。
- 服务器和客户端都需要支持相应的压缩方法才能实现压缩传输。大多数现代浏览器和服务器软件都支持 Gzip 和 Brotli。

在配置服务器时，通常需要确保压缩功能被启用，并且正确配置了哪些文件类型需要被压缩。服务器管理员可以通过服务器配置文件（如 Apache 的 `.htaccess` 文件或 Nginx 的配置文件）来控制这些设置。

总的来说，HTTP 压缩是一种重要的性能优化手段，它通过减少传输数据量来提升网页加载速度和用户体验。随着技术的发展，新的压缩算法如 Brotli 正在逐渐取代传统的 Gzip，以提供更好的压缩效率。

## Gzip

Gzip 是一种广泛使用的数据压缩格式，它基于 DEFLATE 压缩算法。

### Gzip 在 Nginx 中的常用压缩指令配置

1. **gzip on;**
   - 开启 Gzip 压缩功能。默认情况下，Gzip 是关闭的。

2. **gzip_buffers 32 4k|16 8k;**
   - 设置用于压缩的缓冲区数量和大小。例如，`32 4k` 表示使用32个大小为4KB的缓冲区，或者`16 8k` 表示使用16个大小为8KB的缓冲区。这有助于服务器更高效地处理压缩任务。通常建议 64 位操作系统建议 16 8k ，32 位操作系统建议使用 32 4k。

3. **gzip_comp_level 1;**
   - 设置压缩等级，范围是1到9，数字越大表示压缩比越高，但同时也会消耗更多的CPU资源。

4. **gzip_http_version 1.1;**
   - 指定使用 Gzip 压缩的最低 HTTP 版本。这里设置为 HTTP/1.1，意味着只有当客户端支持 HTTP/1.1 时，才会启用 Gzip 压缩。

5. **gzip_min_length 256;**
   - 设置只有当响应体长度大于或等于256字节时才进行压缩。对于较小的文件，压缩可能不会带来明显的带宽节省，有时甚至会增加文件大小。

6. **gzip_proxied** (多选)
   - 控制作为反向代理时，针对上游服务器返回的头信息进行压缩。选项包括：
     - `off`：不做限制。
     - `expired`：如果header头中包含 "Expires" 头信息则启用压缩。
     - `no-cache`：如果header头中包含 "Cache-Control:no-cache" 头信息则启用压缩。
     - `no-store`：如果header头中包含 "Cache-Control:no-store" 头信息则启用压缩。
     - `private`：如果header头中包含 "Cache-Control:private" 头信息则启用压缩。
     - `no_last_modified`：如果header头中不包含 "Last-Modified" 头信息则启用压缩。
     - `no_etag`：如果header头中不包含 "ETag" 头信息则启用压缩。
     - `auth`：如果header头中包含 "Authorization" 头信息则启用压缩。
     - `any`：无条件启用压缩。

7. **gzip_vary on;**
   - 增加一个 `Vary: Accept-Encoding` 头部，这有助于缓存服务器正确处理压缩内容，确保兼容性。

8. **gzip_types**
   - 指定哪些 MIME 类型的内容需要被压缩。例如，可以指定压缩文本、JavaScript、CSS、HTML 等文件类型。

9. **gzip_disable**
   - 禁止某些浏览器使用 Gzip 压缩。可以使用正则表达式来匹配特定的浏览器用户代理字符串。

这些配置项允许服务器管理员根据实际需求和服务器性能，精细地调整 Gzip 压缩行为，以达到最佳的性能和资源使用平衡。正确配置 Gzip 压缩可以显著减少传输的数据量，加快网页加载速度，从而提升用户体验。

### 示例

```
  gzip on;
  gzip_buffers 16 8k;
  gzip_comp_level 6;
  gzip_http_version 1.1;
  gzip_min_length 256;
  gzip_proxied any;
  gzip_vary on;
  gzip_types text/plain application/x-javascript text/css application/xml;
  gzip_types
    text/xml application/xml application/atom+xml application/rss+xml application/xhtml+xml image/svg+xml
    text/javascript application/javascript application/x-javascript
    text/x-json application/json application/x-web-app-manifest+json
    text/css text/plain text/x-component
    font/opentype application/x-font-ttf application/vnd.ms-fontobject
    image/x-icon;
  gzip_disable "MSIE [1-6]\.(?!.*SV1)";
```

一个 Nginx 服务器的 Gzip 压缩配置示例，下面是对每个指令的详细解释：

1. **gzip on;**
   - 开启 Gzip 压缩功能。

2. **gzip_buffers 16 8k;**
   - 设置用于压缩的缓冲区数量和大小。这里配置了16个缓冲区，每个缓冲区大小为8KB。这有助于服务器更高效地处理压缩任务。

3. **gzip_comp_level 6;**
   - 设置压缩级别为6，范围是1到9，6是中等压缩级别，平衡了压缩时间和压缩后的大小。

4. **gzip_http_version 1.1;**
   - 指定使用 Gzip 压缩的最低 HTTP 版本为 HTTP/1.1。

5. **gzip_min_length 256;**
   - 设置只有当响应体长度大于或等于256字节时才进行压缩。对于较小的文件，压缩可能不会带来明显的带宽节省，有时甚至会增加文件大小。

6. **gzip_proxied any;**
   - 作为反向代理时，此设置表示无论上游服务器返回的头信息如何，都启用压缩。

7. **gzip_vary on;**
   - 在响应头中添加 `Vary: Accept-Encoding`，告知客户端服务器支持内容编码，有助于缓存代理正确处理压缩内容。

8. **gzip_types**
   - 指定哪些 MIME 类型的内容需要被压缩。这里列举了多种文本和脚本类型，包括 HTML、CSS、JavaScript、JSON、SVG 等。

9. **gzip_disable "MSIE [1-6]\.(?!.*SV1)";**
   - 禁止对特定浏览器（如旧版的 Internet Explorer 1 到 6）使用 Gzip 压缩，因为这些旧版浏览器可能不支持或处理压缩内容有问题。

这个配置示例展示了如何在 Nginx 中设置 Gzip 压缩，以优化传输数据的大小，加快网页加载速度，同时确保与不同客户端的兼容性。通过合理配置，可以平衡压缩效率和服务器性能，提升用户体验。在实际部署时，需要根据服务器的具体情况和需求调整这些参数。

完整的配置文件类似于这样

```nginx
http {
    # 开启 Gzip 压缩功能
    gzip on;

    # 设置用于压缩的缓冲区数量和大小
    gzip_buffers 16 8k;

    # 设置压缩级别为6
    gzip_comp_level 6;

    # 指定使用 Gzip 压缩的最低 HTTP 版本为 HTTP/1.1
    gzip_http_version 1.1;

    # 设置只有当响应体长度大于或等于256字节时才进行压缩
    gzip_min_length 256;

    # 作为反向代理时，无论上游服务器返回的头信息如何，都启用压缩
    gzip_proxied any;

    # 在响应头中添加 Vary: Accept-Encoding
    gzip_vary on;

    # 指定哪些 MIME 类型的内容需要被压缩
    gzip_types text/plain
                text/xml
                application/xml
                application/atom+xml
                application/rss+xml
                application/xhtml+xml
                image/svg+xml
                text/javascript
                application/javascript
                application/x-javascript
                text/x-json
                application/json
                application/x-web-app-manifest+json
                text/css
                text/x-component
                font/opentype
                application/x-font-ttf
                application/vnd.ms-fontobject
                image/x-icon;

    # 禁止对特定浏览器（如旧版的 Internet Explorer 1 到 6）使用 Gzip 压缩
    gzip_disable "MSIE [1-6]\.(?!.*SV1)";

    # 其他服务器配置...
    server {
        listen 80;
        server_name example.com;

        # 配置静态文件路径
        root /var/www/html;

        # 配置 location 块
        location / {
            try_files $uri $uri/ =404;
        }
    }
}
```

**一般来说，只需要配置文本类的文件压缩就可以了，图片与其他资源的压缩率极低，不如不压缩**。

### 静态压缩

当在 Nginx 中配置开启了 gzip 压缩传输以后，由于需要在内存中进行压缩，所以就无法使用零拷贝技术传输文件了，这带来了很大的性能损失，所以 Nginx 提供了一个解决方案，即将提前压缩好的文件与原文件一起存储在服务器上，Nginx 通过判断客户端是否支持压缩来自动选择发送原文件还是压缩以后的文件，因为压缩文件与原文件都是提前处理好的，就又可以使用零拷贝技术了。这种压缩处理方式称为静态压缩，而上文的压缩方式为动态压缩。

#### http_gzip_static_module 

要启用 Nginx 的静态压缩功能需要重新编译 Nginx，将 http_gzip_static_module 模块一起编译进 Nginx。

这个模块允许 Nginx 直接提供预压缩的文件（通常以 `.gz` 结尾），而不是在每次请求时动态压缩。这意味着，如果你有一个静态文件的压缩版本，Nginx 可以直接提供这个压缩文件，从而减少服务器的 CPU 负载并加快响应速度。两个示例文件名为 index.html 与 index.html.gz。

要启用 `http_gzip_static_module`，你需要在编译 Nginx 时添加特定的配置选项。这通常在编译 Nginx 之前通过 `./configure` 脚本完成。例如：

```bash
./configure --with-http_gzip_static_module ＃ 这里需要将之前编译的参数也加上
```

这行命令会告诉编译系统在构建 Nginx 时包含 `http_gzip_static_module` 模块。完成配置后，你需要继续编译和安装 Nginx。

以下是该模块支持的一些配置指令：

- **语法**: `gzip_static on | off ｜ always;`
- **默认**: `gzip_static off;`
- **上下文**: `http`, `server`, `location`

此指令用于开启或关闭预压缩文件的自动支持。
- 当设置为 `on` 时，Nginx 会尝试直接提供 `.gz` 文件，如果请求的文件有对应的 `.gz` 压缩版本，Nginx 将直接发送该文件而不是动态压缩。
- 如果设置为 `off`，则不会使用预压缩文件，而是根据其他 Gzip 相关指令动态压缩内容。
- 如果设置为 `always` 强制 Nginx 总是提供预压缩的文件，即使客户端请求的是未压缩的文件。如果对应的 .gz 文件不存在，Nginx 将返回 404 错误。这个选项比较特殊，它要求你必须确保所有请求的文件都有对应的 .gz 版本，否则会导致 404 错误。通常情况下，`gzip_static on;` 已经足够使用。

**示例配置**

```nginx
http {
    # 开启 http_gzip_static_module
    gzip_static on;

    # 其他配置...
}
```

**注意事项**

- 在使用 `gzip_static` 之前，你需要确保已经手动创建了需要的 `.gz` 文件，并将它们放置在合适的位置。
- 该模块通常用于静态内容的优化，对于动态生成的内容，可能需要使用 `ngx_http_gzip_module` 进行动态压缩。
- 该模块的使用可以与动态压缩模块（`ngx_http_gzip_module`）并存，根据内容类型和配置选择最合适的压缩方式。

#### ngx_http_gunzip_module 

在某些情况下，同时存储原文件与压缩后的文件会占用大量服务器空间，此时就可以在服务器上只存储压缩文件，将原文件删掉，在目标客户端请求原文件或不支持压缩文件时，动态的将压缩文件解压后传输给客户端，这个模块由 ngx_http_gunzip_module 负责实现

`ngx_http_gunzip_module` 是 Nginx 的一个第三方模块，它允许 Nginx 在服务器上只存储压缩文件，并在客户端请求时动态解压缩文件。这个模块特别适用于以下场景：

- **节省存储空间**：当服务器存储空间有限时，只保留压缩文件可以显著减少所需的磁盘空间。
- **动态解压缩**：如果客户端不支持 Gzip 压缩或请求未压缩的文件，`ngx_http_gunzip_module` 可以动态地将压缩文件解压并传输给客户端。

使用场景

1. **存储空间有限**：对于存储空间受限的服务器，只保留压缩文件可以节省大量空间。
2. **客户端兼容性**：对于不支持 Gzip 压缩的旧版浏览器或设备，此模块可以确保它们仍能接收未压缩的文件内容。

`ngx_http_gunzip_module` 并不是 Nginx 官方提供的标准模块，而是第三方模块。因此，它不能通过标准的 `./configure` 脚本来启用。要使用 `ngx_http_gunzip_module`，你需要从第三方源获取该模块的代码，并按照其提供的安装说明进行编译和安装。

通常，安装第三方 Nginx 模块的步骤可能包括：

1. 下载第三方模块的源代码。
2. 解压源代码（如果需要）。
3. 根据模块提供的安装说明进行编译和安装。

例如，安装步骤可能类似于：

```bash
# 下载第三方模块源代码
wget [模块源代码的URL]

# 解压源代码（如果需要）
tar -zxvf [模块源代码文件]

# 进入模块源代码目录
cd [模块源代码目录]

# 根据模块提供的安装说明进行编译和安装
./configure --add-module=[模块源代码目录路径]
make
make install
```

配置示例

要使用 `ngx_http_gunzip_module`，首先需要确保你已经安装了这个模块。安装后，你可以在 Nginx 配置文件中启用它，通常通过添加如下指令：

```nginx
http {
    # 其他配置...

    # 启用 gunzip 模块
    gunzip on;

    # 其他配置...
}
```

注意事项

- `ngx_http_gunzip_module` 不是 Nginx 标准发行版的一部分，可能需要从第三方源获取并安装。
- 使用此模块时，确保服务器有足够的 CPU 资源来处理动态解压缩的任务，因为这可能会增加服务器的负载。
- 由于动态解压缩可能会影响响应时间，建议仅在确实需要时使用此模块。
- **在启用 ngx_http_gunzip_module 模块时 gzip_static 的配置必须是 always，否则会去找未压缩的文件版本，就会404**，即强制发送压缩包，客户端又不支持的情况下才生效

## Brotli

Brotli 是一种由谷歌开发的开源数据压缩算法，旨在提供高压缩比和较快的压缩速度，同时保持合理的解压速度。Brotli 的设计目标是替代现有的压缩算法，如 Gzip，以提供更好的性能和压缩效率。**注意：只有在 HTTPS 协议下才可以使用 Brotli 压缩算法**。因为浏览器必须发送携带 `Accept-Encoding: br` 头的请求才会使用 Brotli 算法，而这个请求头只有在 HTTPS 协议下浏览器才会加。

### Brotli 的特点：

1. **高压缩比**：Brotli 使用了复杂的字典和先进的压缩技术，能够提供比 Gzip 更高的压缩比，尤其在压缩文本数据（如 HTML、CSS、JavaScript 文件）时效果显著。

2. **关键字字典**：Brotli 使用了一个预定义的大型关键字字典，这个字典包含了大量常见的文本片段和模式。在压缩过程中，算法会查找并替换这些模式，以达到更高的压缩效率。

3. **支持多种内容编码**：Brotli 可以作为 HTTP 响应头中的 `Content-Encoding` 选项使用，这意味着它可以在 HTTPS 协议下工作，为网站提供安全的压缩传输。

4. **兼容性**：虽然 Brotli 是较新的压缩算法，但现代浏览器和一些服务器软件（如 Nginx 和 Apache）已经开始支持它。为了充分利用 Brotli 的优势，需要确保客户端和服务器端都支持 Brotli 压缩。

## 合并客户端请求

在向服务器请求网页页面时，有时候网页页面中会内联非常多的 css，js 等文件，浏览器在解析到这些文件时，会依次向服务器发送请求来获取这些文件，这样就会增加服务器压力。

淘宝网提供了一可以将多个客户端请求合并为一个请求的 Nginx 插件，原理是在客户端发起请求时不再使用原来的方式发送请求，而是在发送请求时携带一定的参数，如 `http://test.com/??1.css,2.css` 来将多个请求合并为一个，而在 Nginx 端收到这种请求时，将请求解析为多个文件，动态在内存中合并成为一个文件响应给客户端，这样就减少了服务器的并发请求压力，但这样也带来一个问题，即 Nginx 的零拷贝技术 sendfile 失效，这也是没有办法的，为了减少服务器压力而牺牲掉部分性能的处理方式。

### nginx-http-concat

Nginx官方介绍

https://www.nginx.com/resources/wiki/modules/concat/

git地址

https://github.com/alibaba/nginx-http-concat

这里是对 Nginx 的 `ngx_http_concat` 模块的介绍，以及如何安装和配置该模块的步骤。

**Nginx 官方介绍**

`ngx_http_concat` 模块是 Tengine 分发的一部分，Tengine 是由淘宝网开发的 Nginx 分发版，它包含了一些在标准 Nginx 中尚未出现的新模块。`ngx_http_concat` 模块允许将多个客户端请求合并为一个请求，从而减少服务器压力并提高性能。

**模块功能**

- **合并请求**：允许将多个 CSS 或 JavaScript 文件合并为一个请求。
- **版本控制**：支持通过版本字符串来控制文件缓存。
- **配置灵活性**：可以定义哪些 MIME 类型的文件可以被合并，以及每个请求最多可以合并多少文件。

**安装步骤**

1. **下载源码**：
   ```bash
   git clone git://github.com/alibaba/nginx-http-concat.git
   ```

2. **编译安装**：
   在编译 Nginx 时，需要添加模块路径：
   ```bash
   ./configure --add-module=/path/to/nginx-http-concat
   make
   make install
   ```

**配置示例**

在 Nginx 配置文件中，可以启用 `concat` 功能并设置相关参数，例如：

```nginx
location /static/css/ {
    concat on;
    concat_max_files 30;
}
```

在这个配置中：

- `concat on;` 启用合并请求功能。
- `concat_max_files 30;` 设置每个请求最多可以合并的文件数量为30。

**注意事项**

- 确保在配置文件中正确设置了 `concat` 相关的指令。
- 合并文件时，需要考虑文件大小和服务器的页面大小限制，可能需要调整 `large_client_header_buffers` 指令。
- 合并文件后，建议对文件进行压缩处理，以进一步减少传输的数据量。

**结论**

通过使用 `ngx_http_concat` 模块，可以有效地减少客户端与服务器之间的请求数量，从而减轻服务器压力并提升页面加载速度。该模块特别适用于需要处理大量静态资源的网站，如电子商务和拍卖网站。安装和配置过程相对简单，但需要确保服务器配置能够支持合并后的文件大小。

# Linux 服务器文件同步

见 《 Linux 服务器文件同步.md 》

# 多级缓存

多级缓存是为了将流量逐级分流，让最终到达服务器的流量在可控范围内，解决高并发问题的最好办法就是没有高并发，因此在缓存的设计中，越贴近用户的缓存效率越高，效果也越好。

## 静态资源缓存

### 资源静态化

在一个高并发系统中，比如电商平台，并发量最高的是商品详情页，因为一个人可能只访问一次首页，但是会多次访问详情页，且这种并发量是极高的，那么，设计和构建这类系统时，如何支持如此大的并发支持，并不是一个简单的问题。

一般的，一个请求的实际流程大概包括以下步骤：用户发送请求到 Nginx ，Nginx 将请求转发至上游服务器，上游服务器向 DB 请求动态数据，经过一系列计算，使用模板引擎（比如jsp）渲染页面，返回到 Nginx，再返回至用户。

如果每次请求都需要经过这样多的流程，那资源消耗是极大的，所以可以将后端经过计算和渲染的页面直接生成一个文件，存储在 Nginx 上，当下次用户请求时，直接从 Nginx 通过 sendfile 零拷贝返回，效率提升是巨大的。

这种处理方案即资源静态化，当然，这样处理的方案也会带来更多的问题，比如 Nginx 集群环境下如何同步每台机器的静态资源状态一致性，以及某些动态数据（如商品价格）如何实时更新等。

对于同步每台机器的静态资源，可以采用 Linux 的 rsync 来实现，动态数据动态加载可以通过前端 js 来动态加载实现，或通过 Nginx 的 openresty 模板引擎前置来落地实现。

#### SSI 服务端文件合并

Nginx 可以通过 ngx_http_ssi_module 模块来实现在一个文件中引入另一个文件的功能，比较常用的就是在一个静态的 html 页面中引入一个公共的 top 页面或者 bottom 页面，将它们按顺序拼接为一个文件返回给用户。

官方文档 http://nginx.org/en/docs/http/ngx_http_ssi_module.html

以下是关于 `ngx_http_ssi_module` 的说明：

Nginx SSI 模块概述

- **模块名称**: ngx_http_ssi_module
- **功能**: 作为过滤器处理通过它的响应中的 SSI (Server Side Includes) 命令。
- **支持状态**: 支持的 SSI 命令列表不完整。

Nginx 配置文件配置指令

- **ssi**: 开启或关闭响应中 SSI 命令的处理。
  - 语法: `ssi on | off;`
  - 默认: `off`
  - 上下文: http, server, location, if in location

- **ssi_last_modified**: 在 SSI 处理期间保留原始响应的“Last-Modified”头信息，以促进响应缓存。每一个在磁盘上的文件都会维护一个最后修改时间，这里的 “Last-Modified” 信息即代表文件的最后修改时间，当开启该选项时，将以磁盘上主文件的最后修改时间为准，而不是 Nginx 合并文件时间，在客户端使用缓存的时候，会判断文件是否被修改过，如果一直以合并时间为准，会导致客户端缓存失效。
  - 语法: `ssi_last_modified on | off;`
  - 默认: `off`
  - 上下文: http, server, location

- **ssi_min_file_chunk**: 设置响应存储在磁盘上的最小部分大小，从这个大小开始使用 sendfile 发送。在 Nginx 执行文件合并时，会检查合并出来的文件大小，当文件大小达到此配置的大小时，会将文件写入磁盘，写入磁盘以后的文件才可以使用 sendfile 发送。
  - 语法: `ssi_min_file_chunk size;`
  - 默认: `1k`
  - 上下文: http, server, location

- **ssi_silent_errors**: 如果在 SSI 处理期间发生错误，是否抑制输出错误信息。包括脚本命令错误，或引入文件不存在等错误。
  - 语法: `ssi_silent_errors on | off;`
  - 默认: `off`
  - 上下文: http, server, location

- **ssi_types**: 指定除了“text/html”之外，哪些 MIME 类型的响应中可以处理 SSI 命令。
  - 语法: `ssi_types mime-type ...;`
  - 默认: `text/html`
  - 上下文: http, server, location

- **ssi_value_length**: 设置 SSI 命令中参数值的最大长度。
  - 语法: `ssi_value_length length;`
  - 默认: `256`
  - 上下文: http, server, location

##### SSI 命令 

SSI (Server-Side Includes) 命令遵循特定的语法格式，用于在 Nginx 中嵌入动态内容或执行条件逻辑。下面是基于提供的文档内容，对 SSI 命令的总结：

SSI 命令通用格式
```html
<!--# command parameter1=value1 parameter2=value2 ... -->
```

支持的 SSI 命令

1. **block**
   - 定义一个可被 `include` 命令使用的代码块。
   - 示例：
     ```html
     <!--# block name="one" -->
     <!--# endblock -->
     ```

2. **config**
   - 设置 SSI 处理期间的参数，如错误消息和时间格式。
   - 示例：
     ```html
     <!--# config errmsg="Error occurred" timefmt="%d-%b-%Y %H:%M:%S" -->
     ```

3. **echo**
   - 输出一个变量的值。
   - 示例：
     ```html
     <!--# echo var="name" -->
     ```

4. **if**
   - 执行条件包含。
   - 示例：
     ```html
     <!--# if expr="$name = value" -->
     <!--# endif -->
     ```

5. **include**
   - 将另一个文件或请求的结果包含到当前页面中。
   - 示例：
     ```html
     <!--# include file="footer.html" -->
     ```

6. **set**
   - 设置一个变量的值。
   - 示例：
     ```html
     <!--# set var="variable_name" value="value" -->
     ```

参数说明
- **command**: 必须的，指定要执行的 SSI 命令。
- **parameter**: 可选的，根据不同的命令有不同的参数，用于传递额外的信息或设置。

注意事项
- SSI 命令通常用于 HTML 文件中，但它们必须以 `<!--#` 开始，并以 `-->` 结束。

**内嵌变量**

- **$date_local**: 本地时区的当前时间。
- **$date_gmt**: GMT 的当前时间。

## 浏览器缓存

在实际的项目中，浏览器缓存的作用非常明显，以淘宝网为例，在双十一等大型活动进行之前，就已经将一部分数据缓存到了用户的浏览器中，在真正开始活动的时候，很多用户根本就没有机会将请求发送至服务器。

在用户进入商品的抢购页面时，通常抢购按钮会有多种状态，比如未开始状态，那么在商品真正开始抢购之后，一般需要通过刷新页面的方式刷新按钮，在此时就可以发送数据到服务器端，服务器实时计算当前某个商品的实际想要抢购的人数，由此来计算出热门商品列表并下发给客户端，客户端在收到热门商品列表后，如果用户抢购的热门商品已经达到比如 1:10000 或更大的比例时，实际上就没有必要让所有用户都发送请求给服务器，而是直接将按钮变为抢购失败，由此来限制用户请求流量。

在浏览器缓存的上下文中，对于缓存的读取方式，通常可以分为内存缓存（Memory Cache）和磁盘缓存（Disk Cache），

**内存缓存（Memory Cache）**

- **来源**: 当你访问一个网页时，浏览器会将一些资源（如图片、脚本等）加载到内存中，以便快速访问。
- **特点**:
  - 资源通常存储在RAM中，因此访问速度非常快。
  - 这些资源是临时存储的，当浏览器关闭或者标签页关闭时，这些缓存通常会被清除。
  - 内存缓存通常用于存储最近访问过的资源，以提高页面加载速度。

**磁盘缓存（Disk Cache）**

- **来源**: 浏览器也会将一些资源存储在用户的硬盘上，以便在需要时可以快速加载，而无需重新从网络下载。
- **特点**:
  - 资源存储在硬盘上，因此访问速度比内存缓存慢，但比重新从网络下载要快得多。
  - 这些缓存资源不会因为浏览器或标签页关闭而消失，它们会保留在硬盘上直到被显式清除或过期。
  - 磁盘缓存通常用于存储较大的资源文件，或者那些不经常变化的资源。

**缓存标识**

在通过浏览器开发者工具可以观察到请求是从什么地方获取到数据的

- **from memory cache**: 表示资源是从内存缓存中获取的，通常意味着资源最近被访问过，并且存储在RAM中。
- **from disk cache**: 表示资源是从硬盘上的缓存中获取的，意味着资源之前被下载过，并存储在用户的硬盘上。

在实际使用中，浏览器会根据资源的类型、大小、访问频率以及缓存策略等因素决定使用哪种缓存机制。了解这些缓存机制有助于开发者优化网页性能，减少加载时间，提升用户体验。

对于浏览器在与服务器交互的过程，缓存分为可以两种主要类型：强制缓存（也称为强缓存）和协商缓存（也称为弱缓存）。

### 协商缓存

协商缓存则是在资源过期后，浏览器仍然可以向服务器发送请求，但会带上缓存标识，询问服务器资源是否发生了变化。如果服务器判断资源没有变化，就会返回304状态码，告诉浏览器可以继续使用缓存。协商缓存主要依赖于以下两个HTTP头：

1. **Last-Modified / If-Modified-Since**: `Last-Modified` 是服务器响应头，表示资源最后修改的时间。当资源过期后，浏览器再次请求时，会带上 `If-Modified-Since` 这个请求头，其值就是上次服务器返回的 `Last-Modified` 值，该值是从本机的缓存文件中自动获取的。如果资源自上次请求后没有修改，**服务器返回304状态码**，浏览器使用本地缓存，从本地加载文件，就不需要从服务器拉取了；如果资源有更新，则返回新的资源文件和200状态码。

2. **ETag / If-None-Match**: `ETag` 是资源的唯一标识符，由服务器生成。当资源过期后，浏览器会带上 `If-None-Match` 请求头，其值为上次服务器返回的 `ETag` 值。服务器通过比较资源的当前 `ETag` 和请求头中的 `If-None-Match` 值来决定是否返回304状态码。

在默认情况下，Nginx 服务器不会自动为静态文件添加协商缓存相关的响应头（如 `Last-Modified` 或 `ETag`）。Nginx 需要通过配置来启用这些特性。有的 Nginx 版本可能可以直接支持这些特性。

为了启用协商缓存，你需要在 Nginx 配置文件中添加一些指令。以下是一个简单的配置示例，用于启用协商缓存：

```nginx
http {
    # 其他配置...

    server {
        # 服务器配置...

        location / {
            # 静态文件服务配置
            root /path/to/your/files;

            # 启用文件修改时间的记录
            if_modified_since off;
            add_header Last-Modified $date_gmt;

            # 启用 ETag
            etag on;

            # 其他配置...
        }
    }
}
```

在上面的配置中：

- `if_modified_since off;` 确保 Nginx 不会使用客户端提供的 `If-Modified-Since` 头。
- `add_header Last-Modified $date_gmt;` 为响应添加 `Last-Modified` 头，表示文件最后修改时间。
- `etag on;` 启用 `ETag` 功能，为每个文件生成一个唯一的标识符。

请注意，这只是一个基本示例。在实际部署时，你可能需要根据具体需求调整配置。启用这些特性后，Nginx 将会为静态文件响应添加 `Last-Modified` 和 `ETag` 头，从而支持浏览器的协商缓存机制。

完成配置后，记得重新加载或重启 Nginx 以使更改生效。

### 强制缓存

强制缓存指的是当浏览器向服务器请求资源时，服务器通过HTTP响应头告诉浏览器可以使用缓存多长时间。在这个时间范围内，浏览器会直接使用本地缓存的资源，而不会再向服务器发送请求。强制缓存的实现主要依赖于两个HTTP响应头：

1. **Expires**: 这个头指定了资源的过期时间。例如，`Expires: Wed, 21 Oct 2023 07:28:00 GMT` 表示资源在这个时间点之后过期。在过期之前，浏览器会直接使用缓存。

2. **Cache-Control**: 这个头提供了更细粒度的控制。例如，`Cache-Control: max-age=3600` 表示资源在3600秒（即1小时）内有效。浏览器会根据这个时间来判断资源是否过期。

在 Nginx 中配置强制缓存主要涉及设置HTTP响应头，以控制浏览器或客户端缓存资源的时间。强制缓存通过设置 `Expires` 和 `Cache-Control` 响应头来实现。

配置强制缓存

在 `server` 块中，你可以为静态文件设置缓存策略。以下是一个配置示例：

```nginx
server {
    listen 80;
    server_name example.com;

    location / {
        root /path/to/your/web/files;
        index index.html index.htm;

        # 设置强制缓存
        add_header Cache-Control "public"; # 表明响应可以被任何缓存所缓存
        add_header Pragma "public"; # HTTP/1.0 兼容
        add_header Expires "Wed, 21 Oct 2023 07:28:00 GMT"; # 设置资源过期时间
    }
}
```

在这个例子中：

- `add_header Cache-Control "public"` 表示响应可以被任何缓存所缓存。
- `add_header Pragma "public"` 是为了兼容 HTTP/1.0，虽然在 HTTP/1.1 中 `Cache-Control` 头已经取代了 `Pragma`，但添加它不会造成任何负面影响。
- `add_header Expires` 设置了资源的过期时间。在这个时间点之后，浏览器会认为资源已经过期，需要重新从服务器获取。

#### Cache-Control 指令

表格详细列出了 `Cache-Control` 指令的不同标记及其功能，这些指令在 HTTP/1.1 协议中用于控制缓存行为。

| 标记                    | 类型        | 功能                                                          |
| ---------------------- | ---------- | ------------------------------------------------------------ |
| public                  | 响应头      | 表明响应可以被任何缓存所缓存。                                  |
| private                 | 响应头      | 表明响应只能被客户端缓存，代理服务器不能缓存。                  |
| no-cache                | 请求/响应头 | 表明客户端可以缓存响应，但使用缓存前必须向服务器验证缓存是否过期。 |
| no-store                | 请求/响应头 | 表明响应内容不应被存储在缓存或任何地方。                         |
| max-age                 | 请求/响应头 | 指定资源在客户端可以被缓存的最大时间（以秒为单位）。             |
| s-maxage                | 请求/响应头 | 指定资源在代理服务器（如 CDN）中可以被缓存的最大时间（以秒为单位）。 |
| max-stale               | 请求/响应头 | 允许客户端使用过期的缓存，只要它们没有超过指定的时间。           |
| min-fresh               | 请求/响应头 | 要求缓存至少保持指定的时间是新鲜的。                             |
| must-revalidate         | 请求/响应头 | 表明一旦缓存过期，必须向服务器验证缓存的有效性。                 |
| proxy-revalidate        | 请求/响应头 | 类似于 `must-revalidate`，但仅适用于代理服务器。                 |
| stale-while-revalidate  | 响应头      | 允许客户端在后台验证缓存的同时使用过期缓存。                     |
| stale-if-error          | 响应头      | 如果验证请求失败（例如服务器返回5XX错误），则允许使用过期缓存。   |
| only-if-cached          | 请求头      | 表明客户端只希望使用缓存的响应，如果缓存不存在则返回504错误。     |


当浏览器需要验证缓存是否过期时，它会在二次请求时携带 `If-Modified-Since` 头。这个头包含了资源上次被修改的时间。如果服务器上的资源自上次修改后没有变化，服务器将返回 `304 Not Modified` 响应，告诉浏览器可以继续使用缓存的版本。如果资源已经改变，服务器将返回新的资源内容和 `200 OK` 响应。

假设服务器在响应中包含 `Cache-Control: max-age=3600`，这意味着资源在客户端可以被缓存最多3600秒（即1小时）。如果浏览器在1小时后再次请求相同的资源，它会检查本地缓存，并在 `If-Modified-Since` 头中包含资源上次修改的时间。如果资源在这段时间内没有被修改，服务器将返回 `304 Not Modified`，否则返回新的资源和 `200 OK`。

#### Expires

过期时间

```
expires 30s;   #缓存30秒
expires 30m;  #缓存30分钟   
expires 2h;     #缓存2小时
expires 30d;    #缓存30天
```

总结

- **强制缓存**：直接使用本地缓存，不与服务器进行通信，适用于不经常变化的资源。
- **协商缓存**：在资源过期后与服务器进行一次“协商”，判断资源是否更新，适用于经常变化的资源。

#### last-modified 与 ssi 冲突

SSI 和 `Last-Modified`/`If-Modified-Since` 机制之间可能会出现冲突，主要在以下情况下：

1. **动态内容更新**：如果 SSI 用于动态生成内容，那么每次页面请求时，即使内容是动态生成的，页面的最后修改时间也会更新。这会导致 `Last-Modified` 时间频繁变化，使得客户端缓存失效，即使页面内容实际上并没有变化。

2. **缓存失效**：由于 `Last-Modified` 时间的频繁更新，客户端可能会频繁地向服务器请求资源，而不是使用缓存。这不仅降低了性能，还增加了服务器的负载。

解决方案

为了解决 SSI 和 `Last-Modified`/`If-Modified-Since` 之间的冲突，可以采取以下措施：

1. **使用 ETag**：`ETag`（实体标签）是一种更精确的资源标识符，可以用来判断资源是否发生了变化。与 `Last-Modified` 不同，`ETag` 不依赖于资源的修改时间，而是依赖于资源内容的哈希值或其他唯一标识符。这样，即使 SSI 动态生成内容，只要内容未变，`ETag` 保持不变，客户端可以继续使用缓存。

2. **禁用 Last-Modified**：如果动态内容频繁更新，可以考虑在服务器配置中禁用 `Last-Modified` 头的发送。这样，客户端将不会基于 `Last-Modified` 时间来判断资源是否过期，而是依赖于其他缓存策略，如 `Cache-Control`。

3. **调整缓存策略**：在使用 SSI 的页面上，可以设置更合适的 `Cache-Control` 策略，例如使用 `no-cache` 或 `must-revalidate`，以确保内容的实时更新。

#### 浏览器缓存原则

- 多级集群负载时last-modified必须保持一致
- 还有一些场景下我们希望禁用浏览器缓存。比如轮训api上报数据数据
- 浏览器缓存很难彻底禁用，大家的做法是加版本号，随机数等方法。
- 只缓存200响应头的数据，像3XX这类跳转的页面不需要缓存。
- 对于js，css这类可以缓存很久的数据，可以通过加版本号的方式更新内容
- 不需要强一致性的数据，可以缓存几秒
- 异步加载的接口数据，可以使用ETag来校验。
- 在服务器添加Server头，有利于排查错误
- 分为手机APP和Client以及是否遵循http协议
- 在没有联网的状态下可以展示数据
- 流量消耗过多
- 提前下发  避免秒杀时同时下发数据造成流量短时间暴增
- 兜底数据 在服务器崩溃和网络不可用的时候展示
- 临时缓存  退出即清理
- 固定缓存  展示框架这种，可能很长时间不会更新，可用随客户端下发
  - **首页**有的时候可以看做是框架 应该禁用缓存，以保证加载的资源都是最新的
- 父子连接 页面跳转时有一部分内容不需要重新加载，可用从父菜单带过来
- 预加载     某些逻辑可用判定用户接下来的操作，那么可用异步加载那些资源
- 漂亮的加载过程 异步加载 先展示框架，然后异步加载内容，避免主线程阻塞

## CDN 缓存

CDN（内容分发网络）缓存是一种技术，用于加速互联网内容的分发。它通过将内容（如图片、视频、CSS、JavaScript文件等）存储在世界各地的多个地理位置的服务器上，从而减少内容加载时间。当用户尝试访问网站时，CDN会将请求重定向到最近的服务器，以提供更快的加载速度和更好的用户体验。CDN缓存的工作原理如下：

1. **内容缓存**：
   - 当用户首次访问网站时，网站的内容会被缓存到CDN网络中的服务器上。
   - 这些缓存的内容包括静态资源，如图片、CSS文件、JavaScript文件等。

2. **智能路由**：
   - CDN使用智能路由技术将用户的请求重定向到最近的服务器，或者根据网络状况选择最佳的服务器。
   - 这样可以减少数据传输的距离和时间，从而加快内容的加载速度。

3. **缓存策略**：
   - CDN缓存通常有多种策略，如缓存过期时间（TTL），决定内容在CDN服务器上保留多长时间。
   - 当内容更新时，可以通过CDN控制面板或API来清除缓存，确保用户获取到最新的内容。

4. **减少服务器负载**：
   - 通过CDN缓存，原始服务器的负载会大大减少，因为它不需要直接处理所有用户的请求。
   - 这不仅提高了网站的性能，还降低了带宽成本和硬件需求。

5. **安全性**：
   - 许多CDN还提供额外的安全功能，如DDoS攻击防护、Web应用防火墙等，以保护网站不受恶意攻击。

6. **动态内容支持**：
   - 虽然CDN最擅长缓存静态资源，但现代CDN也支持缓存动态内容，通过各种技术如边缘计算来实现。

使用CDN缓存的好处是显而易见的，它不仅提高了网站的加载速度，还提升了用户体验，并且有助于网站应对高流量。对于希望在全球范围内提供快速、可靠服务的网站来说，CDN缓存是一个非常重要的工具。

比如像百度谷歌等搜索网站通常会把搜索结果的前10页缓存在CND服务器上，在内容变更时统一更新，因为对时效性要求不高。










































































# 常见问题

## Nginx 在集群中的作用

1. 作为统一的流量入口
2. 内外网隔离
3. 反向代理服务器
4. 负载均衡
5. 缓存静态内容，动静分离

## Nginx 如何发现代理的服务器压力过大或服务器反应迟钝

在使用 Nginx 作为反向代理服务器时，监控后端服务器的压力和响应速度是非常重要的。Nginx 提供了多种机制来帮助你发现代理的服务器压力过大或服务器反应迟钝。以下是一些常用的方法：

1. **监控状态码**：
   Nginx 可以通过检查后端服务器返回的状态码来判断服务器的健康状况。例如，你可以配置 Nginx 仅将请求转发到返回 200 OK 状态码的服务器。如果服务器返回 5xx 错误，Nginx 可以认为该服务器暂时不可用，并将请求转发到其他服务器。

2. **使用 `proxy_next_upstream` 指令**：
   通过 `proxy_next_upstream` 指令，你可以定义在什么情况下 Nginx 应该将请求转发到下一个服务器。例如，如果一个服务器响应超时或返回错误，Nginx 可以自动将请求转发到下一个服务器。

3. **设置超时**：
   Nginx 允许你设置多个超时参数，如 `proxy_connect_timeout`、`proxy_send_timeout`、`proxy_read_timeout` 等，这些参数可以帮助你控制 Nginx 等待后端服务器响应的时间。如果后端服务器在这些超时时间内没有响应，Nginx 可以认为服务器压力过大或反应迟钝。

4. **使用 `upstream` 模块的健康检查**：
   Nginx 的 `upstream` 模块支持简单的健康检查功能。你可以配置 Nginx 定期向后端服务器发送健康检查请求，并根据响应结果决定是否将流量转发到该服务器。

5. **监控 Nginx 日志**：
   通过分析 Nginx 的访问日志和错误日志，你可以了解后端服务器的响应时间和错误率。如果发现某个服务器的响应时间异常增长或错误率上升，这可能表明服务器压力过大或反应迟钝。

6. **使用外部监控工具**：
   除了 Nginx 内置的监控功能外，你还可以使用外部监控工具（如 Nagios、Zabbix、Prometheus 等）来监控 Nginx 和后端服务器的性能。这些工具可以提供更详细的性能指标和警报功能。

7. **负载均衡策略**：
   通过合理配置负载均衡策略，如轮询（round-robin）、最少连接（least_conn）等，可以有效分散请求到不同的后端服务器，避免单个服务器压力过大。

通过上述方法，你可以有效地监控和管理 Nginx 代理的后端服务器，确保服务的稳定性和响应速度。在实际部署时，你可能需要结合多种方法来达到最佳效果。

## 如何感知配置文件的变更及拉取最新配置

当 Nginx 集群规模较大时，手动更新每个节点的配置文件会变得非常繁琐且容易出错。为了高效地管理和批量更新 Nginx 配置，可以采用类似于配置中心的机制。以下是一些常见的方法：

1. **使用配置管理工具**：
   - **Ansible**：通过 Ansible 的 playbook 可以自动化地在多个 Nginx 服务器上执行配置更新。可以编写一个 Ansible playbook 来推送配置文件、执行测试命令和重新加载 Nginx。
   - **Chef** 和 **Puppet**：这些配置管理工具允许你定义配置文件的期望状态，并自动将这些配置应用到所有受管节点上。

2. **使用版本控制系统**：
   - 将 Nginx 配置文件存放在如 Git 这样的版本控制系统中。通过版本控制系统的钩子（hooks）或 CI/CD 流程，可以自动化地将配置变更推送到所有 Nginx 服务器。

3. **使用配置中心服务**：
   - **Consul**、**Zookeeper**、**etcd** 等服务可以作为配置中心，存储和管理 Nginx 配置。通过这些服务，可以集中管理配置文件，并在变更时通知所有 Nginx 服务器。

4. **使用容器化和编排工具**：
   - **Docker** 和 **Kubernetes** 等容器化和编排工具可以用来部署和管理 Nginx 容器。通过容器镜像和部署模板，可以确保所有 Nginx 实例都使用相同的配置。

5. **使用脚本和命令行工具**：
   - 编写脚本，利用 SSH 或其他远程执行工具（如 `pdsh`、`salt` 等）来在所有 Nginx 服务器上执行配置更新和重启命令。

6. **使用云服务提供商的管理工具**：
   - 如果 Nginx 部署在云环境中，可以利用云服务提供商提供的管理工具（如 AWS 的 CloudFormation、Azure 的 ARM 模板、Google Cloud 的 Deployment Manager 等）来自动化配置更新。

7. **使用自定义脚本和API**：
   - 如果有自定义的监控和管理平台，可以开发脚本或 API 来自动化配置更新流程。

无论采用哪种方法，关键在于确保配置更新的流程是可重复的、可靠的，并且能够快速回滚到之前的配置状态，以防更新后的配置出现问题。同时，应该在更新配置之前进行充分的测试，以确保变更不会影响服务的正常运行。



