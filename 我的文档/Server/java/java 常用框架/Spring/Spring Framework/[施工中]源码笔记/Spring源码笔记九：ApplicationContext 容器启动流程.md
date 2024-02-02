# Spring源码笔记九：ApplicationContext 容器启动流程

&emsp;&emsp;除了 XmlBeanFactory 可以用于加载 bean 之外，Spring 中还提供了另一个 ApplicationContext 接口用于拓展 BeanFactory 中现有的功能，ApplicationContext 包含了 BeanFactory 的所有功能，通常建议优先使用，除非在一些有特殊限制的场合，比如字节长度对内存有很大的影响时(Applet)。绝大多数情况下，建议使用 ApplicationContext。

&emsp;&emsp;使用 ApplicationContext 加载 xml：

```java
ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("myTestResources/applicationContext.xml");
UserA userA = (UserA)context.getBean("userA");
UserB userB = (UserB)context.getBean("userB");
```

&emsp;&emsp;现在以 ClassPathXmlApplicationContext 作为切入点进行分析。

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

&emsp;&emsp;ClassPathXmlApplicationContext 中支持将配置文件的路径以数组方式传入，ClassPathXmlApplicationContext 可以对数组进行解析并加载。而对于功能实现都在 `refresh()` 方法中实现。

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

&emsp;&emsp;这里主要用于解析给定的路径数组，当然，数组中如果包含特殊占位符，例如 ${userName} ,那么在 resolvePath 方法中将统一搜寻匹配的系统变量并替换，寻找和替换的具体过程前面已经讲过了。

## 2. refresh

&emsp;&emsp;在设置路径之后，便可以根据路径做配置文件的解析以及各种功能的实现了，其中 refresh 方法中几乎包含了 ApplicationContext 的所有功能。

**AbstractApplicationContext.refresh**

```java
public void refresh() throws BeansException, IllegalStateException {
	synchronized (this.startupShutdownMonitor) {
		StartupStep contextRefresh = this.applicationStartup.start("spring.context.refresh");

		// Prepare this context for refreshing.
		// 1. 刷新前的准备工作
		prepareRefresh();

		// Tell the subclass to refresh the internal bean factory.
		// 2. 创建并初始化 BeanFactory 并进行 xml 文件读取
		ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();

		// Prepare the bean factory for use in this context.
		// 3. 为 BeanFactory 填充各种功能
		prepareBeanFactory(beanFactory);

		try {
			// Allows post-processing of the bean factory in context subclasses.
			// 4. 用于预留给某些 ApplicationContext 的特殊实现中为 BeanFactory 注册特殊的 BeanPostProcessor 等
			postProcessBeanFactory(beanFactory);

			StartupStep beanPostProcess = this.applicationStartup.start("spring.context.beans.post-process");

			// Invoke factory processors registered as beans in the context.
			// 5. 激活各种 BeanFactory 增强器
			invokeBeanFactoryPostProcessors(beanFactory);

			// Register bean processors that intercept bean creation.
			// 6. 注册各种针对 bean 创建时使用的增强器
			registerBeanPostProcessors(beanFactory);
			beanPostProcess.end();

			// Initialize message source for this context.
			// 7. 为环境初始化不同的语言环境，即国际化处理
			initMessageSource();

			// Initialize event multicaster for this context.
			// 8. 初始化事件广播器
			initApplicationEventMulticaster();

			// Initialize other special beans in specific context subclasses.
			// 9. 预留给特殊的子类拓展
			onRefresh();

			// Check for listener beans and register them.
			// 10. 注册监听器
			registerListeners();

			// Instantiate all remaining (non-lazy-init) singletons.
			// 11. 实例化所有非懒加载的单例 bean
			finishBeanFactoryInitialization(beanFactory);

			// Last step: publish corresponding event.
			// 12. 完成刷新，通知生命周期处理器刷新容器
			finishRefresh();
		}

		catch (BeansException ex) {
			if (logger.isWarnEnabled()) {
				logger.warn("Exception encountered during context initialization - " +
						"cancelling refresh attempt: " + ex);
			}

			// Destroy already created singletons to avoid dangling resources.
			// 销毁已经创建的单例 bean
			destroyBeans();

			// Reset 'active' flag.
			// 重置此上下文当前是否处于活动状态的标志
			cancelRefresh(ex);

			// Propagate exception to caller.
			throw ex;
		}

		finally {
			// Reset common introspection caches in Spring's core, since we
			// might not ever need metadata for singleton beans anymore...
			// 重置缓存
			resetCommonCaches();
			contextRefresh.end();
		}
	}
}
```

## 3. 刷新容器前的准备工作

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

&emsp;&emsp;实现也很简单，就是容器环境中没有需要验证的属性就抛出异常。

## 4. 创建 BeanFactory 和 xml 转换

&emsp;&emsp;前面说过 ApplicationContext 是对 BeanFactory 的功能上的拓展，它包含 BeanFactory 的所有功能并在此基础上做了大量的拓展，obtainFreshBeanFactory 就是实现 BeanFactory 的函数，经过该方法后 ApplicationContext 就已经拥有了 BeanFactory 的全部功能。 

**AbstractApplicationContext.obtainFreshBeanFactory**

```java
protected ConfigurableListableBeanFactory obtainFreshBeanFactory() {
	// 1. 初始化 BeanFactory，并进行 xml 文件读取
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

&emsp;&emsp;这里只做了判空处理，如果不为空则进行设置，而对其进行设置的代码需要用户继承来自定义实现。还是用继承并拓展的方法实现，这里还使用刚才的 MyClassPathXmlApplicationContext 作为实现。

**MyClassPathXmlApplicationContext.customizeBeanFactory**

```java
@Override
protected void customizeBeanFactory(DefaultListableBeanFactory beanFactory) {
	// 进行自定义设置值
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
	// 初始化 BeanDefinitionReader，支持子类覆盖拓展
	initBeanDefinitionReader(beanDefinitionReader);
	// 加载 BeanDefinition
	loadBeanDefinitions(beanDefinitionReader);
}

protected void loadBeanDefinitions(XmlBeanDefinitionReader reader) throws BeansException, IOException {
	Resource[] configResources = getConfigResources();
	if (configResources != null) {
		reader.loadBeanDefinitions(configResources);
	}
	String[] configLocations = getConfigLocations();
	if (configLocations != null) {
		reader.loadBeanDefinitions(configLocations);
	}
}
```

&emsp;&emsp;这部分和之前的套路一样，也是使用 XmlBeanDefinitionReader 解析 xml 配置文件，经过该方法后 BeanFactory 中已经包含了所有解析好的配置了。

## 5 为 BeanFactory 拓展各种功能

&emsp;&emsp;下面开始是 ApplicationContext 在基于 BeanFactory 功能上的拓展操作。

**AbstractApplicationContext.prepareBeanFactory**

```java
protected void prepareBeanFactory(ConfigurableListableBeanFactory beanFactory) {
	// Tell the internal bean factory to use the context's class loader etc.
	// 设置使用的 ClassLoader
	beanFactory.setBeanClassLoader(getClassLoader());
	if (!shouldIgnoreSpel) {
		// 增加 spel 解析器
		beanFactory.setBeanExpressionResolver(new StandardBeanExpressionResolver(beanFactory.getBeanClassLoader()));
	}
	// 设置默认的 PropertyEditor 属性编辑器
	beanFactory.addPropertyEditorRegistrar(new ResourceEditorRegistrar(this, getEnvironment()));

	// Configure the bean factory with context callbacks.
	beanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this));
	// 设置自动装配时需要忽略的接口
	beanFactory.ignoreDependencyInterface(EnvironmentAware.class);
	beanFactory.ignoreDependencyInterface(EmbeddedValueResolverAware.class);
	beanFactory.ignoreDependencyInterface(ResourceLoaderAware.class);
	beanFactory.ignoreDependencyInterface(ApplicationEventPublisherAware.class);
	beanFactory.ignoreDependencyInterface(MessageSourceAware.class);
	beanFactory.ignoreDependencyInterface(ApplicationContextAware.class);
	beanFactory.ignoreDependencyInterface(ApplicationStartupAware.class);

	// BeanFactory interface not registered as resolvable type in a plain factory.
	// MessageSource registered (and found for autowiring) as a bean.
	// 设置自动装配时的特殊匹配规则
	beanFactory.registerResolvableDependency(BeanFactory.class, beanFactory);
	beanFactory.registerResolvableDependency(ResourceLoader.class, this);
	beanFactory.registerResolvableDependency(ApplicationEventPublisher.class, this);
	beanFactory.registerResolvableDependency(ApplicationContext.class, this);

	// Register early post-processor for detecting inner beans as ApplicationListeners.
	// 注册 ApplicationListenerDetector
	beanFactory.addBeanPostProcessor(new ApplicationListenerDetector(this));

	// Detect a LoadTimeWeaver and prepare for weaving, if found.
	// 增加对 AspectJ 的支持
	// 参考 https://www.cnblogs.com/wade-luffy/p/6073702.html
	if (!NativeDetector.inNativeImage() && beanFactory.containsBean(LOAD_TIME_WEAVER_BEAN_NAME)) {
		beanFactory.addBeanPostProcessor(new LoadTimeWeaverAwareProcessor(beanFactory));
		// Set a temporary ClassLoader for type matching.
		beanFactory.setTempClassLoader(new ContextTypeMatchClassLoader(beanFactory.getBeanClassLoader()));
	}

	// Register default environment beans.
	// 添加默认的系统环境 bean
	if (!beanFactory.containsLocalBean(ENVIRONMENT_BEAN_NAME)) {
		beanFactory.registerSingleton(ENVIRONMENT_BEAN_NAME, getEnvironment());
	}
	if (!beanFactory.containsLocalBean(SYSTEM_PROPERTIES_BEAN_NAME)) {
		beanFactory.registerSingleton(SYSTEM_PROPERTIES_BEAN_NAME, getEnvironment().getSystemProperties());
	}
	if (!beanFactory.containsLocalBean(SYSTEM_ENVIRONMENT_BEAN_NAME)) {
		beanFactory.registerSingleton(SYSTEM_ENVIRONMENT_BEAN_NAME, getEnvironment().getSystemEnvironment());
	}
	if (!beanFactory.containsLocalBean(APPLICATION_STARTUP_BEAN_NAME)) {
		beanFactory.registerSingleton(APPLICATION_STARTUP_BEAN_NAME, getApplicationStartup());
	}
}
```

### 5.1 SpEL

&emsp;&emsp;之前在 bean 初始化之后做属性填充的时候，有解析 SpEL 表达式这一步，SpEL（Spring Expression Language），即 Spring 表达式语言，是比 JSP 的 EL 更强大的一种表达式语言。类似于 Struts 2x 中使用的 OGNL 表达式语言，能在运行时构建复杂表达式、存取对象图属性、对象方法调用等，并且能与 Spring 功能完美整合，比如能用来配置 bean 定义。SpEL 是单独模块，只依赖于 core 模块，不依赖于其他模块，可以单独使用。

#### 5.1.1 SpEL 使用

&emsp;&emsp;SpEL 使用 `#{表达式}` 作为定界符，所有在大框号中的字符都将被认为是 SpEL，例如在 xml 中的使用：

```xml
<bean name="userA" class="org.springframework.myTest.UserA" >
	<property name="userB" value="#{userB}" />
</bean>
<bean name="userB" class="org.springframework.myTest.UserB" />
```

&emsp;&emsp;相当于：

```xml
<bean name="userA" class="org.springframework.myTest.UserA" >
	<property name="userB" ref="userB" />
</bean>
<bean name="userB" class="org.springframework.myTest.UserB" />
```

#### 5.1.2 SpEL 表达式解析

&emsp;&emsp;Spel 表达式的解析一般通过 evaluateBeanDefinitionString 方法开始

**AbstractBeanFactory.evaluateBeanDefinitionString**

```java
protected Object evaluateBeanDefinitionString(@Nullable String value, @Nullable BeanDefinition beanDefinition) {
	if (this.beanExpressionResolver == null) {
		return value;
	}

	Scope scope = null;
	if (beanDefinition != null) {
		String scopeName = beanDefinition.getScope();
		if (scopeName != null) {
			scope = getRegisteredScope(scopeName);
		}
	}
	//使用解析器解析
	return this.beanExpressionResolver.evaluate(value, new BeanExpressionContext(this, scope));
}
```

&emsp;&emsp;当调用这个方法时会判断是否存在语言解析器，如果存在则调用语言解析器的方法进行解析。

**StandardBeanExpressionResolver.evaluate**

```java
public Object evaluate(@Nullable String value, BeanExpressionContext evalContext) throws BeansException {
	if (!StringUtils.hasLength(value)) {
		return value;
	}
	try {
		// 以前解析过了，缓存获取解析结果
		Expression expr = this.expressionCache.get(value);
		if (expr == null) {
			// 开始解析
			expr = this.expressionParser.parseExpression(value, this.beanExpressionParserContext);
			this.expressionCache.put(value, expr);
		}
		StandardEvaluationContext sec = this.evaluationCache.get(evalContext);
		if (sec == null) {
			sec = new StandardEvaluationContext(evalContext);
			sec.addPropertyAccessor(new BeanExpressionContextAccessor());
			sec.addPropertyAccessor(new BeanFactoryAccessor());
			sec.addPropertyAccessor(new MapAccessor());
			sec.addPropertyAccessor(new EnvironmentAccessor());
			sec.setBeanResolver(new BeanFactoryResolver(evalContext.getBeanFactory()));
			sec.setTypeLocator(new StandardTypeLocator(evalContext.getBeanFactory().getBeanClassLoader()));
			ConversionService conversionService = evalContext.getBeanFactory().getConversionService();
			if (conversionService != null) {
				sec.setTypeConverter(new StandardTypeConverter(conversionService));
			}
			customizeEvaluationContext(sec);
			this.evaluationCache.put(evalContext, sec);
		}
		// 获取解析后的值，包括类型转换
		return expr.getValue(sec);
	}
	catch (Throwable ex) {
		throw new BeanExpressionException("Expression parsing failed", ex);
	}
}
```

### 5.2 属性编辑器

&emsp;&emsp;在 bean 初始化进行依赖注入的时候可以将普通属性注入，但是像 Date 类型就无法被识别，类似于下面情况：

```xml
<property name="date">
	<value>2021-11-25</value>
</property>
```

&emsp;&emsp;在上述直接配置使用时，会报类型转换不成功异常，因为需要的属性是 Date 类型的，而提供的是 String 类型的，Spring 中针对此问题提供了两种解决方案。

#### 5.2.1 使用自定义属性编辑器

&emsp;&emsp;使用自定义属性编辑器，通过继承 PropertyEditorSupport，覆盖 setAsText 方法：

```java
public class MyPropertyEditor extends PropertyEditorSupport {

	private String format = "yyyy-MM-dd";

	public void setFormat(String format) {
		this.format = format;
	}
	
	@Override
	public void setAsText(String dateStr){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		Date date = null;
		try {
			date = simpleDateFormat.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		this.setValue(date);
	}

}
```

&emsp;&emsp;创建 PropertyEditorRegistrar 注册 PropertyEditor

```java
public class DatePropertyEditorRegistrars implements PropertyEditorRegistrar {

	private PropertyEditor propertyEditor;

	@Override
	public void registerCustomEditors(PropertyEditorRegistry registry) {
		registry.registerCustomEditor(java.util.Date.class,propertyEditor);
	}

	public void setPropertyEditor(PropertyEditor propertyEditor) {
		this.propertyEditor = propertyEditor;
	}
}
```

&emsp;&emsp;在 xml 配置中将它们组装起来。

```xml
<bean name="userA" class="org.springframework.myTest.UserA">
	<property name="date">
		<value>2021-11-25</value>
	</property>
</bean>

<bean name="myPropertyEditor" class="org.springframework.myTest.PrpertyEdit.MyPropertyEditor" />

<bean name="datePropertyEditorRegistrars" class="org.springframework.myTest.PrpertyEdit.DatePropertyEditorRegistrars">
	<property name="propertyEditor" ref="myPropertyEditor" />
</bean>
<bean class="org.springframework.beans.factory.config.CustomEditorConfigurer" >
	<property name="propertyEditorRegistrars" >
		<list>
			<ref bean="datePropertyEditorRegistrars" />
		</list>
	</property>
</bean>
```

&emsp;&emsp;通过这样的配置，当 Spring 在进行属性注入时遇到 Date 类型的属性时就会调用自定义的属性编辑器进行类型转换，并用解析的结果代替属性进行注入。

#### 5.2.2 使用 Spring 自带的属性编辑器

&emsp;&emsp;除了可以自定义属性编辑器之外，Spring 还提供了一个自带的 Date 属性编辑器 CustomDateEditor。在配置文件中将自定义属性编辑器替换为 CustomDateEditor 可以达到同样的效果。

```xml
<bean name="userA" class="org.springframework.myTest.UserA">
	<property name="date">
		<value>2021-11-25</value>
	</property>
</bean>

<bean name="customDateEditor" class="org.springframework.beans.propertyeditors.CustomDateEditor" >
	<constructor-arg name="dateFormat" ref="dateFormat" />
	<constructor-arg name="allowEmpty" value="false" />
</bean>
<bean name="dateFormat" class="java.text.SimpleDateFormat" scope="prototype">
	<constructor-arg name="pattern" value="yyyy-MM-dd" />
</bean>

<bean name="datePropertyEditorRegistrars" class="org.springframework.myTest.PrpertyEdit.DatePropertyEditorRegistrars">
	<property name="propertyEditor" ref="customDateEditor" />
</bean>
<bean class="org.springframework.beans.factory.config.CustomEditorConfigurer" >
	<property name="propertyEditorRegistrars" >
		<list>
			<ref bean="datePropertyEditorRegistrars" />
		</list>
	</property>
</bean>
```

#### 5.2.3 属性编辑器的注册

&emsp;&emsp;那么属性编辑器是如何注册到容器中的呢，回到前面 prepareBeanFactory 方法中来：

```java
beanFactory.addPropertyEditorRegistrar(new ResourceEditorRegistrar(this, getEnvironment()));
```

&emsp;&emsp;在 addPropertyEditorRegistrar 方法中注册了 Spring 中自带的 ResourceEditorRegistrar，并存储在 propertyEditorRegistrars 属性中。

**AbstractBeanFactory.addPropertyEditorRegistrar**

```java
public void addPropertyEditorRegistrar(PropertyEditorRegistrar registrar) {
	Assert.notNull(registrar, "PropertyEditorRegistrar must not be null");
	this.propertyEditorRegistrars.add(registrar);
}
```

&emsp;&emsp;进入到 ResourceEditorRegistrar 类中，发现该类的结构和我们自定义的 DatePropertyEditorRegistrars 类基本一致，它的核心方法也是 registerCustomEditors。

**ResourceEditorRegistrar.registerCustomEditors**

```java
public void registerCustomEditors(PropertyEditorRegistry registry) {
	ResourceEditor baseEditor = new ResourceEditor(this.resourceLoader, this.propertyResolver);
	doRegisterEditor(registry, Resource.class, baseEditor);
	doRegisterEditor(registry, ContextResource.class, baseEditor);
	doRegisterEditor(registry, InputStream.class, new InputStreamEditor(baseEditor));
	doRegisterEditor(registry, InputSource.class, new InputSourceEditor(baseEditor));
	doRegisterEditor(registry, File.class, new FileEditor(baseEditor));
	doRegisterEditor(registry, Path.class, new PathEditor(baseEditor));
	doRegisterEditor(registry, Reader.class, new ReaderEditor(baseEditor));
	doRegisterEditor(registry, URL.class, new URLEditor(baseEditor));

	ClassLoader classLoader = this.resourceLoader.getClassLoader();
	doRegisterEditor(registry, URI.class, new URIEditor(classLoader));
	doRegisterEditor(registry, Class.class, new ClassEditor(classLoader));
	doRegisterEditor(registry, Class[].class, new ClassArrayEditor(classLoader));

	if (this.resourceLoader instanceof ResourcePatternResolver) {
		doRegisterEditor(registry, Resource[].class,
				new ResourceArrayPropertyEditor((ResourcePatternResolver) this.resourceLoader, this.propertyResolver));
	}
}
private void doRegisterEditor(PropertyEditorRegistry registry, Class<?> requiredType, PropertyEditor editor) {
	if (registry instanceof PropertyEditorRegistrySupport) {
		((PropertyEditorRegistrySupport) registry).overrideDefaultEditor(requiredType, editor);
	}
	else {
		// 调用 PropertyEditorRegistry 的 registerCustomEditor 方法注册 PropertyEditor
		registry.registerCustomEditor(requiredType, editor);
	}
}
```

&emsp;&emsp;这里注册了一系列的常用的属性编辑器，注册后，一旦某个实体 bean 中存在一些注册的类型属性，Spring 就会调用其对应的属性编辑器来进行类型转换并赋值。通过调试发现 registerCustomEditors 方法是在实例化 bean 并将 BeanDefinition 转换为 BeanWrapper 的 initBeanWrapper 方法中调用的。

**AbstractBeanFactory.initBeanWrapper**

```java
protected void initBeanWrapper(BeanWrapper bw) {
	bw.setConversionService(getConversionService());
	registerCustomEditors(bw);
}
```

**AbstractBeanFactory.registerCustomEditors**

```java
protected void registerCustomEditors(PropertyEditorRegistry registry) {
	if (registry instanceof PropertyEditorRegistrySupport) {
		((PropertyEditorRegistrySupport) registry).useConfigValueEditors();
	}
	if (!this.propertyEditorRegistrars.isEmpty()) {
		for (PropertyEditorRegistrar registrar : this.propertyEditorRegistrars) {
			try {
				// 遍历存储的 PropertyEditorRegistrar 并调用其 registerCustomEditors 方法
				registrar.registerCustomEditors(registry);
			}
			catch (BeanCreationException ex) {
				Throwable rootCause = ex.getMostSpecificCause();
				if (rootCause instanceof BeanCurrentlyInCreationException) {
					BeanCreationException bce = (BeanCreationException) rootCause;
					String bceBeanName = bce.getBeanName();
					if (bceBeanName != null && isCurrentlyInCreation(bceBeanName)) {
						if (logger.isDebugEnabled()) {
							logger.debug("PropertyEditorRegistrar [" + registrar.getClass().getName() +
									"] failed because it tried to obtain currently created bean '" +
									ex.getBeanName() + "': " + ex.getMessage());
						}
						onSuppressedException(ex);
						continue;
					}
				}
				throw ex;
			}
		}
	}
	if (!this.customEditors.isEmpty()) {
		this.customEditors.forEach((requiredType, editorClass) ->
				registry.registerCustomEditor(requiredType, BeanUtils.instantiateClass(editorClass)));
	}
}
```

&emsp;&emsp;到此逻辑已经明了，在 Spring 中，BeanWrapper 除了作为封装 bean 的容器之外，它还间接继承了 PropertyEditorRegistry，在将 BeanDefinition 转换为 BeanWrapper 的过程中就会调用 registerCustomEditors 方法将一些 Spring 自带的属性编辑器注册，注册后，在属性填充的环节就可以直接让 Spring 使用这些编辑器来进行属性的解析转换操作了。而 BeanWrapper 的默认实现 BeanWrapperImpl 还继承了 PropertyEditorRegistrySupport，在 PropertyEditorRegistrySupport 中还有这样一个方法：

**PropertyEditorRegistrySupport.createDefaultEditors**

```java
private void createDefaultEditors() {
	this.defaultEditors = new HashMap<>(64);

	// Simple editors, without parameterization capabilities.
	// The JDK does not contain a default editor for any of these target types.
	this.defaultEditors.put(Charset.class, new CharsetEditor());
	this.defaultEditors.put(Class.class, new ClassEditor());
	this.defaultEditors.put(Class[].class, new ClassArrayEditor());
	this.defaultEditors.put(Currency.class, new CurrencyEditor());
	this.defaultEditors.put(File.class, new FileEditor());
	this.defaultEditors.put(InputStream.class, new InputStreamEditor());
	if (!shouldIgnoreXml) {
		this.defaultEditors.put(InputSource.class, new InputSourceEditor());
	}
	this.defaultEditors.put(Locale.class, new LocaleEditor());
	this.defaultEditors.put(Path.class, new PathEditor());
	this.defaultEditors.put(Pattern.class, new PatternEditor());
	this.defaultEditors.put(Properties.class, new PropertiesEditor());
	this.defaultEditors.put(Reader.class, new ReaderEditor());
	this.defaultEditors.put(Resource[].class, new ResourceArrayPropertyEditor());
	this.defaultEditors.put(TimeZone.class, new TimeZoneEditor());
	this.defaultEditors.put(URI.class, new URIEditor());
	this.defaultEditors.put(URL.class, new URLEditor());
	this.defaultEditors.put(UUID.class, new UUIDEditor());
	this.defaultEditors.put(ZoneId.class, new ZoneIdEditor());

	// Default instances of collection editors.
	// Can be overridden by registering custom instances of those as custom editors.
	this.defaultEditors.put(Collection.class, new CustomCollectionEditor(Collection.class));
	this.defaultEditors.put(Set.class, new CustomCollectionEditor(Set.class));
	this.defaultEditors.put(SortedSet.class, new CustomCollectionEditor(SortedSet.class));
	this.defaultEditors.put(List.class, new CustomCollectionEditor(List.class));
	this.defaultEditors.put(SortedMap.class, new CustomMapEditor(SortedMap.class));

	// Default editors for primitive arrays.
	this.defaultEditors.put(byte[].class, new ByteArrayPropertyEditor());
	this.defaultEditors.put(char[].class, new CharArrayPropertyEditor());

	// The JDK does not contain a default editor for char!
	this.defaultEditors.put(char.class, new CharacterEditor(false));
	this.defaultEditors.put(Character.class, new CharacterEditor(true));

	// Spring's CustomBooleanEditor accepts more flag values than the JDK's default editor.
	this.defaultEditors.put(boolean.class, new CustomBooleanEditor(false));
	this.defaultEditors.put(Boolean.class, new CustomBooleanEditor(true));

	// The JDK does not contain default editors for number wrapper types!
	// Override JDK primitive number editors with our own CustomNumberEditor.
	this.defaultEditors.put(byte.class, new CustomNumberEditor(Byte.class, false));
	this.defaultEditors.put(Byte.class, new CustomNumberEditor(Byte.class, true));
	this.defaultEditors.put(short.class, new CustomNumberEditor(Short.class, false));
	this.defaultEditors.put(Short.class, new CustomNumberEditor(Short.class, true));
	this.defaultEditors.put(int.class, new CustomNumberEditor(Integer.class, false));
	this.defaultEditors.put(Integer.class, new CustomNumberEditor(Integer.class, true));
	this.defaultEditors.put(long.class, new CustomNumberEditor(Long.class, false));
	this.defaultEditors.put(Long.class, new CustomNumberEditor(Long.class, true));
	this.defaultEditors.put(float.class, new CustomNumberEditor(Float.class, false));
	this.defaultEditors.put(Float.class, new CustomNumberEditor(Float.class, true));
	this.defaultEditors.put(double.class, new CustomNumberEditor(Double.class, false));
	this.defaultEditors.put(Double.class, new CustomNumberEditor(Double.class, true));
	this.defaultEditors.put(BigDecimal.class, new CustomNumberEditor(BigDecimal.class, true));
	this.defaultEditors.put(BigInteger.class, new CustomNumberEditor(BigInteger.class, true));

	// Only register config value editors if explicitly requested.
	if (this.configValueEditorsActive) {
		StringArrayPropertyEditor sae = new StringArrayPropertyEditor();
		this.defaultEditors.put(String[].class, sae);
		this.defaultEditors.put(short[].class, sae);
		this.defaultEditors.put(int[].class, sae);
		this.defaultEditors.put(long[].class, sae);
	}
}
```

&emsp;&emsp;在这里 Spring 定义了一系列常用的属性编辑器，如果我们定义的 bean 中的某个属性的类型不在上面的时候，才需要自定义个性化的属性编辑器。而通过上面一系列的方法跟踪，发现我们自定义的 DatePropertyEditorRegistrars 并没有被注册，因为它是在后续的步骤中注册的。

### 5.3 ApplicationContextAwareProcessor

&emsp;&emsp;继续往下看，为容器增加 ApplicationContextAwareProcessor 的过程没什么可说的，重点看一下 ApplicationContextAwareProcessor 类本身的作用，进入类中发现其主要方法为 postProcessBeforeInitialization 

**ApplicationContextAwareProcessor.postProcessBeforeInitialization**

```java
public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
	if (!(bean instanceof EnvironmentAware || bean instanceof EmbeddedValueResolverAware ||
			bean instanceof ResourceLoaderAware || bean instanceof ApplicationEventPublisherAware ||
			bean instanceof MessageSourceAware || bean instanceof ApplicationContextAware ||
			bean instanceof ApplicationStartupAware)) {
		return bean;
	}

	AccessControlContext acc = null;

	if (System.getSecurityManager() != null) {
		acc = this.applicationContext.getBeanFactory().getAccessControlContext();
	}

	if (acc != null) {
		AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
			invokeAwareInterfaces(bean);
			return null;
		}, acc);
	}
	else {
		invokeAwareInterfaces(bean);
	}

	return bean;
}

private void invokeAwareInterfaces(Object bean) {
	if (bean instanceof EnvironmentAware) {
		((EnvironmentAware) bean).setEnvironment(this.applicationContext.getEnvironment());
	}
	if (bean instanceof EmbeddedValueResolverAware) {
		((EmbeddedValueResolverAware) bean).setEmbeddedValueResolver(this.embeddedValueResolver);
	}
	if (bean instanceof ResourceLoaderAware) {
		((ResourceLoaderAware) bean).setResourceLoader(this.applicationContext);
	}
	if (bean instanceof ApplicationEventPublisherAware) {
		((ApplicationEventPublisherAware) bean).setApplicationEventPublisher(this.applicationContext);
	}
	if (bean instanceof MessageSourceAware) {
		((MessageSourceAware) bean).setMessageSource(this.applicationContext);
	}
	if (bean instanceof ApplicationStartupAware) {
		((ApplicationStartupAware) bean).setApplicationStartup(this.applicationContext.getApplicationStartup());
	}
	if (bean instanceof ApplicationContextAware) {
		((ApplicationContextAware) bean).setApplicationContext(this.applicationContext);
	}
}
```

&emsp;&emsp;如果还记得前面讲的，postProcessBeforeInitialization 方法将在 bean 初始化过程中执行自定义 init 方法之前被调用，用于为实现了 Aware 接口的 bean 获取对应的资源。

### 5.4 设置自动装配忽略的接口

&emsp;&emsp;当 bean 实现了一些带有 setXXX 方法的接口时，自动装配时需要忽略这些。前面讲自动装配时具体说过，不再赘述。

### 5.5 设置自动装配时的特殊匹配规则

&emsp;&emsp;当 bean 在自动装配时遇到这里注册的类型的属性时，将用这里注册的实例代替。

## 6 激活各种 BeanFactory 增强器

&emsp;&emsp;BeanFactoryPostProcessor 与 BeanPostProcessor 类似，可以对 BeanFactory 容器进行增强处理。需要注意的是，它只对当前容器进行增强处理，不会对另一个容器进行增强，即使这两个容器都是在同一层次上。

### 6.1 PropertyPlaceholderConfigurer

&emsp;&emsp;以 BeanFactoryPostProcessor 的典型实现 PropertyPlaceholderConfigurer 为例来看一下 BeanFactoryPostProcessor 的作用（尽管在 Spring 5.2 版本以后官方更建议使用 PropertySourcesPlaceholderConfigurer 作为 PropertyPlaceholderConfigurer 的替代，但并不影响这里的演示）。在日常使用 Spring 进行配置的时候，经常需要将一些经常变化的属性配置到单独的配置文件中，然后在 xml 配置中引入：

**applicationContext.xml**

```xml
<bean class="org.springframework.beans.factory.config.PropertyPlaceholderConfigurer">
	<property name="locations">
		<list>
			<value>myTestResources/userTest.properties</value>
		</list>
	</property>
</bean>
<bean name="userA" class="org.springframework.myTest.UserA">
	<property name="name" value="${myuser.name}" />
</bean>
```

**userTest.properties**

```properties
myuser.name=zhang
```

&emsp;&emsp;其中出现的 `${myuser.name}` 就是 Spring 的分散配置，可以在另外的配置中为其指定值，当 Spring 遇到它时，就会将它解析成为指定的值。

&emsp;&emsp;PropertyPlaceholderConfigurer 间接继承了 BeanFactoryPostProcessor 接口，这就是问题的关键，当 Spring 加载任何实现了此接口的 bean 时，都会在 BeanFactory 载入所有 bean 配置之后执行其 postProcessBeanFactory 方法，而正是在对 postProcessBeanFactory 方法的实现中完成了配置文件的读取转换工作。

### 6.2 使用自定义 BeanFactoryPostProcessor

&emsp;&emsp;除了使用 Spring 自带的 BeanFactoryPostProcessor 之外，当然也可以使用由用户自定义的 BeanFactoryPostProcessor。例如在一些实体类 bean 中存在比较敏感的信息，在使用时不展示给用户，就可以通过自定义 BeanFactoryPostProcessor 来实现。

```java
public class UserA {
	private String name;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
```

&emsp;&emsp;自定义 MyBeanFactoryPostProcessor。

```java
public class MyBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

	private String str = "23";

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		Iterator<String> beanNamesIterator = beanFactory.getBeanNamesIterator();
		while(beanNamesIterator.hasNext()){
			BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanNamesIterator.next());
			StringValueResolver stringValueResolver = strVal -> {
				if(strVal.contains(str)){
					return strVal.replaceAll(str,"**");
				}
				return strVal;
			};
			BeanDefinitionVisitor beanDefinitionVisitor = new BeanDefinitionVisitor(stringValueResolver);
			beanDefinitionVisitor.visitBeanDefinition(beanDefinition);
		}

	}
}
```

```xml
<bean name="userA" class="org.springframework.myTest.UserA">
	<property name="name" value="123456"/>
</bean>
```

```java
public static void main(String[] args) {
	ConfigurableListableBeanFactory configurableListableBeanFactory = new XmlBeanFactory(new ClassPathResource("myTestResources/applicationContext.xml"));
	MyBeanFactoryPostProcessor myBeanFactoryPostProcessor = new MyBeanFactoryPostProcessor();
	myBeanFactoryPostProcessor.postProcessBeanFactory(configurableListableBeanFactory);
	UserA userA = (UserA)configurableListableBeanFactory.getBean("userA");
	System.out.println(userA.getName());
}
```

&emsp;&emsp;结果：1**456，可以看到通过自定义 MyBeanFactoryPostProcessor 很好的屏蔽掉了敏感信息。

### 6.3 激活 BeanFactoryPostProcessor

&emsp;&emsp;在了解了 BeanFactoryPostProcessor 的使用场景之后，看看 Spring 中是如何激活它的。

**AbstractApplicationContext.invokeBeanFactoryPostProcessors**

```java
protected void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory) {
	PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(beanFactory, getBeanFactoryPostProcessors());

	// Detect a LoadTimeWeaver and prepare for weaving, if found in the meantime
	// (e.g. through an @Bean method registered by ConfigurationClassPostProcessor)
	if (!NativeDetector.inNativeImage() && beanFactory.getTempClassLoader() == null && beanFactory.containsBean(LOAD_TIME_WEAVER_BEAN_NAME)) {
		beanFactory.addBeanPostProcessor(new LoadTimeWeaverAwareProcessor(beanFactory));
		beanFactory.setTempClassLoader(new ContextTypeMatchClassLoader(beanFactory.getBeanClassLoader()));
	}
}
```

**PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors**

```java
public static void invokeBeanFactoryPostProcessors(
		ConfigurableListableBeanFactory beanFactory, List<BeanFactoryPostProcessor> beanFactoryPostProcessors) {

	// WARNING: Although it may appear that the body of this method can be easily
	// refactored to avoid the use of multiple loops and multiple lists, the use
	// of multiple lists and multiple passes over the names of processors is
	// intentional. We must ensure that we honor the contracts for PriorityOrdered
	// and Ordered processors. Specifically, we must NOT cause processors to be
	// instantiated (via getBean() invocations) or registered in the ApplicationContext
	// in the wrong order.
	//
	// Before submitting a pull request (PR) to change this method, please review the
	// list of all declined PRs involving changes to PostProcessorRegistrationDelegate
	// to ensure that your proposal does not result in a breaking change:
	// https://github.com/spring-projects/spring-framework/issues?q=PostProcessorRegistrationDelegate+is%3Aclosed+label%3A%22status%3A+declined%22

	// Invoke BeanDefinitionRegistryPostProcessors first, if any.
	Set<String> processedBeans = new HashSet<>();
	// BeanDefinitionRegistry 类型的处理
	if (beanFactory instanceof BeanDefinitionRegistry) {
		BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
		List<BeanFactoryPostProcessor> regularPostProcessors = new ArrayList<>();
		List<BeanDefinitionRegistryPostProcessor> registryProcessors = new ArrayList<>();
		// 遍历所有的 beanFactoryPostProcessor
		for (BeanFactoryPostProcessor postProcessor : beanFactoryPostProcessors) {
			// 如果 postProcessor 是 BeanDefinitionRegistryPostProcessor 类型
			// 则需要保证 BeanDefinitionRegistryPostProcessor 自己的方法
			// postProcessBeanDefinitionRegistry 优先于 postProcessBeanFactory 方法调用
			if (postProcessor instanceof BeanDefinitionRegistryPostProcessor) {
				BeanDefinitionRegistryPostProcessor registryProcessor =
						(BeanDefinitionRegistryPostProcessor) postProcessor;
				registryProcessor.postProcessBeanDefinitionRegistry(registry);
				registryProcessors.add(registryProcessor);
			}
			else {
				// 记录常规的 beanFactoryPostProcessor
				regularPostProcessors.add(postProcessor);
			}
		}

		// Do not initialize FactoryBeans here: We need to leave all regular beans
		// uninitialized to let the bean factory post-processors apply to them!
		// Separate between BeanDefinitionRegistryPostProcessors that implement
		// PriorityOrdered, Ordered, and the rest.
		List<BeanDefinitionRegistryPostProcessor> currentRegistryProcessors = new ArrayList<>();

		// First, invoke the BeanDefinitionRegistryPostProcessors that implement PriorityOrdered.
		// 首先保证实现了 PriorityOrdered 接口的 BeanDefinitionRegistryPostProcessor 优先被调用
		// 获取从配置文件配置的 postProcessor
		String[] postProcessorNames =
				beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
		for (String ppName : postProcessorNames) {
			if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
				currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
				processedBeans.add(ppName);
			}
		}
		sortPostProcessors(currentRegistryProcessors, beanFactory);
		registryProcessors.addAll(currentRegistryProcessors);
		invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry, beanFactory.getApplicationStartup());
		currentRegistryProcessors.clear();

		// Next, invoke the BeanDefinitionRegistryPostProcessors that implement Ordered.
		// 然后调用实现了 Ordered 接口的 BeanDefinitionRegistryPostProcessor
		postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
		for (String ppName : postProcessorNames) {
			if (!processedBeans.contains(ppName) && beanFactory.isTypeMatch(ppName, Ordered.class)) {
				currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
				processedBeans.add(ppName);
			}
		}
		sortPostProcessors(currentRegistryProcessors, beanFactory);
		registryProcessors.addAll(currentRegistryProcessors);
		invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry, beanFactory.getApplicationStartup());
		currentRegistryProcessors.clear();

		// Finally, invoke all other BeanDefinitionRegistryPostProcessors until no further ones appear.
		// 最后，调用所有其他 BeanDefinitionRegistryPostProcessor
		boolean reiterate = true;
		while (reiterate) {
			reiterate = false;
			postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
			for (String ppName : postProcessorNames) {
				if (!processedBeans.contains(ppName)) {
					currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
					processedBeans.add(ppName);
					reiterate = true;
				}
			}
			sortPostProcessors(currentRegistryProcessors, beanFactory);
			registryProcessors.addAll(currentRegistryProcessors);
			invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry, beanFactory.getApplicationStartup());
			currentRegistryProcessors.clear();
		}

		// Now, invoke the postProcessBeanFactory callback of all processors handled so far.
		// 最后调用所有 beanFactoryPostProcessor 的 postProcessBeanFactory 方法
		invokeBeanFactoryPostProcessors(registryProcessors, beanFactory);
		invokeBeanFactoryPostProcessors(regularPostProcessors, beanFactory);
	}

	else {
		// Invoke factory processors registered with the context instance.
		// 不需要考虑排序，直接调用
		invokeBeanFactoryPostProcessors(beanFactoryPostProcessors, beanFactory);
	}

	// Do not initialize FactoryBeans here: We need to leave all regular beans
	// uninitialized to let the bean factory post-processors apply to them!
	String[] postProcessorNames =
			beanFactory.getBeanNamesForType(BeanFactoryPostProcessor.class, true, false);

	// Separate between BeanFactoryPostProcessors that implement PriorityOrdered,
	// Ordered, and the rest.
	// 将实现了 PriorityOrdered 、Ordered 接口和其余的 BeanFactoryPostProcessor 分开处理
	List<BeanFactoryPostProcessor> priorityOrderedPostProcessors = new ArrayList<>();
	List<String> orderedPostProcessorNames = new ArrayList<>();
	List<String> nonOrderedPostProcessorNames = new ArrayList<>();
	for (String ppName : postProcessorNames) {
		if (processedBeans.contains(ppName)) {
			// skip - already processed in first phase above
		}
		else if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
			priorityOrderedPostProcessors.add(beanFactory.getBean(ppName, BeanFactoryPostProcessor.class));
		}
		else if (beanFactory.isTypeMatch(ppName, Ordered.class)) {
			orderedPostProcessorNames.add(ppName);
		}
		else {
			nonOrderedPostProcessorNames.add(ppName);
		}
	}

	// First, invoke the BeanFactoryPostProcessors that implement PriorityOrdered.
	sortPostProcessors(priorityOrderedPostProcessors, beanFactory);
	invokeBeanFactoryPostProcessors(priorityOrderedPostProcessors, beanFactory);

	// Next, invoke the BeanFactoryPostProcessors that implement Ordered.
	List<BeanFactoryPostProcessor> orderedPostProcessors = new ArrayList<>(orderedPostProcessorNames.size());
	for (String postProcessorName : orderedPostProcessorNames) {
		orderedPostProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
	}
	sortPostProcessors(orderedPostProcessors, beanFactory);
	invokeBeanFactoryPostProcessors(orderedPostProcessors, beanFactory);

	// Finally, invoke all other BeanFactoryPostProcessors.
	List<BeanFactoryPostProcessor> nonOrderedPostProcessors = new ArrayList<>(nonOrderedPostProcessorNames.size());
	for (String postProcessorName : nonOrderedPostProcessorNames) {
		nonOrderedPostProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
	}
	invokeBeanFactoryPostProcessors(nonOrderedPostProcessors, beanFactory);

	// Clear cached merged bean definitions since the post-processors might have
	// modified the original metadata, e.g. replacing placeholders in values...
	beanFactory.clearMetadataCache();
}
```

&emsp;&emsp;从上面方法可以看出，对于 BeanFactoryPostProcessor 的处理主要分为两种情况进行，区分两种情况的判断就是判断当前使用的 beanFactory 是否实现了 BeanDefinitionRegistry 接口，对于实现了 BeanDefinitionRegistry 接口的 beanFactory 来说，则需要考虑针对 BeanDefinitionRegistryPostProcessor 的特殊处理，如果不是则可以忽略直接处理 BeanFactoryPostProcessor。值得一提的是这里对于使用编码方式添加的 BeanFactoryPostProcessor 是不需要做任何排序处理的，而对于在配置文件中读取的 BeanFactoryPostProcessor 来说，因为不能保证读取顺序，为了保证用户调用顺序的要求，Spring 支持按照 PriorityOrdered 、Ordered 接口的顺序调用。

&emsp;&emsp;现在回看 5.2.1 自定义属性编辑器的过程中使用了 CustomEditorConfigurer 类，这个类就实现了 BeanFactoryPostProcessor，并可以看到它给容器添加 PropertyEditorRegistrar 的全过程。

**CustomEditorConfigurer.postProcessBeanFactory**

```java
public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
	if (this.propertyEditorRegistrars != null) {
		for (PropertyEditorRegistrar propertyEditorRegistrar : this.propertyEditorRegistrars) {
			beanFactory.addPropertyEditorRegistrar(propertyEditorRegistrar);
		}
	}
	if (this.customEditors != null) {
		this.customEditors.forEach(beanFactory::registerCustomEditor);
	}
}
```

&emsp;&emsp;当然包括6.1小节的 PropertyPlaceholderConfigurer 的 postProcessBeanFactory 方法也是在此步骤完成调用的。

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/spring/202111301644721.png)

## 7 注册各种针对 bean 创建时使用的增强器

&emsp;&emsp;上面完成了 BeanFactoryPostProcessor 的使用过程，现在看一下针对 bean 创建时使用的增强器的注册过程。

**AbstractApplicationContext.registerBeanPostProcessors**

```java
protected void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory) {
	PostProcessorRegistrationDelegate.registerBeanPostProcessors(beanFactory, this);
}
```

**PostProcessorRegistrationDelegate.registerBeanPostProcessors**

```java
public static void registerBeanPostProcessors(
		ConfigurableListableBeanFactory beanFactory, AbstractApplicationContext applicationContext) {

	// WARNING: Although it may appear that the body of this method can be easily
	// refactored to avoid the use of multiple loops and multiple lists, the use
	// of multiple lists and multiple passes over the names of processors is
	// intentional. We must ensure that we honor the contracts for PriorityOrdered
	// and Ordered processors. Specifically, we must NOT cause processors to be
	// instantiated (via getBean() invocations) or registered in the ApplicationContext
	// in the wrong order.
	//
	// Before submitting a pull request (PR) to change this method, please review the
	// list of all declined PRs involving changes to PostProcessorRegistrationDelegate
	// to ensure that your proposal does not result in a breaking change:
	// https://github.com/spring-projects/spring-framework/issues?q=PostProcessorRegistrationDelegate+is%3Aclosed+label%3A%22status%3A+declined%22
	// 获取从配置文件中配置的所有 BeanPostProcessor
	String[] postProcessorNames = beanFactory.getBeanNamesForType(BeanPostProcessor.class, true, false);

	// Register BeanPostProcessorChecker that logs an info message when
	// a bean is created during BeanPostProcessor instantiation, i.e. when
	// a bean is not eligible for getting processed by all BeanPostProcessors.
	int beanProcessorTargetCount = beanFactory.getBeanPostProcessorCount() + 1 + postProcessorNames.length;
	// BeanPostProcessorChecker 是一个用于打印信息的 BeanPostProcessor
	// 在一些情况下，会出现 Spring 中配置的增强器还没有注册就已经开始了 bean 的初始化
	// BeanPostProcessorChecker 就是为了将这些 bean 信息打印出来
	beanFactory.addBeanPostProcessor(new BeanPostProcessorChecker(beanFactory, beanProcessorTargetCount));

	// Separate between BeanPostProcessors that implement PriorityOrdered,
	// Ordered, and the rest.
	// 将实现了 PriorityOrdered 或 Ordered 接口的 BeanPostProcessor 分开处理
	// 存储实现 PriorityOrdered 的 BeanPostProcessor
	List<BeanPostProcessor> priorityOrderedPostProcessors = new ArrayList<>();
	// 存储实现 MergedBeanDefinitionPostProcessor 的 BeanPostProcessor
	List<BeanPostProcessor> internalPostProcessors = new ArrayList<>();
	// 存储实现 Ordered 的 BeanPostProcessor
	List<String> orderedPostProcessorNames = new ArrayList<>();
	// 存储无序的 BeanPostProcessor
	List<String> nonOrderedPostProcessorNames = new ArrayList<>();
	// 遍历 postProcessorNames
	for (String ppName : postProcessorNames) {
		// 实现了 PriorityOrdered 接口的 BeanPostProcessor 需要优先处理
		if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
			BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
			priorityOrderedPostProcessors.add(pp);
			if (pp instanceof MergedBeanDefinitionPostProcessor) {
				internalPostProcessors.add(pp);
			}
		}
		// 实现了 Ordered 接口的 BeanPostProcessor 第二优先级
		else if (beanFactory.isTypeMatch(ppName, Ordered.class)) {
			orderedPostProcessorNames.add(ppName);
		}
		else {
			// 不需要考虑调用顺序
			nonOrderedPostProcessorNames.add(ppName);
		}
	}

	// First, register the BeanPostProcessors that implement PriorityOrdered.
	// 先注册实现了 PriorityOrdered 接口的 BeanPostProcessor
	sortPostProcessors(priorityOrderedPostProcessors, beanFactory);
	registerBeanPostProcessors(beanFactory, priorityOrderedPostProcessors);

	// Next, register the BeanPostProcessors that implement Ordered.
	// 然后注册实现了 Ordered 接口的 BeanPostProcessor
	List<BeanPostProcessor> orderedPostProcessors = new ArrayList<>(orderedPostProcessorNames.size());
	for (String ppName : orderedPostProcessorNames) {
		BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
		orderedPostProcessors.add(pp);
		if (pp instanceof MergedBeanDefinitionPostProcessor) {
			internalPostProcessors.add(pp);
		}
	}
	sortPostProcessors(orderedPostProcessors, beanFactory);
	registerBeanPostProcessors(beanFactory, orderedPostProcessors);

	// Now, register all regular BeanPostProcessors.
	// 不需要考虑调用顺序的 BeanPostProcessor 最后处理
	List<BeanPostProcessor> nonOrderedPostProcessors = new ArrayList<>(nonOrderedPostProcessorNames.size());
	for (String ppName : nonOrderedPostProcessorNames) {
		BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
		nonOrderedPostProcessors.add(pp);
		if (pp instanceof MergedBeanDefinitionPostProcessor) {
			internalPostProcessors.add(pp);
		}
	}
	registerBeanPostProcessors(beanFactory, nonOrderedPostProcessors);

	// Finally, re-register all internal BeanPostProcessors.
	// 最后注册所有实现 MergedBeanDefinitionPostProcessor 的 BeanPostProcessors
	// 这里并不会重复注册，在 addBeanPostProcessors 方法时会先移除再添加
	sortPostProcessors(internalPostProcessors, beanFactory);
	registerBeanPostProcessors(beanFactory, internalPostProcessors);

	// Re-register post-processor for detecting inner beans as ApplicationListeners,
	// moving it to the end of the processor chain (for picking up proxies etc).
	// 添加 ApplicationListenerDetector
	beanFactory.addBeanPostProcessor(new ApplicationListenerDetector(applicationContext));
}
```

&emsp;&emsp;从上面代码中可以看到逻辑非常清晰，对于 BeanPostProcessor 的处理与 BeanFactoryPostProcessor 的处理非常相似。从实现中可以发现，这里仅仅是将 BeanPostProcessor 实例化并注册到了 beanFactory 容器中，而并非和 BeanFactoryPostProcessor 一样需要在此处调用，BeanPostProcessor 的调用是在 bean 的实例化阶段进行的。在 Spring 中，大部分的功能都是通过增强器的方式进行扩展的，但是在 BeanFactory 中并没有实现增强器的自动注册，但在 ApplicationContext 中却增加了自动注册的功能。也正是因为如此，所以在前面使用 XmlBeanFactory 启动时很多功能都并不支持。

&emsp;&emsp;而且这里只考虑了配置文件中的 BeanPostProcessor 处理，对于通过代码中添加的 BeanPostProcessor 并不需要处理，因为这里并不需要调用。

## 8 国际化处理

### 8.1 国际化的使用

&emsp;&emsp;对于国际化的使用，假设现在有一个支持多国语言的 web 应用，要求系统可以根据客户端系统的语言类型返回对应界面，英文的操作系统返回英文界面，而中文操作系统返回中文界面。对于这种具有国际化要求的应用，开发时不能简单的用硬编码方式编写用户界面等信息，必须为这些信息进行特殊处理，简单来说，就是为每种语言提供一套相应的资源文件，并以规范化的命名方式保存在特定的目录中，由系统自动根据客户端语言选择合适的资源文件。

&emsp;&emsp;"国际化信息"也称为"本地化信息"，一般需要两个条件才可以确定一个特定类型的本地化信息，它们分别是"语言类型"和"国家/地区的类型"。如中文本地化信息既有中国大陆地区的中文，又有中国台湾地区、中国香港地区的中文，还有新加坡地区的中文。在 Java 中通过 java.util.Locale 类表示一个本地化对象，它允许通过**语言**参数和**国家/地区**参数创建一个确定的本地化对象。

例如简体中文 Locale 对象的创建:

```java
Locale local = new Locale("zh","CN");
```

&emsp;&emsp;在 Java 中实现国际化主要是借助一个工具类 ResourceBundle，核心的思想就是，对不同的语言环境提供一个不同的资源文件。定义资源文件时，先将资源文件放在一个资源包中，一个资源包中每个资源文件必须拥有共同的基名，除了基名，每个文件的名称中还必须有标识其本地信息的附加部分，例如 ：一个资源包的基名是"message"，则与中文（中国）、英文（美国）环境相对应的资源文件名分别为 ：

- message_zh_CN.properties
- message_en_US.properties

【示例】

&emsp;&emsp;ResourceBundle 类提供了相应的静态方法 getBundle，这个方法可以根据来访者的国家地区自动绑定对应的资源文件。

**message_zh_CN.properties**

```properties
#张三
name=\u5f20\u4e09 #张三
```

**message_en_US.properties**

```properties
name=ZhangSan
```

```java
public static void main(String[] args) {
    Locale locale_en = new Locale("en","US");
    Locale locale_zh = new Locale("zh","CN");
    ResourceBundle resourceBundle = ResourceBundle.getBundle("message",locale_en);
    System.out.println(resourceBundle.getString("name"));//ZhangSan
    resourceBundle = ResourceBundle.getBundle("message",locale_zh);
    System.out.println(resourceBundle.getString("name"));//张三
}
```

&emsp;&emsp;以上就是一些固定文本的国际化，固定文本包括菜单名，导航条，系统提示信息等，都是手工配置在 properties 文件中的，但有些数据，比方说，数值，货币，时间，日期等由于可能在程序运行时动态产生，所以无法像文字一样简单地将它们从应用程序中分离出来，而需要特殊处理。Java 中提供了 NumberFormat，DateFormat 和 MessageFormat 类分别对数字、日期和字符串进行处理，而在 Spring 中的国际化处理也就是对这些类的封装操作。

### 8.2 MessageSource

&emsp;&emsp;Spring 定义了访问国际化信息的 MessageSource 接口，并提供了几个实现类，这里主要看一下 HierarchicalMessageSource 接口的实现类，HierarchicalMessageSource 的实现类中 ReloadableResourceBundleMessageSource 和 ResourceBundleMessageSource 基于 Java 的 ResourceBundle 基础类实现，允许仅通过资源名加载国际化资源，ReloadableResourceBundleMessageSource 则提供了定时刷新功能，可以在不重启系统的情况下，更新资源信息，StaticMessageSource 主要用于程序测试，它允许通过编程的方式提供国际化信息，而 DelegatingMessageSource 是为了方便操作父 MessageSource 而提供的代理类。

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/spring/202112061007248.png)

&emsp;&emsp;然后来看如何使用，第一步仍然是创建资源包：

**message_zh_CN.properties**

```properties
#测试
test=\u6d4b\u8bd5
```

**message_en_US.properties**

```properties
test=test
```

&emsp;&emsp;然后定义配置文件：

```xml
<!-- 这里 bean 的 id 必须为 messageSource -->
<bean id="messageSource" class="org.springframework.context.support.ResourceBundleMessageSource">
	<property name="basename">
		<value>myTestResources/message/message</value>
	</property>
</bean>
```

```java
public static void main(String[] args) {
	ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("myTestResources/applicationContext.xml");
	System.out.println(context.getMessage("test",null, Locale.CHINA));
}
```

### 8.3 源码实现

**【初始化 messageSource】**

**AbstractApplicationContext.initMessageSource**

```java
protected void initMessageSource() {
	ConfigurableListableBeanFactory beanFactory = getBeanFactory();
	// 如果有自定义配置 messageSource 则使用自定义配置的
	if (beanFactory.containsLocalBean(MESSAGE_SOURCE_BEAN_NAME)) {
		this.messageSource = beanFactory.getBean(MESSAGE_SOURCE_BEAN_NAME, MessageSource.class);
		// Make MessageSource aware of parent MessageSource.
		if (this.parent != null && this.messageSource instanceof HierarchicalMessageSource) {
			HierarchicalMessageSource hms = (HierarchicalMessageSource) this.messageSource;
			if (hms.getParentMessageSource() == null) {
				// Only set parent context as parent MessageSource if no parent MessageSource
				// registered already.
				hms.setParentMessageSource(getInternalParentMessageSource());
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("Using MessageSource [" + this.messageSource + "]");
		}
	}
	else {
		// 没有配置则使用默认配置的 DelegatingMessageSource
		// Use empty MessageSource to be able to accept getMessage calls.
		DelegatingMessageSource dms = new DelegatingMessageSource();
		dms.setParentMessageSource(getInternalParentMessageSource());
		this.messageSource = dms;
		beanFactory.registerSingleton(MESSAGE_SOURCE_BEAN_NAME, this.messageSource);
		if (logger.isTraceEnabled()) {
			logger.trace("No '" + MESSAGE_SOURCE_BEAN_NAME + "' bean, using [" + this.messageSource + "]");
		}
	}
}
```

&emsp;&emsp;在容器启动时会初始化 messageSource，如果有自定义配置 messageSource 则使用自定义配置的，没有则使用默认配置的 DelegatingMessageSource，在获取自定义 messageSource 的时候在代码中硬性规定了 bean 的名字必须为 messageSource，否则就获取不到，当获取到 messageSource 后将其存储在当前容器中，用的时候直接读取即可。

## 9 事件监听器的使用

### 9.1 自定义事件监听器

**【TestEvent】**

```java
public class TestEvent extends ApplicationEvent {

	private String msg;

	public TestEvent(Object source) {
		super(source);
	}

	public TestEvent(Object source, String msg) {
		super(source);
		this.msg = msg;
	}

	public void print(){
		System.out.println(msg);
	}

}
```

**【TestListener】**

```java
public class TestListener implements ApplicationListener<TestEvent> {
	@Override
	public void onApplicationEvent(TestEvent event) {
		event.print();
	}
}
```

**【main】**

```java
public static void main(String[] args) {
	ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("myTestResources/applicationContext.xml");
	TestEvent event = new TestEvent("hello","msg");
	//触发 TestEvent 事件
	context.publishEvent(event);
}
```

**【applicationContext.xml】将事件监听器注册到容器中**

```xml
<bean id="testListener" class="org.springframework.myTest.eventListener.TestListener" />
```

&emsp;&emsp;在 spring 中通过 ApplicationListener 及 ApplicationEventMulticaster 来进行事件驱动开发，即实现观察者设计模式或发布-订阅模式，ApplicationListener 负责监听容器中发布的事件，只要事件发生，就触发监听器的回调，来完成事件驱动开发，扮演观察者设计模式中的 Observer 对象，ApplicationEventMulticaster 用来通知所有的观察者对象，扮演观察者设计模式中的 Subject 对象。

### 9.2 初始化 ApplicationEventMulticaster

**AbstractApplicationContext.initApplicationEventMulticaster**

```java
protected void initApplicationEventMulticaster() {
	ConfigurableListableBeanFactory beanFactory = getBeanFactory();
	// 如果有用户自定义的事件多播器使用用户自定义的
	if (beanFactory.containsLocalBean(APPLICATION_EVENT_MULTICASTER_BEAN_NAME)) {
		this.applicationEventMulticaster =
				beanFactory.getBean(APPLICATION_EVENT_MULTICASTER_BEAN_NAME, ApplicationEventMulticaster.class);
		if (logger.isTraceEnabled()) {
			logger.trace("Using ApplicationEventMulticaster [" + this.applicationEventMulticaster + "]");
		}
	}
	else {
		// 没有自定义的则使用默认的 SimpleApplicationEventMulticaster
		this.applicationEventMulticaster = new SimpleApplicationEventMulticaster(beanFactory);
		beanFactory.registerSingleton(APPLICATION_EVENT_MULTICASTER_BEAN_NAME, this.applicationEventMulticaster);
		if (logger.isTraceEnabled()) {
			logger.trace("No '" + APPLICATION_EVENT_MULTICASTER_BEAN_NAME + "' bean, using " +
					"[" + this.applicationEventMulticaster.getClass().getSimpleName() + "]");
		}
	}
}
```

&emsp;&emsp;如果用户自定义了事件广播器，那么使用用户自定义的事件广播器，如果用户没有自定义事件广播器，那么使用默认的 applicationEventMulticaster。默认的广播器实现中有这样一个属性:

```java
public final Set<ApplicationListener<?>> applicationListeners = new LinkedHashSet<>();
```

&emsp;&emsp;用于将系统中的 ApplicationListener 维护到这个 set 中。

### 9.3 ApplicationEvent 事件的发布

**AbstractApplicationContext.publishEvent**

```java
public void publishEvent(ApplicationEvent event) {
	publishEvent(event, null);
}

protected void publishEvent(Object event, @Nullable ResolvableType eventType) {
	Assert.notNull(event, "Event must not be null");

	// Decorate event as an ApplicationEvent if necessary
	ApplicationEvent applicationEvent;
	if (event instanceof ApplicationEvent) {
		applicationEvent = (ApplicationEvent) event;
	}
	else {
		applicationEvent = new PayloadApplicationEvent<>(this, event);
		if (eventType == null) {
			eventType = ((PayloadApplicationEvent<?>) applicationEvent).getResolvableType();
		}
	}

	// Multicast right now if possible - or lazily once the multicaster is initialized
	if (this.earlyApplicationEvents != null) {
		this.earlyApplicationEvents.add(applicationEvent);
	}
	else {
		// 获取 ApplicationEventMulticaster 并调用其 multicastEvent
		getApplicationEventMulticaster().multicastEvent(applicationEvent, eventType);
	}

	// Publish event via parent context as well...
	if (this.parent != null) {
		if (this.parent instanceof AbstractApplicationContext) {
			((AbstractApplicationContext) this.parent).publishEvent(event, eventType);
		}
		else {
			this.parent.publishEvent(event);
		}
	}
}
```

**SimpleApplicationEventMulticaster.multicastEvent**

```java
public void multicastEvent(final ApplicationEvent event, @Nullable ResolvableType eventType) {
	// 解析事件类型
	ResolvableType type = (eventType != null ? eventType : resolveDefaultEventType(event));
	Executor executor = getTaskExecutor();
	// 获取对应事件类型的监听器并触发其监听事件
	for (ApplicationListener<?> listener : getApplicationListeners(event, type)) {
		if (executor != null) {
			executor.execute(() -> invokeListener(listener, event));
		}
		else {
			invokeListener(listener, event);
		}
	}
}

protected void invokeListener(ApplicationListener<?> listener, ApplicationEvent event) {
	ErrorHandler errorHandler = getErrorHandler();
	if (errorHandler != null) {
		try {
			doInvokeListener(listener, event);
		}
		catch (Throwable err) {
			errorHandler.handleError(err);
		}
	}
	else {
		doInvokeListener(listener, event);
	}
}

private void doInvokeListener(ApplicationListener listener, ApplicationEvent event) {
	try {
		// 调用 ApplicationListener 的 onApplicationEvent 
		listener.onApplicationEvent(event);
	}
	catch (ClassCastException ex) {
		String msg = ex.getMessage();
		if (msg == null || matchesClassCastMessage(msg, event.getClass()) ||
				(event instanceof PayloadApplicationEvent &&
						matchesClassCastMessage(msg, ((PayloadApplicationEvent) event).getPayload().getClass()))) {
			// Possibly a lambda-defined listener which we could not resolve the generic event type for
			// -> let's suppress the exception.
			Log loggerToUse = this.lazyLogger;
			if (loggerToUse == null) {
				loggerToUse = LogFactory.getLog(getClass());
				this.lazyLogger = loggerToUse;
			}
			if (loggerToUse.isTraceEnabled()) {
				loggerToUse.trace("Non-matching event type for listener: " + listener, ex);
			}
		}
		else {
			throw ex;
		}
	}
}
```

&emsp;&emsp;当产生 Spring 事件的时候会使用 SimpleApplicationEventMulticaster 的 multicastEvent 来广播事件，遍历所有匹配的监听器，并调用监听器中的 onApplicationEvent 方法来进行处理。

### 9.3 监听器 ApplicationListener 的注册

**AbstractApplicationContext.registerListeners**

```java
protected void registerListeners() {
		// Register statically specified listeners first.
		// 首先注册代码中硬编码的监听器
		for (ApplicationListener<?> listener : getApplicationListeners()) {
			getApplicationEventMulticaster().addApplicationListener(listener);
		}

		// Do not initialize FactoryBeans here: We need to leave all regular beans
		// uninitialized to let post-processors apply to them!
		// 然后注册配置的监听器
		String[] listenerBeanNames = getBeanNamesForType(ApplicationListener.class, true, false);
		for (String listenerBeanName : listenerBeanNames) {
			getApplicationEventMulticaster().addApplicationListenerBean(listenerBeanName);
		}

		// Publish early application events now that we finally have a multicaster...
		// 发布早期的应用程序事件
		Set<ApplicationEvent> earlyEventsToProcess = this.earlyApplicationEvents;
		this.earlyApplicationEvents = null;
		if (!CollectionUtils.isEmpty(earlyEventsToProcess)) {
			for (ApplicationEvent earlyEvent : earlyEventsToProcess) {
				getApplicationEventMulticaster().multicastEvent(earlyEvent);
			}
		}
	}
```

## 10 实例化所有非懒加载的单例 bean

**AbstractApplicationContext.finishBeanFactoryInitialization**

```java

```

### 10.1 conversionService 的使用

&emsp;&emsp;前面讲过使用自定义类型转换器将 String 转换为 Date 的方式，在 Spring 中还提供了另一种转换方式，使用 conversionService 来进行类型转换。

&emsp;&emsp;自定义一个类型转换器 String2DateConverter

```java
public class String2DateConverter implements Converter<String, Date> {
	@Override
	public Date convert(String source) {
		try {
			return DateUtils.parseDate(source,"yyyy-MM-dd");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
}
```

&emsp;&emsp;配置文件

```xml
<bean id="conversionService" class="org.springframework.context.support.ConversionServiceFactoryBean">
	<property name="converters">
		<list>
			<bean class="org.springframework.myTest.testConverter.String2DateConverter" />
		</list>
	</property>
</bean>
```

&emsp;&emsp;使用

```java
public static void main(String[] args) {
	ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("myTestResources/applicationContext.xml");
	ConversionService conversionService = context.getBeanFactory().getConversionService();
	Date convert = conversionService.convert("2020-12-10", Date.class);
	System.out.println(convert);
}
```

#### 10.1.2 源码结构

&emsp;&emsp;conversionService 的使用主要涉及一个类(ConversionServiceFactoryBean)和两个接口(ConversionService、Converter)，现在分别来看，先从配置文件配置的 ConversionServiceFactoryBean 入手。ConversionServiceFactoryBean 类的功能很简单，就是提供对 ConversionService 的创建和将具体的转换器 Converter 注册到 ConversionService 中的功能。

&emsp;&emsp;**ConversionService 的创建**：默认情况下 Spring 使用 DefaultConversionService 实例作为 ConversionService 的实现，子类可以重写该方法以自定义创建 ConversionService 实例，在创建 DefaultConversionService 时在其初始化方法 **addDefaultConverters()** 中已经添加了一些常用的转换器。

**ConversionServiceFactoryBean.createConversionService**

```java
protected GenericConversionService createConversionService() {
	return new DefaultConversionService();
}
```

&emsp;&emsp;**Converter 的注册**：此方法在设置了所有 bean 属性后在执行自定义 init 方法之前调用，用于将具体的 Converter 注册到刚创建的 DefaultConversionService 中。

**ConversionServiceFactoryBean.afterPropertiesSet**

```java
public void afterPropertiesSet() {
	this.conversionService = createConversionService();
	ConversionServiceFactory.registerConverters(this.converters, this.conversionService);
}
```

&emsp;&emsp;在使用时通过 ConversionService 的 convert() 方法来匹配合适的类型转换器完成属性转换。

### 10.2 实例化剩余的所有非懒加载单例 bean

&emsp;&emsp;ApplicationContext 在启动过程中就会将所有的单例 bean 全部实例化，这个过程在 DefaultListableBeanFactory 中实现。

**DefaultListableBeanFactory.preInstantiateSingletons**

```java
public void preInstantiateSingletons() throws BeansException {
	if (logger.isTraceEnabled()) {
		logger.trace("Pre-instantiating singletons in " + this);
	}

	// Iterate over a copy to allow for init methods which in turn register new bean definitions.
	// While this may not be part of the regular factory bootstrap, it does otherwise work fine.
	List<String> beanNames = new ArrayList<>(this.beanDefinitionNames);

	// Trigger initialization of all non-lazy singleton beans...
	// 遍历所有加载的 beandefinition 并依次实例化
	for (String beanName : beanNames) {
		RootBeanDefinition bd = getMergedLocalBeanDefinition(beanName);
		if (!bd.isAbstract() && bd.isSingleton() && !bd.isLazyInit()) {
			if (isFactoryBean(beanName)) {
				Object bean = getBean(FACTORY_BEAN_PREFIX + beanName);
				if (bean instanceof FactoryBean) {
					FactoryBean<?> factory = (FactoryBean<?>) bean;
					boolean isEagerInit;
					if (System.getSecurityManager() != null && factory instanceof SmartFactoryBean) {
						isEagerInit = AccessController.doPrivileged(
								(PrivilegedAction<Boolean>) ((SmartFactoryBean<?>) factory)::isEagerInit,
								getAccessControlContext());
					}
					else {
						isEagerInit = (factory instanceof SmartFactoryBean &&
								((SmartFactoryBean<?>) factory).isEagerInit());
					}
					if (isEagerInit) {
						getBean(beanName);
					}
				}
			}
			else {
				getBean(beanName);
			}
		}
	}

	// Trigger post-initialization callback for all applicable beans...
	// 为所有适用的 bean 触发初始化后回调方法
	for (String beanName : beanNames) {
		Object singletonInstance = getSingleton(beanName);
		if (singletonInstance instanceof SmartInitializingSingleton) {
			StartupStep smartInitialize = this.getApplicationStartup().start("spring.beans.smart-initialize")
					.tag("beanName", beanName);
			SmartInitializingSingleton smartSingleton = (SmartInitializingSingleton) singletonInstance;
			if (System.getSecurityManager() != null) {
				AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
					smartSingleton.afterSingletonsInstantiated();
					return null;
				}, getAccessControlContext());
			}
			else {
				smartSingleton.afterSingletonsInstantiated();
			}
			smartInitialize.end();
		}
	}
}
```

## 11 完成刷新，通知生命周期处理器刷新容器

&emsp;&emsp;完成此容器的刷新，调用 LifecycleProcessor 的 onRefresh（）方法和发布 ContextRefreshedEvent 事件。

**AbstractApplicationContext.finishRefresh**

```java
protected void finishRefresh() {
	// Clear context-level resource caches (such as ASM metadata from scanning).
	// 清除 resource 缓存
	clearResourceCaches();

	// Initialize lifecycle processor for this context.
	// 初始化 LifecycleProcessor 
	initLifecycleProcessor();

	// Propagate refresh to lifecycle processor first.
	// 调用 LifecycleProcessor 的 onRefresh
	getLifecycleProcessor().onRefresh();

	// Publish the final event.
	// 发布容器刷新完成事件
	publishEvent(new ContextRefreshedEvent(this));

	// Participate in LiveBeansView MBean, if active.
	if (!NativeDetector.inNativeImage()) {
		LiveBeansView.registerApplicationContext(this);
	}
}
```

### 11.1 Lifecycle 生命周期回调接口

&emsp;&emsp;生命周期接口定义了任何具有自身生命周期需求的对象的基本方法，任何 spring 管理的对象都可以实现该接口，然后，当 ApplicationContext 本身接收启动和停止信号(例如在运行时停止/重启场景)时，spring 容器将在容器上下文中找出所有实现了 LifeCycle 及其子类接口的类，并一一调用它们。spring 是通过委托给生命周期处理器 **LifecycleProcessor** 来实现这一点的。值得注意的是，目前的 Lifecycle 接口仅在顶级单例 bean 上受支持。 在任何其他组件上，Lifecycle 接口将保持未被检测到，因此将被忽略。

```java
public interface Lifecycle {
    /**
     * 启动当前组件
     * <p>如果组件已经在运行，不应该抛出异常
     * <p>在容器的情况下，这会将开始信号 传播到应用的所有组件中去。
     */
    void start();
    /**
     * (1)通常以同步方式停止该组件，当该方法执行完成后,该组件会被完全停止。当需要异步停止行为时，考虑实现SmartLifecycle 和它的 stop(Runnable) 方法变体。

　　注意，此停止通知在销毁前不能保证到达:　　　　在常规关闭时，{@code Lifecycle} bean将首先收到一个停止通知，然后才传播常规销毁回调;　　　　在上下文的生命周期内的刷新或中止时，只调用销毁方法
　　　　对于容器，这将把停止信号传播到应用的所有组件
     */
    void stop();

    /**
      *  检查此组件是否正在运行。
      *  1. 只有该方法返回false时，start方法才会被执行。
      *  2. 只有该方法返回true时，stop(Runnable callback)或stop()方法才会被执行。
      */
    boolean isRunning();
}
```

#### 11.1.1 LifecycleProcessor

&emsp;&emsp;LifecycleProcessor 本身就是 LifeCycle 接口的扩展，是用于在 ApplicationContext 中处理 Lifecycle bean 的策略接口，当 ApplicationContext 启动或停止时，它会通过 LifecycleProcessor 来与所有声明的 bean 的周期做状态更新，它还添加了另外两个方法来响应 spring 容器上下文的刷新 (onRefresh) 和关闭 (close)，而在 LifecycleProcessor 的使用前需要初始化。

**AbstractApplicationContext.initLifecycleProcessor**

```java
protected void initLifecycleProcessor() {
	ConfigurableListableBeanFactory beanFactory = getBeanFactory();
	if (beanFactory.containsLocalBean(LIFECYCLE_PROCESSOR_BEAN_NAME)) {
		this.lifecycleProcessor =
				beanFactory.getBean(LIFECYCLE_PROCESSOR_BEAN_NAME, LifecycleProcessor.class);
		if (logger.isTraceEnabled()) {
			logger.trace("Using LifecycleProcessor [" + this.lifecycleProcessor + "]");
		}
	}
	else {
		DefaultLifecycleProcessor defaultProcessor = new DefaultLifecycleProcessor();
		defaultProcessor.setBeanFactory(beanFactory);
		this.lifecycleProcessor = defaultProcessor;
		beanFactory.registerSingleton(LIFECYCLE_PROCESSOR_BEAN_NAME, this.lifecycleProcessor);
		if (logger.isTraceEnabled()) {
			logger.trace("No '" + LIFECYCLE_PROCESSOR_BEAN_NAME + "' bean, using " +
					"[" + this.lifecycleProcessor.getClass().getSimpleName() + "]");
		}
	}
}
```

&emsp;&emsp;和之前的套路一样，用户配置了就使用用户配置的，否则就使用默认的 DefaultLifecycleProcessor。

&emsp;&emsp;现在定义一个类实现 Lifecycle 接口。

```java
public class TestLifecycle implements Lifecycle {

	private boolean running = false;

	@Override
	public void start() {
		this.running = true;
		System.out.println("TestLifecycle is start");
	}

	@Override
	public void stop() {
		this.running = false;
		System.out.println("TestLifecycle is stop");
	}

	@Override
	public boolean isRunning() {
		return this.running;
	}

}
```

```java
public static void main(String[] args) {
	ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("myTestResources/applicationContext.xml");
}
```

```xml
<bean name="testLifecycle" class="org.springframework.myTest.TestLifecycle.TestLifecycle"/>
```

&emsp;&emsp;发现在容器启动过程中并没有自动调用代用自定义的 TestLifecycle 里实现的方法，此时就需要显示调用容器的启动和停止方法来实现调用。

```java
public static void main(String[] args) {
	ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("myTestResources/applicationContext.xml");
	context.start();
	context.close();
}
```

#### 11.1.2 SmartLifecycle

&emsp;&emsp;当想要自动的实现声明周期方法的回调时，就需要用到 SmartLifecycle 接口，SmartLifecycle 继承自 Lifecycle，并进行了以下拓展。

```java
public interface SmartLifecycle extends Lifecycle, Phased {

	int DEFAULT_PHASE = Integer.MAX_VALUE;
	
	//返回值指示是否应在上下文刷新时启动此对象
	default boolean isAutoStartup() {
		return true;
	}

	default void stop(Runnable callback) {
		stop();
		callback.run();
	}

	@Override
	default int getPhase() {
		return DEFAULT_PHASE;
	}
}
```

&emsp;&emsp;这个接口还扩展了 Phased 接口，当容器中实现了 Lifecycle 的多个类如果希望有顺序的进行回调时，那么启动和关闭调用的顺序可能很重要。如果任何两个对象之间存在依赖关系，那么依赖方将在依赖后开始，在依赖前停止。然而，有时直接依赖关系是未知的。您可能只知道某个类型的对象应该在另一个类型的对象之前开始。在这些情况下，SmartLifecycle 接口定义了另一个选项，即在其超接口上定义的 getPhase() 方法。getPhase() 方法的返回值指示了这个 Lifecycle 组件应该在哪个阶段启动和停止。当开始时，getPhase() 返回值最小的对象先开始，当停止时，遵循相反的顺序。因此，实现 SmartLifecycle 的对象及其 getPhase() 方法返回 Integer.MIN_VALUE 将在第一个开始和最后一个停止。相反，MAX_VALUE 将指示对象应该在最后启动并首先停止，例如：如果组件 B 依赖于组件 A 已经启动，那么组件 A 的相位值应该低于组件 B。在关闭过程中，组件 B 将在组件 A 之前停止。

&emsp;&emsp;容器中没有实现 SmartLifecycle 的任何 Lifecycle 组件都将被视为它们的阶段值为 0，因此，任何实现类的 phase 的值为负数时都表明一个对象应该在这些标准的生命周期回调之前进行执行，反之亦然。

&emsp;&emsp;SmartLifecycle 定义的 stop 方法接受一个回调。在实现的关闭过程完成之后，任何实现都必须调用回调的 run() 方法。这允许在必要时进行异步关闭，因为 LifecycleProcessor 接口，即 DefaultLifecycleProcessor，的默认实现，将等待每个阶段中的对象组的超时值来调用这个回调。默认的每个阶段超时为30秒，当然也可以通过配置来改变这一点。

#### 11.1.3 onRefresh

&emsp;&emsp;在 LifecycleProcessor 的默认实现即 DefaultLifecycleProcessor 中的 onRefresh 方法中启动了所有实现了接口的bean。

DefaultLifecycleProcessor.onRefresh

```java
public void onRefresh() {
	startBeans(true);
	this.running = true;
}

private void startBeans(boolean autoStartupOnly) {
	Map<String, Lifecycle> lifecycleBeans = getLifecycleBeans();
	Map<Integer, LifecycleGroup> phases = new TreeMap<>();
	lifecycleBeans.forEach((beanName, bean) -> {
		if (!autoStartupOnly || (bean instanceof SmartLifecycle && ((SmartLifecycle) bean).isAutoStartup())) {
			int phase = getPhase(bean);
			phases.computeIfAbsent(
					phase,
					p -> new LifecycleGroup(phase, this.timeoutPerShutdownPhase, lifecycleBeans, autoStartupOnly)
			).add(beanName, bean);
		}
	});
	if (!phases.isEmpty()) {
		phases.values().forEach(LifecycleGroup::start);
	}
}
```

### 11.2 发布容器刷新完成事件

&emsp;&emsp;当完成 ApplicationContext 初始化的时候，将通过 Spring 中的事件发布机制来发出 ContextRefreshedEvent 事件，以保证对应的监听器可以做进一步的处理。

