import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object topN {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("Map")
    val sc = new SparkContext(sparkConf)
    //    时间戳，省份，城市，用户，广告，中间字段使用空格分隔
    val data: RDD[String] = sc.textFile("D:\\java\\Spark-Core\\data\\agent.log")

    val data1: RDD[Array[String]] = data.map((_: String).split(" "))

    val value1: RDD[((String, String), Int)] = data1.map((s: Array[String]) => ((s(1), s(4)), 1))


    val value: RDD[((String, String), Int)] = value1.reduceByKey(_ + _)

    val value2: RDD[(String, (String, Int))] = value.map((s: ((String, String), Int)) => (s._1._1, (s._1._2, s._2)))


    val value3: RDD[(String, Iterable[(String, Int)])] = value2.groupByKey()

    value3.mapValues((iter: Iterable[(String, Int)]) =>
      iter.toList.sortBy((iter: (String, Int)) => iter._2)(Ordering.Int.reverse).take(3)).collect().foreach(println)


    sc.stop()
  }

}
