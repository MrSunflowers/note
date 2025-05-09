[TOC]

## 使用版本

`v14.19.0`

&emsp;&emsp;js 在前端运行时是基于浏览器为运行环境的，因为浏览器实现了不同的功能接口来提供给 js 调用，

&emsp;&emsp;nodejs 就是提供了服务端的功能接口

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202202262329103.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202401081418469.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202202262330828.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202202262331601.png)

## 验证与运行命令

```
node -v 查看 node 版本
node test.js 运行 test.js 文件
npm init -y 初始化项目
```

## 1 文件操作

### 1.1 文件读写

```js
var fs = require('fs');//引入库
fs.readFile();//读文件
fs.writeFile();//写文件
```

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/spring/202202262149553.png)



![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/spring/202202262211840.png)

```js
// 文件读写 begin
const fs = require('fs');
fs.writeFile("../testFile","fileContent","utf-8",function (err) {
    if(err!=null){
        console.log(err);
    }
});

fs.readFile("../testFile","utf-8",function (err,fileContent) {
    if(err!=null){
        console.log(err);
    }
    console.log(fileContent);
});

fs.close(1,function (err){
    if(err!=null){
        console.log(err);
    }
});
// 文件读写 end
```

### 1.2 获取当前文件路径

```js
//获取当前文件所在文件夹路径
console.log(__dirname);
//获取当前文件路径
console.log(__filename);
```

## 2  path 文件路径模块

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/spring/202202262234109.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/spring/202202262242336.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/spring/202202262247481.png)

## 3  Http 模块

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202202271405068.png)

## 4  模块化

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202202271602368.png)

### 4.1 加载模块

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202202271607914.png)

### 4.2 模块的作用域

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202202271610155.png)

### 4.3 向外暴露模块中的成员

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202202271613708.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202202271614560.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202202271616193.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202202272009664.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202202272012038.png)

### 4.4 向外暴露成员时的注意事项

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202202272021750.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202202272017472.png)

### 4.5 模块化规范

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202202272022798.png)



## 5  npm 与包

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202202272026918.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202202272027481.png)

### 5.1 关于格式化时间的包

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202202272032555.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202202272033254.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203012122477.png)

### 5.2 安装指定版本的包

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202202272109823.png)

### 5.3 包管理配置文件

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202202272112047.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203012123610.png)

### 5.4 一次性安装所有依赖包

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202202272122466.png)

### 5.5 devDependencies 节点

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202202272124150.png)

### 5.6 切换下载源

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202202272127451.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202202272130303.png)

### 5.7 包的分类

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203012123119.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202202272134147.png)

### 5.8 md 转 html 工具

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202202272136690.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202202272138285.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203012124606.png)

## 6  模块的加载顺序

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202202272200487.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202202272201386.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202202272202518.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203012124381.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202202272237596.png)

## 7  Express web 开发框架

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202202272239303.png)

### 7.1 使用版本 

```shell
npm i express@4.17.1
```

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203012125990.png)



![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202202272247147.png)

### 7.2 基本使用

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202202272248422.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202202272255934.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202202272255300.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202202272256435.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202202272301216.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202202272307531.png)

### 7.3 托管静态资源

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203012047513.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203012125346.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203012127913.png)

### 7.4 托管多个静态资源目录

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203012051616.png)

### 7.5 挂载路径前缀

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203012053590.png)

### 7.6 热部署 nodemon

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203012104103.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203012105270.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203012106434.png)

## 8  Express 路由

路由就是映射关系

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203012109076.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203012128202.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203012112085.png)

### 8.1 路由的匹配过程

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203012113805.png)

### 8.2 路由的使用

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203012114607.png)

### 8.3 模块化路由

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203012117527.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203012128198.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203012120296.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203012130327.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203012135555.png)

注册路由时可以加入统一的访问前缀

## 9. Express 中间件

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203012138460.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203012141559.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203012142794.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203012143715.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203012145057.png)

### 9.1 全局中间件

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203012146085.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203012147166.png)



![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203012221643.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203022008661.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203022023568.png)

### 9.2 局部中间件

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203022026498.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203022028709.png)

### 9.3 中间件使用的注意点

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203022031022.png)

### 9.4 中间件的分类

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203022033267.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203022033427.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203022034612.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203022039447.png)

### 9.5 Express 内置中间件

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203022043222.png)

#### 9.5.1 解析 JSON 请求

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203022045732.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203022047272.png)

#### 9.5.2 解析表单数据

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203022049108.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203022051896.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203022052335.png)

### 9.6 第三方中间件

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203022053593.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203022055090.png)

### 9.7 自定义中间件

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203022057332.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203022059658.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203022101653.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203022101989.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203022103884.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203022104125.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203022105645.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203022107029.png)

## 10 使用 Express 写接口

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203022111871.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203022113494.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203022113464.png)

### 10.1 编写 get 接口

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203022114736.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203022115674.png)

### 10.2 编写 post 接口

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203022117458.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203022119044.png)

## 11 接口的跨域请求问题

### 11.1 CORS 方式

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203022123324.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203022124394.png)

```shell
npm i cors@2.8.5
```

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203022126906.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203022127337.png)

#### 11.1.1 CORS 跨域资源共享

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203022128795.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203022129034.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203022130981.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203022131397.png)

#### 11.1.2  CORS 请求分类

客户端在请求 CORS 接口时，根据请求方式和请求头的不同，可以将 CORS 的请求分为两大类：

1. 简单请求

2. 预检请求

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203022135791.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203022136581.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203022137670.png)

### 11.2 JSONP 方式

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203022142654.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203022143341.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203022145383.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203022145012.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203022149251.png)

## 12 数据库操作

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203052307313.png)

### 12.1 安装 mysql 模块

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203052308910.png)

```shell
npm i mysql@2.18.1
```

### 12.2 配置 mysql 模块

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203052312695.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203052317635.png)

### 12.3 查询数据

```javascript
const mysql = require('mysql');

const db = mysql.createPool({
    host: '101.43.208.215', //数据库IP
    user: 'root', //账号
    password: 'root', //密码
    database: 'mydb' //数据库实例
});

db.query('select * from users', (err, results) => {
    if (err) return console.log(err.message);
    console.log(results);
});
```

### 12.4 插入数据

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203052324519.png)

```javascript
const mysql = require('mysql');

const db = mysql.createPool({
    host: '101.43.208.215', //数据库IP
    user: 'root', //账号
    password: 'root', //密码
    database: 'mydb' //数据库实例
});

const user = {username:'testUser',password:'123'};

const sql = 'insert into users (username,password) values (?,?)';

db.query(sql, [user.username,user.password],(err, results) => {
    if (err) return console.log(err.message);
    console.log(results);
});
```

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203052339468.png)

### 12.5 数据更新

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203052341347.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203052342452.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203052346475.png)

## 13 前后端身份认证

### 13.1 web 开发模式

主流分为两种

1. 基于服务端渲染的传统 web 开发

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203060006205.png)

2. 基于前后端分离的新型 web 开发

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203060008114.png)

### 13.2 身份认证

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203060011818.png)

### 13.3 session 身份认证

通过客户端存储的 Cookie 来标识用户身份。

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203060017424.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203060018606.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203060020210.png)

###  13.4 session 中间件的使用

```shell
npm i express-session@1.17.1
```

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203060026114.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203060030476.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203060032834.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203060034065.png)

### 13.5  JWT 认证

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203060039263.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203060040045.png)

### 13.6  JWT 的组成

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203060042882.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203060043783.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203060044536.png)

### 13.7  安装 JWT 依赖包

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203060045556.png)

```shell
npm i jsonwebtoken@8.5.1 express-jwt@5.3.3
```

貌似有问题

### 13.8  定义 secret 密钥

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203060055452.png)

### 13.9  生成 JWT 字符串

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203060057206.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203061956466.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203062000983.png)

### 13.10  还原 JWT 字符串

前端使用时需要添加 `Authorization：Bearer+空格+token`

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203060059509.png)

### 13.11  从 req.user 获取用户信息

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203061412652.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203061413062.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203061503096.png)

## 14 项目开发

[Headline - api_server_ev (escook.cn)](http://www.escook.cn:8088/#/)

### 14.1 密码加密

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203061553585.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203061953796.png)

修改密码

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203062010556.png)

### 14.2 数据验证

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203061930017.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203061931903.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203061931676.png)

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202203061932865.png)



