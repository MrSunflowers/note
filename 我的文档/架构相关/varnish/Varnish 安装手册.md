# CentOS 7

## 源码安装（未成功）

这是一份在CentOS 环境下安装Varnish的指南。以下是详细步骤：

1. **安装gcc**：
   - 系统自带了gcc，如果没有，需要先安装。
	 ```
	 yum install -y gcc
	 ```
2. **安装pcre**：
   - 这个在前面讲Nginx时已经安装了。
	 ```
	 yum install -y pcre pcre-devel
	 ```
3. **安装libedit-dev**：
   - 使用以下命令安装：
     ```bash
     yum install libedit-dev*
     ```

4. **下载并解压 Varnish 源码**：
   - 前往 [Varnish官网](https://www.varnish-cache.org/) 下载源码，然后进行解压安装。例如：
     ```bash
     wget https://www.varnish-cache.org/releases/varnish-6.0.5.tgz
     tar -zxvf varnish-6.0.5.tgz
     cd varnish-6.0.5
     ```

5. **设置PCRE路径**：
   
   - 因为安装Varnish需要PCRE，所以先设置一下路径：
     ```bash
     export PKG_CONFIG_PATH=/usr/local/pcre/lib/pkgconfig # 默认安装的 PCRE 路径
     ```
   
6. **配置编译环境**：
   - 执行以下命令进行配置：
     ```bash
     ./configure --prefix=/opt/varnish # 安装位置
     ```

7. **编译并安装**：
   - 执行以下命令进行编译和安装：
     ```bash
     make
     make install
     ```
## 运行

运行前的准备

========================》

运行的基本命令示例

```bash
./varnishd -f /usr/common/varnish/etc/varnish/default.vcl -s malloc,32M -T 127.0.0.1:2000 -a 0.0.0.0:1111
```

其中：

1. `-f` 指定要运行的配置文件。
2. `-s malloc,32M` 选项用来确定Varnish使用的存储类型和存储容量，这里使用的是malloc类型（malloc是一个C函数，用于分配内存空间）。
3. `-T 127.0.0.1:2000` 指定Varnish的管理IP和端口。
4. `-a 0.0.0.0:1111` 指定Varnish对外提供web服务的IP和端口。0.0.0.0 代表本机的 1111 端口

## 测试

配置完成后，可以通过以下URL进行测试：

- `http://192.168.0.103:1111/arch1/goods/toList`：这是访问 Varnish 的。

## 关闭

到 `varnish/sbin` 的路径下，运行以下命令关闭 Varnish：

```bash
pkill varnishd
```

## yum 安装

```
sudo yum install -y epel-release
sudo yum makecache
sudo yum install -y jemalloc
sudo yum install -y redhat-rpm-config
sudo yum install -y varnish
```

检查 Varnish 版本

```
varnishd -V
```

检查 Varnish 服务状态

```
sudo systemctl status varnish
```

启动

```
sudo systemctl start varnish
```

设置为开机自启动

```
sudo systemctl enable varnish
```

配置文件位置

Varnish 的主要配置文件通常位于 /etc/varnish/default.vcl 和 /etc/varnish/varnish.params。