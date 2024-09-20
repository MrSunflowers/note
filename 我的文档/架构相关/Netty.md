Netty 是一个高性能的异步事件驱动的网络应用程序框架，用于快速开发可维护的高性能协议服务器和客户端。它主要被设计用于简化网络编程，如TCP和UDP套接字服务器的开发。由 JBOSS 开源，主要针对在 TCP 协议下，面向 Clients 端的高并发应用开发，或 Peer-to-Peer 场景下的大量数据持续传输应用。

Netty 的关键特性包括：

1. **异步和事件驱动**：Netty 使用了现代的异步网络编程模型，允许你编写高性能的网络应用。它不是简单地使用多线程来处理并发，而是通过事件循环和回调机制来处理并发，这可以显著减少线程上下文切换的开销。

2. **高吞吐量和低延迟**：Netty 被设计为可以处理成千上万的并发连接，同时保持低延迟。它优化了数据的处理流程，减少了不必要的内存复制。

3. **可扩展性**：Netty 提供了丰富的协议栈实现，如HTTP、WebSocket、SSL/TLS等，并允许开发者通过自定义编解码器来扩展支持更多协议。

4. **易于使用和维护**：Netty 的API设计清晰，易于理解和使用。同时，它拥有详尽的文档和活跃的社区支持，使得开发者可以快速上手并维护项目。

5. **健壮性**：Netty 经过长时间的测试和优化，被广泛应用于各种需要高性能网络通信的场景中，如游戏服务器、即时通讯、大数据传输等。

Netty 是用 Java 编写的，但它的设计哲学和性能优势使其成为构建网络应用的首选框架之一。它被广泛应用于互联网服务提供商、金融服务公司、游戏公司等需要处理大量网络连接的场景中。

# IO

Netty 基于 Java NIO 开发，要学习 Netty 必须了解 IO 相关知识。

计算机中的 "IO" 是 "输入/输出"（Input/Output）的缩写，指的是计算机系统与外部环境（如用户、外部设备或网络）之间交换数据的过程。IO 操作是计算机系统中非常基础且关键的功能，它涉及到数据的读取和写入。

**输入（Input）**

输入是指将数据从外部源传输到计算机系统内部的过程。例如：

- 键盘输入：用户通过键盘输入文本或命令。
- 鼠标输入：用户通过鼠标移动和点击来与计算机交互。
- 文件输入：从存储设备（如硬盘、SSD、USB驱动器）读取文件。
- **网络输入**：通过网络接口接收来自其他计算机或服务器的数据。

**输出（Output）**

输出是指将数据从计算机系统发送到外部环境的过程。例如：

- 显示器输出：将图像或文本显示在屏幕上供用户查看。
- 打印机输出：将文档或图像打印到纸张上。
- 文件输出：将数据写入到存储设备中。
- **网络输出**：通过网络发送数据到其他计算机或服务器。

**IO 设备**

IO 设备是实现输入输出功能的硬件设备，如键盘、鼠标、显示器、打印机、扫描仪、网络接口卡等。

**IO 操作的类型**

- **同步IO**：进程发出IO请求后，必须等待操作完成才能继续执行后续操作。
- **异步IO**：进程发出IO请求后，可以继续执行其他任务，IO操作在后台进行，完成后通知进程。
- **阻塞IO**：在IO操作完成前，调用线程会被挂起，直到有数据可读或写。
- **非阻塞IO**：在IO操作无法立即完成时，调用线程不会被挂起，而是立即返回，通常会返回一个指示状态，告知调用者是否需要等待或重试。

IO 是计算机系统与外界交互的桥梁，没有有效的IO机制，计算机系统将无法接收用户指令、处理数据或与外部世界进行通信。IO 系统的设计和性能直接影响到整个计算机系统的效率和用户体验。

IO 是计算机科学中的一个核心概念，它涉及到数据在计算机系统与外部环境之间的流动。理解IO的工作原理对于设计高效、响应迅速的计算机系统至关重要。

## IO 原理

在传统的I/O操作中，数据传输通常涉及用户空间和内核空间之间的数据复制。为了理解这一过程，我们需要先了解操作系统中用户空间和内核空间的概念。

**用户空间与内核空间**

在现代操作系统中，内存空间被分为两个主要部分：

1. **用户空间（User Space）**：这是应用程序运行的地方。用户空间中的代码不能直接访问硬件资源，也不能执行特权操作。用户空间的程序通过系统调用（System Calls）请求操作系统内核来执行这些操作。

2. **内核空间（Kernel Space）**：这是操作系统内核运行的地方。内核空间拥有对硬件资源的直接访问权限，并负责管理系统的资源，如CPU、内存、磁盘等。内核空间可以执行特权操作，如管理文件系统、网络通信、进程调度等。

**数据复制过程**

在传统的I/O操作中，数据从一个设备（如磁盘、网络接口等）传输到用户空间的过程通常涉及以下步骤：

1. **应用程序发起I/O请求**：应用程序通过系统调用请求操作系统内核读取数据。

2. **数据从设备传输到内核空间**：内核空间负责从设备读取数据。数据首先被读入内核空间的缓冲区。

3. **数据从内核空间复制到用户空间**：内核将数据从其缓冲区复制到应用程序在用户空间分配的缓冲区中。

4. **应用程序处理数据**：应用程序现在可以处理这些数据。

5. **数据写回操作**：如果应用程序需要将数据写回磁盘或发送到网络，这个过程会重复，数据首先从用户空间复制到内核空间的缓冲区，然后由内核负责将数据写入设备。

**为什么需要复制数据？**

数据在用户空间和内核空间之间复制的原因是出于安全和隔离的考虑。操作系统通过这种方式确保用户空间的程序不能直接访问或修改内核空间的数据，从而保护系统的稳定性和安全性。此外，这种隔离也使得操作系统可以更有效地管理资源和执行任务。

**性能影响**

尽管这种数据复制机制提供了必要的安全性和隔离性，但它也带来了性能开销。每次数据从内核空间复制到用户空间，或者从用户空间复制到内核空间，都会消耗CPU资源和时间。在处理大量数据或进行高吞吐量的I/O操作时，这种开销可能会成为性能瓶颈。

为了解决这个问题，现代操作系统和编程语言提供了多种优化技术，例如：

- **零拷贝（Zero-Copy）技术**：通过减少数据在用户空间和内核空间之间的复制次数来提高性能。例如，直接从内核空间的缓冲区将数据发送到网络接口，而不需要先复制到用户空间。

- **内存映射（Memory-Mapped Files）**：允许文件数据直接映射到用户空间的内存地址，应用程序可以直接读写这些内存地址，从而避免了数据在用户空间和内核空间之间的复制。

- **异步I/O（Asynchronous I/O）**：允许应用程序在I/O操作进行时继续执行其他任务，而不是等待I/O操作完成。这样可以提高应用程序的响应性和吞吐量。

通过这些技术，现代系统能够更有效地处理I/O操作，减少不必要的数据复制，从而提高整体性能。

## 内存映射文件

内存映射文件（Memory-Mapped Files）是一种允许文件数据直接映射到用户空间内存的技术。这种技术通过操作系统内核将文件的一部分或全部映射到进程的虚拟地址空间中，使得文件数据看起来像是进程内存的一部分。当应用程序访问这些内存地址时，实际上是在访问文件数据，而不需要进行数据在用户空间和内核空间之间的显式复制。

**内存映射文件的工作原理**

1. **映射文件到内存**：操作系统内核将文件的特定区域映射到进程的虚拟地址空间。这通常通过`mmap`系统调用实现。

2. **访问映射区域**：应用程序通过普通的内存访问指令（如读取或写入操作）来访问映射区域。这些操作实际上是对文件数据的访问。

3. **内核处理访问**：当应用程序访问映射区域时，如果数据不在物理内存中（即发生了页面错误），内核会从磁盘读取相应的数据到物理内存，并更新页表，使得虚拟地址映射到正确的物理地址。

4. **数据修改**：如果应用程序修改了映射区域的数据，这些修改会直接反映到物理内存中。当数据被修改后，内核会标记这些页面为“脏”（dirty），表示它们需要被写回到磁盘。

5. **同步到磁盘**：当需要将脏页面写回到磁盘时，内核会负责将这些数据写回文件。这个过程是异步的，应用程序可以继续执行其他任务。

**为什么内存映射文件能避免数据复制**

内存映射文件之所以能避免数据在用户空间和内核空间之间的复制，是因为它利用了虚拟内存管理机制。当应用程序访问映射区域时，实际上是在访问内核管理的物理内存，而不是直接访问磁盘上的文件。这样，数据的读取和写入操作都是在内核空间内部完成的，不需要将数据从内核空间复制到用户空间，或者反过来。

**优点**

- **性能提升**：由于避免了数据在用户空间和内核空间之间的复制，内存映射文件可以显著提高文件I/O操作的性能。

- **简化编程**：应用程序可以像操作普通内存一样操作文件数据，简化了文件I/O的编程模型。

- **共享内存**：多个进程可以映射同一个文件，实现进程间的共享内存通信。

**注意事项**

- **内存管理**：虽然内存映射文件可以提高性能，但需要谨慎管理内存使用，因为映射的文件会占用进程的虚拟地址空间。

- **同步问题**：由于数据修改是异步写回磁盘的，需要确保数据的一致性和完整性，可能需要使用同步机制（如`msync`系统调用）。

- **文件大小限制**：映射的文件大小受到系统可用内存和虚拟地址空间大小的限制。

内存映射文件是现代操作系统中一种高效的文件I/O技术，它通过减少数据复制和利用虚拟内存管理机制来提高性能。然而，它也要求开发者了解其工作原理和限制，以确保正确和高效地使用。

# io 底层的执行流程

[深入理解javaio读写原理及底层流程 - 知乎 (zhihu.com)](https://zhuanlan.zhihu.com/p/442239987)

**Java IO读写原理**

无论是Socket的读写还是文件的读写，在Java层面的应用开发或者是linux系统底层开发，都属于输入input和输出output的处理，简称为IO读写。在原理上和处理流程上，都是一致的。区别在于参数的不同。

用户程序进行IO的读写，基本上会用到read\&write两大系统调用。可能不同操作系统，名称不完全一样，但是功能是一样的。

先强调一个基础知识：read系统调用，并不是把数据直接从物理设备，读数据到内存。write系统调用，也不是直接把数据，写入到物理设备。

read系统调用，是把数据从内核缓冲区复制到进程缓冲区；而write系统调用，是把数据从进程缓冲区复制到内核缓冲区。这个两个系统调用，都不负责数据在内核缓冲区和磁盘之间的交换。底层的读写交换，是由操作系统kernel内核完成的。

**内核态（Kernel Mode）和用户态（User Mode）**

在现代操作系统中，为了保护系统资源和提高安全性，通常会将CPU的运行模式分为内核态和用户态。

1.  **用户态（User Mode）**：这是应用程序运行的模式，它限制了应用程序可以执行的指令和访问的内存区域。在用户态下，应用程序不能直接执行某些特权指令，比如直接访问硬件设备、修改系统数据结构等。这样可以防止应用程序错误或恶意行为对系统造成损害。

2.  **内核态（Kernel Mode）**：这是操作系统内核运行的模式，拥有对硬件资源的完全访问权限。当应用程序需要执行一些需要特权的操作时，比如文件读写、网络通信、设备驱动操作等，它必须通过系统调用（System Call）来请求操作系统内核的帮助。系统调用是操作系统提供的接口，允许用户态程序请求内核态服务。

当系统调用发生时，操作系统会执行以下步骤：

*   **保存当前进程的状态**：操作系统会保存当前用户态程序的寄存器状态、程序计数器等信息，以便在系统调用完成后能够恢复到之前的状态继续执行。
*   **切换到内核态**：CPU模式切换到内核态，以便执行系统调用请求的操作。
*   **执行系统调用**：操作系统内核执行请求的操作，比如读取文件、分配内存等。
*   **恢复用户态程序的状态**：系统调用完成后，操作系统会恢复之前保存的用户态程序的状态信息，包括寄存器、程序计数器等。
*   **返回用户态**：CPU模式切换回用户态，用户态程序继续执行。

这个过程确保了系统资源的安全性和程序的正确执行。通过这种方式，操作系统能够有效地管理资源，同时为应用程序提供必要的服务。

**内核缓冲与进程缓冲区**

缓冲区的目的，是为了减少频繁的系统IO调用。大家都知道，系统调用需要保存之前的进程数据和状态等信息，而结束调用之后回来还需要恢复之前的信息，即操作系统中的内核态（Kernel Mode）和用户态（User Mode）之间的转换，为了减少这种损耗时间、也损耗性能的系统调用，于是出现了缓冲区。

有了缓冲区，操作系统使用read函数把数据从内核缓冲区复制到进程缓冲区，write把数据从进程缓冲区复制到内核缓冲区中。等待缓冲区达到一定数量的时候，再进行IO的调用，提升性能。至于什么时候读取和存储则由内核来决定，用户程序不需要关心。

在linux系统中，系统内核也有个缓冲区叫做内核缓冲区。每个进程有自己独立的缓冲区，叫做进程缓冲区。

所以，用户程序的IO读写程序，大多数情况下，并没有进行实际的IO操作，而是在读写自己的进程缓冲区。

**java IO读写的底层流程**

用户程序进行IO的读写，基本上会用到系统调用read\&write，read把数据从内核缓冲区复制到进程缓冲区，write把数据从进程缓冲区复制到内核缓冲区，它们不等价于数据在内核缓冲区和磁盘之间的交换。

![v2-2d43966234dc1bedb3dee6cd94f7cbba\_r](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202406161703450.jpg)

首先看看一个典型Java 服务端处理网络请求的典型过程：

（1）客户端请求

Linux通过网卡，读取客户断的请求数据，将数据读取到内核缓冲区。

（2）获取请求数据

服务器从内核缓冲区读取数据到Java进程缓冲区。

（1）服务器端业务处理

Java服务端在自己的用户空间中，处理客户端的请求。

（2）服务器端返回数据

Java服务端已构建好的响应，从用户缓冲区写入系统缓冲区。

（3）发送给客户端

Linux内核通过网络 I/O ，将内核缓冲区中的数据，写入网卡，网卡通过底层的通讯协议，会将数据发送给目标客户端。

# 四种主要的IO模型

服务器端编程经常需要构造高性能的IO模型，常见的IO模型有四种：

（1）同步阻塞IO（Blocking IO）

首先，解释一下这里的阻塞与非阻塞：

阻塞IO，指的是需要内核IO操作彻底完成后，才返回到用户空间，执行用户的操作。阻塞指的是用户空间程序的执行状态，用户空间程序需等到IO操作彻底完成。传统的IO模型都是同步阻塞IO。在java中，默认创建的socket都是阻塞的。

其次，解释一下同步与异步：

同步IO，是一种用户空间与内核空间的调用发起方式。同步IO是指用户空间线程是主动发起IO请求的一方，内核空间是被动接受方。异步IO则反过来，是指内核kernel是主动发起IO请求的一方，用户线程是被动接受方。

（4）同步非阻塞IO（Non-blocking IO）

非阻塞IO，指的是用户程序不需要等待内核IO操作完成后，内核立即返回给用户一个状态值，用户空间无需等到内核的IO操作彻底完成，可以立即返回用户空间，执行用户的操作，处于非阻塞的状态。

简单的说：阻塞是指用户空间（调用线程）一直在等待，而且别的事情什么都不做；非阻塞是指用户空间（调用线程）拿到状态就返回，IO操作可以干就干，不可以干，就去干的事情。

非阻塞IO要求socket被设置为NONBLOCK。

强调一下，这里所说的NIO（同步非阻塞IO）模型，并非Java的NIO（New IO）库。

（3）IO多路复用（IO Multiplexing）

即经典的Reactor设计模式，有时也称为异步阻塞IO，Java中的Selector和Linux中的epoll都是这种模型。

（5）异步IO（Asynchronous IO）

异步IO，指的是用户空间与内核空间的调用方式反过来。用户空间线程是变成被动接受的，内核空间是主动调用者。

这一点，有点类似于Java中比较典型的模式是回调模式，用户空间线程向内核空间注册各种IO事件的回调函数，由内核去主动调用。

## 同步阻塞IO（Blocking IO）

在linux中的Java进程中，默认情况下所有的socket都是blocking IO。在阻塞式 I/O 模型中，应用程序在从IO系统调用开始，一直到到系统调用返回，这段时间是阻塞的。返回成功后，应用进程开始处理用户空间的缓存数据。

![img](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202406161705643.jpeg)

举个栗子，发起一个blocking socket的read读操作系统调用，流程大概是这样：

（1）当用户线程调用了read系统调用，内核（kernel）就开始了IO的第一个阶段：准备数据。很多时候，数据在一开始还没有到达（比如，还没有收到一个完整的Socket数据包），这个时候kernel就要等待足够的数据到来。

（2）当kernel一直等到数据准备好了，它就会将数据从kernel内核缓冲区，拷贝到用户缓冲区（用户内存），然后kernel返回结果。

（3）从开始IO读的read系统调用开始，用户线程就进入阻塞状态。一直到kernel返回结果后，用户线程才解除block的状态，重新运行起来。

所以，blocking IO的特点就是在内核进行IO执行的两个阶段，用户线程都被block了。

BIO的优点：

程序简单，在阻塞等待数据期间，用户线程挂起。用户线程基本不会占用 CPU 资源。

BIO的缺点：

一般情况下，会为每个连接配套一条独立的线程，或者说一条线程维护一个连接成功的IO流的读写。在并发量小的情况下，这个没有什么问题。但是，当在高并发的场景下，需要大量的线程来维护大量的网络连接，内存、线程切换开销会非常巨大。因此，基本上，BIO模型在高并发场景下是不可用的。

## 同步非阻塞NIO（None Blocking IO）

在linux系统下，可以通过设置socket使其变为non-blocking。NIO 模型中应用程序在一旦开始IO系统调用，会出现以下两种情况：

（1）在内核缓冲区没有数据的情况下，系统调用会立即返回，返回一个调用失败的信息。

（2）在内核缓冲区有数据的情况下，是阻塞的，直到数据从内核缓冲复制到用户进程缓冲。复制完成后，系统调用返回成功，应用进程开始处理用户空间的缓存数据。

![img](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202406161706092.jpeg)

举个栗子。发起一个non-blocking socket的read读操作系统调用，流程是这个样子：

（1）在内核数据没有准备好的阶段，用户线程发起IO请求时，立即返回。用户线程需要不断地发起IO系统调用。

（2）内核数据到达后，用户线程发起系统调用，用户线程阻塞。内核开始复制数据。它就会将数据从kernel内核缓冲区，拷贝到用户缓冲区（用户内存），然后kernel返回结果。

（3）用户线程才解除block的状态，重新运行起来。经过多次的尝试，用户线程终于真正读取到数据，继续执行。

NIO的特点：

应用程序的线程需要不断的进行 I/O 系统调用，轮询数据是否已经准备好，如果没有准备好，继续轮询，直到完成系统调用为止。

NIO的优点：每次发起的 IO 系统调用，在内核的等待数据过程中可以立即返回。用户线程不会阻塞，实时性较好。

NIO的缺点：需要不断的重复发起IO系统调用，这种不断的轮询，将会不断地询问内核，这将占用大量的 CPU 时间，系统资源利用率较低。

总之，NIO模型在高并发场景下，也是不可用的。一般 Web 服务器不使用这种 IO 模型。一般很少直接使用这种模型，而是在其他IO模型中使用非阻塞IO这一特性。java的实际开发中，也不会涉及这种IO模型。

再次说明，Java NIO（New IO） 不是IO模型中的NIO模型，而是另外的一种模型，叫做IO多路复用模型（ IO multiplexing ）。

## IO多路复用模型(I/O multiplexing）

如何避免同步非阻塞NIO模型中轮询等待的问题呢？这就是IO多路复用模型。

IO多路复用模型，就是通过一种新的系统调用，一个进程可以监视多个文件描述符，一旦某个描述符就绪（一般是内核缓冲区可读/可写），内核kernel能够通知程序进行相应的IO系统调用。

目前支持IO多路复用的系统调用，有 select，epoll等等。select系统调用，是目前几乎在所有的操作系统上都有支持，具有良好跨平台特性。epoll是在linux 2.6内核中提出的，是select系统调用的linux增强版本。

IO多路复用模型的基本原理就是select/epoll系统调用，单个线程不断的轮询select/epoll系统调用所负责的成百上千的socket连接，当某个或者某些socket网络连接有数据到达了，就返回这些可以读写的连接。因此，好处也就显而易见了——通过一次select/epoll系统调用，就查询到到可以读写的一个甚至是成百上千的网络连接。

IO多路复用模型是一种高效的网络编程技术，它允许单个线程或进程同时管理多个网络连接。这种模型的核心思想是**利用操作系统的内核机制**来监视多个socket连接的状态，当这些连接中有数据可读或可写时，操作系统会通知应用程序，从而避免了应用程序对每个socket进行轮询检查的低效做法。

select和epoll是两种常见的IO多路复用技术，它们在不同的操作系统中实现略有不同，但基本原理相似。

1.  **select系统调用**：
    *   select系统调用是较早的IO多路复用技术，它允许应用程序监视一组文件描述符（包括socket）的状态。
    *   应用程序调用select时，需要提供一个文件描述符集合，内核会检查这些文件描述符是否有事件发生（比如可读、可写或异常）。
    *   如果没有事件发生，select调用会阻塞，直到至少有一个文件描述符满足条件。
    *   当有事件发生时，select返回，应用程序可以遍历文件描述符集合，找出哪些描述符有事件发生，然后进行相应的处理。

2.  **epoll系统调用**：
    *   epoll是Linux特有的IO多路复用技术，相比select，它在处理大量文件描述符时更加高效。
    *   epoll使用一个事件表来管理所有被监视的文件描述符，应用程序可以添加、删除或修改事件表中的文件描述符。
    *   epoll有两种工作模式：水平触发（LT）和边缘触发（ET）。水平触发是默认模式，类似于select，只要有数据可读或可写，就会通知应用程序；边缘触发则只有在状态改变时才会通知。
    *   epoll通过内核事件通知机制，减少了不必要的系统调用和上下文切换，提高了性能。

**IO多路复用模型的好处**：

*   **提高效率**：通过单个线程或进程管理多个socket连接，减少了线程或进程的创建和上下文切换的开销。
*   **资源节省**：不需要为每个socket分配一个线程或进程，节省了系统资源。
*   **易于编程**：相比于多线程或多进程模型，IO多路复用模型的编程模型更加简单，易于理解和维护。

综上所述，IO多路复用模型通过select/epoll这样的系统调用，使得单个线程能够高效地管理多个网络连接，从而提高了网络服务的性能和可扩展性。

因此，IO多路复用模型是一种操作系统层面的优化。它利用了操作系统的内核提供的机制来提高网络编程的效率和性能

举个栗子。发起一个多路复用IO的的read读操作系统调用，流程是这个样子：

![img](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202406161706487.jpeg)

在这种模式中，首先不是进行read系统调动，而是进行select/epoll系统调用。当然，这里有一个前提，需要将目标网络连接，提前注册到select/epoll的可查询socket列表中。然后，才可以开启整个的IO多路复用模型的读流程。

（1）进行select/epoll系统调用，查询可以读的连接。kernel会查询所有select的可查询socket列表，当任何一个socket中的数据准备好了，select就会返回。

当用户进程调用了select，那么整个线程会被block（阻塞掉）。

（2）用户线程获得了目标连接后，发起read系统调用，用户线程阻塞。内核开始复制数据。它就会将数据从kernel内核缓冲区，拷贝到用户缓冲区（用户内存），然后kernel返回结果。

（3）用户线程才解除block的状态，用户线程终于真正读取到数据，继续执行。

多路复用IO的特点：

IO多路复用模型，建立在操作系统kernel内核能够提供的多路分离系统调用select/epoll基础之上的。多路复用IO需要用到两个系统调用（system call）， 一个select/epoll查询调用，一个是IO的读取调用。

和NIO模型相似，多路复用IO需要轮询。负责select/epoll查询调用的线程，需要不断的进行select/epoll轮询，查找出可以进行IO操作的连接。

另外，多路复用IO模型与前面的NIO模型，是有关系的。对于每一个可以查询的socket，一般都设置成为non-blocking模型。只是这一点，对于用户程序是透明的（不感知）。

多路复用IO的优点：

用select/epoll的优势在于，它可以同时处理成千上万个连接（connection）。与一条线程只绑定一个连接相比，I/O多路复用技术的最大优势是：系统不必创建线程，也不必维护这些线程，从而大大减小了系统的开销。

Java的NIO（new IO）技术，使用的就是IO多路复用模型。在linux系统上，使用的是epoll系统调用。

多路复用IO的缺点：

本质上，select/epoll系统调用，属于同步IO，也是阻塞IO。都需要在读写事件就绪后，自己负责进行读写，也就是说这个读写过程是阻塞的。

既在同步IO模型中，包括select和epoll在内的IO多路复用技术，虽然可以同时监控多个socket，但当某个socket上的数据就绪时，应用程序仍需要自己负责将数据从内核空间的缓冲区复制到用户空间的缓冲区。这个过程是阻塞的，意味着应用程序在数据复制期间无法执行其他任务。

如何充分的解除线程的阻塞呢？那就是异步IO模型。

## 异步IO模型（asynchronous IO）

如何进一步提升效率，解除最后一点阻塞呢？这就是异步IO模型，全称asynchronous I/O，简称为AIO。

AIO的基本流程是：用户线程通过系统调用，告知kernel内核启动某个IO操作，用户线程返回。kernel内核在整个IO操作（包括数据准备、数据复制）完成后，通知用户程序，用户执行后续的业务操作。

kernel的数据准备是将数据从网络物理设备（网卡）读取到内核缓冲区；kernel的数据复制是将数据从内核缓冲区拷贝到用户程序空间的缓冲区。

![img](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202406161707336.jpeg)

（1）当用户线程调用了read系统调用，立刻就可以开始去做其它的事，用户线程不阻塞。

（2）内核（kernel）就开始了IO的第一个阶段：准备数据。当kernel一直等到数据准备好了，它就会将数据从kernel内核缓冲区，拷贝到用户缓冲区（用户内存）。

（3）kernel会给用户线程发送一个信号（signal），或者回调用户线程注册的回调接口，告诉用户线程read操作完成了。

（4）用户线程读取用户缓冲区的数据，完成后续的业务操作。

异步IO模型的特点：

在内核kernel的等待数据和复制数据的两个阶段，用户线程都不是block(阻塞)的。用户线程需要接受kernel的IO操作完成的事件，或者说注册IO操作完成的回调函数，到操作系统的内核。所以说，异步IO有的时候，也叫做信号驱动 IO 。

异步IO模型缺点：

需要完成事件的注册与传递，这里边需要底层操作系统提供大量的支持，去做大量的工作。

目前来说， Windows 系统下通过 IOCP 实现了真正的异步 I/O。但是，就目前的业界形式来说，Windows 系统，很少作为百万级以上或者说高并发应用的服务器操作系统来使用。

而在 Linux 系统下，异步IO模型在2.6版本才引入，目前并不完善。所以，这也是在 Linux 下，实现高并发网络编程时都是以 IO 复用模型模式为主。

四种IO模型，理论上越往后，阻塞越少，效率也是最优。在这四种 I/O 模型中，前三种属于同步 I/O，因为其中真正的 I/O 操作将阻塞线程。只有最后一种，才是真正的异步 I/O 模型，可惜目前Linux 操作系统尚欠完善。

# 四种模型在 Java 中的实现

## java BIO，NIO，AIO 的区别与实际应用场景分析

[JAVA中BIO、NIO、AIO的分析理解-阿里云开发者社区 (aliyun.com)](https://developer.aliyun.com/article/726698)

[Java编程中的IO模型详解：BIO，NIO，AIO的区别与实际应用场景分析\_java nio bio aio-CSDN博客](https://blog.csdn.net/oWuChenHua/article/details/135394686)

[从理论到实践：深度解读BIO、NIO、AIO的优缺点及使用场景-腾讯云开发者社区-腾讯云 (tencent.com)](https://cloud.tencent.com/developer/article/2337352)

Java中的BIO、NIO和AIO是三种不同的I/O模型，它们在处理网络通信和文件I/O时有着不同的特点和适用场景。

**BIO (Blocking I/O)**

BIO是传统的同步阻塞I/O模型。在这种模型中，当一个客户端连接请求到来时，服务器会为每个客户端创建一个新的线程来处理请求。如果客户端数量很多，服务器将需要大量的线程来处理这些请求，这会导致资源消耗过大，特别是在高并发场景下，服务器的性能会急剧下降。

**应用场景**：

*   适用于连接数不多的场景。
*   适用于简单的客户端-服务器模型，如简单的聊天服务器。

**NIO (Non-blocking I/O)**

NIO是Java 1.4引入的新的I/O模型，它基于事件驱动，使用选择器（Selector）来实现非阻塞I/O。NIO允许一个线程管理多个网络连接，提高了线程的使用效率。NIO适用于需要处理大量连接的场景，如Web服务器、文件服务器等。

**应用场景**：

*   适用于高并发的网络应用，如Web服务器、文件服务器等。
*   适用于需要同时处理多个客户端请求的场景。

**AIO (Asynchronous I/O)**

AIO是Java 7引入的异步非阻塞I/O模型。AIO在NIO的基础上进一步发展，实现了真正的异步I/O操作。在AIO中，当一个I/O操作开始后，线程可以继续执行其他任务，I/O操作完成后，操作系统会通知线程处理结果。AIO适用于需要处理大量I/O操作的场景，如大数据处理、高性能服务器等。

**应用场景**：

*   适用于需要处理大量I/O操作的场景，如大数据处理、高性能服务器等。
*   适用于需要高吞吐量和低延迟的应用。

总结

*   **BIO**：适用于连接数不多的场景，简单易用，但不适合高并发。
*   **NIO**：适用于高并发场景，提高了线程的使用效率，但编程模型相对复杂。
*   **AIO**：适用于需要处理大量I/O操作的场景，提供了更高的性能和吞吐量，但支持的平台和库较少。

在实际应用中，选择哪种I/O模型取决于具体的应用场景和性能需求。对于大多数Web应用和网络服务，NIO是一个很好的选择，因为它提供了较好的性能和可扩展性。对于需要处理大量I/O操作的应用，AIO可能是一个更好的选择。而BIO在一些简单的场景下仍然适用，尤其是在连接数不多的情况下。

## 零拷贝技术

零拷贝（Zero-Copy）技术是一种减少数据在用户空间和内核空间之间复制次数的技术，从而提高数据传输效率的方法。在传统的数据传输过程中，数据需要从磁盘读取到内核空间的缓冲区，然后从内核空间复制到用户空间的缓冲区，最后再从用户空间复制到发送缓冲区或从接收缓冲区复制到用户空间。零拷贝技术通过减少这些不必要的数据复制，可以显著提高数据传输的性能。

例如，在应用程序中想要复制一个文件或向外部发送一个文件，一般情况下，应用程序需要首先向操作系统发出读取文件请求，操作系统收到请求后开始准备数据，数据准备好后通知应用程序可以读取，应用程序发起请求，将数据从内核缓冲区读取到用户缓冲区，然后再复制到指定位置或发送给网卡，发送过程仍需要将数据重新写出到内核缓冲区，然后才能真正发送数据，零拷贝（Zero-Copy）技术就是告诉操作系统，直接将数据复制到指定位置或发送给网卡，不需要给应用程序，跳过中间的过程的一种操作系统级别的优化方式。

**原理**

零拷贝技术的核心思想是减少或避免数据在内核空间和用户空间之间的复制，以及减少CPU的参与。以下是几种常见的零拷贝技术：

1.  **直接I/O**：应用程序直接访问存储设备，绕过内核缓冲区，从而减少一次数据复制。

2.  **内存映射（mmap）**：通过将文件映射到用户空间的内存地址，应用程序可以直接读写文件，避免了数据在内核空间和用户空间之间的复制。

3.  **sendfile**：在Linux系统中，sendfile系统调用可以将数据从一个文件描述符直接传输到另一个文件描述符，减少了一次数据复制。

4.  **DMA（Direct Memory Access）**：直接内存访问允许硬件设备直接读写系统内存，而不需要CPU的介入，从而减少了CPU的负担。

5.  **分散/聚集I/O（Scatter/Gather I/O）**：允许数据从多个缓冲区读取或写入到一个连续的缓冲区，减少了数据的复制次数。

**Java 中的实现**

在Java中，零拷贝技术可以通过以下几种方式实现：

1.  **FileChannel.transferTo() 和 transferFrom()**：这两个方法可以将数据从一个Channel传输到另一个Channel，或者从Channel传输到文件。在某些操作系统上，这些方法可以利用零拷贝技术，减少数据复制。

2.  **NIO的ByteBuffer**：使用ByteBuffer的allocateDirect()方法可以创建一个直接缓冲区，直接缓冲区的数据直接位于物理内存中，可以被操作系统直接访问，从而减少数据在用户空间和内核空间之间的复制。

3.  **Java 7引入的FileChannel.map()**：这个方法可以将文件的一部分映射到内存中，实现内存映射文件，从而实现零拷贝。

4.  **Java 9引入的AIO（Asynchronous I/O）**：虽然AIO本身不直接提供零拷贝技术，但其异步特性可以减少线程的阻塞等待，从而提高系统的整体性能。

需要注意的是，零拷贝技术的实现和效果依赖于底层操作系统和硬件的支持。在某些情况下，零拷贝技术可能无法完全避免数据复制，但仍然可以显著减少数据复制的次数和CPU的参与，从而提高数据传输的效率。

示例代码

### 示例 1: 使用 FileChannel.transferTo()

这个例子演示了如何使用`transferTo()`方法将数据从一个文件传输到另一个文件。

```java
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class TransferExample {
    public static void main(String[] args) {
        try (FileInputStream fis = new FileInputStream("source.txt");
             FileOutputStream fos = new FileOutputStream("destination.txt");
             FileChannel sourceChannel = fis.getChannel();
             FileChannel destinationChannel = fos.getChannel()) {
            
            long position = 0;
            long count = sourceChannel.size();
            
            sourceChannel.transferTo(position, count, destinationChannel);
            System.out.println("Data transfer completed.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

### 示例 2: 使用 ByteBuffer.allocateDirect()

这个例子演示了如何使用直接缓冲区来读取文件内容。

```java
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class DirectBufferExample {
    public static void main(String[] args) {
        try (FileInputStream fis = new FileInputStream("example.txt");
             FileChannel channel = fis.getChannel()) {
            
            ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
            int bytesRead;
            
            while ((bytesRead = channel.read(buffer)) != -1) {
                buffer.flip();
                while (buffer.hasRemaining()) {
                    System.out.print((char) buffer.get());
                }
                buffer.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

### 示例 3: 使用 FileChannel.map()

这个例子演示了如何使用`map()`方法将文件内容映射到内存中。

```java
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class MemoryMappedFileExample {
    public static void main(String[] args) {
        try (RandomAccessFile aFile = new RandomAccessFile("largefile.bin", "rw")) {
            FileChannel inChannel = aFile.getChannel();
            long start = 0;
            long size = inChannel.size();
            
            // 将文件的一部分映射到内存中
            MappedByteBuffer buffer = inChannel.map(FileChannel.MapMode.READ_ONLY, start, size);
            
            // 读取并打印映射的内存区域内容
            for (int i = 0; i < buffer.limit(); i++) {
                System.out.print((char) buffer.get());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

### 示例 4: 使用 Java 9 AIO

这个例子演示了如何使用Java 9引入的异步文件通道进行异步读操作。

```java
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Future;

public class AsyncFileChannelExample {
    public static void main(String[] args) {
        Path path = Paths.get("asyncfile.txt");
        try (AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.READ)) {
            
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            Future<Integer> operation = fileChannel.read(buffer, 0);
            
            // 等待异步操作完成
            int bytesRead = operation.get();
            buffer.flip();
            
            while (buffer.hasRemaining()) {
                System.out.print((char) buffer.get());
            }
            System.out.println("\nRead " + bytesRead + " bytes.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

请注意，这些示例代码仅用于演示目的，实际应用中可能需要更复杂的错误处理和资源管理。在使用这些技术时，确保你的环境支持这些操作，特别是零拷贝技术，因为它们依赖于底层操作系统的支持。

### 注意

在Windows平台上，`FileChannel.transferTo()` 和 `transferFrom()` 方法的传输大小可能会受到限制，这主要是由于Windows的本地I/O系统调用的限制。在某些版本的Windows系统中，确实存在一个限制，即单次传输操作不能超过8MB（有时是4MB，具体取决于Windows的版本和配置）。超过这个限制的传输可能会被操作系统分割成多个较小的传输操作。而 Linux 则没有此限制。

这种限制源于Windows的内部缓冲机制和I/O完成端口的限制。当尝试进行超过限制的传输时，操作系统可能会将大块的传输拆分成多个较小的传输，这可能导致性能下降，因为需要更多的系统调用和上下文切换。

解决方案和注意事项

1. **分块传输**：如果需要传输大量数据，可以将大块数据分成多个小于限制大小的块进行传输。这需要在应用层手动实现分块逻辑。

2. **检查系统限制**：可以通过编程方式检查当前系统对`transferTo()`和`transferFrom()`操作的限制。虽然Java标准库没有直接提供这样的API，但可以通过执行一些测试传输来间接确定限制。

3. **使用其他方法**：如果`FileChannel`的`transferTo()`和`transferFrom()`方法的限制对你的应用影响较大，可以考虑使用其他方法，如使用`allocateDirect()`创建的直接缓冲区进行数据传输，或者使用`FileChannel.map()`进行内存映射。

4. **操作系统更新**：有时候，操作系统更新可能会改变这些限制。如果你正在使用较旧的Windows版本，考虑升级到最新版本可能会解决一些限制问题。

示例代码片段（分块传输）

```java
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class TransferChunkedExample {
    public static void main(String[] args) {
        try (FileInputStream fis = new FileInputStream("source.txt");
             FileOutputStream fos = new FileOutputStream("destination.txt");
             FileChannel sourceChannel = fis.getChannel();
             FileChannel destinationChannel = fos.getChannel()) {
            
            long totalSize = sourceChannel.size();
            long position = 0;
            long maxChunkSize = 8 * 1024 * 1024; // 8MB

            while (position < totalSize) {
                long remaining = totalSize - position;
                long chunkSize = Math.min(remaining, maxChunkSize);
                position += sourceChannel.transferTo(position, chunkSize, destinationChannel);
            }
            System.out.println("Data transfer completed.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

在上述代码中，我们通过循环将大文件分成多个8MB的块进行传输。每次循环调用`transferTo()`时，都会传输文件的一部分，直到整个文件传输完成。

请记住，这些限制和解决方案可能因不同的Windows版本和配置而异，因此在实施之前最好进行适当的测试和验证。

# Java NIO

Netty 基于 Java NIO 开发

## Channel

首先说一下Channel，国内大多翻译成“通道”。Channel和IO中的Stream（流）是差不多一个等级的。只不过Stream是单向的，譬如：InputStream， OutputStream，而Channel是双向的，既可以用来进行读操作，又可以用来进行写操作。NIO中的Channel的主要实现有：

1.  FileChannel
2.  DatagramChannel
3.  SocketChannel
4.  ServerSocketChannel

这里看名字就可以猜出个所以然来：分别可以对应文件IO、UDP和TCP（Server和Client）。

### FileChannel

`FileChannel` 是 Java NIO 中用于文件读写操作的一个类。它提供了对文件的访问和操作能力，允许你以更灵活的方式读取和写入数据。`FileChannel` 与传统的 `FileInputStream` 和 `FileOutputStream` 不同，它支持直接与文件系统交互，而不需要通过中间的缓冲区。

**主要特点**

1. **文件读写**：`FileChannel` 可以直接从文件中读取数据到内存中的 `ByteBuffer`，或者将 `ByteBuffer` 中的数据写入到文件中。

2. **内存映射文件**：`FileChannel` 支持内存映射文件（memory-mapped files），这允许将文件的一部分或全部映射到内存地址空间，从而实现对文件的快速访问和修改。

3. **文件锁定**：`FileChannel` 提供了文件锁定机制，可以用来同步访问共享文件，防止多个进程或线程同时修改文件导致的数据不一致问题。

4. **高效传输**：与基于缓冲区的 I/O 相比，`FileChannel` 可以实现更高效的文件传输，尤其是在处理大文件时。

**使用示例**

下面是一个简单的 `FileChannel` 使用示例，演示如何读取和写入文件：

```java
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.ByteBuffer;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FileChannelExample {
    public static void main(String[] args) throws Exception {
        // 创建 RandomAccessFile 对象
        RandomAccessFile aFile = new RandomAccessFile("example.txt", "rw");

        // 获取 FileChannel
        FileChannel inChannel = aFile.getChannel();

        // 创建一个 Buffer
        ByteBuffer buf = ByteBuffer.allocate(48);

        // 从 FileChannel 读取数据到 Buffer
        int bytesRead = inChannel.read(buf);
        while (bytesRead != -1) {
            buf.flip();
            while(buf.hasRemaining()){
                System.out.print((char) buf.get());
            }
            buf.clear();
            bytesRead = inChannel.read(buf);
        }

        // 关闭 FileChannel
        inChannel.close();

        // 写入数据到文件
        RandomAccessFile anotherFile = new RandomAccessFile("example.txt", "rw");
        FileChannel outChannel = anotherFile.getChannel();

        // 准备要写入的数据
        String newData = "New String to write to file..." + System.currentTimeMillis();
        buf = ByteBuffer.allocate(newData.getBytes().length);
        buf.put(newData.getBytes());
        buf.flip();

        // 将 Buffer 中的数据写入 FileChannel
        outChannel.write(buf);
        outChannel.force(true); // 强制将更改写入文件

        // 关闭 FileChannel
        outChannel.close();
    }
}
```

在这个例子中，我们首先使用 `RandomAccessFile` 打开一个文件，并获取其 `FileChannel`。然后，我们从该文件读取数据到 `ByteBuffer` 中，并打印出来。之后，我们创建另一个 `RandomAccessFile` 对象，获取其 `FileChannel`，并写入新的字符串数据到文件中。

**总结**

`FileChannel` 是 Java NIO 中用于文件操作的一个强大工具，它提供了直接与文件系统交互的能力，特别适合于处理大文件和需要高效数据传输的场景。通过使用 `FileChannel`，可以实现更高效的文件读写操作，同时支持内存映射和文件锁定等高级功能。

### DatagramChannel

`DatagramChannel` 是 Java NIO 中的一个类，用于实现基于数据报（datagram）的无连接网络通信。与基于流的 `SocketChannel` 和 `ServerSocketChannel` 不同，`DatagramChannel` 提供了一种使用 UDP 协议进行数据传输的方式。

**UDP 协议和 `DatagramChannel`**

UDP（User Datagram Protocol）是一种无连接的网络协议，它允许数据包在网络中独立传输。与 TCP（传输控制协议）不同，UDP 不保证数据包的顺序、可靠性或完整性。然而，由于其无连接的特性，UDP 在某些情况下可以提供比 TCP 更高的性能和更低的延迟。

**`DatagramChannel` 的作用**

1. **发送和接收数据报**：`DatagramChannel` 可以发送和接收数据报，数据报是独立的、自包含的消息，不需要建立连接即可发送。

2. **非阻塞模式**：`DatagramChannel` 支持非阻塞模式，这意味着读写操作不会阻塞线程，而是根据可用数据或通道状态立即返回。

3. **多播支持**：`DatagramChannel` 支持多播（multicast），允许将数据报发送给一组特定的主机（多播组），这在某些网络应用（如视频会议、在线游戏）中非常有用。

**使用示例**

下面是一个简单的 `DatagramChannel` 使用示例，演示如何发送和接收数据报：

```java
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class DatagramChannelExample {
    public static void main(String[] args) throws Exception {
        // 创建 DatagramChannel
        DatagramChannel datagramChannel = DatagramChannel.open();
        datagramChannel.bind(null); // 绑定到任意可用端口

        // 准备要发送的数据
        String message = "Hello, UDP!";
        ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());

        // 发送数据报
        InetAddress address = InetAddress.getByName("localhost");
        int port = 9876;
        datagramChannel.send(buffer, new InetSocketAddress(address, port));

        // 接收数据报
        buffer.clear();
        datagramChannel.receive(buffer);
        buffer.flip();
        while (buffer.hasRemaining()) {
            System.out.print((char) buffer.get());
        }

        // 关闭 DatagramChannel
        datagramChannel.close();
    }
}
```

在这个例子中，我们创建了一个 `DatagramChannel`，向本地主机的 9876 端口发送了一个简单的消息，然后接收返回的数据报。

**总结**

`DatagramChannel` 提供了一种使用 UDP 协议进行网络通信的方式，适用于对性能和延迟要求较高，且可以容忍数据丢失的应用场景。由于 UDP 的无连接特性，`DatagramChannel` 在实现某些类型的网络应用时可以提供比 TCP 更灵活的选择。

### ServerSocketChannel 和 SocketChannel

`SocketChannel` 和 `ServerSocketChannel` 是 Java NIO 中用于网络通信的两个核心类，它们分别代表了客户端和服务器端的网络连接。它们之间的关系类似于传统阻塞式 I/O 中的 `Socket` 和 `ServerSocket`，但提供了非阻塞模式和更灵活的 I/O 操作。

**ServerSocketChannel**

`ServerSocketChannel` 是一个可以监听传入连接请求的通道。它类似于传统的 `ServerSocket`，但提供了非阻塞模式的能力。

- **监听端口**：通过调用 `bind()` 方法，`ServerSocketChannel` 可以绑定到一个端口上，开始监听连接请求。
- **接受连接**：使用 `accept()` 方法接受新的连接请求。在非阻塞模式下，如果没有新的连接请求，`accept()` 方法会立即返回 `null`。
- **读写数据**：一旦连接被接受，就可以通过返回的 `SocketChannel` 对象进行读写操作。

**SocketChannel**

`SocketChannel` 代表了一个到远程服务器的连接。它类似于传统的 `Socket`，但同样提供了非阻塞模式的能力。

- **建立连接**：通过 `connect()` 方法连接到远程服务器。在非阻塞模式下，如果立即连接不上，`connect()` 方法会返回 `false`，之后需要检查连接是否完成。
- **读写数据**：连接建立后，可以使用 `read()` 和 `write()` 方法从通道读取数据或向通道写入数据。
- **非阻塞模式**：`SocketChannel` 可以设置为非阻塞模式，在这种模式下，读写操作不会阻塞线程，而是根据可用数据或通道状态立即返回。

关系和区别

- **连接与监听**：`ServerSocketChannel` 用于监听端口并接受新的连接请求，而 `SocketChannel` 用于建立到远程服务器的连接。
- **非阻塞模式**：两者都支持非阻塞模式，这使得它们可以用于构建高性能的网络应用，特别是在需要处理大量连接时。
- **读写操作**：一旦 `ServerSocketChannel` 接受了一个连接，它会返回一个 `SocketChannel` 对象，之后就可以使用这个对象进行数据的读写操作。

**示例**

下面是一个简单的示例，展示如何使用 `ServerSocketChannel` 和 `SocketChannel`：

```java
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.ByteBuffer;

public class NioServer {
    public static void main(String[] args) throws Exception {
        // 创建 ServerSocketChannel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(8080));

        while (true) {
            // 接受连接
            SocketChannel socketChannel = serverSocketChannel.accept();
            if (socketChannel != null) {
                // 读写数据
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                int bytesRead = socketChannel.read(buffer);
                if (bytesRead > 0) {
                    buffer.flip();
                    socketChannel.write(buffer);
                    buffer.clear();
                }
            }
        }
    }
}
```

在这个例子中，服务器监听8080端口，接受连接，并读取数据后立即回写。注意，实际应用中可能需要处理非阻塞模式下的各种状态和异常情况。

## Buffer

Buffer，故名思意，缓冲区，实际上是一个容器，是一个连续数组。 Channel 提供从文件、网络读取数据的渠道，但是读取或写入的数据都必须经由 Buffer

上面的图描述了从一个客户端向服务端发送数据，然后服务端接收数据的过程。客户端发送数据时，必须先将数据存入 Buffer 中，然后将
Buffer 中的内容写入通道。服务端这边接收数据必须通过 Channel 将数据读入到 Buffer 中，然后再从 Buffer 中取出数据来处理。

在 NIO 中， Buffer 是一个顶层父类，它是一个抽象类，常用的 Buffer 的子类有：ByteBuffer、 IntBuffer、 CharBuffer、 LongBuffer、
DoubleBuffer、 FloatBuffer、ShortBuffer 

Java NIO 中的 Buffer 是一个容器对象，用于包含特定类型的数据。在进行读写操作时，数据会被读入或写入 Buffer。下面是一个简单的示例，演示如何使用 Java NIO 中的 `ByteBuffer` 来进行基本的读写操作：

```java
import java.nio.ByteBuffer;

public class BufferDemo {
    public static void main(String[] args) {
        // 创建一个容量为 5 的 ByteBuffer
        ByteBuffer buffer = ByteBuffer.allocate(5);

        // 向 Buffer 写入数据
        String str = "Hello";
        buffer.put(str.getBytes());

        // 重置 Buffer，准备读取数据
        buffer.flip();

        // 读取 Buffer 中的数据
        while (buffer.hasRemaining()) {
            // 获取一个字节的数据
            byte b = buffer.get();
            System.out.print((char) b);
        }

        // 输出结果应该是 "Hello"
    }
}
```

解释步骤：

1. **创建 Buffer**：使用 `ByteBuffer.allocate(5)` 创建一个容量为 5 的 `ByteBuffer`。这个容量指的是 Buffer 可以存储的字节数。

2. **写入数据**：通过 `put` 方法将字符串 "Hello" 的字节数据写入 Buffer。`getBytes()` 方法将字符串转换为字节数组。

3. **准备读取**：调用 `flip()` 方法将 Buffer 从写模式切换到读模式。`flip()` 方法会重置位置（position）到 0，并设置限制（limit）到当前位置。

4. **读取数据**：使用 `hasRemaining()` 检查 Buffer 是否还有剩余数据可读。通过循环调用 `get()` 方法逐个读取字节，并将其转换为字符打印出来。

5. **输出结果**：上述代码执行后，控制台输出 "Hello"。

注意事项：

- **容量（Capacity）**：Buffer 的最大容量，一旦创建不能改变。
- **位置（Position）**：下一个要读或写的元素的索引，初始为 0。
- **限制（Limit）**：在写模式下，表示 Buffer 中最多可以写入的元素数量；在读模式下，表示最多可以读取的元素数量。
- **flip()**：用于从写模式切换到读模式，重置位置到 0，并将限制设置为之前的写入位置。
- **rewind()**：重置位置到 0，但不改变限制，常用于重新读取数据。
- **clear()** 和 **compact()**：用于准备 Buffer 进行下一轮写入。`clear()` 会重置位置到 0，限制到容量；`compact()` 会保留未读取的数据，将未读取的数据移动到 Buffer 的开始位置，然后重置位置。

这个例子展示了 Buffer 的基本使用方法，实际应用中可能涉及更复杂的操作，如直接和间接 Buffer、分散和聚集 I/O 等。

### 内部结构

Java NIO 中的 `Buffer` 是一个用于处理数据的容器，其内部结构设计为高效地在用户空间和内核空间之间传输数据。`Buffer` 的核心属性包括：

1. **容量（Capacity）**：Buffer 能够存储的最大数据量，一旦创建，容量不可更改。

2. **位置（Position）**：下一个要读或写的元素的索引位置。读操作时，位置会从缓冲区的开始移动到末尾；写操作时，位置会从开始移动到末尾。

3. **限制（Limit）**：
   - 在写模式下，限制表示最多可以写入多少数据到 Buffer 中，即 `position` 的上限。
   - 在读模式下，限制表示最多可以从 Buffer 中读取多少数据，即 `position` 的上限。

4. **标记（Mark）**：一个可选的位置值，用于之后的重置操作。调用 `mark()` 方法可以标记当前的位置，之后可以通过 `reset()` 方法将位置重置到标记的位置。

具体操作

- **写入数据到 Buffer**：写入数据时，`position` 从 0 开始，每次写入后递增，直到达到 `limit`。如果尝试写入超过 `limit` 的数据，会抛出 `BufferOverflowException`。

- **从 Buffer 读取数据**：读取数据前，需要调用 `flip()` 方法将 Buffer 从写模式切换到读模式。这会将 `position` 重置为 0，并将 `limit` 设置为之前的 `position` 值。然后，你可以从 Buffer 中读取数据，直到 `position` 达到 `limit`。

- **清空 Buffer**：调用 `clear()` 方法会重置 `position` 到 0，并将 `limit` 设置为 `capacity`，但不会清除 Buffer 中的数据。这使得 Buffer 可以重新用于写入数据。

- **准备下一次写入**：调用 `compact()` 方法会将未读取的数据移动到 Buffer 的开始位置，然后 `position` 会被设置为未读数据的末尾，`limit` 设置为 `capacity`。这适用于读取部分数据后，需要继续写入数据的场景。

示例

以 `ByteBuffer` 为例，下面是一个简单的操作流程：

```java
ByteBuffer buffer = ByteBuffer.allocate(1024); // 创建一个容量为 1024 的 ByteBuffer

buffer.put("Hello".getBytes()); // 写入数据
buffer.flip(); // 切换到读模式

while(buffer.hasRemaining()) {
    char c = (char) buffer.get(); // 读取数据
    System.out.print(c);
}

buffer.clear(); // 清空 Buffer，准备下一次写入
```

总结

Buffer 的内部结构和操作机制设计得非常灵活，允许高效地在不同模式（读和写）之间切换，并且可以处理各种大小的数据。理解 Buffer 的内部结构对于编写高效且正确的 NIO 代码至关重要。

### DirectByteBuffer

`DirectByteBuffer` 是 Java NIO（New Input/Output）包中的一个类，它用于在Java堆外分配内存，即直接内存（Direct Memory）。直接内存不是由JVM管理的堆内存，而是直接由操作系统管理的内存区域。这种内存分配方式可以提高某些操作的性能，尤其是对于那些需要大量数据传输的场景，如网络通信和文件I/O操作。

使用`DirectByteBuffer`可以减少数据在用户空间和内核空间之间的复制次数，从而提高数据传输的效率。`DirectByteBuffer`是Java NIO（New I/O）包中的一部分，它允许Java程序直接操作堆外内存（即直接内存），而不是堆内存。这种直接内存通常由操作系统管理，可以被内核直接访问。

**DirectByteBuffer的工作原理**

1. **直接内存分配**：`DirectByteBuffer`在创建时会分配一块直接内存，这块内存是操作系统管理的，不与Java堆内存直接关联。

2. **内核空间访问**：由于直接内存是操作系统管理的，内核可以直接访问这块内存，无需通过用户空间的缓冲区。

3. **数据传输**：当进行I/O操作时，如读写文件或网络通信，数据可以直接在内核空间和直接内存之间传输，绕过了用户空间的缓冲区。

**减少数据复制次数的原理**

`DirectByteBuffer`减少数据复制次数的原理与内存映射文件的原理不同。内存映射文件是将文件内容映射到用户空间的虚拟内存地址，使得文件数据看起来像是进程的内存。而`DirectByteBuffer`则是直接操作堆外的内存，这块内存是直接由操作系统管理的，可以被内核直接访问。

在使用`DirectByteBuffer`进行I/O操作时，数据传输的路径通常是：

1. **内核空间到直接内存**：当从磁盘读取数据时，内核直接将数据写入到直接内存中，而不是先写入到用户空间的缓冲区。

2. **直接内存到内核空间**：当向磁盘写入数据时，直接内存中的数据直接被内核读取并写入磁盘，无需先复制到用户空间的缓冲区。

**与内存映射文件的区别**

- **内存映射文件**：通过映射文件到用户空间的虚拟内存，使得文件数据看起来像是进程的内存。数据的读写操作仍然需要通过用户空间的内存地址，但操作系统会负责在内核空间和用户空间之间同步数据。

- **DirectByteBuffer**：直接操作堆外的内存，绕过了用户空间的缓冲区。数据的读写操作直接在内核空间和直接内存之间进行，减少了数据复制的次数。

**注意事项**

使用`DirectByteBuffer`时需要注意：

- **内存管理**：直接内存需要手动管理，使用完毕后需要通过`DirectByteBuffer`的`cleaner`机制或`System.gc()`来释放内存。

- **内存泄漏**：如果忘记释放直接内存，可能会导致内存泄漏。

- **性能开销**：虽然减少了数据复制，但直接内存的分配和管理可能会有额外的性能开销。

`DirectByteBuffer`提供了一种高效的数据传输方式，特别适合于需要大量数据传输和高性能I/O操作的应用场景。然而，它也要求开发者对内存管理有更深入的理解和控制。

### MappedByteBuffer

`MappedByteBuffer` 是 Java NIO 中 `FileChannel` 类的一个特殊类型的 `ByteBuffer`，它允许将文件的一部分或全部映射到内存地址空间。通过内存映射文件，可以实现对文件内容的快速访问和修改，而无需使用传统的读写方法。这种方式特别适合于处理大型文件或需要频繁访问文件数据的应用程序。MappedByteBuffer 是一个接口，实际的实现类就是上文的 DirectByteBuffer。

**特点**

1. **内存映射**：`MappedByteBuffer` 通过内存映射的方式，将文件的一部分或全部映射到内存中。这意味着对 `MappedByteBuffer` 的操作实际上是对内存的直接操作，而这些更改会反映到映射的文件上。

2. **直接内存访问**：由于文件内容直接映射到内存，因此可以实现非常快速的数据访问，无需通过中间缓冲区。

3. **垃圾回收管理**：`MappedByteBuffer` 由垃圾回收器管理，不需要手动释放资源，当映射的 `ByteBuffer` 不再被使用时，它所占用的资源会自动被回收。

**创建 MappedByteBuffer**

要创建一个 `MappedByteBuffer`，你需要先通过 `FileChannel` 映射文件的一部分或全部到内存中。下面是一个简单的示例：

```java
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class MappedByteBufferExample {
    public static void main(String[] args) throws Exception {
        // 打开文件并获取 FileChannel
        RandomAccessFile aFile = new RandomAccessFile("example.txt", "rw");
        FileChannel channel = aFile.getChannel();

        // 将文件的一部分映射到内存中
        long start = 0; //开始位置
        long size = channel.size(); // 映射的文件大小，不是索引位置
        MappedByteBuffer mbb = channel.map(FileChannel.MapMode.READ_WRITE, start, size);

        // 现在可以对 mbb 进行读写操作，这些操作会直接反映到文件上
        mbb.putChar(0, 'A'); // 修改文件的第一个字符

        // 关闭 FileChannel
        channel.close();
    }
}
```

在这个例子中，我们首先通过 `RandomAccessFile` 获取 `FileChannel`，然后使用 `map` 方法将整个文件映射到内存中。之后，我们直接对 `MappedByteBuffer` 进行操作，修改文件的第一个字符。

**注意事项**

- **文件大小限制**：映射的文件大小受限于操作系统的限制。在某些系统上，映射的文件大小不能超过 2GB。
- **资源管理**：虽然 `MappedByteBuffer` 由垃圾回收器管理，但映射的文件资源在不再需要时应通过关闭 `FileChannel` 来释放。
- **异常处理**：在处理文件映射时，应妥善处理可能发生的异常，如 `IOException`。

`MappedByteBuffer` 提供了一种高效处理文件数据的方式，尤其适用于需要频繁读写大文件的应用程序。通过内存映射，可以显著提高文件操作的性能。

### 缓冲区批量操作

在 Java NIO 中，"Scattering" 和 "Gathering" 是两个与数据传输相关的重要概念，它们描述了如何高效地处理多个数据缓冲区（`ByteBuffer`）的读写操作。这两个术语通常用于描述 `SocketChannel` 和 `DatagramChannel` 的操作，但也可以适用于其他类型的通道。

**Scattering**

Scattering 读取操作指的是将通道中的数据读入多个缓冲区。这在处理不同类型的数据时非常有用，比如从网络连接中读取一个包含头部信息和数据体的完整消息。使用 Scattering 读取，可以将消息的不同部分分别读入不同的缓冲区。

**示例**

```java
ByteBuffer header = ByteBuffer.allocate(128);
ByteBuffer body = ByteBuffer.allocate(1024);

// 创建一个缓冲区数组
ByteBuffer[] buffers = { header, body };

// 从通道读取数据到缓冲区数组
channel.read(buffers);
```

在这个例子中，数据首先被读入 `header` 缓冲区，当 `header` 满了之后，剩余的数据会被读入 `body` 缓冲区。

**Gathering**

Gathering 写入操作与 Scattering 相反，指的是将多个缓冲区中的数据写入到一个通道中。这在需要将不同类型的数据组合成一个单一消息并发送时非常有用。

**示例**

```java
ByteBuffer header = ByteBuffer.wrap("Header ".getBytes());
ByteBuffer body = ByteBuffer.wrap("Body data".getBytes());

// 创建一个缓冲区数组
ByteBuffer[] buffers = { header, body };

// 将缓冲区数组中的数据写入通道
channel.write(buffers);
```

在这个例子中，`header` 和 `body` 缓冲区中的数据将被连续写入通道，形成一个包含头部和数据体的完整消息。

**优势**

- **效率**：Scattering 和 Gathering 允许你一次性地读取或写入多个缓冲区，这比单独处理每个缓冲区更高效。
- **灵活性**：对于复杂的数据结构，可以使用多个缓冲区来分别处理不同类型的数据，使得数据处理更加模块化和清晰。

**注意事项**

- **顺序**：在使用 Scattering 读取时，通道中的数据将按照缓冲区数组中的顺序被读入各个缓冲区。同样，在使用 Gathering 写入时，缓冲区数组中的顺序决定了数据的写入顺序。
- **限制**：并非所有的操作系统和通道都支持 Scattering 和 Gathering 操作。在使用这些操作之前，最好检查你的环境是否支持。

Scattering 和 Gathering 是 Java NIO 中非常强大的特性，它们使得对复杂数据结构的处理变得更加高效和灵活。

## Selector

Selector 类是 NIO 的核心类， Selector 能够检测多个注册的通道上是否有事件发生，如果有事件发生，便获取事件然后针对每个事件进行
相应的响应处理。这样一来，只是用一个单线程就可以管理多个通道，也就是管理多个连接。这样使得只有在连接真正有读写事件发生时，
才会调用函数来进行读写，就大大地减少了系统开销，并且不必为每个连接都创建一个线程，不用去维护多个线程，并且避免了多线程之间
的上下文切换导致的开销。

Java NIO（New Input/Output）的`Selector`类是Java NIO中用于实现非阻塞IO的关键组件。它允许单个线程管理多个网络连接。下面是一个简单的使用`Selector`的实例，演示了如何在服务器端使用`Selector`来处理多个客户端的连接。

```java
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NioSelectorServer {

    public static void main(String[] args) throws IOException {
        // 打开服务器套接字通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // 绑定到指定端口
        serverSocketChannel.bind(new InetSocketAddress(8080));
        // 设置为非阻塞模式
        serverSocketChannel.configureBlocking(false);

        // 打开选择器
        Selector selector = Selector.open();
        // 将服务器套接字通道注册到选择器上，并指定感兴趣的事件为接受连接
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("Server is listening on port 8080...");

        while (true) {
            // 选择一组键，其对应的通道已为 I/O 操作准备就绪
            int readyChannels = selector.select();
            if (readyChannels == 0) {
                continue;
            }

            // 获取选择器中所有已选择的键
            Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();

            while (selectedKeys.hasNext()) {
                SelectionKey key = selectedKeys.next();

                // 检查键是否是新的连接
                if (key.isAcceptable()) {
                    ServerSocketChannel server = (ServerSocketChannel) key.channel();
                    // 接受连接
                    SocketChannel client = server.accept();
                    client.configureBlocking(false);
                    // 注册客户端到选择器，并指定感兴趣的事件为读取
                    client.register(selector, SelectionKey.OP_READ);
                    System.out.println("Accepted connection from " + client.getRemoteAddress());
                }

                // 检查键是否是读取事件
                if (key.isReadable()) {
                    SocketChannel client = (SocketChannel) key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    int bytesRead = client.read(buffer);
                    if (bytesRead > 0) {
                        buffer.flip();
                        // 读取数据
                        System.out.println("Received message: " + new String(buffer.array(), 0, bytesRead));
                        // 回应客户端
                        buffer.clear();
                        buffer.put("Server received message".getBytes());
                        buffer.flip();
                        client.write(buffer);
                    } else if (bytesRead == -1) {
                        // 客户端关闭连接
                        client.close();
                    }
                }

                // 移除已处理的键
                selectedKeys.remove();
            }
        }
    }
}
```

这个例子中，我们创建了一个服务器端的程序，它监听8080端口。当有新的连接请求时，它接受连接并注册到`Selector`上。当有数据可读时，它读取数据并回送一条消息给客户端。

注意，这个例子仅用于演示目的，实际应用中可能需要更复杂的错误处理和资源管理。此外，为了保持非阻塞特性，所有的操作都应当是非阻塞的，例如，`read`和`write`操作应当在循环中调用，直到所有数据都被读取或写入。

### 常用 API

#### 创建 Selector

要使用 Selector，首先需要创建一个 Selector 实例：

```java
Selector selector = Selector.open();
```

#### 注册 SelectableChannel

要监控一个通道，需要将其注册到 Selector 上。注册时，你需要指定你想要在此通道上做的 I/O 操作类型（如 `SelectionKey.OP_READ`、`SelectionKey.OP_WRITE` 等）：

```java
SelectableChannel channel = ...; // 通道实例
int interestOps = SelectionKey.OP_READ; // 关注的操作类型
channel.register(selector, interestOps);
```

#### 选择操作

通过 `select()` 方法，Selector 会阻塞，直到至少有一个注册的通道处于就绪状态：

```java
int readyChannels = selector.select();
```

如果需要非阻塞地检查通道状态，可以使用 `selectNow()` 方法：

```java
int readyChannels = selector.selectNow();
```

#### 获取就绪的 SelectionKey

一旦 `select()` 方法返回，你可以通过 `selectedKeys()` 方法获取所有就绪的通道对应的 `SelectionKey` 集合：

```java
Set<SelectionKey> selectedKeys = selector.selectedKeys();
```

#### 处理 SelectionKey

遍历 `selectedKeys` 集合，检查每个 `SelectionKey` 的状态，并执行相应的操作：

```java
for (SelectionKey key : selectedKeys) {
    if (key.isReadable()) {
        // 处理可读事件
    } else if (key.isWritable()) {
        // 处理可写事件
    }
    // ... 其他操作
}
```

#### 获取 Channel

在 Java NIO 中，`SelectionKey` 对象代表了一个特定的 `SelectableChannel` 和一个 `Selector` 之间的注册关系。当你将一个 `SelectableChannel` 注册到 `Selector` 上时，注册操作会返回一个 `SelectionKey` 对象。通过这个 `SelectionKey` 对象，你可以获取到与之关联的 `SelectableChannel`。

要从 `SelectionKey` 获取对应的 `SelectableChannel`，可以使用 `channel()` 方法：

```java
SelectionKey key = ...; // SelectionKey 实例
SelectableChannel channel = key.channel();
```

#### 取消注册和清理

当不再需要监控某个通道时，应该取消注册：

```java
key.cancel();
```

在处理完 `SelectionKey` 后，应该从集合中移除它，以避免重复处理：

```java
selectedKeys.remove(key);
```

#### 示例

下面是一个简单的使用 Selector 的示例：

```java
import java.io.IOException;
import java.nio.channels.*;
import java.util.Iterator;

public class SelectorExample {
    public static void main(String[] args) throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(8080));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            if (selector.select() > 0) {
                Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    if (key.isAcceptable()) {
                        // 接受新的连接
                    } else if (key.isReadable()) {
                        // 处理读取事件
                    }
                    keyIterator.remove();
                }
            }
        }
    }
}
```

在这个例子中，我们创建了一个 `Selector` 和一个非阻塞的 `ServerSocketChannel`，并将其注册到 Selector 上。程序会持续监控是否有新的连接请求或可读事件发生，并进行相应的处理。

## 三大组件的关系

Channel 相当于连接，并不直接连接向资源，而是连接向 Buffer，一个 Channel 对应 一个 Buffer，Channel 和 Buffer 一样，是双向可读可写的。
Selector 相当于连接管理器，管理多个已注册在当前 Selector 中的 Channel，一个线程一般对应一个 Selector。程序切换到哪个 Channel，是操作系统内核事件通知的。
对数据的读写都需要经过 Buffer，即应用程序数据缓冲区，是 Java 程序与操作系统内核之间交换数据的桥梁。

## 一个包含客户端和服务端的无阻塞 NIO 示例

非阻塞模式下，`SocketChannel`和`ServerSocketChannel`需要设置为非阻塞模式，并且通常会使用`Selector`来管理多个通道的IO事件。

### 服务端代码

```java
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.io.IOException;

public class NonBlockingServerExample {
    public static void main(String[] args) {
        int port = 12345;
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            serverSocketChannel.bind(new InetSocketAddress(port));
            serverSocketChannel.configureBlocking(false); // 设置为非阻塞模式

            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT); // 注册到选择器

            System.out.println("Server listening on port " + port);

            while (true) {
                if (selector.select() > 0) { // 检查是否有事件发生
                    Set<SelectionKey> selectedKeys = selector.selectedKeys();
                    Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
                    while (keyIterator.hasNext()) {
                        SelectionKey key = keyIterator.next();
                        if (key.isAcceptable()) {
                            SocketChannel socketChannel = serverSocketChannel.accept();
                            socketChannel.configureBlocking(false);
                            socketChannel.register(selector, SelectionKey.OP_READ);
                            System.out.println("Client connected");
                        } else if (key.isReadable()) {
                            SocketChannel socketChannel = (SocketChannel) key.channel();
                            ByteBuffer buffer = ByteBuffer.allocate(1024);
                            int bytesRead = socketChannel.read(buffer);
                            if (bytesRead > 0) {
                                buffer.flip();
                                String message = new String(buffer.array(), 0, bytesRead).trim();
                                System.out.println("Received from client: " + message);

                                // 向客户端发送响应
                                String response = "Server response: " + message;
                                buffer.clear();
                                buffer.put(response.getBytes());
                                buffer.flip();
                                socketChannel.write(buffer);
                            } else if (bytesRead == -1) {
                                socketChannel.close();
                            }
                        }
                        // 将该事件从当前获取的一批事件中去除，避免重复处理
                        keyIterator.remove();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

### 客户端代码

```java
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.io.IOException;

public class NonBlockingClientExample {
    public static void main(String[] args) {
        String host = "127.0.0.1";
        int port = 12345;

        try (SocketChannel socketChannel = SocketChannel.open()) {
            socketChannel.configureBlocking(false); // 设置为非阻塞模式
            socketChannel.connect(new InetSocketAddress(host, port));

            // 发送消息到服务器
            String message = "Hello, Server!";
            ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
            while (buffer.hasRemaining()) {
                socketChannel.write(buffer);
            }

            // 接收服务器的响应
            buffer.clear();
            int bytesRead = socketChannel.read(buffer);
            if (bytesRead > 0) {
                buffer.flip();
                byte[] responseBytes = new byte[bytesRead];
                buffer.get(responseBytes);
                String response = new String(responseBytes);
                System.out.println("Server response: " + response);
            } else if (bytesRead == -1) {
                System.out.println("Connection closed by server.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

**运行说明**

1. **服务端**：首先，服务端的`ServerSocketChannel`和`SocketChannel`都被设置为非阻塞模式。然后，服务端使用`Selector`来监听`OP_ACCEPT`和`OP_READ`事件。当有新的连接或读事件发生时，服务端会处理这些事件。

2. **客户端**：客户端的`SocketChannel`同样设置为非阻塞模式。发送消息时，客户端会循环调用`write`方法直到所有数据都被发送。接收响应时，客户端会尝试读取数据，如果读取到-1，则表示连接已关闭。

**注意事项**

- 在非阻塞模式下，`read`和`write`方法可能不会立即完成，它们可能返回0或-1，表示没有数据可读或连接已关闭。
- 使用`Selector`可以有效地管理多个非阻塞通道，提高服务器的并发处理能力。
- 在实际应用中，可能需要处理更多的异常情况和边缘情况，例如处理`SocketChannel`的`OP_WRITE`事件，以及在客户端和服务器之间进行更复杂的交互。

这个示例展示了如何使用Java NIO的非阻塞模式来实现客户端和服务器之间的通信。在构建高性能的网络应用时，非阻塞模式和选择器的使用是关键。

## SelectionKey

`SelectionKey`是Java NIO中`Selector`和`Channel`之间的桥梁，它代表了一个特定的`Channel`在特定的`Selector`上的注册。当一个`Channel`注册到一个`Selector`时，它会返回一个`SelectionKey`对象，该对象包含了关于该`Channel`注册状态和选择操作的信息。

下面是`SelectionKey`类中一些重要的API和属性：

1. Channel

- `public final Channel channel()`: 返回与该`SelectionKey`关联的`Channel`对象。

2. Selector

- `public final Selector selector()`: 返回注册该`SelectionKey`的`Selector`对象。

3. Interest Sets

- `public final int interestOps()`: 返回当前的感兴趣的操作集合，表示该`Channel`对哪些操作感兴趣。
- `public final SelectionKey interestOps(int ops)`: 设置感兴趣的操作集合。参数`ops`是一个位掩码，可以是`OP_READ`、`OP_WRITE`、`OP_CONNECT`或`OP_ACCEPT`的组合。

4. Ready Sets

- `public final int readyOps()`: 返回当前就绪的操作集合，表示`Channel`已经准备就绪的操作。
- `public final boolean isReadable()`: 检查`Channel`是否准备好进行读操作。
- `public final boolean isWritable()`: 检查`Channel`是否准备好进行写操作。
- `public final boolean isConnectable()`: 检查`Channel`是否完成连接操作。
- `public final boolean isAcceptable()`: 检查`Channel`是否准备好接受新的连接。

5. Attachment

- `public final Object attachment()`: 返回与该`SelectionKey`关联的对象，可以是任意类型，用于在选择操作中附加额外的信息。
- `public final SelectionKey attach(Object ob)`: 将指定的对象`ob`附加到该`SelectionKey`上。

6. Canceling

- `public final void cancel()`: 取消该`SelectionKey`。当`SelectionKey`被取消时，它将不再被`Selector`所选择，并且其关联的`Channel`将被注销。

7. Other Methods

- `public final boolean isValid()`: 检查该`SelectionKey`是否有效。如果`SelectionKey`被取消，或者其关联的`Channel`被关闭，则返回`false`。

使用场景

`SelectionKey`通常在使用`Selector`进行非阻塞IO操作时使用。当通过`Selector`选择操作时，可以检查每个`SelectionKey`的`readyOps()`来确定哪些操作是就绪的，然后根据`isReadable()`, `isWritable()`, `isConnectable()`, `isAcceptable()`等方法来执行相应的操作。

## 基于 Java NIO 的多人群聊系统

服务器端实现监听端口消息并转发，以及监控用户在线和离线状态
客户端实现发送消息到服务器端

# Netty

原生 NIO 存在很多问题所以诞生了 Netty 

- 开发难度大，有许多要考虑的方面，如断连重连，网络闪断，半包读写，失败缓存，网络拥塞和异常流处理等
- JDK NIO 存在原生 BUG ，如Epoll Bug，它会导致 Selecter 空转，导致 CPU 100％，直到 JDK 7 版本该问题依旧存在，官方在JDK 1.6版本的update18中声称修复了这个问题，但是根据一些用户反馈和社区讨论，直到JDK 1.7版本，这个问题依旧存在，尽管发生概率有所降低。这表明该问题并没有被完全解决，只是在某些情况下表现得不那么明显了。

为了规避这样的问题，开发者们通常会采取一些措施，比如使用Netty这样的高级网络框架，它通过内部机制（如周期性统计、重建Selector、资源管理等策略）有效地解决了NIO中的epoll Bug问题。此外，建议开发者尽量使用更新版本的JDK，并关注社区中关于此问题的最新动态和解决方案。如果在使用过程中遇到类似问题，可以考虑升级JDK版本或使用成熟的网络框架来避免潜在的性能问题。

目前 netty 5.x 已经被官方废弃，所以本文以 netty 4.1 为示例版本。

## IO 线程模型

### Reactor 线程模型

针对传统 IO 导致的一个链接对应一个客户端，每个链接又需要一个单独的线程来维护，提出了 Reactor 线程模型。

Reactor模型的核心思想是将IO操作的等待和处理分离，通过一个或多个线程来监听和分发事件，从而实现对大量连接的高效管理。

简单来说，假设现在有四个客户端连向服务器，服务器端提供一个 Reactor 角色来接收这些链接，在接收完链接后将链接分发给后端的不同线程去处理，这样就可以利用线程池的优势来避免线程空闲。

Reactor模型主要有以下几种类型：

1. **单Reactor单线程模型**：一个单独的线程既处理事件监听（Reactor），又处理事件分发和处理（Handler）。这种模型适用于连接数较少，且业务处理简单的场景。

2. **单Reactor多线程模型**：使用一个单独的Reactor线程来监听和分发事件，但使用线程池来处理实际的IO操作。这种模型提高了处理能力，适用于连接数较多的场景。

3. **主从Reactor多线程模型**：这是最常用的模型，其中有一个主Reactor负责监听新的连接请求，一旦建立连接，就将新的Socket分发给从Reactor，从Reactor负责后续的IO事件处理。这种模型可以充分利用多核CPU的优势，提高系统的伸缩性。

其中，分发器的实现即 NIO 的 Selecter 用来接收和分发请求，而事件的处理器即可以根据 Selecter 监听的事件分成几类（建立链接、读、写等）分别独立成不同的方法给不同的线程来调用。

#### 单Reactor单线程模型

单 Reactor 单线程模型是 Reactor 模式中最简单的一种实现方式。在这种模型中，所有的I/O操作和业务逻辑处理都在同一个线程中完成。下面是这种模型的内部结构和工作流程的详细描述：

组件组成

1. **Reactor 线程**：这是模型的核心，负责监听和分发事件。它通常会包含一个事件循环（Event Loop），不断地检查事件源（如套接字）是否有事件发生。

2. **事件源**：通常指网络连接，如套接字（Socket）。在 Reactor 模型中，Reactor 线程会监听这些事件源上的事件，如新连接的建立、数据的可读或可写等。

3. **事件处理器（Handler）**：当事件发生时，Reactor 线程会调用相应的事件处理器来处理事件。事件处理器通常是一个接口或抽象类，具体实现类负责定义如何处理特定类型的事件。

4. **非阻塞I/O**：Reactor 模型通常与非阻塞I/O一起使用，以避免在等待I/O操作完成时阻塞线程。

工作流程

1. **初始化**：创建 Reactor 线程，并初始化事件源（如套接字）。

2. **事件监听**：Reactor 线程进入一个无限循环，使用非阻塞方式监听事件源上的事件。

3. **事件分发**：当事件源上有事件发生时，Reactor 线程会获取到这个事件，并根据事件类型找到对应的事件处理器。

4. **事件处理**：Reactor 线程调用事件处理器的相应方法来处理事件。在这个过程中，可能会进行数据读取、业务逻辑处理等。

5. **循环处理**：处理完一个事件后，Reactor 线程继续监听下一个事件，整个过程不断循环。

优缺点

**优点**：

- **简单**：结构简单，实现起来相对容易。
- **资源占用少**：由于只有一个线程，因此不需要线程间切换的开销，内存占用也相对较少。

**缺点**：

- **性能限制**：由于所有操作都在同一个线程中完成，如果事件处理（特别是业务逻辑处理）耗时较长，会导致事件处理延迟，影响整体性能。
- **无法利用多核**：无法通过多线程并行处理来提高性能，不适合CPU密集型任务。

应用场景

单 Reactor 单线程模型适用于事件处理快速、I/O密集型的应用，如简单的HTTP服务器、轻量级的RPC框架等。对于需要处理大量并发连接或复杂业务逻辑的应用，可能需要考虑更复杂的模型，如单 Reactor 多线程模型或主从 Reactor 多线程模型。

上文的群聊系统的实现就是用的单Reactor单线程模型，因为所有的处理都是在自己的线程里处理的。

#### 单Reactor多线程模型

单Reactor多线程模型就是将涉及业务处理的地方独立成单独的方法分发给其他线程来完成，并将结果返回。其中读、写、发送数据等过程还是在自己的线程中完成。即 Reactor 线程只负责响应事件。

单 Reactor 多线程模型是 Reactor 模式的一种扩展，它在单 Reactor 单线程模型的基础上引入了线程池来处理耗时的事件处理逻辑，从而提高了系统的性能和吞吐量。下面是这种模型的内部结构和工作流程的详细描述：

组件组成

1. **单个 Reactor 线程**：负责监听和分发事件，是整个模型的核心。它使用一个事件循环来不断检查事件源（如套接字）上的事件。

2. **事件源**：网络连接，如套接字（Socket），是事件发生的地方。Reactor 线程会监听这些事件源上的事件。

3. **事件处理器（Handler）**：当事件发生时，Reactor 线程会调用相应的事件处理器来处理事件。事件处理器定义了如何处理特定类型的事件。

4. **工作线程池（Worker Pool）**：一组工作线程，用于处理耗时的事件处理逻辑。这些线程从 Reactor 线程接收事件，并在自己的线程中执行实际的业务逻辑。

5. **非阻塞I/O**：通常与非阻塞I/O一起使用，以避免在等待I/O操作完成时阻塞线程。

工作流程

1. **初始化**：创建 Reactor 线程，并初始化事件源（如套接字）。

2. **事件监听**：Reactor 线程进入一个无限循环，使用非阻塞方式监听事件源上的事件。

3. **事件分发**：当事件源上有事件发生时，Reactor 线程会获取到这个事件，并根据事件类型找到对应的事件处理器。

4. **事件处理**：Reactor 线程将事件分发给工作线程池中的一个线程。工作线程从 Reactor 线程接收事件，并在自己的线程中处理事件。

5. **业务逻辑处理**：工作线程执行实际的业务逻辑处理，这可能包括复杂的计算、数据库操作等。

6. **循环处理**：处理完一个事件后，工作线程返回到线程池中等待下一个任务，Reactor 线程继续监听下一个事件，整个过程不断循环。

优缺点

**优点**：

- **性能提升**：通过引入工作线程池，可以并行处理多个事件，显著提高了处理大量并发连接的能力。
- **资源利用**：相比单线程模型，可以更好地利用多核CPU的优势。

**缺点**：

- **复杂性增加**：相比单 Reactor 单线程模型，引入了线程池，增加了系统的复杂性。
- **线程管理开销**：需要管理线程池，包括线程的创建、销毁和同步等。

应用场景

单 Reactor 多线程模型适用于需要处理大量并发连接，且事件处理逻辑相对复杂的场景。例如，Web服务器、数据库服务器等，这些场景下，事件处理可能包括网络I/O、磁盘I/O以及复杂的业务逻辑处理。通过将耗时操作分配给工作线程池，可以有效提高系统的整体性能和响应速度。

#### 主从Reactor多线程模型

主从Reactor多线程模型是将上一步的读、写、发送数据等过程也拆分成方法发送给从 Reactor 线程来处理。即主Reactor线程只负责接收新的链接。其余的都交给了子 Reactor 线程和 worker 线程处理。而后续子 Reactor 线程也不需要与主 Reactor 进行信息交互，因为读写数据都在子 Reactor 线程中自己处理，子 Reactor 线程只需要和 worker 线程进行数据交互，拿到 worker 线程的业务数据即可。

主从 Reactor 多线程模型是 Reactor 模式的一种高级形式，它通过分离监听新连接的 Reactor（主 Reactor）和处理已连接的连接的 Reactor（从 Reactor）来进一步提高性能和可扩展性。这种模型特别适合于需要处理大量并发连接的场景，如大型网络服务器。下面是这种模型的内部结构和工作流程的详细描述：

组件组成

1. **主 Reactor 线程**：负责监听新的连接请求。当新的连接到来时，主 Reactor 接受连接，并将新的连接分配给从 Reactor。

2. **从 Reactor 线程池**：由多个 Reactor 线程组成，每个 Reactor 线程负责监听一组已连接的连接上的事件。从 Reactor 线程池可以动态调整大小，以适应不同的负载情况。

3. **事件源**：网络连接，如套接字（Socket），是事件发生的地方。主 Reactor 线程监听新的连接事件，而从 Reactor 线程监听已连接的连接上的读写事件。

4. **事件处理器（Handler）**：定义了如何处理特定类型的事件。当事件发生时，相应的事件处理器会被调用。

5. **工作线程池（Worker Pool）**：可选组件，用于处理耗时的业务逻辑。从 Reactor 线程可以将事件分发给工作线程池中的线程，以异步方式执行复杂的业务逻辑处理。

工作流程

1. **初始化**：创建主 Reactor 线程和从 Reactor 线程池。

2. **监听新连接**：主 Reactor 线程监听新的连接请求。当新的连接到来时，主 Reactor 接受连接。

3. **连接分配**：主 Reactor 将新的连接分配给从 Reactor 线程池中的某个 Reactor 线程。

4. **事件监听**：从 Reactor 线程监听分配给它的已连接的连接上的事件。

5. **事件分发**：当事件发生时，从 Reactor 线程获取事件，并调用相应的事件处理器来处理事件。

6. **业务逻辑处理**：如果事件处理需要执行耗时的业务逻辑，从 Reactor 线程可以将任务分发给工作线程池中的线程来异步处理。

7. **循环处理**：主 Reactor 和从 Reactor 线程持续监听和处理事件，整个过程不断循环。

优缺点

**优点**：

- **高并发处理能力**：通过分离新连接的监听和已连接连接的事件处理，可以更有效地处理大量并发连接。
- **可扩展性**：从 Reactor 线程池可以根据需要进行扩展，以处理更多的连接和事件。
- **负载均衡**：主 Reactor 可以将新连接均匀地分配给从 Reactor 线程，实现负载均衡。

**缺点**：

- **复杂性较高**：相比单 Reactor 模型，主从 Reactor 模型的实现和管理更为复杂。
- **资源占用**：需要更多的线程和资源来维护主 Reactor 和从 Reactor 线程池。

应用场景

主从 Reactor 多线程模型适用于需要处理大量并发连接的高性能网络服务器，如大型Web服务器、数据库服务器、高性能代理服务器等。这种模型能够有效地利用多核CPU的优势，提高系统的吞吐量和响应速度。

Netty 线程模型就是从主从Reactor多线程模型发展而来。

### Netty 的线程模型

一个简单的 Netty 模型说明就是： Netty 引入一个 BossGroup 角色来接收客户端连接请求，将请求通过 Selecter.accept() 获取到 SocketChannel，再将 SocketChannel 转化为 NIOSocketChannel 交给 WorkerGroup 通过 Selecter 来监听并交由 Handler 处理。

实际上的 Netty 模型会比上面的描述要复杂一些

Netty 是一个高性能的异步事件驱动的网络应用框架，它使用了一种灵活的线程模型来处理网络通信。Netty 的线程模型基于主从 Reactor 多线程模型，但进行了优化和简化，以适应不同的使用场景。下面是 Netty 线程模型的内部结构和工作流程的详细描述：

组件组成

1. **Boss EventLoopGroup**：负责接收新的连接。它包含一个或多个 Boss EventLoop，每个 Boss EventLoop 负责监听和接受新的连接请求。

2. **Worker EventLoopGroup**：负责处理已接受连接的数据读写。它包含多个 Worker EventLoop，每个 Worker EventLoop 负责一组连接的读写事件。

3. **EventLoop**：是 Netty 线程模型的核心组件，负责监听事件并执行事件处理。每个 EventLoop 通常会绑定到一个线程上，并且会持续运行，处理分配给它的多个连接上的事件。

4. **Channel**：代表一个网络连接，是 Netty 中处理网络通信的抽象。

5. **ChannelHandler**：定义了对网络事件的处理逻辑，如读写事件、连接事件等。ChannelHandler 可以串联成一个 ChannelPipeline，以处理复杂的业务逻辑。

工作流程

1. **初始化**：创建 Boss EventLoopGroup 和 Worker EventLoopGroup。Boss EventLoopGroup 通常只有一个线程，而 Worker EventLoopGroup 可以有多个线程。

2. **监听新连接**：Boss EventLoopGroup 中的一个 Boss EventLoop 监听新的连接请求。

3. **接受连接**：当新的连接请求到来时，Boss EventLoop 接受连接，并将新的 Channel 注册到 Worker EventLoopGroup 中的一个 Worker EventLoop 上。

4. **事件处理**：Worker EventLoop 负责监听和处理已注册的 Channel 上的读写事件。每个 Worker EventLoop 通常会处理多个 Channel。

5. **业务逻辑处理**：当读取到数据或有写事件发生时，数据会传递到 ChannelPipeline 中的 ChannelHandler 进行处理。

6. **循环监听**：每个 EventLoop 会持续运行，不断地监听和处理事件，直到 Channel 关闭。

优缺点

**优点**：

- **高性能**：通过使用非阻塞I/O和高效的事件循环机制，Netty 能够提供高吞吐量和低延迟的网络通信。
- **灵活性**：Netty 允许开发者自定义线程模型和事件处理逻辑，以适应不同的应用场景。
- **可扩展性**：Netty 的线程模型可以很容易地扩展以处理更多的连接和更高的负载。

**缺点**：

- **复杂性**：相比简单的网络库，Netty 的线程模型和事件处理机制相对复杂，需要一定的学习曲线。

应用场景

Netty 的线程模型适用于需要高性能、高可靠性的网络通信场景，如游戏服务器、即时通讯服务器、HTTP服务器等。Netty 的灵活性和性能使其成为构建网络应用的首选框架之一。

可以总结如下

1. Netty抽象出两组线程池 ：BossGroup 专门负责接收客户端的连接，WorkerGroup ：专门负责网络的读写。
2. BossGroup 和 WorkerGroup 类型都是 NioEventLoopGroup。
3. NioEventLoopGroup 相当于一个事件循环组，这个组中含有多个事件循环，每一个事件循环是 NioEventLoop。
4. NioEventLoop 表示一个不断循环的执行处理任务的线程，每个 NioEventLoop 都有一个 selector，用于监听绑定在其上的 socket 的网络通信。
5. NioEventLoopGroup 可以有多个线程，即可以含有多个 NioEventLoop。
6. 每个 Boss NioEventLoop 循环执行的步骤有3步。
7. 轮询accept事件。
8. 处理accept事件，与client建立连接，生成NioSocketChannel，并将其注册到某个worker NioEventLoop上的selector。
9. 处理任务。

## 基本使用示例

### 服务器端示例

下面是一个简单的Netty服务器端示例，用于展示如何使用Netty创建一个基本的服务器。这个例子中，我们将创建一个能够接收客户端消息并返回简单响应的服务器。

首先，确保你已经添加了Netty的依赖到你的项目中。如果你使用Maven，可以在`pom.xml`中添加如下依赖：

```xml
<dependencies>
    <!-- Netty -->
    <dependency>
        <groupId>io.netty</groupId>
        <artifactId>netty-all</artifactId>
        <version>4.1.68.Final</version>
    </dependency>
</dependencies>
```

然后，创建一个Netty服务器的实现：

```java
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class NettyServer {

    private final int port;

    public NettyServer(int port) {
        this.port = port;
    }

    public void start() throws Exception {
        // 创建两个EventLoopGroup对象，一个用于接收连接，另一个用于处理连接的数据读写
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // 创建服务端启动助手
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class) // 指定使用的channel
             .childHandler(new ChannelInitializer<SocketChannel>() { // 设置childHandler来处理网络事件
                 @Override
                 protected void initChannel(SocketChannel ch) {
                     ChannelPipeline pipeline = ch.pipeline();
                     // 添加字符串解码器和编码器
                     pipeline.addLast("decoder", new StringDecoder());
                     pipeline.addLast("encoder", new StringEncoder());
                     // 添加自定义的处理器
                     pipeline.addLast(new SimpleServerHandler());
                 }
             });

            // 绑定端口并同步等待成功，即启动服务端
            ChannelFuture f = b.bind(port).sync();

            // 等待服务端监听端口关闭
            f.channel().closeFuture().sync();
        } finally {
            // 关闭两个EventLoopGroup，释放资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 8080; // 服务器端口
        new NettyServer(port).start();
    }
}

// 自定义的处理器，用于处理读写事件
class SimpleServerHandler extends io.netty.channel.ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(io.netty.channel.ChannelHandlerContext ctx, Object msg) {
        // 接收到消息后，简单地将接收到的字符串转换为大写并回写给客户端
        System.out.println("Server received: " + msg);
        ctx.write(msg);
    }

    @Override
    public void channelReadComplete(io.netty.channel.ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(io.netty.channel.ChannelHandlerContext ctx, Throwable cause) {
        // 出现异常时关闭连接
        cause.printStackTrace();
        ctx.close();
    }
}
```

这个例子中，我们创建了一个`NettyServer`类，它在指定端口上启动了一个服务器。服务器使用了两个`NioEventLoopGroup`，一个用于接收新的连接请求（boss group），另一个用于处理已接收连接的数据读写（worker group）。我们通过`ServerBootstrap`配置了服务器，并指定了一个`ChannelInitializer`来添加处理器到每个新的连接。

`SimpleServerHandler`类扩展了`ChannelInboundHandlerAdapter`，并重写了`channelRead`方法来处理接收到的消息。在这个例子中，服务器简单地将接收到的字符串转换为大写并回写给客户端。

请注意，这只是一个基础示例，实际生产环境中的Netty服务器会更复杂，包括异常处理、日志记录、协议编解码器等。此外，Netty版本更新可能会带来API的变化，请根据实际使用的Netty版本调整代码。

### 客户端示例

当然，下面是一个简单的Netty客户端示例，用于连接到上面提供的服务器，并发送消息给服务器，然后接收服务器的响应。

```java
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class NettyClient {

    private final String host;
    private final int port;

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws Exception {
        // 创建一个EventLoopGroup对象，用于处理客户端事件，客户端只需要创建一个 group
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            // 创建客户端启动助手
            Bootstrap b = new Bootstrap();
            b.group(group) // 设置EventLoopGroup，用于处理客户端事件
             .channel(NioSocketChannel.class) // 指定使用的channel
             .handler(new ChannelInitializer<SocketChannel>() { // 设置ChannelInitializer来处理Channel
                 @Override
                 protected void initChannel(SocketChannel ch) {
                     ChannelPipeline pipeline = ch.pipeline();
                     // 添加字符串解码器和编码器
                     pipeline.addLast("decoder", new StringDecoder());
                     pipeline.addLast("encoder", new StringEncoder());
                     // 添加自定义的处理器
                     pipeline.addLast(new SimpleClientHandler());
                 }
             });

            // 连接到服务器
            ChannelFuture f = b.connect(host, port).sync();

            // 等待连接关闭
            f.channel().closeFuture().sync();
        } finally {
            // 关闭EventLoopGroup，释放资源
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        String host = "localhost"; // 服务器地址
        int port = 8080; // 服务器端口
        new NettyClient(host, port).start();
    }
}

// 自定义的处理器，用于处理读写事件
class SimpleClientHandler extends io.netty.channel.ChannelInboundHandlerAdapter {
    private final String message;

    public SimpleClientHandler() {
        // 初始化要发送的消息
        this.message = "Hello Netty Server!";
    }

    @Override
    public void channelActive(io.netty.channel.ChannelHandlerContext ctx) {
        // 当连接建立时发送消息
        ctx.writeAndFlush(message);
    }

    @Override
    public void channelRead(io.netty.channel.ChannelHandlerContext ctx, Object msg) {
        // 接收到服务器响应的消息后处理
        System.out.println("Client received: " + msg);
    }

    @Override
    public void exceptionCaught(io.netty.channel.ChannelHandlerContext ctx, Throwable cause) {
        // 出现异常时关闭连接
        cause.printStackTrace();
        ctx.close();
    }
}
```

在这个客户端代码中，我们创建了一个`NettyClient`类，它使用`Bootstrap`来配置客户端连接。我们同样使用了`NioEventLoopGroup`来处理客户端的事件循环，并通过`ChannelInitializer`添加了处理器到连接中。

`SimpleClientHandler`类扩展了`ChannelInboundHandlerAdapter`，并重写了`channelActive`方法来在连接建立后发送消息给服务器。同时，它还重写了`channelRead`方法来处理从服务器接收到的响应。

客户端启动后，会连接到服务器，并发送一条消息"Hello Netty Server!"。然后，它会等待并打印服务器的响应。

请确保服务器端代码正在运行，并且客户端代码中的主机地址和端口号与服务器端配置相匹配。这个例子同样需要根据实际使用的Netty版本进行调整。

# Netty 核心组件

## EventLoopGroup

想象一下，你在一个繁忙的餐厅工作，这个餐厅需要同时接待很多顾客，并且为他们提供食物。在这个场景中，`EventLoopGroup` 就像是餐厅里的服务员团队，而每个 `EventLoop` 就相当于一个服务员。

服务员团队（EventLoopGroup）

1. **接待顾客（处理连接）**：当顾客（客户端）进入餐厅（建立连接）时，需要有人接待他们。服务员团队（`EventLoopGroup`）负责分配一个服务员（`EventLoop`）来照顾这个顾客。

2. **分配工作（负载均衡）**：服务员团队需要确保每个服务员都有事情做，不会太忙也不会太闲。如果一个服务员正在忙于服务顾客，团队会将新来的顾客分配给其他空闲的服务员。

3. **处理订单（处理事件）**：服务员（`EventLoop`）负责接收顾客的点餐（网络事件），并确保他们得到正确的食物（数据处理）。服务员会一直负责这个顾客直到他们离开（连接关闭）。

4. **额外任务（任务调度）**：除了照顾顾客点餐，服务员还需要处理一些额外的任务，比如清理桌子、准备餐具等。`EventLoopGroup` 也负责安排这些额外的任务。

服务员（EventLoop）

- **长期关系（Channel 绑定）**：一旦服务员开始服务一个顾客，他会一直负责这个顾客直到他们离开。在 Netty 中，一个 `EventLoop` 一旦被分配给一个 `Channel`，就会一直处理这个 `Channel` 上的所有事件。

- **高效工作（无锁设计）**：服务员们高效地工作，不会互相干扰。在 Netty 中，`EventLoop` 通过无锁设计确保了高效处理事件，避免了线程之间的竞争和锁的使用。

通过这个比喻，我们可以看到 `EventLoopGroup` 在 Netty 中的作用就像一个高效协调服务员团队的领班，确保每个顾客（客户端）都能得到及时和专业的服务。而每个服务员（`EventLoop`）则专注于处理与顾客相关的所有事务，保证了服务的连贯性和效率。

在 Netty 中，`EventLoopGroup` 所管理的 EventLoop 的默认池大小取决于你的操作系统和 JVM 的可用处理器数量。Netty默认会尝试使用CPU核心数的两倍作为`EventLoopGroup`的线程数。这个默认行为是基于Netty的默认构造器，它使用`NioEventLoopGroup`作为实现。

例如，如果你的机器有4个CPU核心，Netty默认会创建一个包含8个线程的`EventLoopGroup`。这个默认值可以通过构造函数的参数进行调整，或者通过设置系统属性来改变。

如果你需要查看或修改这个默认值，可以查看Netty的源代码或文档，了解如何通过构造函数参数来指定线程数，或者如何通过系统属性来设置默认的线程池大小。

请注意，最佳的线程池大小可能依赖于具体的应用场景和硬件配置，因此在生产环境中，建议根据实际的性能测试结果来调整这个值。

在创建服务端应用程序时，需要创建两个 EventLoopGroup 分别代表

1. **Boss EventLoopGroup（主事件循环组）**：
   - 这个 `EventLoopGroup` 通常只有一个 `EventLoop`（尽管可以配置多个），它的主要职责是监听和接受新的连接请求。
   - 当一个客户端尝试连接到服务器时，`Boss EventLoopGroup` 中的一个 `EventLoop` 会负责处理这个新的连接请求，并将其注册到 `Worker EventLoopGroup`。
   - 在 Netty 中，`Boss EventLoopGroup` 通常使用 `NioEventLoopGroup` 类实现，它基于 Java NIO 的选择器（Selector）机制。

2. **Worker EventLoopGroup（工作事件循环组）**：
   - 一旦新的连接被接受，`Boss EventLoopGroup` 会将这个连接分配给 `Worker EventLoopGroup`。
   - `Worker EventLoopGroup` 负责处理已接受连接的数据读写事件，即实际的数据传输和业务逻辑处理。
   - `Worker EventLoopGroup` 通常包含多个 `EventLoop`，以便并行处理多个连接上的事件，提高服务器的吞吐量。
   - 类似地，`Worker EventLoopGroup` 也通常使用 `NioEventLoopGroup` 类实现。

### 创建和使用示例

在 Netty 的服务端启动代码中，你会看到类似下面的代码段：

```java
// 创建 Boss EventLoopGroup 和 Worker EventLoopGroup
EventLoopGroup bossGroup = new NioEventLoopGroup(1); // 通常只需要一个线程
EventLoopGroup workerGroup = new NioEventLoopGroup(); // 可以根据需要配置多个线程

try {
    // 创建服务端启动引导类
    ServerBootstrap b = new ServerBootstrap();
    b.group(bossGroup, workerGroup) // 设置两个 EventLoopGroup
      .channel(NioServerSocketChannel.class) // 指定使用 NIO 的传输 Channel
      // 其他配置省略...

    // 绑定端口并同步等待成功，生成 ChannelFuture
    ChannelFuture f = b.bind(port).sync();

    // 等待服务端监听端口关闭
    f.channel().closeFuture().sync();
} finally {
    // 关闭两个 EventLoopGroup，释放资源
    bossGroup.shutdownGracefully();
    workerGroup.shutdownGracefully();
}
```

在创建基于 Netty 的客户端应用程序时，通常只需要创建一个 `EventLoopGroup`。这是因为客户端通常不需要像服务器端那样处理大量的并发连接。客户端的主要任务是与服务器建立连接，并发送或接收数据。

客户端的 `EventLoopGroup` 负责管理所有的 `Channel`，并为每个 `Channel` 分配一个 `EventLoop` 来处理事件。这些事件包括连接的建立、数据的读写、连接的关闭等。

1. **单个连接**：客户端通常只需要与服务器建立一个连接（或者在连接池的情况下，有限数量的连接），因此不需要像服务器端那样进行复杂的连接管理。

2. **资源优化**：使用单个 `EventLoopGroup` 可以减少资源消耗，因为客户端不需要像服务器端那样处理大量的并发连接。

3. **简化管理**：一个 `EventLoopGroup` 足以处理客户端的所有网络操作，简化了事件循环的管理。

在 Netty 的客户端代码中，创建 `EventLoopGroup` 的过程相对简单：

```java
// 创建一个 EventLoopGroup
EventLoopGroup group = new NioEventLoopGroup();

try {
    // 创建客户端启动引导类
    Bootstrap b = new Bootstrap();
    b.group(group) // 设置 EventLoopGroup
      .channel(NioSocketChannel.class) // 指定使用 NIO 的传输 Channel
      // 其他配置省略...

    // 连接到远程服务器
    ChannelFuture f = b.connect(host, port).sync();

    // 等待连接关闭
    f.channel().closeFuture().sync();
} finally {
    // 关闭 EventLoopGroup，释放资源
    group.shutdownGracefully();
}
```

### EventLoopGroup 的常用实现类及其特点

1. NioEventLoopGroup

- **特点**：
  - 基于 Java NIO 的 `Selector` 实现，适用于大多数基于 NIO 的应用场景。
  - 通常用于服务器端的 `Boss` 和 `Worker` 线程组，以及客户端的线程组。
  - 支持多线程处理，可以配置线程数量，以适应不同的性能需求。
  - `NioEventLoop` 通常会处理多个 `Channel` 的事件，实现高效的事件循环。

- **适用场景**：
  - 需要高性能网络通信的场景。
  - 大多数标准的网络应用，无论是客户端还是服务器端。

2. EpollEventLoopGroup

- **特点**：
  - 仅适用于 Linux 系统。
  - 使用 Linux 的 `epoll` 系统调用来实现高效的 I/O 事件通知，相比 NIO 的 `Selector`，在高负载下性能更优。
  - 与 `NioEventLoopGroup` 类似，支持多线程处理和高效事件循环。

- **适用场景**：
  - 高性能网络服务器，特别是在 Linux 系统上。
  - 处理大量并发连接和高负载网络通信的场景。

3. OioEventLoopGroup

- **特点**：
  - 基于 Java 的阻塞 I/O（Old I/O），使用 `Selector` 但不使用 NIO 的非阻塞特性。
  - 适用于需要兼容旧版 Java 应用的场景，或者对 I/O 性能要求不高的简单应用。
  - 通常不推荐用于高性能网络应用，因为其阻塞特性可能导致线程资源的浪费。

- **适用场景**：
  - 兼容性要求较高的旧系统。
  - 对网络性能要求不高的简单应用场景。

4. DefaultEventLoopGroup

- **特点**：
  - 适用于需要在单个线程中执行任务的场景。
  - 不直接用于处理网络 I/O 事件，而是用于执行一些耗时的计算任务或定时任务。
  - 可以在 `ChannelHandler` 中使用，以避免在 I/O 线程中执行耗时操作。

- **适用场景**：
  - 执行非 I/O 相关的后台任务。
  - 在 `ChannelHandler` 中处理需要延迟或定时执行的任务。

选择合适的 `EventLoopGroup` 实现类取决于你的应用需求、目标平台以及性能要求。对于大多数现代网络应用，`NioEventLoopGroup` 是一个很好的起点。如果你的应用运行在 Linux 系统上，并且对性能有极高的要求，`EpollEventLoopGroup` 可能是更好的选择。对于特定的兼容性或简单应用场景，`OioEventLoopGroup` 和 `DefaultEventLoopGroup` 可能更合适。在实际应用中，根据具体情况选择最合适的实现类，可以优化你的应用性能和资源使用。

## ServerBootstrap 和 Bootstrap

`ServerBootstrap` 和 `Bootstrap` 是 Netty 中用于配置和启动服务器端和客户端的两个核心类。它们都继承自 `Bootstrap` 基类，但针对服务器端和客户端的不同需求提供了特定的配置选项和启动机制。

### ServerBootstrap

`ServerBootstrap` 是用于配置和启动 Netty 服务器端应用程序的类。它允许你设置服务器端的各种参数，如端口监听、线程模型、通道选项等，并最终绑定到一个端口上开始监听新的连接请求。

#### 主要特点和用途：

- **双线程组（Boss 和 Worker）**：`ServerBootstrap` 使用两个 `EventLoopGroup`，一个用于接受新的连接（`Boss`），另一个用于处理已接受连接的数据读写（`Worker`）。
- **通道初始化**：通过 `channel` 方法指定使用的通道类型（如 `NioServerSocketChannel`），并可以添加 `ChannelInitializer` 来初始化每个新创建的 `Channel`。
- **配置选项**：可以设置如 TCP 参数（如 SO_BACKLOG）、保持活动探测、TCP_NODELAY 等。
- **服务端引导流程**：通常在 `ServerBootstrap` 的引导流程中，会先调用 `group` 方法设置线程组，然后通过 `channel` 设置通道类型，接着配置各种选项和处理器，最后调用 `bind` 方法绑定到指定端口并监听。

#### 示例代码：

```java
ServerBootstrap b = new ServerBootstrap();
b.group(bossGroup, workerGroup)
 .channel(NioServerSocketChannel.class)
 .childHandler(new ChannelInitializer<SocketChannel>() {
     @Override
     public void initChannel(SocketChannel ch) {
         ch.pipeline().addLast(new ServerHandler());
     }
 })
 .option(ChannelOption.SO_BACKLOG, 128)
 .childOption(ChannelOption.SO_KEEPALIVE, true);
ChannelFuture f = b.bind(port).sync();
```

### Bootstrap

`Bootstrap` 是用于配置和启动 Netty 客户端应用程序的类。与 `ServerBootstrap` 相比，`Bootstrap` 通常只需要一个 `EventLoopGroup`，因为它不需要处理多个并发连接的监听。

#### 主要特点和用途：

- **单线程组**：客户端通常只需要一个 `EventLoopGroup` 来处理所有连接。
- **通道初始化**：同样可以设置通道类型，并通过 `ChannelInitializer` 初始化 `Channel`。
- **连接引导**：`Bootstrap` 用于主动连接到远程服务器，通过 `connect` 方法建立连接。
- **配置选项**：可以设置如 TCP 参数、保持活动探测等，与 `ServerBootstrap` 类似，但通常用于客户端特有的配置。

#### 示例代码：

```java
Bootstrap b = new Bootstrap();
b.group(eventLoopGroup)
 .channel(NioSocketChannel.class)
 .handler(new ChannelInitializer<SocketChannel>() {
     @Override
     public void initChannel(SocketChannel ch) {
         ch.pipeline().addLast(new ClientHandler());
     }
 })
 .option(ChannelOption.SO_KEEPALIVE, true);
ChannelFuture f = b.connect(host, port).sync();
```

### `ServerBootstrap` 和 `Bootstrap` 在配置上的区别

`ServerBootstrap` 和 `Bootstrap` 在配置上有一些关键的区别，这些区别反映了它们各自在服务器端和客户端的不同角色和需求。下面是一些主要的配置差异：

#### 1. EventLoopGroup 的配置

- **ServerBootstrap**：
  - 使用两个 `EventLoopGroup`：一个 `boss` 和一个 `worker`。
  - `boss` 线程组负责接受新的连接请求，而 `worker` 线程组负责处理已接受连接的数据读写。
  
- **Bootstrap**：
  - 通常只使用一个 `EventLoopGroup`。
  - 用于客户端，负责发起连接和处理数据传输。

#### 2. Channel 类型

- **ServerBootstrap**：
  - 通常使用 `NioServerSocketChannel` 或其他服务器端通道类型作为 `channel` 方法的参数。
  
- **Bootstrap**：
  - 使用 `NioSocketChannel` 或其他客户端通道类型作为 `channel` 方法的参数。

#### 3. 通道初始化

- **ServerBootstrap**：
  - 使用 `childHandler` 方法来设置 `ChannelInitializer`，该初始化器用于初始化新接受的连接（`Channel`）。
  
- **Bootstrap**：
  - 使用 `handler` 方法来设置 `ChannelInitializer`，该初始化器用于初始化客户端连接。

#### 4. 绑定和连接

- **ServerBootstrap**：
  - 使用 `bind` 方法来绑定服务器到一个本地端口，开始监听新的连接请求。
  
- **Bootstrap**：
  - 使用 `connect` 方法来主动连接到远程服务器的地址和端口。

#### 5. 配置选项

- **ServerBootstrap**：
  - 可以设置与服务器端相关的配置选项，如 `childOption`，这些选项将应用于所有新接受的连接。
  
- **Bootstrap**：
  - 可以设置与客户端相关的配置选项，如 `option`，这些选项将应用于客户端连接。

## NioServerSocketChannel 和 NioSocketChannel

`NioServerSocketChannel` 和 `NioSocketChannel` 是 Netty 中用于处理基于 Java NIO 的网络通信的两个核心类。它们分别代表了服务器端的监听通道和客户端的连接通道。

### NioServerSocketChannel

`NioServerSocketChannel` 是一个基于 Java NIO 的 `ServerSocketChannel` 的实现，用于服务器端监听新的 TCP 连接请求。

#### 主要特点：

- **服务器端监听**：它用于创建一个可以监听网络端口的服务器端通道，等待客户端的连接请求。
- **非阻塞模式**：与 Java NIO 的 `ServerSocketChannel` 类似，`NioServerSocketChannel` 在内部使用非阻塞模式，这意味着它不会阻塞线程来等待连接请求，而是使用选择器（Selector）来监听事件。
- **事件驱动**：当有新的连接请求到达时，`NioServerSocketChannel` 会生成一个事件，并由 `EventLoop` 线程处理。
- **通道初始化**：在 `ServerBootstrap` 中配置 `NioServerSocketChannel` 时，通常会通过 `ChannelInitializer` 来添加特定的处理器（`ChannelHandler`），用于处理新接受的连接。

#### 使用场景：

`NioServerSocketChannel` 通常用于服务器端，用于创建可以接受客户端连接请求的监听端口。

### NioSocketChannel

`NioSocketChannel` 是一个基于 Java NIO 的 `SocketChannel` 的实现，用于客户端与服务器之间的 TCP 连接。

#### 主要特点：

- **客户端连接**：它用于创建一个可以与服务器建立连接的客户端通道。
- **非阻塞模式**：与 `NioServerSocketChannel` 类似，`NioSocketChannel` 也运行在非阻塞模式下，适用于高并发场景。
- **事件驱动**：`NioSocketChannel` 用于处理数据的读写事件，当数据可读或可写时，会触发相应的事件。
- **通道初始化**：在 `Bootstrap` 中配置 `NioSocketChannel` 时，同样会通过 `ChannelInitializer` 来添加特定的处理器（`ChannelHandler`），用于处理数据传输。

#### 使用场景：

`NioSocketChannel` 通常用于客户端，用于发起与服务器的连接，并进行数据的发送和接收。

### 对比

- **角色差异**：`NioServerSocketChannel` 主要用于服务器端监听和接受连接，而 `NioSocketChannel` 用于客户端发起连接和数据传输。
- **使用场景**：在 `ServerBootstrap` 和 `Bootstrap` 的配置中，分别使用 `NioServerSocketChannel` 和 `NioSocketChannel` 来满足服务器端和客户端的不同需求。
- **事件处理**：两者都基于事件驱动模型，能够高效地处理网络事件，如连接请求、数据读写等。

### 示例代码

**服务器端使用 NioServerSocketChannel**：

```java
ServerBootstrap b = new ServerBootstrap();
b.group(bossGroup, workerGroup)
 .channel(NioServerSocketChannel.class)
 .childHandler(new ChannelInitializer<SocketChannel>() {
     @Override
     public void initChannel(SocketChannel ch) {
         // 添加处理器
     }
 });
```

**客户端使用 NioSocketChannel**：

```java
Bootstrap b = new Bootstrap();
b.group(eventLoopGroup)
 .channel(NioSocketChannel.class)
 .handler(new ChannelInitializer<SocketChannel>() {
     @Override
     public void initChannel(SocketChannel ch) {
         // 添加处理器
     }
 });
```

### 其他实现

除了 `NioServerSocketChannel` 和 `NioSocketChannel`，Netty 还提供了其他几种基于不同传输类型的通道实现。每种实现针对不同的 I/O 模型和使用场景，具有不同的特点和性能特性。下面是一些常见的通道实现及其区别和特点：

1. OioServerSocketChannel 和 OioSocketChannel

- **特点**：
  - 基于 Java 的旧 I/O（OIO），使用阻塞 I/O 操作。
  - 适用于需要与旧系统兼容的场景，或者对 I/O 性能要求不高的简单应用。
  - `OioServerSocketChannel` 用于服务器端监听新的连接请求，而 `OioSocketChannel` 用于客户端连接。

- **区别**：
  - `OioServerSocketChannel` 和 `OioSocketChannel` 在处理 I/O 操作时会阻塞线程，这可能导致在高负载下性能下降。
  - 与 `NioServerSocketChannel` 和 `NioSocketChannel` 相比，它们不使用选择器（Selector），因此不支持非阻塞 I/O 和事件驱动模型。

2. EpollServerSocketChannel 和 EpollSocketChannel

- **特点**：
  - 仅适用于 Linux 系统。
  - 使用 Linux 的 `epoll` 系统调用来实现高效的 I/O 事件通知，相比 NIO 的 `Selector`，在高负载下性能更优。
  - `EpollServerSocketChannel` 用于服务器端监听新的连接请求，而 `EpollSocketChannel` 用于客户端连接。

- **区别**：
  - `EpollServerSocketChannel` 和 `EpollSocketChannel` 专为 Linux 系统设计，利用 `epoll` 的高效性能，特别适合构建高性能的网络服务器。
  - 它们不适用于非 Linux 系统，因为 `epoll` 是 Linux 特有的系统调用。

3. KQueueServerSocketChannel 和 KQueueSocketChannel

- **特点**：
  - 仅适用于类 Unix 系统，如 macOS 和 BSD 系统。
  - 使用 `kqueue` 系统调用来实现高效的 I/O 事件通知。
  - `KQueueServerSocketChannel` 用于服务器端监听新的连接请求，而 `KQueueSocketChannel` 用于客户端连接。

- **区别**：
  - `KQueueServerSocketChannel` 和 `KQueueSocketChannel` 类似于 `EpollServerSocketChannel` 和 `EpollSocketChannel`，但它们是为支持 `kqueue` 的系统设计的。
  - 它们提供了与 `epoll` 类似的性能优势，但仅限于特定的类 Unix 系统。

Netty 提供了多种通道实现，以适应不同的操作系统和性能需求。`NioServerSocketChannel` 和 `NioSocketChannel` 是最通用的实现，适用于大多数现代操作系统。`OioServerSocketChannel` 和 `OioSocketChannel` 提供了与旧系统的兼容性，但性能较低。`EpollServerSocketChannel` 和 `EpollSocketChannel` 以及 `KQueueServerSocketChannel` 和 `KQueueSocketChannel` 则为 Linux 和类 Unix 系统提供了更高效的 I/O 操作，特别适合高负载的网络服务器。

## ChannelInitializer

想象一下，`ChannelInitializer` 就像是一个餐厅的迎宾员和领班的结合体。当一个顾客（客户端）进入餐厅（建立连接）时，迎宾员（`ChannelInitializer`）会迎接他们，并告诉领班（`ChannelPipeline`）需要为这个顾客准备哪些服务（处理器 `ChannelHandler`）。

### 通俗解释

- **迎宾员和领班**：在餐厅（Netty 服务器或客户端）中，迎宾员（`ChannelInitializer`）负责迎接新来的顾客（新的连接）。一旦顾客坐下（连接被接受），迎宾员就会通知领班（`ChannelPipeline`），告诉他们需要为这个顾客提供哪些服务。

- **服务（处理器）**：领班（`ChannelPipeline`）根据迎宾员（`ChannelInitializer`）的指示，为顾客（连接）安排一系列服务（处理器 `ChannelHandler`）。这些服务可能包括点餐（数据接收）、上菜（数据处理）、结账（连接关闭）等。

- **初始化过程**：迎宾员（`ChannelInitializer`）在顾客（连接）到来时只工作一次，一旦顾客的全部服务（处理器）都安排好了，迎宾员就不再需要了，可以去迎接下一个顾客（新的连接）。

### 实际应用

在实际的 Netty 应用中，当一个新的客户端尝试连接到服务器时，服务器会创建一个新的 `Channel`（通道）。`ChannelInitializer` 就在这个时候介入，它负责向这个新创建的 `Channel` 的 `ChannelPipeline` 中添加一系列的处理器（`ChannelHandler`）。这些处理器定义了如何处理数据，比如读取数据、编码数据、业务逻辑处理等。

一旦这些处理器被添加到 `ChannelPipeline` 中，`ChannelInitializer` 的任务就完成了，它会从 `ChannelPipeline` 中移除自己，让通道准备好接收和发送数据。

`ChannelInitializer` 是 Netty 中的一个重要组件，用于在 `Channel`（通道）注册到 `EventLoop`（事件循环）时初始化它。这个过程通常发生在新的连接被接受或新的客户端连接被建立时。`ChannelInitializer` 允许开发者添加自定义的处理器（`ChannelHandler`）到 `ChannelPipeline`（通道管道）中，这些处理器定义了如何处理通过该通道传输的数据。

### 主要作用

- **初始化通道**：当一个新的 `Channel` 被创建并注册到 `EventLoop` 上时，`ChannelInitializer` 负责初始化该 `Channel`。初始化通常包括添加一个或多个 `ChannelHandler` 到 `ChannelPipeline` 中。
  
- **添加处理器**：`ChannelInitializer` 实现了 `ChannelInboundHandler` 接口，但它的生命周期方法（如 `channelRegistered` 和 `channelUnregistered`）被重写以执行初始化操作。一旦 `Channel` 初始化完成，`ChannelInitializer` 通常会从 `ChannelPipeline` 中移除自己，避免影响后续的数据处理。

### 使用场景

`ChannelInitializer` 通常在 `ServerBootstrap` 或 `Bootstrap` 的配置过程中使用，以设置服务器端或客户端的通道初始化逻辑。

- **服务器端**：在服务器端，`ChannelInitializer` 用于添加处理新接受连接的 `ChannelHandler`，如编解码器、业务逻辑处理器等。
  
- **客户端**：在客户端，`ChannelInitializer` 用于添加处理连接建立、数据传输等任务的 `ChannelHandler`。

### 示例代码

以下是一个简单的 `ChannelInitializer` 实现示例，用于添加一个简单的处理器到 `ChannelPipeline`：

```java
public class MyChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) {
        // 添加处理器到 ChannelPipeline
        ch.pipeline().addLast(new MyHandler());
    }
}

// 在 ServerBootstrap 或 Bootstrap 中使用
ServerBootstrap b = new ServerBootstrap();
b.group(bossGroup, workerGroup)
 .channel(NioServerSocketChannel.class)
 .childHandler(new MyChannelInitializer()); // 使用自定义的 ChannelInitializer
```

## ChannelPipeline

`ChannelPipeline` 代表了一个 `Channel`（通道）的处理器（`ChannelHandler`）链。每个 `Channel` 都有一个与之关联的 `ChannelPipeline`，用于处理通过该 `Channel` 传输的所有数据和事件。ChannelPipeline 内部维护了一个 ChannelHandler 的双向链表。

### 主要特点

- **处理器链**：`ChannelPipeline` 维护了一个处理器（`ChannelHandler`）的链表，这些处理器定义了数据如何被处理。数据在 `ChannelPipeline` 中从头到尾流动，每个处理器可以对数据进行读取、修改、转发等操作。

- **事件传播**：`ChannelPipeline` 负责事件的传播。当数据或事件到达 `Channel` 时，`ChannelPipeline` 会决定哪个处理器首先接收到这些数据或事件，并按顺序传递给链中的下一个处理器。

- **动态性**：`ChannelPipeline` 允许在运行时动态地添加或移除处理器。这意味着你可以根据需要调整数据处理流程，而无需关闭和重新创建 `Channel`。

### 关键概念

- **入站（Inbound）处理器**：处理从远程节点到本地节点的数据流动，例如读取操作。
- **出站（Outbound）处理器**：处理从本地节点到远程节点的数据流动，例如写入操作。

### 使用场景

- **数据处理**：通过在 `ChannelPipeline` 中添加自定义的 `ChannelHandler`，你可以实现数据的编解码、日志记录、异常处理、业务逻辑处理等功能。
- **协议实现**：在构建协议服务器或客户端时，`ChannelPipeline` 允许你定义协议的处理流程，每个处理器可以处理协议的一个特定部分。

### 示例

假设你正在构建一个简单的 HTTP 服务器，你可能会在 `ChannelPipeline` 中添加以下处理器：

1. **HttpServerCodec**：Netty 提供的处理器，用于处理 HTTP 请求和响应的编解码。
2. **HttpObjectAggregator**：将多个部分的 HTTP 消息聚合成完整的 HTTP 消息。
3. **HttpRequestHandler**：处理完整的 HTTP 请求，并生成响应。

## ChannelHandler

`ChannelHandler` 是 Netty 中用于处理数据和事件的接口，它允许开发者定义网络通信的业务逻辑。通过实现和组合不同的 `ChannelHandler`，可以构建出功能丰富、灵活的网络应用程序。无论是数据的编解码、协议的实现，还是异常的处理，`ChannelHandler` 都是实现这些功能的关键组件。

### 示例

假设你正在构建一个简单的 HTTP 服务器，你可能会创建一个自定义的 `ChannelHandler` 来处理 HTTP 请求：

```java
public class MyHttpHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // 处理接收到的 HTTP 请求
        FullHttpRequest request = (FullHttpRequest) msg;
        // 处理请求逻辑...
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 处理异常情况
        cause.printStackTrace();
        ctx.close();
    }
}
```

### ChannelInboundHandlerAdapter 和 ChannelOutboundHandlerAdapter

`ChannelInboundHandlerAdapter` 和 `ChannelOutboundHandlerAdapter` 是 Netty 提供的两个辅助类，它们分别实现了 `ChannelInboundHandler` 和 `ChannelOutboundHandler` 接口。这两个类为处理入站和出站事件提供了默认的空实现，使得开发者可以专注于实现自己关心的特定事件处理逻辑。

#### ChannelInboundHandlerAdapter

`ChannelInboundHandlerAdapter` 是处理入站事件的辅助类。入站事件包括数据接收、连接激活、异常发生等。通过继承 `ChannelInboundHandlerAdapter`，开发者可以重写以下方法来处理这些事件：

- `channelRegistered`：通道注册到 `EventLoop` 时调用。
- `channelUnregistered`：通道从 `EventLoop` 注销时调用。
- `channelActive`：通道变为活跃状态时调用。
- `channelInactive`：通道变为非活跃状态时调用。
- `channelRead`：从远程节点接收到数据时调用。
- `channelReadComplete`：读操作完成时调用。
- `channelWritabilityChanged`：通道的可写状态发生变化时调用。
- `exceptionCaught`：捕获到异常时调用。

#### ChannelOutboundHandlerAdapter

`ChannelOutboundHandlerAdapter` 是处理出站事件的辅助类。出站事件包括数据发送、连接关闭、通道刷新等。继承 `ChannelOutboundHandlerAdapter` 后，开发者可以重写以下方法来处理这些事件：

- `bind(ChannelHandlerContext, SocketAddress, ChannelPromise)`：绑定到给定地址。
- `connect(ChannelHandlerContext, SocketAddress, SocketAddress, ChannelPromise)`：连接到远程节点。
- `disconnect(ChannelHandlerContext, ChannelPromise)`：断开连接。
- `close(ChannelHandlerContext, ChannelPromise)`：关闭通道。
- `deregister(ChannelHandlerContext, ChannelPromise)`：从 `EventLoop` 注销通道。
- `read(ChannelHandlerContext)`：请求读取数据。
- `write(ChannelHandlerContext, Object, ChannelPromise)`：写数据到远程节点。
- `flush(ChannelHandlerContext)`：将之前写入的数据刷新到远程节点。

使用场景

- **自定义事件处理逻辑**：通过继承这两个类，开发者可以只关注自己需要处理的事件，而忽略其他事件的处理。
- **业务逻辑实现**：在重写的方法中实现业务逻辑，如数据处理、连接管理、异常处理等。

#### 示例

以下是一个简单的示例，展示了如何使用这两个辅助类：

```java
public class MyInboundHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // 处理接收到的数据
        System.out.println("Received data: " + msg);
        // 释放消息资源
        ReferenceCountUtil.release(msg);
    }
}

public class MyOutboundHandler extends ChannelOutboundHandlerAdapter {
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        // 自定义写入逻辑
        // 例如，可以在这里添加一些数据处理或验证逻辑
        System.out.println("Writing data: " + msg);
        // 调用父类的 write 方法继续正常的写入流程
        super.write(ctx, msg, promise);
    }
}
```

### Netty 内置的一些常用的 ChannelHandler

Netty 提供了一系列内置的 `ChannelHandler` 实现，这些实现覆盖了从数据编解码到协议处理的广泛需求。以下是一些常用的内置 `ChannelHandler`：

1. 编解码器（Codec）

- **ByteToMessageDecoder**：将接收到的字节解码为消息。
- **MessageToByteEncoder**：将消息编码为字节。
- **StringDecoder** 和 **StringEncoder**：用于解码和编码字符串。
- **ObjectDecoder** 和 **ObjectEncoder**：用于解码和编码 Java 对象。
- **LengthFieldBasedFrameDecoder** 和 **LengthFieldPrepender**：用于处理基于长度字段的协议帧。

2. 协议处理器

- **HttpServerCodec**：用于处理 HTTP 请求和响应的编解码。
- **HttpObjectAggregator**：将多个 HTTP 消息部分聚合成一个完整的 HTTP 消息。
- **HttpContentCompressor**：用于压缩 HTTP 内容。

3. 业务逻辑处理器

- **SimpleChannelInboundHandler**：处理接收到的消息，并在消息处理完毕后自动释放消息。
- **ChannelInboundHandlerAdapter**：提供了一个基础的处理器实现，可以覆盖其方法来处理各种事件。

4. 异常处理器

- **ExceptionHandler**：用于处理 `ChannelPipeline` 中发生的异常。

5. 业务逻辑辅助处理器

- **ChannelDuplexHandler**：同时实现了 `ChannelInboundHandler` 和 `ChannelOutboundHandler` 接口，可以处理入站和出站事件。
- **ChannelOutboundHandlerAdapter**：提供了一个基础的出站处理器实现，可以覆盖其方法来处理出站事件。

6. 安全处理器

- **SslHandler**：用于处理 SSL/TLS 加密和解密。

7. 流量控制处理器

- **ChunkedWriteHandler**：用于支持大文件传输，如支持 HTTP 的 chunked 编码传输。
- **WriteBufferWaterMarkHandler**：用于控制写缓冲区的高低水位线，防止内存溢出。

### ChannelHandler 的状态

在 Netty 中，`ChannelHandler` 实例本身并不是单例的。每个 `Channel` 都有自己的 `ChannelPipeline`，而每个 `ChannelPipeline` 可以包含多个 `ChannelHandler` 实例。这意味着，对于每个连接的 `Channel`，其 `ChannelPipeline` 中的 `ChannelHandler` 实例都是独立的。

- **每个 `Channel` 有独立的 `ChannelHandler` 实例**：当一个 `Channel` 被创建时，它的 `ChannelPipeline` 会根据配置添加 `ChannelHandler` 实例。这些实例是为该 `Channel` 专门创建的，与其他 `Channel` 的 `ChannelHandler` 实例是分开的。

- **`ChannelHandler` 可以被多个 `Channel` 共享**：虽然每个 `Channel` 的 `ChannelHandler` 实例是独立的，但你可以设计 `ChannelHandler` 使其可以被多个 `Channel` 共享。这通常通过将 `ChannelHandler` 实现为无状态的，并确保它不持有任何特定于 `Channel` 的状态来实现。

设计建议

- **无状态的 `ChannelHandler`**：为了使 `ChannelHandler` 可以被多个 `Channel` 共享，应尽量设计无状态的 `ChannelHandler`。无状态意味着 `ChannelHandler` 不依赖于外部状态，不保存任何与特定 `Channel` 相关的数据。

- **线程安全**：如果 `ChannelHandler` 被多个线程（如不同的 `EventLoop`）访问，需要确保它是线程安全的。

示例

以下是一个简单的 `ChannelHandler` 实现，它被设计为无状态的，因此可以被多个 `Channel` 共享：

```java
public class MyHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // 处理接收到的数据
        // 由于没有保存任何状态，这个处理器可以被多个 Channel 共享
    }
}
```

## ChannelHandlerContext

`ChannelHandlerContext` 是 Netty 中的一个核心组件，它代表了 `ChannelHandler` 和 `ChannelPipeline` 之间的关联。每个 `ChannelHandler` 在被添加到 `ChannelPipeline` 时，都会与一个 `ChannelHandlerContext` 实例相关联。`ChannelHandlerContext` 提供了对 `Channel` 的引用，并且可以用来执行各种操作，如读写数据、管理事件传播等。

主要特点

- **关联 `ChannelHandler` 和 `Channel`**：`ChannelHandlerContext` 为 `ChannelHandler` 提供了对 `Channel` 的访问，允许 `ChannelHandler` 与 `Channel` 交互。

- **操作 `ChannelPipeline`**：通过 `ChannelHandlerContext`，`ChannelHandler` 可以操作其所在的 `ChannelPipeline`，例如添加或移除其他 `ChannelHandler`，或者触发事件。

- **事件传播**：`ChannelHandlerContext` 负责将事件（如读取数据、连接激活等）从 `ChannelPipeline` 中的一个 `ChannelHandler` 传递到下一个 `ChannelHandler`。

### 常用方法

1. 读写操作

- **`write(Object msg)`**：将消息写入到 `ChannelPipeline` 中的下一个 `ChannelOutboundHandler`。消息将被发送到远程节点。

- **`writeAndFlush(Object msg)`**：与 `write(Object msg)` 类似，但会立即刷新所有待写数据到远程节点。

2. 事件触发

- **`fireChannelRegistered()`**：触发 `channelRegistered` 事件到下一个 `ChannelInboundHandler`。

- **`fireChannelActive()`**：触发 `channelActive` 事件到下一个 `ChannelInboundHandler`。

- **`fireChannelRead(Object msg)`**：触发 `channelRead` 事件到下一个 `ChannelInboundHandler`。

- **`fireChannelReadComplete()`**：触发 `channelReadComplete` 事件到下一个 `ChannelInboundHandler`。

- **`fireExceptionCaught(Throwable cause)`**：触发 `exceptionCaught` 事件到下一个 `ChannelHandler`。

3. 获取 `Channel` 和 `ChannelPipeline`

- **`channel()`**：返回与该处理器关联的 `Channel` 实例。

- **`pipeline()`**：返回当前处理器所在的 `ChannelPipeline` 实例。

4. 管理 `ChannelPipeline`

- **`bind(SocketAddress localAddress)`**：绑定到给定的地址。

- **`connect(SocketAddress remoteAddress)`**：连接到远程节点。

- **`disconnect()`**：断开连接。

- **`close()`**：关闭通道。

- **`deregister()`**：从 `EventLoop` 注销通道。

5. 其他操作

- **`read()`**：请求读取数据。

- **`flush()`**：将之前写入的数据刷新到远程节点。

- **`isRemoved()`**：检查该处理器是否已从其 `ChannelPipeline` 中移除。

### 示例

以下是一个简单的 `ChannelHandler` 实现，展示了如何使用 `ChannelHandlerContext`：

```java
public class MyHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        // 当连接激活时，写入一条消息
        ctx.write("Hello, Netty!");
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // 处理接收到的数据
        System.out.println("Received: " + msg);
        // 将数据传递给下一个处理器
        ctx.fireChannelRead(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 处理异常
        cause.printStackTrace();
        ctx.close();
    }
}
```

`ChannelHandlerContext` 提供了丰富的接口，使得 `ChannelHandler` 能够灵活地处理数据和事件。通过这些方法，你可以执行读写操作、触发事件、管理 `ChannelPipeline`，以及执行其他与 `Channel` 相关的操作。掌握这些方法对于编写高效和可维护的 Netty 应用程序至关重要。

使用场景

- **数据传输**：通过 `ChannelHandlerContext` 发送数据到远程节点或从远程节点接收数据。
- **事件处理**：在 `ChannelHandler` 中，使用 `ChannelHandlerContext` 来触发事件到下一个处理器。
- **管理 `ChannelPipeline`**：动态地添加或移除 `ChannelHandler`，或者调整 `ChannelHandler` 的顺序。

## TaskQueue 自定义任务

Netty中的`TaskQueue`可以类比为一个特殊的待办事项列表，这个列表被设计用来帮助处理一些需要在后台悄悄完成的任务，而不干扰到主要的工作流程。想象一下，你在一个繁忙的餐厅工作，主要任务是接待顾客和上菜，但偶尔也需要处理一些不那么紧急的事情，比如整理菜单或者补充餐具。如果这些事情在接待顾客时做，就会影响服务速度，所以你决定把它们记在一个小本子上，等没有顾客的时候再处理。

**任务队列的通俗比喻**

在Netty中，`TaskQueue`就是那个小本子。Netty是一个处理网络通信的框架，它需要快速响应各种网络事件，比如接收数据、发送数据等。如果在处理这些网络事件的同时，还去做一些耗时的操作（比如访问数据库、进行复杂的计算等），就会让整个网络通信变慢，就像在接待顾客时去整理菜单一样。

所以，Netty设计了`TaskQueue`，当你有耗时的任务时，不是直接去做，而是把它们写到这个队列里。Netty有一个专门的工作人员（EventLoop线程），会在处理完所有紧急的网络事件后，查看这个队列，然后依次处理这些任务。这样，即使有耗时的任务，也不会影响到网络事件的处理速度。

在Netty中，你可以通过简单的代码把任务加入到`TaskQueue`中。比如，你正在处理一个网络请求，发现需要做一些耗时的操作，就可以这样做：

```java
Channel channel = ...; // 获取到一个Channel实例
EventLoop eventLoop = channel.eventLoop();

// 提交一个简单的任务
eventLoop.execute(new Runnable() {
    @Override
    public void run() {
        // 这里执行耗时操作
    }
});
```

注意事项

- **不要阻塞工作人员**：虽然`TaskQueue`很有用，但你提交的任务不应该阻塞工作人员（EventLoop线程）。如果任务执行时间太长，它仍然会影响整个网络通信的效率。

- **线程安全**：由于可能有多个地方同时向`TaskQueue`提交任务，所以这个队列是线程安全的，你不需要担心在多线程环境下使用它时会出现问题。

比如提交一个耗时比较长的任务给一个 worker 去异步执行。

主要特点和用途

1. **线程安全**：`TaskQueue`保证了任务的添加和执行是线程安全的，即使在多线程环境下，也不会出现数据竞争或不一致的问题。

2. **任务排队**：当有任务需要异步执行时，这些任务会被添加到`TaskQueue`中。`EventLoop`会按照队列的顺序来依次执行这些任务。

3. **异步执行**：`TaskQueue`允许开发者将耗时的操作（如数据库访问、复杂的计算等）异步地放入队列中，由`EventLoop`在适当的时候执行，这样可以避免阻塞I/O线程，提高应用的性能。

4. **与事件循环集成**：`TaskQueue`与Netty的事件循环机制紧密集成，确保了任务的执行不会影响到I/O事件的处理。

使用场景

- **延时任务**：可以将需要延时执行的任务加入到`TaskQueue`中，由`EventLoop`在指定时间后执行。

- **后台任务**：对于一些不紧急但需要在服务器端执行的后台任务，可以使用`TaskQueue`来异步处理。

- **资源释放**：在某些情况下，可能需要在连接关闭后释放一些资源，这些清理工作可以作为任务加入到`TaskQueue`中。

示例代码

```java
// 假设我们有一个EventLoopGroup和EventLoop
EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
EventLoop eventLoop = eventLoopGroup.next();

// 创建一个任务
Runnable task = () -> {
    // 执行一些耗时操作
    System.out.println("Task executed by " + Thread.currentThread().getName());
};

// 将任务添加到TaskQueue中
eventLoop.execute(task);
```

在这个例子中，`task`将被添加到与`eventLoop`关联的`TaskQueue`中，并在`eventLoop`的线程中执行。

注意事项

- `TaskQueue`中的任务执行顺序通常遵循先进先出（FIFO）的原则，但Netty的某些版本可能支持优先级队列或其他类型的队列。
- 在Netty 4.x版本中，`TaskQueue`是`EventExecutor`的一部分，而在Netty 5.x版本中，`TaskQueue`的概念被进一步抽象化，以支持更灵活的任务调度。

`TaskQueue`是Netty实现高效、异步任务处理的关键组件之一，它使得开发者可以轻松地将任务安排到合适的线程中执行，而无需担心线程管理和同步问题。

### 自定义普通任务

在Netty中，自定义普通任务通常意味着创建一个可以在`EventLoop`的线程中异步执行的任务。下面是一个简单的示例，演示如何在Netty中自定义并执行一个普通任务：

```java
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class NettyServer {

    private final int port;

    public NettyServer(int port) {
        this.port = port;
    }

    public void start() throws Exception {
        // 创建两个EventLoopGroup对象，一个用于接收连接，另一个用于处理连接的数据读写
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // 创建服务端启动助手
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class) // 指定使用的channel
             .childHandler(new ChannelInitializer<SocketChannel>() { // 设置childHandler来处理网络事件
                 @Override
                 protected void initChannel(SocketChannel ch) {
                     // 添加字符串解码器和编码器
                     ch.pipeline().addLast(new StringDecoder());
                     ch.pipeline().addLast(new StringEncoder());
                     // 添加自定义的处理器
                     ch.pipeline().addLast(new SimpleServerHandler());
                 }
             });

            // 绑定端口并同步等待成功，即启动服务端
            ChannelFuture f = b.bind(port).sync();

            // 等待服务端监听端口关闭
            f.channel().closeFuture().sync();
        } finally {
            // 关闭两个EventLoopGroup，释放资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 8080; // 服务器端口
        new NettyServer(port).start();
    }
}

class SimpleServerHandler extends io.netty.channel.ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(io.netty.channel.ChannelHandlerContext ctx) {
        // 当连接建立时，执行一个自定义任务
        ctx.channel().eventLoop().execute(new Runnable() {
            @Override
            public void run() {
                // 这里可以执行耗时操作，例如记录日志、计算等
                System.out.println("执行自定义任务，当前线程：" + Thread.currentThread().getName());
            }
        });
    }

    @Override
    public void channelRead(io.netty.channel.ChannelHandlerContext ctx, Object msg) {
        // 接收到消息后，简单地将接收到的字符串转换为大写并回写给客户端
        System.out.println("Server received: " + msg);
        ctx.write(msg);
    }

    @Override
    public void channelReadComplete(io.netty.channel.ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(io.netty.channel.ChannelHandlerContext ctx, Throwable cause) {
        // 出现异常时关闭连接
        cause.printStackTrace();
        ctx.close();
    }
}
```

在这个示例中，我们创建了一个Netty服务器，它监听指定的端口并接受客户端连接。当一个新的连接被建立时（`channelActive`方法被调用时），我们通过`ctx.channel().eventLoop().execute()`方法提交了一个自定义任务到`EventLoop`的线程中执行。这个自定义任务是一个简单的打印操作，用于演示如何在`EventLoop`的线程中执行任务。

请注意，这个示例仅用于演示如何提交自定义任务，并没有实现完整的客户端和服务器之间的通信逻辑。在实际应用中，你可能需要根据具体需求来扩展和调整代码。

### 自定义定时任务

在Netty中，你可以使用`ScheduledExecutorService`来安排定时任务。Netty的`EventLoop`提供了`schedule`、`scheduleAtFixedRate`和`scheduleWithFixedDelay`方法来安排这些任务。下面是一个自定义定时任务的示例，演示如何在Netty中安排一个定时任务，该任务每隔一定时间间隔执行一次。

创建一个Netty应用程序，其中包含自定义定时任务的执行：

```java
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class NettyServer {

    private final int port;

    public NettyServer(int port) {
        this.port = port;
    }

    public void start() throws Exception {
        // 创建两个EventLoopGroup对象，一个用于接收连接，另一个用于处理连接的数据读写
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // 创建服务端启动助手
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class) // 指定使用的channel
             .childHandler(new ChannelInitializer<SocketChannel>() { // 设置childHandler来处理网络事件
                 @Override
                 protected void initChannel(SocketChannel ch) {
                     // 添加字符串解码器和编码器
                     ch.pipeline().addLast(new StringDecoder());
                     ch.pipeline().addLast(new StringEncoder());
                     // 添加自定义的处理器
                     ch.pipeline().addLast(new SimpleServerHandler());
                 }
             });

            // 绑定端口并同步等待成功，即启动服务端
            ChannelFuture f = b.bind(port).sync();

            // 等待服务端监听端口关闭
            f.channel().closeFuture().sync();
        } finally {
            // 关闭两个EventLoopGroup，释放资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 8080; // 服务器端口
        new NettyServer(port).start();
    }
}

class SimpleServerHandler extends io.netty.channel.ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(io.netty.channel.ChannelHandlerContext ctx) {
        // 当连接建立时，安排一个定时任务
        ctx.channel().eventLoop().scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                // 这里可以执行定时任务，例如发送心跳包、清理资源等
                System.out.println("执行定时任务，当前线程：" + Thread.currentThread().getName());
            }
        }, 0, 5, TimeUnit.SECONDS); // 每5秒执行一次
    }

    @Override
    public void channelRead(io.netty.channel.ChannelHandlerContext ctx, Object msg) {
        // 接收到消息后，简单地将接收到的字符串转换为大写并回写给客户端
        System.out.println("Server received: " + msg);
        ctx.write(msg);
    }

    @Override
    public void channelReadComplete(io.netty.channel.ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(io.netty.channel.ChannelHandlerContext ctx, Throwable cause) {
        // 出现异常时关闭连接
        cause.printStackTrace();
        ctx.close();
    }
}
```

在这个示例中，当一个新的连接被建立时（`channelActive`方法被调用时），我们使用`ctx.channel().eventLoop().scheduleAtFixedRate()`方法安排了一个定时任务。这个任务是一个简单的打印操作，每隔5秒执行一次，用于演示如何在Netty中安排定时任务。

请注意，这个示例仅用于演示如何安排定时任务，并没有实现完整的客户端和服务器之间的通信逻辑。在实际应用中，你可能需要根据具体需求来扩展和调整代码。

## ChannelFuture

Netty 是一个高性能的异步事件驱动的网络应用程序框架，用于快速开发可维护的高性能协议服务器和客户端。在 Netty 中，`ChannelFuture` 是一个非常核心的概念，它用于处理异步操作的结果。

1. 异步操作与同步操作的区别

在传统的同步编程模型中，操作会阻塞当前线程直到操作完成。而在异步编程模型中，操作会立即返回，而实际的工作会在后台线程中完成，当前线程可以继续执行其他任务。

2. ChannelFuture 的作用

在 Netty 中，所有的 I/O 操作都是异步的。这意味着当调用一个方法如 `channel.write()` 或 `channel.connect()` 时，操作会立即返回一个 `ChannelFuture` 对象，而实际的 I/O 操作会在之后的某个时间点完成。

`ChannelFuture` 提供了以下关键功能：

- **状态监听**：你可以添加 `ChannelFutureListener` 到 `ChannelFuture`，当操作完成时，监听器的 `operationComplete` 方法会被调用。这允许你执行一些操作，比如在数据发送完毕后关闭连接。

- **非阻塞结果查询**：你可以查询 `ChannelFuture` 的状态来了解操作是否已经完成、是否成功、或者是否失败。

- **异常处理**：如果操作失败，`ChannelFuture` 会持有导致失败的异常，你可以通过调用 `cause()` 方法来获取它。

3. 使用示例

下面是一个简单的例子，演示如何使用 `ChannelFuture`：

```java
// 获取Channel实例
Channel channel = ...;

// 异步写数据
ChannelFuture future = channel.write(someObject);

// 添加监听器，处理操作完成后的逻辑
future.addListener(new ChannelFutureListener() {
    @Override
    public void operationComplete(ChannelFuture future) {
        if (future.isSuccess()) {
            System.out.println("Write successful.");
        } else {
            System.err.println("Write failed.");
            future.cause().printStackTrace();
        }
    }
});

// 或者使用lambda表达式简化
future.addListener(future1 -> {
    if (future1.isSuccess()) {
        System.out.println("Write successful.");
    } else {
        System.err.println("Write failed.");
        future1.cause().printStackTrace();
    }
});
```

4. 关键点总结

- **异步性**：`ChannelFuture` 是异步操作完成的标志，它允许你继续执行其他任务，而不是阻塞等待操作完成。
- **监听器**：通过添加 `ChannelFutureListener`，你可以在操作完成时得到通知，并执行相应的逻辑。
- **非阻塞查询**：你可以随时查询 `ChannelFuture` 的状态，了解操作是否完成、成功或失败。
- **异常处理**：如果操作失败，`ChannelFuture` 会持有异常信息，你可以通过它来诊断问题。

理解 `ChannelFuture` 的工作原理和使用方法对于编写高效、可维护的 Netty 应用程序至关重要。

一个完整的代码示例如下

更清晰地展示如何使用 `ChannelFuture` 来添加事件监听器，这样可以更好地体现其事件监听的特性。

服务器端代码示例（包含 ChannelFuture 事件监听）

```java
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class EchoServer {
    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public void start() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .childHandler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 protected void initChannel(SocketChannel ch) {
                     ch.pipeline().addLast(new StringDecoder(), new StringEncoder(), new EchoServerHandler());
                 }
             });

            // 绑定端口并异步等待成功，即启动服务端
            ChannelFuture f = b.bind(port).sync();
            System.out.println("Server started and listening on " + f.channel().localAddress());

            // 添加监听器来处理 ChannelFuture
            f.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) {
                    if (future.isSuccess()) {
                        System.out.println("Server bound successfully.");
                    } else {
                        System.err.println("Server bound failed.");
                        future.cause().printStackTrace();
                    }
                }
            });

            // 等待服务端监听端口关闭
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;
        new EchoServer(port).start();
    }
}

class EchoServerHandler extends io.netty.channel.ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(io.netty.channel.ChannelHandlerContext ctx, Object msg) {
        ctx.write(msg); // 回声消息给发送者
    }

    @Override
    public void channelReadComplete(io.netty.channel.ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(io.netty.channel.ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
```

我们使用 `bind(port).sync()` 方法异步地绑定端口，并通过 `addListener` 方法添加了一个 `ChannelFutureListener`。当绑定操作完成时，`operationComplete` 方法会被调用，允许我们根据操作的成功与否执行相应的逻辑。

客户端代码可以类似地修改，以展示如何在客户端使用 `ChannelFuture` 监听连接操作的完成。

通过这种方式，`ChannelFuture` 允许你以非阻塞的方式处理异步事件，使得你的应用程序能够更加高效地处理 I/O 操作。

### 事件监听

`ChannelFuture` 主要用于监听异步操作的结果，它提供了以下几种事件的监听方式：

1. **操作完成（Operation Complete）**:
   - 这是最基本的事件，表示异步操作已经完成。无论操作是成功还是失败，都会触发这个事件。
   - 通过添加 `ChannelFutureListener`，可以在操作完成时得到通知。

2. **操作成功（Operation Success）**:
   - 当异步操作成功完成时，会触发这个事件。
   - 在 `ChannelFutureListener` 的 `operationComplete` 方法中，你可以通过 `future.isSuccess()` 判断操作是否成功。

3. **操作失败（Operation Failure）**:
   - 如果在执行异步操作过程中发生异常或错误，会触发操作失败事件。
   - 在 `operationComplete` 方法中，你可以通过 `future.isSuccess()` 判断操作是否失败，并通过 `future.cause()` 获取异常信息。

4. **取消操作（Operation Cancellation）**:
   - 如果异步操作被取消，会触发这个事件。
   - 通常，你可以调用 `ChannelFuture.cancel()` 方法来尝试取消一个操作。如果操作被成功取消，`isCancelled()` 方法会返回 `true`。

5. **操作可写（Operation Writable）**:
   - 在某些情况下，如使用 `ChannelHandlerContext.writeAndFlush()` 方法时，你可能对写操作何时完成感兴趣。
   - 通过监听写操作的 `ChannelFuture`，你可以知道何时可以安全地进行下一个写操作。

示例代码

```java
ChannelFuture future = channel.write(someObject);
future.addListener(new ChannelFutureListener() {
    @Override
    public void operationComplete(ChannelFuture future) {
        if (future.isSuccess()) {
            System.out.println("Operation succeeded.");
        } else {
            System.err.println("Operation failed.");
            future.cause().printStackTrace();
        }
    }
});
```

在这个示例中，我们监听了操作完成事件，并根据操作的成功与否打印不同的信息。

注意事项

- `ChannelFutureListener` 只能被触发一次，一旦操作完成，监听器就会被移除。
- 通常，监听器中的代码应该尽可能轻量，避免执行耗时或复杂的操作，因为它们是在 I/O 线程中执行的。

`ChannelFuture` 的这些事件监听机制是 Netty 异步编程模型的核心部分，允许开发者高效地处理网络操作的结果，而无需阻塞等待。

### 示例1

```java
ChannelFuture f = b.bind(port).sync();
```

- **`b`**：这里假设`b`是一个`ServerBootstrap`的实例，它是Netty中用于配置和启动服务器端Channel的辅助类。

- **`.bind(port)`**：调用`bind`方法告诉Netty在指定的`port`端口上监听新的连接。这个方法会立即返回一个`ChannelFuture`实例，表示这个绑定操作的异步结果。

- **`.sync()`**：`sync()`方法是`ChannelFuture`的一个方法，它会阻塞当前线程直到绑定操作完成。如果绑定成功，返回的`ChannelFuture`会标记为成功完成；如果绑定失败（比如端口已被占用），则会抛出异常。

综上所述，第一行代码的作用是启动Netty服务器在指定端口上监听连接，并等待这个操作完成。如果操作成功，它会返回一个`ChannelFuture`对象，你可以用这个对象来进一步操作或监控Channel的状态。

### 示例2

```java
f.channel().closeFuture().sync();
```

- **`f.channel()`**：`ChannelFuture`对象`f`代表了一个异步操作的结果，通过调用它的`channel()`方法可以获取到与之关联的`Channel`实例。`Channel`是Netty中用于网络通信的抽象，代表了一个打开的连接。

- **`.closeFuture()`**：`closeFuture()`方法返回一个`ChannelFuture`，它在Channel关闭时完成。这个方法通常用于等待Channel关闭的事件，这样你就可以在Channel关闭后执行一些清理工作。

- **`.sync()`**：再次调用`sync()`方法，它会阻塞当前线程直到Channel关闭完成。如果Channel已经关闭，这个调用会立即返回。

第二行代码的作用是等待服务器Channel关闭。在实际应用中，你可能需要在关闭Channel之前完成一些清理工作，比如释放资源、通知其他组件等。通过调用`closeFuture().sync()`，你可以确保在Channel真正关闭之后再继续执行后续代码。

## Unpooled

`Unpooled` 是 Netty 中的一个工具类，它提供了一组静态方法来创建和操作未池化的（即非池化的）缓冲区。Netty 通过池化技术来优化内存使用和性能，但有时候你可能需要创建一个不使用池化的缓冲区，这时 `Unpooled` 类就非常有用。

以下是 `Unpooled` 类中一些常用的方法：

1. **buffer()**:
    - 创建一个新的 `ByteBuf` 实例。默认情况下，这个缓冲区是动态扩展的，可以根据需要增长。

2. **directBuffer()**:
    - 创建一个新的直接缓冲区（Direct ByteBuf），它在堆外分配内存，减少了数据在 JVM 和操作系统之间的拷贝次数，适用于 I/O 操作。

3. **wrappedBuffer(byte[] array)**:
    - 将一个普通的 Java 字节数组包装成一个 `ByteBuf`。这个缓冲区不会复制数据，而是直接使用传入的数组。

4. **copiedBuffer(byte[] array)**:
    - 创建一个新的 `ByteBuf`，并复制传入的数组内容。这种方式下，原始数组可以被修改而不影响 `ByteBuf`。

5. **wrappedBuffer(ByteBuffer buffer)**:
    - 将一个 `ByteBuffer` 包装成一个 `ByteBuf`。这允许在 Netty 和使用 `ByteBuffer` 的旧代码之间进行互操作。

6. **copiedBuffer(ByteBuffer buffer)**:
    - 创建一个新的 `ByteBuf`，并复制 `ByteBuffer` 的内容。这允许在不影响原始 `ByteBuffer` 的情况下操作数据。

7. **heapBuffer(int initialCapacity)** 和 **heapBuffer(int initialCapacity, int maxCapacity)**:
    - 创建一个新的堆缓冲区（Heap ByteBuf），可以指定初始容量和最大容量。

8. **directBuffer(int initialCapacity)** 和 **directBuffer(int initialCapacity, int maxCapacity)**:
    - 创建一个新的直接缓冲区，可以指定初始容量和最大容量。

使用 `Unpooled` 类创建的缓冲区，通常用于那些生命周期短暂、使用频率不高的场景，比如发送一次性的消息。对于需要频繁创建和销毁的缓冲区，使用池化的缓冲区（通过 `PooledByteBufAllocator`）会更加高效，因为它可以重用缓冲区实例，减少内存分配和垃圾回收的开销。

举个例子，如果你需要发送一个简单的消息，可以这样做：

```java
ByteBuf buffer = Unpooled.copiedBuffer("Hello, Netty!", Charset.defaultCharset());
Channel channel = ... // 获取到一个Channel实例
channel.writeAndFlush(buffer);
```

在这个例子中，我们创建了一个包含字符串 "Hello, Netty!" 的 `ByteBuf`，然后将其发送到一个 `Channel`。使用 `copiedBuffer` 确保了数据的独立性，即使原始数据被修改，发送的数据也不会受到影响。

总之，`Unpooled` 类为创建和操作缓冲区提供了便捷的方法，特别是在需要非池化缓冲区的场景中非常有用。

## netty 的 ByteBuf 与 Java NIO 的 Buffer 的区别

Netty 的 `ByteBuf` 和 Java NIO 的 `Buffer` 都是用于处理字节数据的容器，但它们在设计和使用上有一些显著的区别。下面我将详细解释这些差异。

### Java NIO 的 Buffer

Java NIO 的 `Buffer` 是 Java NIO 套件中用于处理数据的基础类。它是一个抽象类，提供了以下几种类型的缓冲区：

- `ByteBuffer`
- `CharBuffer`
- `DoubleBuffer`
- `FloatBuffer`
- `IntBuffer`
- `LongBuffer`
- `ShortBuffer`

这些缓冲区类型分别对应不同的数据类型。`ByteBuffer` 是最常用的，因为它直接用于处理字节数据。

**特点**：

- **固定容量**：Java NIO 的缓冲区一旦创建，其容量是固定的，如果需要更大的容量，必须创建一个新的缓冲区。
- **位置和界限**：`Buffer` 类包含三个关键属性：位置（position）、限制（limit）和容量（capacity）。这些属性定义了缓冲区的状态，并在读写操作中不断变化。
- **直接与非直接缓冲区**：缓冲区可以是直接的（直接与操作系统底层缓冲区关联）或非直接的（位于 JVM 堆上）。
- **手动管理**：使用 Java NIO 的缓冲区需要手动管理位置指针，以及在读写之间切换（flip, clear, rewind 等方法）。

### Netty 的 ByteBuf

Netty 的 `ByteBuf` 是一个专门为网络应用设计的字节容器，它解决了 Java NIO `ByteBuffer` 的一些限制，并提供了更灵活和强大的功能。

**特点**：

- **动态容量**：`ByteBuf` 可以动态扩展容量，无需创建新的缓冲区实例。
- **引用计数**：`ByteBuf` 支持引用计数，可以高效地管理内存，特别是在池化缓冲区时。
- **透明的零拷贝**：`ByteBuf` 支持零拷贝操作，如 `slice`, `duplicate`, `readRetainedSlice` 等，这些操作返回新的 `ByteBuf` 实例，但不会复制数据。
- **简洁的API**：`ByteBuf` 提供了更简洁直观的API，不需要手动管理位置指针和界限，读写操作自动管理这些状态。
- **池化**：Netty 提供了缓冲区池化机制，可以重用缓冲区实例，减少内存分配和垃圾回收的开销。

Netty 的 `ByteBuf` 在设计上更加灵活和高效，特别适合于高性能网络应用。它解决了 Java NIO `ByteBuffer` 的一些限制，如固定容量和手动管理状态的复杂性。`ByteBuf` 的动态容量、简洁的API和零拷贝操作使得它在处理网络数据时更加方便和高效。而 Java NIO 的 `Buffer` 作为基础类，虽然功能较为基础，但在一些简单的应用场景中仍然适用。

### Java NIO ByteBuffer 示例

```java
import java.nio.ByteBuffer;

public class ByteBufferExample {
    public static void main(String[] args) {
        // 创建一个容量为1024字节的ByteBuffer
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        // 写入数据到ByteBuffer
        String str = "Hello, NIO!";
        buffer.put(str.getBytes());

        // 切换到读模式
        buffer.flip();

        // 读取数据
        byte[] data = new byte[buffer.limit()];
        buffer.get(data);
        System.out.println(new String(data));

        // 清空缓冲区，但不释放底层数组
        buffer.clear();
    }
}
```

### Netty ByteBuf 示例

```java
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class ByteBufExample {
    public static void main(String[] args) {
        // 创建一个容量为1024字节的ByteBuf
        ByteBuf buffer = Unpooled.buffer(1024);

        // 写入数据到ByteBuf
        String str = "Hello, Netty!";
        buffer.writeBytes(str.getBytes());

        // 读取数据
        byte[] data = new byte[buffer.readableBytes()];
        buffer.readBytes(data);
        System.out.println(new String(data));

        // 释放ByteBuf资源
        buffer.release();
    }
}
```

### 对比

- **容量和扩展**：在Java NIO中，ByteBuffer一旦创建，容量就固定了。如果需要更大的容量，必须创建一个新的ByteBuffer。而在Netty中，ByteBuf可以动态扩展，无需手动创建新的实例。

- **API简洁性**：在使用ByteBuffer时，需要手动调用`flip()`来切换读写模式，而读取数据后通常需要调用`clear()`或`compact()`来准备下一次写入。相比之下，ByteBuf的API更为简洁，读写操作自动管理内部状态，不需要手动切换模式。

- **内存管理**：ByteBuffer不支持引用计数，而ByteBuf支持。这意味着在Netty中，可以更有效地管理内存，尤其是在使用池化缓冲区时。

- **零拷贝**：Netty的ByteBuf支持零拷贝操作，例如`slice`和`duplicate`，这些操作可以创建新的视图而不复制数据。这在处理大型数据时非常有用，可以显著提高性能。

通过这些示例，你可以看到Netty的`ByteBuf`在处理网络数据时提供了更高级、更方便的抽象，而Java NIO的`ByteBuffer`则提供了更底层的控制，但需要更多的手动操作和管理。

# 使用 netty 作为 Http 服务器

示例代码

服务器端示例

```java
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class EchoServer {
    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public void start() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .childHandler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 protected void initChannel(SocketChannel ch) {
                     ch.pipeline().addLast(new EchoServerHandler());
                 }
             });

            ChannelFuture f = b.bind(port).sync();
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;
        new EchoServer(port).start();
    }
}
```

客户端示例

```java
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class EchoClient {
    private final String host;
    private final int port;

    public EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
             .channel(NioSocketChannel.class)
             .handler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 protected void initChannel(SocketChannel ch) {
                     ch.pipeline().addLast(new EchoClientHandler());
                 }
             });

            ChannelFuture f = b.connect(host, port).sync();
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        String host = "127.0.0.1";
        int port = 8080;
        new EchoClient(host, port).start();
    }
}
```

在这两个示例中，`EchoServerHandler` 和 `EchoClientHandler` 分别是服务器和客户端的处理器，用于处理接收到的数据。

`Bootstrap` 类是 Netty 中用于配置和启动网络应用程序的核心组件。`ServerBootstrap` 用于配置服务器，而 `Bootstrap` 用于配置客户端。它们都允许你设置线程模型、通道类型、通道选项和处理器链，从而灵活地构建出满足不同需求的网络应用程序。

# 基于 Netty 的群聊系统的实现

要实现一个基于Netty的群聊服务器，你需要创建一个服务器端程序，它能够接受多个客户端的连接，并将接收到的消息转发给所有连接的客户端。下面是一个简单的群聊服务器实现示例。

1. 创建服务器端的ChannelInitializer

首先，你需要创建一个`ChannelInitializer`来初始化每个连接的Channel。

```java
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;

public class ChatServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        
        // 使用换行符作为消息的分隔符
        pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Unpooled.copiedBuffer("$_".getBytes())));
        pipeline.addLast(new StringDecoder());
        pipeline.addLast(new StringEncoder());
        
        // 自定义的处理器，用于处理消息转发
        pipeline.addLast(new ChatServerHandler());
    }
}
```

2. 创建自定义的ChannelHandler

接下来，创建一个`ChannelHandler`来处理消息的接收和转发。

```java
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class ChatServerHandler extends SimpleChannelInboundHandler<String> {
    // 用于保存所有客户端的Channel
    public static ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        // 当新的客户端连接时，将其Channel加入到clients组中
        clients.add(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        // 当客户端断开连接时，从clients组中移除其Channel
        clients.remove(ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        // 当接收到消息时，转发给所有客户端
        for (Channel channel : clients) {
            if (channel != ctx.channel()) {
                channel.writeAndFlush(msg + "$_");
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 出现异常时关闭连接
        cause.printStackTrace();
        ctx.close();
    }
}
```

3. 启动服务器

最后，创建一个主类来启动服务器。

```java
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class ChatServer {
    private int port;

    public ChatServer(int port) {
        this.port = port;
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .childHandler(new ChatServerInitializer());

            b.bind(port).sync().channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 8080;
        }
        new ChatServer(port).run();
    }
}
```

这个简单的群聊服务器示例展示了如何使用Netty来创建一个可以接受多个客户端连接，并将接收到的消息广播给所有客户端的服务器。服务器使用`ChannelGroup`来管理所有连接的客户端，并在`ChatServerHandler`中处理消息的接收和转发。注意，这里使用了`$_`作为消息的分隔符，以确保消息的正确解析。

要运行这个示例，你需要将上述代码片段整合到相应的Java文件中，并确保Netty库已经添加到你的项目依赖中。然后，你可以通过运行`ChatServer`的`main`方法来启动服务器，并连接多个客户端进行测试。

## ChannelGroup

`ChannelGroup` 是 Netty 中用于管理一组 `Channel` 实例的集合。它提供了一种方便的方式来对一组连接进行操作，比如向所有连接的客户端发送消息、关闭所有连接或检查连接状态等。`ChannelGroup` 是线程安全的，可以在多个线程中安全地使用。

### 主要特点和用途

1. **线程安全**：`ChannelGroup` 是线程安全的，可以在多个线程中安全地添加或移除 `Channel`，以及对 `Channel` 执行操作。

2. **操作集合**：它提供了集合操作的接口，如添加、移除、清空 `Channel`，以及检查 `Channel` 是否存在于集合中。

3. **消息广播**：`ChannelGroup` 最常见的用途之一是向所有连接的客户端广播消息。你可以通过调用 `writeAndFlush` 方法来向集合中的每个 `Channel` 发送消息。

4. **自动管理**：当 `Channel` 关闭时，`ChannelGroup` 可以自动从集合中移除对应的 `Channel` 实例。这避免了需要手动管理每个 `Channel` 的生命周期。

### 如何使用 ChannelGroup

在 Netty 中，`ChannelGroup` 通常与 `ChannelHandler` 结合使用，特别是在处理群聊服务器或需要向多个客户端广播消息的场景中。

```java
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

// 创建一个全局唯一的ChannelGroup实例
ChannelGroup allChannels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

// 在ChannelHandler中使用ChannelGroup
public class ChatServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 当新的客户端连接时，将其Channel加入到ChannelGroup中
        allChannels.add(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 当客户端断开连接时，从ChannelGroup中移除其Channel
        allChannels.remove(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 当接收到消息时，转发给所有客户端
        allChannels.writeAndFlush(msg);
    }
}
```

- **自动移除**：当 `Channel` 关闭时，`ChannelGroup` 会自动从集合中移除对应的 `Channel`。这意味着你不需要手动调用移除方法，除非你有特殊需求需要立即从集合中移除 `Channel`。

- **线程安全操作**：由于 `ChannelGroup` 是线程安全的，你可以在任何线程中安全地调用它的方法，包括添加或移除 `Channel`，以及向 `Channel` 发送消息。

- **广播消息**：`ChannelGroup` 提供了 `writeAndFlush` 方法，允许你向集合中的所有 `Channel` 发送消息。这对于实现群聊功能非常有用。

通过使用 `ChannelGroup`，你可以简化对多个客户端连接的管理，特别是在需要向所有客户端广播消息的场景中，它提供了一种高效且线程安全的方式来处理这些操作。
















