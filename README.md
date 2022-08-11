[![OSCS Status](https://www.oscs1024.com/platform/badge/angelkevin/java.svg?size=small)](https://www.oscs1024.com/project/angelkevin/java?ref=badge_small)


---

# 大数据开发环境介绍
## 这是我自己搭建的开发环境，虽然都是单节点模式，在学习的情况下还是足够使用的

如果大家需要的话可以点[此链接](https://pan.baidu.com/s/1h70_3xJaMJtiggNM_X-UdQ?pwd=zkw6)下载哦！欢迎白嫖，如果可以给我点一个⭐start⭐的话我也感激不尽😊。


此虚拟机基于VMware以及centos7构建完成，包含hadoop,kafka,flume,hive,mysql,sqoop,zookeepr,jdk,hive on spark，各个版本都是兼容的，本人亲测有效。

---

hadoop 已经完成初始化；hive为本地模式；防火墙已经关闭；root密码是：root；MySQL的用户名为root，密码是：123456；用户hadoop的密码为：hadoop。

---

虚拟网络编辑如下

![image](https://user-images.githubusercontent.com/71579923/182029086-8ba049e8-e36c-470e-9487-f6ed89e1b4a7.png)

NAT设置如下

![image](https://user-images.githubusercontent.com/71579923/182029123-879ac17d-261b-4c95-8581-046800d46536.png)


vmware虚拟网络格式为NAT模式


![image](https://user-images.githubusercontent.com/71579923/182029072-5a4f92d4-876d-4295-94d8-71e251b513ee.png)


---

在系统中新建网络为IPV4并设置为手动

地址：192.168.170.133

网络掩码：255.255.255.0

网关：192.168.170.2

DNS：192.168.170.2

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
export SPARK_HOME=/opt/softwares/spark-3.0.0-bin-hadoop3.2
export PATH=$PATH:$SPARK_HOME/bin
```
如果有需要帮忙的地方可以给我发邮件📧<2215408861@qq.com>

安装过程参考书籍《Hadoop大数据开发实战》 张伟洋著


# 一个用java刷题的大学生，一个学大数据的大学生
### 第二个文件夹是存放自己刷力扣的答案
### 第一个文件夹是存放自己学大数据生态的一些代码
*因为都是java语言就懒得分类了*
