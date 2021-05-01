package com.parse;

import com.http.HttpServlet;

import java.util.Map;

/**
 * @author：rkc
 * @date：Created in 2021/4/26 18:30
 * @description：
 */
public interface WebConfigParser {

    void servletMapping(String path, Map<String, HttpServlet> servletMap, Map<String, String> servletMapping);
}
