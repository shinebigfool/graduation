package com.example.graduate.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.graduate.pojo.User;


import java.util.List;
import java.util.Map;

public interface UserService extends IService<User> {
    int qryTotalRow(Map<String,Object> params);
    int qryTotal(Map<String,Object> params);
    List<User> qryUserPage(Page<User> page,String name,int cid,String uname);
}
