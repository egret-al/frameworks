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
import java.util.concurrent.*;

/**
 * @author：rkc
 * @date：Created in 2021/4/30 20:02
 * @description：
 */
public class HttpServerContext extends AbstractServerContext {
    private static final ExecutorService EXECUTOR_SERVICE = new ThreadPoolExecutor(5, 10, 10, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(10));

    /* <servlet></servlet>标签， <servlet-name>为key，<servlet-class>为value */
    private static final Map<String, HttpServlet> servletMap = new ConcurrentHashMap<>(256);

    /* <servlet-mapping></servlet-mapping>标签，<url-pattern>为key，<servlet-name>为value */
    private static final Map<String, String> servletMapping = new ConcurrentHashMap<>(256);

    private static ServerSocket serverSocket;

    static {
        String bastPath = HttpServerContext.class.getResource("/").getPath();
        File webXmlFile = new File(bastPath + "web.xml");
        if (webXmlFile.exists()) {
            //xml的方式进行解析
            WebConfigParser webConfigParser = new XmlWebConfigParser();
            webConfigParser.servletMapping(webXmlFile.getPath(), servletMap, servletMapping);
        }
    }

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
        WebConfigParser annotationParser = new AnnotationWebConfigParser();
        //以注解的形式进行解析，然后再以xml的形式进行解析，在解析时会进行判断，因此优先级是注解大于xml
        ServletComponentScan servletComponentScan = clazz.getAnnotation(ServletComponentScan.class);
        if (servletComponentScan != null) {
            for (String packagePath : servletComponentScan.value()) {
                annotationParser.servletMapping(packagePath, servletMap, servletMapping);
            }
        } else {
            System.err.println(clazz.getSimpleName() + "没有标注@ServletComponentScan注解！将跳过注解加载");
        }
        start(port);
    }

    @Override
    public Map<String, HttpServlet> getServletMap() {
        return servletMap;
    }

    @Override
    public Map<String, String> getServletMapping() {
        return servletMapping;
    }
}
