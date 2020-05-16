package com.andrew.secondkill.controller;

import com.andrew.secondkill.domain.User;
import com.andrew.secondkill.rabbitmq.MqSender;
import com.andrew.secondkill.redis.RedisService;
import com.andrew.secondkill.redis.UserKeyPrefix;
import com.andrew.secondkill.result.CodeMsg;
import com.andrew.secondkill.result.Result;
import com.andrew.secondkill.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Class
 *
 * @author andrew
 * @date 2020/3/20
 */
@Controller
public class TestController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisService redisService;

    @RequestMapping("/test")
    @ResponseBody
    public String test(){
        return "Hello World! Test Test";
    }

    @RequestMapping("/mysuccess")
    @ResponseBody
    public Result<String> success(){
        return Result.success("I am Andrew");
    }

    @RequestMapping("/myerror")
    @ResponseBody
    public Result<String> error(){
        return Result.error(CodeMsg.SERVER_ERROR);
    }

    @RequestMapping("/nihao")
    public String hello(Model model){
        model.addAttribute("name","Loser");
        return "hello";
    }

    @Autowired
    MqSender mqSender;

    @RequestMapping("/mq")
    @ResponseBody
    public Result<String> mq(){
        mqSender.send("Hello World! Welcome , Andrew , hey!");
        return Result.success("This is Test for RabbitMQ");
    }

    @RequestMapping("/topic/mq")
    @ResponseBody
    public Result<String> topicMq(){
        mqSender.sendTopic("Hello World! Welcome , Andrew , hey!");
        return Result.success("This is Test for Topic模式");
    }

    @RequestMapping("/fanout/mq")
    @ResponseBody
    public Result<String> fanoutMq(){
        mqSender.sendFanout("Hello World! Welcome , Andrew , hey!");
        return Result.success("This is Test for Fanout模式");
    }

    @RequestMapping("/headers/mq")
    @ResponseBody
    public Result<String> headersMq(){
        mqSender.sendHeaders("Hello World! Welcome , Andrew , hey!");
        return Result.success("This is Test for Headers模式");
    }

    /*
    @RequestMapping("/db/get")
    @ResponseBody
    public Result<User> getUser(){
        User user = userService.getUser(2);
        return Result.success(user);
    }

    @RequestMapping("/redis/get")
    @ResponseBody
    public Result<String> getRedis(){
        String name = redisService.get(UserKeyPrefix.userIDprefix,"666", String.class);
        return Result.success(name);
    }

    @RequestMapping("/redis/set")
    @ResponseBody
    public Result<User> setRedis(){
        User user = new User();
        user.setId(666);
        user.setName("汤姆");
        boolean result = redisService.set(UserKeyPrefix.userIDprefix,"666", user);
        System.out.println(result);
        User getAgain = redisService.get(UserKeyPrefix.userIDprefix,"666", User.class);
        return Result.success(getAgain);
    }*/
}
