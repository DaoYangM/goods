package com.ye.goods.service.Impl;

import com.ye.goods.common.Const;
import com.ye.goods.common.ServerResponse;
import com.ye.goods.dao.UserMapper;
import com.ye.goods.pojo.User;
import com.ye.goods.service.IUserService;
import com.ye.goods.utils.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@Service
public class UserServiceImpl implements IUserService {

    private static UserMapper userMapper;

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public ServerResponse login(String username, String password, HttpSession session) {
        User user = userMapper.login(username, MD5Util.MD5EncodeUtf8(password));
        if (user != null) {
            user.setPassword("");
            session.setAttribute(Const.CURRENT_USER, user);
            return ServerResponse.SUCCESS(user);
        } else
            return ServerResponse.ERROR("Login failed");
    }

    @Override
    public boolean checkUsernameAndPassword(String username, String password) {
        User user = userMapper.login(username, MD5Util.MD5EncodeUtf8(password));
        if (user != null) {
            user.setPassword("");
            return true;
        } else
            return false;
    }

    @Override
    public ServerResponse info(String username) {
        if (username != null) {
            User user = userMapper.selectByUsername(username);
            if (user != null)
                return ServerResponse.SUCCESS(user);
        }
        return ServerResponse.ERROR_NEED_LOGIN();
    }

    @Override
    public ServerResponse register(User user) {
        if (userMapper.checkUsername(user.getUsername()) > 0 ||
                userMapper.checkEmail(user.getEmail()) > 0)
            return ServerResponse.ERROR("User has been register");
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        user.setRole(1);
        int result = userMapper.insert(user);
        return result == 1? ServerResponse.SUCCESS(true): ServerResponse.ERROR("Register failed");
    }

    @Override
    public ServerResponse update(User user) {
        return userMapper.updateByPrimaryKeySelective(user) == 1?
                ServerResponse.SUCCESS(true): ServerResponse.ERROR("Register failed");
    }
}
