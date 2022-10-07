package RDD

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD

object RDD_File1 {
  def main(args: Array[String]): Unit = {
    //TODO 创建环境
    val sparkconf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("File")
    val sc = new SparkContext(sparkconf)
    //从文件中读取数据
    //textFile : 以行为单位来读取数据读取数据是字符串
    //wholeTextFiles : 以文件为单位来读取数据，读取数据显示为元组，第一个元素是文件路径，第二个表示文件内容
    val rdd: RDD[(String, String)] = sc.wholeTextFiles("D:\\java\\Spark\\data\\1.txt")
    //从目录中读取
    sc.wholeTextFiles("D:\\java\\Spark\\data").collect().foreach(println)
    //正则匹配
    sc.wholeTextFiles("D:\\java\\Spark\\data\\1*.txt").collect().foreach(println)
    rdd.collect().foreach(println)
    //TODO 关闭环境
    sc.stop()
  }

}
