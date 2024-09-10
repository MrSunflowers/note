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

# Java NIO 三大核心组件

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

## MappedByteBuffer

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

## 三大组件的关系

Channel 相当于连接，并不直接连接向资源，而是连接向 Buffer，一个 Channel 对应 一个 Buffer，Channel 和 Buffer 一样，是双向可读可写的。
Selector 相当于连接管理器，管理多个已注册在当前 Selector 中的 Channel，一个线程一般对应一个 Selector。程序切换到哪个 Channel，是操作系统内核事件通知的。
对数据的读写都需要经过 Buffer，即应用程序数据缓冲区，是 Java 程序与操作系统内核之间交换数据的桥梁。

## 

























