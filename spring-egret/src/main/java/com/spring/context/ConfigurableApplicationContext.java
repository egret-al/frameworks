package com.spring.context;

import com.spring.config.ConfigurableListableBeanFactory;
import com.spring.factory.config.BeanFactoryPostProcessor;

public interface ConfigurableApplicationContext {

    void refresh();

    void close();

    boolean isActive();

    <T> T getBean(String name, Class<T> requiredType);

    Object getBean(String name);

    ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException;

    void addBeanFactoryPostProcessor(BeanFactoryPostProcessor postProcessor);
}
