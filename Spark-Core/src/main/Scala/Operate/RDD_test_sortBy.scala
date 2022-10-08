package Operate

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object RDD_test_sortBy {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("Map")
    val sc = new SparkContext(sparkConf)
    val rdd: RDD[Int] = sc.makeRDD(
      List(1, 2, 4, 3, 5, 8, 7, 6), 4
    )
    //默认升序,不会改变分区,会存在shuffle组合
    //false是降序
    rdd.sortBy(num => num).collect().foreach(print)


    sc.stop()
  }

}
