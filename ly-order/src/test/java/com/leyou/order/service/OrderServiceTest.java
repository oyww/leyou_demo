package com.leyou.order.service;

import com.leyou.common.utils.NumberUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderServiceTest {
    @Autowired
    private AmqpTemplate amqpTemplate;
    public static final String ORDER_CARTS = "order:cart:uid:";//用户购物车对应的id前缀

    @Test
    public void createOrder() throws Exception {
        HashMap<String, String> map = new HashMap<>();
        map.put("uid", "48");//获取用户的id
//        amqpTemplate.convertAndSend("ly.sms.exchange", "sms.verify.code", map);

        amqpTemplate.convertAndSend("ly.sms.exchange", "order.clearn.carts", map);
    }

}