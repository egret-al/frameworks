package com.demo;

import com.demo.entity.Score;
import com.demo.entity.User;
import com.ibatis.util.ObjectUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author：rkc
 * @date：Created in 2021/6/11 17:05
 * @description：
 */
public class ClassTest {

    private static final Map<String, Field> fieldMap = new HashMap<>();

    public static void main(String[] args) throws Exception {
//        Score score = new Score();
//        User user = new User();
//        user.setScore(score);
//        fun(user, user.getClass().getSimpleName());
//        for (String key : fieldMap.keySet()) {
//            System.out.println(key + " -> " + fieldMap.get(key));
//        }
        String name = "user.score.id";
        System.out.println(name.substring(name.indexOf(".") + 1));
    }

    public static void fun(Object obj, String prefix) throws Exception {
        Class<?> clazz = obj.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (!ObjectUtils.isBaseType(field.getType())) {
                Method method = obj.getClass().getMethod(ObjectUtils.getter(field.getName()));
                Object fieldObj = method.invoke(obj);
                fun(fieldObj, prefix + "." + fieldObj.getClass().getSimpleName());
                return;
            }
            fieldMap.put(prefix + "." + field.getName(), field);
//            System.out.println(prefix + "." + field.getName());
        }
    }
}
