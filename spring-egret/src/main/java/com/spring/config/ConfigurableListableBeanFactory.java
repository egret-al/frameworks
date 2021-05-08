package com.spring.config;

import com.spring.factory.NoSuchBeanDefinitionException;

import java.util.List;
import java.util.Map;

public interface ConfigurableListableBeanFactory {

    BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException;

    Map<String, BeanDefinition> getBeanDefinitions() throws NoSuchBeanDefinitionException;
}
