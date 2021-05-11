package com;

import com.annotation.ServletComponentScan;
import com.http.AnnotationServerContext;
import com.http.ServerContext;

import java.io.IOException;

/**
 * @author：rkc
 * @date：Created in 2021/5/11 21:29
 * @description：
 */
@ServletComponentScan("com.controller")
public class SpringApplication {

    public static void main(String[] args) throws IOException {
        ServerContext serverContext = new AnnotationServerContext();
        serverContext.start(SpringApplication.class, 8080);
    }
}
