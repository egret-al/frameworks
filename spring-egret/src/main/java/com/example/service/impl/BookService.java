package com.example.service.impl;

import com.spring.annotation.Autowired;
import com.spring.annotation.Service;

/**
 * @author rkc
 * @date 2021/3/14 12:51
 */
@Service
public class BookService {

    private String name = "aa";

//    @Autowired
    private UserService userService;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    //    @Autowired
    private BeanPostProcessorImpl beanPostProcessor;

    public BeanPostProcessorImpl getBeanPostProcessor() {
        return beanPostProcessor;
    }

    public UserService getUserService() {
        return userService;
    }
}
