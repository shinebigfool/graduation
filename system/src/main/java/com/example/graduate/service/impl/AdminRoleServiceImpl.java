package com.example.graduate.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.graduate.mappers.AdminRoleMapper;
import com.example.graduate.pojo.AdminRole;
import com.example.graduate.service.AdminRoleService;
import org.springframework.stereotype.Service;

@Service
public class AdminRoleServiceImpl extends ServiceImpl<AdminRoleMapper, AdminRole> implements AdminRoleService {
}
