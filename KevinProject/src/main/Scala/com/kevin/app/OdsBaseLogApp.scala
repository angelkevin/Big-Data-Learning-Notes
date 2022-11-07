package com.kevin.app

import com.alibaba.fastjson.serializer.SerializeConfig
import com.alibaba.fastjson.{JSON, JSONObject}
import com.kevin.bean.PageLog
import com.kevin.util.MyKafkaUtils
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * 日志数据消费分流
 * 1.执行环境
 * 2.从kafka中消费数据
 * 3.数据处理
 * 3.1 转换数据结构
 * 转换成JSON
 * 3.2 分流
 */

object OdsBaseLogApp {

  def main(args: Array[String]): Unit = {


    val sparkConf: SparkConf = new SparkConf().setAppName("ods_base_log_app").setMaster("local[2]")
    val ssc = new StreamingContext(sparkConf, Seconds(5))

    //TODO 从kafka中消费数据
    val topic = "topic_log"
    val groupId = "ods_base_log_app"
    val kafkaDStream: InputDStream[ConsumerRecord[String, String]] = MyKafkaUtils.GetKafkaDStream(ssc, "topic_log", "ods_base_log_app")
    //TODO 处理数据
    //TODO 转换数据结构
    val jsonObjectDStream: DStream[JSONObject] = kafkaDStream.map(
      (consumerRecord: ConsumerRecord[String, String]) => {
        val value: String = consumerRecord.value()
        //TODO 转换数据结构
        val jSONObject: JSONObject = JSON.parseObject(value)
        jSONObject
      }
    )
    //TODO 分流
    //  日志数据
    //    页面访问数据
    //      公共字段
    //      曝光数据
    //      时间数据
    //      事件数据
    //      启动数据
    //      错误数据
    //   启动数据
    //      公共字段
    //      启动数据
    //      错误数据
    val DWD_PAGE_LOG_TOPIC = "dwd_page_log_topic"
    val DWD_PAGE_DISPLAY_TOPIC = "dwd_page_display_topic"
    val DWD_PAGE_ACTION_TOPIC = "dwd_page_action_topic"
    val DWD_PAGE_START_TOPIC = "dwd_page_start_topic"
    val DWD_PAGE_ERROR_TOPIC = "dwd_page_error_topic"
    jsonObjectDStream.foreachRDD(
      (rdd: RDD[JSONObject]) => {
        rdd.foreach(
          (jsonObj: JSONObject) => {
            //TODO 分流
            val errObj: JSONObject = jsonObj.getJSONObject("err")
            if (errObj != null) {
              MyKafkaUtils.send(DWD_PAGE_ERROR_TOPIC, jsonObj.toJSONString)
            } else {
              //提取公共字段
              val commonObj: JSONObject = jsonObj.getJSONObject("common")
              val ar: String = commonObj.getString("ar")
              val uid: String = commonObj.getString("uid")
              val os: String = commonObj.getString("os")
              val ch: String = commonObj.getString("ch")
              val isNew: String = commonObj.getString("isNew")
              val md: String = commonObj.getString("md")
              val mid: String = commonObj.getString("mid")
              val vc: String = commonObj.getString("vc")
              val ba: String = commonObj.getString("ba")
              //提取时间戳
              val ts: Long = jsonObj.getLong("ts")
              //页面数据
              val pageObj: JSONObject = jsonObj.getJSONObject("page")
              if (pageObj != null) {
                //提取页面字段
                val page_id: String = pageObj.getString("page_id")
                val pageItem: String = pageObj.getString("item")
                val pageItemType: String = pageObj.getString("item_type")
                val duringTime: Long = pageObj.getLong("during_time")
                val lastPageId: String = pageObj.getString("last_page_id")
                val sourceType: String = pageObj.getString("source_type")
                //发送pagelog
                val pageLog: PageLog = PageLog(mid, uid, ar, ch, isNew, md, os, vc, ba, page_id, lastPageId, pageItem, pageItemType, duringTime, sourceType, ts)
                //提取曝光数据
                MyKafkaUtils.send(DWD_PAGE_LOG_TOPIC,JSON.toJSONString(pageLog,new SerializeConfig(true)))

                //提取时间数据
              }
              //启动数据
            }

          }
        )
      }
    )


    ssc.start()
    ssc.awaitTermination()

  }
}