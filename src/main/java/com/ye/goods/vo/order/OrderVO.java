package com.ye.goods.vo.order;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderVO {
    private Long orderNo;

    private BigDecimal payment;

    private Integer paymentType;

    private String paymentTypeDesc;

    private Integer postage;

    private Integer status;

    // no
    private String statusDesc;

    private String paymentTime;

    private Long sendTime;

    private Long endTime;

    private Long closeTime;

    private Long createTime;

    //订单的明细
    private List<OrderItemVO> orderItemVOList;

    private String imageHost;

    private Integer shippingId;

    private String receiverName;

    private ShippingVO shippingVO;
}
