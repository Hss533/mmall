package com.mmall.common;

import com.mmall.util.PropertiesUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisPool {
    private static JedisPool pool;
    //jedis连接池
    private static Integer maxTotal = Integer.parseInt(PropertiesUtil.getProperty("redis.max.total", "20"));//最大连接数
    private static Integer maxIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.max.idle", "20"));//在jedis中最大的idle状态（空闲的）的jedis实例个数
    private static Integer minIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.min.idle", "20"));//在jedis中最小的idle状态的jedis实例个数

    private static boolean testOnBorrow = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.borrow", "true"));
    //在borrow一个jedis实例的时候，是否要进行验证操作，如果赋值为true。
    // 则得到的jedis实例肯定是可以用的。
    private static boolean testOnReturn = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.return", "true"));
    //还的时候

    private static String redisIp = PropertiesUtil.getProperty("redis1.ip");
    private static Integer redisPort = Integer.parseInt(PropertiesUtil.getProperty("redis1.port"));

    static {
        initPool();
    }

    //只需要一个实例化
    private static void initPool() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);
        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);
        config.setBlockWhenExhausted(true);//是如果链接多出来的话 采用什么策略 false会抛出异常 true会进行阻塞 直到超市
        pool = new JedisPool(config, redisIp, redisPort, 1000 * 2);


    }

    public static Jedis getJedis() {
        return pool.getResource();
    }

    //将jedis放回连接池
    public static void returnBrokenResources(Jedis jedis) {
        if (jedis != null) {
            pool.returnBrokenResource(jedis);
        }
    }

    public static void returnResourses(Jedis jedis) {
        if (jedis != null) {
            pool.returnResource(jedis);
        }
    }

    public static void main(String[] args) {
        Jedis jedis=pool.getResource();
        jedis.set("hss","value");
        returnResourses(jedis);
        pool.destroy();//临时diaoyog    }
        System.out.println("end");}
}
