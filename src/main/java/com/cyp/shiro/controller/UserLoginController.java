package com.cyp.shiro.controller;

import com.alibaba.fastjson.JSONObject;
import com.cyp.shiro.base.BaseResponse;
import com.cyp.shiro.config.JwtTokenManager;
import com.cyp.shiro.constant.ShiroConstant;
import com.cyp.shiro.exception.MyTokenException;
import com.cyp.shiro.pojo.User;
import com.cyp.shiro.service.UserLoginService;
import io.jsonwebtoken.ExpiredJwtException;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UserLoginController {

    @Autowired
    UserLoginService userLoginService;

    @Autowired
    JwtTokenManager jwtTokenManager;

    @RequestMapping("/login")
    public String login(String username, String password){
        //获取当前的用户
        Subject subject = SecurityUtils.getSubject();
        //封装当前用户的登陆数据
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        try {
            subject.login(token); //执行登陆方法，如果没有异常就说明ok了
            return "\"msg\",\"登陆成功\"";
        } catch (UnknownAccountException e) {
            return "\"msg\",\"用户名错误\"";
        } catch (IncorrectCredentialsException e) {
            return "\"msg\",\"密码错误\"";
        }
    }

    /**
     * @Description jwt的json登录方式
     * @param
     * @return
     */
    @RequestMapping("/loginjwt")
    public BaseResponse loginjwt(String username, String password){
        //获取当前的用户
        Subject subject = SecurityUtils.getSubject();
        //封装当前用户的登陆数据

        UsernamePasswordToken token = new UsernamePasswordToken(username, password);

        try {
            //实现登录
            String jwtToken = null;
            subject.login(token); //执行登陆方法，如果没有异常就说明ok了
            //登录完成之后需要颁发令牌
            String sessionId = SecurityUtils.getSubject().getSession().getId().toString();
            User user = new User(username,password);
            user.setPassword("no see");
            Map<String,Object> claims = new HashMap<>();
            claims.put("User", JSONObject.toJSONString(user));
            /*
                Shiro的Session接口有一个setTimeout()方法，登录后，可以用如下方式取得session
                SecurityUtils.getSubject().getSession().setTimeout(1800000);

                设置的最大时间，正负都可以，为负数时表示永不超时。
                SecurityUtils.getSubject().getSession().setTimeout(-1000l);subject.getSession().getTimeout()

                注意：这里设置的时间单位是:ms，但是Shiro会把这个时间转成:s，而且是会舍掉小数部分，这样我设置的是-1ms，转成s后就是0s，马上就过期了。所有要是除以1000以后还是负数，必须设置小于-1000
            * */
            jwtToken = jwtTokenManager.issuedToken("system", 60000, sessionId, claims);
            BaseResponse baseResponse = new BaseResponse( ShiroConstant.LOGIN_SUCCESS_CODE,ShiroConstant.LOGIN_SUCCESS_MESSAGE,jwtToken);
            return baseResponse;
        } catch (UnknownAccountException e) {
            BaseResponse baseResponse = new BaseResponse(ShiroConstant.LOGIN_FAILURE_CODE,ShiroConstant.LOGIN_FAILURE_MESSAGE,"");
            return baseResponse;
        } catch (IncorrectCredentialsException e) {
            BaseResponse baseResponse = new BaseResponse(111, "密码错误", "");
            return baseResponse;
        }
    }


    @RequestMapping("/insert")
    public int insert(User user){
        return userLoginService.insertUser(user);
    }


    @RequestMapping("/listinsert")
    public String listinsert(){
//        //获取当前的用户
        Subject subject = SecurityUtils.getSubject();
//        //-------校验当前用户没有的角色
//        try {
//            subject.checkRole("admin");
//            return "当前用户有admin角色";
//        }catch (Exception ex){
//            return "当前用户没有admin角色";
//        }
        //-------校验当前用户满意的权限
        try {
            subject.checkPermission("list:update:insert");
            return "当前用户有插入的权限";
        }catch (Exception ex){
            return "当前用户没有插入的权限";
        }
    }


    @RequestMapping("/admin/insert")
    public String a(){
        return "/admin/insert"+"|||"+"当前用户角色"+SecurityUtils.getSubject().getPrincipal().toString();
    }
    @RequestMapping("/admin/update")
    public String b(){
        return "/admin/update"+"|||"+"当前用户角色"+SecurityUtils.getSubject().getPrincipal().toString();
    }
//    @RequiresPermissions( "list:insert" )
    @RequestMapping("/user/insert")
    public String c(){
        return "/user/insert"+"|||"+"当前用户角色"+SecurityUtils.getSubject().getPrincipal().toString();
    }
    @RequestMapping("/user/update")
    public String d(){
        try{
        String s = null; //对象s为空(null)
        int length = s.length();//发生空指针异常
        }catch (MyTokenException e){
            throw new MyTokenException();
        }

        return "/user/update"+"|||"+"当前用户角色"+SecurityUtils.getSubject().getPrincipal().toString()+"length";
    }
    @RequestMapping("/noauthorized")
    public String e(){
        return "noauthorized（没有权限）";
    }
    @RequestMapping("/login1")
    public String f(){
        return "login1（登录界面）";
    }
}
