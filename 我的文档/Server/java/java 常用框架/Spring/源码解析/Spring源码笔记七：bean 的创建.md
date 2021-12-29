# Spring源码笔记七: bean 的创建


## 1 开始创建 bean

&emsp;&emsp;接上文，当经过处理器处理后，如果创建了代理，那么就只需直接返回创建的代理对象，否则需要进行常规的 bean 的创建，而常规的 bean 的创建工作是在 `doCreateBean` 中完成。

**AbstractAutowireCapableBeanFactory.doCreateBean**

```java
protected Object doCreateBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args)
		throws BeanCreationException {

	// Instantiate the bean.
	BeanWrapper instanceWrapper = null;
	if (mbd.isSingleton()) {
		// 如果是单例则需要首先尝试从 FactoryBean 实例的缓存 factoryBeanInstanceCache 中获取。
		instanceWrapper = this.factoryBeanInstanceCache.remove(beanName);
	}

	if (instanceWrapper == null) {
		// 1. 实例化 bean，将 BeanDefinition 转换为 BeanWrapper 。
		instanceWrapper = createBeanInstance(beanName, mbd, args);
	}
	// 获取 bean 实例
	Object bean = instanceWrapper.getWrappedInstance();
	// 获取 bean 类型
	Class<?> beanType = instanceWrapper.getWrappedClass();
	if (beanType != NullBean.class) {
		mbd.resolvedTargetType = beanType;
	}

	// Allow post-processors to modify the merged bean definition.
	synchronized (mbd.postProcessingLock) {
		if (!mbd.postProcessed) {
			try {
				// 2. bean 创建后的拓展处理，例如注解的扫描记录等
				applyMergedBeanDefinitionPostProcessors(mbd, beanType, beanName);
			}
			catch (Throwable ex) {
				throw new BeanCreationException(mbd.getResourceDescription(), beanName,
						"Post-processing of merged bean definition failed", ex);
			}
			mbd.postProcessed = true;
		}
	}

	// Eagerly cache singletons to be able to resolve circular references
	// even when triggered by lifecycle interfaces like BeanFactoryAware.
	// 3. 循环依赖 -- 记录早期的 bean 引用
	// 此时的 bean 刚创建成功，还未初始化
	boolean earlySingletonExposure = (mbd.isSingleton() && this.allowCircularReferences &&
			isSingletonCurrentlyInCreation(beanName));
	// 当前bean是单例 且 允许自动处理循环依赖 且 当前 bean 正在创建
	// 在 Spring 中，会有一个个专门的属性默认为 DefaultSingletonBeanRegistry 的 singletonsCurrentlyInCreation
	// 来记录 bean 的创建状态，在 bean 开始创建前会将 beanName 记录在属性中，在 bean 创建结束后会将 beanName 从属性中移除。
	if (earlySingletonExposure) {
		if (logger.isTraceEnabled()) {
			logger.trace("Eagerly caching bean '" + beanName +
					"' to allow for resolving potential circular references");
		}
		// 将初始化完成前的 bean 通过 ObjectFactory 记录下来
		addSingletonFactory(beanName, () -> getEarlyBeanReference(beanName, mbd, bean));
	}

	// Initialize the bean instance.
	Object exposedObject = bean;
	try {
		// 4. 填充 bean 的属性
		populateBean(beanName, mbd, instanceWrapper);
		// 5. 调用初始化方法
		exposedObject = initializeBean(beanName, exposedObject, mbd);
	}
	catch (Throwable ex) {
		if (ex instanceof BeanCreationException && beanName.equals(((BeanCreationException) ex).getBeanName())) {
			throw (BeanCreationException) ex;
		}
		else {
			throw new BeanCreationException(
					mbd.getResourceDescription(), beanName, "Initialization of bean failed", ex);
		}
	}
	// 6. 循环依赖 -- 实例检查
	if (earlySingletonExposure) {
		Object earlySingletonReference = getSingleton(beanName, false);
		if (earlySingletonReference != null) {
			if (exposedObject == bean) {
				exposedObject = earlySingletonReference;
			}
			else if (!this.allowRawInjectionDespiteWrapping && hasDependentBean(beanName)) {
				// 获取所有依赖当前 bean 的 bean 名称
				String[] dependentBeans = getDependentBeans(beanName);
				Set<String> actualDependentBeans = new LinkedHashSet<>(dependentBeans.length);
				// 遍历并删除他们
				for (String dependentBean : dependentBeans) {
					if (!removeSingletonIfCreatedForTypeCheckOnly(dependentBean)) {
						actualDependentBeans.add(dependentBean);
					}
				}
				// 存在没有删除成功的 bean
				if (!actualDependentBeans.isEmpty()) {
					// 当前 bean 已经被其他 bean 作为属性并注入，但注入的并不是最终的版本
					throw new BeanCurrentlyInCreationException(beanName,
							"Bean with name '" + beanName + "' has been injected into other beans [" +
							StringUtils.collectionToCommaDelimitedString(actualDependentBeans) +
							"] in its raw version as part of a circular reference, but has eventually been " +
							"wrapped. This means that said other beans do not use the final version of the " +
							"bean. This is often the result of over-eager type matching - consider using " +
							"'getBeanNamesForType' with the 'allowEagerInit' flag turned off, for example.");
				}
			}
		}
	}

	// Register bean as disposable.
	try {
		// 7. 注册 DisposableBean
		registerDisposableBeanIfNecessary(beanName, bean, mbd);
	}
	catch (BeanDefinitionValidationException ex) {
		throw new BeanCreationException(
				mbd.getResourceDescription(), beanName, "Invalid destruction signature", ex);
	}

	return exposedObject;
}
```

1. 实例化 bean，将 BeanDefinition 转换为 BeanWrapper。
2. 后处理器 applyMergedBeanDefinitionPostProcessors 的应用。bean 合并后的处理， A utowired 注解正是通过此方法实现诸如类型的预解析。
3. 循环依赖 -- 记录早期的 bean
4. 填充 bean 的属性。
5. 初始化 bean。
6. 循环依赖的检测与处理。
7. 注册 DisposableBean。

## 2 实例化 bean 

**AbstractAutowireCapableBeanFactory.createBeanInstance**

```java
protected BeanWrapper createBeanInstance(String beanName, RootBeanDefinition mbd, @Nullable Object[] args) {
		// Make sure bean class is actually resolved at this point.
		// 1. 获取之前解析的类
		Class<?> beanClass = resolveBeanClass(mbd, beanName);
		// 权限检查，如果 beanclass 不为空，且 beanclass 不是 public 的
		// 且没有权限访问访问非公共构造函数和方法抛出异常
		if (beanClass != null && !Modifier.isPublic(beanClass.getModifiers()) && !mbd.isNonPublicAccessAllowed()) {
			throw new BeanCreationException(mbd.getResourceDescription(), beanName,
					"Bean class isn't public, and non-public access not allowed: " + beanClass.getName());
		}
		// 2. 通过其他方式获取实例
		// 如果在 BeanDefinition 中配置了 instanceSupplier 属性则使用 instanceSupplier 来生成 bean
		Supplier<?> instanceSupplier = mbd.getInstanceSupplier();
		if (instanceSupplier != null) {
			return obtainFromSupplier(instanceSupplier, beanName);
		}

		// 如果在配置文件中配置了 factory-method ，尝试使用配置生成 bean
		if (mbd.getFactoryMethodName() != null) {
			return instantiateUsingFactoryMethod(beanName, mbd, args);
		}

		// 3.获取缓存的构造函数
		// 获取缓存的构造函数，一个 bean 可能拥有多个构造函数，每个构造函数的参数不同，调用前需要根据参数确定构造函数
		// 判断过程比较消耗性能，所以采用缓存机制，如果之前已经解析过了，则不需要再次解析，直接从缓存中获取即可
		// Shortcut when re-creating the same bean...
		boolean resolved = false;
		boolean autowireNecessary = false;
		if (args == null) {
			synchronized (mbd.constructorArgumentLock) {
				//如果缓存中已经存在解析过的构造函数
				if (mbd.resolvedConstructorOrFactoryMethod != null) {
					resolved = true;
					autowireNecessary = mbd.constructorArgumentsResolved;
				}
			}
		}
		//  如果已经解析过则不需要再次解析
		if (resolved) {
			if (autowireNecessary) {
				return autowireConstructor(beanName, mbd, null, null);
			}
			else {
				return instantiateBean(beanName, mbd);
			}
		}

		// Candidate constructors for autowiring?
		// 第一次推断构造方法
		// 在对象实例化之前的回调处理,可以用来选择使用哪些构造函数
		Constructor<?>[] ctors = determineConstructorsFromBeanPostProcessors(beanClass, beanName);
		if (ctors != null ||
				// 获取自动装配方式
				mbd.getResolvedAutowireMode() == AUTOWIRE_CONSTRUCTOR
				// 判断是否配置了构造器参数 constructor-arg
				|| mbd.hasConstructorArgumentValues()
				|| !ObjectUtils.isEmpty(args)) {
			return autowireConstructor(beanName, mbd, ctors, args);
		}

		// Preferred constructors for default construction?
		// 第二次推断构造方法
		// 获取 class 的所有公共构造函数
		ctors = mbd.getPreferredConstructors();
		if (ctors != null) {
			// 对于实例的创建，Spring 分成了两种情况，一种是通用的实例化，另一种是带有参数的实例化，
			// 带有参数的实例化过程相当复杂，因为不确定，所以在判断对应的参数上做了大量工作
			return autowireConstructor(beanName, mbd, ctors, null);
		}

		// No special handling: simply use no-arg constructor.
		// 默认的无参构造器实例化
		return instantiateBean(beanName, mbd);
	}
```

&emsp;&emsp;实例化 bean 将 BeanDefinition 转换为 BeanWrapper 的过程非常复杂，其中包含了函数的推断过程，但是上面 createBeanInstance 函数的逻辑还是比较清晰的：

1. 在之前的准备工作中，已经将 beanClass 解析为 Class 引用并存储在 BeanDefinition 中，这里只需将其取出即可。
2. 尝试通过用户自定义的回调方式获取实例。
3. 如果配置了 factory-method ，使用配置的 factory-method 获取实例。
4. 使用常规方式，通过解析构造函数并调用它产生实例，一个类可能会有多个构造函数，每个构造函数都有不同的参数，所以需要根据参数推断构造函数并进行实例化。
5. 如果上面都不存在，则使用默认的构造函数进行 bean 的实例化。

### 2.1 通过回调函数创建 bean 实例

&emsp;&emsp;一般的，实例 bean 的创建是使用反射技术来实现的，而 JIT 对反射的优化程度是不同的，也就是说有时反射的性能是比较差的，从 java 8 开始支持方法引用，方法引用使得开发者可以直接引用现存的方法、Java类的构造方法或者实例对象，使用方法引用可以提供一个回调方法，直接调用回调方法即可获取对象，不需要通过反射。当在 BeanDefinition 中配置了 instanceSupplier 属性则使用 instanceSupplier 来生成 bean ，instanceSupplier 用于替代工厂方法（包含静态工厂）或者构造器创建对象，但是其后面的生命周期回调不受影响。

【示例】

user 类

```java
public class User {
	private String name;

	public User(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void init() {
		System.out.println("user 初始化.....");
	}
}
```

配置文件

```xml
<bean name="user" class="myTest.User" init-method="init">
   <constructor-arg name="name" value="userName"></constructor-arg>
</bean>
```

Main 类

```java
public static void main(String[] args) {
   Resource resource = new ClassPathResource("myTestResources/applicationContext.xml");
   XmlBeanFactory beanFactory = new XmlBeanFactory(resource);
   GenericBeanDefinition genericBeanDefinition = (GenericBeanDefinition) beanFactory.getBeanDefinition("user");
    //设置引用方法
   genericBeanDefinition.setInstanceSupplier(Main::getUser);
   User user = (User)beanFactory.getBean("user");
   System.out.println(user.getName());
    //结果:
    //user 初始化.....
	//张三
}
public static User getUser(){
	return new User("张三");
}
```

&emsp;&emsp;可以看到 instanceSupplier 配置的回调确实替代了正常的实例化过程，而其生命周期回调不受影响。方法的实现也非常简单，就是框架在创建对象的时候会校验这个 instanceSupplier 是否有值，有的话，调用这个字段获取对象。

```java
protected BeanWrapper obtainFromSupplier(Supplier<?> instanceSupplier, String beanName) {
		Object instance;

		String outerBean = this.currentlyCreatedBean.get();
		this.currentlyCreatedBean.set(beanName);
		try {
			//直接调用用户定义的回调函数
			instance = instanceSupplier.get();
		}
		finally {
			if (outerBean != null) {
				this.currentlyCreatedBean.set(outerBean);
			}
			else {
				this.currentlyCreatedBean.remove();
			}
		}

		if (instance == null) {
			instance = new NullBean();
		}
		BeanWrapper bw = new BeanWrapperImpl(instance);
		initBeanWrapper(bw);
		return bw;
	}
```

&emsp;&emsp;BeanWrapper 是对 bean 实例的一个包装类，其中缓存了 bean 的一些详细信息，包括 bean 中的属性和属性对应的 getter、setter 方法等，BeanWrapperImpl 是 BeanWrapper 的默认实现，可以访问 bean 的属性、设置 bean 的属性值等。BeanWrapperImpl 类还提供了许多默认属性编辑器，支持多种不同类型之间的类型转换，还可以将数组、集合类型的属性转换成指定特殊类型的数组或集合。

### 2.2 使用 factory-method 实例化 bean

&emsp;&emsp;如果在配置文件中配置了 factory-method ，尝试使用配置的 factory-method 生成 bean。

**AbstractAutowireCapableBeanFactory.instantiateUsingFactoryMethod**

```java
protected BeanWrapper instantiateUsingFactoryMethod(
			String beanName, RootBeanDefinition mbd, @Nullable Object[] explicitArgs) {
		//创建解析构造函数的委托并调用其instantiateUsingFactoryMethod
		return new ConstructorResolver(this).instantiateUsingFactoryMethod(beanName, mbd, explicitArgs);
	}
```

**ConstructorResolver.instantiateUsingFactoryMethod**

```java
public BeanWrapper instantiateUsingFactoryMethod(
			String beanName, RootBeanDefinition mbd, @Nullable Object[] explicitArgs) {

		BeanWrapperImpl bw = new BeanWrapperImpl();
		this.beanFactory.initBeanWrapper(bw);

		Object factoryBean;
		Class<?> factoryClass;
		boolean isStatic;

		String factoryBeanName = mbd.getFactoryBeanName();
		if (factoryBeanName != null) {
			//非静态工厂的处理
			if (factoryBeanName.equals(beanName)) {
				// factoryBeanName 是自己本身抛出异常
				throw new BeanDefinitionStoreException(mbd.getResourceDescription(), beanName,
						"factory-bean reference points back to the same bean definition");
			}
			//先获取 factoryBean 实例
			factoryBean = this.beanFactory.getBean(factoryBeanName);
			if (mbd.isSingleton() && this.beanFactory.containsSingleton(beanName)) {
				throw new ImplicitlyAppearedSingletonException();
			}
			//为给定的 bean 注册一个依赖 bean，在 factoryBean 销毁之前销毁当前 bean
			this.beanFactory.registerDependentBean(factoryBeanName, beanName);
			factoryClass = factoryBean.getClass();
			isStatic = false;
		}
		else {
			// It's a static factory method on the bean class.
			// 静态工厂的处理
			if (!mbd.hasBeanClass()) {
				throw new BeanDefinitionStoreException(mbd.getResourceDescription(), beanName,
						"bean definition declares neither a bean class nor a factory-bean reference");
			}
			factoryBean = null;
			factoryClass = mbd.getBeanClass();
			isStatic = true;
		}
		// 当前选用的 factoryMethod
		Method factoryMethodToUse = null;
		ArgumentsHolder argsHolderToUse = null;
		Object[] argsToUse = null;

		if (explicitArgs != null) {
			argsToUse = explicitArgs;
		}
		else {
			Object[] argsToResolve = null;
			synchronized (mbd.constructorArgumentLock) {
				//如果缓存中已经存在解析过的 FactoryMethod
				factoryMethodToUse = (Method) mbd.resolvedConstructorOrFactoryMethod;
				//存在解析过的 FactoryMethod 且 参数已经解析过了
				if (factoryMethodToUse != null && mbd.constructorArgumentsResolved) {
					// Found a cached factory method...
					// 从缓存中获取解析完成的参数列表
					argsToUse = mbd.resolvedConstructorArguments;
					if (argsToUse == null) {
						//获取需要进一步解析的参数列表
						argsToResolve = mbd.preparedConstructorArguments;
					}
				}
			}
			if (argsToResolve != null) {
				// 解析参数类型，将参数配置解析成为最终值
				// 如构造函数：A(int,int) ==> 就会把配置中的("1","1") 转化为 (1,1)
				// 缓存中的值可能是原始值也可能是最终值
				argsToUse = resolvePreparedArguments(beanName, mbd, bw, factoryMethodToUse, argsToResolve);
			}
		}
		// 没有缓存，第一次使用
		if (factoryMethodToUse == null || argsToUse == null) {
			// Need to determine the factory method...
			// Try all methods with this name to see if they match the given arguments.
			// 现在确定工厂方法 FactoryMethod 尝试查看使用此名称的所有方法，看看它们是否与给定的参数匹配。
			factoryClass = ClassUtils.getUserClass(factoryClass);

			List<Method> candidates = null;
			// FactoryMethod 是否有重载
			if (mbd.isFactoryMethodUnique) {
				if (factoryMethodToUse == null) {
					factoryMethodToUse = mbd.getResolvedFactoryMethod();
				}
				if (factoryMethodToUse != null) {
					candidates = Collections.singletonList(factoryMethodToUse);
				}
			}
			if (candidates == null) {
				candidates = new ArrayList<>();
				// 根据是否允许访问非公共构造函数和方法返回结果
				Method[] rawCandidates = getCandidateMethods(factoryClass, mbd);
				// 查找并记录所有与工厂方法同名的方法
				for (Method candidate : rawCandidates) {
					if (Modifier.isStatic(candidate.getModifiers()) == isStatic && mbd.isFactoryMethod(candidate)) {
						candidates.add(candidate);
					}
				}
			}

			// 当与工厂方法同名的候选方法只有一个，且调用 getBean 方法时没有传参，
			// 且没有缓存过参数，直接通过调用实例化方法执行该候选方法
			if (candidates.size() == 1 && explicitArgs == null && !mbd.hasConstructorArgumentValues()) {
				Method uniqueCandidate = candidates.get(0);
				if (uniqueCandidate.getParameterCount() == 0) {
					//没有参数，设置缓存
					mbd.factoryMethodToIntrospect = uniqueCandidate;
					synchronized (mbd.constructorArgumentLock) {
						mbd.resolvedConstructorOrFactoryMethod = uniqueCandidate;
						mbd.constructorArgumentsResolved = true;
						mbd.resolvedConstructorArguments = EMPTY_ARGS;
					}
					bw.setBeanInstance(instantiate(beanName, mbd, factoryBean, uniqueCandidate, EMPTY_ARGS));
					return bw;
				}
			}

			// 有多个候选方法，排序给定的构造函数，
			// public 函数优先参数数量降序、非 public 构造函数参数数量降序
			if (candidates.size() > 1) {  // explicitly skip immutable singletonList
				candidates.sort(AutowireUtils.EXECUTABLE_COMPARATOR);
			}

			//存储解析 constructor-arg 后的参数
			ConstructorArgumentValues resolvedValues = null;
			boolean autowiring = (mbd.getResolvedAutowireMode() == AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR);
			int minTypeDiffWeight = Integer.MAX_VALUE;
			Set<Method> ambiguousFactoryMethods = null;
			//存储参数个数
			int minNrOfArgs;
			if (explicitArgs != null) {
				minNrOfArgs = explicitArgs.length;
			}
			else {
				// We don't have arguments passed in programmatically, so we need to resolve the
				// arguments specified in the constructor arguments held in the bean definition.
				// 没有传入的参数，因此需要解析 beandefinition 中保存的配置文件的参数 constructor-arg
				if (mbd.hasConstructorArgumentValues()) {
					ConstructorArgumentValues cargs = mbd.getConstructorArgumentValues();
					resolvedValues = new ConstructorArgumentValues();
					//解析并返回参数个数
					minNrOfArgs = resolveConstructorArguments(beanName, mbd, bw, cargs, resolvedValues);
				}
				else {
					minNrOfArgs = 0;
				}
			}

			Deque<UnsatisfiedDependencyException> causes = null;

			// 遍历所有同名的候选函数
			for (Method candidate : candidates) {
				int parameterCount = candidate.getParameterCount();
				// 找到的方候选法参数至少应该大于等于传入/解析到的参数个数
				if (parameterCount >= minNrOfArgs) {
					ArgumentsHolder argsHolder;

					//获取候选方法的参数列表类型
					Class<?>[] paramTypes = candidate.getParameterTypes();
					if (explicitArgs != null) {
						// Explicit arguments given -> arguments length must match exactly.
						// 在调用getBean方法时传的参数不为空，则工厂方法的参数个数需要与
						// 传入的参数个数严格一致
						if (paramTypes.length != explicitArgs.length) {
							continue;
						}
						argsHolder = new ArgumentsHolder(explicitArgs);
					}
					else {
						// Resolved constructor arguments: type conversion and/or autowiring necessary.
						// 当传入的参数为空，需要根据候选方法的参数解析
						try {
							String[] paramNames = null;
							ParameterNameDiscoverer pnd = this.beanFactory.getParameterNameDiscoverer();
							if (pnd != null) {
								//获取候选方法的参数名称列表
								paramNames = pnd.getParameterNames(candidate);
							}
							//尝试根据候选函数参数类型匹配并转换解析到的参数
							argsHolder = createArgumentArray(beanName, mbd, resolvedValues, bw,
									paramTypes, paramNames, candidate, autowiring, candidates.size() == 1);
						}
						catch (UnsatisfiedDependencyException ex) {
							if (logger.isTraceEnabled()) {
								logger.trace("Ignoring factory method [" + candidate + "] of bean '" + beanName + "': " + ex);
							}
							// Swallow and try next overloaded factory method.
							if (causes == null) {
								causes = new ArrayDeque<>(1);
							}
							causes.add(ex);
							continue;
						}
					}

					// 计算工厂方法的权重，分严格模式和宽松模式，数值越小权重越高
					int typeDiffWeight = (mbd.isLenientConstructorResolution() ?
							argsHolder.getTypeDifferenceWeight(paramTypes) : argsHolder.getAssignabilityWeight(paramTypes));
					// Choose this factory method if it represents the closest match.
					// 当计算的值小于最大值时，记录该方法和参数，等待与遍历的下一个候选工厂方法比对比
					if (typeDiffWeight < minTypeDiffWeight) {
						factoryMethodToUse = candidate;
						argsHolderToUse = argsHolder;
						argsToUse = argsHolder.arguments;
						minTypeDiffWeight = typeDiffWeight;
						ambiguousFactoryMethods = null;
					}
					// Find out about ambiguity: In case of the same type difference weight
					// for methods with the same number of parameters, collect such candidates
					// and eventually raise an ambiguity exception.
					// However, only perform that check in non-lenient constructor resolution mode,
					// and explicitly ignore overridden methods (with the same parameter signature).
					// 当遍历方法时，前面已经设置了 factoryMethodToUse 且当前方法与记录的方法权重相同
					// 则记录当前方法，继续遍历如果找到更合适的方法则清空记录
					else if (factoryMethodToUse != null && typeDiffWeight == minTypeDiffWeight &&
							!mbd.isLenientConstructorResolution() &&
							paramTypes.length == factoryMethodToUse.getParameterCount() &&
							!Arrays.equals(paramTypes, factoryMethodToUse.getParameterTypes())) {
						if (ambiguousFactoryMethods == null) {
							ambiguousFactoryMethods = new LinkedHashSet<>();
							ambiguousFactoryMethods.add(factoryMethodToUse);
						}
						ambiguousFactoryMethods.add(candidate);
					}
				}
			}

			if (factoryMethodToUse == null || argsToUse == null) {
				if (causes != null) {
					UnsatisfiedDependencyException ex = causes.removeLast();
					for (Exception cause : causes) {
						this.beanFactory.onSuppressedException(cause);
					}
					throw ex;
				}
				List<String> argTypes = new ArrayList<>(minNrOfArgs);
				if (explicitArgs != null) {
					for (Object arg : explicitArgs) {
						argTypes.add(arg != null ? arg.getClass().getSimpleName() : "null");
					}
				}
				else if (resolvedValues != null) {
					Set<ValueHolder> valueHolders = new LinkedHashSet<>(resolvedValues.getArgumentCount());
					valueHolders.addAll(resolvedValues.getIndexedArgumentValues().values());
					valueHolders.addAll(resolvedValues.getGenericArgumentValues());
					for (ValueHolder value : valueHolders) {
						String argType = (value.getType() != null ? ClassUtils.getShortName(value.getType()) :
								(value.getValue() != null ? value.getValue().getClass().getSimpleName() : "null"));
						argTypes.add(argType);
					}
				}
				String argDesc = StringUtils.collectionToCommaDelimitedString(argTypes);
				throw new BeanCreationException(mbd.getResourceDescription(), beanName,
						"No matching factory method found on class [" + factoryClass.getName() + "]: " +
						(mbd.getFactoryBeanName() != null ?
							"factory bean '" + mbd.getFactoryBeanName() + "'; " : "") +
						"factory method '" + mbd.getFactoryMethodName() + "(" + argDesc + ")'. " +
						"Check that a method with the specified name " +
						(minNrOfArgs > 0 ? "and arguments " : "") +
						"exists and that it is " +
						(isStatic ? "static" : "non-static") + ".");
			}
			else if (void.class == factoryMethodToUse.getReturnType()) {
				throw new BeanCreationException(mbd.getResourceDescription(), beanName,
						"Invalid factory method '" + mbd.getFactoryMethodName() + "' on class [" +
						factoryClass.getName() + "]: needs to have a non-void return type!");
			}
			else if (ambiguousFactoryMethods != null) {
				// 存在多个相同权重的方法，不知道选哪个
				throw new BeanCreationException(mbd.getResourceDescription(), beanName,
						"Ambiguous factory method matches found on class [" + factoryClass.getName() + "] " +
						"(hint: specify index/type/name arguments for simple parameters to avoid type ambiguities): " +
						ambiguousFactoryMethods);
			}

			if (explicitArgs == null && argsHolderToUse != null) {
				//将解析的函数加入缓存
				mbd.factoryMethodToIntrospect = factoryMethodToUse;
				argsHolderToUse.storeCache(mbd, factoryMethodToUse);
			}
		}
		//开始实例化
		bw.setBeanInstance(instantiate(beanName, mbd, factoryBean, factoryMethodToUse, argsToUse));
		return bw;
	}
```

&emsp;&emsp;方法非常长，下面一步步看。

1. Class的获取：前面讲过简单工厂模式分为两种，静态工厂和非静态工厂，同样这里对应了两种类获取方式，对于非静态工厂需要先获取工厂本身的实例，从获取的实例获取 Class，而静态工厂直接从 BeanDefinition 中获取 Class。
2. FactoryMethod 参数的确定：如果在调用方法时传入了参数，则使用传入的参数，没有传入参数则尝试从缓存获取，在缓存中的参数可能是原始类型，也可能是最终类型，所以从缓存中得到的参数也要经过类型转换器来确保参数类型与对应的函数参数类型完全对应。
3. 解析 FactoryMethod：因为一个 bean 对应的类中可能会有多个同名函数，而每个函数的参数不同，Spring 会根据参数去判断应该使用哪个方法，而判断过程是比较消耗性能的，所以采用缓存机制，如果之前已经解析过则不需要重复解析，而是从缓存中直接获取即可，否则需要从头解析，并将解析的结果放入缓存中。

&emsp;&emsp;下面是解析 FactoryMethod 的过程：

1. 查找并记录所有与工厂方法同名的方法，当与工厂方法同名的候选方法只有一个，且调用 getBean 方法时没有传参，且没有配置过参数，那么可以直接通过执行该候选方法完成实例化。
2. 当有多个候选方法时，先排序给定的构造函数，按照 public 函数优先参数数量降序、非 public 构造函数参数数量降序排列。
3. 解析存储在 BeanDefinition 中的 xml 配置参数 constructor-arg。
4. 尝试根据**候选函数的参数类型匹配并转换**上一步解析到的参数。
5. 计算每个候选函数的权重并最终**唯一**确定选用的候选方法。
6. 使用解析到的方法完成实例化。

&emsp;&emsp;可以看出，对于实例的创建，Spring 分为了两种情况，一种是通用实例化，另一种是带有参数的实例化，带有参数的函数处理过程非常复杂，因为参数具有的不确定性，所以在参数的判断上做了大量工作。

#### 2.2.1  计算方法的权重

##### 宽松模式

**ConstructorResolver.ArgumentsHolder.getTypeDifferenceWeight**

```java
/**
 * 宽松模式<br>
 * 计算候选方法权重，分别使用转换后的参数和原始参数进行计算，数值越小权重越高，如果
 * 发现转换后的参数和原始参数权重相等，将原始参数权重减去1024以提高其权重占比，来实现
 * 优先使用原始参数
 */
public int getTypeDifferenceWeight(Class<?>[] paramTypes) {
	// If valid arguments found, determine type difference weight.
	// Try type difference weight on both the converted arguments and
	// the raw arguments. If the raw weight is better, use it.
	// Decrease raw weight by 1024 to prefer it over equal converted weight.
	int typeDiffWeight = MethodInvoker.getTypeDifferenceWeight(paramTypes, this.arguments);
	int rawTypeDiffWeight = MethodInvoker.getTypeDifferenceWeight(paramTypes, this.rawArguments) - 1024;
	return Math.min(rawTypeDiffWeight, typeDiffWeight);
}
```

**MethodInvoker.getTypeDifferenceWeight**

```java
public static int getTypeDifferenceWeight(Class<?>[] paramTypes, Object[] args) {
		int result = 0;
		for (int i = 0; i < paramTypes.length; i++) {
			//参数类型是否可以转化为候选方法的参数类型
			if (!ClassUtils.isAssignableValue(paramTypes[i], args[i])) {
				return Integer.MAX_VALUE;
			}
			if (args[i] != null) {
				Class<?> paramType = paramTypes[i];
				Class<?> superClass = args[i].getClass().getSuperclass();
				while (superClass != null) {
					//参数类型是否为方法参数的子类
					if (paramType.equals(superClass)) {
						result = result + 2;
						superClass = null;
					}
					else if (ClassUtils.isAssignable(paramType, superClass)) {
						result = result + 2;
						superClass = superClass.getSuperclass();
					}
					else {
						superClass = null;
					}
				}
				if (paramType.isInterface()) {
					result = result + 1;
				}
			}
		}
		return result;
	}
```

##### 严格模式

**ConstructorResolver.ArgumentsHolder.getAssignabilityWeight**

```java
/**
 * 严格模式<br>
 * 直接检测参数是否可以转化为候选函数的参数类型，当转换后的参数和原始参数都不满足转换条件
 * 优先选用原始参数，都满足条件则返回权重最大值减去1024
 */
public int getAssignabilityWeight(Class<?>[] paramTypes) {
	for (int i = 0; i < paramTypes.length; i++) {
		if (!ClassUtils.isAssignableValue(paramTypes[i], this.arguments[i])) {
			return Integer.MAX_VALUE;
		}
	}
	for (int i = 0; i < paramTypes.length; i++) {
		if (!ClassUtils.isAssignableValue(paramTypes[i], this.rawArguments[i])) {
			return Integer.MAX_VALUE - 512;
		}
	}
	return Integer.MAX_VALUE - 1024;
}
```

**ClassUtils.isAssignableValue ：参数匹配**

```java
public static boolean isAssignableValue(Class<?> type, @Nullable Object value) {
		Assert.notNull(type, "Type must not be null");
		return (value != null ? isAssignable(type, value.getClass()) : !type.isPrimitive());
	}

public static boolean isAssignable(Class<?> lhsType, Class<?> rhsType) {
		Assert.notNull(lhsType, "Left-hand side type must not be null");
		Assert.notNull(rhsType, "Right-hand side type must not be null");
		//确定此Class对象表示的类或接口是否与指定的Class参数表示的类或接口相同，或者是其超类或超接口。
		//如果是，则返回true否则返回false。
		if (lhsType.isAssignableFrom(rhsType)) {
			return true;
		}
		if (lhsType.isPrimitive()) {
			//原始类型
			Class<?> resolvedPrimitive = primitiveWrapperTypeMap.get(rhsType);
			return (lhsType == resolvedPrimitive);
		}
		else {
			//包装类型
			Class<?> resolvedWrapper = primitiveTypeToWrapperMap.get(rhsType);
			return (resolvedWrapper != null && lhsType.isAssignableFrom(resolvedWrapper));
		}
	}
```

### 2.3 使用构造方法实例化 bean

&emsp;&emsp;使用构造方法实例化 bean 的过程与工厂方法相似，比如，传入参数的处理，候选方法的排序，参数的注入，权重计算等。不同的是 instantiateUsingFactoryMethod 有 factoryBean 的查找，重要的逻辑基本差不多，重点说下在调用 autowireConstructor 方法前就要先获取到构造器，并作为参数传入，重点来关注 spring 默认的获取构造方法的逻辑。

**AbstractAutowireCapableBeanFactory.determineConstructorsFromBeanPostProcessors ：使用增强器获取构造方法**

```java
protected Constructor<?>[] determineConstructorsFromBeanPostProcessors(@Nullable Class<?> beanClass, String beanName)
			throws BeansException {
		// 判断该 beanClass 是否存在实例化前的回调
		if (beanClass != null && hasInstantiationAwareBeanPostProcessors()) {
			// 遍历所有注册的 SmartInstantiationAwareBeanPostProcessor 实例，
			// 执行其 determineCandidateConstructors 方法
			// （如有需要可以自定义 SmartInstantiationAwareBeanPostProcessor，按需求重写 determineCandidateConstructors 方法）
			for (SmartInstantiationAwareBeanPostProcessor bp : getBeanPostProcessorCache().smartInstantiationAware) {
				Constructor<?>[] ctors = bp.determineCandidateConstructors(beanClass, beanName);
				if (ctors != null) {
					return ctors;
				}
			}
		}
		return null;
	}
```

&emsp;&emsp;这里会调用所有注册的 SmartInstantiationAwareBeanPostProcessor 实例，执行其 determineCandidateConstructors 方法，没有注册增强器则直接返回 null，这里重点看下 AutowiredAnnotationBeanPostProcessor.determineCandidateConstructors。

**AutowiredAnnotationBeanPostProcessor.determineCandidateConstructors : 构造方法智能选择器**

```java
public Constructor<?>[] determineCandidateConstructors(Class<?> beanClass, final String beanName)
			throws BeanCreationException {

		// Let's check for lookup methods here...
		// 检测 Lookup 注解
		if (!this.lookupMethodsChecked.contains(beanName)) {
			//确定类中是否有Lookup注解标注的方法
			if (AnnotationUtils.isCandidateClass(beanClass, Lookup.class)) {
				try {
					Class<?> targetClass = beanClass;
					do {
						ReflectionUtils.doWithLocalMethods(targetClass, method -> {
							//遍历所有方法并获取Lookup注解
							Lookup lookup = method.getAnnotation(Lookup.class);
							if (lookup != null) {
								Assert.state(this.beanFactory != null, "No BeanFactory available");
								LookupOverride override = new LookupOverride(method, lookup.value());
								try {
									//这里和之前的xml解析一样
									//记录在了 AbstractBeanDefinition 中的 methodOverrides 属性中
									RootBeanDefinition mbd = (RootBeanDefinition)
											this.beanFactory.getMergedBeanDefinition(beanName);
									mbd.getMethodOverrides().addOverride(override);
								}
								catch (NoSuchBeanDefinitionException ex) {
									throw new BeanCreationException(beanName,
											"Cannot apply @Lookup to beans without corresponding bean definition");
								}
							}
						});
						//向父类中寻找
						targetClass = targetClass.getSuperclass();
					}
					while (targetClass != null && targetClass != Object.class);

				}
				catch (IllegalStateException ex) {
					throw new BeanCreationException(beanName, "Lookup method resolution failed", ex);
				}
			}
			this.lookupMethodsChecked.add(beanName);
		}

		// Quick check on the concurrent map first, with minimal locking.
		// 开始检查构造函数，
		// 如果之前已经有用构造方法实例化 bean，就会有缓存
		Constructor<?>[] candidateConstructors = this.candidateConstructorsCache.get(beanClass);
		if (candidateConstructors == null) {
			// Fully synchronized resolution now...
			synchronized (this.candidateConstructorsCache) {
				candidateConstructors = this.candidateConstructorsCache.get(beanClass);
				if (candidateConstructors == null) {
					Constructor<?>[] rawCandidates;
					try {
						// 缓存中不存在的时候使用 getDeclaredConstructors 方法通过反射获取所有的构造方法
						// 包括公共、受保护、默认（包）访问和私有构造函数。
						// 返回的数组中的元素没有排序，也没有任何特定的顺序。
						rawCandidates = beanClass.getDeclaredConstructors();
					}
					catch (Throwable ex) {
						throw new BeanCreationException(beanName,
								"Resolution of declared constructors on bean Class [" + beanClass.getName() +
								"] from ClassLoader [" + beanClass.getClassLoader() + "] failed", ex);
					}
					//候选构造方法
					List<Constructor<?>> candidates = new ArrayList<>(rawCandidates.length);
					Constructor<?> requiredConstructor = null;
					Constructor<?> defaultConstructor = null;
					// Kotlin 处理 对于 java 一般默认的返回 null
					Constructor<?> primaryConstructor = BeanUtils.findPrimaryConstructor(beanClass);
					int nonSyntheticConstructors = 0;
					//遍历这些构造函数
					for (Constructor<?> candidate : rawCandidates) {
						if (!candidate.isSynthetic()) {
							nonSyntheticConstructors++;
						}
						else if (primaryConstructor != null) {
							continue;
						}
						//查看当前构造方法是否标有 @Autowired 注解
						//当有多个构造方法, 可以通过标注 @Autowired 的方式来指定使用哪个构造方法
						MergedAnnotation<?> ann = findAutowiredAnnotation(candidate);
						if (ann == null) {
							//如果没有@Autowired注解，查找父类的构造方法有没有@Autowired注解
							Class<?> userClass = ClassUtils.getUserClass(beanClass);
							if (userClass != beanClass) {
								try {
									Constructor<?> superCtor =
											userClass.getDeclaredConstructor(candidate.getParameterTypes());
									ann = findAutowiredAnnotation(superCtor);
								}
								catch (NoSuchMethodException ex) {
									// Simply proceed, no equivalent superclass constructor found...
								}
							}
						}
						//有 @Autowired 的情况
						if (ann != null) {
							//在多个方法上标注了 @Autowired , 则spring会抛出异常, 不知道使用哪个
							if (requiredConstructor != null) {
								throw new BeanCreationException(beanName,
										"Invalid autowire-marked constructor: " + candidate +
										". Found constructor with 'required' Autowired annotation already: " +
										requiredConstructor);
							}
							//获取autowire注解中required属性值
							//若required=true又有多个标注autowire的构造方法，则报错
							boolean required = determineRequiredStatus(ann);
							if (required) {
								if (!candidates.isEmpty()) {
									throw new BeanCreationException(beanName,
											"Invalid autowire-marked constructors: " + candidates +
											". Found constructor with 'required' Autowired annotation: " +
											candidate);
								}
								//当前候选构造方法
								requiredConstructor = candidate;
							}
							//加入候选构造方法列表
							candidates.add(candidate);
						}
						//当构造方法没有@Autowired注解且参数个数为0，选为defaultConstructor
						else if (candidate.getParameterCount() == 0) {
							//构造函数没有参数, 则设置为默认的构造函数
							defaultConstructor = candidate;
						}
					}
					//到这里, 已经循环完了所有的构造方法
					if (!candidates.isEmpty()) {
						//成功找到了含有@Autowired注解的构造方法
						// Add default constructor to list of optional constructors, as fallback.
						if (requiredConstructor == null) {
							//没有找到@Autowired required=true的构造方法
							if (defaultConstructor != null) {
								//将默认构造函数添加到可选构造函数列表中，作为后备。
								//往候选方法中加入defaultConstructor
								candidates.add(defaultConstructor);
							}
							else if (candidates.size() == 1 && logger.isInfoEnabled()) {
								logger.info("Inconsistent constructor declaration on bean with name '" + beanName +
										"': single autowire-marked constructor flagged as optional - " +
										"this constructor is effectively required since there is no " +
										"default constructor to fall back to: " + candidates.get(0));
							}
						}
						//候选方法放入缓存
						candidateConstructors = candidates.toArray(new Constructor<?>[0]);
					}
					//当获取的所有构造方法只有一个，且不是@autowired注解的（注解的在上面处理了）
					//且该构造方法有多个参数
					else if (rawCandidates.length == 1 && rawCandidates[0].getParameterCount() > 0) {
						candidateConstructors = new Constructor<?>[] {rawCandidates[0]};
					}
					// Kotlin 处理
					else if (nonSyntheticConstructors == 2 && primaryConstructor != null &&
							defaultConstructor != null && !primaryConstructor.equals(defaultConstructor)) {
						candidateConstructors = new Constructor<?>[] {primaryConstructor, defaultConstructor};
					}
					// Kotlin 处理
					else if (nonSyntheticConstructors == 1 && primaryConstructor != null) {
						candidateConstructors = new Constructor<?>[] {primaryConstructor};
					}
					else {
						//没找到合适的构造函数
						candidateConstructors = new Constructor<?>[0];
					}
					//缓存选定的候选构造方法
					this.candidateConstructorsCache.put(beanClass, candidateConstructors);
				}
			}
		}
		//这里如果没找到, 则会返回 null, 而不会返回空数组
		return (candidateConstructors.length > 0 ? candidateConstructors : null);
	}
```

&emsp;&emsp;不难看出这里主要是处理由 @Autowired 注解标注的构造方法的筛选和处理的，对于普通的构造方法并没有过多的处理，另外还记录了由 @Lookup 注解标注的方法，逻辑和前面解析 xml 配置方式一样，将方法记录在 AbstractBeanDefinition 中的 methodOverrides 属性中。下面是构造方法的主要处理逻辑。

1. 遍历构造方法；
2. 只有一个无参构造方法, 则返回null；
3. 只有一个有参构造方法, 则返回这个构造方法；
4. 有多个构造方法且没有@Autowired, 此时spring不知道使用哪一个，干脆返回 null；
5. 有多个构造方法, 且在其中一个方法上标注了 @Autowired, 则会返回这个标注的构造方法；
6. 有多个构造方法, 且在多个方法上标注了@Autowired, 则spring会抛出异常。


### 2.4 不带参数的构造函数实例化

**AbstractAutowireCapableBeanFactory.instantiateBean : 使用其默认构造函数实例化给定的 bean**

```java
protected BeanWrapper instantiateBean(String beanName, RootBeanDefinition mbd) {
		try {
			Object beanInstance;
			if (System.getSecurityManager() != null) {
				beanInstance = AccessController.doPrivileged(
						(PrivilegedAction<Object>) () -> getInstantiationStrategy().instantiate(mbd, beanName, this),
						getAccessControlContext());
			}
			else {
				// 获取实例化策略并实例化类
				beanInstance = getInstantiationStrategy().instantiate(mbd, beanName, this);
			}
			BeanWrapper bw = new BeanWrapperImpl(beanInstance);
			initBeanWrapper(bw);
			return bw;
		}
		catch (Throwable ex) {
			throw new BeanCreationException(
					mbd.getResourceDescription(), beanName, "Instantiation of bean failed", ex);
		}
	}
```

&emsp;&emsp;可以发现并没有什么复杂的逻辑，Spring 把精力都放在了构造函数及参数的匹配上，这里没有参数，直接调用实例化策略实例化即可。

## 3 实例化策略

&emsp;&emsp;所谓的实例化策略，其实就是实例化方式，在 Spring 中，用户如果没有使用 replace 或 lookup 的配置方法，那么就直接使用反射方式实例化，但是如果使用了这两个特性，就必须要使用动态代理的方式将之对应的拦截增强器设置进去，返回一个包含拦截器的代理实例。Spring 的实例化策略体现在 InstantiationStrategy 接口中，其默认有 CglibSubclassingInstantiationStrategy 和 SimpleInstantiationStrategy 两个实现类，其中 CglibSubclassingInstantiationStrategy 为 SimpleInstantiationStrategy 的子类。

**SimpleInstantiationStrategy.instantiate : 简单的实例化策略**

```java
public Object instantiate(RootBeanDefinition bd, @Nullable String beanName, BeanFactory owner) {
		// Don't override the class with CGLIB if no overrides.
		// 如果有需要覆盖或者动态替换的方法，需要使用cglib进行动态代理，
		// 在创建代理的同时将动态方法织入类中
		// 如果没有需要覆盖或者动态替换的方法直接使用反射实例化
		if (!bd.hasMethodOverrides()) {
			Constructor<?> constructorToUse;
			synchronized (bd.constructorArgumentLock) {
				constructorToUse = (Constructor<?>) bd.resolvedConstructorOrFactoryMethod;
				if (constructorToUse == null) {
					final Class<?> clazz = bd.getBeanClass();
					if (clazz.isInterface()) {
						throw new BeanInstantiationException(clazz, "Specified class is an interface");
					}
					try {
						if (System.getSecurityManager() != null) {
							constructorToUse = AccessController.doPrivileged(
									(PrivilegedExceptionAction<Constructor<?>>) clazz::getDeclaredConstructor);
						}
						else {
							constructorToUse = clazz.getDeclaredConstructor();
						}
						bd.resolvedConstructorOrFactoryMethod = constructorToUse;
					}
					catch (Throwable ex) {
						throw new BeanInstantiationException(clazz, "No default constructor found", ex);
					}
				}
			}
			// 使用反射实例化
			return BeanUtils.instantiateClass(constructorToUse);
		}
		else {
			// Must generate CGLIB subclass.
			// 必须使用CGLIB代理实例化，这里调用的是 Cglib 子类的方法
			return instantiateWithMethodInjection(bd, beanName, owner);
		}
	}
```

**CglibSubclassingInstantiationStrategy.instantiateWithMethodInjection : Cglib 子类代理实例化策略**

```java
protected Object instantiateWithMethodInjection(RootBeanDefinition bd, @Nullable String beanName, BeanFactory owner) {
	return instantiateWithMethodInjection(bd, beanName, owner, null);
}
	
protected Object instantiateWithMethodInjection(RootBeanDefinition bd, @Nullable String beanName, BeanFactory owner,
		@Nullable Constructor<?> ctor, Object... args) {

	// Must generate CGLIB subclass...
	return new CglibSubclassCreator(bd, owner).instantiate(ctor, args);
}
	
public Object instantiate(@Nullable Constructor<?> ctor, Object... args) {
	// 使用 Cglib 生成子类 Class
	Class<?> subclass = createEnhancedSubclass(this.beanDefinition);
	Object instance;
	// 没有构造方法，使用默认的
	if (ctor == null) {
		instance = BeanUtils.instantiateClass(subclass);
	}
	// 有构造方法，获取构造方法并调用构造方法实例化
	else {
		try {
			Constructor<?> enhancedSubclassConstructor = subclass.getConstructor(ctor.getParameterTypes());
			instance = enhancedSubclassConstructor.newInstance(args);
		}
		catch (Exception ex) {
			throw new BeanInstantiationException(this.beanDefinition.getBeanClass(),
					"Failed to invoke constructor for CGLIB enhanced subclass [" + subclass.getName() + "]", ex);
		}
	}
	// SPR-10785: set callbacks directly on the instance instead of in the
	// enhanced class (via the Enhancer) in order to avoid memory leaks.
	Factory factory = (Factory) instance;
	factory.setCallbacks(new Callback[] {NoOp.INSTANCE,
			new LookupOverrideMethodInterceptor(this.beanDefinition, this.owner),
			new ReplaceOverrideMethodInterceptor(this.beanDefinition, this.owner)});
	return instance;
}
```

&emsp;&emsp;对于 Cglib 代理的实例化处理，先使用 Cglib 创建出子类 Class，然后使用子类 Class 创建 bean。回到 AbstractAutowireCapableBeanFactory.doCreateBean 方法中，第一步实例化 bean，将 BeanDefinition 转换为 BeanWrapper 已经完成。

## 4 bean 后置处理器

&emsp;&emsp;在 bean 实例化完成后，此时的 bean 还尚未成熟，bean 中的属性还未初始化，接下来，就是想办法对 bean 中的属性进行注入，其中就包括 `@Autowired` 注解的处理等操作，而处理的第一步，就是先扫描有哪些属性需要处理。MergedBeanDefinitionPostProcessor 接口提供了一个在 bean 创建后的增强处理函数，可以完成一部分注解的扫描工作。

**AbstractAutowireCapableBeanFactory.applyMergedBeanDefinitionPostProcessors**

```java
protected void applyMergedBeanDefinitionPostProcessors(RootBeanDefinition mbd, Class<?> beanType, String beanName) {
		for (MergedBeanDefinitionPostProcessor processor : getBeanPostProcessorCache().mergedDefinition) {
			processor.postProcessMergedBeanDefinition(mbd, beanType, beanName);
		}
	}
```

&emsp;&emsp;这里会调用所有注册的 MergedBeanDefinitionPostProcessor 实例的 postProcessMergedBeanDefinition 方法，这里挑几个实现看一下。


### AutowiredAnnotationBeanPostProcessor

&emsp;&emsp;AutowiredAnnotationBeanPostProcessor 实现了对带注解的字段、setter 方法和任意配置方法的自动注入过程，默认情况下通过检测 Spring 的 @Autowired 和 @Value 注解来识别要注入的属性或方法，还支持 JSR-330 的 @Inject 注解（如果可用）作为 Spring 自己的 @Autowired 的直接替代。

&emsp;&emsp;对于**由 @Autowired 标注的构造函数**来说，任何给定的 bean 类中**只能有一个**构造函数可以将 “required” 属性设置声明为 true，此时 bean 中不能再出现第二个被 @Autowired 标注的构造函数，无论它的 “required” 属性是 true 还是 false。当有多个 @Autowired(required = false) 声明的构造函数时，它们将被视为自动装配的候选者，如果没有候选者可以使用，那么将使用主/默认构造函数（如果存在），如果一个类只声明一个构造函数，它将始终被使用，带注解的构造函数不必是公共的。@Autowired 注解标注在构造函数上的情况的解析逻辑在上文的构造方法智能选择器处有详细说明。

&emsp;&emsp;默认的 AutowiredAnnotationBeanPostProcessor 将由 `context:annotation-config` 和 `context:component-scan` XML 标签注册。如果打算指定自定义 AutowiredAnnotationBeanPostProcessor，请删除或关闭那里的默认注释配置。

&emsp;&emsp;**注意**：注解注入会在 XML 注入之前进行；因此，对于在注解中和在 xml 中都配置了注入的情况，后一种配置将覆盖前者。

**AutowiredAnnotationBeanPostProcessor.AutowiredAnnotationBeanPostProcessor : 构造方法**

&emsp;&emsp;这里初始化了该增强器处理的注解类型，可以看到该类主要扫描处理的有 Autowired、Value 和 Inject 注解。

```java
public AutowiredAnnotationBeanPostProcessor() {
		this.autowiredAnnotationTypes.add(Autowired.class);
		this.autowiredAnnotationTypes.add(Value.class);
		try {
			this.autowiredAnnotationTypes.add((Class<? extends Annotation>)
					ClassUtils.forName("javax.inject.Inject", AutowiredAnnotationBeanPostProcessor.class.getClassLoader()));
			logger.trace("JSR-330 'javax.inject.Inject' annotation found and supported for autowiring");
		}
		catch (ClassNotFoundException ex) {
			// JSR-330 API not available - simply skip.
		}
	}
```

**AutowiredAnnotationBeanPostProcessor.postProcessMergedBeanDefinition**

```java
public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
		//扫描注解
		InjectionMetadata metadata = findAutowiringMetadata(beanName, beanType, null);
		//如果集合里有注解的属性和方法，就会将他们注册到beanDefinition的集合externallyManagedConfigMembers中
		metadata.checkConfigMembers(beanDefinition);
	}
	
private InjectionMetadata findAutowiringMetadata(String beanName, Class<?> clazz, @Nullable PropertyValues pvs) {
	// Fall back to class name as cache key, for backwards compatibility with custom callers.
	String cacheKey = (StringUtils.hasLength(beanName) ? beanName : clazz.getName());
	// Quick check on the concurrent map first, with minimal locking.
	InjectionMetadata metadata = this.injectionMetadataCache.get(cacheKey);
	//从缓存获取并判断是否需要刷新
	if (InjectionMetadata.needsRefresh(metadata, clazz)) {
		synchronized (this.injectionMetadataCache) {
			metadata = this.injectionMetadataCache.get(cacheKey);
			if (InjectionMetadata.needsRefresh(metadata, clazz)) {
				if (metadata != null) {
					metadata.clear(pvs);
				}
				metadata = buildAutowiringMetadata(clazz);
				this.injectionMetadataCache.put(cacheKey, metadata);
			}
		}
	}
	return metadata;
}

private InjectionMetadata buildAutowiringMetadata(final Class<?> clazz) {
	//确定给定的类是否是有要扫描的注解
	if (!AnnotationUtils.isCandidateClass(clazz, this.autowiredAnnotationTypes)) {
		return InjectionMetadata.EMPTY;
	}

	List<InjectionMetadata.InjectedElement> elements = new ArrayList<>();
	//需要处理的目标类
	Class<?> targetClass = clazz;

	do {
		final List<InjectionMetadata.InjectedElement> currElements = new ArrayList<>();

		// 1.通过反射获取该类所有的字段，并遍历每一个字段，
		// 通过方法 findAutowiredAnnotation 遍历每一个字段的所用注解，
		// 如果用autowired修饰了，则返回 auotowired 相关属性
		ReflectionUtils.doWithLocalFields(targetClass, field -> {
			MergedAnnotation<?> ann = findAutowiredAnnotation(field);
			if (ann != null) {
				// 校验注解是否用在了static字段上
				if (Modifier.isStatic(field.getModifiers())) {
					//静态字段不支持自动注入
					if (logger.isInfoEnabled()) {
						logger.info("Autowired annotation is not supported on static fields: " + field);
					}
					return;
				}
				//确定Required属性的值
				boolean required = determineRequiredStatus(ann);
				//记录需要注入的字段
				currElements.add(new AutowiredFieldElement(field, required));
			}
		});

		//2.通过反射处理类的method
		ReflectionUtils.doWithLocalMethods(targetClass, method -> {
			Method bridgedMethod = BridgeMethodResolver.findBridgedMethod(method);
			if (!BridgeMethodResolver.isVisibilityBridgeMethodPair(method, bridgedMethod)) {
				return;
			}
			MergedAnnotation<?> ann = findAutowiredAnnotation(bridgedMethod);
			if (ann != null && method.equals(ClassUtils.getMostSpecificMethod(method, clazz))) {
				if (Modifier.isStatic(method.getModifiers())) {
					//静态方法不支持自动注入
					if (logger.isInfoEnabled()) {
						logger.info("Autowired annotation is not supported on static methods: " + method);
					}
					return;
				}
				if (method.getParameterCount() == 0) {
					if (logger.isInfoEnabled()) {
						//自动装配的注解应该只用在有参数的方法上
						logger.info("Autowired annotation should only be used on methods with parameters: " +
								method);
					}
				}
				//确定Required属性的值
				boolean required = determineRequiredStatus(ann);
				PropertyDescriptor pd = BeanUtils.findPropertyForMethod(bridgedMethod, clazz);
				//记录需要注入的方法
				currElements.add(new AutowiredMethodElement(method, required, pd));
			}
		});
		//用@Autowired修饰的注解可能不止一个，因此都加在 elements 这个容器里面，一起处理
		elements.addAll(0, currElements);
		// 类似递归去寻找他父类的注入信息
		targetClass = targetClass.getSuperclass();
	}
	while (targetClass != null && targetClass != Object.class);

	return InjectionMetadata.forElements(elements, clazz);
}
```


&emsp;&emsp;可以看到 AutowiredAnnotationBeanPostProcessor 会扫描 Bean 的类中是否使用了 @Autowired 、 @Value 以及 @Inject 注解，扫描到的属性会封装成一个 InjectionMetadata，并缓存 injectionMetadataCache 中 key 为当前的 beanName，后续根据缓存信息进行属性填充；需要注意的是，这里只进行了扫描记录工作, 并没有进行属性注入工作。

### CommonAnnotationBeanPostProcessor

&emsp;&emsp;CommonAnnotationBeanPostProcessor 通过继承 InitDestroyAnnotationBeanPostProcessor 对 @PostConstruct 和 @PreDestroy 注解，以及依据 bean name 依赖注入的 @Resource 注解，也对 @WebServiceRef 注解提供支持，具有创建 JAX-WS 服务端点的能力。最后，处理器还支持 EJB3 ( @EJB )。

&emsp;&emsp;首先 CommonAnnotationBeanPostProcessor 的构造方法将处理的注解 @PostConstruct 和 @PreDestroy 传递给父类

```java
public CommonAnnotationBeanPostProcessor() {
	setOrder(Ordered.LOWEST_PRECEDENCE - 3);
	setInitAnnotationType(PostConstruct.class);
	setDestroyAnnotationType(PreDestroy.class);
	ignoreResourceType("javax.xml.ws.WebServiceContext");
}
```

&emsp;&emsp;注解的扫描处理都差不多，遍历方法将贴有 @PostConstruct 和 @PreDestroy 注解的方法分别保存在 initMethods 和 destroyMethods 中。

&emsp;&emsp;@PostConstruct 和 @PreDestroy 注解最先由 Java EE5 规范中引入，用于实现 Bean 初始化之前依赖注入完成后和容器销毁之前的自定义操作，应用注释的方法必须满足以下所有条件(以下条件是 Java EE5 中的规范，和 Spring 的实现有出入，写在括号里了)：

1. 被注解方法不得抛出已检查异常
2. 被注解方法不得有任何参数
3. 被注解方法返回值为 void (但实际也可以有返回值，至少不会报错，只会忽略)
4. 被注解方法需是非静态方法(测试结果是可以)
5. 此方法只会被执行一次
6. 该方法不应该是 final 修饰的(测试结果是可以)
7. 只有一个方法可以使用此注释进行注解(似乎 Spring 没有遵循此限制)

&emsp;&emsp;回到 CommonAnnotationBeanPostProcessor 中来，首先由静态代码块加载注解。

```java
static {
	webServiceRefClass = loadAnnotationType("javax.xml.ws.WebServiceRef");
	ejbClass = loadAnnotationType("javax.ejb.EJB");

	resourceAnnotationTypes.add(Resource.class);
	if (webServiceRefClass != null) {
		resourceAnnotationTypes.add(webServiceRefClass);
	}
	if (ejbClass != null) {
		resourceAnnotationTypes.add(ejbClass);
	}
}
```

&emsp;&emsp;后面的扫描和前面的差别不大，不在赘述，这里主要扫描处理 @EJB、@Resource 和 @WebServiceRef 注解。

### PersistenceAnnotationBeanPostProcessor

&emsp;&emsp;PersistenceAnnotationBeanPostProcessor 增强器用于处理 `@PersistenceUnit` 和 `@PersistenceContext` 注解，用于注入相应的持久化 JPA 资源 EntityManagerFactory(持久化工厂，可以生成多个EntityManager) 和 EntityManager(EntityManager是JPA中用于增删改查的接口，它的作用相当于一座桥梁，连接内存中的java对象和数据库的数据存储) (或者它们的子类变量)。任何 Spring 管理的对象中的任何此类带注释的字段或方法都将自动注入。

&emsp;&emsp;**注意 :**在目前的实现中, PersistenceAnnotationBeanPostProcessor 仅支持 `@PersistenceUnit` 和 `@PersistenceContext` 上带有属性 unitName 或者不带任何属性(比如使用缺省值 persistence unit)的情况。**如果这些注解使用在类上，并且使用了属性 name , 这些注解会被忽略**，因为它们此时仅仅作为部署提示 (参考Java EE规范)。

&emsp;&emsp;在使用基于 xml 配置的方式启动应用时，当在 xml 配置中使用 `<context:annotation-config/>` 或 `<context:component-scan/>` 标签时，Spring 将会为当前应用程序注册一个默认的 PersistenceAnnotationBeanPostProcessor，而对于基于注解的应用程序，例如 SpringBoot，如果使用了 JPA，Spring 也会自动注册一个默认的 PersistenceAnnotationBeanPostProcessor。如果想要指定一个自定义的 PersistenceAnnotationBeanPostProcessor，需要删除或关闭相应的 xml 配置或注解。











