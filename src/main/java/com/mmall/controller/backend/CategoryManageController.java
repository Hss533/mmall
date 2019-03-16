package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 商品类别管理
 */
@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private ICategoryService iCategoryService;
    /**
     * 增加商品种类
     * @param session
     * @param categoryName
     * @param parent_id
     * @return
     */
        @RequestMapping(value = "add_category.do" ,method = RequestMethod.POST)
        @ResponseBody
        //defaultValue指的是前端传回来的值默认为0
        public ServerResponse addCategory(HttpSession session,String categoryName,@RequestParam(value = "parent_id",defaultValue = "0") int parent_id)
        {
            User  currentUser=(User) session.getAttribute(Const.CURRENT_USE);
           if(currentUser==null)
           {
               return  ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录请登录");
           }
           else
           {
               ServerResponse response=iUserService.isAdmin(currentUser);
               if(response.idSucsess())
               {
                   ServerResponse response2 = iCategoryService.addCategory(categoryName, parent_id);
                   return response2;
               }
               else return response;
           }

        }
        @RequestMapping(value = "update_category.do",method = RequestMethod.POST)
        @ResponseBody
        public ServerResponse updateCategoryName(HttpSession session,Integer categoryId,String categoryName)
        {
            User  currentUser=(User) session.getAttribute(Const.CURRENT_USE);
            if(currentUser==null)
            {
                return  ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录请登录");
            }
            else
            {
                ServerResponse response=iUserService.isAdmin(currentUser);
                if(response.idSucsess())
                {
                   ServerResponse response1=iCategoryService.updateCategoryName(categoryId,categoryName);
                   return response1;//返回更新结果值
                }
                else return response;//返回不是管理员
            }
        }

        /**
         * 根据id获取的是子节点的商品
         * @param session
         * @param categoryId
         * @return
         */
        @RequestMapping(value = "get_category.do",method = RequestMethod.POST)
        @ResponseBody
        public ServerResponse getChildParallelCategory(HttpSession session,@RequestParam(value = "categoryId",defaultValue = "0")Integer categoryId)
        {
            User currentUser=(User) session.getAttribute(Const.CURRENT_USE);
            if(currentUser==null)
            {
                return  ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录请登录");
            }
            else
            {
                ServerResponse response=iUserService.isAdmin(currentUser);
                if(response.idSucsess())
                {
                    //返回子节点平级的商品类别，不进行递归
                    ServerResponse response1=iCategoryService.getChildrenParallelCategory(categoryId);
                    return response1;//返回更新结果值
                }
                else return response;//返回不是管理员
            }
        }

    /**
     * 递归查询本节点和子节点的ID
     * @param session
     * @param categoryId
     * @return
     */
    @RequestMapping(value = "get_deep_category.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getCategoryAndDeepChildrenCategory(HttpSession session,@RequestParam(value = "categoryId",defaultValue = "0")Integer categoryId)
    {
        User  currentUser=(User) session.getAttribute(Const.CURRENT_USE);
        if(currentUser==null)
        {
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录请登录");
        }
        else
        {
            ServerResponse response=iUserService.isAdmin(currentUser);
            if(response.idSucsess())
            {
                ServerResponse response1=iCategoryService.selectCategoryAndChildrenById(categoryId);
                return response1;
            }
            else return response;
        }
    }

}
