package rabbitmq.mq;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import rabbitmq.config.RabbitmqConfig;

/**
 * 组件，交与spring管理
 * @author 鲜磊 on 2019/4/21
 **/
@Component
public class ReceiveHandler {

    //监听queue队列
    @RabbitListener(queues = {RabbitmqConfig.QUEUE_INFORM_EMAIL})
    public void receive_email(String msg, Message message , Channel channel){
        System.out.println("Receives the message: "+msg);
    }
}
