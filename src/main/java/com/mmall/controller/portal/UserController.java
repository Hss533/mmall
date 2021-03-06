package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user/")
public class UserController {
    @Autowired
    private IUserService iUserService;
    /**
     * 用户登录模块
     * @param username
     * @param password
     * @param session
     * @return
     */
    //@ResponseBody是自动通过springmvc中的将返回值序列化成json
    @RequestMapping(value = "login.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(@RequestParam(value = "username",required = false) String username, String password, HttpSession session)
    {
       ServerResponse<User> response=iUserService.login(username,password);
       if(response.idSucsess())
       {
           session.setAttribute(Const.CURRENT_USE,response.getData());
       }
       return response;
    }


    @ResponseBody
    @RequestMapping(value = "logout.do",method = RequestMethod.POST)
    public ServerResponse<String> logout(HttpSession session)
    {
        session.removeAttribute(Const.CURRENT_USE);
        return  ServerResponse.createBySuccess();

    }


    @RequestMapping(value = "register.do",method =RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user){
        return iUserService.register(user);
    }

    //校验用户名和邮箱是否存在 防止通过接口进行调用
    // 防止用户通过接口调用注册接口 注册的时候通过实时调用这个接口
    @RequestMapping(value = "check_valid.do",method =RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str,String type){
        //这个type是用来进行校验的，来校验ta时用户名还是邮箱
        return iUserService.check_vaild(str,type);
    }


    @RequestMapping(value = "get_user_info.do",method =RequestMethod.POST)
    @ResponseBody
    //获取用户登录信息
    public ServerResponse<User> getUserInfo(HttpSession session)
    {
        User user=(User)session.getAttribute(Const.CURRENT_USE);
        if(user!=null)
        {
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");
    }

    //忘记密码
    @RequestMapping(value = "forget_get_question.do",method =RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> selectQuestion(String username)
    {
        return iUserService.selectQuestion(username);
    }

    //回答问题 利用本地的guava缓存来设置token,guava 是有有效期的，以此来设置token的有效期
    @RequestMapping(value = "forget_check_answer.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username, String question, String answer) {
        return  iUserService.forgetCheckAnswer(username,question,answer);

    }

    //重置密码
    @RequestMapping(value = "forget_reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetResetPassword(String username,String password,String forgetToken)
    {
        return  iUserService.forgetResetPassword(username,password,forgetToken);
    }

    //登录状态的重置密码
    @RequestMapping(value = "reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(HttpSession session,String passwordOld,String passwordNew)
    {
        User user=(User) session.getAttribute(Const.CURRENT_USE);
        if(user==null)
        {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        return iUserService.resetPassword(passwordOld,passwordNew,user);
    }

    //更新用户个人信息，更新完成之后要更新session还要将更新完成之后的用户返回给前端
    @RequestMapping(value = "update_information.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> update_information(HttpSession session,User user)
    {

        User currentUser=(User)session.getAttribute(Const.CURRENT_USE);
        if(currentUser==null)
        {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        //为什么没有id?
        //防止越权问题？？
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        //更新的用户的username和id都是从登录用户中获取的
        ServerResponse response=iUserService.updateInformation(user);
        if(response.idSucsess())
        {
            session.setAttribute(Const.CURRENT_USE,response.getData());
        }
        return response;
    }

    //获取用户的详细信息
    @RequestMapping(value = "get_information.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> get_information(HttpSession session)
    {
        //如果调用这个接口用户没有登录  则强制登录
        User currentUser=(User) session.getAttribute(Const.CURRENT_USE);

        if(currentUser==null)
        {
            System.out.println("asdsa");
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录许需要强制登录status=10");

        }
       return iUserService.getInformation(currentUser.getId());
    }
}
