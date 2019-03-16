package com.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.service.IProductService;
import com.mmall.vo.CartVo;
import com.mmall.vo.ProductDetial;
import com.mmall.vo.ProductListVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 用户模块的商品
 */
@Controller
@RequestMapping("/product/")
public class ProductController {
    @Autowired
    private IProductService productService;

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<ProductDetial> detail(Integer productId)
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
    @RequestMapping("list.do")
    @ResponseBody
    private ServerResponse<PageInfo> list(@RequestParam(value = "keyword",required = false)String keyword,
                                          @RequestParam(value = "categoryId",required = false)Integer categoryId,
                                          @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                          @RequestParam(value = "pageSize",defaultValue = "10") int pageSize,
                                          @RequestParam(value = "orderBy",defaultValue = "") String orderBy)
    {

        return  null;
    }
}
