package Operate

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object RDD_test_Groupby {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("Map")
    val sc = new SparkContext(sparkConf)
    val rdd: RDD[Int] = sc.makeRDD(
      List(1, 2, 3, 4), 2
    )
    val tuples: RDD[(Int, Iterable[Int])] = rdd.groupBy(data => data % 2)

    tuples.collect().foreach(println)
  }
}










