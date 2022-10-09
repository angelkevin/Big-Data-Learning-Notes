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
