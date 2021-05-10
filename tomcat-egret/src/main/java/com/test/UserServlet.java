package com.test;

import com.annotation.WebServlet;
import com.handler.HttpServerHandler;
import com.http.HttpServlet;
import com.http.HttpServletRequest;
import com.http.HttpServletResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

@WebServlet("/user")
public class UserServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        OutputStream writer = response.getWriter();
        String name = request.getParameter("name");
        String age = request.getParameter("age");
        System.out.println(name + "  " + age);

        String json = "{" +
                "name: '张三'," +
                "age: 18," +
                "sex: '男'}";
        response.begin();
        writer.write(json.getBytes(StandardCharsets.UTF_8));
        writer.flush();
        writer.close();
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        System.out.println("post");
        response.getWriter().flush();
        response.getWriter().close();
    }
}
