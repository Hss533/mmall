package com.mmall.task;

import com.mmall.common.Const;
import com.mmall.service.IOrderService;
import com.mmall.util.PropertiesUtil;
import com.mmall.util.RedisShardedPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

/**
 * 下了订单之后，30min都没有付款的话，应该将订单关闭
 */
@Component
@Slf4j
public class CloseOrderTask {

    @Autowired
    private IOrderService orderService;

    //为了解决死锁问题，这个注解是在没有温柔关闭Tomcat情况下，shutdown()之前会调用这个这个方法，执行将锁释放 ，防止死锁
    //但是如果kill进程的情况下，是不可能执行这个方法的，还是会造成死锁
    @PreDestroy
    public  void delLock(){
        //删除redis分布式锁
        RedisShardedPoolUtil.del(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
    }

    //如果是一台Tomcat的话是可以的，但是如果有两台Tomcat或者更多的机器，就可能出现两台机器对同订单进行关闭，这个是没有必要的。
    @Scheduled(cron = "0 */1 * * * ?")//每分钟整数倍执行一次
    public void closeOrderTaskV1()
    {
        log.info("关闭订单定时任务启动");
        //每分钟执行一次，执行的时候关闭在当前时间两小时前关闭的订单
        int hour=Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.hour","2"));
        //orderService.closeOrder(hour);
        log.info("关闭订单定时任务关闭");
    }

    @Scheduled(cron = "0 */1 * * * ?")//每分钟整数倍执行一次
    public void closeOrderTaskV2(){

        log.info("关闭订单定时任务启动");
        long lockTimeout=Long.parseLong(PropertiesUtil.getProperty("lock.timeout","5000"));
        Long setNxResult= RedisShardedPoolUtil.setnx(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,String.valueOf(System.currentTimeMillis()+lockTimeout));
        if(setNxResult!=null&&setNxResult.intValue()==1)
        {
            //如果返回值是1，代表设置成功 ，获取锁
            closeOrder(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
            //这个锁应该是有有效期的
        }
        else {
            log.info("没有获得分布式锁：{}",Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        }
        log.info("关闭订单定时任务结束");
    }

    //新设置的锁要设置有效期 否则就是长期有效的
    private  void closeOrder(String lockName)
    {
        RedisShardedPoolUtil.expire(lockName,50);//有效期50秒,防止死锁
        log.info("获取{},ThreadName{}",Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,Thread.currentThread());//哪个线程获取了这个锁
        int hour=Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.hour","2"));//hour是表示多少小时之前的订单
        orderService.closeOrder(hour);
        //结束完后 删除锁 主动释放锁
        RedisShardedPoolUtil.del(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        log.info("释放分布式锁{},ThreadName{}",Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,Thread.currentThread());

    }

    public void closeOrderTaskV3()
    {
        long lockTimeout=Long.parseLong(PropertiesUtil.getProperty("lock.timeout","5000"));
        Long setNxResult= RedisShardedPoolUtil.setnx(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,String.valueOf(System.currentTimeMillis()+lockTimeout));
        if(setNxResult!=null && setNxResult.intValue()==1){
            //获取分布式锁成功
            closeOrder(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        }else {
            //未获取分布式锁，继续判断，判断时间戳，看是否可以充值并获取分布式锁
            String lockValue=RedisShardedPoolUtil.get(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
            if(lockValue!=null && System.currentTimeMillis()>Long.parseLong(lockValue)){
                //获取到锁，并且锁已经失效了
                //getSet 设置新值，返回旧值
                String getSetResult=RedisShardedPoolUtil.getSet(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,String.valueOf(System.currentTimeMillis()+lockTimeout));
                //再次用当前时间戳getSet,返回给定的key的旧值-->旧值判断，是否可以获取锁
                if(getSetResult==null||(getSetResult!=null && StringUtils.equals(getSetResult,lockValue))){
                    //真正获取锁
                    closeOrder(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
                }
                else {
                    log.info("没有获取到分布式锁");
                }
            }else {
                //代表老的分布式锁还没有失效，还在使用
                log.info("没有获取到分布式锁");
            }
        }
        log.info("关闭订单定时任务结束");
    }


}
