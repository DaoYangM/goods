package com.ye.goods.vo.order;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderProductVO {
    private List<OrderItemVO> orderItemVoList;

    private BigDecimal productTotalPrice;

    private String imageHost;
}
