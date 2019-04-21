package com.xuecheng.test.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * 生产者(rabbitmq) work queue工作队列
 * 测试队列生产者程序
 * @author 鲜磊 on 2019/4/20
 * 发送端操作流程
    创建连接
    创建通道
    声明队列
    发送消息
 **/
public class Producer01 {

    //消息队列名称
    private static  final  String QUEUE = "helloworld";

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

        try {
            //创建一个与rabbitmq的连接
            connection = connectionFactory.newConnection();
            //创建一个Exchange的连接，每个连接可以创建多个通道，每个通道代表一个会话任务
            channel = connection.createChannel();
            //声明队列
            /*
            * queueDeclare("", false, true, true, (Map)null);
            * params1:队列名称
            * params2:是否持久化，如果持久化，mq重启还存在
            * params3:是否独占连接，队列中允许在该链接中访问，如果connection关闭连接，队列规则自动删除如果讲此参数设置为true可用于临时队列的创建
            * params4:自动删除，队列不再使用时是否自动删除此队列，如果将此参数设置为true就可以实现临时队列，队列不用就自动删除
            * params5:参数可以设置为队列的扩展参数， 例如：存活时间
            * */
            channel.queueDeclare(QUEUE,true,false,false,null);
            //消息发布
            /*
            * params1:交换机，如果不指定将使用MQ默认交换机，设置为空字符串""
            * params2:routingKey 交换机根据key来将消息转发到指定队列，如果使用默认交换机，key设置为队列的名称
            * params3:消息属性
            * params4:消息内容
            * */
            String message = "Hello World 江苏工院";
            channel.basicPublish("",QUEUE,null,message.getBytes());
            //消息结束
            System.out.println("send message is:"+message);

        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            try{
                // 关闭连接 先关闭channel 然后关闭connection
                if(channel != null){
                    channel.close();
                }

                if(connection != null){
                    connection.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }
}
