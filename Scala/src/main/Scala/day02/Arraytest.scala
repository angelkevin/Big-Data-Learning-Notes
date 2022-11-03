package day02

object Arraytest {

  def main(args: Array[String]): Unit = {

    var ints = new Array[Int](5)
    println(ints.length)
    val s: String = "asd"
    for (i <- Range(0,ints.length)){
      ints(i) =i
      println(ints(i))
    }
    ints.foreach(print)



  }


}
