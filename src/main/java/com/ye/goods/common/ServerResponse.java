package com.ye.goods.common;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServerResponse<T> {
    private Integer code;
    private String desc;
    private T target;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public T getTarget() {
        return target;
    }

    public void setTarget(T target) {
        this.target = target;
    }

    public boolean isSuccess() {
        return this.code.equals(ResponseCode.SUCCESS.getCode());
    }

    private ServerResponse(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private ServerResponse(int code, String desc, T target) {
        this(code, desc);
        this.target = target;
    }

    public static <T> ServerResponse<T> SUCCESS(T target) {
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getDesc(), target);
    }

    public static <T> ServerResponse<T> ERROR(String desc) {
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(), desc);
    }

    public static <T> ServerResponse<T> ERROR_NEED_LOGIN() {
        return new ServerResponse<T>(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
    }

    public static <T> ServerResponse<T> ERROR_NEED_ADMIN() {
        return new ServerResponse<T>(ResponseCode.NEED_ADMIN.getCode(), ResponseCode.NEED_ADMIN.getDesc());
    }

    public static <T> ServerResponse<T> ERROR_ILLEGAL_ARGUMENT() {
        return new ServerResponse<T>(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
    }

    public static <T> ServerResponse<T> ERROR_ILLEGAL_ARGUMENT(String args) {
        return new ServerResponse<T>(ResponseCode.ILLEGAL_ARGUMENT.getCode(), args);
    }
}
