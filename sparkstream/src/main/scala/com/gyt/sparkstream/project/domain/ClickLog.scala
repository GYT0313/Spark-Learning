package com.gyt.sparkstream.project.domain


/**
  * 清洗后的日志信息(类似Java的JavaBean)
  */

case class ClickLog(ip: String, time: String, courseId: Int,
                    statusCode: Int, referer: String)
