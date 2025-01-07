# java 应用程序 CPU 占用高问题排查

## 1. 使用 `top` 命令查看 CPU 使用情况

`top` 命令用于实时监控系统的整体性能和各个进程的 CPU、内存使用情况。通过 `top`，您可以快速识别出 CPU 占用较高的 Java 进程。

```bash
top
```

- **查看整体负载**：观察 `load average` 值，判断系统是否过载。
- **识别高 CPU 进程**：在 `top` 的输出中，按 `P` 键（默认按 CPU 使用率排序），查找占用 CPU 最高的 Java 进程（通常以 `java` 开头）。
- **记录进程 ID（PID）**：找到高 CPU 使用的 Java 进程后，记录其 PID。

示例输出
```
top - 15:30:45 up 21 days,  3:45,  1 user,  load average: 1.25, 1.33, 1.22
Tasks: 250 total,   1 running, 249 sleeping,   0 stopped,   0 zombie
%Cpu(s): 15.0 us,  5.0 sy,  0.0 ni, 78.0 id,  2.0 wa,  0.0 hi,  0.0 si,  0.0 st
KiB Mem :  8010432 total,  1234567 free,  2345678 used,  4429187 buff/cache
KiB Swap:        0 total,        0 free,        0 used.  4000000 avail Mem 

  PID USER      PR  NI    VIRT    RES    SHR S  %CPU %MEM     TIME+ COMMAND
12345 user      20   0  5000m  900m   200m S  90.0 11.2   123:45 java
...
```

## 2. 使用 `ps` 命令查找 Java 进程的线程信息

通过 `ps` 命令，可以获取到指定 Java 进程的线程信息，包括每个线程的 CPU 使用情况和线程 ID（TID）。

```bash
ps -mp <PID> -o THREAD,tid,time
```

参数解释
- `-m`：显示线程信息。
- `-p <PID>`：指定进程 ID。
- `-o THREAD,tid,time`：指定输出的列，包括线程状态、TID 和 CPU 使用时间。

示例输出
```
USER     %CPU PRI SCNT S     TID     TIME COMMAND
user     90.0  20   0  S    12346  123:45 java
user      5.0  20   0  S    12347    5:30 java
...
```

- **识别高 CPU 线程**：查找 `%CPU` 最高的线程。
- **记录 TID（线程 ID）**：记录高 CPU 线程的 TID。

## 3. 将线程 ID 转换为 16 进制

Java 的线程 ID 在 `jstack` 的输出中以 16 进制表示。因此，需要将之前获取的线程 ID（TID）转换为 16 进制，以便在 `jstack` 输出中查找对应的线程。

```bash
printf "%x\n" <TID>
```

```bash
printf "%x\n" 12346
# 输出: 303a
```

## 4. 使用 `jstack` 导出线程堆栈并查找对应的线程

`jstack` 是 Java 提供的一个工具，用于导出 Java 进程的线程堆栈信息。通过将 TID 转换为 16 进制后，可以在 `jstack` 的输出中定位到具体的线程堆栈信息，从而分析线程在做什么。

```bash
jstack <PID> | grep <hex_tid> -A100
```

- `<PID>`：Java 进程的进程 ID。
- `<hex_tid>`：线程 ID 的 16 进制表示。
- `-A100`：表示在匹配的行之后显示 100 行内容（可根据需要调整）。

示例
```bash
jstack 12345 | grep 303a -A100
```

分析
- **分析线程状态**：查看线程当前的状态（如 `RUNNABLE`、`WAITING`、`TIMED_WAITING` 等）。
- **识别阻塞或死锁**：检查是否有线程被阻塞或存在死锁情况。
- **定位高 CPU 线程**：如果某个线程持续处于 `RUNNABLE` 状态且占用大量 CPU，可能是导致高 CPU 使用的罪魁祸首。

## 5. 分析 `jstack` 输出的日志

通过分析 `jstack` 输出的线程堆栈日志，可以深入了解每个线程的执行状态和当前执行的任务，从而识别出导致高 CPU 使用的具体原因。

常见问题及分析
- **无限循环**：线程在执行一个无限循环，导致 CPU 占用高。
- **死锁**：多个线程互相等待资源，导致系统资源耗尽。
- **资源竞争**：多个线程频繁访问共享资源，导致频繁的上下文切换。
- **垃圾收集问题**：频繁的垃圾收集（GC）操作也会导致 CPU 占用高。

示例分析
假设 `jstack` 输出中某个线程的堆栈如下：
```
"Thread-123" #456 prio=5 os_prio=0 tid=0x00007f8a1c0c7000 nid=0x303a runnable [0x00007f8a1c0c8000]
   java.lang.Thread.State: RUNNABLE
    at com.example.MyClass.loopMethod(MyClass.java:123)
    at com.example.MyClass.run(MyClass.java:456)
    at java.lang.Thread.run(Thread.java:748)
```

- **分析**：`loopMethod` 方法可能存在无限循环或长时间运行的操作，导致线程持续占用 CPU。

## 补充建议

### 1. 使用 `htop` 替代 `top`
`htop` 提供了更友好的用户界面和更丰富的功能，可以更直观地查看系统资源和进程/线程信息。
```bash
htop
```

### 2. 使用 `pidstat` 进行更详细的 CPU 使用分析
`pidstat` 是 `sysstat` 工具集中的一个工具，可以提供更详细的进程和线程级别的 CPU 使用情况。
```bash
pidstat -t -p <PID> 1 10
```

### 3. 使用 `jvisualvm` 进行实时监控
`jvisualvm` 是 Java 提供的可视化监控工具，可以实时查看 Java 应用程序的性能指标和线程堆栈信息。
```bash
jvisualvm
```

### 4. 使用 `jmc`（Java Mission Control）进行高级分析
`jmc` 提供了更高级的性能分析和监控功能，适合深入分析复杂的性能问题。
```bash
jmc
```

### 5. 使用 `jstack` 定时导出线程堆栈
为了捕捉瞬时的线程状态，可以定时执行 `jstack`，并保存输出日志。
```bash
while true; do jstack <PID> > jstack_output_$(date +%F_%H-%M-%S).txt; sleep 5; done
```

### 6. 使用 `jProfiler` 或 `YourKit` 等商业工具
这些工具提供了更强大的分析和监控功能，但需要购买许可证。

