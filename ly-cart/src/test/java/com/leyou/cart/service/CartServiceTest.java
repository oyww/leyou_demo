package com.leyou.cart.service;

import com.leyou.cart.client.GoodsClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CartServiceTest {
    @Autowired
    private StringRedisTemplate redisTemplate;

    static final String KEY_PREFIX = "ly:cart:uid:";

    @Test
    public void deleteCarts() throws Exception {
        Boolean delete = redisTemplate.delete(KEY_PREFIX+"48");
        if (delete) {
            System.err.println("delete = " + delete);
        }else {
            System.err.println("不存在");
        }
    }

}