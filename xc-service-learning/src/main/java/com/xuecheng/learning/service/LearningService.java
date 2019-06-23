package com.xuecheng.learning.service;

import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.domain.learning.XcLearningCourse;
import com.xuecheng.framework.domain.learning.response.GetMediaResult;
import com.xuecheng.framework.domain.learning.response.LearningCode;
import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.framework.domain.task.XcTaskHis;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.exception.ExceptionCatch;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.learning.client.CourseSearchClient;
import com.xuecheng.learning.dao.XcLearningCourseRepository;
import com.xuecheng.learning.dao.XcTaskHisRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

/**
 * @author 鲜磊 on 2019/6/2
 **/
@Service
public class LearningService {

    @Autowired
    CourseSearchClient courseSearchClient;

    @Autowired
    XcLearningCourseRepository xcLearningCourseRepository;

    @Autowired
    XcTaskHisRepository xcTaskHisRepository;

    //获取课程学习地址（视频播放地址）
    public GetMediaResult getmedia(String courseId, String teachplanId) {
        //校验学生的学生权限...

        //远程调用搜索服务查询课程计划所对应的课程媒资信息
        TeachplanMediaPub teachplanMediaPub = courseSearchClient.getmedia(teachplanId);
        if(teachplanMediaPub == null || StringUtils.isEmpty(teachplanMediaPub.getMediaUrl())){
            //获取学习地址错误
            ExceptionCast.cast(LearningCode.LEARNING_GETMEDIA_ERROR);
        }
        return new GetMediaResult(CommonCode.SUCCESS,teachplanMediaPub.getMediaUrl());
    }


    //添加课程
    public ResponseResult addcourse(String userId, String courseId,String valid,Date
            startTime,Date endTime,XcTask xcTask){

        //校验是否都为null的情况下
        if(StringUtils.isEmpty(courseId)){
            ExceptionCast.cast(LearningCode.LEARNING_GETMEDIA_ERROR);
        }

        if(StringUtils.isEmpty(userId)){
            ExceptionCast.cast(LearningCode.CHOOSECOURSE_USERISNULL);
        }

        if(xcTask == null || StringUtils.isEmpty(xcTask.getId())){
            ExceptionCast.cast(LearningCode.CHOOSECOURSE_TASKISNULL);
        }

        //查询历史任务
        XcLearningCourse xcLearningCourse = xcLearningCourseRepository.findXcLearningCourseByUserIdAndCourseId(userId, courseId);

        //选课存在就更新日期
        if(xcLearningCourse != null){
            //课程开始时间
            xcLearningCourse.setStartTime(new Date());
            //课程结束时间
            xcLearningCourse.setEndTime(new Date());
            //课程的状态
            xcLearningCourse.setStatus("501001");
            //保存课程
            xcLearningCourseRepository.save(xcLearningCourse);
        }else {
            //没有就添加课程
            xcLearningCourse = new XcLearningCourse();
            //用户id
            xcLearningCourse.setUserId(userId);
            //课程id
            xcLearningCourse.setCourseId(courseId);
            //课程开始时间
            xcLearningCourse.setStartTime(new Date());
            //课程结束时间
            xcLearningCourse.setEndTime(new Date());
            //课程的状态
            xcLearningCourse.setStatus("501001");
            //保存课程
            xcLearningCourseRepository.save(xcLearningCourse);
        }

        //向历史人物表中插入记录
        Optional<XcTaskHis> taskHisOptional = xcTaskHisRepository.findById(xcTask.getId());

        //如果查询不到就添加
        if(!taskHisOptional.isPresent()){
            //创建新的对象，添加历史任务
            XcTaskHis xcTaskHis = new XcTaskHis();
            //使用流拷贝的方式实现
            BeanUtils.copyProperties(xcTask,xcTaskHis);
            //保存记录
            xcTaskHisRepository.save(xcTaskHis);
        }
        //成功返回
        return new ResponseResult(CommonCode.SUCCESS);

    }



}
