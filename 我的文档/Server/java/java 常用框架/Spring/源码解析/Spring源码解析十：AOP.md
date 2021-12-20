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

