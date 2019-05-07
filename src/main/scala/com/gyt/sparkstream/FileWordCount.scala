package com.gyt.sparkstream

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

object FileWordCount {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf()
    sparkConf.setMaster("local").setAppName("FileWordCount")
    val ssc = new StreamingContext(sparkConf, Seconds(4))
    val lines = ssc.textFileStream("file:///home/hadoop/spark-2.4.0/test")
    val results = lines.flatMap(_.split(" ")).map((_, 1)).reduceByKey(_+_)
    results.print()
    ssc.start()
    ssc.awaitTermination()
  }
}
