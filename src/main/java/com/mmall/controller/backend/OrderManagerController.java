package com.mmall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import com.mmall.service.IUserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisPoolUtil;
import com.mmall.vo.OrderVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import  java.lang.Math;
@Controller
@RequestMapping("/manage/order")
public class OrderManagerController
{

    @Autowired
    private IOrderService orderService;

    @Autowired
    private IUserService userService;

    @RequestMapping("order_list.do")
    @ResponseBody
    public ServerResponse<PageInfo> orderList(HttpServletRequest request,
                                              @RequestParam(value = "pageNum",defaultValue = "1")int pageNum,
                                              @RequestParam(value = "pageSize",defaultValue = "10")int pageSize)
    {
        String loginToken= CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(loginToken))
        {
            return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");
        }
        String userJson= RedisPoolUtil.get(loginToken);
        User currentUser= JsonUtil.string2Object(userJson,User.class);
        if(currentUser==null)
        {
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录管理员");
        }
        if(userService.idAdminAndIsNotnull(currentUser).idSucsess())
        {
            return  orderService.manageList(pageNum,pageSize);
        }
        else
        {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }
    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<OrderVo> detail(HttpServletRequest request, Long orderNo)
    {
        String loginToken= CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(loginToken))
        {
            return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");
        }
        String userJson= RedisPoolUtil.get(loginToken);
        User currentUser= JsonUtil.string2Object(userJson,User.class);
        if(currentUser==null)
        {
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录管理员");
        }
        if(userService.idAdminAndIsNotnull(currentUser).idSucsess())
        {
            return  orderService.manageDetail(orderNo);
        }
        else
        {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }
    //为以后的分页查询做打算
    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse search(HttpServletRequest request,
                                 Long orderNum,
                                 @RequestParam(value = "pageNum",defaultValue = "1")int pageNum,
                                 @RequestParam(value = "pageSize",defaultValue = "10")int pageSize)
    {
        String loginToken= CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(loginToken))
        {
            return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");
        }
        String userJson= RedisPoolUtil.get(loginToken);
        User currentUser= JsonUtil.string2Object(userJson,User.class); if(currentUser==null)
        {
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录管理员");
        }
        if(userService.idAdminAndIsNotnull(currentUser).idSucsess())
        {

            return  orderService.manageSearch(orderNum,pageNum,pageSize);
        }
        else
        {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    @RequestMapping("send_goods.do")
    @ResponseBody
    public ServerResponse sendGoods(HttpServletRequest request,Long orderNum)
    {
        String loginToken= CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(loginToken))
        {
            return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");
        }
        String userJson= RedisPoolUtil.get(loginToken);
        User currentUser= JsonUtil.string2Object(userJson,User.class);
        if(currentUser==null)
        {
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录管理员");
        }
        if(userService.idAdminAndIsNotnull(currentUser).idSucsess())
        {
            return  orderService.manageSendGoods(orderNum);
        }
        else
        {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }
}
