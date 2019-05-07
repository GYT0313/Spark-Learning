package com.gyt.sparkstream

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}


object BlankFilter {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf()
    sparkConf.setMaster("local[2]").setAppName("BlankFilter")
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
