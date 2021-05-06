package com.example.service.impl;

import com.spring.annotation.*;
import com.spring.factory.BeanNameAware;
import com.spring.factory.InitializingBean;

/**
 * @author rkc
 * @date 2021/3/12 13:40
 */
@Repository
public class UserService implements BeanNameAware, InitializingBean {

    @Autowired
    private IndexServiceImpl1 indexServiceImpl1;

    @Autowired
    private BookService bookService;

    private String beanName;

    public BookService getBookService() {
        return bookService;
    }

    public IndexServiceImpl1 getIndexService() {
        return indexServiceImpl1;
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
