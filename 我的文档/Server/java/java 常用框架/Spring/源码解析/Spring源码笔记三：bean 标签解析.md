# Spring源码笔记三：bean 标签解析



&emsp;&emsp;`BeanDefinitionDocumentReader` 接口用于解析包含 Spring bean 定义信息的 XML 文档，并根据要解析的文档实例化，它根据 Spring 的默认 XML bean 定义格式读取 bean 定义信息，读取所需 XML 文档的结构、元素和属性名称在此类中进行了硬编码，此类将解析 XML 文件中的所有 bean 定义元素，其中保存了传递过来封装好了的上下文对象和真正用于解析元素的委托类。接上文，来看默认实现 `DefaultBeanDefinitionDocumentReader` 的`doRegisterBeanDefinitions` 方法。

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/spring/202111011721895.png)

| BeanDefinitionDocumentReader 封装的部分属性 |           说明           |
| :-----------------------------------------: | :----------------------: |
|              XmlReaderContext               |   封装好了的上下文对象   |
|        BeanDefinitionParserDelegate         | 真正用于解析元素的委托类 |

## 1. 解析开始

**DefaultBeanDefinitionDocumentReader.doRegisterBeanDefinitions**

```java
protected void doRegisterBeanDefinitions(Element root) {
    //1.创建处理属性的专门处理的解析器
   BeanDefinitionParserDelegate parent = this.delegate;
   this.delegate = createDelegate(getReaderContext(), root, parent);
	//2.处理 profile 属性
   if (this.delegate.isDefaultNamespace(root)) {
      String profileSpec = root.getAttribute(PROFILE_ATTRIBUTE);
      if (StringUtils.hasText(profileSpec)) {
         String[] specifiedProfiles = StringUtils.tokenizeToStringArray(
               profileSpec, BeanDefinitionParserDelegate.MULTI_VALUE_ATTRIBUTE_DELIMITERS);
			   //判断配置文件是否被激活
         if (!getReaderContext().getEnvironment().acceptsProfiles(specifiedProfiles)) {
            if (logger.isDebugEnabled()) {
               logger.debug("Skipped XML bean definition file due to specified profiles [" + profileSpec +
                     "] not matching: " + getReaderContext().getResource());
            }
            return;
         }
      }
   }
   //解析预处理
   preProcessXml(root);
   //3.标签解析
   parseBeanDefinitions(root, this.delegate);
   //解析后处理
   postProcessXml(root);

   this.delegate = parent;
}
```

&emsp;&emsp;进入方法首先利用封装的 readerContext 创建了一个用于解析 XML bean definitions 的委托类 BeanDefinitionParserDelegate。

### 1.1 beans 标签和 default 值设置

**DefaultBeanDefinitionDocumentReader.createDelegate 创建解析器**

```java
protected BeanDefinitionParserDelegate createDelegate(
      XmlReaderContext readerContext, Element root, @Nullable BeanDefinitionParserDelegate parentDelegate) {
   //1.实例化和给 BeanDefinitionParserDelegate 设置默认的 readerContext
   BeanDefinitionParserDelegate delegate = new BeanDefinitionParserDelegate(readerContext);
    //2.初始化默认的延迟初始化、自动装配、依赖项检查设置、初始化方法、销毁方法等
   delegate.initDefaults(root, parentDelegate);
   return delegate;
}
```

**initDefaults 解析设置 default**

**BeanDefinitionParserDelegate.initDefaults**

```java
public void initDefaults(Element root, @Nullable BeanDefinitionParserDelegate parent) {
   //1.填充默认属性
   populateDefaults(this.defaults, (parent != null ? parent.defaults : null), root);
   //2.触发 EmptyReaderEventListener 的 defaultsRegistered 事件 ，EmptyReaderEventListener 是在封装 readerContext 时构建的
   this.readerContext.fireDefaultsRegistered(this.defaults);
}
```

&emsp;&emsp;这里主要就是解析设置 Spring `<beans>` 标签中以 default 开头的属性，比如设置是否使用懒加载启动容器、默认的自动装配方式、初始化方法等，Spring 将这些属性抽象成了一个 `DocumentDefaultsDefinition` 简单 JavaBean 对象，里面放的就是这些值和 get，set 方法。

**BeanDefinitionParserDelegate.populateDefaults**

&emsp;&emsp;`populateDefaults` 是具体的设置 `DocumentDefaultsDefinition` 方法，大体思路就是以前设置过就用以前设置的，否则使用默认值。

```java
protected void populateDefaults(DocumentDefaultsDefinition defaults, @Nullable DocumentDefaultsDefinition parentDefaults, Element root) {
    //设置懒加载 
    //默认值为false,会加载全部对象实例图，使项目启动速度变慢。在项目开发中可用true,项目运行中一般用false
   String lazyInit = root.getAttribute(DEFAULT_LAZY_INIT_ATTRIBUTE);
   if (isDefaultValue(lazyInit)) {
      // Potentially inherited from outer <beans> sections, otherwise falling back to false.
      lazyInit = (parentDefaults != null ? parentDefaults.getLazyInit() : FALSE_VALUE);
   }
   defaults.setLazyInit(lazyInit);
	//合并设置 默认为 false 
   String merge = root.getAttribute(DEFAULT_MERGE_ATTRIBUTE);
   if (isDefaultValue(merge)) {
      // Potentially inherited from outer <beans> sections, otherwise falling back to false.
      merge = (parentDefaults != null ? parentDefaults.getMerge() : FALSE_VALUE);
   }
   defaults.setMerge(merge);
	//配置的所有bean默认的自动装配行为 默认为no 有四种类型：byname,bytype,constructor,autodetect,no
   String autowire = root.getAttribute(DEFAULT_AUTOWIRE_ATTRIBUTE);
   if (isDefaultValue(autowire)) {
      // Potentially inherited from outer <beans> sections, otherwise falling back to 'no'.
      autowire = (parentDefaults != null ? parentDefaults.getAutowire() : AUTOWIRE_NO_VALUE);
   }
   defaults.setAutowire(autowire);
	//配置的所有bean默认是否作为自动装配的候选Bean 默认为 null
   if (root.hasAttribute(DEFAULT_AUTOWIRE_CANDIDATES_ATTRIBUTE)) {
      defaults.setAutowireCandidates(root.getAttribute(DEFAULT_AUTOWIRE_CANDIDATES_ATTRIBUTE));
   }
   else if (parentDefaults != null) {
      defaults.setAutowireCandidates(parentDefaults.getAutowireCandidates());
   }
	//配置的所有bean默认的初始化方法 默认为 null 
    //初始化方法，此方法将在BeanFactory创建JavaBean实例之后，在向应用层返回引用之前执行
   if (root.hasAttribute(DEFAULT_INIT_METHOD_ATTRIBUTE)) {
      defaults.setInitMethod(root.getAttribute(DEFAULT_INIT_METHOD_ATTRIBUTE));
   }
   else if (parentDefaults != null) {
      defaults.setInitMethod(parentDefaults.getInitMethod());
   }
	//配置的所有bean默认的回收方法 默认为 null 此方法将在BeanFactory销毁的时候执行，一般用于资源释放。
   if (root.hasAttribute(DEFAULT_DESTROY_METHOD_ATTRIBUTE)) {
      defaults.setDestroyMethod(root.getAttribute(DEFAULT_DESTROY_METHOD_ATTRIBUTE));
   }
   else if (parentDefaults != null) {
      defaults.setDestroyMethod(parentDefaults.getDestroyMethod());
   }
	//设置 source 为 null  表示不需要存储源 防止在正常运行时使用期间将过多元数据保存在内存中
   defaults.setSource(this.readerContext.extractSource(root));
}
```

&emsp;&emsp;`<beans>`元素下所指定的属性都可以在每个`<bean>`子元素中单独指定，只要将属性名去掉default 即可，这样就可以对特定 bean 起作用。在解析设置完 beans 标签中以 default 开头的属性后，触发 EmptyReaderEventListener 的 defaultsRegistered 事件，表示 default 属性已经设置完毕，这里就用到了在 readerContext 中封装的监听器。

### 1.2.  处理 profile 属性

&emsp;&emsp;回到一开始的 `DefaultBeanDefinitionDocumentReader` 的 `doRegisterBeanDefinitions` 方法，接着往下看。先判断是否是默认的命名空间 `isDefaultNamespace`，也就是 http://www.springframework.org/schema/beans ，然后判断了一下当前有没有配置 profile 属性，如果有则使用当前激活的配置文件。

&emsp;&emsp;profile 属性用于在配置文件中部署多套配置来使用不同的开发环境，这样就可以方便的就那些切换开发、部署环境，最常用的就是更换不同的数据库。首先程序会获取 beans 节点是否定义了 profile 属性，如果定义了则会需要到环境变量中去寻找，因为 profile 是可以同时指定多个的，需要程序对其拆分，并解析每个 profile 是都符合环境变量所定义的，不定义则不会浪费性能去解析。

### 1.3.  标签解析 

**DefaultBeanDefinitionDocumentReader.parseBeanDefinitions**

&emsp;&emsp;接着往下，可以看到 preProcessXml 和 postProcessXml 方法都是空实现，分别对应 xml 解析的预处理和后处理，如果需要在标签解析前后做一些处理的话，那么只需要继承此类并重写这两个方法即可。

&emsp;&emsp;在处理完 profile 属性就开始进行 xml 文件的读取了，进入 `parseBeanDefinitions` 方法。

```java
protected void parseBeanDefinitions(Element root, BeanDefinitionParserDelegate delegate) {
   //判断默认 namespace
   if (delegate.isDefaultNamespace(root)) {
      NodeList nl = root.getChildNodes();
      for (int i = 0; i < nl.getLength(); i++) {
         Node node = nl.item(i);
         if (node instanceof Element) {
            Element ele = (Element) node;
            if (delegate.isDefaultNamespace(ele)) {
               //默认标签的解析处理，其中就包含 bean 标签的解析
               parseDefaultElement(ele, delegate);
            }
            else {
               //自定义标签的解析处理
               delegate.parseCustomElement(ele);
            }
         }
      }
   }
   else {
      //自定义标签的解析处理
      delegate.parseCustomElement(root);
   }
}
```

&emsp;&emsp;在 Spring 的 xml 配置中有两大类标签声明，一种是默认的，例如 

```xml
<bean id="myTestBean" class="myTest.MyTestBean"></bean>
```

&emsp;&emsp;另外就是自定义的标签，例如：

```xml
<tx:annotation-driven/>
```

&emsp;&emsp;两种方式的读取和解析是不一样的，如果是默认的配置方式，Spring 可以自动解析，如果是自定义的，需要用户实现一些接口以及配置，在上面的代码中体现在默认 namespace 的判断上，对于根节点或子节点如果是默认命名空间，也就是默认的标签，使用 `parseDefaultElement` 方法解析，否则使用 `parseCustomElement` 方法解析自定义标签，下面先看默认标签的解析。

### 1.4 默认标签的解析

**DefaultBeanDefinitionDocumentReader.parseDefaultElement**

```java
private void parseDefaultElement(Element ele, BeanDefinitionParserDelegate delegate) {
   // import 标签
   if (delegate.nodeNameEquals(ele, IMPORT_ELEMENT)) {
      importBeanDefinitionResource(ele);
   }
   // alias 标签
   else if (delegate.nodeNameEquals(ele, ALIAS_ELEMENT)) {
      processAliasRegistration(ele);
   }
   // bean 标签
   else if (delegate.nodeNameEquals(ele, BEAN_ELEMENT)) {
      processBeanDefinition(ele, delegate);
   }
   // beans 标签
   else if (delegate.nodeNameEquals(ele, NESTED_BEANS_ELEMENT)) {
       //递归调用 doRegisterBeanDefinitions
      doRegisterBeanDefinitions(ele);
   }
}
```

&emsp;&emsp;默认标签解析在 `parseDefaultElement` 中进行，分别对4种不同标签做了不同的处理。其中对 bean 标签的解析最为复杂也最为重要，所有这里对默认标签的解析主要看一下 bean 标签的解析，进入 `processBeanDefinition` 方法。

## 2. bean 标签的解析

**DefaultBeanDefinitionDocumentReader.processBeanDefinition**

```java
protected void processBeanDefinition(Element ele, BeanDefinitionParserDelegate delegate) {
   //1.解析 bean 标签
   BeanDefinitionHolder bdHolder = delegate.parseBeanDefinitionElement(ele);
   if (bdHolder != null) {
      //2.当子节点下有自定义标签，对自定义标签进行解析
      bdHolder = delegate.decorateBeanDefinitionIfRequired(ele, bdHolder);
      try {
         //3.对解析后的的 bdHolder 进行注册
         BeanDefinitionReaderUtils.registerBeanDefinition(bdHolder, getReaderContext().getRegistry());
      }
      catch (BeanDefinitionStoreException ex) {
         getReaderContext().error("Failed to register bean definition with name '" +
               bdHolder.getBeanName() + "'", ele, ex);
      }
      // 4.触发注册完成监听事件
      getReaderContext().fireComponentRegistered(new BeanComponentDefinition(bdHolder));
   }
}
```

&emsp;&emsp;可以看到，bean 的解析工作又交给了 `parseBeanDefinitionElement` 方法，而它返回的 bdHolder 中已经包含了配置文件中各种配置信息，例如 bean 的 class、name、id、alias 等属性。

### 2.1  name 和  id 属性的处理

**BeanDefinitionParserDelegate.parseBeanDefinitionElement** 

```java
public BeanDefinitionHolder parseBeanDefinitionElement(Element ele, @Nullable BeanDefinition containingBean) {
   String id = ele.getAttribute(ID_ATTRIBUTE);
   String nameAttr = ele.getAttribute(NAME_ATTRIBUTE);
   //1.分割解析 name 属性
   List<String> aliases = new ArrayList<>();
   if (StringUtils.hasLength(nameAttr)) {
      String[] nameArr = StringUtils.tokenizeToStringArray(nameAttr, MULTI_VALUE_ATTRIBUTE_DELIMITERS);
      aliases.addAll(Arrays.asList(nameArr));
   }
   //2.解析 id 属性
   String beanName = id;
   if (!StringUtils.hasText(beanName) && !aliases.isEmpty()) {
      beanName = aliases.remove(0);
      if (logger.isTraceEnabled()) {
         logger.trace("No XML 'id' specified - using '" + beanName +
               "' as bean name and " + aliases + " as aliases");
      }
   }

   if (containingBean == null) {
      //3.检测当前 bean 的唯一性
      checkNameUniqueness(beanName, aliases, ele);
   }
   //4. 进一步解析其他所有属性并统一封装至 GenericBeanDefinition 类型的实例中
   AbstractBeanDefinition beanDefinition = parseBeanDefinitionElement(ele, beanName, containingBean);
   //5. 如果检测到 bean 没有指定 beanName ，那么使用默认规则为此 Bean 生成 beanName。
   // 默认使用 bean className + # + 哈希码
   if (beanDefinition != null) {
      if (!StringUtils.hasText(beanName)) {
         try {
            if (containingBean != null) {
               beanName = BeanDefinitionReaderUtils.generateBeanName(
                     beanDefinition, this.readerContext.getRegistry(), true);
            }
            else {
               beanName = this.readerContext.generateBeanName(beanDefinition);
               // Register an alias for the plain bean class name, if still possible,
               // if the generator returned the class name plus a suffix.
               // This is expected for Spring 1.2/2.0 backwards compatibility.
               String beanClassName = beanDefinition.getBeanClassName();
               if (beanClassName != null &&
                     beanName.startsWith(beanClassName) && beanName.length() > beanClassName.length() &&
                     !this.readerContext.getRegistry().isBeanNameInUse(beanClassName)) {
                  aliases.add(beanClassName);
               }
            }
            if (logger.isTraceEnabled()) {
               logger.trace("Neither XML 'id' nor 'name' specified - " +
                     "using generated bean name [" + beanName + "]");
            }
         }
         catch (Exception ex) {
            error(ex.getMessage(), ele);
            return null;
         }
      }
      String[] aliasesArray = StringUtils.toStringArray(aliases);
      //6.将获取到的信息封装到 BeanDefinitionHolder 的实例中
      return new BeanDefinitionHolder(beanDefinition, beanName, aliasesArray);
   }

   return null;
}
```

### 2.2 其他属性的解析

&emsp;&emsp;前面获取 name 和 id 属性并验证唯一性并没有什么可说的，主要逻辑已经写在注释里了，直接看第4步，获取 AbstractBeanDefinition 。

**BeanDefinitionParserDelegate.parseBeanDefinitionElement** 

```java
public AbstractBeanDefinition parseBeanDefinitionElement(
      Element ele, String beanName, @Nullable BeanDefinition containingBean) {

   this.parseState.push(new BeanEntry(beanName));
   //1.获取 class 属性
   String className = null;
   if (ele.hasAttribute(CLASS_ATTRIBUTE)) {
      className = ele.getAttribute(CLASS_ATTRIBUTE).trim();
   }
   //2.获取 parent 属性
   String parent = null;
   if (ele.hasAttribute(PARENT_ATTRIBUTE)) {
      parent = ele.getAttribute(PARENT_ATTRIBUTE);
   }

   try {
      //3.创建用于存储 bean 的基本属性的 BeanDefinition
      AbstractBeanDefinition bd = createBeanDefinition(className, parent);
	  //4.解析 bean 的各种属性
      parseBeanDefinitionAttributes(ele, beanName, containingBean, bd);
      //5.提取 description 信息
      bd.setDescription(DomUtils.getChildElementValueByTagName(ele, DESCRIPTION_ELEMENT));
	  //6.解析元数据
      parseMetaElements(ele, bd);
      //7.解析 lookup-method 属性
      parseLookupOverrideSubElements(ele, bd.getMethodOverrides());
      //8.解析 replaced-method 属性
      parseReplacedMethodSubElements(ele, bd.getMethodOverrides());
	  //9.解析构造参数
      parseConstructorArgElements(ele, bd);
      //10.解析 property 元素
      parsePropertyElements(ele, bd);
      //11.解析 qualifier 元素
      parseQualifierElements(ele, bd);
	  
      bd.setResource(this.readerContext.getResource());
      bd.setSource(extractSource(ele));

      return bd;
   }
   catch (ClassNotFoundException ex) {
      error("Bean class [" + className + "] not found", ele, ex);
   }
   catch (NoClassDefFoundError err) {
      error("Class that bean class [" + className + "] depends on not found", ele, err);
   }
   catch (Throwable ex) {
      error("Unexpected failure during bean definition parsing", ele, ex);
   }
   finally {
      this.parseState.pop();
   }

   return null;
}
```

&emsp;&emsp;在这里可以很清晰的看到 bean 所有主要属性的解析工作。

#### 2.2.1 创建 BeanDefinition

在前面说过，BeanDefinition 是一个接口，它在 Spring 中存在着三种常用实现

- RootBeanDefinition
- GenericBeanDefinition
- ChildBeanDefinition

&emsp;&emsp;这三种实现均继承了 `AbstractBeanDefinition` ，其中 BeanDefinition 是配置文件中 `<bean>` 元素标签在容器中的内部表示形式，`<bean>` 元素标签拥有的 class、scope、lazy-init 等配置属性，在 BeanDefinition 中提供了相对应的 beanClass、scope、lazyInit 属性，BeanDefinition 和 `<bean>`  中的属性一一对应。其中 `RootBeanDefinition` 是最常用的实现类，它对应一般的 `<bean>` 元素标签 ，`GenericBeanDefinition` 是自 2.5 版本以后新加入的 bean 文件配置属性定义类，用于标准的、基本的 BeanDefinition。

&emsp;&emsp;在配置文件中可以定义父 `<bean>` 和子 `<bean>` ，父 `<bean>` 用 `RootBeanDefinition` 表示，而子 `<bean>` 用 `ChildBeanDefinition` 表示，而没有父 `<bean>` 的 `<bean>` 使用 `RootBeanDefinition` 表示，这也是说它是最常用的原因之一，`AbstractBeanDefinition` 对三者共同的类信息进行抽象。

&emsp;&emsp;也就是说，`RootBeanDefinition` 只能表示没有父类的 `<bean>` ，这在源码中也有体现，它的 getParentName 方法永远返回为 null ，而不接受 setParentName 设置的值，而 `ChildBeanDefinition` 则相反，其 parentName 属性不能为空，也就是必须要有父 bean。

&emsp;&emsp;Spring 通过 `GenericBeanDefinition` 将配置文件中的 `<bean>` 配置信息转换为容器的内部表示，并将这些 BeanDefinition 注册到 `BeanDefinitonRegistry` 中，Spring 容器的 `BeanDefinitionRegistrγ` 就像是  Spring 配置信息的内存数据库，主要是以 map 的形式保存，后续操作直接从 `BeanDefinitionRegistrγ` 中读取配置信息。

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/spring/BeanDefinition.png)

&emsp;&emsp;由此可知，要解析属性，首先要创建用于承载属性的实例，也就是创建 GenericBeanDefinition 类型的实例，而上面第3部的createBeanDefinition(className, parent) 的作用就是实现此功能。

**BeanDefinitionReaderUtils.createBeanDefinition**

```java
public static AbstractBeanDefinition createBeanDefinition(
      @Nullable String parentName, @Nullable String className, @Nullable ClassLoader classLoader) throws ClassNotFoundException {

   GenericBeanDefinition bd = new GenericBeanDefinition();
   bd.setParentName(parentName);
   if (className != null) {
      //如果 classLoader 为不为空则加载 Class 否则只记录className
      if (classLoader != null) {
         bd.setBeanClass(ClassUtils.forName(className, classLoader));
      }
      else {
         bd.setBeanClassName(className);
      }
   }
   return bd;
}
```

##### AbstractBeanDefinition 属性

&emsp;&emsp;GenericBeanDefinition 继承并实现了 AbstractBeanDefinition，大部分的属性都保存在AbstractBeanDefinition 中。

【主要属性】

```java
	//指示 bean 的作用范围，对应 bean 的 scope 属性
	@Nullable
	private String scope = SCOPE_DEFAULT;

	//是否抽象，对应 bean 属性的 abstract
	private boolean abstractFlag = false;

	//是否延迟加载，对应 bean 属性的 lazy-init
	@Nullable
	private Boolean lazyInit;

	//自动注入的模式，对应 bean 属性的 autowire
	private int autowireMode = AUTOWIRE_NO;

	//依赖检查
	private int dependencyCheck = DEPENDENCY_CHECK_NONE;

	//表示 bean 的实例化需要依靠另一个 bean 先实例化，对应属性 depends-on
	@Nullable
	private String[] dependsOn;
	/**
	 * 是否作为候选者，也就是说在容器查找自动装配的对象时， <br>
	 * 将不考虑该 bean ，但其本身还是可以使用自动装配来注入其他bean， <br>
	 * 对应 autowire-candidate
	 */ 
	private boolean autowireCandidate = true;

	//当作为自动装配的候选者时，是否作为首选
	private boolean primary = false;

	//用于记录 Qualifier
	private final Map<String, AutowireCandidateQualifier> qualifiers = new LinkedHashMap<>();

	/**
	 * 替代工厂方法（包含静态工厂）或者构造器创建对象，但是其后面的生命周期回调不影响。 <br>
	 * 框架在创建对象的时候会校验这个instanceSupplier是否有值，有的话，调用这个字段获取对象。 <br>
	 * 不管是静态工厂还是工厂方法，都需要通过反射调用目标方法创建对象，反射或多或少影响性能。 <br>
	 * 从 java 8 开始支持函数式接口编程，可以提供一个回调方法，直接调用回调方法即可获取对象，不需要通过反射。
	 */ 
	@Nullable
	private Supplier<?> instanceSupplier;

	//允许访问非公开的构造器和方法
	private boolean nonPublicAccessAllowed = true;

	/**
	 是否以一种宽松的模式解析构造函数，默认为true <br>
	 如果为 false 则在下面情况下 <br>
	 interface ITest{}  <br>
	 class ITestImpl implements ITest{};  <br>
	 class Main{  <br>
	 Main(ITest i){}  <br>
	 Main(ITestImpl i){}  <br>
	 }  <br>
	 抛出异常，因为 Spring 无法准确定位哪个构造函数
	 */
	private boolean lenientConstructorResolution = true;

	//对应 bean 属性 factory-bean
	@Nullable
	private String factoryBeanName;

	//对应 bean 属性 factory-method
	@Nullable
	private String factoryMethodName;

	//用于存储构造函数注入属性，对应 bean 的 constructor-arg
	@Nullable
	private ConstructorArgumentValues constructorArgumentValues;

	//用于存储普通属性
	@Nullable
	private MutablePropertyValues propertyValues;

	//存储重写方法，记录 lookup-method、replaced-method 元素
	private MethodOverrides methodOverrides = new MethodOverrides();

	//对应 bean 属性 init-method
	@Nullable
	private String initMethodName;

	//对应 bean 属性 destroy-method
	@Nullable
	private String destroyMethodName;

	//是否执行 init-method
	private boolean enforceInitMethod = true;

	//是否执行 destroy-method
	private boolean enforceDestroyMethod = true;

	//是否是用户定义的而不是应用程程序本身定义的，创建 AOP 时为 true
	private boolean synthetic = false;

	/**
	 * 定义 bean 的作用域  <br>
	 * APPLICATION：用户  <br>
	 * INFRASTRUCTURE：内部使用，与用户无关  <br>
	 * SUPPORT 某些复杂配置的一部分
	 */
	private int role = BeanDefinition.ROLE_APPLICATION;

	//bean 的描述信息
	@Nullable
	private String description;

```

#### 2.2.2 解析 bean 属性

&emsp;&emsp;当创建了 BeanDefinition 后，接下来就是填充 BeanDefinition 中的各种属性，一步一步来看，`parseBeanDefinitionAttributes` 方法是对 element 所有元素进行解析，这里 Spring 完成了对所有 bean 属性的解析。

**BeanDefinitionParserDelegate.parseBeanDefinitionAttributes** 

```java
public AbstractBeanDefinition parseBeanDefinitionAttributes(Element ele, String beanName,
			@Nullable BeanDefinition containingBean, AbstractBeanDefinition bd) {
		// 1. 解析 socpe 属性
    	//  singleton 属性已经被 scope 替代
		if (ele.hasAttribute(SINGLETON_ATTRIBUTE)) {
			error("Old 1.x 'singleton' attribute in use - upgrade to 'scope' declaration", ele);
		}
        else if (ele.hasAttribute(SCOPE_ATTRIBUTE)) {
			bd.setScope(ele.getAttribute(SCOPE_ATTRIBUTE));
		}
		else if (containingBean != null) {
			//在嵌入 beanDifinition 情况下
            //没有单独指定 scope 属性且 containingBean 不为空则使用父类默认属性
			bd.setScope(containingBean.getScope());
		}
		//2. abstract 属性解析
		if (ele.hasAttribute(ABSTRACT_ATTRIBUTE)) {
			bd.setAbstract(TRUE_VALUE.equals(ele.getAttribute(ABSTRACT_ATTRIBUTE)));
		}
		//3. 懒加载，默认为 false
		String lazyInit = ele.getAttribute(LAZY_INIT_ATTRIBUTE);
		if (isDefaultValue(lazyInit)) {
			lazyInit = this.defaults.getLazyInit();
		}
		bd.setLazyInit(TRUE_VALUE.equals(lazyInit));
		//4. 自动注入方式 
		String autowire = ele.getAttribute(AUTOWIRE_ATTRIBUTE);
		bd.setAutowireMode(getAutowireMode(autowire));
		//5. depends-on 属性解析
    	//有时候在创建某个Bean之前，需要先创建另一个Bean，这时使用 depends-on 属性。
		if (ele.hasAttribute(DEPENDS_ON_ATTRIBUTE)) {
			String dependsOn = ele.getAttribute(DEPENDS_ON_ATTRIBUTE);
			bd.setDependsOn(StringUtils.tokenizeToStringArray(dependsOn, MULTI_VALUE_ATTRIBUTE_DELIMITERS));
		}
		//6. autowire-candidate
    	//是否作为其它 bean 自动装配的候选者
    	//当 spring <bean> 标签的 autowire-candidate 属性设置为 false 时，
    	//容器在查找自动装配对象时，将不考虑该 bean，
    	//即该 bean 不会被作为其它bean自动装配的候选者，
    	//但该bean本身还是可以使用自动装配来注入其它bean的。
		String autowireCandidate = ele.getAttribute(AUTOWIRE_CANDIDATE_ATTRIBUTE);
		if (isDefaultValue(autowireCandidate)) {
			String candidatePattern = this.defaults.getAutowireCandidates();
			if (candidatePattern != null) {
				String[] patterns = StringUtils.commaDelimitedListToStringArray(candidatePattern);
				bd.setAutowireCandidate(PatternMatchUtils.simpleMatch(patterns, beanName));
			}
		}
		else {
			bd.setAutowireCandidate(TRUE_VALUE.equals(autowireCandidate));
		}
		// 7. primary 属性 默认为 false 
    	// 当一个 bean 的primary 设置为true，然后容器中有多个与该bean相同类型的其他bean，
    	// 此时，当使用@Autowired想要注入一个这种类型的bean时，
    	// 就不会因为容器中存在多个该类型的bean而出现异常。而是优先使用primary为true的bean。
    	// 如果容器中不仅有多个该类型的bean，而且这些bean中有多个的primary的值设置为true，
    	// 那么使用byType注入还是会出错，此时需要使用 qualifier 属性
		if (ele.hasAttribute(PRIMARY_ATTRIBUTE)) {
			bd.setPrimary(TRUE_VALUE.equals(ele.getAttribute(PRIMARY_ATTRIBUTE)));
		}
		//8.init_method 初始化方法
		if (ele.hasAttribute(INIT_METHOD_ATTRIBUTE)) {
			String initMethodName = ele.getAttribute(INIT_METHOD_ATTRIBUTE);
			bd.setInitMethodName(initMethodName);
		}
		else if (this.defaults.getInitMethod() != null) {
			bd.setInitMethodName(this.defaults.getInitMethod());
			bd.setEnforceInitMethod(false);
		}
		//9. destroy_method 销毁方法 
		if (ele.hasAttribute(DESTROY_METHOD_ATTRIBUTE)) {
			String destroyMethodName = ele.getAttribute(DESTROY_METHOD_ATTRIBUTE);
			bd.setDestroyMethodName(destroyMethodName);
		}
		else if (this.defaults.getDestroyMethod() != null) {
			bd.setDestroyMethodName(this.defaults.getDestroyMethod());
			bd.setEnforceDestroyMethod(false);
		}
		// 10. factory-method 工厂方法
		if (ele.hasAttribute(FACTORY_METHOD_ATTRIBUTE)) {
			bd.setFactoryMethodName(ele.getAttribute(FACTORY_METHOD_ATTRIBUTE));
		}
		if (ele.hasAttribute(FACTORY_BEAN_ATTRIBUTE)) {
			bd.setFactoryBeanName(ele.getAttribute(FACTORY_BEAN_ATTRIBUTE));
		}

		return bd;
	}
```

#### 2.2.3 解析 mate 元数据

```xml
<bean id="myTestDomain" class="myTest.MyTestDomain" >
   <meta key="testStr" value="testStrValue"/>
</bean>
```

&emsp;&emsp;回到 2.2 小节，在基本属性解析完成后，接下来是元数据 mate 的解析。上面这段代码并不会体现在 MyTestDomain 的属性当中，而是一个额外的声明，官方的说法是附加到 `BeanDefinition` 的任意元数据，当需要使用里面的信息的时候可以通过 `BeanDefinition.getAttribute(key)` 方法进行获取，对 mate 属性的解析代码：

**BeanDefinitionParserDelegate.parseMetaElements**


```java
public void parseMetaElements(Element ele, BeanMetadataAttributeAccessor attributeAccessor) {
    //获取当前节点的所有子元素
   NodeList nl = ele.getChildNodes();
   for (int i = 0; i < nl.getLength(); i++) {
      Node node = nl.item(i);
       //提取meta
      if (isCandidateElement(node) && nodeNameEquals(node, META_ELEMENT)) {
         Element metaElement = (Element) node;
         String key = metaElement.getAttribute(KEY_ATTRIBUTE);
         String value = metaElement.getAttribute(VALUE_ATTRIBUTE);
          // 使用 key 、value 封装为 BeanMetadataAttribute
         BeanMetadataAttribute attribute = new BeanMetadataAttribute(key, value);
         attribute.setSource(extractSource(metaElement));
          // 将 meta 信息 记录到 BeanMetadataAttributeAccessor 中
         attributeAccessor.addMetadataAttribute(attribute);
      }
   }
}
```

#### 2.2.4 解析 lookup-method 属性

&emsp;&emsp;子元素 lookup-method 属性是一种特殊的方法注入，它是把一个方法声明为返回某种类型的 bean ，但实际要返回的 bean 是在配置文件里面配置的，此方法可用在设计有些可插拔的功能上，解除程序依赖。举个例子，现在有一个接口和两个实现类：

**Test 接口**

```java
public interface Test {
   public void doTest();
}
```

**Test1 类**

```java
public class Test1 implements Test {
   @Override
   public void doTest() {
      System.out.println("Test1");
   }
}
```

**Test2 类**

```java
public class Test2 implements Test {
   @Override
   public void doTest() {
      System.out.println("Test2");
   }
}
```

&emsp;&emsp;在平时工作中，经常有在不同情况下使用不同的实现类的需求，这时候就可以使用 lookup-method 属性来配置。

**需要使用 Test 接口的类 GetBeanTest**

```java
public abstract class GetBeanTest {
   public void showMe(){
      this.getBean().doTest();
   }

   protected abstract Test getBean();
}
```

&emsp;&emsp;这里在获取 Test 的实现类的时候并没有在代码里规定，而是使用一个抽象方法来获取这里需要使用的 bean，而获取具体 bean 的操作交由  Spring 来完成，配置文件：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd"
>
	<!-- 这里lookup-method就表示在GetBeanTest中的getBean方法返回的是test1 -->
   <bean id="getBeanTest" class="myTest.lookup.GetBeanTest" >
      <lookup-method name="getBean" bean="test1"></lookup-method>
   </bean>

   <bean id="test1" class="myTest.lookup.Test1">
   </bean>

</beans>
```

**main**

```java
public static void main(String[] args) {
   Resource resource = new ClassPathResource("myTestResources/applicationContext.xml");
   BeanFactory beanFactory = new XmlBeanFactory(resource);
   GetBeanTest getBeanTest = (GetBeanTest)beanFactory.getBean("getBeanTest");
   getBeanTest.showMe();//Test1
}
```

【属性解析】

**BeanDefinitionParserDelegate.parseLookupOverrideSubElements**

```java
public void parseLookupOverrideSubElements(Element beanEle, MethodOverrides overrides) {
   NodeList nl = beanEle.getChildNodes();
   for (int i = 0; i < nl.getLength(); i++) {
      Node node = nl.item(i);
       //当且仅当在Spring默认Bean的子元素下且元素为lookup-method时有效
      if (isCandidateElement(node) && nodeNameEquals(node, LOOKUP_METHOD_ELEMENT)) {
         Element ele = (Element) node;
          //获取方法名
         String methodName = ele.getAttribute(NAME_ATTRIBUTE);
          //获取bean
         String beanRef = ele.getAttribute(BEAN_ELEMENT);
         LookupOverride override = new LookupOverride(methodName, beanRef);
         override.setSource(extractSource(ele));
         overrides.addOverride(override);
      }
   }
}
```

#### 2.2.5 解析 replaced-method 属性

&emsp;&emsp;方法替换：可以在运行时用新的方法替换现有的方法。与之前的 lookup-method 不同的是，replaced-method 不但可以动态地替换返回实体 bean , 而且还能动态地更改原有方法的逻辑。

【示例】

需要被替换的方法 doTest

```java
public class Test1{
   public void doTest() {
      System.out.println("Test1");
   }
}
```

用来替换的方法，需要实现 MethodReplacer 接口

```java
public class Test2 implements MethodReplacer {

   @Override
   public Object reimplement(Object obj, Method method, Object[] args) throws Throwable {
      System.out.println("Test2");
      return null;
   }
}
```

配置文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd"
>

   <bean id="test1" class="myTest.replaced.Test1">
      <replaced-method name="doTest" replacer="test2"></replaced-method>
   </bean>

   <bean id="test2" class="myTest.replaced.Test2"></bean>

</beans>
```

main

```java
public class Main {
   public static void main(String[] args) {
      Resource resource = new ClassPathResource("myTestResources/applicationContext.xml");
      BeanFactory beanFactory = new XmlBeanFactory(resource);
      Test1 test1 = (Test1)beanFactory.getBean("test1");
      test1.doTest();//Test2
   }
}
```

【属性解析】

**BeanDefinitionParserDelegate.parseReplacedMethodSubElements**

```java
public void parseReplacedMethodSubElements(Element beanEle, MethodOverrides overrides) {
   NodeList nl = beanEle.getChildNodes();
   for (int i = 0; i < nl.getLength(); i++) {
      Node node = nl.item(i);
       //当且仅当在 Spring 默认 Bean 的子元素下且元素为 replaced-method 时有效
      if (isCandidateElement(node) && nodeNameEquals(node, REPLACED_METHOD_ELEMENT)) {
         Element replacedMethodEle = (Element) node;
          //被替换的方法
         String name = replacedMethodEle.getAttribute(NAME_ATTRIBUTE);
          //新的方法
         String callback = replacedMethodEle.getAttribute(REPLACER_ATTRIBUTE);
         ReplaceOverride replaceOverride = new ReplaceOverride(name, callback);          
		 //在方法重载的情况下标识替换方法的参数
         List<Element> argTypeEles = DomUtils.getChildElementsByTagName(replacedMethodEle, ARG_TYPE_ELEMENT);
         for (Element argTypeEle : argTypeEles) {
             //记录参数
            String match = argTypeEle.getAttribute(ARG_TYPE_MATCH_ATTRIBUTE);
            match = (StringUtils.hasText(match) ? match : DomUtils.getTextValue(argTypeEle));
            if (StringUtils.hasText(match)) {
               replaceOverride.addTypeIdentifier(match);
            }
         }
         replaceOverride.setSource(extractSource(replacedMethodEle));
         overrides.addOverride(replaceOverride);
      }
   }
}
```

&emsp;&emsp;可以看到无论是 lookup-method 还是 replaced-method 都构造了一个 MethodOverride，并最终记录在了 AbstractBeanDefinition 中的 methodOverrides 属性中。

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/spring/202111020933269.png)

#### 2.2.6 解析 constructor-arg 构造参数

```xml
<bean id="test2" class="myTest.replaced.Test2">
	<constructor-arg index="0">
		<value>test</value>
	</constructor-arg>
</bean>
```

**BeanDefinitionParserDelegate.parseConstructorArgElements**

```java
public void parseConstructorArgElements(Element beanEle, BeanDefinition bd) {
   NodeList nl = beanEle.getChildNodes();
   for (int i = 0; i < nl.getLength(); i++) {
      Node node = nl.item(i);
      if (isCandidateElement(node) && nodeNameEquals(node, CONSTRUCTOR_ARG_ELEMENT)) {
         parseConstructorArgElement((Element) node, bd);
      }
   }
}
```

遍历所有子元素，找到 constructor-arg ，然后交给 parseConstructorArgElement 解析。

**BeanDefinitionParserDelegate.parseConstructorArgElement**

```java
public void parseConstructorArgElement(Element ele, BeanDefinition bd) {
    // index 属性
   String indexAttr = ele.getAttribute(INDEX_ATTRIBUTE);
    // type 属性
   String typeAttr = ele.getAttribute(TYPE_ATTRIBUTE);
    // name 属性
   String nameAttr = ele.getAttribute(NAME_ATTRIBUTE);
    //1.当有 index 属性时的处理
   if (StringUtils.hasLength(indexAttr)) {
      try {
         int index = Integer.parseInt(indexAttr);
         if (index < 0) {
            error("'index' cannot be lower than 0", ele);
         }
         else {
            try {
               this.parseState.push(new ConstructorArgumentEntry(index));
               // 1.1 解析获取元素值
               Object value = parsePropertyValue(ele, bd, null);
               // 1.2 使用 ConstructorArgumentValues.ValueHolder 封装解析的值
               ConstructorArgumentValues.ValueHolder valueHolder = new ConstructorArgumentValues.ValueHolder(value);
               //将type、name和index属性一并封装到ConstructorArgumentValues.ValueHolder中
               if (StringUtils.hasLength(typeAttr)) {
                  valueHolder.setType(typeAttr);
               }
               if (StringUtils.hasLength(nameAttr)) {
                  valueHolder.setName(nameAttr);
               }
               valueHolder.setSource(extractSource(ele));
               if (bd.getConstructorArgumentValues().hasIndexedArgumentValue(index)) {
                  error("Ambiguous constructor-arg entries for index " + index, ele);
               }
               else {
                  // 1.3 将valueHolder添加至当前BeanDefinition的indexedArgumentValues属性中
                  bd.getConstructorArgumentValues().addIndexedArgumentValue(index, valueHolder);
               }
            }
            finally {
               this.parseState.pop();
            }
         }
      }
      catch (NumberFormatException ex) {
         error("Attribute 'index' of tag 'constructor-arg' must be an integer", ele);
      }
   }
    //2.没有 index 的处理
   else {
      try {
         this.parseState.push(new ConstructorArgumentEntry());
          // 2.1 解析获取元素值
         Object value = parsePropertyValue(ele, bd, null);
          // 2.2 使用 ConstructorArgumentValues.ValueHolder 封装解析的值
         ConstructorArgumentValues.ValueHolder valueHolder = new ConstructorArgumentValues.ValueHolder (value);
          //将type、name属性一并封装
         if (StringUtils.hasLength(typeAttr)) {
            valueHolder.setType(typeAttr);
         }
         if (StringUtils.hasLength(nameAttr)) {
            valueHolder.setName(nameAttr);
         }
         valueHolder.setSource(extractSource(ele));
          // 1.3 将valueHolder添加至当前BeanDefinition的genericArgumentValues属性中
         bd.getConstructorArgumentValues().addGenericArgumentValue(valueHolder);
      }
      finally {
         this.parseState.pop();
      }
   }
}
```

&emsp;&emsp;对于是否使用 index 属性，Spring 的处理流程是不同的，关键是属性信息的保存位置不同，Spring 将构造函数的参数封装为一个 `ConstructorArgumentValues` 对象，真正的参数又被封装在 `ConstructorArgumentValues.ValueHolder` 中，有 index 的参数放在 `Map<Integer, ValueHolder> indexedArgumentValues = new LinkedHashMap<>()` 没有的存放在 `List<ValueHolder> genericArgumentValues = new ArrayList<>()` 中，最后将封装好的 `ConstructorArgumentValues` 记录在 BeanDefinition 中，然后看具体解析获取元素的值。


##### 2.2.6.1 解析 constructor-arg 的值

**BeanDefinitionParserDelegate.parsePropertyValue**

```java
public Object parsePropertyValue(Element ele, BeanDefinition bd, @Nullable String propertyName) {
   String elementName = (propertyName != null ?
         "<property> element for property '" + propertyName + "'" :
         "<constructor-arg> element");

   // 一个属性只能对应一种类型：ref、value、list 等
   NodeList nl = ele.getChildNodes();
   Element subElement = null;
   for (int i = 0; i < nl.getLength(); i++) {
      Node node = nl.item(i);
       //如果是 description 或者 meta 不处理
      if (node instanceof Element && !nodeNameEquals(node, DESCRIPTION_ELEMENT) &&
            !nodeNameEquals(node, META_ELEMENT)) {
         if (subElement != null) {
            error(elementName + " must not contain more than one sub-element", ele);
         }
         else {
            subElement = (Element) node;
         }
      }
   }
	//获取 ref 属性
   boolean hasRefAttribute = ele.hasAttribute(REF_ATTRIBUTE);
    //获取 value 属性
   boolean hasValueAttribute = ele.hasAttribute(VALUE_ATTRIBUTE);
   if ((hasRefAttribute && hasValueAttribute) ||
         ((hasRefAttribute || hasValueAttribute) && subElement != null)) {
       //在同一个标签上不会存在
       //	1.同时既有 ref 属性又有 value 属性
       //	2.存在 ref 属性或者 value 属性且又拥有子元素
      error(elementName +
            " is only allowed to contain either 'ref' attribute OR 'value' attribute OR sub-element", ele);
   }

   if (hasRefAttribute) {
       //ref引用属性的处理，使用RuntimeBeanReference封装对应名称
      String refName = ele.getAttribute(REF_ATTRIBUTE);
      if (!StringUtils.hasText(refName)) {
         error(elementName + " contains empty 'ref' attribute", ele);
      }
      RuntimeBeanReference ref = new RuntimeBeanReference(refName);
      ref.setSource(extractSource(ele));
      return ref;
   }
   else if (hasValueAttribute) {
       //value 属性使用 TypedStringValue 封装
      TypedStringValue valueHolder = new TypedStringValue(ele.getAttribute(VALUE_ATTRIBUTE));
      valueHolder.setSource(extractSource(ele));
      return valueHolder;
   }
   else if (subElement != null) {
       //解析子元素 例如 <map> 之类的
      return parsePropertySubElement(subElement, bd);
   }
   else {
      // 既没有 ref 也没有 value 也没有子元素
      error(elementName + " must specify a ref or value", ele);
      return null;
   }
}
```

&emsp;&emsp;Spring 将 ref 引用属性封装为一个用于属性值对象的不可变占位符类 `RuntimeBeanReference`，并将其记录在 BeanDefinition 中，当它是对工厂中另一个 bean 的引用时，将在运行时解析。

&emsp;&emsp;正常的 Value 会被封装为 TypedStringValue，其中包含了 value 值、value 类型等，实际上对解析值的转换工作由 bean 工厂执行。

##### 2.2.6.2 constructor-arg 子元素的分类处理

**BeanDefinitionParserDelegate.parsePropertySubElement**

```java
public Object parsePropertySubElement(Element ele, @Nullable BeanDefinition bd, @Nullable String defaultValueType) {
    //1. 子元素自定义标签由 parseNestedCustomElement 解析
   if (!isDefaultNamespace(ele)) {
      return parseNestedCustomElement(ele, bd);
   }
    //2. bean 标签
   else if (nodeNameEquals(ele, BEAN_ELEMENT)) {
      BeanDefinitionHolder nestedBd = parseBeanDefinitionElement(ele, bd);
      if (nestedBd != null) {
		  //2.1当子节点下有自定义标签，对自定义标签进行解析
         nestedBd = decorateBeanDefinitionIfRequired(ele, nestedBd, bd);
      }
      return nestedBd;
   }
    //3. ref 引用
   else if (nodeNameEquals(ele, REF_ELEMENT)) {
      String refName = ele.getAttribute(BEAN_REF_ATTRIBUTE);
      boolean toParent = false;
      if (!StringUtils.hasLength(refName)) {
         refName = ele.getAttribute(PARENT_REF_ATTRIBUTE);
         toParent = true;
         if (!StringUtils.hasLength(refName)) {
            error("'bean' or 'parent' is required for <ref> element", ele);
            return null;
         }
      }
      if (!StringUtils.hasText(refName)) {
         error("<ref> element contains empty target attribute", ele);
         return null;
      }
      RuntimeBeanReference ref = new RuntimeBeanReference(refName, toParent);
      ref.setSource(extractSource(ele));
      return ref;
   }
    // 4. idref 元素
   else if (nodeNameEquals(ele, IDREF_ELEMENT)) {
      return parseIdRefElement(ele);
   }
    // 5. value 元素
   else if (nodeNameEquals(ele, VALUE_ELEMENT)) {
      return parseValueElement(ele, defaultValueType);
   }
    // 6. null 元素
   else if (nodeNameEquals(ele, NULL_ELEMENT)) {
      TypedStringValue nullHolder = new TypedStringValue(null);
      nullHolder.setSource(extractSource(ele));
      return nullHolder;
   }
    // 7. array 元素
   else if (nodeNameEquals(ele, ARRAY_ELEMENT)) {
      return parseArrayElement(ele, bd);
   }
    // 8. list 元素
   else if (nodeNameEquals(ele, LIST_ELEMENT)) {
      return parseListElement(ele, bd);
   }
    // 9. set 元素
   else if (nodeNameEquals(ele, SET_ELEMENT)) {
      return parseSetElement(ele, bd);
   }
    // 10. map 元素
   else if (nodeNameEquals(ele, MAP_ELEMENT)) {
      return parseMapElement(ele, bd);
   }
    // 11. props 元素
   else if (nodeNameEquals(ele, PROPS_ELEMENT)) {
      return parsePropsElement(ele);
   }
   else {
      error("Unknown property sub-element: [" + ele.getNodeName() + "]", ele);
      return null;
   }
}
```

&emsp;&emsp;到这里，构造函数的解析工作已基本完成，具体的子元素的分类处理逻辑比较简单，这里就不一一赘述了。

#### 2.2.7 解析 property 

```xml
<bean id="test2" class="myTest.replaced.Test2">
	<property name="name" value="value"></property>
</bean>
```

**BeanDefinitionParserDelegate.parsePropertyElements**

```java
public void parsePropertyElements(Element beanEle, BeanDefinition bd) {
   NodeList nl = beanEle.getChildNodes();
   for (int i = 0; i < nl.getLength(); i++) {
      Node node = nl.item(i);
      if (isCandidateElement(node) && nodeNameEquals(node, PROPERTY_ELEMENT)) {
         parsePropertyElement((Element) node, bd);
      }
   }
}
```

&emsp;&emsp;提取所有 property 的子元素交由 parsePropertyElement 处理。

**BeanDefinitionParserDelegate.parsePropertyElement**

```java
public void parsePropertyElement(Element ele, BeanDefinition bd) {
   String propertyName = ele.getAttribute(NAME_ATTRIBUTE);
   if (!StringUtils.hasLength(propertyName)) {
      error("Tag 'property' must have a 'name' attribute", ele);
      return;
   }
   this.parseState.push(new PropertyEntry(propertyName));
   try {
       //不允许对同一属性重复配置
      if (bd.getPropertyValues().contains(propertyName)) {
         error("Multiple 'property' definitions for property '" + propertyName + "'", ele);
         return;
      }
      Object val = parsePropertyValue(ele, bd, propertyName);
      PropertyValue pv = new PropertyValue(propertyName, val);
      parseMetaElements(ele, pv);
      pv.setSource(extractSource(ele));
      bd.getPropertyValues().addPropertyValue(pv);
   }
   finally {
      this.parseState.pop();
   }
}
```

&emsp;&emsp;与 constructor-arg 不同的是 parsePropertyElement 将 parsePropertyValue 解析出的返回值使用 PropertyValue 封装，并记录在 BeanDefinition 的 propertyValues 属性中。

#### 2.2.8 解析 qualifier

&emsp;&emsp;Spring 设计这个bean的配置项，目的是限定装配的 bean 的确切名称，Spring 容器中的候选 bean 数目有且必须仅有一个，也就是在自动装配时必须能够唯一确定一个匹配的 bean，当一个接口有多个不同实现时，使用自动注入时会报 `NoUniqueBeanDefinitionException` 异常信息，为了消除歧义，Spring 允许使用 qualifier 来指定注入 bean 的名称。当然 primary 也可以消除歧义，但 qualifier 的优先级高于 primary ，前者是指定必须是 qualifier 规定的 bean，其他的不认，而 primary 是规定了一个优先级。

&emsp;&emsp;其解析过程与之前大同小异，这里不再重复叙述。

## 3. 解析默认标签中的自定义标签元素

&emsp;&emsp;至此，默认标签的解析与提取已经完成，回顾一下文章第2小节 bean 标签的解析的 `processBeanDefinition` 方法一共做了4件事：

1. 解析 bean 标签；

2. 当子节点下有自定义标签，对自定义标签进行解析；

3. 对解析后的的 bdHolder 进行注册；

4. 触发注册完成监听事件。

&emsp;&emsp;其中第一步已经完成，现在看第二步，`decorateBeanDefinitionIfRequired`方法，当 Spring 中的 Bean 使用的是默认标签配置，但其中的子元素却使用了自定义配置时，便会用到它，之前说过，Bean 的解析分为两种类型，一种是默认类型的解析，另一种是自定义类型的解析，而这里是在默认标签的解析过程中，也就是说，这个自定义标签解析方法是针对于在默认标签下的自定义属性的解析，比如以下：

```xml
<bean id="myTestDomain" class="org.springframework.myTest.MyTestDomain" >
   <myBean:user username="aaa"/>
</bean>
```

**BeanDefinitionParserDelegate.decorateBeanDefinitionIfRequired**

```java
public BeanDefinitionHolder decorateBeanDefinitionIfRequired(Element ele, BeanDefinitionHolder originalDef) {
   return decorateBeanDefinitionIfRequired(ele, originalDef, null);
}
```

&emsp;&emsp;这里函数中第三个参数是父类 bean，当对某个嵌套配置进行解析时需要传递父类的 BeanDefinition 。阅读源码可知是为了使用父类的 scope 属性，以备子类若没有设置 scope 时默认使用父类的属性，这里因为是顶层配置，所以传递 null 。

```java
public BeanDefinitionHolder decorateBeanDefinitionIfRequired(
      Element ele, BeanDefinitionHolder originalDef, @Nullable BeanDefinition containingBd) {

   BeanDefinitionHolder finalDefinition = originalDef;

   // 1.遍历所有节点，看看是否有需要解析的属性
   NamedNodeMap attributes = ele.getAttributes();
   for (int i = 0; i < attributes.getLength(); i++) {
      Node node = attributes.item(i);
      finalDefinition = decorateIfRequired(node, finalDefinition, containingBd);
   }

   // 2.遍历所有子节点，看看是否有需要解析的属性
   NodeList children = ele.getChildNodes();
   for (int i = 0; i < children.getLength(); i++) {
      Node node = children.item(i);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
         finalDefinition = decorateIfRequired(node, finalDefinition, containingBd);
      }
   }
   return finalDefinition;
}
```

&emsp;&emsp;代码的逻辑很简单，接下来进入 `decorateIfRequired` 方法。

**BeanDefinitionParserDelegate.decorateIfRequired**

```java
public BeanDefinitionHolder decorateIfRequired(
      Node node, BeanDefinitionHolder originalDef, @Nullable BeanDefinition containingBd) {
   //1.获取自定义标签的命名空间
   String namespaceUri = getNamespaceURI(node);
   //2.对自定义标签解析
   if (namespaceUri != null && !isDefaultNamespace(namespaceUri)) {
	  //3.根据命名空间获取对应的处理器
      NamespaceHandler handler = this.readerContext.getNamespaceHandlerResolver().resolve(namespaceUri);
      if (handler != null) {
		 //4.开始解析
         BeanDefinitionHolder decorated =
               handler.decorate(node, originalDef, new ParserContext(this.readerContext, this, containingBd));
         if (decorated != null) {
            return decorated;
         }
      }
      else if (namespaceUri.startsWith("http://www.springframework.org/schema/")) {
         error("Unable to locate Spring NamespaceHandler for XML schema namespace [" + namespaceUri + "]", node);
      }
      else {
         // 自定义命名空间，不由 Spring 处理 - 可能是 “xml:...”。
         if (logger.isDebugEnabled()) {
            logger.debug("No Spring NamespaceHandler found for XML schema namespace [" + namespaceUri + "]");
         }
      }
   }
   return originalDef;
}
```

&emsp;&emsp;走到这里，逻辑已经非常清晰了，首先获取属性或元素的命名空间，以此来判断是否需要解析处理，至于更详细的根据命名空间获取对应的处理器和解析过程和第 2.2.6.2 小节的第一步子元素自定义标签的解析，将在后面介绍，可以看到，这里对于默认标签的处理直接略过了，因为默认标签到了这里已经被处理完了。