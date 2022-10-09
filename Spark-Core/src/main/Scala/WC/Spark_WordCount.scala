package WC

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object Spark_WordCount {
  def main(args: Array[String]): Unit = {
    //建立连接
    val wordCount: SparkConf = new SparkConf().setMaster("local").setAppName("WordCount")
    val sc: SparkContext = new SparkContext(wordCount)
    //执行业务
    //1.读取文件一行行的数据
    val line: RDD[String] = sc.textFile("data\\1.txt")
    println(line.toDebugString)
    //2.分词
    val words: RDD[String] = line.flatMap((x: String) => x.split(" "))
    println(words.toDebugString)
    //3.将数据根据单词进行分组，拆分单词
    val wordGroup: RDD[(String, Iterable[String])] = words.groupBy((word: String) => word)
    //4.对分组后进行转换
    println(wordGroup.toDebugString)
    val value: RDD[(String, Int)] = wordGroup.map {
      case (word, list) => (word, list.size)
    }
    val tuples: Array[(String, Int)] = value.collect()
    tuples.foreach(println)
    //关闭连接
    sc.stop()
  }

}
