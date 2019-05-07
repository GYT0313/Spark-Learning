package com.gyt.sparkstream

import java.sql.{Connection, DriverManager}

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

/*
 * 将统计结果写入MySQL
 */
object ForEachWordCount2 {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[2]").setAppName("ForEachWordCount2")
    val ssc = new StreamingContext(sparkConf, Seconds(5))
    val lines = ssc.socketTextStream("master", 9999)
    val result = lines.flatMap(_.split(" ")).map((_,1)).reduceByKey(_+_)

    //TODO... 将结果写入MySQL
    result.foreachRDD( rdd => {
      rdd.foreachPartition( partitionRecords => {
        val connection = createConnection()
        partitionRecords.foreach( record => {
          // 已经插入MySQL, 更新操作
          if (hasAlredyStored(record._1, connection)) {
            val sql = "UPDATE wordcount SET wordcount = wordcount + " +
                  record._2 + " WHERE word = '" + record._1 + "';"
            connection.createStatement().executeUpdate(sql)
          } else {
            val sql = "INSERT INTO wordcount(word, wordcount) VALUES('" +
              record._1 + "'," + record._2 + ");"
            connection.createStatement().execute(sql)
          }
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

  def hasAlredyStored(word: String, connection: Connection): Boolean = {
    val sql = "SELECT * FROM wordcount WHERE word = '" + word + "';"
    val results = connection.createStatement().executeQuery(sql)
    if (results.next()) return true
    return false
  }

}
