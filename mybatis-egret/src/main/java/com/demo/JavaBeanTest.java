package com.demo;

import com.demo.entity.Address;
import com.demo.entity.Score;
import com.demo.entity.User;

import java.lang.reflect.*;
import java.util.*;

/**
 * @author：rkc
 * @date：Created in 2021/6/14 9:35
 * @description：
 */
public class JavaBeanTest {

    private List<User> users = new ArrayList<>();


    public static void main(String[] args) throws Exception {
//        Field field = JavaBeanTest.class.getDeclaredField("users");
//        if (Collection.class.isAssignableFrom(field.getType())) {
//            //如果是集合类型，则获取其泛型的真实类型
//            ParameterizedType type = (ParameterizedType) field.getGenericType();
//            System.out.println(Arrays.toString(type.getActualTypeArguments()));
//        }

        testObj();
    }

    public static void objField(Object obj) throws InvocationTargetException, IllegalAccessException {
        System.out.println();
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            String name = field.getName().substring(0, 1).toUpperCase(Locale.ENGLISH) + field.getName().substring(1);
            Method getter = null;
            Method setter = null;
            try {
                getter = clazz.getMethod("get" + name);
                setter = clazz.getMethod("set" + name, field.getType());
            } catch (NoSuchMethodException e) {
                //说明此时getter或者setter方法获取不到，不执行后面的代码
                continue;
            }

            Object fieldObj = getter.invoke(obj);
            System.out.print(fields[i].getName() + "->" + fieldObj + "  ");

            //如果当前遍历的字段是一个JavaBean，则递归。使用判断基本数据类型来进行递归剪枝
            if (!isBaseType(fieldObj) && isJavaBean(field.getType())) {
                objField(fieldObj);
            }
            //如果当前遍历的是一个集合字段，取出其真实类型，如果真实类型是JavaBean，则递归
            if (Collection.class.isAssignableFrom(field.getType())) {
                ParameterizedType type = (ParameterizedType) field.getGenericType();
                Type actualType = type.getActualTypeArguments()[0];
                if (isJavaBean((Class<?>) actualType)) {
                    System.out.println("集合合法字段");
                    //取出当前集合对象
                    Collection<?> collection = (Collection<?>) fieldObj;
                    Iterator<?> iterator = collection.stream().iterator();
                    while (iterator.hasNext()) {
                        System.out.println(iterator.next());
                    }
                }
            }
        }
    }

    public static boolean isJavaBean(Class<?> clazz) {
        if (clazz == null) {
            throw new NullPointerException();
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
                if (getter == null || setter == null) {
                    return false;
                }
            }
        } catch (NoSuchMethodException e) {
            return false;
        }
        return true;
    }

    public static void testObj() throws Exception {
        Score score = new Score();
        score.setId(1);
        score.setMybatis(100);
        score.setSpring(99);
        score.setSpringmvc(98);
        score.setUserId(1);

        List<Address> addresses = new ArrayList<>();
        Address address = new Address();
        address.setCompany("公司地址");
        address.setHome("家庭地址");
        address.setSchool("学校地址");
        addresses.add(address);
        User user = new User();
        user.setId(1);
        user.setEmail("2243756824@qq.com");
        user.setMobile("17602382966");
        user.setNote("note");
        user.setUsername("虞姬");
        user.setRealName("虞姬");
        user.setScore(score);
        user.setSex("女");
        user.setAddresses(addresses);
        objField(user);
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
}
