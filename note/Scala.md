# Scala变量与常量

>var 变量名 [:变量类型] = 初始值    var i :Int = 10   **变量**
>
>val 变量名 [:变量类型] = 初始值    val i :Int = 10  **常量**

```scala
var i :Int = 10  
val i :Int = 10  
```

# IO输入和输出

## 控制台输入输出

```scala
package Day01

import scala.io.StdIn

object IO {
  def main(args: Array[String]): Unit = {
    //输入信息
    val name = StdIn.readLine()
    val age = StdIn.readInt()
    println(s"我叫${name},今年${age}岁")
  }
}
```

## 文件的写入和写出

```scala
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
```

# Scala数据类型

![image-20221005141534508](C:\Users\22154\AppData\Roaming\Typora\typora-user-images\image-20221005141534508.png)

所有的数据都是对象：都是Any的子类

## 数值类型（Byte、Short、Int、Long、Float、Double）

| 数据类型 |             描述             |
| :------: | :--------------------------: |
| Byte[1]  | 一个字节，8位有符号补码整数  |
| Short[2] | 两个字节，16位有符号补码整数 |
|  Int[4]  | 四个字节，32位有符号补码整数 |
| Long[8]  | 八个字节，64位有符号补码整数 |

整数默认的数据类型是Int，定义Long类型需要在数值后面加一个L

浮点数默认的数据类型是Double，定义Float类型需要在数值后面加一个F

## 字符类型

Char,用单引号表示

'\t'制表符，\n'换行符。反斜杠表示转义。字符底层保存的是ASCII码

## 空类型（Unit、Null、Nothing）

| 数据类型 | 描述                                                         |
| :------: | ------------------------------------------------------------ |
|   Unit   | 表示无值和其他语言中的void等同，用作不返回任何结果的方法的结果类型，unit只有一个实例值写作（） |
|   Null   | null，Null只有一个实例值就是null                             |
| Nothing  | Nothing类型在Scala的类层级最低端，是任何其他类型的子类型，当一个函数我们不确定有没有正常的返回值。可以用Nothing来指定返回类型，这样有一个好处，就是我们可以把返回的值（异常）赋值给其他函数或者变量（兼容性） |

```scala
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

```

#  类型转换

1.自动提升原则：有多种类型的数据混合运算的时候，系统首先自动将所有数据转到精度大的那个数据类型；

2.把精度大的数值类型赋给精度精度小的数值类型会报错；

3.（byte，short）和char之间不会相互自动转换；

4.byte，short，char他们三者可以计算，在计算的时候自动转换为int类型；

```scala
package Day01

object DataTypeConversion {
  def main(args: Array[String]): Unit = {
    val a1 :Byte=10
    val a2 : Long=255L
    val result = a1+a2
    println(result)
    val a3 :Int = a1
  }
}
```



# for循环

```scala
package Day01

import scala.collection.immutable
import scala.language.postfixOps

object forfor {
  def main(args: Array[String]): Unit = {

    //[1,10]
    for (i <- 1 to 10) {
      println(i)

    }
    //[1,10)
    for (i <- Range(1, 10)) {
      println(i)
    }
    //[1,10)
    for (i <- 1 until 10) {
      println(i)
    }

    //循环守卫，满足if进行相当于continue
    for (i <- 1 until 10 if i != 5) {
      println(i)
    }
    //循环步长
    for (i <- 1 to 10 by 1) {
      println(i)
    }
    //倒序输出
    for (i <- 1 to 10 reverse) {
      println(i)
    }

    for (i <- 1 to (9)) {
      for (j <- 1 to (i)) {
        print(s"${j} * ${i} = " + i * j + "\t")
      }
      println("\n")
    }
    //简写
    for (i <- 1 to (9); j <- 1 to (i)) {
      print(s"${j} * ${i} = " + i * j + "\t")
      if (i == j) println("\n")
    }
    //循环引入变量
    for (i <- 1 to 10; j = 10 - i) {
      println(i, j)
    }
    //循环返回值
    val units: immutable.IndexedSeq[Int] = for (i <- 1 to 10) yield i
    println(units)

  }

}

```

