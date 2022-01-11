# Spring源码笔记十：AOP 的使用

&emsp;&emsp;AOP（Aspect Orient Programming），直译过来就是面向切面编程。AOP 是一种编程思想，是面向对象编程（OOP）的一种补充。面向对象编程将程序抽象成各个层次的对象，而面向切面编程是将程序抽象成各个切面。

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/spring/202112201009153.png)

&emsp;&emsp;从该图可以很形象地看出，所谓切面，相当于应用对象间的横切点，可以将其单独抽象为单独的模块。

**AOP的目的：**

&emsp;&emsp;AOP能够将那些与业务无关，却为业务模块所共同调用的逻辑或责任（例如事务处理、日志管理、权限控制等）封装起来，便于减少系统的重复代码，降低模块间的耦合度，并有利于未来的可拓展性和可维护性。

**AOP的优势：**

&emsp;&emsp;降低模块的耦合度、使系统容易扩展、更好的代码复用性。

【示例】

实际的代码

```java
public class testServiceImpl implements testService {
	public void save(Object object) {
		try {
			//开启事务
			begin();
			//保存
			save(object);
			//提交
			commit();
		} catch (Exception e) {
			e.printStackTrace();
			//回滚
			rollBack();
		}

	}
}
```

&emsp;&emsp;假设现在有这样一个类，其中的 save 方法就是将数据保存至数据库，而在保存过程中的事务开启、事务提交与回滚操作实际上是在所有类似功能方法中都是重复的，那么，就可以将这些重复的操作抽象成一个方法，然后在需要这些操作的地方分别调用这些方法，又或者某天不需要它了，就只需要删除抽象出去的方法，方便后续维护。

期望的代码

```java
public class testServiceImpl implements testService  {
	public void save(Object object) {
		//保存
		save(object);
	}
}
```

**AOP的实现：**

&emsp;&emsp;AOP 要达到的效果是，保证开发者不修改源代码的前提下，去为系统中的业务组件添加某种通用功能。AOP 的本质是由 AOP 框架修改业务组件的多个方法的源代码，AOP 其实就是代理模式的典型应用，按照 AOP 框架修改源代码的时机，可以将其分为两类：

- 静态 AOP 实现， AOP 框架在编译阶段对程序源代码进行修改，生成了静态的 AOP 代理类（生成的 *.class 文件已经被改掉了，需要使用特定的编译器），比如 AspectJ。
- 动态 AOP 实现， AOP 框架在运行阶段对动态生成代理对象（在内存中以 JDK 动态代理，或 CGlib 动态地生成 AOP 代理类），如 Spring AOP。

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/spring/202112201031807.png)

## 1 静态代理和动态代理

&emsp;&emsp;顾名思义，代理，就是你委托别人帮你办事，所以代理模式也有人称作委托模式的。比如领导要做什么事，可以委托他的秘书去帮忙做，这时就可以把秘书看做领导的代理。客户端直接使用的都是代理对象，不知道真实对象是谁，此时代理对象可以在客户端和真实对象之间起到中介的作用。

&emsp;&emsp;同样的，代理模式也分为两种实现方式：

- 静态代理模式：在程序运行前就已经存在代理类的字节码文件，代理对象和真实对象的关系在运行前就确定了。
- 动态代理模式：动态代理类是在程序运行期间由 JVM 通过反射等机制动态的生成的，代理对象和真实对象的关系是在程序运行事情才确定的。

### 1.1 静态代理

&emsp;&emsp;静态代理，代理类和被代理的类实现同样的接口，代理类同时持有被代理类的引用，这样，当需要调用被代理类的方法时，可以通过调用代理类的方法来做到。还是使用上面提到的示例：

**TestService 接口**

```java
public interface TestService {
	void save();
}
```

**TestServiceImpl 实现**

```java
public class TestServiceImpl implements TestService {
	@Override
	public void save() {
		System.out.println("保存数据到数据库");
	}
}
```

**TestServiceStaticProxy 静态代理类**

```java
public class TestServiceStaticProxy implements TestService {

	@Autowired
	private TestService testServiceImpl;

	@Override
	public void save() {
		try {
			System.out.println("开启事务");
			this.testServiceImpl.save();
			System.out.println("事务提交");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("事务回滚");
		}
	}
}
```

**测试**

```java
public static void main(String[] args) {
	ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("myTestResources/applicationContext.xml");
	TestService testService = (TestService) context.getBean("testServiceStaticProxy");
	testService.save();
}
```

```xml
<context:annotation-config/>
<context:component-scan base-package="org.springframework.myTest"/>
<bean name="testServiceImpl" class="org.springframework.myTest.aop.demo.TestServiceImpl" />
<bean name="testServiceStaticProxy" class="org.springframework.myTest.aop.demo.TestServiceStaticProxy" />
```

**优点：**

- 业务类只需要关注业务逻辑本身，保证了业务类的重用性，把真实对象隐藏起来了，保护真实对象。

**缺点：**

- 代理对象的某个接口只服务于某一种类型的对象，也就是说每一个真实对象都得创建一个代理对象。
- 如果需要代理的方法很多，则要为每一种方法都进行代理。 
- 如果接口增加一个方法，除了所有实现类需要实现这个方法外，所有代理类也需要实现此方法，增加了代码维护的复杂度。

### 1.2 动态代理

&emsp;&emsp;动态代理类是在程序运行期间由 JVM 通过反射等机制动态的生成，动态代理根据实现方式的不同又可以分为 JDK 动态代理和 CGlib 动态代理。

- JDK 动态代理：利用反射机制生成一个实现代理接口的类，在调用具体方法前调用 InvokeHandler 来处理。
- CGlib 动态代理：利用 ASM（开源的 Java 字节码编辑库，操作字节码）开源包，将代理对象类的 class 文件加载进来，通过修改其字节码生成子类来处理。

&emsp;&emsp;区别：JDK 代理只能对**实现接口**的类生成代理，CGlib 是针对类实现代理，对指定的类生成一个子类，并覆盖其中的方法，这种通过继承类的实现方式，不能代理 final 修饰的类。

#### 1.2.1 JDK 动态代理

&emsp;&emsp;JDK 动态代理，它提供了一组静态方法来为一组接口动态地生成代理类及其对象，返回动态生成的代理对象。

&emsp;&emsp;新建 MyInvocationHandler 实现 InvocationHandler 接口，负责集中处理动态代理类上的所有方法调用。

```java
public class MyInvocationHandler implements InvocationHandler {

	private Object object;

	public MyInvocationHandler(Object object) {
		this.object = object;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Object result = null;
		try {
			System.out.println("开启事务");
			result = method.invoke(object,args);
			System.out.println("事务提交");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("事务回滚");
		}
		return result;
	}
}
```

&emsp;&emsp;使用 Proxy 来创建代理类。

```java
public static void main(String[] args) {
	/**
	 *  loader		:类加载器
	 *  interfaces	:模拟的接口
	 *  hanlder		:代理执行处理器
	 */
	TestService testService = (TestService)Proxy.newProxyInstance(TestServiceImpl.class.getClassLoader(), new Class[]{TestService.class}, new MyInvocationHandler(new TestServiceImpl()));
	testService.save();
}
```

&emsp;&emsp;可以看出，通过 MyInvocationHandler 可以实现代理 TestServiceImpl 类，实际上实现的是任意类的任意方法的代理，这里只是因为传入的是 TestServiceImpl 类。

#### 1.2.2 CGlib 动态代理

&emsp;&emsp;CGlib 针对类进行代理，因此被代理的类不需要实现接口，使用方法与上面类似。首先新建一个类 MyMethodInterceptor 实现 MethodInterceptor 接口，这里需要注意的是实际调用是 methodProxy.invokeSuper 如果使用 invoke 方法，则需要传入被代理的类对象，否则出现死循环。

```java
public class MyMethodInterceptor implements MethodInterceptor {
	/**
	 * @param o 表示增强的对象
	 * @param method 需要增强的方法
	 * @param objects 要被拦截方法的参数
	 * @param methodProxy 表示要触发父类的方法对象
	 * @return java.lang.Object
	 */
	@Override
	public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
		Object result = null;
		try {
			System.out.println("开启事务");
			result = methodProxy.invokeSuper(o,objects);
			System.out.println("事务提交");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("事务回滚");
		}
		return result;
	}
}
```

&emsp;&emsp;TestServiceImpl 不再需要实现接口。

```java
public class TestServiceImpl{
	public void save() {
		System.out.println("保存数据到数据库");
	}
}
```

**测试**

```java
public static void main(String[] args) {
	Enhancer enhancer = new Enhancer();
	enhancer.setSuperclass(TestServiceImpl.class);
	enhancer.setCallback(new MyMethodInterceptor());
	TestServiceImpl testServiceImpl = (TestServiceImpl)enhancer.create();
	testServiceImpl.save();
}
```

## 2 AOP 相关概念

- **切面（Aspect）**: 切面是通知和切点的结合，由 Spring 的 Advisor 或拦截器实现。
- **连接点（Joinpoint）**: 连接点表示应用程序执行过程中能够插入切面的一个位置，这个位置可以是方法的调用、异常的抛出等，在 Spring AOP 中，连接点总是方法的调用。
- **通知（Advice）**: AOP 框架中具体的增强处理，通知描述了切面应该何时执行以及执行怎样的增强处理，也就是在方法执行的什么时候（方法前/后等）做什么操作（具体增强的功能）。
- **切点（PointCut）**: 可以插入增强处理的连接点，在哪些类，哪些方法上切入，AOP 框架必须允许开发者指定切入点。例如，使用正则表达式指定，Spring 中默认使用 AspectJ 切入点表达式语言。
- **引入（Introduction）**：引入允许向现有的类添加新的方法或者属性。例如，你可以使用一个引入使任何对象实现 IsModified 接口，来简化缓存。
- **目标对象（Target Object）**: 含有连接点的对象，也被称作被通知或被代理对象。
- **AOP代理（AOP Proxy）**: AOP 框架创建的对象，包含通知，在 Spring 中，AOP 代理可以是 JDK 动态代理或者 CGLIB 代理。
- **织入（Weaving）**: 将增强处理添加到目标对象中，并创建一个被增强的对象，这个过程就是织入，就是把切面加入到对象，并创建出代理对象的过程，这可以在编译时完成（例如使用 AspectJ 编译器），也可以在运行时完成。Spring 和其他纯 Java AOP 框架一样，在运行时完成织入，织入一般可以发生在如下几个时机：
&emsp;&emsp;- **编译时**：当一个类文件被编译时进行织入，这需要特殊的编译器才可以做的到，例如 AspectJ 的织入编译器。
&emsp;&emsp;- **类加载时**：使用特殊的 ClassLoader 在目标类被加载到程序之前增强类的字节码。
&emsp;&emsp;- **运行时**：切面在运行的某个时刻被织入, Spring AOP 就是以这种方式织入切面的。

&emsp;&emsp;AOP 框架有很多种，实现方式有可能有所不同， 主要分为两大类：一是采用动态代理技术（**典型代表为Spring AOP**），利用截取消息的方式（**典型代表为AspectJ-AOP**），对该消息进行装饰，以取代原有对象行为的执行；二是采用静态织入的方式，引入特定的语法创建 “方面” ，从而使得编译器可以在编译期间织入有关 “方面” 的代码。不同的 AOP 框架支持的连接点也有所区别，例如 AspectJ 和 JBoss，除了支持方法切点，它们还支持字段和构造器的连接点。而 Spring AOP 不能拦截对对象字段的修改，也不支持构造器连接点，无法在 Bean 创建时应用通知。

&emsp;&emsp;Spring AOP 与 ApectJ 的目的一致，都是为了统一处理横切业务，但与 AspectJ 不同的是，Spring AOP 并不尝试提供完整的 AOP 功能，Spring AOP 更注重的是与 Spring IOC 容器的结合，并结合该优势来解决横切业务的问题，因此在 AOP 的功能完善方面，AspectJ 具有更大的优势。

&emsp;&emsp;同时 Spring 注意到 AspectJ 在 AOP 的实现方式上依赖于特殊编译器( ajc 编译器)，Spring 回避了这点，转向采用动态代理技术来构建 Spring AOP 的内部机制（动态织入），这是与 AspectJ（静态织入）最根本的区别。

&emsp;&emsp;在 AspectJ 1.5 后，引入了 @Aspect 形式的注解方式的开发，Spring 也非常快地跟进了这种方式，因此 Spring 2.0 后便使用了与 AspectJ 一样风格的注解。请注意 **Spring 只是使用了与 AspectJ 5 一样风格的注解**，但仍然没有使用 AspectJ 的编译器，底层使用的依旧是动态代理技术，因此并不依赖于 AspectJ 的编译器。

## 3 Spring AOP 的使用

&emsp;&emsp;Spring 提供了 AOP 的实现，在低版本 Spring 中定义一个切面是比较麻烦的，需要实现特定的接口，并进行一些较为复杂的配置，低版本 Spring AOP 的配置是被批评最多的地方。Spring 听取这方面的批评声音，并下决心彻底改变这一现状。在 Spring2.0 中，Spring AOP 已经焕然一新，你可以使用 @AspectJ 注解非常容易的定义一个切面，不需要实现任何的接口。

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/spring/202201061046410.png)

&emsp;&emsp;Spring2.0 采用 @AspectJ 注解对 POJO 进行标注，从而定义一个包含切点信息和增强横切逻辑的切面，Spring 2.0 可以将这个切面织入到匹配的目标 Bean 中。@AspectJ 注解使用 AspectJ 切点表达式语法进行切点定义，可以通过切点函数、运算符、通配符等高级功能进行切点定义，拥有强大的连接点描述能力。

&emsp;&emsp;尽管 Spring 一再简化配置，并且大有使用注解取代 XML 之势，但无论如何 XML 还是 Spring 的基石。这里依旧使用前面的示例，需要在 TestServiceImpl 的 save 方法前后添加增强的功能。

### 3.1 AspectJ 切入点语法

&emsp;&emsp;首先就要解决一个问题，怎么表示在哪些方法上增强，一些先行者开发了一套语言来支持 AOP ———— AspectJ(语言)。

```
execution(modifiers-pattern? ret-type-pattern declaring-type-pattern?name-pattern(param-pattern) throws-pattern?)
翻译成中文：
execution(<访问修饰符>? <返回类型> <声明类型>? <方法名>(<参数>) <异常>?)
举例：
public static Class java.lang.Class.forName(String className)throws ClassNotFoundException 
[public static](访问修饰符) [Class](返回类型) [java.lang.Class](声明类型).[forName](方法名)[(String className)](参数)[throws ClassNotFoundException](异常)
```

&emsp;&emsp;标准的 AspectJ Aop 的 pointcut 的表达式类型是很丰富的，但是 Spring Aop 只支持其中的一部分，外加 Spring Aop 自己扩充的表达式，分别如下：

1. execution：一般用于指定方法的执行。
2. within：指定某些类型的全部方法，也可用来指定一个包。
3. this：Spring Aop 是基于动态代理的，生成的 bean 也是一个代理对象，this 就是这个代理对象，当这个对象可以转换为指定的类型时，对应切点生效。
4. target：当被代理的对象可以转换为指定的类型时，对应的切点生效。
5. args：当执行的方法的参数为指定类型时生效。
6. @target：对象**的运行时绑定的方法所属的类上**拥有指定的注解时生效。
7. @args：当执行的方法参数上拥有指定的注解时生效。
8. @within：对象**的运行时绑定的方法所属的类或其子类上**拥有指定的注解时生效。
9. @annotation：当执行的方法上拥有指定的注解时生效。
10. reference pointcut：表示引用其他切点，只有 @ApectJ 注解风格支持，xml 风格不支持。
11. bean(idOrNameOfBean)：当调用的方法是指定的 idOrNameOfBean 的方法时生效。(Spring AOP 自己扩展支持的)。

&emsp;&emsp;使用 execution 指示器选择切入点，当方法表达式以 * 号开始，表示不关心方法的返回值类型，对于方法参数列表，使用 .. 表示切点选择任意的方法，多个匹配之间可以使用连接符 &&、||、！来表示 “且”、“或”、“非” 的关系。但是在使用 XML 文件配置时，这些符号有特殊的含义，所以使用 “and”、“or”、“not” 来表示。

**几种常用的切入点表达式示例**

- 任意公共方法

```
execution(public * *(..))
```

- 任意以 "set" 开头的方法

```
execution(* set*(..))
```

- TestService 接口中定义的任意方法

```
execution(* org.springframework.myTest.aop.demo.TestService.*(..))
```

- myTest 包中定义的任意方法

```
execution(* org.springframework.myTest.*.*(..))
或
within(org.springframework.myTest.*)
```

- myTest 包**及其子包**中定义的任意方法

```
execution(* org.springframework.myTest..*.*(..))
或
within(org.springframework.myTest..*)
```

- myTest 包及其子包中所有以 Service 结尾的类中的任意方法

```
execution(* org.springframework.myTest..*Service.*(..))
```

- myTest 包及其子包中所有以 Service 结尾的类中的任意方法并且 bean 名称为 testServiceImpl

```
execution(* org.springframework.myTest..*Service.*(..)) and bean(testServiceImpl)
```

### 3.2 Spring AOP 增强的时机

- aop:before（前置增强）：在方法执行之前执行增强；
- aop:after-returning（后置增强）：在方法正常执行完成之后执行增强；
- aop:after-throwing（异常增强）：在方法抛出异常退出时执行增强，可以通过配置 throwing 来获得拦截到的异常信息；
- aop:after（最终增强）：在方法执行之后执行，相当于在 finally 里面执行；
- aop:around（环绕增强）：最强大的一种增强类型。 环绕增强可以在方法调用前后完成自定义的行为，环绕通知有**两个要求**：
&emsp;&emsp; 1.方法必须要返回一个Object（返回的结果）
&emsp;&emsp; 2.方法的第一个参数必须是ProceedingJoinPoint（可以继续向下传递的切入点）

### 3.3 示例

#### 3.3.1 xml 示例

&emsp;&emsp;首先定义一个通知类 TransactionManager 来确定需要增强什么操作。

```java
public class TransactionManager {

	public void begin(){
		System.out.println("开启事务");
	}

	public void commit(){
		System.out.println("事务提交");
	}

	public void rollBack(Exception e){
		System.out.println("事务回滚" + e);
	}

	public void close(){
		System.out.println("释放资源");
	}

	public Object around (ProceedingJoinPoint proceedingJoinPoint){
		Object ret = null;
		begin();
		try {
			ret = proceedingJoinPoint.proceed();
			commit();
		} catch (Throwable throwable) {
			rollBack(new Exception(throwable.getMessage()));
			throwable.printStackTrace();
		}finally {
			close();
		}
		return ret;
	}

}
```

&emsp;&emsp;然后是测试类 TestService

```java
public interface TestService {
	void save() throws Exception;
}

public class TestServiceImpl implements TestService{

	@Override
	public void save() throws Exception {
		System.out.println("保存数据到数据库");
		throw new Exception("异常");
	}

}
```

&emsp;&emsp;xml配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	   xmlns:context="http://www.springframework.org/schema/context"
	   xmlns:aop="http://www.springframework.org/schema/aop"
	   xsi:schemaLocation="http://www.springframework.org/schema/beans
	   http://www.springframework.org/schema/beans/spring-beans.xsd
	   http://www.springframework.org/schema/context
	   https://www.springframework.org/schema/context/spring-context.xsd 
	   http://www.springframework.org/schema/aop 
	   https://www.springframework.org/schema/aop/spring-aop.xsd"
>

	<context:annotation-config/>
	<context:component-scan base-package="org.springframework.myTest"/>

	<bean name="testServiceImpl" class="org.springframework.myTest.aop.demo.TestServiceImpl" />
	
	<!-- 通知 bean -->
	<bean name="transactionManager" class="org.springframework.myTest.aop.demo.TransactionManager" />

	<aop:config>

		<!-- 切入点 -->
		<aop:pointcut id="transactionPointcut" expression="execution(* org.springframework.myTest..*Service.*(..))"/>

		<!-- 切面 = 切入点 + 通知 -->
		<aop:aspect ref="transactionManager">
			<!--通知:
				在方法执行的什么时机（方法前/方法后/方法前后）做什么（增强的功能）
				aop:before（前置增强）：在方法执行之前执行增强；
				aop:after-returning（后置增强）：在方法正常执行完成之后执行增强；
				aop:after-throwing（异常增强）：在方法抛出异常退出时执行增强，可以通过配置 throwing 来获得拦截到的异常信息；
				aop:after（最终增强）：在方法执行之后执行，相当于在 finally 里面执行；
				aop:around（环绕增强）：最强大的一种增强类型。 环绕增强可以在方法调用前后完成自定义的行为，环绕通知有两个要求：
					1.方法必须要返回一个Object（返回的结果）
					2.方法的第一个参数必须是ProceedingJoinPoint（可以继续向下传递的切入点）
			-->
			<!--在切入点方法执行之前执行通知的 begin 方法-->
			<!--<aop:before method="begin" pointcut-ref="transactionPointcut" />-->

			<!--在切入点方法执行之后执行通知的 commit 方法-->
			<!--<aop:after-returning method="commit" pointcut-ref="transactionPointcut"/>-->

			<!--在切入点方法执行抛出异常之后执行通知的 rollBack 方法-->
			<!--<aop:after-throwing method="rollBack" pointcut-ref="transactionPointcut" throwing="e"/>-->

			<!--在切入点方法执行后执行通知的 close 方法,相当于在 finally 里面执行-->
			<!--<aop:after method="close" pointcut-ref="transactionPointcut" />-->

			<aop:around method="around" pointcut-ref="transactionPointcut" />

		</aop:aspect>

	</aop:config>

</beans>
```

&emsp;&emsp;测试方法

```java
public static void main(String[] args) throws Exception {
	ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("myTestResources/applicationContext.xml");
	TestService testServiceImpl = (TestService) context.getBean("testServiceImpl");
	testServiceImpl.save();
}
```

#### 3.3.2 注解使用示例

&emsp;&emsp;借助于 Spring Aop 的命名空间可以将纯 POJO 转换为切面，实际上这些 POJO 只是提供了满足切点的条件时所需要调用的方法，但是，这种技术需要 XML 进行配置，不能支持注解。所以 spring 借鉴了 AspectJ 的切面，以提供注解驱动的 AOP，本质上它依然是 Spring 基于代理的 AOP，只是编程模型与 AspectJ 完全一致，这种风格的好处就是不需要使用 XML 进行配置。

```java
@Aspect
public class TransactionManager {
	//定义切入点，切入点名称为方法名称
	@Pointcut("execution(* org.springframework.myTest..*Service.*(..))")
	public void pointcut(){
	}

	@Before("pointcut()")
	public void begin(){
		System.out.println("开启事务");
	}

	@AfterReturning("pointcut()")
	public void commit(){
		System.out.println("事务提交");
	}

	@AfterThrowing(value = "pointcut()",throwing = "e")
	public void rollBack(Exception e){
		System.out.println("事务回滚" + e);
	}

	@After("pointcut()")
	public void close(){
		System.out.println("释放资源");
	}
	
	@Around("pointcut()")
	public Object around (ProceedingJoinPoint proceedingJoinPoint){
		Object ret = null;
		begin();
		try {
			ret = proceedingJoinPoint.proceed();
			commit();
		} catch (Throwable throwable) {
			rollBack(new Exception(throwable.getMessage()));
			throwable.printStackTrace();
		}finally {
			close();
		}
		return ret;
	}

}
```

&emsp;&emsp;xml配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	   xmlns:context="http://www.springframework.org/schema/context"
	   xmlns:aop="http://www.springframework.org/schema/aop"
	   xsi:schemaLocation="http://www.springframework.org/schema/beans
	   http://www.springframework.org/schema/beans/spring-beans.xsd
	   http://www.springframework.org/schema/context
	   https://www.springframework.org/schema/context/spring-context.xsd
	   http://www.springframework.org/schema/aop
	   https://www.springframework.org/schema/aop/spring-aop.xsd"
>

	<context:annotation-config/>
	<context:component-scan base-package="org.springframework.myTest"/>
	<aop:aspectj-autoproxy />

	<bean name="testServiceImpl" class="org.springframework.myTest.aop.demo.TestServiceImpl" />
	<bean name="transactionManager" class="org.springframework.myTest.aop.demo.TransactionManager" />

</beans>
```

#### 3.3.3 advisor 标签

&emsp;&emsp;Spring 中还有一个标签 advisor 也可以用于配置。

- `<aop:aspect>`：定义切面（切面包括通知和切点）
- `<aop:advisor>`：定义通知器（通知器跟切面一样，也包括通知和切点）

&emsp;&emsp;在使用 `<aop:aspect>` 定义切面时，通知只需要定义一般的 bean 就行，而定义 `<aop:advisor>` 中引用的通知时，通知必须实现 Advice 接口。

**通知 TransactionManager 类**

```java
public class TransactionManager implements MethodBeforeAdvice, AfterReturningAdvice,ThrowsAdvice {

	public void begin(){
		System.out.println("开启事务");
	}

	public void commit(){
		System.out.println("事务提交");
		System.out.println("释放资源");
	}

	public void rollBack(Exception e){
		System.out.println("事务回滚" + e);
		System.out.println("释放资源");
	}

	@Override
	public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
		commit();
	}

	@Override
	public void before(Method method, Object[] args, Object target) throws Throwable {
		begin();
	}

	public void afterThrowing(Exception ex){
		rollBack(ex);
	}

}
```

**aop 配置**

```xml
<bean name="testServiceImpl" class="org.springframework.myTest.aop.demo.TestServiceImpl" />
<!-- 通知 bean -->
<bean name="transactionManager" class="org.springframework.myTest.aop.demo.TransactionManager" />

<aop:config >

	<aop:pointcut id="transactionPointcut" expression="execution(* org.springframework.myTest..*Service*.*(..))"/>

	<aop:advisor advice-ref="transactionManager" pointcut-ref="transactionPointcut" />

</aop:config>
```

#### 3.3.4 declare-parents 标签

&emsp;&emsp;如果有这样一个需求，为一个已知的 API 添加一个新的功能，由于是已知的 API，不能修改其类，只能通过外部包装。但是如果通过之前的 AOP 前置或后置通知，又不太合理，最简单的办法就是实现某个自定义的接口，这个接口包含了想要添加的方法。但是 JAVA 不是一门动态的语言，无法再编译后动态添加新的功能，这个时候就可以使用 aop:declare-parents 来实现。

&emsp;&emsp;假设给现有的类 TestServiceImpl 添加一个 update 功能，新建要添加的功能接口及实现：

```java
public interface AddService {
	void update();
}
public class AddServiceImpl implements AddService{
	@Override
	public void update() {
		System.out.println("更新数据库");
	}
}
```

配置文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	   xmlns:aop="http://www.springframework.org/schema/aop"
	   xsi:schemaLocation="http://www.springframework.org/schema/beans
	   http://www.springframework.org/schema/beans/spring-beans.xsd
	   http://www.springframework.org/schema/aop
	   https://www.springframework.org/schema/aop/spring-aop.xsd"
>


	<bean name="testServiceImpl" class="org.springframework.myTest.aop.demo.TestServiceImpl" />


	<aop:config proxy-target-class="true"  >
		<aop:aspect >
			<aop:declare-parents types-matching="org.springframework.myTest.aop.demo.TestServiceImpl"
								 implement-interface="org.springframework.myTest.aop.demo.AddService"
								 default-impl="org.springframework.myTest.aop.demo.AddServiceImpl" />
		</aop:aspect>
	</aop:config>

</beans>
```

测试类

```java
public static void main(String[] args) throws Exception {
	ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("myTestResources/applicationContext_AOP.xml");
	TestServiceImpl testServiceImpl = (TestServiceImpl) context.getBean("testServiceImpl");
	testServiceImpl.save();
	((AddService)testServiceImpl).update();
}
```

&emsp;&emsp;在使用时，直接通过 getBean 获得 bean 转换成相应的接口就可以使用了。

## 4 AOP 标签解析

&emsp;&emsp;示例中展示了对方法的增强处理，使辅助功能可以独立于核心业务之外，方便于程序的扩展和解耦，更详细的使用这里就不深入研究了，可以参考[官方文档](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#aop)，然后看一下 AOP 标签的解析过程。根据 Spring 自定义标签的解析过程，如果声明了自定义标签，那么就一定在程序中注册了对应的解析器。根据自定义标签的流程分析寻找，最终找到 AopNamespaceHandler 类中注册了 AOP 相关的解析器。

**AopNamespaceHandler.init**

```java
public void init() {
	// In 2.0 XSD as well as in 2.5+ XSDs
	registerBeanDefinitionParser("config", new ConfigBeanDefinitionParser());
	registerBeanDefinitionParser("aspectj-autoproxy", new AspectJAutoProxyBeanDefinitionParser());
	registerBeanDefinitionDecorator("scoped-proxy", new ScopedProxyBeanDefinitionDecorator());

	// Only in 2.0 XSD: moved to context namespace in 2.5+
	registerBeanDefinitionParser("spring-configured", new SpringConfiguredBeanDefinitionParser());
}
```

&emsp;&emsp;无论是任何自定义解析器，由于是对 BeanDefinitionParser 接口的统一实现，所以入口都是从 parse 函数开始解析的。

**ConfigBeanDefinitionParser.parse**

```java
public BeanDefinition parse(Element element, ParserContext parserContext) {
	CompositeComponentDefinition compositeDef =
			new CompositeComponentDefinition(element.getTagName(), parserContext.extractSource(element));
	parserContext.pushContainingComponent(compositeDef);

	// 1. 根据 <aop:config> 标签创建并配置 自动代理创建器
	configureAutoProxyCreator(parserContext, element);

	List<Element> childElts = DomUtils.getChildElements(element);
	for (Element elt: childElts) {
		String localName = parserContext.getDelegate().getLocalName(elt);
		// 切入点 pointcut 标签解析
		if (POINTCUT.equals(localName)) {
			parsePointcut(elt, parserContext);
		}
		// advisor 标签解析
		else if (ADVISOR.equals(localName)) {
			parseAdvisor(elt, parserContext);
		}
		// 切面 aspect 标签解析
		else if (ASPECT.equals(localName)) {
			parseAspect(elt, parserContext);
		}
	}
	parserContext.popAndRegisterContainingComponent();
	return null;
}
```

### 4.1 升级或创建自动代理创建器

&emsp;&emsp;解析的第一个比较重要的方法就是 configureAutoProxyCreator，在这里完成了**自动代理创建器**的创建过程，也是关键逻辑的实现。

**ConfigBeanDefinitionParser.configureAutoProxyCreator**

```java
private void configureAutoProxyCreator(ParserContext parserContext, Element element) {
	AopNamespaceUtils.registerAspectJAutoProxyCreatorIfNecessary(parserContext, element);
}
```

**AopNamespaceUtils.registerAspectJAutoProxyCreatorIfNecessary**

```java
public static void registerAspectJAutoProxyCreatorIfNecessary(
		ParserContext parserContext, Element sourceElement) {
	// 1. 升级或创建 自动代理创建器的 beanDefinition
	BeanDefinition beanDefinition = AopConfigUtils.registerAspectJAutoProxyCreatorIfNecessary(
			parserContext.getRegistry(), parserContext.extractSource(sourceElement));
	// 2. 处理 config 标签的 proxy-target-class 和 expose-proxy 属性
	useClassProxyingIfNecessary(parserContext.getRegistry(), sourceElement);
	// 3. 注册组件并触发组件注册监听回调，由监听器进一步处理
	registerComponentIfNecessary(beanDefinition, parserContext);
}
```

**AopConfigUtils.registerAspectJAutoProxyCreatorIfNecessary**

```java
public static BeanDefinition registerAspectJAutoProxyCreatorIfNecessary(
		BeanDefinitionRegistry registry, @Nullable Object source) {

	return registerOrEscalateApcAsRequired(AspectJAwareAdvisorAutoProxyCreator.class, registry, source);
}
```

&emsp;&emsp;这里涉及到一个优先级问题，如果容器中包含了自动代理创建器且存在的自动代理创建器与当前使用的不一致，那么需要根据优先级来判断到底要使用哪一个。下面由于是第一次创建，容器中没有自动代理创建器，所以这里直接使用 **AspectJAwareAdvisorAutoProxyCreator** 作为实现类。

**AopConfigUtils.registerOrEscalateApcAsRequired**

```java
private static BeanDefinition registerOrEscalateApcAsRequired(
		Class<?> cls, BeanDefinitionRegistry registry, @Nullable Object source) {

	Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
	// 如果容器中包含了自动代理创建器 且 存在的自动代理创建器与当前使用的不一致
	// 那么需要根据优先级来判断到底要使用哪一个
	if (registry.containsBeanDefinition(AUTO_PROXY_CREATOR_BEAN_NAME)) {
		BeanDefinition apcDefinition = registry.getBeanDefinition(AUTO_PROXY_CREATOR_BEAN_NAME);
		if (!cls.getName().equals(apcDefinition.getBeanClassName())) {
			int currentPriority = findPriorityForClass(apcDefinition.getBeanClassName());
			int requiredPriority = findPriorityForClass(cls);
			// 存在的自动代理创建器优先级小于当前使用的自动代理创建器
			// 数值越大优先级越高
			if (currentPriority < requiredPriority) {
				// 将存在的自动代理创建器的 className 属性替换为当前使用的自动代理创建器
				apcDefinition.setBeanClassName(cls.getName());
			}
		}
		// 存在于当前使用相同，不需要再次创建
		return null;
	}
	// 当前不存在自动代理创建器，使用 cls 创建一个
	RootBeanDefinition beanDefinition = new RootBeanDefinition(cls);
	beanDefinition.setSource(source);
	beanDefinition.getPropertyValues().add("order", Ordered.HIGHEST_PRECEDENCE);
	beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
	registry.registerBeanDefinition(AUTO_PROXY_CREATOR_BEAN_NAME, beanDefinition);
	return beanDefinition;
}
```

&emsp;&emsp;在静态代码块中设置的自动代理创建器的优先级顺序：

```java
private static final List<Class<?>> APC_PRIORITY_LIST = new ArrayList<>(3);
static {
	// Set up the escalation list...
	APC_PRIORITY_LIST.add(InfrastructureAdvisorAutoProxyCreator.class);// 1
	APC_PRIORITY_LIST.add(AspectJAwareAdvisorAutoProxyCreator.class);// 2
	APC_PRIORITY_LIST.add(AnnotationAwareAspectJAutoProxyCreator.class);// 3
}
```

**proxy-target-class 和 expose-proxy**

&emsp;&emsp;在这一步骤中涉及到 config 标签的两个属性 proxy-target-class 和 expose-proxy。

- proxy-target-class：Spring 中使用了 JDK 动态代理和 CGLIB 动态代理两种实现方式，当代理目标实现了至少一个接口时，则会使用 JDK 动态代理，否则使用 CGLIB 动态代理，设置 proxy-target-class 为 true 则会强制使用 CGLIB 动态代理。
- expose-proxy：有时候**对象内部**自我调用将无法实施切面中的增强，设置 expose-proxy 为 true 则可以暴露代理对象，实现增强操作，例如：

```java
public class TestServiceImpl {
	public void save() throws Exception {
		System.out.println("保存数据到数据库");
		//this.update(); 此处的 this 指向目标对象，因此无法实施切面中的增强
		((TestServiceImpl)AopContext.currentProxy()).update();
	}
	public void update(){
		System.out.println("更新数据到数据库");
	}
}
```

&emsp;&emsp;这里属性 proxy-target-class 和 expose-proxy 的解析其实也就是一个属性设置的过程。

```java
public static void forceAutoProxyCreatorToUseClassProxying(BeanDefinitionRegistry registry) {
	if (registry.containsBeanDefinition(AUTO_PROXY_CREATOR_BEAN_NAME)) {
		BeanDefinition definition = registry.getBeanDefinition(AUTO_PROXY_CREATOR_BEAN_NAME);
		definition.getPropertyValues().add("proxyTargetClass", Boolean.TRUE);
	}
}

public static void forceAutoProxyCreatorToExposeProxy(BeanDefinitionRegistry registry) {
	if (registry.containsBeanDefinition(AUTO_PROXY_CREATOR_BEAN_NAME)) {
		BeanDefinition definition = registry.getBeanDefinition(AUTO_PROXY_CREATOR_BEAN_NAME);
		definition.getPropertyValues().add("exposeProxy", Boolean.TRUE);
	}
}
```

&emsp;&emsp;Spring 中 AOP 就是通过增强器（BeanPostProcessor）的形式来添加代理功能的，上述过程就是在解析使用的增强器实现类。

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/spring/202112291735945.png)

### 4.2 标签解析

&emsp;&emsp;然后以 aspect 标签为例，简单看一下 AOP 标签解析的过程。

**ConfigBeanDefinitionParser.parseAspect**

```java
private void parseAspect(Element aspectElement, ParserContext parserContext) {
	String aspectId = aspectElement.getAttribute(ID);
	String aspectName = aspectElement.getAttribute(REF);

	try {
		this.parseState.push(new AspectEntry(aspectId, aspectName));
		List<BeanDefinition> beanDefinitions = new ArrayList<>();
		List<BeanReference> beanReferences = new ArrayList<>();

		// 1. declare-parents 标签
		List<Element> declareParents = DomUtils.getChildElementsByTagName(aspectElement, DECLARE_PARENTS);
		for (int i = METHOD_INDEX; i < declareParents.size(); i++) {
			Element declareParentsElement = declareParents.get(i);
			beanDefinitions.add(parseDeclareParents(declareParentsElement, parserContext));
		}

		// We have to parse "advice" and all the advice kinds in one loop, to get the
		// ordering semantics right.
		NodeList nodeList = aspectElement.getChildNodes();
		boolean adviceFoundAlready = false;
		// 2. 如果 aspect 标签的子节点中存在'before'、'after'、'after-returning'、'after-throwing' 或 'around' 标签
		// 那么 aspect 标签必须拥有 ref 属性
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			// 子标签如果是 'before'、'after'、'after-returning'、'after-throwing' 或 'around' 标签
			if (isAdviceNode(node, parserContext)) {
				if (!adviceFoundAlready) {
					adviceFoundAlready = true;
					// aspect 标签必须拥有 ref 属性
					if (!StringUtils.hasText(aspectName)) {
						parserContext.getReaderContext().error(
								"<aspect> tag needs aspect bean reference via 'ref' attribute when declaring advices.",
								aspectElement, this.parseState.snapshot());
						return;
					}
					beanReferences.add(new RuntimeBeanReference(aspectName));
				}
				// 3. 对遍历到的子标签逐个解析为BeanDefinition
				AbstractBeanDefinition advisorDefinition = parseAdvice(
						aspectName, i, aspectElement, (Element) node, parserContext, beanDefinitions, beanReferences);
				beanDefinitions.add(advisorDefinition);
			}
		}

		AspectComponentDefinition aspectComponentDefinition = createAspectComponentDefinition(
				aspectElement, aspectId, beanDefinitions, beanReferences, parserContext);
		parserContext.pushContainingComponent(aspectComponentDefinition);

		// 3. pointcut 标签
		List<Element> pointcuts = DomUtils.getChildElementsByTagName(aspectElement, POINTCUT);
		for (Element pointcutElement : pointcuts) {
			parsePointcut(pointcutElement, parserContext);
		}

		parserContext.popAndRegisterContainingComponent();
	}
	finally {
		this.parseState.pop();
	}
}
```

&emsp;&emsp;解析 “before”、“after”、“after-returning”、“after-throwing” 或 “around” 之一，并使用提供的 BeanDefinitionRegistry 注册生成的 BeanDefinition。

**ConfigBeanDefinitionParser.parseAdvice**

```java
private AbstractBeanDefinition parseAdvice(
		String aspectName, int order, Element aspectElement, Element adviceElement, ParserContext parserContext,
		List<BeanDefinition> beanDefinitions, List<BeanReference> beanReferences) {

	try {
		this.parseState.push(new AdviceEntry(parserContext.getDelegate().getLocalName(adviceElement)));

		// create the method factory bean
		RootBeanDefinition methodDefinition = new RootBeanDefinition(MethodLocatingFactoryBean.class);
		// 通知 beanName
		methodDefinition.getPropertyValues().add("targetBeanName", aspectName);
		// 要调用通知 bean 的 methodName
		methodDefinition.getPropertyValues().add("methodName", adviceElement.getAttribute("method"));
		methodDefinition.setSynthetic(true);

		// create instance factory definition
		RootBeanDefinition aspectFactoryDef =
				new RootBeanDefinition(SimpleBeanFactoryAwareAspectInstanceFactory.class);
		aspectFactoryDef.getPropertyValues().add("aspectBeanName", aspectName);
		aspectFactoryDef.setSynthetic(true);

		// register the pointcut
		// 将标签解析为 BeanDefinition
		AbstractBeanDefinition adviceDef = createAdviceDefinition(
				adviceElement, parserContext, aspectName, order, methodDefinition, aspectFactoryDef,
				beanDefinitions, beanReferences);

		// configure the advisor
		// 配置 advisor
		RootBeanDefinition advisorDefinition = new RootBeanDefinition(AspectJPointcutAdvisor.class);
		advisorDefinition.setSource(parserContext.extractSource(adviceElement));
		advisorDefinition.getConstructorArgumentValues().addGenericArgumentValue(adviceDef);
		if (aspectElement.hasAttribute(ORDER_PROPERTY)) {
			advisorDefinition.getPropertyValues().add(
					ORDER_PROPERTY, aspectElement.getAttribute(ORDER_PROPERTY));
		}

		// register the final advisor
		// 注册最终的 advisor
		parserContext.getReaderContext().registerWithGeneratedName(advisorDefinition);

		return advisorDefinition;
	}
	finally {
		this.parseState.pop();
	}
}
```

&emsp;&emsp;子标签会使用对应的**实现类构造并转化为 AspectJPointcutAdvisor**，各种标签对应使用的实现类关系如下：

**ConfigBeanDefinitionParser.getAdviceClass**

```java
private Class<?> getAdviceClass(Element adviceElement, ParserContext parserContext) {
	String elementName = parserContext.getDelegate().getLocalName(adviceElement);
	//before
	if (BEFORE.equals(elementName)) {
		return AspectJMethodBeforeAdvice.class;
	}
	//after
	else if (AFTER.equals(elementName)) {
		return AspectJAfterAdvice.class;
	}
	//after_returning
	else if (AFTER_RETURNING_ELEMENT.equals(elementName)) {
		return AspectJAfterReturningAdvice.class;
	}
	//after_throwing
	else if (AFTER_THROWING_ELEMENT.equals(elementName)) {
		return AspectJAfterThrowingAdvice.class;
	}
	//around
	else if (AROUND.equals(elementName)) {
		return AspectJAroundAdvice.class;
	}
	else {
		throw new IllegalArgumentException("Unknown advice kind [" + elementName + "].");
	}
}
```

&emsp;&emsp;其他标签的解析过程与 aspect 标签类似。

## 5 Spring 中的 AOP 结构

### 5.1 Pointcut

```java
public interface Pointcut {

    ClassFilter getClassFilter();

    MethodMatcher getMethodMatcher();
	
	Pointcut TRUE = TruePointcut.INSTANCE;
}
```

&emsp;&emsp;`org.springframework.aop.Pointcut` 接口是 Spring AOP 体系中对切点的顶层抽象，其提供两个方法，返回结果分别用于在不同的级别上限定 Joinpoint 的匹配范围，满足不同粒度的匹配，还提供了一个 TruePointcut 实例，当 Pointcut 为 TruePointcut 类型时，则会忽略所有的匹配条件，永远返回 true。Spring Aop 主要支持在方法级别上的匹配，所以对类级别的匹配支持相对简单一些。

&emsp;&emsp;ClassFilter 接口用于将切入点限制为一组给定的目标类。

```java
public interface ClassFilter {
	
    boolean matches(Class clazz);
	
}
```

&emsp;&emsp;MethodMatcher 接口用于测试此切点是否与目标类上的给定方法匹配。

```java
public interface MethodMatcher {
	
	//静态匹配：在匹配条件不是太严格时使用，可以满足大部分场景的使用
    boolean matches(Method m, Class<?> targetClass);
	
	/**
	 * 是否需要执行动态匹配
	 *	两个方法的分界线就是boolean isRuntime()方法
	 *	1、先调用静态匹配，若返回 true 此时就会继续去检查 isRuntime() 的返回值
	 *	2、若 isRuntime() 也返回 true，那就继续调用动态匹配
	 *	若静态匹配失败，则动态匹配一定失败，因为动态匹配比静态匹配要严格
	 */
    boolean isRuntime();

	// 动态匹配（运行时匹配）: 它是严格的匹配，在运行时动态的对参数的类型进行匹配
    boolean matches(Method m, Class<?> targetClass, Object... args);
}
```

&emsp;&emsp;同时，‎Spring 提供了几种方便的切点实现，不同类型的切点所对应的方法匹配模式也不同，主要包括静态匹配和动态匹配两种。

#### 5.1.1 静态 Pointcut

**静态匹配的抽象实现**

```java
public abstract class StaticMethodMatcher implements MethodMatcher {
	// 永远返回 false 表示只会进行静态匹配
	@Override
	public final boolean isRuntime() {
		return false;
	}

	@Override
	public final boolean matches(Method method, Class<?> targetClass, Object... args) {
		// should never be invoked because isRuntime() returns false
		throw new UnsupportedOperationException("Illegal MethodMatcher usage");
	}

}
```

&emsp;&emsp;静态匹配不会考虑具体方法参数，因为不用每次都检查参数，那么对于同样的类型的方法匹配结果，就可以在框架内部缓存以提高性能。对于大多数用法来说，静态切点已经足够了，而且是最好的实现方式，静态切点的一个典型例子是基于 `java.util.regex` 提供的正则表达式匹配实现的 JdkRegexpMethodPointcut。

&emsp;&emsp;JdkRegexpMethodPointcut 提供两个列表分别存储匹配的正则表达式(patterns)和排除的正则表达式(excludedPatterns)，一个使用的例子如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	   xmlns:aop="http://www.springframework.org/schema/aop"
	   xsi:schemaLocation="http://www.springframework.org/schema/beans
	   http://www.springframework.org/schema/beans/spring-beans.xsd
	   http://www.springframework.org/schema/aop
	   https://www.springframework.org/schema/aop/spring-aop.xsd"
>

	<bean name="testServiceImpl" class="org.springframework.myTest.aop.demo.TestServiceImpl" />

	<bean name="transactionManager" class="org.springframework.myTest.aop.demo.TransactionManager" />

	<bean id="regexPointcut" class="org.springframework.aop.support.JdkRegexpMethodPointcut">
		<property name="patterns">
			<list value-type="java.lang.String">
				<value>.*save.*</value><!-- 拦截所有方法名含有save的方法 -->
			</list>
		</property>
	</bean>

	<aop:config >
		<aop:advisor advice-ref="transactionManager" pointcut-ref="regexPointcut" />
	</aop:config>

</beans>
```

&emsp;&emsp;Spring 中还提供了一个 RegexpMethodPointcutAdvisor 类来简化这个配置过程，RegexpMethodPointcutAdvisor 可以方便的同时指定一个JdkRegexpMethodPointcut 和其需要对应的 Advice，使用示例如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	   xmlns:aop="http://www.springframework.org/schema/aop"
	   xsi:schemaLocation="http://www.springframework.org/schema/beans
	   http://www.springframework.org/schema/beans/spring-beans.xsd
	   http://www.springframework.org/schema/aop
	   https://www.springframework.org/schema/aop/spring-aop.xsd"
>

	<aop:aspectj-autoproxy/>

	<bean name="testServiceImpl" class="org.springframework.myTest.aop.demo.TestServiceImpl" />

	<bean name="transactionManager" class="org.springframework.myTest.aop.demo.TransactionManager" />

	<bean name="regexpMethodPointcutAdvisor" class="org.springframework.aop.support.RegexpMethodPointcutAdvisor">
		<property name="advice" ref="transactionManager"/>
		<property name="patterns">
			<list value-type="java.lang.String">
				<value>.*save.*</value><!-- 拦截所有方法名含有save的方法 -->
			</list>
		</property>
 	</bean>

</beans>
```

#### 5.1.2 动态 Pointcut

&emsp;&emsp;动态切点的计算相较静态切点成本要高的多，在计算时需要考虑静态信息和参数信息，这就导致每次匹配都需要重新计算，计算结果不能被缓存，因为参数会变化。

**动态匹配的抽象实现**

```java
public abstract class DynamicMethodMatcher implements MethodMatcher {

	@Override
	public final boolean isRuntime() {
		return true;
	}

	/**
	 * Can override to add preconditions for dynamic matching. This implementation
	 * always returns true.
	 */
	@Override
	public boolean matches(Method method, Class<?> targetClass) {
		return true;
	}

}
```

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/spring/202201060930945.png)

### 5.2 Advice

&emsp;&emsp;在 Spring 中，每个 Advice 都是一个 bean，通过 Advice 来指定在切点的何处具体做什么事情，Advice 只与方法和参数有关，与代理方式和代理状态无关。根据增强方式和位置大致可以分为前置增强器，后置增强器，拦截器和引介增强器几种，具体关系如下图所示：

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/spring/202201071016971.png)

### 5.3 Advisor

&emsp;&emsp;在 Spring 中，Advisor 用来管理 Advice 及其对应的 pointcut，它的继承体系主要有两个：PointcutAdvisor 和 IntroductionAdvisor，其中 IntroductionAdvisor 只能应用于类级别的拦截，只能使用 Introduction 类型的 Advice，而 PointcutAdvisor 可以使用几乎任意类型的 Pointcut 和 Advice。

&emsp;&emsp;`org.springframework.aop.support.DefaultPointcutAdvisor` 是 Spring 提供的最常用的 Advisor，可以把任意的两个 Advice 和 Pointcut 组合在一起。

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/spring/202201071120295.png)

















## 4.2 AOP 代理的创建方式

&emsp;&emsp;结合前面讲的 bean 实例化过程可以看出，在真正进行默认的 bean 创建之前，可以通过应用 InstantiationAwareBeanPostProcessor.postProcessBeforeInstantiation 来实现短路拦截操作，AOP 代理的创建就是在这里，由上面创建的自动代理创建器完成的，类图如下：

&emsp;&emsp;实现了 BeanPostProcessor 接口的 bean 都会在 ApplicationContext 容器启动时被注册，并在其他 bean 实例化过程中通过其对应的接口方法实现对 bean 的额外处理，所以这里创建代理的入口就是 postProcessBeforeInstantiation。

**AbstractAutoProxyCreator.postProcessBeforeInstantiation**

```java

```

[](https://blog.csdn.net/f641385712/article/details/89303088)

[](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#aop-advice-before)

&emsp;&emsp;Pointcut 还有一个子接口 ExpressionPointcut，用于解析 String 类型的切点表达式。AOP 自定义标签解析将 pointcut 标签解析为 AspectJExpressionPointcut 类，它使用 AspectJ 提供的库来解析 AspectJ 切入点表达式字符串，结构类图如下：

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/spring/202201041546571.png)

[](https://blog.csdn.net/f641385712/article/details/89178421)