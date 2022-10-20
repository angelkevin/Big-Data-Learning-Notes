package Sql

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

object UDF {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local[*]").setAppName("sql")
    val sparkSession: SparkSession = SparkSession.builder().config(conf).getOrCreate()
    import sparkSession.implicits._
    //自定义UDF函数 
    sparkSession.udf.register("zkw", (name: String) => {
      "name:" + name
    })


    val df = sparkSession.read.json("data/user.json")
    df.createOrReplaceTempView("user")

    sparkSession.sql("select age , zkw(name) as name from user").show()

    sparkSession.close()


  }

}
