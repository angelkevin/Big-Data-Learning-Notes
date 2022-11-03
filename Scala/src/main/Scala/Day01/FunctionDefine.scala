package Day01

class FunctionDefine {
  //    没有参数没有返回值
  def f1(): Unit = {
    println("没有参数没有返回值")
  }

  //    无参有返回值
  def f2(): String = {
    println("无参有返回值")
    "666"
  }
}

object FunctionDefine {
  def main(args: Array[String]): Unit = {
    val define = new FunctionDefine
    define.f1()
    define.f2()
  }
}


