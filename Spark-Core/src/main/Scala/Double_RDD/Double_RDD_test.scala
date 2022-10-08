package Double_RDD

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD

object Double_RDD_test {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("Map")
    val sc = new SparkContext(sparkConf)
    val rdd: RDD[Int] = sc.makeRDD(
      List(1, 2, 4, 3, 5)
    )
    val rdd1: RDD[Int] = sc.makeRDD(
      List(5, 8, 7, 6, 4)
    )

    //交集
    println(rdd.intersection(rdd1).collect().mkString("Array(", ", ", ")"))

    //并集
    println(rdd.union(rdd1).collect().mkString("Array(", ", ", ")"))

    //差集
    println(rdd.subtract(rdd1).collect().mkString("Array(", ", ", ")"))

    //拉链
    println(rdd.zip(rdd1).collect().mkString("Array(", ", ", ")"))

    sc.stop()

  }

}
