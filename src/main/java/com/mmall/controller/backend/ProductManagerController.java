package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.sun.java.browser.net.ProxyService;
import org.apache.ibatis.annotations.InsertProvider;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.jws.soap.SOAPBinding;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/product")
public class ProductManagerController {


    @Autowired
    private IUserService userService;
    @Autowired
    private IProductService iProductService;


    public ServerResponse<String> updateAndInsertProduct(HttpSession session)
    {
        User user=(User) session.getAttribute(Const.CURRENT_USE);
        ServerResponse response;
        response=userService.idAdminAndIsNotnull(user);
        if(response.idSucsess())
        {

        }
        return response;
    }

    public static void main(String[] args) {

        User a=new User();
        User b=new User();
        System.out.println(a==b);//比较两个对象的物理地址是否相同
        System.out.println(a.equals(b));//
    }
}
