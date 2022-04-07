[TOC]

# zookeeper

&emsp;&emsp;是一个基于观察者模式设计的分布式服务管理框架，它负责存储和管理客户端关心的数据(例如哪台服务器存储了我需要调用的接口)，然后接受服务端的注册，一旦服务端数据或状态发生变化，Zookeeper就将负责通知在 Zookeeper 上注册的客户端，客户端可以利用这些信息来做出反应。服务器上下线就是在 Zookeeper 中创建和删除节点的动作。对于 Zookeeper 集群来说，服务器与客户端都是客户端，只不过服务器是创建节点，而客户端是负责监听。

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202204072248175.png)

# zookeeper 集群

1. Zookeeper 集群：由一个领导者（Leader），多个跟随者（Follower）组成，Leader 负责集群状态的更新，这样就不会因为多台服务器同时更新数据导致的数据不一致性问题，主从复制，读写分离。
2. 集群中只要有半数以上节点存活，Zookeeper 集群就能正常服务。所以Zookeeper 适合**安装奇数台服务器**，例如集群 6 台服务器，与 5 台服务器效果相同，都是挂 3 台服务器即集群不可用。
3. 全局数据一致：每个 Zookeeper Server 保存一份相同的数据副本，Client 无论连接到哪个 Zookeeper Server，数据都是一致的。
4. 更新请求顺序执行，来自同一个 Client 的更新请求按其发送顺序依次执行。
5. 数据更新原子性，一次数据更新要么成功，要么失败。
6. 实时性，在一定时间范围内，Client 能读到最新数据。

# zookeeper 数据结构

&emsp;&emsp;ZooKeeper 数据模型的结构与 Unix 文件系统很类似，整体上可以看作是一棵树，每个节点称做一个 ZNode。每一个 ZNode 默认能够存储 1MB 的数据(小的数据量可以保证集群数据同步的速度)，每个 ZNode 都可以通过其路径唯一标识。

# zookeeper 应用场景

- 统一命名服务![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202204062132282.png)
- 统一配置管理![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202204062134619.png)
- 统一集群管理![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202204062134489.png)
- 服务器节点动态上下线![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202204062135276.png)
- 软负载均衡![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202204062135316.png)

# zookeeper 选举机制

&emsp;&emsp;首先，每个 zookeeper 节点都有三个节点值：

- SID：服务器ID。用来唯一标识一台 ZooKeeper 集群中的机器，每台机器不能重复，和myid一致。
- ZXID：事务ID。ZXID是一个事务ID，用来标识一次服务器状态的变更。在某一时刻，集群中的每台机器的 ZXID 值不一定完全一致，这和 ZooKeeper 服务器对于客户端“更新请求”的处理逻辑有关。
- Epoch：每个 Leader 任期的代号。没有 Leader 时同一轮投票过程中的逻辑时钟值是相同的。每投完一次票这个数据就会增加

&emsp;&emsp;假设现在有 5 台服务器

## 第一次启动

&emsp;&emsp;服务器优先投票给自己

- 服务器1启动，发起一次选举。服务器1投自己一票。此时服务器 1 票数一票，不够半数以上（3票），选举无法完成，服务器1状态保持为观察状态 LOOKING；
- 此时服务器2启动，再发起一次选举。也投自己一票，然后与服务器 1 交换选票信息：交换过程中服务器 1 发现服务器 2 的服务器 ID 比自己大，更改选票，将自己手中的选票投给服务器 2。此时服务器 1 票数 0 票，服务器 2 票数 2 票，没有达到半数以上结果，选举无法完成，服务器1，2都状态保持LOOKING
- 接下来服务器3启动，发起一次选举。流程与服务器2相同，也是先投给自己，然后与其他服务器交换选票信息，此时服务器 1 和 2 都会更改选票为服务器 3。此次投票结果：服务器 1 为 0 票，服务器 2 为 0 票，服务器 3 为 3 票。此时服务器 3 的票数已经超过半数，服务器 3 当选Leader。服务器 1，2 更改状态为 FOLLOWING，服务器 3 更改状态为LEADING；
- 服务器4启动，发起一次选举。交换信息发现服务器 1，2，3 已经不是 LOOKING 状态。此时选票为服务器 3 为 3 票，服务器 4 为 1 票。服务器 4 服从多数，更改选票信息给服务器 3，并更改状态为 FOLLOWING；
- 服务器5启动，同4一样。

## 非第一次启动

&emsp;&emsp;当 ZooKeeper 集群中的一台服务器出现以下两种情况之一时，就会开始进入Leader选举：

1. 服务器初始化启动。
2. 服务器运行期间无法和Leader保持连接。

&emsp;&emsp;而当一台机器进入 Leader 选举流程时，当前集群也可能会处于以下两种状态：

1. 集群中本来就已经存在一个Leader。对于已经存在 Leader 的情况，机器试图去选举 Leader 时，会被告知当前服务器的 Leader 信息，对于该机器来说，仅仅需要和 Leader 机器建立连接，并进行状态同步即可。

2. 集群中确实不存在 Leader。假设 ZooKeeper 由 5 台服务器组成，SID 分别为 1、2、3、4、5，ZXID分别为 8、8、8、7、7，并且此时 SID 为 3 的服务器是 Leader。某一时刻，3 和 5 服务器出现故障，因此开始进行 Leader 选举。

&emsp;&emsp;此时服务器 1、2、4 的状态为：

| 服务器 | EPOCH | ZXID | SID  |
| :----: | :---: | :--: | :--: |
|   1    |   1   |  8   |  1   |
|   2    |   1   |  8   |  2   |
|   3    |   1   |  7   |  4   |

&emsp;&emsp;选举Leader规则 ：

1. EPOCH 大的直接胜出
2. EPOCH 相同，事务 id 大的胜出
3. 事务 id 相同，服务器 id 大的胜出

