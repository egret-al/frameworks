package com.test;

import com.ibatis.session.DefaultSqlSessionFactory;
import com.ibatis.session.SqlSession;
import com.ibatis.session.SqlSessionFactory;
import com.test.entity.User;
import com.test.mapper.UserMapper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * @author rkc
 * @date 2021/3/18 20:51
 */
@SuppressWarnings("all")
public class Main  {

    public static void main(String[] args) throws Exception {
        SqlSessionFactory sqlSessionFactory = new DefaultSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSqlSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        List<User> users = userMapper.selectAll();
        System.out.println(users);
    }

    public static <T> T getMapper(Class<?> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{ clazz }, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println(method);
                return null;
            }
        });
    }
}
