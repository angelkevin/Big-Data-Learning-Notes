package Action

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object RDD_test_collect {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("Map")
    val sc = new SparkContext(sparkConf)
    val rdd = sc.makeRDD(
      List(("a", 1), ("b", 1), ("b", 3), ("a", 2), ("b", 2), ("a", 3)), 2)



    //调用的是sc.runJob
    rdd.collect()




    sc.stop()

  }

}
