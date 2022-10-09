package KV

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object RDD_test_coGroup {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("Map")
    val sc = new SparkContext(sparkConf)
    val rdd = sc.makeRDD(List(("a", 1), ("b", 1), ("b", 3)), 2)

    val rdd1 = sc.makeRDD(List(("a", 2), ("b", 2), ("a", 3)), 2)


    val value: RDD[(String, (Iterable[Int], Iterable[Int]))] = rdd.cogroup(rdd1)
    value.collect().foreach(println)

    sc.stop()

  }

}
