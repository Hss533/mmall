package com.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.service.IProductService;
import com.mmall.vo.CartVo;
import com.mmall.vo.ProductDetial;
import com.mmall.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * 用户模块的商品
 */
@Controller
@RequestMapping("/product/")
public class ProductController
{

    @Autowired
    private IProductService productService;

    @RequestMapping(value = "detail.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<ProductDetial> detail(Integer productId)
    {
        return productService.manageGetDetails(productId);
    }

    @RequestMapping(value = "/{productId}",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<ProductDetial> detailRESTful(@PathVariable("productId") Integer productId)
    {
        return productService.manageGetDetails(productId);
    }

    /**
     * 搜索商品  如果没有任何就是商品列表
     * @param keyword
     * @param categoryId
     * @param pageNum
     * @param pageSize
     * @param orderBy
     * @return
     */
    @RequestMapping(value = "list.do",method = RequestMethod.GET)
    @ResponseBody
    private ServerResponse<PageInfo> list(@RequestParam(value = "keyword",required = false)String keyword,
                                          @RequestParam(value = "categoryId",required = false)Integer categoryId,
                                          @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                          @RequestParam(value = "pageSize",defaultValue = "10") int pageSize,
                                          @RequestParam(value = "orderBy",defaultValue = "") String orderBy)
    {
        return  productService.getProductByKeywordCategory(keyword,categoryId,pageNum,pageSize,orderBy);
    }

    //TODO 改成restful这种资源定位的话，不能为空，必须是占位的。
    @RequestMapping(value = "/{keyword}/{categoryId}/{pageNum}/{pageSize}/{orderBy}",method = RequestMethod.GET)
    @ResponseBody
    private ServerResponse<PageInfo> listRESTful(@PathVariable(value = "keyword")String keyword,
                                          @PathVariable(value = "categoryId")Integer categoryId,
                                          @PathVariable(value = "pageNum") Integer pageNum,
                                          @PathVariable(value = "pageSize") Integer pageSize,
                                          @PathVariable(value = "orderBy") String orderBy)
    {
        if(pageNum==null){
            pageNum=1;
        }
        if(pageSize==null){
            pageSize=10;
        }
        if(StringUtils.isBlank(orderBy)){
            orderBy="price_asc";
        }

        return  productService.getProductByKeywordCategory(keyword,categoryId,pageNum,pageSize,orderBy);
    }

    /**
     如果有两个接口
     /{categoryId}/{pageNum}/{pageSize}/{orderBy}
     /{keyword}/{pageNum}/{pageSize}/{orderBy}
     这个是不行的，spring MVC无法判别应该把请求分发到哪个接口，

     自定义资源占位
     如果想要改变这种情况，要 这样
     /keyword/{keyword}/{pageNum}/{pageSize}/{orderBy}
     注意！！这个keyword是常量不是变量
     /category/{categoryId}/{pageNum}/{pageSize}/{orderBy}

     */
}
