package com.ye.goods.common;

import java.util.HashSet;
import java.util.Set;

public interface Const {
    String CURRENT_USER = "currentUser";
    long EXPIRATIONTIME = 432_000_000;     // 5天
    String SECRET = "P@ssw02d";            // JWT密码
    String TOKEN_PREFIX = "Bearer";        // Token前缀
    String HEADER_STRING = "Authorization";
    String FTP_ADDRESS = "ftp://35.185.173.15/";

    interface  ProductOrderBy {
        Set<String> PRICE_ASC_DESC = new HashSet<String>() {{
            add("price_asc");
            add("price_desc");
        }};
    }
}
