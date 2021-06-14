package com.ibatis.executor.statement;

import com.ibatis.config.MappedStatement;
import com.ibatis.executor.Executor;
import com.ibatis.executor.parameter.ParameterHandler;
import com.ibatis.mapping.StatementType;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * RoutingStatementHandler并不是真实的服务对象类，而是通过适配模式来找到对应的
 * StatementHandler来执行，其委托对象分为三种：SimpleStatementHandler、PreparedStatementHandler、CallableStatementHandler，
 * 分别对应了JDBC的Statement、PreparedStatement、CalledStatement
 * @author：rkc
 * @date：Created in 2021/6/12 15:59
 * @description：
 */
public class RoutingStatementHandler implements StatementHandler {

    private final StatementHandler delegate;

    public RoutingStatementHandler(Executor executor, MappedStatement ms, Object parameter) {
        if (ms.getStatementType() == StatementType.PREPARED) {
            delegate = new PreparedStatementHandler(executor, ms, parameter);
        } else {
            //其他的类型，比如SimpleStatementHandler、CallableStatementHandler，暂不做处理
            throw new RuntimeException("暂不支持的statement");
        }
    }

    @Override
    public Statement prepare(Connection connection, Integer transactionTimeout) throws SQLException {
        //prepare()、parameterize()、update()、query()等操作都是调用适配后的委托类执行
        return delegate.prepare(connection, transactionTimeout);
    }

    @Override
    public void parameterize(Statement statement) throws SQLException {
        delegate.parameterize(statement);
    }

    @Override
    public int update(Statement statement) throws SQLException {
        return delegate.update(statement);
    }

    @Override
    public <E> List<E> query(Statement statement) throws SQLException {
        return delegate.query(statement);
    }

    @Override
    public ParameterHandler getParameterHandler() {
        return delegate.getParameterHandler();
    }
}
