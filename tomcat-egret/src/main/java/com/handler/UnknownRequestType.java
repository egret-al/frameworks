package com.handler;

import com.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author：rkc
 * @date：Created in 2021/4/26 18:39
 * @description：
 */
public class UnknownRequestType implements ResponseErrorHandler {
    @Override
    public void handle(HttpServletResponse response) throws IOException {

    }
}
