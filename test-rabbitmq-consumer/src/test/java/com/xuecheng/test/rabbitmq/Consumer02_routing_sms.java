package com.xuecheng.test.rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;

/** 测试SMS路由key
 * @author 鲜磊 on 2019/4/21
 **/
public class Consumer02_routing_sms {

    //队列名称
    private static final String QUEUE_INFORM_SMS = "queue_inform_sms";
    private static final String EXCHANGE_ROUTING_INFORM="exchange_routing_inform";
    private static final String ROUTINGKEY_SMS="inform_sms";

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
            //创建一个新的连接
            connection = connectionFactory.newConnection();

            //创建通道
            channel = connection.createChannel();
            //声明交换机
            channel.exchangeDeclare(EXCHANGE_ROUTING_INFORM, BuiltinExchangeType.DIRECT);
            //声明队列
            channel.queueDeclare(QUEUE_INFORM_SMS,true,false,false,null);
            //参数：String queue, String exchange, String routingKey
            /**
             * 参数明细：
             * 1、queue 队列名称
             * 2、exchange 交换机名称
             * 3、routingKey 路由key，作用是交换机根据路由key的值将消息转发到指定的队列中，在发布订阅模式中调协为空字符串
             */
            channel.queueBind(QUEUE_INFORM_SMS,EXCHANGE_ROUTING_INFORM,ROUTINGKEY_SMS);
            //监听队列
            DefaultConsumer defaultConsumer = new DefaultConsumer(channel){

                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    //交换机
                    envelope.getExchange();
                    //路由
                    envelope.getRoutingKey();
                    //消息id
                    envelope.getDeliveryTag();
                    //转换消息
                    String message = new String(body,"utf-8");
                    //打印队列消息
                    System.out.println("Receives the message："+message);
                }
            };

            channel.basicConsume(QUEUE_INFORM_SMS,true, defaultConsumer);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
