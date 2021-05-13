package com.example;

import com.example.controller.IndexController;
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
        IndexController indexController = context.getBean("indexController", IndexController.class);
        System.out.println(indexController);
        System.out.println(indexController.getIndexService());
    }
}
