package com.leyou.sms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
@Data
@ConfigurationProperties(prefix = "ly.sms")
public class SmsProperties {
    private String email_user_name;
    private String email_pass_word;
    private String emailTitleName;
    private String verifyCodeTemplate;
}
