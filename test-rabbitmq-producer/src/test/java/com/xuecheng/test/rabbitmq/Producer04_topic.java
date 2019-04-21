package com.xuecheng.test.rabbitmq;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * topic生产者测试
 * @author 鲜磊 on 2019/4/21
 **/
public class Producer04_topic {
    //队列名称
    private static final String QUEUE_INFORM_EMAIL = "queue_inform_email";
    private static final String QUEUE_INFORM_SMS = "queue_inform_sms";
    private static final String EXCHANGE_TOPICS_INFORM="exchange_topics_inform";
    private static final String ROUTINGKEY_EMAIL="inform.#.email.#";
    private static final String ROUTINGKEY_SMS="inform.#.sms.#";

    public static void main(String[] args) {
        Connection connection = null;
        Channel channel = null;
        //创建rabbitmq连接工厂
        ConnectionFactory connectionFactory = ProducerBean.connectionFactoryUtils();
       /* ConnectionFactory connectionFactory = new ConnectionFactory();
        //设置用户名、密码、端口、host等基本属性
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        //设置虚拟主机的路径，/，虚拟主机等同于一个独立的服务,可以设置多个虚拟主机
        connectionFactory.setVirtualHost("/");*/
        try{
            //创建一个MQ新的连接
            connection = connectionFactory.newConnection();
            //创建一个通道
            channel = connection.createChannel();

            //声明交换机
            channel.exchangeDeclare(EXCHANGE_TOPICS_INFORM, BuiltinExchangeType.TOPIC);
            //声明队列
            channel.queueDeclare(QUEUE_INFORM_EMAIL,true,false,false,null);
            channel.queueDeclare(QUEUE_INFORM_SMS,true,false,false,null);

            //绑定交换机声明路由key
            channel.queueBind(QUEUE_INFORM_EMAIL,EXCHANGE_TOPICS_INFORM,ROUTINGKEY_EMAIL);
            channel.queueBind(QUEUE_INFORM_SMS,EXCHANGE_TOPICS_INFORM,ROUTINGKEY_SMS);

            //发送消息 声明路由key
            for(int i=0;i<5;i++){
                String message = "send email inform message to user"+i;
                channel.basicPublish(EXCHANGE_TOPICS_INFORM,"inform.email",null,message.getBytes());
                System.out.println("send is message:"+message);
            }

            for(int i=0;i<5;i++){
                String message = "send sms inform message to user"+i;
                channel.basicPublish(EXCHANGE_TOPICS_INFORM,"inform.sms",null,message.getBytes());
                System.out.println("send is message:"+message);
            }
            for(int i=0;i<5;i++){
                String message = "send sms and email inform message to user"+i;
                channel.basicPublish(EXCHANGE_TOPICS_INFORM,"inform.sms.email",null,message.getBytes());
                System.out.println("send is message:"+message);
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(channel != null){
                try {
                    channel.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            if(connection != null){
                try {
                    connection.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
