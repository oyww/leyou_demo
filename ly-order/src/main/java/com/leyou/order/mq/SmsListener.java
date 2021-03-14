package com.leyou.order.mq;

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

/**
 * 暂时用不上，删除组件注解
 */

@Slf4j
public class SmsListener {
    @Autowired
    private StringRedisTemplate redisTemplate;

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
                            type = ExchangeTypes.DIRECT//交换类型
                    ),
                    key = "order.clearn.carts"))//路由key
    public void listen(Map<String, String> msg) throws Exception {
        String email = msg.remove("email");
        //把email固定成测试账号
        email = "oywwcs@126.com";
        if (StringUtils.isBlank(email)) {
            log.error("[信息服务] 请求参数有误");
            return;
        }

        if (CollectionUtils.isEmpty(msg)) {
            return;
        }
        log.info("[订单服务] 验证码发送成功 ，收码地址是{},验证码是:{}",email,msg.get("msg"));

    }
}
