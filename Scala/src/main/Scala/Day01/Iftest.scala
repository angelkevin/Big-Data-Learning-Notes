package Day01

import scala.io.{Source, StdIn}

object Iftest {
  def main(args: Array[String]): Unit = {
    val x: Int = StdIn.readInt()
    if (x < 4) {
      println(66)
    } else if (x < 6) {
      println(777)
    } else {
      println(555)
    }
    val result: Any = if (x < 4) {
      println(66)
      "zkw"
    } else if (x < 6) {
      println(777)
      777
    } else {
      println(555)
      555
    }
    print(result)


  }

}
