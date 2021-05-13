package com.servlet;

import com.http.AnnotationServerContext;
import com.http.ServerContext;
import com.spring.context.AnnotationConfigApplicationContext;
import com.spring.context.ConfigurableApplicationContext;
import com.spring.support.AbstractApplicationContext;

import java.io.IOException;

/**
 * @author：rkc
 * @date：Created in 2021/5/12 11:10
 * @description：
 */
public class SpringApplication {

    private static AbstractApplicationContext applicationContext;

    public static ConfigurableApplicationContext run(Class<?> clazz) throws IOException {
        return run(clazz, 8080);
    }

    public static ConfigurableApplicationContext run(Class<?> clazz, int port) throws IOException {
        //自动扫描DispatcherServlet，并且将它交给http服务器管理，然后启动服务器
        applicationContext = new AnnotationConfigApplicationContext(clazz);
        ServerContext serverContext = new AnnotationServerContext();
        serverContext.start("com.servlet", port);
        return applicationContext;
    }

    public static AbstractApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
