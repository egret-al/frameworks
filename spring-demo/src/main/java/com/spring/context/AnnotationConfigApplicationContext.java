package com.spring.context;

import com.spring.annotation.ClassPathBeanDefinitionScanner;
import com.spring.config.BeanDefinition;
import com.spring.support.DefaultListableBeanFactory;

import java.util.Set;

/**
 * @author rkc
 * @date 2021/3/12 13:34
 */
public class AnnotationConfigApplicationContext {

    private final ClassPathBeanDefinitionScanner scanner;
    private final DefaultListableBeanFactory beanFactory;
    private final Object startupShutdownMonitor = new Object();

    public AnnotationConfigApplicationContext(Class<?> configClass) {
        beanFactory = new DefaultListableBeanFactory();
        this.scanner = new ClassPathBeanDefinitionScanner();

        register(configClass);
        refresh();
    }

    public void register(Class<?> componentClasses) {
        Set<Class<?>> classSet = scanner.scan(componentClasses);
        beanFactory.registerBeanDefinition(classSet);
    }

    public void refresh() {
        synchronized (this.startupShutdownMonitor) {
            //TODO 环境准备、各种监听器的注册、工厂创建、国际化处理等操作

            //完成工厂的创建，开始准备处理需要Spring管理得bean
            getBeanFactory().finishBeanFactoryInitialization();
        }
    }

    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        getBeanFactory().registerBeanDefinition(beanName, beanDefinition);
    }

    public <T> T getBean(String name, Class<T> requiredType) {
        return this.beanFactory.getBean(name, requiredType);
    }

    public DefaultListableBeanFactory getBeanFactory() {
        return beanFactory;
    }
}
