package com.ibatis.builder;

import com.ibatis.config.Configuration;

/**
 * @author：rkc
 * @date：Created in 2021/6/11 15:22
 * @description：
 */
public abstract class BaseBuilder {

    protected final Configuration configuration;

    public BaseBuilder(Configuration configuration) {
        this.configuration = configuration;
    }

    public Configuration getConfiguration() {
        return configuration;
    }
}
