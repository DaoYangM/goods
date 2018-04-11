package com.ye.goods.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers(HttpMethod.POST, "/user/login/").permitAll()
                .antMatchers(HttpMethod.POST, "/user/register/").permitAll()
                .antMatchers(HttpMethod.GET, "/product/**/").permitAll()
                .antMatchers(HttpMethod.GET, "/user/info/").hasAuthority("AUTH_WRITE")
                .antMatchers(HttpMethod.GET, "/user/info/").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/manage/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET, "/manage/products/upload/").permitAll()
                .antMatchers(HttpMethod.POST, "/manage/products/upload/").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(new JWTLoginFilter("/user/login/", authenticationManager()),
                        UsernamePasswordAuthenticationFilter.class)
                // 添加一个过滤器验证其他请求的Token是否合法z
                .addFilterBefore(new JWTAuthenticationFilter(),
                        UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(new CustomAuthenticationProvider());
    }
}
