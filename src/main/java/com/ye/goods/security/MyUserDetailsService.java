package com.ye.goods.security;

import com.ye.goods.dao.UserMapper;
import com.ye.goods.pojo.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

        boolean accountNotLocked = true;

        User user =  userMapper.selectByUsername(s);

        if (user.getRole().equals(0))
            accountNotLocked = false;

        return new org.springframework.security.core.userdetails.User(s, user.getPassword(), true,
                true, true, accountNotLocked, AuthorityUtils.commaSeparatedStringToAuthorityList("ADMIN"));
    }
}
