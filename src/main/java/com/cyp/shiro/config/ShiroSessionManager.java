package com.cyp.shiro.config;

import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.cyp.shiro.base.BaseResponse;
import com.cyp.shiro.constant.ShiroConstant;
import com.cyp.shiro.utils.EmptyUtil;
import io.jsonwebtoken.Claims;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * @Description：自定义会话管理器
 */
public class ShiroSessionManager extends DefaultWebSessionManager {

    //从请求中获得sessionId的key
    private static final String AUTHORIZATION = "jwtToken";

    //自定义注入的资源类型名称
    private static final String REFERENCED_SESSION_ID_SOURCE = "Stateless request";

    @Autowired
    JwtTokenManager jwtTokenManager;

    protected Serializable getSessionId(ServletRequest request, ServletResponse response) {
        //判断request请求中是否带有jwtToken的key
        String jwtToken = WebUtils.toHttp(request).getHeader(AUTHORIZATION);
        //如果没有走默认的cook获得sessionId的方式
        if (EmptyUtil.isNullOrEmpty(jwtToken)){
            return super.getSessionId(request, response);
            //如果有走jwtToken获得sessionId的的方式
        }else {
            Claims claims = jwtTokenManager.decodeToken(jwtToken);
            if (claims == null){
                System.out.println("111");
                BaseResponse baseResponse = new BaseResponse(ShiroConstant.TOKEN_TIMEOUT_CODE,ShiroConstant.TOKEN_TIMEOUT_MESSAGE);
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/json; charset=utf-8");
                try {
                    response.getWriter().write(JSONObject.toJSONString(baseResponse));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }else{
                String id = (String) claims.get("jti");
                request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE,
                        REFERENCED_SESSION_ID_SOURCE);
                request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, id);
                request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID, Boolean.TRUE);
                return id;
            }
        }

    }

}
