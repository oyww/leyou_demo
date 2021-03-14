package com.leyou.page.web;

import com.leyou.page.pojo.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.Date;

@Controller
public class HelloController {
    @GetMapping("hello")
    public String toHello(Model model){
        User user = new User();
        user.setAge(21);
        user.setName("Jack Chen");
        user.setFriend(new User("李小龙", 30,null));

        model.addAttribute("item", user);
        model.addAttribute("msg","hello,Thymeleaf");
        return "hello";
    }
    @GetMapping("hello2")
    public String toHello2(Model model){
        model.addAttribute("today", new Date());
        return "hello2";
    }
    @GetMapping("hello3")
    public String toHello3(Model model){
        ArrayList<User> users = new ArrayList<>();
        for (int i = 0; i <15 ; i++) {
            users.add(new User("李小龙", 30+i,null));
        }
        model.addAttribute("users", users);
        return "hello3";
    }
}
