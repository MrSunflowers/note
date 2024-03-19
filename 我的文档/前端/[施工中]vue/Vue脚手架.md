[TOC]
# Vue CLI 安装

[文档](https://cli.vuejs.org/zh/guide/)

安装：

```shell
npm install -g @vue/cli
```

如果出现下载缓慢可配置 npm 使用淘宝镜像

**使用淘宝镜像**：
在命令行中执行以下命令，将 npm 镜像源设置为淘宝镜像：
```bash
npm config set registry https://registry.npm.taobao.org
```

**验证设置是否生效**：
可以通过以下命令查看当前 npm 镜像源是否已经切换为淘宝镜像：
```bash
npm config get registry
```
如果输出为 `https://registry.npm.taobao.org`，则表示已成功切换为淘宝镜像。

**恢复默认镜像源**：
如果需要恢复为 npm 默认的镜像源，可以执行以下命令：
```bash
npm config set registry https://registry.npmjs.org
```

在安装过程中可能会出现警告或卡住的情况，回车跳过即可

验证安装是否成功

```bash
vue --version
```

如果 Vue CLI 安装成功，命令提示符会显示 Vue CLI 的版本号，类似于 `@vue/cli x.x.x`。

## 卸载

执行卸载命令：
在命令提示符中执行以下命令来卸载全局安装的 Vue CLI：
```bash
npm uninstall -g @vue/cli
```

验证是否成功卸载：
您可以通过以下命令来验证是否成功卸载 Vue CLI：
```bash
vue --version
```
如果 Vue CLI 工具已成功卸载，命令提示符会提示找不到 `vue` 命令或者显示类似的错误信息。

# 创建项目

切换至创建项目的目录

```shell
vue create my-project
```

![image-20240319160013403](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202403191600514.png)

![image-20240319160344326](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202403191603400.png)

至此，项目创建完成

# 启动项目

进入项目根目录，执行

```shell
npm run serve
```

# 项目结构

Vue.js 2 项目的文件结构通常遵循一定的约定，主要包括以下几个核心文件和目录：

`src` 目录：
- **`assets`：** 存放项目中使用的静态资源文件，如图片、样式表等。
- **`components`：** 存放 Vue 组件，可复用的 UI 组件通常放在这里。
- **`views`：** 存放页面级的 Vue 组件，每个页面对应一个 Vue 组件。
- **`router`：** 存放 Vue Router 路由配置文件，用于管理页面路由。
- **`store`：** 存放 Vuex 状态管理相关的文件，用于管理应用的状态。
- **`App.vue`：** 根组件，整个应用的入口组件。
- **`main.js`：** 应用的入口文件，初始化 Vue 实例，加载各种插件和配置。

`public` 目录：
- **`index.html`：** 应用的 HTML 入口文件，通常包含根元素和引入打包后的 JS、CSS 文件的标签。

`node_modules` 目录：
- 存放项目依赖的第三方模块，通过 npm 或 yarn 安装的依赖会放在这个目录下。

其他配置文件：
- **`package.json`：** 项目的配置文件，包含项目名称、版本、依赖等信息。
- **`package-lock.json`：** 是 npm 5 以后版本引入的一个文件，用于锁定安装时的依赖版本，文件会记录当前项目依赖包的确切版本号，包括直接和间接依赖。这样可以保证在不同环境下安装的依赖版本一致，避免由于依赖版本不一致导致的问题。
- **`babel.config.js`：** Babel 配置文件，用于配置 JavaScript 代码的转译规则。
- **`vue.config.js`：** Vue CLI 配置文件，用于配置构建工具、webpack 等相关配置。

其他文件和目录：
- **`README.md`：** 项目的说明文档，包含项目介绍、安装步骤等信息。
- **`.gitignore`：** Git 版本控制忽略文件列表，用于指定哪些文件不需要纳入版本控制。
- **`public` 目录下的其他静态文件：** 如 favicon.ico、robots.txt 等。

## main.js

`main.js` 是 Vue.js 项目的入口文件，主要负责创建 Vue 实例、加载各种插件和配置，并将根组件挂载到页面上。

### 主要结构和功能：

1. **导入 Vue 库和根组件：**
   - 导入 Vue.js ：`import Vue from 'vue';`
   - 导入根组件（通常是 `App.vue`）：`import App from './App.vue';`
2. **导入和配置插件：**
   - 导入需要使用的插件，如 Vue Router、Vuex 等。
   - 配置插件，例如使用插件的方式：`Vue.use(VueRouter);`
3. **创建 Vue 实例：**
   - 创建一个新的 Vue 实例：`new Vue({})`
   - 在实例中配置一些选项，如路由、状态管理等。
4. **挂载根组件到页面上：**
   - 使用 `render` 函数将根组件挂载到页面上：
   ```
   new Vue({
     render: h => h(App)
   }).$mount('#app');
   ```
   - 这里的 `#app` 是根元素的选择器，表示将根组件挂载到页面上的某个元素上。
5. **其他可能的配置：**
   - 可能会包括一些全局配置，如设置 Vue 的原型方法、全局组件等。

### 示例代码：

```javascript
import Vue from 'vue';
import App from './App.vue';
import VueRouter from 'vue-router';
import router from './router';
Vue.use(VueRouter);
new Vue({
  router,
  render: h => h(App)
}).$mount('#app');
```

在 `main.js` 中，通常会进行一些全局配置和初始化工作，以确保整个 Vue.js 应用的正常运行。它是整个项目的入口文件，负责协调各个模块的加载和交互，是 Vue.js 项目中非常重要的一个文件。

### render

`import Vue from 'vue'` 改代码默认情况下引入的为精简版的 vue 

![image-20240319175450458](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202403191754607.png)

其中的 vue.js 才是完整的 vue，即 vue.runtime.esm.js 中并没有包含模板解析器

之前写的

```js
import App from './App.vue'

new Vue({
	el:'#root',
	template:`<App></App>`,
	components:{App},
})
```

并不能直接运行，因为缺少了模板解析器

解决方法一：

引入完整的 vue

```javascript
import Vue from 'vue/dist/vue.js'
```


解决方法二：

使用 render 函数解析 template 模板

render 函数接收一个 createElement 函数作为参数，通过调用 createElement 函数来创建虚拟 DOM 元素，最终渲染到页面上。

在 Vue.js 中，`createElement` 函数是用于创建虚拟 DOM 元素的函数，通常在 `render` 函数中被调用。虚拟 DOM 元素是一个 JavaScript 对象，描述了要渲染到页面上的 DOM 结构。`createElement` 函数接受多个参数，用于描述要创建的 DOM 元素的类型、属性、子元素等信息。

下面是 `createElement` 函数的基本语法和参数说明：

```javascript
createElement(tag, options, children)
```

- `tag`：表示要创建的 DOM 元素的标签名，可以是一个字符串或一个组件选项对象。
- `options`：一个包含元素属性、事件处理程序等配置的对象。可以包含的配置项有 `attrs`、`on`、`class` 等。
- `children`：表示当前元素的子元素，可以是一个字符串、数组或其他虚拟 DOM 元素。

下面是一个简单的示例，展示了如何使用 `createElement` 函数创建一个包含文本内容的 `div` 元素：

```javascript
render: function(createElement) {
  return createElement('div', 'Hello, World!');
}
```

在这个示例中，我们调用 `createElement` 函数来创建一个 `div` 元素，并设置其文本内容为 "Hello, World!"。

除了上述基本用法外，`createElement` 函数还可以用于创建包含子元素的复杂结构。例如，可以通过传递一个数组作为 `children` 参数来创建包含多个子元素的元素。**或者传入一个 vue 组件，通常是 App 组件**

需要注意的是，`createElement` 函数通常在 `render` 函数中被调用，用于描述组件的结构。


## index.html

```html
<!DOCTYPE html>
<html lang="">
    <head>
        <meta charset="utf-8">
        <!--确保在 Internet Explorer 浏览器中以最新的标准模式来解析和渲染页面，避免出现兼容性问题。-->
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <!--这个 meta 标签告诉浏览器如何设置页面的宽度和缩放比例，以确保页面在移动设备上有良好的显示效果。-->
        <meta name="viewport" content="width=device-width,initial-scale=1.0">
        <!--
        这个标签告诉浏览器在网站标签页、收藏夹和书签中显示的图标文件路径
        BASE_URL 指向项目的public文件夹
        -->
        <link rel="icon" href="<%= BASE_URL %>favicon.ico">
        <!--配置网页标题 htmlWebpackPlugin.options.title 指向 package.json 的 name-->
        <title><%= htmlWebpackPlugin.options.title %></title>
    </head>
    <body>
        <!--noscript 标签用于在浏览器不支持脚本或禁用脚本时显示替代内容。
            当浏览器无法执行页面中的脚本时，会显示 noscript 标签内的内容。-->
        <noscript>
            <strong>We're sorry but <%= htmlWebpackPlugin.options.title %> doesn't work properly without JavaScript
                enabled. Please enable it to continue.</strong>
        </noscript>
        <div id="app"></div>
        <!-- built files will be auto injected -->
    </body>
</html>
```

