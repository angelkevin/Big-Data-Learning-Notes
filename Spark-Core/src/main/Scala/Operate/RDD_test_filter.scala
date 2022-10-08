package Operate

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object RDD_test_filter {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("Map")
    val sc = new SparkContext(sparkConf)
    val rdd: RDD[Int] = sc.makeRDD(
      List(1, 2, 3, 4), 2
    )
    //将数据根据指定的规则进行筛选过滤,符合数据的保留,不符合的丢弃.当数据进行筛选过滤后,分区不变,可能会数据倾斜
    val FilterRDD: RDD[Int] = rdd.filter(num => num % 2 != 0)

    FilterRDD.collect().foreach(println)
  }
}










