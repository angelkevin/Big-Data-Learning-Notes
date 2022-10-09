package Example

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object HotCategoryTop10Analysis {
  def main(args: Array[String]): Unit = {
    //TODO : Top10热门品类
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("Map")
    val sc = new SparkContext(sparkConf)
    //读取数据
    val data: RDD[String] = sc.textFile("D:\\java\\Spark-Core\\data\\user_visit_action.txt")
    //统计品类的点击数量(id,点击数量)
    val click: RDD[String] = data.filter(
      (action: String) => {
        val datas: Array[String] = action.split("_")
        datas(6) != "-1"
      }
    )
    val clickResult: RDD[(String, Int)] = click.map((action: String) => {
      val datas: Array[String] = action.split("_")
      (datas(6), 1)
    }).reduceByKey((_: Int) + (_: Int))
    //统计品类的下单数量(id,下单数量)
    val ordered: RDD[String] = data.filter(
      (action: String) => {
        val datas: Array[String] = action.split("_")
        datas(8) != "null"
      }
    )
    val orderedResult: RDD[(String, Int)] = ordered.flatMap(
      action => {
        val datas: Array[String] = action.split("_")
        val cid = datas(8)
        val cids = cid.split(",")
        cids.map(id => (id, 1))
      }
    ).reduceByKey(_ + _)
    //统计品类的支付数量(id,支付数量)
    val pay: RDD[String] = data.filter(
      (action: String) => {
        val datas: Array[String] = action.split("_")
        datas(8) != "null"
      }
    )
    val payResult: RDD[(String, Int)] = pay.flatMap(
      action => {
        val datas: Array[String] = action.split("_")
        val cid = datas(10)
        val cids = cid.split(",")
        cids.map(id => (id, 1))
      }
    ).reduceByKey(_ + _)
    //将品类进行排序,并且取前十
    val cogroupRDD: RDD[(String, (Iterable[Int], Iterable[Int], Iterable[Int]))] = clickResult.cogroup(orderedResult, payResult)
    val result: RDD[(String, (Int, Int, Int))] = cogroupRDD.mapValues {
      case (click, ordered, pay) => {
        var clickcount = 0
        var orderedcount = 0
        var paycount = 0
        if (click.iterator.hasNext) {
          clickcount = click.iterator.next()
        }
        if (ordered.iterator.hasNext) {
          orderedcount = ordered.iterator.next()
        }
        if (pay.iterator.hasNext) {
          paycount = pay.iterator.next()
        }
        (clickcount, orderedcount, paycount)
      }
    }
    //先按照点击数排名，靠前的就排名高；如果点击数相同，再比较下单数；下单数再相同，就比较支付数
    val resultRDD: Array[(String, (Int, Int, Int))] = result.sortBy(data => data._2._1, false).take(10)
    resultRDD.foreach(println)

  }

}
