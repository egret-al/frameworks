package com.demo;

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
        IndexService indexService = context.getBean("indexService", IndexService.class);
        BookService bookService = context.getBean("bookService", BookService.class);
        System.out.println(indexService);
        System.out.println(bookService);
    }
}
