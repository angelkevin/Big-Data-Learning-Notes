# SparkStreaming

> 准实时,微批次

Spark Streaming 用于流式数据的处理。Spark Streaming 支持的数据输入源很多，例如：Kafka、 Flume、Twitter、ZeroMQ 和简单的 TCP 套接字等等。数据输入后可以用 Spark 的高度抽象原语 如：map、reduce、join、window 等进行运算。而结果也能保存在很多地方，如 HDFS，数据库等。 

和 Spark 基于 RDD 的概念很相似，Spark Streaming 使用离散化流(discretized stream)作为抽象表示，叫作 DStream。DStream 是随时间推移而收到的数据的序列。在内部，每个时间区间收 到的数据都作为 RDD 存在，而 DStream 是由这些 RDD 所组成的序列(因此得名“离散化”)。所以 简单来将，DStream 就是对RDD 在实时数据处理场景的一种封装。

## 整体架构图

![image-20221010185941631](D:\java\img\image-20221010185941631.png)

## SparkStreaming 架构图

![image-20221010190007411](C:\Users\22154\AppData\Roaming\Typora\typora-user-images\image-20221010190007411.png)



