package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetial;
import com.mmall.vo.ProductListVo;

public interface IProductService {

    //保存或者更新
    ServerResponse<String> saveOrUpdate(Product product);

    ServerResponse<String> updateStatus(Integer productId,Integer status);
    //获取商品详细信息

    ServerResponse<ProductDetial> manageGetDetails(Integer productId);

    ServerResponse<PageInfo> getProductList(int pageNum, int pageSize);

    ServerResponse<PageInfo> productSearch(int pageNum, int pageSize,String productName,Integer productId);

    //进行搜索 涉及分页操作
    ServerResponse<PageInfo> getProductByKeywordCategory(String keyWord,Integer categoryId,int pageNum,int pageSize,String orderBy);
}
