package Day01

object String {
  def main(args: Array[String]): Unit = {

    var x = "zkw"
    //    打印多个相同的字符串
    println(x * 3)
    //    类似于C语言的printf
    printf("我的名字是%s\n", x)
    //    字符串模板：s"${}"
    println(s"我的名字是${x}")

    val number: Double = 0.354464
    //    保留两位小数
    printf("%2.2f\n", number)
    //    格式化模板字符串
    println(f"${number}%2.2f\n")
    //     raw不解析后面的%f
    println(raw"${number}%2.2f")
    //     三引号表示字符串，保持多行表示
    var sql =
      s"""
         |select *
         |from
         |  student
         |where
         |  name = ${x}
         |and
         |  age>${number}
         |
         |""".stripMargin

    println(sql)
  }
}
