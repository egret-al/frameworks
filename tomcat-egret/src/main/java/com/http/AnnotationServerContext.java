package com.http;

import com.annotation.ServletComponentScan;
import com.handler.HttpServerHandler;
import com.parse.AnnotationWebConfigParser;
import com.parse.WebConfigParser;
import com.parse.XmlWebConfigParser;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * @author：rkc
 * @date：Created in 2021/4/30 20:02
 * @description：
 */
public class AnnotationServerContext extends AbstractServerContext {

    @Override
    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.printf("服务器在%d端口启动...\n", port);
        while (true) {
            //进行监听，一旦有请求，就交给线程池处理
            Socket socket = serverSocket.accept();
            HttpServerHandler httpServerHandler = new HttpServerHandler(this, socket);
            EXECUTOR_SERVICE.execute(httpServerHandler);
        }
    }

    @Override
    public void start(Class<?> clazz, int port) throws IOException {
        //以注解的形式进行解析，然后再以xml的形式进行解析，在解析时会进行判断，因此优先级是注解大于xml
        ServletComponentScan servletComponentScan = clazz.getAnnotation(ServletComponentScan.class);
        if (servletComponentScan != null) {
            for (String packagePath : servletComponentScan.value()) {
                getAnnotationParser().servletMapping(packagePath, getServletMap(), getServletMapping());
            }
        } else {
            System.err.println(clazz.getSimpleName() + "没有标注@ServletComponentScan注解！将跳过注解加载");
        }
        start(port);
    }
}
