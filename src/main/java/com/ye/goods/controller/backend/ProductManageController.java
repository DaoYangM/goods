package com.ye.goods.controller.backend;

import com.ye.goods.common.ServerResponse;
import com.ye.goods.pojo.Product;
import com.ye.goods.service.IFileService;
import com.ye.goods.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/manage/products")
public class ProductManageController {

    private IProductService productService;
    private IFileService  fileService;

    @Autowired
    public ProductManageController(IProductService productService, IFileService fileService) {
        this.productService = productService;
        this.fileService = fileService;
    }

    @PostMapping("/save")
    public ServerResponse save(@Validated Product product) {
        if (StringUtils.isNotBlank(product.getSubImages())) {
            String[] images = product.getSubImages().split(",");
            product.setMainImage(images[0]);
        }

        return productService.saveOrUpdate(product);
    }

    @GetMapping("/{productId}")
    public ServerResponse getDetail(@PathVariable Integer productId) {
        if (productId == null)
            return ServerResponse.ERROR_ILLEGAL_ARGUMENT();
        return productService.getDetail(productId);
    }

    @GetMapping
    public ServerResponse search(@RequestParam(defaultValue = "") String productName,
                                 @RequestParam(defaultValue = "") Integer categoryId,
                                 @RequestParam(defaultValue = "") Integer productId,
                                 @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                 @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return productService.search(productName, categoryId, productId, pageNum, pageSize);
    }

    @PutMapping("/update/status/{productId}")
    public ServerResponse updateStatus(@PathVariable Integer productId, Integer status) {
        if (status == null)
            return ServerResponse.ERROR_ILLEGAL_ARGUMENT();
        return productService.updateStatus(productId, status);
    }

    @PostMapping("/upload")
    public ServerResponse upload(HttpServletRequest request,
                                 @RequestParam(value = "upload_file", required = false)
                                         MultipartFile[] multipartFileList) throws IOException {
        String path = request.getSession().getServletContext().getRealPath("upload");

        List<String> targetFileName = fileService.upload(multipartFileList, path);


        return ServerResponse.SUCCESS(targetFileName);
    }

    @GetMapping("/upload")
    public ModelAndView uploadGet() {
        return new ModelAndView("default-upload");
    }
}
