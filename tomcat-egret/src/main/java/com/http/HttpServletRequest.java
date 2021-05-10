package com.http;

import java.util.Map;

/**
 * @author：rkc
 * @date：Created in 2021/4/26 18:30
 * @description：
 */
public interface HttpServletRequest {

    String REQUEST_TYPE = "REQUEST_TYPE";
    String URI = "URI";
    String PROTOCOL = "PROTOCOL";

    String getHeader(String name);

    String getMethod();

    String getContextPath();

    String getQueryString();

    String getRemoteUser();

    String getRequestURI();

    void addHeader(String key, String value);

    String getParameter(String name);

    String getParameter(String name, String defaultValue);

    byte[] getRequestBody();
}
