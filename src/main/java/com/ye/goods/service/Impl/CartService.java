package com.ye.goods.service.Impl;

import com.ye.goods.common.enums.CartCheckedEnum;
import com.ye.goods.common.ServerResponse;
import com.ye.goods.dao.CartMapper;
import com.ye.goods.dao.ProductMapper;
import com.ye.goods.dao.UserMapper;
import com.ye.goods.pojo.Cart;
import com.ye.goods.pojo.Product;
import com.ye.goods.pojo.User;
import com.ye.goods.service.ICartService;
import com.ye.goods.utils.BigDecimalUtil;
import com.ye.goods.vo.cart.CartProductVO;
import com.ye.goods.vo.cart.CartVO;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CartService implements ICartService {

    private CartMapper cartMapper;

    private UserMapper userMapper;

    private ProductMapper productMapper;

    @Autowired
    public CartService(CartMapper cartMapper, UserMapper userMapper, ProductMapper productMapper) {
        this.cartMapper = cartMapper;
        this.userMapper = userMapper;
        this.productMapper = productMapper;
    }

    @Override
    @Transactional
    public ServerResponse add(String username, Integer productId, Integer count) {
        User user = userMapper.selectByUsername(username);
        Product product = productMapper.selectByPrimaryKey(productId);

        if (user != null && product != null) {
            Cart cartExisted = cartMapper.selectCartByUserIdAndProductId(user.getId(), product.getId());
            if (cartExisted == null) {
                    Cart cart = new Cart();

                    if (!decreaseQuantity(product, count))
                        return ServerResponse.ERROR("库存不足");

                    cart.setUserId(user.getId());
                    cart.setProductId(product.getId());
                    cart.setChecked(CartCheckedEnum.CHECKED.getCode());
                    cart.setQuantity(count);

                    if (cartMapper.insert(cart) == 1)
                        return  ServerResponse.SUCCESS("添加购物车成功");

                return ServerResponse.ERROR("添加购物车失败");
            } else {
                Cart cart = cartMapper.selectCartByUserIdAndProductId(user.getId(), product.getId());
                int countUpdate = cart.getQuantity() + count;
                if (!decreaseQuantity(product, countUpdate))
                    return ServerResponse.ERROR("库存不足");

                cart.setChecked(CartCheckedEnum.CHECKED.getCode());
                cart.setQuantity(countUpdate);

                if (cartMapper.updateByPrimaryKeySelective(cart) == 1)
                    return  ServerResponse.SUCCESS("添加购物车成功");
                return ServerResponse.ERROR("添加购物车失败");
            }
        } else
            return ServerResponse.ERROR("用户没有登录， 或商品不存在");

    }

    @Override
    public ServerResponse all(String username) {
        User user = userMapper.selectByUsername(username);
        if (user != null) {
            return ServerResponse.SUCCESS(getCartVOLimit(user.getId()));
        }

        return ServerResponse.ERROR_NEED_LOGIN();
    }

    @Override
    public ServerResponse updateCount(String username, Integer productId, Integer count) {
        User user = userMapper.selectByUsername(username);
        Product product = productMapper.selectByPrimaryKey(productId);

        if (user != null && product != null) {
            Cart cart = cartMapper.selectCartByUserIdAndProductId(user.getId(), product.getId());

            if (!decreaseQuantity(product, count))
                return ServerResponse.ERROR("库存不足");

            cart.setUserId(user.getId());
            cart.setProductId(product.getId());
            cart.setChecked(CartCheckedEnum.CHECKED.getCode());
            cart.setQuantity(count);

            if (cartMapper.updateByPrimaryKeySelective(cart) == 1)
                return  ServerResponse.SUCCESS("更新购物车成功");

        }
        return ServerResponse.ERROR("更新购物车失败");
    }

    @Override
    @Transactional
    public ServerResponse delete(String username, String productIds) {
        User user = userMapper.selectByUsername(username);
        if (user != null) {
            List<String> productIdList = Lists.newArrayList(productIds.split(","));
            if (!productIdList.isEmpty()) {

                int deleteCount = cartMapper.deleteByUserIdProductIds(user.getId(), productIdList);

                return deleteCount != 0? ServerResponse.SUCCESS("购物车商品删除成功"):
                ServerResponse.ERROR_ILLEGAL_ARGUMENT();
            }
            return ServerResponse.ERROR_ILLEGAL_ARGUMENT();
        }
        return ServerResponse.ERROR_NEED_LOGIN();
    }

    @Override
    public ServerResponse selectAllOrUnSelectAll(String username, Integer code) {
        User user = userMapper.selectByUsername(username);
        String msg = "全选成功";
        if (code.equals(CartCheckedEnum.UNCHECKED.getCode()))
            msg = "全反选成功";
        if (user != null) {
                return cartMapper.selectAllOrUnSelectAll(user.getId(), code, null) > 0?
                        ServerResponse.SUCCESS(msg):
             ServerResponse.ERROR_ILLEGAL_ARGUMENT();
        }
        return ServerResponse.ERROR_NEED_LOGIN();
    }

    @Override
    public ServerResponse selectOneOrUnSelectOne(String username, Integer code, Integer productId) {
        User user = userMapper.selectByUsername(username);
        String msg = "单选成功";
        if (code.equals(CartCheckedEnum.UNCHECKED.getCode()))
            msg = "反选成功";
        if (user != null) {
            return cartMapper.selectAllOrUnSelectAll(user.getId(), code, productId) > 0?
                    ServerResponse.SUCCESS(msg):
                    ServerResponse.ERROR_ILLEGAL_ARGUMENT();
        }
        return ServerResponse.ERROR_NEED_LOGIN();
    }

    private CartVO getCartVOLimit(Integer userId) {

        CartVO  cartVO = new CartVO();

        List<Cart> cartList = cartMapper.selectCartByUserId(userId);

        BigDecimal cartTotalPrice = new BigDecimal("0");

        List<CartProductVO> productVOList = new ArrayList<>();

        for (Cart cart: cartList) {
            BigDecimal productTotalPrice = new BigDecimal("0");

            Product product = productMapper.selectByPrimaryKey(cart.getProductId());

            CartProductVO cartProductVO = new CartProductVO();

            cartProductVO.setId(cart.getId());
            cartProductVO.setProductName(product.getName());
            cartProductVO.setProductId(product.getId());
            cartProductVO.setProductMainImage(product.getMainImage());
            cartProductVO.setProductPrice(product.getPrice());
            cartProductVO.setProductSubtitle(product.getSubtitle());
            cartProductVO.setQuantity(cart.getQuantity());

            productTotalPrice = BigDecimalUtil.mul(cart.getQuantity(), product.getPrice().doubleValue());

            if (cart.getChecked().equals(CartCheckedEnum.CHECKED.getCode())) {
                cartTotalPrice = BigDecimalUtil.add(productTotalPrice.doubleValue(), cartTotalPrice.doubleValue());
                cartProductVO.setProductChecked(CartCheckedEnum.CHECKED.getCode());
            } else {
                cartProductVO.setProductChecked(CartCheckedEnum.UNCHECKED.getCode());
            }


            cartProductVO.setProductTotalPrice(productTotalPrice);

            productVOList.add(cartProductVO);
        }

        cartVO.setCartTotalPrice(cartTotalPrice);
        cartVO.setProductVOList(productVOList);

        return cartVO;
    }

    private boolean decreaseQuantity(Product product ,int count) {
        Integer productQuantity =  product.getStock();

        if (productQuantity < count) {
            log.error("删除库存失败");
            return false;
        }

        product.setStock(product.getStock() - count);
        productMapper.updateByPrimaryKeySelective(product);
        log.info("删除库存成功");
        return true;
    }
}
