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

# 4.域名解析

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











