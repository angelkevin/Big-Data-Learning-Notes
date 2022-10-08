package Operate

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object RDD_test_Groupby {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("Map")
    val sc = new SparkContext(sparkConf)
    val rdd: RDD[Int] = sc.makeRDD(
      List(1, 2, 3, 4), 2
    )
    val words = sc.makeRDD(List("Hello", "Hadoop", "Fuck"))
    val GroupRDD: RDD[(Char, Iterable[String])] = words.groupBy(
      data => data.charAt(0)
    )
    GroupRDD.map(data => (data._1, data._2.size)).collect().foreach(println)


    //模式匹配
    GroupRDD.map({
      case (c, strings) => (c, strings.size)
    }
    ).collect().foreach(println)


    //GroupBy会将数据打乱重新组合,这个操作我们称之为shuffle
    val tuples: RDD[(Int, Iterable[Int])] = rdd.groupBy(data => data % 2)


    tuples.collect().foreach(println)
  }
}










