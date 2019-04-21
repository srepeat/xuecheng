package com.xuecheng.test.rabbitmq;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * 生产者
 * publish/subscribe订阅发布模式
 * @author 鲜磊 on 2019/4/21
 **/
public class Producer02_publish {

    //定义两个消息队列，一个交换机
    private static final String QUEUE_INFORM_EMAIL = "queue_inform_email";
    private static final String QUEUE_INFORM_SMS = "queue_inform_sms";
    private static final String EXCHANGE_FANOUT_INFORM="exchange_fanout_inform";

    public static void main(String[] args) {
        Connection connection = null;
        Channel channel = null;
        //创建rabbitmq连接工厂
        ConnectionFactory connectionFactory = new ConnectionFactory();
        //设置用户名、密码、端口、host等基本属性
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        //设置虚拟主机的路径，/，虚拟主机等同于一个独立的服务,可以设置多个虚拟主机
        connectionFactory.setVirtualHost("/");

        try{
            //创建一个连接
            connection = connectionFactory.newConnection();
            //创建一个连接通道
            channel = connection.createChannel();

            //声明交换机
            //参数 订阅发布 工作队列 路由 topic通配  header转发
            channel.exchangeDeclare(EXCHANGE_FANOUT_INFORM, BuiltinExchangeType.FANOUT);


            //声明队列
            channel.queueDeclare(QUEUE_INFORM_EMAIL,true,false,false,null);
            channel.queueDeclare(QUEUE_INFORM_SMS,true,false,false,null);

            //绑定交换机
            //参数String queue, String exchange, String routingKey
            /**
             * 队列名称，交换机名称，路由key
             */
           channel.queueBind(QUEUE_INFORM_EMAIL,EXCHANGE_FANOUT_INFORM,"");
           channel.queueBind(QUEUE_INFORM_SMS,EXCHANGE_FANOUT_INFORM,"");

           //发送消息
            for(int i=0;i<5;i++){
                String message = "inform to user";
                //参数 路由  队列名称 扩展属性 消息内容
                channel.basicPublish(EXCHANGE_FANOUT_INFORM,"",null,message.getBytes());
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
