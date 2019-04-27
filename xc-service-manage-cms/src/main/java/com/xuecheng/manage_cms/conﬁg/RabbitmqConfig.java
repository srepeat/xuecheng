package com.xuecheng.manage_cms.conﬁg;

import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 鲜磊 on 2019/4/26
 **/
@Configuration
public class RabbitmqConfig {

    public static final String EX_ROUTING_CMS_POSTPAGE="ex_routing_cms_postpage";


    /**
     * 绑定交换机
     * @return
     */
    @Bean(EX_ROUTING_CMS_POSTPAGE)
    public Exchange EX_ROUTING_CMS_POSTPAGE(){
        return ExchangeBuilder.directExchange(EX_ROUTING_CMS_POSTPAGE).durable(true).build();
    }


}
