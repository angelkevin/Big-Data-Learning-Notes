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
```
