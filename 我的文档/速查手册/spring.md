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

# Spring Boot

## Spring Boot 自动配置原理

Spring Boot 自动配置是 Spring Boot 框架的核心特性之一，它旨在简化 Spring 应用的配置。自动配置能够根据类路径中的jar依赖、已定义的bean以及各种属性设置来自动配置Spring应用。Spring Boot的自动配置通过以下机制实现：

1. **条件注解**：Spring Boot使用一系列的条件注解来决定是否要自动配置某个bean。例如，如果你的项目中包含了某个特定的jar包，Spring Boot可能会自动配置与之相关的bean。常见的条件注解包括`@ConditionalOnClass`、`@ConditionalOnMissingBean`、`@ConditionalOnProperty`等。

2. **@EnableAutoConfiguration**：在Spring Boot应用的主类或配置类上使用`@SpringBootApplication`注解，该注解内部包含了`@EnableAutoConfiguration`注解。`@EnableAutoConfiguration`注解会触发自动配置的加载过程。

3. **spring.factories文件**：Spring Boot查找`/META-INF/spring.factories`文件，该文件中列出了所有可能需要自动配置的类。Spring Boot会读取这个文件，并根据文件中定义的配置类来决定是否进行自动配置。

4. **排除特定自动配置**：如果开发者不希望使用某些自动配置，可以通过`@SpringBootApplication`注解的`exclude`属性来排除它们，或者使用`spring.autoconfigure.exclude`属性在配置文件中排除。

5. **配置属性绑定**：Spring Boot自动配置会读取`application.properties`或`application.yml`中的配置属性，并将这些属性绑定到相应的bean上，以便于自定义自动配置的行为。

6. **智能推断**：Spring Boot会根据类路径中的jar包和bean的定义智能推断需要配置哪些组件。例如，如果类路径中存在`H2`数据库的jar包，Spring Boot可能会自动配置一个内存数据库。

7. **自定义自动配置**：开发者也可以编写自己的自动配置类，并在`/META-INF/spring.factories`文件中注册，以便Spring Boot在启动时加载。

通过这些机制，Spring Boot能够根据应用的实际情况，智能地进行配置，极大地简化了开发者的配置工作。开发者只需要关注业务逻辑的实现，而不需要手动配置大量的Spring框架细节。

## SPI 机制

SPI 机制，全称为 Service Provider Interface，即服务提供者接口，是一种在Java编程语言中用于实现服务加载的机制。它允许在运行时动态地加载和使用服务，而无需在编译时知道具体的服务实现。这种机制特别适用于那些需要插件化或者模块化的系统，允许系统在不修改源代码的情况下扩展新的功能。

SPI 的核心思想是定义一组接口，然后由不同的服务提供者实现这些接口。系统在运行时通过特定的类加载器来查找和加载这些实现类。Java的SPI机制主要通过以下步骤实现：

1. **定义服务接口**：首先定义一个服务接口，这个接口定义了服务提供者需要实现的方法。

2. **服务提供者实现接口**：服务提供者根据服务接口编写具体的实现类，并将这些实现类打包成JAR文件。

3. **在资源目录下创建配置文件**：在服务提供者的JAR包的`META-INF/services`目录下创建一个以服务接口全限定名为名的文件（例如`com.example.MyService`），文件内容为实现类的全限定名（例如`com.example.impl.MyServiceImpl`）。

4. **服务加载**：系统在运行时通过`ServiceLoader`类来加载服务。`ServiceLoader`会读取`META-INF/services`目录下的配置文件，根据文件中指定的实现类全限定名来加载具体的实现类。

5. **使用服务**：加载到的实现类可以被系统使用，实现具体的功能。

SPI机制的优点是增加了系统的灵活性和可扩展性，使得服务的提供者和消费者可以解耦，便于管理和维护。然而，SPI也存在一些缺点，比如加载效率问题、可能的类加载器问题等。在使用时需要根据具体的应用场景和需求来权衡利弊。

SPI（Service Provider Interface）机制的工作原理主要涉及以下几个步骤：

1. 定义服务接口

首先，你需要定义一个服务接口。这个接口定义了服务提供者需要实现的方法。例如，假设我们有一个服务接口`MyService`，它定义了一个方法`doSomething()`。

```java
public interface MyService {
    void doSomething();
}
```

2. 服务提供者实现接口

服务提供者需要根据这个接口编写具体的实现类。例如，一个服务提供者可能实现了一个`MyServiceImpl`类：

```java
public class MyServiceImpl implements MyService {
    @Override
    public void doSomething() {
        System.out.println("MyServiceImpl does something.");
    }
}
```

3. 创建配置文件

服务提供者需要在自己的JAR包中的`META-INF/services`目录下创建一个配置文件，文件名是服务接口的全限定名，内容是实现类的全限定名。

例如，配置文件`com.example.MyService`的内容如下：

```
com.example.MyServiceImpl
```

4. 服务加载

系统在运行时通过`ServiceLoader`类来加载服务。`ServiceLoader`会读取`META-INF/services`目录下的配置文件，根据文件中指定的实现类全限定名来加载具体的实现类。

例如，加载服务的代码如下：

```java
ServiceLoader<MyService> serviceLoader = ServiceLoader.load(MyService.class);
for (MyService service : serviceLoader) {
    service.doSomething();
}
```

5. 使用服务

加载到的实现类可以被系统使用，实现具体的功能。在上面的例子中，`serviceLoader`会加载所有在配置文件中指定的`MyService`实现，并允许我们调用它们的`doSomething()`方法。

**工作原理细节**

- **ServiceLoader 类**：`ServiceLoader`类是Java提供的一个用于加载服务的工具类。它使用懒加载机制，即只有在实际需要使用服务时才加载服务实现。
- **查找服务实现**：`ServiceLoader`在初始化时会读取所有配置文件，并将配置文件中指定的实现类全限定名存储起来。当需要使用服务时，`ServiceLoader`会根据这些全限定名来加载具体的实现类。
- **类加载器**：`ServiceLoader`使用当前线程的上下文类加载器来加载服务实现。这允许在不同的类加载器环境中灵活加载服务。

SPI机制的优点是增加了系统的灵活性和可扩展性，使得服务的提供者和消费者可以解耦，便于管理和维护。然而，SPI也存在一些缺点，比如加载效率问题、可能的类加载器问题等。在使用时需要根据具体的应用场景和需求来权衡利弊。

### SPI 机制在 SpringBoot 中的应用

在Spring Boot中，SPI机制（Service Provider Interface）被用来实现各种插件化和扩展点的功能。Spring Boot利用Java的SPI机制来发现和加载一些特定的组件，比如数据源、消息代理等。下面是一些Spring Boot中SPI机制的应用示例：

1. 数据源自动配置

在Spring Boot中，如果你添加了`spring-boot-starter-jdbc`或`spring-boot-starter-data-jpa`依赖，Spring Boot会自动配置数据源。它通过SPI机制查找并加载`DataSource`接口的实现类。

例如，HikariCP是一个流行的数据库连接池实现，它提供了一个`DataSource`接口的实现。在`spring-boot-starter-jdbc`中，HikariCP的JAR包包含了`META-INF/services/java.sql.Driver`文件，指定了`com.zaxxer.hikari.HikariDataSource`作为实现类。

Spring Boot的自动配置机制会根据这个文件找到并使用HikariCP作为数据源。

2. 消息代理自动配置

Spring Boot也使用SPI机制来自动配置消息代理，如RabbitMQ和Kafka。这些框架通常提供了一个`META-INF/services`目录下的配置文件，指定了它们的连接工厂或配置类。

例如，RabbitMQ的自动配置会查找`META-INF/services/com.rabbitmq.client.ConnectionFactory`文件，找到并使用RabbitMQ提供的`ConnectionFactory`实现。

3. 自定义SPI实现

开发者也可以利用Spring Boot的SPI机制来实现自定义的扩展点。你可以定义一个接口，并通过SPI机制提供多个实现。Spring Boot的自动配置机制会根据这些实现来配置相应的组件。

例如，你可以定义一个`MyService`接口和多个实现类，然后在你的项目中添加一个`META-INF/services/com.example.MyService`文件，列出所有实现类的全限定名。Spring Boot的自动配置可以检测到这些实现，并根据需要进行配置。

















