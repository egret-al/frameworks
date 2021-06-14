package com.ibatis.builder;

import com.ibatis.config.Configuration;
import com.ibatis.session.DefaultSqlSessionFactory;
import com.ibatis.session.SqlSessionFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author：rkc
 * @date：Created in 2021/6/12 14:15
 * @description：
 */
public class SqlSessionFactoryBuilder {

    public SqlSessionFactory build(InputStream inputStream) {
        try {
            XMLConfigBuilder parser = new XMLConfigBuilder(inputStream);
            return build(parser.parse());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public SqlSessionFactory build(Configuration configuration) {
        return new DefaultSqlSessionFactory(configuration);
    }
}
