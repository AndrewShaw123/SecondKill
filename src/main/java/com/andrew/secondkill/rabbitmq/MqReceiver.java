package com.andrew.secondkill.rabbitmq;

import com.andrew.secondkill.domain.KillOrder;
import com.andrew.secondkill.domain.Order;
import com.andrew.secondkill.domain.User;
import com.andrew.secondkill.exception.GlobalException;
import com.andrew.secondkill.redis.RedisService;
import com.andrew.secondkill.result.CodeMsg;
import com.andrew.secondkill.result.Result;
import com.andrew.secondkill.service.GoodsService;
import com.andrew.secondkill.service.KillService;
import com.andrew.secondkill.service.OrderService;
import com.andrew.secondkill.vo.GoodsVo;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * MqReciever Class
 *
 * @author andrew
 * @date 2020/4/1
 */
@Service
public class MqReceiver {

    private static Logger log = LoggerFactory.getLogger(MqSender.class);
    
    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    KillService killService;

    @Autowired
    OrderService orderService;


    /**
     * 监听队列 @RabbitListener(queues = "队列名")
     */

    @RabbitListener(queues = MqConfig.KILL_QUEUE,ackMode = "MANUAL")
    public void receiveKillMessage(Message msg, Channel channel, String message) throws Exception{

        long deliveryTag = msg.getMessageProperties().getDeliveryTag();

        try{

            KillMessage killMessage = redisService.stringToBean(message, KillMessage.class);
            long goodsId = killMessage.getGoodsId();
            User user = killMessage.getUser();

            //数据库检查库存
            GoodsVo goodsVo = goodsService.detailGoodsVo(goodsId);
            int stockCount = goodsVo.getStockCount();

            if(stockCount<=0){
                return ;
            }
            //同一个用户的两个请求同时到这里 会造成重复下单-->解决：数据库做唯一索引让插入抛出异常
            KillOrder existOrder = orderService.getKillOrderByUserIdAndGoodsId(user.getId(), goodsId);
            if(existOrder!=null){
                return;
            }
            //秒杀--> 数据库减少库存+生成订单
            Order killOrder = killService.kill(user, goodsVo);

            //消费者手动确认接收到
            /*
            basicAck(long deliveryTag, boolean multiple)

            multiple=true: 消息id<=deliveryTag的消息，都会被确认
            myltiple=false: 消息id=deliveryTag的消息，都会被确认
             */
            channel.basicAck(deliveryTag,false);

        }catch (Exception e){
            /*
                basicNack(long deliveryTag, boolean multiple, boolean requeue)
                basicReject(long deliveryTag, boolean requeue)
            */
            channel.basicReject(deliveryTag, true);
            e.printStackTrace();
        }
    }
    
    //---------------------------------------------------------
    
    
    @RabbitListener(queues = MqConfig.DIRECT_QUEUE)
    public void receive(String message){
        log.info("RECEIVE GET------->"+message);
    }



    @RabbitListener(queues = MqConfig.TOPIC_QUEUE1)
    public void receiveTopic1(String message){
        log.info("RECEIVE GET TOPIC 1------->"+message);
    }

    @RabbitListener(queues = MqConfig.TOPIC_QUEUE2)
    public void receiveTopic2(String message){
        log.info("RECEIVE GET TOPIC 2------->"+message);
    }



    @RabbitListener(queues = MqConfig.HEADERS_QUEUE)
    public void receiveHeaders(byte[] message){
        log.info("RECEIVE GET HEADERS ------->"+new String(message));
    }

}
