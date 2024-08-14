[TOC]

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

```
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

```
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

```
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

```
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

```
server_name  test81.xzj520520.cn  test82.xzj520520.cn;
```


### 通配符匹配

使用通配符的方式如下：

```
server_name  *.xzj520520.cn;
```

需要注意的是**精确匹配**的优先级大于**通配符匹配**和**正则匹配**。


### 正则匹配

采用正则的匹配方式如下:

![41d0a93f50338db6ed3b62480fc92319](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202311052123608.png)

正则匹配格式，必须以~开头，比如：`server_name ~^www\d+\.example\.net$;`。如果开头没有`~`，则nginx认为是精确匹配。在逻辑上，需要添加`^`和`$`锚定符号。注意，正则匹配格式中`.`为正则元字符，如果需要匹配`.`，则需要反斜线转义。如果正则匹配中含有`{`和`}`则需要双引号引用起来，避免nginx报错，如果没有加双引号，则nginx会报如下错误：`directive "server_name" is not terminated by ";" in ...`。

### 特殊匹配格式

采用正则的匹配方式如下:

```
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

```

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

```
proxy_pass + 空格 + 完整网址（一般为非 https 协议）/ http:// + 服务器组别名 + 封号结尾
```

示例：

```

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

```
upstream 别名 {
    server 192.168.1.131:80;
    server 192.168.1.132:80;
}
```

克隆两个 centos，ip分别设为192.168.1.131，192.168.1.132

示例：

```
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

默认带有负载均衡功能，默认策略为轮训

## 负载均衡器策略

### 1.轮训

见上述示例

### 2.权重 

weight 值越大权重越大，权重是相对的

```
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

### 3.down (宕机)不参与负载均衡

```
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

### 4.backup 备用服务器，主机不可用时使用

```

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

### 5.IP Hash 

根据客户端的ip地址转发同一台服务器，可以保持会话，但是很少用这种方式去保持会话，例如我们当前正在使用wifi访问，当切换成手机信号访问时，会话就不保持了。

### 6.least_conn

最少连接访问，优先访问连接最少的那一台服务器，这种方式也很少使用，因为连接少，可能是由于该服务器配置较低，刚开始赋予的权重较低。

### 7.url_hash（需要第三方插件） 

根据用户访问的url定向转发请求，不同的url转发到不同的服务器进行处理（定向流量转发），一般用于定向寻找资源，例如100个文件散列在10台机器上，就可以根据请求文件资源的哈希定位到具体的机器上。

### 8.fair（需要第三方插件）

根据后端服务器响应时间转发请求，这种方式也很少使用，因为容易造成流量倾斜，给某一台服务器压垮。

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

```
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

```
location = / {
    proxy_pass http://127.0.0.1:8080/; 
}
```


第二个必选规则

处理静态文件请求，这是nginx作为http服务器的强项,有两种配置模式，目录匹配或后缀匹配,任选其一或搭配使用

```
location ^~ /static/ {
    root /webroot/static/;
}

location ~* \.(html|gif|jpg|jpeg|png|css|js|ico)$ {
    root /webroot/res/;
}
```

第三个规则

通用规则，用来转发动态请求到后端应用服务器

```
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

```
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

```
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

Keepalived 软件起初是专为 LVS 负载均衡软件设计的，用来管理并监控 LVS 集群系统中各个服务节点的状态，后来又加入了可以实现高可用的 VRRP 功能。因此，Keepalived 除了能够管理 LVS 软件外，还可以作为其他服务（例如：Nginx、Haproxy、MySQL等）的高可用解决方案软件。VRRP 出现的目的就是为了解决静态路由单点故障问题的，它能够保证当个别节点宕机时，整个网络可以不间断地运行。所以，Keepalived 一方面具有配置管理 LVS 的功能，同时还具有对 LVS 下面节点进行健康检查的功能，另一方面也可实现系统网络服务的高可用功能。

keepalived 官网 http://www.keepalived.org

其主要作用包括：

1. **故障检测与转移**：Keepalived 能够监控服务器的健康状态，一旦检测到某台服务器宕机或出现故障，它会自动将该服务器从系统中剔除，并使用其他正常工作的服务器来替代其工作。当故障服务器恢复正常后，Keepalived 可以自动将其重新加入到服务器群中，整个过程无需人工干预。

2. **高可用性保障**：通过使用虚拟路由冗余协议（VRRP），Keepalived 可以实现服务的高可用性。在VRRP模式下，通常有两台服务器运行Keepalived，一台作为主服务器（MASTER），另一台作为备份服务器（BACKUP）。对外表现为一个虚拟IP，主服务器定期向备份服务器发送特定消息，如果备份服务器收不到这些消息，即认为主服务器宕机，备份服务器就会接管虚拟IP，继续提供服务，从而保证服务的连续性。

3. **负载均衡支持**：Keepalived 可以与负载均衡技术（如LVS、Haproxy、Nginx）结合使用，以实现集群的高可用性。它通过监控集群中各个服务节点的状态，确保服务的稳定运行。

4. **多层检测**：Keepalived 可以在IP层（Layer3）、传输层（Layer4）和应用层（Layer5）进行服务器状态的检测。Layer3检测基于ICMP数据包，Layer4检测基于TCP端口状态，而Layer5检测则根据用户设定的条件来检查服务器程序的运行状态。

keepalived服务的三个重要功能：

- 管理LVS负载均衡软件
- 实现LVS集群节点的健康检查中
- 作为系统网络服务的高可用性（failover）


配置 Keepalived 主要涉及编辑其配置文件 `/etc/keepalived/keepalived.conf`。下面是一个基本的配置步骤和示例：

## Keepalived 使用

### 步骤 1: 安装 Keepalived

在基于Debian的系统（如Ubuntu）上，使用以下命令安装：
```bash
sudo apt-get update
sudo apt-get install keepalived
```
在基于Red Hat的系统（如CentOS）上，使用以下命令安装：
```bash
sudo yum install keepalived
```

### 步骤 2: 编辑配置文件

配置文件通常位于 `/etc/keepalived/keepalived.conf`。你可以使用文本编辑器打开并编辑它，例如使用 `nano` 或 `vi`。

### 步骤 3: 配置 VRRP 实例

VRRP（虚拟路由冗余协议）是实现高可用性的关键。以下是一个简单的 VRRP 配置示例：

```conf
! Configuration File for keepalived

global_defs {
   notification_email {
     admin@example.com
   }
   notification_email_from keepalived@example.com
   smtp_server 127.0.0.1
   smtp_connect_timeout 30
   router_id LVS_DEVEL
}

vrrp_instance VI_1 {
    state MASTER
    interface eth0
    virtual_router_id 51
    priority 100
    advert_int 1
    authentication {
        auth_type PASS
        auth_pass 1111
    }
    virtual_ipaddress {
        192.168.0.100
    }
}
```

在这个例子中：
- `state` 表示当前服务器的角色，可以是 MASTER 或 BACKUP。
- `interface` 是服务器上用于 VRRP 的网络接口。
- `virtual_router_id` 是一个在局域网内唯一的标识符。
- `priority` 决定了在多个 MASTER 之间哪个有更高的优先级。
- `advert_int` 是 VRRP通告消息的发送间隔（秒）。
- `authentication` 是用于 VRRP 通信的认证方式。
- `virtual_ipaddress` 是虚拟IP地址，客户端将使用这个地址访问服务。

### 步骤 4: 配置健康检查

Keepalived 可以配置健康检查来确认服务是否正常运行。这通常通过在 `vrrp_instance` 之外定义 `virtual_server` 来实现：

```conf
virtual_server_group my_group {
    192.168.0.100 80
}

virtual_server 192.168.0.100 80 {
    delay_loop 6
    lb_algo rr
    lb_kind NAT
    persistence_timeout 50
    protocol TCP

    real_server 192.168.0.101 80 {
        weight 1
        HTTP_GET {
            url {
              path /
              status_code 200
            }
            connect_timeout 3
            nb_get_retry 3
            delay_before_retry 3
        }
    }
}
```

在这个例子中：
- `virtual_server_group` 定义了一组虚拟服务器。
- `virtual_server` 定义了具体的虚拟服务器地址和端口。
- `real_server` 定义了实际提供服务的服务器地址和端口。
- `HTTP_GET` 是用于检查服务是否正常运行的健康检查方法。

### 步骤 5: 重启 Keepalived 服务

配置完成后，需要重启 Keepalived 服务以应用更改：

```bash
sudo systemctl restart keepalived
```

或者，如果你的系统不使用 `systemctl`，可以使用以下命令：

```bash
sudo service keepalived restart
```

### 注意事项

- 确保配置文件的语法正确，可以使用 `keepalived -f /etc/keepalived/keepalived.conf -n` 来检查配置文件的语法。
- 在生产环境中，根据实际网络环境和需求，可能需要对配置进行更详细的调整和优化。
- 请确保在修改配置文件之前备份原始文件，以防配置错误导致服务中断。

以上步骤提供了一个基本的 Keepalived 配置框架，具体配置可能需要根据你的网络架构和需求进行调整。

## keepalived 实战

centos安装命令：

```
 yum install -y keepalived
```

keepalived 的配置文件在如下位置：

```
/etc/keepalived/keepalived.conf
```

在该实战中，101 为主 nginx，102 为备用机，首先需要修改 101 和 102 的 keepalived.conf 配置

101 的 keepalived.conf 配置，使用 ens33 网卡

```
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

使用 systemctl start keepalived 启动 keepalived,查看 ip 发现多了虚拟 ip 192.168.8.200

102 的 keepalived.conf 配置,使用 ens33 网卡

```
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

## Keepalived 的脚本钩子功能

Keepalived 提供了脚本钩子功能，允许在特定事件发生时执行自定义脚本。例如，你可以定义一个脚本来在 VRRP 状态改变时运行，从而实现更复杂的逻辑。

Keepalived 的脚本钩子功能允许在特定的 VRRP 事件发生时执行自定义脚本。这可以用于在服务状态改变时执行额外的逻辑，比如在检测到服务故障时触发某些操作。下面是如何配置 Keepalived 脚本钩子的步骤：

### 步骤 1: 编写脚本

首先，编写一个脚本来执行你希望在 VRRP 状态改变时进行的操作。例如，下面的脚本在检测到 Nginx 停止时会输出一条消息：

```bash
#!/bin/bash

# 检查 Nginx 是否在运行
NGINX_STATUS=$(pgrep -f nginx)

# 如果 Nginx 没有运行，则输出一条消息
if [ -z "$NGINX_STATUS" ]; then
    echo "Nginx is down."
    # 在这里可以添加其他逻辑，比如发送通知或执行其他操作
fi
```

确保脚本具有执行权限：

```bash
chmod +x /path/to/your/script.sh
```

### 步骤 2: 配置 Keepalived

在 Keepalived 的配置文件 `/etc/keepalived/keepalived.conf` 中，添加 `vrrp_script` 部分来指定脚本及其执行频率。以下是一个配置示例：

```conf
global_defs {
    notification_email {
        admin@example.com
    }
    notification_email_from keepalived@example.com
    smtp_server 127.0.0.1
    smtp_connect_timeout 30
    router_id LVS_DEVEL
}

vrrp_script check_nginx {
    script "/path/to/your/script.sh"  # 指定脚本路径
    interval 5                         # 每5秒执行一次脚本
    weight -20                         # 如果脚本返回非零值，优先级减少20
}

vrrp_instance VI_1 {
    state MASTER
    interface eth0
    virtual_router_id 51
    priority 100
    advert_int 1
    authentication {
        auth_type PASS
        auth_pass 1111
    }
    virtual_ipaddress {
        192.168.0.100
    }
}
```

在这个配置中，`vrrp_script` 部分定义了一个名为 `check_nginx` 的脚本钩子，它会每5秒执行一次指定的脚本。如果脚本返回非零值（表示检测到 Nginx 停止），则当前服务器的 VRRP 优先级会减少20。

### 步骤 3: 重启 Keepalived

配置完成后，需要重启 Keepalived 服务以应用更改：

```bash
sudo systemctl restart keepalived
```

或者，如果你的系统不使用 `systemctl`，可以使用以下命令：

```bash
sudo service keepalived restart
```

### 注意事项

- 确保脚本路径正确，并且脚本具有执行权限。
- 脚本钩子的 `weight` 参数可以根据需要调整，以影响 VRRP 优先级。
- 通过 `interval` 参数可以控制脚本执行的频率。
- 在生产环境中，确保脚本的逻辑是安全和可靠的，避免不必要的故障转移。

通过这种方式，你可以根据服务的实际运行状态来动态调整 Keepalived 的行为，实现更精细的高可用性控制。

## 通过脚本检测本机 NGINX 的服务状态

要通过 Keepalived 的健康检查来监控 Nginx 服务状态，并在服务超时或无响应时杀死本机的 Keepalived 进程，你可以使用 Keepalived 的 `track_script` 功能。这个功能允许你运行一个或多个脚本，并根据脚本的返回值来调整 VRRP 实例的优先级。

### 步骤 1: 编写检查 Nginx 状态的脚本

首先，创建一个脚本来检查 Nginx 是否在运行。如果 Nginx 服务无响应或超时，脚本将返回一个非零值。

```bash
#!/bin/bash

# 检查 Nginx 是否在运行
NGINX_STATUS=$(pgrep -f nginx)

# 设置超时时间（秒）
TIMEOUT=10

# 使用 curl 检查 Nginx 是否响应
RESPONSE=$(curl --max-time $TIMEOUT --silent --output /dev/null --head http://localhost/)

# 如果 Nginx 没有运行或 curl 超时，则返回非零值
if [ -z "$NGINX_STATUS" ] || [ $? -ne 0 ]; then
    echo "Nginx is down or not responding."
    exit 1
else
    echo "Nginx is up and running."
    exit 0
fi
```

确保脚本具有执行权限：

```bash
chmod +x /path/to/your/check_nginx.sh
```

### 步骤 2: 配置 Keepalived

在 Keepalived 的配置文件 `/etc/keepalived/keepalived.conf` 中，添加 `track_script` 部分来指定脚本，并在 `vrrp_instance` 部分中使用 `track_script` 的结果来调整优先级。

```conf
global_defs {
    notification_email {
        admin@example.com
    }
    notification_email_from keepalived@example.com
    smtp_server 127.0.0.1
    smtp_connect_timeout 30
    router_id LVS_DEVEL
}

vrrp_script check_nginx {
    script "/path/to/your/check_nginx.sh"
    interval 5
    weight -20
}

vrrp_instance VI_1 {
    state MASTER
    interface eth0
    virtual_router_id 51
    priority 100
    advert_int 1
    authentication {
        auth_type PASS
        auth_pass 1111
    }
    virtual_ipaddress {
        192.168.0.100
    }
    track_script {
        check_nginx
    }
}
```

在这个配置中，`track_script` 部分指定了之前创建的脚本。如果脚本返回非零值（表示 Nginx 服务无响应或超时），`weight` 参数将减少20，这可能触发虚拟IP地址的漂移。

### 步骤 3: 重启 Keepalived

配置完成后，需要重启 Keepalived 服务以应用更改：

```bash
sudo systemctl restart keepalived
```

或者，如果你的系统不使用 `systemctl`，可以使用以下命令：

```bash
sudo service keepalived restart
```

### 注意事项

- 确保脚本路径正确，并且脚本具有执行权限。
- 根据你的环境调整 `TIMEOUT` 变量的值。
- 通过 `weight` 参数可以控制脚本执行结果对 VRRP 优先级的影响。
- 在生产环境中，确保脚本的逻辑是安全和可靠的，避免不必要的故障转移。

通过这种方式，Keepalived 将能够根据 Nginx 的实际运行状态来动态调整其行为，从而实现更精细的高可用性控制。

# 12.https签名配置

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

### https 配置

#### 自签名 


















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



