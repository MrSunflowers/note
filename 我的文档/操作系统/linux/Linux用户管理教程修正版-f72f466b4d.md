# 第七章: Linux用户管理

## 一、用户相关文件

### 1. /etc/passwd 用户信息文件
示例: `root:x:0:0:root:/root:/bin/bash`

| 字段 | 含义 |
|------|------|
| 第一列 | 用户名 |
| 第二列 | 密码位 |
| 第三列 | 用户ID (UID) |
| 第四列 | 组ID (GID) |
| 第五列 | 用户说明 |
| 第六列 | 用户家目录 |
| 第七列 | 登录shell |

**UID说明**:
- 0: 超级用户UID
- 1-499: 系统用户(伪用户)UID
- 500-60000: 普通用户UID
- 2.6.x内核后支持2^32个UID

### 2. /etc/shadow 影子文件
示例: `root:$6$9w5Td6lg$bgpsy3olsq9WwWvS5Sst2W3ZiJpuCGDY.4w4MRk3ob/i85fI38RH15wzVoomff9isV1PzdcXmixzhnMVhMxbv0:15775:0:99999:7:::`

| 字段 | 含义 |
|------|------|
| 第一列 | 用户名 |
| 第二列 | 加密密码 |
| 第三列 | 密码最近更改时间(时间戳) |
| 第四列 | 两次密码修改间隔时间 |
| 第五列 | 密码有效期 |
| 第六列 | 密码修改到期前警告天数 |
| 第七列 | 密码过期后宽限天数 |
| 第八列 | 密码失效时间 |
| 第九列 | 保留 |

**时间戳转换示例**:
```bash
# 时间戳转日期
date -d "1970-01-01 15775 days"

# 日期转时间戳
echo $(($(date --date="2013/03/11" +%s)/86400+1))
```

### 3. /etc/group 组信息文件
示例: `root:x:0:root`

| 字段 | 含义 |
|------|------|
| 第一列 | 组名 |
| 第二列 | 组密码位 |
| 第三列 | 组ID (GID) |
| 第四列 | 附加组成员 |

**组概念**:
- 初始组: 每个用户只能有一个初始组
- 附加组: 每个用户可以有多个附加组

### 4. /etc/gshadow 组密码文件
保存组密码和组管理员信息

### 5. 用户家目录
默认位于`/home/用户名`

### 6. 用户邮箱目录
位于`/var/spool/mail/用户名`

### 7. 用户模板目录
`/etc/skel/` - 新建用户时会复制此目录内容到用户家目录

## 二、用户管理命令

### 1. 添加用户

#### 1.1 useradd 命令
```bash
useradd [选项] 用户名
```

**常用选项**:
- `-u`: 指定UID
- `-g`: 指定初始组
- `-G`: 指定附加组
- `-c`: 添加用户说明
- `-d`: 指定家目录(绝对路径)
- `-s`: 指定登录shell

**示例**:
```bash
# 创建用户lamp1
groupadd lamp1  # 先创建组
useradd -u 550 -g lamp1 -G root -d /home/lamp1 -c "test user" -s /bin/bash lamp1

# 验证创建结果
grep "lamp1" /etc/passwd /etc/shadow /etc/group
```

#### 1.2 useradd 默认配置文件
- `/etc/default/useradd`
- `/etc/login.defs`

**/etc/default/useradd 内容**:
```
GROUP=100
HOME=/home
INACTIVE=-1
EXPIRE=
SHELL=/bin/bash
SKEL=/etc/skel
CREATE_MAIL_SPOOL=yes
```

**/etc/login.defs 关键配置**:
```
MAIL_DIR /var/spool/mail
PASS_MAX_DAYS 99999
PASS_MIN_DAYS 0
PASS_MIN_LEN 5
PASS_WARN_AGE 7
UID_MIN 500
UID_MAX 60000
GID_MIN 500
GID_MAX 60000
CREATE_HOME yes
UMASK 077
USERGROUPS_ENAB yes
ENCRYPT_METHOD SHA512
```

### 2. 设定密码
```bash
passwd [选项] 用户名
```

**常用选项**:
- `-l`: 锁定用户
- `-u`: 解锁用户
- `--stdin`: 通过管道设置密码

**示例**:
```bash
# 设置用户密码
echo "123" | passwd --stdin user1

# 强制用户首次登录修改密码
chage -d 0 user1
```

### 3. 修改用户信息
```bash
usermod [选项] 用户名
```

**常用选项**:
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

### 4. 删除用户
```bash
userdel [-r] 用户名
```
- `-r`: 同时删除用户家目录和邮箱

### 5. 切换用户身份
```bash
su [选项] 用户名
```
- `-`: 连带环境变量一起切换
- `-c`: 仅执行一次命令

**示例**:
```bash
# 切换用户并执行命令
su - user1 -c "ls -l"
```

## 三、组管理命令

### 1. 添加用户组
```bash
groupadd [选项] 组名
```
- `-g`: 指定GID

### 2. 删除用户组
```bash
groupdel 组名
```
> 注意: 不能删除作为其他用户初始组的组

### 3. 管理组成员
```bash
gpasswd [选项] 组名
```
- `-a 用户名`: 添加用户到组
- `-d 用户名`: 从组中删除用户

**示例**:
```bash
# 添加用户到组
gpasswd -a user1 grouptest

# 从组中删除用户
gpasswd -d user1 grouptest
```

### 4. 改变有效组
```bash
newgrp 组名
```

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

# 创建文件(属组为当前有效组)
touch test2

# 查看结果
ll test1 test2
```