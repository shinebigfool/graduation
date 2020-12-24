package com.example.graduate.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.graduate.mappers.UserMapper;
import com.example.graduate.pojo.User;
import com.example.graduate.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{

    @Override
    public int qryTotalRow(Map<String, Object> params) {
        return this.baseMapper.qryTotalRow(params);
    }

    @Override
    public int qryTotal(Map<String, Object> params) {
        return this.baseMapper.qryTotal(params);
    }

    @Override
    public List<User> qryUserPage(Page<User> page, String name, int cid, String uname) {
        return this.baseMapper.qryUserPage(page,name,cid,uname);
    }

}
