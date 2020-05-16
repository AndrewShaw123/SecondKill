package com.andrew.secondkill.redis;

/**
 * GoodsKeyPrefix Class
 *
 * @author andrew
 * @date 2020/3/30
 */
public class GoodsKeyPrefix extends BaseKeyPrefix{

    private GoodsKeyPrefix(String prefix, int expireSecond) {
        super(prefix, expireSecond);
    }

    private GoodsKeyPrefix(String prefix) {
        super(prefix);
    }

    public static GoodsKeyPrefix goodsLISTprefix = new GoodsKeyPrefix("goodsList",60);
    public static GoodsKeyPrefix goodsDETAILprefix = new GoodsKeyPrefix("goodsDetail",60);
    public static GoodsKeyPrefix goodsSTOCKprefix = new GoodsKeyPrefix("goodsStock",0);
}
