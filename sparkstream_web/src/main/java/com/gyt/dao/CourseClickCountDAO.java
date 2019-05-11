package com.gyt.dao;


import com.gyt.domain.CourseClickCount;
import com.gyt.utils.HBaseUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 实战课程访问数量访问层
 */

@Component
public class CourseClickCountDAO {

    public List<CourseClickCount> query(String day) throws Exception {
        List<CourseClickCount> list = new ArrayList<>();

        // 到HBase中根据day获取数据
        Map<String, Long> map = HBaseUtils.getInstance().query("course_clickcount", day);

        for (Map.Entry<String, Long> entry : map.entrySet()) {
            CourseClickCount model = new CourseClickCount();
            model.setName(entry.getKey());
            model.setValue(entry.getValue());
            list.add(model);
        }

        return list;
    }

}
