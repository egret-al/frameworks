package com.controller;

import com.annotation.WebServlet;
import com.http.HttpServlet;
import com.http.HttpServletRequest;
import com.http.HttpServletResponse;

import java.io.IOException;

/**
 * @author：rkc
 * @date：Created in 2021/5/11 21:30
 * @description：
 */
@WebServlet("/*")
public class UserController extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        httpServletResponse.setContentType("text/html;charset=utf-8");
        System.out.println("111");
    }

    @Override
    public void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        httpServletResponse.setContentType("text/html;charset=utf-8");
        System.out.println("222");
    }
}
