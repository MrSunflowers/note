# 虚拟路由冗余协议（VRRP）概述

**虚拟路由冗余协议（VRRP）** 是一种用于提高网络可靠性的容错协议。它的主要功能是在局域网中提供默认网关的冗余，以防止单点故障导致网络中断。

VRRP 的工作原理

1. **虚拟路由器**：
   - VRRP 通过将一组物理路由器虚拟化成一个逻辑上的虚拟路由器。这个虚拟路由器有一个虚拟IP地址和MAC地址，局域网内的主机将这个虚拟IP地址设置为默认网关。
   - 虚拟路由器的IP地址可以与组内某个路由器的实际IP地址相同，也可以不同。

2. **Master 和 Backup 路由器**：
   - 在 VRRP 组中，优先级最高的路由器被选为 Master，负责转发数据包和响应 ARP 请求。
   - 其他路由器作为 Backup，监控 Master 的状态。如果 Master 出现故障，Backup 路由器会根据优先级重新选举新的 Master。

3. **选举机制**：
   - 优先级是决定 Master 的关键因素，优先级高的路由器更有可能成为 Master。
   - 如果优先级相同，则比较路由器的 IP 地址，IP 地址较大的路由器优先。

4. **状态切换**：
   - 当 Master 路由器发生故障时，Backup 路由器会立即检测到，并通过发送免费 ARP 更新网络中的 ARP 表项，确保网络通信的连续性。

VRRP 的优点

- **高可用性**：通过提供冗余网关，VRRP 提高了网络的可靠性和可用性。
- **简化管理**：无需修改主机配置或动态路由协议即可实现网关冗余。
- **快速切换**：故障切换速度快，对用户透明。

VRRP 的应用场景

- **企业网络**：用于确保企业网络中的关键服务（如文件服务器、邮件服务器）的高可用性。
- **数据中心**：在数据中心环境中，VRRP 可以用于提供冗余的默认网关，确保数据流量不中断。

VRRP 的配置

配置 VRRP 通常涉及以下几个步骤：

1. **启用 VRRP**：在路由器上启用 VRRP 功能。
2. **设置虚拟路由器**：配置虚拟路由器的 IP 地址和其他参数。
3. **配置优先级**：为每个路由器分配优先级，以决定 Master 和 Backup 的角色。
4. **测试配置**：通过模拟故障来验证 VRRP 的有效性。

Keepalived 软件起初是专为 LVS 负载均衡软件设计的，用来管理并监控 LVS 集群系统中各个服务节点的状态，后来又加入了可以实现高可用的 **VRRP** 功能。因此，Keepalived 除了能够管理 LVS 软件外，还可以作为其他服务（例如：Nginx、Haproxy、MySQL等）的高可用解决方案软件。**VRRP 出现的目的就是为了解决静态路由单点故障问题的，它能够保证当个别节点宕机时，整个网络可以不间断地运行**。所以，Keepalived 一方面具有配置管理 LVS 的功能，同时还具有对 LVS 下面节点进行健康检查的功能，另一方面也可实现系统网络服务的高可用功能。

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

# 安装

在基于Debian的系统（如Ubuntu）上，使用以下命令安装：

```bash
sudo apt-get update
sudo apt-get install keepalived
```

在基于Red Hat的系统（如CentOS）上，使用以下命令安装：

```bash
sudo yum install keepalived
```

# 配置文件

Keepalived 配置文件通常位于 `/etc/keepalived/keepalived.conf`。你可以使用文本编辑器打开并编辑它，例如使用 `nano` 或 `vi`。

刚安装好的 keepalived 配置文件包含以下部分

```conf
! Configuration File for keepalived

global_defs {
   notification_email {
     acassen@firewall.loc
     failover@firewall.loc
     sysadmin@firewall.loc
   }
   notification_email_from Alexandre.Cassen@firewall.loc
   smtp_server 192.168.200.1
   smtp_connect_timeout 30
   router_id LVS_DEVEL
   vrrp_skip_check_adv_addr
   vrrp_strict
   vrrp_garp_interval 0
   vrrp_gna_interval 0
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
        192.168.200.16
        192.168.200.17
        192.168.200.18
    }
}

virtual_server 192.168.200.100 443 {
    delay_loop 6
    lb_algo rr
    lb_kind NAT
    persistence_timeout 50
    protocol TCP

    real_server 192.168.201.100 443 {
        weight 1
        SSL_GET {
            url {
              path /
              digest ff20ad2481f97b1754ef3e12ecd3a9cc
            }
            url {
              path /mrtg/
              digest 9b3a0c85a887a256d6939da88aabd8cd
            }
            connect_timeout 3
            nb_get_retry 3
            delay_before_retry 3
        }
    }
}

virtual_server 10.10.10.2 1358 {
    delay_loop 6
    lb_algo rr 
    lb_kind NAT
    persistence_timeout 50
    protocol TCP

    sorry_server 192.168.200.200 1358

    real_server 192.168.200.2 1358 {
        weight 1
        HTTP_GET {
            url { 
              path /testurl/test.jsp
              digest 640205b7b0fc66c1ea91c463fac6334d
            }
            url { 
              path /testurl2/test.jsp
              digest 640205b7b0fc66c1ea91c463fac6334d
            }
            url { 
              path /testurl3/test.jsp
              digest 640205b7b0fc66c1ea91c463fac6334d
            }
            connect_timeout 3
            nb_get_retry 3
            delay_before_retry 3
        }
    }

    real_server 192.168.200.3 1358 {
        weight 1
        HTTP_GET {
            url { 
              path /testurl/test.jsp
              digest 640205b7b0fc66c1ea91c463fac6334c
            }
            url { 
              path /testurl2/test.jsp
              digest 640205b7b0fc66c1ea91c463fac6334c
            }
            connect_timeout 3
            nb_get_retry 3
            delay_before_retry 3
        }
    }
}

virtual_server 10.10.10.3 1358 {
    delay_loop 3
    lb_algo rr 
    lb_kind NAT
    persistence_timeout 50
    protocol TCP

    real_server 192.168.200.4 1358 {
        weight 1
        HTTP_GET {
            url { 
              path /testurl/test.jsp
              digest 640205b7b0fc66c1ea91c463fac6334d
            }
            url { 
              path /testurl2/test.jsp
              digest 640205b7b0fc66c1ea91c463fac6334d
            }
            url { 
              path /testurl3/test.jsp
              digest 640205b7b0fc66c1ea91c463fac6334d
            }
            connect_timeout 3
            nb_get_retry 3
            delay_before_retry 3
        }
    }

    real_server 192.168.200.5 1358 {
        weight 1
        HTTP_GET {
            url { 
              path /testurl/test.jsp
              digest 640205b7b0fc66c1ea91c463fac6334d
            }
            url { 
              path /testurl2/test.jsp
              digest 640205b7b0fc66c1ea91c463fac6334d
            }
            url { 
              path /testurl3/test.jsp
              digest 640205b7b0fc66c1ea91c463fac6334d
            }
            connect_timeout 3
            nb_get_retry 3
            delay_before_retry 3
        }
    }
}
```

这个配置文件是用于 `keepalived` 的，它是一个用于 Linux 系统的高可用性解决方案，特别是在负载均衡和虚拟路由冗余协议（VRRP）方面。下面我将详细解释每个配置项：

## global_defs 部分

- `notification_email`：定义了在故障转移时接收通知的电子邮件地址列表。
- `notification_email_from`：定义了发送通知邮件的发件人地址。
- `smtp_server`：指定用于发送通知邮件的 SMTP 服务器地址。
- `smtp_connect_timeout`：设置 SMTP 服务器连接的超时时间（秒）。
- `router_id`：设置 VRRP 实例的路由器标识符，通常用于标识 VRRP 实例。
- `vrrp_skip_check_adv_addr`：指示 keepalived 在 VRRP 广告中跳过检查地址。
- `vrrp_strict`：设置 VRRP 的严格模式。
- `vrrp_garp_interval` 和 `vrrp_gna_interval`：设置 VRRP 的通告间隔，这里都设置为 0，意味着不发送 GARP 或 GNNA 消息。

## vrrp_instance VI_1 部分

- `state`：设置该 VRRP 实例的状态，这里是 `MASTER`，表示这个实例是主节点。
- `interface`：指定 VRRP 实例使用的网络接口，这里是 `eth0`。
- `virtual_router_id`：设置虚拟路由器的 ID，这里为 51。
- `priority`：设置该实例的优先级，这里是 100。
- `advert_int`：设置 VRRP 广告包的发送间隔（秒）。
- `authentication`：定义 VRRP 实例的认证方式，这里使用简单密码认证，密码为 `1111`。
- `virtual_ipaddress`：定义由 VRRP 实例管理的虚拟 IP 地址列表。

## virtual_server 部分

- `virtual_server`：定义虚拟服务器的 IP 地址和端口，这里是 `192.168.200.100` 的 443 端口。
- `delay_loop`：设置健康检查之间的延迟时间（秒）。
- `lb_algo`：设置负载均衡算法，这里是轮询（rr）。
- `lb_kind`：设置负载均衡的类型，这里是 NAT。
- `persistence_timeout`：设置会话保持的时间（秒）。
- `protocol`：定义使用的协议，这里是 TCP。

在 `virtual_server` 下，定义了多个 `real_server`，它们是实际提供服务的服务器。每个 `real_server` 配置了 IP 地址、端口、权重（weight）、健康检查方法（如 `HTTP_GET`）和相关参数（如 `connect_timeout`、`nb_get_retry`、`delay_before_retry`）。

例如，对于 `192.168.200.100:443`，有三个 `real_server`，分别位于 `192.168.201.100`、`192.168.200.2` 和 `192.168.200.3`。每个服务器都有自己的健康检查路径和预期的摘要值。

此外，还定义了 `sorry_server`，它是一个备用服务器，当所有其他服务器都不可用时使用。

整体来看，这个配置文件定义了一个高可用的负载均衡环境，其中包含一个主节点和多个实际提供服务的服务器。通过健康检查确保服务的高可用性和负载均衡。

# 配置虚拟 IP （VRRP）

VRRP（虚拟路由冗余协议）是实现高可用性的关键。下面使用 NGINX 为例来演示如何配置

## 主机配置

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

## 备机配置

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

这里使用的网卡可以使用 `ip addr` 命令来查看

使用 systemctl start keepalived 启动 keepalived ,查看 ip addr 发现多了虚拟 ip 192.168.8.200

**实际上 keepalived 在进行 IP 漂移时只会检测 keepalived 进程是否存活，如果 keepalived 进程存活则表示机器存活，否则进行 IP 漂移，所以在实际使用中需要配合其他手段来实现高可用，比如通过脚本来检测 NGINX 是否存活，如果 NGINX 宕机则杀死 keepalived 进程，实现 IP 漂移**。所以 Keepalived 可以检测一切软件，如 redis、mysql 等

# 常用命令

## 重启 Keepalived 服务

配置完成后，需要重启 Keepalived 服务以应用更改：

```bash
sudo systemctl restart keepalived
```

或者，如果你的系统不使用 `systemctl`，可以使用以下命令：

```bash
sudo service keepalived restart
```

## 查看 Keepalived 服务日志

```bash
tail -f /var/log/messages
```

## 配置 Keepalived 开机自启

```bash
sudo systemctl enable keepalived
```

这个命令会创建一个符号链接，将 Keepalived 服务文件链接到 `/etc/systemd/system/` 目录下，确保在系统启动时自动启动 Keepalived。

## 启动 Keepalived 服务

```bash
systemctl start keepalived
```

## 停止 Keepalived 服务

```bash
systemctl stop keepalived
```

## 检查配置文件的语法

确保配置文件的语法正确，可以使用 

```bash
keepalived -f /etc/keepalived/keepalived.conf -n
```

来检查配置文件的语法。

# Keepalived 健康检查功能

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

# Keepalived 的脚本钩子功能

Keepalived 提供了脚本钩子功能，允许在特定事件发生时执行自定义脚本。例如，你可以定义一个脚本来在 VRRP 状态改变时运行，从而实现更复杂的逻辑。

在 Keepalived 的上下文中，"定事件发生时"指的是与虚拟路由器（Virtual Router）或虚拟服务器（Virtual Server）相关的特定事件。Keepalived 使用 VRRP（Virtual Router Redundancy Protocol）协议来管理虚拟路由器的主备状态，并且可以监控本地服务（如 NGINX、Apache 等）的状态。以下是一些常见的触发脚本钩子的事件：

1. **VRRP 状态变化**：当虚拟路由器的状态发生变化时，例如从主（MASTER）变为备份（BACKUP），或者反过来。这通常发生在网络故障或 Keepalived 服务重启时。

2. **脚本检查失败**：如果在 `vrrp_script` 部分定义的脚本返回非零值，表示检测失败，Keepalived 可以根据配置采取行动，比如调整虚拟路由器的优先级。

3. **服务状态变化**：Keepalived 可以配置为监控本地服务（如 HTTP、SSL、TCP 等）。如果这些服务的状态发生变化（例如服务宕掉或重新启动），可以触发脚本执行。

4. **定时任务**：通过 `vrrp_script` 的 `interval` 参数，可以设置脚本按照固定的时间间隔定期执行，即使没有检测到状态变化也可以执行。

举个例子，如果你有一个脚本用于检查 NGINX 服务是否正常运行，你可以配置 Keepalived 在每次脚本执行时检查 NGINX 的状态。如果脚本返回非零值（表示 NGINX 服务有问题），Keepalived 可以根据脚本钩子的配置来调整虚拟路由器的优先级，或者执行其他自定义操作。

## 配置 Keepalived 脚本钩子

在 Keepalived 的配置文件 `/etc/keepalived/keepalived.conf` 中，添加 `vrrp_script` 部分来指定脚本及其执行频率。以下是一个配置示例：

```conf
! Configuration File for keepalived

global_defs {
   router_id NODE1
}

vrrp_script check_nginx {
    script "/opt/check_nginx.sh"  # 指定脚本路径
    interval 1                    # 每1秒执行一次脚本
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

配置完成后，需要重启 Keepalived 服务以应用更改：

```bash
sudo systemctl restart keepalived
```

或者，如果你的系统不使用 `systemctl`，可以使用以下命令：

```bash
sudo service keepalived restart
```

## Keepalived 的邮件通知功能

# 多 VIP 高可用架构

在传统的 Keepalived 配置中，通常只有一个主节点（Master）处于服务状态，备用节点（Backup）只有在主节点故障时才接管服务。这种配置在流量较大时会导致单点性能瓶颈，因为备用节点无法分担流量。为了解决这个问题，可以利用 Keepalived 配置多套虚拟 IP（VIP），并结合 DNS 负载均衡，实现多台 NGINX 服务器的互为主备，从而构建一个真正的高可用负载均衡架构。

## 架构概述

1. **多套 VIP 配置**：
   - 使用 Keepalived 为每台 NGINX 服务器配置不同的 VIP。例如，假设有两台 NGINX 服务器，可以为每台服务器分别配置一个 VIP。
   - 每台 NGINX 服务器既是 Master 也是 Backup，Master 负责处理流量，Backup 作为冗余。

2. **DNS 负载均衡**：
   - 在 DNS 服务器上为同一个域名配置多个 A 记录，每个 A 记录对应一个 VIP。
   - DNS 服务器将请求轮询分配到不同的 VIP，从而实现负载均衡。

## 具体配置步骤

### 1. 配置 Keepalived

假设有两台 NGINX 服务器，IP 地址分别为 `192.168.1.10` 和 `192.168.1.11`，我们为每台服务器配置一个 VIP。

**服务器 1（192.168.1.10）** 的 Keepalived 配置示例：

```conf
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
        192.168.1.100
    }
}

vrrp_instance VI_2 {
    state BACKUP
    interface eth0
    virtual_router_id 52
    priority 90
    advert_int 1

    authentication {
        auth_type PASS
        auth_pass 1111
    }

    virtual_ipaddress {
        192.168.1.101
    }
}
```

**服务器 2（192.168.1.11）** 的 Keepalived 配置示例：

```conf
vrrp_instance VI_1 {
    state BACKUP
    interface eth0
    virtual_router_id 51
    priority 90
    advert_int 1

    authentication {
        auth_type PASS
        auth_pass 1111
    }

    virtual_ipaddress {
        192.168.1.100
    }
}

vrrp_instance VI_2 {
    state MASTER
    interface eth0
    virtual_router_id 52
    priority 100
    advert_int 1

    authentication {
        auth_type PASS
        auth_pass 1111
    }

    virtual_ipaddress {
        192.168.1.101
    }
}
```

### 2. 配置 DNS 负载均衡

在 DNS 服务器上，为同一个域名配置多个 A 记录。例如：

```
example.com. IN A 192.168.1.100
example.com. IN A 192.168.1.101
```

这样，DNS 服务器会将请求轮询分配到 `192.168.1.100` 和 `192.168.1.101`，实现负载均衡。

## 优点

1. **高可用性**：
   - 每台 NGINX 服务器都有对应的 VIP，确保在任何一台服务器故障时，另一台服务器可以接管服务。
   - 通过 DNS 负载均衡，流量可以在多台服务器之间均衡分配，避免单点性能瓶颈。

2. **可扩展性**：
   - 可以根据需要增加更多的 NGINX 服务器和 VIP，进一步提升负载均衡能力和高可用性。

3. **简单易行**：
   - 配置相对简单，不需要引入复杂的负载均衡设备或软件。

## 注意事项

1. **DNS 缓存**：
   - DNS 负载均衡依赖于客户端的 DNS 解析结果，DNS 缓存可能会影响负载均衡的效果。可以适当调整 DNS 的 TTL（Time To Live）值来减少缓存时间。

2. **健康检查**：
   - 需要配置 Keepalived 的健康检查机制，确保 VIP 的切换能够及时响应服务器状态变化。

3. **一致性**：
   - 在多台服务器之间保持 Keepalived 配置的一致性，避免配置错误导致的服务不可用。
























