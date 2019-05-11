package com.gyt.domain;

import com.gyt.dao.CourseClickCountDAO;

import java.util.List;

public class CourseClickCountDaoTest {

    public static void main(String[] args) throws Exception{
        CourseClickCountDAO dao = new CourseClickCountDAO();
        List<CourseClickCount> list = dao.query("20190511");
        for (CourseClickCount res : list) {
            System.out.println(res.getName() + " : " + res.getValue());
        }
    }

}
