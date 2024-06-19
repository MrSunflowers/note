[TOC]

[(31条消息) 完整学习路线！！（建议收藏）_冰河的专栏-CSDN博客](https://blog.csdn.net/l1028386804/article/details/116081409)

## 1.java OOP

### 1.java的基本数据类型

   共有8中基本数据类型，大致可以分为4类：字符型：char；布尔型：boolean ；整数类型：byte`、`short`、`int`、`long ；浮点类型：double、float。其中对于整数类型来说：byte>short>int>long，对应的字节数以及字节数分别是：byte 1个字节，-2^7~2^7-1,short：2个字节，-2^15~(2^15-1)，int：4个字节，(-2^31)~(2^31-1)，long：8个字节，(2^31-1)~(2^31-1)。

  **注意：**如果在定义整数类型变量时如果超出取值范围，编译报错；而如果是在计算过程中计算结果超出取值范围时，计算结果**发生了溢出，溢出的时候并不会抛异常，也没有任何提示，只是计算结果有问题。**

### 2.包装类

​     java中常见的包装类：byte：Byte，short：Short，int：Integer，long：Long，float：Float，double：Double，char：Character ，boolean：Boolean，其中除了int和char以外都是将基本类型的首字母进行大写即为对应的包装类。

####   2.1装箱和拆箱

​     装箱：就是将基本类型的数据封装成包装器类型，调用包装类的valueOf方法；而拆箱就是自动将包装类型转化为基本类型的过程，调用XXXvalue方法。

   对应的有自动装箱和自动拆箱：

* 自动装箱: 就是将基本数据类型自动转换成对应的包装类。

* 自动拆箱：就是将包装类自动转换成对应的基本数据类型。

````java
Integer i =10;  //自动装箱
int b= i;     //自动拆箱
````

#### 2.3 数值比较

​    由于自动装箱时缓存问题，如果整形范围在-128-17之间时，在装箱时如果已经存在，不会再创建新的对象，所以在进行==比较时返回为true，而如果不在这个区间，返回false，只能通过.equals方法进行内容比较。

   1）Integer和int比较：首先将Integer自动拆箱为int，然后进行int值的比较，因此不涉及到取值的范围；

````java
Integer i = 300;
int i2 =300;
System.out.println(i==i2);  //无论是大于128还是小于128 返回结果都是true
````

   2）Integer与Integer的比较

​     由于在赋值的时候会进行装箱的操作，因此需要注意：

​        a. -128<= x<=127的整数，将会直接缓存在IntegerCache中，那么当赋值在这个区间的时候，不会创建新的Integer对象，而是从缓存中获取已经创建好的Integer对象。

​       b. 大于这个范围的时候，直接new Integer来创建Integer对象，则就是比较对象。

### 3.char类型能不能转为int？double类型？String类型呢？

   char在java中是特殊的类型，它的int值从1开始，一共有2的16次方个数。大小是char>int>long>double向上转化是隐式转化，可以自动的转为int、double类型，但是不能隐式的转为String类型，向下可以强制转化为byte，short类型。

###   4.java中常见的包

  java.io、java.long、java.math、java.util包、java.sql包、java.net包等

### 5、String、Stringbuffer、StringBuilder

   其中String是只读字符串，其长度是不可变的，且是被finall修饰过的，不能被其他类所继承，String字符串一经被创建就不能被修改，对其所作的操作其实是返回了一个新的String字符串；而Stringbuffer和Stringbuilder都是用来进行字符串操作的类，其中Stringbuffer中的方法被synchronized修饰过的，具有线程安全，而Stringbuilder不是线程安全的，所以其效率要高于Stringbuffer。

### 6.普通类，抽象类，接口

​     普通类：普通类中不能有抽象方法，可以直接进行实例化，可以单继承一个类或者抽象类，也可以多实现接口；

​     而抽象类中可以有抽象方法，也可以没有抽象方法，不能直接进行实例化，必须有子类去继承他，再进行子类的实例化；抽象类中的抽象方法必须添加abstract关键词，并且方法不能使用final和static关键词修饰。

​    接口：在jdk1.8之前，接口中的方法必须全部是抽象方法，且方法默认用public abstract进行修饰，变量默认通过public static final进行修饰即是一个常量，实现类只能进行读取不能进行改变；而在jdk1.8之后，在接口中的方法可以有实现方法体，但是必须添加default或者添加static进行修饰，接口之间可以进行多继承。

### 7.hashCode的作用

​     对象通过调用该对象的hashCode方法根据对象内存地址得到该对象的hash值，这样一来当集合要添加新元素时，先调用hashCode方法获取到钙元素的hash值，也就是对应的存储位置，然后判断该位置上是否有元素，如果没有直接进行插入，如果有值，在调用equals方法判断该元素与粗出位置元素是否是同一个值，如果是不进行存储（set，list是覆盖），如果不是同一个元素则散列到其他地方。

​    因此，可以通过hashCode方法来较少对equals方法的调用，提高查询效率，主要用于散列存储结构中快速确定对象的存储地址。

#### 1.equals和hashCode的区别

​     Java的超类Object类已经定义了equals()和hashCode()方法，在Obeject类中，equals()比较的是两个对象的内存地址是否相等，而hashCode()返回的是对象的内存地址。所以hashCode主要是用于查找使用的，而equals()是用于比较两个对象是否相等的。但有时候我们根据特定的需求，可能要重写这两个方法，在重写这两个方法的时候，主要注意保持一下几个特性：

  1）如果两个对象的equals方法返回true，则那么这两个对象的hashCode值一定要相等；

  2）如果两个对象的hashCode值相等，则对应的equals方法返回值不一定为true，只能说明这两个对象在一个散列存储结构中；

  3）如果一个对象的equals方法被重写，那么对应的hashCode方法也应该被重写，保持一致。

### 8、java中创建对象的几种方式

   java中提供了4中创建对象的方式：

  1）使用new关键字：

​      用 `new` 关键字创建对象，实际上是做了两个工作，一是在内存中开辟空间，二是初始化对象。但是`new` 关键字只能创建非抽象对象。

  2）通过反射创建对象：

​      反射创建对象分为两种方式，一是使用Class类的`new Instance()` 方法，二是使用Constructor类的`new Instatnce()` 方法。两者的区别是Class的new newInstance() 只能够调用无参的构造函数，即默认的构造函数；Constructor.newInstance() 可以根据传入的参数，调用任意构造构造函数。

````java
//Class方式实现
Employee emp2 = (Employee) Class.forName("org.programming.mitra.exercises.Employee")
                               .newInstance();

//Constructor方式实现
Constructor<Employee> constructor = Employee.class.getConstructor();
Employee emp3 = constructor.newInstance();
````

3）使用clone方法

​     要实现拷贝的对象实现Cloneable类，并重写clone()方法。

4）使用反序列化方式

​     当序列化和反序列化一个对象，jvm会给我们创建一个单独的对象。在序列化时，通过使用IO流中的ObjectOutputStream，先构建ObjectOutputStream对象，然后调用ObjectOutputStream的writeObject方法，将本地java对象写入到本地文件中；在反序列化时，jvm创建对象并不会调用任何构造函数。为了反序列化一个对象，需要让类实现Serializable接口。然后在使用`new ObjectInputStream().readObject()` 来将本地文件中的对象转化为java对象。

````java
private static void aa() {
        Employee e = new Employee();
        e.name = "zhangsan";
        e.address = "beiqinglu";
        e.age = 20;
        try {
            // 创建序列化流对象
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("C:\\Users\\admin\\Desktop\\employee.txt"));
            // 写出对象
            out.writeObject(e);
            out.writeObject(null);//解决反序列化时报错java.io.EOFException，注意此时需要在读取时添加非空判断
            // 释放资源
            out.flush();
            out.close();
            System.out.println("Serialized data is saved"); // 姓名，地址被序列化，年龄没有被序列化。
        } catch(IOException i)   {
            i.printStackTrace();
        }
    }


//反序列化
private static void bb() throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream("C:\\Users\\admin\\Desktop\\employee.txt"));

        Employee employee = new Employee();
        while ((employee = (Employee)in.readObject())!=null){
            System.out.println(employee.toString());
        }

        in.close();
    }
````

### 9.对象的深拷贝和浅拷贝

   深拷贝：就是在复制对象时连同对象中所有的属性都进行拷贝，包括对象中的一些引用，重新指向当前的复制对象。

   浅拷贝：仅复制所拷贝的对象，原对象的引用仍指向原对象。

### 10、static的用法：

  可以用来修饰方法，变量，表示该方法或者变量是静态资源，可以供实例所共享，除此之外还可以用于修饰代码块，用于进行初始化操作；其次也可以用来修饰内部类，表示该类是静态内部类，可以通过类名进行调用。

### 11.final、finally、finalize()区别

####   1. finally 是块级控制代码

#####      1.1 return 语句的执行顺序

​       finally语句是在return语句执行之后，return语句返回之前执行的：即无论try或者catch中如果有return语句，那么闲执行return语句将返回值放在栈顶等待出栈，然后去执行finally中的方法，如果finally中也有return，将finally中的return值放入栈顶，出栈，得到的值就是finally中return的值；如果finally中没有return，则执行finally逻辑后去栈顶拿到try或者catch中的return的值，作为返回结果，此时无论finally中的逻辑有没有修改值，拿到的返回值是之前在栈顶中的值。

````java
public static int Inc(){
        int x;
        try{
            x=1;
            x = 10/0;
            return x;
        }catch (Exception e){
            x=2;
            return x;
        }finally{
            x=3;
            //return x;
        }
 }

返回结果：如果finally中没有return，返回值为2，如果有return返回值为3；
````

````java
public static int func(){
        int a = 10;
        try{
            System.out.println("try中的代码块");
            int i = 10/0;
            return a += 10;
        }catch (Exception e){
            System.out.println("catch中的代码块");
            return a += 10;
        }finally {
            System.out.println("finally中的代码块");
            if(a > 10){
                System.out.println("a > 10,"+"a="+a);
            }
        }
    }

//控制台打印
try中的代码块
catch中的代码块
finally中的代码块
a > 10,a=20
20
````

##### 1.2.覆盖问题

   1）如果finally中有return，那么会覆盖掉try或者catch中的return的值。

````java
public static int Inc(){
        int x;
        try{
            x=1;
            x = 10/0;
            return x;
        }catch (Exception e){
            x=2;
            return x;
        }finally{
            x=3;
            return x;
        }
 }

//返回值为3
````

  2）如果finally中没有返回值，对于基本类型，finally中的修改逻辑并不会影响try或者catch中return的值，如果是引用对象会有影响。

````java
public static void main(String[] args) {
   System.out.println(getMap().get("KEY").toString());
}
//返回值为finally
public static Map<String,String> getMap(){
        Map<String,String> map = new HashMap<>();
        map.put("KEY","INIT");
        try{
            map.put("KEY","try");
            return map;
        }catch (Exception e){
            e.printStackTrace();
            map.put("KEY","catch");
        }finally {
            map.put("KEY","finally");
            map = null;
        }
        return map;
}
````

#### 2.final 

​    是关键字，可以用来修饰类，方法，变量，其中修饰类类不能被继承，修饰方法该方法不能被重写，修饰变量变量值不能被修改。

#### 3.finalize()

​     是Object中的一个方法，是JVM中垃圾回收器回收垃圾时会调用的方法，如果垃圾回收器要回收某一个对象，会先调用该对象的finalize()方法，实现回收之前应该处理的逻辑，如果调用了该对象的finalize方法后发现该对象又重新复活那么就不会进行清理，否则进行标记等待jvm下次到达时进行清理。

### 12.JDBC和数据库连接池

####   1.JDBC

​    是Java应用程序用来连接关系型数据库的标准API，为数据库访问提供统一的接口。常规数据库连接一般有一下步骤：

- 装载数据库驱动程序；
- 建立数据库连接；
- 创建数据库操作对象；
- 访问数据库，执行sql语句；
- 处理返回结果集；
- 断开数据库连接。

#####    1.1常见使用问题

   1）使用PrearedStatement，可以通过预编译的方式避免在拼接SQL时造成SQL注入。

   2）禁用自动提交：这个最佳实践在我们使用JDBC的批量提交的时候显得非常有用，将自动提交禁用后，你可以将一组数据库操作放在一个事务中，而自动提交模式每次执行SQL语句都将执行自己的事务，并且在执行结束提交。

   3）使用Batch Update：批量更新/删除，比单个更新/删除，能显著减少数据传输的往返次数，提高性能。

   4）尽可能的使用列名获取ResultSet中的数据，从而避免invalidColumIndexError：
         JDBC中的查询结果封装在ResultSet中，我们可以通过列名和列序号两种方式获取查询的数据，当我们传入的列序号不正确的时候，就会抛出invalidColumIndexException，例如你传入了0，就会出错，因为ResultSet中的列序号是从1开始的。另外，如果你更改了数据表中列的顺序，你也不必更改JDBC代码，保持了程序的健壮性。有一些Java程序员可能会说通过序号访问列要比列名访问快一些，确实是这样，但是为了程序的健壮性、可读性，我还是更推荐你使用列名来访问。

  5）使用变量绑定而不是字符串拼接
       使用PreparedStatment可以防止注入，而使用`?`或者其他占位符也会提升性能，因为这样数据库就可以使用不同的参数执行相同的查询，提示性能，也防止SQL注入。

  6）执行完毕后，关闭资源，确保资源能得到释放。

#### 2.连接池

#####  2.1数据库连接

   当数据库服务器和客户端位于不同的主机时，就需要建立网络连接来进行通信。客户端必须使用数据库连接来发送命令和接收应答、数据。通过提供给客户端数据库的驱动指定连接字符串后，客户端就可以和数据库建立连接。

   主要包括长连接和短连接，其中

   短连接：即每次操作数据库都需要先进行连接，然后执行相关语句，最后关闭连接，这样会使得连接的开销会很大，在生产繁忙的系统中，连接也可能会受到系统端口数的限制，如果要每秒建立几千个连接，那么连接断开后，端口不会被马上回收利用，必须经历一个FIN阶段等待，直到可被回收利用为止，这样就可能会导致端口资源不够用。

  长连接：长连接是指程序之间的连接在建立之后，就一直打开，被后续程序重用。使用长连接的初衷是减少连接的开销。当收到一个永久连接的请求时，检查是否已经存在一个相同的永久连接。存在则复用；不存在则重新建立一个新的连接。所谓**相同**的连接是指基本连接信息，即用户名、密码、主机及端口都相同。

##### 2.2 数据库连接池

​     就是为数据库连接建立一个“缓冲池”，预先在缓冲池中放入一定数量的数据库连接，需要进行数据库连接时直接从缓冲池中取出一个，使用完毕后再放回到池中。

​     数据库连接池负责分配、管理和释放数据库连接，它允许应用程序重复使用一个现有的数据库连接，而不是重新建立一个。

​    数据库连接池在初始化时将创建一定数量的数据库连接放到连接池中，这些数据库连接的数量是由最小数据库连接数来设定的。无论这些数据库连接是否被使用，连接池都将一直保证至少拥有这么多的连接数量。连接池的最大数据库连接数量限定了这个连接池能占有的最大连接数，当应用程序向连接池请求的连接数超过最大连接数量时，这些请求将被加入到等待队列中。

##### 2.3 多种开源数据库连接池

  （1）DBCP 是Apache提供的数据库连接池。tomcat服务器自带dbcp数据库连接池。速度相对c3p0较快，但因自身存在BUG，Hibernate3已不再提供支持。

 （2）C3P0 是一个开源组织提供的一个数据库连接池，速度相对较慢，稳定性还可以，hibernate官方推荐使用。

（3）Proxool 是sourceforge下的一个开源项目数据库连接池，有监控连接池状态的功能，稳定性较c3p0差一点.

DataSource 通常被称为数据源，它包含连接池和连接池管理两个部分，习惯上也经常把 DataSource 称为连接池，用来替代DriverManager来获取Connection，获取速度快，同时可以大幅度提高数据库访问速度。

##### 2.4.C3p0数据库连接池

   1）获取数据库连接池的方式：

​    a.通过硬代码编程的方式：

````java
//使用C3P0数据库连接池的方式，获取数据库的连接：不推荐
public static Connection getConnection1() throws Exception{
	ComboPooledDataSource cpds = new ComboPooledDataSource();
	cpds.setDriverClass("com.mysql.jdbc.Driver"); 
	cpds.setJdbcUrl("jdbc:mysql://localhost:3306/test");
	cpds.setUser("root");
	cpds.setPassword("abc123");
		
//	cpds.setMaxPoolSize(100);
	
	Connection conn = cpds.getConnection();
	return conn;
}
````

  b.通过配置文件的方式：推荐使用

````xml
<?xml version="1.0" encoding="UTF-8"?>
<c3p0-config>
	<named-config name="helloc3p0">
		<!-- 获取连接的4个基本信息 -->
		<property name="user">root</property>
		<property name="password">abc123</property>
		<property name="jdbcUrl">jdbc:mysql:///test</property>
		<property name="driverClass">com.mysql.jdbc.Driver</property>
		
		<!-- 涉及到数据库连接池的管理的相关属性的设置 -->
		<!-- 若数据库中连接数不足时, 一次向数据库服务器申请多少个连接 -->
		<property name="acquireIncrement">5</property>
		<!-- 初始化数据库连接池时连接的数量 -->
		<property name="initialPoolSize">5</property>
		<!-- 数据库连接池中的最小的数据库连接数 -->
		<property name="minPoolSize">5</property>
		<!-- 数据库连接池中的最大的数据库连接数 -->
		<property name="maxPoolSize">10</property>
		<!-- C3P0 数据库连接池可以维护的 Statement 的个数 -->
		<property name="maxStatements">20</property>
		<!-- 每个连接同时可以使用的 Statement 对象的个数 -->
		<property name="maxStatementsPerConnection">5</property>

	</named-config>
</c3p0-config>
````

````java
//使用C3P0数据库连接池的配置文件方式，获取数据库的连接：推荐
private static DataSource cpds = new ComboPooledDataSource("helloc3p0");
public static Connection getConnection2() throws SQLException{
	Connection conn = cpds.getConnection();
	return conn;
}
````

### 13.排序都有哪几种方法？请列举（待完成）

​     常见的排序算法有：插入排序、冒泡排序、快速排序法、选择排序、归并排序、堆排序等。

####   1.插入排序

​       将待插元素，依次与已排序好的子数列元素**从后到前进**行比较，如果当前元素值比待插元素值大，则将移位到与其相邻的后一个位置，否则直接将待插元素插入当前元素相邻的后一位置，因为说明已经找到插入点的最终位置。



### 14.java中是值传递还是引用传递？

  值传递：是指在调用函数时将实际参数复制一份传递到函数中，这样在函数中如果对参数进行修改，将不会影响到实际参数。

  引用传递：是指在调用函数时将实际参数的地址传递到函数中，那么在函数中对参数所进行的修改，将影响到实际参数。

在java参数传递中如果参数是基本类型，传递的实际是基本类型的副本，因此无论方法中如何修改参数值，原值不发生改变，是值传递，而如果参数是引用类型，实际传递的是引用的地址，如论怎么修改对象的地址不发生改变，可以改变对象的内容，所以也是值传递。

### 15、java中会存在内存泄漏吗？简要回答

  由于java中有gc垃圾回收机制，当java中的对象不再被使用时，垃圾回收器会自动进行回收，不会产生内存泄漏，但是在实际开发中如果一个长生命周期对象中持有大量的短生命周期的对象，导致垃圾回收器不会对这些短生命周期的对象进行回收，可能会造成内存的泄漏。比如：Hibernate 的 Session（ 一级 缓存 ）中的 对象 属于 持久 态，垃圾 回收 器是 不会 回收这些 对象 的，然而 这些 对象 中可 能存 在无 用的 垃圾 对象 ，如果 不及 时关 闭（close）或清 空（ flush）一 级缓 存就 可能 导致 内存 泄露 。

## 2.java集合/泛型

父接口：Collection：主要包含add()在集合末尾添加元素；remove()从集合中移除元素；isEmpty()判断集合元素是否为空；size()返回集合元素的个数；addAll()将一个集合中的所有元素添加到另一个集合中去；Iterator()迭代器遍历集合元素等。

###   1.List集合

​     list集合有序（即插入顺序和去除顺序保持一致），可重复，允许有多个值为空，可以通过索引来直接操作数据。主要有ArrayList和Linkedlist以及vector。

#### 1.1 ArrayList对象

  1.基本概念

​       底层是基于数组来实现，查询快（基于索引进行查询），增删慢（如果删除的是非尾部数据，需要进行数组的移动），线程不安全，但效率高，可以存储重复的元素。

​      当我们在new ArrayList()对象的时候，如果没有传入参数那么默认容量为0，当我们在进行添加元素时会进行扩容，此时扩充容量为10，然后添加数据，如果需要再次扩容，则扩容为原来的1.5倍；如果在创建对象的时候传入参数，则集合容量为默认指定大小，如果需要自动扩容，则扩容为原来的1.5倍。

  2.常见方法 

​     1） 添加方法是：.add(e)；　　获取方法是：.get(index)；　　删除方法是：.remove(index)； 按照索引删除；　　.remove(Object o)； 按照元素内容删除；

​     2）根据索引位置改变元素的值：.set(index, element); 和 .add(index, element);其中set方法是将index位置的值替换为element，而add是将在index上添加一个element，原位置的元素依次向后移。

​    3）转换：.toString()转为字符串；.toArray()转换为数组

​    4）遍历：可以使用普通for循环，增强for循环，或者使用iteator迭代器进行遍历。

####  1.2 Linkedlist

​      底层基于链表并且是双向链表的形式，查询慢（需要进行全部遍历），增删快（指针指向即可），线程不安全，但效率高，可以存储重复的元素。Linkedlist中的节点主要包括元素，pre和next，当添加或者删除数据的时候只需要确定好pre和next的指向即可，在进行遍历的时候每次移动指针的指向一次进行遍历即可。

​     当我们创建Linkedlist对象时，如果是空构造方法，首先会创建一个空的list对象，然后在进行add的时候，首先将first和last都指向了第一个元素，以后每次在添加的时候，将构建一个node节点new Node<>(last, e, null);然后将当前上个节点last指向当前节点，将当前节点的pre指向last，一次进行。如果是传入collection集合，那么其实也是先创建了参数为空的集合，然后调用addAll方法将collection中的元素添加到Linkedlist集合中去。

   1.常见方法

​      1）add（E e）:将指定元素添加到此列表的尾部；

​           add(int index, E element)：在此列表中指定的位置插入指定的元素。

​		  addAll(Collection<? extends E> c)：添加指定 collection 中的所有元素到此列表的结尾，顺序是指定 collection 的迭代器返回这些元素的顺序。

 		 addAll(int index, Collection<? extends E> c)：将指定 collection 中的所有元素从指定位置开始插入此列表。
 	
 		 AddFirst(E e): 将指定元素插入此列表的开头。
 	
 		addLast(E e): 将指定元素添加到此列表的结尾。

​    2）从此列表中移除首次出现的指定元素，remove(o);

​    3）查找方法：get(int index)返回此列表中指定位置的元素。  getFirst()返回此列表中第一个元素； getLast()返回此列表中最后一个元素；indexOf()返回此列表中首次出现指定元素的索引位置。

#### 1.3 Vector

​     和ArrayList一样，底层是基于数组来实现的，只是Vector是线程安全的，由于是基于数组所以查询效率高，而添加和删除效率低，并且可以存储重复的元素。

​    new Vector时候，如果是无参构造那么初始list集合的大小为10，并且以后如果需要扩容则扩容大小为原来的2倍，如果指定了初始容量大小，那么默认大小即为指定大小；当然我们也可以在创建vector对象的时候去指定扩容因子，如果不指定默认每次按照原来的两倍进行扩容，如果指定则每次在原来的基础上增加扩容因子大小。

### 2.set集合

   set集合是无序的（插入顺序和取出顺序无法保证），且不可以重复（相同元素不能插入），只允许有一个null值。主要包括HashSet、LinkedHashSet、TreeSet等对象

####   2.1 HashSet

​      HashSet实现了Set接口，底层实际是HashMap（数组+链表+红黑树），可以存放null值，但只能允许一个null，不保证元素是有序的，取决于hash后，在确定索引的结果。其中元素的唯一性是靠所存储元素类型是否重写hashCode()和equals()方法来保证的，如果没有重写这两个方法，则无法保证元素的唯一性。

​     1）实现唯一性原理：首先当创建HashSet对象时，底层创建了一个HashMap对象，然后当调用add添加元素时，首先会去调用hash计算当前元素的hashCode散列值，然后判断当前hash位置上有无元素，如果没有直接将元素插入到该位置，如果该位置有值在调用equals（）方法，如果返回true说明这两个元素是同一个元素，不进行存储，否则说明有hash冲突，此时采用链表的方法，在该位置将冲突的元素以链表的形式进行存储。

​    2）源码分析：

#### 2.2 linkedHashSet

​      linkedHashSet底层基于linkedHashMap，它是HashSet的子类，底层维护了一个数组+双向链表（可以保证插入顺序和取出顺序保持一致），可以根据元素的hashCode值来决定要存储的位置，同时使用链表维护元素的次序，不允许添加重复的元素。

​     由于其底层是基于双向链表即和Linkedlist相似，在table数组中存放的是每一个节点node，包含before和after以及item，每次插入数据之前先调用hashCode算法去计算hash值，从而确定元素的索引位置，如果在索引位置上没有元素直接进行存储，如果有元素，在比较equals方法，如果返回true，则不进行存储，否则以链表的形式进行存储。然后node节点中的next执行当前存储元素，当前存储元素的pre指向上一个元素，这样就能保证元素的存储顺序。

#### 2.3 TreeSet

  TreeSet是SortSet接口的实现方法，可以确保元素处于排序的状态。主要包括两种排序方式：自然排序和定制排序。

  1）自然排序： 

   实现comparable接口实现其compareTo方法，在方法中定义比较大小的逻辑，方法返回值大于0，说明a是大于b的，如果返回值小于0 ，则说明a是小于b的，如果返回值为0 ，则说明两个值的大小相等。

````java
首先创建person对象，让该对象实现comparable接口，并实现其compareTo方法，进行大小比较逻辑。然后在进行set.add()时会根据自动进行比较并做好排序。
````

  2）定制排序：

​    在创建TreeSet对象时传入comparator接口，实现该匿名内部类的compare方法，如果该方法的返回值大于0，说明a是大于b的，如果返回是小于0，说明a是小于b的，如果返回值为0，说明a和b相等。

````java
public static void main(String[] args){
        Person p1 = new Person();
        p1.age =20;
        Person p2 =new Person();
        p2.age = 30;
        TreeSet<Person> set = new TreeSet<Person>(new Comparator<Person>() {

            @Override
            public int compare(Person o1, Person o2) {
                //年龄越小的排在越后面
                if(o1.age<o2.age){
                    return 1;
                }else if(o1.age>o2.age){
                    return -1;
                }else{
                    return 0;
                }

            }
        });
        set.add(p1);
        set.add(p2);
        System.out.println(set);
    }
````

### 3. Map集合

​      Map中用于保存具有映射关系的数据Key-Value的键值对形式，其中key不可以重复，允许key值为空，但是只能有一个，如果key值相同后者会将前面存储的值进行覆盖；value也可以为空，可以有多个。（和前面说的HashSet相似，只是HashSet是特殊的HashMap其value时钟是一个Object对象）。其常见接口有HashMap，Hashtable、Properties等。

####   3.1 HashMap

​      1）HashMap底层基于数组+链表的方式，底层维护了Node类型的数组，默认为null；

​      2）当创建对象时将加载因子初始化为0.75，调用put方法进行添加时，首先调用hash方法计算出key的hashCode值，然后算出在数组中的索引，然后判断数组中该索引的位置是否有值，如果没有直接在该索引处添加，如果有值，再调用equals方法判断key值是否相等，如果相等就用新值替换旧值，如果不相同在该索引处以链表的形式挂载，注意在挂载前判断是树结构还是链表的形式，根据不同的结构类型进行不同的添加；

   3）扩容：第一次添加时需要扩容至16，临界值为12（16*0.75），以后扩容为原来的2倍，临界值也是原来的两倍；

   4）在jdk8以前，在添加数据时如果索引处有数，并且key不相等时，以链表的形式进行存储，而在jdk8以后，首先会以链表的形式进行挂载，如果链表长度超过8，再判断数组的长度有没有超过64，如果没有则进行扩容，是hash尽可能的分配到别的位置，如果数组长度超过64，则以红黑树的形式进行挂载。

####   3.2 HashTable

   其使用方法基本和HashMap一样，只是Hashtable是线程安全的，而HashMap是线程不安全的，因此其效率可能较于Hashtable快，当然我们可以选择ConcurrentHashMap。

  其底层是数组+链表的方式，key、value均不能为空，是线程安全的，初始值为11，以后每次扩容为当前容量的2倍+1。

####   3.3 TreeMap

​    和TreeSet一样，它是TreeSet的底层实现，可以进行排序，可以通过传入compartor接口，然后重写接口中的compare方法。

#### 3.4 Properties

   Properties 继承自HashTable类，并实现了Map接口，也可以存储key-v键值对，主要用于读取文件中的key-v值并进行存储。

````java
InputStream inStream = new FileInputStream(new File("filePath"));
Properties prop = new Properties();  
prop.load(inStream);  
String key = prop.getProperty("username");  //获取值
//String key = (String) prop.get("username");

setProperty(String key, String value)//设置值
````

### 4.常见面试题

####  1.concurrentHashMap的扩容机制（JDK1.8以前）

​     1）concurrentHashMap的简述：

​       concurrentHashMap是JUC包下的一个类，它是HashMap的的一个线程安全类，采用的分段式锁Segment，只对所操作的段进行加锁，并不会影响客户端对其他段的操作。其本质是一个Segment数组，而每个Segment对象中包含了HashEntry对象的链表。默认情况下concurrentHashMap中有16个segment数组，每个数组中又包含了以HashEntry的链表，其中以Key-value的形式。

   2）concurrentHashMap的结构

​    concurrentHashMap中包含了segment数组，每个数组中包含了HashEntry对象，而HashEntry对象和HashMap中的Entry对象类似，主要有next，hash，key，value属性，但是在HashEntry中next，key，hash是被final修饰的，而value值是被volatile修饰，这样就保证了在对concurrentHashMap对象进行读取操作时不需要进行加锁操作。因为其中next，key,hash等值不会发生改变，而value值是可以随时可见的。因此在进行读操作时是不需要进行加锁的，只有在进行写操作时需要进行加锁，所以比Hashtable的效率要高。

  3）其构造函数有5中，其中最全的是**ConcurrentHashMap(int initialCapacity, float loadFactor, int concurrencyLevel)**表示在进行创建对象时指定了容量，加载因子，分段式锁的个数（并发数），而如果没有指定或者部分指定时，会赋值默认值，容量16，加载因子0.75，并发数16。

  4）concurrentHashMap的put方法：

   **在put添加元素时，concurrentHashMap不允许key和value为空。**首先在进行添加时会根据key计算出hashCode值，然后找到该hash对应的segment对象，并进行上锁，然后进行数据的添加。添加完毕后进行解锁。

  5）concurrentHashMap的扩容：

  jdk1.6：由于是基于segment对象操作的，所以其扩容机制也是在每一个segment中完成的，首先在segment中判断当前是否需要扩容如果需要和HashMap一样，进行2倍扩容，扩容完成后将原有加点赋值到新的扩容后的对象中。

  jdk1.8 在调用put方法时，如果需要扩容，首先将数组大小扩充为原来的2倍，然后将当前concurrentHashMap中的数组划分为几组，每组线程分别负责各自组元素的转移至新数组中。

  6）concurrentHashMap中的读操作

​     和put方法写操作类似，首先计算出key的hash值，然后 根据hash值找到指定的segment对象，从segment中读取数据。**但是由于写入的时候key和value值均不能为空，但是在读取过程中可能会为空，这是由于在初始化HashEntry时指令重排序导致，其jdk内部通过加锁重读的方式进行了解决。**

####   2.jdk1.8以后

​    在jdk1.8以后，concurrentHashMap底层也是使用了数组+链表+红黑树的方式来实现，而对于并发控制则是通过使用关键字synchronized+CAS的方式来进行控制的。

[HashMap在Jdk1.7和1.8中的实现 | 猿人谷 (yuanrengu.com)](https://yuanrengu.com/2020/ba184259.html)

## 3.java异常

### 3.1异常结构？常见异常的种类，及处理机制

​    主要接口Throwable，主要包括Error和Except，其中exception包括编译时异常和运行时异常，可以通过try catch finally来进行捕获，也可以通过Throw直接进行抛出，而Error则表示的是系统错误，不能通过程序进行解决。

#### 3.1.1 运行时异常：

   RuntimeException及其子类都被称为运行时异常，其特点是Java编译器不会检查它。也就是说，当程序中可能出现这类异常时，倘若既"没有通过throws声明抛出它"，也"没有用try-catch语句捕 获它"，还是会编译通过。比如除数为0，数组越界，类转换异常，空指针等。

#### 3.1.2 编译时异常：

​    Exception类本身，以及Exception的子类中除了"运行时异常"之外的其它子类都属于被检查异常。特点 : Java编译器会检查它。此类异 常，要么通过throws进行声明抛出，要么通过try-catch进行捕获处理，否则不能通过编译。IO异常，FileNotFoundException ，SQL异常等等，必须在编译时进行捕获或者抛出。

#### 3.1.3 error错误

   和运行时异常一样，编译器在编译时不会进行检查，当资源不足、约束失败、或是其它程序无法继续运行的条件发生时，就产生错 误。比如OutOfMemoryError，堆栈溢出等。

### 3.2 Throw和Throws的区别

   1）位置不同：throw通常用于函数内，后面跟一个异常对象，可以是常见的异常对象也可以是自定义异常对象，而Throws通常用于方法上，后面直接跟异常类。

  2）功能不同：throws用来声明一个异常，让调用者知道该方法可能会出现问题，做好提前预处理；而throw则是抛出具体问题对象，执行到throw方法停止，并给调用者返回具体的问题对象。并且Throws表示可能会出现某种异常，并不一定会发生，而如果执行到了throw则代表一定抛出了某一种异常问题。

### 3.3 finally的使用

  参见1.11中return的返回位置等问题。

## 4.java中的IO

###   4.1 简单描述一下java中的IO

​     1）在java中可以通过IO流来实现数据的输入和输出，它是在java的IO包中，其特点有：先进先出，先写入流的内容最先被读出，而且是按照顺序进行读取，不能随机访问流中的数据，同一种流要么是读流，要么是写流，不可能同时具备这两种流。

​    2）在java中根据不同的划分情况可以将IO流划分为：根据流的方向分为输入流和输出流；根据操作流的单元分为字节流和字符流；根据角色不同可以分为节点流和处理流。

### 4.2 什么是java中的NIO

  NIO是new IO的简写，是在IO流的基础上进行的升级，其具有相同的作用和目的，但实现方式不同，NIO主要用到的是块（缓冲区），操作效率要高于IO流；java中有两套的NIO，一种是标准的NIO，还有是网络编程的NIO。

### 4.3java中常见的IO流有哪些？

  关于文件的：FileInputSteam/FileOutputStream 、FileReader/FileWriter

  缓冲流：BufferInputStream/BufferedOutputSream、BufferReader/BufferedWriter

  转换流（字节流和字符流之间的转换）：InputStreamReader/OutputStreamWriter

  序列化流：ObjectOutputStream（序列化）/ObjectInputStream（反序列化）

​       **对于序列化流中：**

​     1）Transient关键字的作用：  a. 在变量前加上Transient关键字可以阻止该字段被序列化到文件中，实际在序列化后字段的值为默认初始值，比如int的初始值为0，对象型为null等；b. 可以保对一些敏感的数据进行加密操作，比如在服务器给客户端传输数据时，对象中如果有敏感数据，在序列化时进行加密，然后在反序列化时再进行解密即可。

  打印流：PrintWrite 等等

### 4.4java中的NIO

​      NIO 主要有三大核心部分： Channel(通道)， Buffer(缓冲区), Selector。传统 IO 基于字节流和字符流进行操作， 而 NIO 基于 Channel 和 Buffer(缓冲区)进行操作，数据总是从通道读取到缓冲区中，或者从缓冲区写入到通道中。 Selector(选择区)用于监听多个通道的事件（比 如：连接打开，数据到达）。因此，单个线程可以监听多个数据通道。 NIO 和传统 IO 之间第一个最大的区别是， IO 是面向流的， NIO 是 面向缓冲区的。

#### 4.4.1 NIO的特点

​    1）缓冲区：java中的IO流是针对一个或者多个字节流进行的，而且流中的数据不能进行前后的移动，而对于NIO来说是将数据线存放在缓冲区，需要是可以再缓冲区进行前后移动，这样就增加了处理过程中的灵活性。

   2）非阻塞：IO 的各种流是阻塞的。这意味着，当一个线程调用 read() 或 write()时，该线程被阻塞，直到有一些数据被读取，或数据完全写入。该线程 在此期间不能再干任何事情了。 NIO 的非阻塞模式，使一个线程从某通道发送请求读取数据，但是它仅能得到目前可用的数据，如果目前 没有数据可用时，就什么都不会获取。而不是保持线程阻塞，所以直至数据变的可以读取之前，该线程可以继续做其他的事情。

  3）可以双向操作：java中的IO流都是单向的，要么是读流，要么是写流，一个普通的流不可能同时具备读和写功能，而NIO是基于Channel的，可以进行双向操作。

## 5.java中的反射

###   5.1什么是反射？哪里用到反射

​      反射就是动态获取类信息，以及动态调用对象的方法的功能，对于任意的类能够知道这个类的所有属性和方法，对于任意给定的对象能够动态的调用该对象的方法，把这个过程就叫做反射。

​     反射的应用场景非常多，比如jdbc的连接，spring的AOP底层实现等。

###   5.2 反射的实现方式：

​     1.获取class对象：

​       1）Class.forName(“类的路径”)； 2）类.class ; 3）对象.getClass(); 4）基本类型的包装类

​    2.创建对象的实例：

​      1）使用class对象的Newinstance方法：但是这种方法要求 该 Class 对象对应的类有默认的空构造器;

​      2）使用class对象获取Constructor对象，再调用Constructor的newinstance 方法来获取实例对象。

````java
//获取 Person 类的 Class 对象
Class clazz=Class.forName("reflection.Person");
//使用.newInstane 方法创建对象
Person p=(Person) clazz.newInstance();
//获取构造方法并创建对象
Constructor c=clazz.getDeclaredConstructor(String.class,String.class,int.class);
//创建对象并设置属性13/04/2018
Person p1=(Person) c.newInstance("李四","男",20);
````

###   5.3 反射的优缺点：

​    优点： 1）能够运行时动态获取类的实例，提高灵活性； 2）与动态编译结合

​    缺点：1）使用反射性能较低，需要解析字节码，将内存中的对象进行解析。

​                解决方案： 1、通过setAccessible(true)关闭JDK的安全检查来提升反射速度； 2、多次创建一个类的实例时，有缓存会快很多 3、ReflflectASM工具类，通过字节码生成的方式加快反射速度 

​                2）相对不安全，破坏了封装性（因为通过反射可以获得私有方法和属性）

###   5.4 反射常见API

​     1）Class类：反射的核心类，可以获取类的属性和方法

​     2）Field类：Java.lang.reflec 包中的类，表示类的成员变量，可以用来获取和设置类之中的属性 值。 

​     3）Method 类： Java.lang.reflec 包中的类，表示类的方法，它可以用来获取类中的方法信息或 者执行方法。

​     4）Constructor 类： Java.lang.reflec 包中的类，表示类的构造方法。

```java
//获取 Person 类的 Class 对象
Class clazz=Class.forName("reflection.Person");
//获取 Person 类的所有方法信息
Method[] method=clazz.getDeclaredMethods();
for(Method m:method){
System.out.println(m.toString());
}
//获取 Person 类的所有成员属性信息
Field[] field=clazz.getDeclaredFields();
for(Field f:field){
System.out.println(f.toString());
}
//获取 Person 类的所有构造方法信息
Constructor[] constructor=clazz.getDeclaredConstructors();
for(Constructor c:constructor){
System.out.println(c.toString());
}
```

### 5.5 **请说明如何通过反射获取和设置对象私有字段的值？**

​     可以通过类对象的getDeclaredField()方法字段（Field）对象，然后再通过字段对象的setAccessible(true)将其设置为可以访问，接下来就可以通过get/set方法来获取/设置字段的值了。

## 6.自定义注解

###   1.谈谈你对注解的理解

​     注解本质就是一个继承了 Annotation 接口的接口，可以理解为是一种标记，可作用在类、方法、变量、参数、包等位置，程序在进行编译或者运行时发现这些标记，完成相关的特殊处理逻辑。

###  2.注解的实现原理？

​     注解的本质是继承了 Annotation 接口的接口，在注解里面只支持java基本类型，String类型以及枚举类。根据反射的思想来获取annotation中定义的注解，完成相关功能。

###  3.创建一个自定义注解

  1）修饰符：访问修饰符必须是public，不写默认就是public；

  2）关键字：@interface修饰

  3）注解名称：可以进行自定义名称

  4）注解内容：根据需要的参数

````java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface XinLinLog {
    String value() default"";
}
````

### 4.元注解

 @TARGET ：用于描述注解的使用范围，可以作用于类，方法，构造函数，属性，参数，包等；

  @Retention：表明该注解的生命周期，常见的有Source编译时，Class jvm加载时，RUNTIME运行时；

  @Inherited：是一个标记注解，被标记了@Inherited的类型是可以被继承的；

   @Documented：表明该注解标记的元素可以被Javadoc 或类似的工具文档化

### 5.java中常见的注解有哪些

   1）java内置注解：@Override覆盖父类方法，@Suppvisewarning忽略警告

   2）元注解：@target，@Retention ，@Inherited， @Documented

   3）第三方注解：ssm中常见的@controller，@service,@dao,@requestmapping，@ResponseBody，@autowired等，springboot中@springbootapplication，@EnableDiscoveryClient等等。

spring：@controller，@service，@dao，@autowired，@value，@configuration，@Aspect，@before.... ，@transaction等等

Springmvc：@requestMapping，@getmapping，......，@requestBody，@PathVariable，@restBody等等

Mybatis：@Insert，@update，@Select，@delete...，@ResultMap，@One，@many等。

  4）自定义注解：xxxx

### 6.自定义注解的使用

####   1.结合AOP使用自定义注解

​      比如常见的web阶段的登录权限校验，前端把Tooken放到JSON中传给后端没后端根据tooken的值判断当前用户是否已经登录，此时可以使用注解来完成：

   1）自定义注解类，那个类的方法上使用该注解，就需要进行校验tooken。

````java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AppAuthenticationValidate {
    //必填参数
    String[] requestParams() default {};
}
````

  2）然后可以结合AOP，创建一个切面，切入点为创建的自定义注解，然后在方法内些相关的tooken校验规则，实现相关的逻辑。

````java
@Aspect
@Component
@Slf4j
public class AppAuthenticationValidateAspect {

    @Reference(check = false, timeout = 18000)
    private CommonUserService commonUserService;

    //切入点
    @Before("@annotation(cn.com.bluemoon.admin.web.common.aspect.AppAuthenticationValidate)")
    public void repeatSumbitIntercept( JoinPoint joinPoint) {

        //获取接口的参数
        Object[] o = joinPoint.getArgs();
        JSONObject jsonObject = null;
        String[] parameterNames = ((CodeSignature) joinPoint.getSignature()).getParameterNames();
        String source = null;

        for(int i=0;i<parameterNames.length;i++){
            String paramName = parameterNames[i];

            if(paramName.equals("source")){
                //获取token来源
                source = (String)o[i];
            }
            if(paramName.equals("jsonObject")){
                jsonObject = (JSONObject) o[i];
            }
        }
        if(jsonObject == null){
            throw new WebException(ResponseConstant.ILLEGAL_PARAM_CODE, ResponseConstant.ILLEGAL_PARAM_MSG);
        }
        String token = jsonObject.getString("token");
        if(StringUtils.isBlank(token)){
            throw new WebException(ResponseConstant.TOKEN_EXPIRED_CODE,"登录超时，请重新登录");
        }

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        AppAuthenticationValidate annotation = method.getAnnotation(AppAuthenticationValidate.class);
        String[] requestParams = annotation.requestParams();
        //校验必填参数
        ParamsValidateUtil.isNotBlank(jsonObject,requestParams);
        ResponseBean<String> response = null;
        if(StringUtils.isBlank(source)){
            response = this.commonUserService.checkAppToken(token);
        }else{
            response = this.commonUserService.checkAppTokenByAppType(token,source);
        }

        if (response.getIsSuccess() && ResponseConstant.REQUEST_SUCCESS_CODE == response.getResponseCode()) {
            String empCode = response.getData();
            log.info("---token ={}， empCode={}--", token, empCode);
            jsonObject.put(ProcessParamConstant.APP_EMP_CODE,empCode);
        } else {
            log.info("---token验证不通过，token ={}---", token);
            throw new WebException(ResponseConstant.TOKEN_EXPIRED_CODE, "登录超时，请重新登录");
        }
    }
}
````

3）最后在需要进行判断的接口方法上添加注解即可。

#### 2.属性校验

  类似于JSR303数据校验，只是如果使用JSR303提供的数据校验规则不满意可以自定义规则，然后在属性上添加注解即可。使用步骤：首先自定义注解类；其次自定义校验规则类，用于自定义属性校验规则，该类需要实现ConstraintValidator接口，并实现其两个方法，一个用于初始化操作，而另一个用于写自己的校验规则；其次将自定义注解类和自定义规则类进行关联，通过使用@Constraint(validatedBy = {CheckValidator.class})//指向实现验证的类。最后在实体类的属性上添加自定义注解类即可。

 1）自定义注解；

````java
@Target({ElementType.FIELD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {CheckValidator.class})//指向实现验证的类
public @interface Check {

    CheckValidatorEnum type() default CheckValidatorEnum.Null;

    long min() default 1;
    long max() default 1;

    String message() default "参数异常";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}

//校验枚举类
@Getter
public enum  CheckValidatorEnum {
    Null(1,"为空校验"),
    AGE(2,"年龄校验"),
    Phone(3,"手机校验");

    CheckValidatorEnum(int code,String desc){
        this.code = code;
        this.desc = desc;
    }
    private int code;
    private String desc;
}
````

 2、自定义规则校验类，用于自定义所需要的校验规则,该类实现ConstraintValidator，并实现两个方法，一个用于初始化，一个用于写自己的校验规则。

````java
public class CheckValidator implements ConstraintValidator<Check,Object> {
    
    private Check check;
    private CheckValidatorEnum checkValidatorEnum;

    @Override
    public void initialize(Check CheckAnnotation){
        this.check = CheckAnnotation;
        this.checkValidatorEnum = CheckAnnotation.type();
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        
        if(checkValidatorEnum.getCode()==CheckValidatorEnum.Null.getCode()){
            //非空校验
            if(o != null && !o.toString().equals("")){
                return true;
            }
        }else if(checkValidatorEnum.getCode()==CheckValidatorEnum.AGE.getCode()){
            //年龄校验
            if(o.toString() == null){
                return true;
            }
            long min = this.check.min();
            long max = this.check.max();
            Integer age = Integer.parseInt(o.toString());
            if(age>=min && age<=max){
                return true;
            }
        }else if(checkValidatorEnum.getCode()==CheckValidatorEnum.Phone.getCode()){
            if(o == null){
                return true;
            }
            //手机号码校验
            if(CommonUtil.isMobile(o.toString())){
                return true;
            }
        }
        return false;
    }
}
````

  3）将自定义注解和自定义注解类进行关联，通过在自定义注解上面添加注解@Constraint(validatedBy = {CheckValidator.class})//指向实现验证的类。

````java
@Constraint(validatedBy = {CheckValidator.class})//指向实现验证的类
````

  4）然后在实体类属性上使用自定义注解即可。

  5）可以结合使用全局异常处理，来处理参数校验失败的逻辑。

## 7.多线程&并发

### 简要说一下java中的多线程

  进程：资源分配的最小单元，每个进程之间相互独立，有自己的数据空间，进程之间切换资源开销较大，一个进程中可以有1-n个线程。

  线程：CPU调度的最小单元，每个线程有自己的运行栈和程序计数器，同一个线程中资源共享，线程之间切换开销较小。

线程的5个阶段：创建--就绪--运行--阻塞--终止，创建线程有4中方式，继承Thread类，实现Runnable接口，实现Callable接口或者通过线程池进行创建；通过线程可以发挥多核CPU的优势，防止阻塞提高运行效率。

### 实现线程的几种方式？

  1）自定义类继承Thread类，实现run方法，然后创建线程类对象，调用对象的start方法即可；

  2）实现Runnable接口，实现run方法：使用时先创建线程类对象，再创建Thread类，将线程类对象作为对象传入，然后调用Thread的start方法：

````java
 RunnableThreadTest rtt = new RunnableThreadTest();
 Thread t1 = new Thread(rtt,"新线程1").start();
 Thread t2 = new Thread(rtt,"新线程2").start();
````

  3）实现Callable接口：创建线程类实现Callable接口，并实现call方法，然后创建FutureTask类来包装线程类，最后在创建线程类，将FutureTask作为参数进行传入，然后调用Thread类的start方法。当调用FutureTask类的get方法时返回线程执行的结果。

````java
CallableThreadTest ctt = new CallableThreadTest();
FutureTask<Integer> ft = new FutureTask<>(ctt);
Thread t1 = Thread t1 = new Thread(ft, "有返回值的线程").start();

System.out.println("子线程的返回值：" + ft.get());
````

 4）通过线程池创建：避免频繁的创建和注销线程带来的效率损耗问题。

````java
 ExecutorService executorService = Executors.newFixedThreadPool(5); //创建线程池
 executorService.execute(new TestRunnable());  //创建线程类，并调用executorService.execute方法
 executorService.shutdown();  //最后关闭线程池
````

### 线程池相关

​      Java 里面线程池的顶级接口是 Executor，但是严格意义上讲 Executor 并不是一个线程池，而 只是一个执行线程的工具。真正的线程池接口是 ExecutorService。 

​     Executor框架包括：线程池，Executor，Executors，ExecutorService，CompletionService，Future，Callable等。

​     ExecutorService的生命周期包括三种状态：运行、关闭、终止。创建后便进入运行状态，当调用了shutdown（）方法时，便进入关闭状态，此时意味着ExecutorService不再接受新的任务，但它还在执行已经提交了的任务，当所有已经提交了的任务执行完后，便到达终止状态。如果不调用shutdown（）方法，ExecutorService会一直处在运行状态，不断接收新的任务，执行新的任务，服务器端一般不需要关闭它，保持一直运行即可。

  其中线程池中的Execu方法和submit方法的区别是：都是向线程池中提交任务，只是Execut方法没有返回值，而调用submit方法返回的是future对象，调用该对象的get方法即可获取到返回的结果值。

####   1.线程池的创建

  线程池构造方法的参数：

  corePoolSize：线程池的核心线程数；

  maximumPoolSize:最大线程数，不管你提交多少任务，线程池里最多工作线程数就是maximumPoolSize；

  keepAliveTime:线程的存活时间。当线程池里的线程数大于corePoolSize时，如果等了keepAliveTime时长还没有任务可执行，则线程退出。

  workQueue：一个阻塞队列，提交的任务将会被放到这个队列里。

  threadFactory：线程工厂，用来创建线程，主要是为了给线程起名字，默认工厂的线程名字：pool-1-thread-3。

  handler：拒绝策略，当线程池里线程被耗尽，且队列也满了的时候会调用。

#### 2.线程池的执行流程

  当提交任务时，首先判断当前是否还有核心线程，如果有创建线程执行任务，如果核心线程数已满，再去判断当前阻塞队列是否已满，如果队列未满，将当前任务放置队列中等待，如果队列也已经满了，在看看当前线程池是否已满，如果已满，按照拒绝策略无法执行该任务，否则存放在线程池中。

#### 3.创建线程池的方式有几种

   1）**ThreadPoolExecutor**：最原始的创建线程池的方式，即所有的参数都是自定义手动进行创建；

   2）**newSingleThreadExecutor：单线程池**也就是处于工作状态的核心线程数只有1，它保证了所有的任务都是按照顺序进行执行的；

   3）**newCachedThreadPool：可缓存线程池，**其核心线程数为0，最大线程是很大，存活时间为60秒，可以用来处理大量短时间工作任务的线程池，这种线程池不会太大的消耗资源，如果超过最大时间，会被清空。

  4）**newFixedThreadPool：定长线程池,**指定核心线程数等于最大线程数，即也就是任何时候都会有核心线程数的队列是存活的，如果当前线程数超过核心线程数就得在队列中等待，直到有线程退出。

#### 4.线程池的优势

   1）降低资源的消耗：避免了线程的重复创建和销毁；

   2）提高响应速度：当任务到达时不必要等待线程的创建，直接拿过来就可以使用

   3）方便统一管理：通过线程池来统一管理线程的数目，优先级等等。

#### 5.线程池的拒绝策略

  executors提供了4中策略：

  1）**AbortPolicy（默认）**：丢弃任务并抛出 **RejectedExecutionException** 异常。

  2）**CallerRunsPolicy**：由调用线程处理该任务。

  3）**DiscardPolicy**：丢弃任务，但是不抛出异常。可以配合这种模式进行自定义的处理方式。

  4）**DiscardOldestPolicy**：丢弃队列最早的未处理任务，然后重新尝试执行任务。

### Thread和Runnable的区别

实现Runnable接口比继承Thread类所具有的优势：

1）：适合多个相同的程序代码的线程去处理同一个资源（实现Runnable接口可以进行资源共享）

2）：可以避免java中的单继承的限制（接口可以有多实现）

3）：增加程序的健壮性，代码可以被多个线程共享，代码和数据独立

4）：线程池只能放入实现Runable或callable类线程，不能直接放入继承Thread的类。

### Runnable和Callable的区别

​     Runnable接口中的run方法没有返回值，调用run方法只是执行相关逻辑，而Callable接口中的run方法有返回值，是一个泛型，和future和futureTask配合用来获取异步执行的结果。实际开发过程中可以利用这一点，如果多线程等待时间过长没有返回值，我们可以取消该请求。

### 如何停止一个正在运行的线程

   1）可以使用退出标志符，在线程类中，定义一个标志位并在run方法中添加判断条件，只有满足是执行，否则不执行；然后在主程序中如果希望程序停止，给线程类中的标志赋值即可。

   2）可以调用Stop方法，不过该方法已经过时不推荐使用；

   3）调用interrupt方法，打断当前正在运行的线程：主要分两种情况：1.如果当前线程处于阻塞状态，使用interrupt时会抛出异常，此时需要去捕获这个异常信息，在捕获异常信息时通过break来跳出循环；2.如果线程处于非阻塞状态，调用isInterrupted()判断线程的中断标志来退出循环。当使用interrupt()方法时，中断标志就会置 true，和使用自 定义的标志来控制循环是一样的道理。

````java
//方式一：通过使用退出标志
class MyThread extends Thread {
    volatile boolean stop = false;
    public void run() {
        while (!stop) {
            System.out.println(getName() + " is running");
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("week up from blcok...");
                stop = true; // 在异常处理代码中修改共享变量的状态
            }
        }
        System.out.println(getName() + " is exiting...");
    }
}
//方式二：对于非阻塞状态，通过使用interrupt
public class ThreadSafe extends Thread {
    public void run() {
        while (!isInterrupted()){ //非阻塞过程中通过判断中断标志来退出
            try{
           		 Thread.sleep(5*1000);//阻塞过程捕获中断异常来退出
            }catch(InterruptedException e){
            	e.printStackTrace();
            	break;//捕获到异常之后，执行 break 跳出循环
            }
        }
    }
}
````

### 说说对synchronized的理解

  synchronized关键字是用来解决多线程资源访问的同步性问题，可以用在方法或者代码块中，通过该关键字保证在同一时刻只能由一个线程来进行访问。

  1）作用在实例方法上，此时synchronized的对象锁是当前类的实例对象，先执行此方法的线程获取到锁，此后同一个实例对象的其他线程如果要访问该方法必须等当前线程释放锁后再进行访问。

````java
public class Ticket implements Runnable {
    static int count = 0;

    public synchronized void increase(){
        count++;
    }
    @Override
    public void run() {
        for (int i = 0; i <1000000 ; i++) {
            increase();
        }
    }
}

public class TestTicket {
    public static void main(String[] args) throws InterruptedException {
        Ticket ticket1 = new Ticket();
        //Ticket ticket2 = new Ticket();
        Thread t1 = new Thread(ticket1);
        Thread t2 = new Thread(ticket1);
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println(Ticket.count);
    }
}

//运行结果：2000000 
````

**注意：是同一个实例对象的其他线程，如果不是一个实例对象仍然可以访问。**对于上述例子，如果创建一个新的ticket2实例，那么最终输出结果就不是2000000，因为存在着两个不同的实例对象锁，因此t1和t2都会进入各自的对象锁，也就是说t1和t2线程使用的是不同的锁，因此线程安全是无法保证的。这时可以将方法静态化，加上static，这样当前锁就是类对象，无论创建了多少个实例对象，其锁只有同一个。

  2）用在静态方法上：此时对应的锁是类对象，和创建的实例对象无关。

````java
public class Ticket implements Runnable {
    static int count = 0;

    public synchronized static void increase(){
        count++;
    }
    @Override
    public void run() {
        for (int i = 0; i <1000000 ; i++) {
            increase();
        }
    }
}

public class TestTicket {
    public static void main(String[] args) throws InterruptedException {
        Ticket ticket1 = new Ticket();
        Ticket ticket2 = new Ticket();
        Thread t1 = new Thread(ticket1);
        Thread t2 = new Thread(ticket2);
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println(Ticket.count);
    }
}

//运行结果：2000000 
````

 3）作用域代码块上：在进行加锁是指定锁对象或者this对象都是指代当前实例。

````java
public class AccountingSync implements Runnable{
    static AccountingSync instance=new AccountingSync();
    static int i=0;
    @Override
    public void run() {
        //省略其他耗时操作....
        //使用同步代码块对变量i进行同步操作,锁对象为instance
        synchronized(instance){
            for(int j=0;j<1000000;j++){
                    i++;
              }
        }
    }
    public static void main(String[] args) throws InterruptedException {
        Thread t1=new Thread(instance);
        Thread t2=new Thread(instance);
        t1.start();t2.start();
        t1.join();t2.join();
        System.out.println(i);
    }
}
````

### volatile的作用

​    仅能用来修饰变量，被volatile修饰的变量保证了其他线程对这个变量的操作是可见的，即一个线程修改了该变量，其他线程能够立马看见该值的变化；它和synchronized的区别是：synchronized会有阻塞，只有当前线程释放锁后其他线程才能访问，而volatile没有阻塞的作用，并且他只能保证该变量的可见性，而不能保证数据的原子性；更重要的作用是被volatile修饰的变量不会再编译器便器的过程中被编译器优化，而synchronized则有可能。

### 后台线程

   也叫守护线程，为其他用户线程提供服务，即当没有用户线程需要服务时会自动退出。其优先级最低，可以在线程对象创建之前，用线程对象的 setDaemon(true)来设置为守护线程。垃圾回收器就是一个经典的守护线程。

### 锁

#### 1.乐观锁

​       乐观锁是一种乐观思想，即认为遇到并发的可能性较小，每次去获取数据的时候认为别人不会进行修改，因此不进行加锁，但是在更新的时候会去判断在此期间有没有人更新过该值（是通过比较版本号的方式），如果取出的版本号大于当前的版本号则认为被修改过，本次更新失败，稍后重新操作，否则进行数据的更新。java 中的乐观锁基本都是通过 CAS 操作实现的， CAS 是一种更新的原子操作， 比较当前值跟传入值是否一样，一样则更新，否则失败。

#### 2.悲观锁

​     悲观锁是一种悲观的思想，即认为遇到并发的可能性较大，每次读写都会进行加锁，这样别人在去操作的时候会进行阻塞，直到拿到锁，java中的悲观锁就是通过使用synchronized关键字。

#### 3.自旋锁

​    自旋锁即就是其他未获取到锁的线程并不会直接进入到阻塞状态，而是先“等一等”，等当前正在持锁的线程释放掉后立即开始抢占锁。自旋锁原理非常简单， 如果持有锁的线程能在很短时间内释放锁资源，那么那些等待竞争锁的线程就不需要做内核态和用户态之间的切换 进入阻塞挂起状态，它们只需要等一等（自旋），等持有锁的线程释放锁后即可立即获取锁，这样就避免用户线程和内核的切换的消耗。

​     1）注意由于自旋锁是非常消耗CPU资源的，因为CPU会不停的空转，所以使用自旋锁的前提是持有锁的线程会在很短的时间内释放掉锁资源；并且需要设定一个最大的自旋等待时间，避免在长时间回去不到锁喉，导致CPU的资源过度消耗。

​     2）自旋锁的优缺点 

​      自旋锁尽可能的减少线程的阻塞，这对于锁的竞争不激烈，且占用锁时间非常短的代码块来说性能能大幅度的提升，因为自旋的消耗会小于 线程阻塞挂起再唤醒的操作的消耗，这些操作会导致线程发生两次上下文切换！

#### 4.synchronized锁

   它属于独占锁，同时属于可重入锁，可以将任意一个非null的对象作为锁，其作用范围是：

​    1）当用作方法时，锁的对象是实例；

​    2）当作用于静态方法时，锁的对象是class实例；

​    3）当作用于对象实例时，锁住的是以该对象为锁的代码块。

#####  1.核心组件

​     1）Wait Set：哪些调用 wait 方法被阻塞的线程被放置在这里；

​     2）Contention List： 竞争队列，所有请求锁的线程首先被放在这个竞争队列中； 

​     3）Entry List： Contention List 中那些有资格成为候选资源的线程被移动到 Entry List 中； 

​    4）OnDeck：任意时刻， 最多只有一个线程正在竞争锁资源，该线程被成为 OnDeck； 

​    5） Owner：当前已经获取到所资源的线程被称为 Owner； 6) !Owner：当前释放锁的线程。

####  5.死锁、活锁、饥饿

#####    1.死锁

​     死锁就是两个或者两个以上的线程之间互相持有对方资源，互不撒手，如果没有外力干涉是无法进行进行下去的。形成死锁的条件：

​    1）线程之间必须有互斥关系；2）请求与保持条件：如果一个进程因为请求资源而被阻塞，对已获得的资源保持不放；3）不剥夺条件：如果一个进程已获得资源，在资源释放之前，不能被剥夺；4）线程之间首尾相连形成闭环。

#####   2.活锁

   活锁是指任务没有被阻塞，而是由于某些条件没有满足，导致一直重复尝试，失败，尝试失败。其和死锁的区别是处于活锁的对象的状态是不断的变化，而死锁的状态是保持不变的；并且活锁是有可能解开的，但是死锁在没有外部介入后是不能向下进行的。

#####    3.饥饿

​    饥饿是指一个或者多个线程之间由于种种原因无法获得所需要的资源，导致一直无法运行的状态。导致饥饿的原因有：1）优先级高的线程始终抢占了低优先级的线程的运行权；2）线程被永久的阻塞在了一个等待进入同步块的状态。

### 线程的调度算法？

​      常见的有两种算法：分时调度模型，抢占式调度模型；

​      分时调度模型：按照时间平均分配的原则，让所有的线程轮流获得CPU的执行权，平均分配占用CPU的时间片；

​      抢占式调度模型：是指优先让可运行池中优先级高的线程占用CPU，如果可运行池中的 线程优先级相同，那么就随机选择一个线程，使其占用CPU。处于运行状态的线程会一直运行，直至它不得不放弃 CPU。

### Java中synchronized 和 ReentrantLock 有什么不同？

​    共同点：两者都是阻塞式同步锁，也就是说如果一个线程获得了对象锁，进入同步块，其他访问该同步块的线程就会被阻塞，直到当前线程放弃对象锁后才能进行访问。

​    不同点：

​       1）Synchronized经过编译，会在同步块的前后分别形成monitorenter和monitorexit这个两个字节码指 令。在执行monitorenter指令时，首先要尝试获取对象锁。如果这个对象没被锁定，或者当前线程已经 拥有了那个对象锁，把锁的计算器加1，相应的，在执行monitorexit指令时会将锁计算器就减1，当计 算器为0时，锁就被释放了。如果获取对象锁失败，那当前线程就要阻塞，直到对象锁被另一个线程释 放为止 。

​     2）而对于ReentrantLock是java.util.concurrent包下提供的一套互斥锁，相比Synchronized，ReentrantLock类提供了一些高级功能：

​    a.等待可中断，持有锁的线程长期不释放的时候，正在等待的线程可以选择放弃等待，这相当于 Synchronized来说可以避免出现死锁的情况。

​    b.公平锁，多个线程等待同一个锁时，必须按照申请锁的时间顺序获得锁，Synchronized锁非公平锁， ReentrantLock默认的构造函数是创建的非公平锁，可以通过参数true设为公平锁，但公平锁表现的性 能不是很好。

  1）**synchronized**是关键字，而ReentrantLock是一个类；

  2）synchronized是自动加锁和释放锁，而ReentrantLock需要手动调用lock和unlock进行加锁解锁；

  3）synchronized是非公平锁，而ReentrantLock是公平锁；

  4）ReentrantLock提供了等待可中断机制，当持有锁的对象长期不释放，等待的锁对象可以放弃等待，避免出现死锁。

### 并发编程的三要素

   原子性：不可分割操作，多个步骤要保证同时成功或者同时失败。可以通过synchronized或者lock加锁的方式来进行保证。

   有序性：程序执行的顺序和代码顺序保持一致。由于java中编译器或者处理器对指令进行重排序，导致执行顺序和代码顺序不一致，可以通过采用关键字volatile进行修饰，被volatile修饰不允许进行重排序。

   可用性：一个线程对共享变量的修改，另一个程序立马能够看见。可以通过volatile进行修饰，这样一个线程如果对变量进行修饰，该修饰的值可以立马被其他的线程所看见。

### 线程池的理解

  详细可以查看线程池.md。

​    线程池其实就是一个缓冲池，可以预先在缓冲池中存放一定数量的线程用于接收任务并进行处理，避免了在使用过程中重复的创建和销毁线程带来的性能问题，提高响应速度。

 1）线程池中常见的参数

 corePoolSize：核心线程数，可以用来处理处理任务的线程数（可以理解为当前线程池中预先创建的线程数），可以指定大小，默认情况下为0，当任务到达时创建。

最大线程数：线程池中所能允许的最大处理任务的线程数，如果任务数超过该最大值，后续可以根据拒绝策略拒绝任务。

最大存活时间：空闲线程最大等待的时间，默认情况下是当线程数大于核心线程数时起作用，超过最大时间的线程会被终止。

缓存队列：提交的任务会被放置到该队列中。

线程工厂：用来创建线程，主要是起名字。

拒绝策略：当线程池中的线程被耗尽，且队列也存放满了时会使用该策略，常见策略有：丢弃抛出异常，丢弃任务不抛出异常，丢弃队列中最早接收未处理的任务，由调用线程执行。

2）线程池的处理流程

​    首先当任务提交时，首先会判断当前线程池中的线程数是否超过核心线程数，如果未超过则创建线程进行执行，否则尝试将其放到缓存队列中，如果添加成功该任务就被缓存在队列中等待空闲的线程获取并执行，如果放入队列失败（一般是队列满了），则会判断当前线程是否超过最大线程数，如果没有超过则创建新的线程执行任务，否则根据拒绝策略处理任务。

3）线程池的状态

 running：创建线程池完毕，并进行初始化后的状态；

 shutdown：调用shutdown方法的状态，此时线程池不允许接收新的任务，但是还可以继续处理线程池中未处理完的任务。

stop：当调用了shutdownNow方法时的状态，此时线程池不接受新的任务，并尝试终止正在处理的所有任务。

TERMINATED：当线程池处于shutdown和stop状态时，次当前线程池，缓存队列中任务清空后的状态为TERMINATED。

4）创建线程池的方式：

  **ThreadPoolExecutor**：最原始的创建线程池的方式，即所有的参数都是自定义手动进行创建，核心线程数，最大线程数等参数。

  **newSingleThreadExecutor：单线程池**也就是处于工作状态的核心线程数只有1，它保证了所有的任务都是按照顺序进行执行的；

  **newCachedThreadPool：可缓存线程池，**其核心线程数为0，最大线程是很大，存活时间为60秒，可以用来处理大量短时间工作任务的线程池，这种线程池不会太大的消耗资源，如果超过最大时间，会被清空。

**newFixedThreadPool：定长线程池,**指定核心线程数等于最大线程数，即也就是任何时候都会有核心线程数的队列是存活的，如果当前线程数超过核心线程数就得在队列中等待，直到有线程退出。

。。。。。。

## 8.JVM

 详见JVM.md文件。

###   1.常见面试题

#### 1.1简述一下JVM中加载class文件的原理？

​    首先jvm中的编译器会将.java文件编译成class文件，类加载器会去加载class文件至内存中，也就是jvm运行时数据区中的方法区中，然后去验证class文件是否满足jvm规范，并进行解析，解析完成后会进行初始化操作。装载方式有两种：显示加载和隐式加载；

  1）显示加载：比如通过class.forName（“XXX”）的方式进行加载；

  2）隐式加载：当遇见new等关键字时会自动去加载class文件。

## 9.spring相关

### 1.简要介绍一下spring

​    spring是一个轻量级框架，可以用来整合或者管理一些其他第三方框架，用来管理bean对象，可以简化开发流程，主要包括spring context，core，AOP，IOC，ASPectJ等等，其核心是AOP和IOC。

   spring的启动流程主要分为三个部分：初始化容器，获取bean对象，实例化bean对象。其中：

   1）初始化容器：spring启动时首先beandefinitionReader会去加载元数据（xml中的bean标签或者通过注解等获取到bean对象），然后对其进行解析生成一个个beandefinition对象，并且将这些beandefinition对象注册到beandefinitionRegister中的map中，遍历map，在这个过程中会去加载一些配置信息进行数据填充，并返回一个beanfactory对象。

   2）获取bean对象：从beanfactory或者缓存中获取对象，如果存在直接返回bean对象，如果不存在则进行bean对象的创建。在从缓存中获取bean对象时，先从以及缓存中获取，如果没有在从二级缓存中获取，如果还没有再 从三级缓存中获取。

  3）bean的实例化：调用docreate方法进行bean对象的实例化，首先判断当前bean的作用域，如果是单例首先清除缓存中的bean对象，然后在进行实例化，将beandefinition对象转化为beanwrapper对象，然后进行依赖处理，提前暴露bean对象用于解决循环依赖问题，然后进行初始化，属性填充等，最后完成bean对象的创建。

### 2.说一下循环依赖问题

​    循环依赖就是指两个或者两个以上的对象之间相互注入对方最终形成闭环。其主要类型有：

   1）通过构造方法进行依赖注入时产生循环依赖；2）通过set方法且在多实例的情况下；3）在set方法且是单实例的情况下产生循环依赖。其中只有第三种在单实例的情况下产生的循环依赖问题可以解决，其余两种会抛出异常。

  解决思路：在创建对象的时候，首先调用其构造方法进行实例化，实例化后将对象提前暴露给了三级缓存，尽管当前对象还没有进行初始化和属性赋值，只能是一个半成品，然后在进行属性注入时发现需要B对象，此时在缓存中没有B对象，需要创建，进行B对象的实例化，将实例化后的B也放入三级缓存中，此时在给B进行属性填充，发现B中需要A对象，这时在去换从中获取A对象，发现有就将A赋值给B，然后B对象就初始化完成，最后在去完成A对象的初始化操作。

### 3.简要介绍AOP和IOC。

​    1）IOC：控制翻转，它是一种设计思想，即就是将创建bean对象的过程交给spring容器，其底层主要用到了反射思想。也可以说DI依赖注入，就是将相互之间有依赖关系的组件，通过spring容器自动将对象中所依赖的属性注入进来，完成对象的创建。

​    依赖注入的方式有三种： 通过构造器，通过setter方法，通过静态工厂注入。

  2）AOP ：面向切面编程，将那些与业务无关，但是多个对象产生出来的公共行为逻辑，抽取出来，在需要的地方以切面的形式插入进去，完成原有的功能，可以减少重复代码，降低系统模块间的耦合度，提高了系统的可维护性。可用于权限认证，日志，事务等处理。

 其关键点在于静态代理和动态代理，静态代理比如Aspectj，动态代理AOP。

   AspectJ：是静态代理的增强，就是在编译阶段生成AOP的代理，会在编译阶段将Aspect（切面）织入到java代码中，运行之后就是增强之后的AOP对象。而动态代理是在运行时再内存中临时为方法生成一个AOP对象，这个aop对象中包含了目标对象的全部方法，并且在特定的地方做了增强。

   3）动态代理又包括jdk动态代理和CGLIB动态代理。

   jdk动态代理：只提供接口代理，不支持类的代理，如果被代理的对象实现了接口可以使用jdk动态代理，其核心是invocationHandler和proxy类，通过调用invoke方法反射来调用目标对象中的代码，接着proxy利用invocationHandler动态创建一个符合接口的实例，生成目标对象的代理对象。

  CGLIB动态代理：如果代理类没有实现invocationHandler接口，呢么会使用CGLIB代理，可以在运行时动态生成指定类的一个子类对象，并覆盖其中特定的方法，然后做增强处理。**由于是通过继承的方式实现动态代理，如果一个类被final修饰，是不能用CGLIB来实现攻台代理的。**

​    切面：抽取出去的模块，或者被@Aspect标记的类，

   切入点：需要被通知的方法，

   增强：需要进行的逻辑处理。

  通常有两种方式：注解或者xml配置的方式

````xml
基于xml配置文件：
   首先自定义切面类，xml文件中注入<bean id="aa" class="切面类"></bean>；
   然后配置切入点和通知：
     <aop:config>
	    <aop:aspect id ="bb" ref="aa" order="1"> 表示执行的先后顺序 
		   <aop:pointcut id ="cc" expression ="那个需要的地方"></aop:pointcut>
           <aop:before method="自定义方法" pointcut-ref ="cc"/>
		   <aop:after method="自定义方法" pointcut-ref ="cc"/>
              ...
	    </aop:aspect>
		<aop:aspect id="log" ref="logHandler" order="2">
                <aop:pointcut id="printLog" expression="execution(* com.xrq.aop.HelloWorld.*(..))" />
                <aop:before method="LogBefore" pointcut-ref="printLog" />
                <aop:after method="LogAfter" pointcut-ref="printLog" />
        </aop:aspect>
	</aop:config>
````

````java
自定义一个类@Aspect:（切面类=增强+切入点）定义增强即就是自定义方法处理逻辑，在方法上添加注解@before(切入点)；@after(切入点)；@afterReturning(切入点); @AfterThrowing(切入点)
  before：前置增强，方法执行前执行，比如连接数据库；
  after : finall增强，不管是抛出异常或者正常退出都会去执行；
  afterReturning：后置增强，方法正常退出后执行；
  afterthrowing: 异常抛出增强；
  ARround：环绕增强。
  切入点：execution(public * *(..))任意公共方法。
  execution(* set*(..))任何一个以set开始的方法。
````

### 4.简要说一下beanfactory和applicationcontext的异同点

   都是spring的两大核心接口，可以用来作为spring的容器；

  1）对于beanfactory来说，它是作为spring底层的核心接口，可以用来创建bean对象并管理bean的整个生命周期，而applicationcontext作为beanfactory的子类，除了具有beanfactory的那些作用外还可以进行国际化的处理，以及外部资源文件的访问等。

  2）beanfactory采用的是延迟加载的方式，即只有在使用bean对象的时候才回去加载bean对象，因此不容易在项目启动的过程中发现配置信息的错误，而applicationcontext则可以在启动时候发现配置信息的错误；

  3）由于beanfactory是延迟加载，所以在启动的过程中速度是非常的块，而运行过程中速度相对较慢，而applicationcontext则正好相反。

### 5.spring的生命周期和作用域

   生命周期：实例化对象（调用构造器方法）--->>属性填充--->>初始化前（前置处理）---->>初始化----->>初始化后（后置处理）---->>依赖检查---->>>生成代理对象--->>销毁。

   作用域：单例singleton 、原型 prototype、request、session、global-session ，其中默认的作用域范围是singleton，可以在进行bean的创建时配置scope=“XXX”。

### 6.spring中的bean是线程安全的吗？如何解决

​    在原型prototype下是线程安全的，如果是单例模式，需要看实例bean的状态，如果是无状态的则是线程安全的，否则是不安全的。解决办法可以将单例模式改为多例模式，scope=‘prototype’，或者通过ThreadLocal来解决。

​    ThreadLocal：是一种本地线程缓存机制，每一个线程可以单独共享，可以将数据缓存在自己内部线程中，这样该线程在任意时候任意方法中都能够访问该数据，且不会出现错误。其底层采用的是ThreadLocalMap来实现，在每一个Thread中都会有一个ThreadLocalMap对象，该对象将每一个ThreadLocal对象作为map的key，而把实际的数据值作为value，这样每次都使用的是自己内部的数据。

​    **注意：在线程池中，如果使用ThreadLocal需要注意垃圾回收，需要自己定remove然后在调用ThreadLocal的remove方法，这样在每次使用完毕后会去进行回收。而如果没有remove，由于线程池中线程在使用完毕后是没有结束的，而是直接运行下一个任务或者继续等待，此时会一直关联着已经使用完毕的数据对象，造成该垃圾不可被回收。**

### 7.spring中提供了哪些配置

​     通常有3中配置方式：1）基于xml配置，这是最基本的配置，通过<bean>标签的方式；2）基于注解的方式，不过spring中默认注解时关闭的，需要在配置文件中开启注解，<beans><context:annotation-config></beans>；3）基于java API进行配置，自定义配置类，通过使用@bean和@configuration注解来实现配置。

````java
@Configuration
public class StudentConfig {
    @Bean
    public StudentBean myStudent() {
   		 return new StudentBean();
    }
}
````

### 8.spring 中常见的注解

  @componment：将被标记的java类作为普通的bean对象进行注入；

  @controller：将被标记的类作为spring web MVC控制器，进行一个注入；

  @service：一般用于服务层；  @repository：常用于数据层，类似于@dao注解。

### 9.spring中@resource和@autowired的区别

​    共同点： 两者都可以用来作为bean的导入，可以作用于字段上，setter方法上，都需要导包，但是@resource是javax.annotation.Resource下的，而@autowired是spring包下的；

   不同点：

   1）autowired默认情况下是按照bytype进行注入，默认情况下他要求依赖对象必须存在，如果可以为空，需要设置required=false，如果我们想按照byName进行注册，需要结合@Qualifilter注解使用；

   2）resource：默认是按照byName进行注入，其有两个属性name和type，而Spring将@Resource注解的name属性解析为bean的名字，而type属性则解析为bean的类型。所以，如果使用name属性，则使 用byName的自动注入策略，而使用type属性时则使用byType自动注入策略。如果既不制定name也不制定type属性，这时将通过反射机制 使用byName自动注入策略 。

​    3）@Resource装配顺序：

​      ①如果同时指定了name和type，则从Spring上下文中找到唯一匹配的bean进行装配，找不到则抛出异 常。

​      ②如果指定了name，则从上下文中查找名称（id）匹配的bean进行装配，找不到则抛出异常。

​      ③如果指定了type，则从上下文中找到类似匹配的唯一bean进行装配，找不到或是找到多个，都会抛出异常  

​      ④如果既没有指定name，又没有指定type，则自动按照byName方式进行装配；如果没有匹配，则回退为一个原始类型进行匹配，如果匹 配则自动装配。@Resource的作用相当于@Autowired，只不过@Autowired按照byType自动注入 

### 10.springmvc 的作用

   为web开发提供了视图-模型-控制器等随时可用的组件，用于开发灵活松散的web应用程序。

### 11.DIspatherServlet的工作流程

   1）前台向服务器发送http请求，DispatherServlet对请求进行捕获，然后对其进行解析，找到对应的资源标识符URI，然后根据URI调用handlerMapping获取该处理器映射器对应的所有的对象（包括处理器对应的拦截器），然后以执行链的方式返回给DispatherServlet。

  2）核心控制器DispatherServlet根据handler分配对应的处理器适配器HandlerAdapter。

  3）处理器适配器提取request中的请求模型，填充handler入参，然后执行该handler，执行过程中会调用controller，service等业务逻辑处理。

  4）执行完毕后向DispatherServlet中返回一个modelandview对象；

  5）核心控制器DispatherServlet根据modelandview找到一个合适的ViewResolver，并返回给核心控制器；

  6）DispatherServlet根据model和view进行解析，来渲染视图；

   7）视图将渲染后的结果返回给前台用户。

### 12.springmvc 中的实现重定向和请求转发

  重定向：在返回值前加上redirect，比如："redirect:http://www.baidu.com" 

  转发：在返回值前加上forward，比如：“forward:user.do?name=method4”

  两者区别：

​    1）重定向访问服务器两次，而请求转发访问服务器一次；

​    2）请求转发的URL地址栏不会发生改变，而重定向则会发生改变；

​    3）请求转发只能转到web应用内，而重定向可以请求任意资源；

​    4）请求转发相当于同一个请求，可以共享数据和session数据，而重定向是一个新的请求不能共享请求数据。

### 13. srpingmvc 的常用注解

  @requestMapping：用于处理URL映射的注解，可以用于类上，方法上，用于类上作为请求的父路径，用于方法上表示将要访问的具体路径。

  @requestBody：注解实现接收http请求的json数据，将json转换为java对象。

  @respondsBody：注解实现将conreoller方法返回对象转化为json对象响应给客户。

### 14.spring中用到了哪些设计模式

  单例模式，代理模式，适配器模式，工厂模式

## 10.Linux

###   1.平时如何查看日志

​     1）可以通过命令tail -f -n 1000 文件名 ：即循环显示文件最后1000行信息。其中 f可以循环查看，n:显示行数  。也可以将日志文件下载至本地进行查看分析。

   2）通过使用head命令，和tail命令类似，从开头查看

   3）cat 命令，显示文件的全部内容

###  2.如何为某一个文件夹建立软连接？

  软连接：就是为某一个目录在另外一个目录下建立不同的连接，其目的是节省空间，创建一个虚拟的目录，指向源目录文夹。可以使用ln -s 源文件  目标文件  这个命令，建立软连接后无论更改那个目录文件夹下的文件，另外一处也会发生改动。对应的硬链接ln 源文件 目标文件 ，硬链接其实就是复制，避免误删数据。

### 3.Linux中如何设置定时任务？

  通过crontab来进行指定，时间间隔单位可以是时分秒，也可以是年月日周等。其配置步骤为：

  1）首先需要创建一个任务，可以是一个或者一串命令，也可以是脚本；

   2）使用命令crontab -e  编辑定时任务（或者创建一个文本文件，在文件里写定时任务，然后在-e 后面添加文件名），将本地创建的任务添加到crontab的队列中；**注意：其中定时任务的格式，分 时 日 月 年  1）创建的任务脚本名称。注意中间有间隔。**

   3）可以使用命令查看crontab中添加的任务 crontab -l。

### 4.查找文件命令

  find 目录 参数  ：其中参数有-name 文件名 ；  -size 大小  -ctime 时间  -user 用户  -per 权限

### 5.查找内容：grep、awk、sed

   grep :擅长查找内容，主要用于过滤，格式为grep 参数 关键字  ，其中参数有：-n 显示匹配的行号，-v 显示不被匹配的行，-e 显示多个逻辑之间的关系（or的关系），比如 grep -e 关键字1 -e 关键字2 文件名，在文件中查找关键字1或者关键字2.

  awk：擅长取列，

  sed：擅长取行和替换，

### 6.Linux中查看内存的命令

free 查看系统剩余内存情况， 常用参数-s 进行动态查看，也可以使用top命令进行查看。

## 11.maven和git相关，docker

详见docker.md文档。

###   1.docker

   它是一个应用容器引擎，它以容器的形式将你的应用程序及其所有的依赖进行打包，通过镜像的安装，从而可以保证在任何环境中都可以运行。

###  2.docker的常见命令

   docker pull [options] NAME[:TAG]  从远程仓库中根据镜像名称拉取或者更新镜像，可以指定版本，如果未指定，默认拉取最新的镜像

  docker images [options] [REPOSITORY[:TAG]] 查看镜像详细信息，如果不指定参数默认查看所有的镜像信息

  docker run [options] IMAGE[:TAG] [COMMAND] [ARG..]  启动某一个镜像

  docker rmi 镜像的标识    删除本地的镜像

  docker ps -a   可以查看镜像的运行状态

  docker exec -it XXX bash   进入到某一个容器的内部，XXX可以是容器的name或者容器的id

  docker stop/start  name  停止/启动某一个容器。

### 3.docker运行镜像的流程

​     执行docker pull 首先会检查本地是否有该镜像，如果有不做任何处理，否则会从配置好的镜像仓库中下载该镜像至本地；

​     执行docker run 首先同样会检查该镜像是否存在，如果不存在和docker pull 一样会先下载镜像至本地，然后运行并启动该镜像作为一个docker容器。

##  12.springmvc

  它是spring框架的子模块，通过将复杂的web层应用分成model、view、controller3层，每层都能够完成各自不同的职责，实现了职责解耦。

### 1.简要描述一下springmvc的执行流程：

   1）当前台浏览器发送http请求给服务器，核心控制器DispatherServlet捕获该请求，获取URL访问资源；

   2）DispatherServlet根据URL去匹配对应的handlerMapping；

   3）handlerMapping找到对应的处理器，返回所有的对象包括处理器对象和拦截器对象，并将对象以链的形式返回给核心控制器；

   4）核心控制器通过调用处理器适配器，找到对应的处理器controller；

   5）处理器controller调用业务层执行相关逻辑，并将返回值封装到modelandview对象，返回给DispatherServlet；

   6）核心处理器将返回结果交给对应的视图解析器，视图解析器进行解析，生成具体的view，返回给核心控制器；

   7）核心控制器根据view进行渲染并将结果返回给用户。

### 2.springmvc 中的实现重定向和请求转发

  重定向：在返回值前加上redirect，比如："redirect:http://www.baidu.com" 

  转发：在返回值前加上forward，比如：“forward:user.do?name=method4”

  两者区别：

​    1）重定向访问服务器两次，而请求转发访问服务器一次；

​    2）请求转发的URL地址栏不会发生改变，而重定向则会发生改变；

​    3）请求转发只能转到web应用内，而重定向可以请求任意资源；

​    4）请求转发相当于同一个请求，可以共享数据和session数据，而重定向是一个新的请求不能共享请求数据。

###  3. srpingmvc 的常用注解

   @requestMapping：用于处理URL映射的注解，可以用于类上，方法上，用于类上作为请求的父路径，用于方法上表示将要访问的具体路径。

   @requestBody：注解实现接收http请求的json数据，将json转换为java对象。

   @respondsBody：注解实现将conreoller方法返回对象转化为json对象响应给客户。

### 4.springmvc中如何解决乱码问题：

  1）如果请求是post请求则可以在web.xml文件中通过配置字符集编码过滤器CharacterEncodingFilter，设置值为utf-8。或者自定义拦截器来进行编码处理，然后在springmvc的配置文件中引入自定义拦截器进行处理。

  2）如果是get请求：A.可以通过修改Tomcat的字符集编码和项目编码保持一致；B.通过在控制器接收参数时通过代码的方式进行转码，比较麻烦.new String(request.getParamter("userName").getBytes("ISO8859-1"),"utf-8")。

### 5.springmvc中如何自定义拦截器

  自定义类实现handlerIntercepter接口，实现拦截方法，然后在springmvc中配置引入自定义拦截器。<mvc:interceptors>

````xml
<!-- 配置SpringMvc的拦截器 -->
<mvc:interceptors>
    <!-- 配置一个拦截器的Bean就可以了 默认是对所有请求都拦截 -->
    <bean id="myInterceptor" class="com.zwp.action.MyHandlerInterceptor"></bean>
 
    <!-- 只针对部分请求拦截 -->
    <mvc:interceptor>
       <mvc:mapping path="/modelMap.do" />
       <bean class="com.zwp.action.MyHandlerInterceptorAdapter" />
    </mvc:interceptor>
</mvc:interceptors>
````

### 6.@*RequestParam*参数说明

​    可以用在方法的参数上，为参数设置默认值，当提交参数名称和接收参数名称不一致时可以使用该注解。

````java
 /**
     * 如果提交参数为空时,可以设置默认值
     * 	@RequestParam
     * 		value/name 接收参数的名称
     * 		required=true 为必填参数
     * 		defaultValue="" 设定默认值
     */
    @RequestMapping("/addUser")
    public String addUser(@RequestParam(name = "id",defaultValue = "100") Integer id,
                          @RequestParam(name="name",defaultValue = "张三") String name){

        System.out.println("动态获取数据:id:"+id);
        System.out.println("动态获取数据:name:"+name);
        //新增成功之后.跳转到user页面中
        return "user";
    }
````



## 13.redis相关

### 1.什么是redis

​    redis是一个开源的，基于key-v键值对存储的一个非关系型数据库，基于内存存储，因此读写速度非常快，主要支持String、list、set、Zset、hash等结构。此外也支持事务、持久化操作，也可以用来做分布式锁。

###   2.redis的数据结构及使用场景

   redis中主要有5种数据结构，字符串String，哈希表，集合list，set，Zset等，其中：

   String：可以用来做最简单的数据缓存，可以缓存简单的字符串，也可以用来缓存某个json格式的字符串，redis的分布式锁就是利用这种数据结构，也可以用来作为计数器，session共享，分布式ID等。

  哈希：可以用来存储一些key-v对，多用来存储对象。

  列表list：可以用来缓存微信公众号，或者微博等消息。

  集合set：和list相似，但是是无序的且不能重复，可以用来做共同关注的人，朋友圈点赞等。

 集合Zset：可以用来实现排行榜等功能。

### 3.redis的优缺点

  优点：读写速度快；支持多种数据结构；支持事务，支持持久化操作，AOF和RDB方式。
  缺点：容易受设备容量大小的限制；不支持在线扩容；不具备自动容错和自动恢复功能，必须重启或者手动切换IP。

### 4.简要说一下redis的持久化操作

​      主要有两种方式AOF和RDB。其中RDB是采用了定期快照的方式进行redis的全库备份，这也是redis的默认持久化操作，该机制的触发条件是手动触发或者自动触发，对于该方式redis会单独创建一条线程来进行持久化操作，而主进程不会进行任何的IO操作，因此效率极高。而AOF则是通过保存更新数据库状态的指令来进行持久化操作，以append的方式进行追加，下次启动后执行AOF文件内容来恢复之前的数据库状态，由于是保存指令所以该文件的内容会比较的大，恢复速度也会比较的慢，如果AOF文件内容过大时会执行AOF重写，合并一些重复的指令等操作。

1）两者的区别 

​     a. 对于RDB由于是快照的方式进行备份，它会先将内容写到临时文件中，持久化结束后会将该临时文件覆盖掉原有文件，相对于数据量大时比AOF的启动效率高；

​    b.由于RDB是间隔一段时间进行持久化，所以如果在间隔期间，redis发生故障容易造成数据丢失，而对于AOF来说，由于是记录每一条命令，所以其数据丢失的可能性较小，且它还支持重写机制，当文件内容过大时会压缩文件，重写命令。

​    c.对于AOF来说，文件内容比RDB要大的多，恢复速度相对较慢。

2）推荐使用：

   RDB和AOF混合使用，首先由RDB定期完成内存快照的备份，然后再由AOF完成两次RDB之间的数据备份，由这两部分共同构成持久化文件。该方案的优点是充分利用了RDB加载快、备份文件小及AOF尽可能不丢数据的特性。缺点是兼容性差，一旦开启了混合持久化，在4.0之前的版本都不识别该持久化文件，同时由于前部分是RDB格式，阅读性较低。一般如果对数据要求不敏感的可以使用RDB模式。

### 5.缓存穿透，缓存击穿，缓存雪崩，缓存预热

​    1）缓存穿透：是指在redis中查询了一个不存在数据，此时回去数据库请求查询，如果某一时间段有大量请求会导致数据库服务器压力过大。

 解决办法：如果从数据库中获取值为null也将其放入到redis缓存中；通过布隆过滤器进行拦截。

   2）缓存击穿：是指由于redis中的key设置了过期时间，但是恰巧在过期的时候有大量的请求而只能去数据库中进行查询，导致数据库服务的压力过大。

 解决办法：通过对key进行设置互斥锁，在进行查询时如果key上有所，说明正在进行数据库请求，稍等一会直接去缓存中查询，否则再查询数据库；设置key永不过期。

   3）缓存雪崩：和缓存击穿一样的问题，只是缓存雪崩是指有大量的key在同一时间过期，此时有大量的疯狂请求进来，导致数据库直接崩掉。

  解决办法：尽量避开设置key的相同的过期时间，可以在原有到期时间的基础上加上一个随机数时间，错开过期。

   4）缓存预热：在项目启动后就陆陆续续的去加载数据，而不是等到请求到达时再进行请求。

  解决办法：直接写一个刷新页面的脚本，在启动后点击刷新预先加载一部分数据。

###  6.redis的集群策略

​    主要提供了3中集群策略：

  1）主从模式：这种模式比较简单，主库可以进行写，并且可以与从库进行同步（先快照备份，然后发送给从库，发送期间将新生成的命令缓存起来，发送备份完成后，连同快照和命令一同发送）。这种模式客户端可以连接主库也可以连接从库，但是如果主库挂掉了，需要手动修改IP连接从库，而且这种模式由于受限于机器内存大小，不容易进行扩容。

  2）哨兵模式：这种模式是在主从模式的基础上增加了哨兵节点，可以用来监听节点的变化，如果主节点宕机后，会从从节点中选取一台作为主节点，可以保证集群的高可用，但是也不能够解决容量问题。

  3）cluster模式：支持多主多从，而且是按照key进行槽位分配，可以使得不同的可以落到不同的节点上，这样可以使得整个集群支持更大的数据容量，同时每个主节点拥有自己的多个从节点，保证了集群的可用性。

   因此在选用集群策略时，如果数据量不大，可以使用哨兵模式，而如果数据量比较大时可以选择cluster模式。

### 7.redis的分布式锁

​     setnx来抢夺锁，如果抢夺成功，预先给key设置一个过期时间，如果后续忘记解锁或者由于程序异常导致没有释放锁，在过期时间后会进行锁的释放。首先set设置一个锁，然后get去获取锁，如果获取成功，执行相关逻辑，执行完成后删除锁，否则稍后重试。

### 8.简单说几种redis内存的淘汰策略

​    1）如果内存不够，直接抛出异常；2）从最近很少使用的key中淘汰；3）会随机清除key；4）从设置了过期时间的key中选择最近很少使用的；5）从设置了过期时间的key中随机选择一个key进行删除；6）从key中选择最早到期的key。

### 9.redis的过期键的删除策略

   1）定时过期：给每一key绑定一个定时器，用来时刻监控当前key是否过期，如果过期及时清理，这种方式能够有效的清除过期的key，但是由于需要实时的进行监控，所以会大量占用CPU资源，影响性能。

   2）惰性过期：在每次去访问key的时候去判断当前key是否过期，如果过期进行删除，然后再去数据库中进行查询。这种方式如果有大量的key在短时间内没有进行查询，浪费了大量的资源。

   3）定期过期：每隔一定的时间去扫描一定数量的数据库中设置了过期时间的key，如果过期进行删除。这种方式是最折中的办法。

### 10.redis中的事务

   主要包括3个部分，开启事务multi，提交事务exec,取消事务discard，watch监控等命令组成，并且事务支持执行多条命令，在事务提交exec之前，命令缓存在队列中，直到exec时才执行命令。而且命令执行过程会按照串行化的方式执行队列的命令，即按照命令入队的顺序执行。

   主要包括3个阶段：

   multi：开启事务，此时总是返回一个OK，执行完毕后客户端可以继续发送命令，这些命令被缓存在队列中，当调用exec命令时才会执行。

  exec：提交事务，当输入exec时，执行队列中的命令，此时在执行的过程中如果发现有命令出错，redis会忽略这些错误，正确执行的命令会被提交。如果在exec执行之前，也就是客户端发送命令给redis时，如果命令正确返回QUEUED，此时命令已入队列，而如果命令错误，redis会直接抛出，此时事务不会提交，之前已执行的命令也不会生效。

   discard：如果在执行exec之前执行了discard命令，那么当前事务会被取消，即事务不生效。但是如果已经执行了exec且成功提交后，redis事务是没有回滚的。

   watch：用于监控key值是否发生改动过，如果key被watch标记，在开启事务之前如果该key发生变化，提交事务时会返回一个nil,即事务无法被执行。

## 14.springboot

###   1.简单说一下你对springboot的理解？

​    springboot其实是依靠spring开发的一个开源框架，目的是为了简化spring开发过程中繁琐的配置或者依赖，可以通过引入starter启动器来自动导入所需要的jar包，并维护之间的依赖。而且springboot中内置了运行容器，Tomcat、jetty等，可以实现独立运行。通过约定大于配置的原则，简化了spring开发过程中的繁琐的配置文件。

###   2.怎么理解springboot中的starter？

​    也可以叫做启动器，通过在springboot

###  3.springboot中如何使用定时任务？

​    可以有两种方式：spring中自带的定时任务器@Scheduled 注解，另一种是通过引入第三方框架@Quartz;

   1.@scheduled：首先在启动类上开启定时任务器，@enablescheduling，然后在定时任务方法上添加@scheduled（），配置相关属性参数定时器规则即可。

   2.Quartz，首先引入Quartz依赖，然后自定义JobDetail（要做的事）和trigger（触发器），然后将其作为参数进行使用，注意还要在启动类上添加@enablescheduling。

### 4.说说跨域问题，以及如何解决？

  跨域：指的是从一个域名的网页去请求另一个域名的资源，由于浏览器的同源策略，导致请求被拒绝。

  解决办法：资源共享cros

​    1）通过在每一个controller中添加注解@CrosOrigin即可实现，但是如果在每一个controller中去写比较麻烦，而且容易忘记；

​    2）由于所有的微服务请求都走网关，所以我们可以在网关模块进行统一配置，这样如果发送请求，先到达网关，在网关中进行了跨域处理即可放行。自定义类添加注解@configuration表明当前类是配置类，然后自定义方法，该方法返回值为CrossWebFilter，方法中可以设置呢些请求可以放行等逻辑。最后在方法上添加@bean，将该返回对象交给spring容器进行管理。

## 15.springcloud

###  1.什么是springcloud

   它是一个分布式的应用架构，集成了各个springboot项目，用来对这些微小服务进行管理和协调，主要包括服务注册与发现，负载均衡，服务熔断与降级，服务网关，配置中心等。

###  2.简要说一下springboot、springcloud、dubbo

   springboot它是一个开源的服务框架，即就是一个个的微服务，可以独立运行于springcloud之外，而springcloud需要依赖于springboot项目，dubbo则和springcloud类似也是一个分布式应用管理架构。其和springcloud的区别是：

   1）服务调用方式不同：springcloud采用的是restAPI，而dubbo则是接口调用；

   2）注册中心组件不同：springcloud有大量的组件可以使用，比如eureka、nacos、zookeeper、consul等，而dubbo是zookeeper、nacos；

   3）服务网关不同：springcloud常用的有zuul、gateway，而dubbo则未实现服务网关，需要第三方框架。

### 3.springcloud中有哪些组件？

   服务注册中心：Eureka、负载均衡：Ribbon、服务熔断与降级：Hystrix、服务网关：gateway、注册中心：config

### 4.分别聊聊每个组件。

####   1.Eureka：

​     作为springcloud的注册中心，主要包括两个部分，客户端，服务器端。服务器端主要作用是存储客户端信息列表（IP，端口号，服务名等），便于消费者能够从服务器中获取客户端信息。而客户端将自己注册到服务器端，供消费者进行消费。

​    在使用过程中主要有2个依赖starter-Eureka，Eureka-server；3个注解@EurekaServer，@enableEurekaClient，@DiscoverClient

   1）首先会创建EurekaServer服务端模块：引入eureka-server依赖，然后在配置文件中配置服务器端的IP，端口号，服务名等信息，供客户端访问，当然可以进行集群配置。然后在启动类上添加注解@EurekaServer表示当前模块是服务器端。

  2）创建EurekaClient服务模块：引入starter-Eureka依赖，然后在配置文件中配置当前客户端IP，端口号，服务名等信息，供消费者访问，然后在启动类上添加注解@enableEurekaClient注解表示是客户端；

  3）最后是服务调用者，在启动类上添加注解@DiscoveryClient注解，然后在代码中通过RestTemplate进行访问。

#### 2. Ribbon：

​    作为springcloud的负载均衡策略，而且是客户端负载均衡，即就是从服务注册中心的获取所有的服务注册列表，然后将其缓存到本地，在本地实现均衡策略。其常用策略有轮询，随机策略，时间响应加权策略，重试策略等。当然也可以自定义均衡策略。

  1）引入starter-ribbon依赖，然后自定义配置类，在配置类上添加@configuration注解，代表当前类是配置类，然后在配置类中写均衡策略，然后在方法上添加注解@bean，将返回对象交给spring来进行管理，常见的均衡策略对象有RestTemplate、RandomRule等。

  2）然后在启动类上添加@enableEurekaClient注解，即可使用，如果是自定义均衡策略，需要在启动类上添加@RibbonCLient(name="XXX",configuration="xxx.class")，其中name就是访问的客户端服务名，configuration就是自定义的配置类。

#### 3.openfeign：

   作为springcloud的服务接口调用，通过openfeign可以做到通过使用http请求来访问远程服务就像是在访问本地服务一样，其本质是对ribbon+RestTemplate的封装。它是在feign的基础上支持了springmvc的一些常见注解，比如@requestMapping等。

   1）引入openfeign的依赖，然后在启动类上添加@EnablefeignClients注解；

   2）编写远程接口interface，其抽象方法名和远程服务方法名，参数一致，然后在接口上添加注解@enableFeignClient注解即可。

   3）如果需要做兜底方法，自定义类实现该接口，进行兜底方法处理逻辑，然后在接口的注解@enableFeignClient中，通过属性fallback来进行配置。

`````java
@Component
@FeignClient(value = "CLOUD-PROVIDER-PAYMENT",fallback = PaymentFallbackHystrixService.class)
public interface PaymentHystrixService {

    @GetMapping("payment/info/ok/{id}")
    String paymentInfo_OK(@PathVariable("id") Integer id);

    @GetMapping("payment/info/error/{id}")
    String paymentInfo_ERROR(@PathVariable("id") Integer id);
}
`````

   **注意如果想要查看接口调用过程中的日志信息，可以进行配置设置日志级别：**

   1）首选自定义类，添加配置注解@configuration，然后自定义方法，返回feignLoggerLevel对象，方发中可以设置日志级别，然后在方法上添加注解@bean交给spring进行管理。常见的日志级别有：

- NONE：默认的，不显示任何日志。
- BASIC：仅记录请求方法、URL、响应状态码及执行时间。
- HEADERS：除了BASIC中定义的信息之外，还有请求和响应的头信息。
- FULL：除了HEADERS中定义的信息之外，还有请求和响应的正文及元数据。

  2）然后在application.yml中添加本地日志打印级别即可。

````java
@Configuration
public class FeignConfig {
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
````

````yaml
logging:
  level:
    com.caochenlei.service: debug
````

#### 4.hystrix：

​    它是分布式系统中的一套服务熔断机制，可以用来解决当在各微服务之间进行调用过程中，由于网络原因或者一些异常情况，比如超时等情况下，会导致该连路上其他调用形成阻塞，导致服务宕机或者服务雪崩。通过服务熔断机制可以给调用方返回一个预期可处理的结果，而不是长时间的等待或者直接抛出异常。常见的有服务熔断，服务降级、服务限流等措施。

  其中服务熔断：就是当在指定的时间窗内如果请求失败率达到一定的阈值时，系统会通过熔断器将此链路直接断开，并返回给客户端一个预先的值，在时间到达后会向服务器发送一条请求进行尝试 ，如果请求成功，关闭熔断器，恢复原链路请求，否则继续等待下一次的时间进行尝试；而服务降级其实就是一个兜底的方法，当出现异常等情况下返回给客户一个预先设定的值。服务限流则是通过判断单位时间内访问频率进行控制。

  1）引入hystrix相关依赖，然后在启动类上添加注解@EnableHystrix，开启服务熔断；

  2）在本地接口方法上添加注解@HystrixCommand(fallbackMethod=”XXX”)即可使用。

#### 5.gateWay：

​     作为springcloud的服务网关，可以对请求做统一处理，其核心功能是请求转发，也可以用来解决**跨域**问题，熔断，限流，日志监控，反向代理等。其主要包括路由、断言、过滤。其工作流程：首先客户端发起http请求，gateWay接收请求并去寻找handlingmapping匹配对应的断言，如果匹配成功将请求发送到对应的handler中，然后handler在通过过滤器链将请求发送到我们实际要执行的业务逻辑上，进行业务逻辑处理，处理完成后在进行返回。而我们在过滤器链中可以对发送之前的参数进行校验，权限限制等，并且也可以在返回之前对结果做一次封装等操作。

#### 6.springcloud config：

​    作为分布式应用的配置中心，可以用来对各微服务的配置文件进行统一集中式管理，针对不同环境，不同配置实现动态刷新。主要包括服务端和客户端，其中服务端主要用于存储配置文件，以接口的形式对外暴露配置内容，而客户端通过接口来获取自己的配置文件信息，已达到初始化自己。目前主要通过git/svn的方式作为配置中心。当然也可使用本地存储的方式，如果使用本地存储的方式，在 `application.properties` 或 `application.yml` 文件添加 `spring.profiles.active=native` 配置即可，它会从项目的 **resources**路径下读取配置文件。如果是读取指定的配置文件，那么可以使用 `spring.cloud.config.server.native.searchLocations = file:D:/properties/` 来读取。

  1）服务器端，创建服务器模块，主要用于配置git/svn、本地库等仓库信息：引入spring-cloud-config-server依赖，然后在配置文件中进行仓库信息配置，在启动类上添加@Enableconfig注解。

````properties
 spring.application.name=springcloud-config-server  // 这个是指定服务名称。
 server.port=9005                                  //服务指定的端口。
 eureka.client.serviceUrl.defaultZone=http://localhost:8005/eureka/
 spring.cloud.config.server.git.uri = https://github.com/xuwujing/springcloud-study/   //配置的Git仓库的地址。
 spring.cloud.config.server.git.search-paths = /springcloud-config/config-repo        //git仓库地址下的相对地址 多个用逗号","分割
 spring.cloud.config.server.git.username =          //仓库的账号
 spring.cloud.config.server.git.password =          //仓库密码
 
 #如果是本地配置，将spring.cloud.config.server.git相关配置删除，添加spring.profiles.active=native，然后在resource目录下创建对应的配置文件即可。
````

  2）配置客户端信息，在实际功能模块，引入config-starter依赖，然后创建bootstrap.yml文件，在文件中配置当前服务需要的获取远程配置信息的位置，然后在业务逻辑类controller中添加注解@RefreshScope实现动态刷新。**注意此处的动态刷新是指在应用程序启动时回去指定位置加载配置信息，而如果后续对配置文件改动，需要手动发送post请求来动态刷新，而服务本身是不能够自动感知的。curl -X POST http://localhost:3355/actuator/refresh**。

````yaml
spring:
  application:
    name: config-client
  cloud:
    config:
      label: master  #分支名称
      name: config  #配置文件名称
      profile: dev  #读取后缀名称   上述三个综合http://localhost:3344/master/config-dev.yml
      uri: http://localhost:3344  #配置中心的地址
````

#### 7.springcloud bus

​    bus也叫消息总线，其实就是一个主题，我们可以将消息发布在该主题上，如果订阅了该主题的服务就能接收到发布的消息，可以结合springcloud config来使用，来动态刷新配置文件内容的变化。

​    其实现原理就是bus会对外提供一个接口bus/refresh，然后将该接口绑定到远程仓库上，这样如果远程仓库内容发生变化，会调用该接口进行通知，如果订阅了该消息，应用服务器就会感知这一变化，从而达到动态刷新的功能。

  1）引入依赖spring-cloud-starter-bus-amqp；

  2）修改bootstrap的配置内容：

````yaml
rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest

#rabbitmq相关配置，暴露bus刷新配置的/bus-refresh接口
management:
  endpoints:  #暴露bus刷新配置的端点
    web:
      exposure:
        include: 'bus-refresh'
````

   3）然后发送请求时，发送URL请求给configserver一次，即所有的服务器端、客户端都会刷新最新的内容，当然也可以进行定点推送。

​           curl -X POST http://localhost:3344/actuator/bus-refresh

### 5.Ribbon和nginx的均衡策略区别

  ribbon是客户端负载均衡，而Nginx是服务器端负载均衡。

   ribbon是客户端的负载均衡，它会从服务注册中心的获取服务注册列表，然后将其缓存到本地，在本地实现均衡策略。
   nginx是服务器端的负载均衡，它先拿到所有客户端的请求，然后统一交给nginx，由nginx进行负载均衡转发，实现均衡策略。

## 16.事务相关

### 1.简要说明事务

​    事务就是指在处理某一件事的过程中的一系列操作，这些操作要么全部成功，要么全部失败。事务的四大特性：原子性，一致性，隔离性和持久性。主要分为本地事务和分布式事务，其底层都是基于数据库事务。

​     本地事务:也叫单体应用事务，就是我们之前做单体应用开发中的事务，对于本地事务的管理非常的简单。

​     分布式事务：即就是分布式应用程序开发过程中的事务。

### 2.事务的特性

 事务的四大特性ACID，其中：

  A原子性：是指组成事务的逻辑单元密不可分，缺一不可，这些操作要么全部成功，要么全部失败。

  C一致性：是指数据的一致性，即在事务执行前后必须保证数据的准确性。

  I隔离性：在并发过程中，事务之间是不会相互干扰的，彼此相互独立的运行在自己的事务中。

  D持久性：是指在事务执行完毕后，对数据的修改必须是保存下来，不能因为系统错误或者其他异常而导致数据丢失。

### 3.事务的隔离级别

  读未提交，读已提交，可重复度，串行化读。其中隔离级别越高，由于锁的原因，其性能越低。并且一般情况下脏读是不被允许的，而幻读或者不可重复度在某一些情况下是被允许的。mysql的默认隔离级别是可重复度，而oracle是读已提交。

### 4.事务的传播机制

   REQUIRE：一个事务，如果存在事务就使用，如果不存在创建一个新的事务。

   REQUIRE_NEW：两个事务，如果当前有事务挂起，创建一个新的事务，并且两个事务之间不会互相影响。

   supports：支持当前事务，如果存在事务就使用，如果不存在不使用事务。

   not_supports：以非事务的方式运行，如果当前存在事务将该事务挂起。

   NEVER：从不使用事务，如果有事务抛出异常。

###    5.springboot中整合事务

​      添加注解@transaction注解即可，常见属性有isolation设置隔离级别，propagation设置传播机制。主要包括声明式事务和编程式事务。

####   1.声明式事务：

​     主要可以通过注解或者基于xml文件的形式进行开发。如果是注解只需要在配置文件中开启注解配置<annotion:context-config>,然后在业务逻辑方法上添加@transaction注解即可使用；而如果是基于xml进行配置的话，首先配置数据源DataSource，然后配置事务管理器transactionManage，并向其中注入DataSource，然后通过aop的方式进行事务的配置：定义一个增强tx:advice,注入transmanager，然后配置增强事务的属性；最后aop配置定义切面和切入点。

####  2.编程式事务

   主要是在配置文件中配置事务模板，然后通过调用事务模板的execute方法进行回滚调用。

**注意在spring实际开发过程中，在同一个类中编写了两个方法，并且都进行了事务的配置，此时如果在一个方法中调用另一个或者互相调用时，会发现事务并未有起作用，这是因为在spring中将这些事务配置存放在代理对象中，通过代理对象进行调用。而如果只是在方法内部直接调用方法，其实是相当于把两一个方法中的代码复制到当前方法中**，配置的事务并未起作用，因此必须得通过代理的方式进行调用，具体做法为：

   1）首先导入spring-boot-starter-aop的依赖；

   2）然后在启动类上添加注解@EnableTransactionManagement(proxyTargetClass = true）以开启事务管理；

   3）在添加@EnableAspectJAutoProxy(exposeProxy=true)开启自动代理；

   4）最后在调用的方法中通过XXXService XXX = （XXXService）AopContext.currentProxy() 方法获取获取当前类的代理对象，然后通过代理对象调用相关方法即可。XXX.方法名()即可。

### 6.分布式事务

   在分布式系统开发中由于网络等原因不可避免的会出现一些不可预知的错误，因此对于分布式事务的控制是必不可少的。当前流行的分布式事务主要有基于seata的实现的2阶段事务提交、TCC、消息事务+最终一致性、最大努力通知。

#### 1.   2阶段事务提交(2pc)

​    将事务分为两个阶段准备阶段和提交阶段，准备阶段：事务协调器向个分支事务发起事务请求，个分支事务执行本地事务，并写redo/undo日志，如果执行成功向事务协调器返回yes，否则返回no，此时本地事务并没有提交。提交阶段：根据第一阶段各分支事务反馈结果，如果都成功，则向各分支发送统一提交请求，各分支事务接收请求后进行本地事务提交，并释放资源；而如果有一个本地事务反馈为no，则向各分支事务统一发送回滚请求，个分支事务接收回滚请求后进行本地事务的回滚，并释放资源。

​    **目前主流数据库均支持2pc事务，但是由于是第二阶段进行本地事务提交，并且需要个分支阶段全部执行完成本地事务后才能进行统一的提交并释放资源，所以性能较差。因此seata在2pc的基础上进行了改造。**

#### 2.seata分布式事务

​    是springcloud alibaba提供的一套分布式事务解决方案， 它是在2pc的基础上将第一阶段准备阶段进行了拆分，主要包括3部分，事务管理器，事务协调器，参与者，并且在第一阶段各分支事务执行完毕后，直接提交本地事务，然后向事务管理器进行反馈，如果事务管理器中所有分支事务都是成功的，则向事务协调器反馈，发起全局提交；而如果有一个分支事务是失败的则会进行全局回滚。此时主要是通过补偿式回滚。

#### 3.TCC模式

​    是指try（预留）、confirm（提交）、canal（取消）三个单词的缩写，针对每一个子分支都有着3个接口对外暴露，其核心本质是补偿机制，即针对每一个操作，都要有与之对应的confirm和canal，TM首先发起所有分支事务的try操作，任何一个分支事务的try操作失败，TM将会发起所有分支的canal操作，如果所有的try操作都成功，TM将会发送所有分支的confirm操作。

其主要实现流程是：

   1）第一阶段：主业务服务分别调用所有业务的try操作，并在事务管理其中注册登记所有的分支事务，当所有的try调用成功或者其中的某一个try失败则进入第二阶段；

  2）第二阶段：事务管理器根据第一阶段的返回结果来执行confirm或者canal方法，如果第一阶段所有的try都成功，则调用所有服务的confirm方法，如果有一个抵用失败，则调用对应服务的canal方法，执行回滚操作。

**但是，由于该模式在开发过程中需要针对每一个try预留资源都需要有对应的confirm和canal，因此程序的编写量会大大的增加，并且业务的侵入性特别高，所以在一些业务逻辑较强的地方不推荐使用。**

#### 4.消息队列+最终一致性

​    它是指执行完本地事务后向消息队列中发送一条消息，消费者在从队列中获取消息，进行消费方的本地事务，最终实现一致性。因此必须得保证消本地事务、消息发送的原子性，即本地事务成功，消息必须发送成功，本地事务失败，消息不能被消费者消费。并且还要保证消费者的幂等性消费。

​    实现思路：将本地事务与消息发送置于同一个事务中，如果本地事务回滚，消息就不会发送，如果消息发送失败，回滚本地事务这样就保证了消息发送的可靠性；如果消息被发送到MQ服务器中，利用MQ的ack机制，保证消息会被其他子系统消费，实现最终一致性。

​    1）在事务发起方中创建消息的数据库表，向数据库中插入记录，然后创建一个定时任务去扫描消息表，如果任务未发送向mq中发送消息，直到该消息已被发送出去；

   2）使用消息队列的ack确认机制，如果消息队列已经收到了消息，则会返回一个标志，从而保证队列已收到消息；

基于RocketMQ实现分布式事务：

   首先生产者向MQ发送一条消息，此时MQ中的消息时半消息状态（即不能被消费者消费）；MQ接收消息后会向生产者发送一条ack确认消息，代表接收成功；MQ接收到ack消息后会执行本地事务，执行完毕后根据事务执行成功或者失败，向MQ发送一条commit或者rollback指令，MQ收到指令后对于commit状态的指令，直接向消费者发送消息，执行消费逻辑，而对于rollback的直接作废，在一定时间后消息队列会进行清除，此时整个逻辑完成。而如果MQ没有收到生产者发送的二次确认消息时会主动发送一条请求去回查指令，对于commit的同样将消息发送给消费者，否则消息作废。

#### 5.最大可能通过

​    本地事务执行完成后，将消息发送给消息队列，供消费者进行消费，如果发送失败重新发送直到发送成功，而不用重视接收方是否能够接收到该消息，如果不能消费可以调用接口方法查看。所以可以看出该机制是对于发起方来说有一定的消息重复机制，即也就是如果发送通知失败尽最大可能去重新发送；其次需要提供校对机制，如果接收方接收失败可以通过调用接口进行查询。

#### 6.常见分布式事务的比较

​    1）2pc---seata方案：它的最大的诟病是阻塞协议，由于是基于数据库DB的（undo_log），并且各分支RM必须等待TM的决定后才释放资源，所以会出现资源的阻塞，这种方案不适用于并发较高且子事务生命周期较长的分布式中，但是其强一致性较好；

   2）TCC方案：try-commit-canal，它是基于应用层进行设计的，避免了2pc方案的资源阻塞问题，降低了锁冲突，提高了吞吐量，但是其开发成本较高，每个业务分支都需要一套try、commit、canal的代码开发，代码量大，实现难度较高并且业务的侵入性较强；

   3）可靠消息最终一致性：基于RocketMQ来实现的异步操作，只需要保证事务消息可靠性发送，并且接收方能够收到即可。如果接收方正常提交那么就保证了事务最终一致性，如果回滚后续通过手动接入保证最终一致性。适用于事务周期性较长且实时性要求不高的业务场景，比如注册送积分，登录送优惠，银行贷款还款业务等；

  4）最大努力通知：是分布式事务处理方案中对事务要求最低的一种，适用于一些最终一致性要求较低的业务场景，事务的通知方尽最大的努力去通知，如果通知失败，接收方调用其接口查询即可。使用的场景：银行通知，支付结果通知等外部应用程序，通过http等网络接口来监听MQ中的消息。

## 17.数据库mysql

###   1.mysql的存储引擎

​    常见的有3种：Myisam,Innodb,memory等，其中Myisam是默认存储结构，包含了3个文件，表结构、数据、索引，不支持事务，行锁以及外键，而innodb是两种文件，表结构和数据+索引，支持事务，行锁外键等，memory是基于内存进行存储的。

   1.MYISAM：全表锁，拥有较高的执行速度，不支持事务，不支持外键，并发性能差，占用空间相对较小，对事务完整性没有要求，以 select、insert为主的应用基本上可以使用这引擎。

2. Innodb:行级锁，提供了具有提交、回滚和崩溃回复能力的事务安全，支持自动增长列，支持外键约束，并发能力强，占用空间是 MYISAM的2.5倍，处理效率相对会差一些 。
3. Memory: 全表锁，存储在内容中，速度快，但会占用和数据量成正比的内存空间且数据在mysql重启时会丢失，默认使用HASH索引， 检索效率非常高，但不适用于精确查找，主要用于那些内容变化不频繁的代码表

###  2. innodb和Myisam的区别

   1）从存储文件上讲；2）从索引类别上讲；3）从支持事务，行锁，外键上讲。

###  3.请简述一下mysql中的索引

​    1） 索引是什么？它是一个高效获取数据库数据的一种存储结构，里面维护了数据库表的所有所有指针，其本质是一个数据结构，底层基于b+tree来实现，通过索引可以加快查询速度。

   2）常见的索引有：主键索引、唯一索引、组合索引、全文索引等。

   3） 索引的优缺点：使用索可以加快查询效率，但是索引的占用空间较大，   并且维护成本较高，对数据的增删改查都会导致索引的变化。

   4）创建索引的原则：

​      尽量在经常需要查询的列上添加索引；列的取值范围不大，比如性别（男，女）这样的字段不建议使用索引；最好满足最左匹配原则，这样所建立的索引在查询时尽可能会被命中；一些text，blob等数据类型的字段上不建议使用索引，不方便维护，尽可能的设置成主键自增；更新频繁的字段上不建议使用索引，会打乱已经排好序的索引；对于那些经常不使用的或者数据量特别大的列不建议使用索引。

### 4.数据库中的事务

   1）事务是指操作数据库的一系列指令，这些指令要么全部执行失败，要么全部执行成功，从而保证数据的最终一致性，比如银行转账业务，一方入账，一方出账，两者必须同时成功或者同时失败，业务才算完成。

  2）事务的四大特性：ACID，即也就是

   原子性：组成事务的基本单元（所有指令）密不可分，要么全部成功，要么全部失败。底层依赖undo、redo等日志。

  一致性：事务执行前后数据必须保持正确性。

  隔离性：不同事务之间互相独立，互不干扰，各自运行在自己的事务进程中，底层采用了锁机制和mvcc版本控制的方式。

  持久性：事务提交后，对数据库所作的操作必须同步到数据库中，不能因为系统故障灯原因导致事务丢失。采用了redo日志的方式。

3）数据库事务的隔离级别：

  读未提交：一个事务可以读到另一个事务还未提交的数据，在写的时候添加了共享锁，而读操作未加锁，会带来脏读，幻读，不可重复度等问题。

  读已提交：一个事务只能读取到另一个事务已经提交的数据，在读的时候添加了共享锁，命令执行完毕后释放了锁，可以很好的解决脏读问题，但还是会有幻读和不可重复读问题。

  可重复读：对同一字段的多次读取结果都是一致的，除非数据是被本身事务自己所修改，读操作需要加共享锁，但是在事务提交之前并不释放共享锁，也就是必须等待事务执行完毕以后才释放共享锁。可以避免脏读和不可重复读问题。

   串行化读：隔离级别中最高的，完全服从ACID特性，所有的事务依次执行，完全独立，可以完全避免脏读，不可重复度，幻读等问题，但是执行效率慢。

### 5.事务中的锁

  根据分类种类的不同可以划分为：

  1）根据锁的粒度可以分为：

​      行锁：粒度最小，针对数据库表中的某一条记录进行加锁，大大的减少了数据库中表的冲突，但是锁的开销较大，容易产生死锁。

​      表锁：粒度最大，针对数据库中整张表进行加锁，可以减少锁开销，避免死锁产生，但是容易造成数据库中表的冲突，并发度较低。

​      页锁：一个这种的方案。

  2）根据锁的种类：

​    共享锁：也叫读锁，当用户进行数据读取时，对数据加上共享锁，可以有多个。

​    排他锁：也叫写锁，当用户进行数据写的操作时，对数据进行排他锁，只能由一个，和其他的排他锁互斥，共享锁互斥。

  3）根据锁状态：

   乐观锁：时时刻刻都认为数据不会发生冲突，只有在自己进行提交时区判断当前数据是否有冲突。其实现是基于mvcc版本控制的思想，给数据库中的字段添加版本控制字段，每次操作前先获取该字段的版本号，然后进行相关的以为逻辑处理，在提交的时候再次获取版本号，将两次的版本号进行比较，如果一致则允许提交，否则不允许进行事务的提交。

   悲观锁：时钟认为会发生冲突，在操作数据之前先进行加锁，操作结束后释放当前锁。

### 6.mysql中的死锁，如何解决？

   死锁：是指数据库中两个或者两个以上事务在统一资源获取时，互相都想抢占锁，而导致的恶性竞争。

   解决：如果是不同的程序中并发获取锁，此时尽可能的提前约定好获取的顺序；如果是在同一个事务中，尽肯能的一次性获取所有的资源，或者升级锁的粒度为表锁，也可以采用分布式锁。

### 7.视图

​     视图其本质是一张虚拟的表，包含字段，数值等，我们把数据库中的表的某一些特定的字段，或者需要对外暴露的字段通过视图存储起来，这样即可以保证了数据的安全性，也可以方便后续的查询。

​    通过视图可以提高数据的安全性访问，使得逻辑数据变得独立，但是如果视图的数据来源为多张复杂的数据表，那么如果对数据的查询性能会下降，并且不允许进行增删的操作。

​    创建视图的脚本：create or  replace view xxx

### 8.存储过程

  起就是一些事先定义好的SQL脚本，后续可以进行多次的调用，事先某些业务逻辑。

### 9.SQL优化

  主要包括数据库优化和SQL语句的优化。其中数据库的优化主要包括对数据库表的优化，可以将一些不经常使用的字段放到一张表中，或者将一些需要经常性查询的字段放到一张表中，对表进行详细的拆分；对于大表的数据，在进行查询时限制一次性只能查询部分的数据，比如从时间上（半年）等；实现分库分表的方式。

  对于SQL语句的优化：

​    1）在查询SQL语句中尽量使用select column_name from table，具体的列名来代替select *操作；

​    2）避免在where条件中使用or来进行拼接，可能会使得索引失效，可以使用union关键字将结果进行拼接；

​    3）避免在where条件中对字段进行表达式操作；

​    4）应尽量避免在 where 子句中使用!=或<>操作符，否则将引擎放弃使用索引而进行全表扫描；

​    5）应尽量避免在 where 子句中对字段进行 null值判断，否则将导致引擎放弃使用索引而进行全表扫描；

​    6）很多时候用 exists 代替 in 是一个好的选择，尽量避免使用in或者not in；

​    7）查询条件注意最左匹配原则，尽可能多的使用索引进行查询；

​    8）强制类型转换会使得索引失效。

### 10.简要说明一下drop，delete和truncate的区别

在数据库操作中，`DROP`、`DELETE` 和 `TRUNCATE` 都可以用来删除数据，但它们在使用方式、性能影响和事务处理等方面存在一些区别。以下是它们之间的一些主要区别：

1. **操作对象**：
   - `DROP` 用于删除整个表或数据库，包括表结构和数据。
   - `DELETE` 用于删除表中的数据，但保留表结构。
   - `TRUNCATE` 也用于删除表中的所有数据，但保留表结构。

2. **事务处理**：
   - `DROP` 和 `TRUNCATE` 通常不支持事务回滚（`ROLLBACK`），因为它们是DDL（数据定义语言）操作，而DDL操作通常会自动提交事务。
   - `DELETE` 是DML（数据操作语言）操作，支持事务回滚，因此在执行`DELETE`操作后，如果需要撤销，可以使用`ROLLBACK`命令。

3. **性能影响**：
   - `DROP` 和 `TRUNCATE` 操作通常比 `DELETE` 快，因为它们不记录每一行的删除操作，而是直接删除数据页或整个表。
   - `DELETE` 操作会逐行删除数据，并记录每一行的删除操作，这在数据量大时会非常慢。

4. **触发器和约束**：
   - `DROP` 操作会删除表及其所有相关对象（如触发器、索引等），并且不会触发任何触发器。
   - `DELETE` 和 `TRUNCATE` 都可以触发触发器，但 `TRUNCATE` 通常不会触发`DELETE`触发器，除非表定义了`INSTEAD OF TRUNCATE`触发器。

5. **返回值**：
   - `DROP` 和 `TRUNCATE` 操作通常不返回任何值。
   - `DELETE` 操作可以返回被删除的行数。

6. **使用场景**：
   - 如果需要删除整个表或数据库，使用 `DROP`。
   - 如果需要删除表中的所有数据，但保留表结构，使用 `TRUNCATE`。
   - 如果需要删除表中的部分数据或需要根据条件删除数据，使用 `DELETE`。

在实际使用中，选择哪种操作取决于具体需求和数据库的性能要求。在执行这些操作之前，确保已经做好了数据备份，以防不慎删除重要数据。

## 18.mybatis

### 1.什么是mybatis

​    它是一个半自动化的对象关系映射框架，内部是对jdbc的封装，简化了之前原生态开发加载驱动，连接数据源等一系列复杂的过程，使得开发人员只需要专注于SQL语句本身即可。同时可以使用xml或者注解的方式来配置和映射原生信息，将java bean对象映射成数据库的记录，最终执行SQL语句并将结果映射为bean对象返回。

  其优点是SQL语句是写在xml文件中，解除了SQL语句与程序代码的耦合，并且支持动态的SQL语句，可以进行重用。与传动的jdbc操作相比，能够减少大量的代码量，是的程序开发只关注与SQL的业务逻辑，但是这同时也对开发人员的SQL功底要求很高。

### 2.**#{}和${}的区别是什么？**

​       #{}是预处理，${}字符串替换：在进行解析是会将#{}用？来进行替换，然后在调用set方法进行赋值，而${}直接用参数字符串进行替换，所以会有sql注入风险，而#{}可以很好地来防止SQL注入。

### 3.当实体类中的属性名和数据库中的字段名不一致时怎么办？

  1）通过在查询语句中未字段起别名的方式：

````xml
<select id=”selectorder” parametertype=”int” resultetype=”me.gacl.domain.order”>
    select order_id as id, order_no as orderno ,order_price as price form orders where order_id=#{id};
</select>
````

  2）通过resultmap进行自定义映射关系

````xml
<select id="getOrder" parameterType="int" resultMap="orderresultmap">
	select * from orders where order_id=#{id}
</select>
<resultMap type=”me.gacl.domain.order” id=”orderresultmap”>
    <!–用 id 属性来映射主键字段–>
    <id property=”id” column=”order_id”>
    <!–用 result 属性来映射非主键字段，property 为实体类属性名，column
    为数据表中的属性–>
    <result property = “orderno” column =”order_no”/>
    <result property=”price” column=”order_price” />
</reslutMap>

````

### 4.mybatis 中的模糊查询会怎么写？

  1）通过拼接的方式‘%${name}%’,但不建议这么写，可能会有SQL注入的风险，可以换成‘%‘ ||#{name}||’%‘’;

  2）通过bind标签进行绑定，<bind name='name' value="'%'+name+'%'">,在查询SQL中直接通过name进行调用；

  3）使用concat函数，concat(concat('%',#{name}),'%')

###     5.mybatis中mapper接口传参：

​    主要有三种方式：

​      1.接口中普通传参aa(String name,String pwd)可以用参数下标标识，#{0}，#{1}必须的严格按照传入参数去写，比较麻烦容易出错。

​      2.aa(@param("name")  String username) -----#{name}可以自定义名称，推荐，比较直观，适用于参数个数少，如果参数多麻烦。

​     3.多参数，常用的有map和封装对象属性；一般用map，取值时候用map中的key即可。如果用对象，需要扩充属性，麻烦，但是代码可读性比较强。

### 6.mybatis中的动态SQL

  它是指可以在xml文件中以标签的形式编写动态SQL，执行过程中根据表达式进行相关判断并拼接SQL。mybatis提供了9种动态的SQL标签：trim、set、where、foreach、if、choose、when、otherwise、bind。

* 其中if：

```xml
<!-- 示例 -->
<select id="find" resultType="student" parameterType="student">
        SELECT * FROM student WHERE age >= 18
        <if test="name != null and name != ''">
            AND name like '%${name}%'
        </if>
</select>

只有test中条件满足时才会进行SQL的拼接
```

* choose：

```xml
<!-- choose 和 when , otherwise 是配套标签 类似于java中的switch，只会选中满足条件的一个-->
<select id="findActiveBlogLike"resultType="Blog">
  SELECT * FROM BLOG WHERE state = ‘ACTIVE’
  <choose>
    <when test="title != null">
      AND title like #{title}
    </when>
    <when test="author != null and author.name != null">
      AND author_name like #{author.name}
    </when>
    <otherwise>
      AND featured = 1
    </otherwise>
  </choose>
</select>
```

* where:

````xml
<select id="findActiveBlogLike" resultType="Blog">
  SELECT * FROM BLOG
  <where>
    <if test="state != null">
         state = #{state}
    </if>
    <if test="title != null">
        AND title like #{title}
    </if>
    <if test="author != null and author.name != null">
        AND author_name like #{author.name}
    </if>
  </where>
</select>

其中where标签只有内部标签至少有一个标签命中条件时，才会进行where拼接，并且如果条件中的首个拼接语句包含了and或者or时会自动的去掉，也可以用trim替换
<select id="findActiveBlogLike" resultType="Blog">
  SELECT * FROM BLOG
  <trim prefix="WHERE" prefixOverrides="AND | OR">
     <if test="state != null">
         state = #{state}
    </if>
    <if test="title != null">
        AND title like #{title}
    </if>
    <if test="author != null and author.name != null">
        AND author_name like #{author.name}
    </if>
  </trim>
</select>
````

* set标签，在update更新语句中使用，和where类似，只有子条件至少返回一个时才会添加set，并且如果是以，开头的会自动删除，再进行拼接。y额可以使用trim，

````xml
<trim prefix="SET" prefixOverrides=",">
   ...
</trim>
````

* SQL标签，用于将重复的SQL内容提取出来，在使用的时候通过include标签进行引用

````xml
<select id="findUser" parameterType="user" resultType="user">
	SELECT * FROM user
	<include refid="whereClause"/>
</select>

<sql id="whereClause">
     <where>
         <if test user != null>
         	AND username like '%${user.name}%'
         </if>
     </where>
</sql>
````

* bind标签，可以用来做一些表达式的拼接，比如模糊查询等。

````xml
<select id="selectBlogsLike" resultType="Blog">
  <bind name="pattern" value="'%' + _parameter.getTitle() + '%'" />
  SELECT * FROM BLOG WHERE title LIKE #{pattern}
</select>
````

### 7.mybatis中是否支持延迟加载？原理是什么?

​     Mybatis 中支持 association（一对一） 关联对象和 collection（一对多） 关联集合对象的延迟加载，默认情况下是关闭的，可以通过在xml文件中设置 lazyLoadingEnabled=true|false。其实现原理是通过CGLIB创建代理对象，调用目标方法，当获取数据时调用对象的get方法时发现对象为空，此时会去发送一条该关联对象的SQL语句并执行，查询出结果并将结果映射到对象中，然后该对象就有值了，再次调用get方法即可获取到值。

### 8.mybatis中的缓存

​     主要有两级缓存，一级缓存为本地sqlsession级别缓存，且默认是开启的，当调用session的提交或者close方法后会将缓存中的信息清空。而二级缓存是mapper缓存，默认不开启可以通过xml文件的方式进行开启。

​    其中一级缓存是sqlsession级别的，如果在同一个sqlsession中执行了两次相同的SQL查询，第二次及以后都会从缓存中获取，但是如果sqlsession提交或者关闭了，缓存中的内容会被清除，下一次查询同样会从数据库中进行查询。或者中间执行了增删改操作都会导致缓存中的内容被清空。

   二级缓存默认不开启，可以在mybatis中通过setting进行开启，<settings name="cacheEnabled" value="true"/>`开启二级缓存总开关，然后在某个具体的mapper.xml中增加`<cache />即可。

在MyBatis中，缓存机制是用于提高数据访问性能的重要特性。MyBatis提供了两级缓存：一级缓存（本地缓存）和二级缓存（全局缓存）。当执行查询操作时，MyBatis会按照以下顺序寻找缓存：

1. **一级缓存（Local Cache）**：
   - MyBatis的一级缓存是基于会话的，每个SqlSession对象都有自己的缓存。
   - 当执行查询操作时，MyBatis首先检查当前SqlSession的一级缓存中是否已经存在该查询结果。
   - 如果存在，直接返回缓存中的数据，不再执行数据库查询。
   - 如果不存在，执行数据库查询并将结果存储在一级缓存中，然后返回结果。

2. **二级缓存（Second Level Cache）**：
   - 如果一级缓存中没有找到数据，MyBatis会检查二级缓存。
   - 二级缓存是基于Mapper的，即同一个Mapper的所有SqlSession共享一个二级缓存。
   - 如果二级缓存中存在数据，则返回缓存中的数据，不再执行数据库查询。
   - 如果二级缓存中也不存在数据，则执行数据库查询，并将结果存储在一级缓存和二级缓存中。

3. **数据库查询**：
   - 如果一级和二级缓存中都没有找到数据，MyBatis将执行数据库查询，并将查询结果存储在一级缓存中。
   - 如果启用了二级缓存，查询结果还会被存储在二级缓存中。

4. **缓存刷新**：
   - 当执行了增删改操作（INSERT、UPDATE、DELETE）时，MyBatis会清除一级缓存。
   - 如果配置了二级缓存，这些操作也会清除二级缓存中对应的数据，以保证数据的一致性。

5. **缓存配置**：
   - MyBatis的缓存可以通过配置文件或注解来启用和配置。
   - 通过`<cache>`标签可以配置二级缓存的属性，如缓存大小、过期时间等。
   - 通过`@CacheNamespace`注解也可以实现类似的功能。

在实际应用中，根据业务需求和数据一致性要求，可以灵活配置和使用MyBatis的缓存机制。例如，对于读多写少的场景，可以启用二级缓存以提高性能；对于需要实时更新的数据，可能需要禁用缓存或谨慎使用。

###     9.mybatis的接口绑定：

​    mybatis的接口绑定其实就是将任意接口方法和sql语句进行绑定，调用接口方法会定位到对应的sql语句，执行SQL并返回结果值。一般有两种方式进行绑定，其一通过注解的形式，在方法上通过@select，@update等，其二是通过xml文件进行绑定，通过namespace +方法名进行定为。

### 10.mybatis搭建

  1）创建mapper.xml文件，编写SQL语句的输入和输出；

  2）在mybatis全局配置文件中，配置数据源，配置每一个mapper文件；

  3）通过SqlSessionFactoryBuilder.build( Resources.getResourceAsStream("mybatis-config.xml")来创建sqlsessionfactory对象，在根据sqlsessionfactory来创建sqlsession对象；

  4）调用sqlsession.getmapper()获取对应的mapper接口，调用mapper接口的方法来执行SQL语句并将结果返回给对应的实体类;

  5）最后关闭连接，释放资源。

### 11.mybatis-config中常见的配置

      <!-- 配置顺序如下
     properties
     
     settings
    
     typeAliases
    
     typeHandlers
    
     objectFactory
    
     plugins
    
     environments
        environment
            transactionManager
            dataSource
    
     mappers
     -->
其中properties用来加载外部的一些配置文件，比如数据库连接配置文件，${}来进行占位符，<serrings>用来进行一些mybatis的常见配置比如是否开启延迟加载<setting name="lazyLoadingEnabled" value="true"/>，是否开启二级缓存<settings name="cacheEnabled" value="true"/>等；typeAliases起别名，在mapper.xml文件中如果引用一些对象，必须要写上全路径名称，如果我们在配置文件中对其其别名那么在使用是就可以直接使用别名。

  plugins插件配置，如果我们在mybatis中使用mybatis插件可以在plugins中进行配置，或者我们自定义插件时，首先自定义类实现intercepter接口，然后在plugins中进行引用配置。

````xml
<!-- PageHelper 分页插件 -->
<plugins>
  <plugin interceptor="com.github.pagehelper.PageInterceptor">
     <property name="helperDialect" value="mysql"/>
  </plugin>
</plugins>
````

   environment配置数据源信息，通过刚才加载进来的配置文件，使用${}来进行占位符的方式。

  mappers：用来配置mapper.xml文件的位置。

### 12.mybatis中如何获取返回的主键id

​    对于数据库支持主键自增的情况如mysql可以使用mybatis提供的属性：

​    通过在insert标签中使用useGeneratedKeys="true" keyProperty="id"的方式进行配置，这样在出入完成后会将主键id封装到对应的java对象中，后续通过对象.getid的方式进行获取。

````xml
<insert id="insert" parameterType="com.yogurt.po.Student" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO student (name,score,age,gender) VALUES (#{name},#{score},#{age},#{gender});
</insert>
````

  如果想oracle这种不支持主键自增的方式，可以使用标签<selectKey>并设置order为before即可，在执行时会先执行selectKey 去查询主键id，然后执行<insert>插入语句。

````xml
<insert id="insertMateriel" parameterType="...entity.TLampMateriel">
    <selectKey  keyProperty="tLampMateriel.id" resultType="int" order="BEFORE">
       select SEQ_USER_ID.nextval from dual
    </selectKey>
    insert into T_LAMP_MATERIEL (ID,NAME) values (#{tLampMateriel.id}, #{tLampMateriel.name})
</insert>
````

**当然在selectKey中不一定非得去序列中获取，我们也可以自定义比如从当前表获取最大id进行+1操作等。**

````xml
<insert id="addLoginLog" parameterType="map" >
    <selectKey  keyProperty="id" resultType="int" order="BEFORE">
      select nvl(max(id),0)+1 from ap_loginlog
    </selectKey>
    insert into ap_loginlog(ID,MEMBER_ID) values(#{id},#{memberId})
</insert>
````

### 13.mybatis中的批量操作

   主要包括批量查询，批量删除，批量插入等。 <foreach collection="list" item="id" open="(" separator="," close=")">其中collection中可以是list、set、map、array等，item是参数别名，open以XXX开头，close以XXX结尾，separator是以XXX作为分隔符。

````xml
<select id="batchFind" resultType="student" parameterType="java.util.List">
        SELECT * FROM student
        <where>
            <if test="list != null and list.size() > 0">
                AND id in
                <foreach collection="list" item="id" open="(" separator="," close=")">
                    #{id}
                </foreach>
            </if>
        </where>
</select>
````

### 14.mybatis中的分页插件

  原理：拦截器的方式对执行的SQL进行改写，添加了数据库分页的参数。通过调用pageHeleper.startPage(1,3)时，首先对page参数进行封装，然后底层调用了LOCAL_PAGE.set(page) ，将参数传递进去，然后基于不同的数据库实现，对SQL语句进行改写，拼接分页查询语句。

  1）引入分页插件依赖：pagehelper

  2）在mybatis的配置文件的plugins中引入pagehelper插件：

````xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <properties resource="properties/xx.properties"></properties>

    <plugins>
        <plugin interceptor="com.github.pagehelper.PageInterceptor">
            <property name="helperDialect" value="mysql"/>
        </plugin>
    </plugins>

</configuration>
````

  3）通过mapper调用方法前进行封装分页参数

````java
@Test
public void test() {
	SqlSession sqlSession = sqlSessionFactory.openSession();
	ProductMapper mapper = sqlSession.getMapper(ProductMapper.class);
	PageHelper.startPage(1,3);
	List<Product> products = mapper.selectByExample(new ProductExample());
	products.forEach(System.out::println);
}
````

## 19.mybatisplus

   mybatisPlus是在mybatis的基础上做了加强的工具，通过封装了一些借口方法，使用过程中可以直接调用接口方法，可以简化大量的基本SQL语句的编写。

### 1.mybatisplus的基本使用

  1）初始化项目，引入相关依赖：mybatis-plus-boot-starter，以及使用数据库连接相关依赖，lombok等；

   2）创建数据库表对应的实体类；

   3）创建mapper，继承自BaseMapper；

   4）创建启动类，并在启动类上添加mapperScan注解。

   5）编写测试类，通过调用basemapper.XXX()方法进行调用即可。

### 2.核心功能（常见注解）

  常见的有8个注解，可以作用在java实体类上。

  1）@tableName：作用于实体类上，指定实体类和数据库表的映射关系，将实体类转换为小写和数据库表明相同，如果实体类和数据库表明不同时可以通过该注解指定，一般不需要使用该注解。

  2）@tableId：作用在实体类的某一个字段上，表明该字段对应数据库中的主键id，如果实体类的字段和数据库表的主键id字段一样可以不用显示的指定，如果不一样时可以通过value进行指定数据库中的那个主键id的字段。type可以指定主键策略。主要有AUTO：数据库自增，需要依赖于数据库，在插入时不会插入这一列；NONE未设置主键类型，如果没有设置会根据全局主键策略（默认是基于雪花算法的自增id）；INPUT：需要手动指定，如果不指定插入值为null；UUID：根据UUID规则生成；ID_WORKER（分布式全局唯一id，整型）或者ID_WORKER_STR（分布式全局唯一id，字符串类型）。

  3）@tableFiled：作用在实体类的字段上，用于指定实体类属性和数据库字段的映射关系，如果字段需要进行填充通过fill属性指定即可，常见的值有：DEFAULT默认不进行填充，insert插入时进行填充，update更新时进行填充，insert_update插入和更新时进行填充。

  4）@version：乐观锁注解，可以直接作用在某一个属性字段上，需要引入乐观锁插件。

  5）@TableLogic：用于表示字段逻辑处理注释（逻辑删除），需要引入插件。

  6）@orderby：内置SQL指定排序，优先级低于wrapper对象。

### 3.常见的CRUD接口

  主要包括mapper的crud接口和service的crud接口。略。

### 4.条件构造器（重点）

  常见的条件构造器queryWrapper用于select查询时候进行拼接的构造器，UpdateWrapper用于update语句是拼接的条件。

### 5.分页查询

  1）首先引入mybatis-plus的分页插件；

  2）在业务层，通过new page()构造分页对象，然后调用mapper提供的接口方法传入page对象和条件构造器即可。

````java
Page<User> page = new Page<>(3, 2);
// 执行分页查询
Page<User> userPage = userMapper.selectPage(page, wrapper);
````

## 20.java web阶段

###  1.什么是servlet容器？

   它是java web 运行时环境，负责管理servlet和jsp。

   servlet：运行在web服务器端的一小段java代码，可接受来自客户端的请求并进行响应，一般通过http请求。

### 2.servlet的生命周期？

  1）初始化：只执行一次，且默认情况下是第一次请求到达时创建servlet对象，如果需要在启动服务时进行创建，可以配置load-on-startup参数。

 2）服务service：每次请求都会进行调用；

 3）销毁：当服务器重启或者关闭时，释放servlet锁占用的资源。

### 3.jsp？

​     jsp即java server pages,即就是页面，其本质是servlet，其执行原理首先将jsp生成servlet.java源文件，然后进行编译成class文件，执行时调用service方法。相较于servlet它更加擅长于前端页面的渲染等。

### 4.jsp的4大域对象和9大内置对象

  4大域对象：page，request，session，application；

  9大内置对象：request，response，out，session，application，page，pagecontext，config等。

### 5.请求转发和重定向的区别？

### 6.AJax

  它是异步的js和xml，其作用是在页面不发生跳转的情况下实现局部刷新功能，可以简单的理解为通过js向服务器发送请求。

## 21.登录功能

  详见登录功能.md

###   1.单系统登录

​     单体应用中的登录功能，采用了session和cookie会话机制。即当客户端发送请求时，服务器端接收请求，并进行登录校验，验证通过后生成一个会话id，存储在session中，然后将会话id作为参数传递给浏览器，浏览器拿到会话id保存在本地cookie中，下一次请求时带着会话id，发送给服务器，如果服务器从session中能够获取到会话id则说明已经登录，可以进行操作；否则说明未登录引导跳转至登录页面。

  其中对于cookie包括浏览器客户端和服务器端，客户端通过document.cookie进行创建，而服务器端可以通过cookie c = new cookie()的方式进行创建，发送cookie，response.addCookie();获取cookie，request.getCookie()。也可以设置cookie的最大过期时间。

###   2.单点登录

​     就是在多系统的应用群中，如果其中的某一个系统做了登录操作，那么其他系统中得到授权的应该不需要再次进行登录操作，而是直接可以使用相关功能。 常见的实现方式有3种：

  1）基于session广播机制：即也就是session复制，如果某一个系统中登录成功，将该session复制到其他系统中，这样效率较慢。

  2）基于cookie+redis的方式：和单系统类似，只是在多系统中将会话id保存在redis中，这样多系统之间也就可以进行访问。

  3）tooken令牌：最常见的方式，即也就是通过sso认证中心，只有在认证中心才能够进行用户名密码等安全操作，其他系统需要从安全认证中心获取授权。间接授权通过令牌实现，sso认证中心验证用户的用户名密码没问题，创建授权令牌，在接下来的跳转过程中，授权令牌作为参数发送给各个子系统，子系统拿到令牌，即得到了授权，可以借此创建局部会话，局部会话登录方式与单系统的登录方式相同。

   Tooken令牌实现过程：

​     原理：首先包括认证中心和子系统，子系统需要与认证中心进行令牌交换，校验以及发起注销请求，而认证中心负责登陆，注销认证，以及接受个子系统发送的认证和注销请求。主要包括客户端和服务器端。

  客户端：拦截未登录的请求，引导跳转至认证中心；接受认证中心的tooken令牌，并创建局部会话;拦截子系统的注销请求，并跳转至认证中心；接受认证中心的注销请求，销毁局部会话。

  服务器端：接收sso-client的认证请求，校验用户是否登录，创建全局会话，创建授权令牌

​                      与sso-client发送令牌通信，校验令牌的有效性，进行系统注册

​                      接收sso-client的注销请求，销毁全局会话。

  
