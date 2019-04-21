package com.xuecheng.test.rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * 工作队列
 * mq消费端
 * @author 鲜磊 on 2019/4/20
 * 接收端
    1）创建连接
    2）创建通道
    3）声明队列
    4）监听队列
    5）接收消息
 **/
public class Consumer01 {
    //消息队列名称
    private static  final  String QUEUE = "helloworld";


    public static void main(String[] args) {
        //创建连接工厂
        ConnectionFactory connectionFactory = new ConnectionFactory();
        //设置服务器的IP和端口
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        //设置虚拟主机的路径，/，虚拟主机等同于一个独立的服务,可以设置多个虚拟主机
        connectionFactory.setVirtualHost("/");
        try {
            //创建一个新的mq连接
            Connection  connection = connectionFactory.newConnection();
            //创建一个连接通道
            Channel channel = connection.createChannel();
            //声明队列
            /*
            * 队列名称
            * 是否持久化
            * 是否独占连接
            * 队列不再使用时是否自动删除
            * 队列参数
            * */
            channel.queueDeclare(QUEUE,true,false,false,null);

            //定义消费者方法
            DefaultConsumer consumer = new DefaultConsumer(channel){
                /**
                 *
                 * @param consumerTag 消费者标签，可以在channel.basicConsume指定
                 * @param envelope  消息包的内容，可以从中获取消费id，消费routingKey,交换机，消息和重传等标志(消息失败后是否重新发送)
                 * @param properties  消息属性
                 * @param body 接收内容
                 * @throws IOException
                 */
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

                    //交换机
                    envelope.getExchange();
                    //路由routingKey
                    envelope.getRoutingKey();
                    //消息id
                    envelope.getDeliveryTag();
                    //消息内容(字符串转换编码)
                    String message = new String(body,"utf-8");
                    //打印队列消息
                    System.out.println("receive message:"+message);

                }
            };

            /**
             * 队列名称
             * 是否自动回复，设置为true表明为自动回复mq，如果设置为false就需要编码实现
             * 消费消息后，消费者收到消息调用此方法
             */
            channel.basicConsume(QUEUE,true,consumer);


        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
