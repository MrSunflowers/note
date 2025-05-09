# Spring | Reactor

Spring Reactor 是一个基于 Java 8 的响应式编程框架，它提供了响应式编程模型，用于构建非阻塞、事件驱动的应用程序。Spring Reactor 是 Spring Framework 5 的一部分，旨在与 Spring 生态系统无缝集成，包括 Spring Boot、Spring Data 和 Spring Cloud。

Spring Reactor 的核心是 Reactive Streams 规范，这是一个用于处理异步数据流的 API 规范。Reactive Streams 规范定义了几个关键的接口：

1. `Publisher`：发布者，负责发布数据流。
2. `Subscriber`：订阅者，负责接收数据流。
3. `Subscription`：订阅，表示订阅者和发布者之间的连接。
4. `Processor`：处理器，既是发布者也是订阅者，可以对数据流进行转换和处理。

Spring Reactor 提供了两种主要的发布者实现：

- `Flux`：表示一个包含 0 到 N 个元素的异步序列。它可以发出三种类型的信号：元素、完成和错误。
- `Mono`：表示一个可能包含 0 或 1 个元素的异步序列。它主要用于表示一个可能不存在的值。

Spring Reactor 的主要特点包括：

- **非阻塞**：Spring Reactor 使用非阻塞的背压机制，这意味着当订阅者无法处理更多数据时，它会通知发布者减慢或停止发送数据。
- **弹性**：Spring Reactor 提供了错误处理和重试机制，使得应用程序能够优雅地处理异常和失败。
- **背压**：背压是一种流控制机制，允许订阅者控制发布者发送数据的速度。
- **与 Spring 生态系统集成**：Spring Reactor 与 Spring Framework 的其他组件紧密集成，使得开发者可以轻松地在 Spring 应用程序中使用响应式编程。

Spring Reactor 适用于需要处理大量并发请求、实时数据流处理和高吞吐量的应用程序，例如微服务架构中的服务、实时分析系统和物联网应用。通过使用 Spring Reactor，开发者可以构建出更加高效、响应式和可扩展的应用程序。

Reactive Streams 规范定义了四个关键的接口，分别是 `Publisher`、`Subscriber`、`Subscription` 和 `Processor`。下面我将结合这些接口提供一个简单的演示示例。

# 响应式流接口

在 Java 9 中，`java.util.concurrent.Flow` 包提供了响应式流的接口，这些接口是 Reactive Streams 规范的实现。下面是这些接口的简要说明以及它们之间的关系：

1. `Publisher`：表示一个数据流的发布者，它可以异步地向一个或多个订阅者发布数据序列。

2. `Subscriber`：表示一个订阅者，它订阅一个 `Publisher` 并接收数据序列。

3. `Subscription`：表示订阅的上下文，它允许 `Subscriber` 控制从 `Publisher` 接收数据的速率。

4. `Processor`：继承自 `Subscriber` 和 `Publisher`，它既是订阅者也是发布者，可以对数据进行转换和处理。

下面是一个简单的代码示例，演示了如何使用这些接口：

```java
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

public class ReactiveStreamsExample {

    public static void main(String[] args) throws InterruptedException {
        // 创建一个 SubmissionPublisher，它实现了 Publisher 接口
        SubmissionPublisher<Integer> publisher = new SubmissionPublisher<>();

        // 创建一个 Subscriber，它实现了 Subscriber 接口
        Flow.Subscriber<Integer> subscriber = new Flow.Subscriber<>() {
            private Flow.Subscription subscription;

            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                // 订阅时保存 Subscription 对象
                this.subscription = subscription;
                // 请求一个数据项
                this.subscription.request(1);
            }

            @Override
            public void onNext(Integer item) {
                // 接收到一个数据项
                System.out.println("Received: " + item);
                // 请求下一个数据项
                this.subscription.request(1);
            }

            @Override
            public void onError(Throwable throwable) {
                // 发生错误时的处理
                throwable.printStackTrace();
            }

            @Override
            public void onComplete() {
                // 数据流完成时的处理
                System.out.println("Done!");
            }
        };

        // 将 Subscriber 注册到 Publisher
        publisher.subscribe(subscriber);

        // 发布数据
        for (int i = 0; i < 10; i++) {
            publisher.submit(i);
        }

        // 关闭 Publisher，通知 Subscriber 数据流结束
        publisher.close();
    }
}
```

在这个示例中，我们首先创建了一个 `SubmissionPublisher`，它是一个实现了 `Publisher` 接口的类，用于发布数据。然后，我们创建了一个实现了 `Subscriber` 接口的匿名类，它定义了 `onSubscribe`、`onNext`、`onError` 和 `onComplete` 方法来处理数据流。

我们通过调用 `publisher.subscribe(subscriber)` 将 `Subscriber` 注册到 `Publisher` 上。然后，我们通过调用 `publisher.submit(i)` 来发布数据。最后，我们调用 `publisher.close()` 来通知 `Subscriber` 数据流已经结束。

这个示例展示了 `Publisher` 和 `Subscriber` 之间的基本交互，以及如何使用 `Subscription` 来控制数据流的速率。`Processor` 接口的使用会更复杂，因为它需要同时处理订阅和发布，但基本原理与 `Subscriber` 和 `Publisher` 类似。

在上一个示例的基础上，我们将演示 `Processor` 的作用。`Processor` 是一个同时实现了 `Subscriber` 和 `Publisher` 接口的类，它允许在数据流中进行转换和处理。在下面的代码示例中，我们将创建一个 `Processor`，它将接收的整数乘以 2 后再发布出去。

```java
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

public class ReactiveStreamsProcessorExample {

    public static void main(String[] args) throws InterruptedException {
        // 创建一个 SubmissionPublisher，它实现了 Publisher 接口
        SubmissionPublisher<Integer> publisher = new SubmissionPublisher<>();

        // 创建一个 Processor，它继承自 SubmissionPublisher 并实现了 Processor 接口
        Flow.Processor<Integer, Integer> processor = new Flow.Processor<>() {
            private Flow.Subscription subscription;

            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                // 订阅时保存 Subscription 对象
                this.subscription = subscription;
                // 请求一个数据项
                this.subscription.request(1);
            }

            @Override
            public void onNext(Integer item) {
                // 接收到一个数据项，进行处理（这里是乘以 2）
                int processedItem = item * 2;
                // 发布处理后的数据项
                publisher.submit(processedItem);
                // 请求下一个数据项
                this.subscription.request(1);
            }

            @Override
            public void onError(Throwable throwable) {
                // 发生错误时的处理
                throwable.printStackTrace();
            }

            @Override
            public void onComplete() {
                // 数据流完成时的处理
                publisher.close();
            }
        };

        // 将 Processor 注册到 Publisher
        publisher.subscribe(processor);

        // 创建一个 Subscriber，它实现了 Subscriber 接口
        Flow.Subscriber<Integer> subscriber = new Flow.Subscriber<>() {
            private Flow.Subscription subscription;

            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                // 订阅时保存 Subscription 对象
                this.subscription = subscription;
                // 请求一个数据项
                this.subscription.request(1);
            }

            @Override
            public void onNext(Integer item) {
                // 接收到一个数据项
                System.out.println("Received: " + item);
                // 请求下一个数据项
                this.subscription.request(1);
            }

            @Override
            public void onError(Throwable throwable) {
                // 发生错误时的处理
                throwable.printStackTrace();
            }

            @Override
            public void onComplete() {
                // 数据流完成时的处理
                System.out.println("Done!");
            }
        };

        // 将 Subscriber 注册到 Processor
        processor.subscribe(subscriber);

        // 发布数据
        for (int i = 0; i < 10; i++) {
            publisher.submit(i);
        }

        // 关闭 Publisher，通知 Subscriber 数据流结束
        publisher.close();
    }
}
```

在这个示例中，我们创建了一个 `Processor`，它接收整数并将其乘以 2 后再发布。我们首先将 `Processor` 注册到 `Publisher` 上，然后将 `Subscriber` 注册到 `Processor` 上。这样，当 `Publisher` 发布数据时，`Processor` 会接收到这些数据，处理后（乘以 2）再发布给 `Subscriber`。

这个示例展示了 `Processor` 在数据流中的作用，它允许我们在数据发布到订阅者之前对其进行转换和处理。在实际应用中，`Processor` 可以用于更复杂的转换逻辑，例如数据过滤、映射、聚合等。

# Reactor

高并发 ： 缓存，异步，队排好
高可用 ： 分片，复制，选领导

Reactor 框架可无缝对接 java Stream、CompletableFuture、Duration 等 API 接口

## 背压（Backpressure）机制

Reactor 是一个响应式编程库，它支持背压（Backpressure）机制，这是一种处理生产者和消费者之间速率不匹配问题的策略。在响应式流中，背压允许消费者控制生产者发送数据的速度，以避免资源耗尽或处理延迟。Reactor 提供了多种背压策略，允许开发者根据具体场景选择合适的处理方式。

以下是 Reactor 中提供的几种背压策略：

1. **onBackpressureBuffer**：这是 Reactor 默认的背压策略。当消费者无法跟上生产者的发送速度时，生产者发送的数据会被缓存在一个队列中。这个队列的大小是有限的，如果队列满了，生产者会停止发送数据，直到消费者能够处理更多的数据。这种策略可以防止内存溢出，但可能会导致数据丢失，如果队列满了，新的数据会被丢弃。

2. **onBackpressureDrop**：在这种策略下，如果消费者跟不上生产者的发送速度，生产者发送的数据会被直接丢弃。这种策略不会导致内存溢出，但可能会丢失数据。

3. **onBackpressureLatest**：这种策略会立即推送最新的数据给消费者，而忽略旧的数据。如果消费者跟不上生产者的发送速度，旧的数据会被丢弃。

4. **onBackpressureError**：在这种策略下，如果消费者跟不上生产者的发送速度，生产者会抛出一个异常，通知消费者处理速率不匹配的问题。

5. **limitRate(n)**：这种策略限制生产者一次最多发送 n 个数据。如果消费者跟不上生产者的发送速度，生产者会停止发送数据，直到消费者能够处理更多的数据。

在实际应用中，开发者可以根据具体的需求选择合适的背压策略。例如，如果数据丢失是可以接受的，可以选择 onBackpressureDrop 策略；如果数据的实时性非常重要，可以选择 onBackpressureLatest 策略；如果需要确保数据的完整性，可以选择 onBackpressureBuffer 策略。

在使用背压策略时，开发者需要确保消费者能够正确处理背压信号，例如通过实现 `Subscriber` 接口的 `onSubscribe` 方法中的 `request` 方法来请求数据。

![image-20240601185240968](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202406011853355.png)

## Flux

在 Reactor 中，数据流被分为了两种

- `Flux`：表示一个包含 0 到 N 个元素的异步序列。它可以发出三种类型的信号：元素、完成和错误。
- `Mono`：表示一个可能包含 0 或 1 个元素的异步序列。它主要用于表示一个可能不存在的值。

它们都是 Publisher 的实现，即数据发布者

定义一个 Flux 数据流

```java
public class Main {
    public static void main(String[] args) {
        Flux<Integer> flux = Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }
}
```

定义的数据流没有使用即没有任何意义
 
```java
public class Main {
    public static void main(String[] args) {
        Flux<Integer> flux = Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        flux.subscribe(System.out::println);// 订阅，即交由谁消费
    }
}
```

```java
public class Main {
    public static void main(String[] args) {
        Flux<Integer> flux = Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        flux.subscribe(System.out::println);// 订阅，即交由谁消费，也是数据流处理器
        flux.subscribe(System.out::println);// 可以有多个处理器，上一个处理器处理完后这个再处理
    }
}
```

## Mono

定义一个 Mono 数据流

```java
public class Main {
    public static void main(String[] args) {
        Mono<Integer> mono = Mono.just(1); // Mono 流中只允许有一个元素
        mono.subscribe(System.out::println);
    }
}
```

## 流的事件感知

```java
public class Main {
    public static void main(String[] args) throws IOException {
        Flux<Integer> flux = Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                // 当流结束后的回调
                .doOnComplete(() -> {
                    System.out.println("done");
                });

        flux.subscribe(System.out::println);// 订阅，即交由谁消费，也是数据流处理器

        System.in.read();
    }
}
```

流的中间操作,与 java 8 的 Stream 类似

```java
public class Main {
    public static void main(String[] args) throws IOException {
        Flux<Integer> flux = Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .filter((i) -> i > 2) // 过滤流
                // 当流结束后的回调
                .doOnComplete(() -> System.out.println("done"));

        flux.subscribe(System.out::println);// 订阅，即交由谁消费，也是数据流处理器
        flux.subscribe(System.out::println);// 订阅，即交由谁消费，也是数据流处理器

        System.in.read();
    }
}
```

