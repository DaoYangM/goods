package com.ye.goods.common.enums;

public enum CartCheckedEnum {

    CHECKED(1),
    UNCHECKED(2);

    private Integer code;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    CartCheckedEnum(Integer code) {
        this.code = code;
    }
}
