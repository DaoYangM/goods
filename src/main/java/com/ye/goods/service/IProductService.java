package com.ye.goods.service;

import com.github.pagehelper.PageInfo;
import com.ye.goods.common.ServerResponse;
import com.ye.goods.pojo.Category;
import com.ye.goods.pojo.Product;

import java.util.Set;

public interface IProductService {

    ServerResponse getDetail(Integer id);

    ServerResponse<PageInfo> getProductByKeywordCategory(
            String keyword, Integer categoryId, Integer pageNum, Integer pageSize,String orderBy
    );

    ServerResponse saveOrUpdate(Product product);

    ServerResponse<PageInfo> all(Integer pageNum, Integer pageSize);
}
