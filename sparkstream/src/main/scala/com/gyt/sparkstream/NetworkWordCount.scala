package com.gyt.sparkstream

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

object NetworkWordCount {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf()
    sparkConf.setMaster("local[2]").setAppName("NetworkWordCount")
    val ssc = new StreamingContext(sparkConf, Seconds(4))
    val lines = ssc.socketTextStream("master", 9999)
    val results = lines.flatMap(_.split(" ")).map((_, 1)).reduceByKey(_+_)
    results.print()
    ssc.start()
    ssc.awaitTermination()
  }
}
