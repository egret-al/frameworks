package com.http;

import com.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author：rkc
 * @date：Created in 2021/4/26 18:30
 * @description：
 */
public class HttpServletRequestWrapper implements HttpServletRequest {

    private HttpRequestType httpRequestType;
    private final Map<String, String> header = new ConcurrentHashMap<>();
    private final Map<String, String> parameters = new ConcurrentHashMap<>();
    private byte[] requestBody;

    public HttpServletRequestWrapper(InputStream inputStream) {
        try {
            //解析输入流
            parseRequestHeader(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getString(InputStream inputStream) throws IOException {
        StringBuilder builder = new StringBuilder();
        InputStreamReader reader = new InputStreamReader(inputStream);
        char[] charBuffer = new char[1024];
        int mark;
        while ((mark = reader.read(charBuffer)) != -1) {
            builder.append(charBuffer, 0, mark);
            if (mark < charBuffer.length) {
                break;
            }
        }
        return builder.toString();
    }

    /**
     * 将请求头输入流进行解析
     *
     * @param inputStream 输入流，包含了http的请求信息
     * @throws IOException IOException
     */
    private void parseRequestHeader(InputStream inputStream) throws IOException {
        //转为字节数组进行处理
        String request = getString(inputStream);
        if (request.isEmpty()) {
            return;
        }
        String[] headAndBody = request.split("\r\n\r\n");
        String[] requestHeader = headAndBody[0].split("\r\n");
        if (headAndBody.length == 2) {
            //存在请求体的post请求
            this.requestBody = headAndBody[1].getBytes(StandardCharsets.UTF_8);
        }
        //解析请求类型
        int oneLineFirstSpaceIndex = requestHeader[0].indexOf(" ");
        int oneLineLastSpaceIndex = requestHeader[0].lastIndexOf(" ");
        String requestType = requestHeader[0].substring(0, oneLineFirstSpaceIndex);
        String uri = requestHeader[0].substring(oneLineFirstSpaceIndex + 1, oneLineLastSpaceIndex);
        //对请求参数的处理
        if (uri.contains("?")) {
            int index = uri.indexOf("?");
            String parameterStr = uri.substring(index + 1);
            for (String items : parameterStr.split("&")) {
                String[] split = items.split("=");
                this.parameters.put(split[0], split[1]);
            }
            uri = uri.substring(0, index);
        }
        String protocol = requestHeader[0].substring(oneLineLastSpaceIndex + 1);

        header.put(HttpServletRequest.REQUEST_TYPE, requestType);
        header.put(HttpServletRequest.PROTOCOL, protocol);
        header.put(HttpServletRequest.URI, uri);
        if (HttpRequestType.GET.name().equals(requestType)) {
            httpRequestType = HttpRequestType.GET;
        } else if (HttpRequestType.POST.name().equals(requestType)) {
            httpRequestType = HttpRequestType.POST;
        } else if (HttpRequestType.DELETE.name().equals(requestType)) {
            httpRequestType = HttpRequestType.DELETE;
        } else if (HttpRequestType.PUT.name().equals(requestType)) {
            httpRequestType = HttpRequestType.PUT;
        }

        for (int i = 1; i < requestHeader.length; i++) {
            header.put(requestHeader[i].substring(0, requestHeader[i].indexOf(": ")), requestHeader[i].substring(requestHeader[i].indexOf(": ") + 2));
        }
    }

    @Override
    public void addHeader(String key, String value) {
        header.put(key, value);
    }

    @Override
    public String getParameter(String name) {
        return parameters.get(name);
    }

    /**
     * 如果参数不存在则返回指定的默认值
     *
     * @param name         参数名
     * @param defaultValue 默认值
     * @return 参数结果
     */
    @Override
    public String getParameter(String name, String defaultValue) {
        String parameter = getParameter(name);
        if (parameter == null || parameter.isEmpty()) {
            return defaultValue;
        }
        return parameter;
    }

    @Override
    public byte[] getRequestBody() {
        return this.requestBody;
    }

    @Override
    public String getHeader(String name) {
        return this.header.get(name);
    }

    @Override
    public String getMethod() {
        return httpRequestType.name();
    }

    @Override
    public String getContextPath() {
        return header.get(HttpServletRequest.URI);
    }

    @Override
    public String getQueryString() {
        return null;
    }

    @Override
    public String getRemoteUser() {
        return null;
    }

    @Override
    public String getRequestURI() {
        return header.get(HttpServletRequest.URI);
    }
}
