package com.ibatis.config;

import com.ibatis.mapping.ResultMap;
import com.ibatis.mapping.SqlCommandType;
import com.ibatis.mapping.StatementType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author rkc
 * @date 2021/3/18 20:09
 */
public class MappedStatement {

    private String resource;
    private Configuration configuration;
    private String namespace;
    private String id;
    private String sqlSource;
    private String parameterType;
    private boolean useGeneratedKeys;
    private SqlCommandType sqlCommandType;
    private StatementType statementType;

    private ResultMap resultMap;

    public static class Builder {
        private MappedStatement mappedStatement = new MappedStatement();

        public Builder(Configuration configuration, String id, String sqlSource, SqlCommandType sqlCommandType) {
            mappedStatement.configuration = configuration;
            mappedStatement.id = id;
            mappedStatement.sqlCommandType = sqlCommandType;
            mappedStatement.useGeneratedKeys = false;
            mappedStatement.sqlSource = sqlSource;
            mappedStatement.statementType = StatementType.PREPARED;
        }

        public Builder useGeneratedKeys(boolean useGeneratedKeys) {
            mappedStatement.useGeneratedKeys = useGeneratedKeys;
            return this;
        }

        public Builder parameterType(String parameterType) {
            mappedStatement.parameterType = parameterType;
            return this;
        }

        public Builder namespace(String namespace) {
            mappedStatement.namespace = namespace;
            return this;
        }

        public Builder resource(String resource) {
            mappedStatement.resource = resource;
            return this;
        }

        public String id() {
            return mappedStatement.id;
        }

        public Builder resultMap(ResultMap resultMap) {
            mappedStatement.resultMap = resultMap;
            return this;
        }

        public MappedStatement build() {
            assert mappedStatement.configuration != null;
            assert mappedStatement.id != null;
            assert mappedStatement.sqlSource != null;
            return mappedStatement;
        }
    }

    public StatementType getStatementType() {
        return statementType;
    }

    public void setSqlSource(String sqlSource) {
        this.sqlSource = sqlSource;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getParameterType() {
        return parameterType;
    }

    public boolean isUseGeneratedKeys() {
        return useGeneratedKeys;
    }

    public SqlCommandType getSqlCommandType() {
        return sqlCommandType;
    }

    public String getResource() {
        return resource;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public String getId() {
        return id;
    }

    public String getSqlSource() {
        return sqlSource;
    }

    public ResultMap getResultMap() {
        return resultMap;
    }
}
