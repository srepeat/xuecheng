package com.xuecheng.order.mq;

import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.order.config.RabbitMQConfig;
import com.xuecheng.order.service.TaskService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * 串行任务执行
 * @author 鲜磊 on 2019/6/21
 **/
@Component
public class ChooseCourseTask {

    //打印日志
    private static final Logger LOGGER = LoggerFactory.getLogger(ChooseCourseTask.class);

    @Autowired
    TaskService taskService;


    //监听完成添加课程队列
    @RabbitListener(queues = RabbitMQConfig.XC_LEARNING_FINISHADDCHOOSECOURSE)
    public void receiveFinishChoosecourseTask(XcTask xcTask){
        if(xcTask!=null && StringUtils.isNotEmpty(xcTask.getId())){
            taskService.finishTask(xcTask.getId());
        }
    }

    //测试多个数据调度
    @Scheduled(cron = "0 0/5 * ? * *")
    public void sendChoosecourseTask(){

        //日历类
        Calendar calendar = new GregorianCalendar();
        //设置当前时间
        calendar.setTime(new Date());
        //获取当前时间的前一分钟
        calendar.add(GregorianCalendar.MINUTE,-1);
        //获取时间
        Date time = calendar.getTime();

        List<XcTask> taskList = taskService.findTaskList(time, 10);

        for(XcTask xcTask : taskList){
            //判断获取到的数据是否大于0
            if(taskService.updateTaskVersion(xcTask.getId(),xcTask.getVersion())>0){
                //获取交换机
                String ex = xcTask.getMqExchange();
                //获取routingkey
                String routingkey = xcTask.getMqRoutingkey();
                //发送消息
                taskService.publish(xcTask,ex,routingkey);
            }
        }
    }

    //@Scheduled(cron = "0/1 * * * * * ") //cron表达式
    public void taskTest(){
        LOGGER.info("测试任务一开始。。。。。。");

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LOGGER.info("测试任务一结束。。。。。。");
    }
}
