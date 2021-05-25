package com.example.controller;

import com.spring.annotation.Autowired;
import com.spring.annotation.Controller;

/**
 * @author rkc
 * @date 2021/5/24 22:35
 */
@Controller
public class ShoppingController {

    @Autowired
    private UserController userController;

    public UserController getUserController() {
        return userController;
    }
}
