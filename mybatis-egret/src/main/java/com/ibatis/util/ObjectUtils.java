package com.ibatis.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;

/**
 * @author：rkc
 * @date：Created in 2021/6/14 15:03
 * @description：
 */
public class ObjectUtils {

    public static void main(String[] args) {
        System.out.println(isJavaBean(int.class));
        System.out.println(isJavaBean(Integer.class));
    }

    public static boolean isJavaBean(Class<?> clazz) {
        if (clazz == null) {
            throw new NullPointerException();
        }
        if (clazz.isPrimitive()) {
            return false;
        }
        Field[] fields = clazz.getDeclaredFields();
        try {
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                String fieldName = field.getName();
                Class<?> fieldType = field.getType();
                //根据属性名和get/set命名规则判断是否有对应的方法
                Method getter = clazz.getMethod("get" + (fieldName.substring(0, 1).toUpperCase(Locale.ENGLISH) + fieldName.substring(1)));
                Method setter = clazz.getMethod("set" + (fieldName.substring(0, 1).toUpperCase(Locale.ENGLISH) + fieldName.substring(1)), fieldType);
            }
        } catch (NoSuchMethodException e) {
            return false;
        }
        return true;
    }

    public static String getter(String fieldName) {
        return "get" + fieldName.substring(0, 1).toUpperCase(Locale.ENGLISH) + fieldName.substring(1);
    }

    public static String setter(String fieldName) {
        return "set" + fieldName.substring(0, 1).toUpperCase(Locale.ENGLISH) + fieldName.substring(1);
    }

    public static boolean isBaseType(Class<?> clazz) throws IllegalAccessException {
        if (clazz == String.class) {
            return true;
        }
        try {
            return clazz.isPrimitive() || ((Class<?>) clazz.getField("TYPE").get(null)).isPrimitive();
        } catch (NoSuchFieldException e) {
            return false;
        }
    }

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
}
