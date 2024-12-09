# 前置条件

## 环境

在通过源码安装前，需要一定的前置环境：

1. GCC，一般系统自带，没有需要先安装
2. pcre（正则表达式） 支持，安装命令 `yum install pcre*`
3. zlib，`yum install zlib zlib-devel`
4. 如果需要 SSL 支持的话，安装 openSSL : `yum install openssl openssl-devel`

## 版本区别

常用版本分为四大阵营

Nginx开源版： 包含Nginx基本功能 主要实现了 网站服务器、负载均衡器、代理服务器三大功能
- http://nginx.org/

Nginx plus 商业版： F5 官方出品，商业定制化产品 如 k8s
- https://www.nginx.com

openresty： Nginx + lua 整合 主要学习
- http://openresty.org/cn/

Tengine： 阿里开源 Nginx + C语言 整合
- http://tengine.taobao.org

Nginx的安装可以选择源码编译的方式也可以使用宝塔面板安装，本文采用的是源码编译安装。

# CentOS7

## 源码编译安装

解压文件

```
tar zxvf nginx-1.21.6.tar.gz
```

进入解压后的文件夹

```
cd nginx-1.21.6
```

执行命令
```
./configure --prefix=/opt/nginx  # --prefix=/opt/nginx 指安装路径是/opt/nginx
```

如果出现警告或报错

```
checking for OS
+ Linux 3.10.0-693.el7.x86_64 x86_64
checking for C compiler ... not found
./configure: error: C compiler cc is not found
```

安装gcc

```
yum install -y gcc
```

如果出现警告或报错

```
/configure: error: the HTTP rewrite module requires the PCRE library.
You can either disable the module by using --without-http_rewrite_module
option, or install the PCRE library into the system, or build the PCRE library
statically from the source with nginx by using --with-pcre=<path> option.
```

安装perl库

```
yum install -y pcre pcre-devel
```

如果出现警告或报错

```
./configure: error: the HTTP gzip module requires the zlib library.
You can either disable the module by using --without-http_gzip_module
option, or install the zlib library into the system, or build the zlib library
statically from the source with nginx by using --with-zlib=<path> option.
```

安装zlib库:

```
yum install -y zlib zlib-devel
```

接下来执行

```
make
make install
```

## 启动 nginx

进入安装好的目录 /opt/nginx/sbin

```
./nginx					    # 启动
./nginx -s stop			 	#快速停止
./nginx -s quit 			#优雅关闭，在退出前完成已经接受的连接请求
./nginx -s reload 			#重新加载配置
```

## 安装成系统服务

在如下位置创建服务脚本 nginx.service

```
vi /usr/lib/systemd/system/nginx.service
```

服务脚本内容如下(注意路径要对应，这里的路径是 /opt/nginx/sbin )：

```
[Unit]
Description=nginx - web server
After=network.target remote-fs.target nss-lookup.target

[Service]
Type=forking
PIDFile=/opt/nginx/logs/nginx.pid
ExecStartPre=/opt/nginx/sbin/nginx -t -c /opt/nginx/conf/nginx.conf
ExecStart=/opt/nginx/sbin/nginx -c /opt/nginx/conf/nginx.conf
ExecReload=/opt/nginx/sbin/nginx -s reload
ExecStop=/opt/nginx/sbin/nginx -s stop
ExecQuit=/opt/nginx/sbin/nginx -s quit
PrivateTmp=true

[Install]
WantedBy=multi-user.target
```

重新加载系统服务

```
systemctl daemon-reload
```


启动服务

```
systemctl start nginx.service
```

开机启动

```
systemctl enable nginx.service
```

永久关闭防火墙

```
sudo systemctl disable firewalld
```


测试，访问 http://192.168.1.130/

# Nginx的基本运行

测试配置文件：

安装路径下的/nginx/sbin/nginx -t 

启动：

安装路径下的/nginx/sbin/nginx 

停止

安装路径下的/nginx/sbin/nginx -s stop 
或者是：nginx -s quit

重启

安装路径下的/nginx/sbin/nginx -s reload 

查看进程

ps -ef grep nginx

安装过后，如果从外面访问不了，多半是被防火墙挡住了，可以关闭掉防火墙：
/sbin/service iptables stop

# 第三方模块平滑升级

在使用 NGINX 作为文件下载服务器时，可以有多个 NGINX 组成集群，在集群下游同样由 NGINX 担任网关分发请求，此时，当客户端与文件服务器（NGINX）建立连接时，我们希望后续就由该服务器一直服务该用户，因为建立连接是一个非常耗费资源的动作，此时就需要网关服务器能够识别用户且将用户的请求发往同一台服务器，而普通的 Hash 算法已经不能满足现有的需求，就需要引入更强大的第三方模块来实现，且安装新模块时，我们并不希望对现有配置和服务器状态有所影响，如何平滑的升级 NGINX 就成了眼下的问题，下面以安装 sticky 模块为例来演示如何平滑的升级 NGINX

## 使用 sticky 模块完成对 Nginx 的负载均衡

sticky 使用参考

http://nginx.org/en/docs/http/ngx_http_upstream_module.html#sticky

## 安装 sticky

**1.环境准备**

项目官网

https://bitbucket.org/nginx-goodies/nginx-sticky-module-ng/src/master/

GitHub

https://github.com/bymaximus/nginx-sticky-module-ng

下载地址

https://bitbucket.org/nginx-goodies/nginx-sticky-module-ng/get/1.2.6.zip

编译 nginx-sticky-module-ng 模块依赖于 openssl-devel 包

如果你在编译带有 `nginx-sticky-module-ng` 的 NGINX 时遇到了错误，并且错误提示表明需要 `openssl-devel` 包，这意味着你的系统缺少编译该模块所需的 OpenSSL 开发库。

`openssl-devel` 包含了 OpenSSL 的头文件和库文件，这些是编译依赖于 OpenSSL 的软件（如 `nginx-sticky-module-ng`）所必需的。在不同的 Linux 发行版中，这个包可能有不同的名称，例如在基于 Debian 的系统中，它可能被称为 `libssl-dev`。

要解决这个问题，你需要安装相应的开发包。以下是在一些常见 Linux 发行版上安装 `openssl-devel` 的命令：

对于基于 **Red Hat/CentOS** 的系统（如 CentOS 7 或 Fedora）：

```shell
sudo yum install openssl-devel
```

对于基于 **Debian/Ubuntu** 的系统：

```shell
sudo apt-get install libssl-dev
```

安装完成后，重新运行 `./configure` 和 `make` 命令来编译 NGINX 和 `nginx-sticky-module-ng` 模块。如果在编译过程中遇到其他问题，确保检查错误信息并根据需要调整配置或源代码。

请记住，在生产环境中部署任何软件之前，始终在测试环境中进行充分的测试。

**2.重新编译 NGINX**

将安装包上传至 NGINX 服务器并解压缩

找到原来安装 NGINX 的源码，重新编译 NGINX

进到源码目录重新编译

```shell
./configure --prefix=/opt/nginx --add-module=sticky解压的文件夹名称
```

这里会重新编译生成一个新的 NGINX ，所以在编译时要加上之前编译的参数，比如 `--with-http_ssl_module` 来启用 SSL 支持，如果之前编译没有额外的参数就不需要加

`--add-module` 参数用于指定编译时要添加的第三方模块的目录，如果添加的是 NGINX 自带的模块，则使用 `whith` 参数，比如 `--with-http_ssl_module` 来启用 SSL 支持

执行 make

```shell
make
```

如果遇到报错需要修改 sticky 的源码

这通常是因为 `nginx-sticky-module-ng` 模块依赖于 OpenSSL 库来执行哈希计算，而编译器找不到相关的头文件。添加这两行代码将确保编译器能够找到执行哈希计算所需的函数声明。

打开 `ngx_http_sticky_misc.c` 文件

在12行添加

```c
#include <openssl/sha.h>
#include <openssl/md5.h>
```

在修改源码后，你需要重新编译和 make

```shell
./configure --prefix=/opt/nginx --add-module=sticky解压的文件夹名称
make
```

在 make 完成后，编译好的程序在 NGINX 安装包的 objs 文件夹下，需要用新的 NGINX 可执行文件替换掉旧的 NGINX 可执行文件

开始替换前备份之前的程序

```
mv /usr/local/nginx/sbin/nginx /usr/local/nginx/sbin/nginx.old
```

把编译好的 Nginx 程序替换到原来的目录里

```shell
cp objs/nginx /usr/local/nginx/sbin/
```

在生产环境下，在替换之前通常会经过严格测试后才允许替换，这里除了功能性测试之外还涉及到几个 NGINX 的测试命令

升级检测

```
make upgrade
```

检查程序中是否包含新模块

```
nginx -V
```


# Nginx 编译命令

```bash
./configure
```


## 常见的Nginx安装配置选项

编译参数可能会根据版本的不同进行变化，使用 `./configure --help` 查看编译参数列表，常见的选项如下：

- `--prefix=<path>` - 安装路径，如果没有指定，默认为 `/usr/local/nginx`。
- `--sbin-path=<path>` - nginx 可执行命令的文件，如果没有指定，默认为 `<prefix>/sbin/nginx`。
- `--conf-path=<path>` - 在没有使用 `-c` 参数指定的情况下 `nginx.conf` 的默认位置，如果没有指定，默认为 `<prefix>/conf/nginx.conf`。
- `--pid-path=<path>` - nginx.pid 的路径，如果没有在 `nginx.conf` 中通过 “pid” 指令指定，默认为 `<prefix>/logs/nginx.pid`。
- `--lock-path=<path>` - nginx.lock 文件路径，如果没有指定，默认为 `<prefix>/logs/nginx.lock`。
- `--error-log-path=<path>` - 当没有在 `nginx.conf` 中使用 “error_log” 指令指定时的错误日志位置，如果没有指定，默认为 `<prefix>/logs/error.log`。
- `--http-log-path=<path>` - 当没有在 `nginx.conf` 中使用 “access_log” 指令指定时的访问日志位置，如果没有指定，默认为 `<prefix>/logs/access.log`。
- `--user=<user>` - 当没有在 `nginx.conf` 中使用 “user” 指令指定时 nginx 运行的用户，如果没有指定，默认为 “nobody”。
- `--group=<group>` - 当没有在 `nginx.conf` 中使用 “user” 指令指定时 nginx 运行的组，如果没有指定，默认为 “nobody”。
- `--builddir=DIR` - 设置构建目录。
- `--with-rtsig_module` - 启用 rtsig 模块。

1. **--with-select_module --without-select_module**
   - 如果在 configure 的时候没有发现 kqueue, epoll, rtsig 或 /dev/poll 其中之一，select 模块始终为启用状态。

2. **--with-poll_module --without-poll_module**
   - 如果在 configure 的时候没有发现 kqueue, epoll, rtsig 或 /dev/poll 其中之一，poll 模块始终为启用状态。

3. **--with-http_ssl_module**
   - 启用 ngx_http_ssl_module，启用 SSL 支持并且能够处理 HTTPS 请求。需要 OpenSSL，在 Debian 系统中，对应的包为 libssl-dev。

4. **--with-http_realip_module**
   - 启用 ngx_http_realip_module。

5. **--with-http_addition_module**
   - 启用 ngx_http_addition_module。

6. **--with-http_sub_module**
   - 启用 ngx_http_sub_module。

7. **--with-http_dav_module**
   - 启用 ngx_http_dav_module。

8. **--with-http_flv_module**
   - 启用 ngx_http_flv_module。

9. **--with-http_stub_status_module**
   - 启用 “server status”（服务状态）页。

10. **--without-http_charset_module**
    - 禁用 ngx_http_charset_module。

11. **--without-http_gzip_module**
    - 禁用 ngx_http_gzip_module，如果启用，需要 zlib 包。

12. **--without-http_ssi_module**
    - 禁用 ngx_http_ssi_module。

13. **--without-http_userid_module**
    - 禁用 ngx_http_userid_module。

14. **--without-http_access_module**
    - 禁用 ngx_http_access_module。

15. **--without-http_auth_basic_module**
    - 禁用 ngx_http_auth_basic_module。

