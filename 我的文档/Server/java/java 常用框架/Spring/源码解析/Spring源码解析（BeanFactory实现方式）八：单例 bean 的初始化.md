# Spring源码解析（BeanFactory实现方式）八：单例 bean 的初始化

&emsp;&emsp;接上文，回到 doCreateBean 中来，前两步看完，下面接着看第三步。

## 1 记录早期的 bean

&emsp;&emsp;现在的 bean 处于刚刚实例化，调用了构造方法，里面需要注入的属性还并没有初始化的状态，回到之前提到过的循环依赖问题，Spring 将其处理分为了三种情况，分别为**构造器循环依赖**、**prototype 作用域的 bean 的循环依赖**和 **Setter 循环依赖**，其中**构造器循环依赖**和 **prototype 作用域的 bean 的循环依赖**是**无法解决**的(多例某些情况可以注入成功，比如A为单例，B为多例)。

```java
boolean earlySingletonExposure = (mbd.isSingleton() && this.allowCircularReferences &&
		isSingletonCurrentlyInCreation(beanName));
		//当前bean是单例且需要自动处理循环依赖且当前bean正在创建
		//在 Spring 中，会有一个个专门的属性默认为 DefaultSingletonBeanRegistry 的 singletonsCurrentlyInCreation 
		//来记录 bean 的加载状态，在 bean 开始创建前会将 beanName 记录在属性中，在 bean 创建结束后会将 beanName 从属性中移除。
if (earlySingletonExposure) {
	if (logger.isTraceEnabled()) {
		logger.trace("Eagerly caching bean '" + beanName +
				"' to allow for resolving potential circular references");
	}
	//在 bean 初始化完成前将创建实例的ObjectFactory实现加入缓存
	//getEarlyBeanReference 就是 ObjectFactory 的匿名实现，这里并不会执行
	//后面调用才会执行
	addSingletonFactory(beanName, () -> getEarlyBeanReference(beanName, mbd, bean));
}
```

&emsp;&emsp;这里最重要的一步就是在 bean 初始化完成前将创建实例的 ObjectFactory 的实现加入 DefaultSingletonBeanRegistry 的 singletonFactories 属性中，也就是三级缓存中，ObjectFactory 是一个函数式接口，仅有一个方法，可以传入 lambda 表达式，可以是匿名内部类，通过调用 getObject 方法来执行具体的逻辑。

**DefaultSingletonBeanRegistry.addSingletonFactory**

```java
protected void addSingletonFactory(String beanName, ObjectFactory<?> singletonFactory) {
		Assert.notNull(singletonFactory, "Singleton factory must not be null");
		synchronized (this.singletonObjects) {
			if (!this.singletonObjects.containsKey(beanName)) {
				//三级缓存 创建实例的ObjectFactory实现
				this.singletonFactories.put(beanName, singletonFactory);
				//将bean从二级缓存中移除
				this.earlySingletonObjects.remove(beanName);
                 //表示哪些类被注册过了，加入
				this.registeredSingletons.add(beanName);
			}
		}
	}
```

&emsp;&emsp;再来看看这里创建实例的 ObjectFactory 的实现 getEarlyBeanReference 方法。

```java
protected Object getEarlyBeanReference(String beanName, RootBeanDefinition mbd, Object bean) {
		Object exposedObject = bean;
		if (!mbd.isSynthetic() && hasInstantiationAwareBeanPostProcessors()) {
			for (SmartInstantiationAwareBeanPostProcessor bp : getBeanPostProcessorCache().smartInstantiationAware) {
				exposedObject = bp.getEarlyBeanReference(exposedObject, beanName);
			}
		}
		return exposedObject;
	}
```

&emsp;&emsp;这里又是熟悉的 bean 增强器处理，主要应用 SmartInstantiationAwareBeanPostProcessor 增强器，其中 AOP 的处理就是在这里将 advice 动态织入 bean 中的，若没有则直接返回 bean，不做任何处理。

## 2 填充 bean 的属性

**AbstractAutowireCapableBeanFactory.populateBean : 填充属性**

```java
protected void populateBean(String beanName, RootBeanDefinition mbd, @Nullable BeanWrapper bw) {
	if (bw == null) {
		if (mbd.hasPropertyValues()) {
			//当前 BeanWrapper 为 null 但 BeanDefinition 有值，无法将属性值应用于 null 的实例
			throw new BeanCreationException(
					mbd.getResourceDescription(), beanName, "Cannot apply property values to null instance");
		}
		else {
			// Skip property population phase for null instance.
			// 没有可填充的属性
			return;
		}
	}

	// Give any InstantiationAwareBeanPostProcessors the opportunity to modify the
	// state of the bean before properties are set. This can be used, for example,
	// to support styles of field injection.
	// 1. 应用在属性填充之前的 bean 增强器,可以在在属性填充之前有机会修改 bean 的状态
	if (!mbd.isSynthetic() && hasInstantiationAwareBeanPostProcessors()) {
		for (InstantiationAwareBeanPostProcessor bp : getBeanPostProcessorCache().instantiationAware) {
			//返回值为是否继续填充bean
			if (!bp.postProcessAfterInstantiation(bw.getWrappedInstance(), beanName)) {
				return;
			}
		}
	}

	// 下面正式开始属性填充
	// 这里获取的是已经解析过的属性，就是前面 xml 解析的 property 属性参数
	PropertyValues pvs = (mbd.hasPropertyValues() ? mbd.getPropertyValues() : null);

	int resolvedAutowireMode = mbd.getResolvedAutowireMode();
	// 2 扫描获取自动装配属性
	// 这里的自动装配是指在当前 bean 中的有 setter 方法的
	// 且 xml 中的 property 属性中没有配置的属性，因为上面的 pvs 中已经包含了 xml 中的 property 属性
	// 按属性类型或是按属性名称获取
	if (resolvedAutowireMode == AUTOWIRE_BY_NAME || resolvedAutowireMode == AUTOWIRE_BY_TYPE) {
		// 构造一个新的 MutablePropertyValues 来存放前面获取的属性和即将扫描到的需要自动装配的属性
		MutablePropertyValues newPvs = new MutablePropertyValues(pvs);
		// Add property values based on autowire by name if applicable.
		// 按属性名称的属性获取
		if (resolvedAutowireMode == AUTOWIRE_BY_NAME) {
			autowireByName(beanName, mbd, bw, newPvs);
		}
		// Add property values based on autowire by type if applicable.
		// 按属性类型的属性获取
		if (resolvedAutowireMode == AUTOWIRE_BY_TYPE) {
			autowireByType(beanName, mbd, bw, newPvs);
		}
		// 现在 pvs 中已经包含需要自动装配的属性参数了
		pvs = newPvs;
	}

	boolean hasInstAwareBpps = hasInstantiationAwareBeanPostProcessors();
	boolean needsDepCheck = (mbd.getDependencyCheck() != AbstractBeanDefinition.DEPENDENCY_CHECK_NONE);

	PropertyDescriptor[] filteredPds = null;
	// 3. 注解注入处理
	if (hasInstAwareBpps) {
		if (pvs == null) {
			pvs = mbd.getPropertyValues();
		}
		for (InstantiationAwareBeanPostProcessor bp : getBeanPostProcessorCache().instantiationAware) {
			PropertyValues pvsToUse = bp.postProcessProperties(pvs, bw.getWrappedInstance(), beanName);
			if (pvsToUse == null) {
				if (filteredPds == null) {
					filteredPds = filterPropertyDescriptorsForDependencyCheck(bw, mbd.allowCaching);
				}
				pvsToUse = bp.postProcessPropertyValues(pvs, filteredPds, bw.getWrappedInstance(), beanName);
				if (pvsToUse == null) {
					return;
				}
			}
			pvs = pvsToUse;
		}
	}
	// 4. 依赖检查
	if (needsDepCheck) {
		if (filteredPds == null) {
			filteredPds = filterPropertyDescriptorsForDependencyCheck(bw, mbd.allowCaching);
		}
		//依赖检查，对应 depends-on 属性，用来表示一个bean A的实例化依赖另一个bean B的实例化
		checkDependencies(beanName, mbd, filteredPds, pvs);
	}

	if (pvs != null) {
		//5.将属性参数应用到 bean 中
		applyPropertyValues(beanName, mbd, bw, pvs);
	}
}
```

&emsp;&emsp;在函数 populateBean 中一共做了这几步处理：

1. 提供一个在属性填充之前的 bean 增强器回调函数 InstantiationAwareBeanPostProcessor.postProcessAfterInstantiation 来实现在属性填充之前有机会修改 bean 的属性，并可以根据返回值决定是否需要由 Spring 继续处理。
2. 扫描获取自动装配属性，根据不同的注入类型(ByName/ByType)，来获取需要自动装配的属性，并统一存入 PropertyValues 中。这里的自动装配的属性是指在当前 bean 中的、有 setter 方法的，且 xml 中的 property 中**没有**配置的属性，因为在 xml 中的 property 中配置的属性早在 xml 解析的时候就已经解析成为 PropertyValues 并存储在 BeanDefinition 中了。
3. 处理在上篇文中由增强器扫描记录的注解注入的属性，依然由其对应的增强器自己注入。
4. 进行依赖检查。
5. 将所有 PropertyValues 中的属性填充至 bean 中。

### 2.1 autowireByName

**AbstractAutowireCapableBeanFactory.autowireByName**

```java
protected void autowireByName(
		String beanName, AbstractBeanDefinition mbd, BeanWrapper bw, MutablePropertyValues pvs) {
	// 返回需要自动注入的属性名数组
	// 如果当前属性有 setter 方法且 该属性是可以被注入的 且 pvs中没有配置该属性
	// 且 该属性不是"简单值类型"，比如一些基本类型和基本类型的包装类型等
	String[] propertyNames = unsatisfiedNonSimpleProperties(mbd, bw);
	// 遍历属性
	for (String propertyName : propertyNames) {
		if (containsBean(propertyName)) {
			//循环初始化相关bean
			Object bean = getBean(propertyName);
			pvs.add(propertyName, bean);
			//注册依赖，告诉容器说我当前被注入的bean初始化的时候需要依赖哪些bean
			registerDependentBean(propertyName, beanName);
			if (logger.isTraceEnabled()) {
				logger.trace("Added autowiring by name from bean name '" + beanName +
						"' via property '" + propertyName + "' to bean named '" + propertyName + "'");
			}
		}
		else {
			if (logger.isTraceEnabled()) {
				logger.trace("Not autowiring property '" + propertyName + "' of bean '" + beanName +
						"' by name: no matching bean found");
			}
		}
	}
}
```

&emsp;&emsp;这里逻辑很简单，就是找到需要自动装配的属性，然后循环创建相关 bean ，进而将 bean 加入到准备好的 psv 中。

**AbstractAutowireCapableBeanFactory.unsatisfiedNonSimpleProperties : 寻找自动装配的属性**

```java
protected String[] unsatisfiedNonSimpleProperties(AbstractBeanDefinition mbd, BeanWrapper bw) {
	Set<String> result = new TreeSet<>();
	//获取 BeanDefinition 中存储的所有属性值 
	PropertyValues pvs = mbd.getPropertyValues();
	// 从前面包装bean实例的BeanWrapper中获取bean中的所有属性的集合
	PropertyDescriptor[] pds = bw.getPropertyDescriptors();
	//遍历 bean 中的所有属性
	for (PropertyDescriptor pd : pds) {
		// 如果当前属性有 setter 方法且 该属性是可以被注入的 且 当前 pvs 没有该属性
		// 且 该属性不是"简单值类型"，比如一些基本类型和基本类型的包装类型等
		if (pd.getWriteMethod() != null && !isExcludedFromDependencyCheck(pd) && !pvs.contains(pd.getName()) &&
				!BeanUtils.isSimpleProperty(pd.getPropertyType())) {
			result.add(pd.getName());
		}
	}
	return StringUtils.toStringArray(result);
}
```

&emsp;&emsp;这里寻找需要注入的属性的时候在 isExcludedFromDependencyCheck 方法中排除掉了不需要自动注入的类型和接口，这在前面第一篇文章中讲的 AbstractAutowireCapableBeanFactory 的初始化的时候添加的忽略接口正好对应。

### 2.2 autowireByType

**AbstractAutowireCapableBeanFactory.autowireByType**

```java
protected void autowireByType(
		String beanName, AbstractBeanDefinition mbd, BeanWrapper bw, MutablePropertyValues pvs) {
	//获取工厂的自定义类型转换器
	TypeConverter converter = getCustomTypeConverter();
	if (converter == null) {
		//没有就使用 BeanWrapper 来充当默认的类型转换器
		converter = bw;
	}
	// 提供对集合类型参数自动装配的支持，用来记录集合中所有依赖的 bean
	Set<String> autowiredBeanNames = new LinkedHashSet<>(4);
	// 1. 获取需要自动装配的属性名数组
	// 如果当前属性有 setter 方法且 该属性是可以被注入的 且 当前 pvs 中没有该属性
	// 且 该属性不是"简单值类型"，比如一些基本类型和基本类型的包装类型等
	String[] propertyNames = unsatisfiedNonSimpleProperties(mbd, bw);
	// 2. 遍历需要自动注入的属性数组
	for (String propertyName : propertyNames) {
		try {
			// 2.1 从 BeanWrapper 中获取该属性的属性描述符
			PropertyDescriptor pd = bw.getPropertyDescriptor(propertyName);
			// Don't try autowiring by type for type Object: never makes sense,
			// even if it technically is a unsatisfied, non-simple property.
			// 不要尝试按类型为 Object 类型自动装配：永远没有意义，即使它在技术上是一个 "非简单" 的属性。
			if (Object.class != pd.getPropertyType()) {
				// 获取该属性对应的 Setter 方法的方法描述
				MethodParameter methodParam = BeanUtils.getWriteMethodParameter(pd);
				// Do not allow eager init for type matching in case of a prioritized post-processor.
				// 判断当前被注入的 bean 对象是否是 PriorityOrder 实例，如果是就不允许急于初始化来进行类型匹配。
				boolean eager = !(bw.getWrappedInstance() instanceof PriorityOrdered);
				// 将属性的 setter 方法封装成为一个 DependencyDescriptor，其中包括包装函数参数、方法参数或字段等信息
				// 这个对象非常重要，是依赖注入场景中的非常核心的对象
				DependencyDescriptor desc = new AutowireByTypeDependencyDescriptor(methodParam, eager);

				// 3. resolveDependency 方法根据类型查找依赖，是 Spring 进行依赖查找的核心方法
				Object autowiredArgument = resolveDependency(desc, beanName, autowiredBeanNames, converter);
				if (autowiredArgument != null) {
					// 将匹配到的 bean 加入 pvs
					pvs.add(propertyName, autowiredArgument);
				}
				// 4. 注册集合类型参数依赖的 bean
				for (String autowiredBeanName : autowiredBeanNames) {
					registerDependentBean(autowiredBeanName, beanName);
					if (logger.isTraceEnabled()) {
						logger.trace("Autowiring by type from bean name '" + beanName + "' via property '" +
								propertyName + "' to bean named '" + autowiredBeanName + "'");
					}
				}
				autowiredBeanNames.clear();
			}
		}
		catch (BeansException ex) {
			throw new UnsatisfiedDependencyException(mbd.getResourceDescription(), beanName, propertyName, ex);
		}
	}
}
```

&emsp;&emsp;在按类型扫描获取匹配的 bean 时，其功能实现明显要比按名称扫描获取复杂的多：

1. 获取需要自动装配的属性名数组；
2. 遍历需要自动注入的属性数组；
3. 根据类型查找依赖的 bean；
4. 注册集合类型参数依赖的 bean。

&emsp;&emsp;其中最复杂的就是寻找类型匹配的 bean，同时，Spring 还提供了对集合类型的参数自动装配的支持，如：

```java
private List<Test> tests;
```

&emsp;&emsp;Spring 将会把所有与 Test 匹配的类型找出来，并注入到 tests 属性中，正是由于这一因素，所以在函数中新建了局部变量 autowiredBeanNames 用于存储所有依赖的 bean，如果只是对非集合类的属性注入来说，此属性并无用处。

#### 根据类型查找依赖

**DefaultListableBeanFactory.resolveDependency**

```java
public Object resolveDependency(DependencyDescriptor descriptor, @Nullable String requestingBeanName,
		@Nullable Set<String> autowiredBeanNames, @Nullable TypeConverter typeConverter) throws BeansException {

	descriptor.initParameterNameDiscovery(getParameterNameDiscoverer());
	// 1. Optional 处理
	if (Optional.class == descriptor.getDependencyType()) {
		return createOptionalDependency(descriptor, requestingBeanName);
	}
	// 2. ObjectFactory 或 ObjectProvider 的处理
	else if (ObjectFactory.class == descriptor.getDependencyType() ||
			ObjectProvider.class == descriptor.getDependencyType()) {
		return new DependencyObjectProvider(descriptor, requestingBeanName);
	}
	// 3. Jsr330 规范相关处理
	else if (javaxInjectProviderClass == descriptor.getDependencyType()) {
		return new Jsr330Factory().createDependencyProvider(descriptor, requestingBeanName);
	}
	// 4. 通用自动装配匹配逻辑
	else {
		// 懒加载的处理
		Object result = getAutowireCandidateResolver().getLazyResolutionProxyIfNecessary(
				descriptor, requestingBeanName);
		if (result == null) {
			// 根据类型匹配 bean
			result = doResolveDependency(descriptor, requestingBeanName, autowiredBeanNames, typeConverter);
		}
		return result;
	}
}
```

&emsp;&emsp;其中核心逻辑是 doResolveDependency。

**DefaultListableBeanFactory.doResolveDependency**

```java
public Object doResolveDependency(DependencyDescriptor descriptor, @Nullable String beanName,
		@Nullable Set<String> autowiredBeanNames, @Nullable TypeConverter typeConverter) throws BeansException {

	InjectionPoint previousInjectionPoint = ConstructorResolver.setCurrentInjectionPoint(descriptor);
	try {
		// 1. 快速查找，默认实现返回 null ，可由子类实现来提供一个快捷匹配 bean 的方法
		Object shortcut = descriptor.resolveShortcut(this);
		if (shortcut != null) {
			return shortcut;
		}
		// 当没有快捷匹配时，进入正常匹配流程
		// 如果参数是被容器包裹的对象，则获取被包裹的真实参数类型，
		// 比如获取可能被 Optional 包装的参数类型
		// 没有被包裹则直接获取参数类型
		Class<?> type = descriptor.getDependencyType();
		// 2. @value 注解处理
		// 2.1 获取方法或方法参数上可能存在的 @Value 注解的 value 值
		Object value = getAutowireCandidateResolver().getSuggestedValue(descriptor);
		if (value != null) {
			// 2.2 解析 value 的值
			if (value instanceof String) {
				// 解析 value 中的占位符 例如 ${user.name}
				String strVal = resolveEmbeddedValue((String) value);
				BeanDefinition bd = (beanName != null && containsBean(beanName) ?
						getMergedBeanDefinition(beanName) : null);
				// 解析 value 中的 Spel 表达式 #{}
				value = evaluateBeanDefinitionString(strVal, bd);
			}
			// 2.3 拿到解析后的 value 值后，转换成需要的类型后返回
			TypeConverter converter = (typeConverter != null ? typeConverter : getTypeConverter());
			try {
				return converter.convertIfNecessary(value, type, descriptor.getTypeDescriptor());
			}
			catch (UnsupportedOperationException ex) {
				// A custom TypeConverter which does not support TypeDescriptor resolution...
				return (descriptor.getField() != null ?
						converter.convertIfNecessary(value, type, descriptor.getField()) :
						converter.convertIfNecessary(value, type, descriptor.getMethodParameter()));
			}
		}
		// 下面是没有 @Value 注解的情况
		// 3. 集合、数组等类型的处理
		Object multipleBeans = resolveMultipleBeans(descriptor, beanName, autowiredBeanNames, typeConverter);
		if (multipleBeans != null) {
			return multipleBeans;
		}

		// 4. 查找与所需类型匹配的 bean 实例 (可能有多个匹配)
		// matchingBeans：key = beanName，value = 对应的 bean 实例
		Map<String, Object> matchingBeans = findAutowireCandidates(beanName, type, descriptor);
		if (matchingBeans.isEmpty()) {
			// 如果没找到，是必须注入则报错，否则返回 null
			if (isRequired(descriptor)) {
				raiseNoMatchingBeanFound(type, descriptor.getResolvableType(), descriptor);
			}
			return null;
		}
		// 候选 bean 名称
		String autowiredBeanName;
		// 候选 bean 实例
		Object instanceCandidate;

		// 如果匹配到多个
		if (matchingBeans.size() > 1) {
			// 确定给定 bean 集合中的自动装配的唯一候选者名称
			autowiredBeanName = determineAutowireCandidate(matchingBeans, descriptor);
			if (autowiredBeanName == null) {
				if (isRequired(descriptor) || !indicatesMultipleBeans(type)) {
					return descriptor.resolveNotUnique(descriptor.getResolvableType(), matchingBeans);
				}
				else {
					// In case of an optional Collection/Map, silently ignore a non-unique case:
					// possibly it was meant to be an empty collection of multiple regular beans
					// (before 4.3 in particular when we didn't even look for collection beans).
					return null;
				}
			}
			instanceCandidate = matchingBeans.get(autowiredBeanName);
		}
		else {
			// We have exactly one match.
			// 刚好只有一个匹配
			Map.Entry<String, Object> entry = matchingBeans.entrySet().iterator().next();
			autowiredBeanName = entry.getKey();
			instanceCandidate = entry.getValue();
		}

		if (autowiredBeanNames != null) {
			autowiredBeanNames.add(autowiredBeanName);
		}
		if (instanceCandidate instanceof Class) {
			instanceCandidate = descriptor.resolveCandidate(autowiredBeanName, type, this);
		}
		Object result = instanceCandidate;
		if (result instanceof NullBean) {
			if (isRequired(descriptor)) {
				raiseNoMatchingBeanFound(type, descriptor.getResolvableType(), descriptor);
			}
			result = null;
		}
		if (!ClassUtils.isAssignableValue(type, result)) {
			throw new BeanNotOfRequiredTypeException(autowiredBeanName, type, instanceCandidate.getClass());
		}
		return result;
	}
	finally {
		ConstructorResolver.setCurrentInjectionPoint(previousInjectionPoint);
	}
}
```

1. 通过提供一个自定义 DependencyDescriptor 来提供一个快捷匹配 bean 的方法，默认实现返回 null；
2. @value 注解处理，包括 value 值的解析转换工作；
3. 集合、数组等类型的自动装配匹配处理；
4. 查找与所需类型匹配的 bean 实例；
5. 确定唯一的候选 bean 并返回。

## 3 注解注入处理

&emsp;&emsp;注解的注入处理是通过其对应的增强器完成的，这里以处理 @Autowired 注解的 AutowiredAnnotationBeanPostProcessor 来看实现过程。

**AutowiredAnnotationBeanPostProcessor.postProcessProperties**

```java
public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) {
	// 判断是否需要重新扫描注解
	InjectionMetadata metadata = findAutowiringMetadata(beanName, bean.getClass(), pvs);
	try {
		metadata.inject(bean, beanName, pvs);
	}
	catch (BeanCreationException ex) {
		throw ex;
	}
	catch (Throwable ex) {
		throw new BeanCreationException(beanName, "Injection of autowired dependencies failed", ex);
	}
	return pvs;
}
```

**InjectionMetadata.inject**

```java
public void inject(Object target, @Nullable String beanName, @Nullable PropertyValues pvs) throws Throwable {
	Collection<InjectedElement> checkedElements = this.checkedElements;
	Collection<InjectedElement> elementsToIterate =
			(checkedElements != null ? checkedElements : this.injectedElements);
	// 遍历要注入的属性并执行注入
	if (!elementsToIterate.isEmpty()) {
		for (InjectedElement element : elementsToIterate) {
			element.inject(target, beanName, pvs);
		}
	}
}
```

**AutowiredAnnotationBeanPostProcessor.inject**

```java
protected void inject(Object bean, @Nullable String beanName, @Nullable PropertyValues pvs) throws Throwable {
	Field field = (Field) this.member;
	Object value;
	// 有缓存则获取缓存方法参数或字段值去解析
	if (this.cached) {
		try {
			value = resolvedCachedArgument(beanName, this.cachedFieldValue);
		}
		catch (NoSuchBeanDefinitionException ex) {
			// Unexpected removal of target bean for cached argument -> re-resolve
			// 意外删除了缓存参数的目标 bean -> 重新解析
			value = resolveFieldValue(field, bean, beanName);
		}
	}
	else {
		value = resolveFieldValue(field, bean, beanName);
	}
	if (value != null) {
		// 判断字段是否可写
		ReflectionUtils.makeAccessible(field);
		// 为字段写入值
		field.set(bean, value);
	}
}
```

&emsp;&emsp;上面是对 @Autowired 标注的字段的解析注入过程，可以看出逻辑并不复杂，而对于 @Autowired 标注的方法来说，无非就是将解析的字段换成了方法参数，其余并没有什么不同，对于其他几个注解的自动注入也是大同小异，这里不再赘述。



&emsp;&emsp;到这里，已经完成了对所有自动装配属性的获取，但是获取的属性是以 PropertyValues 的形式存在的，还没有应用到实例化的 bean 中，接下来就是将这些属性应用到 bean 实例中。