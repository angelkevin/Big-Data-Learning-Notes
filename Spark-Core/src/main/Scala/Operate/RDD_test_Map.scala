package Operate

import org.apache.spark
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object RDD_test_Map {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local").setAppName("Map")
    val sc = new SparkContext(sparkConf)
    var rdd: RDD[Int] = sc.makeRDD(
      List(1,2,3,4)
    )
    //转换函数
    def Map_Function(num: Int): Int = {
      num * 2
    }

    //调用自定义函数
    rdd.map(Map_Function).collect().foreach(println)
    //使用Lambada表达式
    rdd.map((data: Int) => data *2).collect().foreach(println)
    //至简原则
    rdd.map(_*2).collect().foreach(println)



    sc.stop()
  }

}
