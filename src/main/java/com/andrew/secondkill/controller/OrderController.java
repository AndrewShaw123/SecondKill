package com.andrew.secondkill.controller;

import com.andrew.secondkill.domain.Order;
import com.andrew.secondkill.result.CodeMsg;
import com.andrew.secondkill.result.Result;
import com.andrew.secondkill.service.GoodsService;
import com.andrew.secondkill.service.OrderService;
import com.andrew.secondkill.vo.GoodsVo;
import com.andrew.secondkill.vo.OrderDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * OrderController Class
 *
 * @author andrew
 * @date 2020/3/31
 */
@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    OrderService orderService;

    @Autowired
    GoodsService goodsService;

    @RequestMapping("/detail")
    @ResponseBody
    public Result<OrderDetailVo> detail(@RequestParam("orderId")int orderId){
        Order order = orderService.getOrderByOrderId(orderId);
        if(order==null){
            return Result.error(CodeMsg.ORDER_NOT_EXIST);
        }
        GoodsVo goodsVo = goodsService.detailGoodsVo(order.getGoodsId());
        OrderDetailVo orderDetailVo = new OrderDetailVo(goodsVo,order);
        return Result.success(orderDetailVo);
    }

}
