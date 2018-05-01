package com.ye.goods.service.Impl;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ye.goods.common.AlipayCallbackStatus;
import com.ye.goods.common.ServerResponse;
import com.ye.goods.common.enums.OrderStatusEnum;
import com.ye.goods.common.enums.ProductStatusEnum;
import com.ye.goods.common.exception.ShippingException;
import com.ye.goods.dao.*;
import com.ye.goods.pojo.*;
import com.google.common.collect.Maps;
import com.ye.goods.service.IOrderService;
import com.ye.goods.utils.BigDecimalUtil;
import com.ye.goods.utils.DateTimeUtil;
import com.ye.goods.utils.FTPUtil;
import com.ye.goods.utils.Properties.Properties;
import com.ye.goods.vo.order.OrderItemVO;
import com.ye.goods.vo.order.OrderProductVO;
import com.ye.goods.vo.order.OrderVO;
import com.ye.goods.vo.order.ShippingVO;
import org.apache.commons.lang.StringUtils;
import org.assertj.core.util.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service("orderService")
public class OrderServiceImpl implements IOrderService {

    private static Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    private OrderMapper orderMapper;

    private OrderItemMapper orderItemMapper;

    private Properties properties;

    private PayInfoMapper payInfoMapper;

    private CartMapper cartMapper;

    private ProductMapper productMapper;

    private ShippingMapper shippingMapper;

    @Autowired
    public void setShippingMapper(ShippingMapper shippingMapper) {
        this.shippingMapper = shippingMapper;
    }

    @Autowired
    public void setProductMapper(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    @Autowired
    public void setCartMapper(CartMapper cartMapper) {
        this.cartMapper = cartMapper;
    }

    @Autowired
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    @Autowired
    public void setPayInfoMapper(PayInfoMapper payInfoMapper) {
        this.payInfoMapper = payInfoMapper;
    }

    @Autowired
    public OrderServiceImpl(OrderMapper orderMapper, OrderItemMapper orderItemMapper) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
    }
    private static  AlipayTradeService tradeService;

    static {

        /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfbinfo.properties");

        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();

    }

    @Override
    public ServerResponse create(Integer userId, Integer shippingId) {

        Shipping shipping = shippingMapper.selectByUserIdAndShippingId(userId, shippingId);

        if (shipping == null)
            return ServerResponse.ERROR("收货地址不存在");

        List<Cart> cartList = cartMapper.selectCheckedCart(userId);

        ServerResponse<List<OrderItem>> response = this.getCartOrderItem(userId, cartList);


        if (response.isSuccess()) {
            List<OrderItem> orderItemList = (List<OrderItem>) response.getTarget();

            if (CollectionUtils.isEmpty(orderItemList)) {
                return ServerResponse.ERROR("订单不存在");
            }
            // 计算订单总价
            BigDecimal orderTotalPrice = this.getTotalPrice(orderItemList);

            // 生成订单
            Order order = this.assembleOrder(userId, shippingId, orderTotalPrice);

            if (order == null)
                return ServerResponse.ERROR("订单生成失败");

            for (OrderItem orderItem : orderItemList) {
                orderItem.setOrderNo(order.getOrderNo());
            }

            orderItemMapper.batchInsert(orderItemList);
            // 批量插入

            //清空购物车
            for (Cart cart : cartList) {
                cartMapper.deleteByPrimaryKey(cart.getId());
            }
            log.info("正在清空购物车");

            // 组装OrderVO

            OrderVO orderVO = assembleOrderVO(order, orderItemList);
            orderVO.setCreateTime(order.getCreateTime().getTime());

            return ServerResponse.SUCCESS(orderVO);
        }
        return ServerResponse.ERROR("订单细明查询失败");
    }

    @Override
    public ServerResponse pay(Long orderNo, Integer userId, String path) {
        Map<String ,String> resultMap = Maps.newHashMap();
        Order order = orderMapper.selectByUserIdAndOrderNo(userId,orderNo);
        if(order == null){
            return ServerResponse.ERROR("用户没有该订单");
        }
        resultMap.put("orderNo",String.valueOf(order.getOrderNo()));



        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = order.getOrderNo().toString();


        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = new StringBuilder().append("happymmall扫码支付,订单号:").append(outTradeNo).toString();


        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = order.getPayment().toString();


        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";



        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = new StringBuilder().append("订单").append(outTradeNo).append("购买商品共").append(totalAmount).append("元").toString();


        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");




        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();

        List<OrderItem> orderItemList = orderItemMapper.getByOrderNoAndUserId(userId, orderNo);
        for(OrderItem orderItem : orderItemList){
            GoodsDetail goods = GoodsDetail.newInstance(orderItem.getProductId().toString(), orderItem.getProductName(),
                    BigDecimalUtil.mul(orderItem.getCurrentUnitPrice().doubleValue(),new Double(100).doubleValue()).longValue(),
                    orderItem.getQuantity());
            goodsDetailList.add(goods);
        }

        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                .setNotifyUrl(properties.getAlipay().getCallbackUrl())//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setGoodsDetailList(goodsDetailList);


        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("支付宝预下单成功: )");

                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);

                File foler = new File(path);
                if (!foler.exists()) {
                    foler.setWritable(true);
                    foler.mkdirs();
                }

                // 需要修改为运行机器上的路径
                String qrPath = String.format(path + "/qr-%s.png", response.getOutTradeNo());
                String qrFileName = String.format("qr-%s.png", response.getOutTradeNo());
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrPath);

                File targetFile = new File(path, qrFileName);
                FTPUtil.uploadFile(Lists.newArrayList(targetFile), properties);

                log.info("filePath:" + qrPath);
                //                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, filePath);
                String qrUrl = "ftp://" + properties.getFtp().getImageHost() + "/" + properties.getFtp().getImgDirectory() + "/" + targetFile.getName();

                resultMap.put("qrUrl", qrUrl);

                return ServerResponse.SUCCESS(resultMap);

            case FAILED:
                log.error("支付宝预下单失败!!!");
                return ServerResponse.ERROR("支付宝预下单失败!!!");

            case UNKNOWN:
                log.error("系统异常，预下单状态未知!!!");
                return ServerResponse.ERROR("系统异常，预下单状态未知!!!");

            default:
                log.error("不支持的交易状态，交易返回异常!!!");
                return ServerResponse.ERROR("不支持的交易状态，交易返回异常!!!");
        }
    }

    @Override
    public ServerResponse alipayCallback(Map<String, String> params) {
        Long out_trade_no = Long.parseLong(params.get("out_trade_no"));
        String tradeNo = params.get("trade_no");
        String tradeStatus = params.get("trade_status");

        Order order = orderMapper.selectByOrderNo(out_trade_no);
        if (order == null) {
            return ServerResponse.ERROR("alipayCallback: 订单号判断错误");
        }

        if (order.getStatus() >= OrderStatusEnum.PAID.getCode()) {
            return ServerResponse.SUCCESS("支付宝重复调用");
        }

        if (AlipayCallbackStatus.TRADE_SUCCESS.equals(tradeStatus)) {

            order.setPaymentTime(DateTimeUtil.strToDate(params.get("gmt_payment")));
            order.setEndTime(DateTimeUtil.strToDate(params.get("gmt_payment")));
            order.setStatus(OrderStatusEnum.PAID.getCode());
            orderMapper.updateByPrimaryKeySelective(order);
        }

        PayInfo payInfo = new PayInfo();
        payInfo.setUserId(order.getUserId());
        payInfo.setOrderNo(order.getOrderNo());
        payInfo.setPayPlatform(1);
        payInfo.setPlatformNumber(tradeNo);
        payInfo.setPlatformStatus(tradeStatus);

        payInfoMapper.insert(payInfo);

        return ServerResponse.SUCCESS("支付宝回调成功");
    }

    @Override
    public ServerResponse queryOrderPayStatus(Integer userId, Long orderNo) {
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if (order == null) {
            log.error("没有该订单");
            return ServerResponse.ERROR("没有该订单");
        }

        if (order.getStatus() >= OrderStatusEnum.PAID.getCode()) {
            return ServerResponse.SUCCESS(true);
        }
        return ServerResponse.ERROR("没有付款");
    }

    @Override
    public ServerResponse cancel(Integer userId, Long orderNo) {
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if (order == null) {
            log.error("没有该订单");
            return ServerResponse.ERROR("没有该订单");
        }

        if (order.getStatus().equals(OrderStatusEnum.NO_PAY.getCode())) {
            order.setStatus(OrderStatusEnum.CANCELED.getCode());
            int result = orderMapper.updateByPrimaryKeySelective(order);

            return result == 1? ServerResponse.SUCCESS("订单取消成功"):
                    ServerResponse.ERROR("订单取消失败");
        }

        return ServerResponse.ERROR("只有未付款的订单才能取消");
    }

    @Override
    public ServerResponse productChecked(Integer userId) {
        List<Cart> cartList = cartMapper.selectCartByUserId(userId);
        List<OrderItemVO> orderItemVOList;
        OrderProductVO orderProductVO = new OrderProductVO();

        ServerResponse serverResponse = this.getCartOrderItem(userId, cartList);

        if (serverResponse.isSuccess()) {
            List<OrderItem> orderItemList = (List<OrderItem>) serverResponse.getTarget();
            orderItemVOList = this.assembleListOrderItemVO(orderItemList);
            orderProductVO.setOrderItemVoList(orderItemVOList);
            orderProductVO.setProductTotalPrice(this.getTotalPrice(orderItemList));
            orderProductVO.setImageHost(properties.getFtp().getImageHost());
        }
        return ServerResponse.SUCCESS(orderProductVO);
    }

    @Override
    public ServerResponse detail(Integer userId, Long orderNo) {
        Order order;
        List<OrderItem> orderItemList;

        if (userId == null) {
            order = orderMapper.selectByOrderNo(orderNo);

            if (order == null) {
                return ServerResponse.ERROR("订单不存在");
            }

            orderItemList = orderItemMapper.selectByOrderNo(order.getOrderNo());

        } else {
            order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);

            if (order == null) {
                return ServerResponse.ERROR("订单不存在");
            }
            orderItemList = orderItemMapper.getByOrderNoAndUserId(userId, order.getOrderNo());
        }

        OrderVO orderVO = this.assembleOrderVO(order, orderItemList);

        return orderVO != null?
             ServerResponse.SUCCESS(orderVO): ServerResponse.ERROR("订单详情查看失败");
    }

    @Override
    public ServerResponse all(Integer userId, Integer pageNum, Integer pageSize) {
        List<Order> orderList = orderMapper.selectByUserId(userId);
        PageHelper.startPage(pageNum, pageSize);

        PageInfo<OrderVO> pageInfo = new PageInfo<OrderVO>(this.assembleOrderVOList(orderList, userId));

        return ServerResponse.SUCCESS(pageInfo);
    }

    @Override
    public ServerResponse manageAll(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        List<Order> orderList = orderMapper.selectAll();

        assembleOrderVOList(orderList, null);

        PageInfo<OrderVO> pageInfo = new PageInfo<OrderVO>(this.assembleOrderVOList(orderList, null));

        return ServerResponse.SUCCESS(pageInfo);
    }

    @Override
    public ServerResponse manageSearch(String productName, Integer pageNum, Integer pageSize) {

        PageHelper.startPage(pageNum, pageSize);

        List<Long> orderNoList = orderItemMapper.findByProductName(productName);

        List<OrderVO> orderVOList = new ArrayList<>();



        List<Long> unique = orderNoList.stream().distinct().collect(Collectors.toList());

        for (Long orderNo: unique) {
            Order order = orderMapper.selectByOrderNo(orderNo);
            OrderVO orderVO = this.assembleOrderVO(order, orderItemMapper.selectByOrderNo(order.getOrderNo()));

            orderVOList.add(orderVO);
        }
        PageInfo<OrderVO> pageInfo = new PageInfo<>(orderVOList);
        return ServerResponse.SUCCESS(pageInfo);
    }


    // 简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            log.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                log.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            log.info("body:" + response.getBody());
        }
    }

    private ServerResponse getCartOrderItem(Integer userId, List<Cart> cartList) {
        List<OrderItem> orderItemList = Lists.newArrayList();

        if (CollectionUtils.isEmpty(cartList))
            return ServerResponse.ERROR("购物车为空");

        for (Cart cart: cartList) {
            OrderItem orderItem = new OrderItem();
            Product product = productMapper.selectByPrimaryKey(cart.getProductId());

            if (product == null) {
                log.error("商品不存在");
                return ServerResponse.ERROR("商品不存在");

            }
            if (ProductStatusEnum.ON_SALE.getCode().equals(product.getStatus())) {
                if (product.getStock() < cart.getQuantity()) {
                    log.error("库存不足");
                    return ServerResponse.ERROR("库存不足");
                }

                orderItem.setUserId(userId);
                orderItem.setProductId(product.getId());
                orderItem.setQuantity(cart.getQuantity());
                orderItem.setProductName(product.getName());
                orderItem.setProductImage(product.getMainImage());
                orderItem.setCurrentUnitPrice(product.getPrice());
                orderItem.setTotalPrice(BigDecimalUtil.mul(cart.getQuantity().doubleValue(), product.getPrice().doubleValue()));

                orderItemList.add(orderItem);
            } else {
                return ServerResponse.ERROR("商品不存在");
            }
        }

        return ServerResponse.SUCCESS(orderItemList);
    }

    private BigDecimal getTotalPrice(List<OrderItem> orderItemList) {
        BigDecimal orderTotalPrice = new BigDecimal("0");

        for (OrderItem item : orderItemList) {
            orderTotalPrice = BigDecimalUtil.add(orderTotalPrice.doubleValue(), item.getTotalPrice().doubleValue());
        }

        return orderTotalPrice;
    }

    private Order assembleOrder(Integer userId, Integer shippingId, BigDecimal totalPrice) {
        long orderNo = this.generatorOrderNo();

        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setStatus(OrderStatusEnum.NO_PAY.getCode());
        order.setPostage(0);
        order.setPaymentType(1);

        order.setUserId(userId);
        order.setShippingId(shippingId);
        order.setPayment(totalPrice);
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());

        int result = orderMapper.insert(order);

        return result == 1? order: null;
    }

    private OrderVO assembleOrderVO(Order order, List<OrderItem> orderItemList) {
        OrderVO orderVO = new OrderVO();
        ShippingVO shippingVO = new ShippingVO();
        List<OrderItemVO> orderItemVOList;

        BeanUtils.copyProperties(order, orderVO);

        Shipping shipping = shippingMapper.selectByUserIdAndShippingId(order.getUserId(), order.getShippingId());

        if (shipping == null) {
            throw new ShippingException("收货地址不存在");
        }

        orderVO.setImageHost(properties.getFtp().getImageHost());
        orderVO.setReceiverName(shipping.getReceiverName());

        orderItemVOList = this.assembleListOrderItemVO(orderItemList);
        orderVO.setOrderItemVOList(orderItemVOList);

        BeanUtils.copyProperties(shipping, shippingVO);
        orderVO.setShippingVO(shippingVO);

        return orderVO;
    }

    private List<OrderVO> assembleOrderVOList(List<Order> orderList, Integer userId) {
        List<OrderVO> orderVOList = new ArrayList<>();
        OrderVO orderVO;

        for (Order order : orderList) {
            if (userId == null) {
               orderVO = this.assembleOrderVO(order, this.orderItemMapper.selectByOrderNo(order.getOrderNo()));
            } else {
                orderVO = this.assembleOrderVO(order,
                        this.orderItemMapper.getByOrderNoAndUserId(userId, order.getOrderNo()));
            }
                orderVOList.add(orderVO);
        }

         return orderVOList;
     }

    private List<OrderItemVO> assembleListOrderItemVO(List<OrderItem> orderItemList) {

        List<OrderItemVO> orderItemVOS = new ArrayList<>();

        for (OrderItem orderItem : orderItemList) {
            OrderItemVO orderItemVO = new OrderItemVO();
            BeanUtils.copyProperties(orderItem, orderItemVO);
            orderItemVOS.add(orderItemVO);
        }

        return orderItemVOS;
    }

    private long generatorOrderNo() {
        long currentTime = System.currentTimeMillis();
        return currentTime + new Random().nextInt(100);
    }

}
