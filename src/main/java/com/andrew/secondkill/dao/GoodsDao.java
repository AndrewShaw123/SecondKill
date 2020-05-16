package com.andrew.secondkill.dao;

import com.andrew.secondkill.domain.KillGoods;
import com.andrew.secondkill.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * GoodsDao Class
 *
 * @author andrew
 * @date 2020/3/24
 */
@Mapper
public interface GoodsDao {

    @Select("SELECT g.* , kg.kill_price , kg.stock_count , kg.start_date , kg.end_date " +
            "FROM kill.goods g left join kill.kill_goods kg ON g.id=kg.goods_id;")
    List<GoodsVo> listGoodsVo();

    @Select("SELECT g.* , kg.kill_price , kg.stock_count , kg.start_date , kg.end_date " +
            "FROM kill.goods g left join kill.kill_goods kg ON g.id=kg.goods_id " +
            "WHERE g.id=#{goodsId};")
    GoodsVo getGoodsByGoodsId(@Param("goodsId") long goodsId);

    @Update("UPDATE kill.kill_goods " +
            "SET stock_count=stock_count-1 " +
            "WHERE goods_id=#{goodsId} and stock_count > 0;")
    int reduceStock(@Param("goodsId") long goodsId);
}
