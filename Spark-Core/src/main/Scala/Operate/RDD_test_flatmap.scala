package Operate

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object RDD_test_flatmap {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("Map")
    val sc = new SparkContext(sparkConf)
    val rdd = sc.makeRDD(List(
      List(1, 2, 3, 4), List(1, 5, 6, 8, 4)), 2
    )

    val value: RDD[Int] = rdd.flatMap(data => data)


    value.collect().foreach(println)
  }
}










