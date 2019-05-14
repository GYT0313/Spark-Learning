package com.gyt.sparkstream

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * 统计黑名单 transform
  *
  * 输入： DStream
  * 20190507,zs
  * 20190507,ls
  * 20190507,ww
  * 转换为: (zs, (20190507,zs))(ls, (20190507,ls))(ww, (20190507,ww))
  *
  * 黑名单: RDD
  * ww
  * ls
  * 转换为: (zs, ture)(ls, true)
  *
  *
  * leftJoin:
  * (zs, ((20190507,zs), true))
  * (ls, ((20190507,ls), true))
  * (ww, ((20190507,ww), false))  --> 输出
  */

object BlankFilter {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf()
    sparkConf.setAppName("BlankFilter").setMaster("local[2]")
    val ssc = new StreamingContext(sparkConf, Seconds(4))

    val blanks = List("zs", "ls")
    val blanksRDD = ssc.sparkContext.parallelize(blanks).map(x => (x, true))

    val lines = ssc.socketTextStream("master", 9999)
    /*
     * lines = 20190507,zs
     * leftOuterJoin = (zs, ((20190507,zs), true))
     */
    val whiteVisitor = lines.map(x => (x.split(",")(1), x)).transform( rdd => {
      rdd.leftOuterJoin(blanksRDD)
        .filter(x => x._2._2.getOrElse(false) != true)
        .map(x => x._2._1)
    })

    whiteVisitor.print()
    ssc.start()
    ssc.awaitTermination()
  }
}
