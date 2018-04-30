package com.ye.goods.service;

import com.ye.goods.common.ServerResponse;

import java.util.Map;

public interface IOrderService {
    ServerResponse create(Integer user, Integer shippingId);

    ServerResponse pay(Long orderNo, Integer userId, String path);

    ServerResponse alipayCallback(Map<String, String> params);

    ServerResponse queryOrderPayStatus(Integer userId, Long orderNo);

    ServerResponse cancel(Integer userId, Long orderNo);

    ServerResponse productChecked(Integer userId);

    ServerResponse detail(Integer userId, Long orderNo);

    ServerResponse all(Integer userId, Integer pageNum, Integer pageSize);

    ServerResponse manageAll(Integer pageNum, Integer pageSize);

    ServerResponse manageSearch(String productName, Integer pageNum, Integer pageSize);
}
