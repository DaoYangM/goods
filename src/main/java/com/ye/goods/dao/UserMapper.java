package com.ye.goods.dao;

import com.ye.goods.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    User selectByUsername(String username);

    User login(@Param("username") String username,
               @Param("password") String password);

    int checkUsername(String username);

    int checkEmail(String email);

    String getQuestion(String username);

    String getAnswer(String username);
}