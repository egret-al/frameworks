package com.spring.support;

import com.spring.config.BeanDefinition;

/**
 * @author rkc
 * @date 2021/3/14 14:02
 */
public class RootBeanDefinition implements BeanDefinition {

    private Class<?> beanClass;
    private String scope;
    private Boolean isLazy;

    private final String SCOPE_DEFAULT = "";

    @Override
    public Class<?> getBeanClass() {
        return beanClass;
    }

    @Override
    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    @Override
    public String getScope() {
        return this.scope;
    }

    @Override
    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    public void setLazyInit(boolean lazyInit) {
        this.isLazy = lazyInit;
    }

    @Override
    public boolean isLazyInit() {
        return this.isLazy;
    }

    @Override
    public boolean isSingleton() {
        return SCOPE_SINGLETON.equals(this.scope) || SCOPE_DEFAULT.equals(this.scope);
    }

    @Override
    public boolean isPrototype() {
        return SCOPE_PROTOTYPE.equals(this.scope);
    }
}
