package Example

import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, SparkSession}


case class Product(productId: Int, name: String, imageUrl: String, categories: String, tags: String)

case class Rating(userId: Int, productId: Int, score: Double, timestamp: Int)

case class MongoConfig(uri: String, db: String)


object DATA {
  val PATH = "D:\\java\\Spark-Core\\data\\user.json"
  val PATH1 = ""
  val MONGODB_PRODUCT_COLLECTION = "Product"
  val MONGODB_RATING_COLLECTION = "Rating"


  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("DataLoader")
    val spark: SparkSession = SparkSession.builder().config(sparkConf).getOrCreate()
    import spark.implicits._

    val productRdd: RDD[String] = spark.sparkContext.textFile(PATH)
    val productDF: DataFrame = productRdd.map((x: String) => {
      val strings: Array[String] = x.split("\\^")
      Product(strings(0).toInt, strings(1).trim, strings(4).trim, strings(5).trim, strings(6).trim)
    }).toDF()

    val ratingRDD: RDD[String] = spark.sparkContext.textFile(PATH1)
    val ratingDF: DataFrame = ratingRDD.map((item: String) => {
      val attr: Array[String] = item.split(",")
      Rating(attr(0).toInt, attr(1).toInt, attr(2).toDouble, attr(3).toInt)
    }).toDF()

    implicit val mongoConfig: MongoConfig = MongoConfig("1111", "1111")


  }

  def storeMongo(productDF: DataFrame, ratingDF: DataFrame)(implicit mongoConfig: MongoConfig): Unit = {


  }


}
