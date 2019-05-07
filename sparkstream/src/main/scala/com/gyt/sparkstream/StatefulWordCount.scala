package com.gyt.sparkstream

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

object StatefulWordCount {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[2]").setAppName("StatefulWordCount")
    val ssc = new StreamingContext(sparkConf, Seconds(5))
    ssc.checkpoint("./checkpoint")
    val lines = ssc.socketTextStream("master", 9999)
    val result = lines.flatMap(_.split(" ")).map((_,1))
    val state = result.updateStateByKey(updateFunction _)
    state.print()
    ssc.start()
    ssc.awaitTermination()
  }
  def updateFunction(currentValues: Seq[Int], previousValues: Option[Int]): Option[Int] = {
    val newCount = currentValues.sum
    val previousCount = previousValues.getOrElse(0)
    Some(newCount + previousCount)
  }
}
