package com.ye.goods.service;

import com.ye.goods.common.ServerResponse;
import com.ye.goods.pojo.Shipping;

public interface IShippingService {

    ServerResponse save(String username, Shipping shipping);

    ServerResponse all(String username);

    ServerResponse delete(String username, Integer shippingId);
}
