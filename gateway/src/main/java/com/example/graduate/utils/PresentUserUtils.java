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
    public static boolean checkAdminRole(){
        Subject subject = SecurityUtils.getSubject();
        return subject.hasRole("admin");
    }
}
