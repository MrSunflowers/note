# Spring源码解析九：ApplicationContext 容器启动流程

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
	// 初始化 BeanDefinitionReader ，支持子类覆盖拓展
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

&emsp;&emsp;使用自定义属性编辑器，通过继承 PropertyEditorSupport ，覆盖 setAsText 方法：

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

&emsp;&emsp;这里注册了一系列的常用的属性编辑器，注册后，一旦某个实体 bean 中存在一些注册的类型属性，Spring 就会调用其对应的属性编辑器来进行类型转换并赋值。通过调试发现 registerCustomEditors 方法是在实例化 bean，并将 BeanDefinition 转换为 BeanWrapper 的 initBeanWrapper 方法中调用的。

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

&emsp;&emsp;到此逻辑已经明了，在 Spring 中，BeanWrapper 除了作为封装 bean 的容器之外，它还间接继承了 PropertyEditorRegistry ，在将 BeanDefinition 转换为 BeanWrapper 的过程中就会调用 registerCustomEditors 方法将一些 Spring 自带的属性编辑器注册，注册后，在属性填充的环节就可以直接让 Spring 使用这些编辑器来进行属性的解析转换操作了。而 BeanWrapper 的默认实现 BeanWrapperImpl 还继承了 PropertyEditorRegistrySupport，在 PropertyEditorRegistrySupport 中还有这样一个方法：

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

&emsp;&emsp;PropertyPlaceholderConfigurer 间接继承了 BeanFactoryPostProcessor 接口，这就是问题的关键，当 Spring 加载任何实现了此接口的 bean 时，都会在 BeanFactory 载入所有 bean 配置之后执行其 postProcessBeanFactory 方法，而 PropertyPlaceholderConfigurer 正时在对 postProcessBeanFactory 方法的实现中完成了配置文件的读取转换工作。