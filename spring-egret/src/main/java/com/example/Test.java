package com.example;

import com.example.controller.IndexController;
import com.example.service.IndexService;
import com.example.service.impl.IndexServiceImpl1;
import com.example.service.impl.IndexServiceImpl2;
import com.spring.annotation.ComponentScan;
import com.spring.context.AnnotationConfigApplicationContext;
import com.spring.context.ConfigurableApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rkc
 * @date 2021/3/12 13:35
 */
@ComponentScan({"com.example.controller", "com.example.service"})
public class Test {

    public static void main(String[] args) {
        List<String> packagePathList = new ArrayList<>();
        packagePathList.add("com.example.controller");
        packagePathList.add("com.example.service");
        ConfigurableApplicationContext context = new AnnotationConfigApplicationContext(packagePathList);
        IndexController indexController = context.getBean("indexController", IndexController.class);
        System.out.println(indexController);
//        ConfigurableApplicationContext context = new AnnotationConfigApplicationContext(Test.class);
//        IndexController indexController = context.getBean("indexController", IndexController.class);
//        System.out.println(indexController);
//        System.out.println(indexController.getIndexService());
    }
}
