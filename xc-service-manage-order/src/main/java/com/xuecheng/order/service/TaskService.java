package com.xuecheng.order.service;

import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.framework.domain.task.XcTaskHis;
import com.xuecheng.order.dao.XcTaskHisRepository;
import com.xuecheng.order.dao.XcTaskRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
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
    XcTaskHisRepository xcTaskHisRepository;

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
    @Transactional
    public void publish(XcTask xcTask,String ex,String routingKey){
        //查询id是否存在
        Optional<XcTask> taskOptional = xcTaskRepository.findById(xcTask.getId());
        if(taskOptional.isPresent()){
            //发送消息
            rabbitTemplate.convertAndSend(ex,routingKey,xcTask);
            XcTask xcTask1 = taskOptional.get();
            //更新时间
            xcTask.setUpdateTime(new Date());
            //保存到数据库中
            xcTaskRepository.save(xcTask1);
        }
    }

    //更新版本
    @Transactional
    public int updateTaskVersion(String id,int version){
        //调用dao方法，如果大于1表示可以更新，小于表示获取不到
        int count = xcTaskRepository.updateTaskVersion(id, version);

        return count;
    }

    //删除任务
    @Transactional
    public void finishTask(String taskId){
        //查询任务是否存在
        Optional<XcTask> xcTaskOptional = xcTaskRepository.findById(taskId);
        if(xcTaskOptional.isPresent()){
            //未完成任务
            XcTask xcTask = xcTaskOptional.get();
            xcTask.setDeleteTime(new Date());

            //历史任务
            XcTaskHis xcTaskHis = new XcTaskHis();
            BeanUtils.copyProperties(xcTask,xcTaskHis);

            //保存历史任务
            xcTaskHisRepository.save(xcTaskHis);
            //删除未完成任务
            xcTaskRepository.delete(xcTask);
        }

    }

}













