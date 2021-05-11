package com.example.test;

import com.annotation.ServletComponentScan;
import com.http.AnnotationServerContext;
import com.http.ServerContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author：rkc
 * @date：Created in 2021/4/27 8:41
 * @description：
 */
@ServletComponentScan({"com.example.test", "com.example.servlet"})
public class Main {

    public static void main(String[] args) throws IOException {
        ServerContext context = new AnnotationServerContext();
        context.start(Main.class, 8080);
    }
}
