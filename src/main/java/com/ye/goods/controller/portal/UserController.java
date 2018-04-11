package com.ye.goods.controller.portal;

import com.ye.goods.anno.NeedLogin;
import com.ye.goods.anno.ValidateFields;
import com.ye.goods.aop.NeedLoginAop;
import com.ye.goods.common.ServerResponse;
import com.ye.goods.dao.UserMapper;
import com.ye.goods.pojo.User;
import com.ye.goods.service.IUserService;
import com.ye.goods.utils.MD5Util;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static com.ye.goods.common.Const.HEADER_STRING;
import static com.ye.goods.common.Const.SECRET;
import static com.ye.goods.common.Const.TOKEN_PREFIX;

@RestController
@RequestMapping("/user/")
public class UserController {

    private IUserService userService;
    private NeedLoginAop needLoginAop;
    private UserMapper userMapper;

    @Autowired
    public UserController(IUserService userService, NeedLoginAop needLoginAop, UserMapper userMapper) {
        this.userService = userService;
        this.needLoginAop = needLoginAop;
        this.userMapper = userMapper;
    }

    @PostMapping("/login/")
    @ValidateFields
    public ServerResponse login(@Validated User user, BindingResult result, HttpSession session) {
//        return userService.login(user.getUsername(), user.getPassword(), session);
        return null;
    }

    @GetMapping("/info/")
    @NeedLogin
    public ServerResponse info(HttpServletRequest request) {
            String username = needLoginAop.getUsername();
            return userService.info(username);
    }

    @PostMapping("/register/")
    @ValidateFields
    public ServerResponse register(@Validated User user, BindingResult result) {
        return userService.register(user);
    }

    @PutMapping("/update/")
    @NeedLogin
    @ValidateFields
    public ServerResponse update(@Validated User user, HttpServletRequest request) {
        String username = needLoginAop.getUsername();
        User userOrigin = userMapper.selectByUsername(username);
        user.setUsername(username);
        user.setId(userOrigin.getId());
        user.setRole(userOrigin.getRole());

        return userService.update(user);
    }
}

