package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisShardedPoolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
//TODO 改正session 改为redis相关的
@Controller
@RequestMapping("/manage/user")
public class UserManagerController {
    @Autowired
    private IUserService iUserService;

    //登录成功之后要返回登录信息
    @RequestMapping(value = "login.do",method = RequestMethod.POST)
    @ResponseBody //返回的时候自动序列化成JSON
    public ServerResponse<User> login(String username, String password, HttpServletResponse httpServletResponse, HttpSession session)
    {
        ServerResponse<User> response=iUserService.login(username,password);
        if(response.idSucsess())
        {
            User user=response.getData();
            if(user.getRole()== Const.Role.ROLE_ADMIN)
            {
                CookieUtil.writeLoginToken(httpServletResponse,session.getId());
                RedisShardedPoolUtil.setEx(session.getId(), JsonUtil.obj2String(response.getData()),Const.RedisCachneExtime.REDIS_SESSION_SETIME);
                return response;
            }
            else {
                return ServerResponse.createByErrorMessage("不是管理员，无法进行登录");
            }

        }
        return response;
    }
    //TODO 管理员相当于是商户  普通用户相当于是消费者

}
