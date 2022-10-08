package Operate

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object Flatmap {

  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("Map")
    val sc = new SparkContext(sparkConf)
    val value = sc.makeRDD(List(
      List(1, 2, 3, 4), List(5, 6, 7, 8))
    )
    val value1: RDD[Int] = value.flatMap(
      (list: Seq[Int]) => {
        println(list)
        list
      }
    )


    val rdd : RDD[String] =sc.makeRDD(List("Hello World","Fuck you"))

    val value2: RDD[String] = rdd.flatMap(
      _.split(" ")
    )

    value2.collect().foreach(println)

    value1.collect().foreach(println)



  }

}
