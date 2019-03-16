package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Shipping;
import com.mmall.pojo.User;
import org.springframework.stereotype.Service;

import java.util.List;


public interface IShippingServer
{

    ServerResponse add(Shipping shipping, Integer userId);
    ServerResponse update(Shipping shipping,Integer userId);
    ServerResponse del(Integer shippingId,Integer userId);
    //ServerResponse<Shipping> select(Integer userId,Integer shippingId);
    ServerResponse<PageInfo> list(Integer userId,Integer pageNum,Integer pageSize);
    ServerResponse<Shipping> detail(Integer userId, Integer ShippingId);
}
