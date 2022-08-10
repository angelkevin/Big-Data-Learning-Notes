# Flink

```shell
#启动Flink
start-cluster.sh
#命令行提交job
flink run -m centos01:8081 -c FLink.StreamWordCounts -p 2 ./******.jar
#命令行取消job
flink cancel JobID
```



```shell
#yarn模式
yarn-session.sh  -nm test
-nm		#任务名字
-d		#分离模式
-jm		#内存大小
-qu		#YARN队列名
-tm		#每个testmanager所使用的
```



## 支持的数据类型

>![image-20220810214154408](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20220810214154408.png)
