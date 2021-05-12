package com.http;

import com.handler.HttpServerHandler;
import com.parse.AnnotationWebConfigParser;
import com.parse.WebConfigParser;
import com.parse.XmlWebConfigParser;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * @author：rkc
 * @date：Created in 2021/5/1 11:03
 * @description：
 */
public abstract class AbstractServerContext implements ServerContext {

    protected static final ExecutorService EXECUTOR_SERVICE = new ThreadPoolExecutor(5, 10, 10, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(10));
    /* <servlet></servlet>标签， <servlet-name>为key，<servlet-class>为value */
    private static final Map<String, HttpServlet> servletMap = new ConcurrentHashMap<>(256);
    /* <servlet-mapping></servlet-mapping>标签，<url-pattern>为key，<servlet-name>为value */
    private static final Map<String, String> servletMapping = new ConcurrentHashMap<>(256);
    /* ServerSocket */
    protected static ServerSocket serverSocket;
    /* 解析器 */
    private final WebConfigParser annotationParser = new AnnotationWebConfigParser();

    static {
        String bastPath = Objects.requireNonNull(AnnotationServerContext.class.getResource("/")).getPath();
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
        EXECUTOR_SERVICE.execute(() -> {
            System.err.printf("The server is listening on port %d...\n", port);
            while (true) {
                //进行监听，一旦有请求，就交给线程池处理
                try {
                    Socket socket = serverSocket.accept();
                    HttpServerHandler httpServerHandler = new HttpServerHandler(this, socket);
                    EXECUTOR_SERVICE.execute(httpServerHandler);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void start(String packagePath, int port) throws IOException {
        annotationParser.servletMapping(packagePath, servletMap, servletMapping);
        start(port);
    }

    @Override
    public void start(List<String> packageList, int port) throws IOException {
        for (String packagePath : packageList) {
            annotationParser.servletMapping(packagePath, servletMap, servletMapping);
        }
        start(port);
    }

    public WebConfigParser getAnnotationParser() {
        return annotationParser;
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
