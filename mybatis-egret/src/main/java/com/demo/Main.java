package com.demo;

import com.demo.entity.Score;
import com.ibatis.builder.Resources;
import com.ibatis.builder.SqlSessionFactoryBuilder;
import com.ibatis.session.DefaultSqlSessionFactory;
import com.ibatis.session.SqlSession;
import com.ibatis.session.SqlSessionFactory;
import com.demo.entity.User;
import com.demo.mapper.UserMapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.*;
import java.sql.*;
import java.util.*;

/**
 * @author rkc
 * @date 2021/3/18 20:51
 */
@SuppressWarnings("all")
public class Main {

    public static void main(String[] args) throws Exception {
        InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        for (User user : userMapper.selectAll2()) {
            System.out.println(user);
        }
    }

    public static <T> T getMapper(Class<?> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println(method);
                return null;
            }
        });
    }
}
