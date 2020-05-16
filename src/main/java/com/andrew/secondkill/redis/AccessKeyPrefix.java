package com.andrew.secondkill.redis;

/**
 * AccessKeyPrefix Class
 *
 * @author andrew
 * @date 2020/4/4
 */
public class AccessKeyPrefix extends BaseKeyPrefix{

    private AccessKeyPrefix(String prefix, int expireSecond) {
        super(prefix, expireSecond);
    }

    private AccessKeyPrefix(String prefix) {
        super(prefix);
    }

    /**
     * 访问频率前缀 默认x秒钟内访问x次
     */
    public static AccessKeyPrefix withExpire(int expireSecond){
        return new AccessKeyPrefix("accessLimit",expireSecond);
    }


}
