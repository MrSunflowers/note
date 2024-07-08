# spring

## 依赖注入方式

- 构造函数注入
- setter 注入
- 接口注入

## spring bean 的实例化流程

Spring Bean的实例化流程是Spring框架的核心部分之一，它涉及了Spring容器如何创建和管理应用程序中的对象。以下是Spring Bean实例化的基本流程：

1. **Bean定义的加载**：
   - 在Spring容器启动时，它会读取配置信息（XML配置、注解配置或Java配置类）。
   - 这些配置信息包含了Bean的定义，包括Bean的类路径、作用域、属性值、构造参数等。

2. **Bean定义的解析**：
   - Spring容器解析这些Bean定义，并将它们注册到Bean工厂中。
   - Bean定义被封装成`BeanDefinition`对象，这些对象包含了Bean的所有配置信息。

3. **Bean的创建**：
   - 当请求获取Bean时（例如通过`ApplicationContext.getBean()`方法），Spring容器会根据Bean定义来创建Bean实例。
   - 如果Bean定义了作用域为单例（Singleton），则Spring容器会确保整个应用中只有一个Bean实例。

4. **依赖注入**：
   - 在创建Bean实例之前，Spring容器会处理Bean的依赖关系。
   - 如果Bean定义了依赖其他Bean，则Spring容器会先创建这些依赖的Bean，并将它们注入到当前Bean中。

5. **Bean实例化**：
   - Spring容器使用反射机制来创建Bean的实例。
   - 如果Bean定义了构造函数参数或工厂方法，Spring容器会使用这些信息来创建Bean实例。

6. **Bean属性填充**：
   - 一旦Bean实例被创建，Spring容器会根据Bean定义来填充Bean的属性。
   - 这可能包括设置属性值、注入其他Bean的引用等。

7. **Bean初始化**：
   - 在Bean的属性被填充之后，Spring容器会调用Bean的初始化方法（例如`@PostConstruct`注解的方法或实现`InitializingBean`接口的`afterPropertiesSet`方法）。
   - 这个步骤允许Bean执行一些初始化操作，如建立数据库连接、加载资源等。

8. **Bean的使用**：
   - 一旦Bean被初始化，它就可以被应用程序使用了。
   - Spring容器会将Bean实例存储在缓存中，以便后续请求时可以快速获取。

9. **Bean的销毁**：
   - 当Spring容器关闭时，它会销毁所有的单例Bean。
   - 如果Bean定义了销毁方法（例如`@PreDestroy`注解的方法或实现`DisposableBean`接口的`destroy`方法），Spring容器会在销毁Bean之前调用这些方法。

整个流程中，Spring容器通过其内部的`BeanFactory`或`ApplicationContext`接口来管理Bean的生命周期，确保Bean的创建、配置、使用和销毁过程符合预期的配置和行为。

![](https://raw.githubusercontent.com/MrSunflowers/images/main/note/spring/202201101031279.png)

## Autowired 和 resource 的区别

`@Autowired` 和 `@Resource` 是Spring框架中用于依赖注入的两个注解，它们都可以用来自动装配Spring管理的bean。尽管它们的目的相似，但它们在使用方式和注入机制上存在一些差异。

`@Autowired`

- `@Autowired` 是Spring框架提供的注解，它遵循Spring的自动装配规则。
- 它可以用于字段、构造器、setter方法以及方法参数上。
- `@Autowired` 默认按照类型（Type）进行自动装配，如果需要按照名称（Name）进行装配，可以结合`@Qualifier`注解使用。
- 如果Spring容器中没有找到匹配的bean，将会抛出异常。可以通过设置`required`属性为`false`来避免这种情况，此时如果没有找到匹配的bean，该字段将不会被装配。

`@Resource`

- `@Resource` 是Java EE提供的注解，它遵循Java EE的依赖注入规则。
- 它主要用于字段和setter方法上。
- `@Resource` 默认按照名称（Name）进行自动装配，如果需要按照类型（Type）进行装配，可以设置`name`属性为空字符串。
- 如果没有找到匹配的bean，`@Resource`不会抛出异常，而是会静默失败。

区别总结

- **来源不同**：`@Autowired`是Spring框架的注解，而`@Resource`是Java EE的注解。
- **默认装配方式不同**：`@Autowired`默认按类型装配，`@Resource`默认按名称装配。
- **异常处理不同**：`@Autowired`在没有找到匹配的bean时会抛出异常，而`@Resource`则不会。
- **使用位置**：`@Autowired`可以用于字段、构造器、setter方法和方法参数，而`@Resource`主要用于字段和setter方法。

在实际开发中，选择使用`@Autowired`还是`@Resource`取决于你的具体需求和偏好。如果你需要更灵活的装配方式，或者希望在装配失败时得到明确的异常提示，`@Autowired`可能是更好的选择。如果你倾向于按名称装配，并且希望在装配失败时避免异常，那么`@Resource`可能更适合你。

## 循环依赖问题

&emsp;&emsp;bean 已经创建完成，总结一下循环依赖的整体处理流程，用例子来说明可能更容易理解，创建两个 bean

```xml
<bean name="userA" class="myTest.UserA" autowire="byType" />
<bean name="userB" class="myTest.UserB" autowire="byType" />
```

【UserB】

```java
public class UserB{
	
	private UserA userA;

	public UserA getUserA() {
		return userA;
	}

	public void setUserA(UserA userA) {
		this.userA = userA;
	}

}
```

【UserA】

```java
public class UserA {
	
	private UserB userB;

	public UserB getUserB() {
		return userB;
	}

	public void setUserB(UserB userB) {
		this.userB = userB;
	}

}
```

1. 将 UserA 实例化以后，在填充属性之前，会将这个时候的 UserA 通过 ObjectFactory 记录下来，并存入三级缓存 `singletonFactories` 中。
2. 在填充 UserA 的属性时发现 UserA 中引用了 UserB，这时去调用 doGetBean 方法去获取 UserB，同样，在填充属性之前，会将这个时候的 UserB 通过 ObjectFactory 记录下来，并存入三级缓存 `singletonFactories` 中，现在 `singletonFactories` 中同时存在 UserA 和 UserB 的早期引用。
3. 在填充 UserB 的属性时发现 UserB 中引用了 UserA，此时再次调用 doGetBean 取获取 UserA，因为在 `singletonFactories` 中已经存在 UserA 直接获取在第一步中缓存的 UserA 的引用。
4. 将缓存的 UserA 的引用转换为半成品对象，加入二级缓存 `earlySingletonObjects` 中，并从三级缓存 `singletonFactories` 中移除，现在二级缓存 `earlySingletonObjects` 中存在 UserA，三级缓存 `singletonFactories` 中存在 UserB。
5. 将获取到的 UserA 填充至 UserB 中，UserB 完成创建过程，并将完整的单例对象存入一级缓存 `singletonObjects` 中，同时将 UserB 从三级缓存 `singletonFactories` 中移除，现在三级缓存 `singletonFactories` 中没有缓存的 bean 了。
6. 获取到 UserB 的 UserA 随后完成创建过程，并将完整的单例对象存入一级缓存 `singletonObjects` 中，同时从二级缓存 `earlySingletonObjects` 中移除，现在二级缓存 `earlySingletonObjects` 中也没有缓存的 bean 了，循环引用处理完成。

# Spring MVC

## DispatcherServlet 的工作流程

`DispatcherServlet` 是Spring MVC框架的核心组件，负责处理Web应用中的请求和响应。以下是`DispatcherServlet`的工作流程：

1. **接收请求**：客户端（如浏览器）发送HTTP请求到服务器，请求被转发到`DispatcherServlet`。

2. **请求预处理**：`DispatcherServlet`首先调用`HandlerMapping`来查找请求的处理器（`Handler`，即Controller）和拦截器（`HandlerInterceptor`）。

3. **拦截器预处理**：如果存在拦截器，`DispatcherServlet`会调用拦截器的`preHandle`方法进行预处理。如果预处理返回`false`，则请求处理流程终止，`DispatcherServlet`会返回响应给客户端。

4. **处理器执行**：如果拦截器预处理通过，`DispatcherServlet`会调用相应的`Handler`（Controller）来处理请求。`Handler`会根据请求执行业务逻辑，并返回一个`ModelAndView`对象，该对象包含了要渲染的视图名称和模型数据。

5. **视图解析**：`DispatcherServlet`将`ModelAndView`对象传递给`ViewResolver`，`ViewResolver`根据视图名称解析出具体的`View`对象。

6. **视图渲染**：`DispatcherServlet`调用`View`对象的`render`方法，将模型数据填充到视图中，并渲染输出到响应对象中。

7. **拦截器后处理**：在视图渲染完成后，`DispatcherServlet`会调用拦截器的`postHandle`方法进行后处理。如果在处理过程中发生了异常，还会调用`afterCompletion`方法。

8. **响应返回**：最后，`DispatcherServlet`将响应对象返回给客户端。

整个流程可以用以下的伪代码来表示：

```java
public class DispatcherServlet extends HttpServlet {
    protected void service(HttpServletRequest request, HttpServletResponse response) {
        // 1. 请求预处理
        HandlerExecutionChain handlerChain = getHandler(request);
        if (handlerChain == null) {
            // 未找到处理器
            return;
        }

        // 2. 拦截器预处理
        boolean preHandle = applyPreHandle(request, response, handlerChain.getHandler());
        if (!preHandle) {
            return;
        }

        // 3. 处理器执行
        ModelAndView modelAndView = handlerChain.getHandler().handle(request, response);
        applyPostHandle(request, response, handlerChain.getHandler(), modelAndView);

        // 4. 视图解析和渲染
        processDispatchResult(request, response, handlerChain, modelAndView);

        // 5. 拦截器后处理
        applyAfterCompletion(request, response, handlerChain.getHandler(), null);
    }
}
```

`DispatcherServlet`的配置通常在`web.xml`中进行，或者使用Spring Boot时，通过`@SpringBootApplication`注解自动配置。`DispatcherServlet`的配置包括映射路径、处理器映射、视图解析器等。

通过`DispatcherServlet`，Spring MVC实现了请求的分发、处理和响应的整个流程，使得开发者可以专注于业务逻辑的实现，而无需关心底层的请求处理细节。


