package KV

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object RDD_test_jionByKey {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("Map")
    val sc = new SparkContext(sparkConf)
    val rdd = sc.makeRDD(List(("a", 1), ("b", 1), ("b", 3)), 2)

    val rdd1 = sc.makeRDD(List(("a", 2), ("b", 2), ("a", 3)), 2)


    // 没有key匹配上不出现结果中
    // 要是不够出现类似笛卡尔积,会造成数据翻倍


    rdd.join(rdd1).collect().foreach(println)


    sc.stop()

  }

}
