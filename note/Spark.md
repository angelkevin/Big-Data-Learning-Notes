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

### RDD value类型

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

#### mapPartitions

> 可以以分区为单位来进行分区转换操作,但是会将整个分区来加载到内存进行引用如果处理完的数据不被释放掉,存在对象的引用,在内存较小,数据量较大的场合下容易内存溢出

```scala
package Operate

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object RDD_test_partitions {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("Map")
    val sc = new SparkContext(sparkConf)
    var rdd: RDD[Int] = sc.makeRDD(
      List(1, 2, 3, 4), 2
    )
    rdd.mapPartitions(
      iter => {
        println(">>>>>>")
        iter.map(_*2)
      }
    ).collect()
  }
}

```

mapPartitions算子需要传入一个迭代器,返回一个迭代器,没有要求元素的个数不变,map算子类似于串行操作,所以性能较低,mapPartitions类似于批处理,但是容易造成内存溢出

#### mapPartitionsWithIndex

>将待处理的数据以分区为单位发送到计算节点进行处理，这里的处理是指可以进行任意的处 理，哪怕是过滤数据，在处理时同时可以获取当前分区索引。

```scala
package Operate

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD

object RDD_test_mapPartitionswithIndex {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("Map")
    val sc = new SparkContext(sparkConf)
    var rdd: RDD[Int] = sc.makeRDD(
      List(1, 2, 3, 4), 2
    )
    rdd.mapPartitionsWithIndex(
      (index, iter) => {
        if (index == 1) {
          iter
        } else {
          Nil.iterator //空迭代器
        }
      }
    )
    rdd.mapPartitionsWithIndex(
      (index, iter) => {
        iter.map(
          num => (index, num)
        )
      }
    ).collect().foreach(println)
  }

}

```

#### flatmap

>将处理的数据进行扁平化后再进行映射处理，所以算子也称之为扁平映射

```scala
package Operate

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object Flatmap {

  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("Map")
    val sc = new SparkContext(sparkConf)
    val value = sc.makeRDD(List(
      List(1, 2, 3, 4), List(5, 6, 7, 8))
    )
    val value1: RDD[Int] = value.flatMap(
      (list: Seq[Int]) => {
        println(list)
        list
      }
    )
    val rdd : RDD[String] =sc.makeRDD(List("Hello World","Fuck you"))
    val value2: RDD[String] = rdd.flatMap(
      _.split(" ")
    )
    value2.collect().foreach(println)
    value1.collect().foreach(println)
  }
}

```

#### glom

> 将同一个分区的数据直接转换为相同类型的内存数组进行处理，分区不变

```scala
package Operate

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object RDD_test_glom {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("Map")
    val sc = new SparkContext(sparkConf)
    val rdd: RDD[Int] = sc.makeRDD(
      List(1, 2, 3, 4), 2
    )
    //将同一个分区直接转换成相同类型的内存数组进行处理,分区数量不变
    val value: RDD[Array[Int]] = rdd.glom()
    println(value.map(
      data => data.max
    ).collect().sum)
  }
}
```

#### groupBy

> 将数据根据指定的规则进行分组, 分区默认不变，但是数据会被打乱重新组合，我们将这样 的操作称之为 shuffle。极限情况下，数据可能被分在同一个分区中

```scala
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
```

#### filter

>将数据根据指定的规则进行筛选过滤，符合规则的数据保留，不符合规则的数据丢弃。 当数据进行筛选过滤后，分区不变，但是分区内的数据可能不均衡，生产环境下，可能会出 现数据倾斜。

```scala
package Operate

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object RDD_test_filter {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("Map")
    val sc = new SparkContext(sparkConf)
    val rdd: RDD[Int] = sc.makeRDD(
      List(1, 2, 3, 4), 2
    )
    //将数据根据指定的规则进行筛选过滤,符合数据的保留,不符合的丢弃.当数据进行筛选过滤后,分区不变,可能会数据倾斜
    val FilterRDD: RDD[Int] = rdd.filter(num => num % 2 != 0)

    FilterRDD.collect().foreach(println)
  }
}
```

#### sample

> // 抽取数据不放回（伯努利算法） 
>
> // 伯努利算法：又叫 0、1 分布。例如扔硬币，要么正面，要么反面。
>
>  // 具体实现：根据种子和随机算法算出一个数和第二个参数设置几率比较，小于第二个参数要，大于不 要
>
>  // 第一个参数：抽取的数据是否放回，false：不放回
>
>  // 第二个参数：抽取的几率，范围在[0,1]之间,0：全不取；1：全取； /
>
> / 第三个参数：随机数种子 
>
> // 抽取数据放回（泊松算法）
>
>  // 第一个参数：抽取的数据是否放回，true：放回；false：不放回 
>
> // 第二个参数：重复数据的几率，范围大于等于 0.表示每一个元素被期望抽取到的次数 
>
> // 第三个参数：随机数种子

```scala
package Operate

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object RDD_test_sample {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("Map")
    val sc = new SparkContext(sparkConf)
    val rdd: RDD[Int] = sc.makeRDD(
      List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12), 2
    )

    //sample需要传入三个参数
    //第一个表示数据是否放回,true放回,false丢弃
    //第二个表示数据源中的数据被抽取的概率
    //第三个表示抽取时随机种子,如果不传递,那么使用的是当前的系统时间
    rdd.sample(false, 0.4, 2).collect().foreach(println)

  }
}
```

#### distinct

> 将数据集中重复的数据去重

```scala 
package Operate

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object RDD_test_Distinct {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("Map")
    val sc = new SparkContext(sparkConf)
    var rdd: RDD[Int] = sc.makeRDD(
      List(1, 2, 3, 4, 1, 2, 3, 4), 2
    )


    //case _ => map(x => (x, null)).reduceByKey((x, _) => x, numPartitions).map(_._1)
    //(1,null) (2,null) (1,null) (2,null)
    //reducebykey
    //(1,null)=>()1
    rdd.distinct().collect().foreach(println)
  }
}
```

#### coalesce

> 根据数据量缩减分区，用于大数据集过滤后，提高小数据集的执行效率 当 spark 程序中，存在过多的小任务的时候，可以通过 coalesce 方法，收缩合并分区，减少 分区的个数，减小任务调度成本

```scala
package Operate

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object RDD_test_coalesce {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("Map")
    val sc = new SparkContext(sparkConf)
    val rdd: RDD[Int] = sc.makeRDD(
      List(1, 2, 3, 4,5,6,7,8),4
    )
    //coalesce减少分区数量,防止资源浪费
    //不会将数据打乱重新组合,只是缩减分区
    //如果想让数据均衡一些可以进行shuffle处理,没有规律
    //def coalesce(numPartitions: Int, shuffle: Boolean = false)
    rdd.coalesce(2,true).saveAsTextFile("output")
    //coalesce可以增加分区数量,如果不shuffle将没有意义
    //扩大分区可以使用repartition
    rdd.repartition(8)
    //coalesce(numPartitions, shuffle = true)
    rdd.coalesce(8,true).saveAsTextFile("output1")
    sc.stop()
  }
}
```

#### repartition

> 该操作内部其实执行的是 coalesce 操作，参数 shuffle 的默认值为 true。无论是将分区数多的 RDD 转换为分区数少的 RDD，还是将分区数少的 RDD 转换为分区数多的 RDD，repartition 操作都可以完成，因为无论如何都会经 shuffle 过程。

```scala
rdd.repartition(8)
```

#### sortBy

> 该操作用于排序数据。在排序之前，可以将数据通过 f 函数进行处理，之后按照 f 函数处理 的结果进行排序，默认为升序排列。排序后新产生的 RDD 的分区数与原 RDD 的分区数一 致。中间存在 shuffle 的过程

```scala
package Operate

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object RDD_test_sortBy {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("Map")
    val sc = new SparkContext(sparkConf)
    val rdd: RDD[Int] = sc.makeRDD(
      List(1, 2, 4, 3, 5, 8, 7, 6), 4
    )
    //默认升序,不会改变分区,会存在shuffle组合
    //false是降序
    rdd.sortBy(num => num).collect().foreach(print)
    sc.stop()
  }
}
```

### RDD双value类型

>交集,并集,差集,拉链

```scala
package Double_RDD

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object Double_RDD_test_zip {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("Map")
    val sc = new SparkContext(sparkConf)
    val rdd: RDD[Int] = sc.makeRDD(
      List(1,2, 4, 3, 5), 2
    )
    val rdd1: RDD[Int] = sc.makeRDD(
      List(5, 7, 6, 4), 2
    )

    rdd.zip(rdd1)
    sc.stop()
  }
}

```

### RDD Key-Value类型



