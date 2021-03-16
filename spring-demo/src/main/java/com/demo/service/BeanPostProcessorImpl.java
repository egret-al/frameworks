package com.demo.service;

import com.spring.annotation.Component;
import com.spring.factory.BeanPostProcessor;

/**
 * @author rkc
 * @date 2021/3/12 21:40
 */
@Component
public class BeanPostProcessorImpl implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }
}
