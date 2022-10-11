# Kafka

> **传统定义**:Kafka是一个分布式的基于发布/订阅模式的消息队列,主要应用于大数据实时处理领域

> **发布订阅**:消息的发布者不会将消息直接发送给特定的订阅者,而是将发布的消息分为不同的类别,订阅者只接收感兴趣的消息



## 传统的消息队列的应用场景

- **缓存/消峰**:有助于控制和优化数据流经过系统的速度,解决生产消息和消费消息处理速度不一样的情况
- **解耦**:允许独立的扩展或修改两边的处理过程,只要确保他们遵守同样的接口约束

![image-20221010213439158](C:\Users\22154\AppData\Roaming\Typora\typora-user-images\image-20221010213439158.png)

- **异步通信**:允许用户把一个消息放入队列,但并不去处理他,然后在需要的时候再去处理他们

## 消息队列的两种模式

### 点对点

![image-20221010214239582](C:\Users\22154\AppData\Roaming\Typora\typora-user-images\image-20221010214239582.png)

### 发布订阅

![image-20221010214349195](C:\Users\22154\AppData\Roaming\Typora\typora-user-images\image-20221010214349195.png)

**同一消费者组内不允许多个消费者同时消费同一分区的消息,而不同的消费者组可以同时消费同一分区消息**

## Kafka 基础架构

<img src="C:\Users\22154\AppData\Roaming\Typora\typora-user-images\image-20221010215347752.png" alt="image-20221010215347752" style="zoom:150%;" />

## 命令

| 参数 | 描述 |
| :--: | :--: |
| --bootstrap-server | 连接的 Kafka Broker 主机名称和端口号。 |
|--topic | 操作的 topic 名称。|
|--create |创建主题。 |
|--delete |删除主题。 |
|--alter| 修改主题。 |
|--list |查看所有主题。|
|--describe| 查看主题详细描述。|
|--partitions|  设置分区数。 |
|--replication-factor| 设置分区副本。|
|--config  |更新系统默认的配置。|

``` shell
cd /usr/local/kafka/bin/ ./kafka-server-start.sh -daemon config/server.properties

#启动kafka
bin/kafka-topics.sh --zookeeper hadoop01:2181 --topic first --create --partitions 1 --replication-factor 3

# 创建主题（topic），partitions 分区数，replication-factor 副本数；分区只能加不能减
kafka-console-producer.sh --broker-list hadoop01:9092 --topic first
# 创建生产者
kafka-console-consumer.sh --bootstrap-server hadoop02:9092 --topic first bin/kafka-server-start.sh config/server.properties 
# 创建消费者
```

## 生产者分区

### 分区器的好处

> （1）便于合理使用存储资源，每个Partition在一个Broker上存储，可以把海量的数据按照分区切割成一 块一块数据存储在多台Broker上。合理控制分区的任务，可以实现负载均衡的效果。 
>
> （2）提高并行度，生产者可以以分区为单位发送数据；消费者可以以分区为单位进行消费数据。

