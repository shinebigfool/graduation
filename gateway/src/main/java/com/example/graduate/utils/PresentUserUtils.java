package com.example.graduate.utils;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

public class PresentUserUtils {
    public static String qryPresentUserAccount(){
        if(SecurityUtils.getSubject().getPrincipal()==null){
            return "";
        }
        return SecurityUtils.getSubject().getPrincipal().toString();
    }
    //检查当前登录用户是否为管理员
    public static boolean hasAdminRole(){
        Subject subject = SecurityUtils.getSubject();
        return subject.hasRole("admin");
    }
}
