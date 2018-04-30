package com.ye.goods.vo.order;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemVO {

    private Long orderNo;

    private Integer productId;

    private String productName;

    private String productImage;

    private BigDecimal currentUnitPrice;

    private Integer quantity;

    private BigDecimal totalPrice;

    private String createTime;
}
