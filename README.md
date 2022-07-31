# 大数据开发环境


此虚拟机基于VMware以及centos7构建完成，包含hadoop,kafka,flume,hive,mysql,sqoop,zookeepr,jdk

---

hadoop 已经完成初始化，hive为本地模式。

---

设置网络为IPV4设置为手动

地址：192.168.170.133

网络掩码：255.255.255.0

网关：192.168.170.2

DNS：192.168.170.2

---
在 / 目录下有start.sh这是我写的用来一键启停hadoop，kafka，zookeeper的脚本

启动命令为./start.sh start

停止命令为./start.sh stop

---

本文件为OVF虚拟机文件导入vmware即可使用

---
```shell
/etc/host文件内容为
192.168.170.133 centos01
192.168.170.133 localhost
```
```shell
# 环境变量
/etc/profile文件内容为
export HADOOP_HOME=/opt/softwares/hadoop-3.1.3
export PATH=$PATH:$HADOOP_HOME/bin 
export PATH=$PATH:$HADOOP_HOME/sbin 
export JAVA_HOME=/opt/softwares/jdk1.8.0_212
export PATH=$PATH:$JAVA_HOME/bin
export ZOOKEEPER_HOME=/opt/softwares/apache-zookeeper-3.5.7-bin
export PATH=$PATH:$ZOOKEEPER_HOME/bin:$ZOOKEEPER_HOME/conf
export FLUME_HOME=/opt/softwares/apache-flume-1.9.0-bin
export PATH=$PATH:$FLUME_HOME/bin
export HIVE_HOME=/opt/softwares/apache-hive-3.1.2-bin
export PATH=$PATH:$HIVE_HOME/bin
export SQOOP_HOME=/opt/softwares/sqoop-1.4.6.bin__hadoop-2.0.4-alpha
export PATH=$PATH:$SQOOP_HOME/bin
export KAFKA_HOME=/opt/softwares/kafka_2.11-2.4.1
export PATH=$PATH:$KAFKA_HOME/bin