package com.xuecheng.order.mq;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executor;

/**
 * 并行调度
 * @author 鲜磊 on 2019/6/21
 **/
@Configuration
@EnableScheduling
public class AsyncTaskConfig implements SchedulingConfigurer,AsyncConfigurer {

    //线程池数量
    private int corePoolSize = 5;


    public ThreadPoolTaskScheduler taskScheduler(){

        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.initialize(); //初始化
        scheduler.setPoolSize(corePoolSize); // 线程池数量

        return scheduler;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {

        scheduledTaskRegistrar.setScheduler(taskScheduler());
    }

    @Override
    public Executor getAsyncExecutor() {

        return taskScheduler();
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return null;
    }
}
