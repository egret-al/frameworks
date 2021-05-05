package com.spring.factory;

/**
 * @author rkc
 * @date 2021/3/12 21:37
 */
@SuppressWarnings("all")
public interface BeanPostProcessor {

    default Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    default Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }
}
