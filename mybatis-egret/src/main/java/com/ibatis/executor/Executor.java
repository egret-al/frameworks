package com.ibatis.executor;

import com.ibatis.config.MappedStatement;

import java.sql.SQLException;
import java.util.List;

/**
 * 执行器，由执行器调度StatementHandler、ParameterHandler、ResultSetHandler执行对应的SQL语句
 * mybatis中存在三种执行器：SimpleExecutor、ReuseExecutor、BatchExecutor
 * @author rkc
 * @date 2021/3/18 20:10
 */
public interface Executor {

    <E> List<E> query(MappedStatement ms, Object parameter) throws SQLException;

    int update(MappedStatement ms, Object parameter) throws SQLException;
}
