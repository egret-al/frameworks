package com.ibatis.config;

import com.ibatis.executor.Executor;
import com.ibatis.executor.SimpleExecutor;
import com.ibatis.executor.parameter.DefaultParameterHandler;
import com.ibatis.executor.parameter.ParameterHandler;
import com.ibatis.executor.resultset.DefaultResultSetHandler;
import com.ibatis.executor.resultset.ResultSetHandler;
import com.ibatis.executor.statement.RoutingStatementHandler;
import com.ibatis.executor.statement.StatementHandler;
import com.ibatis.mapping.ResultMap;
import com.ibatis.session.ExecutorType;

import java.util.*;

/**
 * @author rkc
 * @date 2021/3/18 20:08
 */
public class Configuration {

    /* 存放数据库连接信息 */
    private final Map<String, String> database = new HashMap<>(4);
    /* 存放mapper文件中所有被MappedStatement封装后的sql */
    private final Map<String, MappedStatement> mappedStatements = new HashMap<>();
    /* 存放<resultMap>标签中的信息 */
    private final Map<String, ResultMap> resultMaps = new HashMap<>();
    /* 存放各个mapper文件的位置 */
    private final List<String> mapperLocations = new ArrayList<>(10);

    private boolean useColumnLabel = true;

    public void addMapperLocation(String location) {
        Objects.requireNonNull(location);
        mapperLocations.add(location);
    }

    public void addProperty(String name, String value) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(value);
        this.database.put(name, value);
    }

    public List<String> getMapperLocations() {
        return mapperLocations;
    }

    public Executor newExecutor(ExecutorType executorType) {
        if (executorType == ExecutorType.SIMPLE) {
            return new SimpleExecutor(this);
        }
        return null;
    }

    public ParameterHandler newParameterHandler(MappedStatement mappedStatement, Object parameterObject) {
        return new DefaultParameterHandler(mappedStatement, parameterObject);
    }

    public ResultSetHandler newResultSetHandler(Executor executor, MappedStatement mappedStatement, ParameterHandler parameterHandler) {
        return new DefaultResultSetHandler(executor, mappedStatement, parameterHandler);
    }

    public StatementHandler newStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameterObject) {
        return new RoutingStatementHandler(executor, mappedStatement, parameterObject);
    }

    public void addResultMap(ResultMap rm) {
        resultMaps.put(rm.getId(), rm);
    }

    public Collection<String> getResultMapNames() {
        return resultMaps.keySet();
    }

    public Collection<ResultMap> getResultMaps() {
        return resultMaps.values();
    }

    public ResultMap getResultMap(String id) {
        return resultMaps.get(id);
    }

    public boolean hasResultMap(String id) {
        return resultMaps.containsKey(id);
    }

    public Map<String, MappedStatement> getMappedStatements() {
        return mappedStatements;
    }

    public String getDriver() {
        return database.get("driver");
    }

    public String getUrl() {
        return database.get("url");
    }

    public String getUsername() {
        return database.get("username");
    }

    public String getPassword() {
        return database.get("password");
    }

    public boolean isUseColumnLabel() {
        return useColumnLabel;
    }
}
