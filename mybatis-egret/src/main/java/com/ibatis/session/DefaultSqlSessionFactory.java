package com.ibatis.session;

import com.ibatis.config.Configuration;
import com.ibatis.config.MappedStatement;
import com.ibatis.mapping.ResultMap;
import com.ibatis.mapping.ResultMapping;
import com.ibatis.mapping.SqlCommandType;
import com.ibatis.util.IgnoreDTDEntityResolver;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author rkc
 * @date 2021/3/18 20:29
 */
@SuppressWarnings("all")
public class DefaultSqlSessionFactory implements SqlSessionFactory {

    private final Configuration configuration;

    public DefaultSqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public SqlSession openSession() {
        return new DefaultSqlSession(configuration, configuration.newExecutor(ExecutorType.SIMPLE));
    }
}
