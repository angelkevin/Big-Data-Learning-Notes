package Broadcast

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object broadcast {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("Map")
    val sc = new SparkContext(sparkConf)
    val rdd: RDD[Int] = sc.makeRDD(
      List(1, 2, 4, 3, 5)
    )
    val rdd1: RDD[Int] = sc.makeRDD(
      List(5, 8, 7, 6, 4)
    )



    sc.stop()

  }

}
