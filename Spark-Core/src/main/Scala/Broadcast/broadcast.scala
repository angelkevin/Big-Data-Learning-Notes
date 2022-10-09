package Broadcast

import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable

object broadcast {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("Map")
    val sc = new SparkContext(sparkConf)
    val rdd: RDD[(String, Int)] = sc.makeRDD(
      List(("a", 1), ("b", 1)))


    val map: mutable.Map[String, Int] = mutable.Map(("a", 1), ("b", 1))

    //闭包数据,都是以task为单位发送,每个任务中包含闭包数据,一个Executor中有大量的重复数据,并会占用大量的内存,
    //Executor其实h是一个JVM,所以在启动时,会分配内存,完全可以将任务中的闭包数据放置在Executor的内存中达到共享的目的
    //spark中的广播变量不能修改:是分布式只读变量

    val bc: Broadcast[mutable.Map[String, Int]] = sc.broadcast(map)

    rdd.map {
      case (w, c) => {
        val i: Int = bc.value.getOrElse(w, 0)
        (w, (c, i))

      }
    }.collect().foreach(println)

    sc.stop()

  }

}
