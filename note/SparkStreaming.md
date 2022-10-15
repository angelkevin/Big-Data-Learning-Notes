# ![image-20221013152620536](D:\java\img\image-20221013152620536.png)SparkStreaming

> 准实时,微批次

Spark Streaming 用于流式数据的处理。Spark Streaming 支持的数据输入源很多，例如：Kafka、 Flume、Twitter、ZeroMQ 和简单的 TCP 套接字等等。数据输入后可以用 Spark 的高度抽象原语 如：map、reduce、join、window 等进行运算。而结果也能保存在很多地方，如 HDFS，数据库等。 

和 Spark 基于 RDD 的概念很相似，Spark Streaming 使用离散化流(discretized stream)作为抽象表示，叫作 DStream。DStream 是随时间推移而收到的数据的序列。在内部，每个时间区间收 到的数据都作为 RDD 存在，而 DStream 是由这些 RDD 所组成的序列(因此得名“离散化”)。所以 简单来将，DStream 就是对RDD 在实时数据处理场景的一种封装。

## 整体架构图

![image-20221013152607883](D:\java\img\image-20221013152607883.png)



## SparkStreaming 架构图

![image-20221010190007411](C:\Users\22154\AppData\Roaming\Typora\typora-user-images\image-20221010190007411.png)



## Dstream创建

### RDD队列

测试过程中，可以通过使用 ssc.queueStream(queueOfRDDs)来创建DStream，每一个推送到这个队列中的RDD，都会作为一个DStream 处理。

```scala
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}

import scala.collection.mutable

object queueStream{
  def main(args: Array[String]) {
    //1.初始化 Spark 配置信息
    val conf = new SparkConf().setMaster("local[*]").setAppName("RDDStream")
    //2.初始化 SparkStreamingContext
    val ssc = new StreamingContext(conf, Seconds(3))
    //3.创建 RDD 队列
    val rddQueue = new mutable.Queue[RDD[Int]]()
    //4.创建 QueueInputDStream
    val inputStream = ssc.queueStream(rddQueue,oneAtATime = false)
    //5.处理队列中的 RDD 数据
    val mappedStream = inputStream.map((_,1))
    val reducedStream = mappedStream.reduceByKey(_ + _)
    //6.打印结果
    reducedStream.print()
    //7.启动任务
    ssc.start()
    //8.循环创建并向 RDD 队列中放入 RDD
    for (i <- 1 to 5) {
      rddQueue += ssc.sparkContext.makeRDD(1 to 300, 10)
      Thread.sleep(2000)
    }
    ssc.awaitTermination()
  }
}

```

### 自定义数据源

需要继承Receiver，并实现 onStart、onStop 方法来自定义数据源采集。

```scala
package DIYSource

import org.apache.spark.SparkConf
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.receiver.Receiver

import scala.util.Random

object Receiver {

  def main(args: Array[String]): Unit = {
    val streaming: SparkConf = new SparkConf().setMaster("local[*]").setAppName("Streaming")
    //第二个表示批量处理周期(采集周期)
    val ssc = new StreamingContext(streaming, Seconds(3))
    ssc.receiverStream(new MyReceiver()).print()
    ssc.start()
    ssc.awaitTermination()

  }

  /**
   * 自定义数据采集器
   * 1,继承receiver,定义泛型,传递参数
   * 重新方法
   */
  class MyReceiver extends Receiver[String](StorageLevel.MEMORY_ONLY) {
    private var flag = true

    override def onStart(): Unit = {
      new Thread(new Runnable {
        override def run(): Unit = {
          while (flag) {
            val massage = new Random().nextInt(10).toString
            store(massage)
            Thread.sleep(500)
          }
        }
      }).start()
    }

    override def onStop(): Unit = {
      flag = false
    }
  }

}

```

### kafka数据源

```scala
package kafka

import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, InputDStream, ReceiverInputDStream}
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}

object Kafka {
  def main(args: Array[String]): Unit = {


    val streaming: SparkConf = new SparkConf().setMaster("local[*]").setAppName("Streaming")

    val ssc = new StreamingContext(streaming,Seconds(3))

    //3.定义 Kafka 参数
    val kafkaPara: Map[String, Object] = Map[String, Object](
      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG ->
        "hadoop01:9092,hadoop02:9092,hadoop03:9092",
      ConsumerConfig.GROUP_ID_CONFIG -> "atguigu",//消费者组
      "key.deserializer" ->
        "org.apache.kafka.common.serialization.StringDeserializer",
      "value.deserializer" ->
        "org.apache.kafka.common.serialization.StringDeserializer"
    )


    val kafkaStream: InputDStream[ConsumerRecord[String, String]] = KafkaUtils.createDirectStream[String, String](
      ssc,
      //采集的节点和计算节点的关系,由框架来匹配
      LocationStrategies.PreferConsistent,
      //消费者策略传入主题
      ConsumerStrategies.Subscribe[String, String](Set("test"), kafkaPara)
    )

    kafkaStream.map((_: ConsumerRecord[String, String]).value()).print()

  }

}

```

## DStream 转换

### 无状态转化操作 

![image-20221013154330116](D:\java\img\image-20221013154330116.png)

### 有状态操作

#### UpdateStateByKey 

UpdateStateByKey 原语用于记录历史记录，有时，我们需要在 DStream 中跨批次维护状态(例如流计算中累加wordcount)。针对这种情况，updateStateByKey()为我们提供了对一个状态变量的访问，用于键值对形式的 DStream。给定一个由(键，事件)对构成的 DStream，并传递一个指定如何根据新的事件更新每个键对应状态的函数，它可以构建出一个新的 DStream，其内部数据为(键，状态) 对。 updateStateByKey() 的结果会是一个新的DStream，其内部的RDD 序列是由每个时间区间对应的(键，状态)对组成的。 

#### Window Operations 

可以设置窗口的大小和滑动窗口的间隔来动态的获取当前Steaming 的允许状态。所有基于窗口的操作都需要两个参数，分别为窗口时长以及滑动步长。 

```scala
package State

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

object State {

  def main(args: Array[String]): Unit = {
    def main(args: Array[String]) {
      val conf = new
          SparkConf().setMaster("local[2]").setAppName("NetworkWordCount")
      val ssc = new StreamingContext(conf, Seconds(3))
       //    要设置检查点的路径
      ssc.checkpoint("./ck")

      val lines = ssc.socketTextStream(" linux1 ",9999)

      val words = lines.flatMap(_.split(" "))

      val pairs = words.map(word => (word, 1))
//      滑动窗口，窗口时长和滑动步长
//      窗口可以滑动，但是可能出现重复数据计算，可以设置滑动步长
      val wordCounts = pairs.reduceByKeyAndWindow((a: Int, b: Int) => (a + b), Seconds(12), Seconds(6))
      wordCounts.print()
      ssc.start()
      ssc.awaitTermination()

    }

  }
}

```



关于Window 的操作还有如下方法： 
（1）window(windowLength, slideInterval): 基于对源DStream 窗化的批次进行计算返回一个新的Dstream； 

（2）countByWindow(windowLength, slideInterval): 返回一个滑动窗口计数流中的元素个数；

（3）reduceByWindow(func, windowLength, slideInterval): 通过使用自定义函数整合滑动区间流元素来创建一个新的单元素流； 

（4）reduceByKeyAndWindow(func, windowLength, slideInterval, [numTasks]): 当在一个(K,V)对的DStream 上调用此函数，会返回一个新(K,V)对的DStream，此处通过对滑动窗口中批次数据使用reduce 函数来整合每个 key 的value 值。 

（5）reduceByKeyAndWindow(func, invFunc, windowLength, slideInterval, [numTasks]): 这个函数是上述函数的变化版本，每个窗口的reduce 值都是通过用前一个窗的 reduce 值来递增计算。通过reduce 进入到滑动窗口数据并”反向 reduce”离开窗口的旧数据来实现这个操作。一个例子是随着窗口滑动对keys 的“加”“减”计数。通过前边介绍可以想到，这个函数只适用于”可逆的reduce 函数”，也就是这些 reduce 函数有相应的”反 reduce”函数(以参数 invFunc 形式
传入)。如前述函数，reduce 任务的数量通过可选参数来配置。 **当窗口的范围比较大，但是滑动幅度比较小，那么就可以采用增加数据和减少数据的方式无需重复计算**

## DStream 输出

➢ print()：在运行流程序的驱动结点上打印 DStream 中每一批次数据的最开始 10 个元素。这 用于开发和调试。在 Python API 中，同样的操作叫 print()。 

➢ saveAsTextFiles(prefix, [suffix])：以 text 文件形式存储这个 DStream 的内容。每一批次的存 储文件名基于参数中的 prefix 和 suffix。”prefix-Time_IN_MS[.suffix]”。 

➢ saveAsObjectFiles(prefix, [suffix])：以 Java 对象序列化的方式将 Stream 中的数据保存为 SequenceFiles . 每一批次的存储文件名基于参数中的为"prefix-TIME_IN_MS[.suffix]". Python 中目前不可用。 

➢ saveAsHadoopFiles(prefix, [suffix])：将 Stream 中的数据保存为 Hadoop files. 每一批次的存 储文件名基于参数中的为"prefix-TIME_IN_MS[.suffix]"。Python API 中目前不可用。 

➢ foreachRDD(func)：这是最通用的输出操作，即将函数 func 用于产生于 stream 的每一个 RDD。其中参数传入的函数 func 应该实现将每一个 RDD 中数据推送到外部系统，如将 RDD 存入文件或者通过网络将其写入数据库。
