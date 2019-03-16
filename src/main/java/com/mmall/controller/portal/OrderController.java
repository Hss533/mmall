package com.mmall.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Order;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

@Controller
@RequestMapping("/order/")
public class OrderController
{

    private static final org.slf4j.Logger logger= LoggerFactory.getLogger(OrderController.class);
    @Autowired
    private IOrderService orderService;


    //创建订单
    @RequestMapping(value = "create.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse create(HttpSession session,Integer shippingId)
    {
        User user=(User)session.getAttribute(Const.CURRENT_USE);
        if(user==null)
        {
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return null;
    }





















































    @RequestMapping(value = "pay.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse pay(HttpSession session, Long orderNo, HttpServletRequest request)
    {
        User user=(User)session.getAttribute(Const.CURRENT_USE);
        if(user==null)
        {
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        String path=request.getSession().getServletContext().getRealPath("upload");

        return  orderService.pay(user.getId(),path,orderNo);
    }

    @RequestMapping("alipay_callback.do")
    @ResponseBody
    public Object aliPayCallBack(HttpServletRequest request)
    {
        Map<String ,String>  params= Maps.newHashMap();

        //支付宝会把所有的毁掉方法request中供我们自己获取
        Map  requestParams=  request.getParameterMap();
        for(Iterator iterator=requestParams.keySet().iterator();iterator.hasNext();)
        {
            String name=(String)iterator.next();
            String[] value=(String[]) requestParams.get(name);
            String valueStr="";
            for(int i=0;i<value.length;i++)
            {
                // i=0  value=length-1  length=1
                valueStr=(i==value.length-1)?valueStr+value[i]:valueStr+value[i]+",";
            }
            params.put(name,valueStr);
        }
        logger.info("支付宝回调，sign:{},trade_status:{},参数:{}",params.get("sign"),params.get("trade_status"),params.toString());
        //非常重要  验证回调的正确性 是不是支付宝发的 避免重复通知
        params.remove("sign_type");
        try
        {
            boolean alipayRSACheckedV2= AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(),"utf-8",Configs.getSignType());
            if(!alipayRSACheckedV2)
            {
                return ServerResponse.createByErrorMessage("非法请求！！请注意！");
            }
        }
        catch (AlipayApiException e)
        {
            logger.info("支付宝验签异常",e);
        }
        //todo 业务逻辑
        ServerResponse serverResponse=orderService.aliPayCallBack(params);
        if(serverResponse.idSucsess())
        {
            return Const.AlipayCallback.RESPONSE_SUCCESS;
        }
        return Const.AlipayCallback.RESPONSE_FAILED;

    }

    /**
     * 查询订单的状态
     * @param session
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "order_status.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse orderStatus(HttpSession session,Long orderNo)
    {
        User user=(User) session.getAttribute(Const.CURRENT_USE);
        if(user==null)
        {
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        ServerResponse serverResponse=orderService.queryOrderPayStatus(user.getId(),orderNo);
        if(serverResponse.idSucsess())
        {
            return ServerResponse.createBySuccess(true);
        }
        return ServerResponse.createBySuccess(false);
    }

}
