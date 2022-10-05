package Day01

import java.io.{File, PrintWriter, Writer}
import scala.io.Source

object FileIo {
  def main(args: Array[String]): Unit = {

//    从文件读取数据
    val source = Source.fromFile("E:/新建文本文档 (2).txt")
    println(source.foreach(print))
//    将数据写入文件
    val writer = new PrintWriter(new File("D:\\java\\Scala\\src\\main\\resources\\1.txt"))
    writer.write("hello jly")
    writer.close()
  }
}
