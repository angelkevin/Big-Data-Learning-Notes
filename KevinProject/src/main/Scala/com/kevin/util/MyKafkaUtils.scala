package com.kevin.util

import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}

import java.util.Properties
import scala.collection.mutable

/**
 * kafka工具类
 */
object MyKafkaUtils {

  private val consumerConfigs = mutable.Map[String, Object](
    //集群位置
    //ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> "hadoop01:9092,hadoop02:9092,hadoop03:9092",
    //ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> MyPropsUtils("BOOTSTRAP_SERVERS_CONFIG"),
    ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> MyPropsUtils(MyConfig.BOOTSTRAP_SERVERS_CONFIG),
    //kv反序列化器
    ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG -> "org.apache.kafka.common.serialization.StringDeserializer",
    ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG -> "org.apache.kafka.common.serialization.StringDeserializer",
    //offset提交
    ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG -> "true",
    //offset重置
    ConsumerConfig.AUTO_OFFSET_RESET_CONFIG -> "latest"
    //.....

  )

  /**
   * 基于SparkSteaming消费数据
   */
  def GetKafkaDStream(ssc: StreamingContext, topic: String, groupId: String): InputDStream[ConsumerRecord[String, String]] = {
    consumerConfigs.put(ConsumerConfig.GROUP_ID_CONFIG, groupId)
    //
    val kafkaDStream: InputDStream[ConsumerRecord[String, String]] = KafkaUtils.createDirectStream(ssc, LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](Array(topic), consumerConfigs))
    kafkaDStream
  }


  /**
   * 生产数据
   */
  val producer: KafkaProducer[String, String] = createProducer()

  def createProducer(): KafkaProducer[String, String] = {
    val properties = new Properties()
    properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, MyPropsUtils("BOOTSTRAP_SERVERS_CONFIG"))
    properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
    properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")

    //acks
    properties.put(ProducerConfig.ACKS_CONFIG, "all")
    //batch.size
    //linger.ms
    //retries
    //幂等性
    properties.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true")
    val producer = new KafkaProducer[String, String](properties)
    producer
  }

  /**
   * 发送方法,默认粘性分区
   *
   * @param topic
   * @param msg
   */
  def send(topic: String, msg: String): Unit = {
    producer.send(new ProducerRecord[String, String](topic, msg))
  }

}
