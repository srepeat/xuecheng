package com.xuecheng.test.rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;

/** 测试email topic统配
 * @author 鲜磊 on 2019/4/21
 **/
public class Consumer03_topic_email {

    //队列名称
    private static final String QUEUE_INFORM_EMAIL = "queue_inform_email";
    private static final String EXCHANGE_TOPICS_INFORM="exchange_topics_inform";
    private static final String ROUTINGKEY_EMAIL="inform.#.email.#";

    public static void main(String[] args) {
        Connection connection = null;
        Channel channel = null;
        //创建rabbitmq连接工厂
        ConnectionFactory connectionFactory = ProducerBean.connectionFactoryUtils();

        try{
            //创建一个新的连接
            connection = connectionFactory.newConnection();

            //创建通道
            channel = connection.createChannel();
            //声明交换机
            channel.exchangeDeclare(EXCHANGE_TOPICS_INFORM, BuiltinExchangeType.TOPIC);
            //声明队列
            channel.queueDeclare(QUEUE_INFORM_EMAIL,true,false,false,null);
            //参数：String queue, String exchange, String routingKey
            /**
             * 参数明细：
             * 1、queue 队列名称
             * 2、exchange 交换机名称
             * 3、routingKey 路由key，作用是交换机根据路由key的值将消息转发到指定的队列中，在发布订阅模式中调协为空字符串
             */
            channel.queueBind(QUEUE_INFORM_EMAIL,EXCHANGE_TOPICS_INFORM,ROUTINGKEY_EMAIL);
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

            channel.basicConsume(QUEUE_INFORM_EMAIL,true, defaultConsumer);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
