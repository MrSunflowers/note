# Spring源码笔记一：配置文件的封装和 XmlBeanFactory 结构


## 1. 配置文件的封装


### 1.1 Resource


&emsp;&emsp;在Java 中，将不同来源的资源抽象成 URL ，通过注册不同的 handler ( URLStreamHandler )  来处理不同来源的资源的读取逻辑，一般 handler 类型使用不同的前缀（协议， Protoc ol ）来识别，如“file ：” “ “http ：“ ” jar ：“ 等，然而 URL 没有默认定义相对 Classpath 或ServletContext 等资源的 handler ，虽然可以注册自己的 URLStreamHander 解析特定的 URL 前缀（协议 ）， 比如 “Classpath:”，然而这需要了解URL的实现机制，而 URL 也也没有提供基本的方法，如检查当前资源是否存在、检查当前资源是否可读等方法，因此 Spring 内部使用到的资源实现了自己的抽象结构 Resource 接口封装底层资源。

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/spring/202111011544066.png)

【常用方法】

| 方法                                | 说明                                                         |
| ----------------------------------- | ------------------------------------------------------------ |
| boolean exists();                   | 确定该资源是否以物理形式实际存在                             |
| boolean isReadable()                | 指示是否可以通过getInputStream()读取此资源的非空内容         |
| boolean isOpen()                    | 指示此资源是否表示具有打开流的句柄。 如果为true ，则 InputStream 不能多次读取，必须读取并关闭以避免资源泄漏，默认为false |
| boolean isFile()                    | 确定此资源是否代表文件系统中的文件，默认为false 。           |
| getURL()                            | 提供资源到URL的类型转换                                      |
| getURI()                            | 提供资源到URI的类型转换                                      |
| getFile()                           | 提供资源到File的类型转换                                     |
| lastModified()                      | 确定此资源的最后修改时间戳                                   |
| createRelative(String relativePath) | 创建与此资源相关的资源                                       |
| getFilename()                       | 获取文件名                                                   |
| getDescription()                    | 返回此资源的描述，用于在使用资源时输出错误信息               |

&emsp;&emsp;对不同来源的资源文件都有相应的 Resource 实现：文件资源（FileSystemResource）、Classpath资源（ClassPathResource）、URL资源（UrlResource ）等。

### 1.2 InputStreamSource 

&emsp;&emsp;InputStreamSource 接口中只有一个方法，getlnputStream() ，它封装了任何能够返回InputStream的类。

| 方法             | 说明                     |
| ---------------- | ------------------------ |
| getInputStream() | 每次调用都会创建一个新流 |

### 1.3 ClassPathResource

&emsp;&emsp;ClassPathResource 是 Resource 接口的实现类之一，它使用使用给定的ClassLoader或给定的 Class 来加载资源。在方法构造时将内部参数初始化，包括资源路径path，类加载器 classLoader 和用于加载资源的类 clazz，根据构造方法的不同选择不同的初始化方式。
​	
&emsp;&emsp;至于接口中的方法实现是比较简单的，以 `getInputStream()` 方法为例，看看它的具体实现：

**ClassPathResource.getInputStream**

```java
public InputStream getInputStream() throws IOException {
		InputStream is;
		if (this.clazz != null) {
			is = this.clazz.getResourceAsStream(this.path);
		}
		else if (this.classLoader != null) {
			is = this.classLoader.getResourceAsStream(this.path);
		}
		else {
			is = ClassLoader.getSystemResourceAsStream(this.path);
		}
		if (is == null) {
			throw new FileNotFoundException(getDescription() + " cannot be opened because it does not exist");
		}
		return is;
	}
```

在 ClassPathResource 中的实现中使用 class 或者 classLoader 直接获取。

**FileSystemResource.getInputStream**

```java
public InputStream getInputStream() throws IOException {
		try {
			return Files.newInputStream(this.filePath);
		}
		catch (NoSuchFileException ex) {
			throw new FileNotFoundException(ex.getMessage());
		}
	}
```

在 FileSystemResource 中实现则更简单，直接使用 Files 来获取。

&emsp;&emsp;在日常的开发工作中，资源文件的加载也是比较常用的，就可以使用 Spring 提供的类来加载文件，得到 inputStream 后，就可以按照以前的开发方式继续开发。

## 2. XmlBeanFactory 功能结构解析

在进行 xml 文件的解析之前，先需要一个容器来存储和管理解析到的内容，它就是 BeanFactory 。

### 2.1 BeanFactory

&emsp;&emsp;BeanFactory 是一个接口，它提供了 IOC 容器最基本的形式，给具体的 IOC 容器的实现提供了规范，BeanFactory 是个 Factory，也就是 IOC 容器或对象工厂，在 Spring 中，所有的 Bean 都是由 BeanFactory (也就是IOC容器)来进行管理的。

【常用方法】

| 方法                              | 描述                                               |
| --------------------------------- | -------------------------------------------------- |
| getBean                           | 返回指定 bean 的一个实例，它可以是单例的或多例的。 |
| getBeanProvider                   | 返回指定 bean 的提供者                             |
| boolean containsBean(String name) | 这个 bean 工厂是否包含具有给定名称的 bean          |
| boolean isSingleton(String name)  | 返回这个 bean 是否是单例                           |
| boolean isPrototype(String name)  | 返回这个 bean 是否是多例                           |
| boolean isTypeMatch()             | 检查具有给定名称的 bean 是否与指定的类型匹配       |
| Class<?> getType()                | 返回给定名称的 bean 的类型                         |
| String[] getAliases(String name)  | 返回给定 bean 名称的别名                           |

### 2.2 BeanDefinition

&emsp;&emsp;BeanDefintion 定义了 Bean 在 IoC 容器内的存储的基本数据结构，BeanDefinition 描述了一个 bean 的实例，包括属性值，构造方法参数值和继承自它的类的更多信息，在 Spring 容器启动的过程中，会将 Bean 解析成 Spring 内部的 BeanDefinition 结构存储。BeanDefintion 接口继承了AttributeAccessor 接口和 BeanMetadataElement 接口。

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/spring/202111011612195.png)


#### 2.2.1 AttributeAccessor 

&emsp;&emsp;AttributeAccessor 接口定义了属性（key-value）修改或者获取。AttributeAccessor 为 BeanDefinition的父接口，使 BeanDefinition 具有处理属性的功能

```java
public interface AttributeAccessor {

    /**
     * 设置属性信息
     */
    void setAttribute(String name, @Nullable Object value);

    /**
     * 根据名称获取属性值 
     */
    @Nullable
    Object getAttribute(String name);

    /**
     * 根据名称移除属性
     */
    @Nullable
    Object removeAttribute(String name);

    /**
     * 判断是否函数此名称的属性
     */
    boolean hasAttribute(String name);

    /**
     * 返回所有属性
     * Return the names of all attributes.
     */
    String[] attributeNames();

}
```

#### 2.2.2 BeanMetadataElement

&emsp;&emsp;BeanMetadataElement 接口提供了一个 getResource() 方法,用来传输一个可配置的源对象，BeanDefinition 继承了BeanMetadataElement，说明BeanDefinition同样可以持有 Bean 元数据元素。

```java
public interface BeanMetadataElement {

    /**
     * 获取可配置的源对象
     * Return the configuration source {@code Object} for this metadata element
     * (may be {@code null}).
     */
    @Nullable
    Object getSource();

}
```

【BeanDefinition 常用属性】

| 属性名称            | 描述                           |
| ------------------- | ------------------------------ |
| SCOPE_SINGLETON     | 描述标准单例标识符 “singleton” |
| SCOPE_PROTOTYPE     | 描述标准原型标识符 “prototype” |
| ROLE_APPLICATION    | 对应于用户定义的 bean。        |
| ROLE_SUPPORT        | 对应于来源于配置文件的 Bean。  |
| ROLE_INFRASTRUCTURE | 对应于Spring 内部的 Bean。     |

【BeanDefinition常用方法】

| 方法名称                                                  | 描述                                                         |
| --------------------------------------------------------- | ------------------------------------------------------------ |
| void setParentName(String parentName)                     | 设置此 bean 定义的父定义的名称（如果有）。                   |
| String getParentName()                                    | 返回此 bean 定义的父定义的名称（如果有）。                   |
| void setBeanClassName(String beanClassName);              | 设置bean的类的名称。                                         |
| String getBeanClassName()                                 | 返回此 bean 定义的当前 bean 类名称。                         |
| void setScope(String scope);                              | 设置该bean的目标范围                                         |
| String getScope()                                         | 返回此 bean 的当前目标范围的名称，如果尚不知道，则返回null 。 |
| void setLazyInit(boolean lazyInit)                        | 设置这个 bean 是否是懒加载，如果为false ，则 bean 将在启动时实例化。 |
| boolean isLazyInit()                                      | 返回这个 bean 是否是懒加载，仅适用于单例 bean。              |
| void setDependsOn(String... dependsOn)                    | 设置当前bean是否需要在其他bean初始化之后初始化，bean 工厂将保证这些 bean 首先被初始化。 |
| String[] getDependsOn();                                  | 返回此 bean 所依赖的 bean 名称。                             |
| void setAutowireCandidate(boolean autowireCandidate);     | 设置此 bean 是否是自动注入到其他 bean 的候选者。             |
| boolean isAutowireCandidate();                            | 返回此 bean 是否是自动注入到其他 bean 的候选者。             |
| void setPrimary(boolean primary);                         | 设置此 bean 是否是主要的自动注入候选者。                     |
| boolean isPrimary();                                      | 返回此 bean 是否是主要的自动注入候选者。                     |
| void setFactoryBeanName(String factoryBeanName);          | 指定要使用的工厂 bean（如果有）。                            |
| String getFactoryBeanName();                              | 返回工厂 bean 名称（如果有）                                 |
| void setFactoryMethodName(String factoryMethodName);      | 指定工厂方法（如果有）。                                     |
| void setFactoryMethodName(String factoryMethodName);      | 返回工厂方法（如果有）                                       |
| ConstructorArgumentValues getConstructorArgumentValues(); | 返回此 bean 的构造函数参数值。                               |
| boolean hasConstructorArgumentValues()                    | 返回此bean是否有构造函数参数值                               |
| MutablePropertyValues getPropertyValues();                | 返回bean 实例的属性值                                        |
| boolean hasPropertyValues()                               | 返回是否有 bean 实例的属性值                                 |
| void setInitMethodName(String initMethodName);            | 设置初始化方法的名称。                                       |
| String getInitMethodName();                               | 返回初始化方法的名称。                                       |
| void setDestroyMethodName(String destroyMethodName);      | 设置销毁方法的名称。                                         |
| String getDestroyMethodName();                            | 返回销毁方法的名称                                           |
| void setRole(int role);                                   | 为这个BeanDefinition设置角色提示                             |
| int getRole();                                            | 获取此BeanDefinition的角色提示。                             |
| void setDescription(String description);                  | 设置该bean的描述                                             |
| String getDescription();                                  | 返回此 bean 的描述                                           |
| ResolvableType getResolvableType();                       | 返回此 bean 定义的可解析类型。                               |
| boolean isSingleton();                                    | 返回这是否是一个Singleton ，在所有调用中返回一个单一的共享实例。 |
| boolean isPrototype();                                    | 返回这是否为Prototype ，并为每次调用返回一个独立的实例       |
| boolean isAbstract();                                     | 返回此 bean 是否“抽象”，即不打算实例化。                     |
| String getResourceDescription();                          | 返回此 bean 定义来自的资源的描述（为了在出现错误时显示上下文）。 |
| BeanDefinition getOriginatingBeanDefinition();            | 返回原始 BeanDefinition，如果没有则返回null 。               |

可以看出 BeanDefinition 提供了对 bean 属性的定制化方法，也为后续的操作提供了灵活的定制化基础。

### 2.3 XmlBeanFactory

&emsp;&emsp;在查看具体的 xml 解析工作之前，需要先了解一下当前 BeanFactory 容器的结构和功能。


![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/spring/202111011613679.png)

&emsp;&emsp;通过上面简单的类图可以发现，当前的 XmlBeanFactory 不仅仅实现了 BeanFactory 接口，也实现了 SingletonBeanRegistry 和 BeanDefinitionRegistry 接口。也就是说当前的 XmlBeanFactory 既是 BeanFactory 容器也是当前容器的 SingletonBeanRegistry 和 BeanDefinitionRegistry。

#### 2.3.1 BeanDefinitionRegistry

&emsp;&emsp;BeanDefinitionRegistry 是持有 BeanDefinition 的注册表的接口，它是 Spring 中 BeanFactory 容器中唯一封装 BeanDefinition 的注册表的接口 ，它定义了关于 BeanDefinition 的注册、移除、查询等一系列的操作。BeanDefinitionRegistry 继承了 AliasRegistry 接口，AliasRegistry 接口封装了用于管理 bean 的别名的方法，包括注册、移除、判断等方法，BeanDefinitionRegistry 通过继承 AliasRegistry 接口也具有了管理别名的功能。

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/spring/202111011616151.png)

【常用方法介绍】

| 方法                                                         | 描述                                                         |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) | 使用名称注册一个BeanDefinition到注册表中                     |
| void removeBeanDefinition(String beanName)                   | 删除给定名称的 BeanDefinition                                |
| BeanDefinition getBeanDefinition(String beanName)            | 返回给定 bean 名称的 BeanDefinition                          |
| boolean containsBeanDefinition(String beanName)              | 检查此注册表是否包含具有给定名称的 BeanDefinition            |
| String[] getBeanDefinitionNames();                           | 返回此注册表中定义的所有 bean 的名称                         |
| int getBeanDefinitionCount();                                | 返回注册表中定义的 bean 数                                   |
| boolean isBeanNameInUse(String beanName);                    | 确定给定的 bean 名称是否已在此注册表中使用，即是否有本地 bean 或别名注册在此名称下 |
| void registerAlias(String name, String alias)                | 注册别名                                                     |
| void removeAlias(String alias);                              | 从此注册表中删除指定的别名                                   |
| boolean isAlias(String name);                                | 确定给定的名称是否定义为别名（而不是实际注册的组件的名称）   |
| String[] getAliases(String name);                            | 返回给定名称的别名（如果已定义）                             |

#### 2.3.2 SingletonBeanRegistry

&emsp;&emsp;SingletonBeanRegistry 是单例 Bean 的注册中心，此接口是针对 Spring 中的单例 Bean 设计的。提供了统一访问单例 Bean 的功能。

【常用方法】

| 方法                                                         | 描述                                                         |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| void registerSingleton(String beanName, Object singletonObject) | 在 bean 注册表中将给定的现有对象注册为单例，给定的实例是**完全初始化**的 |
| Object getSingleton(String beanName);                        | 返回在给定名称下注册的（原始）单例对象，只返回已经**实例化**的单例 |
| boolean containsSingleton(String beanName);                  | 检查此注册表是否包含具有给定名称的单例实例。                 |
| String[] getSingletonNames();                                | 返回在此注册表中注册的单例 bean 的名称。                     |
| int getSingletonCount();                                     | 返回在此注册表中注册的单例 bean 的数量                       |



## 3. XmlBeanFactory 核心类介绍

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/spring/202111011621516.png)

&emsp;&emsp;通过上面的介绍，现在对 BeanFactory 的结构和功能也有了简单的了解，在正式开始源码分析之前，需要结合上面的类图再了解一下在 XmlBeanFactory 体系中核心的几个类。

### 3.1 SimpleAliasRegistry

&emsp;&emsp;SimpleAliasRegistry 作为 AliasRegistry 接口的简单实现，主要使用 map 作为 alias (别名) 的缓存，并添加了对于别名的简单操作方法，比如是否允许别名覆盖、确定别名是否已经被注册等方法，这里只是提供了简单的实现，可以在子类中覆盖以提供更加灵活的功能。

### 3.2 DefaultSingletonBeanRegistry

&emsp;&emsp;DefaultSingletonBeanRegistry 继承 SimpleAliasRegistry 并对 SingletonBeanRegistry 接口定义的各函数进行了实现，这里定义了几个非常重要的属性，也就是 Spring 容器中的三级缓存 ，在后面会用到：

```java
	//一级缓存 单例对象缓存 存储初始化以后的完整的 bean 
	/** Cache of singleton objects: bean name to bean instance. */
	private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);
	//三级缓存 存储 lambda 表达式 ，未实例化的 bean
	/** Cache of singleton factories: bean name to ObjectFactory. */
	private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<>(16);
	//二级缓存 存放实例化 但未初始化的 bean 半成品对象
	/** Cache of early singleton objects: bean name to bean instance. */
	private final Map<String, Object> earlySingletonObjects = new ConcurrentHashMap<>(16);
```

### 3.3 FactoryBeanRegistrySupport

&emsp;&emsp;FactoryBeanRegistrySupport 继承了 DefaultSingletonBeanRegistry 类 ，在 DefaultSingletonBeanRegistry 的基础上增加了对 FactoryBean 的特殊处理功能。

### 3.4 AbstractBeanFactory

&emsp;&emsp;AbstractBeanFactory 综合了 FactoryBeanRegistrySupport 和 ConfigurableBeanFactory (BeanFactory的配置接口) 的功能。

### 3.5 AbstractAutowireCapableBeanFactory

&emsp;&emsp;AbstractAutowireCapableBeanFactory 综合 AbstractBeanFactory 并对 AutowireCapableBeanFactory 接口进行实现。有两个属性需要注意：

```java
	// 是否自动解析循环引用
	/** Whether to automatically try to resolve circular references between beans. */
	private boolean allowCircularReferences = true;
	// 是否允许在循环引用的情况下注入原始bean实例，即使它在后面的处理中被包装
	/**
	 * Whether to resort to injecting a raw bean instance in case of circular reference,
	 * even if the injected bean eventually got wrapped.
	 */
	private boolean allowRawInjectionDespiteWrapping = false;
```

构造方法

```java
/**
	 * Create a new AbstractAutowireCapableBeanFactory.
	 */
	public AbstractAutowireCapableBeanFactory() {
		super();
		ignoreDependencyInterface(BeanNameAware.class);
		ignoreDependencyInterface(BeanFactoryAware.class);
		ignoreDependencyInterface(BeanClassLoaderAware.class);
	}
```

&emsp;&emsp;有时候接口中会有 setXxx 方法，它不需要被自动注入，ignoreDependencyInterface  的主要功能是存储需要忽略的接口，在自动装配的时候会用到。

### 3.6 DefaultlistableBeanFactory 

&emsp;&emsp;XmlBeanFactory 继承自 DefaultlistableBeanFactory 而 DefaultlistableBeanFactory 是整个 bean 加载的核心部分，是 Spring 注册及加载 bean 的默认实现，而对于 XmlBeanFactory 与 DefaultlistableBeanFactory 不同的地方其实是在 XmlBeanFactory 中使用了自定义的 XML 读取器 XmlBeanDefinitionReader ，实现了个性化的 BeanDefinitionReader 读取。

```java
	//是否允许bean覆盖
	/** Whether to allow re-registration of a different definition with the same name. */
	private boolean allowBeanDefinitionOverriding = true;
```

## 4. XmlBeanDefinitionReader 

&emsp;&emsp;上面简单介绍了 XmlBeanFactory 体系的核心类，下面回到 XmlBeanFactory 类的构造方法中。前面说过在 XmlBeanFactory 中使用了自定义的 XML 读取器 XmlBeanDefinitionReader ，XmlBeanDefinitionReader 继承自 AbstractBeanDefinitionReader ，AbstractBeanDefinitionReader 实现了 BeanDefinitionReader 和 EnvironmentCapable 接口。

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/spring/202111011627544.png)

### 4.1 BeanDefinitionReader 

&emsp;&emsp;BeanDefinitionReader 的作用是读取 Spring 配置文件中的内容，将其转换为 IoC 容器内部的数据结构：BeanDefinition。

### 4.2 EnvironmentCapable 

&emsp;&emsp;EnvironmentCapable 中只定义了一个 getEnvironment() 方法，封装了获取系统属性和环境变量的入口，可以在平时的开发中，用来获取系统属性和环境变量，它实现也是很简单的。

```java
protected void customizePropertySources(MutablePropertySources propertySources) {
		propertySources.addLast(
				new PropertiesPropertySource(SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME, getSystemProperties()));
		propertySources.addLast(
				new SystemEnvironmentPropertySource(SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, getSystemEnvironment()));
	}
```

进入方法，获取系统属性

```java
public Map<String, Object> getSystemProperties() {
		try {
			return (Map) System.getProperties();
		}
		... ...
	}
```

获取环境变量

```java
public Map<String, Object> getSystemEnvironment() {
		... ...
		try {
			return (Map) System.getenv();
		}
		... ...
	}
```

&emsp;&emsp;继续回到 XmlBeanFactory 中来，在这里可以看到，在实例化 XmlBeanDefinitionReader 的时候，将当前容器 XmlBeanFactory 作为参数传入，前面已经知道，当前的 XmlBeanFactory 就是当前容器的 BeanDefinitionRegistry ,而 BeanDefinitionRegistry 接口一次只能注册一个 BeanDefinition，而且只能自己构造 BeanDefinition 类来注册。BeanDefinitionReader 解决了这些问题，它可以把 Resources 转化为多个 BeanDefinition 并注册到 BeanDefinitionRegistry 中。

```java
public XmlBeanFactory(Resource resource, BeanFactory parentBeanFactory) throws BeansException {
		super(parentBeanFactory);
		this.reader.loadBeanDefinitions(resource);
	}
```

&emsp;&emsp;这样一来，this.reader.loadBeanDefinitions(resource) 就变成了继续阅读的重点，本文主要大致介绍了 Spring 中一些基础的类的作用和概念，继续阅读源码也会遇到这些，这里先有个印象，后面看到能想起来就行。
