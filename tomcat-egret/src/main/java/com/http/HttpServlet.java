package com.http;

import com.handler.UnknownRequestType;

import java.io.IOException;

/**
 * @author：rkc
 * @date：Created in 2021/4/26 18:30
 * @description：适配GenericServlet，可以根据自己的选择来进行选择性覆盖方法
 */
public abstract class HttpServlet implements GenericServlet {

    @Override
    public void init() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (HttpRequestType.GET.name().equals(request.getMethod())) {
            doGet(request, response);
        } else if (HttpRequestType.POST.name().equals(request.getMethod())){
            doPost(request, response);
        } else {
            UnknownRequestType unknownRequestType = new UnknownRequestType();
            unknownRequestType.handle(response);
        }
    }

    public abstract void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException;

    public abstract void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
