package Day01

import scala.io.StdIn

object IO {
  def main(args: Array[String]): Unit = {
    //输入信息
    val name = StdIn.readLine()
    val age = StdIn.readInt()
    println(s"我叫${name},今年${age}岁")


  }

}
