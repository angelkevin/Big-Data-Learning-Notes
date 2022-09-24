# 内部表

> Hive中默认创建的普通表为管理表或者内部表，内部表的数据由Hive进行管理，默认存储于数据仓库目录'/user/hive/warehouse'中，可在hive中配置文件hive-site.xml中对数据仓库的目录进行修改。
>
> 删除内部表的时候，表数据和元数据一起删除。

```sql
create db;		-- 创建数据库
create table student(id int,name string);		-- 创建student表，字段id为整形，字段name为字符串
DESC table;		-- 查看table的表结构
DESC FORMATTED table;		-- 查看table的详细表结构
INSERT INTO student VALUES(1000,'ZKWW')		-- 插入数据
SELECT 8 FROM table;		-- 查询表中的所有数据
CREATE TABLE score(sno int,name string,score int) row format delimited fields terminated by '\t' -- 新建学生成绩表socore，其中学号sno 为int，姓名name为字符串，得分score为整形，并指定Tab键为分隔符
LOAD DATA LOCAL INPUT '/home/score.txt' INTO TABLE score;		-- 将score.txt文件写入表score中
DROP TABLE IF EXISTS db.student;		-- 删除表
```

# 外部表

>除了内部表，hive也可以使用关键字'EXTERNAL'创建外部表。外部表的数据可存储在数据仓库之外的位置，因此hive并未认为其完全拥有这份数据。
>
>外部表在创建的时候可以关联HDFS中已经存在的数据，也可以手动添加数据。删除外部表不会删除表数据，但是元数据会被删除。

```sql
CREATE EXTERNAL TABLE test.emp(id int,name string);		-- 创建一个外部表，字段id为整形，字段name为字符串
create external table test_db.emp2(id int,name string)
	row format delimited fields terminated by '\t'
    location '/input/hive';		-- 创建test.emp表，并指定在HDFS上的存储目录为'/input/hive'，表段分隔符为tab键
drop table test.emp;		-- 删除表test.emp，只删除元数据，不会删除实际数据
```

# 分区表

>Hive可以使用关键字PARTITIONED BY 对一张表进行分区操作。可以根据某一列的值将表分为多个分区，每一个分区对应每一个目录。当查询数据的时候，根据where条件查询的指定分区不需要全表扫描，从而可以加快数据的查询速度，在HDFS文件系统中，分区实际上只是在表数据下嵌套的子目录。

 ```sql
 CREATE EXTERNAL TABLE ods_activity_info(
     `id` STRING COMMENT '编号',
     `activity_name` STRING  COMMENT '活动名称',
     `activity_type` STRING  COMMENT '活动类型',
     `start_time` STRING  COMMENT '开始时间',
     `end_time` STRING  COMMENT '结束时间',
     `create_time` STRING  COMMENT '创建时间'
 ) COMMENT '活动信息表'
 PARTITIONED BY (`dt` STRING)
 ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'
 STORED AS
   INPUTFORMAT 'com.hadoop.mapred.DeprecatedLzoTextInputFormat'
   OUTPUTFORMAT 'org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat'
 LOCATION '/warehouse/gmall/ods/ods_activity_info/';
 
 load data local inpath '/1.txt' into table dept partition(day = '22') -- 加载数据到分区
 show partition [table] -- 展示分区
 alter table [table_name] drop partition(day='2022') -- 删除分区
 desc formatted [table_name] -- 查看分区表的结构
 msck repair table [table name] -- 批量修复分区
 alter table [table name] add partition(day='2021') --  添加分区
 
 hive.exec.dynamic.partition=ture
 -- 动态分区按照最后一个作为分区
 
 ```

# 分桶表

>在hive中，可以将表或分区进一步细分成桶，桶是对数据进行更细粒度的划分，以便获得更高的查询效率。桶在数据储存上与分区不同的是，一个分区会存储为一个目录，数据文件存储于该目录中，而一个桶将储存为一个文件，数据内容存储于该文件中。
>
>

```sql
create table test_bucket_sorted (
`id` int comment 'ID', 
`name` string comment '名字'
)
clustered by(id) sorte into 4 buckets
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',' ;

load data local inpath '/1.txt' into table stu;
-- 分桶规则是用哈希
-- reducer 最好设置为-1,让其自己决定，或者设置大于等于桶的个数

select * from table_name table sample(bucket 1 out of 4 on id)
-- 分桶表，抽样查询
```

# DDl

```sql
alter table test rename to test8; -- 重命名表，新表的名字不能存在
alter table table_name change column id stu_id int; -- 重命名列并转化类型
alter table table_name add colums (name string); -- 添加列
alter table table_name replace colums (name string); -- 替换列，全部替换
```

# DML

## 数据导入

```sql
 load data [local] input '数据的path' [overwrite] into table student [partition=...];
 -- load data 表示加载数据
 -- local 表示从本地加载，否则从HDFS加载
 -- inpath 表示加载路径
 -- overwrite 表示覆盖表中的已知数据，否则表示追加
 -- into table 表示加载到那张表
 -- student 表示表名具体的
 -- partition 表示上传到指定分区
 
 insert into table student select * from student_1;
 -- 边查询边插入，查询导入
 
 insert overwrite table student select * from student_1;
 -- 查询导入，覆盖写入
 
 create table student3 as select id,name from student;
 -- 根据查询结果创建表
 
 create table student5(id string,name string) row format delimited fields terminated by '\t' location '/student5';
 -- 根据location创建表加载数据 一般使用外部表
 
 import table student2 from '/user/hive/warehouse/export/student';
 -- 先用export导出后，再将数据导入
 
```

## 数据导出

```sql
insert overwrite local directory '/opt/student' select * from student row format delimited files terminated by ',';
-- insert 导出文件 并且用逗号分割 不用local就是导入到hdfs路径，local是导入到本地
 
dfs -get /user/hive/student.txt /opt/student3.txt
-- Hadoop命令导出到本地

hive -e 'select * from default.student;' >> student1.txt
-- 使用hive的shell命令

export table student2 to '/user/hive/warehouse/export/student';
-- 导出到hdfs路径

truncate table student;
-- 清楚表的数据，只删除管理表，不删除外部表中的数据
```

## 数据查询

```sql
select [all|distinct] from table;
-- 查询操作

as 
-- 给列起别名

count -- 求总行数
max -- 求最大值
min -- 求最小值
sum -- 求总和
avg -- 求平均

select * from stu where -- where后跟条件

```

### join连接

```sql
select e.ename,e.deno,d.dname from emp e jion dept d on e.deptno=d.deptno;
-- 内连接，只有进行连接两个表中都存在与连接条件相匹配的数据才会被保留下来

select e.empno,e.empname,d.deptno,d.deptname from emp e left join deop on d no e.deptno = d.deptno
-- 左外连接，以左表为主，右边没有交集的数据为Null

select e.empno,e.empname,d.deptno,d.deptname from emp e right join deop on d no e.deptno = d.deptno
-- 右外连接，以右表为主，左边没有交集的数据为Null

select e.empno,e.empname,d.deptno,d.deptname from emp e full join deop on d no e.deptno = d.deptno
-- 全连接 outer

select * from stu ，student；
-- 笛卡尔积，表中所有行相互连接，连接条件无效
```

### 排序

```sql
select * from stu order by id [desc]
-- 默认升序，desc降序

select * from emp distribut by deptno sort by;
-- 分区排序，分区自定义分区，记得设置reduce，

select * from emp  cluster by deptno;
-- 结合了distribut 和 sort，只能升序，

-- order by全局排序，只有一个reducer（ASC 升序，RESC 降序）效率低
-- sort by 每个reducer里面内部排序，和distribut by 一起使用
-- cluster by 当sort by 和distribut by 字段相同的时候并且是升序，使用clust by，

```

# Hive-DML函数

```sql
nvl([value],[value]) -- 给Null复制,可以写死代替，也可以用字段代替

case [table_name] when [conditions] then [value] else [value] end -- case 后跟表名，when后跟条件，then 后跟条件成立的值，else 后跟条件不成立的值   <=> if(conditions,value,value)

concat([value],-,[value]...) -- 拼接列，或者值
concat_ws([regex ],[value],[value]...) -- 提前写分隔符，除了拼接字符串，也可以拼接数组，map
collect_set -- 只接受基本数据类型，他的主要作用是将某字段的值进行去重汇总，产生Array类型字段

explode([col]) -- 将一切复杂的map和array拆分成多行 
lateral VIEW explode(split(str,[regex])) [table_name] AS name; -- 加侧写表，和原来的表产生连接

-- 窗口函数，后面只能跟over
over(partition by 列名 order by 列名 rows between 开始位置 and 结束位置)
select name , count(*) over() from stu; -- 每一条数据后都跟着结果，over
select name,orderdate,sum() over(partition by name) from bussness; -- 开窗，用name进行分区 over( row between value and value) 
    -- n preceding 往前n行 
    -- n followng 往后n行
    -- unbounded  preceding 表示从前面的起点 
    -- unbounded followng 表示到后面的终点
select name，orderdate,lag(orderdata,1) over(partition by name order by orderdate) from bussiness;
-- 展现前一行的数据
-- LAG(col,n,default_val):往前第n行数据 
-- LEAD(col,n,default_val)：往后第n行数据
select name,orderdate,cost,ntile(5) over(order by orderdate) groupid from bussiness;
-- 分组，分为五个组
-- rank() 排序相同会重复，总数不会变 1 1 3
-- dens_rank() 排序相同时会重复，总数会减少 1 1 2
-- row_number() 会顺序计算 1 2 3 

--



```















































