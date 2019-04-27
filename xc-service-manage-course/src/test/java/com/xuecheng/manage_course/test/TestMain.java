package com.xuecheng.manage_course.test;

import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.manage_course.dao.CourseMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author 鲜磊 on 2019/4/27
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestMain {

    @Autowired
    CourseMapper courseMapper;

    @Test
    public void findById(){
        CourseBase courseBaseById = courseMapper.findCourseBaseById("402885816240d276016240f7e5000002");
        System.out.println(courseBaseById);
    }
}
