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
<body>
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
<body>
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
<body>
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
<body>
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
<body>
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



### q










































# 小技巧

## 浏览器强制刷新

shift+刷新

