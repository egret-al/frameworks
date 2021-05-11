package com.handler;

import com.http.*;
import com.parse.WebConfigParser;
import com.parse.XmlWebConfigParser;
import com.util.StringUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author：rkc
 * @date：Created in 2021/4/26 18:30
 * @description：
 */
public class HttpServerHandler implements Runnable {

    private final Socket socket;
    private final ServerContext serverContext;

    public HttpServerHandler(ServerContext serverContext, Socket socket) {
        this.serverContext = serverContext;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            //封装为HttpServletRequest
            HttpServletRequest request = new HttpServletRequestWrapper(socket.getInputStream());
            //封装HttpServletResponse
            HttpServletResponse response = new HttpServletResponseWrapper(socket.getOutputStream());

            HttpServlet servlet = getServlet(request.getRequestURI());
            if (servlet == null) {
                response.getWriter().write("HTTP/1.1 404 OK\r\n".getBytes(StandardCharsets.UTF_8));
                ResponseErrorHandler responseErrorHandler = new UrlResponseErrorHandler();
                responseErrorHandler.handle(response);
                return;
            }
            response.getWriter().write(HttpServletResponse.CODE_200);
            servlet.service(request, response);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                //关闭socket，否则客户端会一直阻塞
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 从给定的uri匹配一个servlet
     * @param uri 精确的uri
     * @return 能够调用的servlet
     */
    private HttpServlet getServlet(String uri) {
        for (Map.Entry<String, String> entry : serverContext.getServletMapping().entrySet()) {
            //进行模糊匹配，如果能够匹配就获取servlet的name然后得到servlet
            if (StringUtils.uriMatch(uri, entry.getKey())) {
                System.out.println(uri + "  " + entry.getValue());
                return serverContext.getServletMap().get(entry.getValue());
            }
        }
        //匹配失败
        return null;
    }
}
