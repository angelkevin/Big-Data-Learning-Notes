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

- 数据以行为单位进行读取，spark读取文件采取的是hadoop的方式读取，所以一行一行的读取，和字节数没有关系
- 数据读取的时候以偏移量为单位，偏移量不会被重复读取
- 数据分区偏移量计算

### RDD的创建

从集合中创建 RDD,Spark 主要提供了两个方法：parallelize 和 makeRDD,从底层代码实现来讲，makeRDD 方法其实就是 parallelize 方法

默认情况下,Spark可以将一个作业切分多个任务后，发送给Executor节点并行计算，而能够并行计算的任务数量我们称之为并行度。这个数量可以在构建 RDD 时指定。

```scala
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

```

```Scala
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

```

```scala
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

```



## RDD算子



RDD算子：转换：功能的补充和封装，将旧的RDD包装成新的RDD

RDD算子：执行：触发任务调度和作业执行



### RDD转换算子

RDD分区内数据的执行是有序的,RDD的计算一个分区内的数据是一个个执行逻辑,只有前面一个数据全部的逻辑执行完毕后,才会执行.	

#### map

>将处理的数据逐条进行映射转换，这里的转换可以是类型的转换，也可以是值的转换。

```scala
package Operate

import org.apache.spark
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object Map_test {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local").setAppName("Map")
    val sc = new SparkContext(sparkConf)
    var rdd: RDD[Int] = sc.makeRDD(
      List(1,2,3,4)
    )
    //转换函数
    def Map_Function(num: Int): Int = {
      num * 2
    }

    //调用自定义函数
    rdd.map(Map_Function).collect().foreach(println)
    //使用Lambada表达式
    rdd.map((data: Int) => data *2).collect().foreach(println)
    //至简原则
    rdd.map(_*2).collect().foreach(println)
    sc.stop()
  }
}

```





