package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import com.mmall.util.RedisPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service("iUserService")
public class IUserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {

        int resultCount = userMapper.checkUsername(username);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        //todo 用户名和密码 在登录的时候比较的时加密后的密码
        User user = userMapper.selectLogin(username, MD5Util.MD5EncodeUtf8(password));
        if (user == null) {
            return ServerResponse.createByErrorMessage("密码错误");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功", user);

    }

    /**
     * 注册
     *
     * @param user
     * @return
     */
    @Override
    public ServerResponse<String> register(User user) {
        ServerResponse validResponse = this.check_vaild(user.getUsername(), Const.USERNAME);
        if (!validResponse.idSucsess()) {
            return validResponse;
        }
        validResponse = this.check_vaild(user.getEmail(), Const.EMAIl);
        if (!validResponse.idSucsess()) {
            return validResponse;
        }
        //使用枚举显得有点厚重 所以可以折中采用接口的方式
        user.setRole(Const.Role.ROLE_CUSTOMER);
        //接下来时MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int resultCount = userMapper.insert(user);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");
    }

    /**
     * 用户进行验证
     *
     * @param str
     * @param type
     * @return
     */
    @Override
    public ServerResponse<String> check_vaild(String str, String type) {
        int resultCount = 0;
        if (StringUtils.isNotBlank(type)) {
            //开始校验
            if (Const.USERNAME.equals(type)) {
                resultCount = userMapper.checkUsername(str);
                if (resultCount > 0) {
                    return ServerResponse.createByErrorMessage("用户名已经存在");
                }
                else {
                    return ServerResponse.createBySuccessMessage("用户名未注册");
                }
            }
            if (Const.EMAIl.equals(type)) {
                resultCount = userMapper.checkEmail(str);
                if (resultCount > 0) {
                    return ServerResponse.createByErrorMessage("邮箱已经存在");
                }
                else {
                    return  ServerResponse.createBySuccessMessage("邮箱未注册");
                }
            }
            return ServerResponse.createByErrorMessage("参数错误");
        } else
            return ServerResponse.createByErrorMessage("参数错误");

    }

    @Override
    public ServerResponse<String> selectQuestion(String username) {
        //检查用户名是否存在
        ServerResponse validResponse = this.check_vaild(username, Const.USERNAME);
        if (validResponse.idSucsess()) {
            //用户名不存在
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String question = userMapper.selectQuestionByUserName(username);
        if (StringUtils.isNotBlank(question)) {
            return ServerResponse.createByErrorMessage(question);
        }
        return ServerResponse.createByErrorMessage("找回密码的问题不存在");
    }

    /**
     * 第二期吧check
     * @param username
     * @param question
     * @param answer
     * @return
     */
    @Override
    public ServerResponse<String> forgetCheckAnswer(String username, String question, String answer) {
        int resultCount = userMapper.checkAnswer(username, question, answer);
        if (resultCount > 0) {
            //说明这个判断正确，是本人的问题及问题的答案
            String forgetToken = UUID.randomUUID().toString();
            //把这个forgetoken放到本地token中
            //TokenCache.setKey(TokenCache.TOKEN_PREFIX + username, forgetToken);
            //二期就不能把这个放到本地了  要放到Redis中
            RedisPoolUtil.setEx(TokenCache.TOKEN_PREFIX + username,forgetToken,Const.RedisCachneExtime.REDIS_FORGETTOKEN_SETIME);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("问题的答案错误");
    }

    /**
     * 重置密码操作
     *
     * @param username
     * @param password
     * @param forgetToken
     * @return
     */
    @Override
    public ServerResponse<String> forgetResetPassword(String username, String password, String forgetToken) {
        if (StringUtils.isBlank(forgetToken)) {
            return ServerResponse.createByErrorMessage("参数错误,token需要传递");
        }
        ServerResponse validResponse = this.check_vaild(username, Const.USERNAME);
        if (validResponse.idSucsess()) {
            //用户不存在
            return ServerResponse.createByErrorMessage("用户不存在");
        }
//      String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);
        String token=RedisPoolUtil.get(TokenCache.TOKEN_PREFIX + username);
        if (StringUtils.isBlank(token)) {
            return ServerResponse.createByErrorMessage("token无效或者token过期了");
        }
        if (StringUtils.equals(forgetToken, token)) {
            String md5Password = MD5Util.MD5EncodeUtf8(password);
            int rowCount = userMapper.updatePasswordByUsername(username, md5Password);
            if (rowCount > 0) {
                return ServerResponse.createBySuccessMessage("修改密码成功");

            }
        } else {
            return ServerResponse.createByErrorMessage("token错误，请重新获取重置密码的token");

        }
        return ServerResponse.createByErrorMessage("修改密码失败");

    }

    @Override
    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user) {
        //这个User防止横向越权，要校验一下用户的旧密码，一定要指定是这个用户
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld), user.getId());
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("密码不正确");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        //有选择性的进行更新
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if (updateCount > 0) {
            return ServerResponse.createBySuccessMessage("密码更新成功");
        } else
            return ServerResponse.createByErrorMessage("密码更新失败");
    }

    @Override
    public ServerResponse<User> updateInformation(User user) {
        //username不能被更新
        //Email也要进行一个校验
        int resiltCount = userMapper.checkEmailByUserId(user.getUsername(), user.getEmail());
        if (resiltCount > 0) {
            return ServerResponse.createByErrorMessage("email已经存在,请尝试别的Email");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        //采用这个更新，有什么更新什么
        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if (updateCount > 0) {
            return ServerResponse.createBySuccess("更新个人信息成功", updateUser);
        }

        return ServerResponse.createByErrorMessage("更新个人信息失败");
    }

    /**
     * 对于web聊天室来说，将用户登录的信息（user对象，session）以hashmap的形式进行存储
     * 在统一浏览器上，所有用户登录所生成的session都是相同的
     * 为了防止相同的用户在不同的浏览器上进行多次登录，所以在登录的时候要
     * 所以要在不同的浏览器上登录不同的用户的化，要调用方法先绑定后解绑，在绑定 这个顺序是不能更改的，是Java源码这么写的
     * 所以如果出现姓名相同的用户在不同浏览器上进行登录的话，就会出现两个浏览器上的用户都下线的情况，因为两个对象相同，
     * 所以两个都会进行下线，因为是先，所以在判断的时候
     */
    @Override
    public ServerResponse<User> getInformation(Integer userId) {

        User user = userMapper.selectByPrimaryKey(userId);
        if (userId == null) {
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }

        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);

    }

    //后端
    @Override
    public ServerResponse isAdmin(User user) {
        if (user.getRole() == Const.Role.ROLE_ADMIN && user != null)
            return ServerResponse.createBySuccessMessage("是管理员");
        else
            return ServerResponse.createByErrorMessage("不是管理员");

    }

    @Override
    public ServerResponse idAdminAndIsNotnull(User user) {

        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "未登录需要登录");
        } else {
            if (user.getRole() == Const.Role.ROLE_ADMIN) {
                return ServerResponse.createBySuccess("可以");
            } else {
                return ServerResponse.createByErrorMessage("没有管理员权限");
            }
        }

    }
}
