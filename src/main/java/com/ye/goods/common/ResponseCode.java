package com.ye.goods.common;

public enum ResponseCode {
    SUCCESS(0, "SUCCESS"),
    ERROR(5, "ERROR"),
    NEED_LOGIN(10, "NEED_LOGIN"),
    NEED_ADMIN(20, "NEED_ADMIN"),
    ILLEGAL_ARGUMENT(2, "ILLEGAL_ARGUMENT");

    private int code;
    private String desc;

    ResponseCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
