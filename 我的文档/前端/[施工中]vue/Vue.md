# 渐进式框架

"渐进式框架"是指一种框架设计理念，它允许开发者在使用框架时逐步采用框架提供的各种功能，而不是强制性地要求开发者一开始就全部采用框架的所有特性。
具体来说，渐进式框架通常具有以下特点：
1. **模块化和可选性：** 渐进式框架通常将功能划分为独立的模块，开发者可以根据自己的需求选择性地采用这些模块。这意味着开发者可以根据项目的实际需求，逐步引入框架的各种功能，而不必一开始就全部引入。
2. **低入门门槛：** 渐进式框架通常提供了简单易懂的基础功能，使得新手开发者可以快速上手并构建基本的应用。随着项目的发展，开发者可以逐步引入更复杂的功能和特性。
3. **灵活性和可定制性：** 渐进式框架通常具有一定程度的灵活性和可定制性，开发者可以根据自己的需求定制框架的行为，或者选择替代方案来满足特定的需求。
Vue.js自称为渐进式框架，因为它允许开发者逐步采用Vue.js提供的各种功能，从简单的数据绑定和模板语法开始，逐渐引入组件化、路由、状态管理等高级特性，使得开发者可以根据项目的实际需求选择性地采用Vue.js的不同功能，而不必被迫一开始就使用框架的全部功能。这种设计理念使得Vue.js适用于各种规模的项目，并且对于新手开发者来说也具有较低的学习曲线。

# 单页面应用

单页面应用（Single Page Application，SPA）是一种Web应用程序的架构模式，它在加载初始页面后，不会因为用户的操作而重新加载整个页面，而是通过动态更新页面的方式来实现页面内容的变化。下面是单页面应用的优缺点：
优点：
1. 用户体验好：SPA在用户与页面交互时，无需频繁地刷新页面，可以提供更流畅的用户体验，减少页面闪烁和加载时间。
2. 前后端分离：SPA可以实现前后端分离，前端负责页面渲染和交互逻辑，后端负责数据接口和业务逻辑，降低了系统的耦合度。
3. 减少服务器负担：由于SPA只需加载一次页面，之后的页面内容变化通过异步请求数据来实现，可以减轻服务器的负担，提高服务器性能。
4. 跨平台性：SPA可以更好地支持移动设备，因为它可以通过API与后端通信，动态更新页面内容，适应不同的屏幕尺寸和设备类型。
缺点：
1. 首次加载时间长：由于SPA需要一次性加载所有相关的JavaScript和CSS文件，因此首次加载时间可能会比传统多页面应用长。
2. SEO(搜索引擎优化)难度较大：搜索引擎对于SPA的抓取和索引能力有限，相对于传统多页面应用，SPA的SEO(搜索引擎优化)优化难度较大。
3. 安全性考虑：由于SPA通常会使用大量的JavaScript代码来实现页面逻辑，因此需要特别注意安全性问题，避免受到XSS等攻击。
4. 前进后退按钮问题：SPA在浏览器的历史记录中只有一个入口，因此在处理浏览器的前进后退按钮时需要特别注意，以保证用户体验。
综合来看，单页面应用适合对用户体验要求较高、需要与后端分离、注重跨平台性的应用场景，但也需要注意其在首次加载时间、SEO(搜索引擎优化)优化、安全性和浏览器历史记录等方面的局限性。

# 虚拟 DOM

虚拟DOM（Virtual DOM）是前端框架中常见的一种性能优化技术，它的原理可以简单概括如下：
1. **创建虚拟DOM：** 当页面的状态发生变化时，框架会创建一颗虚拟DOM树，这颗树是对真实DOM的抽象表示，它包含了整个页面的结构和内容。
2. **对比差异：** 在有新的状态变化时，框架会比较新旧两颗虚拟DOM树之间的差异，找出需要进行更新的部分。
3. **更新最小化：** 框架会根据差异找出需要更新的部分，并只更新这些部分到真实的DOM中，而不是重新渲染整个页面。
虚拟DOM的原理主要基于以下两个假设：
- **DOM操作昂贵：** 直接对真实的DOM进行操作（比如增删改查）是比较昂贵的，会引起页面的重排和重绘，影响性能。
- **内存操作便宜：** 对虚拟DOM进行操作是在内存中进行的，成本较低，而且可以批量处理，不会立即影响到页面的表现。
通过使用虚拟DOM，框架可以在内存中构建出一颗虚拟的DOM树，然后在状态变化时，通过比较新旧两颗虚拟DOM树之间的差异，只更新需要更新的部分到真实的DOM中，从而减少了对真实DOM的操作次数，提高了页面的渲染性能。
需要注意的是，并非所有的前端框架都使用虚拟DOM，但虚拟DOM的概念和原理对于理解前端性能优化和框架设计仍然具有重要意义。

假设有一个简单的列表页面，包含一个列表和一个按钮，点击按钮时会向列表中添加一个新的项。使用虚拟DOM进行更新最小化的过程如下：
1. **初始渲染：** 首次渲染页面时，框架会创建虚拟DOM树来表示页面的结构，然后将虚拟DOM渲染到真实的DOM中。
2. **状态变化：** 当用户点击按钮时，页面的状态发生变化，需要向列表中添加一个新的项。
3. **创建虚拟DOM：** 框架会根据新的状态变化，创建一个新的虚拟DOM树，表示更新后的页面结构。
4. **对比差异：** 框架会比较新旧两颗虚拟DOM树之间的差异，找出需要进行更新的部分，即新添加的列表项。
5. **更新最小化：** 框架只会更新新添加的列表项到真实的DOM中，而不会重新渲染整个列表。这样就减少了对真实DOM的操作次数，提高了页面的渲染性能。
通过更新最小化，框架可以在状态变化时，只更新需要更新的部分到真实的DOM中，而不是重新渲染整个页面，从而提高了页面的渲染性能。

# 文档

## vue2.0

[教程](https://v2.cn.vuejs.org/v2/guide/) 教学

[API](https://v2.cn.vuejs.org/v2/api/) API 速查字典

[风格指南 — Vue.js (vuejs.org)](https://v2.cn.vuejs.org/v2/style-guide/) 编码风格推荐

[Markdown 编辑器 — Vue.js (vuejs.org)](https://v2.cn.vuejs.org/v2/examples/) 官方示例

[介绍 — Vue.js (vuejs.org)](https://v2.cn.vuejs.org/v2/cookbook/) 高级特性

[vuejs/awesome-vue: 🎉 A curated list of awesome things related to Vue.js (github.com)](https://github.com/vuejs/awesome-vue)  &  [Awesome Vue packages - Awesome JS](https://awesomejs.dev/for/vue/)   第三方库 （jar）

# 安装

[安装 — Vue.js (vuejs.org)](https://v2.cn.vuejs.org/v2/guide/installation.html)

# Vue.config 全局配置

[API — Vue.js (vuejs.org)](https://v2.cn.vuejs.org/v2/api/#全局配置)

## 应用

main.js

```js
Vue.config.productionTip=false;
Vue.config.devtools=true;
Vue.config.debug=true;
```

# vue

## vue 实例

[Vue 实例 — Vue.js (vuejs.org)](https://v2.cn.vuejs.org/v2/guide/instance.html)

```html
<!-- 
	初识Vue：
		1.想让Vue工作，就必须创建一个Vue实例，且要传入一个配置对象；
		2.root容器里的代码依然符合html规范，只不过混入了一些特殊的Vue语法；
		3.root容器里的代码被称为【Vue模板】；
		4.Vue实例和容器是一一对应的；
		5.真实开发中只有一个Vue实例，并且会配合着组件一起使用；
		6.{{xxx}}中的xxx要写js表达式，且xxx可以自动读取到data中的所有属性；
		7.一旦data中的数据发生改变，那么页面中用到该数据的地方也会自动更新；
		注意区分：js表达式 和 js代码(语句)
				1.表达式：一个表达式会产生一个值，可以放在任何一个需要值的地方：
							(1). a
							(2). a+b (js 表达式)
							(3). demo(1) (函数调用表达式)
							(4). x === y ? 'a' : 'b' (三元表达式)
				2.js代码(语句)
							(1). if(){}
							(2). for(){}
-->
<!-- 准备好一个容器 -->
<div id="demo">
	<h1>Hello，{{name.toUpperCase()}}，{{address}}</h1>
</div>
<script type="text/javascript" >
	Vue.config.productionTip = false //阻止 vue 在启动时生成生产提示。
	//创建Vue实例
	new Vue({
		el:'#demo', //el用于指定当前Vue实例为哪个容器服务，值通常为css选择器字符串。也可写为 document.getElementById("demo")
		data:{ //data中用于存储数据，数据供el所指定的容器去使用，值我们暂时先写成一个对象。
			name:'atguigu',
			address:'北京'
		}
	})
</script>
```

### data 与 el 的 2 种写法

在vue对象中，所有以`$`符号开头的属性，包括其原型上的`$`符号开头的属性，都是给程序员使用的

例如 data与el的2种写法

data与el的2种写法
	1.el有2种写法
		(1).new Vue时候配置el属性。
		(2).先创建Vue实例，随后再通过vm.$mount('#root')指定el的值。
	2.data有2种写法
		(1).对象式
		(2).函数式
		如何选择：**目前哪种写法都可以，以后学习到组件时，data必须使用函数式，否则会报错。**
	3.一个重要的原则：
		**由Vue管理的函数，一定不要写箭头函数，一旦写了箭头函数，this就不再是Vue实例了**。

```html
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8" />
		<title>el与data的两种写法</title>
		<!-- 引入Vue -->
		<script type="text/javascript" src="../js/vue.js"></script>
	</head>
	<body>
		<!-- 准备好一个容器-->
		<div id="root">
			<h1>你好，{{name}}</h1>
		</div>
	</body>

	<script type="text/javascript">
		Vue.config.productionTip = false //阻止 vue 在启动时生成生产提示。

		//el的两种写法
		/* const v = new Vue({
			//el:'#root', //第一种写法
			data:{
				name:'尚硅谷'
			}
		})
		console.log(v)
		v.$mount('#root') //第二种写法 */

		//data的两种写法
		new Vue({
			el:'#root',
			//data的第一种写法：对象式
			/* data:{
				name:'尚硅谷'
			} */

			//data的第二种写法：函数式
			data(){
				console.log('@@@',this) //此处的this是Vue实例对象
				return{
					name:'尚硅谷'
				}
			}
		})
	</script>
</html>
```

### $mount 挂载

在Vue.js中，`$mount` 是Vue实例的一个方法，它用于手动挂载（或者说手动渲染）Vue实例到一个DOM元素上。通常情况下，Vue实例会在创建时自动挂载到指定的DOM元素上，但有时候我们也需要手动控制Vue实例的挂载过程，这时就可以使用 `$mount` 方法。
以下是 `$mount` 方法的一些主要用法和特点：
1. **手动挂载：**
   ```javascript
   var vm = new Vue({
     data: {
       message: 'Hello, Vue!'
     }
   });
   vm.$mount('#app');
   ```
   在这个例子中，我们首先创建了一个Vue实例 `vm`，然后使用 `$mount` 方法手动将该实例挂载到id为 `app` 的DOM元素上。这样就实现了手动挂载Vue实例到指定的DOM元素上。
2. **延迟挂载：**
   ```javascript
   var vm = new Vue({
     data: {
       message: 'Hello, Vue!'
     }
   });
   vm.$mount(); // 手动挂载
   document.getElementById('app').appendChild(vm.$el); // 将挂载后的DOM元素手动添加到页面上
   ```
   在这个例子中，我们先创建了Vue实例 `vm`，然后使用 `$mount` 方法手动进行挂载，但没有指定挂载的目标元素，然后通过 `vm.$el` 获取到挂载后的DOM元素，最后手动将该DOM元素添加到页面上。
3. **动态挂载：**
   ```javascript
   var vm = new Vue({
     data: {
       message: 'Hello, Vue!'
     }
   });
   vm.$mount('#app'); // 初始挂载
   // ...一些操作后，需要重新挂载到新的DOM元素上
   vm.$mount('#new-app'); // 动态重新挂载到新的DOM元素上
   ```
   在这个例子中，我们首先将Vue实例 `vm` 初始挂载到id为 `app` 的DOM元素上，然后在一些操作后，需要将该实例重新挂载到id为 `new-app` 的DOM元素上，这时可以再次使用 `$mount` 方法来实现动态重新挂载。
总的来说，`$mount` 方法是Vue实例的一个重要方法，它提供了手动控制Vue实例挂载过程的能力，可以实现手动挂载、延迟挂载、动态挂载等操作，对于一些特定的场景和需求非常有用。

### MVVM

MVVM模型
		1. M：模型(Model) ：data中的数据
		2. V：视图(View) ：模板代码
		3. VM：视图模型(ViewModel)：Vue实例
	观察发现：
		1.data中所有的属性，最后都出现在了vm身上。
		2.vm身上所有的属性 及 Vue原型上所有属性，在Vue模板中都可以直接使用。
        
例如

```html
<h1>测试一下2：{{$options}}</h1>
```
        
## 模板语法

root容器里的代码被称为【Vue模板】；

Vue模板语法有2大类：
    1.插值语法：
            功能：用于解析标签体内容。
            写法：{{xxx}}，xxx是js表达式，且可以直接读取到data中的所有属性。
    2.指令语法：
            功能：用于解析标签（包括：标签属性、标签体内容、绑定事件.....）。
            举例：v-bind:href="xxx" 或  简写为 :href="xxx"，xxx同样要写js表达式，
                    且可以直接读取到data中的所有属性。
            备注：Vue中有很多的指令，且形式都是：v-????，此处我们只是拿v-bind举个例子。

1. **插值语法：**
   - 插值语法使用双大括号 `{{ }}` 来插入变量的值或表达式的结果。
   - 插值语法通常用于将数据动态地显示在页面上，例如显示变量的值、计算表达式的结果等。
   - 插值语法不仅可以用在文本内容中，还可以用在元素的属性中，但只能用于HTML标签中的文本内容和普通属性，不能用于特殊属性（如`class`、`style`等）。
2. **指令语法：**
   - 指令语法使用Vue.js提供的指令来操作DOM元素和绑定数据。
   - 指令语法以 `v-` 开头，例如`v-bind`、`v-if`、`v-for`等。
   - 指令语法用于实现更复杂的DOM操作和数据绑定，例如动态绑定属性、条件渲染、列表渲染等。

### 插值语法

Vue.js中的插值表达式使用双大括号 `{{ }}` 进行标识，用于在模板中插入数据或表达式的结果。以下是一些示例来说明Vue.js的插值表达式的格式和内容：
1. **插入数据：**
```html
   <div>
     <p>{{ message }}</p>
   </div>
```
   在上面的示例中，`{{ message }}` 是一个插值表达式，它会在页面上显示`message`变量的值。
2. **表达式：**
```html
   <div>
     <p>{{ number + 1 }}</p>
   </div>
```
   在这个示例中，`{{ number + 1 }}` 是一个插值表达式，它会在页面上显示`number`变量的值加1的结果。
3. **调用函数：**
```html
   <div>
     <p>{{ formatName(name) }}</p>
   </div>
```
   在这个示例中，`{{ formatName(name) }}` 是一个插值表达式，它会在页面上显示调用`formatName`函数并传入`name`参数后的结果。
4. **三元表达式：**
```html
   <div>
     <p>{{ isReady ? 'Ready' : 'Not ready' }}</p>
   </div>
```
   在这个示例中，`{{ isReady ? 'Ready' : 'Not ready' }}` 是一个插值表达式，它会根据`isReady`变量的值显示不同的内容。

### 指令语法

在文本中可以使用插值表达式来将数据动态绑定到页面中，但在操作DOM元素及其属性时就需要用到指令语法

#### v-bind 单向数据绑定

在Vue.js中，`v-bind` 是一个指令，它的作用是动态地将一个或多个属性绑定到表达式或变量上。通过`v-bind`指令，我们可以实现动态地绑定HTML元素的属性，例如`href`、`class`、`style`等，从而根据数据的变化来动态地改变元素的属性值。
以下是`v-bind`的一些常见用法：
1. **绑定HTML属性：**
```html
   <a v-bind:href="url">Visit Vue.js Website</a>
```
   在这个例子中，`v-bind:href`指令将`href`属性绑定到`url`变量上，当`url`的值发生变化时，链接的地址也会相应地改变。
2. **动态CSS类名：**
```html
   <div v-bind:class="{ active: isActive, 'text-danger': hasError }"></div>
```
   在这个例子中，`v-bind:class`指令根据`isActive`和`hasError`变量的值动态地添加或移除CSS类名，实现动态的样式变化。
3. **绑定内联样式：**
```html
   <div v-bind:style="{ color: textColor, fontSize: fontSize + 'px' }">Dynamic Style</div>
```
   在这个例子中，`v-bind:style`指令将元素的内联样式绑定到`textColor`和`fontSize`变量上，实现动态的样式设置。
4. **绑定属性值：**
```html
   <input v-bind:value="inputValue">
```
   在这个例子中，`v-bind:value`指令将`input`元素的值绑定到`inputValue`变量上，使得输入框的值可以根据`inputValue`的变化而动态更新。
总之，`v-bind`指令是Vue.js中用于动态绑定属性的重要指令，通过它可以实现在模板中根据数据的变化动态地改变元素的属性值，使得页面能够实现更丰富的交互和动态效果。

`v-bind:` 可简写为 `:`

```html
   <input :value="inputValue">
```

#### v-model 双向数据绑定

Vue中有2种数据绑定的方式：
	1.单向绑定(v-bind)：数据只能从data流向页面。
	2.双向绑定(v-model)：数据不仅能从data流向页面，还可以从页面流向data。
		备注：
			1.双向绑定一般都应用在表单类元素上（如：input、select等）
			2.v-model:value 可以简写为 v-model，因为v-model默认收集的就是value值。

```html
<input v-model:value="message" placeholder="Enter message"> 可简写为
<input v-model="message" placeholder="Enter message">
```


在Vue.js中，`v-model` 是一个指令，它主要用于实现表单输入元素和应用状态之间的双向数据绑定。通过`v-model`指令，我们可以轻松地实现表单输入元素（如input、textarea、select等）和Vue实例中数据的双向绑定，使得数据的变化能够自动反映在表单元素上，同时用户在表单元素上的输入也能够自动更新到数据中。
以下是`v-model`的一些常见用法：
1. **输入框的双向绑定：**
   ```html
   <input v-model="message" placeholder="Enter message">
   <p>{{ message }}</p>
   ```
   在这个例子中，`v-model="message"` 将输入框的值与Vue实例中的 `message` 数据进行双向绑定，当用户在输入框中输入内容时，`message`的值也会相应地更新，同时`message`的变化也会自动反映在输入框中。
2. **复选框的双向绑定：**
   ```html
   <input type="checkbox" v-model="checked">
   <p>{{ checked }}</p>
   ```
   在这个例子中，`v-model="checked"` 将复选框的状态与Vue实例中的 `checked` 数据进行双向绑定，当用户勾选或取消勾选复选框时，`checked`的值会相应地更新，同时`checked`的变化也会自动反映在复选框中。
3. **下拉框的双向绑定：**
   ```html
   <select v-model="selected">
     <option value="option1">Option 1</option>
     <option value="option2">Option 2</option>
   </select>
   <p>{{ selected }}</p>
   ```
   在这个例子中，`v-model="selected"` 将下拉框的选项值与Vue实例中的 `selected` 数据进行双向绑定，当用户选择不同的选项时，`selected`的值会相应地更新，同时`selected`的变化也会自动反映在下拉框中。

如下代码是错误的，因为v-model只能应用在表单类元素（输入类元素）上

```html
<h2 v-model:x="name">你好啊</h2>
```

# 小技巧

## 浏览器强制刷新

shift+刷新

