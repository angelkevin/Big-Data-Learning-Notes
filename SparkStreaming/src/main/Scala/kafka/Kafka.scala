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
