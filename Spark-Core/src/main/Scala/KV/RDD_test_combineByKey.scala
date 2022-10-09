package KV

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object RDD_test_combineByKey {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("Map")
    val sc = new SparkContext(sparkConf)
    val rdd = sc.makeRDD(
      List(("a", 1), ("b", 1), ("b", 3), ("a", 2), ("b", 2), ("a", 3)), 2)

    //combineByKey
    //第一个参数:将相同的key进行结构化转换,实现操作
    //第二个参数:分区内的计算规则
    //第三个参数:分区间的计算规则
    val value: RDD[(String, (Int, Int))] = rdd.combineByKey(v => (v, 1), (t: (Int, Int), v) => (t._1 + v, t._2 + 1),
      (t: (Int, Int), v: (Int, Int)) => (t._1 + v._1, t._2 + v._2)
    )


    value.map((k: (String, (Int, Int))) => (k._1,k._2._1/k._2._2)).collect().foreach(println)


    sc.stop()

  }

}
