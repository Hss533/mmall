package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

@Service("iCategoryService")
public class ICategoryServiceImpl implements ICategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    private Logger logger=LoggerFactory.getLogger(ICategoryServiceImpl.class);

    public ServerResponse<String> addCategory(String  categoryName,Integer parent_id){

        if (StringUtils.isNotBlank(categoryName)&&parent_id!=null)
        {
            Category category=new Category(
            );

            category.setName(categoryName);
            category.setParentId(parent_id);
            category.setStatus(true);
            if (categoryMapper.insert(category) > 0) {
                return ServerResponse.createBySuccessMessage("添加成功");
            } else
                return ServerResponse.createByErrorMessage("添加失败");
        }
        else {
            return ServerResponse.createByErrorMessage("参数错误");
        }
    }

    @Override
    public ServerResponse<String> updateCategoryName(Integer categoryId, String categoryName) {
        Category category=new Category();
        category.setName(categoryName);
        category.setId(categoryId);
        //有选择的更新，有什么属性更新什么属性
        int resultCount=categoryMapper.updateByPrimaryKeySelective(category);
        if(resultCount>0)
            return ServerResponse.createBySuccessMessage("更新成功");
        else
        return ServerResponse.createByErrorMessage("更新不成功");
    }

    //根据id创建子分类
    public ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId)
    {
        List<Category> categories=categoryMapper.getChildrenParallelbyCategoryName(categoryId);
        //不仅可以判空，还能判断集合中是不是为空集
        if (CollectionUtils.isEmpty(categories))
        {
            logger.info("未找到当前分类的子分类");
        }
        return ServerResponse.createBySuccess(categories);
    }

    //递归查询本节点的id和孩子结点的id
    @Override
    public ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId) {
        Set<Category> categorySet= Sets.newHashSet();
        findChildCategory(categorySet,categoryId);
        List<Integer> categoryIdList= Lists.newArrayList();
        if(categoryId!=null)
        {
            for(Category categoryItem:categorySet)
            {
                categoryIdList.add(categoryItem.getId());
            }
        }
        return ServerResponse.createBySuccess(categoryIdList);
    }

    //递归算法，计算子节点
    private Set<Category> findChildCategory(Set<Category> categorySet,Integer categoryId)
    {
        Category category=categoryMapper.selectByPrimaryKey(categoryId);
        if(category!=null)
        {
            categorySet.add(category);
        }
        //查找子节点
        List<Category> categoryList=categoryMapper.getChildrenParallelbyCategoryName(categoryId);
        //mybatis不会返回null
        for(Category categoryItem:categoryList)
        {
            findChildCategory(categorySet,categoryItem.getId());
        }
        return categorySet;
    }
}
