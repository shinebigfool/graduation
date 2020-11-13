package com.example.graduate.utils;

import org.apache.shiro.SecurityUtils;

public class PresentUserUtils {
    public static String qryPresentUserAccount(){
        if(SecurityUtils.getSubject().getPrincipal()==null){
            return "";
        }
        return SecurityUtils.getSubject().getPrincipal().toString();
    }
}
