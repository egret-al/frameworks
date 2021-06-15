package com.ibatis.mapping;

import com.ibatis.config.Configuration;

/**
 * 字段映射，一个ResultMapping实例对应ResultSet中一个字段到JavaBean中一个属性的映射关系
 * sql元素中，除了<discriminator></discriminator>子元素以外的其他元素都会生成此类型的实例
 * 其中包括：<idArg><arg><id><result><association><collection>
 * 内部的Builder类，负责数据的整合和校验，不负责传入参数的解析
 * @author：rkc
 * @date：Created in 2021/6/10 14:42
 * @description：
 */
public class ResultMapping {
    // 核心配置对象
    private Configuration configuration;
    // 属性名，对应元素的property
    private String property;
    // 字段名，对应元素的column
    private String column;
    // 属性的java类型，对应元素的javaType属性
    private Class<?> javaType;
    // 字段的jdbc类型，对应元素的jdbcType属性
    private String jdbcType;

    private ResultMappingType resultMappingType = ResultMappingType.RESULT_MAPPING;

    public static class Builder {
        private ResultMapping resultMapping = new ResultMapping();

        public Builder(Configuration configuration, String property, String column) {
            this(configuration, property);
            resultMapping.column = column;
        }

        public Builder(Configuration configuration, String property, String column, Class<?> javaType) {
            this(configuration, property);
            resultMapping.column = column;
            resultMapping.javaType = javaType;
        }

        public ResultMapping build() {
            return resultMapping;
        }

        public Builder(Configuration configuration, String property) {
            resultMapping.configuration = configuration;
            resultMapping.property = property;
        }

        public Builder resultMappingType(ResultMappingType resultMappingType) {
            resultMapping.resultMappingType = resultMappingType;
            return this;
        }

        public Builder javaType(Class<?> javaType) {
            resultMapping.javaType = javaType;
            return this;
        }

        public Builder jdbcType(String jdbcType) {
            resultMapping.jdbcType = jdbcType;
            return this;
        }
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public String getProperty() {
        return property;
    }

    public ResultMappingType getResultMappingType() {
        return resultMappingType;
    }

    public String getColumn() {
        return column;
    }

    public Class<?> getJavaType() {
        return javaType;
    }

    public String getJdbcType() {
        return jdbcType;
    }
}
