package com.andrew.secondkill.service;

import com.andrew.secondkill.domain.Goods;
import com.andrew.secondkill.domain.KillOrder;
import com.andrew.secondkill.domain.Order;
import com.andrew.secondkill.domain.User;
import com.andrew.secondkill.exception.GlobalException;
import com.andrew.secondkill.redis.KillKeyPrefix;
import com.andrew.secondkill.redis.RedisService;
import com.andrew.secondkill.result.CodeMsg;
import com.andrew.secondkill.util.MD5Util;
import com.andrew.secondkill.util.UUIDUtil;
import com.andrew.secondkill.vo.GoodsVo;
import com.sun.org.apache.bcel.internal.classfile.Code;
import com.sun.org.apache.bcel.internal.generic.RETURN;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * KillService Class
 *
 * @author andrew
 * @date 2020/3/26
 */
@Service
public class KillService {

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    RedisService redisService;

    /**
     * 减少库存 + 下订单 +写入秒杀订单
     * @param user 用户id
     * @param goodsVo 商品信息
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Order kill(User user, GoodsVo goodsVo) {
        int affectRow = goodsService.reduceStock(goodsVo.getId());
        if(affectRow==0){
            //没有库存 设置标志
            setGoodsOver(goodsVo.getId());
            throw new GlobalException(CodeMsg.KILL_OVER);
        }
        return orderService.createOrder(user,goodsVo);
    }



    /**
     * 获得秒杀结果
     *
     * 秒杀成功返回订单号
     * 失败返回-1
     * 排队中返回0
     */
    public long getKillResult(Long userId, long goodsId) {
        KillOrder killOrder = orderService.getKillOrderByUserIdAndGoodsId(userId, goodsId);
        if(killOrder!=null){
            return killOrder.getOrderId();
        }else{
            /* 没有订单的原因-->1.队列还没有消费
                            -->2.秒杀失败，没有库存
            */
            boolean isOver = getGoodsOver(goodsId);
            if(isOver){
                return -1;
            }else{
                return 0;
            }
        }
    }

    private boolean getGoodsOver(long goodsId) {
        return redisService.exists(KillKeyPrefix.killGoodsOVERprefix,""+goodsId);
    }

    private void setGoodsOver(Long goodsId) {
        redisService.set(KillKeyPrefix.killGoodsOVERprefix,""+goodsId,true);
    }

    public boolean checkKillPath(long userId, long goodsId,String path) {
        String cachePath = redisService.get(KillKeyPrefix.killPATHprefix, "" + userId + "_" + goodsId, String.class);
        return cachePath.equals(path);
    }

    public String createKillPath(Long userId, long goodsId) {
        String randomPath = MD5Util.md5(UUIDUtil.uuid()+"andrew");
        redisService.set(KillKeyPrefix.killPATHprefix,""+userId+"_"+goodsId,randomPath);
        return randomPath;
    }

    /**
     * 生成验证码
     *
     */
    public BufferedImage createVerifyCode(User user, long goodsId) {
        if(user==null||goodsId<0){
            return null;
        }
        int width = 80;
        int height = 32;
        //create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // set the background color
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        // draw the border
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
        // create a random instance to generate the codes
        Random rdm = new Random();
        // make some confusion
        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        // generate a random code
        String verifyCode = generateVerifyCode(rdm);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyCode, 8, 24);
        g.dispose();
        //把验证码存到redis中
        int rnd = calc(verifyCode);
        redisService.set(KillKeyPrefix.killVerifyCode, user.getId()+","+goodsId, rnd);
        //输出图片
        return image;
    }

    private int calc(String exp) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            return (Integer)engine.eval(exp);
        }catch(Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static char[] ops = new char[] {'+', '-', '*'};
    /**
     * + - *
     * */
    private String generateVerifyCode(Random rdm) {
        int num1 = rdm.nextInt(10);
        int num2 = rdm.nextInt(10);
        int num3 = rdm.nextInt(10);
        char op1 = ops[rdm.nextInt(3)];
        char op2 = ops[rdm.nextInt(3)];
        String exp = ""+ num1 + op1 + num2 + op2 + num3;
        return exp;
    }

    public boolean checkVerifyCode(Long userId, long goodsId, int verifyCode) {
        Integer cacheVerifyCode = redisService.get(KillKeyPrefix.killVerifyCode, userId + "," + goodsId, Integer.class);
        if(cacheVerifyCode==null||cacheVerifyCode-verifyCode!=0){
            return false;
        }
        //redis删除验证码 防止被继续利用
        redisService.delete(KillKeyPrefix.killVerifyCode, userId + "," + goodsId);
        return true;
    }
}
