# 第四章:常用命令

## 一 命令的基本格式  

### 1. 命令的提示符  
`[root@localhost ~]#`  
- `[]`: 提示符分隔符号，无特殊含义  
- `root`: 当前登录用户  
- `@`: 分隔符号，无特殊含义  
- `localhost`: 系统简写主机名（完整主机名是`localhost.localdomain`）  
- `~`: 用户当前所在目录（家目录）  
- `#`: 命令提示符（超级用户为`#`，普通用户为`$`）  

### 2. 命令的基本格式  
`[root@localhost ~]# 命令 [选项] [参数]`  

#### ls命令示例  
**命令名称**：ls  
**英文原意**：list  
**所在路径**：/bin/ls  
**执行权限**：所有用户  
**功能描述**：显示目录下的内容  

```bash
[root@localhost ~]# ls [选项] [文件名或目录名]
```

**常用选项**：  
- `-a`: 显示所有文件（包括隐藏文件）  
- `--color=when`: 支持颜色输出（`when`可选`always`/`never`/`auto`，默认`always`）  
- `-d`: 显示目录信息而非目录下的文件  
- `-h`: 人性化显示文件大小（如KB/MB）  
- `-i`: 显示文件的i节点号  
- `-l`: 长格式显示文件详细信息  

#### 长格式输出解析  
```bash
[root@localhost ~]# ls -l
总用量 44
-rw-------. 1 root root 1207 1月 14 18:18 anaconda-ks.cfg
```

| 列数 | 含义 |
|------|------|
| 1 | 权限（如`-rw-------`） |
| 2 | 引用计数（文件硬链接数/目录子目录数） |
| 3 | 所有者（如`root`） |
| 4 | 所属组（如`root`） |
| 5 | 文件大小（默认单位：字节） |
| 6 | 文件修改时间（非创建时间） |
| 7 | 文件名 |

**选项**：调整命令功能；**参数**：命令操作对象（省略时使用默认参数）  

## 二 目录操作命令  

### 1. ls命令  
见前文详细说明。  

### 2. cd命令  
**命令名称**：cd  
**英文原意**：change directory  
**所在路径**：Shell内置命令  
**执行权限**：所有用户  
**功能描述**：切换所在目录  

#### cd命令简化用法  

| 特殊符号 | 作用 |
|----------|------|
| `~` | 切换到当前用户的家目录 |
| `..` | 切换到上级目录 |
| `.` | 当前目录 |
| `-` | 切换到上一次所在目录 |
| `/` | 根目录 |

### 3. pwd命令  
**命令名称**：pwd  
**英文原意**：print name of current/working directory  
**所在路径**：/bin/pwd  
**执行权限**：所有用户  
**功能描述**：查询所在的工作目录  

```bash
[root@localhost ~]# pwd
/root
```

### 4. mkdir命令  
**命令名称**：mkdir  
**英文原意**：make directories  
**所在路径**：/bin/mkdir  
**执行权限**：所有用户  
**功能描述**：创建空目录  

```bash
[root@localhost ~]# mkdir [选项] 目录名
```

**常用选项**：  
- `-p`: 递归创建多级目录（如`mkdir -p /a/b/c`）  

### 5. rmdir命令  
**命令名称**：rmdir  
**英文原意**：remove empty directories  
**所在路径**：/bin/rmdir  
**执行权限**：所有用户  
**功能描述**：删除空目录  

```bash
[root@localhost ~]# rmdir [选项] 目录名
```

**常用选项**：  
- `-p`: 递归删除空目录  

> **注意**：`rmdir`只能删除空目录，实际操作中更常用`rm -r`删除目录（见后文）。  

## 三 文件操作命令  

### 1. touch命令  
**命令名称**：touch  
**英文原意**：change file timestamps  
**所在路径**：/bin/touch  
**执行权限**：所有用户  
**功能描述**：创建空文件或修改文件时间戳  

```bash
[root@localhost ~]# touch 文件名
```

### 2. stat命令  
**命令名称**：stat  
**英文原意**：display file or file system status  
**所在路径**：/usr/bin/stat  
**执行权限**：所有用户  
**功能描述**：显示文件或文件系统的详细信息  

```bash
[root@localhost ~]# stat anaconda-ks.cfg
文件: "anaconda-ks.cfg"
大小: 1453       块: 4          IO块: 4096   普通文件
设备: 803h/2051d  Inode: 33574991    硬链接: 1
权限: (0600/-rw-------)  Uid: ( 0/    root)   Gid: ( 0/    root)
环境: system_u:object_r:admin_home_t:s0
最近访问: 2018-11-06 23:22:23.409038121 +0800
最近更改: 2018-10-24 00:53:08.760018638 +0800  # 数据修改时间
最近改动: 2018-10-24 00:53:08.760018638 +0800  # 状态修改时间
创建时间: -
```

### 3. cat命令  
**命令名称**：cat  
**英文原意**：concatenate files and print on the standard output  
**所在路径**：/bin/cat  
**执行权限**：所有用户  
**功能描述**：合并文件并输出内容  

```bash
[root@localhost ~]# cat [选项] 文件名
```

**常用选项**：  
- `-A`: 显示所有隐藏符号（相当于`-vET`）  
- `-E`: 显示行尾回车符`$`  
- `-n`: 显示行号  
- `-T`: 将Tab键显示为`^I`  
- `-v`: 显示特殊字符  

### 4. more命令  
**命令名称**：more  
**英文原意**：file perusal filter for crt viewing  
**所在路径**：/bin/more  
**执行权限**：所有用户  
**功能描述**：分屏显示文件内容  

**常用交互命令**：  
- **空格键**：向下翻页  
- **b**：向上翻页  
- **回车键**：向下滚动一行  
- **/字符串**：搜索指定字符串  
- **q**：退出  

### 5. less命令  
**命令名称**：less  
**英文原意**：opposite of more  
**所在路径**：/usr/bin/less  
**执行权限**：所有用户  
**功能描述**：分行显示文件内容（支持向上翻页）  

### 6. head命令  
**命令名称**：head  
**英文原意**：output the first part of files  
**所在路径**：/usr/bin/head  
**执行权限**：所有用户  
**功能描述**：显示文件开头内容  

```bash
[root@localhost ~]# head [选项] 文件名
```

**常用选项**：  
- `-n 行数`: 指定显示的行数（如`head -n 10 file.txt`显示前10行）  
- `-v`: 显示文件名  

### 7. tail命令  
**命令名称**：tail  
**英文原意**：output the last part of files  
**所在路径**：/usr/bin/tail  
**执行权限**：所有用户  
**功能描述**：显示文件结尾内容  

```bash
[root@localhost ~]# tail [选项] 文件名
```

**常用选项**：  
- `-n 行数`: 指定显示的行数（如`tail -n 5 file.txt`显示后5行）  
- `-f`: 实时监听文件新增内容（常用于日志监控）  

### 8. ln命令  
**命令名称**：ln  
**英文原意**：make links between files  
**所在路径**：/bin/ln  
**执行权限**：所有用户  
**功能描述**：在文件之间建立链接  

#### 基本格式  
```bash
[root@localhost ~]# ln [选项] 源文件 目标文件
```

**常用选项**：  
- `-s`: 建立软链接（不加`-s`则建立硬链接）  
- `-f`: 强制创建（若目标文件存在则先删除）  

#### 硬链接与软链接的区别  

| 特性 | 硬链接 | 软链接（符号链接） |
|------|--------|-------------------|
| Inode与Block | 与源文件相同 | 与源文件不同 |
| 跨分区 | 不支持 | 支持 |
| 链接目录 | 不支持 | 支持 |
| 源文件删除后 | 仍可访问 | 不可访问（失效） |
| 大小 | 与源文件相同 | 仅保存源文件路径（通常很小） |

#### 示例  
```bash
# 创建硬链接
[root@localhost ~]# touch cangls
[root@localhost ~]# ln /root/cangls /tmp/  # /tmp/cangls与源文件硬链接

# 创建软链接
[root@localhost ~]# touch bols
[root@localhost ~]# ln -s /root/bols /tmp/  # /tmp/bols为软链接
```

## 四 目录和文件都能操作的命令  

### 1. rm命令  
**命令名称**：rm  
**英文原意**：remove files or directories  
**所在路径**：/bin/rm  
**执行权限**：所有用户  
**功能描述**：删除文件或目录  

```bash
[root@localhost ~]# rm [选项] 文件或目录
```

**常用选项**：  
- `-f`: 强制删除（无提示）  
- `-i`: 交互删除（删除前询问）  
- `-r`: 递归删除（用于删除目录）  

> **危险提示**：`rm -rf /`会强制删除根目录下所有文件，谨慎使用！  

### 2. cp命令  
**命令名称**：cp  
**英文原意**：copy files and directories  
**所在路径**：/bin/cp  
**执行权限**：所有用户  
**功能描述**：复制文件和目录  

```bash
[root@localhost ~]# cp [选项] 源文件 目标文件
```

**常用选项**：  
- `-a`: 相当于`-dpr`（保留权限、递归复制、复制软链接本身）  
- `-d`: 复制软链接时保留链接属性  
- `-i`: 目标文件存在时询问是否覆盖  
- `-p`: 保留源文件的属性（所有者、权限、时间戳）  
- `-r`: 递归复制目录  

### 3. mv命令  
**命令名称**：mv  
**英文原意**：move (rename) files  
**所在路径**：/bin/mv  
**执行权限**：所有用户  
**功能描述**：移动文件或重命名  

```bash
[root@localhost ~]# mv [选项] 源文件 目标文件
```

**常用选项**：  
- `-f`: 强制覆盖（无提示）  
- `-i`: 交互移动（目标文件存在时询问）  
- `-v`: 显示详细过程  

#### 示例  
```bash
# 重命名文件
[root@localhost ~]# mv oldname.txt newname.txt

# 移动文件到目录
[root@localhost ~]# mv file.txt /tmp/

# 移动目录
[root@localhost ~]# mv dir1 /tmp/
```

## 五 基本权限管理  

### 1. 权限位的含义  
使用`ls -l`查看文件权限时，第一列共10位字符（不含末尾`.`），格式如下：  
`- rw- r-- r--`  

#### 权限位解析  
- **第1位**：文件类型  
  - `-`: 普通文件  
  - `d`: 目录  
  - `l`: 软链接  
  - `b`: 块设备文件（如硬盘分区）  
  - `c`: 字符设备文件（如键盘）  
  - `p`: 管道文件  
  - `s`: 套接字文件  

- **第2-4位**：所有者权限（u）  
- **第5-7位**：所属组权限（g）  
- **第8-10位**：其他人权限（o）  

#### 权限符号含义  
- `r`: 读权限（4）  
- `w`: 写权限（2）  
- `x`: 执行权限（1）  
- `-`: 无权限（0）  

### 2. chmod命令：修改权限  
**命令名称**：chmod  
**英文原意**：change file mode bits  
**所在路径**：/bin/chmod  
**执行权限**：所有用户  
**功能描述**：修改文件的权限模式  

#### 基本格式  
```bash
[root@localhost ~]# chmod [选项] 权限模式 文件名
```

**常用选项**：  
- `-R`: 递归修改目录下所有文件权限  

#### 权限模式表示方法  

##### 1. 符号模式  
格式：`[ugoa][+-=][rwx]`  
- **用户身份**：`u`（所有者）、`g`（所属组）、`o`（其他人）、`a`（所有用户）  
- **赋予方式**：`+`（添加权限）、`-`（移除权限）、`=`（设置权限）  
- **权限**：`r`、`w`、`x`  

**示例**：  
```bash
# 给所有者添加执行权限
chmod u+x file.txt

# 移除所属组的写权限
chmod g-w file.txt

# 设置其他人仅有读权限
chmod o=r file.txt

# 给所有用户添加读权限
chmod a+r file.txt
```

##### 2. 数字模式  
用三位八进制数表示权限（每一位对应u/g/o）：  
- `r=4`、`w=2`、`x=1`，权限值为对应权限之和  

**常用权限组合**：  
- `644`: 文件默认权限（所有者rw-，组和其他人r--）  
- `755`: 执行文件/目录默认权限（所有者rwx，组和其他人r-x）  
- `777`: 最大权限（所有用户rwx，不建议使用）  

**示例**：  
```bash
# 设置权限为rw-r--r--（644）
chmod 644 file.txt

# 设置权限为rwxr-xr-x（755）
chmod 755 script.sh

# 递归设置目录权限
chmod -R 755 /data/
```

### 3. 权限对文件和目录的作用  

#### 对文件的作用  
- `r`: 读取文件内容（可执行`cat`/`more`/`less`等命令）  
- `w`: 修改文件内容（可执行`vim`/`echo`等命令，**不包括删除文件**）  
- `x`: 执行文件（文件必须为可执行程序）  

#### 对目录的作用  
- `r`: 查看目录下的文件列表（可执行`ls`命令）  
- `w`: 修改目录内容（可创建/删除/重命名子文件/目录，需配合`x`权限）  
- `x`: 进入目录（可执行`cd`命令）  

### 4. chown命令：修改所有者和所属组  
**命令名称**：chown  
**英文原意**：change file owner and group  
**所在路径**：/bin/chown  
**执行权限**：所有用户  
**功能描述**：修改文件/目录的所有者和所属组  

#### 基本格式  
```bash
[root@localhost ~]# chown [选项] 所有者:所属组 文件或目录
```

**常用选项**：  
- `-R`: 递归修改目录下所有文件  

**示例**：  
```bash
# 修改所有者为user1
chown user1 file.txt

# 修改所属组为group1
chown :group1 file.txt

# 同时修改所有者和所属组
chown user1:group1 file.txt

# 递归修改目录
chown -R user1:group1 /data/
```

### 5. umask默认权限  
`umask`用于设置创建文件/目录时的默认权限掩码，通过`umask`命令查看：  

```bash
[root@localhost ~]# umask
0022  # 八进制表示
[root@localhost ~]# umask -S
u=rwx,g=rx,o=rx  # 符号表示
```

#### 计算方法  
- **文件默认最大权限**：666（无执行权限）  
- **目录默认最大权限**：777  
- **实际权限** = 最大权限 - umask值  

#### 示例  
若`umask=022`：  
- **文件权限**：666 - 022 = 644（`rw-r--r--`）  
- **目录权限**：777 - 022 = 755（`rwxr-xr-x`）  

> **注意**：umask计算不是简单数字相减，而是权限位的按位取反后与最大权限进行按位与运算。  

## 六 帮助命令  

### 1. man命令  
**命令名称**：man  
**英文原意**：format and display the on-line manual pages  
**所在路径**：/usr/bin/man  
**执行权限**：所有用户  
**功能描述**：显示联机帮助手册  

#### 基本格式  
```bash
[root@localhost ~]# man [选项] 命令
```

**常用选项**：  
- `-f`: 查看命令所属的帮助级别（相当于`whatis`命令）  
- `-k`: 搜索包含关键词的帮助（相当于`apropos`命令）  

#### 帮助级别  
man手册分为9个级别，常用级别：  
- `1`: 用户命令  
- `5`: 配置文件  
- `8`: 管理员命令  

#### 常用快捷键  
- **空格键**：向下翻页  
- **b**：向上翻页  
- **q**：退出  
- `/关键词`：向下搜索关键词  
- `?关键词`：向上搜索关键词  
- `n`：跳转到下一个搜索结果  

### 2. info命令  
**命令名称**：info  
**功能描述**：提供更详细的帮助信息（层级结构）  

#### 常用快捷键  
- **Tab**：在节点间切换  
- **Enter**：进入子节点  
- **u**：返回上一层  
- **q**：退出  

### 3. help命令  
**命令名称**：help  
**功能描述**：获取Shell内置命令的帮助（如`cd`/`echo`等）  

```bash
[root@localhost ~]# help cd
```

### 4. --help选项  
大多数命令支持`--help`选项，快速查看简要帮助：  

```bash
[root@localhost ~]# ls --help
[root@localhost ~]# cp --help
```

## 七 搜索命令  

### 1. whereis命令  
**命令名称**：whereis  
**英文原意**：locate the binary, source, and manual page files for a command  
**所在路径**：/usr/bin/whereis  
**执行权限**：所有用户  
**功能描述**：搜索系统命令的二进制文件、源文件和帮助文档路径  

```bash
[root@localhost ~]# whereis ls
ls: /bin/ls /usr/share/man/man1/ls.1.gz
```

### 2. which命令  
**命令名称**：which  
**英文原意**：shows the full path of (shell) commands  
**所在路径**：/usr/bin/which  
**执行权限**：所有用户  
**功能描述**：显示命令的二进制文件路径及别名  

```bash
[root@localhost ~]# which ls
alias ls='ls --color=auto'
        /bin/ls
```

### 3. locate命令  
**命令名称**：locate  
**英文原意**：find files by name  
**所在路径**：/usr/bin/locate  
**执行权限**：所有用户  
**功能描述**：快速搜索文件（基于数据库`/var/lib/mlocate/mlocate.db`）  

#### 基本用法  
```bash
[root@localhost ~]# locate 文件名
```

#### 注意事项  
- 首次使用前需更新数据库：`updatedb`（后台自动更新，手动更新需root权限）  
- 配置文件：`/etc/updatedb.conf`（可排除不需要索引的路径）  

### 4. find命令  
**命令名称**：find  
**英文原意**：search for files in a directory hierarchy  
**所在路径**：/bin/find  
**执行权限**：所有用户  
**功能描述**：在指定目录下递归搜索文件（实时搜索，支持多条件）  

#### 基本格式  
```bash
[root@localhost ~]# find 搜索路径 [选项] 搜索内容
```

#### 常用选项  

##### 按文件名搜索  
- `-name "文件名"`: 区分大小写  
- `-iname "文件名"`: 不区分大小写  
- `-inum inode号`: 按inode号搜索  

**示例**：  
```bash
# 搜索/etc目录下名为passwd的文件
find /etc -name "passwd"

# 搜索当前目录下以.txt结尾的文件
find . -name "*.txt"
```

##### 按文件大小搜索  
`-size [+|-]大小`  
- **单位**：  
  - `b`: 512字节（默认）  
  - `c`: 字节  
  - `k`: KB（1024字节）  
  - `M`: MB（1048576字节）  
  - `G`: GB（1073741824字节）  
- `+大小`: 大于指定大小  
- `-大小`: 小于指定大小  

**示例**：  
```bash
# 搜索大于100MB的文件
find / -size +100M

# 搜索小于1KB的文件
find /tmp -size -1k
```

##### 按修改时间搜索  
Linux文件有三种时间戳：  
- `atime`: 访问时间（读取内容）  
- `mtime`: 数据修改时间（内容变化）  
- `ctime`: 状态修改时间（权限/所有者变化）  

`-mtime [+|-]天数`  
- `+天数`: 大于指定天数未修改  
- `-天数`: 小于指定天数内修改  

**示例**：  
```bash
# 搜索3天内修改过的文件
find /data -mtime -3

# 搜索7天前修改过的文件
find /var/log -mtime +7
```

##### 按权限搜索  
`-perm 权限模式`  
- `perm 644`: 权限**完全匹配**644  
- `perm -644`: 权限**包含**644（即所有者至少rw，组和其他人至少r）  

**示例**：  
```bash
# 搜索权限为755的文件
find /usr/bin -perm 755

# 搜索所有者有写权限的文件
find /home -perm -u+w
```

##### 按所有者和所属组搜索  
- `-user 用户名`: 按所有者搜索  
- `-group 组名`: 按所属组搜索  
- `-nouser`: 搜索无所有者的文件（可能为垃圾文件）  

**示例**：  
```bash
# 搜索所有者为user1的文件
find /home -user user1

# 搜索无所有者的文件
find / -nouser
```

##### 按文件类型搜索  
`-type 文件类型`  
- `f`: 普通文件  
- `d`: 目录  
- `l`: 软链接  
- `b`: 块设备  
- `c`: 字符设备  

**示例**：  
```bash
# 搜索所有目录
find /tmp -type d

# 搜索所有软链接
find /usr/bin -type l
```

##### 逻辑运算符  
- `-a`: 逻辑与（默认，可省略）  
- `-o`: 逻辑或  
- `-not`: 逻辑非  

**示例**：  
```bash
# 搜索大于100MB且类型为普通文件的文件
find / -size +100M -a -type f

# 搜索文件名以.txt或.log结尾的文件
find /var -name "*.txt" -o -name "*.log"

# 搜索不是目录的文件
find /tmp -not -type d
```

##### -exec选项：对搜索结果执行命令  
格式：`find ... -exec 命令 {} \;`  
- `{}`: 代表搜索结果的文件名  
- `\;`: 命令结束符  

**示例**：  
```bash
# 搜索.txt文件并删除
find /tmp -name "*.txt" -exec rm -f {} \;

# 搜索.log文件并查看详细信息
find /var/log -name "*.log" -exec ls -l {} \;
```

## 八 压缩和解压缩命令  

Linux常用压缩格式：`.zip`、`.gz`、`.bz2`、`.tar.gz`、`.tar.bz2`  

### 1. .zip格式  
#### 压缩命令：zip  
**命令名称**：zip  
**所在路径**：/usr/bin/zip  
**功能描述**：压缩文件或目录  

```bash
[root@localhost ~]# zip [选项] 压缩包名 源文件/目录
```

**常用选项**：  
- `-r`: 递归压缩目录  

**示例**：  
```bash
# 压缩文件
zip file.zip file1.txt file2.txt

# 压缩目录
zip -r dir.zip /data/dir/
```

#### 解压缩命令：unzip  
**命令名称**：unzip  
**所在路径**：/usr/bin/unzip  
**功能描述**：解压缩.zip文件  

```bash
[root@localhost ~]# unzip [选项] 压缩包名
```

**常用选项**：  
- `-d 目录`: 指定解压路径  

**示例**：  
```bash
# 解压到当前目录
unzip file.zip

# 解压到指定目录
unzip file.zip -d /tmp/
```

### 2. .gz格式  
#### 压缩命令：gzip  
**命令名称**：gzip  
**所在路径**：/bin/gzip  
**功能描述**：压缩文件（不保留源文件，不支持目录）  

```bash
[root@localhost ~]# gzip [选项] 源文件
```

**常用选项**：  
- `-c`: 将压缩数据输出到标准输出（可保留源文件）  
- `-d`: 解压缩  
- `-r`: 递归压缩目录下的所有文件  

**示例**：  
```bash
# 压缩文件（源文件会被删除）
gzip file.txt  # 生成file.txt.gz

# 压缩并保留源文件
gzip -c file.txt > file.txt.gz

# 解压缩
gzip -d file.txt.gz
```

#### 解压缩命令：gunzip  
**命令名称**：gunzip  
**功能描述**：解压缩.gz文件  

```bash
[root@localhost ~]# gunzip file.txt.gz
```

### 3. .bz2格式  
#### 压缩命令：bzip2  
**命令名称**：bzip2  
**所在路径**：/usr/bin/bzip2  
**功能描述**：压缩文件（比gzip压缩率更高，不保留源文件）  

```bash
[root@localhost ~]# bzip2 [选项] 源文件
```

**常用选项**：  
- `-k`: 保留源文件  
- `-d`: 解压缩  

**示例**：  
```bash
# 压缩文件（源文件会被删除）
bzip2 file.txt  # 生成file.txt.bz2

# 压缩并保留源文件
bzip2 -k file.txt

# 解压缩
bzip2 -d file.txt.bz2
```

#### 解压缩命令：bunzip2  
**命令名称**：bunzip2  
**功能描述**：解压缩.bz2文件  

```bash
[root@localhost ~]# bunzip2 file.txt.bz2
```

### 4. .tar格式（打包）  
tar命令用于打包文件/目录（不压缩），常与gzip/bzip2结合实现压缩。  

#### 打包命令  
```bash
[root@localhost ~]# tar [选项] 包名 源文件/目录
```

**常用选项**：  
- `-c`: 创建包  
- `-f`: 指定包名  
- `-v`: 显示过程  

**示例**：  
```bash
# 打包文件
tar -cvf files.tar file1.txt file2.txt

# 打包目录
tar -cvf dir.tar /data/dir/
```

#### 解打包命令  
```bash
[root@localhost ~]# tar [选项] 包名
```

**常用选项**：  
- `-x`: 解打包  
- `-f`: 指定包名  
- `-v`: 显示过程  
- `-t`: 查看包内文件列表  
- `-C 目录`: 指定解包路径  

**示例**：  
```bash
# 解打包到当前目录
tar -xvf files.tar

# 查看包内文件
tar -tvf dir.tar

# 解打包到指定目录
tar -xvf dir.tar -C /tmp/
```

### 5. .tar.gz和.tar.bz2格式（打包+压缩）  
#### .tar.gz格式（gzip压缩）  
```bash
# 压缩
tar -zcvf 包名.tar.gz 源文件/目录

# 解压缩
tar -zxvf 包名.tar.gz [-C 目录]
```

#### .tar.bz2格式（bzip2压缩）  
```bash
# 压缩
tar -jcvf 包名.tar.bz2 源文件/目录

# 解压缩
tar -jxvf 包名.tar.bz2 [-C 目录]
```

**示例**：  
```bash
# 压缩目录为.tar.gz
tar -zcvf data.tar.gz /data/

# 解压缩.tar.bz2到/tmp
tar -jxvf logs.tar.bz2 -C /tmp/

# 仅查看压缩包内容
tar -ztvf data.tar.gz
```

## 九 关机和重启命令  

### 1. sync命令：数据同步  
**命令名称**：sync  
**英文原意**：flush file system buffers  
**所在路径**：/bin/sync  
**执行权限**：所有用户  
**功能描述**：将内存中的数据同步到硬盘（防止数据丢失）  

```bash
[root@localhost ~]# sync
```

### 2. shutdown命令  
**命令名称**：shutdown  
**英文原意**：bring the system down  
**所在路径**：/sbin/shutdown  
**执行权限**：超级用户  
**功能描述**：安全关机或重启  

#### 基本格式  
```bash
[root@localhost ~]# shutdown [选项] 时间 [警告信息]
```

**常用选项**：  
- `-h`: 关机  
- `-r`: 重启  
- `-c`: 取消已计划的关机/重启  

**示例**：  
```bash
# 立即关机
shutdown -h now

# 10分钟后重启
shutdown -r +10 "System will reboot in 10 minutes"

# 指定时间关机（23:30）
shutdown -h 23:30

# 取消关机计划
shutdown -c
```

### 3. reboot命令  
**命令名称**：reboot  
**功能描述**：重启系统（等同于`shutdown -r now`）  

```bash
[root@localhost ~]# reboot
```

### 4. halt和poweroff命令  
- `halt`: 关机（直接关闭系统，不推荐）  
- `poweroff`: 关机并切断电源（不推荐）  

### 5. init命令  
修改系统运行级别（不推荐用于关机/重启）：  
- `init 0`: 关机  
- `init 6`: 重启  

## 十 常用网络命令  

### 1. ifconfig命令：配置网络接口  
**命令名称**：ifconfig  
**英文原意**：configure a network interface  
**所在路径**：/sbin/ifconfig  
**执行权限**：超级用户  
**功能描述**：查看/配置网络接口信息  

```bash
[root@localhost ~]# ifconfig
```

#### 输出解析  
```
eth0: flags=4163<UP,BROADCAST,RUNNING,MULTICAST>  mtu 1500
        inet 192.168.1.100  netmask 255.255.255.0  broadcast 192.168.1.255
        inet6 fe80::5054:ff:fe12:3456  prefixlen 64  scopeid 0x20<link>
        ether 52:54:00:12:34:56  txqueuelen 1000  (Ethernet)
        RX packets 1234  bytes 567890 (554.5 KiB)RX errors 0  dropped 0  overruns 0  frame 0
        TX packets 567  bytes 123456 (120.5 KiB)
        TX errors 0  dropped 0 overruns 0  carrier 0  collisions 0

lo: flags=73<UP,LOOPBACK,RUNNING>  mtu 65536
        inet 127.0.0.1  netmask 255.0.0.0
        ...
```

- `eth0`: 网卡名称（`lo`为本地回环）  
- `inet`: IPv4地址  
- `netmask`: 子网掩码  
- `broadcast`: 广播地址  
- `ether`: MAC地址  

### 2. ping命令：测试网络连通性  
**命令名称**：ping  
**英文原意**：send ICMP ECHO_REQUEST to network hosts  
**所在路径**：/bin/ping  
**执行权限**：所有用户  
**功能描述**：通过ICMP协议测试主机连通性  

```bash
[root@localhost ~]# ping [选项] IP地址/域名
```

**常用选项**：  
- `-c 次数`: 指定ping的次数  
- `-s 字节`: 指定数据包大小  
- `-b 广播地址`: 对整个网段进行探测  

**示例**：  
```bash
# 测试与网关的连通性
ping -c 4 192.168.1.1

# 测试域名解析
ping www.baidu.com
```

### 3. netstat命令：查看网络状态  
**命令名称**：netstat  
**英文原意**：Print network connections, routing tables, interface statistics...  
**所在路径**：/bin/netstat  
**执行权限**：所有用户  
**功能描述**：查看网络连接、端口监听、路由表等信息  

```bash
[root@localhost ~]# netstat [选项]
```

**常用选项组合**：  
- `-tuln`: 查看TCP/UDP监听端口  
  - `-t`: TCP协议  
  - `-u`: UDP协议  
  - `-l`: 监听状态  
  - `-n`: 显示IP和端口号（不解析域名/服务名）  

- `-an`: 查看所有网络连接  
- `-p`: 显示进程PID和名称（需root权限）  

**示例**：  
```bash
# 查看监听端口
netstat -tuln

# 查看所有连接及进程
netstat -anp | grep ESTABLISHED

# 查看路由表
netstat -r
```

### 4. write命令：向用户发送消息  
**命令名称**：write  
**英文原意**：send a message to another user  
**所在路径**：/usr/bin/write  
**执行权限**：所有用户  
**功能描述**：向在线用户发送实时消息  

```bash
[root@localhost ~]# write 用户名 终端
```

**示例**：  
```bash
# 查看用户登录终端
[root@localhost ~]# w
 10:00:00 up 1 day,  2:30,  2 users,  load average: 0.00, 0.01, 0.05
USER     TTY      FROM             LOGIN@   IDLE   JCPU   PCPU WHAT
root     tty1                      09:30    30:00   0.02s  0.02s -bash
user1    pts/0    192.168.1.101    09:45    10.00s  0.10s  0.05s vim file.txt

# 向user1发送消息
[root@localhost ~]# write user1 pts/0
Hello, this is a test message.
Press Ctrl+D to send.
^D  # 按Ctrl+D结束输入并发送
```

### 5. wall命令：广播消息  
**命令名称**：wall  
**英文原意**：write a message to all users  
**功能描述**：向所有在线用户发送广播消息  

```bash
[root@localhost ~]# wall "System will reboot in 5 minutes! Please save your work."
```

### 6. mail命令：发送电子邮件  
**命令名称**：mail  
**所在路径**：/bin/mail  
**执行权限**：所有用户  
**功能描述**：发送和接收电子邮件  

#### 发送邮件  
```bash
[root@localhost ~]# mail [选项] 收件人
```

**常用选项**：  
- `-s "主题"`: 指定邮件主题  

**示例**：  
```bash
# 交互式发送邮件
mail user1
Subject: Test Email
Hello user1,
This is a test email.
.  # 输入.并回车结束正文
Cc:  # 可输入抄送地址，直接回车发送

# 非交互式发送（从文件读取内容）
echo "Email content" | mail -s "Subject" user1

# 发送文件内容
mail -s "Log file" admin < /var/log/messages
```

#### 查看邮件  
```bash
[root@localhost ~]# mail
```

## 十一 系统痕迹命令  

### 1. w命令：查看当前登录用户  
**命令名称**：w  
**英文原意**：Show who is logged on and what they are doing.  
**所在路径**：/usr/bin/w  
**执行权限**：所有用户  
**功能描述**：显示当前登录用户及活动情况  

```bash
[root@localhost ~]# w
 14:30:00 up 2 days,  1:45,  3 users,  load average: 0.05, 0.03, 0.01
USER     TTY      FROM             LOGIN@   IDLE   JCPU   PCPU WHAT
root     tty1                      12:00    2:30m  0.05s  0.05s -bash
user1    pts/0    192.168.1.100    13:15    10.00s  0.12s  0.08s vim report.txt
user2    pts/1    192.168.1.102    14:20     0.00s  0.04s  0.00s w
```

### 2. who命令：简洁显示登录用户  
**命令名称**：who  
**功能描述**：显示当前登录用户的简略信息  

```bash
[root@localhost ~]# who
root     tty1         2023-10-01 12:00
user1    pts/0        2023-10-01 13:15 (192.168.1.100)
user2    pts/1        2023-10-01 14:20 (192.168.1.102)
```

### 3. last命令：查看登录历史  
**命令名称**：last  
**所在路径**：/usr/bin/last  
**执行权限**：所有用户  
**功能描述**：显示所有用户的登录历史记录（读取`/var/log/wtmp`）  

```bash
[root@localhost ~]# last
root     pts/0        192.168.1.100    Sun Oct  1 13:15   still logged in
user1    pts/1        192.168.1.101    Sat Sep 30 20:00 - down   (02:30)
reboot   system boot  3.10.0-1160.el7.x Sat Sep 30 19:58 - 22:30   (02:32)
```

### 4. lastlog命令：查看用户最后登录时间  
**命令名称**：lastlog  
**所在路径**：/usr/bin/lastlog  
**执行权限**：所有用户  
**功能描述**：显示所有用户最后一次登录信息（读取`/var/log/lastlog`）  

```bash
[root@localhost ~]# lastlog
Username         Port     From             Latest
root             tty1                      Sun Oct  1 12:00:00 +0800 2023
bin                                      **Never logged in**
daemon                                   **Never logged in**
user1            pts/0    192.168.1.100    Sun Oct  1 13:15:00 +0800 2023
```

### 5. lastb命令：查看错误登录记录  
**命令名称**：lastb  
**所在路径**：/usr/bin/lastb  
**执行权限**：所有用户（需root权限查看详细信息）  
**功能描述**：显示失败的登录尝试（读取`/var/log/btmp`）  

```bash
[root@localhost ~]# lastb
root     tty1         Sat Sep 30 22:35 - 22:35  (00:00)
user2    ssh:notty    192.168.1.200    Sat Sep 30 22:30 - 22:30  (00:00)
```

## 十二 挂载命令  

### 1. mount命令：挂载文件系统  
**命令名称**：mount  
**所在路径**：/bin/mount  
**执行权限**：所有用户  
**功能描述**：挂载存储设备（如硬盘分区、光盘、U盘等）  

#### 基本格式  
```bash
[root@localhost ~]# mount [选项] 设备文件名 挂载点
```

**常用选项**：  
- `-t 文件系统`: 指定文件系统类型（如`ext4`、`xfs`、`iso9660`）  
- `-o 选项`: 指定挂载参数（如`rw`/`ro`、`exec`/`noexec`等）  
- `-a`: 依据`/etc/fstab`自动挂载所有设备  
- `-l`: 显示卷标名称  

#### 常见文件系统类型  
- `ext4`: Linux主流文件系统  
- `xfs`: CentOS 7+默认文件系统  
- `iso9660`: 光盘文件系统  
- `vfat`: FAT32文件系统（Windows兼容）  

#### 示例  
```bash
# 挂载硬盘分区
mount /dev/sdb1 /mnt/disk1

# 挂载光盘
mount -t iso9660 /dev/cdrom /mnt/cdrom

# 挂载U盘（FAT32格式）
mount -t vfat /dev/sdc1 /mnt/usb -o iocharset=utf8  # 支持中文

# 查看已挂载的文件系统
mount
```

### 2. umount命令：卸载文件系统  
**命令名称**：umount  
**所在路径**：/bin/umount  
**执行权限**：所有用户  
**功能描述**：卸载已挂载的文件系统  

#### 基本格式  
```bash
[root@localhost ~]# umount 设备文件名/挂载点
```

**示例**：  
```bash
# 按设备文件名卸载
umount /dev/sdb1

# 按挂载点卸载
umount /mnt/cdrom

# 强制卸载（设备忙时）
umount -l /mnt/usb  # -l: 延迟卸载，进程结束后执行
```

### 3. /etc/fstab文件：自动挂载配置  
`/etc/fstab`文件记录系统启动时自动挂载的设备，格式如下：  

```
设备文件名  挂载点  文件系统类型  挂载选项  备份标记  自检顺序
/dev/sda1   /boot   ext4         defaults    0       0
UUID=xxx    /       xfs          defaults    0       0
/dev/cdrom  /mnt/cdrom iso9660     noauto      0       0
```

**字段说明**：  
- **设备文件名/UUID**: 设备标识（UUID更可靠）  
- **挂载点**: 必须为空目录  
- **文件系统类型**: 如`ext4`、`xfs`等  
- **挂载选项**: 常用`defaults`（默认权限）  
- **备份标记**: `0`不备份，`1`备份  
- **自检顺序**: `0`不自检，`1`先自检，`2`后自检  

> **注意**：修改`/etc/fstab`后需执行`mount -a`测试，避免配置错误导致系统无法启动。
### 4. 挂载U盘  
U盘设备文件名不固定，需先通过`fdisk -l`查询，再进行挂载：  

#### 基本步骤  
1. **查询U盘设备**  
```bash
[root@localhost ~]# fdisk -l  # 查看所有磁盘分区，U盘通常为/dev/sdb1、/dev/sdc1等
```

2. **挂载U盘**  
Windows格式化的U盘多为FAT32格式，挂载命令：  
```bash
# 基本挂载（不支持中文）
mount -t vfat /dev/sdb1 /mnt/usb/

# 支持中文（指定UTF-8编码）
mount -t vfat -o iocharset=utf8 /dev/sdb1 /mnt/usb/
```

3. **卸载U盘**  
```bash
umount /mnt/usb/
```

### 5. 挂载NTFS分区  
Linux默认不支持NTFS文件系统，需安装`ntfs-3g`插件：  

#### 安装步骤  
1. **下载并编译NTFS-3G**  
```bash
# 下载源码包（需从官网获取）
wget http://tuxera.com/opensource/ntfs-3g_ntfsprogs-2022.10.3.tgz

# 解压并编译
tar -zxvf ntfs-3g_ntfsprogs-2022.10.3.tgz
cd ntfs-3g_ntfsprogs-2022.10.3/
./configure  # 配置编译参数
make         # 编译
make install # 安装
```

2. **挂载NTFS分区**  
```bash
mount -t ntfs-3g /dev/sdb1 /mnt/ntfs/  # /dev/sdb1为NTFS分区
```

3. **卸载NTFS分区**  
```bash
umount /mnt/ntfs/
```