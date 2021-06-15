package com.ibatis.executor.resultset;

import com.ibatis.config.Configuration;
import com.ibatis.config.MappedStatement;
import com.ibatis.executor.Executor;
import com.ibatis.executor.parameter.ParameterHandler;
import com.ibatis.mapping.ResultMap;
import com.ibatis.util.ObjectUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * @author：rkc
 * @date：Created in 2021/6/11 21:16
 * @description：
 */
public class DefaultResultSetHandler implements ResultSetHandler {

    private final Executor executor;
    private final Configuration configuration;
    private final MappedStatement mappedStatement;
    private final ParameterHandler parameterHandler;

    public DefaultResultSetHandler(Executor executor, MappedStatement mappedStatement, ParameterHandler parameterHandler) {
        this.executor = executor;
        this.mappedStatement = mappedStatement;
        this.parameterHandler = parameterHandler;
        this.configuration = mappedStatement.getConfiguration();
    }

    @Override
    public List<Object> handleResultSets(Statement stmt) throws SQLException {
        List<Object> multipleResults = new ArrayList<>();
        //一般情况下，mappedStatement只有一个ResultMap，因此不需要用集合来保存所有的resultMap
        ResultMap resultMap = mappedStatement.getResultMap();
        ResultSetTable rst = new ResultSetTable(stmt.getResultSet(), configuration);
        try {
            //遍历每一行
            for (int i = 0; i < rst.row(); i++) {
                Object obj = resultMap.getType().getDeclaredConstructor().newInstance();
                for (int j = 0; j < rst.column(); j++) {
                    String columnName = rst.getColumnNames().get(j);
                    //根据列名得到需要注入的属性名
                    String name = resultMap.getName(columnName);
                    System.out.println(name);
                    name = name.substring(name.indexOf(".") + 1);
                    injection(obj, name, rst.getValue(i, columnName));
                }
                multipleResults.add(obj);
            }
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        } finally {
            closeResultSet(stmt.getResultSet());
        }
        return multipleResults;
    }

    private void injection(Object obj, String name, Object value) throws NoSuchMethodException,
            InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchFieldException {
        if (value == null) {
            return;
        }
        //以点形式分开，对其遍历进行获取属性
        String[] fields = name.split("\\.");
        Object o1 = obj, o2 = null;
        for (int i = 0; i < fields.length; i++) {
            Field field = o1.getClass().getDeclaredField(fields[i]);
            Method getterMethod = o1.getClass().getMethod(ObjectUtils.getter(field.getName()));
            if (!ObjectUtils.isBaseType(field.getType())) {
                Object res = getterMethod.invoke(o1);
                if (res != null) {
                    o1 = res;
                    continue;
                }
            }
            if (i == fields.length - 1) {
                Method setterMethod = o1.getClass().getMethod(ObjectUtils.setter(fields[i]), field.getType());
                setterMethod.invoke(o1, value);
            } else {
                o2 = getterMethod.invoke(o1);
                if (o2 == null) {
                    o2 = getterMethod.getReturnType().getDeclaredConstructor().newInstance();
                    Method setterMethod = o1.getClass().getMethod(ObjectUtils.setter(fields[i]), o2.getClass());
                    setterMethod.invoke(o1, o2);
                }
                o1 = o2;
            }
        }
    }

    public static class ResultSetTable {
        private final int row;
        private final int column;
        private final List<List<Object>> values = new ArrayList<>();
        private final List<String> columnNames = new ArrayList<>();
        private final List<String> classNames = new ArrayList<>();
        private final ResultSet resultSet;
        private final List<String> jdbcTypes = new ArrayList<>();

        public ResultSetTable(ResultSet resultSet, Configuration configuration) throws SQLException {
            this.resultSet = resultSet;
            ResultSetMetaData metaData = resultSet.getMetaData();
            this.column = metaData.getColumnCount();
            //增加元数据信息
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                columnNames.add(configuration.isUseColumnLabel() ? metaData.getColumnLabel(i) : metaData.getColumnName(i));
                jdbcTypes.add(metaData.getColumnTypeName(i));
                classNames.add(metaData.getColumnClassName(i));
            }
            //获取其数据
            while (resultSet.next()) {
                List<Object> value = new ArrayList<>(column);
                //遍历列
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    value.add(resultSet.getObject(i));
                }
                values.add(value);
            }
            this.row = values.size();
        }

        public Object getValue(int index, String columnName) {
            int columnIndex = -1;
            for (int i = 0; i <= columnNames.size(); i++) {
                if (columnNames.get(i).equalsIgnoreCase(columnName)) {
                    columnIndex = i;
                    break;
                }
            }
            if (columnIndex == -1) {
                throw new RuntimeException("不存在列：" + columnName);
            }
            return values.get(index).get(columnIndex);
        }

        public List<Object> getValue(int index) {
            return values.get(index);
        }

        public List<List<Object>> getValues() {
            return values;
        }

        public String getJdbcType(String columnName) {
            for (int i = 0; i < columnNames.size(); i++) {
                if (columnNames.get(i).equalsIgnoreCase(columnName)) {
                    return jdbcTypes.get(i);
                }
            }
            return null;
        }

        public ResultSet getResultSet() {
            return resultSet;
        }

        public List<String> getColumnNames() {
            return this.columnNames;
        }

        public List<String> getClassNames() {
            return Collections.unmodifiableList(classNames);
        }

        public List<String> getJdbcTypes() {
            return jdbcTypes;
        }

        public int row() {
            return row;
        }

        public int column() {
            return column;
        }
    }

    private void closeResultSet(ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
