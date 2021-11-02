# Spring源码解析（BeanFactory实现方式）八：单例 bean 的初始化

&emsp;&emsp;接上文，回到 doCreateBean 中来，前两步看完，下面接着看第三步。

## 1 记录早期的 bean

&emsp;&emsp;经过前面源码的阅读，可知现在的 bean 是处于刚建立对象，调用了构造方法，里面的属性和自动注入还并没有初始化的状态，回想之前提到过的循环依赖问题，Spring 容器的循环依赖包括构造器循环依赖和 Setter 循环依赖，Spring 将循环依赖的处理分为了三种情况来处理，分别为构造器循环依赖、prototype 作用域的 bean 的循环依赖和 Setter 循环依赖，其中构造器循环依赖和 prototype 作用域的 bean 的循环依赖是无法解决的，只能抛出异常。

```java
boolean earlySingletonExposure = (mbd.isSingleton() && this.allowCircularReferences &&
				isSingletonCurrentlyInCreation(beanName));
				//当前bean是单例且尝试自动处理循环依赖且当前bean正在创建
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

		PropertyValues pvs = (mbd.hasPropertyValues() ? mbd.getPropertyValues() : null);

		//获取自动装配方式
		int resolvedAutowireMode = mbd.getResolvedAutowireMode();
		//2.按属性类型或是按属性名称
		if (resolvedAutowireMode == AUTOWIRE_BY_NAME || resolvedAutowireMode == AUTOWIRE_BY_TYPE) {
			MutablePropertyValues newPvs = new MutablePropertyValues(pvs);
			// Add property values based on autowire by name if applicable.
			// 按名称注入
			if (resolvedAutowireMode == AUTOWIRE_BY_NAME) {
				autowireByName(beanName, mbd, bw, newPvs);
			}
			// Add property values based on autowire by type if applicable.
			// 安类型注入
			if (resolvedAutowireMode == AUTOWIRE_BY_TYPE) {
				autowireByType(beanName, mbd, bw, newPvs);
			}
			pvs = newPvs;
		}

		//3.是否有注册的增强器处理
		boolean hasInstAwareBpps = hasInstantiationAwareBeanPostProcessors();
		//是否需要依赖检查
		boolean needsDepCheck = (mbd.getDependencyCheck() != AbstractBeanDefinition.DEPENDENCY_CHECK_NONE);

		PropertyDescriptor[] filteredPds = null;
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
					//对所有需要依赖检查的属性进行后处理
					pvsToUse = bp.postProcessPropertyValues(pvs, filteredPds, bw.getWrappedInstance(), beanName);
					if (pvsToUse == null) {
						return;
					}
				}
				pvs = pvsToUse;
			}
		}
		if (needsDepCheck) {
			if (filteredPds == null) {
				filteredPds = filterPropertyDescriptorsForDependencyCheck(bw, mbd.allowCaching);
			}
			//4.依赖检查，对应depends-on属性，3.0已经弃用此属性
			checkDependencies(beanName, mbd, filteredPds, pvs);
		}

		if (pvs != null) {
			//将属性应用到bean中
			applyPropertyValues(beanName, mbd, bw, pvs);
		}
	}
```

&emsp;&emsp;在函数 populateBean 中一共做了这几步处理：

1. 提供一个增强器回调函数 InstantiationAwareBeanPostProcessor.postProcessAfterInstantiation 来实现自定义填充属性，并根据返回值决定是否需要由 Spring 继续处理。
2. 根据注入类型(ByName/ByType)，提取依赖的 bean，并统一存入 PropertyValues 中。
3. 提供一个增强器回调函数 InstantiationAwareBeanPostProcessor.postProcessPropertyValues 来实现对获取到的属性填充前的处理，例如 RequiredAnnotationBeanPostProcessor 中对属性的验证。
4. 将所有 PropertyValues 中的属性填充至 BeanWrapper 中。

### 2.1 autowireByName

**AbstractAutowireCapableBeanFactory.autowireByName**

```java
protected void autowireByName(
		String beanName, AbstractBeanDefinition mbd, BeanWrapper bw, MutablePropertyValues pvs) {
	// 获取 bean 中需要自动装配的属性集合
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

&emsp;&emsp;这里逻辑很简单，就是找到需要注入的属性，然后循环初始化注入的bean ，进而加入到准备好的 psv 中。

**AbstractAutowireCapableBeanFactory.unsatisfiedNonSimpleProperties : 寻找需要注入的属性**

```java
protected String[] unsatisfiedNonSimpleProperties(AbstractBeanDefinition mbd, BeanWrapper bw) {
	Set<String> result = new TreeSet<>();
	//获取 BeanDefinition 中存储的所有属性值 ( xml 中配置的 property )
	PropertyValues pvs = mbd.getPropertyValues();
	// 从前面包装bean实例的BeanWrapper中获取bean中的所有属性的集合
	PropertyDescriptor[] pds = bw.getPropertyDescriptors();
	//遍历 bean 中的所有属性
	for (PropertyDescriptor pd : pds) {
		// 如果当前属性有 setter 方法且 该属性是可以被注入的 且 xml 中的 property 属性中没有配置该属性
		// 且 该属性不是"简单值类型"，比如一些基本类型和基本类型的包装类型等
		if (pd.getWriteMethod() != null && !isExcludedFromDependencyCheck(pd) && !pvs.contains(pd.getName()) &&
				!BeanUtils.isSimpleProperty(pd.getPropertyType())) {
			result.add(pd.getName());
		}
	}
	return StringUtils.toStringArray(result);
}
```

&emsp;&emsp;注意看，这里寻找需要注入的属性的时候在 isExcludedFromDependencyCheck 方法中排除掉了不需要自动注入的类型和接口，这在前面第一篇文章中讲的 AbstractAutowireCapableBeanFactory 的初始化的时候添加的忽略接口正好对应。


### 2.2 autowireByType

[](https://www.jianshu.com/p/2670dda90828)