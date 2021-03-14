package com.spring.factory;

/**
 * @author rkc
 * @date 2021/3/13 21:39
 */
public interface ObjectFactory<T> {

    T getObject();
}
