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
