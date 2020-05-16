package com.andrew.secondkill.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * MqConfig Class
 *
 * @author andrew
 * @date 2020/4/1
 */
@Configuration
public class MqConfig {

    public static final String KILL_QUEUE = "KillQueue";

    public static final String DIRECT_QUEUE = "DirectQueue";

    public static final String TOPIC_QUEUE1 = "TopicQueue1";
    public static final String TOPIC_QUEUE2 = "TopicQueue2";
    public static final String TOPIC_EXCHANGE = "TopicExchange";

    public static final String FANOUT_EXCHANGE = "FanoutExchange";

    public static final String HEADERS_QUEUE = "HeadersQueue";
    public static final String HEADERS_EXCHANGE = "HeadersExchange";

    /**
     * 秒杀队列
     * 存放KillMessage
     */
    @Bean
    public Queue killQueue(){
        return new Queue(KILL_QUEUE,true);
    }

    //-----------------------------------------------------------------

    /**
     *  置相关的消息确认回调函数
     *  确认消息到达Exchanger
     *  确认消息到达Queue
     *
     */
    @Bean
    public RabbitTemplate createRabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        //设置开启Mandatory,才能触发回调函数,无论消息推送结果怎么样都强制调用回调函数
        rabbitTemplate.setMandatory(true);

        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                System.out.println("ConfirmCallback:     "+"相关数据："+correlationData);
                System.out.println("ConfirmCallback:     "+"确认情况："+ack);
                System.out.println("ConfirmCallback:     "+"原因："+cause);
            }
        });

        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                System.out.println("ReturnCallback:     "+"消息："+message);
                System.out.println("ReturnCallback:     "+"回应码："+replyCode);
                System.out.println("ReturnCallback:     "+"回应信息："+replyText);
                System.out.println("ReturnCallback:     "+"交换机："+exchange);
                System.out.println("ReturnCallback:     "+"路由键："+routingKey);
            }
        });

        return rabbitTemplate;
    }
    //-----------------------------------------------------------------

    /**
     * 根据Queue名字传送数据
     *
     * Direct模式 交换机Exchange
     */
    @Bean
    public Queue queue(){
        return new Queue(DIRECT_QUEUE,true);
    }

    /**
     * Topic模式 交换机Exchange
     * 匹配RoutingKey 路由键
     */
    @Bean
    public Queue topicQueue1(){
        return new Queue(TOPIC_QUEUE1,true);
    }

    @Bean
    public Queue topicQueue2(){
        return new Queue(TOPIC_QUEUE2,true);
    }

    /**
     *  boolean durable 交换器持久化
     *  boolean autoDelete
     *
     */
    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange(TOPIC_EXCHANGE,true,false);
    }

    @Bean
    public Binding topicBinding1(){
        return BindingBuilder.bind(topicQueue1()).to(topicExchange()).with("pooh.1");
    }

    @Bean
    public Binding topicBinding2(){
        return BindingBuilder.bind(topicQueue2()).to(topicExchange()).with("pooh.#");
    }

    /**
     * 广播
     * Fanout模式 交换机Exchange
     */
    @Bean
    public FanoutExchange fanoutExchange(){
        return new FanoutExchange(FANOUT_EXCHANGE);
    }

    @Bean
    public Binding fanoutBinding1(){
        return BindingBuilder.bind(topicQueue1()).to(fanoutExchange());
    }

    @Bean
    public Binding fanoutBinding2(){
        return BindingBuilder.bind(topicQueue2()).to(fanoutExchange());
    }

    @Bean
    public Binding fanoutBinding3(){
        return BindingBuilder.bind(queue()).to(fanoutExchange());
    }

    /**
     * Headers模式 交换机Exchange
     * 匹配Map内容
     */
    @Bean
    public HeadersExchange headersExchange(){
        return new HeadersExchange(HEADERS_EXCHANGE);
    }

    @Bean
    public Queue headersQueue(){
        return new Queue(HEADERS_QUEUE,true);
    }

    @Bean
    public Binding headersBinding1(){
        Map<String,Object> map = new HashMap<>();
        map.put("key1","value1");
        map.put("key2","value2");
        return BindingBuilder.bind(headersQueue()).to(headersExchange()).whereAny(map).match();
    }

}
