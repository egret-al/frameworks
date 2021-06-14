package com.demo;

import com.demo.entity.User;

import java.lang.reflect.Field;

/**
 * @author：rkc
 * @date：Created in 2021/6/11 17:05
 * @description：
 */
public class ClassTest {

    public static void main(String[] args) throws Exception {
        Class<?> clazz = User.class;
        System.out.println(clazz.getDeclaredField("score").getType().getName());
    }
}
