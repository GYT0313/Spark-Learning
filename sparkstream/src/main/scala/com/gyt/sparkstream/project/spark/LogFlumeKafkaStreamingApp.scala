package com.gyt.sparkstream.project.spark

import com.gyt.sparkstream.project.dao.{CourseClickCountDAO, CourseSearchClickCountDAO}
import com.gyt.sparkstream.project.domain.{ClickLog, CourseClickCount, CourseSearchCount}
import com.gyt.sparkstream.project.utils.DateUtils
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}

import scala.collection.mutable.ListBuffer

object LogFlumeKafkaStreamingApp {
  def main(args: Array[String]): Unit = {
    if (args.length < 3) {
      System.err.println("Usage: LogFlumeKafkaStreamingApp <brokers> <groupId> <topics>")
      System.exit(1)
    }

    // Create context with 60 second batch interval
    val sparkConf = new SparkConf().setAppName("LogFlumeKafkaStreamingApp")//.setMaster("local[3]")
    val ssc = new StreamingContext(sparkConf, Seconds(60))

    // Create direct kafka stream with brokers and topics
    val Array(brokers, groupId, topics) = args
    val topicsSet = topics.split(",").toSet
    val kafkaParams = Map[String, Object](
      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> brokers,
      ConsumerConfig.GROUP_ID_CONFIG -> groupId,
      ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer],
      ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer])
    // Create direct inputStream
    val messages = KafkaUtils.createDirectStream[String, String](
      ssc,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](topicsSet, kafkaParams))
    // 测试一: 数据接收
    //messages.map(_.value()).count().print()

    // 测试二: 数据清洗
    val logs = messages.map(_.value())
    val cleanData = logs.map(lines => {
      val infos = lines.split("\t")
      val url = infos(2).split(" ")(1)
      var courseId = 0
      // infos(2) = "GET /class/131.html HTTP/1.1"
      // url = /class/131.html
      if (url.startsWith("/class")) {
        val courseIdHTML = url.split("/")(2)
        courseId = courseIdHTML.substring(0, courseIdHTML.lastIndexOf("."))
          .toInt
      }
      ClickLog(infos(0), DateUtils.parseToMinute(infos(1)), courseId,
        infos(3).toInt, infos(4))
    }).filter(clickLog => clickLog.courseId != 0)

    // ClickLog(124.88.30.10,20190510210401,131,200,https://www.so.com/s?&q=Storm实战)
    //cleanData.print()

    // 测试三: 统计今天到现在为止实战课程统计量
    cleanData.map(x => {
      // HBase: rowkey  20190510_11
      (x.time.substring(0, 8) + "_" + x.courseId, 1)
    }).reduceByKey(_ + _).foreachRDD(rdd => {
      // 按照分区写入
      rdd.foreachPartition(partitionRecords => {
        // 将分区作为一批数据
        val list = new ListBuffer[CourseClickCount]
        partitionRecords.foreach(record => {
          list.append(CourseClickCount(record._1, record._2))
        })
        CourseClickCountDAO.save(list)
      })
    })

    // 测试四: 统计搜索引擎过来到点击量
    cleanData.map(x => {
      /**
        * https://www.so.com/s?&q=Storm实战
        * =>
        * https:/www.so.com/s?&q=Storm实战
        */
      val referer = x.referer.replaceAll("//", "/")
      val splits = referer.split("/")
      var host = ""
      // 过滤 -
      if (splits.length > 2) {
        host = splits(1)
      }
      (host, x.courseId, x.time.substring(0, 8))
    }).filter(_._1 != "").map(x => {
      // (20190511_www.baidu.com_121, 1)
      (x._3 + "_" + x._1 + "_" + x._2, 1)
    }).reduceByKey(_ + _).foreachRDD(rdd => {
      // 按照分区写入
      rdd.foreachPartition(partitionRecords => {
        // 将分区作为一批数据
        val list = new ListBuffer[CourseSearchCount]
        partitionRecords.foreach(record => {
          list.append(CourseSearchCount(record._1, record._2))
        })
        CourseSearchClickCountDAO.save(list)
      })
    })


    ssc.start()
    ssc.awaitTermination()
  }
}
