package com.leyou.user.web;

import com.leyou.common.enums.ExceptionEnums;
import com.leyou.common.exception.LyException;
import com.leyou.user.pojo.User;
import com.leyou.user.service.Userservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.stream.Collectors;

@RestController
public class UserController {
    @Autowired
    private Userservice userservice;

    @GetMapping("/check/{data}/{type}")
    public ResponseEntity<Boolean> checkData(@PathVariable("data") String data, @PathVariable("type") Integer type) {

        return ResponseEntity.ok(userservice.checkData(data, type));

    }
    @PostMapping("send")
//    @PostMapping("code")
    public ResponseEntity<Void> sendCode(@RequestParam("phone") String phone){
//        phone = "oywwcs@126.com";
        userservice.sendCode(phone);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    @PostMapping("register")
    public ResponseEntity<Void> register(@Valid User user, BindingResult result){
        if (result.hasErrors()) {//伪造请求的可以不用给友好提示
            String message = result.getFieldErrors().stream().map(e -> e.getDefaultMessage()).collect(Collectors.joining("|"));
            throw new RuntimeException(message);
        }
        userservice.register(user);
//        return ResponseEntity.status(HttpStatus.OK).build();//返回200
        return ResponseEntity.status(HttpStatus.CREATED).build(); //返回201
    }
    @GetMapping("query")
    public ResponseEntity<User> queryUserByUserNameAndPassword(@RequestParam("username") String name,@RequestParam("password") String password){//PathParam注解默认要求参数必须有，还可以起别名
        User user = null;
        try {
            user = userservice.queryUserByUserNameAndPassword(name, password);
        } catch (Exception e) {
            throw new LyException(ExceptionEnums.INVALID_USERNAME_PASSWORD);
        }
        return ResponseEntity.ok(user);

    }
}
