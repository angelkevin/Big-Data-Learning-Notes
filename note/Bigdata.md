[TOC]



# 大数据环境配置教程

**本环境基于centos 7搭建完成，使用的是单节点模式，搭建分布式仅供参考，所有的文件都解压放在/home/softwares中，centos01为本虚拟机名字，防火墙需要关闭**

# sudo vim /etc/hosts

```xml
<configuration>
 <property>
 <property>
 <name>hive.metastore.warehouse.dir</name>
 <value>/home/software/hive/warehouse</value>
 </property>
 <property>
 <!-- 配置MySQL的连接字符串 -->
 <name>javax.jdo.option.ConnectionURL</name>
 <value>jdbc:mysql://hadoop01:3306/hive?createDatabaseIfNotExist=true</value>
 </property>
 <property>
 <!-- 配置MySQL的连接驱动 -->
 <name>javax.jdo.option.ConnectionDriverName</name>
 <value>com.mysql.jdbc.Driver</value>
 </property>
<property>
 <!-- 配置登录MySQL的用户名 -->
 <name>javax.jdo.option.ConnectionUserName</name>
 <value>root</value>
 </property>
 <property>
 <!-- 配置登录MySQL的密码 -->
 <name>javax.jdo.option.ConnectionPassword</name>
 <value>root</value>
 </property>
 <property>
 <!-- 配置Hive的慢查询的日志目录 -->
 <name>hive.querylog.location</name>
 <value>hdfs://192.168.112.128:9000/user/hive/log</value>
 </property>
<property>
 <!-- hive的server2的连接端口 -->
 <name>hive.server2.thrift.port</name>
 <value>10000</value>
 </property>
 <property>
 <!-- hive的server2的连接主机名称 -->
 分区 郑州轻工业大学大数据实训 的第 21 页
 <!-- hive的server2的连接主机名称 -->
 <name>hive.server2.thrift.bind.host</name>
 <value>192.168.112.128</value>
 </property>
<property>
 <!-- hive的元数据服务的uri -->
 <name>hive.metastore.uris</name>
 <value>thrift://192.168.112.128:9083</value>
 </property>
</configuration>
```

# sudo vim /etc/profile

## 所有的环境变量，在下面就不一一赘述了

``` shell
export HADOOP_HOME=/home/softwares/hadoop-3.1.3
export PATH=$PATH:$HADOOP_HOME/bin
export PATH=$PATH:$HADOOP_HOME/sbin
export JAVA_HOME=/home/softwares/jdk1.8.0_212
export PATH=$PATH:$JAVA_HOME/bin
export ZOOKEEPER_HOME=/home/softwares/zookeeper
export PATH=$PATH:$ZOOKEEPER_HOME/bin:$ZOOKEEPER_HOME/conf
export FLUME_HOME=/home/softwares/flume
export PATH=$PATH:$FLUME_HOME/bin
export HIVE_HOME=/home/softwares/hive
export PATH=$PATH:$HIVE_HOME/bin
export SQOOP_HOME=/home/softwares/sqoop
export PATH=$PATH:$SQOOP_HOME/bin
export KAFKA_HOME=/home/softwares/kafka
export PATH=$PATH:$KAFKA_HOME/bin
export SPARK_HOME=/home/softwares/spark
export PATH=$PATH:$SPARK_HOME/bin
export PATH=/root:$PATH
export SQOOP_HOME=/home/softwares/sqoop
export PATH=$PATH:$SQOOP_HOME/bin

export HDFS_NAMENODE_USER=root
export HDFS_DATANODE_USER=root
export HDFS_SECONDARYNAMENODE_USER=root
export YARN_RESOURCEMANAGER_USER=root
export YARN_NODEMANAGER_USER=root

```



# Hadoop

## Hadoop Setting

### hadoop-env.sh maperd-env.sh yarn-env.sh

```shell
export JAVA_HOME=/home/softwares/jdk1.8.0_212
```

### core-site.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
<!--
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License. See accompanying LICENSE file.
-->

<!-- Put site-specific property overrides in this file. -->

<configuration>
    <property>
        <name>hadoop.tmp.dir</name>
        <value>file:/home/softwares/hadoop-3.1.3/tmp</value>
        <description>Abase for other temporary directories.</description>
    </property>
    <property>
        <name>fs.defaultFS</name>
        <value>hdfs://hadoop01:9000</value> <!--name node 端口以及Hadoop地址-->
    </property>
</configuration>
```

### hdfs-site.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
<!--
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License. See accompanying LICENSE file.
-->

<!-- Put site-specific property overrides in this file. -->

<configuration>
    <property>
        <name>dfs.replication</name>
        <value>2</value>		<!--副本数目-->
    </property>
    <property>
        <name>dfs.namenode.name.dir</name>
        <value>file:/home/softwares/hadoop-3.1.3/tmp/dfs/name</value>		<!--namenode文件夹地址-->
    </property>
    <property>
        <name>dfs.datanode.data.dir</name>
        <value>file:/home/softwares/hadoop-3.1.3/tmp/dfs/data</value>		<!--datanode文件夹地址-->
    </property>
    <property>
        <name>dfs.permissions.enabled</name>
        <value>false</value>		<!--是否在HDFS中开启权限检查,默认为true-->
    </property>
</configuration>
```

### maper-site.xml

```xml
<?xml version="1.0"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
<!--
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License. See accompanying LICENSE file.
-->

<configuration>
    <property>
        <name>mapreduce.framework.name</name>
        <value>yarn</value>
    </property>
</configuration>
```

### yarn-site.xml

```xml
<?xml version="1.0"?>
<!--
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License. See accompanying LICENSE file.
-->
<configuration>
    <property>
        <name>yarn.nodemanager.aux-services</name>
        <value>mapreduce_shuffle</value>
    </property>
</configuration>
```

## start and stop Hadoop

```shell
hadoop namenode -format #只在第一次运行的时候进行格式化
start-all.sh
stop-all.sh
jps		#查看是否启动成功
```



# Zookeeper

## Zookeeper Setting

### zoo.conf(单节点)

```shell
tickTime=2000
dataDir=/home/softwares/apache-zookeeper-3.5.7-bin/data		#文件存储位置
clientPort=2181		#端口号
```

### zoo.conf(多节点)

```shell
tickTime=2000
dataDir=/home/softwares/apache-zookeeper-3.5.7-bin/data		#文件存储位置
clientPort=2181		#端口号
initLimit=5
syncLimit=2		#最多心跳数

server.1=?		#example centos01:2888:3888
server.2=?		#主机名字不一样端口号一样
server.3=?
```

## Zookeeper Start

```shell
zkServer.sh start	#启动
zkServer.sh status # 查看状态
```



# Kafka

## Kafka Setting

### server.properties

```shell
broker.id=1		#每一个Broker的标识符
num.partitions=1		#每个主题的分区数量
offsets.topic.replication.factor=1		#消息备份副本数
listeners=PLAINTEXT://centos01:9092		#监听地址，默认端口9092
log.dirs=/home/softwares/kafka_2.11-2.4.1/tmp/kafka-logs		#KafKa消息数据的存储位置，可以多个，用逗号分割
zookeeper.connect=centos01:2181		#Zookeeper连接地址，如果有多个节点，一一写上
```

```shell
#先启动zookeeper

bin/kafka-server-start.sh -daemon config/server.properties		#kafka安装目录下执行 
kafka-console-producer.sh --broker-list centos01:9092 --topic test		#创建名字为test的topic
kafka-topics.sh --create  --zookeeper  centos01:2181 --replication-factor 1 --partitions 1 --topic test
kafka-console-consumer.sh --bootstrap-server centos01:9092 --topic test --from-beginning
```



# Flume

```shell
flume-ng agent --name a1 --conf conf --conf-file /home/softwares/apache-flume-1.9.0-bin/conf/flume-kafka.properties -Dflume.root.logger=INFO,console
flume-ng agent --name a2 --conf conf --conf-file /home/softwares/apache-flume-1.9.0-bin/conf/flume-hdfs.properties -Dflume.root.logger=INFO,console
```

## Flume-config-example

### Flume-Kafka. Properties

```shell
#定义组件
a1.sources=r1
a1.channels=c1
a1.sinks=k1
#配置source
a1.sources.r1.type = TAILDIR
a1.sources.r1.positionFile = /home/softwares/applog/log/taildir_position.json
a1.sources.r1.filegroups = f1
a1.sources.r1.filegroups.f1 = /home/softwares/applog/log/app.*
a1.sources.r1.interceptors = i1
a1.sources.r1.interceptors.i1.type = org.example.ETLInterceptor$Builder
#配置channel
a1.channels.c1.type = memory
a1.channels.c1.capacity = 1000
a1.channels.c1.transactionCapacity = 100
#配置sink
a1.sinks.k1.type = org.apache.flume.sink.kafka.KafkaSink
a1.sinks.k1.kafka.topic = test
a1.sinks.k1.kafka.bootstrap.servers = centos01:9092
#拼接组件
a1.sources.r1.channels=c1
a1.sinks.k1.channel = c1
```

### Flume-HDFS.properties

```shell
#定义组件
a2.sources = r2
a2.channels = c2
a2.sinks = k2
#配置source
a2.sources.r2.type = org.apache.flume.source.kafka.KafkaSource
a2.sources.r2.kafka.bootstrap.servers = centos01:9092
a2.sources.r2.kafka.topics=test
#配置channel
a2.channels.c2.type = file
#配置sink
a2.sinks.k2.type = hdfs
a2.sinks.k2.hdfs.path = hdfs://192.168.170.133:9000/flume/%y-%m-%d
a2.sinks.k2.hdfs.fileType=DataStream
#拼接组件
a2.sinks.k2.channel = c2
a2.sources.r2.channels=c2
```

## Flume-Interceptor

``` java
package org.example;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;

/**
 * 1.实现接口Interceptor
 * 2.4个构造方法
 * 3.静态内部类builder
 */
public class ETLInterceptor implements Interceptor {
    public static boolean isjson(String log) {
        boolean flag = false;
        try {
            JSONObject.parseObject(log);
            flag = true;
        } catch (JSONException ignored) {

        }
        return flag;
    }

    @Override
    public void initialize() {

    }

    @Override
    public Event intercept(Event event) {
        byte[] body = event.getBody();
        String log = new String(body, StandardCharsets.UTF_8);

        boolean flag = isjson(log);

        return flag ? event : null;
    }

    @Override
    public List<Event> intercept(List<Event> list) {
        Iterator<Event> iterator = list.iterator();
        while (iterator.hasNext()) {
            Event event = iterator.next();
            if (intercept(event) == null) {
                iterator.remove();
            }
        }

        return list;
    }

    @Override
    public void close() {

    }

    public static class Builder implements Interceptor.Builder {

        @Override
        public Interceptor build() {
            return new ETLInterceptor();
        }

        @Override
        public void configure(Context context) {

        }
    }
}

```



# MySQL

```shell
# 先卸载原有依赖
rpm -qa|grep mysql
rpm -qa|grep mariadb
rpm -e --nodeps ?	#前两个命令出现的内容 
#安装MySQL按照顺序安装
rpm -ivh mysql-community-common-5.7.29-1.el6.x86_64.rpm
rpm -ivh mysql-community-libs-5.7.29-1.el6.x86_64.rpm
rpm -ivh mysql-community-client-5.7.29-1.el6.x86_64.rpm
rpm -ivh mysql-community-server-5.7.29-1.el6.x86_64.rpm
#初始化
mysqld --initialize --user=mysql
#获得初始化密码
cat /var/log/mysqld.log | grep 'temporary password is generated'
#启动MySQL
service mysql start
#设置自启动
systemctl enable mysqld.sercice

#service mysql start | stop | restart | status
##start：启动 MySQL 服务
##stop：停止 MySQL 服务
##restart：重启 MySQL 服务
##status：查看 MySQL 服务状态
##start：启动 MySQL 服务
##stop：停止 MySQL 服务
##restart：重启 MySQL 服务
##status：查看 MySQL 服务状态

mysql -u root[用户名] -p [密码]
set password for 'root'@'localhost'=password('？');
#配置远程访问
grant all privileges on *.* to 'root' @'%' identified by 'root';
flush privileges;
```



# Hive on Spark

## Hive Setting(本地模式)

### hive-env.sh

```shell
HADOOP_HOME=export HADOOP_HOME=/home/softwares/hadoop-3.1.3
```

### Set MySQL

```shell
create database hive_db;
create user hive IDENTIFIED by 'hive';
grant all privileges on hive_db.* to hive@'%' identified by 'hive';
flush privileges;
```

### hive/lib

```
将MySQL驱动上传至hive中的lib
```



### Hive-site.xml

```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
<configuration>
    <property>
        <name>javax.jdo.option.ConnectionURL</name>
        <value>jdbc:mysql://192.168.170.130:3306/hive_db</value>
    </property>
    <property>
        <name>javax.jdo.option.ConnectionDriverName</name>
        <value>com.mysql.jdbc.Driver</value>
    </property>
    <property>
        <name>javax.jdo.option.ConnectionUserName</name>
        <value>hive</value>
    </property>
    <property>
        <name>javax.jdo.option.ConnectionPassword</name>
        <value>hive</value>
    </property>
    <property>
        <name>hive.metastore.schema.verification</name>
        <value>false</value>
    </property>
    <property>
        <name>hive.server2.thrift.port</name>
        <value>10000</value>
    </property>
    <property>
        <name>hive.server2.thrift.bind.host</name>
        <value>hadoop01</value>
    </property>
    <property>
        <name>hive.metastore.event.db.notification.api.auth</name>
        <value>false</value>
    </property>
    <property>
        <name>hive.cli.print.header</name>
        <value>true</value>
    </property>
    <property>
        <name>hive.cli.print.current.db</name>
        <value>true</value>
    </property>
    <!--hive on spark -->
    <property>
        <name>spark.yarn.jars</name>
        <value>hdfs://centos01:9000/spark-jar/*</value>
    </property>

    <property>
        <name>hive.execution.engine</name>
        <value>spark</value>
    </property>
</configuration>
```

```shell
#初始化元数据
schematool -dbType mysql -initSchema
```

### HDFS

```shell
hadoop fs -mkdir /spark-history
hadoop fs -mkdir /spark-jars
```

### spark-defaluts.conf

```shell
spark.master yarn
spark.eventLog.enabled true
spark.eventLog.dir hdfs://centos01:8020/spark-history
spark.executor.memory 1g
spark.driver.memory 1g
```

### put jar

```shell
#采用 Spark 纯净版 jar 包，不包含 hadoop 和 hive 相关依赖，避免冲突
hadoop fs -put spark-3.0.0-bin-without-hadoop/jars/* /spark-jar
```



# Sqoop

## Sqoop setting

### sqoop-env.sh



# sqoop命令

```shell

bin/sqoop import \
--connect jdbc:mysql://hadoop01:3306/gmall \
-username root \
--password 123456 \
--table user_info \
--columns id,login_name \
--where 'id>=1 and id<=20' \
--target-dir /user_info \
--delete-target-dir \
--fields-terminated-by '\t' \
--num-mappers 2 \
--split-by id


bin/sqoop import \
--connect jdbc:mysql://hadoop01:3306/gmall \
-username root \
--password 123456 \
--query 'select id,login_name from user_info where id>=10 and id<=20 and $CONDITIONS' \
--target-dir /user_info \
--delete-target-dir \
--fields-terminated-by '\t' \
--num-mappers 2 \
--split-by id

```

```shell
bin/sqoop import \
--connect jdbc:mysql://hadoop01:3306/gmall \
-username root \
--password 123456 \
--query 'select * from order_info $CONDITIONS' \
--target-dir /user_info/2020-0614 \
--delete-target-dir \
--fields-terminated-by '\t' \
--num-mappers 2 \
--split-by id



bin/sqoop import \
--connect jdbc:mysql://hadoop01:3306/gmall \
-username root \
--password 123456 \
--query 'select id,login_name from user_info where create_time='' and id<=20 and $CONDITIONS' \
--target-dir /user_info \
--delete-target-dir \
--fields-terminated-by '\t' \
--num-mappers 2 \
--split-by id
```















```shell

#! /bin/bash

case $1 in
"start"){
        for i in hadoop02 hadoop03
        do
                echo " --------启动 $i 采集flume-------"
                ssh $i "source /etc/profile;nohup /home/softwares/flume/bin/flume-ng agent --conf-file  /home/softwares/flume/conf/file-flume-kafka.conf --name a1 -Dflume.root.logger=INFO,LOGFILE >/home/softwares/flume/log1.txt 2>&1  &"
        done
};;	
"stop"){
        for i in hadoop02 hadoop03
        do
                echo " --------停止 $i 采集flume-------"
                ssh $i "ps -ef | grep file-flume-kafka | grep -v grep |awk  '{print \$2}' | xargs -n1 kill -9 "
        done

};;
esac

```

```shell
#! /bin/bash

case $1 in
"start"){
        for i in hadoop02 hadoop03
        do
                echo " --------启动 $i 采集flume-------"
                ssh $i "source /etc/profile;nohup /home/softwares/flume/bin/flume-ng agent --conf-file  /home/softwares/flume/conf/kafka-flume-hdfs.conf --name a1 -Dflume.root.logger=INFO,LOGFILE >/home/softwares/flume/log2.txt 2>&1  &"
        done
};;	
"stop"){
        for i in hadoop02 hadoop03
        do
                echo " --------停止 $i 采集flume-------"
                ssh $i "ps -ef | grep kafka-flume-hdfs.conf | grep -v grep |awk  '{print \$2}' | xargs -n1 kill -9 "
        done

};;
esac

```



```java
## 组件
a1.sources=r1
a1.channels=c1
a1.sinks=k1

## source1
a1.sources.r1.type = org.apache.flume.source.kafka.KafkaSource
a1.sources.r1.batchSize = 5000
a1.sources.r1.batchDurationMillis = 2000
a1.sources.r1.kafka.bootstrap.servers = hadoop01:9092,hadoop02:9092,hadoop03:9092
a1.sources.r1.kafka.topics=topic_log


## channel1
a1.channels.c1.type = file
a1.channels.c1.checkpointDir = /home/softwares/flume/checkpoint/behavior1
a1.channels.c1.dataDirs = /home/softwares/flume/data/behavior1/


## sink1
a1.sinks.k1.type = hdfs
a1.sinks.k1.hdfs.path = /origin_data/gmall/log/topic_log/%Y-%m-%d
a1.sinks.k1.hdfs.filePrefix = log-
a1.sinks.k1.hdfs.round = false

#控制生成的小文件
a1.sinks.k1.hdfs.rollInterval = 10
a1.sinks.k1.hdfs.rollSize = 134217728
a1.sinks.k1.hdfs.rollCount = 0

## 控制输出文件是原生文件。
a1.sinks.k1.hdfs.fileType = CompressedStream
a1.sinks.k1.hdfs.codeC = lzop

## 拼装
a1.sources.r1.channels = c1
a1.sinks.k1.channel= c1

```

```shell
#!/bin/bash

case $1 in
"start"){
        echo ================== 启动 集群 ==================

        #启动 Zookeeper集群
        zk.sh start

        #启动 Hadoop集群
        /home/softwares/hadoop-3.1.3/sbin/start-all.sh

        #启动 Kafka采集集群
        kf.sh start

        #启动 Flume采集集群
        f1.sh start

        #启动 Flume消费集群
        f2.sh start

        };;
"stop"){
        echo ================== 停止 集群 ==================

        #停止 Flume消费集群
        f2.sh stop

        #停止 Flume采集集群
        f1.sh stop

        #停止 Kafka采集集群
        kf.sh stop

        #停止 Hadoop集群
        /home/softwares/hadoop-3.1.3/sbin/stop-all.sh sto

        #停止 Zookeeper集群
        zk.sh stop

};;
esac

```











# 下载链接

虚拟机下载链接https://pan.baidu.com/s/1h70_3xJaMJtiggNM_X-UdQ?pwd=zkw6

所需文件下载链接链接：https://pan.baidu.com/s/1HNiUwjjxg_TZxVnoe_4G8A?pwd=zkw6 
