package com.spring.context;

import com.spring.annotation.ClassPathBeanDefinitionScanner;
import com.spring.config.BeanDefinition;
import com.spring.config.ConfigurableListableBeanFactory;
import com.spring.support.AbstractApplicationContext;
import com.spring.support.DefaultListableBeanFactory;

import java.util.Set;

/**
 * @author rkc
 * @date 2021/3/12 13:34
 */
public class AnnotationConfigApplicationContext extends AbstractApplicationContext {

    private final ClassPathBeanDefinitionScanner scanner;

    public AnnotationConfigApplicationContext(Class<?> configClass) {
        super();
        this.scanner = new ClassPathBeanDefinitionScanner();
        register(configClass);
        refresh();
    }

    public void register(Class<?> componentClasses) {
        Set<Class<?>> classSet = scanner.scan(componentClasses);
        getBeanDefinitionRegistry().registerBeanDefinition(classSet);
    }
}
