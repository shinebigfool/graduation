package com.example.graduate.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.graduate.mappers.AdminMenuMapper;
import com.example.graduate.pojo.AdminMenu;
import com.example.graduate.service.AdminMenuService;
import org.springframework.stereotype.Service;

@Service
public class AdminMenuServiceImpl extends ServiceImpl<AdminMenuMapper, AdminMenu> implements AdminMenuService{
}
