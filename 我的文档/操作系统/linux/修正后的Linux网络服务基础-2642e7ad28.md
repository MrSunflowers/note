# 尚硅谷 云计算Linux 课程系列：Linux网络基础

## 1 知识回顾

### 网络地址
- **IP地址**：互联网协议地址，为网络或主机分配逻辑地址，工作在网络层
- **MAC地址**：物理地址，设备固定地址，工作在链路层  
  示例：`00-23-5A-15-99-42`

### 协议分类
| 协议层级 | 常见协议 |
|----------|----------|
| 应用层 | FTP、HTTP、SMTP、Telnet、DNS |
| 传输层 | TCP、UDP |
| 网络层 | IP、ICMP、ARP |
| 数据链路层 | PPP协议 |
| 物理层 | 不常用 |

### 常见端口
| 端口 | 服务 | 说明 |
|------|------|------|
| 20/21 | ftp服务 | 文件共享 |
| 22 | ssh服务 | 安全远程管理 |
| 23 | telnet服务 | 不安全远程管理 |
| 25 | smtp | 发信 |
| 465 | smtp(ssl) | 加密发信 |
| 110 | pop3 | 收信 |
| 143 | imap4 | 收信 |
| 993 | imap4 (ssl) | 加密收信 |
| 80 | http | 网页访问 |
| 443 | https | 加密网页访问 |
| 3306 | mysql | 数据库连接 |
| 53 | DNS | 域名解析 |

## 2 常见网络配置

### 配置方式对比
| 配置类型 | 特点 | 适用场景 |
|----------|------|----------|
| 临时配置 | 命令调整，立即生效，重启失效 | 网络调试 |
| 固定配置 | 修改配置文件，需重启服务 | 服务器固定参数 |

> **注意**：CentOS 7.x支持多数命令永久生效，减少手动修改配置文件错误率

## 2.1 IP地址配置

### 临时配置命令
```bash
# 设置IP和子网掩码
ifconfig eth0 192.168.12.250 netmask 255.255.255.0
# 简写形式
ifconfig eth0 192.168.12.250/24
```

### 永久配置文件
**路径**：`/etc/sysconfig/network-scripts/ifcfg-eth0`
```ini
DEVICE=eth0          # 设备名称
NAME=eth0            # 网卡名称
BOOTPROTO=static     # 静态IP
ONBOOT=yes           # 开机加载
IPADDR=192.168.12.250 # IP地址
NETMASK=255.255.255.0 # 子网掩码(PREFIX=24)
GATEWAY=192.168.12.1  # 网关
DNS1=8.8.8.8         # DNS服务器
```

### 网络服务控制
```bash
# 重启网络服务
service network restart
# 启停网卡
ifup eth0    # 启动网卡
ifdown eth0  # 关闭网卡
```

## 2.2 主机名配置
```bash
# 临时生效
hostname 主机名
# 永久生效
vi /etc/sysconfig/network
```

## 2.3 网关配置
```bash
# 查看路由表
route
# 临时添加默认网关
route add default gw 192.168.12.1
# 临时删除默认网关
route del default gw 192.168.12.1
# 永久配置
vi /etc/sysconfig/network-scripts/ifcfg-eth0  # 添加GATEWAY参数
```

## 2.4 DNS配置

### 局部配置（单网卡）
```ini
# 在ifcfg-eth0中添加
DNS1=8.8.8.8
DNS2=114.114.114.114
```

### 全局配置
```bash
vi /etc/resolv.conf
# 添加
nameserver 8.8.8.8
nameserver 114.114.114.114
```

### DNS测试
```bash
nslookup www.atguigu.com
```

### 主机映射
**文件**：`/etc/hosts`  
作用：本地IP与主机名映射，优先级高于DNS  
格式：`IP地址 主机名 别名`

## 3 网络常用命令

### 3.1 网络状态查看：netstat
```bash
# 常用选项组合
netstat -antulp  # 显示所有TCP/UDP连接，包含进程信息
```
| 选项 | 含义 |
|------|------|
| -a | 显示所有活动连接 |
| -n | 数字形式显示地址和端口 |
| -t | 仅显示TCP协议 |
| -u | 仅显示UDP协议 |
| -p | 显示进程PID和名称 |
| -l | 仅显示监听状态连接 |

### 3.2 路径追踪：traceroute
```bash
# 基本用法
traceroute www.atguigu.com
# 常用选项
traceroute -n -q 3 -p 80 www.atguigu.com
```
| 选项 | 含义 |
|------|------|
| -n | 不解析主机名（加快速度） |
| -q 3 | 每个节点发送3个探测包 |
| -p 80 | 使用UDP 80端口测试 |

> **注意**：NAT模式下可能无法正常工作，建议使用桥接模式

### 3.3 连通性测试：ping
```bash
# 基本用法
ping www.atguigu.com
# 常用选项
ping -c 4 -i 0.5 -s 1024 www.atguigu.com
```
| 选项 | 含义 |
|------|------|
| -c 4 | 发送4个探测包 |
| -i 0.5 | 间隔0.5秒发送一次 |
| -s 1024 | 数据包大小为1024字节 |

### 3.4 地址解析：arp
```bash
# 查看ARP缓存
arp -a
# 删除指定IP的ARP记录
arp -d 192.168.12.1
```

### 3.5 网络扫描：nmap
```bash
# 扫描网段存活主机
nmap -sP 192.168.12.0/24
# 扫描指定主机开放端口
nmap -sT 192.168.12.250
```

## 4 远程管理工具

| 场景 | 工具/命令 | 特点 |
|------|-----------|------|
| Windows→Linux | Xshell、SecureCRT | 图形界面，功能丰富 |
| Linux→Windows | rdesktop | 图形界面远程 |
| Linux→Linux | ssh命令 | 字符界面，安全高效 |

## 5 SSH安全远程管理

### 5.1 SSH协议简介
- **全称**：Secure Shell
- **层级**：应用层协议
- **默认端口**：22
- **特点**：加密传输，防止信息泄露
- **适用系统**：大多数UNIX和类UNIX系统

### 5.2 登录验证模式

#### 密码验证流程
1. 客户端发送连接请求
2. 服务器返回公钥
3. 客户端用公钥加密密码并发送
4. 服务器解密验证，建立连接
```bash
# 登录命令
ssh 用户名@IP地址
ssh root@192.168.88.20
```

#### 密钥对验证流程
![SSH密钥对验证流程](https://p3-flow-imagex-sign.byteimg.com/ocean-cloud-tos/pdf/0a1e484a47023a297f592bf676b21480_4_1200.jpg~tplv-a9rns2rl98-resize-crop:502:194:565:277:63:83.jpeg?rk3s=1567c5c4&x-expires=1790258110&x-signature=CLcEXm3axtv4ggJ1DV1lxw77PNQ%3D)

### 5.3 配置SSH服务

#### 环境准备
```bash
# 临时关闭防火墙和SELinux
iptables -F
setenforce 0

# 永久关闭（需重启）
chkconfig iptables off
sed -i '7s/enforcing/disabled/' /etc/selinux/config
```

#### 密钥对验证配置步骤

1. **客户端生成密钥对**
```bash
ssh-keygen -t rsa -b 2048
# 参数说明
# -t：指定加密类型（rsa/dsa）
# -b：密钥长度（2048位）
```
> 生成过程中可设置密钥保存路径和密码保护

2. **上传公钥到服务器**
```bash
ssh-copy-id root@192.168.88.20
```

3. **密钥对登录测试**
```bash
ssh root@192.168.88.20
```

#### 安全加固配置
**配置文件**：`/etc/ssh/sshd_config`

| 安全措施 | 配置项 | 建议值 |
|----------|--------|--------|
| 禁止密码登录 | PasswordAuthentication | no |
| 禁止root登录 | PermitRootLogin | no |
| 修改默认端口 | Port | 59527（高位端口） |
| 限制监听IP | ListenAddress | 192.168.88.100 |

```bash
# 重启SSH服务使配置生效
service sshd restart
# 测试非默认端口登录
ssh -p 59527 root@192.168.88.20
```

### 5.4 SSH相关工具

#### SCP远程复制
```bash
# 本地文件复制到远程
scp /root/file.txt root@192.168.88.20:/tmp
# 远程文件复制到本地
scp root@192.168.88.20:/tmp/file.txt /root
# 指定端口
scp -P 59527 /root/file.txt root@192.168.88.20:/tmp
```

#### SFTP文件传输
```bash
# 连接服务器
sftp root@192.168.88.20
# 指定端口
sftp -oPort=59527 root@192.168.88.20
```
**常用交互命令**：
- `put 本地文件 远程路径`：上传文件
- `get 远程文件 本地路径`：下载文件
- `ls/lls`：查看远程/本地目录
- `pwd/lpwd`：查看远程/本地路径
- `quit`：退出连接

## 6 TCP Wrappers（简单防火墙）

### 6.1 简介
- **工作层级**：传输层（TCP）
- **实现方式**：通过libwrap.so库文件实现
- **控制对象**：调用该库的服务程序（如sshd、vsftpd）

**检查服务是否支持**：
```bash
# 查看sshd是否使用libwrap库
ldd /usr/sbin/sshd | grep libwrap.so
```

### 6.2 工作原理
1. 客户端发起连接请求
2. TCP Wrappers检查访问控制规则
3. 允许：转发给服务进程处理
4. 拒绝：直接中断连接
![TCP Wrappers工作流程](https://p9-flow-imagex-sign.byteimg.com/ocean-cloud-tos/pdf/0a1e484a47023a297f592bf676b21480_8_1200.jpg~tplv-a9rns2rl98-resize-crop:119:400:438:697:319:297.jpeg?rk3s=1567c5c4&x-expires=1790258110&x-signature=JztMKUS%2BJNO0RpXxjvcUfts85lk%3D)

### 6.3 配置文件
- **优先级**：`hosts.allow` > `hosts.deny`
- **路径**：`/etc/hosts.allow` 和 `/etc/hosts.deny`

**配置格式**：
```
服务列表@主机: 客户端列表
```

**客户端列表表示方法**：
- 单个IP：`192.168.88.20`
- 网段：`192.168.88.` 或 `192.168.88.0/255.255.255.0`
- 所有主机：`ALL`
- 本地主机：`LOCAL`

### 6.4 典型应用案例

#### 案例1：允许特定IP访问SSH
```bash
# hosts.allow
sshd:192.168.88.20 192.168.88.30

# hosts.deny
sshd:ALL
```

#### 案例2：拒绝特定网段访问FTP
```bash
# hosts.deny
vsftpd:192.168.90.
```

#### 案例3：允许所有主机访问所有服务
```bash
# hosts.allow和hosts.deny均保持默认空配置
```