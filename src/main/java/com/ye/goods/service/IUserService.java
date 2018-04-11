package com.ye.goods.service;

import com.ye.goods.common.ServerResponse;
import com.ye.goods.pojo.User;

import javax.servlet.http.HttpSession;

public interface IUserService {
    ServerResponse login(String username, String password, HttpSession session);

    boolean checkUsernameAndPassword(String username, String password);

    ServerResponse info(String username);

    ServerResponse register(User user);

    ServerResponse update(User user);
}
