package com.xuecheng.manage_cms_client.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置类
 * @author 鲜磊 on 2019/4/25
 **/
@Configuration
public class RabbitmqConfig {

    //消息队列名称
    public static final String QUEUE_CMS_POSTPAGE = "queue_cms_postpage";

    //交换机的名称
    public static final String EX_ROUTING_CMS_POSTPAGE="ex_routing_cms_postpage";

    //队列的名称
    @Value("${xuecheng.mq.queue}")
    public  String queue_cms_postpage_name;
    //routingKey 即站点Id
    @Value("${xuecheng.mq.routingKey}")
    public  String routingKey;

    /**
     * 配置交换机
     * @return exchange
     */

    @Bean(EX_ROUTING_CMS_POSTPAGE)
    public Exchange EXCHANGE_TOPICS_INFORM(){

        return ExchangeBuilder.directExchange(EX_ROUTING_CMS_POSTPAGE).durable(true).build();
    }


    /**
     * 声明队列
     * @return
     */
    @Bean(QUEUE_CMS_POSTPAGE)
    public Queue QUEUE_CMS_POSTPAGE(){
        return new Queue(queue_cms_postpage_name);
    }
    /**
     * 绑定队列
     * @param queue
     * @param exchange
     * @return
     */
    @Bean
    public Binding BINDING_QUEUE_INFORM_SMS(@Qualifier(QUEUE_CMS_POSTPAGE) Queue queue,
        @Qualifier(EX_ROUTING_CMS_POSTPAGE) Exchange exchange ){

        return BindingBuilder.bind(queue).to(exchange).with(routingKey).noargs();

    }

}
