package com.test.mapper;

import com.test.entity.User;

import java.util.List;

/**
 * @author rkc
 * @date 2021/3/18 20:11
 */
public interface UserMapper {

    List<User> selectAll();
}
