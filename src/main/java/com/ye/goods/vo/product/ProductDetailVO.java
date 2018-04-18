package com.ye.goods.vo.product;

import com.ye.goods.pojo.Product;

public class ProductDetailVO extends Product {

    private String imageHost;

    private Integer parentCategoryId;

    public String getImageHost() {
        return imageHost;
    }

    public void setImageHost(String imageHost) {
        this.imageHost = imageHost;
    }

    public Integer getParentCategoryId() {
        return parentCategoryId;
    }

    public void setParentCategoryId(Integer parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }
}
