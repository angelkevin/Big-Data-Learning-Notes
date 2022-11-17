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

---

```sql
CREATE INDEX index_name ON table_name (column_name) -- 在表中创建索引，以便更加快速高效地查询数据
ALTER TABLE table_name DROP INDEX index_name -- 删除表格中的索引
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

