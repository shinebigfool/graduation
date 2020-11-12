package com.example.graduate.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.graduate.mappers.AdminRoleMenuMapper;
import com.example.graduate.pojo.AdminRoleMenu;
import com.example.graduate.service.AdminRoleMenuService;
import org.springframework.stereotype.Service;

@Service
public class AdminRoleMenuServiceImpl extends ServiceImpl<AdminRoleMenuMapper, AdminRoleMenu> implements AdminRoleMenuService {
}
