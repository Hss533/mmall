package com.mmall.task;

import com.mmall.service.IOrderService;
import com.mmall.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 下了订单之后，30min都没有付款的话，应该将订单关闭
 */
@Component
@Slf4j
public class CloseOrderTask {

    @Autowired
    private IOrderService orderService;

    //没有考虑Tomcat集群做的
    @Scheduled(cron = "0 */1 * * * ?")//每分钟整数倍执行一次
    public void closeOrderTaskV1()
    {
        log.info("关闭订单定时任务启动");
        //每分钟执行一次，执行的时候关闭在当前时间两小时前关闭的订单
        int hour=Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.hour","2"));
        orderService.closeOrder(hour);

    }

}
