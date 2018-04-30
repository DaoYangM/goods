package com.ye.goods.common.enums;

import lombok.Data;

public enum  OrderStatusEnum {
    CANCELED(0, "已取消"),
    NO_PAY(10, "未支付"),
    PAID(20, "已付款"),
    ORDER_SUCCESS(50, "订单完成"),
    ORDER_CLOSE(60, "订单关闭");

    private String value;

    private Integer code;

    OrderStatusEnum(Integer code, String value) {
        this.value = value;
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public Integer getCode() {
        return code;
    }
}
