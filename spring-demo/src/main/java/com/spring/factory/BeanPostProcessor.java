package com.spring.factory;

/**
 * @author rkc
 * @date 2021/3/12 21:37
 */
public interface BeanPostProcessor {

    @SuppressWarnings("all")
    default Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    @SuppressWarnings("all")
    default Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }
}
