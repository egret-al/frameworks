package com.ibatis.mapping;

import com.ibatis.config.Configuration;

import java.util.*;

/**
 * 结果映射，保存着表与对象之间的映射关系
 * @author：rkc
 * @date：Created in 2021/6/10 14:42
 * @description：
 */
public class ResultMap {

    // 对应<resultMap>的id属性
    private String id;
    // 对应<resultMap>的type属性
    private Class<?> type;
    // 对应除<discriminator>元素外的所有属性映射关系
    private List<ResultMapping> resultMappings;
    // 对应所有属性映射中不带有Constructor标志的映射关系
    private List<ResultMapping> propertyResultMappings;
    // 对应所有属性映射中的column属性的集合
    private Set<String> mappedColumns;
    // 对应所有Java属性映射中的property集合
    private Set<String> mappedProperties;

    private Configuration configuration;

    private ResultMap() {
    }

    public static class Builder {
        private ResultMap resultMap = new ResultMap();

        public Builder(Configuration configuration, String id, Class<?> type, List<ResultMapping> resultMappings) {
            resultMap.configuration = configuration;
            resultMap.id = id;
            resultMap.type = type;
            resultMap.resultMappings = resultMappings;
        }

        public ResultMap build() {
            if (resultMap.id == null) {
                throw new IllegalArgumentException("ResultMaps must have an id");
            }
            resultMap.mappedColumns = new HashSet<>();
            resultMap.mappedProperties = new HashSet<>();
            resultMap.propertyResultMappings = new ArrayList<>();

            for (ResultMapping resultMapping : resultMap.resultMappings) {
                String column = resultMapping.getColumn();
                if (column != null) {
                    //字段名都添加到mappedColumns
                    resultMap.mappedColumns.add(column.toUpperCase(Locale.ENGLISH));
                }
                String property = resultMapping.getProperty();
                if (property != null) {
                    //对应的字段添加到mappedProperties
                    resultMap.mappedProperties.add(property);
                }
            }
            return resultMap;
        }
    }

    public String getId() {
        return id;
    }

    public Class<?> getType() {
        return type;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public List<ResultMapping> getResultMappings() {
        return resultMappings;
    }

    public List<ResultMapping> getPropertyResultMappings() {
        return propertyResultMappings;
    }

    public Set<String> getMappedColumns() {
        return mappedColumns;
    }

    public Set<String> getMappedProperties() {
        return mappedProperties;
    }

    public String getProperty(String column) {
        for (ResultMapping resultMapping : resultMappings) {
            if (resultMapping.getColumn().equalsIgnoreCase(column)) {
                return resultMapping.getProperty();
            }
        }
        return null;
    }

    public String getColumn(String property) {
        for (ResultMapping resultMapping : resultMappings) {
            if (resultMapping.getProperty().equalsIgnoreCase(property)) {
                return resultMapping.getColumn();
            }
        }
        return null;
    }
}
