package com.ye.goods.dao;

import com.ye.goods.common.ServerResponse;
import com.ye.goods.pojo.Shipping;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShippingMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Shipping record);

    int insertSelective(Shipping record);

    Shipping selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Shipping record);

    int updateByPrimaryKey(Shipping record);

    int deleteByUserIdAndShippingId(Integer id, Integer shippingId);

    Shipping selectByUserIdAndShippingId(@Param("userId") Integer userId,
                                       @Param("shippingId") Integer shippingId);
    List<Shipping> all(Integer userId);
}