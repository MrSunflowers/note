# plsql编程基础

## 基础运行环境及命令

```plsql
declare
	--变量在此处声明 没有变量 declare 可以省略
begin
	--测试代码写在这里，逻辑执行部分（方法体）
end;
```

plsql编程不区分大小写

		plsql程序除了运行在PLSQL Developer 的测试窗口中，还可以运行在命令行的sqlplus中，用户登录后，直接编写代码，代码编写结束后以  **/**  结尾，回车表示过程代码编写完毕。

```sql
SQL> set serveroutput on
SQL> begin 
	 dbms_output.put_line('hello world!');
	 end;
	 /
```

		在oracle sqlplus客户端默认的输出选项是被关闭的，此时要通过使用set  serveroutput  on  命令设置环境变量serveroutput为打开状态，从而使得pl/sql程序能够在SQL*plus中输出结果。
	
		**set serveroutput on命令是不可以放在存储过程中执行的.**
	
		如果你怕忘记打开或者嫌麻烦，可以把 set serveroutput on 写在
		D:\oracle\product\10.2.0\db_1\sqlplus\admin\glogin.sql 中，因为sqlplus打开的时候会自动加载这个文件。

| 常用指令            | 作用                                             |
| ------------------- | ------------------------------------------------ |
| set colsep '\|'     | 输出分隔符                                       |
| SET TIMING ON       | 显示SQL语句的运行时间。默认值为OFF。             |
| SET AUTOTRACE ON    | 设置允许对执行的sql进行分析                      |
| set echo off        | 显示start启动的脚本中的每个sql命令，缺省为on     |
| set echo on         | 设置运行命令是是否显示语句                       |
| set feedback on     | 设置显示“已选择XX行”                             |
| set feedback off    | 回显本次sql命令处理的记录条数，缺省为on          |
| set heading off     | 输出域标题，缺省为on                             |
| set pagesize 0      | 输出每页行数，缺省为24,为了避免分页，可设定为0   |
| set linesize 80     | 输出一行字符个数，缺省为80                       |
| set numwidth 12     | 输出number类型域长度，缺省为10                   |
| set termout off     | 显示脚本中的命令的执行结果，缺省为on             |
| set trimout on      | 去除标准输出每行的拖尾空格，缺省为off            |
| set trimspool on    | 去除重定向（spool）输出每行的拖尾空格，缺省为off |
| set serveroutput on | 设置允许显示输出类似dbms_output                  |



## 变量declare

### 普通变量

#### 变量的声明

```sql
-- 变量名 变量类型（变量长度）
value_name varchar2(20);
```

#### 数据类型

普通的数据库数据类型。

| 数据类型                | 参数                    | 描述                                                         |
| ----------------------- | ----------------------- | ------------------------------------------------------------ |
| char(n)                 | n：1~2000               | 定长字符串，n字节长，如果不指定长度，缺省为1个字节长（一个汉字为2字节） |
| varchar2(n)             | n：1~4000               | 可变长的字符串，具体定义时指明最大长度n，<br/>这种数据类型可以放数字、字母以及ASCII码字符集(或者EBCDIC等数据库系统接受的字符集标准)中的所有符号。<br/>如果数据长度没有达到最大值n，Oracle 8i会根据数据大小自动调节字段长度，<br/>如果你的数据前后有空格，Oracle 8i会自动将其删去。VARCHAR2是最常用的数据类型。<br/>可做索引的最大长度3209。 |
| number(m,n)             | m：1~38 <br/>n：-84~127 | 可变长的数值列，允许0、正值及负值，m是所有有效数字的位数，n是小数点以后的位数。<br/>如：number(5,2)，则这个字段的最大值是99,999，如果数值超出了位数限制就会被截取多余的位数。<br/>如：number(5,2)，但在一行数据中的这个字段输入575.316，则真正保存到字段中的数值是575.32。<br/>如：number(3,0)，输入575.316，真正保存的数据是575。 |
| date                    | 无                      | 从公元前4712年1月1日到公元4712年12月31日的所有合法日期，<br/>Oracle 8i其实在内部是按7个字节来保存日期数据，在定义中还包括小时、分、秒。<br/>缺省格式为DD-MON-YY，如07-11月-00 表示2000年11月7日。 |
| long                    | 无                      | 可变长字符列，最大长度限制是2GB，用于不需要作字符串搜索的长串数据，如果要进行字符搜索就要用varchar2类型。<br/>long是一种较老的数据类型，将来会逐渐被BLOB、CLOB、NCLOB等大的对象数据类型所取代。 |
| raw(n)                  | n：1~2000               | 可变长二进制数据，在具体定义字段的时候**必须**指明最大长度n，Oracle 8i用这种格式来保存较小的图形文件或带格式的文本文件，如Miceosoft Word文档。<br/>raw是一种较老的数据类型，将来会逐渐被BLOB、CLOB、NCLOB等大的对象数据类型所取代。 |
| long raw                | 无                      | 可变长二进制数据，最大长度是2GB。Oracle 8i用这种格式来保存较大的图形文件或带格式的文本文件，如Miceosoft Word文档，以及音频、视频等非文本文件。<br/>**在同一张表中不能同时有long类型和long raw类型**，long raw也是一种较老的数据类型，将来会逐渐被BLOB、CLOB、NCLOB等大的对象数据类型所取代。 |
| blob<br/>clob<br/>nclob | 无                      | 三种大型对象(LOB)，用来保存较大的图形文件或带格式的文本文件，如Miceosoft Word文档，以及音频、视频等非文本文件，最大长度是4GB。<br/>LOB有几种类型，取决于你使用的字节的类型，Oracle 8i实实在在地将这些数据存储在数据库内部保存。<br/>可以执行读取、存储、写入等特殊操作。<br/>BLOB : <br/>全称为二进制大型对象（Binary Large Object)。它用于存储数据库中的大型二进制对象。可存储的最大大小为4G字节<br/>CLOB : <br/>CLOB全称为字符大型对象（Character Large Object)。它与LONG数据类型类似，只不过CLOB用于存储数据库中的大型单字节**字符**数据块，不支持宽度不等的字符集。可存储的最大大小为4G字节<br/>NCLOB :  <br/>基于国家语言字符集的NCLOB数据类型用于存储数据库中的固定宽度单字节或多字节字符的大型数据块，不支持宽度不等的字符集。可存储的最大大小为4G字节 |
| bfile                   | 无                      | 在数据库外部保存的大型二进制对象文件，最大长度是4GB。<br/>这种外部的LOB类型，通过数据库记录变化情况，但是数据的具体保存是在数据库外部进行的。<br/>Oracle 8i可以读取、查询BFILE，但是不能写入。<br/>大小由操作系统决定。<br/>BFILE 当大型二进制对象的大小大于4G字节时，BFILE数据类型用于将其存储在数据库外的操作系统文件中；当其大小不足4G字节时，则将其存储在数据库内部的操作系统文件中，BFILE列存储文件定位程序，此定位程序指向服务器上的大型二进制文件。 |



#### 赋值方式

1. 直接赋值语句  :=  （例如 v_name := '张三'）
2. 语句赋值，使用select ... info ... 赋值 （语法：select 值 into 变量）

【示例】

```sql
declare
v_name varchar2(20) := '123'; -- 声明变量时直接赋值
begin
-- 直接赋值
v_name := 'zhangsan';
-- 语句赋值
select 'abc' into v_name from dual;
select d.dealno into v_name from db_info where d.dealno = '1876765';
end;
```



### 特殊变量

#### 引用型变量

`表名.列名%TYPE` 变量类型和长度取决于表中字段类型和长度

##### 变量的声明

通过 `表名.列名%TYPE` 声明变量的类型和长度。

【示例】

```sql
declare
	v_dealno dbinfo.dealno%type;
begin

end；
```

##### 赋值方式

```sql
declare
	v_name sys_user.username%type := '张三'; -- 声明时直接赋值
	v_age sys_user.age%type;
begin
	select username,age into v_name,v_age from sys_user where username = '张三'; -- 语句赋值
	dbms_output.put_line('姓名' || v_name || ' 年龄 ' || age);
end;
```

#### 记录型变量

用于接受返回的**一整条**记录的值

##### 变量声明

语法：`变量名  表名%rowtype`

【示例】

```sql
declare
	v_row dbinfo%rowtype; -- 记录型变量
begin

end；
```

##### 赋值方式

```sql
declare
	v_row dbinfo%rowtype;
begin
	select * into v_row where dealno = '1873838';
	dbms_output.put_line(v_row.dealno); -- 打印此行中的dealno字段的值
end；
```

#### 游标

用于临时存储查询返回的多行数据，通过遍历游标可以访问被查询的数据。

【使用】

1. 声明

   `cursor 游标名[(参数列表)] is 查询语句`

2. 打开

   `open 游标名`

3. 读取

   `fetch 游标名 into 变量列表`

4. 关闭

   `close 游标名`

##### 游标的属性

| 游标属性  | 返回值类型 | 说明                                              |
| --------- | ---------- | ------------------------------------------------- |
| %rowcount | 整型       | 获得fetch语句返回的数据行数                       |
| %found    | 布尔型     | **最近的fetch语句返回数据不为空则为真**，否则为假 |
| %notfound | 布尔型     | 与%found相反，**默认为有值**                      |
| %isopen   | 布尔型     | 游标是否打开                                      |

其中%notfound是在游标中找不到元素的时候返回true，通常用来退出循环。

##### 使用示例

固定游标使用示例

```sql
declare
	-- 声明游标
	cursor c_dbinfo is
		select dealno,trxref from db_info;
	-- 创建变量来接收游标中的数据
	v_dealon db_info.dealno%type;
	v_trxref db_info.trxref%type;
begin
	open c_dbinfo; -- 打开游标，游标只有打开时才可以使用
	loop 
		fetch c_dbinfo into v_dealno,v_trxref; -- 去信息到变量中
	exit when c_dbinfo%notfound; -- 没有数据时退出循环
		dbms_output.put_line(v_dealno); -- 取出的数据
	end loop; -- 结束循环
	close c_dbinfo; -- 关闭游标
end;
```

参数游标使用示例
```sql
declare
	-- 声明游标
	cursor c_dbinfo(v_event_no db_info.event_no%type) is
		select dealno,trxref from db_info where event_no = v_event_no;
	-- 创建变量来接收游标中的数据，传递参数进入作为条件参数
	v_dealon db_info.dealno%type;
	v_trxref db_info.trxref%type;
begin
	open c_dbinfo('0003'); -- 打开游标，游标只有打开时才可以使用,传入条件参数
	loop 
		fetch c_dbinfo into v_dealno,v_trxref; -- 去信息到变量中
	exit when c_dbinfo%notfound; -- 没有数据时退出循环
		dbms_output.put_line(v_dealno); -- 取出的数据
	end loop; -- 结束循环
	close c_dbinfo; -- 关闭游标
end;
```

**【注意】%notfound默认为false，也就是默认为有值，实际上有没有值需要经过fetch指令之后才会覆盖到%notfound中。**


## 流程控制

### 条件分支

语法

```sql
begin
	if 条件1 then 执行1
		elsif 条件2 then 执行2
		else 执行3
	end if；
end；
```

【注意】**elsif**  关键子

【示例】

```sql
declare
	v_count number;
begin
	select count(*) into v_count from db_info;
	if v_count >= 0
		then dbms_output.put_line(v_count);
	end if;
end；
```

### 循环

在oracle中有三种循环：

for循环

```sql
declare
begin
   for i in 2..10 Loop
     dbms_output.put_line('结果i是:'||i);
  end loop;
end;
```

while的循环

```sql
declare
x number;
begin
 x := 5 ;
 while x>0 loop
   x := x-1; -- 循环的每次处理
 if x = 3 then
   return; -- 跳出循环
 else
    dbms_output.put_line('x的值是'||x);
   end if;
 end loop; 
end;
```

loop循环

```sql
declare
x number;
begin
  x:=0;
  loop
    x := x+1;
    exit when x >5 ; -- x大于5是终止
     dbms_output.put_line('结果x是two：'||x);
  end loop;
  end;
```

## 存储过程

### 语法

```sql
create or replace procedure 过程名称[(参数列表)] is -- (is可以换成as)
-- 在此处声明变量，不需要declare
begin

end [过程名称]；
```

依据参数类型，将存储过程分为3类：

1. 不带参数的
2. 带参数的
3. 带参数和返回值的

当在执行存储过程时，plsql是将其存储起来，并不是运行它，要运行它需要在test窗口调用即可。

```sql
begin
test;
end;
```

也可以在plsql窗口调用

```sql
SQL> exec test;
```

【示例】

无参数存储过程

```sql
create or replace procedure test is -- (is可以换成as)
-- 在此处声明变量，不需要declare
	x number := 100;
begin
	dbms_output.put_line(x);
end test；
```

有参数的存储过程

```sql
create or replace procedure test(v_in in number, v_out out number) is -- in 表示输入参数 out表示返回参数
-- 在此处声明变量，不需要declare
	x number := 100;
begin
	dbms_output.put_line(v_in);
	v_out := x;
end test；
-- 调用
test(20,x);
```

### 使用Java程序调用存储过程

```sql
create or replace procedure queryempinfo(eno in number,pename out varchar2,psal out number,pjob out varchar2) as
begin
--得到该员工的姓名 月薪和职位
select ename, sal, job into pename, psal, pjob from emp where empno = eno;
end queryempinfo;
```



```java
package com.tomhu.procedure;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.jdbc.OracleTypes;

public class Procedure {

    private Connection conn;
    private CallableStatement stat;
    private ResultSet rs;

    String url = "jdbc:oracle:thin:@127.0.0.1:1521:orcl";
    String driverName = "oracle.jdbc.driver.OracleDriver";
    String username = "scott";
    String password = "******";
    String sql = "call queryempinfo(?,?,?,?)";

    // 调用存储过程
    public void callProcedure() {
        try {
            Class.forName(driverName);
            conn = DriverManager.getConnection(url, username, password);
            stat = conn.prepareCall(sql);

            // 一个输入参数和三个输出参数
            stat.setInt(1, 7566);
            stat.registerOutParameter(2, OracleTypes.VARCHAR);
            stat.registerOutParameter(3, OracleTypes.NUMBER);
            stat.registerOutParameter(4, OracleTypes.VARCHAR);
            stat.execute();

            String name = stat.getString(2);
            int sal = stat.getInt(3);
            String job = stat.getString(4);

            System.out.println("name: " + name + ", sal: " + sal + ", job: " + job);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(conn, stat, rs);
        }
    }

    // 关闭连接
    public void close(Connection conn, CallableStatement stat, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                rs = null;
            }
        }
        if (stat != null) {
            try {
                stat.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                stat = null;
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                conn = null;
            }
        }
    }

    public static void main(String[] args) {
        new Procedure().callProcedure();
    }
}
```

结果

```
name: JONES, sal: 2975, job: MANAGER
```

## 触发器

		触发器是与一个表相关联的，当一个特定的数据操作（insert，update，delete）在指定表上发出时，Oracle会自动执行触发器。
	
		触发器分为：

- 语句级触发器：

  		在指定的操作语句操作之前或之后执行一次，不管这条操作影响了多少行数据，触发器只执行一次。

- 行级触发器

  		触发语句作用的每一条记录都会被触发，在行级触发器中使用old和new伪记录变量，来识别值的状态。

### 触发器权限

数据库创建用户时想要在本用户下使用触发器，需要给用户触发器的权限

使用DBA用户执行 `GRANT CREATE TRIGGER TO user_name;`

如果想在当前用户下创建其他用户的触发器需要具有`CREATE ANY TRIGGER`的权限

如果要创建的触发器作用在数据库上的比如对start或者shutdown事件触发，则需要具有`ADMINISTER DATABASE TRIGGER`系统权限。

### 语法

1.基于DML操作的触发器

		这类触发器是在当用户对一个表进行insert delete update 操作时触发行为的，在对表进行触发行为的时候使用for each row激发表中涉及的每行数据。

该类触发器语法：

```sql
CREATE [OR REPLACE] TRIGGER trigger_name

BEFORE/AFTER INSERT[DELETE UPDATE ]

ON table_name

FOR EACH ROW[WHEN cond]  -- WHEN cond 是行级限制条件 【示例】：When(old.name in ‘hello world’)当oldname是 hello world是执行触发条件

declare

begin

end 触发器名；
```

		创建基于DML的触发器时，由于操作对象是表，所以有一个可选项即 `for each row` 以实现对每一行都激发触发器行为，Oracle提供2个临时表来访问每行中的新值和旧值即  :new 和 :old。

| 触发语句 | :old             | :new             |
| -------- | ---------------- | ---------------- |
| insert   | 所有字段都是null | 将要插入的数据   |
| update   | 更新前该行的值   | 更新后的值       |
| delete   | 删除以前该行的值 | 所有字段都是null |

【示例】

```sql
CREATE OR REPLACE TRIGGER update_trigger

AFTER UPDATE ON table_name

FOR EACH ROW

  BEGIN

    dbms_output.put_line('旧值：='||:OLD.CHARS);

    dbms_output.put_line('新值：='||:NEW.CHARS);

  END;
```

审核触发器的创建

		顾名思义，就是当用户操作一个重要的表时，如插入数据和更新数据，希望记录该用户的用户名和更改时间等信息，以备审核时用。创建审核触发器前，我们需要创建一个表，记录审核信息

【示例】

```sql
-- 创建审核表

CREATE TABLE user_modify

(

user_name VARCHAR2(20),

modify_time DATE,

modify_content VARCHAR2(20)

);

-- 创建触发器

CREATE OR REPLACE TRIGGER user_change

BEFORE UPDATE OR INSERT ON emp

FOR EACH ROW

 BEGIN

  IF inserting THEN 

   INSERT INTO user_modify

   VALUES(USER,SYSDATE,'updating');

  END IF;

 END;
```

删除触发器的创建

该类触发器的主要作用就是当在进行删除操作的时候，把删除的记录记录到另一张备份表中。直接实例

```sql
-- 创建删除备份表

CREATE TABLE delete_back

(

back_id VARCHAR2(20),

back_id2 VARCHAR2(20),

back_id3 VARCHAR2(20)

);

-- 创建触发器

CREATE OR REPLACE TRIGGER delete_back

BEFORE DELETE ON emp

FOR EACH ROW

 BEGIN

  INSERT INTO delete_back VALUES(old.back_id,old.back_id2,old.back_id3);

 END;
```

当用户操作emp表进行删除操作，激发触发器往delete_back表插入删除的数据明细

2.基于DDL操作的触发器，语法

		此类触发器的典型应用，当创建修改删除数据库表的时候在之前或者之后记录该用户的操作信息，以作为用户操作日志。

```sql
CREATE [OR REPLACE] TRIGGER trigger_name

BEFORE/AFTER CREATE[DROP ALTER]--创建，删除修改

ON database_name [WHEN cond]

declare

begin

end 触发器名；
```

3.基于数据库级操作的触发器

```sql
CREATE [OR REPLACE] TRIGGER trigger_name

BEFORE/AFTER START[SHUTDOWM,LOGON,LOGOFF]--数据库、日志启动关闭

ON database_name [WHEN cond]

declare

begin

end 触发器名；
```



### 触发器基本管理操作

1.重新编译触发器

 `alter trigger trigger_name complie`

2.屏蔽触发器(不删除使其无效)

 `alter trigger trigger_name disable`

3.删除触发器

 `drop trigger trigger_name`

### 应用

完成数据库主键自动自增可以使用**触发器**和**序列**来实现

## 异常处理

### 语法

```sql
declare 

begin

exception 

when NO_DATA_FOUND then -- （异常名称）

出现异常后的处理；

end；
```

### 异常类型

#### 预定义异常**(总计21种，具体见文档)**

常用类型：

| 异常名称      | 异常代码  | 说明                            |
| ------------- | --------- | ------------------------------- |
| NO_DATA_FOUND | ORA-01403 | 未找到行                        |
| TOO_MANY_ROWS | ORA-01422 | 语句返回多行数据                |
| VALUE_ERROR   | ORA-06502 | 类型转换错误                    |
| ZERO_DIVIDE   | ORA-01476 | 程序尝试除以 0                  |
| STORAGE_ERROR | ORA-06500 | PL/SQL 运行时内存溢出或内存不足 |

#### 非预定义异常(EXCEPTION_INIT )

非预定义异常需要在declare中申明，申明后使用即与预定义异常相同。

【申明方法一】

```sql
declare
      e_deptno_remaining exception ;
      PRAGMA EXCEPTION_INIT (e_deptno_remaining, - 2292 );
begin
    ...
exception
when e_deptno_remaining then
    dbms_output.put_line( ' 非预定义2292 ' );
when others then
    dbms_output.put_line( 'others' );
end ;
```

		-2292 必须是 oracle 自定义的错误号，前面加负号如果需要自己设定，则必须在-20000 —— -20999之间，此方法无法定义异常信息。

【申明方法二】

错误号与错误信息均可自己定义且无需在declare和exception中声明

```sql
declare
      i int := 5;
begin
     if i = 5 then
     raise_application_error (-20086 /*-20000——-20999*/ , '自定义错误信息');
     endif;
end;
```

#### 自定义异常

自定义异常分为declare、raise、exception三部分

【示例】

```sql
declare
      i int := 3;
      ex exception;
begin
      if i <=2then
      raise ex; -- 注册异常
      else dbms_output.put_line(i);                             
      endif;
exception
when ex then
      dbms_output.put_line('xxx');
end;
```

#### Others的异常类

在EXCEPTION中定义任何的异常后尽量都使用when others表示遭遇到除此之外的任何异常如何处理

【示例】

```sql
EXCEPTION
      WHEN exception_name1 THEN   -- handler
        sequence_of_statements1
      WHEN exception_name2 THEN   -- another handler
        sequence_of_statements2
        ...
      WHEN OTHERS THEN            -- optional handler
        sequence_of_statements3
END; 
```

另外，在EXCEPTION中可以使用OR链接

```sql
WHEN over_limit OR under_limit OR VALUE_ERROR THEN
```





## 常用api

### 打印输出到控制台

```plsql
BEGIN
DBMS_OUTPUT.ENABLE (buffer_size => NULL);--设置 dbms_output 输出的缓冲。 不设置如果输出超过2000字节就不可以用了……
dbms_output.put('a'); --写入buffer但不输出
dbms_output.put('b'); --写入buffer但不输出 
--dbms_output.new_line; --换行后buffer里面的内容会输出 
dbms_output.put_line('hello world!'); --输出并换行，buffer里面的内容也会输出 
dbms_output.put('d'); --写入buffer但不输出
--dbms_output.new_line;
end;
```

		DBMS_OUTPUT包主要用于调试PL/SQL程序，或者在SQL*PLUS命令中显示信息(displaying message)和报表，譬如我们可以写一个简单的匿名PL/SQL程序块，而该块出于某种目的使用DBMS_OUTPUT包来显示一些信息。
		在该DBMS_OUTPUT包中存在2个存储过程，它们是**PUT_LINE**和**PUT**过程，使用这2个Procedure可以做到将信息存放到PL/SQL的Buffer中，以便其他的触发器、存储过程、程序包来读取。在独立的PL/SQL程序或匿名块中，我们还可以使用**GET_LINES**和**GET**这2个存储过程来将存放在PL/SQL Buffer中的信息输出(display)到屏幕。

| DBMS_OUTPUT 包子程序摘要 | 类型     | 作用                      |
| ------------------------ | -------- | ------------------------- |
| DISABLE                  | 存储过程 | 禁用消息输出              |
| ENABLE                   | 存储过程 | 启用消息输出              |
| GET_LINE                 | 存储过程 | 从buffer中获取单行信息    |
| GET_LINES                | 存储过程 | 从buffer中获取信息数组    |
| NEW_LINE                 | 存储过程 | 终结有PUT过程所创建的一行 |
| PUT                      | 存储过程 | 将一行信息放到buffer中    |
| PUT_LINE                 | 存储过程 | 将部分行信息放到buffer中  |

注意目前PUT过程已废弃，因为遗留问题将被保留，但不在推荐使用。
**DISABLE存储过程**
该存储过程用以禁用对PUT, PUT_LINE, NEW_LINE, GET_LINE, and GET_LINES过程的调用，并会清理buffer中任何残存信息。
与之相反的是ENABLE存储过程，若在SQL*PLUS中使用SERVEROUTPUT选项则不需要调用该存储过程。
语法
DBMS_OUTPUT.DISABLE;
编译器指示
pragma restrict_references(disable,WNDS,RNDS);
**ENABLE存储过程**
该存储过程用以启用对PUT, PUT_LINE, NEW_LINE, GET_LINE, and GET_LINES存储过程的调用。
语法
DBMS_OUTPUT.ENABLE (
buffer_size IN INTEGER DEFAULT 20000);
编译指示
pragma restrict_references(enable,WNDS,RNDS);