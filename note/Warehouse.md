

# 大数据环境搭建

**环境准备**：centos7，VMware虚拟机平台

## 虚拟机安装

> 都采用管理员root登录

### 关闭防火墙

```shell
sudo systemctl stop firewalld.service	#关闭虚拟机
sudo systemctl disable firewalld.service	#禁止虚拟机开机启动
```

### 修改主机名

```shell
sudo vi /etc/hostname
#修改后要reboot重启
```

### 卸载原有JDK

```shell
rpm -e --nodeps java-1.7.0-openjdk-headless-1.7.0.261-2.6.22.2.el7_8.x86_64
rpm -e --nodeps python-javapackages-3.4.1-11.el7.noarch
rpm -e --nodeps tzdata-java-2020a-1.el7.noarch
rpm -e --nodeps java-1.8.0-openjdk-headless-1.8.0.262.b10-1.el7.x86_64
rpm -e --nodeps java-1.8.0-openjdk-1.8.0.262.b10-1.el7.x86_64
rpm -e --nodeps javapackages-tools-3.4.1-11.el7.noarch
rpm -e --nodeps java-1.7.0-openjdk-1.7.0.261-2.6.22.2.el7_8.x86_64
```

### 虚拟机克隆

### 设置固定ip

### 修改每个节点的hosts文件

```shell
#仅仅为示例，包括后续的配置环境也是一样
192.168.170.130 hadoop01
192.168.170.131 hadoop02
192.168.170.132 hadoop03
```

### 配置免密登录

```shell
#每个节点都执行
ssh-keygen
ssh-copy-id root@hadoop01
ssh-copy-id root@hadoop02
ssh-copy-id root@hadoop03
```

***至此，虚拟机环境准备完毕***

## JDK安装

在/home目录下创建softwares文件夹，上传JDK文件至虚拟机并使用**tar -xvf**命令解压至该目录下

修改环境变量

```shell
vim /etc/profile
#添加以下内容
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
##如果是root用户登录则需要配置如下参数
export HDFS_NAMENODE_USER=root
export HDFS_DATANODE_USER=root
export HDFS_SECONDARYNAMENODE_USER=root
export YARN_RESOURCEMANAGER_USER=root
export YARN_NODEMANAGER_USER=root

#刷新环境变量
source /etc/profile
```

添加完毕后使用**java -version**命令进行检查是否配置成功

## Hadoop-3.1.3安装

把Hadoop-3.1.3的压缩包上传至虚拟机下并使用**tar -xvf**命令解压至/**home/softwares**目录下

进入**/home/softwares/hadoop-3.1.3/etc/hadoop/**文件，修改如下配置文件

### hadoop-env.sh maperd-env.sh yarn-env.sh

```shell
#添加JDk的环境变量
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
    <!--临时目录-->
    <property>
        <name>hadoop.tmp.dir</name>
        <value>file:/home/softwares/hadoop-3.1.3/tmp</value>
    </property>
    <!--name node 端口以及Hadoop地址-->
    <property>
        <name>fs.defaultFS</name>
        <value>hdfs://hadoop01:9000</value> 
    </property>
    <!--用户如果是root的话建议添加-->
    <property>
        <name>hadoop.proxyuser.root.hosts</name>
        <value>*</value>
    </property>
    <property>
        <name>hadoop.proxyuser.root.groups</name>
        <value>*</value>
    </property>
    <!--添加支持Lzo索引，可不需要-->
    <property>
        <name>io.compression.codecs</name>
        <value>
            org.apache.hadoop.io.compress.GzipCodec,
            org.apache.hadoop.io.compress.DefaultCodec,
            org.apache.hadoop.io.compress.BZip2Codec,
            org.apache.hadoop.io.compress.SnappyCodec,
            com.hadoop.compression.lzo.LzoCodec,
            com.hadoop.compression.lzo.LzopCodec
        </value>
    </property>
    <property>
        <name>io.compression.codec.lzo.class</name>
        <value>com.hadoop.compression.lzo.LzoCodec</value>
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
    <property>
        <name>mapreduce.application.classpath</name>
        <value>/home/softwares/hadoop-3.1.3/share/hadoop/mapreduce/*,/home/softwares/hadoop-3.1.3/share/hadoop/mapreduce/lib/*</value>
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

### works

```shell
#将原有的删除，添加以下内容
hadoop01
hadoop02
hadoop03
```

*至此，Hadoop配置完毕*

```shell
#执行命令
hadoop namenode -format	#只在第一次运行的时候进行格式化
start-all.sh	#启动命令
stop-all.sh	#关闭命令
jps	#查看是否启动成功
```



## Zookeeper

把Zookeeper的压缩包上传至虚拟机下并使用**tar -xvf**命令解压至/**home/softwares**目录下

进入