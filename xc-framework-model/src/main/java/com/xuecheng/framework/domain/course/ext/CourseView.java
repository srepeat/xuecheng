package com.xuecheng.framework.domain.course.ext;

import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.CoursePic;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * 课程响应结果类
 * @author 鲜磊 on 2019/5/8
 **/
@Data
@NoArgsConstructor
@ToString
public class CourseView implements Serializable {

    //基础信息
    private CourseBase courseBase;
    //课程营销
    private CourseMarket courseMarket;
    //课程图片
    private CoursePic coursePic;
    //教学计划
    private TeachplanNode teachplanNode;

}
