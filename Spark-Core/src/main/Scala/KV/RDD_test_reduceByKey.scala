package KV

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object RDD_test_reduceByKey {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("Map")
    val sc = new SparkContext(sparkConf)
    val rdd: RDD[Int] = sc.makeRDD(
      List(1, 2, 2, 3, 5)
    )
    val rdd1: RDD[(Int, Int)] = rdd.map((_, 1))
    //两两计算
    rdd1.reduceByKey((num, num1) => num + num1).collect().foreach(print)


    sc.stop()

  }

}
