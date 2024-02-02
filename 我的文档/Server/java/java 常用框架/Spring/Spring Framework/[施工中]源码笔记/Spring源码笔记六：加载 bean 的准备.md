# Spring源码笔记六：加载 bean 的准备

&emsp;&emsp;前面已经完成了从 xml 到 BeanDefinition 的解析工作，接下来就是 bean 加载和使用。

&emsp;&emsp;对于加载 bean 的工作，同样很复杂，回到程序的一开始。

```java
public static void main(String[] args) {
   Resource resource = new ClassPathResource("myTestResources/applicationContext.xml");
   BeanFactory beanFactory = new XmlBeanFactory(resource);
   User user = (User)beanFactory.getBean("user");
   }
```

&emsp;&emsp;对于加载 bean 的调用，是从 `beanFactory.getBean` 开始的。

**AbstractBeanFactory.getBean**

```java
public Object getBean(String name) throws BeansException {
		//调用真正的处理方法 doGetBean
		return doGetBean(name, null, null, false);
	}
```

**AbstractBeanFactory.doGetBean**

````java
protected <T> T doGetBean(
			String name, @Nullable Class<T> requiredType, @Nullable Object[] args, boolean typeCheckOnly)
			throws BeansException {
	// 1.获取实际的 bean 的名字
	String beanName = transformedBeanName(name);
	Object beanInstance;

	// Eagerly check singleton cache for manually registered singletons.
	// 2.对于单例 bean 来说，首先尝试从缓存中获取 bean
	// 也是
	// 循环依赖 -- 获取早期的单例 bean 引用
	Object sharedInstance = getSingleton(beanName);
	if (sharedInstance != null && args == null) {
		if (logger.isTraceEnabled()) {
			if (isSingletonCurrentlyInCreation(beanName)) {
				logger.trace("Returning eagerly cached instance of singleton bean '" + beanName +
						"' that is not fully initialized yet - a consequence of a circular reference");
			}
			else {
				logger.trace("Returning cached instance of singleton bean '" + beanName + "'");
			}
		}
		// 3 检测实例是否为FactoryBean，比如在 FactoryBean 的情况不是
		// 直接返回实例本身，而是返回 FactoryBean 所创建的实例
		beanInstance = getObjectForBeanInstance(sharedInstance, name, beanName, null);
	}

	else {
		// Fail if we're already creating this bean instance:
		// We're assumably within a circular reference.
		// 4. 创建实例前的准备
		// 如果指定的 bean 是 prototype 作用域的并且正在创建中则返回 true
		if (isPrototypeCurrentlyInCreation(beanName)) {
			// 循环依赖 -- 多例检测
			throw new BeanCurrentlyInCreationException(beanName);
		}

		// Check if bean definition exists in this factory.
		BeanFactory parentBeanFactory = getParentBeanFactory();
		// 4.1 当前容器中并没有 beanName 对应的 BeanDefinition，尝试
		// 从父容器中查找并由父容器创建
		if (parentBeanFactory != null && !containsBeanDefinition(beanName)) {
			// Not found -> check parent.
			// 依旧先是解析beanName
			String nameToLookup = originalBeanName(name);
			if (parentBeanFactory instanceof AbstractBeanFactory) {
				// 调用父容器的 doGetBean
				return ((AbstractBeanFactory) parentBeanFactory).doGetBean(
						nameToLookup, requiredType, args, typeCheckOnly);
			}
			else if (args != null) {
				// Delegation to parent with explicit args.
				return (T) parentBeanFactory.getBean(nameToLookup, args);
			}
			else if (requiredType != null) {
				// No args -> delegate to standard getBean method.
				return parentBeanFactory.getBean(nameToLookup, requiredType);
			}
			else {
				return (T) parentBeanFactory.getBean(nameToLookup);
			}
		}
		// 4.2 将指定的 bean 标记为即将创建
		if (!typeCheckOnly) {
			markBeanAsCreated(beanName);
		}

		StartupStep beanCreation = this.applicationStartup.start("spring.beans.instantiate")
				.tag("beanName", name);
		try {
			if (requiredType != null) {
				beanCreation.tag("beanType", requiredType::toString);
			}
			// 4.3 将存储信息的 GernericBeanDefinition 转换为 RootBeanDefinition，
			// 如果指定的 bean 是子 bean 会同时合并父类的相关属性
			RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
			checkMergedBeanDefinition(mbd, beanName, args);

			// Guarantee initialization of beans that the current bean depends on.
			// 4.4 若存在依赖，先实例化依赖的 bean
			String[] dependsOn = mbd.getDependsOn();
			if (dependsOn != null) {
				for (String dep : dependsOn) {
					if (isDependent(beanName, dep)) {
						throw new BeanCreationException(mbd.getResourceDescription(), beanName,
								"Circular depends-on relationship between '" + beanName + "' and '" + dep + "'");
					}
					registerDependentBean(dep, beanName);
					try {
						getBean(dep);
					}
					catch (NoSuchBeanDefinitionException ex) {
						throw new BeanCreationException(mbd.getResourceDescription(), beanName,
								"'" + beanName + "' depends on missing bean '" + dep + "'", ex);
					}
				}
			}

			// Create bean instance.
			// 5.创建 bean 实例
			if (mbd.isSingleton()) {
				// 5.1 单例 bean 的创建
				sharedInstance = getSingleton(beanName, () -> {
					try {
						return createBean(beanName, mbd, args);
					}
					catch (BeansException ex) {
						// Explicitly remove instance from singleton cache: It might have been put there
						// eagerly by the creation process, to allow for circular reference resolution.
						// Also remove any beans that received a temporary reference to the bean.
						destroySingleton(beanName);
						throw ex;
					}
				});
				beanInstance = getObjectForBeanInstance(sharedInstance, name, beanName, mbd);
			}

			else if (mbd.isPrototype()) {
				// It's a prototype -> create a new instance.
				// 5.2 多例 bean 的创建，直接 new 一个
				Object prototypeInstance = null;
				try {
					// 创建 bean 之前的回调，默认实现将 bean 设置为正在创建
					beforePrototypeCreation(beanName);
					prototypeInstance = createBean(beanName, mbd, args);
				}
				finally {
					// 创建 bean 之后的回调，默认实现将 bean 从正在创建中移除
					afterPrototypeCreation(beanName);
				}
				beanInstance = getObjectForBeanInstance(prototypeInstance, name, beanName, mbd);
			}

			else {
				// 5.3 其他 scope 的 bean 的创建
				String scopeName = mbd.getScope();
				if (!StringUtils.hasLength(scopeName)) {
					throw new IllegalStateException("No scope name defined for bean ´" + beanName + "'");
				}
				Scope scope = this.scopes.get(scopeName);
				if (scope == null) {
					throw new IllegalStateException("No Scope registered for scope name '" + scopeName + "'");
				}
				try {
					Object scopedInstance = scope.get(beanName, () -> {
						beforePrototypeCreation(beanName);
						try {
							return createBean(beanName, mbd, args);
						}
						finally {
							afterPrototypeCreation(beanName);
						}
					});
					beanInstance = getObjectForBeanInstance(scopedInstance, name, beanName, mbd);
				}
				catch (IllegalStateException ex) {
					throw new ScopeNotActiveException(beanName, scopeName, ex);
				}
			}
		}
		catch (BeansException ex) {
			beanCreation.tag("exception", ex.getClass().toString());
			beanCreation.tag("message", String.valueOf(ex.getMessage()));
			cleanupAfterBeanCreationFailure(beanName);
			throw ex;
		}
		finally {
			beanCreation.end();
		}
	}
	// 6. 检测 bean 的类型是否符合实际类型
	return adaptBeanInstance(name, beanInstance, requiredType);
}
````

&emsp;&emsp;方法非常长，从方法上就可以看出 bean 的加载过程非常复杂，其中涉及各种考虑的情况，先按照上面的注释大致了解一下过程。步骤如下：

1. 获取 beanName
2. 尝试从缓存中获取 bean 
3. 创建实例前的准备
4. 创建 bean 实例
5. bean 的类型检测

&emsp;&emsp;接下来一步一步往下看。

## 1. 获取 beanName

&emsp;&emsp;对于 beanName 的获取，由于传入的参数可能是别名，也可能是 FactoryBean，所以需要对名称进行一系列的解析工作。在看解析工作之前，先了解一下 FactoryBean 是什么。

### 1.1 FactoryBean

&emsp;&emsp;创建一个类 Car ，里面有它的颜色 Color 属性。 

```java
public class Car {
   private Color color;
   //在这个汽车一生产出来就带有颜色属性
   public Car() {
      this.color = new Red();
   }
}
```

&emsp;&emsp;随着业务发展越来越复杂，现在希望在生产汽车的时候，可能不一定是红色，也可能是蓝色或者其他颜色，而 Car 这个类又是属于非常核心的类，在后续的开发中，不希望每次业务变动都去更改 Car 的代码逻辑，而且在构造方法中对于 Color 对象创建过于冗余，不符合单一职责的原则，所以将 Color 对象的创建过程通过工厂方法模式来完成。

```java
//创建一个静态工厂类，用于创建 Color 对象
public class StaticColorFactory {
	public static Color getInstance(){
		return new Red();
	}
}
```

```java
public class Car {
	private Color color;
	public Car() {
		//通过工厂来创建 Color
		this.color = StaticColorFactory.getInstance();
	}
}
```

&emsp;&emsp;在将一个对象实例交由 Spring 容器管理时，通常是通过以下 XML 配置：

```xml
<!-- Car -->
<bean id="car" class="myTest.factoryBeanTest.Car">
   <property name="color" ref="red"></property>
</bean>
<!-- Red -->
<bean id="red" class="myTest.factoryBeanTest.Red">
</bean>
```

&emsp;&emsp;但此时，Red 对象实例并不是由 Spring 容器管理，而是由静态工厂方法创建的，此时应该将 XML 配置修改为以下方式：

```xml
<!-- Car -->
<bean id="car" class="myTest.factoryBeanTest.Car">
   <property name="color" ref="red"></property>
</bean>
<!-- 静态工厂类 -->
<bean id="red" class="myTest.factoryBeanTest.StaticColorFactory" factory-method="getInstance">
</bean>
```

&emsp;&emsp;有静态工厂方法，就有非静态工厂方法，区别就是方法不是静态的。

```java
public class ColorFactory {
   public Color getInstance(){
      return new Red();
   }
}
```

&emsp;&emsp;实例工厂方法在 Spring 中 XML 配置略有不同：

```xml
<!-- Car -->
<bean id="car" class="myTest.factoryBeanTest.Car">
   <property name="color" ref="red"></property>
</bean>
<!-- 实例工厂类 -->
<bean id="colorFactory" class="myTest.factoryBeanTest.ColorFactory" >
</bean>
<!-- 实例工厂类的工厂方法 -->
<bean id="red" factory-bean="colorFactory" factory-method="getInstance"></bean>
```

&emsp;&emsp;通过配置可以看到，需要首先在 Spring 中实例化工厂，再通过工厂对象实例化 Red 对象。

&emsp;&emsp;在有了对工厂方法在 Spring 中创建对象实例的认识后，FactoryBean 实际上就是为我们简化这个操作。下面通过 FactoryBean 来创建 Red 对象。

&emsp;&emsp;还是刚才的 ColorFactory，现在让它实现 Spring 提供的 FactoryBean 接口。

```java
public class ColorFactory implements FactoryBean<Color> {
	
	//返回由 FactoryBean 创建的 bean 实例
	//如果 isSingleton 返回 true，则该实例
	//会放到 Spring 容器中单例缓存池中
   @Override
   public Color getObject() throws Exception {
      return new Red();
   }
   
	//返回 bean 类型
   @Override
   public Class<?> getObjectType() {
      return Red.class;
   }
   
	//返回 bean 作用域
   @Override
   public boolean isSingleton() {
      return false;
   }
}
```

```xml
<!-- Car -->
<bean id="car" class="myTest.factoryBeanTest.Car">
   <property name="color" ref="red"></property>
</bean>
<!-- FactoryBean 类 -->
<bean id="red" class="myTest.factoryBeanTest.ColorFactory" >
</bean>
```

&emsp;&emsp;这样就不用像工厂方法那样配置相应的属性，直接按照普通的 Bean 注入即可，当配置文件中的 bean 的 class 配置的是 FactoryBean 的实现类时，通过 getBean() 方法返回的不再是 FactoryBean 实例本身，而是 FactoryBean 中 getObject 方法所返回的对象，如果实在想要获取 ColorFactory 的对象实例，则需要在 Bean 的名称前加入 “&” 即可（“&red”）。

&emsp;&emsp;FactoryBean 接口对于 Spring 框架来说占有重要的地位，Spring 自身就提供了 70 多个 FactoryBean 的实现。他们隐藏了实例化一些复杂 bean 的细节，给上层应用带来了便利，从 Spring 3.0 开始，FactoryBean 开始支持泛型，即接口声明改为 FactoryBean\<T> 的形式。

### 1.2 beanName 解析

&emsp;&emsp;了解了 FactoryBean 的作用之后，再来看对 beanName 的解析过程。

**AbstractBeanFactory.transformedBeanName**

```java
protected String transformedBeanName(String name) {
   return canonicalName(BeanFactoryUtils.transformedBeanName(name));
}
```

**BeanFactoryUtils.transformedBeanName : 去除工厂引用前缀(&)**

```java
public static String transformedBeanName(String name) {
   Assert.notNull(name, "'name' must not be null");
    //如果没有引用前缀直接返回
   if (!name.startsWith(BeanFactory.FACTORY_BEAN_PREFIX)) {
      return name;
   }
    //去除工厂引用前缀(&)，返回
    //如果 name＝"&Red",去除 & 返回 Red
   return transformedBeanNameCache.computeIfAbsent(name, beanName -> {
      do {
         beanName = beanName.substring(BeanFactory.FACTORY_BEAN_PREFIX.length());
      }
      while (beanName.startsWith(BeanFactory.FACTORY_BEAN_PREFIX));
      return beanName;
   });
}
```

**SimpleAliasRegistry.canonicalName : 将别名解析为规范名称**

```java
public String canonicalName(String name) {
   String canonicalName = name;
   // 将别名解析为规范名称,例如别名 A 指向名称为 B 的 bean 则返回B
   // 若别名A 指向别名B ，别名 B 又指向名称为 C 的 bean 则返回C 。
   String resolvedName;
   do {
      resolvedName = this.aliasMap.get(canonicalName);
      if (resolvedName != null) {
         canonicalName = resolvedName;
      }
   }
   while (resolvedName != null);
   return canonicalName;
}
```

## 2. 尝试从缓存中获取 bean 

&emsp;&emsp;单例 bean 在同一个 Spring 容器中只会被创建一次，后续再次获取该 bean 会直接从单例缓存中获取，如果缓存中没有，才会尝试去创建。那么此时就会出现一个问题，当 Spring 实例化并尝试初始化一个 bean，在进行依赖注入时，可能会出现循环依赖问题，也就是说下面的情况：

```java
public class UserA {
     private UserB userB;
 }
```

```java
public class UserB {
     private UserA userA;
 }
```

```xml
<bean name="userA" class="myTest.UserA"  >
    <!-- userA 中引用了 userB -->
   <property name="userB" ref="userB"></property>
</bean>

<bean name="userB" class="myTest.UserB" >
    <!-- userB 中又引用了 userA -->
   <property name="userA" ref="userA"></property>
</bean>
```

&emsp;&emsp;Spring 对上面情况的处理比较复杂，在接下来的源码阅读中，方方面面都有对循环引用的处理。往下看 Bean 加载的第二步，从缓存中获取 bean 。

**DefaultSingletonBeanRegistry.getSingleton : 缓存中获取bean**

```java
public Object getSingleton(String beanName) {
   return getSingleton(beanName, true);
}
```

```java
protected Object getSingleton(String beanName, boolean allowEarlyReference) {
	// Quick check for existing instance without full singleton lock
	// 先从一级缓存寻找
	Object singletonObject = this.singletonObjects.get(beanName);
	if (singletonObject == null && isSingletonCurrentlyInCreation(beanName)) {
		// 如果当前一级缓存没找到且当前 bean 正在创建中
		// 从二级缓存寻找
		singletonObject = this.earlySingletonObjects.get(beanName);
		if (singletonObject == null && allowEarlyReference) {
			// 二级缓存没找到且需要创建引用对象
			synchronized (this.singletonObjects) {
				// Consistent creation of early reference within full singleton lock
				singletonObject = this.singletonObjects.get(beanName);
				if (singletonObject == null) {
					singletonObject = this.earlySingletonObjects.get(beanName);
					if (singletonObject == null) {
						// 从三级缓存获取
						ObjectFactory<?> singletonFactory = this.singletonFactories.get(beanName);
						if (singletonFactory != null) {
							// 调用预先设定的方法，获取早期的 bean 引用，并存入早期单例 bean 缓存中
							singletonObject = singletonFactory.getObject();
							// 加入缓存，earlySingletonObjects 与 singletonFactories 互斥，不能同时存在
							this.earlySingletonObjects.put(beanName, singletonObject);
							this.singletonFactories.remove(beanName);
						}
					}
				}
			}
		}
	}
	return singletonObject;
}
```

&emsp;&emsp;这个方法涉及到上面说的循环依赖的检测以及三级缓存的应用，是循环引用处理的关键步骤。继续往下阅读，接下来是从 BeanFactory 的实例中获取对象。

## 3. 从 BeanFactory 的实例中获取对象

&emsp;&emsp;`getObjectForBeanInstance` 是一个高频使用的方法，无论是从缓存中获得 bean 还是根据不同的 scope 策略加载的 bean。总之，得到 bean 的实例以后要做的第一件事就是调用这个方法来检测正确性，其实就是用于检测当前 bean 是否是 FactoryBean 类型的 bean，如果是，那么需要调用该 bean 对应的 FactoryBean 实例中的 `getObject()` 方法作为返回值。

**AbstractBeanFactory.getObjectForBeanInstance : 判断 bean 是否是 FactoryBean **

```java
protected Object getObjectForBeanInstance(
      Object beanInstance, String name, String beanName, @Nullable RootBeanDefinition mbd) {

   // 1. 判断当前 name 是以 & 为前缀的，注意这里判断的是未解析之前的原始beanName
   // 返回 FactoryBean 本身的实例，因为当前 name 是以 & 为前缀
   if (BeanFactoryUtils.isFactoryDereference(name)) {
      if (beanInstance instanceof NullBean) {
         return beanInstance;
      }
	  //当 bean 实例不是 FactoryBean 类型时报错
      if (!(beanInstance instanceof FactoryBean)) {
         throw new BeanIsNotAFactoryException(beanName, beanInstance.getClass());
      }
      if (mbd != null) {
         mbd.isFactoryBean = true;
      }
      return beanInstance;
   }

   // 2. 经过上面的筛选，现在的 bean 实例可能是一个正常的 bean 也可能是一个 FactoryBean
   // 如果是 FactoryBean 使用它创建实例
   if (!(beanInstance instanceof FactoryBean)) {
	   //不是 FactoryBean 直接返回 bean 实例
      return beanInstance;
   }
	// 3. 获取实例的准备
   Object object = null;
   if (mbd != null) {
      mbd.isFactoryBean = true;
   }
   else {
	   //尝试从FactoryBean创建的单例对象的缓存中获取 bean 实例
      object = getCachedObjectForFactoryBean(beanName);
   }
   if (object == null) {
	   //从 FactoryBean 获取实例
      FactoryBean<?> factory = (FactoryBean<?>) beanInstance;
      if (mbd == null && containsBeanDefinition(beanName)) {
		 // 将存储信息的 GernericBeanDefinition 转换为 RootBeanDefinition，
		 // 如果指定的 bean 是子 bean 会同时合并父类的相关属性
         mbd = getMergedLocalBeanDefinition(beanName);
      }
	  //判断该BeanDefinition是否是用户自定义的而不是程序本身定义的
	  //如果为true则指代那些不是用户自己定义的bean
	  //例如<aop:config>引入的auto-proxying类的bean
	  //false指代用户自己定义的bean
	  //例如如通过xml配置文件配置的bean
      boolean synthetic = (mbd != null && mbd.isSynthetic());
	  //4. 获取实例
      object = getObjectFromFactoryBean(factory, beanName, !synthetic);
   }
   return object;
}
```

&emsp;&emsp;从上面代码来看，这个方法大多是在为实例的获取做准备工作，而真正的获取实例交给了 `getObjectFromFactoryBean` 方法。

**FactoryBeanRegistrySupport.getObjectFromFactoryBean ：单例和多例的处理**

```java
protected Object getObjectFromFactoryBean(FactoryBean<?> factory, String beanName, boolean shouldPostProcess) {
		// 1. 如果 factory 管理的对象是单例且 该 FactoryBean 本身已经被实例化了
		if (factory.isSingleton() && containsSingleton(beanName)) {
			synchronized (getSingletonMutex()) {
				// 先尝试从 factoryBean 单例缓存中获取，检查对应的 bean 是否已经加载过了
				Object object = this.factoryBeanObjectCache.get(beanName);
				if (object == null) {
					//factoryBean 单例缓存中没有 交由 doGetObjectFromFactoryBean 获取
					object = doGetObjectFromFactoryBean(factory, beanName);
					// Only post-process and store if not put there already during getObject() call above
					// (e.g. because of circular reference processing triggered by custom getBean calls)
					// 在上面的 doGetObjectFromFactoryBean 方法调用过程中，如果存在循环引用，
					// 可能会导致当前方法被多次调用,此时当前 beanName 已经被加入了 factoryBeanObjectCache 缓存
					// 可以直接返回结果，另一方面,加了这个判断也可以
					// 防止 postProcessObjectFromFactoryBean 方法被调用多次，保证
					// object 的唯一性。
					Object alreadyThere = this.factoryBeanObjectCache.get(beanName);
					if (alreadyThere != null) {
						object = alreadyThere;
					}
					else {
						if (shouldPostProcess) {
							// 需要做增强处理
							if (isSingletonCurrentlyInCreation(beanName)) {
								// 指定的单例 bean 当前正在创建中
								// Temporarily return non-post-processed object, not storing it yet..
								return object;
							}
							// 创建单例之前的回调方法
							beforeSingletonCreation(beanName);
							try {
								// bean 加载的一个特点就是在 bean 实例化结束都尽量调用 BeanPostProcessor 里面的方法
								// bean 实例化后处理器
								object = postProcessObjectFromFactoryBean(object, beanName);
							}
							catch (Throwable ex) {
								throw new BeanCreationException(beanName,
										"Post-processing of FactoryBean's singleton object failed", ex);
							}
							finally {
								// 创建单例之后的回调方法
								afterSingletonCreation(beanName);
							}
						}
						if (containsSingleton(beanName)) {
							this.factoryBeanObjectCache.put(beanName, object);
						}
					}
				}
				return object;
			}
		}
		else {
			// 不是单例模式则直接调用 doGetObjectFromFactoryBean 方法
			Object object = doGetObjectFromFactoryBean(factory, beanName);
			if (shouldPostProcess) {
				try {
					object = postProcessObjectFromFactoryBean(object, beanName);
				}
				catch (Throwable ex) {
					throw new BeanCreationException(beanName, "Post-processing of FactoryBean's object failed", ex);
				}
			}
			return object;
		}
	}
```

&emsp;&emsp;这里依旧没有真正调用获取 Bean 的方法，而是在下面的 `doGetObjectFromFactoryBean` 中获取。

**FactoryBeanRegistrySupport.doGetObjectFromFactoryBean : 直接调用自定义的 getObject 方法**

```java
private Object doGetObjectFromFactoryBean(FactoryBean<?> factory, String beanName) throws BeanCreationException {
   Object object;
   try {
	   // 1. 如果已经为当前应用程序建立了安全管理器，需要验证权限
      if (System.getSecurityManager() != null) {
         AccessControlContext acc = getAccessControlContext();
         try {
			 // 来自不同的位置的代码可以由一个CodeSource对象描述其位置和签名证书。
			 // 根据代码的CodeSource的不同，代码拥有不同的权限。例如所有Java SDK自带的代码都具有所有的权限。
			 // 用户编写的代码可以自己定制权限（通过SecurityManager）。
			 // 调用doPrivileged的方法可以不管其他方法的权限，而仅仅根据当前方法的权
			 // 限来判断用户是否能访问某个resource。也即可以规定用户只能用某种预定的方式
			 // 来访问其本来不能访问的resource。
            object = AccessController.doPrivileged((PrivilegedExceptionAction<Object>) factory::getObject, acc);
         }
         catch (PrivilegedActionException pae) {
            throw pae.getException();
         }
      }
      else {
		  //直接调用自定义的 getObject
         object = factory.getObject();
      }
   }
   catch (FactoryBeanNotInitializedException ex) {
      throw new BeanCurrentlyInCreationException(beanName, ex.toString());
   }
   catch (Throwable ex) {
      throw new BeanCreationException(beanName, "FactoryBean threw exception on object creation", ex);
   }

   // object 为空的处理
   //如果该FactoryBean还没有初始化完成，并且从FactoryBean中获取的bean实例为空则抛出异常。
   //这是因为在很多情况下，可能由于FactoryBean没有完全初始化完成，
   //导致调用FactoryBean中的getObject方法返回为空。
   if (object == null) {
      if (isSingletonCurrentlyInCreation(beanName)) {
         throw new BeanCurrentlyInCreationException(
               beanName, "FactoryBean which is currently in creation returned null from getObject");
      }
      object = new NullBean();
   }
   return object;
}
```

### 3.1 bean 的 BeanPostProcessor 额外处理

&emsp;&emsp;可以注意到，上面获取到 object 后并没有直接返回，而后面又进入了 postProcessObjectFromFactoryBean 方法。这是 Spring 提供的旨在支持可插拔式的拓展实现，这里是应用所有的已注册的 BeanPostProcessor 让它们有机会对从 FactoryBeans 获得的对象进行额外的增强处理，比如自动代理。

&emsp;&emsp;本身这里默认实现只是按原样返回给定的对象。

```java
protected Object postProcessObjectFromFactoryBean(Object object, String beanName) throws BeansException {
	return object;
}
```

&emsp;&emsp;也可以对其进行拓展，比如子类可以通过覆盖它来实现子类自己的增强处理，比如 AbstractAutowireCapableBeanFactory 中的覆盖实现：

```java
protected Object postProcessObjectFromFactoryBean(Object object, String beanName) {
	return applyBeanPostProcessorsAfterInitialization(object, beanName);
}

public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName)
		throws BeansException {

	Object result = existingBean;
	for (BeanPostProcessor processor : getBeanPostProcessors()) {
		//应用所有的已注册的 BeanPostProcessor 对对象进行额外的增强处理
		Object current = processor.postProcessAfterInitialization(result, beanName);
		if (current == null) {
			return result;
		}
		result = current;
	}
	return result;
}
```

## 4. 合并属性和依赖处理

### 4.1 合并属性

&emsp;&emsp;前面 bean 标签解析的时候，已经将配置文件信息封装为 GenericBeanDefinition 并注册到 BeanDefinitonRegistry 的 beanDefinitionMap 中。

**AbstractBeanFactory.getMergedLocalBeanDefinition ：合并并转换 BeanDefinition**

```java
protected RootBeanDefinition getMergedLocalBeanDefinition(String beanName) throws BeansException {
	//先从已经合并的 BeanDefinition 缓存中获取
   RootBeanDefinition mbd = this.mergedBeanDefinitions.get(beanName);
   if (mbd != null && !mbd.stale) {
      return mbd;
   }
   //从 beanDefinitionMap 中获取 BeanDefinition 并继续调用 getMergedBeanDefinition 方法
   return getMergedBeanDefinition(beanName, getBeanDefinition(beanName));
}

protected RootBeanDefinition getMergedBeanDefinition(String beanName, BeanDefinition bd)
      throws BeanDefinitionStoreException {
   return getMergedBeanDefinition(beanName, bd, null);
}


protected RootBeanDefinition getMergedBeanDefinition(
			String beanName, BeanDefinition bd, @Nullable BeanDefinition containingBd)
			throws BeanDefinitionStoreException {

		synchronized (this.mergedBeanDefinitions) {
			RootBeanDefinition mbd = null;
			RootBeanDefinition previous = null;

			// 先从合并的BeanDefinition缓存中获取
			if (containingBd == null) {
				mbd = this.mergedBeanDefinitions.get(beanName);
			}

			if (mbd == null || mbd.stale) {
				previous = mbd;
				//判断 BeanDefinition 类型
				if (bd.getParentName() == null) {
					//1.parentName 为空为 RootBeanDefinition 直接拷贝一份
					if (bd instanceof RootBeanDefinition) {
						mbd = ((RootBeanDefinition) bd).cloneBeanDefinition();
					}
					else {
						mbd = new RootBeanDefinition(bd);
					}
				}
				else {
					//2.parentName 不为空为 ChildBeanDefinition 需要合并属性
					BeanDefinition pbd;
					try {
						String parentBeanName = transformedBeanName(bd.getParentName());
						if (!beanName.equals(parentBeanName)) {
							//递归调用 getMergedBeanDefinition 检查 parentBeanName 所对应的 BeanDefinition 是否还有 parent
							//如果有则继续递归合并，直至顶层元素
							pbd = getMergedBeanDefinition(parentBeanName);
						}
						else {
							BeanFactory parent = getParentBeanFactory();
							if (parent instanceof ConfigurableBeanFactory) {
								pbd = ((ConfigurableBeanFactory) parent).getMergedBeanDefinition(parentBeanName);
							}
							else {
								throw new NoSuchBeanDefinitionException(parentBeanName,
										"Parent name '" + parentBeanName + "' is equal to bean name '" + beanName +
												"': cannot be resolved without a ConfigurableBeanFactory parent");
							}
						}
					}
					catch (NoSuchBeanDefinitionException ex) {
						throw new BeanDefinitionStoreException(bd.getResourceDescription(), beanName,
								"Could not resolve parent bean definition '" + bd.getParentName() + "'", ex);
					}
					// 使用 parentBeanDefinition 构造一个 RootBeanDefinition
					mbd = new RootBeanDefinition(pbd);
					// 覆盖子类的值
					mbd.overrideFrom(bd);
				}

				// Set default singleton scope, if not configured before.
				// 设置默认的 scope 默认为 单例
				if (!StringUtils.hasLength(mbd.getScope())) {
					mbd.setScope(SCOPE_SINGLETON);
				}

				// A bean contained in a non-singleton bean cannot be a singleton itself.
				// Let's correct this on the fly here, since this might be the result of
				// parent-child merging for the outer bean, in which case the original inner bean
				// definition will not have inherited the merged outer bean's singleton status.
				// 非单例bean中包含的bean本身不能是单例。
				// 让我们在这里即时纠正 因为这可能是外层bean的父子合并的结果
				// 在这种情况下，原始的内部bean定义将不会继承合并的外部bean的单例状态。
				if (containingBd != null && !containingBd.isSingleton() && mbd.isSingleton()) {
					mbd.setScope(containingBd.getScope());
				}

				// Cache the merged bean definition for the time being
				// (it might still get re-merged later on in order to pick up metadata changes)
				// 暂时缓存合并的 BeanDefinition（它可能仍会在稍后重新合并以获取元数据更改）
				if (containingBd == null && isCacheBeanMetadata()) {
					this.mergedBeanDefinitions.put(beanName, mbd);
				}
			}
			if (previous != null) {
				copyRelevantMergedBeanDefinitionCaches(previous, mbd);
			}
			return mbd;
		}
	}
```

&emsp;&emsp;上面方法中的合并逻辑比较清晰，通过判断 parentName 来判定 BeanDefinition 是否有父 bean，来决定使用不同的处理方式，对于RootBeanDefinition 直接拷贝一份即可，如果是 ChildBeanDefinition 则需要再次递归判断父 BeanDefinition 是否是顶层元素并通过 mbd.overrideFrom(bd) 合并属性，合并的逻辑主要就是使用子 bean 的值覆盖父 bean 的值，也就实现了逻辑上的继承关系，即子类有单独设置的属性值则使用子类设置的值，没有的设置的使用父类的值。

### 4.2 依赖闭环的检测

&emsp;&emsp;Spring 在创建 bean 之前，需要保证当前 bean 所依赖的 bean 先初始化。

````java
... ...
String[] dependsOn = mbd.getDependsOn();
if (dependsOn != null) {
	for (String dep: dependsOn) {
		//依赖闭环的检测和处理
		if (isDependent(beanName, dep)) {
			throw new BeanCreationException(mbd.getResourceDescription(), beanName,
				"Circular depends-on relationship between '" + beanName + "' and '" + dep + "'");
		}
		//注册 DependentBean
		registerDependentBean(dep, beanName);
		try {
			//初始化依赖的 bean
			getBean(dep);
		} catch (NoSuchBeanDefinitionException ex) {
			throw new BeanCreationException(mbd.getResourceDescription(), beanName,
				"'" + beanName + "' depends on missing bean '" + dep + "'", ex);
		}
	}
}
... ...
````

**DefaultSingletonBeanRegistry.isDependent ：依赖闭环的检测**

````java
protected boolean isDependent(String beanName, String dependentBeanName) {
		synchronized (this.dependentBeanMap) {
			return isDependent(beanName, dependentBeanName, null);
		}
	}
	
private boolean isDependent(String beanName, String dependentBeanName, @Nullable Set<String> alreadySeen) {
		if (alreadySeen != null && alreadySeen.contains(beanName)) {
			return false;
		}
		String canonicalName = canonicalName(beanName);
		Set<String> dependentBeans = this.dependentBeanMap.get(canonicalName);
		if (dependentBeans == null) {
			return false;
		}
		if (dependentBeans.contains(dependentBeanName)) {
			return true;
		}
		for (String transitiveDependency : dependentBeans) {
			if (alreadySeen == null) {
				alreadySeen = new HashSet<>();
			}
			alreadySeen.add(beanName);
			if (isDependent(transitiveDependency, dependentBeanName, alreadySeen)) {
				return true;
			}
		}
		return false;
	}
````

**DefaultSingletonBeanRegistry.registerDependentBean**

````java
public void registerDependentBean(String beanName, String dependentBeanName) {
		String canonicalName = canonicalName(beanName);

		synchronized (this.dependentBeanMap) {
			Set<String> dependentBeans =
					this.dependentBeanMap.computeIfAbsent(canonicalName, k -> new LinkedHashSet<>(8));
			if (!dependentBeans.add(dependentBeanName)) {
				return;
			}
		}

		synchronized (this.dependenciesForBeanMap) {
			Set<String> dependenciesForBean =
					this.dependenciesForBeanMap.computeIfAbsent(dependentBeanName, k -> new LinkedHashSet<>(8));
			dependenciesForBean.add(canonicalName);
		}
	}
````

&emsp;&emsp;看一下依赖闭环的处理逻辑，注意两个方法的调用参数顺序。

A依赖B：

1. 调用 isDependent，参数为 beanName=A，dependentBeanName=B，alreadySeen=null
2. 检测 dependentBeanMap 能否获取到A，获取不到，dependentBeans 为空，不存在依赖关系。
3. 调用 registerDependentBean，dependentBeanMap 添加 key=B，value=A

B依赖C：

1. 调用 isDependent，参数为 beanName=B，dependentBeanName=C，alreadySeen=null
2. 检测 dependentBeanMap 能否获取到 B，可以获取到，dependentBeans 不为空，已存在A
3. dependentBeans 没有包含 C。
4. 迭代 dependentBeans，目前就存在一个A
5. alreadySeen 添加 B
6. 调用 isDependent，参数为 beanName=A，dependentBeanName=C，alreadySeen={B}
7. 检测 dependentBeanMap 能否获取到A，获取不到，dependentBeans 为空，不存在依赖关系。
8. 调用 registerDependentBean，dependentBeanMap 添加 key=C，value=B

C依赖A：

1. beanName=C，dependentBeanName=A，alreadySeen=null
2. 检测 dependentBeanMap 能否获取到C，可以获取到，dependentBeans 不为空，已存在B
3. dependentBeans 没有包含 A。
4. alreadySeen 添加 C
5. 调用 isDependent，参数为 beanName=B，dependentBeanName=A，alreadySeen={C}
6. 检测 dependentBeanMap 能否获取到 B，可以获取到，dependentBeans 不为空，已存在 A
7. dependentBeans 包含 A，已检测到存在依赖闭环。

## 5. 单例 bean 的创建

&emsp;&emsp;前面看了从缓存中获取单例的过程，如果缓存中不存在，就需要从头开始 bean 的加载过程，接下来就是使用 `getSingleton` 方法的重载来实现 bean 的加载过程。

**DefaultSingletonBeanRegistry.getSingleton**

```java
public Object getSingleton(String beanName, ObjectFactory<?> singletonFactory) {
   Assert.notNull(beanName, "Bean name must not be null");
   //全局变量需要同步
   synchronized (this.singletonObjects) {
	  // 1.仍然是先从缓存中获取，检查对应的 bean 是否已经加载过了
      Object singletonObject = this.singletonObjects.get(beanName);
      if (singletonObject == null) {
         if (this.singletonsCurrentlyInDestruction) {
            throw new BeanCreationNotAllowedException(beanName,
                  "Singleton bean creation not allowed while singletons of this factory are in destruction " +
                  "(Do not request a bean from a BeanFactory in a destroy method implementation!)");
         }
         if (logger.isDebugEnabled()) {
            logger.debug("Creating shared instance of singleton bean '" + beanName + "'");
         }
		 //单例创建之前的回调，将当前 bean 标记为正在创建
         beforeSingletonCreation(beanName);
         boolean newSingleton = false;
         boolean recordSuppressedExceptions = (this.suppressedExceptions == null);
         if (recordSuppressedExceptions) {
            this.suppressedExceptions = new LinkedHashSet<>();
         }
         try {
			 //2.创建实例
            singletonObject = singletonFactory.getObject();
            newSingleton = true;
         }
         ... ...
         finally {
            if (recordSuppressedExceptions) {
               this.suppressedExceptions = null;
            }
			//创建单例后的回调：去除bean的创建状态
            afterSingletonCreation(beanName);
         }
         if (newSingleton) {
			 //3.加入缓存并删除加载 bean 过程中所记录的各种辅助状态。
            addSingleton(beanName, singletonObject);
         }
      }
      return singletonObject;
   }
}
```

&emsp;&emsp;可以看到这里并没有真正进行 bean 的实例化过程，跟着代码继续跟进到 `singletonFactory.getObject()` 中，可以发现是调用上面传入的 `ObjectFactory` 类型的参数的方法 `getObject`，这里使用的 `getObject` 方法实际上是 `AbstractAutowireCapableBeanFactory` 类的 `createBean` 方法，所以，核心的创建过程是调用了 `createBean` 方法。在 `beforeSingletonCreation` 方法中做了一个很重要的操作，就是记录当前 bean 的创建状态，也就是通过 `this.singletonsCurrentlyInCreation.add(beanName)` 将正在创建的 bean 记录在缓存中，方便对循环依赖进行检测。

## 6. 创建 bean 的准备工作

**AbstractAutowireCapableBeanFactory.createBean**

```java
protected Object createBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args)
      throws BeanCreationException {

   if (logger.isTraceEnabled()) {
      logger.trace("Creating instance of bean '" + beanName + "'");
   }
   RootBeanDefinition mbdToUse = mbd;
   //1.解析 bean Class
   Class<?> resolvedClass = resolveBeanClass(mbd, beanName);
   if (resolvedClass != null && !mbd.hasBeanClass() && mbd.getBeanClassName() != null) {
      mbdToUse = new RootBeanDefinition(mbd);
      mbdToUse.setBeanClass(resolvedClass);
   }

   // 2.准备方法覆盖 就是处理前面解析记录的 lookup-method 和 replace-method
   try {
      mbdToUse.prepareMethodOverrides();
   }
   catch (BeanDefinitionValidationException ex) {
      throw new BeanDefinitionStoreException(mbdToUse.getResourceDescription(),
            beanName, "Validation of method overrides failed", ex);
   }

   try {
      // 3.短路操作，让 BeanPostProcessors 有机会返回一个代理而不是目标 bean 实例。
      Object bean = resolveBeforeInstantiation(beanName, mbdToUse);
      if (bean != null) {
         return bean;
      }
   }
   catch (Throwable ex) {
      throw new BeanCreationException(mbdToUse.getResourceDescription(), beanName,
            "BeanPostProcessor before instantiation of bean failed", ex);
   }

   try {
	   //4.开始实例化 bean
      Object beanInstance = doCreateBean(beanName, mbdToUse, args);
      if (logger.isTraceEnabled()) {
         logger.trace("Finished creating instance of bean '" + beanName + "'");
      }
      return beanInstance;
   }
   catch (BeanCreationException | ImplicitlyAppearedSingletonException ex) {
      throw ex;
   }
   catch (Throwable ex) {
      throw new BeanCreationException(
            mbdToUse.getResourceDescription(), beanName, "Unexpected exception during bean creation", ex);
   }
}
```

&emsp;&emsp;在看了这么多源码以后，或多或少也应该能发现一些规律，就是在 Spring 中真正干活的函数一般都是以 do 开头的，这里也一样，创建 bean 的真正函数是 `doCreateBean`，而前面都是创建前的准备工作，下面先看看准备工作都做了些什么。

### 6.1 解析 beanClass

**AbstractBeanFactory.resolveBeanClass**

```java
protected Class < ? > resolveBeanClass(RootBeanDefinition mbd, String beanName, Class < ? > ...typesToMatch)
throws CannotLoadBeanClassException {

	if (mbd.hasBeanClass()) {
		return mbd.getBeanClass();
	}
	//如果已经为当前应用程序建立了安全管理器，需要验证权限
	if (System.getSecurityManager() != null) {
		// 来自不同的位置的代码可以由一个CodeSource对象描述其位置和签名证书。
		// 根据代码的CodeSource的不同，代码拥有不同的权限。例如所有Java SDK自带的代码都具有所有的权限。
		// 用户编写的代码可以自己定制权限（通过SecurityManager）。
		// 调用doPrivileged的方法可以不管其他方法的权限，而仅仅根据当前方法的权
		// 限来判断用户是否能访问某个resource。也即可以规定用户只能用某种预定的方式
		// 来访问其本来不能访问的resource。
		return AccessController.doPrivileged((PrivilegedExceptionAction < Class < ? >> )
			() - > doResolveBeanClass(mbd, typesToMatch), getAccessControlContext());
	} else {
		return doResolveBeanClass(mbd, typesToMatch);
	}
	//错误处理已省略
}
```

**AbstractBeanFactory.doResolveBeanClass ：将 BeanDefinition 中存储的 beanClassName 解析成为 Class 实例**

```java
private Class<?> doResolveBeanClass(RootBeanDefinition mbd, Class<?>... typesToMatch)
			throws ClassNotFoundException {
		// 获取该工厂的加载 bean 用的类加载器
		ClassLoader beanClassLoader = getBeanClassLoader();
		// 初始化动态类加载器为该工厂加载 bean 用的类加载器,如果该工厂有
		// 临时类加载器时，该动态类加载器就是该工厂的临时类加载器
		ClassLoader dynamicLoader = beanClassLoader;
		// 表示 BeanDefinition 的配置的 bean 类名需要被 dynameicLoader 加载的标记
		boolean freshResolve = false;

		if (!ObjectUtils.isEmpty(typesToMatch)) {
			// When just doing type checks (i.e. not creating an actual instance yet),
			// use the specified temporary class loader (e.g. in a weaving scenario).
			// 当仅进行类型检查时（即尚未创建实际实例），使用指定的临时类加载器
			// 获取该工厂的临时类加载器，该临时类加载器专门用于类型匹配
			ClassLoader tempClassLoader = getTempClassLoader();
			if (tempClassLoader != null) {
				// 以该工厂的临时类加载器作为动态类加载器
				dynamicLoader = tempClassLoader;
				// 标记 BeanDefinition 的配置的 bean 类名需要重新被动态类加载器加载
				freshResolve = true;
				// DecoratingClassLoader: 装饰 ClassLoader 的基类,提供对排除的包和类的通用处理
				// 如果临时类加载器是 DecoratingClassLoader 的基类
				if (tempClassLoader instanceof DecoratingClassLoader) {
					// 将临时类加载器强转为 DecoratingClassLoader 实例
					DecoratingClassLoader dcl = (DecoratingClassLoader) tempClassLoader;
					// 对要匹配的类型进行在装饰类加载器中的排除，以交由父 ClassLoader 以常规方式处理
					for (Class<?> typeToMatch : typesToMatch) {
						dcl.excludeClass(typeToMatch.getName());
					}
				}
			}
		}
		//从 BeanDefinition 中取配置的bean类名
		String className = mbd.getBeanClassName();
		if (className != null) {
			// 评估 benaDefinition 中包含的 className,如果 className 是可解析表达式，
			// 会对其进行解析，否则直接返回 className
			Object evaluated = evaluateBeanDefinitionString(className, mbd);
			if (!className.equals(evaluated)) {
				// A dynamically resolved expression, supported as of 4.2...
				// 从 4.2 开始支持动态解析的表达式
				if (evaluated instanceof Class) {
					return (Class<?>) evaluated;
				}
				else if (evaluated instanceof String) {
					className = (String) evaluated;
					freshResolve = true;
				}
				else {
					throw new IllegalStateException("Invalid class name expression result: " + evaluated);
				}
			}
			if (freshResolve) {
				// When resolving against a temporary class loader, exit early in order
				// to avoid storing the resolved Class in the bean definition.
				// 使用临时类加载器继续解析
				if (dynamicLoader != null) {
					try {
						// 使用dynamicLoader加载className对应的类型，并返回加载成功的Class对象
						return dynamicLoader.loadClass(className);
					}
					catch (ClassNotFoundException ex) {
						if (logger.isTraceEnabled()) {
							logger.trace("Could not load class [" + className + "] from " + dynamicLoader + ": " + ex);
						}
					}
				}
				return ClassUtils.forName(className, dynamicLoader);
			}
		}
		// Resolve regularly, caching the result in the BeanDefinition...
		// 使用classLoader加载当前BeanDefinitiond对象所配置的Bean类名的Class对象
		// 每次调用都会重新加载,可通过 AbstractBeanDefinition.getBeanClass 获取缓存
		return mbd.resolveBeanClass(beanClassLoader);
	}
````

### 6.2. 处理override 屬性

**AbstractBeanDefinition.prepareMethodOverrides**

```java
public void prepareMethodOverrides() throws BeanDefinitionValidationException {
   // Check that lookup methods exist and determine their overloaded status.
   // 遍历 methodOverrides 并确定其对应状态
   if (hasMethodOverrides()) {
      getMethodOverrides().getOverrides().forEach(this::prepareMethodOverride);
   }
}
```

**AbstractBeanDefinition.prepareMethodOverride**

```java
protected void prepareMethodOverride(MethodOverride mo) throws BeanDefinitionValidationException {
   int count = ClassUtils.getMethodCountForName(getBeanClass(), mo.getMethodName());
   //methodOverrides 对应的方法不存在
   if (count == 0) {
      throw new BeanDefinitionValidationException(
            "Invalid method override: no method with name '" + mo.getMethodName() +
            "' on class [" + getBeanClassName() + "]");
   }
   else if (count == 1) {
      // methodOverrides 对应的方法唯一确定，不需要再通过检测方法参数来确定方法，设定为false，默认为true
      // Mark override as not overloaded, to avoid the overhead of arg type checking.
      mo.setOverloaded(false);
   }
}
```

&emsp;&emsp;之前反复提到过，在Spring 配置中存在lookup-method 和 replace-method 两个配置功能，而这两个配置的加载其实就是将配置统一存放在 BeanDefinition 中的 methodOverrides 属性里，这两个功能实现原理其实是在 bean 实例化的时候如果检测到存在 methodOverrides 属性，会动态地为当前 bean 生成代理并使用对应的拦截器为 bean 做增强处理，相关逻辑实现在后面的 bean 的实例化部分。

&emsp;&emsp;这里要提到的是，对于方法的匹配来讲，如果一个类中存在若干个重载方法，那么，在函数调用及增强的时候还需要根据参数类型进行匹配，来最终确认当前调用的到底是哪个函数。这里，Spring 将一部分匹配工作在这里完成了，如果当前类中的方法只有一个，那么就将覆盖标记为未重载，这样在后续调用的时候便可以直接使用找到的方法，而不需要进行方法的参数匹配验证，而且还可以提前对方法存在性进行验证。

### 6.3. 实例化的前、后置处理器

&emsp;&emsp;在真正调用 `doCreate` 方法创建 bean 的实例前使用了这样一个方法 `resolveBeforeInstantiation` 对 `BeanDefinigiton` 中的属性做些前置处理，紧接着是一个短路操作，经过 `resolveBeforeInstantiation` 方法处理后的结果如果不为空，那么会直接略过后续的 bean 的创建而直接返回结果。

**AbstractAutowireCapableBeanFactory.resolveBeforeInstantiation**

```java
protected Object resolveBeforeInstantiation(String beanName, RootBeanDefinition mbd) {
   Object bean = null;
   if (!Boolean.FALSE.equals(mbd.beforeInstantiationResolved)) {
      // Make sure bean class is actually resolved at this point.
      if (!mbd.isSynthetic() && hasInstantiationAwareBeanPostProcessors()) {
         Class<?> targetType = determineTargetType(beanName, mbd);
         if (targetType != null) {
            // 实例化之前的处理
            bean = applyBeanPostProcessorsBeforeInstantiation(targetType, beanName);
            if (bean != null) {
               // 实例化之后的处理
               bean = applyBeanPostProcessorsAfterInitialization(bean, beanName);
            }
         }
      }
      mbd.beforeInstantiationResolved = (bean != null);
   }
   return bean;
}
```

#### 6.3.1 实例化之前的处理

&emsp;&emsp;bean 的实例化前调用，给子类一个修改 BeanDefinition 的机会，也就是说当程序经过这个方法后，bean 可能已经不是之前的 bean 了，而是或许成为了一个经过处理的代理 bean，可能是通过 cglib 生成的，也可能是通过其他技术生成的。

**AbstractAutowireCapableBeanFactory.applyBeanPostProcessorsBeforeInstantiation**

```java
protected Object applyBeanPostProcessorsBeforeInstantiation(Class<?> beanClass, String beanName) {
   for (InstantiationAwareBeanPostProcessor bp : getBeanPostProcessorCache().instantiationAware) {
      Object result = bp.postProcessBeforeInstantiation(beanClass, beanName);
      if (result != null) {
         return result;
      }
   }
   return null;
}
```

#### 6.3.2 实例化之后的处理

&emsp;&emsp;这里可以对已经创建的 bean 实例进行再加工操作，具体流程上面已经说过，不在赘述。

**AbstractAutowireCapableBeanFactory.applyBeanPostProcessorsAfterInitialization**

```java
public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName)
      throws BeansException {

   Object result = existingBean;
   for (BeanPostProcessor processor : getBeanPostProcessors()) {
      Object current = processor.postProcessAfterInitialization(result, beanName);
      if (current == null) {
         return result;
      }
      result = current;
   }
   return result;
}
```

