package com.demo;

import com.demo.config.ApplicationConfig;
import com.demo.service.BookService;
import com.demo.service.IndexService;
import com.demo.service.UserService;
import com.spring.annotation.ComponentScan;
import com.spring.context.AnnotationConfigApplicationContext;

/**
 * @author rkc
 * @date 2021/3/12 13:35
 */
@ComponentScan("com.demo.service")
public class Test {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Test.class);
        UserService userService = context.getBean("userService", UserService.class);
        IndexService indexService = context.getBean("indexService", IndexService.class);
        BookService bookService = context.getBean("bookService", BookService.class);
        System.out.println("userService: " + userService + "   userService.indexService: " + userService.getIndexService());
        System.out.println("userService: " + userService + "   userService.bookService: " + userService.getBookService());
        System.out.println("bookService: " + bookService + "   bookService.userService: " + bookService.getUserService());
        System.out.println("indexService: " + indexService + "   indexService.UserService: " + indexService.getUserService());
    }
}
