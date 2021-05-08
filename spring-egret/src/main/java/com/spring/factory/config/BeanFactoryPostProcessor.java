package com.spring.factory.config;

import com.spring.beans.BeansException;
import com.spring.config.ConfigurableListableBeanFactory;

/**
 * @author：rkc
 * @date：Created in 2021/5/7 14:14
 * @description：
 */
@FunctionalInterface
public interface BeanFactoryPostProcessor {

    void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException;
}
