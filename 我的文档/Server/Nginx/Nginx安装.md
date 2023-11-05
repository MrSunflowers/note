版本区别

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