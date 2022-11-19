# MySQL

## SQL服务器

**RDBMS - 关系数据库管理系统（Relational Database Management System）**

关系数据库管理系统 (RDBMS) 也是一种数据库管理系统，其数据库是根据数据间的关系来组织和访问数据的。

20 世纪 70 年代初，IBM 公司发明了 RDBMS。

RDBMS 是 SQL 的基础，也是所有现代数据库系统诸如 Oracle、SQL Server、IBM DB2、Sybase、MySQL 以及 Microsoft Access 的基础。

## SQL

### 语法

```sql
SELECT column_name(s) FROM table_name -- 查询数据
SELECT DISTINCT column_name(s) FROM table_name -- 去重
SELECT column_name(s) FROM table_name WHERE condition -- 条件查询
SELECT column_name(s) FROM table_name ORDER BY column_name(s) (默认升序) -- 排序
INSERT INTO table_name VALUES (value1, value2,....) -- 插入数据
INSERT INTO table_name (column1, column2,...) VALUES (value1, value2,....) -- 插入数据
UPDATE table_name SET column_name(s) = 新value WHERE column_name(s) = 某value -- 更新数据
DELETE FROM table_name WHERE column_name(s) = value -- 删除数据
```

---

```SQL
SELECT column_name(s) FROM table_name LIMIT 数字 -- 查询前n条数据
SELECT column_name(s) FROM table_name WHERE column_name LIKE pattern -- 搜索column中的指定模式
SELECT column_name(s) FROM table_name WHERE column_name IN (value1,value2,...) -- 查询多个value
SELECT column_name(s) FROM table_name WHERE column_name BETWEEN value1 AND value2 -- 选取介于两个value之间的数据范围
SELECT column_name(s) FROM table_name AS alias_name -- 用于给表起别名
SELECT column_name(s) FROM table_name1 INNER JOIN table_name2 ON table_name1.column_name=table_name2.column_name -- 在表中存在至少一个匹配时，INNER JOIN 关键字返回行 <=>Join
SELECT column_name(s) FROM table_name1 LEFT JOIN table_name2 ON table_name1.column_name=table_name2.column_name -- LEFT JOIN 关键字会从左表 (table_name1) 那里返回所有的行，即使在右表 (table_name2) 中没有匹配的行
SELECT column_name(s) FROM table_name1 FULL JOIN table_name2 ON table_name1.column_name=table_name2.column_name -- 只要其中某个表存在匹配，FULL JOIN 关键字就会返回行
SELECT column_name(s) FROM table_name1 UNION SELECT column_name(s) FROM table_name2 -- UNION 操作符用于合并两个或多个 SELECT 语句的结果集
SELECT column_name(s) INTO new_table_name [IN externaldatabase] FROM old_tablename -- 从一个表中选取数据，然后把数据插入另一个表中
```

---

```sql
CREATE DATABASE database_name -- 创建数据库
CREATE TABLE table_name (column_name1 数据类型,column_name2 数据类型,column_name3 数据类型,....) -- 语句用于创建数据库中的表
ALTER TABLE table_name ADD column_name datatype -- 添加column
ALTER TABLE old_table_name RENAME TO new_table_name -- 更新表名
ALTER TABLE table_name MODIFY column_name　<数据类型> -- 修改列名
ALTER TABLE table_name CHANGE old_column_name new_column_name 新数据类型 -- 修改列名和数据类型
DROP COLUMN column_name -- 删除列
CREATE TABLE Persons(P_Id int NOT NULL AUTO_INCREMENT) -- Auto-increment 会在新记录插入表中时生成一个唯一的数字
CREATE VIEW view_name AS SELECT column_name(s) FROM table_name WHERE condition -- 视图是基于 SQL 语句的结果集的可视化的表
CREATE OR REPLACE VIEW view_name AS SELECT column_name(s) FROM table_name WHERE condition -- 更新视图
DROP VIEW view_name -- 删除视图

```

---

```sql
NOT NULL -- NOT NULL 约束强制列不接受 NULL 值
UNIQUE	-- UNIQUE 约束唯一标识数据库表中的每条记录
PRIMARY KEY -- PRIMARY KEY 约束唯一标识数据库表中的每条记录
FOREIGN KEY -- 一个表中的 FOREIGN KEY 指向另一个表中的 PRIMARY KEY
CHECK -- CHECK 约束用于限制列中的值的范围
DEFAULT -- DEFAULT 约束用于向列中插入默认值
```

### 数据类型

#### Text 类型

| 数据类型               | 描述                                                         |
| :--------------------- | :----------------------------------------------------------- |
| CHAR(*size*)           | 保存固定长度的字符串（可包含字母、数字以及特殊字符）。在括号中指定字符串的长度。最多 255 个字符。 |
| VARCHAR(*size*)        | 保存可变长度的字符串（可包含字母、数字以及特殊字符）。在括号中指定字符串的最大长度。最多 255 个字符。注释：如果值的长度大于 255，则被转换为 TEXT 类型。 |
| TINYTEXT               | 存放最大长度为 255 个字符的字符串。                          |
| TEXT                   | 存放最大长度为 65,535 个字符的字符串。                       |
| BLOB                   | 用于 BLOBs (Binary Large OBjects)。存放最多 65,535 字节的数据。 |
| MEDIUMTEXT             | 存放最大长度为 16,777,215 个字符的字符串。                   |
| MEDIUMBLOB             | 用于 BLOBs (Binary Large OBjects)。存放最多 16,777,215 字节的数据。 |
| LONGTEXT               | 存放最大长度为 4,294,967,295 个字符的字符串。                |
| LONGBLOB               | 用于 BLOBs (Binary Large OBjects)。存放最多 4,294,967,295 字节的数据。 |
| ENUM(*x*,*y*,*z*,etc.) | 允许你输入可能值的列表。可以在 ENUM 列表中列出最大 65535 个值。如果列表中不存在插入的值，则插入空值。注释：这些值是按照你输入的顺序存储的。可以按照此格式输入可能的值：ENUM('X','Y','Z') |
| SET                    | 与 ENUM 类似，SET 最多只能包含 64 个列表项，不过 SET 可存储一个以上的值。 |

#### Number 类型

| 数据类型            | 描述                                                         |
| :------------------ | :----------------------------------------------------------- |
| TINYINT(*size*)     | -128 到 127 常规。0 到 255 无符号*。在括号中规定最大位数。   |
| SMALLINT(*size*)    | -32768 到 32767 常规。0 到 65535 无符号*。在括号中规定最大位数。 |
| MEDIUMINT(*size*)   | -8388608 到 8388607 普通。0 to 16777215 无符号*。在括号中规定最大位数。 |
| INT(*size*)         | -2147483648 到 2147483647 常规。0 到 4294967295 无符号*。在括号中规定最大位数。 |
| BIGINT(*size*)      | -9223372036854775808 到 9223372036854775807 常规。0 到 18446744073709551615 无符号*。在括号中规定最大位数。 |
| FLOAT(*size*,*d*)   | 带有浮动小数点的小数字。在括号中规定最大位数。在 d 参数中规定小数点右侧的最大位数。 |
| DOUBLE(*size*,*d*)  | 带有浮动小数点的大数字。在括号中规定最大位数。在 d 参数中规定小数点右侧的最大位数。 |
| DECIMAL(*size*,*d*) | 作为字符串存储的 DOUBLE 类型，允许固定的小数点。             |

\* 这些整数类型拥有额外的选项 UNSIGNED。通常，整数可以是负数或正数。如果添加 UNSIGNED 属性，那么范围将从 0 开始，而不是某个负数。

#### Date 类型

| 数据类型    | 描述                                                         |
| :---------- | :----------------------------------------------------------- |
| DATE()      | 日期。格式：YYYY-MM-DD注释：支持的范围是从 '1000-01-01' 到 '9999-12-31' |
| DATETIME()  | *日期和时间的组合。格式：YYYY-MM-DD HH:MM:SS注释：支持的范围是从 '1000-01-01 00:00:00' 到 '9999-12-31 23:59:59' |
| TIMESTAMP() | *时间戳。TIMESTAMP 值使用 Unix 纪元('1970-01-01 00:00:00' UTC) 至今的描述来存储。格式：YYYY-MM-DD HH:MM:SS注释：支持的范围是从 '1970-01-01 00:00:01' UTC 到 '2038-01-09 03:14:07' UTC |
| TIME()      | 时间。格式：HH:MM:SS注释：支持的范围是从 '-838:59:59' 到 '838:59:59' |
| YEAR()      | 2 位或 4 位格式的年。注释：4 位格式所允许的值：1901 到 2155。2 位格式所允许的值：70 到 69，表示从 1970 到 2069。 |

\* 即便 DATETIME 和 TIMESTAMP 返回相同的格式，它们的工作方式很不同。在 INSERT 或 UPDATE 查询中，TIMESTAMP 自动把自身设置为当前的日期和时间。TIMESTAMP 也接受不同的格式，比如 YYYYMMDDHHMMSS、YYMMDDHHMMSS、YYYYMMDD 或 YYMMDD。



## 事务

是一组操作的集合,他是一个不可分割的工作单位,事务会把所有的操作作为一个整体一起向事务系统提交或撤销这些操作请求,即这些操作要不同时成功,要不同时失败.

### 事务四大特性

* 原子性:原子性是指事务是一个不可分割的工作单位，事务中的操作要么都发生，要么都不发生。
* 一致性:事务前后数据的完整性必须保持一致。
* 隔离性:事务的隔离性是多个用户并发访问数据库时，数据库为每一个用户开启的事务，不能被其他事务的操作数据所干扰，多个并发事务之间要相互隔离。
* 持久性:持久性是指一个事务一旦被提交，它对数据库中数据的改变就是永久性的，接下来即使数据库发生故障也不应该对其有任何影响

### 事务操作

```sql
START TRANSACTION;-- 开启事务,自动提交

COMMIT;-- 提交事务

ROLLBACK;-- 回退事务



set @@autocommit=0 -- 设置为手动提交

COMMIT;-- 提交事务

ROLLBACK;-- 回退事务
```

### 并发事务的问题

|问题|描述  |
| :--: | :------------------------------------: |
|     脏读   | 一个事务读到另外一个事务没有提交的数据  |
| 不可重复读 | 一个事务先后读取同一个记录,但是两次的读取的数据不同 |
| 幻读 | 一个事务按照条件查询数据时,没有对应的数据行,但是在插入的时候,这行数据又存在了 |

### 事务的隔离级别

**Read uncommitted(读未提交)** :什么都解决不了

**Read committed(读提交)** :解决了脏读

**repeatable read(可重复读取,MySQL默认)** 解决了脏读和不可重复读

**Serializable(可序化)** 都解决了

 ```sql
 -- 查看事务隔离级别
 SELECT @@transaction_isolation;
 
 -- 设置事务隔离级别
 SET [GLOBAL|SESSION] TRANSACTION ISOLATION LEVEL level;
 ```

## 引擎

存储引擎就是存储数据,建立索引,更新/查询数据等技术的实现方式.存储引擎是基于表的,而不是基于库的,索引存储引擎也可以被称为表类型.

```sql
show create table table_name; -- 查看建表语句可以查询到引擎,默认InnoDB
CREATE TABLE table_name (column_name1 数据类型,column_name2 数据类型,column_name3 数据类型,....) ENGINE = INNODB -- 在创建表时,指定存储引擎
show engines; -- 查看当前数据库支持的存储引擎
```

### 引擎特点

* InnoDB

  * 是一种兼顾高可靠性和高性能的同学存储性能引擎
  * DML操作遵循ACID模型,支持事务
  * 行级锁,提高高并发性能
  * 支持外键约束,保证数据的完整性和正确性
* MyISAM
  * 最早的默认存储引擎
  * 不支持事务
  * 支持表锁,不支持行锁
  * 访问速度快
* Memory
  * 是存储在内存中的
  * 内存存放
  * hash索引

# 索引

**索引**是帮助MySQL高效获取数据的数据结构.在数据之外,数据库系统还维护着满足特定的查找算法的数据结构

优点:提高检索效率,降低数据库IO成本;通过索引进行排序,降低排序成本

缺点:索引列要占空间;提高了查询效率,同时也降低了更新表的速度

## 索引分类

B树,B+树,hash

为什么使用B+树:

> 相对于二叉树，层级更少，搜索效率高;
>
> 对于B-tree，无论是叶子节点还是非叶子节点，都会保存数据，这样导致一页中存储的键值减少，指针跟着减少，要同样保存大量数据，只能增加树的高度，导致性能降低;
>
> 相对Hash索引，B+tree支持范围匹配及排序操作;

主键索引、唯一索引、常规索引、全文索引

聚集索引、二级索引

## 索引操作

```sql
CREATE INDEX index_name ON table_name (column_names) -- 在表中创建索引，以便更加快速高效地查询数据
ALTER TABLE table_name DROP INDEX index_name -- 删除表格中的索引
-- 联合索引可以规避回表查询
```

## SQL 性能分析

```sql
SHOW GOBAL STATUS LIKE 'Com_______' -- SQL执行频率
```

**慢查询日志**

通过慢查询日志来查看执行超过十秒的SQL语句,来定位执行缓慢的SQL

**profile详细**

```sql
select @@have_profiling -- 查看是否支持
select @@profiling -- 查看是否开启
set profiling=1 -- 开启
show profiles -- 查看SQL的操作时间
show profile (cpu) for query id -- 查看执行耗时的详细情况
```

**explain执行计划**

可以查看到explain获取mysql如何执行select,查看他的执行计划

>id:select的查询的序列号,表示查询中执行select子句或者是操作表的顺序
>
>select_type:查询的类型
>
>type:表示连接类型性能由好到差:NULL,SYSTEM,const,eq_ref,ref,range,index,all
>
>possinle_keys:在这张表中可能用到的索引
>
>key:实际用到的索引
>
>key_len:使用到的索引的字节数
>
>rows:执行查询的行数,预估值
>
>filtered:返回结果的行数占查询行数的百分比
>
>EXTra:额外的信息

## 索引使用

* 最左前缀法则:如果索引关联了多列,就要遵循,指的是查询的时候从索引最左边开始查,不能跳过中间的列,如果左边的不存在,索引失效,如果跳过,部分失效
* 范围查询:在联合查询中,范围查询右边的列索引失效
* SQL提示:加入一些提示告诉数据库来达到优化的目的
  * use index
  * ignore index
  * force index

* 覆盖索引:尽量使用覆盖索引(查询使用了索引,并且需要返回的列,在该索引中已经全部可以找到),减少select *
* 前缀索引:将字符串的前一部分来建立索引
* 单列索引:一个列
* 联合索引:多个列

## 索引失效

* 不要再索引列上进行运算操作
* 字符串类型的字段不加引号
* 模糊匹配,后面加%索引不失效,前面加失效.
* or连接的条件,如果前面的条件中有索引,后面的没有,那索引就不会生效
* 数据分布影响,如果mysql评估索引比全表慢,就不会使用索引

## 索引设计原则

1.针对于数据量较大，且查询比较频繁的表建立索引。

2.针对于常作为查询条件(where)、排序(order by)、分组(group by)操作的字段建立索引。

3.尽量选择区分度高的列作为索引，尽量建立唯一索引，区分度越高，使用索引的效率越高。

4.如果是字符串类型的字段，字段的长度较长，可以针对于字段的特点，建立前缀索引。

5.尽量使用联合索引，减少单列索引，查询时，联合索引很多时候可以覆盖索引，节省存储空间，避免回表，提高查询效率。

6.要控制索引的数量，索引并不是多多益善，索引越多，维护索引结构的代价也就越大，会影响增删改的效率。

7.如果索引列不能存储NULL值，请在创建表时使用NOTNULL约束它。当优化器知道每列是否包含NULL值时，它可以更好地确定哪个索引最有效地用于查询。

# SQL优化

## insert优化

* 批量插入
* 手动事务提交
* 主键顺序插入
* 大批量插入数据直接使用load指令进行插入

## 主键优化

* 在InnoDB存储引擎中，表数据都是根据主键顺序组织存放的，这种存储方式的表称为索引组织表
* 页分裂
  * 页可以为空，也可以填充一半，也可以填充100%。每个页包含了2-N行数据(如果一行数据多大，会行溢出)，根据主键排列。
* 页合并
  * 当删除一行记录时，实际上记录并没有被物理删除，只是记录被标记(flaged)为删除并且它的空间变得允许被其他记录声明使用。当页中删除的记录达到MERGE_THRESHOLD(默认为页的50%)，InnoDB会开始寻找最靠近的页（前或后）看看是否可以将两个页合并以优化空间使用。
* 主键设计原则
  * 在满足需求的情况下,尽量降低主键的长度
  * 顺序插入主键
  * 尽量不要使用UUID做主键等其他自然主键
  * 避免修改主键

## order by优化

* Using filesort :通过表的索引或全表扫描，读取满足条件的数据行，然后在排序缓冲区sort buffer中完成排序操作
  所有不是通过索引直接返回排序结果的排序都叫FileSort排序。

* Using index:通过有序索引顺序扫描直接返回有序数据，这种情况即为using index，不需要额外排序，操作效率高。

>根据排序字段建立合适的索引，多字段排序时，也遵循最左前缀法则。
>
>尽量使用覆盖索引。
>
>多字段排序,一个升序一个降序，此时需要注意联合索引在创建时的规则(ASC/DESC)。
>
>如果不可避免的出现filesort，大数据量排序时，可以适当增大排序缓冲区大小 sort_buffer_size(默认256k)。

## group by 优化

在分组操作时，可以通过索引来提高效率。

分组操作时，索引的使用也是满足最左前缀法则的。

## limit优化

通过创建覆盖索引能够比较好地提高性能，可以通过覆盖索引加子查询形式进行优化

## count

自己计数

count（主键)
InnoDB引擎会遍历整张表，把每一行的主键id值都取出来，返回给服务层。服务层拿到主键后，直接按行进行累加(主键不可能为null)。

count(字段)没有not null约束: InnoDB引擎会遍历整张表把每一行的字段值都取出来，返回给服务层，服务层判断是否为null，不为null，计数累加。有not null约束: InnoDB引擎会遍历整张表把每一行的字段值都取出来，返回给服务层，直接按行进行累加。

count ( 1)InnoDB引擎遍历整张表，但不取值。服务层对于返回的每一行，放一个数字“1”进去，直接按行进行累加。

count (*)InnoDB引擎并不会把全部字段取出来，而是专门做了优化，不取值，服务层直接按行进行累加。

## update

根据索引字段进行更新,不然行锁就会变成表锁,并且索引不能失效不然也是

# 视图

视图（View)是一种虚拟存在的表。视图中的数据并不在数据库中实际存在，行和列数据来自定义视图的查询中使用的表，并且是在使用视图时动态生成的。

通俗的讲，视图只保存了查询的SQL逻辑，不保存查询结果。所以我们在创建视图的时候，主要的工作就落在创建这条SQL查询语句上。

```sql
create [or replace] view 视图名称 as select语句 -- 创建视图 也可以用来修改视图
alter view 视图名称 as select 语句 -- 修改视图
with cascaded check option -- 添加检查选项
```

## 视图的检查选项

当使用WITHCHECK OPTON子句创建视图时，MySQL会通过视图检查正在更改的每个行，例如插入，更新，删除，以使其符合视图的定义。MySQL允许基于另一

个视图创建视图，它还会检查依赖视图中的规则以保持一致性。为了确定检查的范围，mysql提供了两个选项:CASCADED 和LOCAL，默认值为CASCADED。

## 视图的更新

要使视图可更新，视图中的行与基础表中的行之间必须存在一对一的关系。如果视图包含以下任何一项，则该视图不可更新

## 视图的作用

* 简单
  * 视图不仅可以简化用户对数据的理解，也可以简化他们的操作。那些被经常使用的查询可以被定义为视图，从而使得用户不必为以后的操作每次指定全部的条件。
* 安全
  * 数据库可以授权，但不能授权到数据库特定行和特定的列上。通过视图用户只能查询和修改他们所能见到的数据
* 屏蔽基表的结构变化对业务的影响
  * 视图可帮助用户屏蔽真实表结构变化带来的影响。

# 存储过程

存储过程是事先经过编译并存储在数据库中的一段SQL语句的集合，调用存储过程可以简化应用开发人员的很多工作，减少数据在数据库和应用服务器之间的传输，对于提高数据处理的效率是有好处的。

存储过程思想上很简单，就是数据库SQL语言层面的代码封装与重用。

**优点:**

>封装,复用
>
>可以接收参数,也可以返回数据
>
>减少网络交互,提升效率

## 创建存储过程

```sql
create procedure 存储过程名称([参数列表])
begin
	-- sql
end 	-- 创建存储过程

call 名称(参数) -- 调用

SELECT * FROM INFORMATION_SCHEMA.ROUTNES WHERE ROUTINE_SCHEMA ='xxx ' -- 查询指定数据库的存储过程及状态信息

SHOW CREATE PROCEDURE 存储过程名称 -- 查询某个存储过程的定义

drop PROCEDURE
```

## 系统变量

```SQL
show session variables [like] -- 查看会话系统变量
show global session variables [like] -- 查看全局系统变量
select @@global.autocommit -- 查看

set session autocommit = 0 -- 设置系统变量
```

## 用户自定义变量

```sql
set @var = expr
set @var := expr
select 字段名 into @var_name from 表名

select @var_name;
```

## 局部变量

是根据需要定义的在局部生效的变量，访问之前，需要DECLARE声明。可用作存储过程内的局部变量和输入参数，局部变量的范围是在其内声明的BEGIN 

...END块。

```sql
declare stu_count int default 0;
set stu_count = 0;
select count(*) into stu_count from student
```

## if判断

```sql
create procedure p3()

begin
    declare score int default 62;
    declare result varchar(10);
    if score >= 85 then
        set result = '优秀';
    elseif score >= 60 then
        set result = '及格';
    end if;
    select result;
end;

call p3;

create procedure p4(in score int,out result varchar(10))
begin
    if score >= 85 then
        set result = '优秀';
    elseif score >= 60 then
        set result = '及格';
    else
        set result ='不及格';
    end if;
end;

call p4(90,@result);
select @result;

```

## case

```sql
create procedure p6(in month int)
begin
    declare result varchar(10);

    case
        when month >= 1 and month <= 3 then set result = '1';
        when month >= 6 and month <= 10 then set result = '2';
        when month >= 3 and month <= 6 then set result = '6';
        end case;

    select concat('month',month,'jidu',result);
end;

call p6(2);
```

## while

```sql
create procedure p7(in num int)
begin
    declare n int default 0;
    while num>0 do
        set n = n+num;
        set num = num-1;
        end while;
    select n;
end;

call p7(100);
```

## repeat

满足条件退出循环

```sql
create procedure p8(in num int)
begin
    declare n int default 0;
    repeat
        set n = n + num;
        set num = num - 1;
    until num <= 0 end repeat;
    select n;
end;

call p8(100);
```

## loop

```sql
create procedure p9(in n int)
begin
    declare total int default 0;
    sum :
    loop
        if n <= 0 then
            leave sum;
        end if;
        set total := total + n;set n := n - 1;
    end loop sum;

end;
```

## 游标

游标(CURSOR)是用来存储查询结果集的数据类型，在存储过程和函数中可以使用游标对结果集进行循环的处理。游标的使用包括游标的声明、OPEN、FETCH 和CLOSE

添加条件处理函数

```sql
create procedure p11(in uage int)
begin
    declare u_cursor cursor for select name, profession from tb_user where age <= uage;declare uname varchar(100);
    declare exit  handler for sqlstate '02000'  close u_cursor;
    declare upro varchar(100);
    drop table if exists tb_user_pro;
    create table if not exists tb_user_pro
    (
        id         int primary key auto_increment,
        name       varchar(150),
        profession varchar(100)
    );
    open u_cursor;
    while true
        do
            fetch u_cursor into uname , upro;
            insert into tb_user_pro values (null, uname, upro);
        end while;
    close u_cursor;
end;

```



# 触发器

触发器是与表有关的数据库对象，指在insert/update/delete之前或之后，触发并执行触发器中定义的SQL语句集合。触发器的这种特性可以协助应用在数据库端确保数据的完整性，日志记录，数据校验等操作。

使用别名OLD和NEW来引用触发器中发生变化的记录内容，这与其他的数据库是相似的。现在触发器还只支持行级触发，不支持语句级触发。

```sql

create trigger tb_user_insert_trigger
    after insert
    on tb_user
    for each row

begin

    insert into user_logs(id, operation, operate_time, operate_id) VALUES (null, 'insert', now(), new.id);
end;

```

```sql
create trigger tb_user_update_trigger
    after update 
    on tb_user
    for each row

begin

    insert into user_logs(id, operation, operate_time, operate_id) VALUES (null, 'insert', now(), new.id);
end;

```

```sql
create trigger tb_user_delete_trigger
    after delete 
    on tb_user
    for each row

begin

    insert into user_logs(id, operation, operate_time, operate_id) VALUES (null, 'insert', now(), OLD.id);
end;
```

# 锁

## 全局锁

对整个数据库实例加锁,整个实例只处于只读状态,后续的DML语句和DDL语句都会被阻塞

场景 : 全库的数据备份

## 表锁

表级锁，每次操作锁住整张表。锁定粒度大，发生锁冲突的概率最高，并发度最低。应用在MylISAM、InnoDB、BDB等存储引擎中。

* 表锁

  * 表共享读锁

  * 表共享写锁

    * ```sql
      lock table read/write
      unlock tables/客户端断开连接
      ```

    * 

* 元数据锁:MDL加锁过程是系统自动控制，无需显式使用，在访问一张表的时候会自动加上。MDL锁主要作用是维护表元数据的数据一致性，在表上有活动事务的时候，不可以对元数据进行写入操作。为了避免DML与DDL冲突，保证读写的正确性。

* 意向锁:为了避免DML在执行时，加的行锁与表锁的冲突，在InnoDB中引入了意向锁，使得表锁不用检查每行数据是否加锁，使用意向锁来减
  少表锁的检查。
