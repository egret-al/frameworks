package com.ibatis.session;

import java.util.List;

/**
 * @author rkc
 * @date 2021/3/18 20:12
 */
public interface SqlSession {

    <T> T selectOne(String statement, Object parameter);

    <E> List<E> selectList(String statement, Object parameter);

    <T> T getMapper(Class<T> type);
}
