package com.demo.controller;

import com.demo.service.IndexService;
import com.spring.annotation.Autowired;
import com.spring.annotation.Controller;

/**
 * @author：rkc
 * @date：Created in 2021/5/6 14:16
 * @description：
 */
@Controller
public class IndexController {

    @Autowired
    private IndexService indexService;

    public IndexService getIndexService() {
        return indexService;
    }
}
