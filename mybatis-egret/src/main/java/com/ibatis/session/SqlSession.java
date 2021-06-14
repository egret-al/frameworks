package com.ibatis.session;

import com.ibatis.config.Configuration;

import java.util.List;

/**
 * @author rkc
 * @date 2021/3/18 20:12
 */
public interface SqlSession {

    Configuration getConfiguration();

    <T> T selectOne(String statement, Object parameter);

    <E> List<E> selectList(String statement, Object parameter);

    <T> T getMapper(Class<T> type);

    int insert(String statement, Object parameter);

    int update(String statement, Object parameter);

    int delete(String statement, Object parameter);
}
