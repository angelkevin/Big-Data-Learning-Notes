# Spark

## WordCount

```scala
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
    val line: RDD[String] = sc.textFile("1.txt")
    //2.分词
    val words: RDD[String] = line.flatMap((x: String) => x.split(" "))
    //3.将数据根据单词进行分组，拆分单词
    val wordGroup: RDD[(String, Iterable[String])] = words.groupBy((word: String) => word)
    //4.对分组后进行转换
    val value: RDD[(String, Int)] = wordGroup.map {
      case (word, list) => (word, list.size)
    }
    val tuples: Array[(String, Int)] = value.collect()
    tuples.foreach(println)
    //关闭连接
    sc.stop()
  }
}
```

```scala
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
```

## Spark运行环境

Spark作为一个数据处理的框架，在国内的主要环境为Yarn，

## Spark核心编程

三大数据结构

>RDD:弹性数据集
>
>累加器:分布式共享只写变量
>
>广播变量:分布式共享只读变量

RDD是最小的计算单元