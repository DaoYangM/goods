package com.ye.goods.controller.portal;

import com.ye.goods.common.ServerResponse;
import com.ye.goods.common.enums.CartCheckedEnum;
import com.ye.goods.service.Impl.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/cart")
public class CartController {

    private CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping
    public ServerResponse add(Principal principal, Integer productId, Integer count) {
        String username = principal.getName();

        if (count == null && count <= 0)
            return ServerResponse.ERROR_ILLEGAL_ARGUMENT();
        return cartService.add(username, productId, count);
    }

    @GetMapping
    public ServerResponse all(Principal principal) {
        String username = principal.getName();

        return cartService.all(username);
    }

    @PutMapping
    public ServerResponse updateCount(Principal principal, Integer productId, Integer count) {
        String username = principal.getName();

        if (count == null && count <= 0)
            return ServerResponse.ERROR_ILLEGAL_ARGUMENT();
        return cartService.updateCount(username, productId, count);
    }

    @DeleteMapping
    public ServerResponse deleteCart(Principal principal, @RequestBody String productIds) {
        String username = principal.getName();
        return cartService.delete(username, productIds);
    }

    @PutMapping("/select/{productId}")
    public ServerResponse selectOne(Principal principal, @PathVariable Integer productId) {
        String username = principal.getName();
        if (productId == null)
            return ServerResponse.ERROR_ILLEGAL_ARGUMENT();
        return cartService.selectOneOrUnSelectOne(username, CartCheckedEnum.CHECKED.getCode(), productId);
    }

    @PutMapping("/unselect/{productId}")
    public ServerResponse unSelectOne(Principal principal, @PathVariable Integer productId) {
        String username = principal.getName();
        if (productId == null)
            return ServerResponse.ERROR_ILLEGAL_ARGUMENT();
        return cartService.selectOneOrUnSelectOne(username, CartCheckedEnum.UNCHECKED.getCode(), productId);
    }

    @PutMapping("/selectall")
    public ServerResponse selectAll(Principal principal) {
        String username = principal.getName();
        return cartService.selectAllOrUnSelectAll(username, CartCheckedEnum.CHECKED.getCode());
    }

    @PutMapping("/unselectall")
    public ServerResponse unSelectAll(Principal principal) {
        String username = principal.getName();
        return cartService.selectAllOrUnSelectAll(username, CartCheckedEnum.UNCHECKED.getCode());
    }

    @GetMapping("/count")
    public ServerResponse count(Principal principal) {
        String username = principal.getName();
        return cartService.count(username);
    }
}
