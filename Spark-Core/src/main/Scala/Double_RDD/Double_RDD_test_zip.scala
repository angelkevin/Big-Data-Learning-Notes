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
