package Day01

object Function {
  def main(args: Array[String]): Unit = {


    //狭义方法
    def sayhi(name:String):Unit ={
      println(s"hi,${name}")
    }
    sayhi("zkw")

    Function.sayhi("jly")
  }
    //广义方法
  def sayhi(name: String): Unit = {
    println(s"hi,${name}")
  }


}
