[TOC]

# JavaScript

[JavaScript 参考文档](https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Guide/Introduction)

参考文档非常详细，这里只做补充说明。

## JavaScript 组成

JavaScript 的内容，包含以下三部分：

- ECMAScript（核心）：JavaScript 语言基础(规定了 JavaScript 脚本的核心语法，如 数据类型、关键字、保留字、运算符、对象和语句等，它不属于任何浏览器。)；
- DOM（文档对象模型）：规定了访问 HTML 和 XML 的接口 (提供了访问 HTML 文档（如 body、form、div、textarea 等）的途径以及操作方法)；
- BOM（浏览器对象模型）：提供了独立于内容而在浏览器窗口之间进行交互的对象和方法(提供了访问某些功能（如浏览器窗口大小、版本信息、浏览历史记录等）的途径以及操作方法)。

## 基本特点

- 是一种解释性脚本语言（代码不进行预编译）。 
- 主要用来向 HTML（标准通用标记语言下的一个应用）页面添加交互行为。 
- 可以直接嵌入 HTML 页面，但写成单独的 js 文件有利于结构和行为的分离。 

## 日常用途

- 嵌入动态文本于 HTML 页面。 
- 对浏览器事件做出响应。 
- 读写 HTML 元素。 
- 在数据被提交到服务器之前验证数据。 
- 检测访客的浏览器信息。 
- 控制 cookies，包括创建和修改等。 

在技术快速发展的时代，JavaScript 不再局限于客户端开发，它也被用于后端开发。JavaScript 为开发人员提供了大量具有模块和特性的模板，使 JavaScript 应用程序的开发更容易。无论是开发动态网站还是 Web 应用程序，到处都能看到 JavaScript 的身影。

# JavaScript 在 HTML 中的存放位置

## 方式一

在 html 标签中，在任何地方都可以添加 script 标签。标签中可以指定标签中的脚本内容，`text/javascript` 就是 js 代码。 虽然可以放在页面的任何地方，但是规范放在 head 标签中。

```html
<script type="text/javascript">
    alert("这是 javascript 代码");
</script>
```

## 方式二

单独使用一个文件来编写 javascript 代码，在需要使用的页面中引入该文件。

```html
<script type="text/javascript" src="type2.js"></script>
```

## 方式三

嵌入到 html 中，例如把代码嵌入到 a 标签的 href 属性中，点击 a 标签的时候,就执行里面代码

```html
<a href="javascript:alert('type3');">type3</a>
```

# JavaScript 基本语法

## 语句块

每条功能执行语句的最后必须用分号(;)结束，每个词之间用空格、制表符、换行符或大括号、小括号这样的分隔符隔开 。语句块使用{}来表示。

## 注释

```javascript
//单行注释
/*多行注释*/
/**
 * 这是一个示例函数，用于加法运算。
 * @param {number} a - 第一个操作数
 * @param {number} b - 第二个操作数
 * @returns {number} - 两个操作数的和
 */
function add(a, b) {
  return a + b;
}
```

