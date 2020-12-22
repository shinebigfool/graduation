package com.example.graduate.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.graduate.mappers.UserClassMapper;
import com.example.graduate.pojo.UserClass;
import com.example.graduate.service.UserClassService;
import org.springframework.stereotype.Service;

@Service
public class UserClassServiceImpl extends ServiceImpl<UserClassMapper, UserClass> implements UserClassService {
}
