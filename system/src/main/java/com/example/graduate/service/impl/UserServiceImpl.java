package com.example.graduate.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.graduate.dto.UserDTO;
import com.example.graduate.mappers.UserMapper;
import com.example.graduate.pojo.User;
import com.example.graduate.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{
    @Override
    public List<UserDTO> qryUsersByPage(Page<UserDTO> page, String name) {
        return this.baseMapper.qryUsersByPage(page,name);
    }

    @Override
    public int qryTotalRow(Map<String, Object> params) {
        return this.baseMapper.qryTotalRow(params);
    }
}
