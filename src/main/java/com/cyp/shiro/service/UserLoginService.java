package com.cyp.shiro.service;

import com.cyp.shiro.pojo.User;

public interface UserLoginService {

    public User queryUserByName(String username);

    int insertUser(User user);

}
