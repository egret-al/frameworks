package com.ibatis.executor.statement;

import com.ibatis.executor.parameter.ParameterHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * 使用数据库的Statement，例如PreparedStatement来完成操作
 *
 * @author：rkc
 * @date：Created in 2021/6/12 15:36
 * @description：
 */
public interface StatementHandler {

    Statement prepare(Connection connection, Integer transactionTimeout) throws SQLException;

    void parameterize(Statement statement) throws SQLException;

    int update(Statement statement) throws SQLException;

    <E> List<E> query(Statement statement) throws SQLException;

    ParameterHandler getParameterHandler();
}
