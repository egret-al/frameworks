package com.demo;

import com.demo.controller.IndexController;
import com.demo.service.BookService;
import com.demo.service.IndexService;
import com.demo.service.UserService;
import com.spring.annotation.ComponentScan;
import com.spring.context.AnnotationConfigApplicationContext;

/**
 * @author rkc
 * @date 2021/3/12 13:35
 */
@ComponentScan({"com.demo.service", "com.demo.controller"})
public class Test {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Test.class);
        IndexService indexService = context.getBean("indexService", IndexService.class);

        IndexController indexController = context.getBean("indexController", IndexController.class);
        System.out.println(indexController);
        System.out.println(indexService);
        System.out.println(indexController.getIndexService());
    }
}
