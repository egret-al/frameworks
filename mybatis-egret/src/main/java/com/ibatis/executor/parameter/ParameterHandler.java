package com.ibatis.executor.parameter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 用于处理SQL中的参数
 * @author：rkc
 * @date：Created in 2021/6/12 15:40
 * @description：
 */
public interface ParameterHandler {

    Object getParameterObject();

    void setParameters(PreparedStatement preparedStatement) throws SQLException;
}
