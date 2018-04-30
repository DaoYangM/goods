package com.ye.goods.security;

import com.ye.goods.common.exception.UsernamePasswordException;
import com.ye.goods.dao.UserMapper;
import com.ye.goods.pojo.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

        boolean accountNotLocked = true;

        User user =  userMapper.selectByUsername(s);

        if (user == null){
            throw new UsernamePasswordException("用户名或密码不正确");
        }

        Set<GrantedAuthority> authorities = new HashSet<>();

        if (user.getRole().equals(0))
            accountNotLocked = false;

        if (user.getRole().equals(1))
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

        return new org.springframework.security.core.userdetails.User(s, user.getPassword(), true,
                true, true, accountNotLocked, authorities);
    }
}
