package com.mmall.controller.portal;
import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Shipping;
import com.mmall.pojo.User;
import com.mmall.service.IShippingServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 收货地址模块的Controller层
 */
@Controller
@RequestMapping("/shipping/")
public class ShippingController
{

    @Autowired
    private IShippingServer shippingServer;

    //springMVC传对象 springMVC对象数据绑定
    @RequestMapping(value = "add.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse add(HttpSession session, Shipping shipping)
    {
        User user=(User)session.getAttribute(Const.CURRENT_USE);
        if(user==null)
        {
          return  ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return shippingServer.add(shipping,user.getId());
    }

    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse update(HttpSession session,Shipping shipping)
    {
        User user=(User)session.getAttribute(Const.CURRENT_USE);
        if(user==null)
        {
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return  shippingServer.update(shipping,user.getId());
    }

    @RequestMapping(value = "del.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse del(HttpSession session,Integer shippingId)
    {
        User user=(User)session.getAttribute(Const.CURRENT_USE);
        if(user==null)
        {
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return  shippingServer.del(shippingId,user.getId());
    }




    @RequestMapping(value = "list.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<PageInfo> list(@RequestParam(value = "pageNum",defaultValue = "1")int pageNum,
                                         @RequestParam(value = "pageSize",defaultValue = "10")int pageSize,
                                         HttpSession session)
    {
        User user=(User)session.getAttribute(Const.CURRENT_USE);
        if(user==null)
        {
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return  shippingServer.list(user.getId(),pageNum,pageSize);
    }

    //todo 地址详情

    @RequestMapping(value = "detail",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Shipping> detail(HttpSession session,Integer shippingId)
    {
        User user=(User)session.getAttribute(Const.CURRENT_USE);
        if(user==null)
        {
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return  shippingServer.detail(user.getId(),shippingId);
    }
}
