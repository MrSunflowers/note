# Spring源码笔记二：Document 的获取

&emsp;&emsp;上篇主要介绍了在加载配置文件之前的准备工作和 XmlBeanFactory 的一部分内容，接着上次继续往下看。

## 1. 准备工作


&emsp;&emsp;之前提到的在 XmlBeanFactory 构造函数中调用了 XmlBeanDefinitionReader 类型的 reader属性提供的方法 this.reader.loadBeanDefinitions(resource) 。

**XmlBeanDefinitionReader.loadBeanDefinitions** 其实分为两段来实现

### 1.1 封装资源文件

```java
public int loadBeanDefinitions(Resource resource) throws BeanDefinitionStoreException {
		return loadBeanDefinitions(new EncodedResource(resource));
}
```

&emsp;&emsp;当进入 XmlBeanDefinitionReader 后首先对 Resource 使用 EncodedResource 类进行封装。通过名称，可以推断这个类主要是用于对资源文件的编码进行处理的，其中主要的逻辑是将资源文件的编码属性和 Resource 构造在一起。

**EncodedResource.getReader** 

```java
public Reader getReader() throws IOException {
		if (this.charset != null) {
			return new InputStreamReader(this.resource.getInputStream(), this.charset);
		}
		else if (this.encoding != null) {
			return new InputStreamReader(this.resource.getInputStream(), this.encoding);
		}
		else {
			return new InputStreamReader(this.resource.getInputStream());
		}
	}
```

&emsp;&emsp;在 getReader() 方法中，当设置了编码属性的时候 Spring 会使用相应的编码作为输入流的编码。当构造好 EncodedResource 对象后，再次进入 loadBeanDefinitions 的重载方法，这个方法内部才是真正的数据准备阶段。

### 1.2 获取输入流

&emsp;&emsp;这个方法比较长，需要分开来看，其实它的内部就做了两件事，第一，通过属性来记录已经加载的资源，第二，使用封装的 EncodedResource 来获取输入流并构造 inputSource 。

```java
public int loadBeanDefinitions(EncodedResource encodedResource) throws BeanDefinitionStoreException {
		Assert.notNull(encodedResource, "EncodedResource must not be null");
		if (logger.isTraceEnabled()) {
			logger.trace("Loading XML bean definitions from " + encodedResource);
		}
		//1.通过属性来记录已经加载的资源
		Set<EncodedResource> currentResources = this.resourcesCurrentlyBeingLoaded.get();

		if (!currentResources.add(encodedResource)) {
			throw new BeanDefinitionStoreException(
					"Detected cyclic loading of " + encodedResource + " - check your import definitions!");
		}
		//2.使用封装的 EncodedResource 来获取输入流并构造 inputSource 
		try (InputStream inputStream = encodedResource.getResource().getInputStream()) {
			InputSource inputSource = new InputSource(inputStream);
			if (encodedResource.getEncoding() != null) {
				inputSource.setEncoding(encodedResource.getEncoding());
			}
            //3.通过构造的 inputSource 和获取的 Resource 调用 doLoadBeanDefinitions
			return doLoadBeanDefinitions(inputSource, encodedResource.getResource());
		}
		catch (IOException ex) {
			throw new BeanDefinitionStoreException(
					"IOException parsing XML document from " + encodedResource.getResource(), ex);
		}
		finally {
			currentResources.remove(encodedResource);
			if (currentResources.isEmpty()) {
				this.resourcesCurrentlyBeingLoaded.remove();
			}
		}
	}
```

&emsp;&emsp;整理一下逻辑，首先对传入的 resource 参数做封装，目的是考虑到 Resource 可能存在编码要求，其次准备 InputSource 对象，给后续的通过 SAX 来读取 XML 文件做基础，最后将准备好的数据通过参数传入真正的核心处理部分 doLoadBeanDefinitions。

## 2. doLoadBeanDefinitions

**XmlBeanDefinitionReader.doLoadBeanDefinitions**

```java
protected int doLoadBeanDefinitions(InputSource inputSource, Resource resource)
			throws BeanDefinitionStoreException {

		try {
            //1.加载 XML 文件并得到对应的 Document 
			Document doc = doLoadDocument(inputSource, resource);
            //2.根据返回的 Document 注册 BeanDefinition 信息
			int count = registerBeanDefinitions(doc, resource);
			if (logger.isDebugEnabled()) {
				logger.debug("Loaded " + count + " bean definitions from " + resource);
			}
			return count;
		}
		... ...
	}
```

在不考虑异常处理的情况下，doLoadBeanDefinitions 方法中做了两件事

- 加载 XML 文件并得到对应的 Document 。
- 根据返回的 Document 注册 BeanDefinition 信息。

这几个步骤支撑着整个 Spring 容器的部分实现，尤其是对配置文件的解析，逻辑非常繁琐复杂，首先从加载 XML 文件开始看。

### 2.1 获取 Document

&emsp;&emsp;有效的 xml 文档：首先 xml 文档是个格式正规的 xml 文档，然后又需要满足验证的要求，这样的 xml 文档称为有效的xml文档，常见的 xml 文件验证模式有两种：XSD 和 DTD。

#### 2.1.1 DTD

&emsp;&emsp;DTD（Document Type Definition）用来描述xml文档的结构，一个DTD文档包含：元素的定义规则、元素之间的关系规则、属性的定义规则，属于 xml 文档组成的一部分，DTD 是一种保证 xml 文档格式正确性的有效方法，可以通过比较 xml 文档和 DTD 文件来看文档是否符合规范，元素和标签使用是否正确。
​	
&emsp;&emsp;要使用 DTD 验证模式的时候，需要在 XML 文件的头部声明，以下是在 Spring 中使用 DTD 声明方式的代码：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE beans PUBLIC "-//SPRING//DTD BEAN 2.0//EN"
		"https://www.springframework.org/dtd/spring-beans-2.0.dtd">
<beans>
	... ...
</beans>
```

有了 DTD，为什么要用 Schema 呢

DTD的局限性

&emsp;&emsp;首先 DTD 文件不遵守 xml 语法（写 xml 文档实例时用一种语法，写 DTD 的时候用另外一种语法）；第二，DTD 数据类型有限（与数据库数据类型不一致）；DTD 不可扩展；DTD不支持命名空间（命名冲突）。

Schema 的新特性

&emsp;&emsp;Shema 是基于 xml 语法的；Schema 可以用能处理 xml 文档的工具处理；Schema 大大扩充了数据类型，可以自定义数据类型；Schema 支持元素的继承; Schema 支持属性组。

#### 2.1.2 XSD

&emsp;&emsp;XSD（XML Schemas Definition）：其作用与 DTD 一样，也是用于验证 xml 文档的有效性，只不过它提供了比 DTD 更强大的功能和更细粒度的数据类型，另外 Schema 还可以自定义数据类型。此外，Schema 也是一个 xml 文件，而 DTD 则不是。XML Schema 描述了 XML 文档的结构，可以用一个指定的 XML Schema 来验证某个文档。

&emsp;&emsp;在使用 XML Schema 文档对 XML 进行检验时，除了要声明名称空间外，还必须指定该名称空间对应的 XML Schema 文档的存储位置。通过 schemaLocation 属性来指定名称空间所对应的 XML Schema 文档存储位置，它包含两个部分，一部分是名称空间的 URL ，另一部分就是该名称空间所标识的 XML Schema 文件位置或 URL 地址。

在 Spring 中使用 XSD

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	   xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

	... ...
</beans>
```

#### 2.1.3 doLoadDocument

&emsp;&emsp;在了解了 DTD 与 XSD 的区别后再去分析 Spring 中对于验证模式的提取就更容易理解了。回到代码中来，上面说到 doLoadDocument 方法，XmlBeanDefinitionReader 将 Document 的加载工作委托给了 DocumentLoader 去执行，DocumentLoader 是一个接口，里面只定义了一个方法 loadDocument 。

```java
protected Document doLoadDocument(InputSource inputSource, Resource resource) throws Exception {
		return this.documentLoader.loadDocument(inputSource, 
                                                getEntityResolver(), 
                                                this.errorHandler,
                                                getValidationModeForResource(resource), 
                                                isNamespaceAware()
                                               );
}
```

#### 2.1.4 获取对应资源的验证模式

&emsp;&emsp;先来看 getValidationModeForResource 方法，从方法名称推断，这个方法应该是获取对应资源的验证模式的，进入方法：

**XmlBeanDefinitionReader.getValidationModeForResource**

```java
protected int getValidationModeForResource(Resource resource) {
		// 如果设定了使用的验证模式则使用设定的验证模式
    	int validationModeToUse = getValidationMode();
		if (validationModeToUse != VALIDATION_AUTO) {
			return validationModeToUse;
		}
    	//否则使用自动检测的方式
		int detectedMode = detectValidationMode(resource);
		if (detectedMode != VALIDATION_AUTO) {
			return detectedMode;
		}
		return VALIDATION_XSD;
	}
```

&emsp;&emsp;方法实现很简单，如果设定了使用的验证模式则使用设定的验证模式，否则使用自动检测的方式。可以通过 XmlBeanDefinitionReader 中的 setValidationMode 方法来设置。自动检测工作又委托给了专门用于检测 XML 流是否使用基于 DTD 或 XSD 的验证的类 XmlValidationModeDetector。

**XmlValidationModeDetector.detectValidationMode**

```java
public int detectValidationMode(InputStream inputStream) throws IOException {
		// Peek into the file to look for DOCTYPE.
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
			boolean isDtdValidated = false;
			String content;
			while ((content = reader.readLine()) != null) {
				content = consumeCommentTokens(content);
                //读取到空行则略过
				if (this.inComment || !StringUtils.hasText(content)) {
					continue;
				}
                //含有DOCTYPE为DTD
				if (hasDoctype(content)) {
					isDtdValidated = true;
					break;
				}
                //一直读取到“<”开始符号，验证模式一定会在开始符号之前
				if (hasOpeningTag(content)) {
					// End of meaningful data...
					break;
				}
			}
            //如果包含 DOCTYPE 就是 DTD 否则就是 XSD
			return (isDtdValidated ? VALIDATION_DTD : VALIDATION_XSD);
		}
		catch (CharConversionException ex) {
			// Choked on some character encoding...
			// Leave the decision up to the caller.
			return VALIDATION_AUTO;
		}
	}
```

&emsp;&emsp;在了解了验证模式的获取后，还有一个问题，现在验证模式有了，还缺验证文件，也就是在上文提到过的配置文件中头部声明的验证文件（ .dtd 或 .xsd ），在默认的加载方式中，验证文件的获取是通过 URL 进行网络下载获取，但这样就会出现问题，当网络情况不佳或者有延迟时就会出错，用户体验也不好。


#### 2.1.5 EntityResolver

&emsp;&emsp;回到 doLoadDocument，在调用 loadDocument 方法时设置了一个参数，EntityResolver，它在官方文档的描述是：如果 SAX 应用程序需要为外部实体实现定制处理，则必须实现该接口，并使用 `setEntityResolver` 方法向 SAX 驱动程序注册一个实例。也就是说，对于解析一个 XML ，SAX 首先读取该 XML 文档上的声明，根据声明去寻找相应的 DTD 定义，以便对文档进行验证。

&emsp;&emsp;EntityResolver 的作用是项目本身可以提供一个如何寻找 DTD 声明文件的方法，即由程序来实现寻找 DTD 声明文件的过程，比如将 DTD 文件放到项目某处，在实现时直接将此文档读取并返回给 SAX 即可，这样就避免了通过网络来寻找相应的声明。

首先看 EntityResolver 的接口方法声明

```java
public abstract InputSource resolveEntity (String publicId, String systemId)
        throws SAXException, IOException;
```

&emsp;&emsp;这里，它接收两个参数 publicld 和 systemId ，并返回一个 inputSource 对象。以上文 XSD 和 DTD 的配置文件为例，当解析 XSD 的配置文件，读取到的两个参数为：

- publicId：null
- systemId：http://www.springframework.org/schema/beans/spring-beans.xsd

当解析 DTD 的配置文件，读取到的两个参数为：

- publicId：-//SPRING//DTD BEAN 2.0//EN
- systemId：https://www.springframework.org/dtd/spring-beans-2.0.dtd

&emsp;&emsp;之前已经提到过，验证文件默认的加载方式是通过URL 进行网络下载获取，这样会造成延迟，用户体验也不好，一般的做法都是将验证文件放置在自己的工程里，以加载 DTD 文件为例来看 Spring 中是如何实现的。

先从获取 EntityResolver 实现来看：

**XmlBeanDefinitionReader.getEntityResolver**

```java
protected EntityResolver getEntityResolver() {
		if (this.entityResolver == null) {
			// Determine default EntityResolver to use.
			ResourceLoader resourceLoader = getResourceLoader();
			if (resourceLoader != null) {
				this.entityResolver = new ResourceEntityResolver(resourceLoader);
			}
			else {
				this.entityResolver = new DelegatingEntityResolver(getBeanClassLoader());
			}
		}
		return this.entityResolver;
	}
```

&emsp;&emsp;这里使用 ResourceEntityResolver 类来作为 EntityResolver 的实现类，而 ResourceEntityResolver 则继承了 DelegatingEntityResolver 类。

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/spring/202111011651006.png)

【方法实现】

**ResourceEntityResolver.resolveEntity**

```java
public InputSource resolveEntity(@Nullable String publicId, @Nullable String systemId)
			throws SAXException, IOException { 
		//调用父类的 resolveEntity 方法构造 source 也就是 DelegatingEntityResolver.resolveEntity
		InputSource source = super.resolveEntity(publicId, systemId);
		//若尝试父类构造失败且 systemId 不为空
		if (source == null && systemId != null) {
			String resourcePath = null;
			try {
				//尝试解码
				String decodedSystemId = URLDecoder.decode(systemId, "UTF-8");
				String givenUrl = new URL(decodedSystemId).toString();
				String systemRootUrl = new File("").toURI().toURL().toString();
				// 如果当前在系统根目录中，尝试本地解析
				if (givenUrl.startsWith(systemRootUrl)) {
					resourcePath = givenUrl.substring(systemRootUrl.length());
				}
			}
			catch (Exception ex) {
				if (logger.isDebugEnabled()) {
					logger.debug("Could not resolve XML entity [" + systemId + "] against system root URL", ex);
				}
				resourcePath = systemId;
			}
			if (resourcePath != null) {
				if (logger.isTraceEnabled()) {
					logger.trace("Trying to locate XML entity [" + systemId + "] as resource [" + resourcePath + "]");
				}
				//从classPath构造InputSource
				Resource resource = this.resourceLoader.getResource(resourcePath);
				source = new InputSource(resource.getInputStream());
				source.setPublicId(publicId);
				source.setSystemId(systemId);
				if (logger.isDebugEnabled()) {
					logger.debug("Found XML entity [" + systemId + "]: " + resource);
				}
			}
			else if (systemId.endsWith(DTD_SUFFIX) || systemId.endsWith(XSD_SUFFIX)) {
				// 没有可解析的本地文件，通过 https 进行外部 dtdxsd 查找，构造InputSource
				String url = systemId;
				if (url.startsWith("http:")) {
					url = "https:" + url.substring(5);
				}
				try {
					source = new InputSource(new URL(url).openStream());
					source.setPublicId(publicId);
					source.setSystemId(systemId);
				}
				catch (IOException ex) {
					if (logger.isDebugEnabled()) {
						logger.debug("Could not resolve XML entity [" + systemId + "] through URL [" + url + "]", ex);
					}
					source = null;
				}
			}
		}

		return source;
	}
```

**DelegatingEntityResolver.resolveEntity**

```java
public InputSource resolveEntity(@Nullable String publicId, @Nullable String systemId)
			throws SAXException, IOException {

		if (systemId != null) {
			if (systemId.endsWith(DTD_SUFFIX)) {
				return this.dtdResolver.resolveEntity(publicId, systemId);
			}
			else if (systemId.endsWith(XSD_SUFFIX)) {
				return this.schemaResolver.resolveEntity(publicId, systemId);
			}
		}

		return null;
	}
```

&emsp;&emsp;父类中的处理逻辑非常简单，对不同的验证模式， Spring 使用了不同的解析器解析，当获取到到的 systemId 以 .dtd 结尾时使用 dtdResolver 解析，以 .xsd 结尾时使用 schemaResolver 解析。而 DelegatingEntityResolver 在构造的时候分别初始化了使用的具体解析器类型。

**DelegatingEntityResolver.DelegatingEntityResolver**

```java
public DelegatingEntityResolver(@Nullable ClassLoader classLoader) {
		//dtd 解析器
		this.dtdResolver = new BeansDtdResolver();
		//schema 解析器
		this.schemaResolver = new PluggableSchemaResolver(classLoader);
	}
```


#### 2.1.6 PluggableSchemaResolver 

&emsp;&emsp;以这里使用的 XSD 解析器 PluggableSchemaResolver 来简单看看原理，在 PluggableSchemaResolver 类中定义了默认的 schema 文件的映射文件路径。

```java
public static final String DEFAULT_SCHEMA_MAPPINGS_LOCATION = "META-INF/spring.schemas";
```

&emsp;&emsp;查看 spring.schemas 文件发现，每一行都是 systemId=schema-location 的形式，其中 schema-location 也就是类路径中的模式文件。

&emsp;&emsp;这里的 resolveEntity 方法对文件 spring.schemas 进行加载并从中找到 systemid 所对应的 XSD 文件封装为 InputSource。

**PluggableSchemaResolver.resolveEntity**

```java
public InputSource resolveEntity(@Nullable String publicId, @Nullable String systemId) throws IOException {
		if (logger.isTraceEnabled()) {
			logger.trace("Trying to resolve XML entity with public id [" + publicId +
					"] and system id [" + systemId + "]");
		}

		if (systemId != null) {
			//加载所有属性，如果在类路径中找到多个同名资源，则合并属性
			String resourceLocation = getSchemaMappings().get(systemId);
			if (resourceLocation == null && systemId.startsWith("https:")) {
				resourceLocation = getSchemaMappings().get("http:" + systemId.substring(6));
			}
			if (resourceLocation != null) {
				Resource resource = new ClassPathResource(resourceLocation, this.classLoader);
				try {
					InputSource source = new InputSource(resource.getInputStream());
					source.setPublicId(publicId);
					source.setSystemId(systemId);
					if (logger.isTraceEnabled()) {
						logger.trace("Found XML schema [" + systemId + "] in classpath: " + resourceLocation);
					}
					return source;
				}
				catch (FileNotFoundException ex) {
					if (logger.isDebugEnabled()) {
						logger.debug("Could not find XML schema [" + systemId + "]: " + resource, ex);
					}
				}
			}
		}

		return null;
	}
```


#### 2.1.7 Document 的获取

&emsp;&emsp;经过了一系列的准备步骤，到这里就可以进行 Document 的加载了。回到上面说的 loadDocument 接口方法的实现：

```java
	public Document loadDocument(InputSource inputSource, EntityResolver entityResolver,
			ErrorHandler errorHandler, int validationMode, boolean namespaceAware) throws Exception {

		DocumentBuilderFactory factory = createDocumentBuilderFactory(validationMode, namespaceAware);
		if (logger.isTraceEnabled()) {
			logger.trace("Using JAXP provider [" + factory.getClass().getName() + "]");
		}
		DocumentBuilder builder = createDocumentBuilder(factory, entityResolver, errorHandler);
		return builder.parse(inputSource);
	}
```

&emsp;&emsp;这里很简单，没有什么特殊的操作，就是把 DocumentBuilderFactory 和 DocumentBuilder 的创建和初始化过程封装了一下，至于通过 SAX 解析 XML 文档并获取 Document 没有什么特殊的地方。

### 2.2. 解析并注册BeanDefinition

&emsp;&emsp;接着往后看，前面说过，在不考虑异常处理的情况下，doLoadBeanDefinitions 方法中做了两件事：

- 加载 XML 文件并得到对应的 Document 。
- 根据返回的 Document 注册 BeanDefinition 信息。

现在 Document 的获取已经完成，接下来来看注册 BeanDefinition 信息。


**XmlBeanDefinitionReader.registerBeanDefinitions**

```java
public int registerBeanDefinitions(Document doc, Resource resource) throws BeanDefinitionStoreException {
   //1. 实例化一个 BeanDefinitionDocumentReader 用于注册 BeanDefinition
   BeanDefinitionDocumentReader documentReader = createBeanDefinitionDocumentReader();
   //2. 统计注册前的 BeanDefinition 个数
   int countBefore = getRegistry().getBeanDefinitionCount();
   //3. 解析并注册 BeanDefinition
   documentReader.registerBeanDefinitions(doc, createReaderContext(resource));
   //4. 返回本次注册的 BeanDefinition 个数
   return getRegistry().getBeanDefinitionCount() - countBefore;
}
```

&emsp;&emsp;这里 XmlBeanDefinitionReader 类将具体的 Document 的读取工作交由 BeanDefinitionDocumentReader 来完成， BeanDefinitionDocumentReader 用于解析包含 BeanDefinition 信息的 XML 文档，进入 BeanDefinitionDocumentReader.registerBeanDefinitions 方法可以看到它主要做的工作。

1. 封装 readerContext 传递给 `DefaultBeanDefinitionDocumentReader` 类以便后续使用；
2. 获取文档的根元素并交由 `doRegisterBeanDefinitions` 进一步解析处理，其中的参数 doc 是通过 loadDocurnent 加载转换出来的。

**DefaultBeanDefinitionDocumentReader.registerBeanDefinitions**

```java
public void registerBeanDefinitions(Document doc, XmlReaderContext readerContext) {
   // 1.封装 readerContext
   this.readerContext = readerContext;
   // 2.提取根元素并交由 BeanDefinitionDocumentReader.doRegisterBeanDefinitions
   // 处理
   doRegisterBeanDefinitions(doc.getDocumentElement());
}
```

#### 2.2.1 封装 readerContext

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/spring/XmlReaderContext.png)

&emsp;&emsp;ReaderContext 类主要用于 Bean 定义读取过程的上下文环境的传递，封装了所有相关配置以及监听器等信息，对在读取 xml 信息的过程中的各个阶段的回调事件提供了支持，XmlReaderContext 继承并对 ReaderContext 进行了拓展实现，专门用于和 XmlBeanDefinitionReader 配合使用，并提供对 namespace 解析器 NamespaceHandlerResolver 的封装。

|          ReaderContext 封装的部分属性          |           说明           |
| :--------------------------: | :----------------------: |
|       XmlBeanDefinitionReader       |    XML 读取器   |
| NamespaceHandlerResolver | namespace 解析器 |
| Resource | 资源信息 |
| ReaderEventListener | 读取监听器 |

```java
public XmlReaderContext createReaderContext(Resource resource) {
   return new XmlReaderContext(resource, this.problemReporter, this.eventListener,
         this.sourceExtractor, this, getNamespaceHandlerResolver());
}
```

&emsp;&emsp;这里的 namespace 解析器使用的是的默认实现 DefaultNamespaceHandlerResolver，它的作用是根据映射文件中包含的映射，将命名空间 URI 解析为实现类，默认情况下，此实现在  `META-INF/spring.handlers` 查找映射文件。

#### 2.2.2 doRegisterBeanDefinitions

&emsp;&emsp;如果说以前一直是 XML 加载解析的准备阶段，那么 doRegisterBeanDefinitions 算是真正地开始进行解析。
