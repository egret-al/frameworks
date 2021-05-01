package com.parse;

import com.annotation.WebServlet;
import com.http.HttpServlet;
import com.util.StringUtils;

import java.io.File;
import java.net.URL;
import java.util.Map;

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
        ClassLoader classLoader = AnnotationWebConfigParser.class.getClassLoader();
        //通过类加载器来加载指定目录下的字节码文件
        URL url = classLoader.getResource(path);
        File directory = new File(url.getFile());
        if (directory.isDirectory()) {
            for (File file : directory.listFiles()) {
                String absolutePath = file.getAbsolutePath();
                absolutePath = absolutePath.substring(absolutePath.indexOf("classes") + ("classes".length() + 1), absolutePath.indexOf(".class"))
                        .replace("\\", ".");
                try {
                    //类加载器进行加载字节码文件
                    Class<?> clazz = classLoader.loadClass(absolutePath);
                    //如果该类存在@WebServlet注解且继承了HttpServlet，则进行加载
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
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
