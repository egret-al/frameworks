package com.spring.context;

import com.spring.annotation.*;
import com.spring.config.BeanDefinition;
import com.spring.support.DefaultListableBeanFactory;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author rkc
 * @date 2021/3/12 13:34
 */
public class AnnotationConfigApplicationContext {

    private final ClassPathBeanDefinitionScanner scanner;
    private final DefaultListableBeanFactory beanFactory;

    public AnnotationConfigApplicationContext(Class<?> configClass) {
        beanFactory = new DefaultListableBeanFactory();
        this.scanner = new ClassPathBeanDefinitionScanner();

        Set<Class<?>> classSet = scanner.scan(configClass);
        beanFactory.registerBeanDefinition(classSet);
    }

    public <T> T getBean(String name, Class<T> requiredType) {
        return this.beanFactory.getBean(name, requiredType);
    }

    public DefaultListableBeanFactory getBeanFactory() {
        return beanFactory;
    }
}
