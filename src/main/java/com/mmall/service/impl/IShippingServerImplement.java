package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.PayInfo;
import com.mmall.pojo.Shipping;
import com.mmall.service.IShippingServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Service("iShippingServer")
public class IShippingServerImplement implements IShippingServer
{
    @Autowired
    private ShippingMapper shippingMapper;

    public ServerResponse add(Shipping shipping,Integer userId)
    {
        shipping.setUserId(userId);
        int rewCount=shippingMapper.insert(shipping);
        if(rewCount>0)
        {
            //新增地址之后，返回新增地址的ID
            Map map= Maps.newHashMap();
            map.put("shipping",shipping.getId());
            return ServerResponse.createBySuccess("新增地址成功",map);
        }
        return ServerResponse.createByErrorMessage("新增地址失败");
    }


    public ServerResponse del(Integer shippingId,Integer userId)
    {
        int rowCount=shippingMapper.deleteByUserIdShippingId(userId,shippingId);
        if(rowCount>0)
        {
            return ServerResponse.createByErrorMessage("删除地址成功");
        }
        else {
            return ServerResponse.createByErrorMessage("删除地址失败");
        }
    }


    public ServerResponse update(Shipping shipping,Integer userId)
    {
        shipping.setUserId(userId);//防止横向越权问题
        int rowCount=shippingMapper.updateByShipping(shipping);
        if(rowCount>0)
        {
            return ServerResponse.createByErrorMessage("修改地址成功");
        }
        else {
            return ServerResponse.createByErrorMessage("修改地址失败");
        }
    }



   /* public ServerResponse<Shipping> select(Integer userId,Integer shippingId)
    {
        Shipping shipping=shippingMapper.selectByUserIdShippingId(userId,shippingId);
        if(shipping==null)
        {
            return ServerResponse.createByErrorMessage("无法查询到该地址");
        }
        else  return ServerResponse.createBySuccess("更新地址成功",shipping);
    }*/

    @Override
    public ServerResponse<PageInfo> list(Integer userId,Integer pageNum,Integer pageSize)
    {
        PageHelper.startPage(pageNum,pageSize);
        List<Shipping> shipping=shippingMapper.selectByUserId(userId);
        PageInfo pageInfo=new PageInfo(shipping);
        return ServerResponse.createBySuccess(pageInfo);
    }

    @Override
    public ServerResponse<Shipping> detail(Integer userId, Integer shippingId)
    {
        Shipping shipping=shippingMapper.selectByUserIdShippingId(userId,shippingId);
        if(shipping==null)
        {
            return ServerResponse.createByErrorMessage("该地址不存在");
        }
        else  return ServerResponse.createBySuccess(shipping);
    }
}
