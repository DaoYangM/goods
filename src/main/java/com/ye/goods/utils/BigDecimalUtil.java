package com.ye.goods.utils;

import java.math.BigDecimal;

public class BigDecimalUtil {

    private BigDecimalUtil() {}

    public static BigDecimal add(double v1, double v2) {
        BigDecimal bigDecimal = new BigDecimal(Double.toString(v1));
        BigDecimal bigDecimal2 = new BigDecimal(Double.toString(v2));

        return bigDecimal.add(bigDecimal2);
    }

    public static BigDecimal mul(double v1, double v2) {
        BigDecimal bigDecimal = new BigDecimal(Double.toString(v1));
        BigDecimal bigDecimal2 = new BigDecimal(Double.toString(v2));

        return bigDecimal.multiply(bigDecimal2);
    }

    public static void main(String[] args) {
        System.out.println(mul(10.1, 2));
    }
}
