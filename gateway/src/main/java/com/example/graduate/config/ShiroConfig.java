package com.example.graduate.config;

import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {
    @Value("${swagger.enable}")
    private boolean swaggerEnable;
    @Bean // shiroFilter 过滤器
    public ShiroFilterFactoryBean shirFilter(@Qualifier("securityManager")SecurityManager securityManager) {
        System.out.println("====ShiroConfiguration.shirFilter()====");
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        // 如果不设置默认会自动寻找Web工程根目录下的"/login"页面
        shiroFilterFactoryBean.setLoginUrl("/");// 设置首页
        // 登录成功后要跳转的链接
        // shiroFilterFactoryBean.setSuccessUrl("/login/home");
        // // 未授权界面;
        shiroFilterFactoryBean.setUnauthorizedUrl("/shiroError");
        // 添加shiro内置过滤器 filterChainDefinitionMap
        // authc:所有url都必须认证通过才可以访问; anon:所有url都都可以匿名访问
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();
        // // 配置不会被拦截的链接 顺序判断
        filterChainDefinitionMap.put("/user/test","anon");
//        filterChainDefinitionMap.put("/user/page","authc");
        filterChainDefinitionMap.put("/user/findUser","authc");
        if (swaggerEnable){
            filterChainDefinitionMap.put("/swagger-ui.html**", "anon");
            filterChainDefinitionMap.put("/swagger-resources/**", "anon");
            filterChainDefinitionMap.put("/webjars/**", "anon");
            filterChainDefinitionMap.put("/swagger-ui.html", "anon");
            filterChainDefinitionMap.put("/v2/**", "anon");
        }



        // <!-- 过滤链定义，从上向下顺序执行，一般将/**放在最为下边 -->:这是一个坑呢，一不小心代码就不好使了;


        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        //自定义拦截器  key:名称 value:filter(过滤器)
        Map<String, Filter> filtersMap = new LinkedHashMap<String, Filter>();
        //限制同一帐号同时在线的个数。

        //设置自定义过滤器到shiro过滤器工厂
        shiroFilterFactoryBean.setFilters(filtersMap);
        return shiroFilterFactoryBean;

    }

    /**
     * 凭证匹配器 （由于我们的密码校验交给Shiro的SimpleAuthenticationInfo进行处理了 ）
     *
     * @return
     */
    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        // 散列算法:这里使用MD5算法;
        hashedCredentialsMatcher.setHashAlgorithmName("md5");
        // 散列的次数，比如散列两次 md5(md5(""));
        hashedCredentialsMatcher.setHashIterations(2);
        return hashedCredentialsMatcher;
    }


    @Bean
    public ShiroRealm getRealm(){
        ShiroRealm realm = new ShiroRealm();
        //设置MD5加密规则  在Realm登录认证的时候会加密用户输入的密码,是否和保存DB的密码一致
        realm.setCredentialsMatcher(hashedCredentialsMatcher());
        return realm;
    }
    /**
     * cookie对象; rememberMeCookie()方法是设置Cookie的生成模版，比如cookie的name，cookie的有效时间等等。
     *
     * @return
     */
    @Bean
    public SimpleCookie rememberMeCookie() {
        // 这个参数是cookie的名称，对应前端的checkbox的name = rememberMe
        SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
        simpleCookie.setSecure(true);
        // 只能通过httpservlet访问,无法通过js脚本将无法读取到cookie信息
        simpleCookie.setHttpOnly(true);
        // <!-- 记住我cookie生效时间30天 ,单位秒;-->
        simpleCookie.setMaxAge(60 * 10);
        return simpleCookie;
    }

//    public CookieRememberMeManager rememberMeManager() {
//        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
//        cookieRememberMeManager.setCookie(rememberMeCookie());
//        cookieRememberMeManager.setCipherKey("NIXINYAN".getBytes());
//        return cookieRememberMeManager;
//    }
//    @Bean
    public CookieRememberMeManager cookieRememberMeManager() {
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
        simpleCookie.setMaxAge(259200000);
        cookieRememberMeManager.setCookie(simpleCookie);
        cookieRememberMeManager.setCipherKey(Base64.decode("6ZmI6I2j5Y+R5aSn5ZOlAA=="));
        return cookieRememberMeManager;
    }

    @Bean
    public SecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();

        securityManager.setRealm(getRealm());
        securityManager.setRememberMeManager(cookieRememberMeManager());
        return securityManager;
    }

    /**
     * 开启Shiro注解(如@RequiresRoles,@RequiresPermissions),
     * 需借助SpringAOP扫描使用Shiro注解的类,并在必要时进行安全逻辑验证
     * 配置以下两个bean(DefaultAdvisorAutoProxyCreator和AuthorizationAttributeSourceAdvisor)
     */
    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        defaultAdvisorAutoProxyCreator.setProxyTargetClass(true);
        return defaultAdvisorAutoProxyCreator;
    }

    /**
     * 开启aop注解支持,解决spingboot环境中使用Shiro注解(如@RequiresRoles,@RequiresPermissions)无效
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(
            @Qualifier("securityManager") SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor sourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        sourceAdvisor.setSecurityManager(securityManager);
        return sourceAdvisor;
    }


}
