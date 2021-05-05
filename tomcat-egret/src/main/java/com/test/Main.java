package com.test;

import com.annotation.ServletComponentScan;
import com.handler.HttpServerHandler;
import com.http.HttpServerContext;
import com.http.ServerContext;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.*;

/**
 * @author：rkc
 * @date：Created in 2021/4/27 8:41
 * @description：
 */
@ServletComponentScan({"com.test", "com.servlet"})
public class Main {

    public static void main(String[] args) throws IOException {
        ServerContext context = new HttpServerContext();
        context.start(Main.class, 8080);
    }
}