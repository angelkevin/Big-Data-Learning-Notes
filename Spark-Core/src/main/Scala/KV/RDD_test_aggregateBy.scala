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
