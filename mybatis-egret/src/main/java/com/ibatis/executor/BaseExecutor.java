package com.ibatis.executor;

import com.ibatis.config.Configuration;
import com.ibatis.config.MappedStatement;

import java.util.List;

/**
 * @author rkc
 * @date 2021/3/19 19:55
 */
public abstract class BaseExecutor implements Executor {

    protected final Configuration configuration;

    public BaseExecutor(Configuration configuration) {
        this.configuration = configuration;
    }
}
