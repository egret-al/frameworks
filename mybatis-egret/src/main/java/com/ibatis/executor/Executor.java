package com.ibatis.executor;

import com.ibatis.config.MappedStatement;

import java.util.List;

/**
 * @author rkc
 * @date 2021/3/18 20:10
 */
public interface Executor {

    <E> List<E> query(MappedStatement ms, Object parameter);
}
