package com.gyt.sparkstream.project.dao

import com.gyt.sparkstream.project.domain.{CourseClickCount, CourseSearchCount}
import com.gyt.sparkstream.project.utils.HBaseUtils
import org.apache.hadoop.hbase.client.Get
import org.apache.hadoop.hbase.util.Bytes

import scala.collection.mutable.ListBuffer

/**
  * 实战课程数据库访问层
  */
object CourseSearchClickCountDAO {
  val tableName = "course_search_clickcount"
  val cf = "info"
  val qualifer = "click_count"

  /**
    * 保存数据到Hbase
    * @param list CourseClickCount集合
    */
  def save(list: ListBuffer[CourseSearchCount]): Unit = {
    val table = HBaseUtils.getInstance().getTable(tableName)

    for (elem <- list) {
      // incrementColumnValue可以把相同行键到值相加
      table.incrementColumnValue(Bytes.toBytes(elem.day_search_courseId), Bytes.toBytes(cf),
        Bytes.toBytes(qualifer), elem.click_count, false)
    }

  }

  /**
    * 根据rowkey查询
    * @param day_courseId rowkey
    * @return
    */
  def query(day_search_courseId: String): Long = {
    val table = HBaseUtils.getInstance().getTable(tableName)

    val get = new Get(Bytes.toBytes(day_search_courseId))
    val value = table.get(get).getValue(cf.getBytes, qualifer.getBytes)

    if (value == null) {
      0L
    } else {
      Bytes.toLong(value)
    }
  }

  def main(args: Array[String]): Unit = {
    val list = new ListBuffer[CourseSearchCount]
    list.append(CourseSearchCount("20190511_www.baidu.com_1", 2))
    list.append(CourseSearchCount("20190511_www.sougou.com_2", 5))
    save(list)
    println(query("20190511_www.sougou.com_2"))
  }


}
