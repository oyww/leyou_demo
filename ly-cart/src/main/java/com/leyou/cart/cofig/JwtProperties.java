package com.leyou.cart.cofig;

import com.leyou.auth.utils.RsaUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;

@Data
@Slf4j
@ConfigurationProperties(prefix = "ly.jwt")
public class JwtProperties {


    private String pubKeyPath;// 公钥

    private PublicKey publicKey; // 公钥对象


    /**
     * cookie名字
     */
    @Value("${ly.jwt.cookieName}")
    private String cookieName;




    /**
     * PostConstrct在构造方法执行之后执行（已经有对象）
     */
    @PostConstruct
    public void init(){
        try {
            File pubKey = new File(pubKeyPath);
            // 获取公钥和私钥
            this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        } catch (Exception e) {
            log.error("初始化公钥失败！", e);
            throw new RuntimeException();
        }
    }
    
    //TODO add getter setter ...
}