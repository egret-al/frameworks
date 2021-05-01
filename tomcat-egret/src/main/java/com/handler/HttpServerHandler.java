package com.handler;

import com.http.*;
import com.parse.WebConfigParser;
import com.parse.XmlWebConfigParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
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

            //根据uri得到一个servletName
            String uri = request.getRequestURI();
            if (uri != null && !uri.isEmpty()) {
                String servletName = serverContext.getServletMapping().get(uri);
                if (servletName != null && !servletName.isEmpty()) {
                    //根据servletName得到servlet
                    HttpServlet httpServlet = serverContext.getServletMap().get(servletName);
                    if (httpServlet != null) {
                        //写入成功的响应头
                        response.getWriter().write(HttpServletResponse.CODE_200);
                        //能够找到对应的servlet进行处理
                        httpServlet.service(request, response);
                    } else {
                        //映射路径出现错误，交由对应的错误处理器进行执行
                        response.getWriter().write("HTTP/1.1 200 OK\r\n".getBytes(StandardCharsets.UTF_8));
                        ResponseErrorHandler urlError = new UrlResponseErrorHandler();
                        urlError.handle(response);
                    }
                }
            } else {
                socket.getOutputStream().write("HTTP/1.1 200 OK\r\nContent-Type:text/html;charset=utf-8\r\n\r\n".getBytes(StandardCharsets.UTF_8));
            }
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
}
