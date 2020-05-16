package com.andrew.secondkill.util;

import com.andrew.secondkill.controller.KillController;
import com.andrew.secondkill.redis.GoodsKeyPrefix;
import com.andrew.secondkill.redis.RedisService;
import com.andrew.secondkill.service.GoodsService;
import com.andrew.secondkill.vo.GoodsVo;

import java.util.List;

/**
 * PreLoadUtil Class
 *
 * @author andrew
 * @date 2020/4/3
 */
public class PreLoadUtil {

    /**
     * 预加载商品库存到redis里-->（key）商品id （value）商品库存
     */
    public static void preLoadSecondKill(){
        GoodsService goodsService = new GoodsService();
        RedisService redisService = new RedisService();

        List<GoodsVo> goodsVoList = goodsService.listGoodsVo();
        if(goodsVoList==null){
            return;
        }
        for(GoodsVo goodsVo:goodsVoList){
            redisService.set(GoodsKeyPrefix.goodsSTOCKprefix,""+goodsVo.getId(),goodsVo.getStockCount());
            KillController.localOverMap.put(goodsVo.getId(),false);
        }
    }

    public static void main(String[] args) {
        preLoadSecondKill();
    }

}
