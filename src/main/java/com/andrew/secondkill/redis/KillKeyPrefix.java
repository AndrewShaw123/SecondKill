package com.andrew.secondkill.redis;

/**
 * KillKeyPrefix Class
 *
 * @author andrew
 * @date 2020/4/2
 */
public class KillKeyPrefix extends BaseKeyPrefix{

    private KillKeyPrefix(String prefix, int expireSecond) {
        super(prefix, expireSecond);
    }

    private KillKeyPrefix(String prefix) {
        super(prefix);
    }


    public static KillKeyPrefix killGoodsOVERprefix = new KillKeyPrefix("goodsOver");

    public static KillKeyPrefix killPATHprefix = new KillKeyPrefix("killPath",60);

    public static KillKeyPrefix killVerifyCode = new KillKeyPrefix( "verifyCode",300);
}
