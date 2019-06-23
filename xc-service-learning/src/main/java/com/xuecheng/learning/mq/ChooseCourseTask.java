package com.xuecheng.learning.mq;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.learning.config.RabbitMQConfig;
import com.xuecheng.learning.service.LearningService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * 监听队列消息
 * @author 鲜磊 on 2019/6/23
 **/
@Component
public class ChooseCourseTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChooseCourseTask.class);

    @Autowired
    LearningService learningService;

    @Autowired
    RabbitTemplate rabbitTemplate;


    //接收选课任务
    /*@RabbitListener(queues = RabbitMQConfig.XC_LEARNING_ADDCHOOSECOURSE)
    public void receiveChoosecourseTask(XcTask xcTask){
        LOGGER.info("receive choose course task,taskId:{}",xcTask.getId());
        //接收到的消息id
        //String id = xcTask.getId();

        //添加选课
        try {
            String requestBody = xcTask.getRequestBody();
            //转换为JSON对象
            Map map = JSON.parseObject(requestBody, Map.class);
            String userId = (String) map.get("userId");
            String courseId = (String) map.get("courseId");

            //定义开始、结束日期
            Date startTime = null;
            Date endTime = null;

            //格式化日期
            SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY‐MM‐dd HH:mm:ss");

            if(map.get("startTime") != null){

                    startTime =dateFormat.parse((String) map.get("startTime"));
                }
            if(map.get("endTime")!=null){
                endTime =dateFormat.parse((String) map.get("endTime"));
            }

            //完成选课
            ResponseResult addcourse = learningService.addcourse(userId, courseId, null, startTime, endTime, xcTask);

            //选课成功发送响应消息
            if(addcourse.isSuccess()){
                //添加选课成功，要向mq发送完成添加选课的消息
                rabbitTemplate.convertAndSend(RabbitMQConfig.XC_LEARNING_ADDCHOOSECOURSE,RabbitMQConfig.XC_LEARNING_FINISHADDCHOOSECOURSE_KEY,xcTask);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    @RabbitListener(queues = RabbitMQConfig.XC_LEARNING_ADDCHOOSECOURSE)
    public void receiveChoosecourseTask(XcTask xcTask){

        //取出消息的内容
        String requestBody = xcTask.getRequestBody();
        Map map = JSON.parseObject(requestBody, Map.class);
        String userId = (String) map.get("userId");
        String courseId = (String) map.get("courseId");
        //解析出valid, Date startTime, Date endTime...

        //添加选课
        //String userId, String courseId, String valid, Date startTime, Date endTime, XcTask xcTask
        ResponseResult addcourse = learningService.addcourse(userId, courseId, null, null, null, xcTask);
        if(addcourse.isSuccess()){
            //添加选课成功，要向mq发送完成添加选课的消息
            rabbitTemplate.convertAndSend(RabbitMQConfig.EX_LEARNING_ADDCHOOSECOURSE,RabbitMQConfig.XC_LEARNING_FINISHADDCHOOSECOURSE_KEY,xcTask);
        }
    }
}
