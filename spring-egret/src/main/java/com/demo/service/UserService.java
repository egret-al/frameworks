package com.demo.service;

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
