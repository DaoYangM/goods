package com.ye.goods.vo.cart;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CartVO {
    private List<CartProductVO> productVOList;
    private BigDecimal cartTotalPrice;
    private String imageHost;
}
