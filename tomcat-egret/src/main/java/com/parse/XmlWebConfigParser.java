package com.parse;

import com.http.HttpServlet;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @author：rkc
 * @date：Created in 2021/4/26 18:30
 * @description：解析web.xml文件的servlet
 */
public class XmlWebConfigParser implements WebConfigParser {

    @Override
    public void servletMapping(Map<String, HttpServlet> servletMap, Map<String, String> servletMapping) {
        try {
            String basePath = XmlWebConfigParser.class.getResource("/").getPath();
            //获取解析器
            SAXReader reader = new SAXReader();
            Document document = reader.read(new File(basePath + "web.xml"));
            Element root = document.getRootElement();
            List<Element> childElements = root.elements();
            for (Element element : childElements) {
                //判断元素名称为servlet的元素
                if ("servlet".equals(element.getName())) {
                    //获取servlet—name元素
                    Element servletName = element.element("servlet-name");
                    //获取servlet-class元素
                    Element servletClass = element.element("servlet-class");
                    //将web.xml中的servlet-name和servlet-class值进行存储
                    Class<?> clazz = Class.forName(servletClass.getText());
                    if (!HttpServlet.class.isAssignableFrom(clazz)) {
                        throw new RuntimeException("类" + clazz.getSimpleName() + "没有继承HttpServlet");
                    }
                    servletMap.put(servletName.getText(), (HttpServlet) clazz.newInstance());
                } else if ("servlet-mapping".equals(element.getName())) {
                    //获得servletName元素
                    Element servletName = element.element("servlet-name");
                    //获得urlPattern元素
                    Element urlPattern = element.element("url-pattern");
                    servletMapping.put(urlPattern.getText(), servletName.getText());
                }
            }
        } catch (ClassNotFoundException | DocumentException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
