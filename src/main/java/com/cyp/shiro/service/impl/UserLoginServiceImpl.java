package com.cyp.shiro.service.impl;

import com.cyp.shiro.mapper.UserLoginMapper;
import com.cyp.shiro.pojo.User;
import com.cyp.shiro.service.UserLoginService;
import com.cyp.shiro.utils.DigestsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserLoginServiceImpl implements UserLoginService {

    @Autowired
    UserLoginMapper userLoginMapper;

    DigestsUtil digestsUtil = new DigestsUtil();
    @Override
    public User queryUserByName(String username) {
        return userLoginMapper.queryUserByUserName(username);
    }

    @Override
    public int insertUser(User user) {
        Map<String,String> map = digestsUtil.entryptPassword(user.getPassword());
        user.setPassword(map.get("password"));
        user.setSalt(map.get("salt"));
        return userLoginMapper.insertUser(user);
    }

}
