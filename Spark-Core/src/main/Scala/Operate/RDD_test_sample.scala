package Operate

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object RDD_test_sample {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("Map")
    val sc = new SparkContext(sparkConf)
    val rdd: RDD[Int] = sc.makeRDD(
      List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12), 2
    )

    //sample需要传入三个参数
    //第一个表示数据是否放回,true放回,false丢弃
    //第二个表示数据源中的数据被抽取的概率
    //第三个表示抽取时随机种子,如果不传递,那么使用的是当前的系统时间
    rdd.sample(false, 0.4, 2).collect().foreach(println)

  }
}










