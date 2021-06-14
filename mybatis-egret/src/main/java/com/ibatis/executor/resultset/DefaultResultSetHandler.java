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
            autoMappingProperty(resultMap, rst, multipleResults, 0, 0, RETURN_NULL);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        } finally {
            closeResultSet(stmt.getResultSet());
        }
        return multipleResults;
    }

    /**
     * 根据ResultMap的映射信息，将ResultSetTable中查询到的数据映射到multipleResults结果中
     *
     * @param resultMap       resultMap
     * @param resultSetTable  查询后的表
     * @param multipleResults 结果
     */
    private Object autoMappingProperty(ResultMap resultMap, ResultSetTable resultSetTable, List<Object> multipleResults, int rowIndex, int columnIndex, int state)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        //遍历每行每列
        for (int i = rowIndex; i < resultSetTable.row(); i++) {
            //根据ResultMap保存的Class信息创建一个对象，并进行映射赋值
            Object obj = resultMap.getType().getDeclaredConstructor().newInstance();
            for (int j = columnIndex; j < resultSetTable.column(); j++) {
                //得到列名，根据列名和ResultMapping得到存在的属性名
                String column = resultSetTable.getColumnNames().get(j);
                String fieldName = resultMap.getProperty(column);
                if (fieldName == null) {
                    //如果当前的resultMap没有对应的属性，则检查其association和collection，根据创建resultMap时的规则进行重组resultMap，并根据对应情况进行映射
                    ResultMap rm = configuration.getResultMap(resultMap.getId() + "_association");
                    if (rm != null) {
                        Object fieldObj = autoMappingProperty(rm, resultSetTable, multipleResults, i, j, RETURN_NOT_NULL);
                        //根据resultMapping的property将当前关联对象进行注入
                        if (j >= resultMap.getResultMappings().size()) {
                            break;
                        }
                        fieldName = resultMap.getResultMappings().get(j).getProperty();
                        Field field = resultMap.getType().getDeclaredField(fieldName);
                        Method setter = resultMap.getType().getMethod(ObjectUtils.setter(fieldName), field.getType());
                        setter.invoke(obj, fieldObj);
                    } else {
                        //尝试从collection中获取
                        rm = configuration.getResultMap(resultMap.getId() + "_collection");
//                        Objects.requireNonNull(rm);
//                        Collection<Object> objects = new ArrayList<>();
//                        Object itemObj = autoMappingProperty(rm, resultSetTable, multipleResults, i, j, RETURN_NOT_NULL);
//                        objects.add(itemObj);
                    }
                    continue;
                }
                Field field = resultMap.getType().getDeclaredField(fieldName);
                //根据映射后的属性名，通过setter方法将其注入
                Method setter = resultMap.getType().getMethod(ObjectUtils.setter(fieldName), field.getType());
                //调用setter方法进行赋值
                Object value = resultSetTable.getValue(i, column);
                if (value != null) {
                    setter.invoke(obj, value);
                }
            }
            if (state == 0) {
                multipleResults.add(obj);
            } else {
                return obj;
            }
        }
        return null;
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
