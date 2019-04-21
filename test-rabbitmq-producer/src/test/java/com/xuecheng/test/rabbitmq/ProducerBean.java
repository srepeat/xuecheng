package com.xuecheng.test.rabbitmq;

import com.rabbitmq.client.ConnectionFactory;

/**
 * 连接工厂基本属性
 * @author 鲜磊 on 2019/4/21
 **/

public class ProducerBean {

    //抽取连接工厂基本属性
    public static ConnectionFactory connectionFactoryUtils(){
        ConnectionFactory connectionFactory = new ConnectionFactory();
        //设置用户名、密码、端口、host等基本属性
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        //设置虚拟主机的路径，/，虚拟主机等同于一个独立的服务,可以设置多个虚拟主机
        connectionFactory.setVirtualHost("/");

        return connectionFactory;
    }
}
