package com.http;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ServerContext {

    Map<String, HttpServlet> getServletMap();

    Map<String, String> getServletMapping();

    void start(int port) throws IOException;

    void start(Class<?> clazz, int port) throws IOException;

    void start(String packagePath, int port) throws IOException;

    void start(List<String> packageList, int port) throws IOException;
}
