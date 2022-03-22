# Spring源码笔记四：beanDinition注册及其他默认标签的解析

## 1. 注册解析的 beanDinition

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

&emsp;&emsp;接上篇文章，默认标签的解析与提取和子节点自定义标签解析已经完成，回顾上篇文章第2小节 bean 标签的解析的 `processBeanDefinition` 方法做的工作：

1. 解析 bean 标签；

2. 当子节点下有自定义标签，对自定义标签进行解析；

3. 对解析后的的 bdHolder 进行注册；

4. 触发注册完成监听事件。

&emsp;&emsp;配置文件已经解析完了，对于得到的 beanDinition 已经可以满足后续的使用了，接下来就是注册工作，在之前第二篇文中说过，解析的 beanDefinition 都会被注册到 BeanDefinitionRegistry 类型的实例中，使用 beanName 作为 key，BeanDefinitionRegistry 是持有 BeanDefinition 的注册表的接口，它是 Spring 中 BeanFactory 容器中唯一封装 BeanDefinition 的注册表的接口 ，它定义了关于 BeanDefinition 的注册、移除、查询等一系列的操作。而对于 beanDinition 的注册分成了两部分：通过 beanName 注册 BeanDefinition 以及别名的注册。

**BeanDefinitionReaderUtils.registerBeanDefinition**

```java
public static void registerBeanDefinition(
			BeanDefinitionHolder definitionHolder, BeanDefinitionRegistry registry)
			throws BeanDefinitionStoreException {

		// 1.通过 beanName 注册 BeanDefinition
		String beanName = definitionHolder.getBeanName();
		registry.registerBeanDefinition(beanName, definitionHolder.getBeanDefinition());

		// 2.别名的注册
		String[] aliases = definitionHolder.getAliases();
		if (aliases != null) {
			for (String alias : aliases) {
				registry.registerAlias(beanName, alias);
			}
		}
	}
```

### 1.1 通过 beanName 注册 beanDinition 

**DefaultListableBeanFactory.registerBeanDefinition**

```java
public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition)
			throws BeanDefinitionStoreException {

		Assert.hasText(beanName, "Bean name must not be empty");
		Assert.notNull(beanDefinition, "BeanDefinition must not be null");

		if (beanDefinition instanceof AbstractBeanDefinition) {
			try {
				// 1.注册前的最后一次校验
				((AbstractBeanDefinition) beanDefinition).validate();
			}
			catch (BeanDefinitionValidationException ex) {
				throw new BeanDefinitionStoreException(beanDefinition.getResourceDescription(), beanName,
						"Validation of bean definition failed", ex);
			}
		}

		BeanDefinition existingDefinition = this.beanDefinitionMap.get(beanName);
		//2.对于 beanName 已经被注册的情况的处理
		if (existingDefinition != null) {
			//是否允许覆盖，如果设置为不允许且对应的beanName已经被覆盖则抛出异常
			if (!isAllowBeanDefinitionOverriding()) {
				throw new BeanDefinitionOverrideException(beanName, beanDefinition, existingDefinition);
			}
			//日志打印省略
			... ...
			//直接覆盖 beanName
			this.beanDefinitionMap.put(beanName, beanDefinition);
		}
		else {
			//3.对于 beanName 没有被注册的情况的处理
			//	先判断当前容器是否已经处于创建阶段，即有任意bean已经被创建过，通常表示项目已经开始运行
			if (hasBeanCreationStarted()) {
				// 处于创建阶段
				synchronized (this.beanDefinitionMap) {
					//注册 beanDefinition
					this.beanDefinitionMap.put(beanName, beanDefinition);
					//更新beanDefinitionNames列表
					//不能修改启动时集合元素，读写分离
					List<String> updatedDefinitions = new ArrayList<>(this.beanDefinitionNames.size() + 1);
					updatedDefinitions.addAll(this.beanDefinitionNames);
					updatedDefinitions.add(beanName);
					this.beanDefinitionNames = updatedDefinitions;
					//更新 manualSingletonNames 集合
					removeManualSingletonName(beanName);
				}
			}
			else {
				// 仍处于启动注册阶段，即没有任何bean被创建
				this.beanDefinitionMap.put(beanName, beanDefinition);
				this.beanDefinitionNames.add(beanName);
				removeManualSingletonName(beanName);
			}
			this.frozenBeanDefinitionNames = null;
		}
		//当前注册的bean的定义已经在beanDefinitionMap缓存中存在，或者其实例已经存在于单例bean的缓存中
		if (existingDefinition != null || containsSingleton(beanName)) {
			//清除上一个 bean 的缓存，在替换或删除现有 bean 定义后会调用
			resetBeanDefinition(beanName);
		}
		//bean是否被冻结，即不应该被进一步修改或后处理 所谓的冻结就是spring不会标识一个beanDefinition已经过期
		//在冻结之后,你如果对一个beanDefinition更改了属性,但是spring不会觉得这个beanDefinition已经过期
		//故而修改的不会生效
		else if (isConfigurationFrozen()) {
			//清除映射
			clearByTypeCache();
		}
	}
```

#### 注册前的校验

**AbstractBeanDefinition.validate**

```java
public void validate() throws BeanDefinitionValidationException {
    //不允许 methodOverrides 与 工厂方法并存
   if (hasMethodOverrides() && getFactoryMethodName() != null) {
      throw new BeanDefinitionValidationException(
            "Cannot combine factory method with container-generated method overrides: " +
            "the factory method must create the concrete bean instance.");
   }
   if (hasBeanClass()) {
      prepareMethodOverrides();
   }
}
```

**AbstractBeanDefinition.prepareMethodOverrides**

```java
public void prepareMethodOverrides() throws BeanDefinitionValidationException {
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
      // methodOverrides 对应方法唯一确定，不需要通过检测方法参数来确定方法，默认为true
      mo.setOverloaded(false);
   }
}
```

### 1.2 别名的注册

&emsp;&emsp;在看完通过 beanName 注册 beanDinition 的原理后，再看别名的注册。

**SimpleAliasRegistry.registerAlias**

```java
public void registerAlias(String name, String alias) {
   Assert.hasText(name, "'name' must not be empty");
   Assert.hasText(alias, "'alias' must not be empty");
   synchronized (this.aliasMap) {
      if (alias.equals(name)) {
		  //1.如果 beanName 与别名相同，则不记录，并删除对应的 alias 
         this.aliasMap.remove(alias);
		 ... ...
      }
      else {
         String registeredName = this.aliasMap.get(alias);
		 //如果别名已经被注册
         if (registeredName != null) {
            if (registeredName.equals(name)) {
               // 已经存在，无需重新注册
               return;
            }
			//不允许覆盖则报错
            if (!allowAliasOverriding()) {
               throw new IllegalStateException("Cannot define alias '" + alias + "' for name '" +
                     name + "': It is already registered for name '" + registeredName + "'.");
            }
            ... ...
         }
		 //验证别名正确性，不允许出现环路，即如果 (A & B) ->C，定义一个 C->A 此时出现环路
         checkForAliasCircle(name, alias);
		 //直接注册
         this.aliasMap.put(alias, name);
         ... ...
      }
   }
}
```

### 1.3 触发监听器

&emsp;&emsp;当别名和 beanDinition 都注册完成以后，最后一步就是触发监听事件，这里的实现只为拓展，当程序开发人员需要对注册  beanDinition 事件进行监听时，可以通过注册监听器的方式将处理逻辑写入监听器中。

## 2 其他默认标签的解析

&emsp;&emsp;上面分析完了默认标签中对 bean 标签的解析，前面说过默认标签一共有4种，下面看一下其他三种。 bean 标签的解析是最重要也最核心的，其他的解析也都是围绕这个进行的。

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

### 2.1 alias 标签

&emsp;&emsp;对 bean 进行定义时，除了使用 ID 属性来指定名称之外，还可以绑定多个名称，可以通过 alias 标签来指定，这些名称都指向同一个 bean ，在某些情况下很有用。

```xml
<bean id="myTestDemo" class="myTest.MyTestDemo" ></bean>
<alias name="myTestDemo" alias="myTestDemo2" />
```

&emsp;&emsp;alias 标签的解析过程在 `processAliasRegistration` 中

**DefaultBeanDefinitionDocumentReader.processAliasRegistration**

```java
protected void processAliasRegistration(Element ele) {
	//获取 beanName
   String name = ele.getAttribute(NAME_ATTRIBUTE);
   //获取 alias
   String alias = ele.getAttribute(ALIAS_ATTRIBUTE);
   boolean valid = true;
   //1.验证
   if (!StringUtils.hasText(name)) {
      getReaderContext().error("Name must not be empty", ele);
      valid = false;
   }
   if (!StringUtils.hasText(alias)) {
      getReaderContext().error("Alias must not be empty", ele);
      valid = false;
   }
   if (valid) {
      try {
		 //2.注册
         getReaderContext().getRegistry().registerAlias(name, alias);
      }
      catch (Exception ex) {
         getReaderContext().error("Failed to register alias '" + alias +
               "' for bean with name '" + name + "'", ele, ex);
      }
	  //3.触发监听器
      getReaderContext().fireAliasRegistered(name, alias, extractSource(ele));
   }
}
```

&emsp;&emsp;这里的解析方法逻辑很简单，先验证然后解析，最后触发监听事件，而注册方法 `registerAlias` 之前也已经看过了。

### 2.2 import 标签

&emsp;&emsp;对于 Spring 的配置文件，在项目越做越复杂时，不可避免的会带来配置文件过于庞杂的问题，将配置文件分模块管理就是解决办法之一。Spring 的配置文件中使用 import 的方式导入模块的配置文件。看一下 import 标签的解析。

**DefaultBeanDefinitionDocumentReader.importBeanDefinitionResource**

```java
protected void importBeanDefinitionResource(Element ele) {
	//获取resource属性
   String location = ele.getAttribute(RESOURCE_ATTRIBUTE);
   if (!StringUtils.hasText(location)) {
      getReaderContext().error("Resource location must not be empty", ele);
      return;
   }

   // 使用 PropertyPlaceholderHelper 解析系统占位符：例如 “${user.dir}”
   location = getReaderContext().getEnvironment().resolveRequiredPlaceholders(location);

   Set<Resource> actualResources = new LinkedHashSet<>(4);

   // 判断 location 是相对路径还是绝对路径
   boolean absoluteLocation = false;
   try {
      absoluteLocation = ResourcePatternUtils.isUrl(location) || ResourceUtils.toURI(location).isAbsolute();
   }
   catch (URISyntaxException ex) {
      // cannot convert to an URI, considering the location relative
      // unless it is the well-known Spring prefix "classpath*:"
   }

   // 如果是绝对路径则直接加载对应的配置文件
   if (absoluteLocation) {
      try {
         int importCount = getReaderContext().getReader().loadBeanDefinitions(location, actualResources);
         if (logger.isTraceEnabled()) {
            logger.trace("Imported " + importCount + " bean definitions from URL location [" + location + "]");
         }
      }
      catch (BeanDefinitionStoreException ex) {
         getReaderContext().error(
               "Failed to import bean definitions from URL location [" + location + "]", ele, ex);
      }
   }
   else {
      // 如果是相对地址则根据相对地址计算出绝对地址
      try {
         int importCount;
         Resource relativeResource = getReaderContext().getResource().createRelative(location);
         if (relativeResource.exists()) {
            importCount = getReaderContext().getReader().loadBeanDefinitions(relativeResource);
            actualResources.add(relativeResource);
         }
         else {
			 //解析失败，使用默认解析器 ResourcePatternResolver 解析
            String baseLocation = getReaderContext().getResource().getURL().toString();
            importCount = getReaderContext().getReader().loadBeanDefinitions(
                  StringUtils.applyRelativePath(baseLocation, location), actualResources);
         }
         if (logger.isTraceEnabled()) {
            logger.trace("Imported " + importCount + " bean definitions from relative location [" + location + "]");
         }
      }
      catch (IOException ex) {
         getReaderContext().error("Failed to resolve current resource location", ele, ex);
      }
      catch (BeanDefinitionStoreException ex) {
         getReaderContext().error(
               "Failed to import bean definitions from relative location [" + location + "]", ele, ex);
      }
   }
   Resource[] actResArray = actualResources.toArray(new Resource[0]);
   //触发监听器
   getReaderContext().fireImportProcessed(location, actResArray, extractSource(ele));
}
```

#### 解析系统占位符

**PropertyPlaceholderHelper.parseStringValue**

对于 import 标签的解析逻辑是比较简单的，总结下来大致顺序如下：

1. 获取resource属性
2. 解析系统占位符
3. 判断 location 是相对路径还是绝对路径
4. 如果是绝对路径则直接加载对应的配置文件
5. 如果是相对地址则根据相对地址计算出绝对地址
6. 触发监听器，解析完成

其中解析系统占位符算是最为复杂的一步

````java
protected String parseStringValue(
			String value, PlaceholderResolver placeholderResolver, @Nullable Set<String> visitedPlaceholders) {
		//查找和当前 前缀(${)相匹配 后缀(})的index,比如 ${${user}}
		int startIndex = value.indexOf(this.placeholderPrefix);
		if (startIndex == -1) {
			return value;
		}
		
		StringBuilder result = new StringBuilder(value);
		while (startIndex != -1) {
			int endIndex = findPlaceholderEndIndex(result, startIndex);
			if (endIndex != -1) {
				//获取需要被替换的占位符字符串
				String placeholder = result.substring(startIndex + this.placeholderPrefix.length(), endIndex);
				String originalPlaceholder = placeholder;
				if (visitedPlaceholders == null) {
					visitedPlaceholders = new HashSet<>(4);
				}
				//避免循环占位符引用
				if (!visitedPlaceholders.add(originalPlaceholder)) {
					throw new IllegalArgumentException(
							"Circular placeholder reference '" + originalPlaceholder + "' in property definitions");
				}
				// 递归调用，获取 ${} 内的内容，比如${${user}} ，第一次执行 parseStringValue 
				// 方法得到${user}，再一次执行parseStringValue 方法，得到user
				placeholder = parseStringValue(placeholder, placeholderResolver, visitedPlaceholders);
				// 由上面得到的值调用 resolvePlaceholder 方法得到具体的值
				String propVal = placeholderResolver.resolvePlaceholder(placeholder);
				if (propVal == null && this.valueSeparator != null) {
					//若需要解析的表达式格式为${user:zhangsan}，先解析得到 user:zhangsan
					//通过 user:zhangsan 找不到对应的值，那么通过 : 作为分隔符
					int separatorIndex = placeholder.indexOf(this.valueSeparator);
					if (separatorIndex != -1) {
						//得到第一个值：user
						String actualPlaceholder = placeholder.substring(0, separatorIndex);
						//得到第二个值：zhangsan
						String defaultValue = placeholder.substring(separatorIndex + this.valueSeparator.length());						    //先通过 第一个值：user 解析
						propVal = placeholderResolver.resolvePlaceholder(actualPlaceholder);
						// 如果解析不到则 使用第二个值：zhangsan 作为默认值
						if (propVal == null) {
							propVal = defaultValue;
						}
					}
				}
				if (propVal != null) {
					// 递归调用，继续解析可能包含的嵌套的占位符。
					propVal = parseStringValue(propVal, placeholderResolver, visitedPlaceholders);
					// 替换占位符
					result.replace(startIndex, endIndex + this.placeholderSuffix.length(), propVal);
					if (logger.isTraceEnabled()) {
						logger.trace("Resolved placeholder '" + placeholder + "'");
					}
					//如果有的话，继续循环解析后面的占位符
					startIndex = result.indexOf(this.placeholderPrefix, startIndex + propVal.length());
				}
				// 无法解析的情况
				else if (this.ignoreUnresolvablePlaceholders) {
					startIndex = result.indexOf(this.placeholderPrefix, endIndex + this.placeholderSuffix.length());
				}
				else {
					throw new IllegalArgumentException("Could not resolve placeholder '" +
							placeholder + "'" + " in value \"" + value + "\"");
				}
				visitedPlaceholders.remove(originalPlaceholder);
			}
			else {
				startIndex = -1;
			}
		}
		return result.toString();
	}

````

### 2.3 beans 标签

&emsp;&emsp;对于 beans 标签的解析，与单独的配置文件没有太大差别，就是递归调用 beans 的解析过程。看到这里，所有的默认标签解析已经完成，并且也完成了 beanDinition 的注册工作，接下来就是自定义标签的解析过程。