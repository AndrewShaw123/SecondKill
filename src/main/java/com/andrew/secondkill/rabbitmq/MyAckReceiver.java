/*
package com.andrew.secondkill.rabbitmq;

import com.andrew.secondkill.redis.RedisService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

*/
/**
 * MyAckReceiver Class
 *  消费者接收到消息的消息确认机制
 *  消费者手动确认接收到
 *
 * @author andrew
 * @date 2020/5/5
 *//*

@Component
public class MyAckReceiver implements ChannelAwareMessageListener {

    @Autowired
    RedisService redisService;

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            String msg = message.toString();
            System.out.println("---");
            System.out.println(message);
            System.out.println("---");
            //可以点进Message里面看源码,单引号直接的数据就是我们的map消息数据
            String[] msgArray = msg.split("'");
            KillMessage killMessage = redisService.stringToBean(msgArray[1].trim(), KillMessage.class);

            System.out.println("  MyAckReceiver  message:"+killMessage.toString());
            System.out.println("消费的主题消息来自："+message.getMessageProperties().getConsumerQueue());
            channel.basicAck(deliveryTag, true);
//			channel.basicReject(deliveryTag, true);//为true会重新放回队列
        } catch (Exception e) {
            channel.basicReject(deliveryTag, false);
            e.printStackTrace();
        }
    }
}
*/
