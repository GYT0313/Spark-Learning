package com.gyt.sparkstream

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

object StatefulWordCount {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[2]").setAppName("StatefulWordCount")
    val ssc = new StreamingContext(sparkConf, Seconds(5))
    // 有状态的操作, 必须设置检查点(一般设置到HDFS, 这里设置到当前./checkpoint)
    ssc.checkpoint("./checkpoint")
    val lines = ssc.socketTextStream("master", 9999)
    val result = lines.flatMap(_.split(" ")).map((_,1))
    val state = result.updateStateByKey(updateFunction _) // _ 是隐式转换
    state.print()
    ssc.start()
    ssc.awaitTermination()
  }
  def updateFunction(currentValues: Seq[Int], previousValues: Option[Int]): Option[Int] = {
    val newCount = currentValues.sum
    val previousCount = previousValues.getOrElse(0) // 有值返回,没有返回0
    Some(newCount + previousCount)
  }
}
