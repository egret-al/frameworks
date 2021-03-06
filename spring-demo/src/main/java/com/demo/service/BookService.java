package com.demo.service;

import com.spring.annotation.Autowired;
import com.spring.annotation.Component;
import com.spring.annotation.Service;

/**
 * @author rkc
 * @date 2021/3/14 12:51
 */
@Service
public class BookService {

    @Autowired
    private UserService userService;

    public UserService getUserService() {
        return userService;
    }
}
