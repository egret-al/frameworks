package com.http;

import java.io.IOException;
import java.util.Map;

public interface ServerContext {

    Map<String, HttpServlet> getServletMap();

    Map<String, String> getServletMapping();

    void start(int port) throws IOException;

    void start(Class<?> clazz, int port) throws IOException;

    void start() throws IOException;
}
