package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * MyBatis接口
 * Created by Administrator.
 */
@Mapper
public interface CourseMapper {

   @Select("select * from course_base where id=#{id}")
   CourseBase findCourseBaseById(String id);

   //分页查询
   List<CourseInfo> findCourseListPage(CourseListRequest courseListRequest);

   //@Select("select * from course_base")
   List<CourseBase> findCourseList();
}
