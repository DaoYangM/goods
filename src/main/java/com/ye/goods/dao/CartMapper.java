package com.ye.goods.dao;

import com.ye.goods.pojo.Cart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    List<Cart> selectCartByUserId(Integer userId);

    Cart selectCartByUserIdAndProductId(@Param("userId") Integer userId,
                                        @Param("productId") Integer productId);

    int deleteByUserIdProductIds(@Param("userId") Integer userId
            , @Param("productIdList") List<String> productIdList);

    int selectAllOrUnSelectAll(@Param("userId") Integer userId, @Param("checked") Integer checked,
                               @Param("productId") Integer productId);

    int getCartProductCount(Integer userId);
}