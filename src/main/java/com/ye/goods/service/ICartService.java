package com.ye.goods.service;

import com.ye.goods.common.ServerResponse;

public interface ICartService {
    ServerResponse add(String username, Integer productId, Integer count);

    ServerResponse all(String username);

    ServerResponse updateCount(String username, Integer productId, Integer count);

    ServerResponse delete(String username, String productIds);

    ServerResponse selectAllOrUnSelectAll(String username, Integer code);

    ServerResponse selectOneOrUnSelectOne(String username, Integer code, Integer productId);
}
