# Spring源码笔记五：自定义标签的解析

&emsp;&emsp;在 Spring 中存在默认标签和自定义标签两种，前面默认标签的解析过程已经分析完了，接下来看下自定义标签的解析。

## 1. 自定义标签的使用

&emsp;&emsp;在 Spring 中对于默认标签和自定义标签是通过命名空间区分的，在需要配置非常复杂的逻辑时，就可以考虑拓展 Spring 自定义标签，扩展自定义标签大致需要几个步骤：

1. 创建一个需要拓展的组件。
2. 定义 XSD 文件。
3. 实现 `BeanDefinitionParser` 接口，用来解析 XSD 文件中定义的组件。
4. 创建 Handler ，拓展自 `NamespaceHandlerSupport` 目的是将组件注册到 Spring 容器。
5. 编写 Spring.handlers 和 Spring.schemas 文件。

### 1.1 创建组件

&emsp;&emsp;首先创建一个普通的POJO

```java
public class User {
   private String name;
   private int age;
   private int email;
//省略get、set方法
   }
```

### 1.2 定义 XSD 文件

&emsp;&emsp;下面的文件中定义了一个新的 targetNamespace 并在这个空间中定义了一个 name 为 user 的 element ，其中有四个属性，其中 ID 是该组件的唯一标识符，不能省略。

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<xsd:schema xmlns="http://www.Sunflower.org/schema/user"
			xmlns:xsd="http://www.w3.org/2001/XMLSchema"
			targetNamespace="http://www.Sunflower.org/schema/user"
			elementFormDefault="qualified"
>

	<xsd:element name="user">
		<xsd:complexType>
			<xsd:attribute id="id" name="id" type="xsd:string" />
			<xsd:attribute name="name" type="xsd:string" />
			<xsd:attribute name="age" type="xsd:int" />
			<xsd:attribute name="email" type="xsd:string" />
		</xsd:complexType>
	</xsd:element>

</xsd:schema>
```

### 1.3 实现 BeanDefinitionParser 接口

```java
public class UserBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {
   //element 对应的类
   @Override
   protected Class<?> getBeanClass(Element element) {
      return User.class;
   }
	//解析过程
   @Override
   protected void doParse(Element element, BeanDefinitionBuilder builder) {
      String name = element.getAttribute("name");
      int age = Integer.valueOf(element.getAttribute("age"));
      String email = element.getAttribute("email");
	  //将解析到的数据放入BeanDefinitionBuilder中，
	  //等到完成所有的bean的解析后统一注册到beanFactory中
      builder.addPropertyValue("name",name);
      builder.addPropertyValue("age",age);
      builder.addPropertyValue("email",email);
   }
}
```

### 1.4 继承拓展 NamespaceHandlerSupport 注册组件

```java
public class MyNamespaceHandler extends NamespaceHandlerSupport {
   @Override
   public void init() {
      registerBeanDefinitionParser("user",new UserBeanDefinitionParser());
   }
}
```

&emsp;&emsp;上述代码的作用就是当遇到类似于 user 开头的元素，就会把这个元素的解析工作交给对应的 UserBeanDefinitionParser 去解析。

### 1.5 编写 Spring.handlers 和 Spring.schemas

 【Spring.handlers】

```properties
http\://www.Sunflower.org/schema/User=org.springframework.myTest.MyNamespaceHandler
```

 【 Spring.schemas】

```properties
http\://www.Sunflower.org/schema/user.xsd=/myTestResources/user.xsd
```

&emsp;&emsp;到这里，自定义的配置就结束了，而 Spring 加载自定义的大致流程是遇到自定义标签然后就去 Spring.handlers 和 Spring.schemas 中去找对应的 handler 和 XSD ，默认位置是/META-INF/下，进而有找到对应的 handler 以及解析元素的 Parser ，从而完成了整个自定义元素的解析，也就是说自定义与 Spring 中默认的标准配置不同在于 Spring 将自定义标签解析的工作委托给了用户去实现。

### 1.6 测试

&emsp;&emsp;创建测试配置文件，在配置文件中引入对应的命名空间以及 XSD 后，就可以直接使用自定义标签了。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
      xmlns:myname="http://www.Sunflower.org/schema/user"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xsi:schemaLocation="http://www.springframework.org/schema/beans
      http://www.springframework.org/schema/beans/spring-beans.xsd
      http://www.Sunflower.org/schema/user
      http://www.Sunflower.org/schema/user.xsd"
>

   <myname:user id="user" name="username" age="18" email="12345875" />

</beans>
```

```java
public class Main {
   public static void main(String[] args) {
      Resource resource = new ClassPathResource("myTestResources/applicationContext.xml");
      BeanFactory beanFactory = new XmlBeanFactory(resource);
      User user = (User)beanFactory.getBean("user");
      System.out.println(user);
      //User{name='username', age=18, email=12345875}
   }
}
```

## 2. 自定义标签的解析

&emsp;&emsp;在了解了自定义标签的使用后，再看它的解析过程就会容易许多，再看一眼自定义标签解析的开始。

**DefaultBeanDefinitionDocumentReader.parseBeanDefinitions**

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

**BeanDefinitionParserDelegate.parseCustomElement**

&emsp;&emsp;parseCustomElement 也是两段方法。

```java
public BeanDefinition parseCustomElement(Element ele) {
	//第二个参数 containingBd 是父类 bean 为了解析嵌套标签使用，
	//这里为顶层元素，设置为null。
   return parseCustomElement(ele, null);
}
```

```java
public BeanDefinition parseCustomElement(Element ele, @Nullable BeanDefinition containingBd) {
	//1.获取自定义标签的nameSpace
	//以上面为例：http://www.Sunflower.org/schema/user
   String namespaceUri = getNamespaceURI(ele);
   if (namespaceUri == null) {
      return null;
   }
   //2.根据命名空间寻找对应的 NamespaceHandler
   NamespaceHandler handler = this.readerContext.getNamespaceHandlerResolver().resolve(namespaceUri);
   if (handler == null) {
      error("Unable to locate Spring NamespaceHandler for XML schema namespace [" + namespaceUri + "]", ele);
      return null;
   }
   //3.调用自定义解析程序进行解析
   return handler.parse(ele, new ParserContext(this.readerContext, this, containingBd));
}
```

&emsp;&emsp;这里思路很简单，一共就三步，获取命名空间没什么可说的，从第二步看。

### 2.1 根据命名空间寻找对应的 NamespaceHandler

&emsp;&emsp;还记得第三篇中获取 Document 的时候初始化的 readerContext 吗，再来看一下。

**XmlBeanDefinitionReader.createReaderContext**

```java
//ReaderContext 的创建
public XmlReaderContext createReaderContext(Resource resource) {
   return new XmlReaderContext(resource, this.problemReporter, this.eventListener,
         this.sourceExtractor, this, getNamespaceHandlerResolver());
}
```

**XmlBeanDefinitionReader.getNamespaceHandlerResolver**

```java
public NamespaceHandlerResolver getNamespaceHandlerResolver() {
		if (this.namespaceHandlerResolver == null) {
			this.namespaceHandlerResolver = createDefaultNamespaceHandlerResolver();
		}
		return this.namespaceHandlerResolver;
}
```

**XmlBeanDefinitionReader.createDefaultNamespaceHandlerResolver**

```java
protected NamespaceHandlerResolver createDefaultNamespaceHandlerResolver() {
		ClassLoader cl = (getResourceLoader() != null ? getResourceLoader().getClassLoader() : getBeanClassLoader());
		return new DefaultNamespaceHandlerResolver(cl);
	}
```

&emsp;&emsp;可以看到在 ReaderContext 初始化的时候其属性 namespaceHandlerResolver 已经被初始化为了 DefaultNamespaceHandlerResolver 的实例，所以这里调用的 `this.readerContext.getNamespaceHandlerResolver()` 获取到的其实就是 DefaultNamespaceHandlerResolver 的实例，而调用的方法就是其 resolve 方法。

**DefaultNamespaceHandlerResolver.resolve**

```java
public NamespaceHandler resolve(String namespaceUri) {
		//从所有 META-INF/spring.handlers 文件中加载 handlerMappings
		Map<String, Object> handlerMappings = getHandlerMappings();
		//根据配置的命名空间找到对应的信息
		Object handlerOrClassName = handlerMappings.get(namespaceUri);
		if (handlerOrClassName == null) {
			return null;
		}
		else if (handlerOrClassName instanceof NamespaceHandler) {
			//已经做过解析的情况，直接从缓存读取
			return (NamespaceHandler) handlerOrClassName;
		}
		else {
			//没有做过解析，返回的是类路径
			String className = (String) handlerOrClassName;
			try {
				Class<?> handlerClass = ClassUtils.forName(className, this.classLoader);
				if (!NamespaceHandler.class.isAssignableFrom(handlerClass)) {
					throw new FatalBeanException("Class [" + className + "] for namespace [" + namespaceUri +
							"] does not implement the [" + NamespaceHandler.class.getName() + "] interface");
				}
				//利用反射实例化类
				NamespaceHandler namespaceHandler = (NamespaceHandler) BeanUtils.instantiateClass(handlerClass);
				//调用自定义的 init 方法
				namespaceHandler.init();
				//加入缓存
				handlerMappings.put(namespaceUri, namespaceHandler);
				return namespaceHandler;
			}
			... ...
		}
	}
```

&emsp;&emsp;函数非常清楚的展示了根据命名空间寻找对应的 NamespaceHandler 并调用对应自定义的 init 方法的过程，其中的关键步骤是通过加载配置的 META-INF/spring.handlers 文件寻找对应的 NamespaceHandler 类，在 init 方法中可以注册多个解析器，只不过上面的示例中只注册了支持 user 标签的解析器，注册后，命名空间处理器就可以根据不同的标签来调用不同的解析器来进行解析。

### 2.2 调用自定义解析程序进行解析

&emsp;&emsp;解析工作之前首先是寻找对应元素的解析器，进而调用解析方法。

**NamespaceHandlerSupport.parse**

```java
public BeanDefinition parse(Element element, ParserContext parserContext) {
		//寻找对应元素的解析器
		BeanDefinitionParser parser = findParserForElement(element, parserContext);
		//调用解析方法
		return (parser != null ? parser.parse(element, parserContext) : null);
	}
```

**NamespaceHandlerSupport.findParserForElement**

```java
private BeanDefinitionParser findParserForElement(Element element, ParserContext parserContext) {
		//获取元素名称
		String localName = parserContext.getDelegate().getLocalName(element);
		//根据元素名称获取解析器
		BeanDefinitionParser parser = this.parsers.get(localName);
		if (parser == null) {
			parserContext.getReaderContext().fatal(
					"Cannot locate BeanDefinitionParser for element [" + localName + "]", element);
		}
		return parser;
	}
```

### 2.2 调用 parse 方法

**AbstractBeanDefinitionParser.parse**

```java
public final BeanDefinition parse(Element element, ParserContext parserContext) {
		//1.调用 parseInternal 方法构造 BeanDefinition
		AbstractBeanDefinition definition = parseInternal(element, parserContext);
		if (definition != null && !parserContext.isNested()) {
			try {
				//2.解析 ID 值
				String id = resolveId(element, definition, parserContext);
				if (!StringUtils.hasText(id)) {
					parserContext.getReaderContext().error(
							"Id is required for element '" + parserContext.getDelegate().getLocalName(element)
									+ "' when used as a top-level tag", element);
				}
				//3.解析 name 如果有
				String[] aliases = null;
				if (shouldParseNameAsAliases()) {
					String name = element.getAttribute(NAME_ATTRIBUTE);
					if (StringUtils.hasLength(name)) {
						aliases = StringUtils.trimArrayElements(StringUtils.commaDelimitedListToStringArray(name));
					}
				}
				//4.将 AbstractBeanDefinition 转换为 BeanDefinitionHolder 并注册
				BeanDefinitionHolder holder = new BeanDefinitionHolder(definition, id, aliases);
				registerBeanDefinition(holder, parserContext.getRegistry());
				if (shouldFireEvents()) {
					//5.通知监听器
					BeanComponentDefinition componentDefinition = new BeanComponentDefinition(holder);
					postProcessComponentDefinition(componentDefinition);
					parserContext.registerComponent(componentDefinition);
				}
			}
			catch (BeanDefinitionStoreException ex) {
				String msg = ex.getMessage();
				parserContext.getReaderContext().error((msg != null ? msg : ex.toString()), element);
				return null;
			}
		}
		return definition;
	}
```

&emsp;&emsp;这里 parse 方法主要完成了对解析后的 AbstractBeanDefinition 处理并注册的功能，而真正的解析工作交由 parseInternal 完成。

**AbstractSingleBeanDefinitionParser.parseInternal**

```java
protected final AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition();
		//1.数据准备阶段
		//调用子类覆盖的方法设置属性
		String parentName = getParentName(element);
		if (parentName != null) {
			builder.getRawBeanDefinition().setParentName(parentName);
		}
		Class<?> beanClass = getBeanClass(element);
		if (beanClass != null) {
			builder.getRawBeanDefinition().setBeanClass(beanClass);
		}
		else {
			String beanClassName = getBeanClassName(element);
			if (beanClassName != null) {
				builder.getRawBeanDefinition().setBeanClassName(beanClassName);
			}
		}
		builder.getRawBeanDefinition().setSource(parserContext.extractSource(element));
		BeanDefinition containingBd = parserContext.getContainingBeanDefinition();
		if (containingBd != null) {
			// 若父类存在则使用父类的 scope 
			builder.setScope(containingBd.getScope());
		}
		if (parserContext.isDefaultLazyInit()) {
			// 设置懒加载
			builder.setLazyInit(true);
		}
		//2.解析
		doParse(element, parserContext, builder);
		return builder.getBeanDefinition();
	}
```

&emsp;&emsp;在 parseInternal 方法中并没有直接调用自定义的解析函数，而是先进行了一系列的数据准备，包括对 beanClass、scope、lazy-init 等属性的准备，最后才调用 doParse 函数。

**AbstractSingleBeanDefinitionParser.doParse**

```java
protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
		doParse(element, builder);
	}
```

```java
protected void doParse(Element element, BeanDefinitionBuilder builder) {
	}
```

&emsp;&emsp;至此，自定义标签的处理过程就全部处理完毕了，整个处理过程同样是按照默认标签的处理流程来实现，只不过个性化的解析工作交给了用户实现。

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/spring/UserBeanDefinitionParser.png)

&emsp;&emsp;到此为止 Spring 的全部解析工作已经完成，也就是 Spring 将信息从配置文件加载到内存的全部过程，接下来就是如何使用这些信息。

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/spring/202112241005413.png)