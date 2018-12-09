package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mysql.fabric.Server;


public interface IUserService
{
        ServerResponse<User> login(String username, String password );
        ServerResponse<String> register(User user);
        ServerResponse<String> check_vaild(String str,String type);
        ServerResponse<String> selectQuestion(String username);
        ServerResponse<String > forgetCheckAnswer(String username,String question,String answer);
        ServerResponse<String> forgetResetPassword(String username,String password,String forgetToken);
        ServerResponse<String> resetPassword(String passwordOld,String passwordNew,User user);
        ServerResponse<User> updateInformation(User user);
        ServerResponse getInformation(Integer userId);
        ServerResponse isAdmin(User user);
        ServerResponse<String> idAdminAndIsNotnull(User user);
}
