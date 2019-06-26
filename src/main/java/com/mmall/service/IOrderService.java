package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.vo.OrderVo;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface IOrderService
{
     ServerResponse pay(Integer userId, String path, Long orderNo);
     ServerResponse aliPayCallBack(Map<String,String> params);
     ServerResponse queryOrderPayStatus(Integer userId,Long orderNo);
     ServerResponse createOrder(Integer userId,Integer shippingId);
     ServerResponse<String> cancel(Integer userId,Long orderNo);
     ServerResponse getOrderCartProduct(Integer userId);
     ServerResponse<OrderVo> getOrderDatail(Integer userId,Long orderNo);


     ServerResponse<PageInfo> getOrderList(Integer userId, int pageNum, int pageSize);
     //backend
     ServerResponse<PageInfo> manageList(int pageNum,int pageSize);
     ServerResponse<OrderVo> manageDetail(Long orderNo);
     ServerResponse<PageInfo> manageSearch(Long orderNo,int pageNum,int pageSize);
     ServerResponse<String> manageSendGoods(Long orderNo);

     //关闭订单 hour个小时以内，未付款的订单，进行关闭
     void closeOrder(int hour);

}
