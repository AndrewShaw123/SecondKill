package com.andrew.secondkill.controller;

import com.andrew.secondkill.access.AccessLimit;
import com.andrew.secondkill.domain.KillOrder;
import com.andrew.secondkill.domain.Order;
import com.andrew.secondkill.domain.User;
import com.andrew.secondkill.rabbitmq.KillMessage;
import com.andrew.secondkill.rabbitmq.MqSender;
import com.andrew.secondkill.redis.AccessKeyPrefix;
import com.andrew.secondkill.redis.GoodsKeyPrefix;
import com.andrew.secondkill.redis.KillKeyPrefix;
import com.andrew.secondkill.redis.RedisService;
import com.andrew.secondkill.result.CodeMsg;
import com.andrew.secondkill.result.Result;
import com.andrew.secondkill.service.GoodsService;
import com.andrew.secondkill.service.KillService;
import com.andrew.secondkill.service.OrderService;
import com.andrew.secondkill.util.MD5Util;
import com.andrew.secondkill.util.UUIDUtil;
import com.andrew.secondkill.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sun.security.provider.MD5;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * KillController Class
 *
 * @author andrew
 * @date 2020/3/26
 */
@Controller
@RequestMapping("/kill")
public class KillController implements InitializingBean {

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    KillService killService;

    @Autowired
    RedisService redisService;

    @Autowired
    MqSender mqSender;

    public static final Map<Long,Boolean> localOverMap = new HashMap<>();

    /**
     * 系统初始化后执行
     * 预先加载当前库存
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsVoList = goodsService.listGoodsVo();
        if(goodsVoList==null){
            return;
        }
        for(GoodsVo goodsVo:goodsVoList){
            redisService.set(GoodsKeyPrefix.goodsSTOCKprefix,""+goodsVo.getId(),goodsVo.getStockCount());
            localOverMap.put(goodsVo.getId(),false);
        }
    }

    /**
     * 优化前:741吞吐量
     * 5000*10个并发 2核
     *
     * 优化前:916吞吐量 1456吞吐量  load average:8
     * 5000*10个并发 4核
     */
    @RequestMapping(value = "/do_kill2" , method = RequestMethod.POST)
    @ResponseBody
    public Result<Order> do_kill2(User user,
                                  @RequestParam("goodsId")long goodsId){

        if(user==null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        GoodsVo goodsVo = goodsService.detailGoodsVo(goodsId);
        int stockCount = goodsVo.getStockCount();

        if(stockCount<=0){
            return Result.error(CodeMsg.KILL_OVER);
        }
        //同一个用户的两个请求同时到这里 会造成重复下单-->解决：数据库做唯一索引让插入抛出异常
        KillOrder existOrder = orderService.getKillOrderByUserIdAndGoodsId(user.getId(), goodsId);
        if(existOrder!=null){
            return Result.error(CodeMsg.REPEATE_KILL);
        }

        Order killOrder = killService.kill(user, goodsVo);

        return Result.success(killOrder);
    }

    //---------------------------------------优化--------------------------------------------

    /**
     * 优化后:1848吞吐量  load average:1
     * 5000*10个并发 4核
     */
    @RequestMapping(value = "/{path}/do_kill" , method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> do_kill(User user,
                                   @RequestParam("goodsId")long goodsId,
                                   @PathVariable("path")String path){

        if(user==null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        //验证请求路径
        if(!killService.checkKillPath(user.getId(),goodsId,path)){
            return  Result.error(CodeMsg.REQUEST_ILLEGAL);
        }

        //内存标记 减少Redis缓存访问
        Boolean isOver = localOverMap.get(goodsId);
        if(isOver){
            return Result.error(CodeMsg.KILL_OVER);
        }

        //从预先加载当前库存中取
        long stock = redisService.decr(GoodsKeyPrefix.goodsSTOCKprefix,""+goodsId);
        if(stock < 0){
            localOverMap.put(goodsId,true);
            return Result.error(CodeMsg.KILL_OVER);
        }
        //判断是否秒杀过了 秒杀订单是否存在
        KillOrder existOrder = orderService.getKillOrderByUserIdAndGoodsId(user.getId(), goodsId);
        if(existOrder!=null){
            return Result.error(CodeMsg.REPEATE_KILL);
        }
        //都通过了就入队 异步处理
        KillMessage killMessage = new KillMessage();
        killMessage.setGoodsId(goodsId);
        killMessage.setUser(user);
        mqSender.killSend(killMessage);

        //返回中间状态 没成功也没失败 让客户端轮询服务端查看结果
        return Result.success(0);
    }

    @RequestMapping("/getResult")
    @ResponseBody
    public Result<Long> getResult(User user,
                                  @RequestParam("goodsId")long goodsId){
        if(user==null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        long result = killService.getKillResult(user.getId(),goodsId);
        //秒杀成功返回订单号 失败返回-1 排队中返回0
        return Result.success(result);
    }

    /**
     * 验证验证码的正确性
     * 验证通过则生成秒杀路径传给客户端
     * 秒杀路径-->userId + goodsId 存放在redis里面 60秒过期
     */
    @AccessLimit(seconds = 5,maxCount = 5,needLogin = true)
    @RequestMapping("/getKillPath")
    @ResponseBody
    public Result<String> getKillPath(HttpServletRequest request,
                                      User user,
                                      @RequestParam("goodsId")long goodsId,
                                      @RequestParam("verifyCode")int verifyCode){
        /*if(user==null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }*/

        /*//限流 一分钟内允许访问x次
        String uri = request.getRequestURI();
        String accessLimitKey = uri+"_"+user.getId();
        Integer count = redisService.get(AccessKeyPrefix.accessLIMITprefix, accessLimitKey, Integer.class);
        if(count==null){
            redisService.set(AccessKeyPrefix.accessLIMITprefix,accessLimitKey,1);
        }else if(count < 5 ){
            redisService.incr(AccessKeyPrefix.accessLIMITprefix,accessLimitKey);
        }else{
            return Result.error(CodeMsg.ACCESS_LIMIT_REACHED);
        }*/


        //确认验证码
        if(!killService.checkVerifyCode(user.getId(),goodsId,verifyCode)){
            return Result.error(CodeMsg.VERIFYCODE_WRONG);
        }
        String randomPath = killService.createKillPath(user.getId(),goodsId);
        return Result.success(randomPath);
    }


    @AccessLimit(seconds = 60,maxCount = 60,needLogin = true)
    @RequestMapping("/verifyCode")
    @ResponseBody
    public Result<String> verifyCode(HttpServletResponse response,
                                     User user,
                                     @RequestParam("goodsId")long goodsId){
        if(user==null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        try {
            BufferedImage image = killService.createVerifyCode(user, goodsId);
            OutputStream out = response.getOutputStream();
            ImageIO.write(image, "JPEG", out);
            out.flush();
            out.close();
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return Result.error(CodeMsg.KILL_FAIL);
        }

    }

}
