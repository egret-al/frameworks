package com.ibatis.session;

import com.ibatis.binding.MapperProxy;
import com.ibatis.config.Configuration;
import com.ibatis.config.MappedStatement;
import com.ibatis.executor.Executor;
import com.ibatis.executor.SimpleExecutor;

import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author rkc
 * @date 2021/3/18 20:12
 */
@SuppressWarnings("all")
public class DefaultSqlSession implements SqlSession {

    private final Configuration configuration;
    private final Executor executor;

    public DefaultSqlSession(Configuration configuration, Executor executor) {
        this.configuration = configuration;
        this.executor = executor;
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public <T> T selectOne(String statement, Object parameter) {
        List<Object> selectList = this.selectList(statement, parameter);
        if (selectList == null || selectList.size() == 0) {
            return null;
        }
        if (selectList.size() == 1) {
            return (T) selectList.get(0);
        } else {
            throw new RuntimeException("返回结果过多！");
        }
    }

    @Override
    public <E> List<E> selectList(String statement, Object parameter) {
        MappedStatement mappedStatement = configuration.getMappedStatements().get(statement);
        try {
            //通过执行器执行查询sql操作
            return executor.query(mappedStatement, parameter);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    public <T> T getMapper(Class<T> mapperInterface) {
        return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[]{ mapperInterface },  new MapperProxy<T>(this, mapperInterface));
    }

    @Override
    public int insert(String statement, Object parameter) {
        return update(statement, parameter);
    }

    @Override
    public int update(String statement, Object parameter) {
        MappedStatement ms = configuration.getMappedStatements().get(statement);
        try {
            return executor.update(ms, parameter);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return -1;
    }

    @Override
    public int delete(String statement, Object parameter) {
        return update(statement, parameter);
    }
}
