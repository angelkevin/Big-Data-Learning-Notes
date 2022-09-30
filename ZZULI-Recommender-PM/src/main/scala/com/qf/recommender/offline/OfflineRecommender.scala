package com.qf.recommender.offline

import org.apache.spark.SparkConf
import org.apache.spark.mllib.recommendation.{ALS, Rating}
import org.apache.spark.sql.SparkSession
import org.jblas.DoubleMatrix

//定义样例类 --- 本质就是封装
case class ProductRating( userId: Int, productId: Int, score: Double, timestamp: Long )
case class MongoConfig( uri: String, db: String )

// 定义标准推荐对象
case class Recommendation( productId: Int, score: Double )
// 定义用户的推荐列表
case class UserRecs( userId: Int, recs: Seq[Recommendation] )
// 定义商品相似度列表
case class ProductRecs( productId: Int, recs: Seq[Recommendation] )

object OfflineRecommender {
  //定义常量
  val MONGODB_RATING_COLLECTION = "Rating"
  //定义推荐表的名称
  val USER_RECS = "UserRecs"
  val PRODUCT_RECS = "ProductRecs"

  val USER_MAX_RECOMMENDATION = 20

  def main(args: Array[String]): Unit = {
    //定义spark和mongodb的连接
    val config = Map(
      "spark.cores" -> "local[*]",
      "mongo.uri" -> "mongodb://192.168.170.130:27017/recommender",
      "mongo.db" -> "recommender"
    )

    //创建sparkSession
    val sparkConf = new SparkConf().setMaster(config("spark.cores")).setAppName("OfflineRecommender")
    val spark = SparkSession.builder().config(sparkConf).getOrCreate()

    implicit val mongoConfig = MongoConfig(config("mongo.uri"), config("mongo.db"))

    //隐式转换
    import spark.implicits._

    //加载用户对商品的评分数据，并且将其数据进行缓存
    val ratingRDD = spark
      .read
      .option("uri", mongoConfig.uri)
      .option("collection", MONGODB_RATING_COLLECTION)
      .format("com.mongodb.spark.sql")
      .load()   //load出来的数据DF
      .as[ProductRating]  //将DF转换成DS
      .rdd  //将DS转换成RDD
      .map(ProductRating => (ProductRating.userId, ProductRating.productId, ProductRating.score))  //映射元组
      .cache()  //缓存

    //将用户评分数据按照UserId去重
    val userRDD = ratingRDD.map(_._1).distinct()
    //将用户评分数据按照ProductId去重
    val productRDD = ratingRDD.map(_._2).distinct()

    //创建训练数据集,一般都是将数据集划分成2部分，一部分训练，一部分测试
    val trainData = ratingRDD.map(x => Rating(x._1, x._2, x._3))

    //rank是模型中隐语义因子的个数, iterations 是迭代的次数, lambda 是ALS的正则化参数
    val (rank, iterations, lambda) = (5, 10, 0.01)

    //调用ALS算法训练隐语义模型
    /*
    rank：ALS模型隐藏特征向量的维度个数，推荐10-100个，特征向量个数。
    iterations： ALS模型训练时候需要迭代的次数，几乎10-100次差不多。
    lambda：是ALS的正则化参，推荐值0.01。建议从0-100选择一些值，来去训练模型。
     */
    val model = ALS.train(trainData, rank, iterations, lambda)

    //计算用户推荐矩阵
    //用userRDD和productRDD做一个笛卡尔积，得到空的userProductsRDD表示的评分矩阵
    val userProducts = userRDD.cartesian(productRDD)  //笛卡尔积使用模型
    val preRating = model.predict(userProducts)  //将用户产品矩阵进行预测
    preRating.foreach(println)

    // 从预测评分矩阵中提取得到用户推荐列表
    val userRecs = preRating
      .filter(_.rating > 0)  //过滤预测评分大于0分以上
      .map(
        rating => (rating.user, (rating.product, rating.rating))
      )
      .groupByKey()  //根据userId进行聚合
      .map {
        case (userId, recs) =>
          //将每个用户的推荐列表按照商品预测的评分进行倒排序，然后取出最高分前20个
          UserRecs(userId, recs.toList.sortWith(_._2 > _._2).take(USER_MAX_RECOMMENDATION).map(x => Recommendation(x._1, x._2)))
      }
      .toDF() //将上诉的RDD转换成DF

    //将用户的推荐列表写入MongoDB中的UserRecs集合中
    userRecs.write
      .option("uri", mongoConfig.uri)
      .option("collection", USER_RECS)
      .mode("overwrite")
      .format("com.mongodb.spark.sql")
      .save()

    //计算商品相似度矩阵
    //获取商品的特征矩阵，数据格式 RDD[(scala.Int, scala.Array[scala.Double])]
    val productFeatures = model
      .productFeatures  //从模型中提取商品特征
      .map {
        //将提取出来的特征转换成元组
        case (productId, features) => (productId, new DoubleMatrix(features))
      }
    productFeatures.foreach(println)  //打印商品特征向量
    // 计算笛卡尔积并过滤合并
    val productRecs = productFeatures.cartesian(productFeatures)  //模型自带
      .filter{case (a,b) => a._1 != b._1}  //将矩阵中对角线上值过滤掉
      .map{case (a,b) =>
        val simScore = this.consinSim(a._2,b._2) // 使用商品特征向量，求余弦相似度
        (a._1,(b._1,simScore))   //封装商品和相似的商品及相似度
      }
      .filter(_._2._2 > 0.6)  //过滤相似度大于0.6的商品
      .groupByKey()   //根据产品分组聚合
      .map{case (productId,items) =>
        //将相似度大于0。6的商品进行封装
        ProductRecs(productId,items.toList.map(x => Recommendation(x._1,x._2)))
      }
      .toDF()  //将商品的相似商品RDD转换成DF

    //将商品的相似商品存储倒MongoDB中的ProductRecs集合中
    productRecs
      .write
      .option("uri", mongoConfig.uri)
      .option("collection",PRODUCT_RECS)
      .mode("overwrite")
      .format("com.mongodb.spark.sql")
      .save()

    //关闭spark
    spark.stop()
  }

  /**
   * 于弦相似度 ： cosa = A * B / |A| * |B|    A和B都是向量
   * @param product1
   * @param product2
   * @return
   */
  def consinSim(product1: DoubleMatrix, product2: DoubleMatrix): Double ={
    //.dot向量的乘
    product1.dot(product2) / (product1.norm2() * product2.norm2())
  }
}