# Spring源码解析十：AOP

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
- 动态 AOP 实现， AOP 框架在运行阶段对动态生成代理对象（在内存中以 JDK 动态代理，或 CGlib 动态地生成 AOP 代理类），如 SpringAOP。

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/spring/202112201031807.png)

## 1 静态代理和动态代理

&emsp;&emsp;顾名思义，代理，就是你委托别人帮你办事，所以代理模式也有人称作委托模式的。比如领导要做什么事，可以委托他的秘书去帮忙做，这时就可以把秘书看做领导的代理。客户端直接使用的都是代理对象，不知道真实对象是谁，此时代理对象可以在客户端和真实对象之间起到中介的作用。

&emsp;&emsp;同样的，代理模式也分为两种实现方式：

- 静态代理：在程序运行前就已经存在代理类的字节码文件，代理对象和真实对象的关系在运行前就确定了。
- 动态代理：动态代理类是在程序运行期间由JVM通过反射等机制动态的生成的，所以不存在代理类的字节码文件。代理对象和真实对象的关系是在程序运行事情才确定的。

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

**main**

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
- 如果需要代理的方法很多，则要为每一种方法都进行代理处理。 
- 如果接口增加一个方法，除了所有实现类需要实现这个方法外，所有代理类也需要实现此方法。增加了代码维护的复杂度。

### 1.2 动态代理

&emsp;&emsp;动态代理类是在程序运行期间由JVM通过反射等机制动态的生成的，所以不存在代理类的字节码文件。代理对象和真实对象的关系是在程序运行事情才确定的。动态代理的根据实现方式的不同又可以分为 JDK 动态代理和 CGlib 动态代理。

- JDK 动态代理：利用反射机制生成一个实现代理接口的类，在调用具体方法前调用InvokeHandler来处理。
- CGlib 动态代理：利用ASM（开源的Java字节码编辑库，操作字节码）开源包，将代理对象类的class文件加载进来，通过修改其字节码生成子类来处理。

&emsp;&emsp;区别：JDK 代理只能对**实现接口**的类生成代理；CGlib是针对类实现代理，对指定的类生成一个子类，并覆盖其中的方法，这种通过继承类的实现方式，不能代理final修饰的类。

#### 1.2.1 JDK 动态代理

&emsp;&emsp;在使用 JDK 动态代理时，需要用到一个类和一个接口 java.lang.reflect.Proxy 类，它提供了一组静态方法来为一组接口动态地生成代理类及其对象，返回动态生成的代理对象。java.lang.reflect.InvocationHandler 接口，负责集中处理动态代理类上的所有方法调用。

&emsp;&emsp;新建 MyInvocationHandler 实现 InvocationHandler 接口，其余类同上。

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

**main**

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

&emsp;&emsp;可以看出，通过 MyInvocationHandler 同样可以实现代理 TestServiceImpl 类的方法实现，实际上实现的是任意类的任意方法的代理，这里只是因为传入的是 TestServiceImpl 类。

#### 1.2.2 CGlib 动态代理

&emsp;&emsp;CGlib 针对类进行代理，因此被代理的类不需要实现接口，使用方法与上面 JDK 代理类似。首先新建一个类 MyMethodInterceptor 实现 MethodInterceptor 接口，这里需要注意的是实际调用是 methodProxy.invokeSuper 如果使用 invoke 方法，则需要传入被代理的类对象，否则出现死循环。

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

&emsp;&emsp;TestServiceImpl不再需要实现接口。

```java
public class TestServiceImpl{
	public void save() {
		System.out.println("保存数据到数据库");
	}
}
```

main

```java
public static void main(String[] args) {
	Enhancer enhancer = new Enhancer();
	enhancer.setSuperclass(TestServiceImpl.class);
	enhancer.setCallback(new MyMethodInterceptor());
	TestServiceImpl testServiceImpl = (TestServiceImpl)enhancer.create();
	testServiceImpl.save();
}
```

## 2 AOP 中的术语 & Spring 中的 AOP

- 通知（Advice）: AOP 框架中的增强处理，通知描述了切面何时执行以及如何执行增强处理，**在方法执行的什么时机（方法前/方法后/方法前后）做什么（增强的功能）**。
- 连接点（join point）: 连接点表示应用执行过程中能够插入切面的一个点，这个点可以是方法的调用、异常的抛出。在 Spring AOP 中，连接点总是方法的调用。
- 切点（PointCut）: 可以插入增强处理的连接点，**在哪些类，哪些方法上切入**。
- 切面（Aspect）: 切面是通知和切点的结合，**切面=切入点+通知**。
- 引入（Introduction）：引入允许向现有的类添加新的方法或者属性。
- 织入（Weaving）: 将增强处理添加到目标对象中，并创建一个被增强的对象，这个过程就是织入，**把切面加入到对象，并创建出代理对象的过程**。

&emsp;&emsp;AOP 框架有很多种，AOP 框架的实现方式有可能不同， Spring 中的 AOP 是通过动态代理实现的。不同的 AOP 框架支持的连接点也有所区别，例如 AspectJ 和 JBoss，除了支持方法切点，它们还支持字段和构造器的连接点。而 Spring AOP 不能拦截对对象字段的修改，也不支持构造器连接点，无法在 Bean 创建时应用通知。

## 3 动态 AOP 的使用

&emsp;&emsp;Spring 提供了 AOP 的实现，在低版本 Spring 中定义一个切面是比较麻烦的，需要实现特定的接口，并进行一些较为复杂的配置，低版本 Spring AOP的配置是被批评最多的地方。Spring 听取这方面的批评声音，并下决心彻底改变这一现状。在 Spring2.0 中，Spring AOP 已经焕然一新，你可以使用 @AspectJ 注解非常容易的定义一个切面，不需要实现任何的接口。

&emsp;&emsp;Spring2.0 采用 @AspectJ 注解对 POJO 进行标注，从而定义一个包含切点信息和增强横切逻辑的切面，Spring 2.0 可以将这个切面织入到匹配的目标 Bean 中。@AspectJ 注解使用 AspectJ 切点表达式语法进行切点定义，可以通过切点函数、运算符、通配符等高级功能进行切点定义，拥有强大的连接点描述能力。

&emsp;&emsp;尽管 Spring 一再简化配置，并且大有使用注解取代 XML 之势，但无论如何 XML 还是 Spring 的基石。还是使用上面提到的示例，需要在 TestServiceImpl 的 save 方法前后添加增强的功能。

### 3.1 AspectJ 切入点语法

&emsp;&emsp;首先就要解决一个问题，怎么表示在哪些方法上增强，一些先行者开发了一套语言来支持 AOP ———— AspectJ(语言)。

```
execution(modifiers-pattern? ret-type-pattern declaring-type-pattern?name-pattern(param-pattern)throws-pattern?)
翻译成中文：
execution(<访问修饰符>? <返回类型> <声明类型>? <方法名>(<参数>) <异常>?)
举例：
public static Class java.lang.Class.forName(String className)throws ClassNotFoundException 
[public static](访问修饰符) [Class](返回类型) [java.lang.Class](声明类型).[forName](方法名)[(String className)](参数)[throws ClassNotFoundException](异常)
```

&emsp;&emsp;使用 execution 指示器选择切入点，当方法表达式以 * 号开始，标识不关心方法的返回值类型，对于方法参数列表，使用 .. 标识切点选择任意的方法，多个匹配之间可以使用链接符 &&、||、！来表示 “且”、“或”、“非” 的关系。但是在使用 XML 文件配置时，这些符号有特殊的含义，所以使用 “and”、“or”、“not”来表示。

**几种常用的切入点表达式**

- 任意公共方法

```
execution(public * * (..))
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
```

- myTest 包**及其子包**中定义的任意方法

```
execution(* org.springframework.myTest..*.*(..))
```

- myTest 包**及其子包**中所有以 Service 结尾的类中的任意方法

```
execution(* org.springframework.myTest..*Service.*(..))
```

- myTest 包**及其子包**中所有以 Service 结尾的类中的任意方法并且 bean 名称为 testServiceImpl 并且在 org.springframework.myTest.aop.demo 包中

```
execution(* org.springframework.myTest..*Service.*(..)) and bean(testServiceImpl) and within(org.springframework.myTest.aop.demo.*)
```

### 3.2 增强的时机

- aop:before（前置增强）：在方法执行之前执行增强；
- aop:after-returning（后置增强）：在方法正常执行完成之后执行增强；
- aop:after-throwing（异常增强）：在方法抛出异常退出时执行增强，可以通过配置 throwing 来获得拦截到的异常信息；
- aop:after（最终增强）：在方法执行之后执行，相当于在 finally 里面执行；
- aop:around（环绕增强）：最强大的一种增强类型。 环绕增强可以在方法调用前后完成自定义的行为，环绕通知有**两个要求**：
&emsp;&emsp; 1.方法必须要返回一个Object（返回的结果）
&emsp;&emsp; 2.方法的第一个参数必须是ProceedingJoinPoint（可以继续向下传递的切入点）

### 3.3 示例

#### 3.3.1 xml 示例

&emsp;&emsp;首先定义一个增强类 TransactionManager 来确定需要增强什么操作。

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

	<bean name="transactionManager" class="org.springframework.myTest.aop.demo.TransactionManager" />

	<aop:config>

		<!--切入点-->
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
			<!--在 transactionPointcut 方法执行之前执行 begin 方法-->
			<!--<aop:before method="begin" pointcut-ref="transactionPointcut" />-->

			<!--在 transactionPointcut 方法执行之后执行 commit 方法-->
			<!--<aop:after-returning method="commit" pointcut-ref="transactionPointcut"/>-->

			<!--在 transactionPointcut 方法执行抛出异常之后执行 rollBack 方法-->
			<!--<aop:after-throwing method="rollBack" pointcut-ref="transactionPointcut" throwing="e"/>-->

			<!--在 transactionPointcut 方法执行后执行 close 方法,相当于在 finally 里面执行-->
			<!--<aop:after method="close" pointcut-ref="transactionPointcut" />-->

			<aop:around method="around" pointcut-ref="transactionPointcut" />

		</aop:aspect>

	</aop:config>

</beans>
```

&emsp;&emsp;main方法

```java
public static void main(String[] args) throws Exception {
	ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("myTestResources/applicationContext.xml");
	TestService testServiceImpl = (TestService) context.getBean("testServiceImpl");
	testServiceImpl.save();
}
```

#### 3.3.1 注解示例

&emsp;&emsp;当然也可以使用注解配置。

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

### 3.4 AOP 自定义标签

&emsp;&emsp;示例中实现了对 save 方法的增强处理，使辅助功能可以独立于核心业务之外，方便于程序的扩展和解耦。根据 Spring 自定义标签的解析过程，如果声明了自定义标签，那么就一定在程序中注册了对应的解析器。



