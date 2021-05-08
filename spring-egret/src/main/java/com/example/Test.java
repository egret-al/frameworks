package com.example;

import com.example.controller.IndexController;
import com.example.service.impl.IndexServiceImpl1;
import com.example.test.BeanFactoryPostProcessorImpl;
import com.spring.annotation.ComponentScan;
import com.spring.context.AnnotationConfigApplicationContext;
import com.spring.context.ConfigurableApplicationContext;

import java.lang.reflect.Field;

/**
 * @author rkc
 * @date 2021/3/12 13:35
 */
@ComponentScan("com.example")
public class Test {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = new AnnotationConfigApplicationContext(Test.class);
        IndexController indexController = context.getBean("indexController", IndexController.class);
        System.out.println(indexController);
        System.out.println(indexController.getIndexService());
    }
}
