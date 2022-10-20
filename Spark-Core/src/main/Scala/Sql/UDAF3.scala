package Sql

import org.apache.spark.SparkConf
import org.apache.spark.sql.expressions.Aggregator
import org.apache.spark.sql._

object UDAF3 {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local[*]").setAppName("sql")
    val sparkSession: SparkSession = SparkSession.builder().config(conf).getOrCreate()

    import sparkSession.implicits._
    val df: DataFrame = sparkSession.read.json("data/user.json")
    df.createOrReplaceTempView("user")
    df.as[User]
    sparkSession.udf.register("av", functions.udaf(new MyAvgUDAF))
    sparkSession.sql("select av(age) from user").show()

    sparkSession.close()


  }
  case class User(Username:String,age:Long)
  /*
  自定义聚合函数
  重写方法
   */
  case class Buff(var total: Long, var count: Long)

  class MyAvgUDAF extends Aggregator[Long, Buff, Long] {
    //    初始值或零值
    //    缓冲区的初始化
    override def zero: Buff = {
      Buff(0L, 0L)
    }

    //计算规则
    override def reduce(b: Buff, a: Long): Buff = {
      b.total = b.total + a
      b.count = b.count + 1
      b
    }

    //合并缓冲区
    override def merge(b1: Buff, b2: Buff): Buff = {
      b1.total = b1.total + b2.total
      b1.count = b1.count + b2.count
      b1
    }

    //计算结果
    override def finish(reduction: Buff): Long = {

      reduction.total / reduction.count
    }

    //缓冲区输出编码操作
    override def bufferEncoder: Encoder[Buff] = Encoders.product

    //输出的编码操作
    override def outputEncoder: Encoder[Long] = Encoders.scalaLong
  }
}

