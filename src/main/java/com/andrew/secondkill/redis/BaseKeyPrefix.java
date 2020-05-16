package com.andrew.secondkill.redis;

/**
 * Class
 *
 * @author andrew
 * @date 2020/3/21
 */
public class BaseKeyPrefix implements KeyPrefix{

    private String prefix;
    private int expireSecond;

    public BaseKeyPrefix(String prefix,int expireSecond){
        this.prefix = prefix;
        this.expireSecond = expireSecond;
    }

    public BaseKeyPrefix(String prefix){
        this(prefix,0);
    }


    @Override
    public String getPrefix() {
        String newPrefix = getClass().getSimpleName()+":"+prefix;
        return newPrefix;
    }

    @Override
    public int expireSecond() {
        return expireSecond;
    }
}
