package com.test;

import com.annotation.WebServlet;
import com.http.HttpServlet;
import com.http.HttpServletRequest;
import com.http.HttpServletResponse;
import com.util.StreamUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author：rkc
 * @date：Created in 2021/4/26 19:53
 * @description：
 */
@WebServlet(value = "/index")
public class IndexServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=utf-8");
        OutputStream writer = response.getWriter();
        response.begin();
        String basePath = IndexServlet.class.getResource("/").getPath();
        File file = new File(basePath + "index.html");
        if (file.exists()) {
            byte[] bytes = StreamUtils.inputStreamToBytes(new FileInputStream(file));
            writer.write(bytes);
        }
        response.getWriter().flush();
        response.getWriter().close();
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.begin();
        String body = "{ name: '李四', age: 20 }";
        response.getWriter().write(body.getBytes(StandardCharsets.UTF_8));
        response.getWriter().flush();
        response.getWriter().close();
    }
}
