package com.demo.service;

import com.spring.annotation.*;

/**
 * @author rkc
 * @date 2021/3/12 13:40
 */
@Controller
public class IndexService {

    @Autowired
    private UserService userService;

    public UserService getUserService() {
        return userService;
    }
}
