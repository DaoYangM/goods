package com.ye.goods.common.enums;

public enum  ProductStatusEnum {

    ON_SALE(1, "在售"),
    NOT_NO_SALE(2, "下架");

    private Integer code;
    private String msg;

    ProductStatusEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
