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
