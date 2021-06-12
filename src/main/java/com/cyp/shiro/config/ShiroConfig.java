package com.cyp.shiro.config;



import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.cyp.shiro.filter.*;
import org.springframework.context.annotation.DependsOn;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class ShiroConfig {

    @Autowired
    JwtTokenManager jwtTokenManager;

//    @Bean
//    public ShiroFilterFactoryBean getShiroFilterFactoryBea(@Qualifier("securityManager")
//                                                                   DefaultWebSecurityManager defaultWebSecurityManager){
//        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
////       设置安全管理器
//        bean.setSecurityManager(defaultWebSecurityManager);
//        Map<String,String > filterMap = new LinkedHashMap<>();
//        //授权，正常情况下，没有授权会跳转到未授权页面
//        filterMap.put("/user/update","perms[list:update]");
//        filterMap.put("/user/insert","perms[list:insert]");
//        filterMap.put("/admin/**","roles[admin]");
//        filterMap.put("/admin/insert","perms[list:insert:update]");
//        filterMap.put("/admin/update","perms[list:insert:update]");
//        //拦截
//        //filterMap.put("/admin/**","authc");
//        //filterMap.put("/user/**","authc");
//        //配置退出 过滤器,其中的具体的退出代码Shiro已经替我们实现了
//        filterMap.put("/logout", "logout");
//        bean.setFilterChainDefinitionMap(filterMap);
//        //设置登陆的请求
//        bean.setLoginUrl("/login1");
//        //未授权页面
//        bean.setUnauthorizedUrl("/noauthorized");
//        return bean;
//    }

//    @Bean(name = "securityManager")
//    public DefaultWebSecurityManager getDefaultWebSecurityManager(@Qualifier("userRealm") UserRealm userRealm){
//        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
////        关联UserRealm
//        securityManager.setRealm(userRealm);
//        securityManager.setSessionManager(shiroSessionManager());
//        /*
//         * 关闭shiro自带的session
//         */
//        DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
//        DefaultSessionStorageEvaluator defaultSessionStorageEvaluator = new DefaultSessionStorageEvaluator();
//        defaultSessionStorageEvaluator.setSessionStorageEnabled(false);
//        subjectDAO.setSessionStorageEvaluator(defaultSessionStorageEvaluator);
//        securityManager.setSubjectDAO(subjectDAO);
//
//        return securityManager;
//    }



    /**
     * @Description 权限管理器
     * @param
     * @return
     */
    @Bean(name="securityManager")
    public DefaultWebSecurityManager defaultWebSecurityManager(){
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(userRealm());
        securityManager.setSessionManager(shiroSessionManager());
        return securityManager;
    }

//    创建Realm对象
    @Bean
    public UserRealm userRealm(){
        return new UserRealm();
    }


    /**
     * @Description 会话管理器
     */
    @Bean(name="sessionManager")
    public DefaultWebSessionManager shiroSessionManager(){
        ShiroSessionManager sessionManager = new ShiroSessionManager();
        sessionManager.setSessionValidationSchedulerEnabled(false);
        sessionManager.setSessionIdCookieEnabled(true);
        return sessionManager;
    }
    /**
     * @Description 保证实现了Shiro内部lifecycle函数的bean执行
     */
    @Bean(name = "lifecycleBeanPostProcessor")
    public static LifecycleBeanPostProcessor getLifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }
    /**
     * @Description AOP式方法级权限检查
     */
    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        defaultAdvisorAutoProxyCreator.setProxyTargetClass(true);
        return defaultAdvisorAutoProxyCreator;
    }
    /**
     * @Description 配合DefaultAdvisorAutoProxyCreator事项注解权限校验
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor getAuthorizationAttributeSourceAdvisor() {
        AuthorizationAttributeSourceAdvisor aasa = new AuthorizationAttributeSourceAdvisor();
        aasa.setSecurityManager(defaultWebSecurityManager());
        return new AuthorizationAttributeSourceAdvisor();
    }
    /**
     * @Description 自定义拦截器定义
     */
    private Map<String, Filter> filters() {
        Map<String, Filter> map = new HashMap<String, Filter>();
        map.put("jwt-authc", new JwtAuthcFilter(jwtTokenManager));
        map.put("jwt-perms", new JwtPermsFilter());
        map.put("jwt-roles", new JwtRolesFilter());
        return map;
    }
    /**
     * @Description 拦截器链
     */
    private Map<String, String> filterChainDefinition(){
                Map<String,String > filterMap = new LinkedHashMap<>();
        //授权，正常情况下，没有授权会跳转到未授权页面
        filterMap.put("/user/update","jwt-perms[list:update]");
        filterMap.put("/user/insert","jwt-perms[list:insert]");
        filterMap.put("/admin/*","jwt-roles[admin]");
        filterMap.put("/admin/insert","jwt-perms[list:insert:update]");
        filterMap.put("/admin/update","jwt-perms[list:insert:update]");
        //拦截
        filterMap.put("/admin/**","jwt-authc");
        filterMap.put("/user/**","jwt-authc");
        //配置退出 过滤器,其中的具体的退出代码Shiro已经替我们实现了
        filterMap.put("/logout", "logout");
//
        return filterMap;
    }

    /**
     * @Description Shiro过滤器
     */
    @Bean("shiroFilter")
    public ShiroFilterFactoryBean shiroFilterFactoryBean(){
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(defaultWebSecurityManager());
        //使自定义拦截器生效
        shiroFilter.setFilters(filters());
        shiroFilter.setFilterChainDefinitionMap(filterChainDefinition());
        shiroFilter.setLoginUrl("/login1");
        shiroFilter.setUnauthorizedUrl("/noauthorized");
        return shiroFilter;
    }

}
