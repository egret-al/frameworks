package com.ibatis.session;

import com.ibatis.binding.MapperProxy;
import com.ibatis.config.Configuration;
import com.ibatis.config.MappedStatement;
import com.ibatis.executor.Executor;
import com.ibatis.executor.SimpleExecutor;
import com.test.mapper.UserMapper;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.List;

/**
 * @author rkc
 * @date 2021/3/18 20:12
 */
@SuppressWarnings("all")
public class DefaultSqlSession implements SqlSession {

    private final Configuration configuration;
    private final Executor executor;

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
        this.executor = new SimpleExecutor(configuration);
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
        //通过执行器执行查询sql操作
        return executor.query(mappedStatement, parameter);
    }

    @Override
    public <T> T getMapper(Class<T> mapperInterface) {
        return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[]{ mapperInterface },  new MapperProxy(this));
    }
}
