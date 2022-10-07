package RDD

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object RDD_Par {
  def main(args: Array[String]): Unit = {
    //TODO 创建环境
    val sparkconf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("File")
    val sc = new SparkContext(sparkconf)
    //创建RDD & 分区
    //第二个参数不传递将使用默认值来设置分区，默认是当前环境最大可用核数
    val rdd: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4), 2)
    //将处理的数据保存为分区文件
    rdd.saveAsTextFile("output")
    sc.stop()

  }

}
