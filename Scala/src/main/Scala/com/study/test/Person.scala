package com.study.test

import scala.beans.BeanProperty

class Person (){
  @BeanProperty
  var name : String =""
  @BeanProperty
  var age : Int = 0
  @BeanProperty
  var sex:String=""
  def this(name:String,age:Int,sex:String)={
    this()
    this.name=name
    this.age=age;
    this.sex=sex;

  }
}
class Teacher extends Person {

  def say(): Unit = {

    println(name)

  }

}
object Person{
  def main(args: Array[String]): Unit = {
    val person = new Person("zkw",10,"s")


    val teacher = new Teacher
    teacher.setName("jly")
    teacher.say()

    println(person.getName,person.getAge,person.getSex)

  }
}

