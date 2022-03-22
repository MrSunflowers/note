# Spring源码笔记十：AOP 的使用

&emsp;&emsp;上篇文中简单介绍了 AOP 的相关概念和标签解析过程，并完成了对自动代理创建器 AspectJAwareAdvisorAutoProxyCreator 的配置和注册过程，AnnotationAwareAspectJAutoProxyCreator 在 AspectJAwareAdvisorAutoProxyCreator 的基础上进行了拓展实现 ，AnnotationAwareAspectJAutoProxyCreator 的注册过程与 AspectJAwareAdvisorAutoProxyCreator 基本一致，这里不再赘述，所以这里直接从 AnnotationAwareAspectJAutoProxyCreator 的工作开始分析。

## 1 创建代理前的准备工作

&emsp;&emsp;实现了 BeanPostProcessor 接口的 bean 都会在 ApplicationContext 容器启动时被注册，并在其他 bean 实例化过程中通过其对应的接口方法实现对 bean 的附加处理，所以处理的第一步的入口就是 postProcessBeforeInstantiation。

**AbstractAutoProxyCreator.postProcessBeforeInstantiation**

```java
public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) {
	// 生成一个名称
	Object cacheKey = getCacheKey(beanClass, beanName);
	// 没有被处理过
	if (!StringUtils.hasLength(beanName) || !this.targetSourcedBeans.contains(beanName)) {
        // advisedBeans 保存所有需要被增强的 bean
		if (this.advisedBeans.containsKey(cacheKey)) {
			return null;
		}
		// 表示不应被代理的基础结构类 || 给定的 bean 名称是需要跳过代理的 bean
		// 当使用 bean 的全限定名称 + .ORIGINAL 表示当前类跳过代理
		if (isInfrastructureClass(beanClass) || shouldSkip(beanClass, beanName)) {
			this.advisedBeans.put(cacheKey, Boolean.FALSE);
			return null;
		}
	}

	// Create proxy here if we have a custom TargetSource.
	// Suppresses unnecessary default instantiation of the target bean:
	// The TargetSource will handle target instances in a custom fashion.
	// 如果配置了 TargetSourceCreator 那么将在这里创建实例，以禁止目标 bean 的不必要的默认实例化：
	// TargetSource 将以自定义方式处理目标实例。
	TargetSource targetSource = getCustomTargetSource(beanClass, beanName);
	if (targetSource != null) {
		if (StringUtils.hasLength(beanName)) {
			this.targetSourcedBeans.add(beanName);
		}
		Object[] specificInterceptors = getAdvicesAndAdvisorsForBean(beanClass, beanName, targetSource);
		Object proxy = createProxy(beanClass, beanName, specificInterceptors, targetSource);
		this.proxyTypes.put(cacheKey, proxy.getClass());
		return proxy;
	}

	return null;
}
```

### 1.1 判断是否为基础结构类

**AnnotationAwareAspectJAutoProxyCreator.isInfrastructureClass**

```java
protected boolean isInfrastructureClass(Class<?> beanClass) {
	// Previously we setProxyTargetClass(true) in the constructor, but that has too
	// broad an impact. Instead we now override isInfrastructureClass to avoid proxying
	// aspects. I'm not entirely happy with that as there is no good reason not
	// to advise aspects, except that it causes advice invocation to go through a
	// proxy, and if the aspect implements e.g the Ordered interface it will be
	// proxied by that interface and fail at runtime as the advice method is not
	// defined on the interface. We could potentially relax the restriction about
	// not advising aspects in the future.
	return (super.isInfrastructureClass(beanClass) ||
			(this.aspectJAdvisorFactory != null && this.aspectJAdvisorFactory.isAspect(beanClass)));
}
```

isInfrastructureClass 方法做了两件事：
1. 调用父类的 isInfrastructureClass 方法，看是不是基础类：Advice，Pointcut，Advisor 或 AopInfrastructureBean 的子类。
2. 调用 isAspect 方法看是不是有可处理的 @Aspect 注解，是否可处理主要查看 Class 是不是由 ajc 编译。

### 1.2 shouldSkip

**AspectJAwareAdvisorAutoProxyCreator.shouldSkip**

```java
protected boolean shouldSkip(Class<?> beanClass, String beanName) {
	// TODO: Consider optimization by caching the list of the aspect names
	// 在当前工厂中查找并实例化所有符合条件的 Advisor bean
	List<Advisor> candidateAdvisors = findCandidateAdvisors();
	//“advisor”、“before”、“after”、“after-returning”、“after-throwing” 或 “around”
	for (Advisor advisor : candidateAdvisors) {
		if (advisor instanceof AspectJPointcutAdvisor &&
				((AspectJPointcutAdvisor) advisor).getAspectName().equals(beanName)) {
			return true;
		}
	}
	// 如果 beanName 使用 bean 的全限定名称 + .ORIGINAL 表示当前类跳过代理
	return super.shouldSkip(beanClass, beanName);
}
```

&emsp;&emsp;shouldSkip 方法负责，在当前工厂中查找并实例化所有的 Advisor，然后找出不需要处理的 bean。



&emsp;&emsp;这里的 findCandidateAdvisors 方法由 AnnotationAwareAspectJAutoProxyCreator 实现。

**AnnotationAwareAspectJAutoProxyCreator.findCandidateAdvisors**

```java
protected List<Advisor> findCandidateAdvisors() {
	// Add all the Spring advisors found according to superclass rules.
	// 1.调用父类方法加载配置文件中的 AOP 声明
	List<Advisor> advisors = super.findCandidateAdvisors();
	// Build Advisors for all AspectJ aspects in the bean factory.
	if (this.aspectJAdvisorsBuilder != null) {
		// 2.加载注解中的 Advisor
		advisors.addAll(this.aspectJAdvisorsBuilder.buildAspectJAdvisors());
	}
	return advisors;
}
```

#### 1.2.1 从配置文件中加载 Advisor

**AbstractAdvisorAutoProxyCreator.findCandidateAdvisors**

```java
protected List<Advisor> findCandidateAdvisors() {
	Assert.state(this.advisorRetrievalHelper != null, "No BeanFactoryAdvisorRetrievalHelper available");
	return this.advisorRetrievalHelper.findAdvisorBeans();
}
```

**BeanFactoryAdvisorRetrievalHelper.findAdvisorBeans**

```java
public List<Advisor> findAdvisorBeans() {
	// Determine list of advisor bean names, if not cached already.
	String[] advisorNames = this.cachedAdvisorBeanNames;
	// 缓存中没有
	if (advisorNames == null) {
		// Do not initialize FactoryBeans here: We need to leave all regular beans
		// uninitialized to let the auto-proxy creator apply to them!
		// 获取给定类型的所有 bean 名称
		advisorNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(
				this.beanFactory, Advisor.class, true, false);
		this.cachedAdvisorBeanNames = advisorNames;
	}
	if (advisorNames.length == 0) {
		return new ArrayList<>();
	}

	List<Advisor> advisors = new ArrayList<>();
	for (String name : advisorNames) {
		// 不合法的 bean 略过，默认返回 true ，可以由子类覆盖
		if (isEligibleBean(name)) {
			if (this.beanFactory.isCurrentlyInCreation(name)) {
				//跳过当前正在创建的 Advisor
				if (logger.isTraceEnabled()) {
					logger.trace("Skipping currently created advisor '" + name + "'");
				}
			}
			else {
				try {
					// 实例化并加入列表
					advisors.add(this.beanFactory.getBean(name, Advisor.class));
				}
				catch (BeanCreationException ex) {
					Throwable rootCause = ex.getMostSpecificCause();
					if (rootCause instanceof BeanCurrentlyInCreationException) {
						BeanCreationException bce = (BeanCreationException) rootCause;
						String bceBeanName = bce.getBeanName();
						if (bceBeanName != null && this.beanFactory.isCurrentlyInCreation(bceBeanName)) {
							if (logger.isTraceEnabled()) {
								logger.trace("Skipping advisor '" + name +
										"' with dependency on currently created bean: " + ex.getMessage());
							}
							// Ignore: indicates a reference back to the bean we're trying to advise.
							// We want to find advisors other than the currently created bean itself.
							continue;
						}
					}
					throw ex;
				}
			}
		}
	}
	return advisors;
}
```

#### 1.2.2 从注解中加载 Advisor

**BeanFactoryAspectJAdvisorsBuilder.buildAspectJAdvisors**

























## 2 创建代理

**AbstractAutoProxyCreator.postProcessAfterInitialization**

```java
public Object postProcessAfterInitialization(@Nullable Object bean, String beanName) {
	if (bean != null) {
		Object cacheKey = getCacheKey(bean.getClass(), beanName);
		if (this.earlyProxyReferences.remove(cacheKey) != bean) {
			// 如果 bean 可以被代理，则创建代理
			return wrapIfNecessary(bean, beanName, cacheKey);
		}
	}
	return bean;
}
```

**AbstractAutoProxyCreator.wrapIfNecessary**

```java
protected Object wrapIfNecessary(Object bean, String beanName, Object cacheKey) {
	// 如果已经处理过
	if (StringUtils.hasLength(beanName) && this.targetSourcedBeans.contains(beanName)) {
		return bean;
	}
	// 不需要代理
	if (Boolean.FALSE.equals(this.advisedBeans.get(cacheKey))) {
		return bean;
	}
	// 不需要代理或跳过
	if (isInfrastructureClass(bean.getClass()) || shouldSkip(bean.getClass(), beanName)) {
		this.advisedBeans.put(cacheKey, Boolean.FALSE);
		return bean;
	}

	// Create proxy if we have advice.
	// 给当前 bean 寻找匹配的 Advice 和 Advisor
	Object[] specificInterceptors = getAdvicesAndAdvisorsForBean(bean.getClass(), beanName, null);
	if (specificInterceptors != DO_NOT_PROXY) {
		this.advisedBeans.put(cacheKey, Boolean.TRUE);
		// 创建代理对象
		Object proxy = createProxy(
				bean.getClass(), beanName, specificInterceptors, new SingletonTargetSource(bean));
		this.proxyTypes.put(cacheKey, proxy.getClass());
		return proxy;
	}

	this.advisedBeans.put(cacheKey, Boolean.FALSE);
	return bean;
}
```

&emsp;&emsp;可以看到，在真正开始创建代理之前还需要经过一些判断条件，比如是否已经处理过或者是否是需要跳过的 bean。

### 2.1 获取 Advisor

**AbstractAdvisorAutoProxyCreator.getAdvicesAndAdvisorsForBean**

```java
protected Object[] getAdvicesAndAdvisorsForBean(
		Class<?> beanClass, String beanName, @Nullable TargetSource targetSource) {
	// 匹配可以应用于该类的 Advisor
	List<Advisor> advisors = findEligibleAdvisors(beanClass, beanName);
	if (advisors.isEmpty()) {
		return DO_NOT_PROXY;
	}
	return advisors.toArray();
}
```

**AbstractAdvisorAutoProxyCreator.findEligibleAdvisors**

```java
protected List<Advisor> findEligibleAdvisors(Class<?> beanClass, String beanName) {
	// 1. 查找所有的 Advisor 以用于自动代理
	List<Advisor> candidateAdvisors = findCandidateAdvisors();
	// 2. 匹配可以应用于该类的 Advisor
	List<Advisor> eligibleAdvisors = findAdvisorsThatCanApply(candidateAdvisors, beanClass, beanName);
	extendAdvisors(eligibleAdvisors);
	if (!eligibleAdvisors.isEmpty()) {
		eligibleAdvisors = sortAdvisors(eligibleAdvisors);
	}
	return eligibleAdvisors;
}
```

&emsp;&emsp;对于查找所有符合条件的 Advisor 包含两个步骤，首先获取所有的 Advisor，然后从中匹配可以应用于当前 bean 的 Advisor。

#### 2.1.1 查找所有的 Advisor









#### 2.1.2 匹配可以应用于该类的 Advisor

**AbstractAdvisorAutoProxyCreator.findAdvisorsThatCanApply**

```java
protected List<Advisor> findAdvisorsThatCanApply(
		List<Advisor> candidateAdvisors, Class<?> beanClass, String beanName) {

	ProxyCreationContext.setCurrentProxiedBeanName(beanName);
	try {
		// 匹配可以应用于该类的 Advisor
		return AopUtils.findAdvisorsThatCanApply(candidateAdvisors, beanClass);
	}
	finally {
		ProxyCreationContext.setCurrentProxiedBeanName(null);
	}
}
```

**AopUtils.findAdvisorsThatCanApply**

```java

```






&emsp;&emsp;Pointcut 还有一个子接口 ExpressionPointcut，用于解析 String 类型的切点表达式。AOP 自定义标签解析将 pointcut 标签解析为 AspectJExpressionPointcut 类，它使用 AspectJ 提供的库来解析 AspectJ 切入点表达式字符串，结构类图如下：

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/spring/202201041546571.png)