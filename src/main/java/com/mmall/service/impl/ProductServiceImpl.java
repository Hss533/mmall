package com.mmall.service.impl;

import com.mmall.common.ServerResponse;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Product;
import com.mmall.service.IProductService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("iProductService")
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public ServerResponse<String> saveOrUpdate(Product product) {

        if(product!=null)
        {

            //设置主图
            if(StringUtils.isNotBlank(product.getSubImages()))
            {
                String[] images=product.getSubImages().split(",");//中间是以，进行分离的
                if(images.length>0)
                {
                    product.setMainImage(images[0]);
                }
            }
            if(product.getId()!=null)
            {
                //更新的时候是全部更新
                int rowCount =productMapper.updateByPrimaryKey(product);
                if(rowCount>0)
                return ServerResponse.createBySuccessMessage("更新产品成功");
                else ServerResponse.createByErrorMessage("更新产品失败");
            }
            else {
                int insertCount=productMapper.insert(product);
                if(insertCount>0)
                    return ServerResponse.createBySuccessMessage("添加产品成功");
                else ServerResponse.createByErrorMessage("添加产品失败");
            }
        }
        else {
            return ServerResponse.createByErrorMessage("参数失败");
        }
        return null;
    }
}
