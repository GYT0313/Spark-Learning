package com.gyt.sparkstream;

import com.gyt.dao.CourseClickCountDAO;
import com.gyt.domain.CourseClickCount;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * web层
 */

@RestController
public class StatApp {

    private static Map<String, String> course = new HashMap<>();

    /**
     * 课程ID 与名称到映射关系
     */
    static {
        course.put("112", "Spark SQL实战");
        course.put("128", "大数据面试");
        course.put("145", "深度学习");
        course.put("116", "Hadoop基础");
        course.put("131", "Storm实战");
        course.put("130", "Spark Streaming实战");
    }

    @Autowired
    CourseClickCountDAO courseClickCountDAO;

//    @RequestMapping(value = "/course_clickcount_hbase", method = RequestMethod.GET)
//    public ModelAndView CourseClickCount() throws Exception {
//
//        ModelAndView view = new ModelAndView("index");
//
//        List<CourseClickCount> list = courseClickCountDAO.query("20190511");
//
//        for (CourseClickCount model : list) {
//            // 根据课程ID 获得课程名称, 并修改model的name属性
//            // model.getName().substring(9) => 20190511_112
//            model.setName(course.get(model.getName().substring(9)));
//        }
//        JSONArray json = JSONArray.fromObject(list);
//        view.addObject("data_json", json);
//
//        return view;
//    }

    @RequestMapping(value = "/course_clickcount_dynamic", method = RequestMethod.POST)
    @ResponseBody
    public List<CourseClickCount> CourseClickCount() throws Exception {

        List<CourseClickCount> list = courseClickCountDAO.query("20190511");

        for (CourseClickCount model : list) {
            // 根据课程ID 获得课程名称, 并修改model的name属性
            // model.getName().substring(9) => 20190511_112
            model.setName(course.get(model.getName().substring(9)));
        }

        return list;
    }

    @RequestMapping(value = "/echarts", method = RequestMethod.GET)
    public ModelAndView echarts() {
        return new ModelAndView("echarts");
    }

}
