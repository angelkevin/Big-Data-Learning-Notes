package Day01

class Student(name: String, age: Int) {
  def printInfo(): Unit = {
    println(name + age + Student.school)
  }
}

object Student {
  val school: String = "清华"

  def main(args: Array[String]): Unit = {
    val zkw = new Student("zkw", 21)
    zkw.printInfo()
  }
}

