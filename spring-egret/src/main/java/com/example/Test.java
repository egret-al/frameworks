package com.example;

import com.example.controller.IndexController;
import com.example.controller.ShoppingController;
import com.example.controller.UserController;
import com.example.service.impl.BookService;
import com.spring.annotation.ComponentScan;
import com.spring.context.AnnotationConfigApplicationContext;
import com.spring.context.ConfigurableApplicationContext;
import com.spring.support.AbstractApplicationContext;

import java.io.File;
import java.net.URL;

/**
 * @author rkc
 * @date 2021/3/12 13:35
 */
@ComponentScan({"com"})
public class Test {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context = new AnnotationConfigApplicationContext(Test.class);
//        BookService bookService = context.getBean("bookService", BookService.class);
//        System.out.println(bookService.hashCode());
//        System.out.println(bookService.getName());

        UserController userController = context.getBean("userController", UserController.class);
        ShoppingController shoppingController = context.getBean("shoppingController", ShoppingController.class);
        System.out.println(userController);
        System.out.println(userController.getShoppingController());
        System.out.println(shoppingController);
        System.out.println(shoppingController.getUserController());
    }
}
