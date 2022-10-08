package Operate

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object RDD_test_Map_par {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("Map")
    val sc = new SparkContext(sparkConf)
    var rdd: RDD[Int] = sc.makeRDD(
      List(1, 2, 3, 4),3
    )

    rdd.map(
      (num: Int) => {
        println(">>>>>>>"+num)
        num * 2
      }
    ).collect()

    rdd.map(
      (num: Int) => {
        println(num)
        num*2
      }
    )


    sc.stop()
  }

}
