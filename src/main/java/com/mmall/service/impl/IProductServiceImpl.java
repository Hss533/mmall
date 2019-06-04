package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.ICartService;
import com.mmall.service.ICategoryService;
import com.mmall.service.IProductService;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetial;
import com.mmall.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Service("iProductService")
public class IProductServiceImpl implements IProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ICategoryService iCategoryService;

    //保存或者新增
    @Override
    public ServerResponse<String> saveOrUpdate(Product product)
    {
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
                if(product.getStatus()==null)
                {
                    product.setStatus(1);
                }
                int insertCount=productMapper.insert(product);
                if(insertCount>0)
                    return ServerResponse.createBySuccessMessage("添加产品成功");
                else ServerResponse.createByErrorMessage("添加产品失败");
            }
        }
        else {
            return ServerResponse.createByErrorMessage("传入的参数有问题");
        }
        return null;
    }

    public ServerResponse<String> updateStatus(Integer productId,Integer status) {
        if (productId == null || status == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int rowCounr =productMapper.updateByPrimaryKeySelective(product);
        if(rowCounr>0)
        {
            return ServerResponse.createBySuccess("修改产品销售状态成功");
        }
        return  ServerResponse.createByErrorMessage("修改产品状态失败");
    }

    //使用vo进行填充

    public ServerResponse<ProductDetial> manageGetDetails(Integer productId)
    {
        if(productId==null)
        {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product=productMapper.selectByPrimaryKey((productId));
        if(product==null)
        {
            return ServerResponse.createByErrorMessage("产品已下架或者删除");
        }
        //pojo-->VO(value object)
        //pojo-->bo(bussiness object)-->vo(view object)
        //返回vo对象
        ProductDetial pro=assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(pro);
    }
    //把product的pojo变成vo
    private  ProductDetial assembleProductDetailVo(Product product)
    {
        ProductDetial productDetial=new ProductDetial();
        productDetial.setId(product.getId());
        productDetial.setSubtitle(product.getDetail());
        productDetial.setPrice(product.getPrice());
        productDetial.setMainImage(product.getMainImage());
        productDetial.setCategoryId(product.getCategoryId());
        productDetial.setName(product.getName());
        productDetial.setStatus(product.getStatus());
        productDetial.setSubImage(product.getSubImages());
        productDetial.setStock(product.getStock());
        productDetial.setDetail(product.getDetail());
        productDetial.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
        Category category=categoryMapper.selectByPrimaryKey(product.getId());
        if(category==null)
        {
            productDetial.setParentCateGoryId(0);//默认是一个根节点
        }
        else {
            productDetial.setParentCateGoryId(category.getParentId());
        }
        productDetial.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetial.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
        return  productDetial;
    }

    public ServerResponse<PageInfo> getProductList(int pageNum,int pageSize)
    {
        //pageHelper的使用，startPage--start 填充自己的SQL查询呢逻辑 pageHelper 收尾
        PageHelper.startPage(pageNum,pageSize);
        List<Product> productList=productMapper.selectList();
        List<ProductListVo> productListVoArrayList= Lists.newArrayList();
        for (Product product:productList)
        {
            ProductListVo productListVo=assembleProductListVo(product);
            productListVoArrayList.add(productListVo);
        }
        PageInfo pageInfo=new PageInfo(productList);
        pageInfo.setList(productListVoArrayList);
        return  ServerResponse.createBySuccess(pageInfo);
    }
    private ProductListVo assembleProductListVo(Product product)
    {
        ProductListVo productListVo=new ProductListVo();
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setId(product.getId());
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
        productListVo.setMainImage(productListVo.getMainImage());
        productListVo.setName(product.getName());
        productListVo.setPrice(product.getPrice());
        productListVo.setStatus(product.getStatus());
        productListVo.setSubtitle(product.getSubtitle());
        return  productListVo;
    }

    public ServerResponse<PageInfo> productSearch(int pageNum,
                                                  int pageSize,
                                                  String productName,
                                                  Integer productId)
    {
        PageHelper.startPage(pageNum,pageSize);
        if(StringUtils.isNotBlank(productName))
        {
            productName=new StringBuilder().append("%").append(productName).append("%").toString();
        }
        List<Product> productList=productMapper.searchProduct(productId,productName);
        List<ProductListVo> productListVoArrayList= Lists.newArrayList();
        for (Product product:productList)
        {
            ProductListVo productListVo=assembleProductListVo(product);
            productListVoArrayList.add(productListVo);
        }
        PageInfo pageInfo=new PageInfo(productList);
        pageInfo.setList(productListVoArrayList);
        return  ServerResponse.createBySuccess(pageInfo);
    }

    /*
    *
    *前台的商品Service
    *
    * */

    public ServerResponse<ProductDetial> getDetails(Integer productId)
    {
        if(productId==null)
        {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product=productMapper.selectByPrimaryKey((productId));
        if(product==null)
        {
            return ServerResponse.createByErrorMessage("产品已下架或者删除");
        }
        if(product.getStatus()!= Const.ProductStatusEnum.ON_SALE.getCode())
        {
            return ServerResponse.createByErrorMessage("商品已经下架或删除");
        }
        ProductDetial pro=assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(pro);
    }


    @Override
    public ServerResponse<PageInfo> getProductByKeywordCategory(String keyWord, Integer categoryId, int pageNum, int pageSize, String orderBy)
    {
        if(StringUtils.isBlank(keyWord) && categoryId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        List<Integer> categoryIdList = new ArrayList<>();

        if(categoryId != null)
        {
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if(category == null && StringUtils.isBlank(keyWord)){
                //没有该分类,并且还没有关键字,这个时候返回一个空的结果集,不报错
                PageHelper.startPage(pageNum,pageSize);
                List<ProductListVo> productListVoList = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productListVoList);
                return ServerResponse.createBySuccess(pageInfo);
            }
            categoryIdList = iCategoryService.selectCategoryAndChildrenById(category.getId()).getData();
        }
        if(StringUtils.isNotBlank(keyWord)){
            keyWord = new StringBuilder().append("%").append(keyWord).append("%").toString();
        }

        PageHelper.startPage(pageNum,pageSize);
        //排序处理
        if(StringUtils.isNotBlank(orderBy)){
            if(Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
                String[] orderByArray = orderBy.split("_");
                PageHelper.orderBy(orderByArray[0]+" "+orderByArray[1]);
            }
        }
        List<Product> productList = productMapper.selectByNameAndCategoryIds(StringUtils.isBlank(keyWord)?null:keyWord,categoryIdList.size()==0?null:categoryIdList);

        List<ProductListVo> productListVoList = Lists.newArrayList();
        for(Product product : productList){
            ProductListVo productListVo = assembleProductListVo(product);
            productListVoList.add(productListVo);
        }

        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }
}
