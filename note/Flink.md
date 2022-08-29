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

# 窗口

>- 时间窗口
>- 计数窗口

>- 滚动窗口（Tumbling Window）：对数据进行均匀切片，时间或计数，定义窗口有多大
>- 滑动窗口（Sliding Window）：除去窗口大小，还有滑动步长，窗口会出现重叠， 
>- 会话窗口（Session Window）：基于会话对数据进行分析，设施会话超时时间
>- 全局窗口：自定义触发器
>
>
