# 基本概念

**Kubernetes 容器集群管理系统**（常简称为K8s）是一个开源的容器编排平台，最初由 Google 开发，现已成为 Cloud Native Computing Foundation（CNCF）的一部分。Kubernetes旨在简化容器化应用程序的部署、扩展和管理，提供了丰富的功能和工具来帮助用户构建可靠、高效的容器化环境。

以下是Kubernetes的一些关键特点和核心概念：

1. **容器编排**：Kubernetes提供了强大的容器编排功能，可以自动化地部署、扩展和管理容器化应用程序。用户可以定义应用程序的期望状态，Kubernetes会根据这些定义自动调整系统状态以保持一致。
2. **集群管理**：Kubernetes支持多个节点的集群管理，用户可以轻松地在不同的物理机或虚拟机上部署容器，并统一管理这些容器。Kubernetes提供了节点管理、负载均衡、存储管理等功能，帮助用户构建高可用、高性能的集群环境。
3. **自动化扩展**：Kubernetes可以根据应用程序的负载情况自动扩展或缩减容器实例数量，以确保系统资源的最优利用和应用程序的高可用性。
4. **服务发现和负载均衡**：Kubernetes提供了内置的服务发现和负载均衡功能，可以帮助应用程序实现动态服务发现和负载均衡，确保应用程序的稳定性和可靠性。
5. **存储管理**：Kubernetes支持多种存储类型，包括持久卷（Persistent Volumes）和持久卷声明（Persistent Volume Claims），可以帮助用户管理应用程序的持久化存储需求。
6. **安全性**：Kubernetes提供了丰富的安全功能，包括身份认证、访问控制、网络策略等，可以帮助用户保护容器化应用程序的安全性。

kubernetes，简称K8s，是用8 代替8 个字符“ubernete”而成的缩写。是一个开源的，用于管理云平台中多个主机上的容器化的应用，Kubernetes 的目标是让部署容器化的应用简单并且高效（powerful）,Kubernetes 提供了应用部署，规划，更新，维护的一种机制。

传统的应用部署方式是通过插件或脚本来安装应用。这样做的缺点是应用的运行、配置、管理、所有生存周期将与当前操作系统绑定，这样做并不利于应用的升级更新/回滚等操作，当然也可以通过创建虚拟机的方式来实现某些功能，但是虚拟机非常重，并不利于可移植性。

新的方式是**通过部署容器方式**实现，每个容器之间互相隔离，每个容器有自己的文件系统，容器之间进程不会相互影响，能区分计算资源。相对于虚拟机，容器能快速部署，由于容器与底层设施、机器文件系统解耦的，所以它能在不同云、不同版本操作系统间进行迁移。

容器占用资源少、部署快，每个应用可以被打包成一个容器镜像，每个应用与容器间成一对一关系也使容器有更大优势，使用容器可以在build 或release 的阶段，为应用创建容器镜像，因为每个应用不需要与其余的应用堆栈组合，也不依赖于生产环境基础结构，这使得从研发到测试、生产能提供一致环境。类似地，容器比虚拟机轻量、更“透明”，这更便于监控和管理。

在Kubernetes 中，我们可以创建多个容器，每个容器里面运行一个应用实例，然后通过内置的负载均衡策略，实现对这一组应用实例的管理、发现、访问，而这些细节都不需要运维人员去进行复杂的手工配置和处理。

1. **自动装箱：**基于容器对应用运行环境的资源配置要求**自动部署应用容器**
2. **自我修复：**当容器失败时，会对容器进行重启。当所部署的 Node 节点有问题时，会对容器进行重新部署和重新调度。当容器未通过监控检查时，会关闭此容器**直到容器正常运行时**，才会对外提供服务。
3. **水平扩展：**通过简单的命令、用户 UI 界面或基于 CPU 等资源使用情况，对应用容器进行规模扩大或规模剪裁
4. **服务发现：**用户不需使用额外的服务发现机制，就能够基于 Kubernetes 自身能力实现服务发现和负载均衡
5. **滚动更新：**可以根据应用的变化，对应用容器运行的应用，进行一次性或批量式更新
6. **版本回退：**可以根据应用部署情况，对应用容器运行的应用，进行历史版本即时回退
7. **密钥和配置管理：**在不需要重新构建镜像的情况下，可以部署和更新密钥和应用配置，类似热部署。
8. **存储编排：**自动实现存储系统挂载及应用，特别对有状态应用实现数据持久化非常重要，存储系统可以来自于本地目录、网络存储(NFS、Gluster、Ceph 等)、公共云存储服务
9. **批处理：**提供一次性任务，定时任务；满足批量数据处理和分析的场景

## 集群架构

![kubernetes](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202403281459571.jpg)

在 Kubernetes 中，节点（Node）是集群中的工作节点，用于运行应用程序的容器。每个节点都有自己的资源（如CPU、内存、存储）和网络，负责接收和执行由控制平面（Control Plane）下发的任务。以下是 Kubernetes 中常见的节点类型及其作用：

1. **主节点（Master Node）**：
   - 主节点是 Kubernetes 集群的控制平面，负责管理集群的状态、调度应用程序、监控集群健康状态等任务。
   - 主节点通常包含以下组件：
     - **kube-apiserver**：提供 Kubernetes API 服务，接收和处理用户请求。
     - **kube-controller-manager**：运行控制器，负责集群中的各种控制逻辑，如副本控制器、节点控制器等。
     - **kube-scheduler**：负责调度应用程序到适合的节点上运行。
     - **etcd**：分布式键值存储，用于保存集群的状态信息。
  
2. **工作节点（Worker Node）**：
   - 工作节点是运行容器化应用程序的节点，负责执行实际的工作负载。
   - 工作节点通常包含以下组件：
     - **kubelet**：负责与主节点通信，接收并执行由主节点下发的任务。
     - **kube-proxy**：负责维护节点上的网络规则，实现服务发现和负载均衡。
     - **Container Runtime**：负责运行容器的软件，如 Docker、containerd 等。
  
3. **辅助节点（Supplementary Node）**：
   - 辅助节点是一种特殊类型的节点，用于扩展集群的功能，而不承担运行工作负载的任务。
   - 辅助节点通常包含一些附加的组件或服务，如日志收集器、监控代理、存储插件等。

4. **自注册节点（Self-Registering Node）**：
   - 自注册节点是一种自动加入集群的节点类型，无需手动配置或管理。
   - Kubernetes 支持自动发现和注册新的工作节点，使得集群的扩展和缩减更加灵活和自动化。

### Master Node

#### kube-apiserver

kube-apiserver 是 Kubernetes 集群中的一个核心组件，可理解为集群的统一入口，它提供了 Kubernetes API 的服务端实现，负责处理来自用户、其他组件和外部系统的请求。**kube-apiserver 支持 RESTful 风格的请求方式，用户可以通过发送 HTTP 请求来与 Kubernetes 集群进行交互。**

通过 RESTful API，用户可以执行各种操作，如创建、删除、更新资源对象（如 Pod、Service、Deployment 等）、查询集群状态、查看日志、扩展集群功能等。RESTful API 的设计使得 Kubernetes 的管理操作变得简单、统一且易于扩展。

一般来说，用户可以使用工具如 kubectl（Kubernetes 命令行工具）或客户端库来发送 RESTful 请求到 kube-apiserver，以执行各种管理操作。例如，通过发送 GET 请求可以获取集群中的资源对象信息，通过发送 POST 请求可以创建新的资源对象，通过发送 DELETE 请求可以删除资源对象，以此类推。

#### kube-controller-manager

kube-controller-manager 是 Kubernetes 集群中的一个核心组件，它是运行在主节点（Master Node）上的控制器管理器，负责运行一系列控制器（controllers）来监控集群状态并确保集群中的各种资源处于期望的状态。kube-controller-manager 包含了多个控制器，每个控制器负责不同的功能，如副本控制器、节点控制器、服务控制器等。

以下是 kube-controller-manager 中一些常见的控制器及其作用：

1. **副本控制器（Replication Controller）**：
   - 负责确保应用程序的副本数量符合用户定义的期望状态。如果有副本数量不足或过多，副本控制器会自动调整副本数量，保持应用程序的稳定性和可用性。
2. **节点控制器（Node Controller）**：
   - 负责监控集群中节点的状态，如节点的健康状态、连接状态等。节点控制器会处理节点的故障、离线等情况，确保集群中的节点状态正确。
3. **服务控制器（Service Controller）**：
   - 负责监控和管理 Kubernetes 中的服务对象。服务控制器会根据服务定义自动创建、更新、删除对应的负载均衡规则，确保服务的可用性和负载均衡。
4. **端点控制器（Endpoints Controller）**：
   - 负责将服务与后端 Pod 的 IP 地址关联起来，确保服务能够正确路由流量到对应的后端 Pod。

除了上述控制器之外，kube-controller-manager 还包含其他一些控制器，如命名空间控制器、资源配额控制器等，每个控制器都扮演着关键的角色，确保集群中的各种资源对象处于正确的状态，保证集群的稳定性和可靠性。

#### kube-scheduler

kube-scheduler 是 Kubernetes 集群中的一个核心组件，它是运行在主节点（Master Node）上的调度器，**负责根据预定义的调度策略（Scheduling Policies）将新创建的 Pod 分配到合适的工作节点（Worker Node）上运行**。kube-scheduler 在创建新的 Pod 时，会考虑诸多因素，如资源需求、硬件约束、亲和性和反亲和性规则等，以确保最佳地利用集群资源并提高整体性能。
以下是 kube-scheduler 的主要功能和工作原理：
1. **调度算法**：
   - kube-scheduler 使用一种称为调度算法（Scheduling Algorithm）的机制来选择最适合的工作节点来运行新的 Pod。调度算法会考虑多种因素，如节点资源利用率、Pod 的资源需求、节点亲和性和反亲和性规则等，以决定最佳的节点分配方案。
2. **调度策略**：
   - kube-scheduler 支持用户定义和配置调度策略，以满足不同场景下的需求。用户可以通过标签选择器（Label Selectors）、亲和性规则（Affinity Rules）、污点（Taints）等方式来定义 Pod 的调度约束和偏好。
3. **调度过程**：
   - 当用户创建一个新的 Pod 时，kube-scheduler 会监听到这个事件，并根据 Pod 的调度需求和集群状态来进行调度决策。它会评估每个工作节点的可用资源、负载情况等信息，选择最适合的节点来运行该 Pod。
4. **调度事件**：
   - kube-scheduler 会生成调度事件（Scheduling Events），记录每个 Pod 的调度过程和结果。这些事件可以帮助用户了解调度决策的原因，排查调度问题和优化调度性能。

### Worker Node

#### kubelet

kubelet 是 Kubernetes 集群中的一个核心组件，它是运行在每个工作节点（Worker Node）上的代理，负责管理节点上的容器和 Pod，与 Kubernetes Master 节点上的 API Server 交互，确保节点上的容器按照用户定义的期望状态运行。kubelet 负责监控节点上的资源使用情况、接收来自控制平面的指令、拉取容器镜像、启动、停止和管理容器等。
以下是 kubelet 的一些主要功能和工作原理：
1. **Pod 生命周期管理**：
   - kubelet 负责管理节点上的 Pod 的生命周期，包括创建、启动、停止、重启和销毁 Pod。它会定期检查 API Server 中的 Pod 配置信息，根据配置信息来维护节点上的 Pod 状态。
2. **容器运行时管理**：
   - kubelet 通过容器运行时接口（如 Docker、containerd、CRI-O 等）来管理节点上的容器。它会与容器运行时进行交互，拉取容器镜像、启动容器、监控容器状态等。
3. **资源管理**：
   - kubelet 负责监控节点上的资源使用情况，包括 CPU、内存、磁盘等资源的利用率。它会根据 Pod 的资源请求和节点的实际资源情况来调度和管理容器，确保节点资源的合理分配。
4. **健康检查**：
   - kubelet 定期对节点上的容器和 Pod 进行健康检查，确保它们处于正常运行状态。如果发现容器或 Pod 出现问题，kubelet 会尝试重启容器或上报问题给控制平面。
5. **日志和指标收集**：
   - kubelet 负责收集节点上容器的日志和指标信息，并将这些信息发送到集中的日志和监控系统中，帮助管理员监控和诊断容器的运行状态。

#### kube-proxy

kube-proxy 是 Kubernetes 集群中的一个核心组件，**它负责实现 Kubernetes 集群内部的服务发现和负载均衡功能。**kube-proxy 在每个工作节点（Worker Node）上运行，通过维护节点上的网络规则来实现服务代理和负载均衡，确保集群内部的服务能够被正确路由和访问。

以下是 kube-proxy 的一些主要功能和工作原理：

1. **服务代理**：
   - kube-proxy 负责为 Kubernetes 集群中的 Service 对象创建代理规则，将 Service 的虚拟 IP（Cluster IP）映射到后端 Pod 的实际 IP 地址和端口。这样，通过访问 Service 的虚拟 IP，请求会被 kube-proxy 路由到对应的后端 Pod 上。
2. **负载均衡**：
   - kube-proxy 支持多种负载均衡模式，包括轮询（Round Robin）、IP 负载均衡（IPVS）等。它会根据 Service 的负载均衡策略和后端 Pod 的健康状态来动态调整流量的分发，确保请求能够均匀分配到各个后端 Pod 上。
3. **网络规则管理**：
   - kube-proxy 维护节点上的网络规则，包括 iptables 规则、IPVS 规则等，用于实现服务代理和负载均衡功能。它会监听 Kubernetes API Server 中 Service 和 Endpoint 的变化，动态更新网络规则以反映最新的服务配置。
4. **节点间通信**：
   - kube-proxy 还负责处理节点间的通信流量，确保集群内部的 Pod 和 Service 能够互相访问。它会为集群内部的通信流量创建路由规则，实现节点间的网络通信。
5. **高可用性**：
   - kube-proxy 支持多个 kube-proxy 实例之间的负载均衡和故障转移，确保即使某个 kube-proxy 实例发生故障，服务发现和负载均衡功能仍然能够正常运行。

## 核心概念

### pod

在 Kubernetes（k8s）中，Pod 是**最小的部署单元**，它是 Kubernetes 集群中可以被调度和管理的基本单位。Pod 可以包含一个或多个紧密相关的容器，这些容器共享相同的网络命名空间、存储卷和其他资源，并在同一个节点上运行。

以下是关于 Kubernetes 中的 Pod 的一些详细介绍：

1. **容器组合**：
   - Pod **可以包含一个或多个容器**，这些容器共享同一个网络命名空间和存储卷。这意味着它们可以方便地共享数据和通信，实现紧密耦合的服务组合。
2. **共享资源**：
   - **Pod 中的容器共享一些资源，如 IP 地址、端口空间和存储卷。**这使得它们可以方便地相互通信和共享数据，而无需额外的配置。
3. **生命周期管理**：
   - Pod 是一个可以创建、启动、停止和销毁的单元。Kubernetes 控制器可以管理 Pod 的生命周期，确保 Pod 按照用户的期望状态运行。
4. **调度和部署**：
   - Kubernetes Scheduler 负责将 Pod 调度到集群中的节点上，根据节点的资源情况和 Pod 的调度要求来选择合适的节点。Pod 可以根据用户定义的调度策略来实现灵活的部署。
5. **网络和存储**：
   - Pod 中的容器共享同一个网络命名空间，它们可以通过 localhost 进行通信。此外，Pod 可以挂载共享的存储卷，用于数据的持久化和共享。
6. **健康检查**：
   - Kubernetes 可以对 Pod 中的容器进行健康检查，确保容器处于正常运行状态。如果容器出现问题，Kubernetes 可以根据健康检查结果自动重启容器。
7. **扩展性**：
   - Pod 是 Kubernetes 中的扩展性单元，用户可以根据需要创建多个 Pod 实例，实现水平扩展和负载均衡。

### service

在 Kubernetes（k8s）中，Service 是一种抽象的概念，**用于定义一组 Pod 的访问方式和网络策略**。Service 提供了一种稳定的网络终结点，允许客户端通过 Service 访问一组具有相同标签的 Pod，而无需关心 Pod 的具体 IP 地址和端口信息。下面是关于 Kubernetes 中 Service 的详细介绍：
1. **负载均衡**：
   - Service 可以为一组具有相同标签的 Pod 提供负载均衡的访问方式。当客户端通过 Service 访问这组 Pod 时，Service 会自动将请求分发到不同的 Pod 实例，实现负载均衡。
2. **稳定的网络终结点**：
   - Service 提供了一个稳定的网络终结点，客户端可以通过 Service 的 Cluster IP 或者 Service Name 来访问后端 Pod，而不需要知道具体的 Pod IP 地址和端口信息。
3. **服务发现**：
   - Service 提供了一种服务发现机制，允许客户端动态地发现和访问运行中的服务。当 Pod 动态创建或销毁时，Service 会自动更新后端 Pod 的列表，确保客户端能够访问到最新的 Pod。
4. **多种类型**：
   - Kubernetes 中有多种类型的 Service，如 ClusterIP、NodePort、LoadBalancer、ExternalName 等，每种类型的 Service 提供不同的访问方式和网络策略，满足不同场景下的需求。
5. **端口转发**：
   - Service 可以通过端口转发将外部流量转发到后端 Pod，实现外部客户端与后端服务的通信。例如，NodePort Service 可以将外部流量转发到集群节点上指定的端口，然后再转发到后端 Pod。
6. **健康检查**：
   - Service 可以配置健康检查，定期检测后端 Pod 的健康状态。如果某个 Pod 不健康，Service 将不再将流量转发给该 Pod，确保服务的稳定性。
7. **内部通信**：
   - Service 不仅可以用于外部客户端访问，还可以用于内部服务之间的通信。通过 Service，不同的服务可以通过 Service Name 来相互访问，实现服务之间的解耦。

### controller

参考 kube-controller-manager

# Kubernetes 集群搭建

单 Master 集群结构

```mermaid
graph LR
A[Master]-->B[Node1]
A[Master]-->C[Node2]
A[Master]-->D[Node3]
```

风险 Master 节点唯一，Master 节点宕机导致集群不可用

多 Master 集群结构

```mermaid
graph LR
A[Master1]-->B[负载均衡技术]-->D[Node1]
C[Master2]-->B[负载均衡技术]
B[负载均衡技术]-->E[Node2]
B[负载均衡技术]-->F[Node3]
```


## 硬件要求

**操作系统**

CentOS7.x-86_x64

集群中所有机器之间网络互通

可以访问外网，需要拉取镜像

禁止 swap 分区

**测试环境**

Master 节点 ： CPU 2 核+ 内存 20GB+
Node 节点 ： CPU 4 核+ 内存 40GB+

**生产环境**

Master 节点 ： CPU 8 核+ 内存 100GB+
Node 节点 ： CPU 16 核+ 内存 500GB+

## 搭建方式

目前生产部署 Kubernetes 集群主要有两种方式：

1. kubeadm 

Kubeadm 是一个K8s 部署工具，提供kubeadm init 和kubeadm join，用于快速部署Kubernetes 集群。
官方地址：https://kubernetes.io/docs/reference/setup-tools/kubeadm/kubeadm/

2. 二进制包（重点学习）

从 github 下载发行版的二进制包，手动部署每个组件，组成 Kubernetes 集群。

Kubeadm 降低部署门槛，但屏蔽了很多细节，遇到问题很难排查。如果想更容易可控，推荐使用二进制包部署Kubernetes 集群，虽然手动部署麻烦点，期间可以学习很多工作原理，也利于后期维护。

## 单 Master 集群搭建

### kubeadm 方式

kubeadm是官方社区推出的一个用于快速部署 kubernetes 集群的工具。

这个工具能通过两条指令完成一个 kubernetes 集群的部署：

```
#### 创建一个 Master 节点
$ kubeadm init

#### 将一个 Node 节点加入到当前集群中
$ kubeadm join <Master节点的IP和端口 >
```

#### 1. 安装要求

在开始之前，部署Kubernetes集群机器需要满足以下几个条件：

- 一台或多台机器，操作系统 CentOS7.x-86_x64
- 硬件配置：2GB或更多RAM，2个CPU或更多CPU，硬盘30GB或更多
- 可以访问外网，需要拉取镜像，如果服务器不能上网，需要提前下载镜像并导入节点
- 禁止swap分区

#### 2. 准备环境

| 角色   | IP           |
| ------ | ------------ |
| master | 192.168.1.11 |
| node1  | 192.168.1.12 |
| node2  | 192.168.1.13 |

```shell
#### 安装 wget
sudo yum install wget

#### 关闭防火墙
systemctl stop firewalld
systemctl disable firewalld

#### 关闭selinux
sed -i 's/enforcing/disabled/' /etc/selinux/config  # 永久
setenforce 0  # 临时

#### 关闭swap
swapoff -a  # 临时
sed -ri 's/.*swap.*/#&/' /etc/fstab    # 永久

#### 根据规划设置主机名
hostnamectl set-hostname <hostname> # k8smaster / k8snode1 / k8snode2

#### 在master添加hosts
cat >> /etc/hosts << EOF
192.168.60.136 master
192.168.60.137 node1
192.168.60.138 node2
EOF

#### 将桥接的IPv4流量传递到iptables的链
cat > /etc/sysctl.d/k8s.conf << EOF
net.bridge.bridge-nf-call-ip6tables = 1
net.bridge.bridge-nf-call-iptables = 1
EOF
sysctl --system  # 生效

#### 时间同步
yum install ntpdate -y
ntpdate time.windows.com
```

#### 3. 所有节点安装Docker/kubeadm/kubelet

Kubernetes 默认 CRI（容器运行时）为 Docker，因此先安装 Docker。

##### 3.1 安装Docker

```shell
$ wget https://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo -O /etc/yum.repos.d/docker-ce.repo
$ yum -y install docker-ce-18.06.1.ce-3.el7
$ systemctl enable docker && systemctl start docker
$ docker --version
Docker version 18.06.1-ce, build e68fc7a
```

```shell
$ cat > /etc/docker/daemon.json << EOF
{
  "registry-mirrors": ["https://b9pmyelo.mirror.aliyuncs.com"]
}
EOF
```

```shell
#### 重启 docker
systemctl restart docker
```

##### 3.2 添加阿里云YUM软件源

```shell
$ cat > /etc/yum.repos.d/kubernetes.repo << EOF
[kubernetes]
name=Kubernetes
baseurl=https://mirrors.aliyun.com/kubernetes/yum/repos/kubernetes-el7-x86_64
enabled=1
gpgcheck=0
repo_gpgcheck=0
gpgkey=https://mirrors.aliyun.com/kubernetes/yum/doc/yum-key.gpg https://mirrors.aliyun.com/kubernetes/yum/doc/rpm-package-key.gpg
EOF
```

##### 3.3 安装kubeadm，kubelet和kubectl

由于版本更新频繁，这里指定版本号部署：

```shell
$ yum install -y kubelet-1.18.0 kubeadm-1.18.0 kubectl-1.18.0
$ systemctl enable kubelet # 开机启动
```

#### 4. 部署Kubernetes Master

在192.168.31.61（Master）执行。

```shell
$ kubeadm init \
  --apiserver-advertise-address=192.168.44.146 \ # 本机 IP
  --image-repository registry.aliyuncs.com/google_containers \ # 指定阿里云镜像仓库地址
  --kubernetes-version v1.18.0 \
  --service-cidr=10.96.0.0/12 \
  --pod-network-cidr=10.244.0.0/16
```

由于默认拉取镜像地址k8s.gcr.io国内无法访问，这里指定阿里云镜像仓库地址。

使用kubectl工具：

```bash
mkdir -p $HOME/.kube
sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
sudo chown $(id -u):$(id -g) $HOME/.kube/config
$ kubectl get nodes
```

#### 5. 加入Kubernetes Node

在192.168.1.12/13（Node）执行。

向集群添加新节点，执行在kubeadm init输出的kubeadm join命令：

```bash
$ kubeadm join 192.168.1.11:6443 --token esce21.q6hetwm8si29qxwn \
    --discovery-token-ca-cert-hash sha256:00603a05805807501d7181c3d60b478788408cfe6cedefedb1f97569708be9c5
```

默认token有效期为24小时，当过期之后，该token就不可用了。这时就需要重新创建token，操作如下：

```
kubeadm token create --print-join-command
```

#### 6. 部署CNI网络插件

```
wget https://raw.githubusercontent.com/coreos/flannel/master/Documentation/kube-flannel.yml
```

默认镜像地址无法访问，sed命令修改为docker hub镜像仓库。

```
kubectl apply -f https://raw.githubusercontent.com/coreos/flannel/master/Documentation/kube-flannel.yml

kubectl get pods -n kube-system
NAME                          READY   STATUS    RESTARTS   AGE
kube-flannel-ds-amd64-2pc95   1/1     Running   0          72s
```

#### 7. 测试kubernetes集群

在Kubernetes集群中创建一个pod，验证是否正常运行：

```
$ kubectl create deployment nginx --image=nginx
$ kubectl expose deployment nginx --port=80 --type=NodePort
$ kubectl get pod,svc
```

访问地址：http://NodeIP:Port  
