package Day01

object Lambda {
  def main(args: Array[String]): Unit = {
    val stringToUnit: String => Unit = (name: String) => {
      println(name)
    }
    stringToUnit("zkw")
    def f(func : String => Unit):Unit={
      func("zkw")
    }
    f(stringToUnit)
  }

}
