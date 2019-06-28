package com.mmall.util;

import com.mmall.common.RedisPool;
import com.mmall.common.RedisShardedPool;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

@Slf4j
public class RedisShardedPoolUtil {
    public static String set(String key,String value){
        ShardedJedis jedis=null;
        String result=null;

        try {
            jedis = RedisShardedPool.getJedis();
            result=jedis.set(key,value);
        } catch (Exception e) {
           log.error("set ket:{} value:{}",key,value);
            RedisShardedPool.returnBrokenResources(jedis);
           return result;
        }
        RedisShardedPool.returnResourses(jedis);
        return result;


    }
    public static String get(String key){
        ShardedJedis jedis=null;
        String result=null;
        try
        {
            jedis = RedisShardedPool.getJedis();
            result=jedis.get(key);
        } catch (Exception e) {
            log.error("set ket:{} ",key);
            RedisShardedPool.returnBrokenResources(jedis);
            return result;
        }
        RedisShardedPool.returnResourses(jedis);
        return result;


    }
    //初次登录的时候把用户的信息放到 存的是秒
    public static String setEx(String key,String value,int exTime){
        ShardedJedis jedis=null;
        String result=null;

        try {
            jedis = RedisShardedPool.getJedis();
            result=jedis.setex(key,exTime,value);
        } catch (Exception e) {
            log.error("set ket:{},value:{} ",key,value);
            RedisShardedPool.returnBrokenResources(jedis);
            return result;
        }
        RedisShardedPool.returnResourses(jedis);
        return result;


    }
    //重新设置key的有效期
    public static Long expire(String key,int exTime){
        ShardedJedis jedis=null;
        Long result=null;

        try {
            jedis = RedisShardedPool.getJedis();
            result=jedis.expire(key,exTime);
        } catch (Exception e) {
            log.error("set ket:{},exTime:{} ",key,exTime);
            RedisShardedPool.returnBrokenResources(jedis);
            return result;
        }
        RedisShardedPool.returnResourses(jedis);
        return result;

    }

    public static  Long del(String key){
        ShardedJedis jedis=null;
        Long result=null;

        try {
            jedis = RedisShardedPool.getJedis();
            result=jedis.del(key);
        } catch (Exception e) {
            log.error("set ket:{} ",key);
            RedisShardedPool.returnBrokenResources(jedis);
            return result;
        }
        RedisShardedPool.returnResourses(jedis);
        return result;
    }

    public static Long setnx(String key,String value){
        ShardedJedis jedis=null;
        Long result=null;

        try {
            jedis = RedisShardedPool.getJedis();
            result=jedis.setnx(key,value);
        } catch (Exception e) {
            log.error("setnx ket:{} value:{}",key,value);
            RedisShardedPool.returnBrokenResources(jedis);
            return result;
        }
        RedisShardedPool.returnResourses(jedis);
        return result;


    }
}
