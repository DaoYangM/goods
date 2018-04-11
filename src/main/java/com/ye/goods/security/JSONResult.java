package com.ye.goods.security;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONResult {
    public static String fillResultString(Integer status, String message, Object result) throws JSONException {
        JSONObject jsonObject = new JSONObject() {{
            put("status", status);
            put("message", message);
            put("result", result);
        }};

        return jsonObject.toString();
    }
}
