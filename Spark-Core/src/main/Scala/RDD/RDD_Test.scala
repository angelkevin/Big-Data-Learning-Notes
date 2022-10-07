package RDD

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.Seq

object RDD_Test {
  def main(args: Array[String]): Unit = {
    val rddConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("RDD")
    val sc = new SparkContext(rddConf)
    //TODO 创建RDD
    //从内存中创建RDD，将内存中的集合作为数据
    val seq = Seq[Int](1, 2, 3, 4)
    //val value: RDD[Int] = sc.parallelize(seq)
    val value: RDD[Int] = sc.makeRDD(seq)
    //makeRDD方法在底层实现就调用了parallelize对象
    value.collect.foreach(print)
    sc.stop()
  }
}
