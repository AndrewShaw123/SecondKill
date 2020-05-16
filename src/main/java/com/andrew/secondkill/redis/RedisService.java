package com.andrew.secondkill.redis;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * RedisService Class
 *
 * @author andrew
 * @date 2020/3/21
 */
@Service
public class RedisService {

    @Autowired
    JedisPool jedisPool;

    public <T> T get(KeyPrefix keyPrefix,String key,Class<T> clazz){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = keyPrefix.getPrefix() + key;
            String str = jedis.get(realKey);
            if(str==null){
                return null;
            }
            T t = stringToBean(str,clazz);
            return t;
        }finally {
            returnToPool(jedis);
        }
    }

    public <T> boolean set(KeyPrefix keyPrefix,String key, T value){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String v = beanToString(value);
            if(v==null||v.length()==0){
                return false;
            }
            String realKey = keyPrefix.getPrefix() + key;
            int expire = keyPrefix.expireSecond();
            if(expire<=0){
                jedis.set(realKey,v);
            }else{
                jedis.setex(realKey,expire,v);
            }
            return true;
        }finally {
            returnToPool(jedis);
        }
    }

    public boolean exists(KeyPrefix keyPrefix,String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String realKey = keyPrefix.getPrefix() + key;
            Boolean result = jedis.exists(realKey);
            return result;
        }finally {
            returnToPool(jedis);
        }
    }

    public boolean expire(KeyPrefix keyPrefix,String key,int second){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String realKey = keyPrefix.getPrefix() + key;
            if(jedis.exists(realKey)){
                jedis.expire(realKey, second);
                return false;
            }else{
                return true;
            }

        }finally {
            returnToPool(jedis);
        }
    }

    public boolean delete(KeyPrefix keyPrefix,String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String realKey = keyPrefix.getPrefix() + key;

            long result = jedis.del(realKey);
            return result > 0;
        }finally {
            returnToPool(jedis);
        }
    }

    public Long incr(KeyPrefix keyPrefix,String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String realKey = keyPrefix.getPrefix() + key;
            Long result = jedis.incr(realKey);
            return result;
        }finally {
            returnToPool(jedis);
        }
    }

    public Long decr(KeyPrefix keyPrefix,String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String realKey = keyPrefix.getPrefix() + key;
            Long result = jedis.decr(realKey);
            return result;
        }finally {
            returnToPool(jedis);
        }
    }

    private void returnToPool(Jedis jedis){
        if(jedis!=null){
            jedis.close();
        }
    }

    //-----------------------------JSON 字符串和对象间的相互转换-----------------------------------------

    public <T> T stringToBean(String str,Class<T> clazz){
        if(clazz==int.class||clazz==Integer.class){
            return (T)Integer.valueOf(str);
        }
        if(clazz==String.class){
            return (T)str;
        }
        if(clazz==long.class||clazz==Long.class){
            return (T)Long.valueOf(str);
        }else{
            return JSON.toJavaObject(JSON.parseObject(str),clazz);
        }

    }

    public <T> String beanToString(T t){
        if(t==null){
            return null;
        }
        Class<?> clzz = t.getClass();
        if(clzz==int.class||clzz==Integer.class){
            return ""+t;
        }
        if(clzz==String.class){
            return (String)t;
        }
        if(clzz==long.class||clzz==Long.class){
            return ""+t;
        }

        return JSON.toJSONString(t);
    }

}
