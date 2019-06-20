package com.mmall.util;

import com.mmall.common.RedisPool;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

@Slf4j
public class RedisPoolUtil {
    public static String set(String key,String value){
        Jedis jedis=null;
        String result=null;

        try {
            jedis = RedisPool.getJedis();
            result=jedis.set(key,value);
        } catch (Exception e) {
           log.error("set ket:{} value:{}",key,value);
           RedisPool.returnBrokenResources(jedis);
           return result;
        }
        RedisPool.returnResourses(jedis);
        return result;


    }
    public static String get(String key){
        Jedis jedis=null;
        String result=null;

        try {
            jedis = RedisPool.getJedis();
            result=jedis.get(key);
        } catch (Exception e) {
            log.error("set ket:{} ",key);
            RedisPool.returnBrokenResources(jedis);
            return result;
        }
        RedisPool.returnResourses(jedis);
        return result;


    }
    //初次登录的时候把用户的信息放到 存的是秒
    public static String setEx(String key,String value,int exTime){
        Jedis jedis=null;
        String result=null;

        try {
            jedis = RedisPool.getJedis();
            result=jedis.setex(key,exTime,value);
        } catch (Exception e) {
            log.error("set ket:{},value:{} ",key,value);
            RedisPool.returnBrokenResources(jedis);
            return result;
        }
        RedisPool.returnResourses(jedis);
        return result;


    }
    //重新设置key的有效期
    public static Long expire(String key,int exTime){
        Jedis jedis=null;
        Long result=null;

        try {
            jedis = RedisPool.getJedis();
            result=jedis.expire(key,exTime);
        } catch (Exception e) {
            log.error("set ket:{},exTime:{} ",key,exTime);
            RedisPool.returnBrokenResources(jedis);
            return result;
        }
        RedisPool.returnResourses(jedis);
        return result;

    }

    public static  Long del(String key){
        Jedis jedis=null;
        Long result=null;

        try {
            jedis = RedisPool.getJedis();
            result=jedis.del(key);
        } catch (Exception e) {
            log.error("set ket:{} ",key);
            RedisPool.returnBrokenResources(jedis);
            return result;
        }
        RedisPool.returnResourses(jedis);
        return result;
    }

    public static void main(String[] args) {
        Jedis jedis=RedisPool.getJedis();
        RedisPoolUtil.set("ketTest","value");
        String value=RedisPoolUtil.get("keyTest");

        RedisPoolUtil.setEx("keyex","valueex",60*10);
        RedisPoolUtil.expire("keyTest",60*10);
        RedisPoolUtil.del("ketTest");
        System.out.println("end");
    }
}
