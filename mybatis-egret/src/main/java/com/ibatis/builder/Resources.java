package com.ibatis.builder;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author：rkc
 * @date：Created in 2021/6/12 15:20
 * @description：
 */
public class Resources {

    private static final ClassLoader classLoader = ClassLoader.getSystemClassLoader();

    public static InputStream getResourceAsStream(String resource) throws IOException {
        return classLoader.getResourceAsStream(resource);
    }
}
