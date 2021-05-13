package com.example;

import com.example.controller.UserController;
import com.example.service.UserService;
import com.example.service.impl.UserServiceImpl;
import com.servlet.SpringApplication;
import com.spring.annotation.ComponentScan;
import com.spring.context.ConfigurableApplicationContext;

/**
 * @author：rkc
 * @date：Created in 2021/5/11 21:29
 * @description：
 */
@ComponentScan("com.example")
public class WebApplication {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context = SpringApplication.run(WebApplication.class, 8081);
        UserController userController = context.getBean("userController", UserController.class);
        UserService userService = context.getBean("userServiceImpl", UserServiceImpl.class);
        System.out.println(userController);
        System.out.println(userController.getUserService());
        System.out.println(userService);
    }
}
