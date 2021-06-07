package com.ibatis.executor;

import com.ibatis.config.Configuration;
import com.ibatis.config.MappedStatement;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author rkc
 * @date 2021/3/18 20:10
 */
public class SimpleExecutor extends BaseExecutor {

    public SimpleExecutor(Configuration configuration) {
        super(configuration);
    }

    @Override
    public <E> List<E> query(MappedStatement ms, Object parameter) {
        List<E> results = new ArrayList<>();
        try {
            Class.forName(configuration.getJdbcDriver());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try (
                Connection connection = DriverManager.getConnection(configuration.getJdbcUrl(), configuration.getJdbcUsername(), configuration.getJdbcPassword());
                PreparedStatement preparedStatement = connection.prepareStatement(ms.getSql());
                ResultSet rs = preparedStatement.executeQuery();
        ) {
            //反射填充结果集
//            parameterize(preparedStatement, parameter);
//            handlerResultSet(resultSet, results, ms.getResultType());
            System.out.println("执行sql：" + ms.getSql());
            //得到元数据
            ResultSetMetaData rsMetaData = rs.getMetaData();
            Class<?> rtClass = Class.forName(ms.getResultType());
            while (rs.next()) {
                //反射创建对象
                Object rt = rtClass.getDeclaredConstructor().newInstance();
                //遍历当前行的所有列，并对rt进行属性注入
                for (int i = 1; i <= rsMetaData.getColumnCount(); i++) {
                    //得到列名和值，列名便于后续与JavaBean进行映射
                    String columnName = rsMetaData.getColumnName(i);
                    Object columnValue = rs.getObject(i);
                    //通过列名找到Field进行赋值
                    Field rtField = rtClass.getDeclaredField(columnName);
                    rtField.setAccessible(true);
                    rtField.set(rt, columnValue);
                }
                results.add((E) rt);
            }
        } catch (SQLException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException | NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            System.err.println("参数映射错误！");
        }
        return results;
    }

    private <E> void handlerResultSet(ResultSet resultSet, List<E> results, String resultType) {

    }

    private void parameterize(PreparedStatement preparedStatement, Object parameter) {

    }
}
