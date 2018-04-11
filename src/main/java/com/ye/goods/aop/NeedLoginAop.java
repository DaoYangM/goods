package com.ye.goods.aop;

import com.ye.goods.common.Const;
import com.ye.goods.common.ServerResponse;
import com.ye.goods.pojo.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

import static com.ye.goods.common.Const.HEADER_STRING;
import static com.ye.goods.common.Const.SECRET;
import static com.ye.goods.common.Const.TOKEN_PREFIX;

@Aspect
@Component
public class NeedLoginAop {

    private static Logger logger = LoggerFactory.getLogger(NeedLoginAop.class);

    private String username;

    @Pointcut("@annotation(com.ye.goods.anno.NeedLogin)")
    private void needLogin() {
    }

    public String getUsername() {
        return this.username;
    }

    @Before("needLogin()")
    private ServerResponse needLoginAdvice(JoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();

        for (Object object : args) {
            if (object instanceof HttpServletRequest) {
                HttpServletRequest request = (HttpServletRequest) object;
                String token = request.getHeader(HEADER_STRING);

                if (token != null) {
                    // 解析 Token
                    Claims claims = Jwts.parser()
                            // 验签
                            .setSigningKey(SECRET)
                            // 去掉 Bearer
                            .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                            .getBody();

                    // 拿用户名
                    String user = claims.getSubject();
                    if (user != null)
                        this.username = user;
                }
            }
        }
        return null;
    }
}
