package Sql

import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Row, SparkSession}

object SparkSql {
  def main(args: Array[String]): Unit = {
    //创建spark的运行环境
    val conf = new SparkConf().setMaster("local[*]").setAppName("sql")
    val sparkSession: SparkSession = SparkSession.builder().config(conf).getOrCreate()
    //使用dataframe,如果涉及转化操作,需要引入转换规则
    import sparkSession.implicits._
    //执行操作
    //DataFrame
//    val dataFrame: DataFrame =
//    //dataFrame.show()
//
//    //DataFrame=>sql
//    //    dataFrame.createOrReplaceTempView("user")
//    //    sparkSession.sql("select * from user").show()
//    //    dataFrame.select($"age" + 1).show()
//
//
//    var seq = Seq(1, 2, 3, 4)
//    seq.toDS().show()
    val rdd: RDD[(String, Int)] = sparkSession.sparkContext.makeRDD(List(("zkw", 666)))
    val frame: DataFrame = rdd.toDF("name", "id")
    val rdd1: RDD[Row] = frame.rdd




    //关闭环境
    sparkSession.close()
  }

}
