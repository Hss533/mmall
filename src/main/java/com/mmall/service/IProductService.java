package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;

public interface IProductService {

    //保存或者更新
    ServerResponse<String> saveOrUpdate(Product product);

}
