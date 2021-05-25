package com.example.service.impl;

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
        if (beanName.equals("bookService")) {
            ((BookService) bean).setName("zz");
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (beanName.equals("bookService")) {
            System.out.println(bean.hashCode());
            return new BookService();
        }
        return bean;
    }
}
