package com.cyp.shiro.mapper;

import com.cyp.shiro.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserLoginMapper {

    User queryUserByUserName(String username);

    int insertUser(User user);
}
