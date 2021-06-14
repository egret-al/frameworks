package com.ibatis.session;

/**
 * @author rkc
 * @date 2021/3/18 20:12
 */
public interface SqlSessionFactory {

    SqlSession openSession();
}
