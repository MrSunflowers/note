# Spring源码笔记十：AOP 的使用

&emsp;&emsp;上篇文中简单介绍了 AOP 的相关概念和标签解析过程，并完成了对自动代理创建器 AspectJAwareAdvisorAutoProxyCreator 的配置和注册过程。

## 1 创建代理的准备工作

&emsp;&emsp;实现了 BeanPostProcessor 接口的 bean 都会在 ApplicationContext 容器启动时被注册，并在其他 bean 实例化过程中通过其对应的接口方法实现对 bean 的额外处理，所以这里的入口就是 postProcessBeforeInstantiation。

**AbstractAutoProxyCreator.postProcessBeforeInstantiation**

```java
public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) {
	// 生成一个名称
	Object cacheKey = getCacheKey(beanClass, beanName);
	// 没有被处理过
	if (!StringUtils.hasLength(beanName) || !this.targetSourcedBeans.contains(beanName)) {
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

&emsp;&emsp;首先在 shouldSkip 方法中加载了所有解析到的 Advisor。

## 2 静态代理和动态代理

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

```


&emsp;&emsp;Pointcut 还有一个子接口 ExpressionPointcut，用于解析 String 类型的切点表达式。AOP 自定义标签解析将 pointcut 标签解析为 AspectJExpressionPointcut 类，它使用 AspectJ 提供的库来解析 AspectJ 切入点表达式字符串，结构类图如下：

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/spring/202201041546571.png)