package com.qf.recommender.static

import java.text.SimpleDateFormat
import java.util.Date
import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SparkSession}


/**
 * Rating数据集
 * 4867        用户ID
 * 457976      商品ID
 * 5.0         评分
 * 1395676800  时间戳
 */
case class Rating(userId:Int,productId:Int,score:Double,timestamp:Long)
/**
 * MongoDB连接配置
 * @param uri    MongoDB的连接uri
 * @param db     要操作的db
 */
case class MongoConfig(uri:String,db:String)

object StatisticsRecommender {

  val MONGODB_RATING_COLLECTION = "Rating"

  //统计表的名称
  val RATE_MORE_PRODUCTS = "RateMoreProducts"
  val RATE_MORE_RECENTLY_PRODUCTS = "RateMoreRecentlyProducts"
  val AVERAGR_PRODUCTS = "AverageProducts"

  //main方法
  def main(args: Array[String]): Unit = {
    val config = Map(
      "spark.cores" -> "local[*]",
      "mongo.uri" -> "mongodb://192.168.170.130:27017/recommender",
      "mongo.db" -> "recommender"
    )

    //创建SparkConf配置
    val sparkConf = new SparkConf().setAppName("StatisticsRecommender").setMaster(config("spark.cores"))
    //创建SparkSession
    val spark = SparkSession.builder().config(sparkConf).getOrCreate()

    implicit val mongoConfig = MongoConfig(config("mongo.uri"),config("mongo.db"))

    //加入隐式转换
    import spark.implicits._

    val ratingDF = spark
      .read
      .option("uri",mongoConfig.uri)
      .option("collection",MONGODB_RATING_COLLECTION)
      .format("com.mongodb.spark.sql")
      .load()
      .as[Rating]
      .toDF()

    //创建一张名叫ratings的表
    ratingDF.createOrReplaceTempView("ratings")

    //1.统计所有历史数据当中每个商品的打分次数   ---> 历史热门推荐
    //数据结构 -》productId,count
    val rateMoreProductsDF = spark.sql(
      """
        |select
        | productId,
        | count(productId) as count
        |from ratings
        |group by productId
        |""".stripMargin)

    println("---------------------------------")
    rateMoreProductsDF.show(10)

    storeDFInMongoDB(rateMoreProductsDF,RATE_MORE_PRODUCTS)

    //2.统计以月为单位拟每个商品的评分数
    //数据结构 -》 productId,count,time

    //创建一个日期格式化工具
    val simpleDateFormat = new SimpleDateFormat("yyyyMM")
    //注册一个日期函数 也可以直接在spark sql里面使用from_unixtime函数
    spark.udf.register("changeDate",(x:Int) => simpleDateFormat.format(new Date(x * 1000L)).toInt)

    // 将原来的Rating数据集中的时间转换成年月的格式
    val ratingOfYearMonthDF = spark.sql(
      """
        |select
        | productId,
        | score,
        | changeDate(timestamp) as yearmonth
        |from
        | ratings
        |""".stripMargin)
    println("------------------------------------")
    ratingOfYearMonthDF.show(10)

    // 将新的数据集注册成为一张表
    ratingOfYearMonthDF.createOrReplaceTempView("ratingOfMonth")
    //统计最近一个月商品被打分次数   --->最近热门商品
    val rateMoreRecentlyProductsDF = spark.sql(
      """
        |select
        | productId,
        | count(productId) as count,
        | yearmonth
        |from
        | ratingOfMonth
        |group by
        | yearmonth,productId
        |order by
        | yearmonth desc,count desc
        |""".stripMargin)

    println("-------------------------------")
    rateMoreRecentlyProductsDF.show(10)
    storeDFInMongoDB(rateMoreRecentlyProductsDF,RATE_MORE_RECENTLY_PRODUCTS)

    //3.统计每个商品的平均评分   ---> 用于大家都喜欢或者大家都在看的推荐
    val averageProductsDF = spark.sql(
      """
        |select
        | productId,
        | avg(score) as avg
        |from ratings
        |group by productId
        |""".stripMargin)

    println("-------------------------------")
    averageProductsDF.show(10)

    storeDFInMongoDB(averageProductsDF,AVERAGR_PRODUCTS)

    spark.stop()
  }


  /**
   * 存储到MongoDB
   * @param DF
   * @param collection_name
   * @param mongoConfig
   */
  def storeDFInMongoDB(DF:DataFrame,collection_name:String)(implicit mongoConfig: MongoConfig): Unit ={
    DF
      .write
      .option("uri",mongoConfig.uri)
      .option("collection",collection_name)
      .mode("overwrite")
      .format("com.mongodb.spark.sql")
      .save()
  }
}