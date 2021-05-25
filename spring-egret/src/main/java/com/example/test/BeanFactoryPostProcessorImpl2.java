package com.example.test;

import com.spring.annotation.Component;
import com.spring.beans.BeansException;
import com.spring.config.BeanDefinition;
import com.spring.config.ConfigurableListableBeanFactory;
import com.spring.factory.config.BeanFactoryPostProcessor;

import java.util.Map;

/**
 * @author：rkc
 * @date：Created in 2021/5/7 22:11
 * @description：
 */
//@Component
public class BeanFactoryPostProcessorImpl2 implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        System.out.println(this.hashCode());
        Map<String, BeanDefinition> beanDefinitions = beanFactory.getBeanDefinitions();
        for (String key : beanDefinitions.keySet()) {
            System.out.println(key + ": " + beanDefinitions.get(key).getBeanClass().getSimpleName());
        }
    }
}
