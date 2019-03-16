package com.mmall.controller.backend;

import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.util.PropertiesUtil;
import com.sun.java.browser.net.ProxyService;
import com.sun.tracing.dtrace.Attributes;
import org.apache.ibatis.annotations.InsertProvider;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.jws.soap.SOAPBinding;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/manage/product")
public class ProductManagerController {


    @Autowired
    private IUserService userService;
    @Autowired
    private IProductService iProductService;

    @Autowired
    private IFileService iFileService;
    /**
     * 保存商品
     * @param session
     * @param product
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "addAndUpdate.do", method = RequestMethod.POST)
    public ServerResponse<String> updateAndInsertProduct(HttpSession session, Product product)
    {
        User currentUser=(User) session.getAttribute(Const.CURRENT_USE);
        if(currentUser==null)
        {
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录管理员");
        }
        if(userService.idAdminAndIsNotnull(currentUser).idSucsess())
        {
            return  iProductService.saveOrUpdate(product);
        }
        else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    /**
     *修改商品状态
     * @param session
     * @param productId
     * @param status
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "update_status.do",method = RequestMethod.POST)
    public ServerResponse<String> updateStatus(HttpSession session,Integer productId,Integer status)
    {
        User currentUser=(User) session.getAttribute(Const.CURRENT_USE);
        ServerResponse response;
        response=userService.idAdminAndIsNotnull(currentUser);
        if(response.idSucsess())
        {
            return iProductService.updateStatus(productId,status);
        }
        else
            return ServerResponse.createByErrorMessage("无权限操作");
    }

    /**
     * 获取商品的详细信息
     * @return
     */
    @RequestMapping(value = "get_details.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getDetails(HttpSession session,Integer productId)
    {
        User currentUser=(User) session.getAttribute(Const.CURRENT_USE);
        ServerResponse response;
        response=userService.idAdminAndIsNotnull(currentUser);
        if(response.idSucsess())
        {
            return  iProductService.manageGetDetails(productId);
        }
        else return ServerResponse.createByErrorMessage("无权限操作");
    }

    /**
     * 后台产品列表
     * 可以进行分页查询
     * @param session
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "list.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getList(HttpSession session,
                                  @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                  @RequestParam(value = "pageSize",defaultValue = "10") int pageSize)
    {

        User currentUser=(User) session.getAttribute(Const.CURRENT_USE);
        ServerResponse response;
        response=userService.idAdminAndIsNotnull(currentUser);
        if(response.idSucsess())
        {
            return iProductService.getProductList(pageNum,pageSize);
        }
        else return ServerResponse.createByErrorMessage("无权限操作");
    }

    /**
     * 每次进行后台操作的时候都要进行登录判断
     * 后台产品搜索查询
     * @param session
     * @return
     */
    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse productSearch(HttpSession session,
                                        @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                        @RequestParam(value = "pageSize",defaultValue = "10") int pageSize,
                                        String productName,
                                        Integer productId)
    {
        User currentUser=(User) session.getAttribute(Const.CURRENT_USE);
        ServerResponse response;
        response=userService.idAdminAndIsNotnull(currentUser);
        if(response.idSucsess())
        {
            return iProductService.productSearch(pageNum,pageSize,productName,productId);
        }
        else return ServerResponse.createByErrorMessage("无权限操作");

    }

    /**
     * 上传图片
     * @return
     */
    @RequestMapping("upload.do")
    @ResponseBody
    public ServerResponse upload(HttpSession session,@RequestParam(value = "upload_file",required = false)MultipartFile file, HttpServletRequest request)
    {
        User currentUser=(User) session.getAttribute(Const.CURRENT_USE);
        ServerResponse response;
        response=userService.idAdminAndIsNotnull(currentUser);
        if(response.idSucsess())
        {
            String path=request.getSession().getServletContext().getRealPath("upload");
            //发布好之后，会在webApp同级穿件upload文件夹 创建文件夹，交给代码，不要自己去创建
            String targetFileNamr=iFileService.upload(file,path);
            String url= PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileNamr;
            Map fileMap= Maps.newHashMap();
            fileMap.put("uri",targetFileNamr);
            fileMap.put("url",url);
            return  ServerResponse.createBySuccess(fileMap);
        }
        else return ServerResponse.createByErrorMessage("无权限操作");

    }

    //富文本上传
    @RequestMapping("richtext_img_upload.do")
    @ResponseBody
    public Map upload(HttpSession session,
                      @RequestParam(value = "upload_file",required = false) MultipartFile file,
                      HttpServletRequest request, HttpServletResponse servletresponse)
    {

        Map resultMap=new HashMap();
        User currentUser=(User) session.getAttribute(Const.CURRENT_USE);
        ServerResponse response;
        response=userService.idAdminAndIsNotnull(currentUser);
        //富文本中对于返回值有着自己的要求，所以按照要求来
        if(response.idSucsess())
        {
            String path=request.getSession().getServletContext().getRealPath("upload");
            //发布好之后，会在webApp同级穿件upload文件夹 吧创建文件夹，交给代码，不要自己去创建
            String targetFileNamr=iFileService.upload(file,path);
            String url= PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileNamr;
            resultMap.put("success",true);
            resultMap.put("msg","上传成功");
            resultMap.put("file_path",url);
            servletresponse.addHeader("Access-Control-Allow-Headers","X-File-Name");
            return  resultMap;
        }
        else {
            resultMap.put("success",false);
            resultMap.put("msg","无权限操作");
            return  resultMap;
        }

    }
}
