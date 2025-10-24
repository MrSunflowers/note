[TOC]

# CentOS 6.x/7.x 对比

## 防火墙、内核版本及默认数据库
| 特性 | CentOS 6.x | CentOS 7.x |
|------|------------|------------|
| 防火墙 | iptables | firewalld |
| 内核版本 | 2.6.x-x | 3.10.x-x |
| 默认数据库 | MySQL | MariaDB |

## 系统配置对比
| 配置项 | CentOS 6.x | CentOS 7.x |
|--------|------------|------------|
| 时间同步 | `ntpq -p` | `chronyc sources` |
| 修改时区 | `/etc/sysconfig/clock` | `timedatectl set-timezone Asia/Shanghai` |
| 修改语言 | `/etc/sysconfig/i18n` | `localectl set-locale LANG=zh_CN.UTF-8` |
| 主机名配置文件 | `/etc/sysconfig/network` | `/etc/hostname` |
| 永久修改主机名 | 编辑配置文件 | `hostnamectl set-hostname atguigu.com` |






# 硬盘

一般来说，操作系统是要存储在硬盘中的，了解硬盘结构及其衍生出的文件系统是了解操作系统的基础。

## 机械硬盘存储结构

我们可以简单将机械硬盘简化为由盘片组成的存储介质，盘片是硬盘存储数据的载体，其使用铝合金或玻璃基底上涂覆很薄的磁性材料、保护材料和润滑材料等多种不同作用的材料层加工而成，其中磁性材料的物理性能和磁层机构直接影响着数据的存储密度和所存储数据的稳定性。

机械硬盘通常由一个或多个盘片构成，而且每个面都被划分为数目相等的磁道，并从外缘开始编号（即最边缘的磁道为 0 磁道，往里依次累加），磁头靠近主轴接触的表面，即线速度最小的地方，是一个特殊的区域，它不存放任何数据，称为启停区或着陆区（Landing Zone），启停区外就是数据区。如此磁盘中具有相同编号的磁道会形成一个圆柱，此圆柱称为磁盘的柱面。磁盘的柱面数与一个盘面上的磁道数是相等的。硬盘中有一个物理设备是专门用于寻找 0 磁道的，名为 0 磁道检测器，由它来完成硬盘的初始定位。

早期的硬盘在每次关机之前需要运行一个被称为Parking的程序，其作用是让磁头回到启停区。现代硬盘在设计上已摒弃了这个虽不复杂却很让人不愉快的小缺陷。硬盘不工作时，磁头停留在启停区，当需要从硬盘读写数据时，磁盘开始旋转。旋转速度达到额定的高速时，磁头就会因盘片旋转产生的气流而抬起，这时磁头才向盘片存放数据的区域移动。盘片旋转产生的气流相当强，足以使磁头托起，并与盘面保持一个微小的距离。这个距离越小，磁头读写数据的灵敏度就越高，当然对硬盘各部件的要求也越高。

![image-20250826213841301](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202508262138379.png)

逻辑上每个盘片的每一面都会被分为磁道、扇区、簇这几个虚拟的概念，不同的硬盘中盘片数不同，一个盘片有两面，这两面都能存储数据，每一面都会对应一个磁头，习惯上将盘面数计为磁头数，用来计算硬盘容量。

存储容量 = 磁头数 X 磁道（柱面）数 X 磁道包含扇区数 X 每扇区字节数

![img](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202508262115320.jpeg)

硬盘的每一个盘面有 300～1024 个磁道，新式大容量硬盘每面的磁道数更多。信息以脉冲串的形式记录在这些轨迹中，这些同心圆不是连续记录数据，而是被划分成一段段的圆弧，这些圆弧的角速度一样。由于径向长度不一样，所以，线速度也不一样，外圈的线速度较内圈的线速度大，即同样的转速下，外圈在同样时间段里，划过的圆弧长度要比内圈划过的圆弧长度大。每段圆弧叫做一个扇区，扇区从“1”开始编号，每个扇区中的数据作为一个单元同时读出或写入。一个标准的3.5in硬盘盘面通常有几百到几千条磁道。磁道是“看”不见的，只是盘面上以特殊形式磁化了的一些磁化区，在磁盘格式化时就已规划完毕。

所有盘面上的同一磁道构成一个圆柱，通常称做柱面（Cylinder），每个圆柱上的磁头由上而下从“0”开始编号。数据的读/写按柱面进行，即磁头读/写数据时首先在同一柱面内从“0”磁头开始进行操作，依次向下在同一柱面的不同盘面即磁头上进行操作，只在同一柱面所有的磁头全部读/写完毕后磁头才转移到下一柱面，因为选取磁头只需通过电子切换即可，而选取柱面则必须通过机械切换。电子切换相当快，比在机械上磁头向邻近磁道移动快得多，所以，数据的读/写按柱面进行，而不按盘面进行。也就是说，一个磁道写满数据后，就在同一柱面的下一个盘面来写，一个柱面写满后，才移到下一个扇区开始写数据。读数据也按照这种方式进行，这样就提高了硬盘的读/写效率。

系统将文件存储到磁盘上时，按柱面、磁头、扇区的方式进行，即最先是第1磁道的第一磁头下（也就是第1盘面的第一磁道）的所有扇区，然后，是同一柱面的下一磁头，……，一个柱面存储满后就推进到下一个柱面，直到把文件内容全部写入磁盘。系统也以相同的顺序读出数据。读出数据时通过告诉磁盘控制器要读出扇区所在的柱面号、磁头号和扇区号（物理地址的三个组成部分）进行。

其中每个扇区的大小通常是 512 字节（现代磁盘可能使用 4K 扇区，但兼容模式下仍显示为 512 字节）。将物理相邻的若干个扇区称为了一个簇。操作系统读写磁盘的基本单位是扇区，而文件系统的基本单位是簇(Cluster)。簇一般有这几类大小 4K，8K，16K，32K，64K等。

根据磁盘原理我们不难看出，在磁盘读取数据的过程中，真正读取数据的时间只占了很小一部分，而大部分时间花在了旋转延迟和寻道时间上，因此我们可以根据程序的局部性原理进行优化。

所谓的局部性原理分为时间和空间上的。由于程序是顺序执行的，因此当前数据段附近的数据有可能在接下来的时间被访问到。这就是所谓的空间局部性。而程序中还存在着循环，因此当前被访问的数据有可能在短时间内被再次访问，这就是所谓的时间局部性原理。

常见的优化手段包括

- 预读：每次读取数据的时间不仅仅读取所需要的数据，还将所请求数据附近的数据进行读取
- 延迟写：同样，根据时间局部性原理，最近被访问的数据有可能再次被访问，因此当数据更改之后不马上写回磁盘，而是继续放在内存中，以备接下来的请求读取或者修改，是减少磁盘IO的另一个有效手段，例如 mysql 的 buffer pool，当一个修改请求被commit之后，并不会立刻写回磁盘，而是将修改的页标记为“脏”，然后根据某种机制通过checkpoint或lazy writer写回磁盘。
- 根据磁盘原理不难看出，如果所请求的数据在磁盘物理磁道之间是连续的，那么会减少磁头的移动距离，从而减少了寻道时间。因此相关的数据放在连续的物理空间上会减少寻道时间。mysql 中，通过聚集索引使得数据根据主键在物理磁盘上连续，从而减少了寻道时间。

# 系统启动过程

操作系统一般存储在外存（如硬盘）中，CPU 无法直接对其取指令并执行。因此，如何将操作系统加载到内存并开始执行是计算机系统设计时需要考虑的一个问题。具体来说，操作系统启动需要解决以下两个问题：

1. 在操作系统正式工作之前，计算机应如何方式将操作系统加载到内存中并将系统控制权交给操作系统。
2. 在操作系统装载的过程中应如何确认操作系统存储在硬盘的哪个位置。

计算机在启动时从一个固定的位置读取一小段程序[通常称为BIOS(Basic Input/Output System，基本输入输出系统)]并运行，进而利用这一小段程序一步步加载操作系统。这个过程被称为系统引导(bootstrapping)过程。

BIOS 需要提前写入 CPU 可直接寻址的位置——内存。在计算机中，基本的内存由RAM(Random Access Memory，随机访问存储器)和ROM(Read-Only Memory，只读存储器)组成。BIOS的存储需要满足掉电不丢失的特性，因此需要用ROM的非易失性来持久保存BIOS。在不同体系架构的CPU上，不同的固化硬件逻辑不同，使得CPU上电启动后指定的首次取指的地址可能存在区别。例如，x86架构CPU约定当CPU启动后从地址CS:IP = 0xF000:0xFFF0加载指令执行。因此，存储BIOS的ROM应根据不同体系架构固化映射到相应的内存地址区域。

以基于x86架构的个人计算机为例，当用户启动计算机的电源时，计算机硬件会自动产生一个中断信号(CPU复位信号)。这个中断信号触发CPU初始化其指令寄存器(例如x86架构下的CS，IP寄存器)，使CPU从特定的内存位置(BIOS所在位置)开始执行。BIOS先完成加电自检(Power-On Self-Test，POST)和硬件初始化工作，再到硬盘中寻找一段为BootLoader的程序，由BootLoader进一步将操作系统加载到内存中。系统引导过程引入BootLoader加载操作系统，而不直接由BIOS将操作系统加载到内存，主要是考虑到以下问题：BIOS作为整个计算机系统启动时执行的第一段程序，应尽量保证其执行的正确性。若将过多的功能集成到BIOS中，会导致BIOS代码膨胀、复杂性增大而不方便维护。同时，BIOS固化在BIOS芯片中，正常情况下是不对其进行修改的，所以它也无法处理软件不断变化的情况。

BootLoader存储在硬盘的启动扇区，通常是第0条磁道第1个扇区。通过在启动扇区中写入特定的字符，可标识该硬盘是可引导的。例如，在MBR(Master Boot Record，主引导记录)格式下，若启动扇区的512B中最后两字节为0x55和0xAA，则标识该设备是可引导的。在确认该硬盘是可引导设备之后，BIOS程序就将BootLoader加载到内存的特定位置，进而跳转到BootLoader并开始执行，把CPU控制权移交给BootLoader。

BootLoader的基本功能包括：初始化硬件设备、为操作系统准备RAM内存，再从硬盘的特定扇区(通常第2个扇区)读入操作系统内核，进而将CPU控制权移交给操作系统内核。

## Linux 系统启动过程

在 Linux 中，在找到可引导设备后，BIOS 会从其中读取 MBR 分区表并移交 CPU 控制权。

在 MBR 中，前 446B 是启动代码，启动代码在取得CPU控制权后，负责检查分区表是否正确，随后将CPU控制权交给x86架构计算机Linux操作系统所用的主流BootLoader——GRUB（GRand Unified BootLoader，全面统一引导加载程序）；其后的64B为分区表，分区表用于硬盘分区，使得每个分区都可安装一个操作系统镜像；最后2B用于标识MBR（若值为0xAA55，则说明该扇区是MBR）。

为了节省空间，操作系统内核通常以压缩形式存储。因此，在指定的内核被加载到内存中并开始执行后，它必须首先从文件的压缩版本中解压，才能继续进行其他操作。内核在开始运行后，将初始化内部的数据结构，检测系统内存在的各个硬件并激活相应的驱动程序以及挂载根文件系统。

### Linux 运行级别

经过以上步骤，应用程序的基本运行环境已经建立，随后第一个运行的应用程序便是 init 程序，该程序将依据 `/etc/inittab` 文件内容进行初始化工作。

`/etc/inittab` 文件最主要的作用就是设定 Linux 的运行等级。例如设定格式为 `:id:5:initdefault:`，表明 Linux 将运行在等级 5 上，即启动的为常见带图形界面的 Linux 操作系统。Linux 的运行等级的关系如下：

- 0 表示关机；
- 1 表示单用户模式；
- 2 表示无网络支持的多用户模式；
- 3 表示有网络支持的多用户模式；
- 4 为保留模式，暂未使用；
- 5 表示有网络支持和 X Window 支持的多用户模式；
- 6 表示重新引导系统，即系统重启。

在 Linux 系统中可以使用 `runlevel` 命令来查看系统的运行级别，命令如下:

```bash
[root@localhost ~]# runlevel
N 3
```

在这个命令的结果中，“N 3” 中的 N 代表进入这个级别之前，上一个级别是什么，3 代表当前级别。 “N” 就是 None 的意思，也就是说系统是开机直接进入的 3 运行级别 ，没有上一个运行级别。 那如果是从图形界面切换到字符界面的话，再查看运行级别，就应该是这样的:

```bash
[root@localhost ~]# runlevel
5 3
```

手工改变当前的运行级别使用 init 命令 (注意着不是 init 进程) 即可，命令如下:

```bash
[root@localhost ~]# init 5
```

进入图形界面，当然要已经安装了图形界面才可以

```bash
[root@localhost ~]# init 0
```

关机

```bash
[root@localhost ~]# init 6
```

重启

不过要注意使用 init 命令关机和重启动 ，并不是太安全 ，容易造成数据丢失 。所以推荐大家还是使用 shutdown 命令进行关机和重启

### 系统默认运行级别

`/etc/inittab` 文件内容

```bash
[root@localhost ~]# vim /etc/inittab

inittab is only used by upstart for the default runlevel.

ADDING OTHER CONFIGURATION HERE WILL HAVE NO EFFECT ON YOUR SYSTEM.

System initialization is started by /etc/init/rcS.conf

#系统会先调用/etc/init/rcS.conf

Individual runlevels are started by /etc/init/rc.conf

#再调用/etc/init/rc.conf，在不同的运行级别启动不同的服务

Ctrl-Alt-Delete is handled by /etc/init/control-alt-delete.conf

#通过这个配置文件判断Ctrl+Alt+Delete 热启动键是否可用

Terminal gettys are handled by /etc/init/tty.conf and /etc/init/serial.conf，

with configuration in /etc/sysconfig/init.

#判断系统可以启动的本地终端数量，及终端的基本设置(如颜色)

For information on how to write upstart event handlers， or how

upstart works， see init(5)， init(8)， and initctl(8).

Default runlevel. The runlevels used are:

0 - halt (Do NOT set initdefault to this)

1 - Single user mode

2 - Multiuser， without NFS (The same as 3， if you do not have networking)

3 - Full multiuser mode

4 - unused

5 - X11

6 - reboot (Do NOT set initdefault to this)

#就是刚刚的0-6 的运行级别的说明

id:3:initdefault:

#这就是系统的默认运行级别，也就是系统开机后直接进入哪个运行级别
```

**注意** 这里的默认运行级别只能写 3 或 5，其他的级别要不就是关机重启，要不就是保留或单用户， 都不能作为系统默认运行级别的。

在设定运行等级后，Linux 操作系统执行的第一个用户程序是 /etc/rc.d/rc.sysinit 脚本程序，它的功能包括设定环境变量（PATH）、网络配置、启动交换（swap）分区、设定 /proc 等。最后执行login程序，用户输入账号和密码登录系统。

### 在 systemd 系统下的变化

在现代 Linux 系统的发展中，现代 Linux 发行版（如 RHEL 7/CentOS 7 及以后、Ubuntu 15.04 及以后、Debian 8 及以后等）已经转向使用 systemd 作为初始化系统。

- `systemd`使用 **“目标 (target)”** 来替代“运行级别”的概念。
- 为了保持兼容性，`systemd`也提供了与传统运行级别对应的目标单元（`.target`），它们本质上是符号链接。

| 运行级别 | systemd 目标                | 用途                                |
| :------- | :-------------------------- | :---------------------------------- |
| `0`      | `poweroff.target`           | 关闭系统                            |
| `1`      | `rescue.target`             | 救援模式/单用户模式                 |
| `2`      | `multi-user.target`(非标准) | 多用户，无图形界面 (通常未直接使用) |
| `3`      | `multi-user.target`         | **多用户，无图形界面 (字符界面)**   |
| `4`      | `multi-user.target`(非标准) | 多用户，无图形界面 (通常未直接使用) |
| `5`      | `graphical.target`          | **多用户，带图形界面**              |
| `6`      | `reboot.target`             | 重启系统                            |

在 systemd 系统上，`runlevel`命令仍然可用，但返回的信息可能意义不大，因为它只是为了兼容而存在。

**更推荐的方式**是使用 `systemctl` 命令来查看当前的默认目标（即等效的运行级别）：

查看当前“运行级别：

```bash
systemctl get-default
```

要查看系统当前所处的目标，可以查看 /lib/systemd/system/目录下对应目标的链接情况，或者使用：

```bash
systemctl list-units --type=target
```

切换“运行级别”：

虽然 init 3 或 init 5 这样的命令通常仍然有效（systemd 会将其转换为对应的目标操作），但 推荐使用 systemctl 命令：

```bash
# 切换到字符界面 (运行级别 3)
sudo systemctl isolate multi-user.target

# 切换到图形界面 (运行级别 5)
sudo systemctl isolate graphical.target

# 重启
sudo systemctl reboot

# 关机
sudo systemctl poweroff
```

设置默认启动目标（相当于修改 /etc/inittab 中的 initdefault）：

```bash
sudo systemctl set-default multi-user.target # 默认启动到字符界面
sudo systemctl set-default graphical.target # 默认启动到图形界面
```

## 软件开机自启动

** `/etc/rc.d/rc.local` 文件 **

这个配置文件会在用户登陆之前读取，是在所有系统服务启动之后执行的最后一个脚本，这个文件中写入什么命令，在每次系统启动时都会执行一次。也就是说，我如果有任何需要在系统启动就运行的工作，只需要写入 `/etc/rc.d/rc.local` 这个配置文件即可。这个文件内容如下:

```bash
[root@localhost ~]# vi /etc/rc.d/rc.local

#!/bin/sh

This script will be executed after all the other init scripts.You can put your own initialization stuff in here if you don't want to do the full Sys V style init stuff.

touch /var/lock/subsys/local
#默认会 touch 这个文件，每次系统启动时 touch 这个文件，这个文件的修改时间就是系统的启动时间了。
/etc/rc.d/init.d/httpd start
#如果写入 RPM 包安装的 apache 的启动命令，apache 服务就会开机时自动启动了。
```

该文件在 CentOS 7 中不再发挥其作用，该文件虽然存在，但其注释内容包含了非常关键的信息，文件开头明确指出 “THIS FILE IS ADDED FOR COMPATIBILITY PURPOSES”，这意味着它只是为了向后兼容旧的习惯或脚本而保留的。

注释强烈建议用户创建自己的 systemd services 或 udev rules 来在启动时运行脚本，而不是使用这个文件。这是官方的最佳实践推荐。

最关键的一点是，“this script will NOT be run after all other services”。在旧系统中，rc.local 是在所有系统服务启动之后执行的最后一个脚本。但在 systemd 的并行启动机制下，它的执行时机不再有这种保证，可能会在其他服务启动之前或之中执行，这可能会引发依赖性问题。

注释还提醒用户，必须手动执行 `chmod +x /etc/rc.d/rc.local` 命令给这个文件添加可执行权限，否则它不会在启动时运行。

CentOS 7 默认使用 systemd 作为初始化系统。绝大多数通过 yum 安装的软件包都会自动配置好 systemd 服务单元。

使用 `systemctl enable` 命令。这个命令并不会现在启动服务，而是创建一个符号链接，告诉系统在下次启动时自动运行该服务。

```bash
systemctl enable httpd
```

成功提示: `Created symlink from /etc/systemd/system/multi-user.target.wants/httpd.service to /usr/lib/systemd/system/httpd.service.`

这表示 `systemd` 已经配置好在多用户模式下自动启动 `httpd`。

可以执行以下命令来确认服务是否已启用和运行：

```bash
systemctl is-enabled httpd
```

显示 enabled则表示已成功设置开机自启。

可以执行以下命令来检查当前运行状态

```bash
systemctl status httpd
```

显示 active (running) 则表示服务正在运行。

之后，可以使用以下命令来管理服务：

- systemctl start httpd - 启动服务
- systemctl stop httpd - 停止服务
- systemctl restart httpd - 重启服务
- systemctl reload httpd - 重新加载配置（不中断服务）
- systemctl disable httpd - 禁用开机自启动

对于 httpd 或任何其他通过官方仓库安装的软件，请始终使用 `systemctl enable <服务名>` 来设置开机自启动。这是 CentOS 7/RHEL 7 及更新版本的正确做法。

当您有一个自己编写的脚本、或从源码编译安装的软件（它们通常不会通过 yum 安装，所以没有现成的 .service 文件），您应该为其创建一个自定义的 systemd 服务单元。

步骤如下

1. 创建服务文件

服务文件通常放在 /etc/systemd/system/ 目录下。例如，为您的一个监控脚本 my_monitor.sh 创建服务：

```bash
sudo vi /etc/systemd/system/my-monitor.service
```

2. 编写服务单元内容

一个最基本的服务文件内容如下

```bash
[Unit]
Description=My Custom Monitoring Script # 服务描述
After=network.target # 指定在什么目标之后启动，表示网络就绪后启动

[Service]
ExecStart=/usr/local/bin/my_monitor.sh # 要执行的核心命令或脚本的绝对路径
Type=simple # 服务类型，简单运行为主进程
User=nobody # 以什么用户身份运行（可选，出于安全考虑）
Restart=on-failure # 失败时自动重启

[Install]
WantedBy=multi-user.target # 指定在哪个系统目标下启用，表示系统进入多用户模式时启动这个服务
```

3. 重新加载 systemd 配置

```bash
sudo systemctl daemon-reload
```

4. 启用并启动服务

```bash
# 设置开机自启
sudo systemctl enable my-monitor.service

# 立即启动服务
sudo systemctl start my-monitor.service

# 检查状态
sudo systemctl status my-monitor.service
```

## 启动引导程序(待完善)

Lilo 作为 Linux 的早期版本的引导程序，现已经不是很常见了，目前主流的引导程序是 grub，grub 相比 Lilo 来讲有很多优势，主要有:

- 支持更多的文件系统;
- grub 的主程序可以直接在文件系统中查找内核文件;
- 在系统启动时，可以利用grub 的交互界面编辑和修改启动选项;
- 可以动态的修改 grub 的配置文件，这样在修改配置文件之后不需要重新安装grub，而只需要重新启动就可以生效了。

### `/boot/grub` 目录

grub 的作用有以下几个:

- 第一是加载操作系统的内核;
- 第二是拥有一个可以让用户选择的菜单，来选择到底启动哪个系统;
- 第三还可以调用其他的启动引导程序，来实现多系统引导。

grub 的配置文件主要是放置在 `/boot/grub/` 目录中的，我们来看看这个目录下到底有哪些文件吧:

```bash
[root@localhost ~]# cd /boot/grub/
[root@localhost grub]# ll -h
总用量 274K
-rw-r--r--. 1 root root 63 4 月 10 21:49 device.map
#grub 中硬盘的设备文件名与系统的设备文件名的对应文件
-rw-r--r--. 1 root root 14K 4 月 10 21:49 e2fs_stage1_5
#ext2/ext3 文件系统的stage 1.5 文件
-rw-r--r--. 1 root root 13K 4 月 10 21:49 fat_stage1_5
#FAT 文件系统的stage 1.5 文件
-rw-r--r--. 1 root root 12K 4 月 10 21:49 ffs_stage1_5
#FFS 文件系统的stage 1.5 文件
-rw-------. 1 root root 737 4 月 10 21:49 grub.conf
#grub 的配置文件
-rw-r--r--. 1 root root 12K 4 月 10 21:49 iso9660_stage1_5
#iso9660 文件系统的Stage 1.5 文件
-rw-r--r--. 1 root root 13K 4 月 10 21:49 jfs_stage1_5
#jfs 文件系统的Stage 1.5 文件
lrwxrwxrwx. 1 root root 11 4 月 10 21:49 menu.lst -> ./grub.conf
#grub 的配置文件。和grub.conf 是软链接，所以两个文件修改哪个都可以
-rw-r--r--. 1 root root 12K 4 月 10 21:49 minix_stage1_5
#minix 文件系统的Stage 1.5 文件
-rw-r--r--. 1 root root 15K 4 月 10 21:49 reiserfs_stage1_5
#reiserfs 文件系统的Stage 1.5 文件
-rw-r--r--. 1 root root 1.4K 11 月 15 2010 splash.xpm.gz
#系统启动时，grub 程序的背景图像
-rw-r--r--. 1 root root 512 4 月 10 21:49 stage1
#安装到引导扇区中的stage1 的备份文件
-rw-r--r--. 1 root root 124K 4 月 10 21:49 stage2
#stage2 的备份文件
-rw-r--r--. 1 root root 12K 4 月 10 21:49 ufs2_stage1_5
#UFS 文件系统的Stage 1.5 文件
-rw-r--r--. 1 root root 12K 4 月 10 21:49 vstafs_stage1_5
#vstafs 文件系统的Stage 1.5 文件
-rw-r--r--. 1 root root 14K 4 月 10 21:49 xfs_stage1_5
#xfs 文件系统的Stage 1.5 文件
```

其实这个目录中主要就是 grub 的配置文件和各种文件系统的 stage1.5 文件。不过 grub 的配置文件有两个 /boot/grub/grub.conf 和 /boot/grub/menu.lst，这两个配置文件是软链接，所以修改哪一个都可以。

### Grub 的配置文件

在 grub 中分区的表示方法

| 硬盘           | 分区           | Linux中设备文件名 | Grub中设备文件名 |
| -------------- | -------------- | ----------------- | ---------------- |
| 第一块SCSI硬盘 | 第一个主分区   | /dev/sda1         | hd (0，0)        |
| 第一块SCSI硬盘 | 第二个主分区   | /dev/sda2         | hd(0，1)         |
| 第一块SCSI硬盘 | 扩展分区       | /dev/sda3         | hd (0，2)        |
| 第一块SCSI硬盘 | 第一个逻辑分区 | /dev/sda5         | hd(0，4)         |
| 第二块SCSI硬盘 | 第一个主分区   | /dev/sdb1         | hd(1，0)         |
| 第二块SCSI硬盘 | 第二个主分区   | /dev/sdb2         | hd(1，1)         |
| 第二块SCSI硬盘 | 扩展分区       | /dev/sdb3         | hd(1，2)         |
| 第二块SCSI硬盘 | 第一个逻辑分区 | /dev/sdb5         | hd(1，4)         |
|                |                |                   |                  |


```bash
[root@localhost ~]# vi /boot/grub/grub.conf
default=0
timeout=5
splashimage=(hd0，0)/grub/splash.xpm.gz
hiddenmenu
#以上为grub 整体设置


title CentOS (2.6.32-279.el6.i686)
	root (hd0，0)
	kernel		 /vmlinuz-2.6.32-279.el6.i686 ro
root=UUID=b9a7a1a8-767f-4a87-8a2b-a535edb362c9 rd_NO_LUKS KEYBOARDTYPE=pc KEYTABLE=usrd_NO_MD crashkernel=auto LANG=zh_CN.UTF-8 rd_NO_LVM rd_NO_DM rhgb quiet
	initrd /initramfs-2.6.32-279.el6.i686.img
```

其中

default=0

表示默认启动第一个系统。也就是如果在等待时间结束后，用户没有选择进入哪一个系统，那么系统会默认进入第一个系统。如果有多系统并存，那么每个系统都会有自己的 title 字段，如果想要默认进入第二个系统，这里就可以设为 default=1。

timeout=5

表示等待时间，默认是 5 秒。也就是进入系统时，如果 5 秒内用户没有按下任意键，那么系统会进入 default 字段定义的系统。当然可以手工修改这个等待时间，如果 timeout=0 则不会等待直接进入系统，timeout=-1 则是一直等待用户输入，而不会自动进入系统。

splashimage=(hd0，0)/grub/splash.xpm.gz

这里是指定 grub 启动时的背景图像文件的保存位置的。记得 CentOS 6.x 启动时后台的蓝色图像吧，就是这个文件的作用。不过这个文件具体在哪里啊?已经说过了hd(0，0)代表第一个硬盘的第一个分区，而系统安装时 /boot 分区就是第一个分区，所以这个背景图像的实际位置就是 /boot/grub/splash.xpm.gz。

hiddenmenu

隐藏菜单。启动时默认只能看到读秒，而不能看到菜单，如果想要看到菜单需要按任意键。如果注释了这句话，那么启动时就能直接看到菜单了。

以上就是grub 的整体设置，下面我们介绍CentOS 系统的启动设置:

title CentOS (2.6.32-279.el6.i686)

title 就是标题的意思，也就是说在 title 后面写入的是什么，那么系统启动时在 grub 的启动菜单中看到的就是什么。

root (hd0，0)

是指启动程序的保存分区。这里要注意啊，这个 root 并不是管理员。在系统中，/boot 分区是独立划分的，而且设备文件名为/dev/sda1，所以在grub 中，就被描述为hd(0，0)。

kernel

/vmlinuz-2.6.32-279.el6.i686 ro root=UUID=b9a7a1a8-767f-4a87-8a2b-a535edb362c9 rd_NO_LUKS KEYBOARDTYPE=pc KEYTABLE=us rd_NO_MD crashkernel=auto LANG=zh_CN.UTF-8 rd_NO_LVM rd_NO_DM rhgb quiet

- /vmlinuz-2.6.32-279.el6.i686:指定了内核文件的位置，这里的/是指/boot 分区。
- ro:启动时以只读方式挂载根文件系统，这是为了不让启动过程影响磁盘内的文件系统。
- root=UUID=b9a7a1a8-767f-4a87-8a2b-a535edb362c9:指定根文件系统的所在位置。这里和以前的 Linux 版本不太一样了，不再是通过分区的设备文件名或卷标号来指定，而是通过分区的 UUID 来进行指定。那么如何查询分区的 UUID 号呢?方法有很多种，最简单的办法就是查询 /etc/fstab 文件，命令如下:

```bash
[root@localhost ~]# cat /etc/fstab | grep "/ "

UUID=b9a7a1a8-767f-4a87-8a2b-a535edb362c9 / ext4 defaults 1 1
```

可以看到 “/” 分区的 UUID 和 kernel 行中的 UUID 是匹配的。注意一下， grep 后的“/ ”，在/后是有空格的。 

- rd_NO_LUKS:禁用LUKS，LUKS 用于给磁盘加密
- rd_NO_MD:禁用软RAID。 
- rd_NO_DM:禁用硬RAID。 
- rd_NO_LVM:禁用LVM。以上禁用都只是在启动过程中禁用，是为了加速系统启动的。 KEYBOARDTYPE=pc KEYTABLE=us:键盘类型。  
- crashkernel=auto:自动为crashkernel 预留内存。 
- LANG=zh_CN.UTF-8:语言环境  
- rhgb:(redhat graphics boot)用图片来代替启动过程中的文字信息。启动完成之后可以使用dmesg 命令来查看这些文字信息。 
- quiet:隐藏启动信息，只显示重要信息。

initrd /initramfs-2.6.32-279.el6.i686.img:指定了initramfs 内存文件系统镜像文件 的所在位置。

### grub 加密

```bash
[root@localhost ~]# grub-md5-crypt
Password:
Retype password:
#输入两次密码
Y84LB1$8tMY2PibScmuOCc8z8U35/
#生成加密密码字串
```

这样就可以生成加密密码字串，这个字串是采用md5 加密的，就是你的密码经 md5 编码之后的。我们会利用这个加密密码字串来加密 grub 配置文件。

grub 菜单整体加密

如果只是加密单个启动菜单，grub 的编辑模式是不能锁定的，还是可以按“e”键进入编辑模式。 而且进入编辑模式后，是可以删除password 字段的，再按“b”(boot 启动)键就可以不用密码直 接进入系统。这时就需要给grub 菜单整体加密了，整体加密后，如果想进入grub 编辑界面必须输入 正确的密码。加密方法其实只是把password 字段换个位置而已，具体方法如下:

```bash
[root@localhost ~]# vi /boot/grub/grub.conf
default=0
timeout=5
password --md5  Y84LB1$8tMY2PibScmuOCc8z8U35/ #password 选项放在整体设置处。
splashimage=(hd0，0)/grub/splash.xpm.gz
```

但是这样加密，启动 CentOS 时，是不需要密码就能正常启动的。那我如果既需要 grub 的整体加密，又需要系统启动时输入正确的密码。那应该怎么做呢?很简单，方法如下:

```bash
default=0
timeout=5
password --md5 Y84LB1$8tMY2PibScmuOCc8z8U35/
splashimage=(hd0，0)/grub/splash.xpm.gz
hiddenmenu
title CentOS (2.6.32-279.el6.i686)
	lock
#在title 字段，加入 lock。代表锁死，如果不输入正确的 grub 密码也不能启动
```

## 系统修复模式

### 单用户模式

我们先来看看单用户模式是怎么使用的吧。Linux 的单用户模式有些类似 Windows 的安全模式，只启动最少的程序用于系统修复。在单用户模式(运行级别为1)中，Linux 引导进入根 shell，网络被禁用，只有少数进程运行。单用户模式可以用来修改文件系统损坏､还原配置文件､移动用户数据 等。

单用户模式常见的错误修复

#### 遗忘 root 密码

这是管理员最容易犯的错误，那么应该如何修复呢?当然是使用单用户模式进行修复了，进入单用户模式最大的特点就是不需要输入用户名和密码就能登录。既然已经登录了单用户模式，那么直接 给root 用户设定新密码即可。命令如下:

```bash
[root@localhost /]# passwd root
```

#### 修改系统默认运行级别

如果我们把系统的默认运行级别修改错误，比如改为了0 或6，系统就不能正常启动了。这时也 可以利用单用户模式进行修复，只要直接修改默认运行级别配置文件/etc/inittab，把系统默认运行 级别修改回来即可。命令如下:

```bash
[root@localhost /]# vi /etc/inittab
id:3:initdefault:
#把默认运行级别修改为3 或5。注意系统的默认运行级别只能使用 3 或 5
```

绝大多数系统错误都可以通过单用户模式进行修复，理论上是只要能够进入单用户模式，那么系统错误就可以被单用户模式修复。当然判断系统到底是哪里出现了问题，是需要不断的经验积累的。

### 光盘修复模式

如何进入光盘修复模式呢?首先你需要有系统光盘，或系统修复光盘。我们这里只需要把CentOS 6.x 的第一张光盘放入光驱，然后重启系统。修改BIOS 的启动顺序，让系统从光盘启动。

光盘修复模式常见的错误修复

#### 重要系统文件丢失，导致系统无法启动

如果系统中的重要系统文件丢失，当然会导致系统无法正常启动。这时也可以利用光盘修复模式 修复。我们假设把/etc/inittab 文件丢失了，我们通过系统启动过程知道这个文件是定义系统默认运 行级别的，如果丢失了这个文件，系统当然不能正常启动，这时就需要进入光盘修复模式中了。然后需要利用chroot 命令。命令格式如下:

```bash
[root@localhost ~]# chroot 目录名
```

chroot 命令的作用是“change root directory”改变系统根目录的意思。也就是可以把根目录 暂时移动到某个目录当中。我们是通过光盘启动的光盘修复模式，所以我们现在所在的根目录不是真 正的系统根目录，而是光盘的模拟根目录。系统根目录被当成外来设备放在了/mnt/sysimage/目录中。 这时就需要chroot 命令把我们现在的所在目录移动成真正的系统根目录。命令如下:

```bash
bash-4.1# chroot /mnt/sysimage
```

这条命令执行之后，当前的根目录就已经是真正的系统根目录了。如果系统有任何错误都可以直 接修复。比如/etc/inittab 文件丢失了。这时如果我们曾经备份过系统重要文件，只需要把备份文件 重新复制到/etc/目录下即可。如果没有备份的文件，就需要从rpm 包中提取inittab 文件，然后复制了。具体命令如下:

```bash
bash-4.1# chroot /mnt/sysimage
#改变主目录
sh-4.1# cd /root
#进入root 目录。因为默认进入的是/目录，如果不进入root，一会提取的inittab 文件会
#报错
sh-4.1# rpm -qf /etc/inittab
initscripts-9.03.31-2.el6.centos.i686
#查询下/etc/inittab 文件属于哪个包。如果系统中文件丢失不能查询，需要通过其他Linux
#系统查询
sh-4.1# mkdir /mnt/cdrom
#建立挂载点
sh-4.1# mount /dev/sr0 /mnt/cdrom
#挂载光盘
sh-4.1# rpm2cpio /mnt/cdrom/Packages/initscripts-9.03.31-2.el6.centos.i686.rpm | cpio -idv ./etc/inittab
#提取inittab 文件到当前目录
sh-4.1# cp etc/inittab /etc/inittab
#复制inittab 文件到指定位置
```

注意此命令执行时不能将文件直接恢复至/etc 目录，只能提取到当前目录下，且恢复的文件名称 所在路径要写完整的绝对路径。提取文件成功后，将其复制到根分区所在的/mnt/sysimage 目录下相 应位置即可。

# 分区

## 系统分区规则

系统分区一般遵循以下原则

- `/boot`(系统启动分区，推荐 1 GB)，其中存放系统启动所必须的执行文件，且在系统启动过程中，会在其中产生一些临时文件，启动分区一般应单独挂载。如和其他分区混用，当分区空间被写满时，无法容纳系统启动时产生的临时文件，则 linux 无法启动。此时即使想清理磁盘的文件也做不到了。
- `/`(根分区)，Linux 要求根分区必须单独挂载，空间一般没有限制，一般控制在 20-50GB
- swap 分区(交换分区)，交换分区可类比于虚拟内存，当内存空间不足时，使用该空间代替内存使用，由于交换分区是磁盘分区，用于扩展内存空间，并不表现在文件系统中，没有独立的文件夹，Linux 推荐根据真实内存大小进行分配。如果真实内存小于 4GB，交换分区推荐为真实内存的两倍；如果真实内存大于 4GB，交换分区推荐与真实内存一致；
- `/home` (常用于文件服务器，如 FTP 文件服务器，不是必须单独挂载)
- `/www` (常用于Web服务器，不是必须单独挂载)

## 传统分区

在进行操作系统安装之前首先需要对磁盘进行分区，无论是任何操作系统，这一步骤都是必须的。

磁盘分区表是硬盘上用于管理分区信息的核心数据结构，**它独立于操作系统而存在**，用于定义硬盘空间如何被划分为不同的逻辑部分。通常存储在硬盘的第一个物理扇区，这个扇区被称为 主引导记录（Master Boot Record， MBR） 或（对于 UEFI/GPT 分区）GUID 分区表头（GUID Partition Table Header）。

主流的两种分区表形式为 MBR（主引导记录）和 GPT（全局唯一标识分区表）。

### MBR 分区表（Master Boot Record）

MBR 是传统的分区方案，自 1983 年沿用至今，其核心特点如下：

1. 容量限制：最大支持 2TB（Terabytes）硬盘容量。若硬盘超过2TB，MBR无法识别超出部分，导致剩余空间浪费。
2. 分区数量：最多支持 4 个主分区。若需更多分区，需将其中一个主分区转为扩展分区，并在其内创建逻辑分区（逻辑分区数量理论上无上限，但受操作系统限制）。
3. 数据安全性：分区表存储在硬盘第一个扇区（512字节），无冗余备份。若该扇区损坏（如病毒攻击），可能导致全部分区信息丢失。
4. 兼容性：兼容所有操作系统（包括 Windows XP 及更旧版本）和传统BIOS启动模式。

由于历史原因，现在的操作系统还是有一大部分使用 MBR 分区方式，其最大特点即存在逻辑分区特性

在最初设计 MBR 分区方式时，其最多仅支持 4 个分区。后续随着科技发展，4 个分区的上限已不够使用，此时即引入了扩展分区的概念，即将原本 4 个分区中的一个作为扩展分区，在扩展分区中再进行分区拆分，以达到突破分区上限的目的，这也导致了在 Linux 系统中，主分区以 1，2，3，4 来表示，拓展分区中的逻辑分区以 5 为最小分区编号。

在博文“[Linux启动过程分析](http://blog.chinaunix.net/uid-23069658-id-3142047.html)”中提到过MBR，它是存在于硬盘的0柱面，0磁头，1扇区里，占512字节的空间。这512字节里包含了主引导程序Bootloader和磁盘分区表DPT。其中Bootloader占446字节，分区表占64字节，一个分区要占用16字节，64字节的分区表只能被划分4个分区，这也就是目前我们的硬盘最多只能支持4个分区记录的原因。

即，如果你将硬盘分成4个主分区的话，必须确保所有的磁盘空间都被使用了(这不是废话么)，一般情况下我们都是划分一个主分区加一个扩展分区，然后在扩展分区里再继续划分逻辑分区。当然，逻辑分区表也需要分区表，它是存在于扩展分区的第一个扇区里，所以逻辑分区的个数最多也只能有512/16=32个，并不是想分多少个逻辑分区都可以。

### GPT 分区表（GUID Partition Table）

GPT是现代分区方案，针对大容量硬盘设计，主要优势包括：

1. 容量支持：理论最大支持 9.4ZB（Zettabytes）（1ZB=1024EB，1EB=1024PB，1PB=1024TB），实际可满足未来数十年存储需求。
2. 分区数量：理论上分区数无上限，但 Windows 系统限制为128个主分区，无需扩展分区即可直接管理。
3. 数据安全性：分区表在磁盘开头（LBA 1）和结尾各存一份，并支持CRC32校验。若主分区表损坏，可自动从备份恢复。
4. 启动方式：需搭配UEFI固件启动操作系统（如Windows 8+/Linux新内核），传统 BIOS 无法引导 GPT 系统盘。

### 手工分区

对传统分区表来说，有两种手工分区的方式，分别为 fdisk 和 parted 命令。

其核心区别如下

| 特性                | `fdisk`                                 | `parted`                                      |
| :------------------ | :-------------------------------------- | :-------------------------------------------- |
| **主要设计目标**    | 传统 MBR 分区操作                       | 现代分区管理，支持 GPT 和 MBR                 |
| **分区表支持**      | MBR (DOS) 为主，新版支持 GPT            | **GPT 和 MBR** (原生支持 GPT)                 |
| **大磁盘支持**      | 传统版本有限 (2TB)，新版 GPT 支持大磁盘 | **原生支持 >2TB 磁盘** (GPT)                  |
| **交互模式**        | **强交互式** (命令提示符)               | 弱交互式 (命令驱动) 或 非交互脚本化           |
| **文件系统操作**    | 基本无 (仅分区)                         | **可创建文件系统** (`mkpart`的 `fs-type`参数) |
| **调整分区大小**    | 非常有限或危险                          | **相对安全地调整分区大小** (`resizepart`)     |
| **单位**            | 柱面 (默认)，可切换扇区                 | **智能单位** (MB， GB， %， s 等)                |
| **输出信息**        | 较简洁                                  | **更详细** (包含文件系统类型等)               |
| **易用性 (初学者)** | 命令较多需记忆                          | 命令较少，但参数复杂                          |
| **脚本化/自动化**   | 较难                                    | **更适合** (`-s`选项)                         |
| **底层操作**        | 直接操作分区表                          | 更高抽象层                                    |

1. **分区表支持 (GPT vs MBR):**

- **`fdisk`:** 传统上主要用于操作 **MBR (Master Boot Record)** 分区表。虽然现代版本的 `fdisk`(如 `fdisk`from util-linux 2.23+) 也支持 **GPT (GUID Partition Table)**，但其对 GPT 的支持有时被认为不如 `parted`或专门的 `gdisk`工具成熟或直观。对于大于 2TB 的磁盘，使用 `fdisk`操作 MBR 会遇到限制。
- **`parted`:** **原生且完善地支持 GPT 和 MBR** 两种分区表类型。它是处理 **大于 2TB 磁盘** 和 **GPT 分区表** 的**首选工具**。

2. **交互模式:**

- **`fdisk`:** 采用**强交互式命令行模式**。启动后 (`fdisk /dev/sdX`)，会进入一个专属的命令提示符环境 (如 `Command (m for help):`)。用户需要在这个环境中输入单字母命令 (如 `n`创建新分区， `d`删除分区， `p`打印分区表， `w`写入并退出， `q`不保存退出等) 来操作分区表。操作完成后需要显式写入 (`w`) 才会生效。
- **`parted`:** 采用**命令驱动模式**。虽然也可以启动一个交互式 shell (`parted /dev/sdX`)，然后输入命令 (如 `print`， `mkpart`， `rm`， `resizepart`， `quit`)，但它更像是在一个普通的 shell 里运行命令。更重要的是，`parted`**非常适合在脚本中非交互式使用** (例如 `parted /dev/sdX mklabel gpt mkpart primary ext4 1MiB 100%`)。操作通常是“即时”的，某些危险操作 (如 `rm`) 可能不需要额外的确认步骤 (务必小心！)。

3. **功能范围:**

- **`fdisk`:** 核心功能集中在**创建、删除、修改分区类型、查看分区表**等基本分区操作上。它**不直接处理文件系统**。你需要分区后，再使用 `mkfs`等命令在分区上创建文件系统。
- **`parted`:** 功能更广泛。除了基本的分区操作 (`mkpart`， `rm`， `name`， `set`设置标志如 `boot`)，它还能：
  - **创建文件系统：** `mkpart`命令可以直接指定 `fs-type`参数 (如 `ext4`， `xfs`， `fat32`， `ntfs`， `btrfs`)，在创建分区的同时（或之后）创建文件系统（底层调用 `mkfs`）。`(mkfs`命令在 `parted`中已被弃用，推荐在分区后用 `mkfs`工具）。
  - **调整分区大小：** `resizepart`命令可以**相对安全地调整分区大小**（通常需要文件系统本身支持在线调整，如 `resize2fs`for ext4）。这是 `fdisk`难以安全完成的复杂操作。
  - **更详细的信息：** `print`命令输出通常比 `fdisk -l`更详细，包括文件系统类型（如果已创建）、分区标志、更人性化的尺寸单位等。
  - **操作分区标志：** `set`命令可以方便地设置/取消设置分区标志（如 `boot`， `esp`， `lvm`， `raid`等）。

4. **单位和易用性:**

- **`fdisk`:** 默认使用**柱面 (cylinders)** 作为单位，这对现代磁盘来说是一个抽象且不直观的概念。虽然可以切换为扇区 (sectors)，但计算起始和结束位置需要用户自己进行扇区数的换算。
- **`parted`:** 默认使用**更人性化的单位**，如 **MB， GB， %** (百分比)，或者精确的扇区 (`s`)。例如，`mkpart primary ext4 1MiB 100%`表示从 1MiB 开始到磁盘末尾创建一个主分区。这大大简化了操作，减少了计算错误。`print`的输出也默认使用易读的单位。

5. **底层操作与风险:**

- 两者都是直接操作磁盘分区表的底层工具。**任何写入操作 (`fdisk`的 `w`， `parted`的大多数修改命令) 都有导致数据丢失的风险。** 操作前务必**备份重要数据**并**确认目标磁盘正确无误**。
- `parted`的某些操作（如 `resizepart`）虽然设计上更安全，但**调整包含数据的分区大小始终是高风险操作**，强烈建议先备份。


总结与选择建议

- **使用 `fdisk`当：**
  - 你正在操作传统的 **MBR 分区表** 磁盘 (小于 2TB)。
  - 你习惯或需要其**强交互式命令提示符模式**。
  - 只需要进行**基本的分区创建、删除、查看**操作。
  - 系统环境较旧或 `parted`不可用。
- **使用 `parted`当：**
  - 磁盘 **大于 2TB** (必须使用 GPT)。
  - 你明确需要使用 **GPT 分区表**。
  - 你需要**调整现有分区的大小** (`resizepart`)。
  - 你希望在分区时**直接指定文件系统类型** (虽然之后仍需 `mkfs`)。
  - 你需要**非交互式/脚本化**地进行分区操作。
  - 你更喜欢使用 **MB， GB 等直观单位** 而不是柱面或手动计算扇区。
  - 你需要查看**更详细的分区信息**或设置**分区标志**。

**对于现代 Linux 系统和新硬件（尤其是大容量 SSD/HDD），`parted`通常是更强大、更灵活、更推荐的工具，特别是因为它对 GPT 的原生支持。** 然而，`fdisk`在操作小型 MBR 磁盘或某些特定场景下仍有其价值。`gdisk`则是专门为 GPT 设计的、类似于 `fdisk`交互模式的工具，是另一个处理 GPT 磁盘的好选择。

### fdisk 命令手工分区

使用 fdisk 命令进行手工分区时，其默认使用 **扇区（Sectors）** 作为单位，柱面在现代磁盘大小不固定，但fdisk仍默认使用它，实际分区时应该切换到扇区模式操作，扇区是磁盘的最小存储单元，通常为 **512 字节**（现代磁盘可能使用 4K 扇区，但兼容模式下仍显示为 512 字节）。

#### 查看磁盘分区信息

```bash
# 查看所有磁盘分区
fdisk -l
# 查看指定磁盘（如 /dev/sda）
fdisk -l /dev/sda
```

#### 对未分配的磁盘进行分区

使用 fdisk 命令进入交互模式

```bash
# 指定磁盘进行分区，由于当前磁盘还未进行分区，所以其分区还没有分区号。（如 /dev/sda）
fdisk /dev/sda
```

fdisk 交互命令说明

| 命令 | 说明 |
| :--- | :--- |
| a | 设置可引导标记 |
| b | 编辑 bsd 磁盘标签 |
| c | 设置 DOS 操作系统兼容标记 |
| d | 删除一个分区 |
| l | 显示已知的文件系统类型。82 为 Linux swap 分区，83 为 Linux 分区 |
| m | 显示帮助菜单 |
| n | 新建分区 |
| o | 建立空白 DOS 分区表 |
| p | 显示分区列表 |
| q | 不保存退出 |
| s | 新建空白 SUN 磁盘标签 |
| t | 改变一个分区的系统 ID |
| u | 改变显示记录单位 |
| v | 验证分区表 |
| w | 保存退出 |
| x | 附加功能（仅专家） |

#### 分区步骤示例

步骤 1：创建主分区

```
Command (m for help): n       # 新建分区
Partition type:
   p   primary (0 primary， 0 extended， 4 free)
   e   extended
Select (default p): p         # 选择主分区
Partition number (1-4， default 1): 1  # 分区号
First sector (2048-20971519， default 2048): 1  # 起始柱面（实际使用建议回车用默认值）
Last sector， +sectors or +size{K，M，G} (1-20971519...): +100M  # 分区大小
```

步骤 2：创建扩展分区

```
Command (m for help): n
Select (default p): e         # 选择扩展分区
Partition number (2-4， default 2): 2  # 分区号
First sector (1024-20971519， default 1024): 124  # 起始柱面
Last sector...: 1024          # 分配所有剩余空间（柱面数）
```

步骤 3：创建逻辑分区

```
Command (m for help): n
Select (default p): l         # 创建逻辑分区（仅在扩展分区内可用）
First sector (125-1024， default 125): 124  # 起始柱面
Last sector...: +100M         # 分区大小
```

步骤 4：保存退出

```
Command (m for help): w       # 写入分区表并退出
```

柱面选择示意图

```
┌───────────────────────────────────────┐
│ 磁盘柱面空间示意图 │
├───────────┬───────────┬──────────────┤
│ 主分区1 │ 扩展分区2 │ 未分配空间 │
│ (1-123) │ (124-1024)│ (1025-end) │
│ ├───────────┼──────────────┤
│ │ 逻辑分区1 │ 逻辑分区2... │
│ │ (124-224) │ (225-324) │
└───────────┴───────────┴──────────────┘
```

有时因为系统的分区表正忙，则需要重新启动系统之后才能使新的分区表生效。

```
Command (m for help):w     <-保存退出
The partition table has been altered!

Calling ioctl() to re-read partition table.

WARNING: Re-reading the partition table failed with error 16.



Device or resource busy.
The kernel still uses the old table.The new table will be used at the next reboot.       <-要求重启动，才能格式化
Syncing disks.
```

#### partprobe 命令：重载分区表

强制重读所有分区文件，重新挂载分区文件内所有分区。这不是分区必须的命令，如果没有提示重启，可以不执行，也可以重启系统

```
(Warning: Unable to open /dev/hdc read-write (Read-only file system). /dev/hdc hasbeen opened read-only.
```

上述警告 : 光盘只读挂载，不是错误，不用紧张

如果这个命令不存在请安装 parted-2.1-18.e16.i686 这个软件包

### parted 命令手工分区

不过 parted 命令自身分区时默认格式化为 ext2 文件系统，但可以识别 ext4 文件系统。如果需要 ext4 文件系统，建议使用 `mkfs` 命令手动格式化。不过这没有太多的影响，因为我们可以先分区再用 mkfs 进行格式化嘛！

进入交互模式

```bash
parted /dev/sdb
```

#### parted 交互命令功能说明

| 命令                                      | 说明                                                        |
| :---------------------------------------- | :---------------------------------------------------------- |
| **帮助与信息**                            |                                                             |
| `help [COMMAND]`                          | 显示所有命令或指定命令的帮助信息。                          |
| `print [devices\|free\|list，all\|NUMBER]` | 显示分区表、活动设备、空闲空间、所有指定分区信息。          |
| `version`                                 | 显示 `parted`版本信息。                                     |
| `quit`                                    | 退出 `parted`程序。                                         |
| **磁盘与设备操作**                        |                                                             |
| `mklabel， mktable LABEL-TYPE`             | 创建新的磁盘标签（即分区表类型，如 `gpt`， `msdos`）。       |
| `select DEVICE`                           | 选择需要编辑的设备（如 `/dev/sdb`）。                       |
| `unit UNIT`                               | 设置输入和显示时使用的默认单位（如 `MB`， `GB`， `%`， `s`）。 |
| **分区管理**                              |                                                             |
| `mkpart PART-TYPE [FS-TYPE] START END`    | 创建一个新分区。                                            |
| `mkpartfs PART-TYPE FS-TYPE START END`    | **（已弃用）** 创建分区并同时建立文件系统。                 |
| `rm NUMBER`                               | 删除指定编号的分区。                                        |
| `resize NUMBER START END`                 | 修改指定编号分区的大小。                                    |
| `move NUMBER START END`                   | 移动指定编号的分区。                                        |
| `name NUMBER NAME`                        | 给指定编号的分区命名（GPT分区表支持）。                     |
| `rescue START END`                        | 尝试在指定范围内修复（救援）丢失的分区。                    |
| **文件系统与标志**                        |                                                             |
| `mkfs NUMBER FS-TYPE`                     | 在指定编号的分区上建立文件系统（格式化）。                  |
| `check NUMBER`                            | 对指定编号的分区做一次简单的文件系统检测。                  |
| `cp [FROM-DEVICE] FROM-NUMBER TO-NUMBER`  | 将文件系统从一个分区复制到另一个分区。                      |
| `set NUMBER FLAG STATE`                   | 改变指定编号分区的标记（如设置 `boot`标志）。               |
| `toggle [NUMBER [FLAG]]`                  | 切换分区表的状态。                                          |

parted 交互模式核心命令详解

1. 基础信息查看与管理

- print (或 p): 这是最常用的命令，用于打印显示当前磁盘的分区表。您可以指定参数查看更具体的信息，如 print all 显示所有设备，print free 显示空闲空间等。
- select DEVICE: 切换需要操作的另一块物理设备，例如 select /dev/sdc。
- help [COMMAND]: 查看所有命令或某个特定命令的帮助信息。

2. 分区表操作

- mklabel， mktable LABEL-TYPE: 为磁盘创建新的分区表类型（即磁盘标签）。这是对磁盘进行分区前必须的第一步操作。常见的 LABEL-TYPE 有：
    ◦ gpt: 适用于现代硬件和大容量硬盘（>2TB）。
    ◦ msdos: 传统的 MBR 分区表。

3. 分区操作（核心功能）

- mkpart PART-TYPE [FS-TYPE] START END: 创建一个新分区。这是最关键的创建命令。
    ◦   PART-TYPE: 分区类型，如 primary（主分区）、extended（扩展分区）、logical（逻辑分区），对于 GPT 分区则简单使用 primary 即可。
    ◦   [FS-TYPE]: 此处请注意：这里指定的文件系统类型（如 ext4）仅在分区表上做一个标识，并不会真正格式化分区。真正的格式化需要使用 mkfs 命令。
    ◦   START END: 分区的起始和结束位置，可以使用多种单位，如 1MiB， 100GiB 或百分比 100%。建议从 1MiB 开始以保证最佳性能（4K对齐）。
- rm NUMBER: 删除指定编号的分区。
- resize NUMBER START END: 调整指定编号分区的大小。这是一个高风险操作，务必谨慎使用。

4. 文件系统与分区标志操作

- mkfs NUMBER FS-TYPE: 在指定分区上创建文件系统（格式化）。例如 mkfs 1 ext4 将第一个分区格式化为 ext4。注意：此操作会摧毁分区上所有现有数据！
- set NUMBER FLAG STATE: 设置分区的标志。例如，为引导分区设置 boot 标志：set 1 boot on。其他常见标志包括 esp (EFI系统分区)， lvm， raid等。

5. 高级功能

- move NUMBER START END: 移动分区（调整位置）。
- name NUMBER NAME: 为分区设置一个名称（GPT分区表支持）。
- rescue START END: 尝试在磁盘的某个范围内 rescue（救援）丢失的分区。
- unit UNIT: 设置输入和显示时使用的单位，如 unit GB 设置为吉字节，unit s 设置为扇区。

总结与最佳实践建议

1.  操作流程：对一块新磁盘的标准操作流程是 select -> mklabel -> mkpart -> print (确认) -> quit -> 使用 mkfs 命令格式化。
2.  重要提示：上文中提到的 mkpartfs 命令（创建分区并直接格式化）在较新的 parted 版本中已被标记为废弃。官方推荐的做法是分开操作：先用 mkpart 创建分区，再用 mkfs 系列命令进行格式化。这样更安全、更灵活。
3.  风险意识：parted 命令的操作通常是直接生效的（没有单独的“写入”步骤，如 fdisk 的 w），在 rm， mklabel 等破坏性操作前一定要再三确认，以免造成数据丢失。

#### 查看磁盘分区信息

在 `parted` 交互环境中执行 `print` 命令：

```bash
(parted) print
Model: VMware， VMware Virtual S (scsi)
Disk /dev/sdb: 21.5GB
Sector size (logical/physical): 512B/512B
Partition Table: msdos

Number  Start     End       Size      Type      File system  Flags
 1      32.3kB    5379MB    5379MB    primary
 2      5379MB    21.5GB    16.1GB    extended
 5      5379MB    7534MB    2155MB    logical   ext4
 6      7534MB    9689MB    2155MB    logical   ext4
```

磁盘整体信息

- **Model**: `VMware， VMware Virtual S (scsi)`
  - 硬盘参数，显示为 VMware 虚拟机中的虚拟 SCSI 硬盘。
- **Disk**: `/dev/sdb: 21.5GB`
  - 磁盘设备名为 `/dev/sdb`，总容量为 21.5 GB。
- **Sector size (logical/physical)**: `512B/512B`
  - 该磁盘的逻辑扇区和物理扇区大小均为 512 字节。
- **Partition Table**: `msdos`
  - 分区表类型为 MSDOS，即传统的 MBR 分区表。

分区信息详解

| 列名 | 说明 |
| :--- | :--- |
| **Number** | 分区编号 |
| **Start** | 分区起始位置（使用 Byte、MB 等直观单位，而非柱面） |
| **End** | 分区结束位置 |
| **Size** | 分区大小 |
| **Type** | 分区类型（主分区 primary、扩展分区 extended、逻辑分区 logical） |
| **File system** | 文件系统类型（`parted` 可识别 ext4 等格式，但历史版本可能无法直接创建） |
| **Flags** | 分区标志（如 boot 等，本例中未设置任何标志） |

1.  该磁盘 (`/dev/sdb`) 采用 **MBR** 分区方案。
2.  分区结构为：
    -   1 个主分区 (`1`)，**未格式化**（无文件系统）。
    -   1 个扩展分区 (`2`)，作为容器，**本身不能被格式化**。
    -   2 个逻辑分区 (`5`， `6`) 位于扩展分区内，均已被格式化为 **ext4** 文件系统。
3.  `parted` 的 `print` 指令能有效识别已存在的 ext4 文件系统，但其格式化功能可能受限。


#### 将磁盘修改为 GPT 分区表

1. 执行修改分区表命令
```bash
(parted) mklabel gpt
```

2. 系统警告信息

```bash
警告:正在使用/dev/sdb上的分区。
```

*说明：由于 `/dev/sdb` 分区已经挂载，所以产生此警告。*

3. 确认选项

```bash
忽略/Ignore/放弃/Cancel? ignore
```

*用户输入 `ignore` 忽略报错。*

4. 严重数据丢失警告

```bash
警告:The existing disk label on /dev/sdb will be destroyed and all data on this disk will be lost. Do you want to continue?
是/Yes/否/No? yes
```
*用户输入 `yes` 确认继续操作，此操作将使原有分区及数据全部消失。*

5. 内核警告信息

```bash
警告:WARNING:the kernel failed to re-read the partition table on /dev/sdb(设备或资源忙). As a result， it may not reflect all of your changes until after reboot.
```
*提示：需要重启系统后更改才能完全生效。*

6. 查看修改后的分区表
```bash
(parted) print
Model: VMware， VMware Virtual S (scsi)
Disk /dev/sdb: 21.5GB
Sector size (logical/physical): 512B/512B
Partition Table: gpt
Number  Start  End  Size  File system  Name  标志
```
*确认结果：分区表已成功更改为 GPT 类型，所有原有分区均已消失。*

重要注意事项

1.  **数据丢失风险**：修改分区表会导致原有分区和分区中的所有数据完全消失，操作前务必确认已备份重要数据。

2.  **重启生效**：此操作需要重启系统后才能完全生效。

3.  **操作目的**：转换分区表的主要目的是为了支持大于 2TB 的分区。如果分区没有大于 2TB，此步骤不是必须的。

4.  **系统配置清理**：
    - **必须**在重启前删除 `/etc/fstab` 文件中与原有分区相关的挂载配置
    - 否则系统启动时会出现报错，可能导致无法正常启动

5.  **操作前提**：确保目标磁盘 (`/dev/sdb`) 没有正在被系统使用或挂载，否则会出现"设备或资源忙"的警告。

#### 新建分区

由于修改了分区表（转换为 GPT），`/dev/sdb` 硬盘中的所有数据已消失，因此需要重新分区。默认文件系统类型为 ext2。

1. 创建分区

使用 `mkpart` 命令通过交互方式创建分区：

```bash
(parted) mkpart
分区名称？[]？disk1      # 输入分区名称 disk1
文件系统类型？[ext2]？   # 直接回车，使用默认文件系统类型 ext2
起始点？1MB             # 分区起始位置设置为 1MB
结束点？5GB             # 分区结束位置设置为 5GB
分区完成
```

2. 查看分区信息

使用 `print` 命令查看当前分区信息：

```bash
(parted) print
Model: VMware， VMware Virtual S (scsi)
Disk /dev/sdb: 21.5GB
Sector size (logical/physical): 512B/512B
Partition Table: gpt
```

分区信息详情

执行 `print` 命令后，显示的分区信息如下表：

| Number | Start   | End     | Size    | File system | Name   | 标志                 |
| :----- | :------ | :------ | :------ | :---------- | :----- | :------------------- |
| 1      | 1049kB  | 5000MB  | 4999MB  | ext2 | disk1  |         |

与 MBR 分区表相比，GPT 分区表的分区信息显示有以下不同：

- **少了 Type 字段**：不再显示分区类型（如主分区、扩展分区、逻辑分区）。
- **多了 Name 字段**：可以为分区指定名称。
- **分区类型概念变化**：在 GPT 分区表中，不再使用 MBR 的主分区、扩展分区和逻辑分区概念，所有分区都是平等的，不再受分区类型限制。

使用 `mkfs -t ext4 /dev/sdb1`：将 `/dev/sdb1` 分区格式化为 ext4 文件系统

#### 调整分区大小

`parted` 命令的一个重要优势是能够调整分区大小。

- **对比 Windows 系统**：在 Windows 中调整分区通常需要转换为动态磁盘或依赖第三方工具（如硬盘分区魔术师）
- **对比 Linux 其他方案**：Linux 中 LVM 和 RAID 也支持分区调整（可视为动态磁盘方法），但使用 `parted` 命令调整分区更加简单直接

> **警告：数据安全操作前提**
> 
> - `parted` 调整**已经挂载使用**的分区时，**不会影响分区中的数据**（数据不会丢失）
> - **但是必须首先卸载分区**，然后再调整分区大小，否则数据可能会出现严重问题
> - 要调整大小的分区**必须已经建立了文件系统（格式化）**，否则会报错

```bash
(parted) resize
分区编号？ 1                ← 指定要修改的分区编号
起始点？ [1049kB]? 1MB      ← 设置分区起始位置
结束点？ [5000MB]? 6GB      ← 设置分区结束位置
```

查看调整结果
```bash
(parted) print             ← 查看分区信息
```

执行 `print` 命令后显示的信息：
```
Model: VMware， VMware Virtual S (scsi)
Disk /dev/sdb: 21.5GB
Sector size (logical/physical): 512B/512B
Partition Table: gpt
Number  Start   End     Size    File system  Name  标志
1       1049kB  6000MB  5999MB  ext2         disk1  ← 分区大小已改变
```

从输出信息可以看到：
- 分区编号 1 的结束位置已从原来的 5000MB 调整为 6000MB
- 分区大小从原来的 4999MB 调整为 5999MB
- 文件系统类型为 ext2
- 分区名称为 disk1
- 分区调整操作成功完成

#### 删除分区

1. 执行删除分区命令
```bash
(parted) rm
```

2. 指定要删除的分区编号
```bash
分区编号？ 1
```

*输入要删除的分区编号（此处为分区1）*

3. 验证删除结果
```bash
(parted) print
```
*使用 print 命令查看当前分区表状态*

执行 `print` 命令后显示的信息：

```bash
Model: VMware， VMware Virtual S (scsi)
Disk /dev/sdb: 21.5GB
Sector size (logical/physical): 512B/512B
Partition Table: gpt
Number  Start  End  Size  File system  Name  标志       ←分区消失
```

*从输出信息可以看到分区1已被成功删除*

#### 重要注意事项

> **parted 与 fdisk 操作机制对比**：
> 
> - **parted** 中所有的操作都是**立即生效**，没有"保存生效"的概念
> - 这与 **fdisk** 交互命令明显不同（fdisk 需要执行 `w` 命令才会写入更改）
> - 因此使用 parted 时，所做的所有操作需要**倍加小心**

### 分配 swap 分区

1. 分区并修改为 Swap 分区 ID

使用 `fdisk` 命令对磁盘进行分区操作：

对 /dev/sdb 磁盘进行分区操作

```bash
[root@localhost ~]# fdisk /dev/sdb
```

修改分区的系统 ID

```bash
Command (m for help): t
```

只有一个分区，所以不用选择分区了

```bash
Selected partition 1
```

改为 swap 的 ID（82 对应 Linux swap / Solaris）

```bash
Hex code (type L to list codes): 82
```

系统提示分区类型已成功修改为 Linux swap

```bash
Changed system type of partition 1 to 82 (Linux swap / Solaris)
```


2. 格式化 Swap 分区

使用 `mkswap` 命令格式化指定的分区为交换分区：

```bash
[root@localhost ~]# mkswap /dev/sdb1
Setting up swapspace version 1， size = 522076 KiB
no label， UUID=c3351dc3-f403-419a-9666-c24615e170fb
```

成功创建交换分区，显示版本、大小和UUID信息

3. 查看内存使用情况

在使用 swap 分区之前，可以先使用 `free` 命令查看当前内存使用情况：

```bash
[root@localhost ~]# free
               total       used       free     shared    buffers     cached
Mem:         1030796     130792     900004          0      15292      55420
-/+ buffers/cache:        60080     970716
Swap:        2047992          0    2047992
```
显示内存总量、已用内存、空闲内存及交换分区信息

4. 启用 Swap 分区

使用 `swapon` 命令启用指定的交换分区：

```bash
[root@localhost ~]# swapon /dev/sdb1
```
启用 /dev/sdb1 作为交换分区

5. 配置开机自动挂载

为了让 swap 分区在开机后自动挂载，需要在 `/etc/fstab` 文件中添加以下内容：

```
/dev/sdb1 swap swap defaults 0 0
```

加入新 swap 分区的相关内容，这里直接使用分区设备文件名，也可以使用 UUID 号进行标识

注意事项

1. **分区类型**：Swap 分区的系统 ID 必须设置为 `82`（Linux swap / Solaris）
2. **格式化必要**：在启用 swap 分区前必须先使用 `mkswap` 命令进行格式化
3. **启用方式**：使用 `swapon` 命令激活交换分区，使用 `swapoff` 命令可停用
4. **持久化配置**：通过 `/etc/fstab` 文件配置可实现开机自动挂载
5. **标识方式**：可以使用设备文件名（如 `/dev/sdb1`）或 UUID 号来标识交换分区

## LVM 逻辑卷管理（待完善）

传统分区分区完成后不支持动态扩展，这与操作系统无关，在 Windows 系统中虽然存在可以更改分区的第三方软件，但分区本身并不提供动态更改分区的方法，第三方软件的做法是不合法的强行更改分区表来更改分区，这可能导致分区损坏，导致数据丢失，是不可取的。

linux 引入 LVM 逻辑卷来实现分区拓展的目的，记住，**任何方式的分区管理都不应对分区进行缩减**，虽然 LVM 可以支持分区压缩，但这可能会导致数据丢失。


# 文件系统

文件系统用于组织和管理磁盘上存储的数据，其必须可通过文件路径的方式，完成对数据信息的存取及加工，要想实现这一功能，其还必须拥有高效的数据索引检索能力。

文件系统一般由操作系统实现，不同的操作系统对文件系统的实现相对有所不同。

| 操作系统             | 主流文件系统                                     | 特点与考量                                                   |
| :------------------- |:-------------------------------------------| :----------------------------------------------------------- |
| **Windows**          | **NTFS**                                   | 为PC和服务器设计，强调**安全性**（完整的ACL权限控制）、**可靠性**（日志功能）和**功能性**（支持加密、压缩、磁盘配额）。**ReFS** 是其新一代文件系统，专注于超大规模和数据中心场景。 |
| **Linux**            | **ext4(CentOS 6.x)**， **XFS(CentOS 7.x)**， **Btrfs** | 选择多样，生态丰富。**ext4** 是稳健通用的选择；**XFS** 在处理大文件和并发I/O上性能极佳，常用于企业级应用；**Btrfs** 则专注于高级功能，如快照、子卷、数据校验和RAID。 |
| **macOS**            | **APFS**                                   | 为**闪存/固态硬盘（SSD）** 优化设计，核心特性是**写时复制（COW）**、**空间共享**（为多个卷动态分配容量）和**极快的目录快照**（这是Time Machine备份速度快的基础）。 |
| **移动端 (Android)** | **F2FS**                                   | 由三星开发，专为**NAND闪存**特性设计，能有效减少读写放大，延长闪存寿命，提升读写性能。 |
| **跨平台/外部存储**  | **exFAT**                                  | 由微软开发，旨在解决FAT32不支持大文件（>4GB）和大分区（>2TB）的问题，同时保持其**轻量化和高兼容性**的特点，是U盘和SD卡的理想选择。 |

在 Linux 操作系统中，以 EXT4 最为经典。EXT3 是 EXT2 的升级版（2001年发布），曾为早期 Linux 发行版（如 Ubuntu 10.04 前、CentOS 5）的默认文件系统，EXT4（第四代扩展文件系统）是绝大多数主流 Linux 发行版的默认选择。

## EXT4 文件系统

由于磁盘中存储的数据众多，EXT4 文件系统将存储空间分为 Inode 存储区和数据块存储区，其中 Inode 区为数据块索引存储区，主要存储数据块的索引信息，即数据块在磁盘的位置信息。

数据块存储区存储文件的实际内容，数据块的大小是固定的，在创建 EXT4 文件系统时可以通过 -b 选项设定（通常为 1KB， 2KB， 4KB）

假设数据块大小被规定为 4KB ，现在有一个 6KB 的文件，其占用的数据块实际上是 2 块，2 块数据块的总大小为 8KB，而文件仅占用 6 KB，值得注意的是，当文件未完全使用数据块的空间时，剩余空间也无法分配给其他文件使用，即**数据块是磁盘存储的最小单位**。

即使这样，当我们访问一个文件的时候，也必须访问两次磁盘：首先读取索引数据，然后读取文件数据块，在两次磁盘访问中需要花费大量时间寻址，这就成为文件系统性能的瓶颈。

为了解决这个问题，EXT4 系统将磁盘分区进一步被分为多个块组，每个块组由一个或多个连续的柱面组成。每个块组都有描述本组磁盘块的状态信息的超级块，同时还有各自的索引节点和空闲块表。这样，文件系统就可以把索引节点相关的文件数据和索引节点放在同一柱面内，从而减少柱面寻址时间，提高效率。

在磁盘格式化时，其 Inode 区的容量就会确定，**且无法拓展**，所以即使在磁盘空间未满时也可能因 Inode 耗尽而无法创建新文件，生产上曾存在因此产生的生产事故。一个实际的案例就是攻击者通过在磁盘中快速创建大量空文件，快速消耗 Inode 容量，导致磁盘无法存储。这种攻击在文件系统与邮件系统中尤为常见。

值得注意的是，INode 节点中并不存储文件的文件名，文件名只是为了方便用户记忆和使用文件，在磁盘上，文件以索引节点的 ID 作为其唯一标识。目录节点是一种特殊的文件，事实上，文件名存储在父目录的文件数据块中，一个目录文件的数据块中存储着其目录下的所有文件的文件名，所以，当文件需要重命名时，只需要更改其目录节点中对应的文件名称的数据即可。这也就导致在 Linux 系统中新建、删除、重命名文件需要获取到其目录节点的权限才能修改。

## 格式化

### mkfs 命令：格式化

`mkfs` 命令的作用是在 CentOS 中创建文件系统（格式化分区）。例如：
- `mkfs -t ext4 /dev/sdb1`：将 `/dev/sdb1` 分区格式化为 ext4 文件系统。
- `mkfs.vfat /dev/sdc1`：将 `/dev/sdc1` 分区格式化为 FAT32 文件系统。

**注意：** `mkfs` 命令不能调整分区的默认参数（例如，默认每个块大小是 4 KB）。除非特殊情况，否则不需要调整这些默认参数。如需调整，需要使用功能更详细的 `mke2fs` 命令。

### 使用 mke2fs 命令格式化分区

mke2fs 命令提供了丰富的选项，允许用户自定义格式化参数。

命令格式：

```
mke2fs [选项] 分区设备文件名
```

**常用选项：**

- `-t 文件系统`：指定要格式化的文件系统类型，如 `ext2`、`ext3`、`ext4`。
- `-b 字节`：指定每个 `block` 块的大小。
- `-i 字节`：指定“字节/inode”的比例，即多少个字节分配一个 inode。
- `-j`：建立带有 ext3 日志功能的文件系统。
- `-L 卷标名`：给文件系统设置卷标名，避免之后再使用 `e2label` 命令设定。

命令示例：

```bash
mke2fs -t ext4 -b 2048 /dev/sdb6
# 该命令用于格式化分区，并指定 block 的大小为 2048 字节。
```

## linux 目录结构

| 目录名                  | 目录的作用描述                                               |
| :---------------------- | :----------------------------------------------------------- |
| **根目录和相关目录**    |                                                              |
| /bin/                   | 存放系统命令的目录，普通用户和超级用户都可以执行。是/usr/bin/目录的软链接。 |
| /boot/                  | 系统启动目录，保存与系统启动相关的文件，如内核文件和启动引导程序（grub）。 |
| /dev/                   | 设备文件保存位置，存储硬件设备的接口文件（如磁盘、USB设备）。 |
| /etc/                   | 配置文件保存位置。系统内所有采用默认安装方式（例如rpm安装）的服务配置文件保存在此目录中，包括用户信息、服务的启动脚本、常用服务的配置文件等。 |
| /home/                  | 普通用户的家目录。在创建用户时，每个用户有一个默认登录和保存数据的位置；所有普通用户的宿主目录是在/home/下建立一个和用户名相同的目录（例如用户user1的家目录是/home/user1/）。 |
| /lib/                   | 系统调用的32位函数库保存位置。是/usr/lib/目录的软链接。      |
| /lib64/                 | 系统调用的64位函数库保存位置。是/usr/lib64/目录的软链接。    |
| /lost+found/            | 当系统意外崩溃或机器意外关机时，产生文件碎片并存放于此。系统启动时，fsck工具会检查并修复已损坏的文件系统。此目录在文件系统分区中出现（例如/lost+found/是根分区的备份恢复目录，/boot/lost+found/是/boot分区的备份恢复目录）。 |
| /media/                 | 挂载目录。系统建议用于挂载媒体设备，如软盘和光盘。但在实际中，管理员可以根据需要自定义挂载点。 |
| /misc/                  | 挂载目录。系统建议用于挂载NFS服务的共享目录。管理员可以自由定义挂载点。 |
| /mnt/                   | 挂载目录。早期Linux系统中唯一的挂载目录，现在系统建议用于挂载额外的设备，如U盘、移动硬盘和其他操作系统的分区。管理员也可以创建子目录（如/mnt/usb）进行挂载。 |
| /opt/                   | 第三方软件安装保存位置。手工安装的源码包软件可以安装到此目录中（但习惯上，许多管理员也使用/usr/local/目录）。 |
| /proc/                  | 虚拟文件系统。该目录中的数据不保存在硬盘上，而是保存到内存中。主要用于保存系统内核、进程、外部设备状态和网络状态等信息（例如/proc/cpuinfo保存CPU信息，/proc/devices保存设备驱动列表，/proc/filesystems保存文件系统列表，/proc/net保存网络协议信息）。 |
| /root/                  | root用户的宿主目录。普通用户的宿主目录在/home/下，而root用户的宿主目录直接在根目录“/”下。 |
| /run/                   | 系统运行时数据保存位置，存储临时运行时信息如会话ID（ssid）、进程ID（pid）等。/var/run/是此目录的软链接（系统重启后数据会丢失）。 |
| /sbin/                  | 存放系统命令的目录，只有超级用户才可以执行。是/usr/sbin/目录的软链接。 |
| /srv/                   | 服务数据目录。一些系统服务启动后，可以在此目录中保存所需的数据（例如Web服务或FTP服务的数据）。 |
| /sys/                   | 虚拟文件系统。与/proc/目录相似，数据保存到内存中，主要用于保存与内核相关的信息（如设备驱动和系统硬件状态）。 |
| /tmp/                   | 临时目录。系统存放临时文件的目录，所有用户都可以访问和写入。建议不要保存重要数据，最好每次开机时清空此目录。 |
| **/usr/目录及其子目录** |                                                              |
| /usr/                   | 系统软件资源目录（不是“user”的缩写，而是“UNIX Software Resource”）。存放系统中安装的大多数软件资源。 |
| /usr/bin/               | 存放系统命令的目录，普通用户和超级用户都可以执行（/bin/目录是其软链接）。 |
| /usr/lib/               | 应用程序调用的函数库保存位置（32位系统，/lib/目录是其软链接）。 |
| /usr/lib64/             | 应用程序调用的64位函数库保存位置（/lib64/目录是其软链接）。  |
| /usr/local/             | 手工安装软件保存位置。系统建议用于安装源码包软件（许多管理员习惯将软件安装到此目录，而不是/opt/）。 |
| /usr/sbin/              | 存放系统命令的目录，只有超级用户才可以执行（/sbin/目录是其软链接）。 |
| /usr/share/             | 应用程序资源文件保存位置，包括帮助文档、说明文档和字体目录。 |
| /usr/src/               | 源码包保存位置。手工下载的源码包和内核源码可以保存到此（习惯上，管理员可能将手工源码包保存到/usr/local/src/，而内核源码保存到/usr/src/kernels/）。 |
| /usr/src/kernels/       | 内核源码保存位置（通常存储内核源代码文件）。                 |
| **/var/目录及其子目录** |                                                              |
| /var/                   | 动态数据保存位置。主要保存缓存、日志以及软件运行所产生的文件（系统运行时频繁变化的数据）。 |
| /var/lib/               | 程序运行中需要调用或改变的数据保存位置（例如MySQL数据库保存在/var/lib/mysql/）。 |
| /var/log/               | 系统日志保存位置，存储各种服务和应用生成的日志文件。         |
| /var/run/               | 一些服务和程序运行后，其进程ID（PID）保存位置。是/run/目录的软链接（优先使用/run/目录）。 |
| /var/spool/             | 放置队列数据的目录，用于存储排队等待处理的数据（如邮件队列和打印队列）。 |
| /var/spool/cron/        | 系统定时任务队列保存位置（例如cron计划任务文件）。           |
| /var/spool/mail/        | 新收到的邮件队列保存位置（系统收到的邮件存储于此）。         |
| /var/www/html/          | RPM包安装的Apache Web服务器的网页主目录（默认网站根目录）。  |


## 硬件设备

在 Linux 中，一切都以文件的形式进行展示，包括硬件设备，如磁盘、鼠标、键盘等。

设备文件被存储在 `/dev` 目录下，其中

| **硬件类型**      | **设备文件名**            | **命名规则说明**                        |
| :---------------- | :------------------------ | :-------------------------------------- |
| IDE硬盘           | `/dev/hd[a-d]`            | `a`: 主盘， `b`: 从盘                    |
| SATA/SCSI/USB硬盘 | `/dev/sd[a-p]`            | 按检测顺序分配（sda， sdb， ...）         |
| 光驱              | `/dev/cdrom`或 `/dev/sr0` | 通常为/dev/sr0的符号链接                |
| 软盘              | `/dev/fd[0-1]`            | fd0: 第一软驱                           |
| 25针打印机        | `/dev/1p[0-2]`            | 并口打印机(LPT端口)                     |
| USB打印机         | `/dev/usb/1p[0-15]`       | USB接口打印机设备                       |
| 鼠标              | `/dev/mouse`              | 通常指向具体设备（如/dev/input/mouse0） |

例如 

- `/dev/hda1` 表示第一块硬盘的第一个主分区
- `/dev/hdb5` 表示第二块硬盘的第一个逻辑分区

## 挂载

在操作系统中，磁盘格式化后还需要挂载以后才能使用，Windows系统会自动完成这一步，Linux 系统需要我们手动完成挂载。

在 Linux 中挂载需要先建立挂载点，一般由一个空目录来作为挂载点，如果挂载点不为空目录，则磁盘挂载以后，目录中原本的文件，将无法使用，当磁盘卸载后，目录中的文件恢复原状。

其原理是直接更改了 INote 的节点的指向信息

**现在让我们模拟一下挂载过程：**

1. **挂载前**： 假设您有一个空目录 `/mnt/usb`。这个目录本身有自己的 inode，它指向一些数据块，这些数据块里存储着文件列表（目前为空）。当您 `ls /mnt/usb` 时，系统会读取 `/mnt/usb` 目录的 inode，然后读取其数据块，发现是空的，所以没有显示。
2. **挂载时**： 当您执行 `mount /dev/sdb1 /mnt/usb` 命令后，神奇的事情发生了。**内核会将 `/mnt/usb` 这个目录的 inode 指针进行重定向**。它不再指向原来那个空的数据块，而是**直接指向了 USB 设备 (`/dev/sdb1`) 文件系统的根目录的 inode**。
3. **挂载后**： 此时，任何通过 `/mnt/usb/` 路径的访问请求，都会被内核直接引导到 USB 设备的文件系统上。`/mnt/usb` 这个目录原本的内容（即那个空的文件列表）被“覆盖”或“隐藏”了，你完全看不到也访问不到它们。
4. **卸载后**： 当您执行 `umount /mnt/usb` 后，内核会解除这个重定向。`/mnt/usb` 目录的 inode 指针又指回了它原本的数据块。那个空的文件列表又恢复了，所以目录再次显示为空。

这就是为什么您说“如果挂载点不为空目录，则磁盘挂载以后，目录中原本的文件，将无法使用”。它们并没有被删除，只是被新挂载的文件系统“遮挡”了。一旦卸载，就又“重现”了。

### 建立挂载点

```bash
mkdir /disk1  # 对应设备文件/dev/sdb1
mkdir /disk5  # 对应设备文件/dev/sdb5
```
- **备注**：将 `/dev/sdb1` 挂载到 `/disk1` 目录中，将 `/dev/sdb5` 挂载到 `/disk5` 目录中  

### 挂载 mount 

使用 mount 命令，将设备文件关联到挂载点 

**命令示例**：  

```bash
mount /dev/sdb1 /disk1  # 挂载/dev/sdb1到/disk1
mount /dev/sdb5 /disk5  # 挂载/dev/sdb5到/disk5
```


### 查看挂载信息  

验证挂载状态及分区信息   

- `mount`：查看所有已挂载的分区和光盘  
- `fdisk -l`：查看系统分区列表  
- `df`：查看分区占用百分比  

### 卸载

unmount

### 自动挂载  

- **操作说明**：修改分区自动挂载配置文件，实现系统启动时自动挂载  
- **配置文件路径**：  
  ```bash
  vi /etc/fstab  # 使用vi编辑器修改fstab文件
  ```
- **注意事项**：  
  > 此文件直接参与系统启动，如果修改错误，系统启动会报错  

- **配置示例（以/dev/sdb1为例）**：  
  ```plaintext
  /dev/sdb1    /disk1    ext3    defaults    1 2
  ```

- **字段说明**：  

| 列号 | 说明         |
|------|--------------|
| 第一列 | 设备文件名（如 `/dev/sdb1`） |
| 第二列 | 挂载点（如 `/disk1`）       |
| 第三列 | 文件系统（如 `ext3`）       |
| 第四列 | 挂载选项（如 `defaults`）   |
| 第五列 | 是否可以被备份 0 不备份 1 每天备份 2 不定期备份 |
| 第六列 | 是否检测磁盘 fsck  0 不检测  1 启动时检测 2 启动后检测 |

也可以使用 UUID 进行挂载，UUID(硬盘通用唯一识别码，可以理解为硬盘的 ID)

这个字段在 Cent0s 5.5的系统当中是写入分区的卷标名或分区设备文件名的，现在变成了硬盘的 UUID。这样做的好处是当硬盘增加了新的分区，或者分区的顺序改变，再或者内核升级后，任然能够保证分区能够正确的加载，而不至于造成启动障碍

那么每个分区的 UUID 到底是什么呢?我们讲过的 dumpe2fs 命令是可以查看到的，命令如下:

```bash
dumpe2fs /dev/sdb5
```

或

```bash
ls -l /dev/disk/by-uuid/
```

重启进行测试 或 使用 `mount -a` 重新挂载所有内容，用它进行测试

### /etc/fstab/文件修复











## 常用的硬盘管理命令

### 查看 Inode 使用情况

Linux 中使用 `df -i` 指令来查看 Inode 使用情况

```shell
[simon@centos6 ~]$ df -i
Filesystem     Inodes  IUsed   IFree IUse% Mounted on
/dev/sda1      524288   4237  520051    1% /
tmpfs          127609      2  127607    1% /dev/shm
/dev/sda2     1572864  18782 1554082    2% /home
```


### df 命令

```bash
df -ahT
```

- `-a`：显示特殊文件系统，这些文件系统几乎都是保存在内存中的。如/proc，因为是挂载在内存中，所以占用量都是 0
- `-h`：单位不再只用KB，而是换算成习惯单位
- `-T`：多出了文件系统类型一列

### du 命令

```
du [选项] [目录或文件名]
```

**选项：**

- `-a`：显示每个子文件的磁盘占用量。默认只统计子目录的磁盘占用量
- `-h`：使用习惯单位显示磁盘占用，如KB，MB或GB等
- `-s`：统计总占用量，而不列出子目录和子文件的占用量

> **注意：**
>  `du` 与 `df` 的区别：
>
> - `du` 用于统计文件占用大小，统计结果是准确的
> - `df` 用于统计空间大小，统计的剩余空间是准确的

### lsof 命令

lsof 命令用于列出当前系统上被进程打开的所有“文件”

`lsof` 输出关键列的含义

- `COMMAND`：进程的名称。
- `PID`：进程ID。
- `USER`：运行进程的用户。
- `FD`：文件描述符。常见值：
  - `cwd`：当前工作目录。
  - `rtd`：根目录。
  - `txt`：程序代码（文本段）。
  - `mem`：内存映射文件。
  - `0u`， `1u`， `2u`：标准输入、输出、错误（文件描述符 0， 1， 2）。
  - `3u`， `4u`...：其他打开的文件。
- `TYPE`：文件类型（如 `REG` 普通文件，`DIR` 目录，`CHR` 字符设备，`BLK` 块设备，`FIFO` 管道，`IPv4`/`IPv6` 网络套接字）。
- `DEVICE`：设备号。
- `SIZE/OFF`：文件大小或偏移量。
- `NODE`：文件的 inode 号。
- `NAME`：文件或网络连接的完整路径名/地址。

由于在 Linux 系统中，一切都以文件的形式表示，于是其主要作用：

#### 查看文件/目录被哪个进程占用：

- •**场景：** 无法卸载磁盘分区（提示设备忙）、无法删除文件（提示文件被占用）、移动文件失败。
- •**命令：** `lsof /path/to/file_or_directory` 或 `lsof /dev/sda1`
- •**输出：** 显示打开该文件或目录的所有进程的详细信息（进程ID、命令名、用户等）。知道进程后，你可以选择终止它（`kill`）或等待它完成。

#### 查看进程打开了哪些文件：

- •**场景：** 诊断进程行为、查找进程使用的配置文件、日志文件、依赖库等；排查进程资源泄露（如打开文件过多）。
- •**命令：** `lsof -p <PID>` (替换 `<PID>` 为具体的进程ID)
- •**输出：** 列出该进程打开的所有文件描述符及其对应的文件。

#### 查看网络连接：

- **场景：** 查看哪些进程在监听端口、哪些进程建立了到特定 IP/端口的连接、排查网络服务问题、检查可疑连接。
- **命令：**
  - `lsof -i`：列出所有网络连接（TCP， UDP， RAW）。
  - `lsof -i :<port>`：列出使用特定端口的所有连接（如 `lsof -i :80`）。
  - `lsof -i tcp`：只列出 TCP 连接。
  - `lsof -i udp`：只列出 UDP 连接。
  - `lsof -i @<ip>`：列出与特定 IP 地址相关的连接（如 `lsof -i @192.168.1.100`）。
  - `lsof -i @<hostname>`：列出与特定主机名相关的连接。
- **输出：** 显示进程、用户、协议、本地地址:端口、远程地址:端口、状态（如 `LISTEN`， `ESTABLISHED`）等信息。功能上类似于 `netstat -tulnp`，但提供更多进程细节。

#### 查看用户打开了哪些文件：

- •**场景：** 了解特定用户的活动（如 `root` 用户打开了哪些关键文件）。
- •**命令：** `lsof -u <username>`
- •**输出：** 列出该用户启动的所有进程打开的所有文件。

#### 查找被删除但仍被进程占用的文件：

- •**场景：** 磁盘空间未释放，怀疑有文件被 `rm` 删除但仍有进程在使用它（常见于日志文件）。
- •**命令：** `lsof +L1` 或 `lsof | grep deleted`
- •**输出：** 在 `NAME` 列会显示 `(deleted)`。这类文件的 inode 仍然被进程持有，直到进程关闭该文件描述符后，空间才会真正释放。重启进程或杀死进程是常见的解决方法。

#### 查看命令/程序使用的文件：

- •**场景：** 了解一个特定程序（如 `httpd`）运行时依赖哪些文件。
- •**命令：** `lsof -c <command_name>` (如 `lsof -c httpd`)
- •**输出：** 列出所有以 `<command_name>` 开头的进程打开的文件。

### fsck 命令：文件系统修复

fsck 命令用于检查和修复文件系统一致性。它的主要作用是在文件系统出现错误、损坏或异常关机（如断电、系统崩溃）后，诊断问题并尝试修复文件系统结构。

语法

```bash
fsck [选项] [设备名或挂载点]
```

`fsck` **绝对不能**在已挂载（`mounted`）且处于读写状态的文件系统上运行！这会导致灾难性的数据损坏。`fsck` 的修复操作有时可能导致数据丢失（尤其是文件内容损坏时），因为它优先保证文件系统结构的完整性。因此，**在运行 `fsck` 之前，强烈建议备份重要数据**。

示例

```bash
fsck -y /dev/sdb1
```

一般来说在检测到上次异常关机时，系统在启动时会自动调用该命令，无需我们手动调用。一般这个命令我们用不到。

### dumpe2fs 命令：显示磁盘的详细信息

dumpe2fs 命令用于显示磁盘的详细状态

命令语法

```bash
dumpe2fs [选项] <设备名>
```

**注意：文件系统通常需要处于未挂载状态或只读挂载状态才能安全使用 `dumpe2fs`。**

示例

```bash
dumpe2fs -h /dev/sda1
```

输出示例（关键部分）：

```
Filesystem volume name:   /          # 卷标（挂载点）
Last mounted on:          /          # 上次挂载点
Filesystem UUID:          12345678-90ab-cdef-1234-567890abcdef # 唯一标识符
Filesystem magic number:  0xEF53     # 标识为 ext* 文件系统
Filesystem revision #:    1 (dynamic) # 修订版本
Filesystem features:      has_journal ext_attr resize_inode dir_index filetype needs_recovery extent 64bit flex_bg sparse_super large_file huge_file dir_nlink extra_isize metadata_csum # 启用的特性
Filesystem flags:         signed_directory_hash
Default mount options:    user_xattr acl # 默认挂载选项
Filesystem state:         clean      # 状态：clean (干净) 或 not clean (需要检查)
Errors behavior:          Continue   # 错误处理方式
Filesystem OS type:       Linux      # 操作系统类型
Inode count:              524288     # 总 inode 数
Block count:              2097152    # 总块数
Reserved block count:     104857     # 保留块数（通常给 root）
Free blocks:              1584321    # 空闲块数
Free inodes:              512306     # 空闲 inode 数
First block:              0          # 第一个数据块
Block size:               4096       # 块大小
...
Journal inode:            8          # 日志的 inode
Default directory hash:   half_md4  # 目录哈希算法
Directory Hash Seed:      abcdef12-3456-7890-abcd-ef1234567890
Journal backup:           inode blocks # 日志备份方式
Checksum type:            crc32c     # 校验和类型
...
```

### stat 命令：查看文件详细信息

命令语法

```bash
stat [选项]... 文件或目录...
```

- 可以同时指定多个文件或目录。
- 使用 stat -f 或 stat --file-system 可以显示文件所在文件系统的状态信息，而不是文件本身的信息。

典型输出示例

```bash
stat file.txt
```

```bash
File: file.txt
Size: 1024           Blocks: 8          IO Block: 4096   regular file
Device: fd00h/64768d    Inode: 789146      Links: 1
Access: (0644/-rw-r--r--)  Uid: ( 1000/   user)   Gid: ( 1000/   group)
Access: 2023-10-27 14:30:00.000000000 +0800
Modify: 2023-10-27 14:20:00.000000000 +0800
Change: 2023-10-27 14:25:00.000000000 +0800
 Birth: 2023-10-27 14:15:00.000000000 +0800  # 如果文件系统支持
```

### file 命令：判断文件类型

```bash
file 文件名
```

# 网络服务

## 计算机网络基础

计算机间进行信息传输是通过计算机网络传输的，而计算机网络是基于多种传输协议组成的，先来介绍网络分层模型，计算机分层模型一般有两种，一种是 OSI 七层模型，另一种是 TCP/IP 四层模型。

### OSI 七层模型

OSI 七层模型是由国际标准化组织（ISO）在 20 世纪 80 年代提出，是一个理论上的、概念性的框架。它的目标是让全球各种不同的网络设备和系统能够遵循一个统一的标准进行通信，即“开放”和“互连”。

OSI 模型的伟大之处在于它的清晰的结构化和分层思想。它将复杂的网络通信过程分解为七个层次，每一层只负责一项相对独立的功能，下层为上层提供服务。这种思想极大地简化了网络设计的复杂性，至今仍是理解和 troubleshooting 网络问题的“黄金法则”。

七层结构:

1. 物理层：传输原始比特流（0和1），定义电气、机械等物理特性（如网线、光纤、无线射频）。
2. 数据链路层：将比特流封装成“帧”，在同一局域网内通过MAC地址进行寻址和差错校验（如交换机工作在此层）。
3. 网络层：在不同网络之间进行逻辑寻址和路径选择（路由），数据包的单位是“包”或“数据报”（如IP协议、路由器工作在此层）。
4. 传输层：提供端到端的可靠或不可靠的数据传输，管理数据包的分段、重组和差错控制（如TCP、UDP协议）。
5. 会话层：负责建立、管理和终止两个通信主机之间的“会话”（Session）。
6. 表示层：负责数据的表示、加密、解密、压缩和解压缩（如将数据从一种格式转换为另一种格式，如JPEG、ASCII）。
7. 应用层：为应用程序提供网络服务接口（如HTTP、FTP、SMTP、DNS等协议）。

### TCP/IP 四层模型

TCP/IP 四层模型是由美国国防部（DOD）在20世纪90年代提出，是一个实际使用的、具体的网络协议模型。它源于美国国防部ARPANET项目的实践，是互联网（Internet）实际遵循和使用的协议栈。**它先于OSI模型被发明和应用**，并在互联网的爆炸式增长中成为了事实上的国际标准。它是在实践中发展起来的，而不是在理论中设计出来的。因此它更简洁、更高效。

四层结构:

- 网络接口层：对应OSI的物理层和数据链路层。负责在本地网络介质上传输数据。
- 网际层：对应OSI的网络层。核心协议是IP协议，负责将数据包从源主机路由到目标主机。
- 传输层：与OSI的传输层完全对应。核心协议是TCP和UDP。
- 应用层：对应OSI的应用层、表示层和会话层。所有应用程序的协议都集中在这一层。

至于所谓的“五层模型”并不是一个独立的新模型，而是一个为了教学和理解方便，将OSI的理论清晰度和TCP/IP的实际应用性相结合的产物。

| OSI七层模型 (理论概念模型) | **五层混合模型 (教学常用模型)** | TCP/IP四层模型 (实际应用模型) | 核心协议举例                |
| :------------------------- | :------------------------------ | :---------------------------- | :-------------------------- |
| **应用层**                 | **应用层**                      | **应用层**                    | HTTP, HTTPS, FTP, DNS, SMTP |
| **表示层**                 | *(融合进应用层)*                | *(融合进应用层)*              | SSL/TLS, JPEG               |
| **会话层**                 | *(融合进应用层)*                | *(融合进应用层)*              |                             |
| **传输层**                 | **传输层**                      | **传输层**                    | TCP, UDP                    |
| **网络层**                 | **网络层**                      | **网际层**                    | IP, ICMP, RIP               |
| **数据链路层**             | **数据链路层**                  | **网络接口层**                | Ethernet, PPP, Wi-Fi        |
| **物理层**                 | **物理层**                      | *(融合进网络接口层)*          |                             |

### 物理层

物理层是最底层的层，它负责将数据从源主机传输到目标主机。物理层关心数据如何在实际的物理介质上变成信号传输，及电信号（网线）、光信号（光纤）、电磁波（Wi-Fi）的区别，如何将0和1比特流转换成介质可传输的信号（如曼彻斯特编码、调频/调幅）等。

实际设备与组件

- 网线（双绞线）：CAT5, CAT5e, CAT6，水晶头（RJ-45）的制作与标准（T568A/T568B）。
- 光纤：单模 vs 多模。
- 网卡（NIC）：计算机接入网络的物理接口。

### 数据链路层

数据链路层负责**局域网内的通信**，解决在同一个本地网络内，如何通过**硬件地址**准确地找到一台设备并传输数据。所谓的硬件地址就是 MAC 地址。

MAC 地址：它是一个**全球唯一**的 48 位标识符，在网卡出厂时就被固化其中。这就是局域网内设备的“物理身份ID”。

数据帧：数据帧是数据在网络中传输的最小单元，数据帧由源 MAC 地址、目标 MAC 地址、数据长度和数据组成。网络层传下来的数据包（Packet）在数据链路层会被“打包”。数据链路层会在其首尾加上帧头（Header）和帧尾（Trailer），形成一个数据帧（Frame）。

- 帧头：最重要的信息就是目的 MAC 地址和源 MAC 地址。
- 帧尾：通常包含一个用于差错校验的序列（如CRC），确保数据在传输过程中没有出错。

#### ARP 地址解析协议

假设局域网内设备A（MAC_A）想给设备B（MAC_B）发送数据

情况一：设备A已经知道设备B的MAC地址，数据链路层直接构建一个数据帧，目标地址是 MAC_B，然后通过物理层发送到传输介质上（如网线）。**局域网内所有设备都会收到这个帧**，但只有MAC地址匹配的设备B会接收并处理它，其他设备则会丢弃它。

情况二：设备A不知道设备B的MAC地址，这时就需要一个“问路”的协议——ARP（Address Resolution Protocol，地址解析协议）。

ARP 协议：ARP协议是局域网内设备之间进行通信时，用于查询MAC地址的协议。ARP协议会向局域网内的所有设备发送一个ARP请求，请求设备MAC地址。当收到ARP请求时，设备会根据ARP请求中的IP地址，返回设备的MAC地址。这样，设备A就可以知道设备B的MAC地址，从而向设备B发送数据。

其实现步骤可简化如下：

1. ARP 询问：设备A会在局域网内广播（Broadcast） 一个特殊的帧（ARP请求），大声喊：“谁的**IP地址**是**IP_B**？请告诉MAC_A！”
2. ARP应答：局域网内所有设备都听到这个广播，但**只有IP地址为IP_B的设备B会做出响应**。它会向设备A单播（Unicast） 一个ARP应答包，说：“我是IP_B，我的MAC地址是MAC_B”。
3. 更新缓存并发送：设备A将IP_B和MAC_B的对应关系存到自己的ARP缓存表中，然后就可以按照情况一的方式正常发送数据帧了。

ARP 是数据链路层和网络层之间的桥梁，它通过网络层地址（IP）来查询数据链路层地址（MAC）。

- **介质访问控制** - “如果大家都在同时‘喊话’，怎么办？”

在共享介质的网络（如早期的总线型以太网）中，如何避免多台设备同时发送数据造成冲突？数据链路层的子层——MAC子层负责解决这个问题。最经典的协议是CSMA/CD（Carrier Sense Multiple Access with Collision Detection，载波侦听多路访问/冲突检测），俗称“先听再说，边说边听”。（在现代全双工交换式网络中，冲突已大大减少）。

- **差错检测** - “信件内容在路上是否被损坏？”

数据在物理链路上传输可能受到干扰而出错。数据链路层在帧尾添加了校验码（如CRC）。接收方收到帧后，会用同样的算法计算校验码。如果计算结果与帧尾的不一致，就会丢弃这个损坏的帧。（注意，数据链路层通常不负责重传，重传由更高层如传输层的TCP协议负责）。

#### 交换机

要理解数据链路层，就必须了解它的代表设备：二层交换机。交换机的出现，根本原因是出于对网络“效率”和“规模”的迫切追求，是为了解决其前身——集线器（Hub）所带来的严重性能瓶颈问题。

我们可以通过一个生动的比喻来理解这个过程：

想象一个古老的村庄（早期的局域网）：

- 集线器 (Hub) 时代： “大喊村” —— 所有人的大喇叭
  - 工作方式：集线器是一个物理层设备。它就像村子中央的一个大喇叭。任何时候，任何一个人想说话，都必须对着这个大喇叭喊。
  - 核心问题：这个大喇叭是共享介质。一旦有人开始喊，全村所有人都必须停下手里所有事情来听，即使这个消息根本不是给他们的。然后要判断是不是在叫自己，如果不是，就忽略掉。 

这就是“广播”和“共享带宽”。同一时间只能有一对人在通信。其他人必须等待，带宽被所有用户共享和争抢。其毫无隐私（安全性差）：所有人的对话，全村都能听到。且如果两个人同时对着喇叭喊，他们的声音就会重叠在一起，导致谁都听不清（数据冲突）。他们必须停下来，等一会儿再重新喊（CSMA/CD协议）。

当村子里人少的时候（网络设备少，数据量小），这种模式还能勉强应付。但随着村子变成小镇，人口越来越多，业务越来越繁忙（网络规模扩大），所有人都挤在一个喇叭下工作，冲突和等待变得无法忍受，网络性能急剧下降。

为了解决早期的共享媒体网络效率低下问题，数据链路层引入了交换机，交换机可以将数据帧直接转发给正确的设备。交换机是一种物理设备。其集成了多个端口，每个端口又通过网线与其他计算机设备直接连接（也可以是其他交换机，连接两台或多台交换机的行为通常被称为级联。这样做的首要目的就是为了扩展端口数量和扩大网络规模，举个例子：如果一个办公室有50台电脑，但你只有24口的交换机。要连接所有电脑，最简单的方法就是再买一台交换机，然后用网线将两台交换机连接起来）。

其核心是两大动作：依靠一张 MAC 地址表学习和转发，其主要工作原理为：

1. **学习**
  - 交换机内部有一张表，记录着每个端口和其对应的设备的MAC地址。
  - 当数据帧从一个端口进入时，交换机会查看帧的源MAC地址，然后将这个地址和端口号的映射关系记录到表中。
  - 通过这种方式，交换机自动学习并构建出整个网络的“地图”。

2. **转发/过滤**：当交换机收到一个数据帧时，它会查看帧的目的MAC地址，并采取以下三种操作之一：
  - 单播转发：在MAC地址表中查到了目的地址对应的端口，则仅将该帧从那个端口转发出去。
  - 广播/洪泛：如果目的地址是广播地址（FF:FF:FF:FF:FF:FF）或不在MAC地址表中，则向除接收端口外的所有端口转发（洪泛）。
  - 过滤：如果目的MAC地址和源MAC地址在同一个端口上，则交换机丢弃该帧。

交换机的每个端口都是一个独立的冲突域。PC1和PC2在通信时，PC3和PC4也可以同时全速通信，互不干扰。这就像给每家每户都拉了独立的电话线，使他们可以直接通信。

交换机的端口数量没有绝对的标准，但可以根据其类型和用途有一个大致的范围：

| 交换机类型            | 典型端口数量                                                 | 主要应用场景                                           |
| :-------------------- | :----------------------------------------------------------- | :----------------------------------------------------- |
| **桌面/非网管交换机** | **5口、8口**                                                 | 家庭、小型办公室(SOHO)，扩展路由器端口                 |
| **基本网管型交换机**  | **16口、24口、48口**                                         | **企业网络的主流选择**，用于接入层，连接大量用户电脑   |
| **机架式交换机**      | **24口、48口**                                               | 标准机架宽度（19英寸）的1U设备，数据中心和企业机房常见 |
| **核心/汇聚交换机**   | **端口密度高**，但可能更注重**高速端口**（如10G、25G、40G、100G）的数量 | 网络骨干，用于连接其他交换机和服务器的上行链路         |
| **迷你/便携式交换机** | **3口、4口**                                                 | 临时移动办公，极度空间受限的环境                       |

#### VLAN

VLAN（虚拟局域网）是一种 logical LAN（逻辑局域网）的实现方式，它允许多个逻辑局域网（LAN）通过一个物理局域网（PAN）进行通信。

一家公司有工程部、财务部、市场部，他们的电脑都连接在同一台物理交换机上。如果没有VLAN，所有部门的广播流量相互混杂。随着网络规模扩大，大量的广播流量会浪费带宽和设备的处理能力（称为“广播风暴”风险）。而且，从安全和管理角度看，所有设备都能“听到”彼此的广播，缺乏隔离。通过配置VLAN，你可以让这三个部门的网络彼此完全隔离，就像他们各自连接在三台独立的物理交换机上一样。

交换机实现VLAN的核心机制是为数据帧打上一个VLAN标签。这个标准是基于IEEE 802.1Q 协议。

工作原理如下：

1. 划分VLAN：网络管理员在交换机的管理界面进行配置，将不同的交换机端口划分到不同的VLAN中（如Port 1-8属于VLAN 10，Port 9-16属于VLAN 20）。每个VLAN都有一个唯一的ID（1-4094）。
2. 端口类型：交换机的端口在VLAN环境中被分为两种关键类型：
  - Access端口：通常用于连接终端设备（如电脑、打印机、服务器）。一个Access端口只属于一个VLAN。
    - 数据入方向（进交换机）：当一台电脑发送一个普通的、没有标签的数据帧进入Access端口时，交换机会根据这个端口所属的VLAN，给数据帧打上对应的VLAN标签。
    - 数据出方向（出交换机）：当交换机要转发一个数据帧给这台电脑时，它会剥离VLAN标签，还原成一个普通的以太网帧再发送出去。电脑根本感知不到VLAN的存在。 
  - Trunk端口：专门用于交换机之间互联（或连接支持VLAN的路由器）。一个Trunk端口允许多个VLAN的数据通过。
    - 数据出入方向：所有通过Trunk端口传输的数据帧都保留着802.1Q VLAN标签。这样，对端的交换机收到后，就能通过标签识别出这个数据帧属于哪个VLAN，从而将其转发到正确的VLAN中去。 
3. 转发规则：交换机根据数据帧的目标MAC地址和其VLAN标签来查询MAC地址表并进行转发。一个VLAN就是一个独立的广播域，广播帧只会在其所属的VLAN内传播，绝不会泄漏到其他VLAN中。

一个简单的实例

假设一台24口交换机，我们进行如下配置：

- VLAN 10：端口1-8，命名为 Engineering
- VLAN 20：端口9-16，命名为 Finance
- 端口24：配置为 Trunk端口，连接另一台交换机。

那么

1. 接在端口1的工程部电脑和接在端口8的工程部电脑可以正常通信。
2. 接在端口9的财务部电脑和接在端口16的财务部电脑可以正常通信。
3. 但是，接在端口1的工程部电脑无法与接在端口9的财务部电脑通信，因为它们处于不同的VLAN（逻辑上像两台不同的交换机）。
4. 从端口24出去的流量会带着VLAN标签，从而将对端交换机也纳入到相应的VLAN体系中，实现网络扩展。

### 网络层

网络层主要解决如何将全球无数个局域网连接起来，并让任何一台主机都能找到另一台主机的问题。

#### IPv4/IPv6

要实现全球互联，首先需要一个统一的、逻辑上的地址方案，这就是 IP 地址。

IPv4：我们最熟悉的格式，如 192.168.1.1。它是一个 32 位的地址，理论上能提供约 42 亿个地址。为了更高效地管理和使用这些地址，还衍生出了子网划分、无类别域间路由（CIDR） 和网络地址转换（NAT） 等技术。

IPv6：IPv6 的地址格式为 2001:0db8:85a3:0000:0000:8a2e:0370:7334。它比 IPv4 更长，有 128 位，理论上能提供约 3.4 亿个地址。

#### IPv4 地址

一个IPv4 地址由 32 位二进制数组成，如 192.168.1.1。其二进制表示如下：

11000000.10101000.00000001.00000001

为了方便阅读，我们将忽略前导零，并使用点分隔符将二进制数转换为十进制数。这种格式称为点分式（Dotted Decimal Notation）。

每段的长度为 8 位，因此，一个 IPv4 地址有 4 个段。每段的取值范围为 0-255。

IP 地址的关键特性在于其层次性，一个 IPv4 地址（如 192.168.1.100 ）本身只是一个 32 位的二进制数。它的关键在于被分成了两个部分：

1. **网络部分 (Network Portion)**：IP地址的前一部分标识了主机所在的网络，就像是一个城市的**邮政编码**或一个小区的名字。它标识了设备所在的**网络区域**。同一个网络内的所有设备，其IP地址的网络部分必须完全相同。
2. **主机部分 (Host Portion)**：IP地址的后一部分标识了该网络下的特定主机，就像是邮政编码区域内的**具体街道门牌号**。它标识了该网络内的**特定设备**。同一个网络内，每个设备的主机部分必须唯一。

#### 子网掩码

一台设备如何知道一个给定的IP地址中，哪部分是网络号，哪部分是主机号？这就依赖于子网掩码。

子网掩码用于定义定义地址的层次结构，是一个与 IP 地址成对出现的、同样 32 位的二进制数。它的唯一作用就是明确指明 IP 地址中网络部分的位数。

子网掩码中连续的二进制 “1” 所对应的 IP 地址部分就是网络部分；连续的二进制 “0” 所对应的部分就是主机部分。

示例：

192.168.1.100 的子网掩码为 255.255.255.0

这表示该设备处于 192.168.1.0 的网络中，在这个网络中该主机的位置为 100。该网络的可用主机范围：192.168.1.1 到 192.168.1.254（共254个地址）, 192.168.1.0 为网络位置，192.168.1.255 被规定为广播地址。网络地址和广播地址不能分配给主机。

在实践中，我们通常用更简洁的 CIDR（无类域间路由）表示法来代替冗长的子网掩码。

格式为：IP地址 / 网络部分的位数

所以，192.168.1.100配上 255.255.255.0这个子网掩码，就可以直接写作：

192.168.1.100/24

这里的 /24 就清晰地表明网络部分占24位，主机部分占8位。

#### 公网 IP 与私有 IP

IP 地址总量约 43 亿个。如今各种各样的计算机设备层出不穷，全球 IPv4 地址早在 2011 年就已分配完毕。所有新加入互联网的设备、网站和服务都无法再获得独立的 IPv4 地址。

为了适应当下 IP 地址短缺的情况，我们把 IP 地址分为公网网段和局域网网段，所有局域网网段内的设备无法直接连接公网，必须依赖一个具有公网 IP 的设备将局域网流量以公网 IP 进行通信，这就是网络的基本结构。

| 特性           | 公网IP (Public IP)                  | 私有IP (Private IP / RFC 1918地址)                    |
| :------------- | :---------------------------------- | :---------------------------------------------------- |
| **全球唯一性** | 全球唯一，用于互联网上的唯一标识    | **无需全球唯一**，仅在本地网络内唯一即可              |
| **可访问性**   | 可直接被互联网上的设备访问          | **无法直接从互联网访问**，需通过NAT转换               |
| **分配方式**   | 由ISP向区域互联网注册机构申请和分配 | 网络管理员**自由分配**，无需申请                      |
| **范围**       | 除私有地址外的所有IPv4地址          | **10.0.0.0/8**, **172.16.0.0/12**, **192.168.0.0/16** |
| **成本**       | 通常是付费的，且资源日益紧张        | **免费**使用                                          |
| **用途**       | 服务器、网站等需要被全球访问的设备  | 路由器、家庭内部设备、企业内网设备等                  |


IANA 专门保留了三个 IP 地址段作为私有地址，这些地址不会在互联网上被路由

1. **A类私有地址**：**`10.0.0.0/8`** (子网掩码 `255.0.0.0`)
- **范围**：`10.0.0.0` - `10.255.255.255`
- **特点**：这是一个非常大的地址块，提供约1677万个主机地址，通常用于超大型企业网络。
2. **B类私有地址**：**`172.16.0.0/12`** (子网掩码 `255.240.0.0`)
- **范围**：`172.16.0.0`- `172.31.255.255`
- **特点**：提供16个连续的B类网络（每个网络约6.5万个地址），总计约104万个地址，常见于中型企业或校园网。
3. **C类私有地址**：**`192.168.0.0/16`** (子网掩码 `255.255.0.0`)
- **范围**：`192.168.0.0`- `192.168.255.255`
- **特点**：这是我们最熟悉的地址段，提供256个连续的C类网络（每个网络254个可用地址），总计约6.5万个地址。**家庭路由器默认的管理网段（如192.168.1.1/24、192.168.0.1/24）就位于此范围**。

经过如此划分后，由于私网地址相互独立存在，我们可以将私网地址段映射为公网地址段，从而实现私网设备之间互相访问公网设备，如此即可节省公网 IP：使成百上千的设备可以共享一个公网 IPv4 地址上网。我们究竟是如何通过私网地址段访问公网地址段呢？这就是连接公私网的桥梁 : 网络地址转换协议（NAT）。

#### NAT 协议

NAT 的本质是一个在路由器上执行的、按需修改 IP 数据包头部信息的进程。我们以一个最常见的场景为例：家庭网络中的一台电脑想要访问互联网上的一个网站。

**角色分配：**

- **内网主机**： 您的电脑 私有IP: `192.168.1.100`
- **NAT路由器**： 您的家用无线路由器 内网IP (LAN口): `192.168.1.1` 公网IP (WAN口, 由ISP分配): `123.123.123.123`
- **公网服务器**： 网站服务器 公网IP: `203.0.113.50`

**通信步骤：**

1. **内网发起请求（出站）**您的电脑 (`192.168.1.100`) 想要访问服务器 (`203.0.113.50`)。电脑构造一个 IP 数据包：**源IP**: `192.168.1.100`(私有IP) **源端口**: `54321`(随机的高端口) **目标IP**: `203.0.113.50`(服务器公网IP) **目标端口**: `80`(HTTP服务端口)
2. **路由器执行 SNAT（源网络地址转换）**数据包发送到路由器 `192.168.1.1`。路由器知道这个包要去往公网，于是启动 NAT 操作：**创建映射**：它在自己的**NAT转换表**中创建一条记录。`(内网IP:端口) -> (公网IP:新端口)``(192.168.1.100:54321) -> (123.123.123.123:60000)` **修改数据包**：将原始数据包的**源IP**和**源端口**替换成映射后的公网IP和端口。发出新数据包：**源IP**: `123.123.123.123`(路由器的公网IP) **源端口**: `60000`(路由器分配的新端口)**目标IP**: `203.0.113.50`**目标端口**: `80`
3. **公网服务器响应**网站服务器收到请求，它只知道是来自 `123.123.123.123:60000`的请求。服务器发送响应包：**源IP**: `203.0.113.50`**源端口**: `80`**目标IP**: `123.123.123.123`(发回给路由器)**目标端口**: `60000`(发回给路由器分配的那个端口)
4. **路由器执行 DNAT（目标网络地址转换）**路由器 (`123.123.123.123`) 在WAN口收到了响应包。它查看NAT转换表：“端口 `60000`对应的是谁？”查表后发现：`60000`-> `192.168.1.100:54321`**修改数据包**： 将响应包的**目标IP**和**目标端口**替换回内网主机的私有IP和端口。将数据包转发到内网：**源IP**: `203.0.113.50`**源端口**: `80`**目标IP**: `192.168.1.100`(您的电脑)**目标端口**: `54321`
5. **内网主机接收响应**您的电脑收到响应包，它认为这就是它最初发出的请求的直接回复，通信顺利完成。

通过上述过程，我们完成了内网与公网之间的通信。同时，它还具备以下优点：

1. **节省IP的核心**： 整个过程中，互联网只看到**一个**公网IP (`123.123.123.123`) 在活动。而在这个公网IP背后，路由器可以通过分配不同的端口号（如60001, 60002...），为成千上万个内网设备同时提供上网服务。这种基于端口的NAT也称为**PAT（端口地址转换）** 或 **NAPT（网络地址端口转换）**，是最常见的NAT类型。
2. **隐藏内网结构**：公网服务器始终不知道真正请求它的客户端的私有IP是什么，这提供了一层天然的安全防火墙。
3. **实现连接共享**： 这正是“多台设备共享一个宽带账号上网”的技术基础。
4. **增加了网络规划的灵活性**：组织可以在内部自由分配私有 IP，而无需向 ISP 或注册机构申请。

但是，NAT的缺点也很明显：

1. **破坏了端到端连接性**：位于NAT后的设备（使用私有IP）难以直接作为服务器被公网主动访问。这对于P2P下载、视频通话、在线游戏等需要直接连接的应用造成了障碍，通常需要依赖**STUN、TURN、ICE**等复杂的 NAT 穿透技术。
2. **增加了网络复杂性**：NAT 转换需要维护状态表，增加了设备的处理负担，也可能成为网络故障的潜在点。
3. **并非真正的安全解决方案**：NAT 提供的安全是一种“隐蔽安全”，**不能替代专业的防火墙**。一旦内网设备中毒，它仍然可以主动向外建立连接并发起攻击或泄露数据。

#### ICMP 协议

ICMP 是网络层的一个至关重要且有趣的协议。可以把它看作是 IP 协议的“助手”或“信使”，负责报告错误、诊断故障和查询信息。

为了更有效地转发 IP 数据包和提高交付成功的机会。当传输 IP 数据包遇到问题时（如目标不可达、超时），ICMP 会向源设备发送一个错误报告，帮助源设备了解并可能调整其发送策略。

**ICMP 报文的主要类型与功能**

ICMP 报文种类很多，主要通过报文中的 类型（Type） 和 代码（Code） 字段来区分。主要可分为两大类：

第一类：差错报告报文（用于通知错误）

当路由器或目标主机处理 IP 数据包出错时，会向源IP地址发送一个 ICMP 差错报告报文。ICMP 差错报文永远不会为另一个ICMP差错报文而发送，以防止无限循环。

常见的差错报告类型包括：

| 类型 (Type) | 代码 (Code) | 描述                                       | 常见场景                                                     |
| :---------- | :---------- | :----------------------------------------- | :----------------------------------------------------------- |
| **3**       | 多种        | **目的地不可达 (Destination Unreachable)** | 网络不可达(0)、主机不可达(1)、端口不可达(3)、协议不可达(2)、 fragmentation needed(4) （需要分片但数据包设置了DF不分片标志） |
| **5**       | 多种        | **重定向 (Redirect)**                      | 通知主机存在更好的下一跳路由                                 |
| **11**      | 0 或 1      | **超时 (Time Exceeded)**                   | **TTL 超时 (0)**：数据包TTL减到0被丢弃。 **分片重组超时 (1)**：接收方等待分片超时。 |
| **12**      | 多种        | **参数问题 (Parameter Problem)**           | IP 头部字段有错误                                            |


第二类：查询报文（用于网络诊断）

这类报文用于主动发起请求，以探测或查询网络信息。一台主机或路由器发送一个查询请求，另一台设备收到后会返回一个查询应答。

常见的查询报文类型包括：

| 类型 (Type) | 代码 (Code) | 描述                                                         | 请求/应答   |
| :---------- | :---------- | :----------------------------------------------------------- | :---------- |
| **8 / 0**   | 0           | **回送请求 (Echo Request)** / **回送应答 (Echo Reply)**      | 请求 / 应答 |
| **13 / 14** | 0           | **时间戳请求 (Timestamp Request)** / **时间戳应答 (Timestamp Reply)** | 请求 / 应答 |

典型的 ICMP 工具：

ping - 连通性测试工具，用于检查两台主机之间是否能够通信，并测量往返时间（RTT）
traceroute( Windows 为 tracert) - 路径追踪，用于探测数据包从源到目的所经过的完整路径（所有路由器），用于定位网络故障点。

ICMP 虽然有用，但也可能被恶意利用：

- **ICMP 攻击**： 例如 **ICMP 洪水攻击**（一种DDoS攻击），攻击者发送大量 `Echo Request`(ping) 报文，耗尽目标资源。
- **信息探测**： 攻击者可以使用 ICMP 来探测内网结构，了解哪些主机是活动的。
- **隧道技术**： 一些恶意软件会利用 ICMP 报文（将其数据隐藏在`Echo Request/Reply`中）来建立隐蔽通信通道，绕过防火墙规则（因为很多防火墙允许ping通过）。

因此，在现代网络安全管理中，我们通常会在防火墙策略中严格过滤不必要的 ICMP 报文类型（例如，允许 Echo Reply入站但阻止 Echo Request入站），而不是简单地全部允许或禁止。

#### 路由器

**路由**

路由指的是决定数据包从源到目的地所遵循的路径的过程。它是一个动态的、智能的决策过程。这个决策过程所依赖的关键是一张“地图”或“路标”——这就是路由表。每一台路由器都维护着自己的一张路由表。

路由表的核心条目通常包括：

- **目标网络**： 我要去哪个网络？（例如 `192.168.1.0/24`）
- **下一跳**： **为了到达目标网络，我应该把数据包交给谁？**（这是最关键的信息，通常是下一个路由器的接口IP地址）
- **出接口**： 我应该从我自己身上的哪个端口发出去？（例如 `GigabitEthernet0/1`）
- **度量值**： 这条路径的“成本”是多少？（跳数、带宽、延迟等，用于比较多条路径的优劣）

路由表的构建方式有三种：

1. **直连路由**： 路由器自动发现。只要给它的一个接口配置了IP地址并激活，它就自动知道该接口直接连接的网络。**这是最优先、最可靠的路由。**
2. **静态路由**： 由网络管理员**手动填写**。适用于小型简单网络，但无法自动适应网络变化。
3. **动态路由**： 路由器之间通过**动态路由协议**（如 OSPF, BGP, RIP）相互通信，自动学习并更新路由表。**这是互联网和大中型网络得以运行的核心**。它们能自动适应网络拓扑变化（如某条链路故障），并重新计算最佳路径。

决策原则：最长前缀匹配

当一个数据包到来时，路由器会查看其**目标IP地址**，并在路由表中查找**最匹配**的条目。

- **规则**： 选择**网络前缀最长**（即子网掩码最具体）的路由条目。
- **比喻**： 这好比寄信，先匹配“北京市海淀区中关村大街5号”，如果匹配不上，再匹配“北京市海淀区”，再匹配不上就匹配“北京市”。

**路由器**

**路由器** 是专门设计用来执行**路由**功能的网络硬件设备。它是路由过程的**物理实体和执行者**。

路由器通常装有多个网卡，每个网卡都对应一个接口。每个接口都对应一个不同的网络。其工作过程如下：

1. **接收数据包**： 从一个网络接口（如家里的网线口或Wi-Fi）接收数据包。
2. **检查目标地址**： 解封装数据包，读取其IP报头中的**目标IP地址**。
3. **查询路由表**： 根据“最长前缀匹配”原则，查找路由表，决定**下一跳**和**出接口**。
4. **转发数据包**： 将数据包从决定好的出接口发送出去，交给下一跳路由器。
5. **重复过程**： 这个过程在路径上的每一台路由器上重复，直到数据包到达最终目的地。

### 传输层

网络层只管把数据包送到目标主机，那么主机上的哪个应用进程应该接收这个包？数据传输是否可靠？这就是传输层（Transport Layer）的关注点。

传输层通过端口号（Port Number）来解决“主机上哪个进程接收这个包”的问题。

传输层通过 **端口号（Port Number）** 来解决“主机上哪个进程接收这个包”的问题。

- **端口号**是一个16位的整数，范围是0~65535。
- 它和网络层的**IP地址**共同构成了一个唯一的标识符，称为**套接字（Socket）**：`(IP地址 : 端口号)`。
  - **IP地址**：定位到唯一的一台主机。
  - **端口号**：定位到这台主机上的唯一一个进程。
- 这就实现了真正的**端到端（End-to-End）** 或 **进程到进程（Process-to-Process）** 的通信。

#### UDP 协议

UDP（User Datagram Protocol，用户数据报协议）是传输层最简单的协议之一。它的设计理念是 **“简单、高效”** ，为此它做出了两个关键取舍：

1. **无连接（Connectionless）**：通信前不需要先建立连接（例如握手）。想发送数据时，直接就可以发送。这降低了初始延迟。
2. **不可靠交付（Unreliable Delivery）**：它不保证数据包一定能到达对端，也不保证数据包按序到达。不提供重传、流量控制、拥塞控制等机制。

UDP的简单性也体现在其报文头结构上，非常精简，只有**8个字节**的头部开销。

```
0      7 8     15 16    23 24    31
+--------+--------+--------+--------+
|   源端口号     |   目标端口号      |
+--------+--------+--------+--------+
|    数据报长度    |    校验和       |
+--------+--------+--------+--------+
|             数据载荷...            |
+-----------------------------------+
```

- **源端口号（Source Port）**：发送方进程的端口号。用于对方回信。**可选字段**，不需要回信时可设为0。
- **目标端口号（Destination Port）**：接收方进程的端口号。**必填字段**，指定数据要交付给哪个应用。
- **数据报长度（Length）**：整个UDP数据报（头部+数据）的总长度（字节）。最小为8（只有头部），最大为65535字节。
- **校验和（Checksum）**：用于检测数据报在传输中是否发生错误（如比特翻转）。**可选字段**，但通常被启用。如果校验出错，数据报会被直接丢弃，**不会**要求重传。

优点

- **速度快，延迟低**：没有建立连接的延迟，也没有重传、确认等机制带来的延迟和开销。
- **头部开销小**：仅8字节，比TCP的20字节头部更节省带宽。
- **无连接状态**：UDP服务器不需要为每个客户端维护连接状态，可以支持大量并发客户端。
- **控制权交给应用层**：应用可以更灵活地设计自己的重传、流控等逻辑。

缺点

- **不可靠**：数据可能丢失、乱序、重复。
- **无拥塞控制**：发送方可以以任何速率发送数据，如果发送过快可能淹没网络，导致网络拥塞，影响其他连接（如TCP连接）。这是一个“不礼貌”的协议。

UDP 的典型应用场景

正因为上述特点，UDP非常适合那些 **“宁愿丢包，也不能等待”** 的应用：

1. **实时音视频通信（VoIP，视频会议，直播）**：少量的数据丢失只会导致短暂的音画质下降或卡顿，但重传延迟会导致无法接受的卡顿和不同步。
2. **DNS（域名解析）**：查询请求和响应通常很小，且一个请求对应一个响应。如果没收到响应，应用层直接重发请求即可，非常简单高效。
3. **DHCP（动态主机配置）**：获取IP地址的过程本身就是在没有IP地址的情况下进行的，使用无连接的UDP非常自然。
4. **SNMP（网络管理）**：用于发送网络状态查询和控制指令。
5. **广播/多播应用**：如TFTP（简单文件传输）。UDP天然支持一对一、一对多、多对多通信。

#### TCP 协议

TCP（Transmission Control Protocol，传输控制协议）的设计理念与UDP完全相反，它的核心特点是：

1. **面向连接（Connection-Oriented）**：通信双方在传输数据前，必须首先建立一条逻辑连接。数据传输结束后，必须释放连接。
2. **可靠交付（Reliable Delivery）**：TCP使用一系列机制（确认、重传、流量控制、拥塞控制）来确保数据**无差错、不丢失、不重复、且按序**到达。
3. **全双工通信（Full-Duplex）**：在一条TCP连接中，双方都可以同时发送和接收数据。
4. **字节流（Byte Stream）**：TCP并不像UDP那样保护消息边界。应用程序交给TCP的数据可能被拆分或合并成多个数据包发送。接收方应用程序看到的只是一个连续的、无结构的字节流。

TCP 报文段结构

TCP为了实现其复杂功能，头部也远比UPD复杂，通常为**20字节**（不含选项字段）。

```
0                   1                   2                   3
 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
+---------------+---------------+---------------+---------------+
|         源端口号 (Source Port)        |      目标端口号 (Destination Port)    |
+---------------+---------------+---------------+---------------+
|               序列号 (Sequence Number)                          |
+---------------+---------------+---------------+---------------+
|               确认号 (Acknowledgment Number)                   |
+---------------+---------------+---------------+---------------+
| 数据偏移 | 保留 |    控制位     |            窗口大小 (Window)           |
| (Data Offset)| (Reserved) |   (Flags)     |                               |
+---------------+---------------+---------------+---------------+
|           校验和 (Checksum)          |      紧急指针 (Urgent Pointer)     |
+---------------+---------------+---------------+---------------+
|                   选项 (Options，可变长度)                     |
+---------------+---------------+---------------+---------------+
|                   数据 (Data)                                 |
+---------------+---------------+---------------+---------------+
```

关键字段解析

- **序列号（Sequence Number）**：**32位**。指出本报文段所发送数据的**第一个字节**在整个字节流中的序号。用于解决**网络包乱序**问题。
- **确认号（Acknowledgment Number）**：**32位**。期望收到对方**下一个报文段**的第一个数据字节的序号。表示该序号之前的所有数据都已正确接收。用于**确认和重传**。
- **数据偏移（Data Offset）**：指出TCP报文段的首部长度（以4字节为单位）。
- **控制位（Flags）**：共6位，每一位代表一个控制功能。
  - **URG**：紧急指针有效。
  - **ACK**：确认号有效。**一旦连接建立，这个标志位总是1**。
  - **PSH**：接收方应尽快将数据交付给应用层，而不是在缓冲区等待。
  - **RST**：重置连接。用于异常中断连接。
  - **SYN**：同步序列号。用于**建立连接**。
  - **FIN**：发送方数据发送完毕，用于**释放连接**。
- **窗口大小（Window）**：**16位**。用于**流量控制**。告诉对方自己当前还能接收多少字节的数据。

TCP 的核心机制

##### 连接管理：三次握手与四次挥手

**三次握手（Three-way Handshake）建立连接**：

1. **客户端 -> 服务器**：发送SYN报文（SYN=1），并选择一个初始序列号`seq=x`。
2. **服务器 -> 客户端**：发送SYN+ACK报文（SYN=1, ACK=1），确认客户端的序列号`ack=x+1`，并选择自己的初始序列号`seq=y`。
3. **客户端 -> 服务器**：发送ACK报文（ACK=1），确认服务器的序列号`ack=y+1`。

**目的**：双方交换初始序列号，确认对方的收发能力，为可靠传输初始化必要的状态变量。

**四次挥手（Four-way Handshake）释放连接**：

1. **主动方 -> 被动方**：发送FIN报文（FIN=1），表示自己数据发完了。
2. **被动方 -> 主动方**：发送ACK报文，确认FIN。
3. （被动方可能还有数据要发送）...
4. **被动方 -> 主动方**：发送FIN报文，表示自己也发完了。
5. **主动方 -> 被动方**：发送ACK报文，确认FIN。

**为什么是四次？** 因为TCP是全双工的，一方关闭发送通道后，还可以继续接收数据。因此关闭需要两个独立的FIN/ACK过程。

##### 可靠传输：确认应答（ACK）与超时重传

- **确认应答（Acknowledgment）**：接收方每收到一个数据包，都会回复一个ACK包，里面的确认号告诉发送方“我期望收到的下一个字节的序号是什么”。
- **超时重传（Retransmission）**：发送方发送一个数据包后会启动一个定时器。如果在定时器超时前没有收到对应的ACK，就认为包丢了，会重新发送。
- **快速重传（Fast Retransmit）**：如果发送方连续收到3个重复的ACK（例如，连续收到三个`ack=101`），它会推断数据段`seq=101`可能丢失了，于是立即重传，而不必等待超时。这大大提高了效率。

##### 流量控制（Flow Control）

防止发送方发送过快，导致接收方缓冲区溢出。

**机制**：接收方通过TCP头部的**窗口大小（Window）** 字段，实时告知发送方自己还有多少空闲的接收缓冲区。发送方发送的数据量不能超过这个窗口大小。

**拥塞控制（Congestion Control）**

防止发送方发送过快，导致整个网络拥堵瘫痪。这是一个全局性的过程。

- **核心算法**：主要包括**慢启动（Slow Start）**、**拥塞避免（Congestion Avoidance）**、**快速恢复（Fast Recovery）**。
- **核心概念：拥塞窗口（cwnd）**：发送方根据自己感知的网络拥塞程度而设定的窗口值。**发送窗口 = min(接收方通告窗口, 拥塞窗口)**。
  - **慢启动**：连接开始时，cwnd从1开始，每收到一个ACK就翻倍（指数增长），快速探测网络容量。
  - **拥塞避免**：当cwnd超过一个阈值（ssthresh）后，转为每收到一个ACK只增加1（线性增长），谨慎接近网络瓶颈。
  - **发生拥塞（超时）**：将ssthresh设为当前cwnd的一半，然后将cwnd重置为1，重新开始慢启动。
  - **发生拥塞（收到3个重复ACK）**：执行快速重传，并将cwnd减半，然后进入快速恢复阶段。

TCP 的典型应用场景

TCP适用于所有要求**数据完整可靠**的应用，而对速度延迟的要求可以放在第二位：

- **Web浏览（HTTP/HTTPS）**
- **电子邮件（SMTP, POP3, IMAP）**
- **文件传输（FTP）**
- **远程登录（SSH）**
- **数据库连接**

### 应用层

应用层利用底层的网络基础设施构建具体的应用。HTTP, HTTPS, DNS, DHCP, FTP, SMTP, POP3/IMAP

#### DHCP 协议

在小型网络中，可以手动为每台计算机配置IP地址、子网掩码、网关等参数。但在大型企业或公共网络中，这样做极其繁琐且容易出错。DHCP 协议使 DHCP 设备自动为局域网内设备分配IP地址。它允许一台计算机在加入网络时，自动从一台服务器获取所有必要的网络配置信息。

DHCP的工作过程通常被称为 **DORA**，包含四个主要步骤。这个过程很好地展示了应用层协议如何与下层协议（UDP）协同工作。

**DHCP Discover（发现）**

- **情景**：客户端刚启动，不知道自己该用什么IP，也不知道网络里谁是 DHCP 服务器。
- **行动**：客户端在本地局域网内**广播**一个 **`DHCP Discover`** 报文。这个报文的大意是：“大家好！我是新来的，有没有DHCP服务器在？请给我分配一个IP地址！”
- **底层细节**：此报文使用UDP协议，**源端口68（客户端）**，**目标端口67（服务器）**。**源IP地址为 0.0.0.0**（因为客户端还没有IP），**目标IP地址为广播地址 255.255.255.255**。

**DHCP Offer（提供）**

- **情景**：网络中的一台或多台DHCP服务器收到了广播的`Discover`报文。
- **行动**：服务器从自己的IP地址池中挑选一个可用的IP地址，然后**广播**（或单播，取决于客户端能力）一个 **`DHCP Offer`** 报文作为回应。报文里包含了为客户端**预分配**的IP地址、子网掩码、租期（IP地址的有效时间）以及其他信息（如网关、DNS服务器地址）。
- **底层细节**：同样使用UDP端口67和68。

**DHCP Request（请求）**

- **情景**：客户端可能会收到多个服务器发来的`Offer`（如果网络中有多个DHCP服务器）。
- **行动**：客户端选择其中一个`Offer`（通常是第一个收到的），然后再次**广播**一个 **`DHCP Request`** 报文。这个报文有两个目的：

  1. 告知它选择的那个服务器：“我接受你提供的配置”。
  2. 告知其他所有服务器：“我拒绝了你们的提供，你们可以把IP地址收回了”。

**DHCP Acknowledgement（确认）**

- **情景**：被选中的服务器收到了`Request`广播。
- **行动**：服务器发送一个 **`DHCP ACK`** 报文进行最终确认。这个报文中包含了客户端所请求的网络配置参数的正式生效信息。
- **情景（如果IP已失效）**：如果服务器发现之前提供的IP地址现在已经分配给别人了（非常罕见），它会回复一个 **`DHCP NAK`**（否定确认）报文，客户端则需要重新开始DORA过程。

收到`ACK`后，客户端就可以正式使用这个IP地址和其他配置参数加入网络了。

**DHCP 的重要特性**

- **租期（Lease）**：DHCP分配的IP地址是有“租借时间”的。租期过半时，客户端会尝试向原服务器**续租**（直接发送`Request`）。这确保了IP地址可以被回收并重新分配，高效地利用有限的IP资源。
- **即插即用**：用户无需任何手动配置，接入网络即可使用。
- **支持重启**：客户端重启后，会尝试重新申请之前使用的IP地址。
- **支持移动性**：设备在不同子网间移动时，可以自动获取新子网的配置。

**为什么使用广播？且使用UDP？**

- **使用广播**：因为在DORA过程的前几步，客户端根本没有IP地址，无法进行正常的单播通信。广播是唯一能确保报文被网络中所有主机（包括DHCP服务器）收到的方式。
- **使用UDP**：因为DHCP的交互是简单的“请求-响应”模式，报文数量少，且不需要建立连接的可靠传输。UDP的无连接和高效特性非常适合这种场景。TCP的复杂握手和重传机制在这里反而是不必要的开销。

#### DNS 域名解析协议

互联网的核心是IP地址，但人类很难记住一串数字（如 142.251.42.206）。相反，我们擅长记忆有意义的名称（如 www.google.com）。

DNS（Domain Name System，域名系统） 的核心功能就是在域名（人类易记） 和 IP地址（机器寻址） 之间进行映射翻译。它是互联网的“分布式电话簿”。

DNS不是一个中心化的服务器，而是一个遍布全球的、按层次结构组织的巨大分布式系统。这种设计避免了单点故障，也易于管理。

DNS 运行在 UDP（和TCP）之上，使用53号端口。

##### 域名空间的结构

DNS域名空间是一个巨大的**倒置树形结构**，由“.”根域开始向下分支。

- **根域名服务器（Root DNS Servers）**：全球有13组（逻辑上是13个，物理上有很多镜像）根服务器，它们不直接解析域名，但知道所有顶级域（TLD）服务器的地址。
- **顶级域（Top-Level Domains, TLD）**：
  - **通用顶级域（gTLD）**：如 `.com`, `.org`, `.net`, `.edu`。
  - **国家代码顶级域（ccTLD）**：如 `.cn`（中国）， `.us`（美国）， `.uk`（英国）。
- **权威域名服务器（Authoritative DNS Servers）**：每个组织（如Google、清华大学）都管理自己域名的解析，并提供权威的DNS服务器。这些服务器存储了其管辖域内**域名到IP的最终映射记录**。
- **次级域**：次级域名，如 www 。
- **主机域**：有的次级域名下还有一层，一般代表某台主机的主机名。

例如，www.baidu.com 以上述域名为例，其完整域名为 www.baidu.com.

其中

- **.** 表示根域，在早期的互联网中，域名最后必须以以 . 结尾。随着时代的发展，. 可以省略，但在域名解析过程中，会自动将 . 添加到末尾。
- **`.com`**： **顶级域（Top-Level Domain, TLD）**
- **`baidu.com`**： **二级域（Second-Level Domain）**，同时也是**注册的域名**。这个整个域的管理权归属于百度公司。
- **`www.baidu.com`**： **三级域**，或者更常见地，称之为**主机名（Hostname）**。它特指 `baidu.com`这个域下的一个具体的主机或服务（在这里通常指Web服务器）。

因为权威域名服务器管理的单位是**域（Domain）**，而不是主机名。百度管理的是一整个 `baidu.com`域，它为这个域下的所有主机名（`www`, `mail`, `api`, `map`等）提供解析服务。

##### 域名的解析过程

以 `www.baidu.com` 为例，域名的解析过程如下：

1. 域名解析过程从浏览器出发，您的计算机或浏览器首先检查自己的缓存，看是否有 www.baidu.com 的IP记录。
2. 如果没有，则查看本机 host 文件是否有指定的 www.baidu.com 的IP记录。
3. 如果没有，则将查询发送给配置好的**DNS服务器**（如运营商DNS）进行查询，DNS 服务器也有缓存，如果缓存中有，则返回给浏览器，否则开始进行正式的查询工作。
4. 首先访问根域名服务器，但根域名服务器只知道顶级域名服务器的IP地址，这就类似于根服务器回复：“我不知道 www.baidu.com 的地址，但我可以给你管理 .com 所在顶级域的域名服务器的地址列表。”
5. 然后 DNS 服务器访问 .com 所在的顶级域名服务器，同样的顶级域名服务器也只知道 .baidu.com 所在的**权威域名服务器**的IP地址。
6. 本地DNS解析器最终向百度公司的权威 DNS 服务器发起查询。权威服务器查询自己的数据库，找到了 www.baidu.com 对应的记录。
    - **关键点**：它返回的**不一定是一个直接的IP地址（A记录）**。对于大型网站，它更可能返回一个 **CNAME记录**（规范名称记录），相当于一个别名。
    - **例如**：权威服务器回复：“`www.baidu.com`的别名是 `www.a.shifen.com`。” （这是一个示例，实际名称可能不同）
7. **额外的CNAME查询**：现在，本地DNS解析器需要重新开始一轮查询（但可能直接从缓存中查找），为 `www.a.shifen.com`解析出最终的IP地址。这个过程可能会重复步骤 3-5，去查找 `a.shifen.com `的权威服务器。
8. **返回最终IP**：最终，本地DNS解析器会拿到一个或多个IP地址（例如 `110.242.68.3`和 `110.242.68.4`）。它会将这些地址缓存起来，并将它们返回给您的计算机。
9. **建立连接**：您的计算机拿到IP，终于可以和百度的服务器建立TCP连接，开始传输网页数据了。

**整个过程如下图所示，清晰地展示了迭代查询与递归查询的结合：**

```mermaid
flowchart TD
A[浏览器请求<br>www.example.com] --> B(查询本地缓存)
B -- 有缓存 --> A
B -- 无缓存 --> C[请求递归解析器<br>ISP DNS/8.8.8.8]
C -- 递归查询 --> D{递归解析器}
D -- 1. 查询根域名服务器 --> E[根服务器]
E -- 返回.com TLD服务器地址 --> D
D -- 2. 查询.com TLD服务器 --> F[.com TLD服务器]
F -- 返回example.com权威服务器地址 --> D
D -- 3. 查询example.com权威服务器 --> G[example.com权威服务器]
G -- 返回最终IP地址 --> D
D -- 缓存结果并返回给用户 --> C
C -- 获得IP --> H[浏览器向目标IP发起HTTP连接]
```

#### HTTP/HTTPS 协议

见 nginx 基础 凤凰架构

#### SSL/TLS 协议

见 凤凰架构

#### WebSocket

HTTP 的通信模式是“一问一答”，服务器不能主动发送消息给客户端。这种模式对于实时应用（如聊天室、实时游戏、股票行情）来说效率极低（通常只能用“轮询”这种笨办法模拟实时）。

传统的 HTTP/1.1 持久连接（Keep-Alive）只为了一次请求/响应而存在，完成后连接可能被关闭。即使不关闭，也无法用于服务器主动发起通信。

WebSocket 是一种在单个 **TCP** 连接上进行**全双工**通信的网络协议。它允许服务端主动向客户端推送数据，使得客户端和服务器之间的数据交换变得更加简单和高效。

WebSocket 的连接过程分为两个阶段：**握手**和**数据传输**。

**阶段一：握手（Handshake） - 基于 HTTP 的升级**

WebSocket 巧妙地利用 HTTP 协议来完成初始握手，这样可以更好地兼容现有网络基础设施（如防火墙、代理服务器）

**客户端握手请求**：

客户端发送一个标准的 HTTP 请求，但包含特殊的头部，表明它希望将协议**升级**为 WebSocket。

```
GET /chat HTTP/1.1
Host: server.example.com
Upgrade: websocket          // 关键：请求升级协议
Connection: Upgrade         // 关键：请求升级连接
Sec-WebSocket-Key: dGhlIHNhbXBsZSBub25jZQ== // 随机生成的密钥，用于安全验证
Sec-WebSocket-Version: 13   // 使用的WebSocket协议版本（13是最常见的）
Origin: http://example.com  // 用于安全校验（同源策略）
(其他可能的HTTP头部，如Cookie)
```

**服务器握手响应**：

如果服务器支持 WebSocket，它会同意协议升级，返回一个 `101 Switching Protocols`响应。

```
HTTP/1.1 101 Switching Protocols
Upgrade: websocket          // 确认升级协议
Connection: Upgrade         // 确认升级连接
Sec-WebSocket-Accept: s3pPLMBiTxaQ9kYGzzhZRbK+xOo= // 基于客户端Key计算出的响应值
```

`Sec-WebSocket-Accept`的值是由客户端的 `Sec-WebSocket-Key`加上一个固定的 GUID `258EAFA5-E914-47DA-95CA-C5AB0DC85B11`，然后进行 SHA-1 哈希，最后 Base64 编码生成的。这个步骤是为了证明服务器确实理解 WebSocket 协议，防止意外升级。

**一旦握手成功，TCP 连接就被“升级”了。** 此后，通信将不再使用 HTTP 语法，而是使用 WebSocket 协议定义的数据帧格式进行双向通信。当前的 TCP 连接变成了一个持久的 WebSocket 连接。

**阶段二：数据传输 - 使用 WebSocket 数据帧**

握手完成后，客户端和服务器开始使用轻量级的 **WebSocket 数据帧**格式来收发消息

一个 WebSocket 帧的简化结构如下：

```
0                   1                   2                   3
0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
+-+-+-+-+-------+-+-------------+-------------------------------+
|F|R|R|R| opcode|M| Payload len |    Extended payload length    |
|I|S|S|S|  (4)  |A|     (7)     |             (16/64)           |
|N|V|V|V|       |S|             |   (if payload len==126/127)   |
| |1|2|3|       |K|             |                               |
+-+-+-+-+-------+-+-------------+ - - - - - - - - - - - - - - - +
|     Extended payload length continued, if payload len == 127  |
+ - - - - - - - - - - - - - - - +-------------------------------+
|                               |Masking-key, if MASK set to 1  |
+-------------------------------+-------------------------------+
| Masking-key (continued)       |          Payload Data         |
+-------------------------------- - - - - - - - - - - - - - - - +
:                     Payload Data continued ...                :
+ - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - +
|                     Payload Data continued ...                |
+---------------------------------------------------------------+
```

- **轻量级头部**：基础头部只有 2 字节，远小于 HTTP 头部。
- **Opcode**：定义帧的类型（如 `1`=文本，`2`=二进制，`8`=关闭连接，`9`=Ping，`10`=Pong）。
- **Masking**：出于安全原因，从客户端发往服务器的帧必须被掩码（Masked）。服务器发往客户端的帧则不需要。
- **Payload**：实际传输的应用数据。

**Ping/Pong 帧**：WebSocket 提供了心跳机制。服务器或客户端可以发送一个 Ping 帧，对方必须回复一个 Pong 帧。这用于保持连接活跃、检查对方是否还在线。

WebSocket 是任何需要**低延迟、高实时性**双向通信的 Web 应用的理想选择：

1. **即时通讯（IM）**：聊天应用（微信网页版、WhatsApp Web）。
2. **实时通知**：社交媒体的点赞、评论通知，新闻推送。
3. **多人在线协作**：协同编辑文档（如 Google Docs）、共享白板。
4. **在线游戏**：多玩家对战游戏，状态需要实时同步。
5. **实时数据仪表盘**：金融股票行情、实时监控系统、体育赛事实时更新。

在浏览器中，WebSocket 的功能通过 `WebSocket`API 提供，使用非常简单：

```javascript
// 1. 创建连接（ws:// 或 wss:// 用于加密，相当于 HTTP 和 HTTPS）
const socket = new WebSocket('wss://echo.websocket.org'); 

// 2. 监听连接打开事件
socket.onopen = function(event) {
  console.log('Connection established!');
  // 3. 发送数据（可以是文本或Blob二进制数据）
  socket.send('Hello Server!'); 
};

// 4. 监听服务器发送的消息
socket.onmessage = function(event) {
  console.log('Message from server:', event.data);
};

// 5. 监听错误
socket.onerror = function(error) {
  console.error('WebSocket error:', error);
};

// 6. 监听连接关闭
socket.onclose = function(event) {
  console.log('Connection closed:', event.code, event.reason);
};

// 主动关闭连接
// socket.close();
```
## vmware 中的网络模式

### 桥接模式 (Bridged Mode)

- **工作原理**：虚拟机的虚拟网卡直接连接到宿主机物理网卡上。相当于在物理网络上虚拟出了一台**真正的、独立的计算机**。虚拟机会和宿主机**并行地**从您所在网络的DHCP服务器（通常是家庭路由器）获取IP地址。
  - 例如：您的路由器网段是 `192.168.1.0/24`，它给宿主机分配了 `192.168.1.100`，那么桥接模式下的虚拟机可能会获取到 `192.168.1.101`。
- **形象比喻**：虚拟机就像一台新电脑，用一根**虚拟的网线**直接插到了您家路由器的同一个LAN口上，和您的物理电脑是**平级关系**。
- **网络连接性**：
  - **同一局域网内的其他机器**：**可以**访问到这台虚拟机。
  - **宿主机**：**可以**与虚拟机通信。
  - **互联网**：**可以**直接访问。
- **优缺点**：
  - **优点**：配置简单，虚拟机完全融入真实网络，行为与物理机无异。
  - **缺点**：会占用一个局域网IP地址。如果网络有安全策略（如公司内网），可能会受到限制或引发冲突。
- **适用场景**：需要让虚拟机像真实机器一样被局域网内其他设备访问的场景，例如将虚拟机作为服务器供他人测试。

### NAT 模式 (Network Address Translation)

- **工作原理**：这是最常用、默认的模式。VMware会在宿主机上创建一个**虚拟的私有网络**（通常网段为 `192.168.x.0/24`）和一个**虚拟的NAT路由器/DHCP服务器**（即VMnet8）。虚拟机连接到此虚拟网络，通过这个虚拟路由器“上网”。
  - 虚拟机会从VMware的DHCP服务获取到一个私有IP（如 `192.168.137.10`）。
  - 当虚拟机访问外网时，虚拟路由器会进行**网络地址转换**，将虚拟机的私有IP转换为宿主机的公网IP后发出。返回的数据包再由虚拟路由器转换后送给虚拟机。
- **形象比喻**：虚拟机就像您家中的**另一台手机或电脑**，连接在**您家的Wi-Fi路由器**后面上网。这个“家”就是VMware虚拟出来的私有网络，而“路由器”就是VMware的虚拟NAT设备。外部网络看不到虚拟机的真实IP，只能看到宿主机这个“家长”的IP。
- **网络连接性**：
  - **同一局域网内的其他机器**：**无法**直接访问到NAT模式下的虚拟机。（除非在虚拟路由器上做端口转发）
  - **宿主机**：**可以**与虚拟机通信。
  - **互联网**：**可以**访问。虚拟机可以“借”宿主机的网络上网。
- **优缺点**：
  - **优点**：**安全方便**。虚拟机可以上网，但被隐藏在内网中，不会与外部网络产生IP冲突。是个人学习和上网的最佳选择。
  - **缺点**：默认情况下，外部机器不能主动访问虚拟机。
- **适用场景**：绝大多数情况。例如需要虚拟机上网下载软件、浏览网页，但又不想它与外部物理网络互相干扰。

### 仅主机模式 (Host-Only Mode)

- **工作原理**：VMware会创建一个**完全封闭的虚拟网络**（通常对应VMnet1），这个网络只包含**宿主机**和**所有使用该模式的虚拟机**。虚拟机无法连接到此虚拟网络之外的任何地方，包括互联网。
  - 宿主机上会有一块虚拟网卡（如VMnet1）连接到这个虚拟网络，并获取一个IP（如 `192.168.56.1`）。
  - 虚拟机会从VMware的DHCP服务获取同网段的IP（如 `192.168.56.101`）。
- **形象比喻**：用一台**虚拟交换机**把几台电脑（虚拟机和宿主机）连接起来，组成一个**与世隔绝的私有局域网**。这个网络没有连接任何路由器，所以无法访问互联网。
- **网络连接性**：
  - **同一局域网内的其他机器**：**绝对无法**访问。
  - **宿主机**：**可以**与虚拟机通信（宿主机是此虚拟网络的一员）。
  - **互联网**：**无法**访问。
- **优缺点**：
  - **优点**：**极度安全隔离**。非常适合构建一个纯净的、与外界物理网络完全断开的测试环境。
  - **缺点**：虚拟机无法上网。
- **适用场景**：进行纯网络实验、测试病毒或不安全软件、搭建需要绝对隔离的测试环境。如果需要让仅主机模式上网，通常需要宿主机开启“Internet连接共享”或手动配置代理，但这超出了该模式的默认设计。

## 网卡

在 CentOS 操作系统中，网卡文件一般位于 `/etc/sysconfig/network-scripts/` 目录下，网卡的命名遵循 `ifcfg-<网卡名称>` 的格式，例如 `ifcfg-eth0`。其中 CentOS 6.x 与 CentOS 7.x 的网卡文件名称不同。导致该现象的原因是，在以传统命名方式为 eth0、eth1（对应文件 ifcfg-eth0）规则下，内核会根据网卡驱动加载顺序分配名称（先加载的网卡是 eth0，依此类推），就会导致网卡顺序可能因硬件变动（如插拔 PCI 设备）或驱动加载顺序变化而改变，导致名称不稳定。而在 CentOS 7.x 中，网卡文件名称默认启用**一致性网络设备命名**方式。例如：

- ens33（PCI-E 网卡，en表示 Ethernet，s33是插槽索引）。
- enp0s3（PCI 总线位置命名，p0s3表示总线 0、插槽 3）。
- eth0（仅当禁用一致性命名或传统 BIOS 时保留）。

当然，在 CentOS 7 中，也可以恢复旧版网卡文件名称。步骤如下

### 修改 CentOS 7 网卡名为 eth0

```bash
# 备份并修改配置文件
cp -a ifcfg-ens33 ifcfg-eth0
sed -i 's/NAME=ens33/NAME=eth0/' ifcfg-eth0
sed -i 's/DEVICE=ens33/DEVICE=eth0/' ifcfg-eth0

# 修改grub配置
vi /etc/default/grub
# 添加参数: net.ifnames=0 biosdevname=0
GRUB_CMDLINE_LINUX="crashkernel=auto rhgb quiet net.ifnames=0 biosdevname=0"

# 更新grub并重启
grub2-mkconfig -o /boot/grub2/grub.cfg
reboot
```

虽然网卡文件名不同，但文件内容却基本相同，所以修改网卡文件名后，配置文件内容也必须修改。

配置文件内容示例

```bash
TYPE=Ethernet
PROXY_METHOD=none
BROWSER_ONLY=no
BOOTPROTO=static
DEFROUTE=yes
IPV4_FAILURE_FATAL=no
IPV6INIT=yes
IPV6_AUTOCONF=yes
IPV6_DEFROUTE=yes
IPV6_FAILURE_FATAL=no
IPV6_ADDR_GEN_MODE=stable-privacy
NAME=ens33
UUID=2a5a6694-f7c7-46f6-a223-4ec416b1e2cf
DEVICE=ens33
ONBOOT=yes
PREFIX=24
IPADDR=192.168.1.100
GATEWAY=192.168.1.1
NETMASK=255.255.255.0
DNS1=192.168.1.1
```

详细说明：

- TYPE=Ethernet : 指定网络接口的类型。Ethernet表示这是一个以太网接口。对于绝大多数有线网卡，此值保持不变。
- PROXY_METHOD=none : 指定代理配置方法。none表示此接口不使用任何代理自动配置（如 PAC）。这个参数通常不需要修改。
- BROWSER_ONLY=no : 与 PROXY_METHOD相关。no表示代理设置不仅适用于浏览器，也适用于系统范围内的所有网络流量。通常保持默认值 no。
- BOOTPROTO=static : 定义接口如何获取其 IP 地址。
  - static或 none: 表示使用手动配置的静态 IP 地址（如示例中的 IPADDR, NETMASK等）。
  - dhcp: 表示通过 DHCP 服务器自动获取 IP 地址、子网掩码、网关等配置。
  - bootp: 使用较旧的 BOOTP 协议获取配置。
- DEFROUTE=yes : 定义此接口是否为系统的默认路由（默认网关）。
  - yes: 表示通过此接口配置的网关（GATEWAY）将被设置为系统的默认路由。通常主网卡会设置为 yes。
  - no: 表示此接口的网关不作为默认路由。
- IPV4_FAILURE_FATAL=no : 定义 IPv4 配置失败是否视为严重错误。
  - no: 如果此接口的 IPv4 配置失败（例如，设置了静态 IP 但该 IP 冲突），系统不会完全终止网络服务，可能会继续尝试启动接口或回退。
  - yes: 如果 IPv4 配置失败，则此接口的启动会失败。
- IPV6INIT=yes : 定义此接口是否为系统的默认路由（默认网关）。yes: 启用接口的 IPv6 支持。no: 禁用接口的 IPv6 支持。
- IPV6_AUTOCONF=yes : 是否在此接口上使用 IPv6 的无状态地址自动配置（SLAAC）。当 yes时，接口会监听路由器广播（RA）并自动生成 IPv6 地址。
- IPV6_DEFROUTE=yes : 类似于 DEFROUTE，但针对 IPv6。定义是否通过此接口接收的 IPv6 路由器广播（RA）来设置默认路由。
- IPV6_FAILURE_FATAL=no : 类似于 IPV4_FAILURE_FATAL，但针对 IPv6。定义 IPv6 配置失败是否视为严重错误。
- IPV6_ADDR_GEN_MODE=stable-privacy : 定义 IPv6 地址的生成模式，主要用于保护隐私。
  - stable-privacy: 基于网络前缀和一个稳定生成的秘密密钥生成地址，每次重启后保持不变，但不同网络生成的地址不同。这是较新且推荐的安全和隐私模式。
  - eui64: 使用传统的基于接口 MAC 地址的模式生成后缀。
- NAME=ens33 : 为此连接配置定义一个描述性的名称。这是一个逻辑名称，通常由用户或网络管理工具（如 NetworkManager）设置，用于易于识别。它可以与物理设备名（DEVICE）不同。
- UUID=2a5a6694-f7c7-46f6-a223-4ec416b1e2cf : 此连接配置的唯一标识符（Universally Unique Identifier）。由系统自动生成，用于唯一识别这个特定的连接配置文件。通常不需要手动修改。
- DEVICE=ens33 : 指定此配置文件所应用到的物理或逻辑网络设备的名称。这个名称必须与系统实际识别到的设备名一致（可通过 ip link命令查看），例如 ens33, eth0, enp0s3等。这是非常重要的参数。
- ONBOOT=yes : 定义是否在系统启动时自动激活此网络接口。
  - yes: 开机自动启动。
  - no: 开机不启动，需要手动激活（例如使用 ifup <device-name>命令）。
- PREFIX=24 : 定义 IPv4 地址的子网掩码，使用 CIDR 表示法。24等同于 255.255.255.0。它与 NETMASK参数是等价的，通常只需设置其中一个（现代配置更推荐使用 PREFIX）。
- IPADDR=192.168.1.100 : 为此接口设置的静态 IPv4 地址。只有当 BOOTPROTO 为 static 或 none 时，此参数才有效。
- GATEWAY=192.168.1.1 : 设置系统的默认网关 IPv4 地址。通常指向你的路由器地址。只有当 BOOTPROTO 为 static 或 none，并且 DEFROUTE=yes 时，此参数才有效。
- NETMASK=255.255.255.0 : 定义 IPv4 地址的子网掩码，使用传统的点分十进制表示法。它与 PREFIX 参数功能相同，通常只需设置其中一个。如果同时设置了 PREFIX 和 NETMASK，它们必须匹配。
- DNS1=192.168.1.1 : 设置主 DNS 服务器的 IPv4 地址。可以指定多个 DNS 服务器，参数名依次为 DNS2, DNS3 等。这些 DNS 设置通常会被写入 /etc/resolv.conf 文件。注意：如果系统使用了 NetworkManager 且其配置为管理 resolv.conf，这里的设置可能会被覆盖。

### 网卡操作

- ifup： Interface UP。用于启用一个网络接口，根据其配置文件为其配置IP地址、子网掩码、网关、DNS等所有网络参数，并将其启动。
- ifdown： Interface DOWN。用于禁用一个网络接口，清除其IP地址等配置，并将其关闭。

它们通过读取定义在 /etc/sysconfig/network-scripts/(CentOS/RHEL/Fedora) 或 /etc/network/interfaces(Debian/Ubuntu) 等位置的配置文件来工作。

```bash
sudo ifup <网络接口名>
sudo ifdown <网络接口名>
```

必须使用 sudo或 root 权限，因为配置网络接口是系统级操作。

```bash
# 启用网卡 eth0
sudo ifup eth0

# 禁用网卡 eth0
sudo ifdown eth0

# 启用网卡 ens33（常见于新版本Linux）
sudo ifup ens33

# 禁用网卡 ens33
sudo ifdown ens33
```

**工作原理与配置文件**

ifup和 ifdown本身是脚本，它们会去调用底层的 ip link set、ip addr add等 iproute2命令，并读取预定义的配置文件。

1. **在 RHEL/CentOS/Fedora 系**

配置文件目录：/etc/sysconfig/network-scripts/

每个接口有一个对应的配置文件，命名格式为：ifcfg-<接口名>

例如，网卡 ens33 的配置文件是 /etc/sysconfig/network-scripts/ifcfg-ens33。

当你执行 sudo ifup ens33时，系统会：

- 读取 /etc/sysconfig/network-scripts/ifcfg-ens33文件。
- 发现 BOOTPROTO=dhcp，于是调用 dhclient程序向路由器请求一个IP地址。
- 配置获取到的IP地址和路由到 ens33网卡。
- 将网卡状态设置为 UP。

2. **在 Debian/Ubuntu 系**

主配置文件通常是：/etc/network/interfaces

一个典型的配置内容可能如下：

```bash
# 环回接口
auto lo
iface lo inet loopback

# 主网卡，使用DHCP自动获取
auto ens33
iface ens33 inet dhcp

# 另一个网卡，配置静态IP
auto ens34
iface ens34 inet static
    address 192.168.1.100
    netmask 255.255.255.0
    gateway 192.168.1.1
    dns-nameservers 8.8.8.8
```

ifup 和 ifdown 会读取这个文件来管理对应的接口。

**常见使用场景与示例**

你编辑了网卡的配置文件，将 BOOTPROTO从 dhcp改为了 static并设置了固定IP。无需重启电脑，只需：

```bash
sudo ifdown ens33  # 先关闭接口
sudo ifup ens33    # 再重新启用接口，新的配置即会生效
```

**注意事项与现代替代方案**

ifup/ifdown 已逐渐被淘汰：在现代化的 Linux 发行版（如 RHEL 8/CentOS 8、最新的Ubuntu）中，传统的 network-scripts 包已被弃用，转而使用 NetworkManager 服务及其配套工具（如 nmcli, nmtui）。

检查工具是否存在：如果你的系统没有 ifup 和 ifdown 命令，说明它可能完全使用 NetworkManager。此时应使用 nmcli：

```bash
# 启用连接
sudo nmcli connection up <connection-name>
# 禁用连接
sudo nmcli connection down <connection-name>
```

重启网络服务：更彻底的方法是重启整个网络服务（在传统系统上）：

```bash
sudo systemctl restart network
```

或者在基于 Netplan 的 Ubuntu 系统上：

```bash
sudo netplan apply
```

值得注意的是，在生产环境中应避免直接使用 `sudo systemctl restart network` 这类命令，因为它们会重启全部网卡，这可能会导致网络中断。在多网卡环境下，应使用 `sudo nmcli connection up <connection-name>` 命令来重启某个网卡，而不是重启整个网络服务。

### 查看本机IP地址

```bash
ip addr show        # 推荐（CentOS 7+/Ubuntu）
ifconfig           # 传统命令（需安装 net-tools）
```

### 查看本机MAC地址

```bash
ip link show                  # 查看所有网卡MAC
sudo ip link set dev eth0 down
sudo ip link set dev eth0 address 00:11:22:33:44:55  # 临时修改MAC
sudo ip link set dev eth0 up
```

## 网关

## 网关与路由

- **网关**: 连接不同网段的通讯设备
- **路由**: 数据从源到目的的路径选择规则

### 路由表查看与配置

```bash
# 查看路由表
route -n
ip route show

# 添加临时默认网关
route add default gw 192.168.1.1

# 永久设置网关(修改网卡配置文件)
GATEWAY=192.168.1.1
```

## Linux下网络管理命令

### DNS配置
**配置文件**:
- 全局: `/etc/resolv.conf`  
  `nameserver 8.8.8.8`
- 局部: 网卡配置文件中的`DNS1=8.8.8.8`
- 本地解析: `/etc/hosts`

**测试命令**:
```bash
nslookup www.baidu.com  # 域名解析测试
dig @8.8.8.8 www.baidu.com  # 详细DNS查询
```

### 网络状态查看
```bash
# 查看所有连接
netstat -tulnp
ss -an

# 查看接口信息
ifconfig
ip addr show

# 查看路由表
route -n
ip route
```

### 网络诊断工具
```bash
# 连通性测试
ping -c 4 -i 0.5 www.baidu.com  # 发送4个数据包，间隔0.5秒

# 路径追踪
traceroute www.baidu.com
mtr www.baidu.com  # 实时路由追踪

# 端口扫描(需安装nmap)
nmap -sP 192.168.1.0/24  # 探测网段存活主机
nmap -sT 192.168.1.1  # 扫描TCP端口
```






















域名解析服务(DNS)
动态主机配置服务(DHCP)
文件传输服务(FTP/Samba)
网络文件系统服务(NFS)
万维网服务(Apache/Nginx/Tomcat)
邮件服务(Mail)
日志服务(ELK)
数据备份服务(Rsync)
数据库服务(MySQL/Redis)


## DNS 服务器及其集群搭建

## 常用端口及服务

| 端口 | 服务 | 用途 |
|------|------|------|
| 20/21 | FTP | 文件传输 |
| 22 | SSH | 安全远程管理 |
| 23 | Telnet | 不安全远程管理 |
| 25 | SMTP | 邮件发送 |
| 80 | HTTP | 网页服务 |
| 443 | HTTPS | 加密网页服务 |
| 3306 | MySQL | 数据库连接 |
| 53 | DNS | 域名解析 |

**端口配置文件**: `/etc/services`

# 用户管理

## 用户信息

在 Linux 系统中，系统的用户信息存储在 /etc/passwd 文件中，其内容示例如下：

```bash
root:x:0:0:root:/root:/bin/bash
bin:x:1:1:bin:/bin:/sbin/nologin
daemon:x:2:2:daemon:/sbin:/sbin/nologin
adm:x:3:4:adm:/var/adm:/sbin/nologin
lp:x:4:7:lp:/var/spool/lpd:/sbin/nologin
sync:x:5:0:sync:/sbin:/bin/sync
shutdown:x:6:0:shutdown:/sbin:/sbin/shutdown
halt:x:7:0:halt:/sbin:/sbin/halt
mail:x:8:12:mail:/var/spool/mail:/sbin/nologin
operator:x:11:0:operator:/root:/sbin/nologin
games:x:12:100:games:/usr/games:/sbin/nologin
ftp:x:14:50:FTP User:/var/ftp:/sbin/nologin
nobody:x:99:99:Nobody:/:/sbin/nologin
systemd-network:x:192:192:systemd Network Management:/:/sbin/nologin
dbus:x:81:81:System message bus:/:/sbin/nologin
polkitd:x:999:998:User for polkitd:/:/sbin/nologin
sshd:x:74:74:Privilege-separated SSH:/var/empty/sshd:/sbin/nologin
postfix:x:89:89::/var/spool/postfix:/sbin/nologin
chrony:x:998:996::/var/lib/chrony:/sbin/nologin
```

其中，每一行表示一个用户信息，格式为：

```bash
username:password:userid:groupid:comment:home directory:shell
```

每项的含义如下：
- username: 用户名
- password: 密码位
- userid: 用户id
  - 在 Linux 中，0 表示超级用户，将一个用户设置为超级用户时，将 userid 设置为 0 即可，不过不建议建立多个超级用户
  - 1-499 为系统用户（伪用户），这些用户是不能登录系统的，而是由系统服务使用，其中 1-99 为系统保留用户，系统自动创建，100-499是预留给用户创建系统账号的。
  - 500-60000为普通用户，建立的用户id从500开始，从2.6.x内核开始，Linux 系统用户id已经可以支持2^32这么多了。
- groupid: 用户组id，建立用户时，若不指定用户所属组，则用户组id默认与用户id一致
- comment: 描述
- home directory: 主目录
- shell: 登录后默认使用的 shell 程序所在位置

在现代的 CentOS 和 Ubuntu 发行版中，UID 范围的划分发生了一些变化，更通用的标准是

- 0: 超级用户 (root)。UID 0 是最高权限的唯一标识。
- 1-999: 系统用户 (System users)。这些用户通常由软件包在安装时自动创建，用于运行系统服务或守护进程。他们通常没有登录 Shell（如 /usr/sbin/nologin或 /bin/false），这是为了系统安全。
- 1000+: 普通用户 (Regular users)。在系统安装后，由管理员创建的第一个普通用户的 UID 通常从 1000 开始，之后创建的用户会依次递增（1001, 1002...）。

**关于密码位 (重要安全概念)**

在非常古老的系统上，加密后的密码确实直接存储在这个字段。出于安全原因，现在所有的现代 Linux 系统都使用了 shadow password suite。这里的 x是一个占位符，它意味着用户的加密密码实际上并不存储在 /etc/passwd文件中，而是被移动到了 /etc/shadow 文件里。

而 /etc/shadow 文件权限是 000(rw-------，仅 root 可读)，这比全世界可读的 /etc/passwd(权限为 644) 要安全得多。

## 用户密码

用户密码文件存储在 /etc/shadow 文件中，其内容示例如下：

```bash
root:$6$7mDpR5mc1Py9qAKz$YiJYJ6OLas2RONTVugT7KV3TClFjEA/ahFgrrCvPGlUch57wObt3ic6sxC0H13ssY9Xo6xSlw4Hc.BC9xwdZD/::0:99999:7:::
bin:*:18353:0:99999:7:::
daemon:*:18353:0:99999:7:::
adm:*:18353:0:99999:7:::
lp:*:18353:0:99999:7:::
sync:*:18353:0:99999:7:::
shutdown:*:18353:0:99999:7:::
halt:*:18353:0:99999:7:::
mail:*:18353:0:99999:7:::
operator:*:18353:0:99999:7:::
games:*:18353:0:99999:7:::
ftp:*:18353:0:99999:7:::
nobody:*:18353:0:99999:7:::
systemd-network:!!:20353::::::
dbus:!!:20353::::::
polkitd:!!:20353::::::
sshd:!!:20353::::::
postfix:!!:20353::::::
chrony:!!:20353::::::
```

其中，每一行表示一个用户密码信息，格式为：
```bash
username:password:last change time:minimum time:maximum time:warn time:inactivity time:expire time:reserved
```

其中，每一项的含义如下：

- username: 用户名
- password: 加密后的密码
  - 密码为空时，表示用户密码为空，即用户不能登录系统
  - 我们也可以在密码前手动添加 "!" 或 “*"，来使密码暂时失效，达到临时禁用密码的效果。
  - 注意所有的伪用户的密码都是 “!!”, 表示密码为空, 不能登录系统,新创建的用户密码也是 “!!"
- last change time: 密码最后修改时间(时间戳)，以 1970-01-01 为起点，单位为天
- minimum time: 密码最小修改时间，两次密码修改间隔时间
- maximum time: 密码最大修改时间，密码有效期
- warn time: 密码修改到期前多少天发出警告
- inactivity time: 密码过期后宽限天数
- expire time: 密码失效时间，这里也为时间戳格式，如果超过了失效时间，即使密码没有过期，用户也无法登录系统
- reserved: 保留字段

**时间戳转换示例**:
```bash
# 将Shadow中的“天数”时间戳转换为日期
# 例如：last change time 为 15775
date -d "1970-01-01 + 15775 days" +%F
# 输出：2013-03-11

# 将日期转换为Shadow中的“天数”时间戳
# 例如：想知道 2020-03-11 对应的时间戳
echo $(($(date -d "2020-03-11" +%s) / 86400))
# 输出：18336
```

**加密密码字段解析**

加密密码本身的格式，它遵循一个标准的命名法，格式：$id$salt$hash

- id : 表示所采用的加密算法。 
  - $1$： MD5 (现在被认为不安全，应避免使用)
  - $2a$或 $2y$： Blowfish
  - $5$： SHA-256
  - $6$： SHA-512 (这是示例中 root用户使用的，也是现代 Linux 系统的默认标准，更安全)
- salt： 一个随机字符串，用于防止彩虹表攻击。即使两个用户密码相同，加密后的哈希值也会因为 salt 不同而完全不同。
- hash： 将用户的密码明文与 salt 组合后，再经过上述算法加密得到的最终哈希值。

**关于 !! 与 * 的细微区别**

- *通常用于系统账户（如 bin, daemon），表示该账户完全被锁定，不能用于密码认证。passwd -l <username>命令会在加密密码前加 !!，但有些系统实现可能会使用 *。
- !!通常表示密码从未被设置过。这正是 useradd 命令创建新用户后的默认状态。运行 passwd -l <username> 也会使用这个。
- 本质上，只要加密密码字段不是以 $id$ 开头，而是任何其他字符，该账户的密码认证就会被阻止。所以 !, *, !! 甚至 invalid 都可以达到禁用效果。

虽然可以直接编辑 /etc/shadow 文件来管理密码，但这非常危险，容易出错导致用户无法登录。更安全、更推荐的方法是使用专用命令：

`chage`： 用于查看和修改密码过期策略的强大工具。
- `chage -l <username>`： 列出用户的密码策略详情。
- `chage -M 90 <username>`： 将密码最大有效期设置为 90 天。
- `chage -E $(date -d "+180 days" +%F) <username>`： 设置账户在 180 天后过期。

`passwd`： 用于管理密码本身。
- `passwd -S <username>`： 查看用户的密码状态（`PS`为已设置，`LK`为被锁定，`NP`无密码）。
- `passwd -l <username>`/ `passwd -u <username>`： 锁定/解锁用户密码。

## 用户组信息

用户组用于管理用户权限，组内用户可以进行权限共享，建立用户时，若不指定用户所属组，则用户组id默认与用户id一致，这个与用户同名的组，且 GID 等于 UID 的组，被称为用户的主组 (Primary Group) 或 初始组 (Initial Group)。

一个用户还可以被加入到其他多个附加组 (Supplementary Groups) 中，这些关系记录在 /etc/group 文件里。加入附加组是为了获取该组所拥有的额外权限（例如，将用户加入 sudo组以授予管理权限，加入 docker组以允许使用 Docker 引擎）。

用户组信息文件存储在 /etc/group 文件中，其内容示例如下：

```bash
root:x:0:
bin:x:1:
daemon:x:2:
sys:x:3:
adm:x:4:
tty:x:5:
disk:x:6:
lp:x:7:
mem:x:8:
kmem:x:9:
wheel:x:10:
cdrom:x:11:
mail:x:12:postfix
man:x:15:
dialout:x:18:
floppy:x:19:
games:x:20:
tape:x:33:
video:x:39:
ftp:x:50:
lock:x:54:
audio:x:63:
nobody:x:99:
users:x:100:
utmp:x:22:
utempter:x:35:
input:x:999:
systemd-journal:x:190:
systemd-network:x:192:
dbus:x:81:
polkitd:x:998:
ssh_keys:x:997:
sshd:x:74:
postdrop:x:90:
postfix:x:89:
chrony:x:996:
```

其中，每一行表示一个用户组信息，格式为：

```bash
groupname:password:groupid:username1,username2,username3...
```

每项的含义如下：
- groupname: 用户组名
- password: 密码位
- groupid: 用户组id
- username1,username2,username3...: 该用户组的附加成员（Supplementary Members）列表。这些用户以该组作为他们的附加组,这个字段并不定义谁的主组是这个组。 一个用户的主组（Primary Group） 是由 /etc/passwd 文件中的 GID字段决定的。

组密码和 /etc/passwd一样，这里的 x也是一个占位符，表示真正的组密码（如果设置了的话）存储在 /etc/gshadow文件中。然而，在现代 Linux 系统管理中，为组设置密码是一种非常古老且极不安全的做法，强烈不推荐使用。几乎 100% 的情况下，这个字段都是 x。管理权限的方式是通过 sudo策略，而不是组密码。可以认为这个功能已经被废弃。

特权组 wheel与 sudo

- 在 CentOS / RHEL / Fedora 等 Red Hat 系发行版中，默认的特权组名称是 wheel。
- 在 Ubuntu / Debian 等发行版中，默认的特权组名称是 sudo。
- 它们的目的是完全一样的：被列入该组的用户可以使用 sudo 命令获得 root 权限。这只是历史遗留造成的命名差异。

## 其他用户相关目录信息

- 用户家目录 : 默认位于`/home/用户名`,超级用户 root 的家目录是 /root 家目录的权限通常设置为 755(drwxr-xr-x)，所有者是用户本身。这确保了用户对自己目录有完全权限，而其他用户只能进入和列表，但不能创建或删除文件。
  - 家目录下存放着用户的个性化配置文件，通常以点号开头（隐藏文件），如：
  - ~/.bashrc: Bash shell 的配置脚本。
  - ~/.bash_profile 或 ~/.profile: 登录时的配置脚本
  - ~/.ssh/: 存放SSH密钥的目录。
- 用户邮箱目录 : 默认位于 `/var/spool/mail/用户名`。在Linux系统中，每个用户都有一个默认的本地邮箱，用于接收系统发送的通知、mail命令发送的邮件或由 cron任务产生的输出。除非你专门配置了本地邮件传输代理（如Postfix, Sendmail）或在服务器上运行需要发送本地通知的服务，否则这个邮箱对于现代桌面用户或许多服务器用户来说可能很少被主动使用。但系统关键警报仍可能发送到这里。可以使用 mail命令来查看此邮箱的内容。
- 用户模板目录 : 默认位于 `/etc/skel` 新建用户时会复制此目录内容到用户家目录，这是系统管理员进行标准化配置的利器。你可以预先在 `/etc/skel` 目录中放置一些文件，这样所有新创建的用户都能拥有一致的初始环境。

## 创建用户

创建用户可以使用 useradd 命令，例如：

命令语法：

useradd [options] <username>

**常用选项**:

- `-u`: 指定UID
- `-g`: 指定初始组，在实际操作中，我们通常不指定 -g [GID]，而是指定 -g [组名]。系统会自动为你分配一个与用户同名且 GID 与 UID 相同的主组。手动指定 GID 容易造成混乱。更常见的做法是省略 -g选项，或者使用 -g users这样的组名来指定一个已存在的组作为主组。
- `-G`: 指定附加组
- `-c`: 添加用户说明
- `-d`: 指定家目录(绝对路径)
- `-s`: 指定登录shell
- `-m`: 指定创建用户时自动创建家目录，如果没有 -m 选项，useradd默认不会创建家目录（即使 CREATE_HOME yes在 /etc/login.defs中设置了）
- `--create-home`: 与 -m 选项功能相同，创建用户时自动创建家目录

创建用户示例：

```bash
useradd -u 1000 -g 1000 -G sudo -c "Linux User" -d /home/linuxuser -s /bin/bash 
```

命令含义：
- `-u 1000`: 设置用户UID为1000
- `-g 1000`: 设置用户GID为1000
- `-G sudo`: 将用户加入sudo组
- `-c "Linux User"`: 添加用户说明
- `-d /home/linuxuser`: 设置用户家目录为/home/linuxuser
- `-s /bin/bash`: 设置用户登录shell为/bin/bash

创建用户时，系统会使用一些默认值，这些默认值主要存储在 /etc/default/useradd 和 /etc/login.defs 文件中。

下面分别看下文件内容

/etc/default/useradd

```bash
# useradd defaults file
GROUP=100
HOME=/home
INACTIVE=-1
EXPIRE=
SHELL=/bin/bash
SKEL=/etc/skel
CREATE_MAIL_SPOOL=yes
```

其中每项的含义如下：
- GROUP: 创建的用户的初始组ID，默认为100，这个配置项在现代 Linux 发行版中通常被注释掉或不再起主要作用。用户主组的创建行为主要由 /etc/login.defs中的 USERGROUPS_ENAB控制。当 USERGROUPS_ENAB yes时，useradd会创建一个与用户同名的新组作为其主组，这是默认且推荐的行为。GROUP=100是一个历史遗留的备用选项。
- HOME: 创建用户的家目录，默认为/home
- INACTIVE: 创建用户的密码失效时间，默认为-1，表示密码永不过期
- EXPIRE: 创建用户的密码过期时间，默认为空
- SHELL: 创建用户的登录shell，默认为/bin/bash
- SKEL: 创建用户时复制的模板目录，默认为/etc/skel
- CREATE_MAIL_SPOOL: 创建用户时是否创建邮箱目录，默认为yes

/etc/login.defs

```bash
#
# Please note that the parameters in this configuration file control the
# behavior of the tools from the shadow-utils component. None of these
# tools uses the PAM mechanism, and the utilities that use PAM (such as the
# passwd command) should therefore be configured elsewhere. Refer to
# /etc/pam.d/system-auth for more information.
#

# *REQUIRED*
#   Directory where mailboxes reside, _or_ name of file, relative to the
#   home directory.  If you _do_ define both, MAIL_DIR takes precedence.
#   QMAIL_DIR is for Qmail
#
#QMAIL_DIR      Maildir
MAIL_DIR        /var/spool/mail
#MAIL_FILE      .mail

# Password aging controls:
#
#       PASS_MAX_DAYS   Maximum number of days a password may be used.
#       PASS_MIN_DAYS   Minimum number of days allowed between password changes.
#       PASS_MIN_LEN    Minimum acceptable password length.
#       PASS_WARN_AGE   Number of days warning given before a password expires.
#
PASS_MAX_DAYS   99999
PASS_MIN_DAYS   0
PASS_MIN_LEN    5
PASS_WARN_AGE   7

#
# Min/max values for automatic uid selection in useradd
#
UID_MIN                  1000
UID_MAX                 60000
# System accounts
SYS_UID_MIN               201
SYS_UID_MAX               999

#
# Min/max values for automatic gid selection in groupadd
#
GID_MIN                  1000
GID_MAX                 60000
# System accounts
SYS_GID_MIN               201
SYS_GID_MAX               999

#
# If defined, this command is run when removing a user.
# It should remove any at/cron/print jobs etc. owned by
# the user to be removed (passed as the first argument).
#
#USERDEL_CMD    /usr/sbin/userdel_local

#
# If useradd should create home directories for users by default
# On RH systems, we do. This option is overridden with the -m flag on
# useradd command line.
#
CREATE_HOME     yes

# The permission mask is initialized to this value. If not specified,
# the permission mask will be initialized to 022.
UMASK           077

# This enables userdel to remove user groups if no members exist.
#
USERGROUPS_ENAB yes

# Use SHA512 to encrypt password.
ENCRYPT_METHOD SHA512
```

其中每项的含义如下：

- MAIL_DIR        /var/spool/mail : 指定用户默认的邮件目录
- PASS_MAX_DAYS   99999: 密码最大有效期，代表多少天之后必须修改密码，默认为99999
- PASS_MIN_DAYS   0: 指定两次修改密码之间的间隔天数，代表第一次修改密码之后，第二次修改密码之前，必须间隔多少天才能修改密码，默认为0，表示没有限制。
- PASS_MIN_LEN    5: 密码最小长度，默认为5，但是现在用户登录时的验证已经被 PAM 模块所取代，所以这个选项不生效。
- PASS_WARN_AGE   7: 密码过期提醒，指定多少天之前密码过期，系统会提醒用户修改密码，默认为7天。
- UID_MIN                  1000: 创建用户的最小UID，默认为1000
- UID_MAX                 60000: 创建用户最大的UID，默认为60000
- GID_MIN                  1000: 创建组的最小GID，默认为1000
- GID_MAX                 60000: 创建组最大的GID，默认为60000
- SYS_UID_MIN               201: 系统用户的最小UID，默认为201
- SYS_UID_MAX               999: 系统用户最大的UID，默认为999
- SYS_GID_MIN               201: 系统组的最小GID，默认为201
- SYS_GID_MAX               999: 系统组最大的GID，默认为999
- CREATE_HOME     yes: 创建用户时是否创建家目录，默认为yes
- UMASK           077: 创建用户家目录的权限，默认为077
- USERGROUPS_ENAB yes: 这是实现“为用户创建同名主组”功能的开关。如果设置为 no，并且没有指定 -g选项，用户可能会被添加到 /etc/default/useradd中定义的 GROUP中（通常是 users 组），这是一种不太常用的模式。
- ENCRYPT_METHOD SHA512: 密码加密方式，默认为SHA512

**useradd 与 adduser 的区别**

这是一个非常常见的困惑点，尤其是在 Ubuntu 和 Debian 上：
- useradd：是一个底层的、可脚本化的二进制命令。它只严格按照你提供的选项和系统默认值来执行操作，没有交互式提示。你上面学习的就是这个命令。
- adduser：是一个高层的、perl 编写的交互式脚本（在 Ubuntu/Debian 上）。它更加“友好”：
  - 它会主动提示你输入密码、全名等信息。
  - 它默认会自动创建家目录 (-m)。
  - 它会提示你确认信息是否正确。
  - 在 CentOS/RHEL 上，adduser 仅仅是 useradd的一个软链接（symbolic link），所以两者是完全一样的。

## 设置用户密码

创建用户时，用户密码默认为空，可以通过以下命令设置用户密码，命令语法为：

```bash
passwd [options] [username]
```
其中，username 为用户名，options 为选项，可以有如下选项：
- -d: 删除用户密码,使其可无密码登录
- -e: 强制用户密码过期,强制用户下次登录时必须更改密码
- -l: 锁定用户
- -u: 解锁用户
- --stdin: 从标准输入读取密码（非标准选项，常见于CentOS，Ubuntu中不存在）

**示例**:

创建用户 alice，并设置密码为 <PASSWORD>

```bash
# 设置用户密码
echo "123" | passwd --stdin user1
# 强制用户首次登录修改密码
chage -d 0 user1
```
chage 命令是管理密码和账户过期策略的标准工具

- chage -l user1：列出用户的所有密码和账户过期信息。
- chage -M 90 user1：设置密码最长为90天。
- chage -E $(date -d "+6 months" +%Y-%m-%d) user1：设置账户在6个月后过期。

## 修改用户信息

修改用户信息可以通过以下命令完成，命令语法为：

```bash
usermod [options] [username]
```

其中，username 为用户名，options 为选项，可以有如下选项：

- `-u`: 修改UID
- `-d`: 修改家目录
- `-c`: 修改用户说明
- `-g`: 修改初始组
- `-G`: 修改附加组
- `-s`: 修改登录shell
- `-e`: 修改失效日期(YYYY-MM-DD)
- `-L`: 锁定用户
- `-U`: 解锁用户
- `-l`: 修改用户名(不推荐)

## 删除用户

删除用户可以通过以下命令完成，命令语法为：

```bash
userdel [options] [username]
```

其中，username 为用户名，options 为选项，可以有如下选项：

- `-r`: 同时删除用户家目录和邮箱

**删除用户时的常见问题与解决方案**

无法删除用户，提示“user is currently used by process”
原因：该用户仍有正在运行的进程
解决方案：
找到并终止所有属于该用户的进程。使用 ps或 pgrep命令查找，然后用 kill或 killall终止。
```bash
# 查找用户 'username' 的所有进程
pgrep -u username
# 或
ps -aux | grep username

# 强制终止用户 'username' 的所有进程
sudo killall -9 -u username
# 或使用 pkill
sudo pkill -9 -u username
```
终止进程后，再执行 userdel命令。

**关于用户文件的清理**

userdel(即使使用 -r选项) 并不会删除属于该用户的其他文件。

例如，如果该用户在系统其他位置（如 /tmp、/var/www等）创建或拥有文件，这些文件将继续保留在磁盘上，但其所有者会变为一个不再存在的 UID（数字）。

最佳实践：在删除用户后，可以使用 find命令来查找并清理这些“无主文件”。

```bash
# 查找所有属于已删除用户（UID为1001）的文件
sudo find / -user 1001 -exec ls -l {} \;

# 找到后，你可以决定是删除它们还是更改其所有者
# 例如，删除所有属于UID 1001的文件（请极度谨慎！）
# sudo find / -user 1001 -exec rm -rf {} \;
```

**用户组的关系**

如果该用户是一个用户组的主要成员（即该组是通过 useradd自动创建的、与用户同名的组），并且 USERGROUPS_ENAB在 /etc/login.defs中设置为 yes（默认），那么使用 userdel -r通常会同时删除这个同名的用户组，因为该组已经没有其他成员了。

如果该用户只是某个已存在组的附加成员，删除用户不会影响这些组的存在。

## 切换用户身份

切换用户身份可以通过以下命令完成，命令语法为：

```bash
su [options] [username]
```
其中，username 为用户名，options 为选项，可以有如下选项：

- `-`: 连带环境变量一起切换
- `-c`: 仅执行一次命令，用于以另一个用户的身份执行单条命令然后立刻返回。这在脚本中非常有用。

**示例**:
```bash
# 切换用户并执行命令
su - user1 -c "ls -l"
```

在 Ubuntu 中，默认情况下，root 用户密码未启用。初始用户自动拥有 sudo 权限。执行管理任务的唯一方式就是使用 sudo。
在 CentOS 中，在安装时会要求你设置 root 密码。初始用户默认没有 sudo 权限，除非你在安装时特意指定或之后手动配置。

**示例**:
```bash
# 使用 sudo 安装软件（Ubuntu 和 CentOS 的通用最佳实践）
sudo apt update        # Ubuntu
sudo dnf update        # CentOS 8+
sudo yum update        # CentOS 7

# 使用 sudo 启动一个 root shell（如果确实需要）
sudo -i                # 相当于 'su - root'，但使用自己的密码且操作被记录
sudo -s                # 相当于 'su root'，启动一个非登录的 root shell
```

## 创建用户组

创建用户组可以通过以下命令完成，命令语法为：

```bash
groupadd [options] [groupname]
```
其中，groupname 为用户组名，options 为选项，可以有如下选项：

- `-g`: 指定GID

## 删除用户组

删除用户组可以通过以下命令完成，命令语法为：
```bash
groupdel [options] [groupname]
```
其中，groupname 为用户组名，options 为选项，可以有如下选项：
- `-f`: 强制删除，即使它仍是某些用户的主组。这是一个非常危险的操作，可能会导致那些用户出现问题，应避免使用。

> 注意: 不能删除作为其他用户初始组的组

## 管理组成员

添加组成员可以通过以下命令完成，命令语法为：
```bash
gpasswd [options] [groupname] [username]
```
其中，groupname 为用户组名，username 为用户名，options 为选项，可以有如下选项：

- `-a 用户名`: 添加用户到组
- `-d 用户名`: 从组中删除用户

**示例**:
```bash
# 添加用户到组
gpasswd -a user1 grouptest
# 从组中删除用户
gpasswd -d user1 grouptest
```

```bash
# 两种方法都可以将 user1 添加到 grouptest (附加组)
sudo gpasswd -a user1 grouptest  # 方法一：以组为中心
sudo usermod -aG grouptest user1 # 方法二：以用户为中心 (更常用)

# 查看 user1 属于哪些组
groups user1
```

## 改变有效组

newgrp 命令用于临时切换用户的有效主组（effective primary group），从而影响新创建文件的属组。，命令语法为：

newgrp会启动一个新的子 Shell。当你完成操作后，需要输入 exit来退出这个子 Shell，才能返回到原来的组环境。

```bash
newgrp [groupname]
```
其中，groupname 为用户组名

**示例**:
```bash
# 创建测试组
groupadd group1

# 添加用户到组
gpasswd -a user1 group1

# 切换用户
su - user1

# 创建文件(属组为初始组)
touch test1

# 切换有效组
newgrp group1

# 创建文件(属组为当基本前有效组)
touch test2

# 查看结果
ll test1 test2
```

# 权限管理

在 Linux 系统中，文件权限由四个基本部分组成，分别为基本权限（UGO 权限），ACL 权限（访问控制列表），SUID/SGID/Sticky Bit 权限和安全增强工具（SELinux/AppArmor）组成。

## 文件基本权限（UGO 权限）

文件基本权限主要用于限制用户对文件的访问控制，在 Linux 系统中，用户可以拥有的权限主要可以包含两大类：
- 基于管理员权限的系统管理员，此类用户拥有对文件的绝对控制权，文件基本权限对其限制意义不大，该类用户以 root 用户为代表。
- 除管理员权限之外的其他用户都为普通用户，也是文件基本权限的主要限制对象。

在 Linux 系统中，创建文件时，文件即会记录当前用户为文件所有者，用户所在主组为文件所属组，一般来说，每个文件都会有这两个属性。

特殊情况下，当用户/组被删除后，文件仍保留原 UID/GID（显示为数字）。由于用户或组已经被删除了，文件会丢失所属用户和组，这种文件被称为幽灵文件。

文件基本权限由 3 组权限组成，分别为用户权限（U），组权限（G），其他用户权限（O）。其分别代表文件所有者对文件的权限，文件所属组用户对文件的权限和其他用户对文件的权限。

通过 ls -all 命令可以列出文件夹下的所有文件和文件夹信息，输出示例如下：

```bash
total 16
drwxr-xr-x   2 root root 4096 2023-10-27 14:30 .
drwxr-xr-x 12 root root 4096 2023-10-27 14:30 ..
-rw-r--r--   1 root root   23 2023-10-27 14:30 file.txt
-rw-r--r--   1 root root   23 2023-10-27 14:30 file2.txt
```

其中第一项为文件类型及权限信息，其输出格式为：

```bash
[文件类型][所有者权限][组权限][其他用户权限]
```

其中第一位是文件类型，d 表示目录，- 表示普通文件，l 表示符号链接，b 表示块设备，c 表示字符设备，p 表示管道。

示例：

| 符号 | 类型               | 示例         |
| :--- | :----------------- | :----------- |
| `d`  | 目录               | `drwxr-xr-x` |
| `-`  | 普通文件           | `-rw-r--r--` |
| `l`  | 符号链接           | `lrwxrwxrwx` |
| `b`  | 块设备（如磁盘）   | `brw-rw----` |
| `c`  | 字符设备（如终端） | `crw--w----` |
| `p`  | 命名管道（FIFO）   | `prw-r--r--` |
| `s`  | 套接字文件         | `srwxrwxrwx` |

后面跟着的九位字符表示文件权限，其含义为：
- `r`: 读权限，对于文件夹来说，表示进入文件夹的权限
- `w`: 写权限，对于文件夹来说，表示创建、删除、修改文件名的权限
- `x`: 执行权限，对于文件夹来说，表示进入文件夹的权限
- `-`: 无权限

系统管理员用户可以变更任意文件的权限，普通用户只能改变属于自己的文件的权限。

### 修改文件所属用户和组

修改文件所属用户和组

```bash
chown root:root orphan_file # 重新分配属主
```

修改所有者

```bash
chown alice file.txt      # 将文件所有者改为 alice（组不变）
```

仅修改组（需冒号）

```bash
chown :developers file.txt       # 仅修改组（所有者不变）
# 等效于 chgrp developers file.txt
```

递归修改（目录及内容）

```bash
chown -R alice:developers /project/  # 递归修改目录下所有文件和子目录
```

root 用户

```bash
chown 0:0 file.txt        # 所有者/组均改为 root
```

### 处理幽灵文件

查找所有幽灵文件

```bash
find / -nouser -o -nogroup  # 查找幽灵文件
```

对于幽灵文件进行处理，直接使用数字（适用于用户/组已删除的情况）

```bash
chown 1001:1002 file.txt  # UID=1001，GID=1002
```

批量修复

```bash
find /path -nouser -exec chown root {} \;      # 无主文件归属 root
find /path -nogroup -exec chown :root {} \;    # 无组文件归属 root 组
```

### 提权

当访问权限不足时，可以通过提权来解决，提权通过 sudo 命令来实现，示例：

```bash
sudo chown user:group file      # 提权执行
```

### 修改文件权限

修改文件权限可以通过 chmod 命令来实现，基本语法为：

```bash
chmod [选项] <权限模式> <文件或目录>
```

其中权限模式 可以是 数字形式（如 755） 或 符号形式（如 u+x）。

常用选项
- -R：递归修改目录及其子目录和文件。
- -v：显示修改的详细信息。
- --preserve-root：防止误操作 /（默认启用）。

权限模式

数字形式（八进制模式）

权限用 3 位八进制数 表示，每位对应 所有者（Owner）、组（Group）、其他用户（Others） 的权限：

| 数字 | 权限  | 二进制 | 说明               |
| :--- | :---- | :----- | :----------------- |
| `0`  | `---` | `000`  | 无权限             |
| `1`  | `--x` | `001`  | 仅执行             |
| `2`  | `-w-` | `010`  | 仅写入             |
| `3`  | `-wx` | `011`  | 写入 + 执行        |
| `4`  | `r--` | `100`  | 仅读取             |
| `5`  | `r-x` | `101`  | 读取 + 执行        |
| `6`  | `rw-` | `110`  | 读取 + 写入        |
| `7`  | `rwx` | `111`  | 读取 + 写入 + 执行 |

示例：

```bash
chmod 755 test.txt   # rwxr-xr-x（所有者：rwx，组和其他：r-x）
chmod 644 test.txt   # rw-r--r--（所有者：rw，组和其他：r）
chmod 600 test.txt   # rw-------（仅所有者可读写）
```

符号形式（ugo模式）

- 用户类别：
  - u（所有者）、g（组）、o（其他）、a（所有用户）。
- 操作符：
  - +（添加权限）、-（移除权限）、=（设置权限）。
- 权限
  - r（读）、w（写）、x（执行）。 

示例

```bash
chmod u+x test.txt    # 给所有者添加执行权限
chmod g-w test.txt    # 移除组的写入权限
chmod o=rx test.txt   # 设置其他用户权限为 r-x
chmod a+r test.txt    # 所有用户添加读权限
```

递归修改目录权限

```bash
chmod -R 755 /path/to/dir   # 递归修改目录及其子文件权限
```

常见问题

chmod无效？

1. 文件系统挂载为只读（mount -o remount,rw /修复）。
2. 文件被设为不可变（chattr +i保护）。
3. 当前用户无权限（需 sudo）。

检查方法

```bash
lsattr test.txt      # 检查是否被 chattr +i 保护
mount | grep "ro,"   # 检查文件系统是否只读
```

## ACL 权限（访问控制列表）

ACL 权限主要是用于解决用户对文件身份不足的问题的，比如

要使用 ACL 权限，必须得到文件系统的支持，默认需要开启该功能才能使用。

### 开启 ACL

# 软件安装

在 Linux 系统中，软件的安装包通常分为两种：

1. 二进制包：其类似于 windows 中的 exe 文件, 用于直接安装, 这种安装方式比较容易，但是无法进行定制化安装。二进制包一般分为 RPM 和 DPKG 两种格式。
2. 源码包：其为源码的压缩包，需要本地解压并编译安装。这种安装方式可以进行定制化安装，但是需要一定的技能。且一般本地手动安装的程序由于更适配本地环境，其运行效率通常比二进制包安装的程序要高。

在现代软件中，发行商提供的二进制包（如 Ubuntu 官方 apt 源中的包）通常是由专家使用为该发行版优化过的编译器和编译选项（如 -O2, -march=x86-64）进行构建的，其优化水平很高。

普通用户自己编译，通常只是简单地执行 ./configure && make && make install，使用的很可能是默认的、保守的编译选项，很难超越发行版专家优化后的成果。

只有在极少数特定场景下，例如您需要为您的特定 CPU 架构（如 -march=native）进行极致优化，或者需要精确地裁剪掉不需要的功能来减少内存占用，自己编译才可能在性能上带来微弱的优势。

源码编译的主要优势在于极致的定制能力（例如：启用/禁用某个特定实验性功能、指定安装路径、链接到特定版本的库），而不是性能。其代价是巨大的维护成本（需要手动解决依赖、手动升级和卸载）。

## RPM 包安装

RPM 包命名规则，以 Apache 的 httpd (PHP应用服务器) 为例 

```
httpd-2.4.6-1.el7.x86_64.rpm
```

其中
- httpd：软件名称
- 2.4.6-1：软件版本，2.4.6 表示的软件版本，1 表示软件发布的次数，它代表用同一份源码（2.4.6）打包第几次。这个数字的增加通常意味着打包者修改了编译参数、打了补丁、更新了打包脚本等，但软件本身的功能特性没有变化。
- el7：软件构建的版本，el7 表示这个包是为 RHEL (Red Hat Enterprise Linux) 7 及其兼容发行版（如 CentOS 7, Scientific Linux 7）构建的。el 代表 "Enterprise Linux"。这是一个非常重要的标识，因为它指明了包所依赖的**基础库的版本**。例如，一个为 `el7`构建的包，会依赖 `el7`系统仓库里提供的 `glibc`, `openssl`等库的版本。如果你试图把它安装到 `el9`系统上，很可能会因为库版本不兼容而失败。
- x86_64：软件适合的硬件平台。RPM 包可以在不同的硬件平台安装，选择适合不同CPU的软件版本，可以最大化的发挥 CPU 性能，所以出现了所谓的 i386(386 以上计算机都可以安装)、i586(586以上的计算机都可以安装)、i686(奔腾 II 以上计算机都可以安装，目前所有的 CPU 都是奔腾 II 以上，所以这个软件版本居多)、x86_64(64 位 CPU可以安装)和 noarch(没有硬件限制)等文件名了。如今，`i386`, `i586`, `i686`这些 32 位架构的包已经非常少见，主要用于兼容一些非常古老的遗留程序。**主流的服务器和桌面环境几乎全部是 `x86_64`(AMD64/Intel 64) 架构。**
- rpm：软件包格式

### RPM 命令

RPM 命令用于安装、升级、卸载 RPM 包，其基本的语法格式如下：

```bash
rpm [选项] [软件包名|软件包文件]
```

其中，`[选项]`用于指定操作类型和附加功能，主要分为以下几大类：

#### 安装、升级、卸载选项（需 root 权限）

这些是修改系统状态的操作，通常需要 `sudo`或 `root`用户身份。

| 选项     | 全称        | 功能说明                                                     |
| :------- | :---------- | :----------------------------------------------------------- |
| **`-i`** | `--install` | **安装**一个 RPM 包。                                        |
| **`-U`** | `--upgrade` | **升级**一个包。如果该包未安装，则执行安装。                 |
| **`-F`** | `--freshen` | **更新**一个包。**只有在该包已安装的情况下才进行升级**，否则什么都不做。 |
| **`-e`** | `--erase`   | **卸载/删除**一个已安装的包。                                |
| **`-v`** | `--verbose` | 显示详细的处理信息。                                         |
| **`-h`** | `--hash`    | 在安装或升级时显示进度条（用 `#`表示）。                     |

**常用组合：**

- **安装并查看进度**：`rpm -ivh package.rpm`
- **升级并查看进度**：`rpm -Uvh package.rpm`

**RPM 安装高级参数**

这些参数通常与安装/升级选项（如 `-i`， `-U`）结合使用，格式为：`rpm -ivh [参数] 包名.rpm`

1. **依赖关系处理参数**

- **`--nodeps`**：
  - **作用**：在安装或升级包时，**不检查**（跳过）依赖关系验证。
  - **风险与场景**：这是**高风险操作**。即使依赖不满足，软件包也会被安装，但几乎必然导致软件无法启动或运行。**仅用于极端情况**，例如你确信依赖已满足（但包管理器无法识别），或者你只想要包里的某个文件。
- **`--test`**：
  - **作用**：**测试模式**。不执行实际安装，只检查依赖关系是否满足以及是否存在冲突。
  - **场景**：在真正安装之前，用于“预演”安装过程，确认一切正常。这是一个**安全且推荐**的排查步骤。

2. **文件与包冲突处理参数**

- **`--replacefiles`**：
  - **作用**：允许安装程序**覆盖**属于其他已安装软件包的文件。
  - **场景**：当安装包中的某个文件已被系统上的其他软件包占用时，使用此选项强制覆盖。**需谨慎**，因为这可能会破坏另一个依赖该文件的软件。
- **`--replacepkgs`**：
  - **作用**：允许**重新安装**一个已经存在的同名软件包。
  - **场景**：当软件包已安装，但其文件可能被意外损坏或丢失时，可以用此选项重新安装一遍来修复。

- **`--force`**：
  - **作用**：**强制安装**。这是 `--replacefiles`和 `--replacepkgs`的**组合**，功能非常强大。
  - **场景**：相当于告诉 RPM“无论遇到文件冲突还是包已存在，都给我强行装上”。这是**最高风险的参数之一**，除非你非常清楚后果，否则应避免使用。

3. **安装路径参数**

- **`--prefix <新路径>`**：**指定一个自定义的安装目录**，而不是软件包预设的默认路径（如 `/usr`）。

并非所有 RPM 包都支持此参数。只有在制作 RPM 包时被设计为“可重定位”的包才有效。
**强烈不推荐使用**。因为将软件安装到非标准路径（如 `/opt/mypackage`）会导致：

1. **系统无法自动识别**：可执行文件不在 `$PATH`中，库文件不在库路径中，手册页不在 `man`路径中。
2. **管理困难**：需要手动配置环境变量、链接文件等，失去了包管理器统一管理的优势。

#### 查询选项（无需 root 权限）

这些操作只是查询信息，不会修改系统，普通用户也可执行。

| 选项            | 全称            | 功能说明                                             |
| :-------------- | :-------------- | :--------------------------------------------------- |
| **`-q`**        | `--query`       | **查询**模式，所有查询操作的基础。                   |
| **`-a`**        | `--all`         | 查询**所有**已安装的包。                             |
| **`-f`**        | `--file`        | 查询某个**文件**属于哪个已安装的包。                 |
| **`-p`**        | `--package`     | 查询一个**尚未安装的 RPM 包文件**的信息。            |
| **`-l`**        | `--list`        | 列出软件包安装的所有**文件列表**。                   |
| **`-i`**        | `--info`        | 显示软件包的**详细信息**（版本、发布时间、描述等）。 |
| **`-c`**        | `--configfiles` | 只列出软件包的**配置文件**。                         |
| **`-d`**        | `--docfiles`    | 只列出软件包的**文档文件**。                         |
| **`--scripts`** |                 | 列出软件包在安装、卸载等过程中执行的**脚本**。       |

**常用组合：**

- 查询已安装的 `httpd` 包的信息：`rpm -qi httpd`
- 查询系统中所有已安装的包：`rpm -qa`
- 查询 `/etc/httpd/conf/httpd.conf` 文件属于哪个包：`rpm -qf /etc/httpd/conf/httpd.conf`
- 查询一个未安装的包文件 `package.rpm`里包含哪些文件：`rpm -qpl package.rpm`
- 查询已安装的 `httpd`包生成了哪些配置文件：`rpm -qc httpd`

#### 验证选项

用于验证已安装软件包的文件属性（如权限、MD5 校验和、大小等）是否与原始包中的信息一致。

| 选项     | 全称       | 功能说明                 |
| :------- | :--------- | :----------------------- |
| **`-V`** | `--verify` | **验证**一个已安装的包。 |

**常用命令：**

- 验证 `httpd`包：`rpm -V httpd`
- 如果输出为空，则表示所有文件验证通过。如果有输出，则列出属性变化的文件（属性变化用单个字符表示，如 `S`=文件大小改变，`M`=权限模式改变，`5`=MD5 校验和改变等）。

**示例输出：**

```bash
S.5....T. c /etc/httpd/conf/httpd.conf
```

这条输出可以分解为三个部分：

1. **验证结果（8个字符）**：`S.5....T.`

- 这8个位置分别代表对文件8种不同属性的检查结果。一个点 `.`表示该项验证**通过**，出现字母则表示该项**未通过**（即文件发生了改变）。
- 根据示例：
  - `S`：文件大小已被修改。
  - `5`：文件的 MD5 校验和（即文件内容）已被修改。
  - `T`：文件的修改时间已被改变。
- **完整属性列表**：
  - `S`- 文件大小
  - `M`- 文件权限和文件类型
  - `5`- MD5 校验和（文件内容）
  - `D`- 设备代码
  - `L`- 文件路径（符号链接）
  - `U`- 文件所有者
  - `G`- 文件所属组
  - `T`- 修改时间

2. **文件类型**：`c`

这个字母标明了文件的类型。`c`表示这是一个**配置文件**。

**其他常见文件类型**：

- `d`- 普通文档
- `g`- “鬼”文件（不应被RPM包包含的文件）
- `l`- 授权文件
- `r`- 描述文件

3. **文件名**：`/etc/httpd/conf/httpd.conf`

被验证的文件的绝对路径。

其中，如果操作的是未安装软件包，则使用包全名，而且需要注意绝对路径。如果操作的是已经安装的软件包，则使用包名即可，系统会生产 RPM 包的数据库( /var/lib/pmy )，而且可以在任意路径下操作。

#### 数字证书

上述的校验方法只能对已安装的 RPM 包中的文件进行校验，但对于未安装的 RPM 包本身，如何验证其属性（如权限、大小、修改时间、MD5 校验和等）呢？

我们在 CentOS/RHEL 系统中使用数字证书（GPG 密钥）来验证 RPM 软件包完整性与真实性，其核心目标是确保您安装的软件包来自可信的发行商（如 CentOS 官方），并且在传输过程中未被篡改。

数字证书的验证遵循如下步骤：

- 首先必须找到原厂的公钥文件，然后进行安装
- 在安装 RPM 包时，会去提取 RPM包中的证书信息，然后和本机安装的原厂证书进行验证
- 如果验证通过，则允许安装；如果验证不通过，则不允许安装并警告

整个验证过程可以概括为以下流程图

```mermaid
flowchart TD
    A[RPM包数字证书验证流程] --> B[“第一步: 准备阶段<br>获取发行商公钥”]
    
    B --> C[“来源一<br>系统默认位置”]
    B --> D[“来源二<br>安装光盘”]
    
    C & D --> E[“第二步: 导入公钥<br>执行 rpm --import 命令”]
    E --> F[“第三步: 验证机制<br>安装RPM包时自动触发”]
    F --> G{“证书签名比对”}
    G -- 匹配 --> H[“验证成功<br>允许安装”]
    G -- 不匹配 --> I[“验证失败<br>拒绝安装并警告”]
```

**第一步：准备阶段 - 获取发行商公钥**

数字证书（即发行商的 GPG 公钥文件）通常存在于两个位置：

- **系统默认位置**：如 `/etc/pki/rpm-gpg/`目录下。系统在安装时可能已经预置。
- **安装光盘中**：如 CentOS 6.3 光盘根目录下的 `RPM-GPG-KEY-CentOS-6`文件。

**第二步：操作阶段 - 将公钥导入 RPM 数据库**

获取公钥文件后，需要将其导入到系统的 RPM 数据库中，这样 RPM 命令才能使用它进行验证。

- **命令**：`rpm --import [公钥文件路径]`
- **示例**：

```bash
rpm --import /etc/pki/rpm-gpg/RPM-GPG-KEY-CentOS-6
```

**第三步：验证阶段 - 安装时自动验证签名**

此后，当您使用 `rpm`或 `yum`安装软件包时，验证过程会自动触发：

**查询系统中已导入的数字证书**

安装成功后，您可以使用以下命令查看所有已导入的公钥：

```bash
rpm -qa | grep gpg-pubkey
```

输出示例：`gpg-pubkey-c105b9de-4e0fd3a3`（这里的长字符串是公钥的唯一标识）。

#### 提取 RPM 包中的文件

想象一下，如果现在系统中已安装的某个软件中的某个文件由于某种原因丢失了，那么修复该文件的方式有两种，一种是将软件卸载，然后重新安装，另一种是将 RPM 包中的该文件提取提取出来，然后将提取出来的文件复制到丢失的文件所在位置。

一般的，我们如果需要提取 RPM 包中的文件，会使用 cpio 工具。但该工具不能直接用 `cpio` 读取 RPM 文件，**必须先将 RPM 包转换**成 `cpio`归档格式。这个转换过程可以使用 `rpm2cpio` 命令完成。

`rpm2cpio`是一个专门用于处理 RPM 包的核心工具，它的作用非常明确：**将 RPM 格式的软件包转换成标准的 `cpio`归档格式**。这个命令本身并不直接解压文件，而是为后续使用 `cpio`命令提取内容做准备。

`rpm2cpio`**不生成任何文件**！它只是将转换后的数据流**打印到终端**。要实际提取文件，必须通过管道 (`|`) 将其输出传递给 `cpio`命令。

语法：

```bash
rpm2cpio [选项] <RPM文件>
```

**示例**：

```bash
rpm2cpio nginx-1.20.1-1.el7.x86_64.rpm
```

这条命令会将 nginxRPM 包的内容以 cpio 格式输出到屏幕（通常是一堆乱码，因为这是二进制数据流）。

提取特定文件

```bash
rpm2cpio package.rpm | cpio -idmv "./path/to/specific/file"
```

在 cpio命令后指定需要提取的文件路径（支持通配符 *）。

示例：只提取 nginx的默认配置文件：

```bash
rpm2cpio nginx.rpm | cpio -idmv "./etc/nginx/nginx.conf"
```

提取的文件会还原 RPM 包中的完整路径结构，但**相对于当前工作目录**。例如：

- 若 RPM 包内有 `/usr/bin/foo`，当前目录是 `/home/user`，
- 提取后文件路径为 `/home/user/usr/bin/foo`。

### 安装位置

RPM 包在进行制作时，会遵循一定的规范，比如其安装位置通常是固定的。其安装路径大致如下：

- **`/etc/`** 一般用于存放安装软件的配置文件。这是系统级配置文件的标准位置。安装 RPM 包时，其主配置文件（如 `httpd.conf`）和相关配置片段（如 `conf.d/`目录下的文件）通常放在这里。升级 RPM 包时，配置文件通常会保留（根据 `rpm`的配置处理策略）。
- **`/usr/bin/`** 存放**普通用户可执行文件**（命令）。这是大多数用户级命令的标准位置。例如，安装 `vim`后，`vim`命令的可执行文件就在这里。此目录在 `$PATH`环境变量中，用户可以直接在终端运行这些命令。
- **`/usr/lib/ 和 /usr/lib64/`** 存放**程序使用的共享库文件（`.so`文件）和模块**。
  - **`/usr/lib/`**：传统上存放 **32位** 系统的库文件。在纯 64 位系统上，也可能存放一些与架构无关的数据或某些特定库。
  - **`/usr/lib64/`**：在 **64位** Linux 系统（如 x86_64）上，存放 **64位** 共享库文件的标准位置。
- **`/usr/share/doc`** 存放**软件包相关的文档**。通常包括 `README`, `INSTALL`, `LICENSE`, `CHANGELOG`, `AUTHORS`等文件，以及示例文件或更详细的说明文档。
- **`/usr/share/`**：存放**与架构无关的只读数据**。
  - **`/usr/share/applications/`** (桌面应用的 `.desktop`文件)。
  - **`/usr/share/icons/`** (图标)。
  - **`/usr/share/locale/`** (本地化/翻译文件)。
  - 软件特定的数据文件（如 `/usr/share/nginx/html/`是 Nginx 的默认网页根目录）。
- **`/usr/share/man`** 存放软件使用手册。使用 `man`命令（如 `man ls`, `man httpd.conf`, `man systemctl`）查看的就是这里的文件。其结构按章节组织（如 `man1/`, `man5/`, `man8/`）：
  - `man1`：用户命令手册。
  - `man5`：配置文件格式手册。
  - `man8`：系统管理员命令手册。
- **`/var/`**：存放**运行时数据**（Variable Data）。
  - **`/var/log/`**：存放**日志文件**。例如，Apache 的日志默认在 `/var/log/httpd/`。
  - **`/var/lib/`**：存放**状态信息和数据库**。例如，MySQL 的数据文件默认在 `/var/lib/mysql/`；RPM 数据库本身也在 `/var/lib/rpm/`。
  - **`/var/cache/`**：存放**缓存数据**。例如，`yum`/`dnf`下载的 RPM 包缓存通常在 `/var/cache/yum/`或 `/var/cache/dnf/`。
  - **`/var/run/`** 或 `/run/`：存放**运行时临时文件**（如 PID 文件）。现代系统通常使用 `/run/`（一个内存文件系统）。
- **`/usr/sbin/`**：存放**系统管理员使用的可执行文件**（通常是需要 `root`权限的命令）。例如，`httpd`服务的主程序、`iptables`、`fdisk`等命令通常安装在这里。此目录也在 `root`用户的 `$PATH`中。
- **`/lib/ 和 /lib64/`**：存放**系统启动和根文件系统所需的关键共享库和内核模块**。类似于 `/usr/lib[64]/`，但这里的库是系统运行所必需的（在 `/usr`挂载之前就需要）。`/lib`(32位库) 和 `/lib64`(64位库) 是 `/usr/lib[64]/`的符号链接（在现代发行版如 CentOS 7+/Ubuntu 中）。

这些路径是 Linux **FHS** 定义的，所有遵循规范的软件（无论是 RPM、DEB 还是源码编译安装）都应尽量遵守。这保证了系统的一致性和可管理性。

且在进行 RPM 包安装时，系统会自动对其产生的安装文件进行管理，这通常会存储在系统的数据库中，用于后续卸载等操作使用

当使用 `rpm -i`或 `yum/dnf install`安装 RPM 包时，RPM 系统会在一个专门的数据库中记录这个包的所有"账本信息"。其默认存储在 /var/lib/rpm/ 目录下，这个目录下包含多个数据库文件（如 `Packages`, `Name`, `Requirename`等），它们使用 Berkeley DB 格式存储。其记录的信息包括：

1. **软件包元数据**：名称、版本、发行号、架构、描述等。
2. **文件清单**：该软件包安装的**每一个文件**的完整路径。
3. **依赖关系**：该软件包依赖哪些其他包或库。
4. **校验和信息**：每个安装文件初始的 MD5 校验和、文件大小、权限、属主等信息。
5. **安装脚本**：包在安装前（`pre`）、安装后（`post`）、卸载前（`preun`）、卸载后（`postun`）要执行的脚本。

其主要作用为：

1. **精确卸载（`rpm -e`）**

- 当您卸载一个包时，RPM 会查询数据库，**精确删除该包安装的所有文件**（配置文件除外，通常会提示保留）。
- 这与源码编译安装形成鲜明对比：编译安装时，文件可能散落在 `/usr/local/`的各个子目录中，很难彻底、干净地手动删除。

2. **查询功能（`rpm -q`）**

- 您可以查询任何一个文件属于哪个包：`rpm -qf /path/to/file`
- 您可以列出某个包安装的所有文件：`rpm -ql package-name`
- 您可以查看已安装包的信息：`rpm -qi package-name`

3. **验证功能（`rpm -V`）**

- 系统可以比较当前文件的状态与数据库中记录的初始状态（校验和、大小、权限等）。
- 如果文件被意外修改、损坏或篡改，`rpm -V`命令会报告差异。这是系统安全性和完整性检查的一个重要工具。

4. **依赖关系解决（主要由 yum/dnf 使用）**

- 高级包管理器（`yum`, `dnf`）会查询这个数据库以及远程仓库的数据库，来分析并解决复杂的依赖关系。

`/var/lib/rpm/`数据库在极少数情况下（如系统突然断电）可能损坏。如果遇到 `rpm`命令报数据库相关的错误，可以尝试重建数据库（**操作前务必备份！**）：

```bash
sudo rpm --rebuilddb
```

通过源码编译（`make install`）或其他方式（如直接复制二进制文件）安装的软件，**不会被记录在 RPM 数据库中**。因此，您无法使用 `rpm `命令来管理或卸载它们。

### 软件启动及状态查看

在 CentOS 6.x 系统中，软件的启动通常依赖`service`命令来启动、停止、重启和查看状态。在 CentOS 7.x 中，软件启动依赖 `systemctl`命令。

| 操作行为 | CentOS 6.x | CentOS 7.x |
|----------|------------|------------|
| 启动服务 | `service 服务名 start` | `systemctl start 服务名` |
| 关闭服务 | `service 服务名 stop` | `systemctl stop 服务名` |
| 重启服务 | `service 服务名 restart` | `systemctl restart 服务名` |
| 查看状态 | `service 服务名 status` | `systemctl status 服务名` |
| 所有服务状态 | `service --status-all` | `systemctl list-units` |
| 设置自启动 | `chkconfig 服务名 on` | `systemctl enable 服务名` |
| 取消自启动 | `chkconfig 服务名 off` | `systemctl disable 服务名` |
| 查看自启动状态 | `chkconfig --list` | `systemctl list-unit-files` |

其中 **`systemctl list-units`**：这个命令会列出所有**当前已加载和活动**的单元（包括服务、挂载点、套接字等），信息量较大。

**更常用、更精确的等价命令**是：

```bash
systemctl list-units --type=service
```

这个命令**专门列出所有服务的状态**，与 `service --status-all`的针对性更接近。

**`systemctl list-unit-files`**：会列出**所有类型**单元（服务、挂载点、套接字、设备等）的启用状态。

**更常用、更精确的等价命令**是：

```bash
systemctl list-unit-files --type=service
```

这个命令**专门列出所有服务的开机自启状态**。

systemd 的功能远比 SysVinit 强大，因此有一些在 CentOS 6 时代没有的、极其有用的新命令：

| 操作行为             | CentOS 7.x (`systemctl`)      | 说明                                                         |
| :------------------- | :---------------------------- | :----------------------------------------------------------- |
| **重新加载配置**     | `systemctl reload 服务名`     | 不重启服务，只重新加载服务的配置文件。对需要保持连接的服务（如 Nginx、Apache）非常有用。 |
| **查看服务是否启用** | `systemctl is-enabled 服务名` | 直接返回服务是否设置为开机自启（`enabled`/`disabled`），用于脚本判断。 |
| **查看服务是否活动** | `systemctl is-active 服务名`  | 直接返回服务当前是否正在运行（`active`/`inactive`），用于脚本判断。 |
| **屏蔽一个服务**     | `systemctl mask 服务名`       | **更强力的禁用**。不仅取消自启，甚至禁止手动启动。会创建一个到 `/dev/null`的符号链接，彻底“屏蔽”该服务。 |
| **取消屏蔽服务**     | `systemctl unmask 服务名`     | 解除 `mask`的操作。                                          |
| **查看服务日志**     | `journalctl -u 服务名`        | 使用 `journalctl`命令查看特定服务的日志，这是 systemd 统一的日志系统。 |

在 CentOS 7 和 8 中，为了保持向后兼容，**`service`和 `chkconfig`命令依然存在**。

- 当你执行 `service httpd start`时，系统实际上会在底层将其翻译成 `systemctl start httpd.service`来执行。
- 同样，`chkconfig httpd on` 也会被翻译成 `systemctl enable httpd.service`。

### yum 安装

在真实场景中，我们几乎永远不会直接从官网下载一个这样的 .rpm 文件然后用 rpm -ivh 命令来安装。因为软件会有复杂的依赖关系（Dependencies）。httpd 可能依赖 httpd-tools, apr, apr-util 等特定的版本。手动安装时，需要自己一个一个找到并安装所有这些依赖包，这是一个极其繁琐且容易出错的“依赖地狱”过程。

**正确的做法是使用包管理器（Package Manager）：**

**在 CentOS/RHEL/Fedora (RPM系) 上**：

- `yum`(CentOS 7)
- `dnf`(CentOS 8+, Fedora，是 `yum`的下一代版本)

包管理器（`yum`/`dnf`）维护着一个巨大的**软件仓库（Repository）** 数据库。当你执行 `sudo yum install httpd`时，它会：

1. 自动在配置的仓库列表里查找名为 `httpd`的软件。
2. 分析出 `httpd`的所有依赖包（如 `apr`, `apr-util`等）。
3. **自动下载并安装所有必需的软件包**。
4. 未来还可以方便地通过 `yum update`来更新所有软件。





















