package com.ibatis.builder;

import com.ibatis.config.Configuration;
import com.ibatis.util.IgnoreDTDEntityResolver;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * 解析mybatis-config.xml
 * @author：rkc
 * @date：Created in 2021/6/11 15:20
 * @description：
 */
@SuppressWarnings("all")
public class XMLConfigBuilder extends BaseBuilder {

    private final InputStream configInputStream;

    public XMLConfigBuilder(InputStream inputStream) {
        super(new Configuration());
        this.configInputStream = inputStream;
    }

    public Configuration parse() {
        //解析 mybatis-config.xml文件，将配置信息加载到Configuration中
        SAXReader reader = new SAXReader();
        reader.setEntityResolver(new IgnoreDTDEntityResolver());
        try {
            //使用dom4j解析xml
            Document document = reader.read(configInputStream);
            Element configuration = document.getRootElement();
            //简化配置，只有一个默认环境
            Element firstEnvironment = (Element) configuration.element("environments").elements("environment").get(0);
            Element dataSource = firstEnvironment.element("dataSource");
            List<Element> properties = dataSource.elements("property");
            for (Element property : properties) {
                Attribute name = property.attribute("name");
                Attribute value = property.attribute("value");
                super.configuration.addProperty(name.getData().toString(), value.getData().toString());
            }
            //解析mappers标签下的mapper
            List<Element> mappers = configuration.element("mappers").elements("mapper");
            for (Element mapper : mappers) {
                mapperElement(mapper);
            }
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
        return configuration;
    }

    private void mapperElement(Element mapper) throws IOException {
        //使用XMLMapperBuilder解析mapper文件
        String resource = mapper.attribute("resource").getData().toString();
        super.configuration.addMapperLocation(resource);
        InputStream inputStream = Resources.getResourceAsStream(resource);
        XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, resource);
        //解析mapper文件
        mapperParser.parse();
    }
}
