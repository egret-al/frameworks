package com.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author：rkc
 * @date：Created in 2021/4/26 18:30
 * @description：
 */
public class HttpServletResponseWrapper implements HttpServletResponse {

    private final OutputStream outputStream;
    private ByteArrayOutputStream writer;

    public HttpServletResponseWrapper(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public OutputStream getWriter() {
        return this.outputStream;
    }

    @Override
    public void setContentType(String contentType) throws IOException {
        String type = "Content-Type:" + contentType;
        if (type.contains("\r\n")) {
            outputStream.write(type.getBytes(StandardCharsets.UTF_8));
        } else {
            outputStream.write((type + "\r\n").getBytes(StandardCharsets.UTF_8));
        }
    }

    @Override
    public void begin() throws IOException {
        outputStream.write("\r\n".getBytes(StandardCharsets.UTF_8));
    }
}
