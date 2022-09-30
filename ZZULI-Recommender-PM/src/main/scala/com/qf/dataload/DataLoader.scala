package com.qf.dataload

import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.{MongoClient, MongoClientURI}
import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SparkSession}

/**
 * Product数据集
 * 3982                            商品ID
 * Fuhlen 富勒 M8眩光舞者时尚节能    商品名称
 * 1057,439,736                    商品分类ID，不需要
 * B009EJN4T2                      亚马逊ID，不需要
 * https://images-cn-4.ssl-image   商品的图片URL
 * 外设产品|鼠标|电脑/办公           商品分类
 * 富勒|鼠标|电子产品|好用|外观漂亮   商品UGC标签
 */
case class Product(productId:Int,name:String,categories:String,imageUrl:String,tags:String)
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

object DataLoader {
  //定义数据文件的路径
  val PRODUCT_DATA_PATH = "D:\\java\\ZZULI-Recommender-PM\\src\\main\\resources\\products.csv"
  val RATING_DATA_APTH = "D:\\java\\ZZULI-Recommender-PM\\src\\main\\resources\\ratings.csv"
  //定义mongodb中存储的表名
  val MONGODB_PRODUCT_COLLECTION = "Product"
  val MONGODB_RATING_COLLECTION = "Rating"


  //主程序主函数
  def main(args: Array[String]): Unit = {
    val config = Map(
      "spark.cores" -> "local[*]",
      "mongo.uri" -> "mongodb://192.168.170.130:27017/recommender",
      "mongo.db" -> "recommender"
    )

    //创建一个sparkConf的配置
    val sparkConf = new SparkConf().setAppName("DataLoader").setMaster(config("spark.cores"))
    //创建一个sparkSession的配置
    val spark = SparkSession.builder().config(sparkConf).getOrCreate()

    //在对DataFrame和Dataset进行操作许多操作都需要这个包进行支持

    import spark.implicits._

    //创建product的RDD(弹性分布集)
    val productRDD = spark.sparkContext.textFile(PRODUCT_DATA_PATH)

    //将RDD转化为DataFrame
    val productDF = productRDD.map(item => {
      val attr = item.split("\\^")
      //Product(productId:Int,name:String,categories:String,imageUrl:String,tags:String)
      Product(attr(0).toInt,attr(1).trim,attr(2).trim,attr(4).trim,attr(5).trim)
    }).toDF()

    //创建rating的RDD
    val ratingRDD = spark.sparkContext.textFile(RATING_DATA_APTH)
    //将RDD转化为DataFrame
    val ratingDF = ratingRDD.map(item => {
      val attr = item.split(",")
      Rating(attr(0).toInt,attr(1).toInt,attr(2).toDouble,attr(3).toLong)
    }).toDF()

    //打印dataframe
    productDF.show()
    ratingDF.show()

    //声明一个隐式的配置对象
    implicit val mongoConfig = MongoConfig(config.get("mongo.uri").get,config("mongo.db"))

    //保存数据到mongodb当中
    storeDataInMongoDB(productDF,ratingDF)

    //关闭spark
    spark.stop()
  }


  /**
   * 保存数据到MongoDB中
   * @param productDF
   * @param ratingDF
   * @param mongoConfig
   */
  def storeDataInMongoDB(productDF:DataFrame,ratingDF:DataFrame)(implicit mongoConfig: MongoConfig): Unit ={
    //创建mongodb的客户端的连接
    val mongoClient = MongoClient(MongoClientURI(mongoConfig.uri))

    //定义通过MongoDB客户端拿到的表操作对象
    val productCollection = mongoClient(mongoConfig.db)(MONGODB_PRODUCT_COLLECTION)
    val ratingCollection = mongoClient(mongoConfig.db)(MONGODB_RATING_COLLECTION)

    //如果MongoDB中有对应的的数据库，那么应该删除
    productCollection.dropCollection()
    ratingCollection.dropCollection()

    //将当前数据写入到MongoDB当中
    productDF
      .write
      .option("uri",mongoConfig.uri)
      .option("collection",MONGODB_PRODUCT_COLLECTION)
      .mode("overwrite")
      .format("com.mongodb.spark.sql")
      .save()

    ratingDF
      .write
      .option("uri",mongoConfig.uri)
      .option("collection",MONGODB_RATING_COLLECTION)
      .mode("overwrite")
      .format("com.mongodb.spark.sql")
      .save()

    //对数据表建索引
    productCollection.createIndex(MongoDBObject("productId" -> 1))
    ratingCollection.createIndex(MongoDBObject("userId" -> 1))
    ratingCollection.createIndex(MongoDBObject("productId" -> 1))
    //关闭MongoDB的连接
    mongoClient.close()
  }
}