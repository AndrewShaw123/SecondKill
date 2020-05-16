package com.andrew.secondkill.redis;

/**
 * Class
 *
 * @author andrew
 * @date 2020/3/21
 */
public class OrderKeyPrefix extends BaseKeyPrefix{

    private OrderKeyPrefix(String prefix, int expireSecond) {
        super(prefix, expireSecond);
    }

    private OrderKeyPrefix(String prefix) {
        super(prefix);
    }

    public static OrderKeyPrefix orderKILLORDERprefix = new OrderKeyPrefix("killOrder");

}
