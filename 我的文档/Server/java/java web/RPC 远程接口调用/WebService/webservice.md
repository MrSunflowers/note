# 概述

&emsp;&emsp;webservice 即 web 服务，它是一种跨编程语言和跨操作系统平台的远程调用技术，Web Service 所使用的是 Internet 上统一、开放的标准，如 HTTP、XML、SOAP（简单对象访问协议）、WSDL 等，所以 Web Service 可以在任何支持这些标准的环境（Windows,Linux）中使用，这有助于大量异构程序和平台之间的互操作性，从而使存在的应用程序能够被广泛的用户访问。

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/spring/202203161014999.png)

&emsp;&emsp;JAVA 中共有三种 WebService 规范，分别是 JAX-WS（JAX-RPC）、JAXM&SAAJ、JAX-RS。

&emsp;&emsp;webService 三要素：soap、wsdl、uddi

# web service 开发规范

## SOAP 协议

&emsp;&emsp;SOAP 即简单对象访问协议 (Simple Object Access Protocol)，它用于交换 XML 编码信息的轻量级协议。它有三个主要方面：XML-envelope 为描述信息内容和如何处理内容定义了框架，将程序对象编码成为 XML 对象的规则，执行远程过程调用 (RPC) 的约定。SOAP可以运行在任何其他传输协议上。

1. SOAP 作为一个基于 XML 语言的协议用于有网上传输数据。
2. SOAP = 在 HTTP 的基础上 + XML 数据。
3. SOAP 是基于 HTTP 的。
4. SOAP 的组成如下

&emsp;&emsp;1). Envelope – 必须的部分。以XML的根元素出现。
&emsp;&emsp;2). Headers – 可选的。
&emsp;&emsp;3). Body – 必须的。在body部分，包含要执行的服务器的方法。和发送到服务器的数据。

## WSDL 接口描述

&emsp;&emsp;Web Service 描述语言 WSDL（SebService Definition Language）就是用机器能阅读的方式提供的一个基于 XML 语言的正式描述文档，用于描述 Web Service 及其函数、参数和返回值。因为是基于 XML 的，所以 WSDL 既是机器可阅读的，又是人可阅读的。

1. 通过 wsdl 说明书，就可以描述 webservice 服务端对外发布的服务；
2. wsdl 说明书是一个基于 xml 的文件，通过 xml 语言描述整个服务；
3. 在wsdl说明中，描述了：
&emsp;&emsp;1).​对外发布的服务名称（类）
&emsp;&emsp;2).接口方法名称（方法）
&emsp;&emsp;3).接口参数（方法参数）
&emsp;&emsp;4).服务返回的数据类型（方法返回值）
​
## UDDI web service 服务统一发布描述

Web 服务提供商使用 UDDI 将自己开发的 Web 服务公布到因特网上，UDDI 是一个跨产业，跨平台的开放性架构，可以帮助 Web 服务提供商在互联网上发布 Web 服务的信息。UDDI 是一种目录服务，企业可以通过 UDDI 来注册和搜索 Web 服务。简单来说，UDDI 就是一个目录，只不过在这个目录中存放的是一些关于 Web 服务的信息，并且 UDDI 通过 SOAP 进行通讯，构建于 . Net 之上。UDDI 的目的是为电子商务建立标准；UDDI 是一套基于 Web 的、分布式的、为 WebService 提供的、信息注册中心的实现标准规范，同时也包含一组使企业能将自身提供的 Web Service 注册，以使别的企业能够发现的访问协议的实现标准。

# JAVA  WebService 规范

分别是 JAXM&SAAJ、JAX-WS（JAX-RPC）、JAX-RS。

## JAX-WS

&emsp;&emsp;JAX-WS（Java API For XML-WebService），JDK1.6 自带的版本为 JAX-WS2.1，其底层支持为 JAXB。JAX-WS（JSR 224）规范的 API 位于`javax.xml.ws.*`包，其中大部分都是注解，提供 API 操作 Web 服务（ 通常在客户端使用的较多，由于客户端可以借助 SDK 生成，因此这个包中的 API 我们较少会直接使用）。

## JAXM&SAAJ

&emsp;&emsp;JAXM（JAVA API For XML Message）主要定义了包含了发送和接收消息所需的 API，相当于 Web 服务的服务器端，其 API 位于 `javax.messaging.*` 包，它是 JAVA EE 的可选包，因此你需要单独下载。

## SAAJ

&emsp;&emsp;SAAJ（SOAP With Attachment API For Java，JSR 67）是与 JAXM 搭配使用的 API，为构建 SOAP 包和解析 SOAP 包提供了重要的支持，支持附件传输，它在服务器端、客户端都需要使用。这里还要提到的是 SAAJ 规范，其 API 位于 `javax.xml.soap.*` 包。


&emsp;&emsp;JAXM&SAAJ 与 JAX-WS 都是基于 SOAP 的 Web 服务，相比之下 JAXM&SAAJ 暴漏了 SOAP 更多的底层细节，编码比较麻烦，而 JAX-WS 更加抽象，隐藏了更多的细节，更加面向对象，实现起来你基本上不需要关心 SOAP 的任何细节。那么如果你想控制 SOAP 消息的更多细节，可以使用 JAXM&SAAJ。

## JAX-RS

&emsp;&emsp;JAX-RS 是JAVA 针对 REST (RepresentationState Transfer) 风格制定的一套 Web 服务规范，由于推出的较晚，该规范并未随 JDK1.6 一起发行，你需要到 JCP 上单独下载 JAX-RS 规范的接口，其 API 位于 `javax.ws.rs.*` 包。这里的 JAX-WS 和 JAX-RS 规范我们采用 Apache CXF 作为实现，CXF 是 Objectweb Celtix 和 Codehaus XFire 合并而成。CXF 的核心是 org.apache.cxf.Bus（总线），类似于 Spring 的ApplicationContext，Bus 由BusFactory 创建，默认是 SpringBusFactory 类，可见默认 CXF 是依赖于 Spring 的，Bus 都有一个ID，默认的 BUS 的 ID 是 cxf。你要注意的是 Apache CXF2.2 的发行包中的 jar 你如果直接全部放到 lib 目录，那么你必须使用 JDK1.6，否则会报 JAX-WS 版本不一致的问题。对于 JAXM&SAAJ 规范我们采用 JDK 中自带的默认实现。

