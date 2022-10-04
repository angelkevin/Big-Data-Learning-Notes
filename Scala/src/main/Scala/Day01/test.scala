package Day01

import java.util

/*
object：关键字，声明一个单例对象，也叫作伴生类
 */

object test {
  //main 方法：从外部可以直接调用执行的方法
  //def 方法名称(参数名称：参数类型)
  //Unit 等同于Java里面的void

  /**
   * 程序入口方法
   *
   * @param args
   */
  def main(args: Array[String]): Unit = {
    add(1, 2)
    println("hello word")
  }

  def add(x: Int, y: Int): Unit = {
    println(x + y)
  }


}
