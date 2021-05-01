package com.servlet;

import com.annotation.WebServlet;
import com.http.HttpServlet;
import com.http.HttpServletRequest;
import com.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author：rkc
 * @date：Created in 2021/5/1 14:17
 * @description：
 */
@WebServlet(value = "/book", name = "book")
public class BookServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doPost(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.begin();
        String body = "{ name: '你好', age: 20 }";
        response.getWriter().write(body.getBytes(StandardCharsets.UTF_8));
        response.getWriter().flush();
        response.getWriter().close();
    }
}
