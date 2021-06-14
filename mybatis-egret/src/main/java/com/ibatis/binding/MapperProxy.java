package com.ibatis.binding;

import com.ibatis.config.Configuration;
import com.ibatis.config.MappedStatement;
import com.ibatis.mapping.SqlCommandType;
import com.ibatis.reflection.ParamNameResolver;
import com.ibatis.session.SqlSession;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;

/**
 * @author rkc
 * @date 2021/3/18 20:08
 */
public class MapperProxy<T> implements InvocationHandler, Serializable {

    private static final long serialVersionUID = -8741367193589341729L;
    private final SqlSession sqlSession;
    private final Class<T> mapperInterface;;

    public MapperProxy(SqlSession sqlSession, Class<T> mapperInterface) {
        this.sqlSession = sqlSession;
        this.mapperInterface = mapperInterface;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("toString".equals(method.getName())) {
            return proxy.getClass().getName();
        }
        //通过回调器来间接调用
        return (new PlainMethodInvoker(new MapperMethod(mapperInterface, method, sqlSession.getConfiguration()))).invoke(proxy, method, args, sqlSession);
    }

    interface MapperMethodInvoker {

        Object invoke(Object proxy, Method method, Object[] args, SqlSession sqlSession) throws Throwable;
    }

    private static class PlainMethodInvoker implements MapperMethodInvoker {
        private final MapperMethod mapperMethod;

        public PlainMethodInvoker(MapperMethod mapperMethod) {
            this.mapperMethod = mapperMethod;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args, SqlSession sqlSession) throws Throwable {
            return mapperMethod.execute(sqlSession, args);
        }
    }
}
