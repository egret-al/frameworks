package com.spring.config;

/**
 * @author rkc
 * @date 2021/3/12 14:14
 */
public interface BeanDefinition {

    String SCOPE_SINGLETON = ConfigurableBeanFactory.SCOPE_SINGLETON;
    String SCOPE_PROTOTYPE = ConfigurableBeanFactory.SCOPE_PROTOTYPE;

    String getScope();

    void setScope(String scope);

    void setLazyInit(boolean lazyInit);

    boolean isLazyInit();

    boolean isSingleton();

    boolean isPrototype();

    Class<?> getBeanClass();

    void setBeanClass(Class<?> beanClass);
}
