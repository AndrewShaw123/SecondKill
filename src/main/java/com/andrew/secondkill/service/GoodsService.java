package com.andrew.secondkill.service;

import com.andrew.secondkill.dao.GoodsDao;
import com.andrew.secondkill.domain.Goods;
import com.andrew.secondkill.domain.KillGoods;
import com.andrew.secondkill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * GoodsService Class
 *
 * @author andrew
 * @date 2020/3/24
 */
@Service
public class GoodsService {

    @Autowired
    GoodsDao goodsDao;

    public List<GoodsVo> listGoodsVo(){
        return goodsDao.listGoodsVo();
    }

    public GoodsVo detailGoodsVo(long goodsId){
        return goodsDao.getGoodsByGoodsId(goodsId);
    }

    public int reduceStock(long goodsId) {
        return goodsDao.reduceStock(goodsId);
    }
}
