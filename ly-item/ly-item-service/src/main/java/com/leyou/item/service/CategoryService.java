package com.leyou.item.service;

import com.leyou.common.enums.ExceptionEnums;
import com.leyou.common.exception.LyException;
import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;
    public List<Category> queryCatgoryListByPid(Long pid){
        Category category = new Category();
        category.setParentId(pid);
        List<Category> categoryList = categoryMapper.select(category);
        if(CollectionUtils.isEmpty(categoryList)){
            throw new LyException(ExceptionEnums.CATEGORY_NOT_FOND);
        }
        return categoryList;
    }

    public List<Category> queryByBrandId(Long bid) {
        return this.categoryMapper.queryByBrandId(bid);
    }

    public List<Category> queryAllCategoryLevelByCid3(Long id) {
        return null;
    }

    public List<Category> queryCategoryByIds(List<Long> ids) {
        List<Category> categoryList = categoryMapper.selectByIdList(ids);
        if(CollectionUtils.isEmpty(categoryList)){
            throw new LyException(ExceptionEnums.CATEGORY_NOT_FOND);
        }
        return categoryList;
    }

    public List<String> queryNameByIds(List<Long> ids) {
        return null;
    }

    public void deleteCategory(Long id) {


    }

    public void updateCategory(Category category) {

    }

    public void saveCategory(Category category) {

    }
}
