package com.andrew.secondkill.rabbitmq;

import com.andrew.secondkill.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * MqSender Class
 *
 * @author andrew
 * @date 2020/4/1
 */
@Service
public class MqSender {

    @Autowired
    AmqpTemplate amqpTemplate;

    @Autowired
    RedisService redisService;

    private static Logger log = LoggerFactory.getLogger(MqSender.class);


    public void killSend(Object message){
        String strMessage = redisService.beanToString(message);
        log.info("SEND KILL_MESSAGE------->"+strMessage);
        amqpTemplate.convertAndSend(MqConfig.KILL_QUEUE,strMessage);
    }

    //---------------------------------------------------------------------------

    public void send(Object message){
        String strMessage = redisService.beanToString(message);
        log.info("SEND MESSAGE------->"+strMessage);
        amqpTemplate.convertAndSend(MqConfig.DIRECT_QUEUE,strMessage);
    }

    public void sendTopic(Object message){
        String strMessage = redisService.beanToString(message);
        amqpTemplate.convertAndSend(MqConfig.TOPIC_EXCHANGE,"pooh.1",strMessage+" -Above is topic 1");
        amqpTemplate.convertAndSend(MqConfig.TOPIC_EXCHANGE,"pooh.2",strMessage+" -Above is topic 2");
    }

    public void sendFanout(Object message){
        String strMessage = redisService.beanToString(message);
        amqpTemplate.convertAndSend(MqConfig.FANOUT_EXCHANGE,"",strMessage);
    }

    public void sendHeaders(Object message){
        String strMessage = redisService.beanToString(message);
        MessageProperties properties = new MessageProperties();
        properties.setHeader("key1","value1");
        //properties.setHeader("key2","value2");
        Message sendMessage = new Message(strMessage.getBytes(),properties);
        amqpTemplate.convertAndSend(MqConfig.HEADERS_EXCHANGE,"",sendMessage);
    }

}
