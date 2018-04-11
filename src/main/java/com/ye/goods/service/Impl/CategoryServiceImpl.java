package com.ye.goods.service.Impl;

import com.ye.goods.dao.CategoryMapper;
import com.ye.goods.pojo.Category;
import com.ye.goods.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CategoryServiceImpl implements ICategoryService {

    private CategoryMapper categoryMapper;

    @Autowired
    public CategoryServiceImpl(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @Override
    public Set<Category> getChildCategory(Integer categoryId) {
        List<Category> categoryList = categoryMapper.getProductParentId(categoryId);
        Set<Category> categorySet = new HashSet<>();
        for (Category category: categoryList) {
            categorySet = getDeepenChildCategory(categorySet, category.getId());
        }

        return categorySet;
    }

    private Set<Category> getDeepenChildCategory(Set<Category> categorySet, Integer categoryId) {
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if (category != null) {
            categorySet.add(category);
            List<Category> categoryList1 = categoryMapper.getProductParentId(category.getId());
            for (Category category1: categoryList1) {
                getDeepenChildCategory(categorySet, category1.getId());
            }
        }
        return categorySet;
    }
}
