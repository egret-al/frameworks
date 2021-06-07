package com.ibatis.binding;

import com.ibatis.session.SqlSession;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;

/**
 * @author rkc
 * @date 2021/3/18 20:08
 */
public class MapperProxy implements InvocationHandler, Serializable {

    private final SqlSession sqlSession;

    public MapperProxy(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("toString".equals(method.getName())) {
            return proxy.getClass().getName();
        }
        //得到返回的字节码
        Class<?> returnType = method.getReturnType();
        if (Collection.class.isAssignableFrom(returnType)) {
            //如果这个返回值是集合类型，那就直接转发selectList
            return sqlSession.selectList(method.getDeclaringClass().getName() + "." + method.getName(), args == null ? null: args[0]);
        }
        //返回的是单个对象
        return sqlSession.selectOne(method.getDeclaringClass().getName() + "." + method.getName(), args == null ? null : args[0]);
    }
}
