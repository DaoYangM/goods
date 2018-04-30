package com.ye.goods.controller.portal;

import com.ye.goods.common.ServerResponse;
import com.ye.goods.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {

    private IProductService productService;

    @Autowired
    public ProductController(IProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{productId}")
    public ServerResponse detail(@PathVariable Integer productId) {
        return productId != null? productService.getDetail(productId): ServerResponse.ERROR_ILLEGAL_ARGUMENT();
    }

    @GetMapping
    public ServerResponse keyWordAndCategory(@RequestParam(value = "keyword", defaultValue = "") String keyword,
                                             @RequestParam(value = "categoryId", defaultValue = "") Integer categoryId,
                                             @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                             @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                             @RequestParam(value = "orderBy", defaultValue = "") String orderBy) {
        return productService.getProductByKeywordCategory(keyword, categoryId, pageNum, pageSize, orderBy);
    }
}
