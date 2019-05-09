package com.xuecheng.govern.center;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @author 鲜磊 on 2019/5/5
 **/
@EnableEurekaServer  //暴露EurekaServer服务
@SpringBootApplication
public class GovernCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(GovernCenterApplication.class,args);
    }
}
