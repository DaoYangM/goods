package com.ye.goods.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ye.goods.common.ServerResponse;
import com.ye.goods.dao.ShippingMapper;
import com.ye.goods.dao.UserMapper;
import com.ye.goods.pojo.Shipping;
import com.ye.goods.pojo.User;
import com.ye.goods.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShippingService implements IShippingService {

    private UserMapper userMapper;
    private ShippingMapper shippingMapper;

    @Autowired
    public ShippingService(UserMapper userMapper, ShippingMapper shippingMapper) {
        this.userMapper = userMapper;
        this.shippingMapper = shippingMapper;
    }

    @Override
    public ServerResponse save(String username, Shipping shipping) {
        User user = userMapper.selectByUsername(username);
        Shipping shippingExist = shippingMapper.selectByPrimaryKey(shipping.getId());
        shipping.setUserId(user.getId());
        if (shippingExist != null) {
            return shippingMapper.updateByPrimaryKeySelective(shipping) == 1? ServerResponse.SUCCESS(true):
                    ServerResponse.ERROR("Shipping update fail");
        }
        return shippingMapper.insert(shipping) == 1? ServerResponse.SUCCESS(true):
                ServerResponse.ERROR("Shipping insert fail");
    }

    @Override
    public ServerResponse all(String username) {
        User user = userMapper.selectByUsername(username);
        PageHelper.startPage(1, 10);
        PageInfo pageInfo = new PageInfo(shippingMapper.all(user.getId()));

        return ServerResponse.SUCCESS(pageInfo);
    }

    @Override
    public ServerResponse delete(String username, Integer shippingId) {
        User user = userMapper.selectByUsername(username);
        return shippingMapper.deleteByUserIdAndShippingId(user.getId(), shippingId) == 1? ServerResponse.SUCCESS(true):
                ServerResponse.ERROR("Shipping delete fail");
    }
}
