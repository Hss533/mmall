package com.mmall.controller.common;
/**
 * 用户在网站上进行任何操作之后，网站都应该重置session的过期时间
 * 过滤.do请求
 */

import com.mmall.common.Const;
import com.mmall.common.RedisPool;
import com.mmall.pojo.User;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisPoolUtil;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SessionExpireFilter  implements Filter{

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest=(HttpServletRequest) servletRequest;
        String loginToken= CookieUtil.readLoginToken(httpServletRequest);

        //判断logintoken是否为空 或者“”
        //如果不为空的话  符合条件 继续拿user信息
        if(StringUtils.isNotEmpty(loginToken)){
            String userJsonStr= RedisPoolUtil.get(loginToken);
            User user= JsonUtil.string2Object(userJsonStr,User.class);
            if(user!=null){
                //如果user不为空，则重置session的时间  ，即调用expire命令
                RedisPoolUtil.expire(loginToken, Const.RedisCachneExtime.REDIS_SESSION_SETIME);
            }
        }
        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {

    }
}
