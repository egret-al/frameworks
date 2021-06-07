package com.ibatis.session;

import com.ibatis.config.Configuration;
import com.ibatis.config.MappedStatement;
import com.ibatis.util.IgnoreDTDEntityResolver;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Properties;

/**
 * @author rkc
 * @date 2021/3/18 20:29
 */
@SuppressWarnings("all")
public class DefaultSqlSessionFactory implements SqlSessionFactory {

    private final Configuration configuration = new Configuration();
    public static final String MAPPER_CONFIG_LOCATION = "mappers";
    public static final String DB_CONFIG_FILE = "mybatis.properties";

    public DefaultSqlSessionFactory() {
        parserConfiguration();
        loadMappedStatements();
    }

    @Override
    public SqlSession openSqlSession() {
        return new DefaultSqlSession(configuration);
    }

    private void loadMappedStatements() {
        URL resource = DefaultSqlSessionFactory.class.getClassLoader().getResource(MAPPER_CONFIG_LOCATION);
        File mappers = new File(resource.getFile());
        if (mappers.isDirectory()) {
            File[] files = mappers.listFiles();
            for (File file : files) {
                loadMappedStatementInfo(file);
            }
        }
    }

    private void loadMappedStatementInfo(File file) {
        SAXReader reader = new SAXReader();
        //不解析DTD文件，避免网络请求
        reader.setEntityResolver(new IgnoreDTDEntityResolver());
        Document document = null;
        try {
            document = reader.read(file);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        Element root = document.getRootElement();
        String namespace = root.attribute("namespace").getData().toString();
        List<Element> select = root.elements("select");
        for (Element element : select) {
            MappedStatement mappedStatement = new MappedStatement();
            String id = element.attribute("id").getData().toString();
            String resultType = element.attribute("resultType").getData().toString();
            String sql = element.getData().toString();
            String sourceId = namespace + "." + id;

            mappedStatement.setSourceId(sourceId);
            mappedStatement.setResultType(resultType);
            mappedStatement.setSql(sql);
            mappedStatement.setNamespace(namespace);

            configuration.getMappedStatements().put(sourceId, mappedStatement);
        }
    }

    private void parserConfiguration() {
        InputStream inputStream = DefaultSqlSessionFactory.class.getClassLoader().getResourceAsStream(DB_CONFIG_FILE);
        Properties properties = new Properties();
        try {
            properties.load(inputStream);
            configuration.setJdbcDriver(properties.get("jdbc.driver").toString());
            configuration.setJdbcUrl(properties.get("jdbc.url").toString());
            configuration.setJdbcUsername(properties.get("jdbc.username").toString());
            configuration.setJdbcPassword(properties.get("jdbc.password").toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
