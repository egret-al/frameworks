package com.example.controller;

import com.spring.annotation.Autowired;
import com.spring.annotation.Controller;

/**
 * @author rkc
 * @date 2021/5/24 22:34
 */
@Controller
public class UserController {

    @Autowired
    private ShoppingController shoppingController;

    public ShoppingController getShoppingController() {
        return shoppingController;
    }
}
