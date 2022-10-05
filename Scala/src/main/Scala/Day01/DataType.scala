package Day01

object DataType {
  def main(args: Array[String]): Unit = {
    //Null数据类型
    var zkw = new Student("zkw", 20)
    zkw = null
    println(zkw)

    //Unit数据类型
    def show(n: Int): Unit = {
      println(n)
    }

    val n: Unit = show(10)
    println(n)

    //Nothing数据类型
    def kk(n: Int): Nothing = {
      throw new Error("我错了")
    }

    println(kk(10))

  }

}
