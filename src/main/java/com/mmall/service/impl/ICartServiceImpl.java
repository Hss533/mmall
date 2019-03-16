package com.mmall.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.ICartService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.CartProductVo;
import com.mmall.vo.CartVo;
import com.mysql.fabric.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

@Service("iCartService")
public class ICartServiceImpl implements ICartService
{

    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;

    /**
     * 向购物车中添加商品
     * @param userId
     * @param productId
     * @param count
     * @return
     */
    public ServerResponse<CartVo> add(Integer userId,Integer productId,Integer count){

        Product product=null;
        if(productId!=null)
        product=productMapper.selectByPrimaryKey(productId);
        //如果商品不存在，商品不在销售状态，要添加的商品数量<=0
        if(product==null ||count==null||product.getStatus()!=Const.ProductStatus.ON_SALE||count<=0)
        {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart=cartMapper.selectCartByUserIdProductId(userId,productId);
        if(cart==null)
        {
            //这个产品不再购物车里面，需要新增一个这个产品的记录
            Cart cartItem=new Cart();
            cartItem.setQuantity(count);
            cartItem.setChecked(Const.Cart.CHECKED);//被选中
            cartItem.setProductId(productId);
            cartItem.setUserId(userId);
            cartMapper.insert(cartItem);
        }
        else
        {
            //这个产品已经在购物车里面了。如果产品已经存在，数量相加
            count=cart.getQuantity()+count;
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKey(cart);
        }
        //添加完商品之后，再返回购物车
        return this.list(userId);
    }

    /**
     * 这个应该是修改购物车中商品的数量
     * @param userId
     * @param productId
     * @param count
     * @return
     */
    public ServerResponse<CartVo> update(Integer userId,Integer productId,Integer count)
    {
        Product product=null;
        if(productId!=null)
        {
            product=productMapper.selectByPrimaryKey(productId);
        }
        if(productId==null ||count==null||product==null||product.getStatus()!=Const.ProductStatus.ON_SALE||count<=0)
        {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        cartMapper.selectCartByUserIdProductId(userId,productId);
        return  this.list(userId);
    }

    /**
     * 删除商品，可能是删除多个商品
     * @param userId
     * @param productIds
     * @return
     */
    public ServerResponse<CartVo> deleteProduct(Integer userId,String  productIds)
    {
        List<String> productList= Splitter.on(",").splitToList(productIds);//将其以逗号分隔开来然后再变成list
        if(productList.size()==0)
        {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());

        }
        cartMapper.deleteByUserIdProductIds(userId,productList);
        return this.list(userId);
    }

    /**
     * 将购物车中返回
     * @param userId
     * @return
     */
    public ServerResponse<CartVo> list (Integer userId)
    {
        CartVo cartVo=this.getCartLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }

    private CartVo getCartLimit(Integer userId)
    {
        CartVo cartVo=new CartVo();

        List<Cart> cartList=cartMapper.selectCartByUserId(userId);//根据ID查找出该人的购物车中所有的商品数量
        List<CartProductVo> cartProductVoList=Lists.newArrayList();

        BigDecimal cartTotalPrice=new BigDecimal("0");

        if(org.apache.commons.collections.CollectionUtils.isNotEmpty(cartList))
        {
            for(Cart cart:cartList)
            {
                CartProductVo cartProductVo=new CartProductVo();
                cartProductVo.setId(cart.getId());
                cartProductVo.setUserId(cart.getUserId());
                cartProductVo.setProductId(cart.getProductId());

                //查询商品信息
                Product product=productMapper.selectByPrimaryKey(cart.getProductId());
                if(product!=null)
                {
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setPriductStatus(product.getStatus());
                    cartProductVo.setProductPricel(product.getPrice());
                    cartProductVo.setProductStock(product.getStock());
                    //判断库存
                    int buyLimitCounr=0;
                    if(product.getStock()>=cart.getQuantity())
                    {
                        //库存充足
                        buyLimitCounr=cart.getQuantity();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    }
                    else {
                        buyLimitCounr=product.getStock();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                        //购物城中更新有效库存
                        Cart cartForQuantity=new Cart();
                        cartForQuantity.setId(cart.getId());
                        cartForQuantity.setQuantity(buyLimitCounr);
                        cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                    }
                    cartProductVo.setQuantity(buyLimitCounr);
                    //计算总价
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.cheng(buyLimitCounr,product.getPrice().doubleValue()));
                    cartProductVo.setProductChecked(cart.getChecked());
                }
                //选中的再加上价钱
                if(cart.getChecked()==Const.Cart.CHECKED)
                {
                    //这一行有错误
                    cartTotalPrice=BigDecimalUtil.add(cartProductVo.getProductTotalPrice().doubleValue(),cartTotalPrice.doubleValue());
                }
                cartProductVoList.add(cartProductVo);
            }

        }
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVoList(cartProductVoList);
        //判断是不是全选状态
        cartVo.setAllchecked(this.getAllCheckedStatus(userId));
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return cartVo;
    }

    /***
     * 判断是否全选
     * @param userId
     * @return
     */
    private boolean getAllCheckedStatus(Integer userId)
    {
        if(userId==null)
        {
            return  false;
        }
        return cartMapper.selectCartProductCheckedStatusByUserId(userId)==0;
    }

    /**
     * 全选或者全反选
     * @param userId
     * @param productId
     * @param checked
     * @return
     */
    public ServerResponse<CartVo> selectOrUnSelect(Integer userId,Integer productId,Integer checked)
    {
        cartMapper.checkedOrUncheckedProduct(userId,productId,checked);
        return this.list(userId);
    }

    /**
     * 获取购物车里面商品的总数
     * 如果一件商品的商品数量增加了10，那么总的商品数量应该是增加了10，而不是1.
     * @param userId
     * @return
     */
    public ServerResponse<Integer> getCartProductCount(Integer userId)
    {
        //如果没有登陆的话，不是显示错误，应该显示的是没有商品
        if (userId==null)
        {
            return ServerResponse.createBySuccess(0);
        }
        return  ServerResponse.createBySuccess(cartMapper.selectCartProductCount(userId));
    }
}
