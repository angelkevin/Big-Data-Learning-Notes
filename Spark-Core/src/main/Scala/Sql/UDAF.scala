package Sql

import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.types.{DataType, LongType, StructField, StructType}

object UDAF {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local[*]").setAppName("sql")
    val sparkSession: SparkSession = SparkSession.builder().config(conf).getOrCreate()
    import sparkSession.implicits._


    val df: DataFrame = sparkSession.read.json("data/user.json")
    df.createOrReplaceTempView("user")
    sparkSession.udf.register("av", new MyAvgUDAF())
    sparkSession.sql("select av(age) from user").show()

    sparkSession.close()


  }

  /*
  自定义聚合函数
  重写方法
   */
  class MyAvgUDAF extends UserDefinedAggregateFunction {
    //    输入的结构
    override def inputSchema: StructType = {
      StructType(
        Array(
          StructField("age", LongType)
        )
      )


    }
    //    缓冲区数据的结构

    override def bufferSchema: StructType = {
      StructType(
        Array(
          StructField("sum", LongType),
          StructField("count", LongType)
        )
      )
    }

    //    函数输出的结构
    override def dataType: DataType =  LongType


    //    稳定性
    override def deterministic: Boolean = true

    //    缓冲区初始化
    override def initialize(buffer: MutableAggregationBuffer): Unit = {
      buffer.update(0,0L)
      buffer.update(1,0L)
    }

    //      缓冲区数据更新
    override def update(buffer: MutableAggregationBuffer, input: Row): Unit = {
      buffer.update(0, buffer.getLong(0) + input.getLong(0))
      buffer.update(1, buffer.getLong(1) + 1)
    }

    //    缓冲区数据合并
    override def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {
      buffer1.update(0, buffer1.getLong(0) + buffer2.getLong(0))
      buffer1.update(1, buffer1.getLong(1) + buffer2.getLong(1))
    }

    //    计算平均值
    override def evaluate(buffer: Row): Any = {
      buffer.getLong(0) / buffer.getLong(1)
    }
  }

}
