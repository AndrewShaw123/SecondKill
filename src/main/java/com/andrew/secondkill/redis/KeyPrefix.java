package com.andrew.secondkill.redis;

/**
 * Class
 *
 * @author andrew
 * @date 2020/3/21
 */
public interface KeyPrefix {

    String getPrefix();

    int expireSecond();

}
