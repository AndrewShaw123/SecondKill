package com.andrew.secondkill.vo;

import com.andrew.secondkill.domain.Order;

/**
 * OrderDetailVo Class
 *
 * @author andrew
 * @date 2020/3/31
 */
public class OrderDetailVo {
    private GoodsVo goods;
    private Order order;

    public OrderDetailVo() {
    }

    public OrderDetailVo(GoodsVo goods, Order order) {
        this.goods = goods;
        this.order = order;
    }

    public GoodsVo getGoods() {
        return goods;
    }

    public void setGoods(GoodsVo goods) {
        this.goods = goods;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
