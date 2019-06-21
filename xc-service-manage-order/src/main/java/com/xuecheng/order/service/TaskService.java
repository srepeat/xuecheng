package com.xuecheng.order.service;

import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.order.dao.XcTaskRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 任务调度
 * @author 鲜磊 on 2019/6/21
 **/
@Service
public class TaskService {

    @Autowired
    XcTaskRepository xcTaskRepository;

    @Autowired
    RabbitTemplate rabbitTemplate;


    //查询多个数据
    public List<XcTask> findTaskList(Date updateTime,int size){

        //分页对象
        Pageable pageable = new PageRequest(0,size);

        Page<XcTask> tasks = xcTaskRepository.findByUpdateTimeBefore(pageable, updateTime);

        List<XcTask> content = tasks.getContent();

        return content;

    }

    //发送消息
    public void publish(XcTask xcTask,String ex,String routingKey){

        //查询id是否存在
        Optional<XcTask> taskOptional = xcTaskRepository.findById(xcTask.getId());
        if(taskOptional.isPresent()){
            //发送消息
            rabbitTemplate.convertAndSend(ex,routingKey,xcTask);
            XcTask xcTask1 = taskOptional.get();
            //更新时间
            xcTask.setUpdateTime(new Date());
        }
    }

}













