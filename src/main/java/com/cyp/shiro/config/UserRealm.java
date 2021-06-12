package com.cyp.shiro.config;

import com.cyp.shiro.pojo.User;
import com.cyp.shiro.service.UserLoginService;
import com.cyp.shiro.service.impl.UserLoginServiceImpl;
import com.cyp.shiro.utils.DigestsUtil;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.Set;


/**
 * @Description：
 */

public class UserRealm extends AuthorizingRealm {

    @Autowired
    UserLoginService userLoginService;
    public UserRealm() {
        //指定密码匹配方式sha1
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher(DigestsUtil.SHA1);
        //指定密码迭代此时
        hashedCredentialsMatcher.setHashIterations(DigestsUtil.ITERATIONS);
        //使用父层方法是匹配方式生效
        setCredentialsMatcher(hashedCredentialsMatcher);
    }

    /**
     * @Description 认证方法
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        //获取登录名
        String username = (String) authenticationToken.getPrincipal();
        User user = userLoginService.queryUserByName(username);
        if(user == null){
            return null;
        }
        String salt = user.getSalt();
        String password = user.getPassword();
        return new SimpleAuthenticationInfo(username,password,ByteSource.Util.bytes(salt),getName());
    }

    /**
     * @Description 授权方法
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        //拿到用户凭证信息
        String username = (String) principalCollection.getPrimaryPrincipal();
        //从数据库中查询对应的角色和权限
        String role = userLoginService.queryUserByName(username).getRoles();
        String permission = userLoginService.queryUserByName(username).getPermissions();

        //构建资源校验对象
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        simpleAuthorizationInfo.addRole(role);
        simpleAuthorizationInfo.addStringPermission(permission);
        return simpleAuthorizationInfo;
    }
}
