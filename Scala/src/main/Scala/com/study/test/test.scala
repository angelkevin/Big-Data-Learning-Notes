package com.study.test

import scala.collection.mutable.ArrayBuffer

object test {
  def main(args: Array[String]): Unit = {
    val ints = new ArrayBuffer[Int]()

    for (i <- 1 to 10){
      ints.append(i)
    }

    ints.append(20)

    println(ints.mkString(","))

  }
}
