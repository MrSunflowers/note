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

可以借助上述特性来实现

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






























## 兄弟组件通信

### 方式一：状态提升

```
```



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
