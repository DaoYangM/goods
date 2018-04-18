package com.ye.goods.controller.portal;

import com.ye.goods.anno.NeedLogin;
import com.ye.goods.anno.ValidateFields;
import com.ye.goods.aop.NeedLoginAop;
import com.ye.goods.common.ServerResponse;
import com.ye.goods.pojo.Shipping;
import com.ye.goods.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/shipping/")
public class ShippingController {

    private NeedLoginAop needLoginAop;
    private IShippingService shippingService;

    @Autowired
    public ShippingController(NeedLoginAop needLoginAop, IShippingService shippingService) {
        this.needLoginAop = needLoginAop;
        this.shippingService = shippingService;
    }

    @PostMapping("/save/")
    @NeedLogin
    @ValidateFields
    public ServerResponse save(HttpServletRequest request, @Validated Shipping shipping) {
        String username = needLoginAop.getUsername();
        return shippingService.save(username, shipping);
    }

    @GetMapping
    @NeedLogin
    @ValidateFields
    public ServerResponse all(HttpServletRequest request) {
        String username = needLoginAop.getUsername();
        return shippingService.all(username);
    }

    @DeleteMapping("/{shippingId}/")
    @NeedLogin
    @ValidateFields
    public ServerResponse delete(HttpServletRequest request, @PathVariable Integer shippingId) {
        String username = needLoginAop.getUsername();
        return shippingService.delete(username, shippingId);
    }
}
