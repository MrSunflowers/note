[TOC]

# JMeter

Jmeter是由Apache公司开发的一个纯Java的开源项目，即可以用于做接口测试也可以用于做性能测试。

Jmeter具备高移植性，可以实现跨平台运行。

Jmeter可以实现分布式负载。

Jmeter采用多线程，允许通过多个线程并发取样或通过独立的线程对不同的功能同时取样。

Jmeter具有较高扩展性。

# Jmeter安装

1、安装JDK，必须JDK1.7以上的版本，推荐1.8的版本

2、进入官网：http://jmeter.apache.org/download_jmeter.cgi 下载最新的Jmeter版本,下载后解压到非中文目录，如：D:\

3、配置Jmeter的环境变量。

(1) 新增变量：JMETER_HOME：D:\apache-jmeter-5.2.1

(2)在 CLASSPATH 变量的最前面加入如下变量： %JMETER_HOME%\lib\ext\ApacheJMeter_core.jar;%JMETER_HOME%\lib\jorphan.jar;

(3)在PATH变量的最前面加入如下变量：%JMETER_HOME%\bin;

4、进入D:\apache-jmeter-5.2.1\bin，双击jmeter.bat，或在dos窗口输入jmeter命令打开jmeter界面，安装成功。

# 设置Jmeter语言为中文环境

1、临时设置

Jmeter菜单栏选择OptionsàChoose LanguageàChinese (Simplified)

这种方法，重启软件后又变为英文环境了。

2、永久设置

进入apache-jmeter-5.2.1\bin目录，找到“jmeter.properties”文件，在文件的第37行后添加“language=zh_CN”，保存之后再打开jmeter就永久变为中文环境了。

![img](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202303282141247.png)

# Jmeter主要元件

1、测试计划：是使用 JMeter 进行测试的起点，它是其它 JMeter测试元件的容器

2、线程组：代表一定数量的用户，它可以用来模拟用户并发发送请求。实际的请求内容在Sampler中定义，它被线程组包含。

3、配置元件：维护Sampler需要的配置信息，并根据实际的需要修改请求的内容。

4、前置处理器：负责在请求之前工作，常用来修改请求的设置

5、定时器：负责定义请求之间的延迟间隔。

6、取样器(Sampler)：是性能测试中向服务器发送请求，记录响应信息、响应时间的最小单元，如：HTTP Request Sampler、FTP Request Sample、TCP Request Sample、JDBC Request Sampler等，每一种不同类型的sampler 可以根据设置的参数向服务器发出不同类型的请求。

7、后置处理器：负责在请求之后工作，常用获取返回的值。

8、断言：用来判断请求响应的结果是否如用户所期望的。

9、监听器：负责收集测试结果，同时确定结果显示的方式。

10、逻辑控制器：可以自定义JMeter发送请求的行为逻辑，它与Sampler结合使用可以模拟复杂的请求序列。

# Jmeter元件的作用域和执行顺序

## 1.元件作用域

配置元件：影响其作用范围内的所有元件。

前置处理器：在其作用范围内的每一个sampler元件之前执行。

定时器：在其作用范围内的每一个sampler有效

后置处理器：在其作用范围内的每一个sampler元件之后执行。

断言：在其作用范围内的对每一个sampler元件执行后的结果进行校验。

监听器：在其作用范围内对每一个sampler元件的信息收集并呈现。

总结：从各个元件的层次结构判断每个元件的作用域。

## 2.元件执行顺序：

配置元件->前置处理器->定时器->取样器->后置处理程序->断言->监听器

注意事项：

1.前置处理器、后置处理器和断言等组件只能对取样器起作用，因此，如果在它们的作用域内没有任何取样器，则不会被执行。

2.如果在同一作用域内有多个同一类型的元件，则这些元件按照它们在测试计划中的上下顺序依次执行。

# Jmeter进行接口测试流程

使用Jmeter进行接口测试的基本步骤如下：

1.测试计划

2.线程组

3.HTTP Cookie管理器

4.Http请求默认值

5.Sampler(HTTP请求)

6.断言

7.监听器(查看结果树、图形结果、聚合报告等)

# Jmeter进行接口测试流程步骤详解

## 1、测试计划

打开Jmeter，在菜单左侧出现 “测试计划”。在这里测试计划我们可以把它理解成新建的空白项目，在这个空白项目下面可以添加一系列的接口。

![img](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202303282143106.jpeg)

## 2、线程组

添加方法：右键点击Test Plan->添加->线程(用户)->线程组。

元件描述：一个线程组可以看做一个虚拟用户组，线程组中的每个线程都可以理解为一个虚拟用户。

![img](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202303282144460.png)

(1)线程数：即虚拟用户数。设置多少个线程数也就是设置多少虚拟用户数

(2)Ramp-Up时间(秒)：设置虚拟用户数全部启动的时长。如果线程数为20,准备时长为10秒,那么需要10秒钟启动20个线程。也就是平均每秒启动2个线程。

(3)循环次数：每个线程发送请求的个数。如果线程数为20,循环次数为10,那么每个线程发送10次请求。总请求数为20*10=200。如果勾选了“永远”, 那么所有线程会一直发送请求,直到手动点击工具栏上的停止按钮,或者设置的线程时间结束。

## 3、HTTP Cookie管理器

添加方法：右键线程组->添加->配置元件->HTTP Cookie管理器。

元件描述：HTTP Cookie管理器可以像浏览器一样存储和发送cookie，如果你要发送一个带cookie的http请求，cookie manager会自动存储该请求的cookies，并且后面如果发送同源站点的http请求时，都可以用这个cookies。

![img](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202303282145074.jpeg)


## 4、HTTP请求默认值

添加方法：右键线程组->添加->配置元件->HTTP请求默认值。

元件描述：HTTP请求默认值是为了方便填写后续内容而设置。主要填写[服务器名称或IP]和[端口号]，后续的HTTP请求中就不用每次都填写IP地址和端口号了。

![img](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202303282146724.jpeg)

## 5、HTTP请求

添加方法：右键线程组->添加->Sampler->HTTP请求。

元件描述：HTTP请求包括接口请求方法、请求路径和请求参数等。

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202303282146197.jpeg)

HTTP请求详解

(1)名称：本属性用于标识一个取样器，建议使用一个有意义的名称。

(2)注释：对于测试没有任何作用，仅用户记录用户可读的注释信息。

(3)服务器名称或IP ：HTTP请求发送的目标服务器名称或IP地址。

(4)端口号：目标服务器的端口号，默认值为80 。

(5)协议：向目标服务器发送HTTP请求时的协议，可以是HTTP或者是HTTPS ，默认值为http

(6)方法：发送HTTP请求的方法，可用方法包括GET、POST、HEAD、PUT、TRACE、OPTIONS、DELETE等。

(7)路径：目标URL路径（不包括服务器地址和端口）

(8)内容编码：内容的编码方式，默认值为iso8859

(9)自动重定向：如果选中该选项，当发送HTTP请求后得到的响应是302/301时，JMeter 自动重定向到新的页面。

(10)使用keep Alive ：保持jmeter 和目标服务器之间的活跃状态，默认选中

(11)对Post使用multipart/from-data：当发送POST 请求时，使用multipart/from-data方法发送，默认不选中。

(12)同请求一起发送参数 ： 在请求中发送URL参数，对于带参数的URL ，jmeter提供了一个简单的对参数化的方法。用户可以将URL中所有参数设置在本表中，表中的每一行是一个参数值对（对应RUL中的 名称1=值1）。

## 6、响应断言

添加方法：右键HTTP请求->添加->取样器->HTTP请求。

元件描述：检查接口是否访问成功。如果检查失败的话会提示找不到断言的内容，没提示的话就代表成功了

![img](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202303282147003.jpeg)

Apply to

指断言作用范围，通常发出一个请求只触发一个请求，所以勾选“main sample only”就可以；若发一个请求可以触发多个服务器请求，就有main sample 和sub-sample之分了。

(1)Main sample and sub-samples：作用于主请求和子请求

(2)Main sample only：仅仅只作用于主请求

(3)Sub-samples only：仅仅只作用于子请求

(4)Jmeter Variable：作用于jmeter变量(输入框内输入jmeter变量名称)

测试字段

响应文本(匹配返回的json数据)、响应代码(匹配返回码:如200, 404,500等)、响应信息(匹配响应信息如“OK”字样)、响应头(匹配响应头)、请求头(匹配请求头)、URL样本(匹配请求的url链接,如果有重定向则包含请求url 和 重定向url)、[文档(文本)](匹配响应数据的文本形式)、忽略状态(一个请求有多个响应断言，第一个响应断言选中此项，当第一个响应断言失败时可以忽略此响应结果，继续进行下一个断言。如果下一个断言成功则还是判定事务是成功的)、请求数据(匹配请求数据)

模式匹配规则

包括：响应内容包含需要匹配的内容即代表响应成功，支持正则表达式。

匹配：响应内容要完全匹配需要匹配的内容即代表响应成功，大小写不敏感，支持正则表达式。

字符串：响应内容包含需要匹配的内容才代表响应成功，大小写敏感，不支持正则表达式

相等：响应内容要完全等于需要匹配的内容才代表响应成功，大小写敏感，不支持正则表达式

否：相当于取反，如果结果为true，勾上否就是false

或者：如果不想用AND连接（所有的模式都必须匹配，断言才算成功），用OR选项可以用于将多个断言模式进行OR连接(只要一个模式匹配，断言就是成功的)

测试模式

其实就是断言的数据。点击“添加”按钮，输入要断言的数据。

## 7、增加监听器

添加方法：线程组 ->右键添加 ->监听器 ->察看结果树。一般还会一并添加图形结果、聚合报告。

元件描述：树状形式显示接口的访问结果，包括请求结果、请求内容、服务器的响应内容。

![img](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202303282148132.jpeg)

