package com.spring.context;

import com.spring.support.AbstractApplicationContext;
import com.spring.beans.BeansException;

/**
 * 在任何需要注入上下文的地方实现该接口
 * @author：rkc
 * @date：Created in 2021/5/9 16:01
 * @description：
 */
public interface ApplicationContextAware {

    void setApplicationContext(AbstractApplicationContext applicationContext) throws BeansException;
}
