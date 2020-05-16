package com.andrew.secondkill.service;

import com.andrew.secondkill.dao.OrderDao;
import com.andrew.secondkill.domain.KillOrder;
import com.andrew.secondkill.domain.Order;
import com.andrew.secondkill.domain.User;
import com.andrew.secondkill.redis.OrderKeyPrefix;
import com.andrew.secondkill.redis.RedisService;
import com.andrew.secondkill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;

/**
 * OrderService Class
 *
 * @author andrew
 * @date 2020/3/26
 */
@Service
public class OrderService {

    @Autowired
    OrderDao orderDao;

    @Autowired
    RedisService redisService;

    public KillOrder getKillOrderByUserIdAndGoodsId(Long userId, long goodsId) {
        //从缓存里取秒杀订单
        return redisService.get(OrderKeyPrefix.orderKILLORDERprefix,""+userId+"_"+goodsId,KillOrder.class);
        //return orderDao.getKillOrderByUserIdAndGoodsId(userId,goodsId);
    }

    @Transactional(rollbackFor = Exception.class)
    public Order createOrder(User user, GoodsVo goods) {

        Order order = new Order();
        order.setCreateDate(new Date());
        order.setDeliveryAddrId(0L);
        order.setGoodsCount(1);
        order.setGoodsId(goods.getId());
        order.setGoodsName(goods.getGoodsName());
        order.setGoodsPrice(goods.getKillPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setUserId(user.getId());

        //插入order表
        orderDao.createOrder(order);

        KillOrder killOrder = new KillOrder();
        killOrder.setGoodsId(goods.getId());
        killOrder.setOrderId(order.getId());
        killOrder.setUserId(user.getId());

        //插入kill_order表
        orderDao.createKillOrder(killOrder);

        //秒杀订单放入redis缓存
        redisService.set(OrderKeyPrefix.orderKILLORDERprefix,""+user.getId()+"_"+goods.getId(),killOrder);
        return order;
    }

    public Order getOrderByOrderId(int orderId){
        return orderDao.getOrderByOrderId(orderId);
    }
}
