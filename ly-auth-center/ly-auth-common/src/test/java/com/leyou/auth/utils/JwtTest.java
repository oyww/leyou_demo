package com.leyou.auth.utils;

import com.leyou.auth.entity.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;

import static org.junit.Assert.*;
@Slf4j
public class JwtTest {
    private static final String pubKeyPath = "D:/java/rsa/rsa.pub";

    private static final String priKeyPath = "D:/java/rsa/rsa.pri";

    private PublicKey publicKey;

    private PrivateKey privateKey;
    @Before
    public void createDir(){
        File file = new File("D:/java/rsa");
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    @Test
    public void testRsa() throws Exception {
        /**
         * 根据密文，生成rsa公钥和私钥,并写入指定文件
         */
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "234");
    }

    @Before
    public void testGetRsa() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);//从文件中读取公钥
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);//从文件中读取密钥
    }

    @Test
    public void testGenerateToken() throws Exception {
        /**
         * 私钥加密token
         * 参数：载荷。私钥，过期时间
         */
        // 生成token，
        String token = JwtUtils.generateToken(new UserInfo(20L, "jack"), privateKey, 5);
        System.out.println("token = " + token);
    }

    @Test
    public void testParseToken()  {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MjAsInVzZXJOYW1lIjoiamFjayIsImV4cCI6MTUyNzMzMDY5NX0.VpGNedy1z0aR262uAp2sM6xB4ljuxa4fzqyyBpZcGTBNLodIfuCNZkOjdlqf-km6TQPoz3epYf8cc_Rf9snsGdz4YPIwpm6X14JKU9jwL74q6zy61J8Nl9q7Zu3YnLibAvcnC_y9omiqKN8-iCi7-MvM-LwVS7y_Cx9eu0aaY8E";//错误的
        token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MjAsInVzZXJuYW1lIjoiamFjayIsImV4cCI6MTYxNTM4NzcwOX0.Is8zK0eSu9m7jEXct1txDc1Na1m5leDZ9NLYXfi5cWTEWI939t3ujdxzxN_nKIL1KrH9tPrFNEd4toCmjoehyYX59dGyJtUPb5U_N-46yhWqxHMyQkVRsa6YhZzbbRzbzL_rW15JlHqCNNM7hITAmMBjwe7odV2AQNAIZc__Np4";

        try {
            // 解析token
            /**
             * 获取token中的用户信息，参数：令牌token，公钥
             */
            UserInfo user = JwtUtils.getInfoFromToken(token, publicKey);
            System.out.println("id: " + user.getId());
            System.out.println("userName: " + user.getUsername());
        } catch (Exception e) {
            log.error("解析出错了，错误原因是:{}",e.getMessage());
        }
    }

}