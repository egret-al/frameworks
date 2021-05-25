package com.spring.annotation;

import com.spring.context.AnnotationConfigApplicationContext;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author rkc
 * @date 2021/3/12 17:54
 */
public class ClassPathBeanDefinitionScanner {

    /**
     * 根据传入的配置类（被@ComponentScan标注的配置类）
     * @param configClass 被@ComponentScan标注的类字节码
     * @return 对应路径下所有的字节码
     */
    public Set<Class<?>> scan(Class<?> configClass) {
        Set<Class<?>> classSet = new HashSet<>();
        //扫描，得到字节码
        if (!configClass.isAnnotationPresent(ComponentScan.class)) {
            throw new RuntimeException("没有配置指定扫描包路径");
        }
        ComponentScan componentScan = configClass.getAnnotation(ComponentScan.class);
        if (componentScan.value().length == 0) {
            throw new RuntimeException("没有指定包路径！");
        }
        for (String packagePath : componentScan.value()) {
            doScan(classSet, packagePath.replace(".", "/"));
        }
        return classSet;
    }

    public void doScan(Set<Class<?>> classSet, String path) {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            String classPath = classLoader.getResource("") + path;
            URL url = new URL(classPath);
            //如果是jar包就进行对jar包扫描
            if ("jar".equalsIgnoreCase(url.getProtocol())) {
                doScanJar(classSet, url, path);
                return;
            }
            File directory = new File(Objects.requireNonNull(url).getFile());
            for (File file : Objects.requireNonNull(directory.listFiles())) {
                if (file.isDirectory()) {
                    //递归进行读取
                    doScan(classSet, path + "/" + file.getName());
                } else {
                    String absolutePath = file.getAbsolutePath();
                    absolutePath = absolutePath.substring(absolutePath.indexOf("classes") + ("classes".length() + 1), absolutePath.indexOf(".class"))
                            .replace("\\", ".");
                    Class<?> clazz = classLoader.loadClass(absolutePath);
                    if (clazz.isAnnotationPresent(Component.class) || clazz.isAnnotationPresent(Service.class)
                            || clazz.isAnnotationPresent(Repository.class) || clazz.isAnnotationPresent(Controller.class)) {
                        classSet.add(clazz);
                    }
                }
            }
        } catch (MalformedURLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 对jar包的扫描
     * @param classSet
     * @param url
     * @param basePack
     */
    private void doScanJar(Set<Class<?>> classSet, URL url, String basePack) {
        try {
            //转换为JarURLConnection
            JarURLConnection connection = (JarURLConnection) url.openConnection();
            if (connection != null && connection.getJarFile() != null) {
                JarFile jarFile = connection.getJarFile();
                //得到该jar下面的类实体
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry jarEntry = entries.nextElement();
                    String jarEntryName = jarEntry.getName();
                    //这里我们需要过滤不是class文件和不在basePack包名下的类
                    if (jarEntryName.contains(".class") && jarEntryName.startsWith(basePack)) {
                        String className = jarEntryName.substring(0, jarEntryName.lastIndexOf(".")).replace("/", ".");
                        Class<?> clazz = Class.forName(className);
                        if (clazz.isAnnotationPresent(Component.class) || clazz.isAnnotationPresent(Service.class)
                                || clazz.isAnnotationPresent(Repository.class) || clazz.isAnnotationPresent(Controller.class)) {
                            classSet.add(clazz);
                        }
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
