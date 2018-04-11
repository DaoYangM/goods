package com.ye.goods.service;

import com.ye.goods.pojo.Category;

import java.util.Set;

public interface ICategoryService {
    Set<Category> getChildCategory(Integer categoryId);

}
