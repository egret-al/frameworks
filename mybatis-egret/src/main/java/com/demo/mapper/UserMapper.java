package com.demo.mapper;

import com.ibatis.annotations.Param;
import com.demo.entity.User;

import java.util.List;

/**
 * @author rkc
 * @date 2021/3/18 20:11
 */
public interface UserMapper {

    List<User> selectAll();

    List<User> selectAll2();

    List<User> selectBySex(String sex);

    User selectUserById(int id);

    int insertUser(User user);

    int deleteUser(@Param("username") String username, @Param("sex") String sex);

    int deleteById(int id);

    int updateUser(User user);
}
