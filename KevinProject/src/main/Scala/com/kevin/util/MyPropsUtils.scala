package com.kevin.util

import java.util.ResourceBundle

object MyPropsUtils {
  private val bundle: ResourceBundle = ResourceBundle.getBundle("config")

  def apply(propsKey: String) = {
    bundle.getString(propsKey)

  }

  def main(args: Array[String]): Unit = {
    println(MyPropsUtils("BOOTSTRAP_SERVERS_CONFIG"))
  }

}
