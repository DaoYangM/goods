package com.ye.goods.controller.backend;

import com.ye.goods.common.ResponseCode;
import com.ye.goods.common.ServerResponse;
import com.ye.goods.service.Impl.OrderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/manage/order")
public class OrderManageController {

    private OrderServiceImpl orderService;

    @Autowired
    public void setOrderService(OrderServiceImpl orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ServerResponse all(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                              @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        return orderService.manageAll(pageNum, pageSize);
    }

    @GetMapping("/{orderNo}")
    public ServerResponse detail(@PathVariable String orderNo) {
        if (orderNo == null) {
            return ServerResponse.ERROR(ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        return orderService.detail(null, Long.parseLong(orderNo));
    }

    @GetMapping("/search")
    public ServerResponse search(@RequestParam("productName") String productName,
                                 @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                 @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return orderService.manageSearch(productName, pageNum, pageSize);
    }
}
