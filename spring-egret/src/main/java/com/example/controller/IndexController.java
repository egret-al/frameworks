package com.example.controller;

import com.example.service.IndexService;
import com.spring.annotation.Autowired;
import com.spring.annotation.Controller;
import com.spring.annotation.Qualifier;

/**
 * @author：rkc
 * @date：Created in 2021/5/6 14:16
 * @description：
 */
@Controller
public class IndexController {

    @Autowired
    @Qualifier("indexServiceImpl1")
    private IndexService indexServiceImpl2;

    public IndexService getIndexService() {
        return indexServiceImpl2;
    }
}
