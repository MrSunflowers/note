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

其中的 vue.js 才是完整的 vue，即 vue.runtime.esm.js 中并没有包含模板解析器，原因是在脚手架中，模板解析器相当于是编译器的存在，在项目打包编译后即不需要了

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

**解决方法一**

引入完整的 vue

```javascript
import Vue from 'vue/dist/vue.js'
```


**解决方法二**

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

## vue.config.js

[配置参考 | Vue CLI (vuejs.org)](https://cli.vuejs.org/zh/config/)

可以使用 `vue inspect > output.js` 查看当前配置

`vue.config.js` 是一个可选的配置文件，用于配置 Vue 项目的构建配置。通过在项目根目录下创建 `vue.config.js` 文件，您可以对 Vue 项目的构建过程进行自定义配置，包括但不限于开发服务器配置、构建输出配置、Webpack 配置等。
下面是一些常见的配置选项，您可以在 `vue.config.js` 文件中进行设置：
1. **publicPath**: 设置应用部署的基本 URL。默认情况下，Vue CLI 会假设您的应用是部署在一个域名的根路径下，如果您的应用部署在子路径下，可以通过设置 `publicPath` 来指定子路径。
2. **outputDir**: 设置构建输出的目录。默认情况下，Vue CLI 会将构建生成的文件输出到 `dist` 目录下，您可以通过设置 `outputDir` 来指定输出目录。
3. **devServer**: 配置开发服务器。您可以在 `devServer` 中设置各种选项，如端口号、代理、热重载等。
4. **configureWebpack**: 如果您需要对 Webpack 进行更深层次的配置，可以通过 `configureWebpack` 选项来添加自定义的 Webpack 配置。
5. **chainWebpack**: 如果您需要对 Webpack 配置进行链式修改，可以使用 `chainWebpack` 选项。通过 `chainWebpack`，您可以在原有的 Webpack 配置基础上进行更细粒度的修改。
6. **css**: 在 `css` 选项中，您可以设置一些关于 CSS 相关的配置，如是否开启 CSS source map、是否启用 CSS Modules 等。
7. **pluginOptions**: 如果您需要配置一些 Vue CLI 插件的选项，可以在 `pluginOptions` 中进行设置。
8. **lintOnSave**: true false 请注意，这样做会关闭所有的语法检查，包括 ESLint、StyleLint 等
以上是一些常见的配置选项，您可以根据项目需求在 `vue.config.js` 文件中进行相应的配置。通过自定义 `vue.config.js` 文件，您可以更灵活地控制 Vue 项目的构建过程，满足项目的特定需求。

# 获取 dom 元素 $refs

vue 在元素中自定义了一个 `ref` 属性，用来代替元素 `dom` 中的 `id` 属性来唯一标识一个元素。

在 Vue 中，`ref` 属性可以用来在模板中为元素或组件指定一个引用标识，以便在组件实例中通过 `$refs` 访问该元素或组件。`ref` 属性与传统的 `id` 属性类似，但是在 Vue 中更加灵活且适用于组件化开发。

以下是一些关于在 Vue 中使用 `ref` 属性的重要事项：

1. **唯一性**：
   - 在同一个 Vue 组件中，`ref` 属性的值必须是唯一的，用来标识不同的元素或组件。
   - 不同组件中可以使用相同的 `ref` 名称，因为 `ref` 是在组件实例内部作为一个对象来存储的。
2. **在模板中使用**：
   - 在模板中，您可以通过 `ref` 属性为元素或组件添加引用。
   - 通过 `this.$refs` 可以访问这些引用，从而操作元素或调用组件方法。
3. **动态引用**：
   - 您也可以使用动态值来设置 `ref` 属性，从而实现动态引用。
   - 例如，可以使用 `v-for` 循环中的索引来动态设置 `ref` 属性。
4. **注意事项**：
   - 尽量避免过度使用 `ref`，因为过多的 `ref` 可能会导致代码难以维护。
   - 如果可能，尽量使用 props 和事件来实现组件之间的通信，而不是依赖 `ref`。

在原生写法中获取一个真实的 `dom` 元素：

```html
<h1 id="title">msg</h1>
var title = document.getElementById('title');
```

在 vue 中使用 VueComponent.$refs 来寻找元素中含有 ref 属性的元素：

下面是一个简单的示例，展示了如何在 Vue 中使用 `ref` 属性：

```vue
<template>
	<div>
		<h1 v-text="msg" ref="title"></h1>
		<button ref="btn" @click="showDOM">点我输出上方的DOM元素</button>
		<School ref="sch"/>
	</div>
</template>

<script>
	//引入School组件
	import School from './components/School'

	export default {
		name:'App',
		components:{School},
		data() {
			return {
				msg:'欢迎学习Vue！'
			}
		},
		methods: {
			showDOM(){
				console.log(this.$refs.title) //真实DOM元素
				console.log(this.$refs.btn) //真实DOM元素
				console.log(this.$refs.sch) //School组件的实例对象（vc）
			}
		},
	}
</script>
```

**当然原生的获取方式也同样可以支持**

# 组件自定义事件 $emit

在Vue中，自定义事件是一种用于组件之间通信的重要机制。通过自定义事件，一个组件可以向其父组件发送消息，从而实现组件之间的数据传递和交互。以下是关于Vue中自定义事件的详细说明：

1. **触发自定义事件**：
   - 在子组件中，可以使用`$emit`方法来触发自定义事件，并向父组件传递数据。例如：
     ```javascript
     // 子组件中触发自定义事件
     this.$emit('custom-event-name', data);
     ```
  
2. **监听自定义事件**：
   - 在父组件中，可以使用`v-on`指令（简写为`@`）来监听子组件触发的自定义事件，并执行相应的处理逻辑。例如：
     ```html
     <!-- 父组件中监听自定义事件 -->
     <child-component @custom-event-name="handleCustomEvent"></child-component>
     ```
     ```javascript
     // 在父组件中定义处理自定义事件的方法
     methods: {
       handleCustomEvent(data) {
         // 处理接收到的数据
       }
     }
     ```
3. **传递参数**：
   - 在触发自定义事件时，可以传递额外的参数给父组件。这些参数可以是任意类型的数据，比如字符串、对象等。
4. **事件修饰符**：
   - Vue还提供了事件修饰符，用于控制事件的行为。例如，`.stop`修饰符可以阻止事件冒泡，`.prevent`修饰符可以阻止默认行为，`.once`修饰符可以让事件只触发一次等。

### 第一种绑定方式

组件自定义事件不同于 DOM 的事件，是 vue 提供的，绑定在**组件实例对象**身上的事件

给组件绑定一个自定义事件

```xml
<child-component @custom-event-name="handleCustomEvent"></child-component>
```

因为是给子组件绑定的事件，所以事件的触发需要在子组件内部使用`$emit`方法来触发

```javascript
// 子组件中触发自定义事件
this.$emit('custom-event-name', data);
```

示例

```vue
<template>
  <div id="app">
    <MyTest @getSubDate="getSubDate" />
  </div>
</template>

<script>
import MyTest from "@/components/MyTest";
export default {
  name: 'App',
  components: {MyTest},
  methods:{
    getSubDate(date){
      console.log("获取到子组件的数据@"+date)
    }
  }
}
</script>
```

```vue
<template>
  <div class="todo-header">
    <input type="button" @click="sendDate" value="发送数据"/>
  </div>
</template>

<script>
export default {
  name: 'MyTest',
  data() {
    return {}
  },
  methods: {
    sendDate() {
      this.$emit('getSubDate','123')
    }
  }
}
</script>
```

### 第二种绑定方式

先获取到子组件的实例对象(`this.$refs.child-component`)，然后在组件挂载到DOM上的时候(生命周期 mounted 回调)，使用 `$on` 手动挂载 `this.$refs.child-component.$on('custom-event-name',this.handleCustomEvent)`

```xml
<template>
  <div id="app">
    <MyTest ref="MyTest"  />
  </div>
</template>

<script>
import MyTest from "@/components/MyTest";
export default {
  name: 'App',
  components: {MyTest},
  methods:{
    getSubDate(date){
      console.log("获取到子组件的数据@"+date)
    }
  },
  mounted() {
    this.$refs.MyTest.$on('getSubDate',this.getSubDate)
  }
}
</script>
```

自定义事件同样也支持事件修饰符

Vue还提供了事件修饰符，用于控制事件的行为。例如，`.stop`修饰符可以阻止事件冒泡，`.prevent`修饰符可以阻止默认行为，`.once`修饰符可以让事件只触发一次等。

```xml
<MyTest @getSubDate.stop="getSubDate" />
this.$refs.MyTest.$once('getSubDate',this.getSubDate)
```

### 解绑自定义事件

```js
this.$off('atguigu')
```

### 组件绑定原生DOM事件

组件上要绑定原生DOM事件，必需要使用`native`修饰符。

```xml
<Student @click.native="show"/>
```

注意：通过

```js
this.$refs.xxx.$on('atguigu',回调)
```

绑定自定义事件时，回调<span style="color:red">要么配置在methods中</span>，<span style="color:red">要么用箭头函数</span>，否则this指向会出问题！

# 全局事件总线

全局事件总线是一种在Vue.js应用程序中用于跨组件通信的模式。它允许您在应用程序中的**任何组件之间进行事件的广播和监听**，而不需要通过父子组件关系传递数据。全局事件总线通常基于Vue实例作为中介，用于处理组件之间的通信。

本质是通过一个中间人来传递自定义事件，此时就用到了动态绑定自定义事件 

以下是使用全局事件总线的基本步骤：

1. **创建全局事件总线**：在Vue应用程序中，您可以创建一个新的Vue实例作为全局事件总线，即中间人。通常，您可以在单独的JavaScript文件中创建这个Vue实例，并导出它以便其他组件使用。
   ```javascript
   // EventBus.js
   import Vue from 'vue';
   export const EventBus = new Vue();
   ```

2. **触发事件**：在任何组件中，您可以通过全局事件总线实例触发事件，并传递需要的数据。
   ```javascript
   // ComponentA.vue
   import { EventBus } from './EventBus.js';
   // 触发事件并传递数据
   EventBus.$emit('event-name', eventData);
   ```

3. **监听事件**：在其他组件中，您可以通过全局事件总线实例监听事件，并定义处理逻辑。
   ```javascript
   // ComponentB.vue
   import { EventBus } from './EventBus.js';
   // 监听事件
   EventBus.$on('event-name', (data) => {
     // 处理接收到的数据
   });
   ```

通过全局事件总线，不同组件之间可以实现解耦，灵活地进行通信，从而简化组件之间的交互。然而，需要注意的是，全局事件总线可能会导致一些难以维护的问题，比如事件命名冲突、难以追踪数据流等，因此在使用时需要谨慎考虑。

示例：

1. **创建全局事件总线**：首先创建一个名为`EventBus.js`的文件，用于定义全局事件总线。
```javascript
// EventBus.js
import Vue from 'vue';
export const EventBus = new Vue();
```

2. **组件A**：在组件A中触发一个事件，并传递数据。
```vue
<!-- ComponentA.vue -->
<template>
  <div>
    <button @click="sendMessage">发送消息到组件B</button>
  </div>
</template>
<script>
import { EventBus } from './EventBus.js';
export default {
  methods: {
    sendMessage() {
      EventBus.$emit('message-from-component-a', 'Hello from Component A!');
    }
  }
}
</script>
```

3. **组件B**：在组件B中监听事件，并处理接收到的数据。
```vue
<!-- ComponentB.vue -->
<template>
  <div>
    <p>组件B</p>
    <p>接收到的消息：{{ message }}</p>
  </div>
</template>
<script>
import { EventBus } from './EventBus.js';
export default {
  data() {
    return {
      message: ''
    };
  },
  created() {
    EventBus.$on('message-from-component-a', (data) => {
      this.message = data;
    });
  }
}
</script>
```
在这个示例中，组件A中的按钮点击事件会触发一个名为`message-from-component-a`的事件，并传递字符串数据。组件B在创建时监听这个事件，接收到数据后更新页面显示。通过全局事件总线，组件A和组件B之间实现了解耦的通信。

也可以自身作为事件总线

```js
new Vue({
	......
	beforeCreate() {
		Vue.prototype.$bus = this //安装全局事件总线，$bus就是当前应用的vm
	},
    ......
}) 
```

使用 

```js
methods(){
  demo(data){......}
}
......
mounted() {
  this.$bus.$on('xxxx',this.demo)
}
```

提供数据：

```
this.$bus.$emit('xxxx',数据)
```

最好在beforeDestroy钩子中，用$off去解绑<span style="color:red">当前组件所用到的</span>事件。

# 组件通信

## 获取子组件实例对象

当 `<School ref="sch"/>` ref 标识在子组件标签上时 `console.log(this.$refs.sch)` 获取到的即是 School 组件的实例对象（vc）

当您想要通过 `ref` 来获取子组件的属性时，可以在父组件中使用 `ref` 来引用子组件，并通过该引用来访问子组件的属性。

## 父->子传参 props

VueComponent.props

在向 vue 的子组件中传递参数时，可以在组件标签中以属性的方式传递，如

```xml
<template>
	<div>
		<Student name="李四" sex="女" :age="18"/>
	</div>
</template>
```

上述示例传递了三个参数 `name="李四" sex="女" :age="18"` 其中 `age` 为 数字类型，默认按字符串类型传递，所以要加上 `:`

然后在子组件中需要接收参数

通过 VueComponent.props 属性来接收

```js
//方式一：简单声明接收
props:['name','age','sex'] 

//方式二：接收的同时对数据进行类型限制
props:{
	name:String,
	age:Number,
	sex:String
}

//方式三：接收的同时对数据：进行类型限制+默认值的指定+必要性的限制
props:{
	name:{
		type:String, //name的类型是字符串
		required:true, //name是必要的
	},
	age:{
		type:Number,
		default:99 //默认值
	},
	sex:{
		type:String,
		required:true
	}
}
```

示例：

```js
export default {
	name:'Student',
	data() {
		console.log(this)
		return {
			msg:'我是一个尚硅谷的学生',
			myAge:this.age
		}
	},
	methods: {
		updateAge(){
			this.myAge++
		}
	},
	//简单声明接收
	// props:['name','age','sex'] 
	//接收的同时对数据进行类型限制
	/* props:{
		name:String,
		age:Number,
		sex:String
	} */
	//接收的同时对数据：进行类型限制+默认值的指定+必要性的限制
	props:{
		name:{
			type:String, //name的类型是字符串
			required:true, //name是必要的
		},
		age:{
			type:Number,
			default:99 //默认值
		},
		sex:{
			type:String,
			required:true
		}
	}
}
```

> 备注：props是只读的，Vue底层会监测你对props的修改，如果进行了修改，就会发出警告，若业务需求确实需要修改，那么请复制props的内容到data中一份，然后去修改data中的数据。
> 方法也可以作为参数传递

## 子->父传参

### 回调函数实现

可以借助上述特性来实现，将父组件中的函数传递给子组件调用，实现传参

示例

```vue
<template>
  <div id="app">
    <!-- 将函数作为参数传给子组件 -->
    <MyTest :getSubDate="getSubDate" />
  </div>
</template>

<script>
import MyTest from "@/components/MyTest";
export default {
  name: 'App',
  components: {MyTest},
  methods:{
    getSubDate(date){
      console.log("获取到子组件的数据@"+date)
    }
  }
}
</script>
```

```vue
<template>
  <div class="todo-header">
    <input type="button" @click="sendDate" value="发送数据"/>
  </div>
</template>

<script>
export default {
  name: 'MyTest',
  props: {
    getSubDate: {
      require: true,
      type: Function
    }
  },
  data() {
    return {}
  },
  methods: {
    sendDate() {
      this.getSubDate("123");
    }
  }
}
</script>
```

### 自定义事件实现

示例

```vue
<template>
  <div id="app">
    <MyTest @getSubDate="getSubDate" />
  </div>
</template>

<script>
import MyTest from "@/components/MyTest";
export default {
  name: 'App',
  components: {MyTest},
  methods:{
    getSubDate(date){
      console.log("获取到子组件的数据@"+date)
    }
  }
}
</script>
```

```vue
<template>
  <div class="todo-header">
    <input type="button" @click="sendDate" value="发送数据"/>
  </div>
</template>

<script>
export default {
  name: 'MyTest',
  data() {
    return {}
  },
  methods: {
    sendDate() {
      this.$emit('getSubDate','123')
    }
  }
}
</script>
```

## 兄弟组件通信

参考全局事件总线

# 消息订阅与发布（pubsub）

`pubsub-js`是一个基于发布/订阅模式的轻量级JavaScript库，用于在应用程序内部实现组件之间的解耦通信。通过`pubsub-js`，您可以在不直接引用或了解其他组件的情况下，实现组件之间的消息传递和事件触发。这种模式使得组件之间的通信更加灵活和简单。

以下是`pubsub-js`的一些主要特点和用法：

1. **发布/订阅模式**：`pubsub-js`基于发布/订阅模式，其中一个组件可以发布（publish）一个事件，而其他组件可以订阅（subscribe）这个事件并接收通知。
2. **全局事件总线**：`pubsub-js`实际上创建了一个全局的事件总线，所有组件都可以通过这个事件总线进行事件的发布和订阅。
3. **解耦性**：使用`pubsub-js`可以帮助您实现组件之间的解耦，因为组件无需直接引用或调用其他组件，只需要通过事件名称进行通信。
4. **灵活性**：`pubsub-js`允许您定义任意的事件名称，并且支持传递数据给订阅者，使得通信更加灵活和定制化。
5. **轻量级**：`pubsub-js`是一个非常轻量级的库，易于集成到任何JavaScript应用程序中，并且不会给应用程序增加过多的负担。

下面是一个简单的示例，演示了如何在Vue.js应用程序中使用`pubsub-js`进行组件通信：

安装pubsub：`npm i pubsub-js`

```javascript
// 安装 pubsub-js
// npm install pubsub-js
// 在组件A中发布事件
import PubSub from 'pubsub-js';
export default {
  methods: {
    sendMessage() {
      PubSub.publish('message-from-component-a', 'Hello from Component A!');
    }
  }
}
```

```javascript
// 在组件B中订阅事件
import PubSub from 'pubsub-js';
export default {
  data() {
    return {
      message: ''
    };
  },
  created() {
    PubSub.subscribe('message-from-component-a', (msg, data) => {
      this.message = data;
    });
  }
}
```

通过`pubsub-js`，组件A可以发布一个名为`message-from-component-a`的事件，并传递数据，而组件B可以订阅这个事件，并接收到数据后更新页面显示。这样就实现了组件之间的解耦通信。

最好在 beforeDestroy 钩子中，用 `PubSub.unsubscribe(pid)` 去<span style="color:red">取消订阅。</span>

# 局部样式 scoped

在 vue 中，style 标签中的样式信息最终都会汇总到一起，即都是全局样式，在大型项目中，可能会存在多个开发者同时开发不同的组件，如果不使用 scoped 样式，不同组件中的样式可能会出现命名冲突，导致样式混乱和不可预测的结果。通过 scoped 样式，可以避免这种命名冲突，确保每个组件的样式都能正确应用。

在 Vue.js 中，可以使用 scoped 样式来实现组件作用域的样式。通过 scoped 样式，我们可以确保组件中的样式只对当前组件生效，不会影响到其他组件，从而实现样式的隔离和封装。
以下是关于 scoped 样式的一些重要信息：
1. **定义 scoped 样式**：
   在 Vue 单文件组件（.vue 文件）中，可以使用 `<style scoped>` 标签来定义 scoped 样式。在 scoped 样式中编写的样式规则只会作用于当前组件的元素，不会影响到其他组件。
2. **实现原理**：
   Vue 在处理 scoped 样式时，会自动生成一个唯一的属性（通常是 `data-v-xxxxxxx`），然后将这个属性添加到样式规则的选择器中，以实现样式的作用域限定。
3. **样式隔离**：
   使用 scoped 样式可以避免全局样式的污染，确保组件的样式只对当前组件生效。这样可以提高样式的可维护性和复用性，减少样式冲突的可能性。
4. **注意事项**：
   - scoped 样式只对当前组件的直接子组件生效，不会影响嵌套组件的样式。
   - 在使用 scoped 样式时，需要注意样式的选择器，确保样式规则的选择器与组件中的元素匹配，以确保样式能够正确应用。
5. **使用 scoped 样式**：
   在编写 Vue 单文件组件时，可以通过以下方式定义 scoped 样式：
   ```html
   <style scoped>
   /* scoped 样式规则 */
   </style>
   ```
   通过使用 scoped 样式，我们可以更好地管理组件的样式，避免样式冲突和污染，提高代码的可维护性和可读性。scoped 样式是 Vue 中非常实用的特性之一，推荐在开发 Vue 项目时充分利用它。

**使用示例**

```vue
<template>
	<div class="demo">
		<h2 class="title">学生姓名：{{name}}</h2>
		<h2 class="atguigu">学生性别：{{sex}}</h2>
	</div>
</template>

<script>
	export default {
		name:'Student',
		data() {
			return {
				name:'张三',
				sex:'男'
			}
		}
	}
</script>

<style lang="less" scoped>
	.demo{
		background-color: pink;
		.atguigu{
			font-size: 40px;
		}
	}
</style>
```

# 混入 mixin

在日常开发中，会有很多组件，组件中经常会有重复的代码，混入可以将重复的代码单独提取出去，实现复用

示例：

```vue
<template>
	<div>
		<h2 @click="showName">学校名称：{{name}}</h2>
		<h2>学校地址：{{address}}</h2>
	</div>
</template>

<script>
	//引入一个hunhe
	import {hunhe,hunhe2} from '../mixin'

	export default {
		name:'School',
		data() {
			return {
				name:'尚硅谷',
				address:'北京',
				x:666
			}
		},
		mixins:[hunhe,hunhe2],
	}
</script>
```

```vue
<template>
	<div>
		<h2 @click="showName">学生姓名：{{name}}</h2>
		<h2>学生性别：{{sex}}</h2>
	</div>
</template>

<script>
	import {hunhe,hunhe2} from '../mixin'

	export default {
		name:'Student',
		data() {
			return {
				name:'张三',
				sex:'男'
			}
		},
		mixins:[hunhe,hunhe2]
	}
</script>
```

mixin.js

```js
export const hunhe = {
	methods: {
		showName(){
			alert(this.name)
		}
	},
	mounted() {
		console.log('你好啊！')
	},
}
export const hunhe2 = {
	data() {
		return {
			x:100,
			y:200
		}
	},
}
```

在 mixin 中的配置片段与组件中冲突时，优先使用组件内定义的，**除了生命周期函数之外，生命周期函数会全部触发**

## 全局配置

main.js

```js
//引入Vue
import Vue from 'vue'
//引入App
import App from './App.vue'
// 引入混合
import {hunhe,hunhe2} from './mixin'
//关闭Vue的生产提示
Vue.config.productionTip = false
//全局应用混合
Vue.mixin(hunhe)
Vue.mixin(hunhe2)


//创建vm
new Vue({
	el:'#app',
	render: h => h(App)
})
```

这样，所有的VC和VM全部拥有了混合的属性

# 插件

1. 功能：用于增强Vue
2. 本质：包含install方法的一个对象，install的第一个参数是Vue，第二个以后的参数是插件使用者传递的数据。

一个最基本的插件

```js
export default {
	install(){
	}
}
```

一个插件必须包含一个 install 方法

使用插件

```js
//引入插件
import plugins from './plugins'
//使用插件
Vue.use(plugins)
```

在 Vue 插件的 `install` 方法中，通常会接收两个参数，这两个参数分别是：
1. **Vue**：这个参数是 Vue 的构造函数，也可以理解为 Vue 实例。在插件中，我们可以使用这个参数来扩展 Vue 实例，注册全局组件、指令、过滤器，添加实例方法等。
2. **options**：这个参数是一个可选的选项对象，用于传递插件的配置选项。通过这个参数，我们可以在安装插件时传递一些配置信息，以便插件根据配置进行相应的初始化操作。
在编写 Vue 插件时，通常会按照以下方式来定义 `install` 方法及其参数：
```javascript
// 定义一个 Vue 插件
const MyPlugin = {
  install(Vue, options) {
    // 在 install 方法中可以使用 Vue 来扩展 Vue 实例
    // 可以注册全局组件、指令、过滤器，添加实例方法等
    // options 参数用于传递插件的配置选项
  }
}
// 使用 Vue.use() 方法安装插件
Vue.use(MyPlugin, { /* 配置选项 */ });
```

一个示例

```js
export default {
	install(Vue,x,y,z){
		console.log(x,y,z)
		//全局过滤器
		Vue.filter('mySlice',function(value){
			return value.slice(0,4)
		})

		//定义全局指令
		Vue.directive('fbind',{
			//指令与元素成功绑定时（一上来）
			bind(element,binding){
				element.value = binding.value
			},
			//指令所在元素被插入页面时
			inserted(element,binding){
				element.focus()
			},
			//指令所在的模板被重新解析时
			update(element,binding){
				element.value = binding.value
			}
		})

		//定义混入
		Vue.mixin({
			data() {
				return {
					x:100,
					y:200
				}
			},
		})

		//给Vue原型上添加一个方法（vm和vc就都能用了）
		Vue.prototype.hello = ()=>{alert('你好啊')}
	}
}
```

使用插件

```js
import plugins from './plugins'
//应用（使用）插件
Vue.use(plugins,1,2,3)
```

# 异步更新 $nextTick

1. 语法：```this.$nextTick(回调函数)```
2. 作用：在下一次 DOM 更新结束后执行其指定的回调。
3. 什么时候用：当改变数据后，要基于更新后的新DOM进行某些操作时，要在nextTick所指定的回调函数中执行。

`$nextTick`是Vue.js提供的一个异步更新DOM的方法，它可以让您在下次DOM更新循环结束之后执行特定的操作。在Vue.js中，DOM更新是异步执行的，当数据发生变化时，Vue会将DOM更新放入队列中，然后在下一个事件循环中批量更新DOM，这样可以提高性能和效率。

下面是关于`$nextTick`的详细介绍：

1. 作用
- **等待DOM更新完成**：使用`$nextTick`可以确保您在Vue实例数据发生变化后，等待Vue完成DOM更新之后再执行特定的操作，确保操作在DOM更新完成后执行。
- **处理DOM相关操作**：适合用于需要操作DOM元素的情况，比如获取DOM元素的尺寸、位置等信息，或者在更新后操作DOM元素。

2. 使用方法

```javascript
this.$nextTick(function () {
  // 在DOM更新完成后执行的操作
});
```

3. 示例

```vue
<template>
  <div>
    <p>当前计数：{{ count }}</p>
    <button @click="incrementCount">增加计数</button>
  </div>
</template>
<script>
export default {
  data() {
    return {
      count: 0
    };
  },
  methods: {
    incrementCount() {
      this.count++;
      this.$nextTick(() => {
        // DOM更新完成后操作
        console.log('DOM已更新，当前计数为：', this.count);
      });
    }
  }
}
</script>
```

4. 注意事项
- **异步更新**：`$nextTick`是异步执行的，因此操作会在下一个DOM更新循环中执行。
- **避免频繁使用**：尽量避免在大量数据变化时频繁使用`$nextTick`，以免影响性能。

5. 应用场景
- **操作DOM元素**：获取更新后的DOM元素尺寸、位置等信息。
- **Vue生命周期钩子中**：在Vue生命周期钩子中使用`$nextTick`，确保在DOM更新完成后执行操作。

# Vue封装的过度与动画

1. 作用：在插入、更新或移除 DOM元素时，在合适的时候给元素添加样式类名。

2. 写法：

   1. 准备好样式：

      - 元素进入的样式：
        1. v-enter：进入的起点
        2. v-enter-active：进入过程中
        3. v-enter-to：进入的终点
      - 元素离开的样式：
        1. v-leave：离开的起点
        2. v-leave-active：离开过程中
        3. v-leave-to：离开的终点

   2. 使用```<transition>```包裹要过度的元素，并配置name属性：

      ```vue
      <transition name="hello">
      	<h1 v-show="isShow">你好啊！</h1>
      </transition>
      ```

   3. 备注：若有多个元素需要过度，则需要使用：```<transition-group>```，且每个元素都要指定```key```值。

# 请求发送

## axios

[Axios中文文档 | Axios中文网 (axios-http.cn)](https://www.axios-http.cn/)

在Vue.js项目中，通常会使用Axios来进行HTTP请求，Axios是一个基于Promise的现代化HTTP库，可以在浏览器和Node.js中使用。下面是关于Vue中Axios的简单介绍和使用方法：

### 安装 Axios
可以使用npm或者yarn来安装Axios：
```bash
npm install axios
```
或
```bash
yarn add axios
```
### 引入 Axios
在Vue项目中，可以在`main.js`或者需要使用Axios的组件中引入Axios：
```javascript
import axios from 'axios';
```
### 发送 GET 请求
```javascript
axios.get('https://api.example.com/data')
  .then(response => {
    console.log(response.data);
  })
  .catch(error => {
    console.error(error);
  });
```
### 发送 POST 请求
```javascript
axios.post('https://api.example.com/data', {
    key: 'value'
  })
  .then(response => {
    console.log(response.data);
  })
  .catch(error => {
    console.error(error);
  });
```
### 在 Vue 组件中使用 Axios
```vue
<template>
  <div>
    <ul>
      <li v-for="item in items" :key="item.id">{{ item.name }}</li>
    </ul>
  </div>
</template>
<script>
import axios from 'axios';
export default {
  data() {
    return {
      items: []
    };
  },
  mounted() {
    axios.get('https://api.example.com/items')
      .then(response => {
        this.items = response.data;
      })
      .catch(error => {
        console.error(error);
      });
  }
}
</script>
```
### 设置全局默认配置
你也可以在Vue项目中设置全局默认配置，比如设置基本的URL、请求头等：
```javascript
axios.defaults.baseURL = 'https://api.example.com';
axios.defaults.headers.common['Authorization'] = 'Bearer token';
```
### 拦截器
Axios还提供了拦截器（interceptors）功能，可以在请求或响应被处理前拦截它们：
```javascript
axios.interceptors.request.use(config => {
  // 在请求发送之前做些什么
  return config;
}, error => {
  return Promise.reject(error);
});
axios.interceptors.response.use(response => {
  // 对响应数据做些什么
  return response;
}, error => {
  return Promise.reject(error);
});
```

## vue-resource

Vue.js官方推荐使用`axios`来处理HTTP请求，而不推荐使用`vue-resource`。`vue-resource`是Vue.js官方推出的一个插件，用于处理HTTP请求，但自Vue 2.0版本起，Vue.js官方不再继续更新和维护`vue-resource`，而是推荐使用更加强大和流行的`axios`库来处理HTTP请求。因此，建议在Vue项目中使用`axios`来代替`vue-resource`。

### Vue-resource简介

`vue-resource`是Vue.js官方推出的一个处理HTTP请求的**插件**，它基于Promise对象实现，可以在Vue实例中方便地进行HTTP请求。`vue-resource`提供了一些简单易用的API，例如`this.$http.get`、`this.$http.post`等，用于发送GET、POST等类型的请求。

### 主要特点
- **基于Promise**：`vue-resource`使用Promise对象处理异步请求，支持链式调用，便于处理异步操作。
- **拦截器**：可以使用拦截器对请求和响应进行处理，方便添加全局的请求拦截器和响应拦截器。
- **全局配置**：可以全局配置请求的默认参数，如请求头、超时时间等。
- **RESTful风格**：`vue-resource`支持RESTful风格的API，方便进行CRUD操作。

### 安装和使用
1. 安装`vue-resource`：
```bash
npm install vue-resource --save
```
2. 在Vue项目中使用`vue-resource`：
```javascript
// main.js
import Vue from 'vue';
import VueResource from 'vue-resource';
Vue.use(VueResource);
// 发送一个GET请求
Vue.http.get('https://api.example.com/data')
  .then(response => {
    // 处理请求成功的响应
  })
  .catch(error => {
    // 处理请求失败的情况
  });
```
### 总结

尽管`vue-resource`是一个方便的HTTP请求插件，但由于官方不再维护和更新，推荐使用`axios`或其他现代的HTTP库来处理HTTP请求。`axios`是一个功能强大且流行的HTTP客户端库，具有更多的功能和更好的支持。

## xhr

在Vue中使用XMLHttpRequest（XHR）发送请求并不是Vue.js推荐的方式，通常推荐使用现代的HTTP客户端库，比如`axios`、`fetch`等。
### 使用XMLHttpRequest发送请求的基本步骤：
1. 创建一个XMLHttpRequest对象：
```javascript
var xhr = new XMLHttpRequest();
```
2. 设置请求的方法、URL和是否异步：
```javascript
xhr.open('GET', 'https://api.example.com/data', true); // 第三个参数true表示异步请求
```
3. 监听请求状态变化：
```javascript
xhr.onreadystatechange = function() {
  if (xhr.readyState === 4 && xhr.status === 200) {
    // 请求成功
    var responseData = xhr.responseText;
    // 处理响应数据
  } else {
    // 请求失败或正在处理中
  }
};
```
4. 发送请求：
```javascript
xhr.send();
```
### 示例代码：
```javascript
var xhr = new XMLHttpRequest();
xhr.open('GET', 'https://api.example.com/data', true);
xhr.onreadystatechange = function() {
  if (xhr.readyState === 4 && xhr.status === 200) {
    var responseData = xhr.responseText;
    // 处理响应数据
  } else {
    // 处理请求失败或其他情况
  }
};
xhr.send();
```
### 注意事项：
- 使用XHR发送请求相对原始和底层，需要手动处理请求、响应和错误处理逻辑。
- XHR在处理复杂请求时可能会变得复杂，而现代的HTTP客户端库（如`axios`）提供了更简洁和易用的API。
- Vue.js推荐使用`axios`等现代HTTP客户端库来处理HTTP请求，这些库提供了更多功能和更好的支持。
总的来说，尽管可以使用XHR发送请求，但在实际开发中，推荐使用现代的HTTP客户端库来简化HTTP请求的处理。

## fetch

在Vue中使用`fetch`发送请求是一种现代的方法，它是基于Promise的API，用于替代传统的XMLHttpRequest（XHR）。`fetch`提供了一种更简洁和现代化的方式来处理HTTP请求。下面是在Vue中如何使用`fetch`发送请求的详细说明：
### 使用Fetch发送请求的基本步骤：
1. 发起GET请求：
```javascript
fetch('https://api.example.com/data')
  .then(response => {
    return response.json(); // 将响应数据转换为JSON格式
  })
  .then(data => {
    // 处理获取到的数据
  })
  .catch(error => {
    // 处理请求错误
  });
```
2. 发起POST请求：
```javascript
fetch('https://api.example.com/data', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify(data) // 将数据转换为JSON格式
})
  .then(response => {
    return response.json();
  })
  .then(data => {
    // 处理获取到的数据
  })
  .catch(error => {
    // 处理请求错误
  });
```
### 示例代码：
```javascript
fetch('https://api.example.com/data')
  .then(response => {
    return response.json();
  })
  .then(data => {
    // 处理获取到的数据
  })
  .catch(error => {
    // 处理请求错误
  });
```
### 注意事项：
- `fetch`返回一个Promise对象，可以使用`.then()`和`.catch()`来处理请求的成功和失败情况。
- 默认情况下，`fetch`不会发送或接收任何cookies，如果需要发送cookies，可以添加`credentials: 'include'`选项。
- `fetch`不会自动发送或接收JSON数据，需要手动处理数据格式转换，比如使用`response.json()`方法。
- 在处理请求头、请求参数等方面，`fetch`相对于传统的XHR更为简洁和现代。
总的来说，`fetch`是一个现代化的方式来处理HTTP请求，相对于传统的XHR更简洁和易用。在Vue项目中，你可以使用`fetch`来发送HTTP请求，并根据返回的Promise对象处理响应数据。

## JQuery

# vue 脚手架配置代理

当前端项目（Vue项目）通过Ajax请求访问不同域名的接口时，由于浏览器的同源策略限制，可能会导致跨域问题，即浏览器拒绝发送跨域请求。但服务器与服务器之间的请求一般不存在跨域问题，所以可以搭建一台与vue在同一域名下的代理服务器进行代理数据请求，从而避免跨域问题。

## 方法一

​	在vue.config.js中添加如下配置：

```js
devServer:{
  proxy:"http://localhost:5000"
}
```

说明：

1. 优点：配置简单，请求资源时直接发给前端（8080）即可。
2. 缺点：不能配置多个代理，不能灵活的控制请求是否走代理。
3. 工作方式：若按照上述配置代理，当请求了前端不存在的资源时，那么该请求会转发给服务器 （优先匹配前端资源）

## 方法二

​	编写vue.config.js配置具体代理规则：

```js
module.exports = {
	devServer: {
      proxy: {
      '/api1': {// 匹配所有以 '/api1'开头的请求路径
        target: 'http://localhost:5000',// 代理目标的基础路径
        changeOrigin: true,
        pathRewrite: {'^/api1': ''}
      },
      '/api2': {// 匹配所有以 '/api2'开头的请求路径
        target: 'http://localhost:5001',// 代理目标的基础路径
        changeOrigin: true,
        pathRewrite: {'^/api2': ''}
      }
    }
  }
}
/*
   changeOrigin设置为true时，服务器收到的请求头中的host为：localhost:5000
   changeOrigin设置为false时，服务器收到的请求头中的host为：localhost:8080
   changeOrigin默认值为true
*/
```

说明：

1. 优点：可以配置多个代理，且可以灵活的控制请求是否走代理。
2. 缺点：配置略微繁琐，请求资源时必须加前缀。

# 插槽

在 vue 中，组件标签除了自闭合 `<Category/>` 的形式外，还可以写成块级标签形式 `<Category></Category>` ，其中块级标签形式中可以编写正常的 html 编码，在子组件中使用`<slot></slot>`作为占位符，在解析时，会用`<Category></Category>`中的html代码块替换子组件中的`<slot></slot>`占位符，这就是插槽

1. 作用：让父组件可以向子组件指定位置插入html结构，也是一种组件间通信的方式，适用于 <strong style="color:red">父组件 ===> 子组件</strong> 。

2. 分类：默认插槽、具名插槽、作用域插槽

3. 使用方式：

   1. 默认插槽：

      ```vue
      父组件中( Category 为子组件)：
              <Category>
                 <div>html结构1</div>
              </Category>
      子组件中：
              <template>
                  <div>
                     <!-- 定义插槽 -->
                     <slot>插槽默认内容...</slot>
                  </div>
              </template>
      ```

   2. 具名插槽：

      ```vue
      父组件中：
              <Category>
                  <template slot="center">
                    <div>html结构1</div>
                  </template>
      
                  <template v-slot:footer>
                     <div>html结构2</div>
                  </template>
              </Category>
      子组件中：
              <template>
                  <div>
                     <!-- 定义插槽 -->
                     <slot name="center">插槽默认内容...</slot>
                     <slot name="footer">插槽默认内容...</slot>
                  </div>
              </template>
      ```

   3. 作用域插槽：

      1. 理解：<span style="color:red">数据在组件的自身，但根据数据生成的结构需要组件的使用者来决定。</span>（games数据在Category组件中，但使用数据所遍历出来的结构由App组件决定）

      2. 具体编码：

         ```vue
         父组件中：
         		<Category>
         			<template scope="scopeData">
         				<!-- 生成的是ul列表 -->
         				<ul>
         					<li v-for="g in scopeData.games" :key="g">{{g}}</li>
         				</ul>
         			</template>
         		</Category>
         
         		<Category>
         			<template slot-scope="scopeData">
         				<!-- 生成的是h4标题 -->
         				<h4 v-for="g in scopeData.games" :key="g">{{g}}</h4>
         			</template>
         		</Category>
         子组件中：
                 <template>
                     <div>
                         <slot :games="games"></slot>
                     </div>
                 </template>
         		
                 <script>
                     export default {
                         name:'Category',
                         props:['title'],
                         //数据在子组件自身
                         data() {
                             return {
                                 games:['红色警戒','穿越火线','劲舞团','超级玛丽']
                             }
                         },
                     }
                 </script>
         ```

# vuex

在Vue中实现集中式状态（数据）管理的一个**Vue插件**，对vue应用中多个组件的共享状态进行集中式的管理（读/写），也是一种组件间通信的方式，且适用于任意组件间通信。

当组件越来越多时，比如一份数据要共享给多个组件，并要实时监控数据的变化，此时使用全局事件总线传递数据过于复杂，vuex 专门解决该类问题

Vuex 是一个专门为 Vue.js 应用程序开发的状态管理模式库。它允许您在应用程序中集中管理所有组件的状态，并提供了一种可预测性的方式来管理状态的变化。以下是关于 Vuex 的详细介绍：

## 何时使用

1. 当多个组件需要共享数据
2. 不同组件行为需要变更同一状态

## 核心概念：

![vuex](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202403251708361.png)

1. **State（状态）**：一个Object对象，用于**存储**应用程序中的共享状态**数据**，类似于组件中的 data 属性。
2. **Getters（获取器）**：用于从 state 中派生出一些状态，类似于组件中的计算属性。
3. **Mutations（变更）**：用于修改 state 中的数据，但是只能进行同步操作。(真正修改数据的动作)
4. **Actions（动作）**：用于提交 mutations (指在编程中用于改变数据的操作)，可以包含异步操作。(可理解为真正修改数据之前要做的事情，例如修改数据的值需要从其他服务异步获取，获取的步骤就应在该步骤进行)
5. **Modules（模块）**：允许将 store 分割成模块，每个模块拥有自己的 state、getters、mutations 和 actions。
6. **Store (存储容器)**：图中并未体现，是 Actions，Mutations和State的管理者


## 安装 Vuex：

版本对应关系

vue2==>Vuex3

vue3==>Vuex4

可以使用 npm 或 yarn 安装 Vuex：

```bash
npm install vuex@3
```

## 基本用法：

1. **引入并使用 Vuex**： 当引入后在 `new Vue` 时即可传入一个 `store` 配置项。
2. **创建 Store 配置**：创建一个 Vuex store 并指定 state、getters、mutations 和 actions。
3. **在 Vue 应用中使用**：在 Vue 应用的根实例中引入 store，并通过 `store` 选项注入到所有子组件中。

## 示例代码：

```javascript
// store.js
import Vue from 'vue';
import Vuex from 'vuex';
// use vuex 必须在 new Vuex.Store 之前执行
Vue.use(Vuex);
const store = new Vuex.Store({
  //准备state对象——保存具体的数据
  state: {
    count: 0
  },
  //准备mutations对象——修改state中的数据
  mutations: {
    increment(state) {
      state.count++;
    }
  },
  //准备actions对象——响应组件中用户的动作
  actions: {
    incrementAsync({ commit }) {
      setTimeout(() => {
        commit('increment');
      }, 1000);
    }
  },
  getters: {
    doubleCount(state) {
      return state.count * 2;
    }
  }
});
export default store;
```

```javascript
// main.js
import Vue from 'vue';
import App from './App.vue';
import store from './store';
new Vue({
  store,
  render: h => h(App)
}).$mount('#app');
```

```vue
<!-- App.vue -->
<template>
  <div>
    <p>{{ $store.state.count }}</p>
    <p>{{ $store.getters.doubleCount }}</p>
    <button @click="increment">Increment</button>
  </div>
</template>
<script>
export default {
  methods: {
    increment() {
      this.$store.commit('increment');
    }
  }
};
</script>
```

## Vuex 的优点：

- **集中式存储**：所有的状态都存储在一个单一的对象中，方便管理和维护。
- **状态管理**：可预测的状态管理，便于调试和追踪状态变化。
- **组件通信**：方便组件之间共享状态和通信。
- **插件化**：可以方便地使用插件来拓展功能。


## 搭建vuex环境

1. 创建文件：```src/store/index.js```

   ```js
   //引入Vue核心库
   import Vue from 'vue'
   //引入Vuex
   import Vuex from 'vuex'
   //应用Vuex插件
   Vue.use(Vuex)
   
   //准备actions对象——响应组件中用户的动作
   const actions = {}
   //准备mutations对象——修改state中的数据
   const mutations = {}
   //准备state对象——保存具体的数据
   const state = {}
   
   //创建并暴露store
   export default new Vuex.Store({
   	actions,
   	mutations,
   	state
   })
   ```

2. 在```main.js```中创建vm时传入```store```配置项

   ```js
   ......
   //引入store
   import store from './store'
   ......
   
   //创建vm
   new Vue({
   	el:'#app',
   	render: h => h(App),
   	store
   })
   ```

![image-20240326101526367](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202403261015868.png)

## 基本使用

1. 初始化数据、配置```actions```、配置```mutations```，操作文件```store.js```

   ```js
   //引入Vue核心库
   import Vue from 'vue'
   //引入Vuex
   import Vuex from 'vuex'
   //引用Vuex
   Vue.use(Vuex)
   
   const actions = {
       //响应组件中加的动作
   	jia(context,value){
   		// console.log('actions中的jia被调用了',miniStore,value)
   		context.commit('JIA',value)
   	},
   }
   
   const mutations = {
       //执行加
   	JIA(state,value){
   		// console.log('mutations中的JIA被调用了',state,value)
   		state.sum += value
   	}
   }
   
   //初始化数据
   const state = {
      sum:0
   }
   
   //创建并暴露store
   export default new Vuex.Store({
   	actions,
   	mutations,
   	state,
   })
   ```

2. 组件中读取vuex中的数据：```$store.state.sum```

3. 组件中修改vuex中的数据：```$store.dispatch('action中的方法名',数据)``` 或 ```$store.commit('mutations中的方法名',数据)```

   >  备注：若没有网络请求或其他业务逻辑，组件中也可以越过actions，即不写```dispatch```，直接编写```commit```

$store.dispatch 调用 actions 中的方法，这一动作类似调用 service 方法，业务逻辑写在 actions 这一步，在 actions 中也可以调用 dispatch 继续调用 actions 的其他方法
$store.commit 调用 mutations 中的方法，这一动作类似调用 dao 方法，数据更新写在 mutations 这一步

## getters的使用

1. 概念：当state中的数据需要经过加工后再使用时，可以使用getters加工。

2. 在```store.js```中追加```getters```配置

   ```js
   ......
   
   const getters = {
   	bigSum(state){
   		return state.sum * 10
   	}
   }
   
   //创建并暴露store
   export default new Vuex.Store({
   	......
   	getters
   })
   ```

3. 组件中读取数据：```$store.getters.bigSum```

## 四个map方法的使用

在 Vuex 中，`mapState`、`mapGetters`、`mapActions` 和 `mapMutations` 是用于简化 Vuex 状态管理的辅助函数，可以帮助我们在组件中更方便地访问和操作 Vuex 中的状态、getters、actions 和 mutations。

下面详细介绍它们的作用和使用方法：

### 1. `mapState`

- **作用**：将 store 中的状态映射到组件的计算属性中，使得组件能够直接访问这些状态，而无需通过 `this.$store.state.xxx` 的方式。即可直接访问 `xxx` 属性

- **使用方法**：

```javascript
import { mapState } from 'vuex';
computed: {
  ...mapState({
    // 将 store 中的 count 状态映射为组件的 count 计算属性
    count: state => state.count
  })
}
```

或

```js
computed: {
    //借助mapState生成计算属性：sum、school、subject（对象写法）
     ...mapState({sum:'sum',school:'school',subject:'subject'}),
         
    //借助mapState生成计算属性：sum、school、subject（数组写法）
    ...mapState(['sum','school','subject']),
},
```

使用示例

```vue
{{sum}}
```

### 2. `mapGetters`

- **作用**：将 store 中的 getters 映射到组件的计算属性中，使得组件能够直接访问这些 getters，而无需通过 `this.$store.getters.xxx` 的方式。

- **使用方法**：

```javascript
import { mapGetters } from 'vuex';
computed: {
  ...mapGetters({
    // 将 store 中的 doubleCount getter 映射为组件的 doubleCount 计算属性
    doubleCount: 'doubleCount'
  })
}
```

或

```js
computed: {
    //借助mapGetters生成计算属性：bigSum（对象写法）
    ...mapGetters({bigSum:'bigSum'}),

    //借助mapGetters生成计算属性：bigSum（数组写法）
    ...mapGetters(['bigSum'])
},
```

### 3. `mapActions`

- **作用**：将 store 中的 actions 映射到组件的 methods 中，使得组件能够直接调用这些 actions，而无需通过 `this.$store.dispatch('xxx')` 的方式。

- **使用方法**：

```javascript
import { mapActions } from 'vuex';
methods: {
  ...mapActions({
    // 将 store 中的 increment action 映射为组件的 increment 方法
    increment: 'increment'
  })
}
```

或

```js
methods:{
    //靠mapActions生成：incrementOdd、incrementWait（对象形式）
    ...mapActions({incrementOdd:'jiaOdd',incrementWait:'jiaWait'})

    //靠mapActions生成：incrementOdd、incrementWait（数组形式）
    ...mapActions(['jiaOdd','jiaWait'])
}
```

### 4. `mapMutations`

- **作用**：将 store 中的 mutations 映射到组件的 methods 中，使得组件能够直接调用这些 mutations，而无需通过 `this.$store.commit('xxx')` 的方式。

- **使用方法**：
```javascript
import { mapMutations } from 'vuex';
methods: {
  ...mapMutations({
    // 将 store 中的 increment mutation 映射为组件的 increment 方法
    increment: 'increment'
  })
}
```

或

 ```js
methods:{
    //靠mapActions生成：increment、decrement（对象形式）
    ...mapMutations({increment:'JIA',decrement:'JIAN'}),
    
    //靠mapMutations生成：JIA、JIAN（对象形式）
    ...mapMutations(['JIA','JIAN']),
}
 ```

调用方法时直接调用即可

```js
this.JIA(1);
```

> 备注：mapActions与mapMutations使用时，若需要传递参数需要：在模板中绑定事件时传递好参数，否则参数是事件对象。

通过使用这些辅助函数，我们可以简化组件与 Vuex 之间的交互流程，减少重复代码的编写，提高代码的可读性和维护性。这些函数使得我们可以更加便捷地在组件中访问和操作 Vuex 中的状态、getters、actions 和 mutations。

## 模块化+命名空间

1. 目的：让代码更好维护，让多种数据分类更加明确。

2. 修改```store.js```

   ```javascript
   const countAbout = {
     namespaced:true,//开启命名空间
     state:{x:1},
     mutations: { ... },
     actions: { ... },
     getters: {
       bigSum(state){
          return state.sum * 10
       }
     }
   }
   
   const personAbout = {
     namespaced:true,//开启命名空间
     state:{ ... },
     mutations: { ... },
     actions: { ... }
   }
   
   const store = new Vuex.Store({
     modules: {
       countAbout,
       personAbout
     }
   })
   ```

3. 开启命名空间后，组件中读取state数据：

   ```js
   //方式一：自己直接读取
   this.$store.state.personAbout.list
   //方式二：借助mapState读取：
   ...mapState('countAbout',['sum','school','subject']),
   ```

4. 开启命名空间后，组件中读取getters数据：

   ```js
   //方式一：自己直接读取
   this.$store.getters['personAbout/firstPersonName']
   //方式二：借助mapGetters读取：
   ...mapGetters('countAbout',['bigSum'])
   ```

5. 开启命名空间后，组件中调用dispatch

   ```js
   //方式一：自己直接dispatch
   this.$store.dispatch('personAbout/addPersonWang',person)
   //方式二：借助mapActions：
   ...mapActions('countAbout',{incrementOdd:'jiaOdd',incrementWait:'jiaWait'})
   ```

6. 开启命名空间后，组件中调用commit

   ```js
   //方式一：自己直接commit
   this.$store.commit('personAbout/ADD_PERSON',person)
   //方式二：借助mapMutations：
   ...mapMutations('countAbout',{increment:'JIA',decrement:'JIAN'}),
   ```

# 路由

在 Vue.js 中，路由（Router）是用于管理不同页面之间跳转和导航的机制。具体来说，Vue Router 是 Vue.js 官方提供的路由管理器，用于实现单页面应用（SPA）中的路由控制。

1. 理解： 一个路由（route）就是一组映射关系（key - value），多个路由需要路由器（router）进行管理。
2. 前端路由：key是路径，value是组件。

## 基本使用

### 安装 Vue Router：

版本对应关系

vue2==>vue-router3
vue3==>vue-router4

```shell
npm install vue-router@3
```

### 创建路由实例

```js
import Vue from 'vue';
//引入VueRouter
import VueRouter from 'vue-router'
//引入Luyou 组件
import About from '../components/About'
import Home from '../components/Home'
//应用插件
Vue.use(VueRouter)
//创建router实例对象，去管理一组一组的路由规则
const router = new VueRouter({
	routes:[
		{
			path:'/about',
			component:About
		},
		{
			path:'/home',
			component:Home
		}
	]
})
//暴露router
export default router
```

### 在 Vue 实例中使用路由

```js
new Vue({
  router,
  render: h => h(App)
}).$mount('#app');
```

### 在组件中使用路由

原始 html 中我们使用 a 标签实现页面的跳转，Vue 中借助 `<router-link>` 标签实现路由的切换

在组件中使用 `<router-link>` 组件实现路由导航，使用 `<router-view>` 组件显示当前路由对应的组件。

实现切换（active-class可配置高亮样式）

```vue
<router-link active-class="active" to="/about">About</router-link>
```

指定展示位置

```vue
<router-view></router-view>
```

示例

```html
<div class="todo-header">
  <router-link to="/gre">MyTestGre</router-link>
  <router-link to="/red">MyTestRed</router-link>
  <router-view></router-view>
</div>
```

### 几个注意点

1. 路由组件通常存放在```pages```文件夹，一般组件通常存放在```components```文件夹。
2. 通过切换，“隐藏”了的路由组件，默认是被销毁掉的，需要的时候再去挂载。
3. 每个组件都有自己的```$route```属性，里面存储着自己的路由信息。
4. 整个应用只有一个router，可以通过组件的```$router```属性获取到。

## 多级路由

1. 配置路由规则，使用children配置项：

```js
routes:[
	{
		path:'/about',
		component:About,
	},
	{
		path:'/home',
		component:Home,
		children:[ //通过children配置子级路由
			{
				path:'news', //此处一定不要写：/news
				component:News
			},
			{
				path:'message',//此处一定不要写：/message
				component:Message
			}
		]
	}
]
```

2. 跳转（要写完整路径）：

```xml
<router-link to="/home/news">News</router-link>
```

## 路由的query参数

1. 传递参数

```vue
<!-- 跳转并携带query参数，to的字符串写法 -->
<router-link :to="/home/message/detail?id=666&title=你好">跳转</router-link>
				
<!-- 跳转并携带query参数，to的对象写法 -->
<router-link 
	:to="{
		path:'/home/message/detail',
		query:{
		   id:666,
            title:'你好'
		}
	}"
>跳转</router-link>
```

2. 路由组件中接收参数：

```xml
<div class="box">
    {{$route.query.id}}
    {{$route.query.title}}
</div>
```

## 命名路由

1. 作用：可以简化路由的跳转。

2. 如何使用

   1. 给路由命名：

      ```js
      {
      	path:'/demo',
      	component:Demo,
      	children:[
      		{
      			path:'test',
      			component:Test,
      			children:[
      				{
                        name:'hello' //给路由命名
      					path:'welcome',
      					component:Hello,
      				}
      			]
      		}
      	]
      }
      ```

   2. 简化跳转：

      ```vue
      <!--简化前，需要写完整的路径 -->
      <router-link to="/demo/test/welcome">跳转</router-link>
      
      <!--简化后，直接通过名字跳转 -->
      <router-link :to="{name:'hello'}">跳转</router-link>
      
      <!--简化写法配合传递参数 -->
      <router-link 
      	:to="{
      		name:'hello',
      		query:{
      		   id:666,
                  title:'你好'
      		}
      	}"
      >跳转</router-link>
      ```

## 路由的params参数

1. 配置路由，声明接收params参数

```js
{
	path:'/home',
	component:Home,
	children:[
		{
			path:'news',
			component:News
		},
		{
            path:'message'
			component:Message,
			children:[
				{
					name:'xiangqing',
					path:'detail/:id/:title', //使用占位符声明接收params参数
					component:Detail
				}
			]
		}
	]
}
```

2. 传递参数

```vue
<!-- 跳转并携带params参数，to的字符串写法 -->
<router-link :to="/home/message/detail/666/你好">跳转</router-link>
				
<!-- 跳转并携带params参数，to的对象写法 -->
<router-link 
	:to="{
		name:'xiangqing',
		params:{
		   id:666,
           title:'你好'
		}
	}"
>跳转</router-link>
```

> 特别注意：路由携带params参数时，若使用to的对象写法，则不能使用path配置项，必须使用name配置！

3. 接收参数：

```js
$route.params.id
$route.params.title
```

## 路由的props配置

作用：让路由组件更方便的收到参数

```js
{
	name:'xiangqing',
	path:'detail/:id',
	component:Detail,

	//第一种写法：props值为对象，该对象中所有的key-value的组合最终都会通过props传给Detail组件
	// props:{a:900}

	//第二种写法：props值为布尔值，布尔值为true，则把路由收到的所有params参数通过props传给Detail组件
	// props:true
	
	//第三种写法：props值为函数，该函数返回的对象中每一组key-value都会通过props传给Detail组件
	props(route){
		return {
			id:route.query.id,
			title:route.query.title
		}
	}
}
```

示例

在配置路由的时候，配置props

```js
import Vue from 'vue';
import VueRouter from 'vue-router';
import MyTestGre from "@/components/MyTestGre";
import MyTestRed from "@/components/MyTestRed";
Vue.use(VueRouter);
export default new VueRouter({
    routes: [
        {
            name:'testName',
            path: '/gre',
            component: MyTestGre,
            props(route) {
                return {
                    id:route.query.id,
                    title:route.query.title
                }
            }
        },
        {
            path: '/red',
            component: MyTestRed
        }
    ]
})
```

接收参数使用 props 接收

```vue
<template>
  <div class="box">
    {{id}}
    {{title}}
  </div>
</template>

<script>
export default {
  name: 'MyTestGre',
  props:{
    id:{
      type:String
    },title:{
      type:String
    }
  },
  data() {
    return {}
  },
  methods: {
  }
}
</script>

<style scoped>
.box{
  width: 20px;
  height: 20px;
  background-color: green;
}
</style>
```

## <router-link> 的replace属性

1. 作用：控制路由跳转时操作浏览器历史记录的模式
2. 浏览器的历史记录有两种写入方式：分别为```push```和```replace```，```push```是追加历史记录，```replace```是替换当前记录。路由跳转时候默认为```push```
3. 如何开启```replace```模式：```<router-link replace .......>News</router-link>```

## 编程式路由导航

作用：不借助```<router-link> ```实现路由跳转，让路由跳转更加灵活，例如使用button

示例编码

```js
//$router的两个API
this.$router.push({
	name:'xiangqing',
		params:{
			id:xxx,
			title:xxx
		}
})

this.$router.replace({
	name:'xiangqing',
		params:{
			id:xxx,
			title:xxx
		}
})
this.$router.forward() //前进
this.$router.back() //后退
this.$router.go() //可前进也可后退
```

示例：

```vue
<template>
  <div class="todo-header">
    <input type="button" @click="gre" value="gre"/>
    <input type="button" @click="red" value="red"/>
    <router-view></router-view>
  </div>
</template>

<script>
export default {
  components:{
  },
  name: 'MyTest',
  data() {
    return {}
  },
  methods: {
    gre() {
      this.$router.push({
          path:'/gre',
          params:{
            id:666,
            title:'test'
          }
      })
    },
    red() {
      this.$router.push({
        path:'/red'
      })
    }
  }
}
</script>
```

## 缓存路由组件

作用：让不展示的路由组件保持挂载，不被销毁。用于有输入数据的情况，切走的时候在切回来数据丢失

具体编码：

```vue
<keep-alive include="可选的，指定要缓存的组件名称，不写默认缓存所有"> 
    <router-view></router-view>
</keep-alive>
```

数组形式

```vue
<keep-alive :include="['组件1','组件2']"> 
    <router-view></router-view>
</keep-alive>
```

## 两个新的生命周期钩子

1. 作用：**路由组件所独有的两个钩子**，用于捕获路由组件的激活状态。可用于在组件失活时释放资源
2. 具体名字：
   1. ```activated```路由组件被激活时触发。
   2. ```deactivated```路由组件失活时触发。

## 路由守卫

作用：对路由进行权限控制

分类：全局守卫、独享守卫、组件内守卫

在 Vue Router 中，`beforeRouteEnter` 是组件内的路由守卫之一，用于在进入当前组件之前执行一些逻辑。`beforeRouteEnter` 钩子函数有三个参数，分别是 `to`、`from` 和 `next`，下面对这三个参数进行详细说明：
1. **to**：
   `to` 参数表示即将进入的目标路由对象，包含了目标路由的路由信息，例如路径、参数、元信息等。`to` 参数是一个 Route 对象，具有以下常用属性：
   - `to.path`：目标路由的路径
   - `to.params`：目标路由的参数
   - `to.query`：目标路由的查询参数
   - `to.meta`：目标路由的元信息
2. **from**：
   `from` 参数表示当前导航正要离开的路由对象，包含了当前路由的信息。`from` 参数也是一个 Route 对象，具有类似的属性：
   - `from.path`：当前路由的路径
   - `from.params`：当前路由的参数
   - `from.query`：当前路由的查询参数
   - `from.meta`：当前路由的元信息
3. **next**：
   `next` 参数是一个函数，用于进入下一个钩子。在 `beforeRouteEnter` 钩子函数中，必须调用 `next` 函数来确保组件成功加载。`next` 函数有以下用法：
   - `next()`：继续导航，进入当前组件
   - `next(false)`：中断当前导航
   - `next('/')`：跳转到一个不同的路径
   - `next(error)`：传递一个错误给 `router.onError` 处理
通过使用这三个参数，您可以在 `beforeRouteEnter` 钩子函数中实现各种逻辑，例如根据路由信息进行数据加载、权限验证等操作。确保在 `beforeRouteEnter` 钩子函数中调用 `next` 函数，以便正确地控制路由导航。

### 全局守卫

示例：

在创建路由时添加方法

```js
import Vue from 'vue';
import VueRouter from 'vue-router';
import MyTestGre from "@/components/MyTestGre";
import MyTestRed from "@/components/MyTestRed";

Vue.use(VueRouter);
const router = new VueRouter({
    routes: [
        {
            name: 'testName',
            path: '/gre',
            component: MyTestGre,
            props(route) {
                return {
                    id: route.query.id,
                    title: route.query.title
                }
            }
        },
        {
            path: '/red',
            component: MyTestRed
        }
    ]
})

router.beforeEach()
router.afterEach()

export default router;
```

具体的方法

```js
//全局前置守卫：初始化时执行、每次路由切换前执行
router.beforeEach((to,from,next)=>{
	console.log('beforeEach',to,from)
	if(to.meta.isAuth){ //判断当前路由是否需要进行权限控制
		if(localStorage.getItem('school') === 'atguigu'){ //权限控制的具体规则
			next() //放行
		}else{
			alert('暂无权限查看')
			// next({name:'guanyu'})
		}
	}else{
		next() //放行
	}
})

//全局后置守卫：初始化时执行、每次路由切换后执行
router.afterEach((to,from)=>{
	console.log('afterEach',to,from)
	if(to.meta.title){ 
		document.title = to.meta.title //修改网页的title
	}else{
		document.title = 'vue_test'
	}
})
```

### 独享守卫

**独享守卫只有前置没有后置**

```js
import Vue from 'vue';
import VueRouter from 'vue-router';
import MyTestGre from "@/components/MyTestGre";
import MyTestRed from "@/components/MyTestRed";

Vue.use(VueRouter);
const router = new VueRouter({
    routes: [
        {
            name: 'testName',
            path: '/gre',
            component: MyTestGre,
            props(route) {
                return {
                    id: route.query.id,
                    title: route.query.title
                }
            }
        },
        {
            path: '/red',
            component: MyTestRed,
            beforeEnter(){}
        }
    ]
})

export default router;

```

```js
beforeEnter(to,from,next){
	console.log('beforeEnter',to,from)
	if(to.meta.isAuth){ //判断当前路由是否需要进行权限控制
		if(localStorage.getItem('school') === 'atguigu'){
			next()
		}else{
			alert('暂无权限查看')
			// next({name:'guanyu'})
		}
	}else{
		next()
	}
}
```

### 组件内守卫

```vue
<template>
  <div class="box">
    {{id}}
    {{title}}
    <input type="text">
  </div>
</template>

<script>
export default {
  name: 'MyTestGre',
  props:{
    id:{
      type:String
    },title:{
      type:String
    }
  },
  data() {
    return {}
  },
  methods: {
  },
  beforeRouteEnter(){
     // 组件内守卫
  }
}
</script>

<style scoped>
.box{
  width: 20px;
  height: 20px;
  background-color: green;
}
</style>
```

进入守卫：通过路由规则，**进入该组件时**被调用
离开守卫：通过路由规则，**离开该组件时**被调用

```js
//进入守卫：通过路由规则，进入该组件时被调用
beforeRouteEnter (to, from, next) {
},
//离开守卫：通过路由规则，离开该组件时被调用
beforeRouteLeave (to, from, next) {
}
```

## meta

在配置路由的时候，可以将自定义属性放在meta里，用于后续判断使用

```js
import Vue from 'vue';
import VueRouter from 'vue-router';
import MyTestGre from "@/components/MyTestGre";
import MyTestRed from "@/components/MyTestRed";

Vue.use(VueRouter);
const router = new VueRouter({
    routes: [
        {
            name: 'testName',
            path: '/gre',
            component: MyTestGre,
            props(route) {
                return {
                    id: route.query.id,
                    title: route.query.title
                }
            }
        },
        {
            path: '/red',
            component: MyTestRed,
            meta:{
                isAuth:true
            }
        }
    ]
})

export default router;
```

组件中获取meta，比如meta.isAuth = true 时才进行权限校验

```vue
<template>
  <div class="box">
    {{id}}
    {{title}}
    <input type="text">
  </div>
</template>

<script>
export default {
  name: 'MyTestGre',
  props:{
    id:{
      type:String
    },title:{
      type:String
    }
  },
  data() {
    return {}
  },
  methods: {
  },
  beforeRouteEnter(to, from, next){
    console.log(to.meta)
    console.log(from.meta)
    console.log(next)
  }
}
</script>

<style scoped>
.box{
  width: 20px;
  height: 20px;
  background-color: green;
}
</style>
```


## 路由器的两种工作模式

1. 对于一个url来说，什么是hash值？———— '#'及其后面的内容就是hash值。
2. hash值不会包含在 HTTP 请求中，即：hash值不会带给服务器。
3. hash模式：
   1. 地址中永远带着#号，不美观 。
   2. 若以后将地址通过第三方手机app分享，若app校验严格，则地址会被标记为不合法。
   3. 兼容性较好。
4. history模式：
   1. 地址干净，美观 。
   2. 兼容性和hash模式相比略差。
   3. 应用部署上线时需要后端人员支持，解决刷新页面服务端404的问题。

```js
const router = new VueRouter({
    mode:'history',
    routes: [
        
    ]
})
```

# 项目打包

npm run build

dist 文件夹

# vue 常用组件库

移动端

1. vant
2. cube UI
3. mint UI

PC端

1. element UI
2. IView UI