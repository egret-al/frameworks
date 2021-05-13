package com.example.controller;

import com.annotation.*;
import com.example.entity.User;
import com.example.service.UserService;
import com.http.HttpServlet;
import com.http.HttpServletRequest;
import com.http.HttpServletResponse;
import com.spring.annotation.Autowired;
import com.spring.annotation.Controller;
import com.spring.annotation.Qualifier;

import java.io.IOException;

/**
 * @author：rkc
 * @date：Created in 2021/5/11 21:30
 * @description：
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    @Qualifier("userServiceImpl")
    private UserService userService;

    @RequestMapping(value = "test3", method = RequestMethod.POST)
    public User test2(HttpServletRequest request, HttpServletResponse response, @RequestBody User user) {
        System.out.println("test3: " + user);
        user.setAge(user.getAge() + 2);
        return user;
    }

    @RequestMapping(value = "test2", method = RequestMethod.GET)
    public Object test2(HttpServletRequest request, HttpServletResponse response, @RequestParam("name") String name) {
        System.out.println("test2: " + name);
        return "success";
    }

    @RequestMapping(value = "test1", method = RequestMethod.GET)
    public Object test1() {
        System.out.println("test1");
        return "success";
    }

    public UserService getUserService() {
        return userService;
    }
}
