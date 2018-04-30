package com.ye.goods.vo.cart;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartProductVO {
    private Integer id;

    private Integer userId;

    private Integer productId;

    private String productName;

    private String productSubtitle;

    private String productMainImage;

    private Integer quantity;

    private Integer stock;

    private BigDecimal productPrice;

    private Integer productStatus;

    private BigDecimal productTotalPrice;

    private Integer productChecked;
}
