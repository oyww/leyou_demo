package com.leyou.order.service;

import com.leyou.auth.entity.UserInfo;
import com.leyou.common.dto.CartDto;
import com.leyou.common.enums.ExceptionEnums;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.IdWorker;
import com.leyou.item.pojo.Sku;
import com.leyou.order.client.AddressClient;
import com.leyou.order.client.GoodsClient;
import com.leyou.order.dto.AddressDto;
import com.leyou.order.dto.OrderDto;
import com.leyou.order.enums.OrderStatusEnum;
import com.leyou.order.enums.PayState;
import com.leyou.order.intercepts.LoginInterceptor;
import com.leyou.order.mapper.OrderDetailMapper;
import com.leyou.order.mapper.OrderMapper;
import com.leyou.order.mapper.OrderStatusMapper;
import com.leyou.order.pojo.Order;
import com.leyou.order.pojo.OrderDetail;
import com.leyou.order.pojo.OrderStatus;
import com.leyou.order.utils.PayHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private OrderStatusMapper orderStatusMapper;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private AmqpTemplate amqpTemplate;
    @Autowired
    private PayHelper payHelper;


    @Transactional
    public Long createOrder(OrderDto orderDto) {
        // 1 新增订单
        Order order = new Order();
        // 1.1 订单编号，基本信息 -- 订单ID，雪花算法（snowflake）生成全局唯一的ID
        long orderId = idWorker.nextId();
        order.setOrderId(orderId);
        order.setCreateTime(new Date());
        order.setPaymentType(orderDto.getPaymentType());

        // 1.2 用户信息
        UserInfo user = LoginInterceptor.getLoginUser();
        order.setUserId(user.getId());
        order.setBuyerNick(user.getUsername());
        order.setBuyerRate(false);

        // 1.3 收货人地址信息 -- orderDTO中只有地址ID（addressID），要根据地址ID去数据库中查询(假数据)
        AddressDto addr = AddressClient.findById(orderDto.getAddressId());
        order.setReceiver(addr.getName());//收货人
        order.setReceiverMobile(addr.getPhone());//收货人手机号码
        order.setReceiverAddress(addr.getAddress());//收货所在街道
        order.setReceiverState(addr.getState());//收货人所在省
        order.setReceiverCity(addr.getCity());//收货人所在城市
        order.setReceiverDistrict(addr.getDistrict());//收货人所在区
        order.setReceiverZip(addr.getZipCode());//收货人邮编

        // 1.4 金额
        Map<Long, Integer> numMap = orderDto.getCarts()
                .stream().collect(Collectors.toMap(CartDto::getSkuId, CartDto::getNum));
        Set<Long> ids = numMap.keySet();
        //set转list直接传到构造方法里
        List<Sku> skus = goodsClient.querySkuListByIds(new ArrayList<>(ids));

        // 准备orderDetail集合
        List<OrderDetail> details = new ArrayList<>();

        Long totalPay = 0L;
        for (Sku sku : skus) {
            totalPay += sku.getPrice() * numMap.get(sku.getId());

            //封装orderDetail
            OrderDetail detail = new OrderDetail();
            detail.setImage(StringUtils.substringBefore(sku.getImages(), ","));
            detail.setNum(numMap.get(sku.getId()));
            detail.setOrderId(orderId);
            detail.setOwnSpec(sku.getOwnSpec());
            detail.setPrice(sku.getPrice());
            detail.setSkuId(sku.getId());
            detail.setTitle(sku.getTitle());

            details.add(detail);
        }
        order.setTotalPay(totalPay);
        order.setActualPay(totalPay + order.getPostFee() - 0);// 实付金额= 总金额 + 邮费 - 优惠金额

        // 1.5 写入数据库
        int count = orderMapper.insertSelective(order);
        if (count != 1) {
            log.error("[订单服务] 创建订单失败，orderID:{}", orderId);
            throw new LyException(ExceptionEnums.CREATE_ORDER_ERROR);
        }

        // 2 新增订单详情
        count = orderDetailMapper.insertList(details);
        if (count != details.size()) {
            log.error("[订单服务] 创建订单详情失败，orderID:{}", orderId);
            throw new LyException(ExceptionEnums.CREATE_ORDER_ERROR);
        }

        // 3 新增订单状态
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderId(orderId);
        orderStatus.setCreateTime(order.getCreateTime());
        orderStatus.setStatus(OrderStatusEnum.UN_PAY.value());
        count = orderStatusMapper.insertSelective(orderStatus);
        if (count != 1) {
            log.error("[订单服务] 创建订单状态失败，orderID:{}", orderId);
            throw new LyException(ExceptionEnums.CREATE_ORDER_ERROR);
        }

        // 4 减库存 -- 需要调用商品微服务，传递商品id和数量两个参数
        List<CartDto> cartDtos = orderDto.getCarts();
        goodsClient.decreaseStock(cartDtos);
        //创建订单成功后清空购物车
        HashMap<String, String> map = new HashMap<>();
        map.put("uid", user.getId().toString());//获取用户的id
        amqpTemplate.convertAndSend("ly.sms.exchange", "order.clearn.carts", map);
        log.info("[订单服务] 订单创建成功，已发送清空购物车信息给购物车服务");

        return orderId;
    }

    public Order getOder(Long orderId) {
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            log.error("[订单服务] 订单查询失败，订单号不存在orderID:{}", orderId);
            throw new LyException(ExceptionEnums.ORDER_NOT_FPND);

        }
        // 查询订单详情
        OrderDetail detail = new OrderDetail();
        detail.setOrderId(orderId);
        List<OrderDetail> orderDetails = orderDetailMapper.select(detail);
        if (CollectionUtils.isEmpty(orderDetails)) {
            throw new LyException(ExceptionEnums.ORDER_DETAIL_NOT_FOUNT);
        }
        order.setOrderDetails(orderDetails);

        // 查询订单状态
        OrderStatus orderStatus = orderStatusMapper.selectByPrimaryKey(orderId);
        if (orderStatus == null) {
            throw new LyException(ExceptionEnums.ORDER_STATUS_NOT_FOUND);
        }
        order.setOrderStatus(orderStatus);

        return order;
    }

    public String createPayUrl(Long orderId) {
        //根据id查询订单
        Order order = getOder(orderId);
        // 判断订单状态，如果订单已支付，下面的查询就很多余
        OrderStatus orderStatus = order.getOrderStatus();
        Integer status = orderStatus.getStatus();
        if(status != OrderStatusEnum.UN_PAY.value()){
            throw new LyException(ExceptionEnums.ORDER_STATUS_ERROE);
        }
        Long actualPay =1l /*order.getActualPay()*/;//实付金额,测试支付1分钱
        String desc = order.getOrderDetails().get(0).getTitle();//获得第一个订单详情标题


        return payHelper.createOrder(orderId, actualPay, desc);
    }

    public void handleNotify(Map<String, String> result) {
        // 1 数据校验
        payHelper.isSuccess(result);
        // 2 签名校验
        payHelper.isValidSign(result);

        // 3 金额校验
        String totalFeeStr = result.get("total_fee");
        String tradeNo = result.get("out_trade_no");//我们传过去他发回来的订单编号
        if(StringUtils.isEmpty(totalFeeStr) || StringUtils.isEmpty(tradeNo)){
            throw new LyException(ExceptionEnums.INVALID_ORDER_PARAM);
        }
        Long totalFee = Long.valueOf(totalFeeStr);
        Long orderId = Long.valueOf(tradeNo);
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if(totalFee != /*order.getActualPay()*/ 1l){
            // 金额不符
            throw new LyException(ExceptionEnums.INVALID_ORDER_PARAM);
        }

        // 4 修改订单状态
        OrderStatus status = new OrderStatus();
        status.setStatus(OrderStatusEnum.PAYED.value());
        status.setOrderId(orderId);
        status.setPaymentTime(new Date());
        int count = orderStatusMapper.updateByPrimaryKeySelective(status);
        if(count != 1){
            throw new LyException(ExceptionEnums.UPDATE_ORDER_STATUS_ERROR);
        }

        log.info("[订单服务], 订单回调正常订单支付成功! 订单编号:{}", orderId);
    }

    public PayState queryOrderStateById(Long orderId) {
        OrderStatus orderStatus = orderStatusMapper.selectByPrimaryKey(orderId);
        Integer status = orderStatus.getStatus();
        if(!status.equals(OrderStatusEnum.UN_PAY.value())){
            return PayState.SUCCESS;// 如果是已支付，则是真的已支付
        }

        // 如果未支付,但其实不一定是未支付,必须去微信查询支付状态
        return payHelper.queryPayState(orderId);
    }
}
