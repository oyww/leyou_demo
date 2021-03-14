package com.leyou.utils;

import com.leyou.SmsApplication;
import com.leyou.common.enums.ExceptionEnums;
import com.leyou.common.exception.LyException;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.HashMap;
@Slf4j
@SpringBootTest(classes = SmsApplication.class)
@RunWith(SpringRunner.class)
public class MailUtilsTest {
    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private MailUtils mailUtils;
    @Autowired
    private TemplateEngine templateEngine;
    @Test
    public void sendMail()  {
        try {
            //model
            HashMap<String, String> map = new HashMap<>();
            map.put("msg","模板内容");
            //初始化上下文对象
            Context context = new Context();
            //参数1 model在模板中的对象名 参数2 model
            context.setVariable("mail",map);
            //根据模板格式化字符串后返回的字符串,参数1 模板名称 参数2 上下文
            String msg = templateEngine.process("temp", context);
            mailUtils.sendMail("oywwcs@126.com",msg);
        } catch (Exception e) {
            log.error("[信息发送服务] 信息发送失败原因是:{}",e.getMessage());
            throw new LyException(ExceptionEnums.MESSAGE_SEND_ERROR);
        }

    }
    /**
     * 测试通过发送消息触发信息发送
     */
    @Test
    public void testMqSendMessage()throws Exception{
        //创建消息体
        HashMap<String, String> map = new HashMap<>();
//        map.put("email","");
        map.put("email","oywwcs@126.com");
        map.put("msg",NumberUtils.generateCode());
        amqpTemplate.convertAndSend("ly.sms.exchange","sms.verify.code",map);
    }

}
/**
 *
 * // 利用 Thymeleaf 模板构建 html 文本
 Context ctx = new Context();
 // 给模板的参数的上下文
 ctx.setVariable("emailParam", emailParam);
 // 执行模板引擎，执行模板引擎需要传入模板名、上下文对象
 // Thymeleaf的默认配置期望所有HTML文件都放在 **resources/templates ** 目录下，以.html扩展名结尾。
 // String emailText = templateEngine.process("email/templates", ctx);
 String emailText = templateEngine.process(template, ctx);
 mimeMessageHelper.setText(emailText, true);

 */