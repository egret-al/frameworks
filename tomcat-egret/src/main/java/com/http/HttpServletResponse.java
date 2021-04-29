package com.http;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author：rkc
 * @date：Created in 2021/4/26 18:30
 * @description：
 */
public interface HttpServletResponse {

    byte[] CODE_200 = "HTTP/1.1 200 OK\r\n".getBytes(StandardCharsets.UTF_8);
    byte[] CONTENT_TYPE_HTML = "Content-Type:text/html;charset=utf-8\r\n".getBytes(StandardCharsets.UTF_8);
    byte[] CONTENT_TYPE_JSON = "Content-Type:application/json;charset=utf-8\r\n".getBytes(StandardCharsets.UTF_8);
    byte[] HEADER_END = "\r\n".getBytes(StandardCharsets.UTF_8);

    OutputStream getWriter();

    /**
     * 每次写入响应体前需要调用以指定类型
     * @param contentType text/html;charset=utf-8或者application/json;charset=utf-8
     * @throws IOException
     */
    void setContentType(String contentType) throws IOException;

    /**
     * 调用后即写入到响应体
     */
    void begin() throws IOException;
}
