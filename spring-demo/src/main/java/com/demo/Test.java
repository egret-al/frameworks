package com.demo;

import com.demo.service.BookService;
import com.demo.service.IndexService;
import com.demo.service.UserService;
import com.spring.annotation.*;
import com.spring.config.BeanDefinition;
import com.spring.context.AnnotationConfigApplicationContext;
import com.spring.resolver.AnnotationParser;
import com.spring.resolver.ComponentAnnotationParser;
import com.spring.resolver.RepositoryAnnotationParser;
import com.spring.resolver.ServiceAnnotationParser;
import com.spring.support.RootBeanDefinition;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

/**
 * @author rkc
 * @date 2021/3/12 13:35
 */
@ComponentScan("com.demo.service")
public class Test {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Test.class);
        System.out.println(context.getBean("userService", UserService.class));
    }
}
