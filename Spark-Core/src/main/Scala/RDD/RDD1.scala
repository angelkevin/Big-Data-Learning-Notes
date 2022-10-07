package RDD

import org.apache.spark.{SparkConf, SparkContext}

object RDD1 {
  def main(args: Array[String]): Unit = {
    //TODO 创建环境
    val sparkconf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("File")
    val sc = new SparkContext(sparkconf)
  }

}
