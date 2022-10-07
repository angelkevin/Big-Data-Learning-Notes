package WC

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD

object Spark_WordCount3 {
  def main(args: Array[String]): Unit = {

    val wordCount: SparkConf = new SparkConf().setMaster("local").setAppName("WordCount")
    val sc: SparkContext = new SparkContext(wordCount)
    val line: RDD[String] = sc.textFile("1.txt")
    val words: RDD[String] = line.flatMap((x: String) => x.split(" "))
    val value: RDD[(String, Int)] = words.map((word: String) => (word, 1))
    val value1: RDD[(String, Int)] = value.reduceByKey((x: Int, y: Int) => x + y)
    value1.collect().foreach(println)
    sc.stop()
  }

}
