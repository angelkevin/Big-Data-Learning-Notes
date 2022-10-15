package Example

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer

import java.util.Properties
import scala.collection.mutable.ListBuffer
import scala.util.Random

object data {
  def main(args: Array[String]): Unit = {
    val properties = new Properties()
    properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.170.130:9092")

    properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName)
    properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName)
    val producer = new KafkaProducer[String, String](properties)
    while (true){
      mockdata().foreach(data => {
        val value = new ProducerRecord[String, String]("test", data)
        producer.send(value)

      })
      Thread.sleep(200)
    }

  }

  def mockdata() = {
    val list: ListBuffer[String] = ListBuffer[String]()
    val areaList: ListBuffer[String] = ListBuffer[String]("华北", "华中", "华南", "华东", "华西")
    val cityList: ListBuffer[String] = ListBuffer[String]("北京", "上海", "广州", "深圳", "重庆")
    for (i <- 1 to 30){
      val area = areaList(new Random().nextInt(3))
      val city = cityList(new Random().nextInt(3))
      var userid = new Random().nextInt(6)
      var adid = new Random().nextInt(6)
      list.append(s"${System.currentTimeMillis()} ${area} ${city} ${userid} ${adid}")
    }
    list
  }

}
