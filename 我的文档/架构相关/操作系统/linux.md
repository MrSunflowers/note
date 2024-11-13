# linux 内核追踪工具 strace

# Linux 服务器文件同步

当服务器形成集群，且解群中有大量的配置文件或静态资源需要保持一致，此时就需要一种技术来监控和同步各个服务器之间的文件。同步文件可以使用 rsync 而监听文件变化可以使用 inotify

## rsync

remote synchronize 是一个远程数据同步工具，可通过 LAN/WAN 快速同步多台主机之间的文件。也可以使用 rsync 同步本地硬盘中的不同目录。 rsync 是用于替代 rcp 的一个工具，rsync 使用所谓的 rsync 算法进行数据同步，这种算法只传送两个文件的不同部分，而不是每次都整份传送，因此速度相当快。

https://www.samba.org/ftp/rsync/rsync.html

rsync 是一个功能强大的文件同步工具，它支持多种同步模式，包括本地模式、远程模式和守护进程模式。rsync 的核心优势之一是其高效的差异同步算法，它只同步源和目标之间发生变化的部分，从而节省了时间和带宽。rsync 还可以利用 inotify（在支持的系统上）来监控文件系统事件，从而实现更高效的文件同步。

Rsync 的三种模式

1. **本地模式**
   - 类似于 `cp` 命令，用于在本地文件系统中同步文件和目录。
   - 例如：`rsync -av /path/to/source/ /path/to/destination/`
   - 这种模式下，rsync 可以用来备份本地目录到另一个位置，同时保留文件的权限、时间戳等属性。

2. **远程模式**
   - 类似于 `scp` 命令，用于在远程主机之间同步文件和目录。
   - 例如：`rsync -avz user@remotehost:/path/to/source/ /path/to/destination/`
   - 这种模式下，rsync 可以通过 SSH 连接到远程主机，并同步文件。支持压缩数据传输，减少网络带宽消耗。

3. **守护进程模式（Socket进程）**
   - rsync 守护进程模式允许 rsync 作为后台服务运行，监听特定端口（默认为 873）上的连接请求。
   - 用户可以配置 rsync 守护进程，允许或拒绝特定的同步请求。
   - 例如，可以设置 rsync 守护进程来允许特定用户同步特定目录，或者拒绝同步某些敏感文件。
   - 守护进程模式是 rsync 作为网络服务的重要功能，它支持通过网络进行高效的数据同步。

### 安装

#### 在源服务器和目标服务器分别安装

```
yum install -y rsync
```

rsync 分为服务端和客户端，服务端需要以守护模式启动，客户端直接使用命令拉取即可。

#### 配置源服务器（rsync 服务端）

配置文件通常位于

```
/etc/rsyncd.conf
```

`rsyncd.conf` 是 rsync 守护进程的配置文件，用于定义 rsync 服务器如何响应客户端请求。这个文件位于 `/etc` 目录下，但其确切位置可能因发行版而异。配置文件中定义了同步模块（module），每个模块可以配置不同的路径、权限、认证方式等。

**配置文件结构**

`rsyncd.conf` 文件通常由一个或多个模块组成，每个模块由方括号内的模块名开始，后跟一系列配置指令。下面是一个简单的配置文件示例：

```conf
# 全局配置
uid = nobody
gid = nobody
use chroot = yes
max connections = 4
timeout = 300

# 定义一个模块 方括号内即模块名称 可以随意起名
[files]
path = /path/to/sync
comment = Sync files
read only = yes
list = yes
uid = root
gid = root
auth users = username
secrets file = /etc/rsyncd.secrets
```

**关键配置项**

- **uid/gid**: 指定运行 rsync 守护进程的用户和组。
- **use chroot**: 是否将模块路径限制在 chroot 环境中。
- **max connections**: 允许的最大连接数。
- **timeout**: 连接超时时间（秒）。
- **path**: 模块同步的本地路径。
- **comment**: 模块的描述信息。
- **read only**: 是否只读模式，默认为 `yes`。
- **list**: 是否允许列出模块内容，默认为 `yes`。
- **uid/gid**: 在模块内覆盖全局的用户和组。
- **auth users**: 允许访问模块的用户列表。
- **secrets file**: 存放用户名和密码的文件路径。

**安全性**

- **secrets file**: 用于存放用户名和密码，格式为 `username:password`，每行一个。
- **auth users**: 指定允许访问的用户，必须与 `secrets file` 中的用户名匹配。
- **hosts allow** 和 **hosts deny**: 限制允许连接的主机或排除某些主机。

**注意事项**

- 确保 `/etc/rsyncd.conf` 文件的权限设置正确，通常需要限制为只有 root 用户可以读写。
- 修改配置文件后，需要重启 rsync 守护进程以使更改生效。
- 由于 rsync 守护进程具有较高的权限，务必确保配置文件的安全性，避免未授权访问。

`rsyncd.conf` 文件的配置非常灵活，可以根据您的具体需求进行调整。务必仔细阅读 rsync 的官方文档，以了解所有可用的配置选项和最佳实践。

#### 启动和管理 rsync 进程

- 启动 rsync 守护进程：`rsync --daemon`
- 停止 rsync 守护进程：通常需要使用系统服务管理命令，如 `systemctl stop rsync` 或 `service rsync stop`。
- 检查 rsync 守护进程状态：`rsync --daemon --config=/etc/rsyncd.conf`

**重启 rsync**

`rsync` 作为守护进程运行时，并没有一个专门的命令来重启它。通常的做法是先停止当前运行的 `rsync` 守护进程，然后重新启动它。下面是在不同操作系统上执行这一操作的一般步骤：

在类 Unix 系统上（如 Linux）

1. **查找 rsync 进程的 PID**：
   ```bash
   ps aux | grep rsync
   ```
   或者
   ```bash
   pgrep rsync
   ```

2. **终止 rsync 进程**：
   ```bash
   kill [PID]
   ```
   其中 `[PID]` 是你从第一步中得到的进程 ID。

3. **重新启动 rsync 守护进程**：

   如果 `rsync` 是通过系统服务管理器（如 systemd）管理的，可以使用如下命令：
   ```bash
   systemctl restart rsync
   ```
   如果不是，你可能需要手动重新启动 rsync 守护进程，这通常意味着重新运行启动 rsync 的命令，例如：
   ```bash
   rsync --daemon
   ```

在 macOS 上

macOS 上的 `rsync` 通常作为 launchd 服务运行，可以使用 `launchctl` 来管理：

1. **卸载 rsync 服务**：
   ```bash
   launchctl unload ~/Library/LaunchAgents/homebrew.mxcl.rsync.plist
   ```
   （路径可能根据安装方式有所不同）

2. **重新加载 rsync 服务**：
   ```bash
   launchctl load ~/Library/LaunchAgents/homebrew.mxcl.rsync.plist
   ```

#### 目标服务器操作指令（rsync 客户端）

查看文件目录

语法

```
rsync --list-only 服务器IP::模块名称/
```

示例

```shell
rsync --list-only 192.168.1.200::files/
```

手动拉取文件

语法

```
rsync -avz 服务器IP::模块名称/ 拉取文件到本地路径
```

示例

```shell
rsync -avz 192.168.1.200::files/ /path/to/sync/
```

#### 服务端安全认证

在生产环境中，一般 rsync 服务端会开启安全认证服务，以保证文件安全。在 rsyncd.conf 中可以指定一个密码文件，要求使用特定的用户名和密码进行认证。这样，只有知道这些凭据的用户才能访问 rsync 服务。

```conf
# 全局配置
uid = nobody
gid = nobody
use chroot = yes
max connections = 4
timeout = 300

# 定义一个模块 方括号内即模块名称 可以随意起名
[files]
path = /path/to/sync
comment = Sync files
read only = yes
list = yes
uid = root
gid = root
# 可以访问的用户名
auth users = sgg
＃ 密码文件，名字可以随意，包括文件后缀
secrets file = /etc/rsyncd.secrets
```

rsyncd.secrets

```conf
sgg:111
```

在上述配置示例中，为 `files` 模块定义了一个 `sgg` 用户，密码为 `111` ，其中配置项 `auth users` 与 `secrets file` 可以配置在全局，也可以配置在模块中。

- **格式正确性**：密码文件 rsyncd.secrets 应该包含用户名和密码，用冒号 : 分隔。如果需要支持多个用户，每个用户占一行。确保每行格式为 username:password，并且没有多余的空格或字符。
- **安全性**：密码文件应该设置适当的权限，通常应该只有 rsync 进程和系统管理员可以读取。可以使用命令 `chmod 600 /etc/rsyncd.secrets` 来设置权限。
- **服务端口**：默认情况下，rsync 守护进程使用 873 端口。确保该端口在防火墙中开放，并且没有被其他服务占用。
- **测试配置**：在正式部署前，应该测试配置文件的正确性。可以使用 `rsync --config=/path/to/rsyncd.conf` 命令来测试配置文件。
- **日志记录**：考虑配置日志记录，以便跟踪同步操作和认证尝试。可以在 `rsyncd.conf` 中设置 `log file = /var/log/rsync.log`。

重启 rsyncd 服务

客户端访问命令

```shell
rsync --list-only 192.168.1.200::files/
```

此时再想要查看服务器文件就会提示需要输入密码，且如何输入都不对

首先，先前创建的密码文件的权限不能太高，给密码权限降权

```shell
chmod 600 /etc/rsyncd.secrets
```

然后重启服务，并在客户端指定用户访问

```shell
rsync --list-only sgg＠192.168.1.200::files/
```

#### 客户端免密登录

创建密码文件，客户端只需要记录密码

rsyncd.secrets

```conf
111
```

设定密码文件权限

```shell
chmod 600 /etc/rsyncd.secrets
```

执行命令

```shell
rsync --list-only --password-file=/etc/rsyncd.secrets sgg＠192.168.1.200::files/
```

### rsync 客户端常用选项

| 选项     | 含义                                                         |
| :------- | :----------------------------------------------------------- |
| -a       | 包含-rtplgoD                                                 |
| -r       | 同步目录时要加上，类似cp时的-r选项                           |
| -v       | 同步时显示一些信息，让我们知道同步的过程                     |
| -l       | 保留软连接                                                   |
| -L       | 加上该选项后，同步软链接时会把源文件给同步                   |
| -p       | 保持文件的权限属性                                           |
| -o       | 保持文件的属主                                               |
| -g       | 保持文件的属组                                               |
| -D       | 保持设备文件信息                                             |
| -t       | 保持文件的时间属性                                           |
| –delete  | 删除DEST中SRC没有的文件                                      |
| –exclude | 过滤指定文件，如–exclude “logs”会把文件名包含logs的文件或者目录过滤掉，不同步 |
| -P       | 显示同步过程，比如速率，比-v更加详细                         |
| -u       | 加上该选项后，如果DEST中的文件比SRC新，则不同步              |
| -z       | 传输时压缩                                                   |

下面是对每个选项的简要说明：

| 选项      | 含义                                                         |
|-----------|--------------------------------------------------------------|
| `-a`      | 归档模式，相当于 `-rlptgoD`（不包括 `-H`、`-A`、`--Xattrs`）   |
| `-r`      | 递归地同步目录                                               |
| `-v`      | 详细模式，显示同步过程中的信息                               |
| `-l`      | 保留符号链接                                                 |
| `-L`      | 将符号链接视为其所指向的文件内容                             |
| `-p`      | 保持文件权限                                                 |
| `-o`      | 保持文件所有者                                               |
| `-g`      | 保持文件所属组                                               |
| `-D`      | 保持设备文件和特殊文件                                     |
| `-t`      | 保持文件修改时间                                             |
| `--delete`| 删除目标目录中源目录不存在的文件                             |
| `--exclude`| 排除匹配的文件或目录，例如 `--exclude="*.log"`               |
| `-P`      | 显示进度条和传输速率，比 `-v` 更详细                         |
| `-u`      | 更新模式，只同步目标目录中比源目录旧的文件                   |
| `-z`      | 在传输时进行压缩                                             |

使用示例

假设您想要同步本地目录 `/source` 到远程服务器 `/dest`，同时保留所有文件属性，并且在传输时进行压缩，您可以使用如下命令：

```bash
rsync -avz --progress /source/ username@remotehost:/dest/
```

如果您还需要排除日志文件，并且只同步比目标目录中现有文件更新的文件，可以添加 `--exclude` 和 `-u` 选项：

```bash
rsync -avuz --progress --exclude="*.log" /source/ username@remotehost:/dest/
```

注意事项

- 使用 `-a` 选项时，它会自动包含 `-r`、`-l`、`-p`、`-t`、`-g` 和 `-D` 选项，但不包括 `-H`（保持硬链接）、`-A`（保持ACLs）和 `--Xattrs`（保持扩展属性）。
- `-z` 选项在传输大量小文件时可能会降低效率，因为它会为每个文件单独压缩。
- `--delete` 选项在同步时非常有用，但使用时要小心，因为它会删除目标目录中源目录不存在的文件。
- 使用 `--exclude` 选项时，可以多次使用来排除多个模式。

`rsync` 是一个功能强大的工具，通过这些选项，您可以灵活地控制文件同步的行为。

### 服务端推送文件

文件的拉取过程上面已经演示了，现在来看由服务端推送文件。

首先，参照上文步骤，将每一台机器都当成服务端进行配置启动，保证每一台服务器都可以使用命令连接另一台服务器。

然后，将拉取命令反过来，即拉取时，本地路径在后，目标服务器地址在前，则推送时顺序反过来

语法

```
rsync -avz 本地路径 rsync://服务器IP:/模块名称
```

示例

```shell
rsync -avz --password-file=/etc/rsyncd.passwd.client /usr/local/nginx/html/ rsync://sgg@192.168.1.200:/files
```

默认情况下，rsync 服务端不接受客户端提交上来的文件，要想推送文件到服务端，则需要修改服务端配置的 `read only` 项改为 `no`

```conf
# 全局配置
uid = nobody
gid = nobody
use chroot = yes
max connections = 4
timeout = 300

# 定义一个模块 方括号内即模块名称 可以随意起名
[files]
path = /path/to/sync
comment = Sync files
# 允许客户端推送文件
read only = no
list = yes
uid = root
gid = root
# 可以访问的用户名
auth users = sgg
＃ 密码文件，名字可以随意，包括文件后缀
secrets file = /etc/rsyncd.secrets
```

### 近实时文件同步方案

在现有的功能基础上，可以编写一个脚本，每两秒拉取一次文件，或由服务器推送一次，即可实现近实时的文件同步，但要实现实时文件同步，需要配合 Inotify 一起使用。

## Inotify

Inotify 是 Linux 内核提供的一种机制，用于监控文件系统事件。它允许程序监控文件系统的变化，如文件或目录的创建、删除、修改等。Inotify 是一种高效、实时的文件系统事件监控方式，特别适用于需要对文件系统活动做出快速响应的应用程序。

**Inotify 的主要特点**：

1. **实时性**：Inotify 能够实时监控文件系统的变化，几乎无延迟地通知应用程序。

2. **高效性**：相比于传统的轮询（polling）方式，Inotify 在性能上有显著优势，因为它不需要不断地检查文件系统状态，而是由内核主动通知变化。

3. **资源占用低**：Inotify 使用事件驱动模型，仅在有事件发生时才占用 CPU 资源，因此对系统资源的占用较低。

**Inotify 的工作原理**：

- **Inotify 实例**：每个使用 Inotify 的程序都会创建一个 Inotify 实例，这个实例由一个唯一的文件描述符（file descriptor）标识。

- **监控点（Watch）**：程序可以为它感兴趣的文件或目录添加监控点。当这些文件或目录发生指定的事件时，内核会将事件信息发送到相应的 Inotify 实例。

- **事件类型**：Inotify 能够监控多种类型的事件，包括文件或目录的创建、删除、移动、修改等。

- **事件队列**：所有监控到的事件都会被放入一个队列中，程序通过读取文件描述符来获取这些事件。

**使用 Inotify 的程序示例**：

- **文件同步工具**：如 `rsync` 可以利用 Inotify 实时监控文件系统的变化，从而只同步变化的部分，提高效率。

- **备份工具**：一些备份软件使用 Inotify 来监控文件系统的变化，实现增量备份。

- **监控工具**：如 `inotify-tools` 包含的 `inotifywait` 和 `inotifywatch` 工具，可以用来监控文件系统事件。

**注意事项**：

- **资源限制**：Inotify 有资源限制，包括可以创建的监控点数量和事件队列的大小。这些限制可以通过修改内核参数进行调整。

- **内核版本**：Inotify 功能从 Linux 2.6.13 版本开始引入，因此需要较新的内核版本支持。

### 安装

下载 Inotify 安装包

```shell
wget http://github.com/downloads/rvoicilas/inotify-tools/inotify-tools-3.14.tar.gz
```

解压

```shell
tar -xzvf inotify-tools-3.14.tar.gz
```

进入解压完成目录，编译安装

```shell
./configure --prefix=/usr/local/inotify
```

```shell
make
```

```shell
make install
```

进入安装 bin 目录

```shell
cd /usr/local/inotify/bin
```

### 监控目录

```
/usr/local/inotify/bin/inotifywait -mrq --timefmt '%Y-%m-%d %H:%M:%S' --format '%T %w%f %e' -e close_write,modify,delete,create,attrib,move //usr/local/nginx/html/
```

上述命令使用了 `inotifywait` 工具来监控 `/usr/local/nginx/html/` 目录下的文件系统事件。`inotifywait` 是 `inotify-tools` 包的一部分，它是一个命令行工具，用于监控文件系统的变化事件。下面是命令的详细解释：

```bash
/usr/local/inotify/bin/inotifywait -mrq \
--timefmt '%Y-%m-%d %H:%M:%S' \
--format '%T %w%f %e' \
-e close_write,modify,delete,create,attrib,move \
/usr/local/nginx/html/
```

**命令解析**

- `/usr/local/inotify/bin/inotifywait`: 这是 `inotifywait` 命令的完整路径。它指定了 `inotifywait` 工具的安装位置。

- `-mrq`: 这是三个选项的组合。
  - `-m` (监控模式): 使 `inotifywait` 在检测到事件后不退出，持续监控。
  - `-r` (递归): 监控指定目录及其子目录下的所有文件和目录。
  - `-q` (静默模式): 减少冗余的输出信息，只输出事件信息。

- `--timefmt '%Y-%m-%d %H:%M:%S'`: 设置时间格式，这里设置为 `年-月-日 时:分:秒` 的格式。

- `--format '%T %w%f %e'`: 设置输出格式，其中：
  - `%T` 是时间，按照 `--timefmt` 设置的格式显示。
  - `%w` 是被监控的目录路径。
  - `%f` 是事件发生的文件名（如果适用）。
  - `%e` 是发生的事件类型。

- `-e close_write,modify,delete,create,attrib,move`: 指定要监控的事件类型。
  - `close_write`: 文件被打开并写入后关闭。
  - `modify`: 文件或目录被修改。
  - `delete`: 文件或目录被删除。
  - `create`: 文件或目录被创建。
  - `attrib`: 文件或目录的属性被修改（例如权限或时间戳）。
  - `move`: 文件或目录被移动。

- `/usr/local/nginx/html/`: 这是要监控的目录路径。

**使用示例**

当您运行这个命令时，它会持续监控 `/usr/local/nginx/html/` 目录及其子目录下的文件系统事件，并按照指定的格式输出事件信息。例如，如果有一个文件被修改，您可能会看到类似这样的输出：

```
2023-04-01 12:34:56 /usr/local/nginx/html/page.html CLOSE_WRITE,CLOSE
```

这表示在指定时间，`page.html` 文件被写入后关闭。

**注意事项**

- 如果您监控的目录非常大或事件非常频繁，`inotify` 的队列可能会溢出。可以通过调整内核参数来增加队列大小，例如 `fs.inotify.max_user_watches`。

### 配合 rsync 实现自动推送脚本

```shell
#!/bin/bash

/usr/local/inotify/bin/inotifywait -mrq --timefmt '%d/%m/%y %H:%M' --format '%T %w%f %e' -e close_write,modify,delete,create,attrib,move //usr/local/nginx/html/ | while read file
do
       
        rsync -az --delete --password-file=/etc/rsyncd.passwd.client /usr/local/nginx/html/ sgg@192.168.44.102::ftp/
done
```

脚本结合了 `inotifywait` 和 `rsync`，目的是在 `/usr/local/nginx/html/` 目录下发生文件系统事件（如文件创建、修改、删除等）时，自动触发文件同步到远程服务器 `192.168.44.102` 上的 `ftp` 模块。下面是脚本的详细解释和一些可能需要考虑的点：

一旦脚本运行，它将持续监控指定目录，并在检测到任何指定的文件系统事件时，自动触发文件同步操作。例如，如果在 `/usr/local/nginx/html/` 目录下创建或修改了文件，`inotifywait` 会检测到 `create` 或 `modify` 事件，并触发 `rsync` 将更改推送到远程服务器。

**此脚本不可用于生产环境**

### inotify 常用参数

表格列出了 `inotifywait` 命令的一些常用参数及其说明。这些参数可以帮助您更精确地控制 `inotify` 的行为，以适应不同的监控需求。下面是对每个参数的详细解释：

| 参数        | 说明                                                         | 含义                                                         |
| ----------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| `-r`        | `--recursive`                                                | 递归监控目录。监控指定目录及其所有子目录。                   |
| `-q`        | `--quiet`                                                    | 安静模式。仅输出事件信息，不输出其他信息。                   |
| `-m`        | `--monitor`                                                  | 监控模式。使 `inotifywait` 在检测到事件后不退出，持续监控。  |
| `--exclude` |                                                              | 排除匹配的文件或目录。可以多次使用以排除多个模式。           |
| `--timefmt` |                                                              | 指定输出时间的格式。例如，`%Y-%m-%d %H:%M:%S`。               |
| `--format`  |                                                              | 指定输出格式。例如，`%T %w%f %e` 会输出时间、文件路径和事件类型。 |
| `-e`        | `--event`                                                    | 指定要监控的事件类型。可以指定多个事件，用逗号分隔。例如，`-e create,delete`。 |

**事件类型**

`-e` 参数后可以跟多个事件类型，这些事件类型包括但不限于：

- `access`: 文件或目录被读取。
- `modify`: 文件或目录的内容被修改。
- `attrib`: 文件或目录属性被改变。
- `close_write`: 文件被写入后关闭。
- `open`: 文件或目录被打开。
- `move_to`: 文件或目录被移动至另外一个目录。
- `move_from`: 文件或目录被移动另一个目录或从另一个目录移动至当前目录。
- `create`: 文件或目录被创建在当前目录。
- `delete`: 文件或目录被删除。
- `delete_self`: 监控的文件或目录被删除。
- `move_self`: 监控的文件或目录被移动。
- `umount`: 文件系统被卸载。

**使用示例**

假设您想要监控 `/var/log` 目录及其子目录，并且只关注文件的创建、修改和删除事件，您可以使用如下命令：

```bash
inotifywait -mrq --timefmt '%Y-%m-%d %H:%M:%S' --format '%T %w%f %e' -e create,modify,delete /var/log/
```

这个命令会持续监控 `/var/log/` 目录及其子目录，并在控制台输出格式化的时间、文件路径和事件类型。

**注意事项**

- 使用 `--exclude` 参数可以排除不需要监控的文件或目录，例如 `--exclude "/*.tmp"` 排除所有 `.tmp` 文件。
- `inotify` 有资源限制，包括可以监控的事件数量和监控的目录数量。如果超出限制，`inotifywait` 可能会失败。可以通过调整内核参数来增加这些限制，例如 `fs.inotify.max_user_watches`。
- 确保 `inotify-tools` 已经安装在您的系统上，以便使用 `inotifywait` 命令。

`inotify` 是一个非常有用的工具，特别是在需要实时监控文件系统变化的场景中。通过合理配置参数，您可以灵活地监控和响应文件系统事件。
