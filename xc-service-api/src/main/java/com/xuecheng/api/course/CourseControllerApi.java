package com.xuecheng.api.course;

import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.CategoryNode;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


/**
 * @author 鲜磊 on 2019/4/28
 **/
@Api(value = "课程管理接口",description = "课程页面管理接口，提供增、删、改、查")
public interface CourseControllerApi {

    @ApiOperation("课程计划查询")
    public TeachplanNode findTeachplanList(String courseId);

    @ApiOperation("课程计划添加")
    public ResponseResult addTeachplan(Teachplan teachplan);

    //@ApiOperation("查询我的课程列表")
    public QueryResponseResult findCourseList(int page, int size, CourseListRequest courseListRequest);

}
