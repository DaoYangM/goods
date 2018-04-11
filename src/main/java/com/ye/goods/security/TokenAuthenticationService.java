package com.ye.goods.security;

import com.ye.goods.common.Const;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.json.JSONException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class TokenAuthenticationService {

    static void addAuthentication(HttpServletResponse response, String username) throws IOException, JSONException {
        String JWT = Jwts.builder()
                .claim("authorities", "ROLE_ADMIN,AUTH_WRITE")
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + Const.EXPIRATIONTIME))
                .signWith(SignatureAlgorithm.HS512, Const.SECRET)
                .compact();

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getOutputStream().println(JSONResult.fillResultString(0, "", JWT));
    }

    static Authentication getAuthentication(HttpServletRequest request) {
        // 从Header中拿到token
        String token = request.getHeader(Const.HEADER_STRING);

        if (token != null) {
            // 解析 Token
            Claims claims = Jwts.parser()
                    // 验签
                    .setSigningKey(Const.SECRET)
                    // 去掉 Bearer
                    .parseClaimsJws(token.replace(Const.TOKEN_PREFIX, ""))
                    .getBody();

            // 拿用户名
            String user = claims.getSubject();

            // 得到 权限（角色）
            List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList((String) claims.get("authorities"));

            // 返回验证令牌
            return user != null ?
                    new UsernamePasswordAuthenticationToken(user, null, authorities) :
                    null;
        }
        return null;
    }
}
