package com.ibatis.executor;

import com.ibatis.config.Configuration;
import com.ibatis.config.MappedStatement;
import com.ibatis.executor.statement.StatementHandler;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.*;

/**
 * @author rkc
 * @date 2021/3/18 20:10
 */
public class SimpleExecutor extends BaseExecutor {

    public SimpleExecutor(Configuration configuration) {
        super(configuration);
    }

    @Override
    @SuppressWarnings("all")
    public <E> List<E> query(MappedStatement ms, Object parameter) throws SQLException {
        Statement statement = null;
        try {
            Configuration configuration = ms.getConfiguration();
            StatementHandler statementHandler = configuration.newStatementHandler(this, ms, parameter);
            statement = prepareStatement(statementHandler);
            return statementHandler.query(statement);
        } finally {
            closeStatement(statement);
        }
    }

    @Override
    public int update(MappedStatement ms, Object parameter) throws SQLException {
        Statement statement = null;
        try {
            //通过Configuration创建一个处理器
            StatementHandler statementHandler = configuration.newStatementHandler(this, ms, parameter);
            //得到statement
            statement = prepareStatement(statementHandler);
            //调用更新方法
            return statementHandler.update(statement);
        } finally {
            closeStatement(statement);
        }
    }

    private Statement prepareStatement(StatementHandler statementHandler) throws SQLException {
        //获取数据库连接
        Connection connection = DriverManager.getConnection(configuration.getUrl(), configuration.getUsername(), configuration.getPassword());
        //预处理操作
        Statement statement = statementHandler.prepare(connection, 0);
        //对statement进行参数化
        statementHandler.parameterize(statement);
        return statement;
    }
}
