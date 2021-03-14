package com.leyou.user.service;

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
public class UserserviceTest {
    @Autowired
    private AmqpTemplate amqpTemplate;
    @Test
    public void sendCode() throws Exception {
        HashMap<String, String> map = new HashMap<>();
        map.put("email", "oywwcs@126.com");//实际传递过来的是email,之后还原有消息服务固定成email
        String code = NumberUtils.generateCode();
        map.put("msg", code);
        amqpTemplate.convertAndSend("ly.sms.exchange", "sms.verify.code", map);

    }

}