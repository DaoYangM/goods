package com.ye.goods.service;

import com.github.pagehelper.PageInfo;
import com.ye.goods.common.ServerResponse;
import com.ye.goods.pojo.Product;

public interface IProductService {

    ServerResponse getDetail(Integer id);

    ServerResponse<PageInfo<Product>> getProductByKeywordCategory(
            String keyword, Integer categoryId, Integer pageNum, Integer pageSize,String orderBy
    );

    ServerResponse saveOrUpdate(Product product);

    ServerResponse<PageInfo<Product>> all(Integer pageNum, Integer pageSize);

    ServerResponse updateStatus(Integer productId, Integer status);

    ServerResponse search(String productName, Integer categoryId, Integer productId, Integer pageNum, Integer pageSize);
}
