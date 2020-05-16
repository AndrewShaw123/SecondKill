package com.andrew.secondkill.dao;

import com.andrew.secondkill.domain.KillOrder;
import com.andrew.secondkill.domain.Order;
import org.apache.ibatis.annotations.*;

/**
 * OrderDao Class
 *
 * @author andrew
 * @date 2020/3/26
 */
@Mapper
public interface OrderDao {

    @Select("SELECT * " +
            "FROM kill.kill_order " +
            "WHERE user_id=#{userId} AND goods_id=#{goodsId};")
    KillOrder getKillOrderByUserIdAndGoodsId(@Param("userId")long userId, @Param("goodsId")long goodsId);

    @Insert("insert into kill.order( `user_id`, `goods_id`, `delivery_addr_id`, `goods_name`, `goods_count`, `goods_price`, `order_channel`, `status`,`create_date`)" +
            "values(#{userId},#{goodsId},#{deliveryAddrId}," +
            "#{goodsName},#{goodsCount},#{goodsPrice}," +
            "#{orderChannel},#{status},#{createDate});")
    @Options(keyColumn="id",keyProperty = "id" ,useGeneratedKeys = true)
    void createOrder(Order order);

    @Insert("insert into kill.kill_order(`user_id`,`goods_id`,`order_id`)" +
            "values(#{userId},#{goodsId},#{orderId});")
    void createKillOrder(KillOrder killOrder);

    @Select("select * from kill.order where id=#{orderId};")
    Order getOrderByOrderId(@Param("orderId") int orderId);
}
