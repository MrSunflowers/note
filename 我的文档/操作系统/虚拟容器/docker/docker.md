[TOC]
# Docker简介

&emsp;&emsp;Docker 相当于跑在 Linux 中的 VMware，一个 linux 中可以创建多个虚拟机，可以将开发软件和环境全部打包，安装的时候，把原始环境一模一样地复制过来，开发人员利用 Docker 可以消除协作编码时“在我的机器上可正常工作，换个地方确不能运行”的问题。

&emsp;&emsp;与传统虚拟机的区别在于它只保留了容器运行的必要环境，而系统运行所需的核心组件部分共用主机的一套，占用资源较少，启动速度快，所以 Linux 容器不是模拟一个完整的操作系统而是对进程进行隔离。

&emsp;&emsp;Docker 是基于 Go 语言实现的云开源项目。

&emsp;&emsp;用途：服务动态扩缩容，例如在10分钟内扩容1000个服务器节点

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

&emsp;&emsp;**传统虚拟机技术**是虚拟出一套硬件后，在其上运行一个**完整操作系统**，在该系统上再运行所需应用进程；

&emsp;&emsp;**容器**内的应用进程直接运行于宿主的内核，容器内没有自己的内核且也没有进行硬件虚拟。因此容器要比传统虚拟机更为轻便。

&emsp;&emsp;每个容器之间互相隔离，每个容器有自己的文件系统 ，容器之间进程不会相互影响，能区分计算资源。

&emsp;&emsp;传统的应用开发完成后，需要提供一堆安装程序和配置说明文档，安装部署后需根据配置文档进行繁杂的配置才能正常运行。Docker化之后只需要交付少量容器镜像文件，在正式生产环境加载镜像并运行即可，应用安装配置在镜像里已经内置好，大大节省部署配置和测试验证时间。

&emsp;&emsp;Docker是内核级虚拟化，其不像传统的虚拟化技术一样需要额外的 Hypervisor 支持，所以在一台物理机上可以运行很多个容器实例，可大大提升物理服务器的CPU和内存的利用率。

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202204161645612.png)

也可以理解为手机中安装的 APP ，镜像即应用市场的 app 程序，他们统一了应用的启动方式，常用命令等，例如查看容器中运行的应用的日志 `docker logs 容器ID`

## Docker 镜像

Docker 镜像（Image）就是一个**只读**的模板。镜像可以用来创建 Docker 容器，一个镜像可以创建很多容器。

它也相当于是一个root文件系统。比如官方镜像 centos:7 就包含了完整的一套 centos:7 最小系统的 root 文件系统。

## 仓库



仓库（Repository）是集中存放镜像文件的场所。

仓库分为**公开仓库**（Public）和**私有仓库**（Private）两种形式。

最大的公开仓库是 Docker Hub ( https://hub.docker.com/ )，

存放了数量庞大的镜像供用户下载。国内的公开仓库包括阿里云 、网易云等

# 下载与安装

docker 官网：http://www.docker.com
Docker Hub 镜像官网: https://hub.docker.com/
[下载地址](https://docs.docker.com/get-docker/)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202204161737717.png)

<img src="https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202204161647764.png"  />

&emsp;&emsp;目前，CentOS 仅发行版本中的内核支持 Docker。Docker 运行在 CentOS 7 (64-bit)上，要求系统为 64 位、Linux 系统内核版本为 3.8 以上，这里选用 Centos 7.x

&emsp;&emsp;查看自己的内核

&emsp;&emsp;`uname` 命令用于打印当前系统相关信息（内核版本号、硬件架构、主机名称和操作系统类型等）。

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202204161650080.png)

[安装文档](https://docs.docker.com/engine/install/centos/)

## 安装步骤

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
sudo yum install docker-ce docker-ce-cli containerd.io
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

输入 `sudo mkdir -p /etc/docker` 命令创建 `/etc/docker` 目录

输入以下命令，往配置文件里面加入阿里云的容器镜像地址

```shell
sudo tee /etc/docker/daemon.json <<-'EOF'
{
  "registry-mirrors": ["https://vaazkjk1.mirror.aliyuncs.com"]
}
EOF
```

输入 `sudo systemctl daemon-reload` 命令，重新加载配置文件。
输入 `sudo systemctl restart docker` 命令，重启 Docker。

# 常用命令

## 帮助和启动类命令

```bash
启动 docker： systemctl start docker
停止 docker： systemctl stop docker
重启 docker： systemctl restart docker
查看 docker 状态： systemctl status docker
开机启动： systemctl enable docker
查看 docker 概要信息： docker info
查看 docker 总体帮助文档： docker --help
查看 docker 命令帮助文档： docker 具体命令 --help
```

## 镜像命令

### 列出本地主机上的镜像

```shell
docker images
```

说明：

- REPOSITORY：表示镜像的仓库源
- TAG：镜像的标签版本号,同一仓库源可以有多个 TAG 版本，代表这个仓库源的不同个版本，使用 REPOSITORY:TAG 来定义不同的镜像。如果你不指定一个镜像的版本标签，例如你只使用 ubuntu，docker 将默认使用 `ubuntu:latest` 镜像。
- IMAGE ID：镜像ID
- CREATED：镜像创建时间
- SIZE：镜像大小

OPTIONS 可选参数说明：

- -a :列出本地所有的镜像（含历史映像层）
- -q :只显示镜像ID。

### 查找镜像

```bash
docker search [OPTIONS] 镜像名字
```

说明：

从 https://hub.docker.com 上查找镜像

OPTIONS 可选参数说明：

-  --limit 数量 : 只列出N个镜像，默认25个

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/spring/202204221426346.png)

### 下载镜像

```
docker pull 镜像名字:版本
```

### 查看镜像/容器/数据卷所占的空间

```
docker system df
```

### 删除镜像

```
docker rmi 某个XXX镜像名字ID
```

删除单个

```
docker rmi -f 镜像ID
```

删除多个

```
docker rmi -f 镜像名1:TAG 镜像名2:TAG
```

删除全部

```
docker rmi -f $(docker images -qa)
```

虚悬镜像

仓库名、标签都是 \<none> 的镜像，俗称虚悬镜像 dangling image，就是没有仓库名和标签的镜像，通常由docker构建错误导致。

## 容器命令

### 新建启动容器

```
docker run [OPTIONS] IMAGE [COMMAND] [ARG...]
```

OPTIONS 可选参数说明：

- --name="容器新名字"    为容器指定一个名称
- -d: 后台运行容器并返回容器ID，也即启动守护式容器(后台运行)
- -i：以交互模式运行容器，通常与 -t 同时使用
- -t：为容器重新分配一个伪输入**终端**，通常与 -i 同时使用，也即启动交互式容器(前台有伪终端，等待交互)
- -P: 随机端口映射，大写P
- -p: 指定端口映射，小写p

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/spring/202204221453576.png)

### 启动交互式容器

在容器内执行/bin/bash命令可以启动交互式容器

```
docker run -it centos /bin/bash
```

```
参数说明：

-i: 交互式操作。

-t: 终端。

centos : centos 镜像。

/bin/bash：放在镜像名后的是命令，这里我们希望有个交互式 Shell，因此用的是 /bin/bash。

要退出终端，直接输入 exit:

通过 run 进去的容器，使用 exit 退出，容器停止，使用 ctrl+p+q 退出，容器不停止
```

### 列出当前所有正在运行的容器

```
docker ps [OPTIONS]
```

OPTIONS说明（常用）：

- -a :列出当前所有正在运行的容器+历史上运行过的

- -l :显示最近创建的容器。

- -n：显示最近n个创建的容器。

- -q :静默模式，只显示容器编号。

### 停止容器

```
docker stop 容器ID或者容器名
```

### 启动容器

```
docker start 容器ID或者容器名
```

### 重启容器

```
docker restart 容器ID或者容器名
```

### 强制停止容器

```
docker kill 容器ID或容器名
```

### 删除已停止的容器

```
docker rm 容器ID
```

一次性删除多个容器实例

```
docker rm -f $(docker ps -a -q)
docker ps -a -q | xargs docker rm
```

### 启动守护式容器

在大部分的场景下，我们希望 docker 的服务是在后台运行的， 我们可以过 -d 指定容器的后台运行模式

```
docker run -d 容器名
```

然后 docker ps -a 进行查看, 会发现容器已经退出

很重要的要说明的一点: Docker 容器后台运行,就必须有一个前台进程.

容器运行的命令如果不是那些一直挂起的命令（比如运行 top，tail ），就是会自动退出的。

这个是docker的机制问题,比如你的 web 容器,我们以 nginx 为例，正常情况下,

我们配置启动服务只需要启动响应的 service 即可。例如 service nginx start

但是,这样做, nginx 为后台进程模式运行,就导致 docker 前台没有运行的应用,

这样的容器后台启动后,会立即自杀因为他觉得他没事可做了.

所以，最佳的解决方案是,将你要运行的程序以前台进程的形式运行，

常见就是命令行模式，表示我还有交互操作，别中断

### 查看容器日志

```
docker logs 容器ID
```

### 查看容器内运行的进程

```
docker top 容器ID
```

### 查看容器详细信息

```
docker inspect 容器ID
```

### 进入正在运行的容器并以命令行交互

```
docker exec -it 容器ID bash
```

重新进入

```
docker attach 容器ID
```

上述两个区别

- attach 直接进入容器启动命令的终端，不会启动新的进程 用 exit 退出，会导致容器的停止
- exec 是在容器中打开新的终端，并且可以启动新的进程 用 exit 退出，不会导致容器的停止

推荐使用 docker exec 命令，因为退出容器终端，不会导致容器的停止

 一般用 -d 后台启动的程序，再用 exec 进入对应容器实例

### 从容器内拷贝文件到主机上

容器→主机

```
docker cp  容器ID:容器内路径 目的主机路径
```

### 导入和导出容器

export 导出容器的内容留作为一个tar归档文件[对应import命令]

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/spring/202204221604057.png)

import 从tar包中的内容创建一个新的文件系统再导入为镜像[对应export]

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/spring/202204221604619.png)

```
docker export 容器ID > 文件名.tar
cat 文件名.tar | docker import - 镜像用户/镜像名:镜像版本号
```

## 一些常见命令

```
attach    Attach to a running container                 # 当前 shell 下 attach 连接指定运行镜像
build     Build an image from a Dockerfile              # 通过 Dockerfile 定制镜像
commit    Create a new image from a container changes   # 提交当前容器为新的镜像
cp        Copy files/folders from the containers filesystem to the host path   #从容器中拷贝指定文件或者目录到宿主机中
create    Create a new container                        # 创建一个新的容器，同 run，但不启动容器
diff      Inspect changes on a container's filesystem   # 查看 docker 容器变化
events    Get real time events from the server          # 从 docker 服务获取容器实时事件
exec      Run a command in an existing container        # 在已存在的容器上运行命令
export    Stream the contents of a container as a tar archive   # 导出容器的内容流作为一个 tar 归档文件[对应 import ]
history   Show the history of an image                  # 展示一个镜像形成历史
images    List images                                   # 列出系统当前镜像
import    Create a new filesystem image from the contents of a tarball # 从tar包中的内容创建一个新的文件系统映像[对应export]
info      Display system-wide information               # 显示系统相关信息
inspect   Return low-level information on a container   # 查看容器详细信息
kill      Kill a running container                      # kill 指定 docker 容器
load      Load an image from a tar archive              # 从一个 tar 包中加载一个镜像[对应 save]
login     Register or Login to the docker registry server    # 注册或者登陆一个 docker 源服务器
logout    Log out from a Docker registry server          # 从当前 Docker registry 退出
logs      Fetch the logs of a container                 # 输出当前容器日志信息
port      Lookup the public-facing port which is NAT-ed to PRIVATE_PORT    # 查看映射端口对应的容器内部源端口
pause     Pause all processes within a container        # 暂停容器
ps        List containers                               # 列出容器列表
pull      Pull an image or a repository from the docker registry server   # 从docker镜像源服务器拉取指定镜像或者库镜像
push      Push an image or a repository to the docker registry server    # 推送指定镜像或者库镜像至docker源服务器
restart   Restart a running container                   # 重启运行的容器
rm        Remove one or more containers                 # 移除一个或者多个容器
rmi       Remove one or more images       # 移除一个或多个镜像[无容器使用该镜像才可删除，否则需删除相关容器才可继续或 -f 强制删除]
run       Run a command in a new container              # 创建一个新的容器并运行一个命令
save      Save an image to a tar archive                # 保存一个镜像为一个 tar 包[对应 load]
search    Search for an image on the Docker Hub         # 在 docker hub 中搜索镜像
start     Start a stopped containers                    # 启动容器
stop      Stop a running containers                     # 停止容器
tag       Tag an image into a repository                # 给源中镜像打标签
top       Lookup the running processes of a container   # 查看容器中运行的进程信息
unpause   Unpause a paused container                    # 取消暂停容器
version   Show the docker version information           # 查看 docker 版本号
wait      Block until a container stops, then print its exit code   # 截取容器停止时的退出状态值
```

# Docker 镜像

## 分层的镜像

以我们的pull为例，在下载的过程中我们可以看到docker的镜像好像是在一层一层的在下载。

-  UnionFS（联合文件系统）

UnionFS（联合文件系统）：Union文件系统（UnionFS）是一种分层、轻量级并且高性能的文件系统，它支持对文件系统的修改作为一次提交来一层层的叠加，同时可以将不同目录挂载到同一个虚拟文件系统下(unite several directories into a single virtual filesystem)。Union 文件系统是 Docker 镜像的基础。镜像可以通过分层来进行继承，基于基础镜像（没有父镜像），可以制作各种具体的应用镜像。

特性：一次同时加载多个文件系统，但从外面看起来，只能看到一个文件系统，联合加载会把各层文件系统叠加起来，这样最终的文件系统会包含所有底层的文件和目录

## Docker镜像加载原理

docker 的镜像实际上由一层一层的文件系统组成，这种层级的文件系统 UnionFS。

bootfs(boot file system)主要包含bootloader和kernel, bootloader主要是引导加载kernel, Linux刚启动时会加载bootfs文件系统，在Docker镜像的最底层是引导文件系统bootfs。这一层与我们典型的Linux/Unix系统是一样的，包含boot加载器和内核。当boot加载完成之后整个内核就都在内存中了，此时内存的使用权已由bootfs转交给内核，此时系统也会卸载bootfs。

rootfs (root file system) ，在bootfs之上。包含的就是典型 Linux 系统中的 /dev, /proc, /bin, /etc 等标准目录和文件。rootfs就是各种不同的操作系统发行版，比如Ubuntu，Centos等等。

对于一个精简的OS，rootfs可以很小，只需要包括最基本的命令、工具和程序库就可以了，因为底层直接用Host的kernel，自己只需要提供 rootfs 就行了。由此可见对于不同的linux发行版, bootfs基本是一致的, rootfs会有差别, 因此不同的发行版可以公用bootfs。

镜像分层最大的一个好处就是共享资源，方便复制迁移，就是为了复用。

比如说有多个镜像都从相同的 base 镜像构建而来，那么 Docker Host 只需在磁盘上保存一份 base 镜像；

同时内存中也只需加载一份 base 镜像，就可以为所有容器服务了。而且镜像的每一层都可以被共享。

**Docker镜像层都是只读的，容器层是可写的**，当容器启动时，一个新的可写层被加载到镜像的顶部。 这一层通常被称作“容器层”，“容器层”之下的都叫“镜像层”。

当容器启动时，一个新的可写层被加载到镜像的顶部。这一层通常被称作“容器层”，“容器层”之下的都叫“镜像层”。

所有对容器的改动 - 无论添加、删除、还是修改文件都只会发生在容器层中。只有容器层是可写的，容器层下面的所有镜像层都是只读的。

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/spring/202204221613381.png)

## 制作自定义镜像

docker commit 命令提交容器副本使之成为一个新的镜像

```
docker commit -m="提交的描述信息" -a="作者" 容器ID 要创建的目标镜像名:[标签名]
```

OPTIONS说明：

-a :提交的镜像作者

-m :提交时的说明文字

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/spring/202204221626097.png)

Docker中的镜像分层，支持通过扩展现有镜像，创建新的镜像。类似Java继承于一个Base基础类，自己再按需扩展。

新镜像是从 base 镜像一层一层叠加生成的。每安装一个软件，就在现有镜像的基础上增加一层

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/spring/202204221626940.png)

## 将本地镜像推送到阿里云和私有云

见word文档

# Docker容器数据卷

卷就是目录或文件，存在于一个或多个容器中，由docker挂载到容器，但不属于联合文件系统，因此能够绕过Union File System提供一些用于持续存储或共享数据的特性：

卷的设计目的就是数据的持久化，完全独立于容器的生存周期，因此Docker不会在容器删除时删除其挂载的数据卷

Docker挂载主机目录访问如果出现cannot open directory .: Permission denied

解决办法：在挂载目录后多加一个--privileged=true参数即可

运行一个带有容器卷存储功能的容器实例

```
docker run -it --privileged=true -v /宿主机绝对路径目录:/容器内目录   镜像名
```

特点：

1：数据卷可在容器之间共享或重用数据

2：卷中的更改可以直接实时生效

3：数据卷中的更改不会包含在镜像的更新中

4：数据卷的生命周期一直持续到没有容器使用它为止

5： docker 修改，主机同步获得 

6 ：主机修改，docker 同步获得

7： docker 容器 stop，主机修改，docker 容器重启看数据是否同步。

读写规则映射添加说明

```
docker run -it --privileged=true -v /宿主机绝对路径目录:/容器内目录:rw 镜像名
```

默认为 rw 权限

设置容器实例内部被限制，只能读取不能写

```
docker run -it --privileged=true -v /宿主机绝对路径目录:/容器内目录:ro 镜像名
```

此时如果宿主机写入内容，可以同步给容器内，容器可以读取到。