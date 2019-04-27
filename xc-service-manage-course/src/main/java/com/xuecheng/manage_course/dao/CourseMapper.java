package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.CourseBase;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * MyBatis接口
 * Created by Administrator.
 */
@Mapper
public interface CourseMapper {

   @Select("select * from course_base where id=#{id}")
   CourseBase findCourseBaseById(String id);
}
