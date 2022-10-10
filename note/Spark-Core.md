# Spark-Core

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

## Spark运行概念

Spark Executor 是集群中运行在工作节点（Worker）中的一个JVM进程，是整个集群中的专门用于计算的的节点。在提交应用中，可以提供制定计算节点的个数，以及对应的资源。这里的资源一般是指工作节点Executor的内存大小和使用的虚拟CPU核（core）数量。

spark根据分区数来决定task的个数，而task的个数和executor所拥有的core数来决定着spark的并行度，当task数多余core数时，就会产生并发操作。

https://liuxiaocong.blog.csdn.net/article/details/123243056?spm=1001.2101.3001.6661.1&utm_medium=distribute.pc_relevant_t0.none-task-blog-2%7Edefault%7ECTRLIST%7ERate-1-123243056-blog-109913568.pc_relevant_multi_platform_whitelistv3&depth_1-utm_source=distribute.pc_relevant_t0.none-task-blog-2%7Edefault%7ECTRLIST%7ERate-1-123243056-blog-109913568.pc_relevant_multi_platform_whitelistv3&utm_relevant_index=1

#### 运行架构
Spark 框架的核心是一个计算引擎，整体来说，它采用了标准 master-slave 的结构。
如下图所示，它展示了一个 Spark 执行时的基本结构。图形中的 Driver 表示 master，
负责管理整个集群中的作业任务调度。图形中的 Executor 则是 slave，负责实际执行任务。
#### 核心组件
##### Driver

Spark 驱动器节点，用于执行 Spark 任务中的 main 方法，负责实际代码的执行工作。
Driver 在 Spark 作业执行时主要负责：
➢ 将用户程序转化为作业（job）
➢ 在 Executor 之间调度任务(task)
➢ 跟踪 Executor 的执行情况
➢ 通过 UI 展示查询运行情况
实际上，我们无法准确地描述 Driver 的定义，因为在整个的编程过程中没有看到任何有关Driver 的字眼。所以简单理解，所谓的 Driver 就是驱使整个应用运行起来的程序，也称之为Driver 类。

##### Executor

Spark Executor 是集群中工作节点（Worker）中的一个 JVM 进程，负责在 Spark 作业中运行具体任务（Task），任务彼此之间相互独立。Spark 应用启动时，Executor 节点被同时启动，并且始终伴随着整个 Spark 应用的生命周期而存在。如果有 Executor 节点发生了故障或崩溃，Spark 应用也可以继续执行，会将出错节点上的任务调度到其他 Executor 节点上继续运行。
Executor 有两个核心功能：
➢ 负责运行组成 Spark 应用的任务，并将结果返回给驱动器进程
➢ 它们通过自身的块管理器（Block Manager）为用户程序中要求缓存的 RDD 提供内存式存储。RDD 是直接缓存在 Executor 进程内的，因此任务可以在运行时充分利用缓存数据加速运算。

####  Master & Worker
Spark 集群的独立部署环境中，不需要依赖其他的资源调度框架，自身就实现了资源调度的功能，所以环境中还有其他两个核心组件：Master 和 Worker，这里的 Master 是一个进程，主要负责资源的调度和分配，并进行集群的监控等职责，类似于 Yarn 环境中的 RM, 而Worker 呢，也是进程，一个 Worker 运行在集群中的一台服务器上，由 Master 分配资源对数据进行并行的处理和计算，类似于 Yarn 环境中 NM。
#### ApplicationMaster
Hadoop 用户向 YARN 集群提交应用程序时,提交程序中应该包含 ApplicationMaster，用于向资源调度器申请执行任务的资源容器 Container，运行用户自己的程序任务 job，监控整个任务的执行，跟踪整个任务的状态，处理任务失败等异常情况。说的简单点就是，ResourceManager（资源）和 Driver（计算）之间的解耦合靠的就是ApplicationMaster。
##  核心概念
### Executor 与 Core

Spark Executor 是集群中运行在工作节点（Worker）中的一个 JVM 进程，是整个集群中的专门用于计算的节点。在提交应用中，可以提供参数指定计算节点的个数，以及对应的资源。这里的资源一般指的是工作节点 Executor 的内存大小和使用的虚拟 CPU 核（Core）数量。

应用程序相关启动参数如下：

| 名称 | 说明 |
| ---------------- | -------------------------------------- |
| --num-executors | 配置 Executor 的数量 |
| --executor-memory | 配置每个 Executor 的内存大小 |
| --executor-cores | 配置每个 Executor 的虚拟 CPU core 数量 |

#### 并行度（Parallelism）

在分布式计算框架中一般都是多个任务同时执行，由于任务分布在不同的计算节点进行计算，所以能够真正地实现多任务并行执行，记住，这里是并行，而不是并发。这里我们将整个集群并行执行任务的数量称之为并行度。那么一个作业到底并行度是多少呢？这个取决于框架的默认配置。应用程序也可以在运行过程中动态修改。

####  有向无环图（DAG）

大数据计算引擎框架我们根据使用方式的不同一般会分为四类，其中第一类就是Hadoop 所承载的 MapReduce,它将计算分为两个阶段，分别为 Map 阶段 和 Reduce 阶段。对于上层应用来说，就不得不想方设法去拆分算法，甚至于不得不在上层应用实现多个 Job的串联，以完成一个完整的算法，例如迭代计算。 由于这样的弊端，催生了支持 DAG 框架的产生。因此，支持 DAG 的框架被划分为第二代计算引擎。如 Tez 以及更上层的Oozie。这里我们不去细究各种 DAG 实现之间的区别，不过对于当时的 Tez 和 Oozie 来说，大多还是批处理的任务。接下来就是以 Spark 为代表的第三代的计算引擎。第三代计算引擎的特点主要是 Job 内部的 DAG 支持（不跨越 Job），以及实时计算。这里所谓的有向无环图，并不是真正意义的图形，而是由 Spark 程序直接映射成的数据流的高级抽象模型。简单理解就是将整个程序计算的执行过程用图形表示出来,这样更直观，更便于理解，可以用于表示程序的拓扑结构。DAG（Directed Acyclic Graph）有向无环图是由点和线组成的拓扑图形，该图形具有方向，不会闭环。

####  提交流程

所谓的提交流程，其实就是我们开发人员根据需求写的应用程序通过 Spark 客户端提交给 Spark 运行环境执行计算的流程。在不同的部署环境中，这个提交过程基本相同，但是又有细微的区别，我们这里不进行详细的比较，但是因为国内工作中，将 Spark 引用部署到Yarn 环境中会更多一些，所以本课程中的提交流程是基于 Yarn 环境的。Spark 应用程序提交到 Yarn 环境中执行的时候，一般会有两种部署执行的方式：Client和 Cluster。两种模式主要区别在于：Driver 程序的运行节点位置。

#### Yarn Client 模式

Client 模式将用于监控和调度的 Driver 模块在客户端执行，而不是在 Yarn 中，所以一般用于测试。
➢ Driver 在任务提交的本地机器上运行
➢ Driver 启动后会和 ResourceManager 通讯申请启动 ApplicationMaster
➢ ResourceManager 分配 container，在合适的 NodeManager 上启动 ApplicationMaster，负责向 ResourceManager 申请 Executor 内存
➢ ResourceManager 接到 ApplicationMaster 的资源申请后会分配 container，然后ApplicationMaster 在资源分配指定的 NodeManager 上启动 Executor 进程
➢ Executor 进程启动后会向 Driver 反向注册，Executor 全部注册完成后 Driver 开始执行main 函数
➢ 之后执行到 Action 算子时，触发一个 Job，并根据宽依赖开始划分 stage，每个 stage 生成对应的 TaskSet，之后将 task 分发到各个 Executor 上执行。

#### Yarn Cluster 模式

Cluster 模式将用于监控和调度的 Driver 模块启动在 Yarn 集群资源中执行。一般应用于实际生产环境。
➢ 在 YARN Cluster 模式下，任务提交后会和 ResourceManager 通讯申请启动ApplicationMaster，
➢ 随后 ResourceManager 分配 container，在合适的 NodeManager 上启动 ApplicationMaster，此时的 ApplicationMaster 就是 Driver。
➢ Driver 启动后向 ResourceManager 申请 Executor 内存，ResourceManager 接到ApplicationMaster 的资源申请后会分配 container，然后在合适的 NodeManager 上启动Executor 进程
➢ Executor 进程启动后会向 Driver 反向注册，Executor 全部注册完成后 Driver 开始执行main 函数，
➢ 之后执行到 Action 算子时，触发一个 Job，并根据宽依赖开始划分 stage，每个 stage 生成对应的 TaskSet，之后将 task 分发到各个 Executor 上执行。



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

> 将数据根据指定的规则进行分组, **分区默认不变**，但是数据会被打乱重新组合，我们将这样 的操作称之为 shuffle。极限情况下，数据可能被分在同一个分区中

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

>将数据根据指定的规则进行筛选过滤，符合规则的数据保留，不符合规则的数据丢弃。 当数据进行筛选过滤后，**分区不变**，但是分区内的数据可能不均衡，生产环境下，可能会出 现数据倾斜。

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

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD

object Double_RDD_test {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("Map")
    val sc = new SparkContext(sparkConf)
    val rdd: RDD[Int] = sc.makeRDD(
      List(1, 2, 4, 3, 5)
    )
    val rdd1: RDD[Int] = sc.makeRDD(
      List(5, 8, 7, 6, 4)
    )

    //数据类型需要一样
    //交集
    println(rdd.intersection(rdd1).collect().mkString("Array(", ", ", ")"))

    //并集
    println(rdd.union(rdd1).collect().mkString("Array(", ", ", ")"))

    //差集,以一个 RDD 元素为主，去除两个 RDD 中重复元素，将其他元素保留下来。求差集
    println(rdd.subtract(rdd1).collect().mkString("Array(", ", ", ")"))

    //拉链,数据类型可以不一样,数据源的分区数量要一样,每一个分区的里面的元素数量要一致
    println(rdd.zip(rdd1).collect().mkString("Array(", ", ", ")"))

    sc.stop()

  }

}

/**
Array(4, 5)
Array(1, 2, 4, 3, 5, 5, 8, 7, 6, 4)
Array(1, 2, 3)
Array((1,5), (2,8), (4,7), (3,6), (5,4))
*/
```

### RDD Key-Value类型

#### partitionBy

> 将数据按照指定 Partitioner 重新进行分区。Spark 默认的分区器是 HashPartitioner

```scala
package KV

import org.apache.spark.rdd.RDD
import org.apache.spark.{HashPartitioner, SparkConf, SparkContext}

object RDD_test_partitionBy {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("Map")
    val sc = new SparkContext(sparkConf)
    val rdd = sc.makeRDD(
      List(("a",1),("b",2),("c",3))
    )

    //RDD=>PairRDDFunctions
    //隐式转换(二次编译)
    //根据指定的规则进行重新分区
    //哈希分区器
    rdd.partitionBy(new HashPartitioner(2))

    sc.stop()

  }

}

```

- 自定义分区器

```scala
import org.apache.spark.rdd.RDD
import org.apache.spark.{HashPartitioner, Partitioner, SparkConf, SparkContext}

object RDD_test_MypartitionBy {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("Map")
    val sc = new SparkContext(sparkConf)
    val rdd = sc.makeRDD(
      List(("a", 1), ("b", 2), ("c", 3))
    )

    val value: RDD[(String, Int)] = rdd.partitionBy(new MyPartitioner)
    value.saveAsTextFile("out")
    sc.stop()

  }

  class MyPartitioner extends Partitioner {

    //分区数量
    override def numPartitions: Int = 3

    //根据我们数据的key值返回返回我们的分区索引
    override def getPartition(key: Any): Int = {
      if (key == "a") {
        0
      } else if (key == "b") {
        1
      } else {
        2
      }

    }
  }

}

```

#### reduceByKey

> 可以将数据按照相同的 Key 对 Value 进行聚合

```scala
package KV

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object RDD_test_reduceByKey {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("Map")
    val sc = new SparkContext(sparkConf)
    val rdd = sc.makeRDD(
      List(("a",1),("b",2),("c",3))
    )
    //reducebykey:相同的key进行value数据的聚合操作
    //两两计算
    rdd.reduceByKey((num, num1) => num + num1).collect().foreach(print)

    c.stop()

  }
}

```

#### groupByKey

> 将数据源的数据根据 key 对 value 进行分组

```scala
package KV

import org.apache.spark.rdd.RDD
import org.apache.spark.{HashPartitioner, SparkConf, SparkContext}

object RDD_test_GroupBy {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("Map")
    val sc = new SparkContext(sparkConf)
    val rdd = sc.makeRDD(
      List(("a",1),("b",2),("c",3)),3)


    rdd.groupByKey().saveAsTextFile("outpath")

    sc.stop()

  }

}

```

> 从 shuffle 的角度：reduceByKey 和 groupByKey 都存在 shuffle 的操作，但是 reduceByKey 可以在 shuffle 前对分区内相同 key 的数据进行预聚合（combine）功能，这样会减少落盘的 数据量，而 groupByKey 只是进行分组，不存在数据量减少的问题，reduceByKey 性能比较 高。 
>
> 从功能的角度：reduceByKey 其实包含分组和聚合的功能。GroupByKey 只能分组，不能聚 合，所以在分组聚合的场合下，推荐使用 reduceByKey，如果仅仅是分组而不需要聚合。那 么还是只能使用 groupByKey

#### aggregateByKey

> 将数据根据不同的规则进行分区内计算和分区间计算

```scala
package KV

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object RDD_test_aggregateBy {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("Map")
    val sc = new SparkContext(sparkConf)
    val rdd: RDD[(String, Int)] = sc.makeRDD(
      List(("a", 1), ("b", 1), ("b", 3), ("a", 2), ("b", 2), ("a", 3)), 2)



    //aggregateByKey存在柯里化有两个参数列表,
    //第一个参数列表,需要传入一个参数,表示初始值
    //  主要用于当我们碰见第一个key的时候和我们的value进行分区内计算
    //第二个参数列表列表
    //  第一个表示分区类计算规则
    //  第二个表示分区间计算规则
    rdd.aggregateByKey(0)((x, y) => math.max(x, y), (x, y) => x + y).collect().foreach(println)
    rdd.aggregateByKey(0)(_ + _, _ + _).collect().foreach(println)

    //分区内分区间的计算规则相同的时候可以用foldbykey
    rdd.foldByKey(0)(_ + _).collect().foreach(println)

    //他的返回值类型是由初始值决定的
    val value: RDD[(String, (Int, Int))] = rdd.aggregateByKey((0, 0))(
      (t: (Int, Int), v: Int) => {
        (t._1 + v, t._2 + 1)
      }, (t1: (Int, Int), t2: (Int, Int)) => {
        (t1._1 + t2._1, t1._2 + t2._2)
      }
    )
    value.map((k: (String, (Int, Int))) => (k._1,k._2._1/k._2._2)).collect().foreach(println)


    sc.stop()

  }

}

```

#### foldByKey

```scala
rdd.foldByKey(0)(_ + _).collect().foreach(println)
```

#### combineByKey

>最通用的对 key-value 型 rdd 进行聚集操作的聚集函数（aggregation function）。类似于 aggregate()，combineByKey()允许用户返回值的类型与输入不一致。

```scala
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
```

#### sortByKey

> 在一个(K,V)的 RDD 上调用，K 必须实现 Ordered 接口(特质)，返回一个按照 key 进行排序 的RDD

#### cogroup

> 在类型为(K,V)和(K,W)的 RDD 上调用，返回一个(K,(Iterable,Iterable))类型的 RDD

```scala
package KV

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object RDD_test_coGroup {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("Map")
    val sc = new SparkContext(sparkConf)
    val rdd = sc.makeRDD(List(("a", 1), ("b", 1), ("c", 3)), 2)

    val rdd1 = sc.makeRDD(List(("a", 2), ("b", 2), ("a", 3)), 2)


    val value: RDD[(String, (Iterable[Int], Iterable[Int]))] = rdd.cogroup(rdd1)
    value.collect().foreach(println)

    sc.stop()

  }

}
/**
(b,(CompactBuffer(1),CompactBuffer(2)))
(a,(CompactBuffer(1),CompactBuffer(2, 3)))
(c,(CompactBuffer(3),CompactBuffer()))
*/
```

#### join

> 在类型为(K,V)和(K,W)的 RDD 上调用，返回一个相同 key 对应的所有元素连接在一起的 (K,(V,W))的 RDD

```scala
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
```

### RDD 序列化

闭包检查 :

从计算的角度, 算子以外的代码都是在 Driver 端执行, 算子里面的代码都是在 Executor 端执行。那么在 scala 的函数式编程中，就会导致算子内经常会用到算子外的数据，这样就 形成了闭包的效果，如果使用的算子外的数据无法序列化，就意味着无法传值给 Executor 端执行，就会发生错误，所以需要在执行任务计算前，检测闭包内的对象是否可以进行序列 化，这个操作我们称之为闭包检测。

### RDD 持久化

RDD 通过 Cache 或者 Persist 方法将前面的计算结果缓存，默认情况下会把数据以缓存 在 JVM 的堆内存中。但是并不是这两个方法被调用时立即缓存，而是触发后面的 action 算 子时，该 RDD 将会被缓存在计算节点的内存中，并供后面重用

### RDD CheckPoint 检查点

所谓的检查点其实就是通过将 RDD 中间结果写入磁盘 由于血缘依赖过长会造成容错成本过高，这样就不如在中间阶段做检查点容错，如果检查点 之后有节点出现问题，可以从检查点开始重做血缘，减少了开销

缓存和检查点区别

 1）Cache 缓存只是将数据保存起来，不切断血缘依赖。Checkpoint 检查点切断血缘依赖。 

2）Cache 缓存的数据通常存储在磁盘、内存等地方，可靠性低。Checkpoint 的数据通常存 储在 HDFS 等容错、高可用的文件系统，可靠性高。 3）建议对 checkpoint()的 RDD 使用 Cache 缓存，这样 checkpoint 的 job 只需从 Cache 缓存 中读取数据即可，否则需要再从头计算一次 RDD。

## 累加器

累加器用来把 Executor 端变量信息聚合到 Driver 端。在 Driver 程序中定义的变量，在 Executor 端的每个 Task 都会得到这个变量的一份新的副本，每个 task 更新这些副本的值后， 传回 Driver 端进行 merge

```scala
package acc

import org.apache.spark.rdd.RDD
import org.apache.spark.util.LongAccumulator
import org.apache.spark.{Partitioner, SparkConf, SparkContext}

object RDD_test_ACC {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("Map")
    val sc = new SparkContext(sparkConf)
    val rdd = sc.makeRDD(
      List(1, 2, 3, 4, 5, 6, 7, 8, 9)
    )

    //获取系统累加器
    //系统自带累加器
    //一般情况下会放在累加器中进行操作
    val sumACC: LongAccumulator = sc.longAccumulator("sum")
    sc.doubleAccumulator("double")
    sc.collectionAccumulator("collect")

    rdd.foreach(num => sumACC.add(num))

    println(sumACC.value)

    sc.stop()

  }

}

```

```scala
package acc

import org.apache.spark.util.{AccumulatorV2, LongAccumulator}
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable

object RDD_test_myACC {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("Map")
    val sc = new SparkContext(sparkConf)
    val rdd = sc.makeRDD(
      List("FUCK", "Hello")
    )

    //创建累加器对象
    val acc = new myAcc()
    //向Spark注册
    val zz: Unit = sc.register(acc, "zz")

    rdd.foreach(data => {
      acc.add(data)
    })

    println(acc.value)


    sc.stop()

  }

  /**
   * 继承累加器AccumulatorV2
   * IN:输入类型
   * out:输出类型
   */

  class myAcc extends AccumulatorV2[String, mutable.Map[String, Long]] {

    private var wcMap = mutable.Map[String, Long]()

    //判断是否为空
    override def isZero: Boolean = {
      wcMap.isEmpty
    }

    //复制累加器
    override def copy(): AccumulatorV2[String, mutable.Map[String, Long]] = {
      new myAcc
    }

    //重置累加器
    override def reset(): Unit = {
      wcMap.clear()
    }

    //累加器的累加
    override def add(v: String): Unit = {
      val l: Long = wcMap.getOrElse(v, 0L) + 1
      wcMap.update(v, l)

    }


    //合并多个累加器

    override def merge(other: AccumulatorV2[String, mutable.Map[String, Long]]): Unit = {
      val map1: mutable.Map[String, Long] = this.wcMap
      val map2: mutable.Map[String, Long] = other.value

      map2.foreach {
        case (word, count) => {
          val newcount: Long = map1.getOrElse(word, 0L) + count
          map1.update(word, newcount)
        }
      }

    }

    //累加器的结果
    override def value: mutable.Map[String, Long] = {
      wcMap
    }
  }

}

```

## 广播变量

广播变量用来高效分发较大的对象。向所有工作节点发送一个较大的只读值，以供一个 或多个 Spark 操作使用。比如，如果你的应用需要向所有节点发送一个较大的只读查询表， 广播变量用起来都很顺手。在多个并行操作中使用同一个变量，但是 Spark 会为每个任务 分别发送。

```scala
package Broadcast

import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable

object broadcast {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("Map")
    val sc = new SparkContext(sparkConf)
    val rdd: RDD[(String, Int)] = sc.makeRDD(
      List(("a", 1), ("b", 1)))


    val map: mutable.Map[String, Int] = mutable.Map(("a", 1), ("b", 1))

    //闭包数据,都是以task为单位发送,每个任务中包含闭包数据,一个Executor中有大量的重复数据,并会占用大量的内存,
    //Executor其实h是一个JVM,所以在启动时,会分配内存,完全可以将任务中的闭包数据放置在Executor的内存中达到共享的目的
    //spark中的广播变量不能修改:是分布式只读变量

    val bc: Broadcast[mutable.Map[String, Int]] = sc.broadcast(map)

    rdd.map {
      case (w, c) => {
        val i: Int = bc.value.getOrElse(w, 0)
        (w, (c, i))

      }
    }.collect().foreach(println)

    sc.stop()

  }

}
```

