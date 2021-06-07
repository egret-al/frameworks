package com.ibatis.config;

import java.util.HashMap;
import java.util.Map;

/**
 * @author rkc
 * @date 2021/3/18 20:08
 */
public class Configuration {

    private String jdbcDriver;
    private String jdbcUrl;
    private String jdbcUsername;
    private String jdbcPassword;

    private final Map<String, MappedStatement> mappedStatements = new HashMap<>();

    public Map<String, MappedStatement> getMappedStatements() {
        return mappedStatements;
    }

    public String getJdbcDriver() {
        return jdbcDriver;
    }

    public void setJdbcDriver(String jdbcDriver) {
        this.jdbcDriver = jdbcDriver;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getJdbcUsername() {
        return jdbcUsername;
    }

    public void setJdbcUsername(String jdbcUsername) {
        this.jdbcUsername = jdbcUsername;
    }

    public String getJdbcPassword() {
        return jdbcPassword;
    }

    public void setJdbcPassword(String jdbcPassword) {
        this.jdbcPassword = jdbcPassword;
    }
}
