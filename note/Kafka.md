# Kafka

> **传统定义**:Kafka是一个分布式的基于发布/订阅模式的消息队列,主要应用于大数据实时处理领域

> **发布订阅**:消息的发布者不会将消息直接发送给特定的订阅者,而是将发布的消息分为不同的类别,订阅者只接收感兴趣的消息



## 传统的消息队列的应用场景

- **缓存/消峰**:有助于控制和优化数据流经过系统的速度,解决生产消息和消费消息处理速度不一样的情况
- **解耦**:允许独立的扩展或修改两边的处理过程,只要确保他们遵守同样的接口约束

![image-20221010213439158](D:\java\img\image-20221010213439158.png)

- **异步通信**:允许用户把一个消息放入队列,但并不去处理他,然后在需要的时候再去处理他们

## 消息队列的两种模式

### 点对点

![image-20221010214239582](D:\java\img\image-20221010214239582.png)

### 发布订阅

![image-20221010214349195](C:\Users\22154\AppData\Roaming\Typora\typora-user-images\image-20221010214349195.png)

**同一消费者组内不允许多个消费者同时消费同一分区的消息,而不同的消费者组可以同时消费同一分区消息**

## Kafka 基础架构

<img src="D:\java\img\image-20221010215347752.png" alt="image-20221010215347752" style="zoom:150%;" />

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

### 自定义生产者

#### 异步发送

```java
package producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class CustomProducer {
    public static void main(String[] args) {
        //1 创建kafka生产者对象
        Properties properties = new Properties();
        //连接
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "hadoop01:9092,hadoop02:9092");
        //指定kv的序列化
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(properties);
        //2 发送数据
        for (int i = 0; i < 10; i++) {
            kafkaProducer.send(new ProducerRecord<>("first", "hello" + i));

        }

        //3 关闭资源

        kafkaProducer.close();
    }
}

```

#### 异步发送带回调

```java
package producer;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import java.util.Properties;

//带回调
public class CustomProducerCallback {
    public static void main(String[] args) {
        //1 创建kafka生产者对象
        Properties properties = new Properties();
        //连接
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "hadoop01:9092,hadoop02:9092");
        //指定kv的序列化
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(properties);
        //2 发送数据
        for (int i = 0; i < 10; i++) {
            kafkaProducer.send(new ProducerRecord<>("first", "hello"), (new Callback() {
                @Override
                public void onCompletion(RecordMetadata metadata, Exception exception) {
                    if (exception == null) {
                        System.out.println("主题:" + metadata.topic() + "分区:" + metadata.partition());
                    }

                }
            }));
        }


        //3 关闭资源

        kafkaProducer.close();
    }
}

```

#### 同步发送

```java
package producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
//同步
public class CustomProducerSync {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //1 创建kafka生产者对象
        Properties properties = new Properties();
        //连接
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "hadoop01:9092,hadoop02:9092");
        //指定kv的序列化
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(properties);
        //2 发送数据 调用get函数
        for (int i = 0; i < 10; i++) {
            kafkaProducer.send(new ProducerRecord<>("first", "hello" + i)).get();

        }

        //3 关闭资源

        kafkaProducer.close();
    }
}

```

## 生产者分区

### 分区器的好处

> （1）便于合理使用存储资源，每个Partition在一个Broker上存储，可以把海量的数据按照分区切割成一 块一块数据存储在多台Broker上。合理控制分区的任务，可以实现负载均衡的效果。 
>
> （2）提高并行度，生产者可以以分区为单位发送数据；消费者可以以分区为单位进行消费数据。

## 默认的分区器 DefaultPartitioner

> （1）指明partition的情况下，直 接将指明的值作为partition值； 例如partition=0，所有数据写入 分区0 
>
> （2）没有指明partition值但有key的情况下，将key的hash值与topic的 partition数进行取余得到partition值； 例如：key1的hash值=5， key2的hash值=6 ，topic的partition数=2，那 么key1 对应的value1写入1号分区，key2对应的value2写入0号分区。 
>
> （3）既没有partition值又没有key值的情况下，Kafka采用Sticky Partition（黏性分区器），会随机选择一个分区，并尽可能一直 使用该分区，待该分区的batch已满或者已完成，Kafka再随机一个分区进行使用（和上一次的分区不同）。 例如：第一次随机选择0号分区，等0号分区当前批次满了（默认16k）或者linger.ms设置的时间到， Kafka再随机一个分区进 行使用（如果还是0会继续随机）。

```java
package producer;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import java.util.Properties;

//带回调
public class CustomProducerCallbackPartitions {
    public static void main(String[] args) {
        //1 创建kafka生产者对象
        Properties properties = new Properties();
        //连接
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "hadoop01:9092,hadoop02:9092");
        //指定kv的序列化
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(properties);
        //2 发送数据
        //(topic,partitions,key,value)
        for (int i = 0; i < 10; i++) {
            kafkaProducer.send(new ProducerRecord<>("test", 2,"", "hello"), (new Callback() {
                @Override
                public void onCompletion(RecordMetadata metadata, Exception exception) {
                    if (exception == null) {
                        System.out.println("主题:" + metadata.topic() + "分区:" + metadata.partition());
                    }
                }
            }));
        }
        //3 关闭资源
        kafkaProducer.close();
    }
}

```

## 自定义分区器

### 分区器

```java
package producer;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

import java.util.Map;

public class MyPartition implements Partitioner {
    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {

        String massage = value.toString();
        if (massage.contains("pornhub")){
            return 0;
        }
        else return 1;

    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> configs) {

    }
}

```

### 分区器的使用

> 关联分区器  properties. Put(ProducerConfig.PARTITIONER_CLASS_CONFIG,"producer.MyPartition");

```java
package producer;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import java.util.Properties;

//带回调
public class CustomProducerCallbackPartitions {
    public static void main(String[] args) {
        //1 创建kafka生产者对象
        Properties properties = new Properties();
        //连接
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "hadoop01:9092,hadoop02:9092");
        //指定kv的序列化
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        //关联自定义分区器
        properties.put(ProducerConfig.PARTITIONER_CLASS_CONFIG,"producer.MyPartition");

        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(properties);
        //2 发送数据
        for (int i = 0; i < 10; i++) {
            kafkaProducer.send(new ProducerRecord<>("test", "hello"), (new Callback() {
                @Override
                public void onCompletion(RecordMetadata metadata, Exception exception) {
                    if (exception == null) {
                        System.out.println("主题:" + metadata.topic() + "分区:" + metadata.partition());
                    }

                }
            }));
        }

        //3 关闭资源
        kafkaProducer.close();
    }
}

```

## 如何提高吞吐量

- batch.size：批次大小，默认16k 

- linger.ms：等待时间，修改为5-100ms 一次拉一个， 来了就走 

- compression.type：压缩snappy

- RecordAccumulator：缓冲区大小，修改为64m

```java
package producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class CustomProducerParameters {
    public static void main(String[] args) {
        // 1 创建kafka生产者对象
        Properties properties = new Properties();
        //连接
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "hadoop01:9092,hadoop02:9092");
        //指定kv的序列化
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        //缓冲区大小,缓冲区大小，默认 32M
        properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        //批次大小,batch.size：批次大小，默认 16K
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        //linger.ms 等待时间，默认 0
        properties.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        //压缩 compression.type：压缩，默认 none，可配置值 gzip、snappy、lz4 和 zstd
        properties.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");

        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(properties);
        // 2 发送数据
        for (int i = 0; i < 100000; i++) {
            kafkaProducer.send(new ProducerRecord<>("test", "i:"+i));

        }

        //3 关闭资源

        kafkaProducer.close();
    }
}

```

## ACK应答级别

Leader维护了一个动态的in-sync replica set（ISR），意为和 Leader保持同步的Follower+Leader集合(leader：0，isr:0,1,2)。 如果Follower长时间未向Leader发送通信请求或同步数据，则 该Follower将被踢出ISR。该时间阈值由replica.lag.time.max.ms参 数设定，默认30s。例如2超时，(leader:0, isr:0,1)。 这样就不用等长期联系不上或者已经故障的节点。



可靠性总结：

 acks=0，生产者发送过来数据就不管了，可靠性差，效率高； 

acks=1，生产者发送过来数据Leader应答，可靠性中等，效率中等； 

acks=-1，生产者发送过来数据Leader和ISR队列里面所有Follwer应答，可靠性高，效率低； 

在生产环境中，acks=0很少使用；acks=1，一般用于传输普通日志，允许丢个别数据；acks=-1，一般用于传输和钱相关的数据， 对可靠性要求比较高的场景。

数据完全可靠条件 = ACK级别设置为-1 + 分区副本大于等于2 + ISR里应答的最小副本数量大于等于2

```java
package producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class CustomProducerAcks {
    public static void main(String[] args) {
        // 1 创建kafka生产者对象
        Properties properties = new Properties();
        //连接
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "hadoop01:9092,hadoop02:9092");
        //指定kv的序列化
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        //设置ACKs
        properties.put(ProducerConfig.ACKS_CONFIG,"1");
        //重试次数
        properties.put(ProducerConfig.RETRIES_CONFIG,10);


        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(properties);
        // 2 发送数据
        for (int i = 0; i < 100; i++) {
            kafkaProducer.send(new ProducerRecord<>("test", "i:"+i));

        }

        //3 关闭资源

        kafkaProducer.close();
    }
}

```

## 数据去重

- 至少一次（At Least Once）= ACK级别设置为-1 + 分区副本大于等于2 + ISR里应答的最小副本数量大于等于2

- 最多一次（At Most Once）= ACK级别设置为0 
-  总结： At Least Once可以保证数据不丢失，但是不能保证数据不重复； At Most Once可以保证数据不重复，但是不能保证数据不丢失。

### 幂等性

**重复数据的判断标准**：具有相同<PID, Partition, SeqNumber>主键的消息提交时，Broker只会持久化一条。其 中PID是Kafka每次重启都会分配一个新的；Partition 表示分区号；Sequence Number是单调自增的。 所以幂等性只能保证的是在单分区单会话内不重复。

> 开启参数 enable.idempotence 默认为 true，false 关闭

### 事务

### 

![image-20221011191800054](D:\java\img\image-20221011191800054-16654957183731.png)



```java
package producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class CustomProducerTransactions {
    public static void main(String[] args) {
        // 1 创建kafka生产者对象
        Properties properties = new Properties();
        //连接
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "hadoop01:9092,hadoop02:9092");
        //指定kv的序列化
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());


        //指定事务ID
        properties.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG,"Transactions");
        
        
        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(properties);

        //初始化事务
        kafkaProducer.initTransactions();
        //开始事务
        kafkaProducer.beginTransaction();

        // 2 发送数据
        try {
            for (int i = 0; i < 100; i++) {
                kafkaProducer.send(new ProducerRecord<>("test", "i:" + i));
            }
            //提交事务
            kafkaProducer.commitTransaction();
        } catch (Exception e) {
            //回滚事务
            kafkaProducer.abortTransaction();
        } finally {
            //3 关闭资源
            kafkaProducer.close();
        }


    }
}

```

## 数据乱序

1）kafka在1.x版本之前保证数据单分区有序，条件如下： max.in.flight.requests.per.connection=1（不需要考虑是否开启幂等性）。 

2）kafka在1.x及以后版本保证数据单分区有序，条件如下： 

（1）未开启幂等性 max.in.flight.requests.per.connection需要设置为1

（2）开启幂等性 max.in.flight.requests.per.connection需要设置小于等于5。 原因说明：因为在kafka1.x以后，启用幂等后，kafka服务端会缓存producer发来的最近5个request的元数据， 故无论如何，都可以保证最近5个request的数据都是有序的。
