package com.leyou.cart.mq;

import com.leyou.cart.service.CartService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Map;


@Component
@Slf4j
public class SmsListener {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private CartService cartService;

    /**
     * 清空购物车
     */
    @RabbitListener(
            bindings = @QueueBinding(
                    //value = 队列名称，durable = 队列是否持久化
                    value = @Queue(value = "order.clearn.carts.queue", durable = "true"),
                    exchange = @Exchange(//交换机
                            value = "ly.sms.exchange",
                            ignoreDeclarationExceptions = "true",//忽略声明异常
                            type = ExchangeTypes.TOPIC//交换类型
                    ),
                    key = "order.clearn.carts"))//路由key
    public void listen(Map<String, String> msg) throws Exception {
        if (CollectionUtils.isEmpty(msg)) {
            log.error("[购物车服务] 用户ID没有接收到");
            return;
        }
        String uid = msg.remove("uid");
        cartService.clearnCarts(uid);
    }
}
