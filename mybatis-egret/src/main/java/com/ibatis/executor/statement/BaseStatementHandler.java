package com.ibatis.executor.statement;

import com.ibatis.config.Configuration;
import com.ibatis.config.MappedStatement;
import com.ibatis.executor.Executor;
import com.ibatis.executor.parameter.ParameterHandler;
import com.ibatis.executor.resultset.ResultSetHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 存放各种Statement所需要公用的信息
 * @author：rkc
 * @date：Created in 2021/6/12 15:42
 * @description：
 */
public abstract class BaseStatementHandler implements StatementHandler {

    protected final Configuration configuration;
    protected final ResultSetHandler resultSetHandler;
    protected final ParameterHandler parameterHandler;

    protected final Executor executor;
    protected final MappedStatement mappedStatement;

    public BaseStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameterObject) {
        this.configuration = mappedStatement.getConfiguration();
        this.executor = executor;
        this.mappedStatement = mappedStatement;
        this.parameterHandler = configuration.newParameterHandler(mappedStatement, parameterObject);
        this.resultSetHandler = configuration.newResultSetHandler(executor, mappedStatement, parameterHandler);
    }

    @Override
    public Statement prepare(Connection connection, Integer transactionTimeout) throws SQLException {
        Statement statement = instantiateStatement(connection);
        //其他的一些预处理事情
        statement.setQueryTimeout(transactionTimeout);
        return statement;
    }

    //由具体的子类进行创建不同的Statement
    protected abstract Statement instantiateStatement(Connection connection) throws SQLException;

    @Override
    public ParameterHandler getParameterHandler() {
        return parameterHandler;
    }
}
