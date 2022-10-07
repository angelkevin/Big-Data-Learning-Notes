package Day01

import scala.collection.immutable
import scala.language.postfixOps
import scala.util.control.Breaks

object forfor {
  def main(args: Array[String]): Unit = {

    //[1,10]
    for (i <- 1 to 10) {
      val x: Int = i
      println(x)
    }
    //[1,10)
    for (i <- Range(1, 10)) {
      println(i)
    }
    //[1,10)
    for (i <- 1 until 10) {
      println(i)
    }

    //循环守卫，满足if进行相当于continue
    for (i <- 1 until 10 if i != 5) {
      println(i)
    }
    //循环步长
    for (i <- 1 to 10 by 1) {
      println(i)
    }
    //倒序输出
    for (i <- 1 to 10 reverse) {
      println(i)
    }

    for (i <- 1 to (9)) {
      for (j <- 1 to (i)) {
        print(s"${j} * ${i} = " + i * j + "\t")
      }
      println("\n")
    }
    //简写
    for (i <- 1 to (9); j <- 1 to (i)) {
      print(s"${j} * ${i} = " + i * j + "\t")
      if (i == j) println("\n")
    }
    //循环引入变量
    for (i <- 1 to 10; j = 10 - i) {
      println(i, j)
    }
    //循环返回值
    val units: immutable.IndexedSeq[Int] = for (i <- 1 to 10) yield i
    println(units)

    //循环中断，调用breaks下面的breakable方法，本质是抛出异常
    Breaks.breakable(
      for (i <- 1 to 10) {
        if (i ==3){
          println(i)
          Breaks.break()
        }
      }

    )


  }


}
