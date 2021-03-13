package com.demo.service;

import com.spring.annotation.Component;
import com.spring.factory.BeanPostProcessor;

/**
 * @author rkc
 * @date 2021/3/12 21:40
 */
@Component("beanPostProcessorImpl")
public class BeanPostProcessorImpl implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        System.out.println("初始化之前");
        System.out.println(bean);
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        System.out.println("初始化之后");
        System.out.println(bean);
        return bean;
    }
}
