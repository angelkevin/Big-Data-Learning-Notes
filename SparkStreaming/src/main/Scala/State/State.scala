package State

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

object State {

  def main(args: Array[String]): Unit = {
    def main(args: Array[String]) {
      val conf = new
          SparkConf().setMaster("local[2]").setAppName("NetworkWordCount")
      val ssc = new StreamingContext(conf, Seconds(3))
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
