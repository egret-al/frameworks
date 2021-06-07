package com.ibatis.util;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author rkc
 * @date 2021/3/20 20:08
 */
public class ReflectionUtil {

    /**
     * 给指定的bean的属性赋值
     * @param bean 被赋值的对象
     * @param propertyName 赋值的字段
     * @param value 赋值的内容
     */
    public static void setPropertyToBean(Object bean, String propertyName, Object value) {
        Field field;
        try {
            field = bean.getClass().getField(propertyName);
            field.setAccessible(true);
            field.set(bean, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将resultSet转换为java对象
     * @param entity 对象
     * @param resultSet 结果集
     * @throws SQLException SQLException
     */
    public static void setPropertyToBeanFromResultSet(Object entity, ResultSet resultSet) throws SQLException {
        Field[] declaredFields = entity.getClass().getDeclaredFields();
        for (Field declaredField : declaredFields) {
            if (declaredField.getType().getSimpleName().equals("String")) {
                setPropertyToBean(entity, declaredField.getName(), resultSet.getString(declaredField.getName()));
            } else if (declaredField.getType().getSimpleName().equals("Integer")) {
                setPropertyToBean(entity, declaredField.getName(), resultSet.getInt(declaredField.getName()));
            } else if (declaredField.getType().getSimpleName().equals("Long")) {
                setPropertyToBean(entity, declaredField.getName(), resultSet.getLong(declaredField.getName()));
            }
        }
    }
}
