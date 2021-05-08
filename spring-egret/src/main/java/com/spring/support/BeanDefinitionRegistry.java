package com.spring.support;

import com.spring.config.BeanDefinition;
import com.spring.factory.NoSuchBeanDefinitionException;

import java.util.Set;

public interface BeanDefinitionRegistry {

    void registerBeanDefinition(String beanName, BeanDefinition beanDefinition);

    void registerBeanDefinition(Set<Class<?>> classSet);

    void removeBeanDefinition(String beanName) throws NoSuchBeanDefinitionException;

    BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException;

    boolean containsBeanDefinition(String beanName);

    int getBeanDefinitionCount();
}
