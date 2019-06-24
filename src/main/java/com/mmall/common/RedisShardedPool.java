package com.mmall.common;

import com.mmall.util.PropertiesUtil;
import redis.clients.jedis.*;
import redis.clients.util.Hashing;
import redis.clients.util.Sharded;

import java.util.ArrayList;
import java.util.List;

public class RedisShardedPool
{
    private static ShardedJedisPool pool;

    private static Integer maxTotal = Integer.parseInt(PropertiesUtil.getProperty("redis.max.total", "20"));
    private static Integer maxIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.max.idle", "20"));
    private static Integer minIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.min.idle", "20"));

    private static boolean testOnBorrow = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.borrow", "true"));
    private static boolean testOnReturn = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.return", "true"));

    //如果是这样的话，那么每添加一个redis，代码要进行改变，这样并不好。
    private static String redis1Ip = PropertiesUtil.getProperty("redis1.ip");
    private static Integer redis1Port = Integer.parseInt(PropertiesUtil.getProperty("redis1.port"));

    private static String redis2Ip = PropertiesUtil.getProperty("redis2.ip");
    private static Integer redis2Port = Integer.parseInt(PropertiesUtil.getProperty("redis2.port"));
    static {
        initPool();
    }

    private static void initPool()
    {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);
        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);
        config.setBlockWhenExhausted(true);

        //超时时间设置成2秒
        JedisShardInfo  info1=new JedisShardInfo(redis1Ip,redis1Port,2*1000);
//        info1.setPassword("");如果有密码的话  添加密码
        JedisShardInfo  info2=new JedisShardInfo(redis2Ip,redis2Port,2*1000);

        List<JedisShardInfo> infos=new ArrayList<>(2);
        infos.add(info1);
        infos.add(info2);


        pool=new ShardedJedisPool(config,infos, Hashing.MURMUR_HASH, Sharded.DEFAULT_KEY_TAG_PATTERN);
    }

    //拿的是分片的jedis链接
    public static ShardedJedis getJedis() {
        return pool.getResource();
    }

    //将jedis放回连接池
    public static void returnBrokenResources(ShardedJedis jedis) {
        if (jedis != null) {
            pool.returnBrokenResource(jedis);
        }
    }

    public static void returnResourses(ShardedJedis jedis) {
        if (jedis != null) {
            pool.returnResource(jedis);
        }
    }

    public static void main(String[] args) {
        ShardedJedis jedis=pool.getResource();
        //jedis.set("hss","value");
        for(int i=0;i<10;i++){
            jedis.set("key"+i,"value"+i);
        }
        returnResourses(jedis);

//        pool.destroy();//临时diaoyog    }
        System.out.println("end");
    }

}
