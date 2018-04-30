package com.ye.goods.common;

public interface AlipayCallbackStatus {
    String WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
    String TRADE_CLOSED = "TRADE_CLOSED";
    String TRADE_SUCCESS = "TRADE_SUCCESS";
    String TRADE_FINISHED = "TRADE_FINISHED";

    String RESPONSE_SUCCESS = "success";
    String RESPONSE_FAILED = "failed";
}
