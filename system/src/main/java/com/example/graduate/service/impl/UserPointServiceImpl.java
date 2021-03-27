package com.example.graduate.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.graduate.mappers.UserPointMapper;
import com.example.graduate.pojo.UserPoint;
import com.example.graduate.service.UserPointService;
import org.springframework.stereotype.Service;

@Service
public class UserPointServiceImpl extends ServiceImpl<UserPointMapper, UserPoint> implements UserPointService {
}
