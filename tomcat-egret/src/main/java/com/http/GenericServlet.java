package com.http;

import java.io.IOException;

/**
 * @author：rkc
 * @date：Created in 2021/4/26 18:30
 * @description：
 */
public interface GenericServlet {

    void init();

    void service(HttpServletRequest request, HttpServletResponse response) throws IOException;

    void destroy();
}
