package com.gyt.sparkstream

import java.sql.DriverManager

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

/*
 * 将统计结果写入MySQL
 */
object ForEachWordCount {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[2]").setAppName("ForEachWordCount")
    val ssc = new StreamingContext(sparkConf, Seconds(5))
    val lines = ssc.socketTextStream("master", 9999)
    val result = lines.flatMap(_.split(" ")).map((_,1)).reduceByKey(_+_)

    //TODO... 将结果写入MySQL
    // 连接不能被序列化
//    result.foreachRDD(rdd => {
//      val connection = createConnection()
//      rdd.foreach(record => {
//        val sql ="INSERT INTO wordcount(word, wordcount) VALUES('" +
//              record._1 + "'," + record._2 + ");"
//        connection.createStatement().execute(sql)
//      })
//    })

    result.foreachRDD( rdd => {
      rdd.foreachPartition( partitionRecords => {
        val connection = createConnection()
        partitionRecords.foreach( record => {
          val sql ="INSERT INTO wordcount(word, wordcount) VALUES('" +
                record._1 + "'," + record._2 + ");"
          connection.createStatement().execute(sql)
        })
        connection.close()
      })
    })

    result.print()
    ssc.start()
    ssc.awaitTermination()
  }

  def createConnection() = {
    Class.forName("com.mysql.jdbc.Driver")
    DriverManager.getConnection("jdbc:mysql://localhost:3306/gyt_wordcount",
      "root", "123456")
  }

}
