package com.demo;

import com.demo.config.ApplicationConfig;
import com.demo.service.UserService;
import com.spring.context.AnnotationConfigApplicationContext;

/**
 * @author rkc
 * @date 2021/3/12 13:35
 */
public class Test {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfig.class);
        System.out.println(context.getBean("userService", UserService.class).getIndexService());
        System.out.println(context.getBean("userService", UserService.class).getBeanName());
    }
}
