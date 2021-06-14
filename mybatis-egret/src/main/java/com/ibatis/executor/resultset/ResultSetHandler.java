package com.ibatis.executor.resultset;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * 进行数据集（ResultSet）的封装返回处理，内容比较复杂
 * @author：rkc
 * @date：Created in 2021/6/11 21:15
 * @description：
 */
public interface ResultSetHandler {

    int RETURN_NOT_NULL = 1;
    int RETURN_NULL = 0;

    //将结果集转化为list
    List<Object> handleResultSets(Statement stmt) throws SQLException;
}
