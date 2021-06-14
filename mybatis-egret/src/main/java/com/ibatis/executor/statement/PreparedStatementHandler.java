package com.ibatis.executor.statement;

import com.ibatis.config.MappedStatement;
import com.ibatis.executor.Executor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * @author：rkc
 * @date：Created in 2021/6/12 15:43
 * @description：
 */
public class PreparedStatementHandler extends BaseStatementHandler {

    public PreparedStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameterObject) {
        super(executor, mappedStatement, parameterObject);
    }

    @Override
    public void parameterize(Statement statement) throws SQLException {
        parameterHandler.setParameters((PreparedStatement) statement);
    }

    @Override
    public int update(Statement statement) throws SQLException {
        //在PreparedStatementHandler里，可知statement是PreparedStatement，因此可以直接转型
        PreparedStatement ps = (PreparedStatement) statement;
        //执行
        ps.execute();
        int row = ps.getUpdateCount();
        return row;
    }

    @Override
    public <E> List<E> query(Statement statement) throws SQLException {
        PreparedStatement ps = (PreparedStatement) statement;
        ps.execute();
        return (List<E>) resultSetHandler.handleResultSets(ps);
    }

    @Override
    protected Statement instantiateStatement(Connection connection) throws SQLException {
        String sql = mappedStatement.getSqlSource();
        return connection.prepareStatement(sql);
    }
}
