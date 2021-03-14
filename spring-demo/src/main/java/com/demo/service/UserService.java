package com.demo.service;

import com.spring.annotation.Autowired;
import com.spring.annotation.Component;
import com.spring.annotation.Scope;
import com.spring.factory.BeanNameAware;
import com.spring.factory.InitializingBean;

/**
 * @author rkc
 * @date 2021/3/12 13:40
 */
@Component("userService")
public class UserService implements BeanNameAware, InitializingBean {

    @Autowired
    private IndexService indexService;

    @Autowired
    private BookService bookService;

    private String beanName;

    public BookService getBookService() {
        return bookService;
    }

    public IndexService getIndexService() {
        return indexService;
    }

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getBeanName() {
        return beanName;
    }

    @Override
    public void afterPropertiesSet() {

    }
}
