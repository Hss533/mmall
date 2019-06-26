package com.mmall.dao;

import com.mmall.pojo.Order;
import com.mmall.pojo.OrderItem;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface OrderItemMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderItem record);

    int insertSelective(OrderItem record);

    OrderItem selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderItem record);

    int updateByPrimaryKey(OrderItem record);

    List<OrderItem> getByOrderNumAndUserId(@Param("userId") Integer userId, @Param("orderNum") Long orderNum);

    List<OrderItem> getByOrderNo(@Param("orderNo")Long orderNo);

    void batchInsert(@Param("orderItemList") List<OrderItem> orderItemList);

    //二期新增加定时关闭订单
    List<Order> selectOrderStatusByCreateTime(@Param("status")Integer status, @Param("date")String date);


    int closeOrderByOrderId(Integer id);
}