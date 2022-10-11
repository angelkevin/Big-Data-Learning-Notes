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
