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
