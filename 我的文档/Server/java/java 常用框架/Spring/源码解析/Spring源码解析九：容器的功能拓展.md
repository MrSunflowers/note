# Spring源码解析九：容器的功能拓展

&emsp;&emsp;除了 XmlBeanFactory 可以用于加载 bean 之外，Spring 中还提供了另一个 ApplicationContext 接口用于拓展 BeanFactory 中现有的功能，ApplicationContext 包含了 BeanFactory 的所有功能，通常建议优先使用，除非在一些有特殊限制的场合，比如字节长度对内存有很大的影响时(Applet)。绝大多数情况下，建议使用 ApplicationContext。

&emsp;&emsp;使用 ApplicationContext 加载 xml：

```java
ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("myTestResources/applicationContext.xml");
UserA userA = (UserA)context.getBean("userA");
UserB userB = (UserB)context.getBean("userB");
```

&emsp;&emsp;同样，现在以 ClassPathXmlApplicationContext 作为切入点进行分析。

**【构造函数】**

```java
public ClassPathXmlApplicationContext(String configLocation) throws BeansException {
	this(new String[] {configLocation}, true, null);
}

public ClassPathXmlApplicationContext(
		String[] configLocations, boolean refresh, @Nullable ApplicationContext parent)
		throws BeansException {

	super(parent);
	setConfigLocations(configLocations);
	if (refresh) {
		refresh();
	}
}
```

&emsp;&emsp;ClassPathXmlApplicationContext 中支持将配置文件的路径以数组方式传入，ClassPathXmlApplicationContext 可以对数组进行解析并加载。而对于解析和功能实现都在 `refresh()` 方法中实现。

## 1. 设置配置路径

**AbstractRefreshableConfigApplicationContext.setConfigLocations**

```java
public void setConfigLocations(@Nullable String... locations) {
	if (locations != null) {
		Assert.noNullElements(locations, "Config locations must not be null");
		this.configLocations = new String[locations.length];
		for (int i = 0; i < locations.length; i++) {
			this.configLocations[i] = resolvePath(locations[i]).trim();
		}
	}
	else {
		this.configLocations = null;
	}
}
```

&emsp;&emsp;这里主要用于解析给定的路径数组，当然，数组中如果包含特殊占位符，例如 ${userName} ,那么在 resolvePath 方法中将统一搜寻匹配的系统变量并替换，寻找和替换的具体操作前面已经讲过了。

## 2. 拓展功能

&emsp;&emsp;在设置路径之后，便可以根据路径做配置文件的解析以及各种功能的实现了，其中 refresh 几乎包含了 ApplicationContext 的所有功能。

**AbstractApplicationContext.refresh**

```java

```

## 3. 刷新前的准备工作

**AbstractApplicationContext.prepareRefresh**

```java
protected void prepareRefresh() {
	// Switch to active.
	this.startupDate = System.currentTimeMillis();
	this.closed.set(false);
	this.active.set(true);

	if (logger.isDebugEnabled()) {
		if (logger.isTraceEnabled()) {
			logger.trace("Refreshing " + this);
		}
		else {
			logger.debug("Refreshing " + getDisplayName());
		}
	}

	// Initialize any placeholder property sources in the context environment.
	// 1. 预留给子类用于在初始化时对系统属性可以额外处理
	initPropertySources();

	// Validate that all properties marked as required are resolvable:
	// see ConfigurablePropertyResolver#setRequiredProperties
	// 2. 验证所有必须的属性是否都已经正确加载
	getEnvironment().validateRequiredProperties();

	// Store pre-refresh ApplicationListeners...
	if (this.earlyApplicationListeners == null) {
		this.earlyApplicationListeners = new LinkedHashSet<>(this.applicationListeners);
	}
	else {
		// Reset local application listeners to pre-refresh state.
		this.applicationListeners.clear();
		this.applicationListeners.addAll(this.earlyApplicationListeners);
	}

	// Allow for the collection of early ApplicationEvents,
	// to be published once the multicaster is available...
	this.earlyApplicationEvents = new LinkedHashSet<>();
}
```

### 3.1 自定义 ApplicationContext 实现属性校验

&emsp;&emsp;有时候在我们的项目运行过程中，所需要的某个属性（例如 user.name），是从系统环境中获得的，而这个属性对我们的项目很重要，没有它不行，这时 Spring 提供了一个可以验证项目的运行环境所必要的系统属性是否都已经加载完成的功能，我们只需要继承当前所使用的 ClassPathXmlApplicationContext 并在其中进行拓展即可。

```java
public class MyClassPathXmlApplicationContext extends ClassPathXmlApplicationContext {
	
	public MyClassPathXmlApplicationContext(String... configLocations) throws BeansException {
		super(configLocations);
	}
	
	@Override
	protected void initPropertySources() {
		// 添加需要验证的系统属性
		getEnvironment().setRequiredProperties("user.name");
	}
}

【main方法】

public static void main(String[] args) {
	//用自定义的 MyClassPathXmlApplicationContext 替换原来的 ClassPathXmlApplicationContext
	MyClassPathXmlApplicationContext context = new MyClassPathXmlApplicationContext("myTestResources/applicationContext.xml");
	UserA userA = (UserA)context.getBean("userA");
}
```

&emsp;&emsp;实现也很简单，就是需要验证的属性为 null 就抛出异常。

## 4. 加载 BeanFactory

&emsp;&emsp;前面说过 ApplicationContext 是对 BeanFactory 的功能上的拓展，它包含 BeanFactory 的所有功能并在此基础上做了大量的拓展，obtainFreshBeanFactory 就是实现 BeanFactory 的函数，经过该方法后 ApplicationContext 就已经拥有了 BeanFactory 的全部功能。 

**AbstractApplicationContext.obtainFreshBeanFactory**

```java
protected ConfigurableListableBeanFactory obtainFreshBeanFactory() {
	// 1. 初始化 BeanFactory，并进行文件读取
	refreshBeanFactory();
	// 2. 返回当前的 BeanFactory
	return getBeanFactory();
}
```

**AbstractRefreshableApplicationContext.refreshBeanFactory**

```java
protected final void refreshBeanFactory() throws BeansException {
	// 1. 如果当前已经存在一个 BeanFactory 销毁它
	if (hasBeanFactory()) {
		destroyBeans();
		closeBeanFactory();
	}
	try {
		// 2. 创建一个新的 DefaultListableBeanFactory 包括初始化 BeanFactory 中的各种属性
		DefaultListableBeanFactory beanFactory = createBeanFactory();
		beanFactory.setSerializationId(getId());
		// 3. 设置循环引用处理方式 以及 是否允许同名 BeanDefinition 覆盖
		customizeBeanFactory(beanFactory);
		// 4. 加载解析 xml 文件
		loadBeanDefinitions(beanFactory);
		this.beanFactory = beanFactory;
	}
	catch (IOException ex) {
		throw new ApplicationContextException("I/O error parsing bean definition source for " + getDisplayName(), ex);
	}
}
```

### 4.1 自定义设置循环引用处理方式

**AbstractRefreshableApplicationContext.customizeBeanFactory**

```java
protected void customizeBeanFactory(DefaultListableBeanFactory beanFactory) {
		if (this.allowBeanDefinitionOverriding != null) {
			beanFactory.setAllowBeanDefinitionOverriding(this.allowBeanDefinitionOverriding);
		}
		if (this.allowCircularReferences != null) {
			beanFactory.setAllowCircularReferences(this.allowCircularReferences);
		}
	}
```

&emsp;&emsp;这里只做了判空处理，如果不为空则进行设置，而对其进行设置的代码需要用户继承来自定义实现。还是以刚才的 MyClassPathXmlApplicationContext 作为实现。

**MyClassPathXmlApplicationContext.customizeBeanFactory**

```java
@Override
protected void customizeBeanFactory(DefaultListableBeanFactory beanFactory) {
	super.setAllowBeanDefinitionOverriding(false);
	super.setAllowCircularReferences(false);
	super.customizeBeanFactory(beanFactory);
}
```

&emsp;&emsp;这样就实现了由用户自定义设置属性。

### 4.2 加载解析 xml 文件

**AbstractXmlApplicationContext.loadBeanDefinitions**

```java
protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws BeansException, IOException {
	// Create a new XmlBeanDefinitionReader for the given BeanFactory.
	// 为给定的 BeanFactory 创建一个新的 XmlBeanDefinitionReader。
	XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);

	// Configure the bean definition reader with this context's
	// resource loading environment.
	// 为 beanDefinitionReader 设置初始化属性
	beanDefinitionReader.setEnvironment(this.getEnvironment());
	beanDefinitionReader.setResourceLoader(this);
	beanDefinitionReader.setEntityResolver(new ResourceEntityResolver(this));

	// Allow a subclass to provide custom initialization of the reader,
	// then proceed with actually loading the bean definitions.
	// 初始化 BeanDefinitionReader ，支持子类覆盖拓展
	initBeanDefinitionReader(beanDefinitionReader);
	// 加载 BeanDefinition
	loadBeanDefinitions(beanDefinitionReader);
}
```

&emsp;&emsp;在准备好了 beanFactory 和 beanDefinitionReader 后就可以进行 xml 的读取工作了，这部分内容和前面讲过的解析过程都一样。
