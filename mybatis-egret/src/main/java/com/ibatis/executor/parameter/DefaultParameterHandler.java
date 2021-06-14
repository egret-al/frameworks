package com.ibatis.executor.parameter;

import com.ibatis.config.Configuration;
import com.ibatis.config.MappedStatement;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * @author：rkc
 * @date：Created in 2021/6/12 15:41
 * @description：
 */
public class DefaultParameterHandler implements ParameterHandler {

    private final MappedStatement mappedStatement;
    private final Object parameterObject;
    private final Configuration configuration;

    public DefaultParameterHandler(MappedStatement mappedStatement, Object parameterObject) {
        this.mappedStatement = mappedStatement;
        this.parameterObject = parameterObject;
        this.configuration = mappedStatement.getConfiguration();
    }

    @Override
    public Object getParameterObject() {
        return parameterObject;
    }

    @Override
    public void setParameters(PreparedStatement preparedStatement) throws SQLException {
        PreparedStatementParams preparedStatementParams = parameterize(mappedStatement.getSqlSource(), parameterObject);
        String sql = preparedStatementParams.sql();
        mappedStatement.setSqlSource(sql);
        System.out.println(sql);
        Object[] params = preparedStatementParams.params();
        //参数赋值
        for (int i = 0; i < params.length; i++) {
            preparedStatement.setObject(i + 1, params[i]);
        }
    }

    private PreparedStatementParams parameterize(String sql, Object parameter) {
        if (parameter == null) {
            return new PreparedStatementParams(sql, new Object[0]);
        }
        PreparedStatementParams result = null;
        try {
            Object[] params = new Object[count(sql, '#')];
            //参数是集合
            if (Collection.class.isAssignableFrom(parameter.getClass()) || parameter.getClass().isArray()) {
                throw new RuntimeException("暂不支持集合参数！");
            }
            //参数是基本数据类型或者其包装类型
            if (parameter instanceof Map) {
                sql = parameterIsMap(sql, (Map<?, ?>) parameter, params);
                return new PreparedStatementParams(sql, params);
            }
            //参数是一个基本数据类型，说明此时的sql语句只需要一个参数，否则就会进入上一个if语句
            if (isBaseType(parameter)) {
                sql = parameterIsBaseType(sql, parameter, params);
                return new PreparedStatementParams(sql, params);
            }
            //参数是单个对象
            sql = parameterIsSingleObject(sql, parameter, params);
            result = new PreparedStatementParams(sql, params);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return result;
    }

    private String parameterIsBaseType(String sql, Object parameter, Object[] params) {
        if (count(sql, '#') != 1) {
            throw new RuntimeException("SQL语句与参数不匹配！" + sql);
        }
        int startIndex = sql.indexOf("#{");
        int endIndex = sql.indexOf("}");
        String key = sql.substring(startIndex + 2, endIndex);
        sql = sql.replace("#{" + key + "}", "?");
        params[0] = parameter;
        return sql;
    }

    /**
     * 参数是Map，key为@Param指定的值，value为需要填入的值
     * @param sql xml中的sql语句
     * @param parameter 参数
     * @param params 与sql中参数位置对应的参数数组
     * @return prepareStatement使用的sql
     */
    private String parameterIsMap(String sql, Map<?, ?> parameter, Object[] params) {
        int index = 0;
        while (sql.contains("#{")) {
            int startIndex = sql.indexOf("#{");
            int endIndex = sql.indexOf("}");
            String key = sql.substring(startIndex + 2, endIndex);
            Object value = Objects.requireNonNull(parameter.get(key));
            params[index++] = value;
            sql = sql.replace("#{" + key + "}", "?");
        }
        return sql;
    }

    /**
     * 参数是单个对象
     * @param sql xml中的sql语句
     * @param parameter 单个对象作为参数
     * @param params 映射到的参数数组，如：select * from user where username=#{username} and sex=#{sex}，则该数组的0代表username
     *               ，1代表sex
     * @return 参数化后的sql，原有的#符号都会被替换为？
     * @throws NoSuchFieldException NoSuchFieldException
     * @throws IllegalAccessException IllegalAccessException
     */
    private String parameterIsSingleObject(String sql, Object parameter, Object[] params) throws NoSuchFieldException, IllegalAccessException {
        int index = 0;
        while (sql.contains("#{")) {
            int startIndex = sql.indexOf("#{");
            int endIndex = sql.indexOf("}");
            String key = sql.substring(startIndex + 2, endIndex);
            Field field = parameter.getClass().getDeclaredField(key);
            field.setAccessible(true);
            Object value = field.get(parameter);
            params[index++] = value;
            sql = sql.replace("#{" + key + "}", "?");
        }
        return sql;
    }

    public static class PreparedStatementParams {
        private final Object[] params;
        private final String sql;

        public PreparedStatementParams(String sql, Object[] params) {
            this.params = params;
            this.sql = sql;
        }

        public Object[] params() {
            return params;
        }

        public String sql() {
            return sql;
        }
    }

    public static int count(String str, char c) {
        int count = 0;
        char[] array = str.toCharArray();
        for (char value : array) {
            if (value == c) {
                count++;
            }
        }
        return count;
    }

    /**
     * 给定一个对象，判断是否是基本数据类型或者其包装类型
     * @param object 被判断的对象
     * @return 是否是基本数据类型
     */
    public static boolean isBaseType(Object object) {
        return object.getClass().isPrimitive()
                || object instanceof String
                || object instanceof Integer
                || object instanceof Double
                || object instanceof Float
                || object instanceof Long
                || object instanceof Boolean
                || object instanceof Byte
                || object instanceof Short;
    }
}
