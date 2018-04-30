package com.ye.goods.controller.portal;

import com.ye.goods.common.ServerResponse;
import com.ye.goods.dao.UserMapper;
import com.ye.goods.pojo.User;
import com.ye.goods.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

@RestController
@RequestMapping("/users/")
public class UserController {

    private IUserService userService;
    private UserMapper userMapper;

    @Autowired
    public UserController(IUserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping("/login")
    public ServerResponse login() {
        return ServerResponse.ERROR_NEED_LOGIN();
    }
//
//    @PutMapping("/login")
//    public ServerResponse login2() {
//        return ServerResponse.ERROR_NEED_LOGIN();
//    }

    @GetMapping("/logout")
    public ServerResponse logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);

            return ServerResponse.SUCCESS("退出登录成功");
        }
        return ServerResponse.ERROR("退出登录失败");
    }

    @GetMapping("/me")
    public ServerResponse info(Principal principal) {
        String loginUsername = principal.getName();

            return ServerResponse.SUCCESS(userService.info(loginUsername));
    }

    @PostMapping("/check/username")
    public ServerResponse checkUsername(String username) {
        if (StringUtils.isBlank(username))
            return ServerResponse.ERROR("用户名不能为空");
        return userService.checkUsername(username);
    }

    @PostMapping("/register")
    public ServerResponse registerUser(@Validated User user) {
        return userService.register(user);
    }

    @PutMapping("/update/detail")
    public ServerResponse updateUser(Principal principal, @Validated User user) {
        String username = principal.getName();
        User userOrigin = userMapper.selectByUsername(username);
        user.setUsername(username);
        user.setId(userOrigin.getId());
        user.setRole(userOrigin.getRole());
        user.setPassword(null);

        return userService.update(user);
    }

    @PutMapping("/password")
    public ServerResponse updatePassword(Principal principal, String oldPassword,
                                          String newPassword) {
        String username = principal.getName();

        if (newPassword.length() < 8)
            return ServerResponse.ERROR("密码长度小于8位");

        if (oldPassword.equals(newPassword))
            return ServerResponse.ERROR("旧密码与新密码相同");

        return userService.updatePassword(username, oldPassword, newPassword);
    }
}

