package com.andrew.secondkill.redis;

/**
 * Class
 *
 * @author andrew
 * @date 2020/3/21
 */
public class UserKeyPrefix extends BaseKeyPrefix{

    private UserKeyPrefix(String prefix, int expireSecond) {
        super(prefix, expireSecond);
    }

    private UserKeyPrefix(String prefix) {
        super(prefix);
    }

    public static UserKeyPrefix userIDprefix = new UserKeyPrefix("id");

    public static UserKeyPrefix userTOKENprefix = new UserKeyPrefix("token",3600*12);

}
