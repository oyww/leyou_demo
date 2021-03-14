package com.leyou.user.api;

import com.leyou.user.pojo.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface UserApi {

    @GetMapping("query")
    public User queryUserByUserNameAndPassword(@RequestParam("username") String name, @RequestParam("password") String password);
}