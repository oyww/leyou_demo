package com.leyou.auth.web;

import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.service.AuthService;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.enums.ExceptionEnums;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.CookieUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
//@EnableConfigurationProperties(JwtProperties.class)
//服务处已经开启加入容器，controller层引用业务层就不用开启了
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private JwtProperties prop;

    /**
     * 登录授权
     *
     * @param username
     * @param password
     * @return
     */
    @PostMapping("accredit")
    public ResponseEntity<Void> authentication(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpServletRequest request,
            HttpServletResponse response) {
        // 登录校验
        String token = this.authService.authentication(username, password);
        if (StringUtils.isBlank(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        //CookieUtils.setCookie(request, response, prop.getCookieName(),token, prop.getCookieMaxAge(), null, true);
        //2.将token写入cookie，并指定httpOnly为true，防止通过js获取和修改
        CookieUtils.setCookie(request, response, prop.getCookieName(), token, prop.getCookieMaxAge(), true);

        return ResponseEntity.ok().build();
    }

    /**
     * 验证用户信息
     *
     * @param token
     * @return
     */
    @GetMapping("verify")
    public ResponseEntity<UserInfo> verifyUser(@CookieValue("LY_TOKEN") String token, HttpServletRequest request, HttpServletResponse response) {
        try {
            // 使用公钥解析token获取载荷信息，直接使用工具类，没有调service，没有异常就说明成功，
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, prop.getPublicKey());
            //刷新token，直接使用工具类，没有调service：载荷、公钥、过期时间
            String newToken = JwtUtils.generateToken(userInfo, prop.getPrivateKey(), prop.getExpire());
            // 然后写入cookie中
            // 将token写入cookie,并指定httpOnly为true，防止通过JS获取和修改
//            CookieUtils.setCookie(request, response, prop.getCookieName(), newToken, prop.getCookieMaxAge(), null,true);
            CookieUtils.newBuilder(response).httpOnly().request(request).build("LY_TOKEN", newToken);
            // 成功后直接返回
            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            // 抛出异常，证明token无效，直接返回401
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
    @GetMapping("logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response){
        try {
            CookieUtils.deleteCookie(request,response,prop.getCookieName());
        } catch (Exception e) {
            throw new LyException(ExceptionEnums.INVALID_REQUST);
        }
        return ResponseEntity.ok().build();

    }
}
