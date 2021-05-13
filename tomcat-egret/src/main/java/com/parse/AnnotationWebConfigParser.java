package com.parse;

import com.annotation.WebServlet;
import com.http.HttpServlet;
import com.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 以注解的形式解析，将会扫描@ServletComponentScan注解指定的包，对包下仅标注了@WebServlet
 * 注解的类进行加载
 * @author：rkc
 * @date：Created in 2021/4/26 18:30
 * @description：
 */
public class AnnotationWebConfigParser implements WebConfigParser {

    @Override
    public void servletMapping(String path, Map<String, HttpServlet> servletMap, Map<String, String> servletMapping) {
        path = path.replace(".", "/");          //得到包路径
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        //通过类加载器来加载指定目录下的字节码文件
        URL url = classLoader.getResource(path);
        String protocol = url.getProtocol();
        if ("jar".equalsIgnoreCase(protocol)) {
            doScanJar(servletMap, servletMapping, url, path);
            return;
        }
        File directory = new File(url.getFile());
        if (directory.isDirectory()) {
            for (File file : Objects.requireNonNull(directory.listFiles())) {
                String absolutePath = file.getAbsolutePath();
                absolutePath = absolutePath.substring(absolutePath.indexOf("classes") + ("classes".length() + 1), absolutePath.indexOf(".class"))
                        .replace("\\", ".");
                try {
                    //类加载器进行加载字节码文件
                    Class<?> clazz = classLoader.loadClass(absolutePath);
                    //如果该类存在@WebServlet注解且继承了HttpServlet，则进行加载
                    loadServlet(servletMap, servletMapping, clazz);
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void loadServlet(Map<String, HttpServlet> servletMap, Map<String, String> servletMapping, Class<?> clazz) throws InstantiationException, IllegalAccessException {
        if (clazz.isAnnotationPresent(WebServlet.class) && HttpServlet.class.isAssignableFrom(clazz)) {
            WebServlet webServlet = clazz.getAnnotation(WebServlet.class);
            String name = webServlet.name();
            String urlPath = webServlet.value();
            if (name.isEmpty()) {
                name = StringUtils.toLowerCaseFirstOne(clazz.getSimpleName());
            }
            if (urlPath.isEmpty()) {
                throw new RuntimeException(name + "没有合法的url路径");
            }
            if (!servletMap.containsKey(clazz.getSimpleName()) && !servletMapping.containsKey(urlPath)) {
                servletMap.put(name, (HttpServlet) clazz.newInstance());
                servletMapping.put(urlPath, name);
            }
        }
    }

    private void doScanJar(Map<String, HttpServlet> servletMap, Map<String, String> servletMapping, URL url, String basePack) {
        try {
            //转换为JarURLConnection
            JarURLConnection connection = (JarURLConnection) url.openConnection();
            if (connection != null) {
                JarFile jarFile = connection.getJarFile();
                if (jarFile != null) {
                    //得到该jar下面的类实体
                    Enumeration<JarEntry> entries = jarFile.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry jarEntry = entries.nextElement();
                        String jarEntryName = jarEntry.getName();
                        //这里我们需要过滤不是class文件和不在basePack包名下的类
                        if (jarEntryName.contains(".class") && jarEntryName.startsWith(basePack)) {
                            String className = jarEntryName.substring(0, jarEntryName.lastIndexOf(".")).replace("/", ".");
                            Class<?> clazz = Class.forName(className);
                            loadServlet(servletMap, servletMapping, clazz);
                        }
                    }
                }
            }
        } catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
