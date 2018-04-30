package com.ye.goods.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.ye.goods.common.AlipayCallbackStatus;
import com.ye.goods.common.ServerResponse;
import com.ye.goods.pojo.User;
import com.ye.goods.service.IOrderService;
import com.ye.goods.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Iterator;
import java.util.Map;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController {

    private IOrderService orderService;

    private IUserService userService;

    @Autowired
    public OrderController(IOrderService orderService, IUserService userService) {
        this.orderService = orderService;
        this.userService =  userService;
    }

    @GetMapping
    public ServerResponse all(Principal principal, @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                              @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        String loginUsername = principal.getName();
        User user = (User)userService.info(loginUsername).getTarget();

        return orderService.all(user.getId(), pageNum, pageSize);
    }

    @PostMapping
    public ServerResponse create(Principal principal, Integer shippingId) {
        String loginUsername = principal.getName();
        User user = (User)userService.info(loginUsername).getTarget();

        return orderService.create(user.getId(), shippingId);
    }

    @GetMapping("/{orderNo}")
    public ServerResponse detail(Principal principal, @PathVariable Long orderNo) {
        String loginUsername = principal.getName();
        User user = (User)userService.info(loginUsername).getTarget();

        if (orderNo == null) {
            return ServerResponse.ERROR("订单号不能为空");
        }

        return orderService.detail(user.getId(), orderNo);
    }


    @PutMapping("/cancel/{orderNo}")
    public ServerResponse cancel(Principal principal, @PathVariable Long orderNo) {
        String loginUsername = principal.getName();
        User user = (User)userService.info(loginUsername).getTarget();

        return orderService.cancel(user.getId(), orderNo);
    }

    @GetMapping("/product/checked")
    public ServerResponse productChecked(Principal principal) {
        String loginUsername = principal.getName();
        User user = (User)userService.info(loginUsername).getTarget();

        return orderService.productChecked(user.getId());
    }

    @GetMapping("/pay")
    public ServerResponse pay(Principal principal, Long orderNo, HttpServletRequest request) {
        String loginUsername = principal.getName();
        User user = (User)userService.info(loginUsername).getTarget();

        String path = request.getSession().getServletContext().getRealPath("upload");

        return orderService.pay(orderNo, user.getId(), path);
    }

    @PostMapping("/alipay/callback")
    public Object alipayCallback(HttpServletRequest request) {
        Map<String,String> params = Maps.newHashMap();

        Map requestParams = request.getParameterMap();
        for(Iterator iter = requestParams.keySet().iterator();iter.hasNext();){
            String name = (String)iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for(int i = 0 ; i <values.length;i++){

                valueStr = (i == values.length -1)?valueStr + values[i]:valueStr + values[i]+",";
            }
            params.put(name,valueStr);
        }
        log.info("支付宝回调,sign:{},trade_status:{},参数:{}",params.get("sign"),params.get("trade_status"),params.toString());

        //非常重要,验证回调的正确性,是不是支付宝发的.并且呢还要避免重复通知.

        params.remove("sign_type");
        try {
            boolean alipayRSACheckedV2 = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(),"utf-8",Configs.getSignType());

            if(!alipayRSACheckedV2){
                return ServerResponse.ERROR("非法请求,验证不通过,再恶意请求我就报警找网警了");
            }
        } catch (AlipayApiException e) {
            log.error("支付宝验证回调异常",e);
        }
        // todo 验证其他数据
        ServerResponse serverResponse = orderService.alipayCallback(params);
        if (serverResponse.getCode() == 0) {
            return AlipayCallbackStatus.RESPONSE_SUCCESS;
        }
        return AlipayCallbackStatus.RESPONSE_FAILED;
    }

    @GetMapping("/query/pay/status")
    public ServerResponse queryOrderPayStatus(Principal principal, Long orderNo) {
        String loginUsername = principal.getName();
        User user = (User)userService.info(loginUsername).getTarget();

        return orderService.queryOrderPayStatus(user.getId(), orderNo);
    }
}
