package com.ye.goods.dao;

import com.ye.goods.pojo.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    List<Product> getProductByKeywordCategory(@Param("productName") String productName,
                                              @Param("categoryIdList") List<Integer> categoryIdList);

    List<Product> selectByProductNameAndCategoryId(@Param("productName") String productName,
                                                   @Param("categoryId") Integer categoryId,
                                                   @Param("productId") Integer productId);

    List<Product> all();
}