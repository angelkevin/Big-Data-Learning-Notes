# HMaster

 1) 监控RegionServer

2) 处理RegionServer故障转移

3) 处理元数据的变更

4) 处理region的分配或移除

5) 在空闲时间进行数据的负载均衡

6) 通过Zookeeper发布自己的位置给客户端

# RegionServer

1) 负责存储HBase的实际数据

2) 处理分配给它的Region

3) 刷新缓存到HDFS

4) 维护HLog

5) 执行压缩

6) 负责处理Region分片

# 常见shell命令

## 创建命名空间

```shell
list_namespace	#展示命名空间,也可以搭配正则表达式使用
create_namespace 'ns1'	#创建命名空间
```
---

## DML


```shell
create 'student','info'	#在默认的数据库下创建名为student.列族为info的表
create 'ns1:t1', {NAME => 'f1', VERSIONS => 5},{NAME => 'f2', VERSIONS => 5}	#在ns1命名空间中创建名为t1列族为f1,f2,且维护版本为五个版本的表

alter 't1', NAME => 'f1', VERSIONS => 5	#修改表名为t1,列族名为f1的版本号
alter 'ns1:t1', NAME => 'f1', METHOD => 'delete'	#删除命名空间为ns1,表名为t1,列族为f1的列族
alter 'ns1:t1', 'delete' => 'f1'

disable 't1'	#先禁用表t1
drop	't1'	#再删除t1

put 'ns1:t1', 'r1', 'c1:INFO', 'value'	#往命名空间为ns1,表名为t1,row_key为r1,列族为c1,列名为INFO,添加值为value的数据	#如果添加相同的值,就会覆盖掉之前的数据

get 't1', 'r1'	#获取表格t1,行号为r1的数据
get 't1', 'r1', {COLUMN => 'c1'}

 delete 'ns1:t1', 'r1', 'c1', ts1	#删除ns1命名空间下的t1表格中,行号为r1,列族名为c1,时间戳为ts1
```

