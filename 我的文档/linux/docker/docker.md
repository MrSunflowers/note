[TOC]
# Docker简介

&emsp;&emsp;相当于跑在 Linux 中的 VMware，一个 linux 中可以创建多个虚拟机，可以将开发软件和环境全部打包，安装的时候，把原始环境一模一样地复制过来，开发人员利用 Docker 可以消除协作编码时“在我的机器上可正常工作”的问题。

&emsp;&emsp;与虚拟机的区别在于它只保留了容器运行的必要环境，其余的部分共用主机的一套。

&emsp;&emsp;Docker 是基于 Go 语言实现的云开源项目。

## 传统虚拟机技术

&emsp;&emsp;虚拟机（virtual machine）就是带环境安装的一种解决方案。

&emsp;&emsp;它可以在一种操作系统里面运行另一种操作系统，比如在Windows10系统里面运行Linux系统CentOS7。应用程序对此毫无感知，因为虚拟机看上去跟真实系统一模一样，而对于底层系统来说，虚拟机就是一个普通文件，不需要了就删掉，对其他部分毫无影响。这类虚拟机完美的运行了另一套系统，能够使应用程序，操作系统和硬件三者之间的逻辑不变。

虚拟机的缺点：
- 资源占用多
- 冗余步骤多
- 启动慢

## 容器虚拟化技术

&emsp;&emsp;由于前面虚拟机存在某些缺点，Linux发展出了另一种虚拟化技术。

&emsp;&emsp;Linux容器是与系统其他部分隔离开的一系列进程，从另一个镜像运行，并由该镜像提供支持进程所需的全部文件。容器提供的镜像包含了应用的所有依赖项，因而在从开发到测试再到生产的整个过程中，它都具有可移植性和一致性。

&emsp;&emsp;**Linux 容器不是模拟一个完整的操作系统而是对进程进行隔离**。有了容器，就可以将软件运行所需的所有资源打包到一个隔离的容器中。**容器与虚拟机不同，不需要捆绑一整套操作系统，只需要软件工作所需的库资源和设置。**系统因此而变得高效轻量并保证部署在任何环境中的软件都能始终如一地运行。

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202204161641704.png)
![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202204161641775.png)

&emsp;&emsp;传统虚拟机技术是虚拟出一套硬件后，在其上运行一个完整操作系统，在该系统上再运行所需应用进程；

&emsp;&emsp;容器内的应用进程直接运行于宿主的内核，容器内没有自己的内核且也没有进行硬件虚拟。因此容器要比传统虚拟机更为轻便。

&emsp;&emsp;每个容器之间互相隔离，每个容器有自己的文件系统 ，容器之间进程不会相互影响，能区分计算资源。

&emsp;&emsp;传统的应用开发完成后，需要提供一堆安装程序和配置说明文档，安装部署后需根据配置文档进行繁杂的配置才能正常运行。Docker化之后只需要交付少量容器镜像文件，在正式生产环境加载镜像并运行即可，应用安装配置在镜像里已经内置好，大大节省部署配置和测试验证时间。

&emsp;&emsp;Docker是内核级虚拟化，其不像传统的虚拟化技术一样需要额外的Hypervisor支持，所以在一台物理机上可以运行很多个容器实例，可大大提升物理服务器的CPU和内存的利用率。

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202204161645612.png)

# 下载与安装

docker 官网：http://www.docker.com
Docker Hub 镜像官网: https://hub.docker.com/
[下载地址](https://docs.docker.com/get-docker/)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202204161737717.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202204161647764.png)

&emsp;&emsp;目前，CentOS 仅发行版本中的内核支持 Docker。Docker 运行在 CentOS 7 (64-bit)上，要求系统为 64 位、Linux 系统内核版本为 3.8 以上，这里选用 Centos7.x

&emsp;&emsp;查看自己的内核

&emsp;&emsp;uname命令用于打印当前系统相关信息（内核版本号、硬件架构、主机名称和操作系统类型等）。

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202204161650080.png)

[安装文档](https://docs.docker.com/engine/install/centos/)

## 安装

安装GCC

```shell
yum -y install gcc
yum -y install gcc-c++
```

1. 在新的主机上首次安装 Docker 引擎之前，您需要设置 Docker 存储库。之后，您可以从存储库安装和更新 Docker。

```shell
sudo yum install -y yum-utils
```

2. 设置 Docker 存储库，**国外镜像可能会挂使用阿里云的镜像**
```shell
sudo yum-config-manager \
    --add-repo \
    https://download.docker.com/linux/centos/docker-ce.repo
```

阿里云镜像：
```shell
sudo yum-config-manager --add-repo http://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo
```

3. 更新 yum 软件包索引

```shell
yum makecache fast
```

4. 安装 Docker

```shell
yum -y install docker-ce docker-ce-cli containerd.io
```

5. Start & stop Docker

```shell
sudo systemctl start docker
sudo systemctl stop docker
```

6. 测试

```shell
docker version
```

7. docker run hello-world 运行 hello-world 镜像，没有则需要去下载

```shell
docker run hello-world
```

8. 卸载

```shell
systemctl stop docker
yum remove docker-ce docker-ce-cli containerd.io
rm -rf /var/lib/docker
rm -rf /var/lib/containerd
```

9. 开机自启

```shell
systemctl enable docker
```

## 配置阿里云镜像加速

第一步，登陆阿里云，进入控制台

[进入控制台](https://homenew.console.aliyun.com/home/dashboard/ProductAndService)

第二步，选择左上角的菜单，选择容器镜像服务。 

第三步，选择镜像加速器菜单， 选择CentOS Tab标签页。

第四步，选择镜像加速器菜单， 选择CentOS Tab标签页。

输入sudo mkdir -p /etc/docker命令创建/etc/docker目录

输入以下命令，往配置文件里面加入阿里云的容器镜像地址

```shell
sudo tee /etc/docker/daemon.json <<-'EOF'
{
  "registry-mirrors": ["https://vaazkjk1.mirror.aliyuncs.com"]
}
EOF
```

输入sudo systemctl daemon-reload命令，重新加载配置文件。
输入sudo systemctl restart docker命令，重启Docker。

# 常用命令

启动 docker： `systemctl start docker`
停止 docker： `systemctl stop docker`
重启 docker： `systemctl restart docker`
查看 docker 状态： `systemctl status docker`
开机启动： `systemctl enable docker`
查看 docker 概要信息： `docker info`
查看 docker 总体帮助文档： `docker --help`
查看 docker 命令帮助文档： docker 具体命令 --help

## 列出本地主机上的镜像

```shell
docker images
```

- REPOSITORY：表示镜像的仓库源
- TAG：镜像的标签版本号
- IMAGE ID：镜像ID
- CREATED：镜像创建时间
- SIZE：镜像大小

&emsp;&emsp;同一仓库源可以有多个 TAG 版本，代表这个仓库源的不同个版本，使用 REPOSITORY:TAG 来定义不同的镜像。

&emsp;&emsp;如果你不指定一个镜像的版本标签，例如你只使用 ubuntu，docker 将默认使用 ubuntu:latest 镜像

OPTIONS说明：

- -a :列出本地所有的镜像（含历史映像层）
- -q :只显示镜像ID。

## 查看目前拥有的镜像


