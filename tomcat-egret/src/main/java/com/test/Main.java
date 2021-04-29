package com.test;

import com.handler.HttpServerHandler;

import java.util.Scanner;
import java.util.concurrent.*;

/**
 * @author：rkc
 * @date：Created in 2021/4/27 8:41
 * @description：
 */
public class Main {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        HttpServerHandler.run(8080);
    }
}
