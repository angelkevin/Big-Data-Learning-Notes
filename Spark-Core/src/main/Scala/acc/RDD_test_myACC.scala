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
