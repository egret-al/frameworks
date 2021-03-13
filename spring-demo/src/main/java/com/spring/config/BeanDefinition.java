package com.spring.config;

/**
 * @author rkc
 * @date 2021/3/12 14:14
 */
public class BeanDefinition {

    private Class<?> beanClass;
    private String scope;
    private Boolean isLazy;

    public String SCOPE_SINGLETON = ConfigurableBeanFactory.SCOPE_SINGLETON;
    public String SCOPE_PROTOTYPE = ConfigurableBeanFactory.SCOPE_PROTOTYPE;

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public Boolean getLazy() {
        return isLazy;
    }

    public void setLazy(Boolean lazy) {
        isLazy = lazy;
    }
}
