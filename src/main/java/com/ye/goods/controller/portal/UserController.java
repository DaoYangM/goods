package com.ye.goods.controller.portal;

import com.ye.goods.anno.NeedLogin;
import com.ye.goods.anno.ValidateFields;
import com.ye.goods.aop.NeedLoginAop;
import com.ye.goods.common.ServerResponse;
import com.ye.goods.dao.UserMapper;
import com.ye.goods.pojo.User;
import com.ye.goods.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/users/")
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

    @GetMapping("/login")
    public ServerResponse login() {
        return ServerResponse.ERROR_NEED_LOGIN();
    }

    @GetMapping("/me")
    public ServerResponse info(Authentication authentication) {
         String loginUsername = ((org.springframework.security.core.userdetails.User)authentication.getPrincipal())
                 .getUsername();

            return ServerResponse.SUCCESS(userService.info(loginUsername));
    }

    @PostMapping("/register")
    public ServerResponse register(@Validated User user) {
        return userService.register(user);
    }

    @PutMapping("/update")
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

