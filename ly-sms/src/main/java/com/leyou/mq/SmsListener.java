package com.leyou.mq;

import com.leyou.utils.MailUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Component
@Slf4j
public class SmsListener {
    @Autowired
    private MailUtils mailUtils;
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 发送验证码
     */
    @RabbitListener(
            bindings = @QueueBinding(
                    //value = 队列名称，durable = 队列是否持久化
                    value = @Queue(value = "sms.verify.code.queue", durable = "true"),
                    exchange = @Exchange(//交换机
                            value = "ly.sms.exchange",
                            ignoreDeclarationExceptions = "true",//忽略声明异常
                            type = ExchangeTypes.TOPIC//交换类型
                    ),
                    key = "sms.verify.code"))//路由key
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
        //model
        //初始化上下文对象
        Context context = new Context();
        //参数1 model在模板中的对象名 参数2 model
        context.setVariable("mail", msg);
        //根据模板格式化字符串后返回的字符串,参数1 模板名称 参数2 上下文
        String text = templateEngine.process(mailUtils.getTemplateName(), context);
        mailUtils.sendMail(email, text);
        log.info("[信息服务] 验证码发送成功 ，收码地址是{},验证码是:{}",email,msg.get("msg"));

    }
}
