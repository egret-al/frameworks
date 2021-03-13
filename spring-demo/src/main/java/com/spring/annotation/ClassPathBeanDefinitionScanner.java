package com.spring.annotation;

import com.spring.context.AnnotationConfigApplicationContext;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author rkc
 * @date 2021/3/12 17:54
 */
public class ClassPathBeanDefinitionScanner {

    public Set<Class<?>> scan(Class<?> configClass) {
        Set<Class<?>> classSet = new HashSet<>();
        //扫描，得到字节码
        if (!configClass.isAnnotationPresent(ComponentScan.class)) {
            throw new RuntimeException("没有配置指定扫描包路径");
        }
        ComponentScan componentScan = configClass.getAnnotation(ComponentScan.class);
        String path = componentScan.value().replace(".", "/");                //得到包路径

        //通过类加载器得到Class
        ClassLoader classLoader = AnnotationConfigApplicationContext.class.getClassLoader();
        URL url = classLoader.getResource(path);
        File directory = new File(url.getFile());
        if (directory.isDirectory()) {
            for (File file : directory.listFiles()) {
                String absolutePath = file.getAbsolutePath();
                absolutePath = absolutePath.substring(absolutePath.indexOf("classes") + ("classes".length() + 1), absolutePath.indexOf(".class"))
                        .replace("\\", ".");
                try {
                    Class<?> clazz = classLoader.loadClass(absolutePath);
                    classSet.add(clazz);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return classSet;
    }
}
