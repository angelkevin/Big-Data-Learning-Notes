package Operate

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object RDD_test_glom {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("Map")
    val sc = new SparkContext(sparkConf)
    val rdd: RDD[Int] = sc.makeRDD(
      List(1, 2, 3, 4), 2
    )
    //将同一个分区直接转换成相同类型的内存数组进行处理,分区数量不变
    val value: RDD[Array[Int]] = rdd.glom()
    println(value.map(
      data => data.max
    ).collect().sum)



  }

}
