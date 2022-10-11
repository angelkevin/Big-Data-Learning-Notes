import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.{Duration, Seconds, StreamingContext}

object wc {
  def main(args: Array[String]): Unit = {

    //TODO 创建对象
    //StreamingContext创建时需要传递两个参数
    //第一个表示环境配置
    val streaming: SparkConf = new SparkConf().setMaster("local[*]").setAppName("Streaming")
    //第二个表示批量处理周期(采集周期)
    val ssc = new StreamingContext(streaming,Seconds(3))



    //TODO 逻辑处理
    //获取端口数据
    val lines: ReceiverInputDStream[String] = ssc.socketTextStream("192.168.170.133", 9999)
    val value: DStream[String] = lines.flatMap(_.split(" "))
    value.map((_,1)).reduceByKey(_+_).print()



    //TODO 关闭环境
    //启动采集器
    ssc.start()
    //等待采集器的关闭
    ssc.awaitTermination()
    //ssc.stop()


  }

}
