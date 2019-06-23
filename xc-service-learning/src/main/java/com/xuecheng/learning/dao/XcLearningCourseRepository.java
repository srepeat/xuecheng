package com.xuecheng.learning.dao;

import com.xuecheng.framework.domain.learning.XcLearningCourse;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 选课id
 * @author 鲜磊 on 2019/6/23
 **/
public interface XcLearningCourseRepository extends JpaRepository<XcLearningCourse,String> {

    //查询用户id和课程id
    XcLearningCourse findXcLearningCourseByUserIdAndCourseId(String userId,String courseId);
}
