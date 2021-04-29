package com.handler;

import com.http.HttpServletResponse;

import java.io.IOException;

/**
 * @author：rkc
 * @date：Created in 2021/4/26 18:30
 * @description：
 */
public interface ResponseErrorHandler {

    void handle(HttpServletResponse response) throws IOException;
}
