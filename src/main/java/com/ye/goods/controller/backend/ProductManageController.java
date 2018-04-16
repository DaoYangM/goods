package com.ye.goods.controller.backend;

import com.ye.goods.anno.NeedLogin;
import com.ye.goods.anno.ValidateFields;
import com.ye.goods.common.ServerResponse;
import com.ye.goods.pojo.Product;
import com.ye.goods.service.IFileService;
import com.ye.goods.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("/manage/products/")
public class ProductManageController {

    private IProductService productService;
    private IFileService  fileService;

    @Autowired
    public ProductManageController(IProductService productService, IFileService fileService) {
        this.productService = productService;
        this.fileService = fileService;
    }

    @GetMapping
    @NeedLogin
    public ServerResponse all(HttpServletRequest request,
                              @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                              @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                              @RequestParam(value = "orderBy", defaultValue = "") String orderBy) {
        return productService.all(pageNum, pageSize);
    }

    @PostMapping("/save")
    @NeedLogin
    @ValidateFields
    public ServerResponse save(HttpServletRequest request,
                               @Validated Product product, BindingResult result) {

        return productService.saveOrUpdate(product);
    }

    @PostMapping("/upload")
    @NeedLogin
    public ServerResponse upload(HttpServletRequest request,
                                 @RequestParam(value = "upload_file", required = false) MultipartFile multipartFile) throws IOException {
        String path = request.getSession().getServletContext().getRealPath("upload");
        String targetFileName = fileService.upload(multipartFile, path);

        return ServerResponse.SUCCESS(targetFileName);
    }

    @RequestMapping(value = "/upload", method = RequestMethod.GET)
    public ModelAndView uploadGet() {
        ModelAndView modelAndView = new ModelAndView("default-upload");
        return modelAndView;
    }
}
