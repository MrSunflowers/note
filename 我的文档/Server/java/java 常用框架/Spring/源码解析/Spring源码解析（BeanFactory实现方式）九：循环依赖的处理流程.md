# Spring源码解析（BeanFactory实现方式）九：循环依赖的处理流程

&emsp;&emsp;接上文，回到 doCreateBean 中。

## 1 记录早期的 bean

&emsp;&emsp;现在的 bean 处于刚刚实例化，调用了构造方法，里面需要注入的属性还并没有初始化的状态，回到之前提到过的循环依赖问题，Spring 将其处理分为了三种情况，分别为**构造器循环依赖**、**prototype 作用域的 bean 的循环依赖**和 **Setter 循环依赖**，其中**构造器循环依赖**和 **prototype 作用域的 bean 的循环依赖**是**无法解决**的(多例某些情况可以注入成功，比如A为单例，B为多例)。

