package com.mmall.dao;

import com.google.common.collect.Lists;
import com.mmall.pojo.Shipping;
import com.mmall.pojo.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShippingMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Shipping record);

    int insertSelective(Shipping record);

    Shipping selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Shipping record);

    int updateByPrimaryKey(Shipping record);

    int deleteByUserIdShippingId(@Param("userId") Integer userId,@Param("shippingId")Integer shippingId);

    int  updateByShipping(Shipping shipping);

    Shipping selectByUserIdShippingId(@Param("userId")Integer userId,@Param("shippingId")Integer shippingId);

    List<Shipping> selectByUserId(Integer userId);
}