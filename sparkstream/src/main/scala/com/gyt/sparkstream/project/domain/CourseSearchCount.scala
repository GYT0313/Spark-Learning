package com.gyt.sparkstream.project.domain


/**
  * 实战课程从搜索引擎过来到点击数量实体类
  * @param day_search_courseId
  * @param click_count
  */

case class CourseSearchCount(day_search_courseId: String, click_count: Long)
