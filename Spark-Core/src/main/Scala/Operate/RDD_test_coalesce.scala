package Operate

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object RDD_test_coalesce {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("Map")
    val sc = new SparkContext(sparkConf)
    val rdd: RDD[Int] = sc.makeRDD(
      List(1, 2, 3, 4,5,6,7,8),4
    )
    //coalesce减少分区数量,防止资源浪费
    //不会将数据打乱重新组合,只是缩减分区
    //如果想让数据均衡一些可以进行shuffle处理,没有规律
    //def coalesce(numPartitions: Int, shuffle: Boolean = false)
    rdd.coalesce(2,true).saveAsTextFile("output")
    //coalesce可以增加分区数量,如果不shuffle将没有意义
    //扩大分区可以使用repartition
    rdd.repartition(8)
    //coalesce(numPartitions, shuffle = true)
    rdd.coalesce(8,true).saveAsTextFile("output1")

    sc.stop()
  }

}
