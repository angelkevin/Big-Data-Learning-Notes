package RDD

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object RDD_File {
  def main(args: Array[String]): Unit = {
    //TODO 创建环境
    val sparkconf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("File")
    val sc = new SparkContext(sparkconf)
    //从文件中读取数据
    val rdd: RDD[String] = sc.textFile("D:\\java\\Spark\\data\\1.txt")
    //从目录中读取
    sc.textFile("D:\\java\\Spark\\data").collect().foreach(println)
    //正则匹配
    sc.textFile("D:\\java\\Spark\\data\\1*.txt").collect().foreach(println)
    rdd.collect().foreach(println)
    //TODO 关闭环境
    sc.stop()
  }
}
