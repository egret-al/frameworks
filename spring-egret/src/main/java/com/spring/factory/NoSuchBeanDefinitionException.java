package com.spring.factory;

/**
 * @author：rkc
 * @date：Created in 2021/5/6 22:31
 * @description：
 */
public class NoSuchBeanDefinitionException extends RuntimeException {

    private final String beanName;

    public NoSuchBeanDefinitionException(String beanName) {
        super("No bean named '" + beanName + "' available");
        this.beanName = beanName;
    }

    public NoSuchBeanDefinitionException(String name, String message) {
        super("No bean named '" + name + "' available: " + message);
        this.beanName = name;
    }
}
