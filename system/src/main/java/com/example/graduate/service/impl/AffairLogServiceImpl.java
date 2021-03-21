package com.example.graduate.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.graduate.mappers.AffairLogMapper;
import com.example.graduate.pojo.AffairLog;
import com.example.graduate.service.AffairLogService;
import org.springframework.stereotype.Service;

@Service
public class AffairLogServiceImpl extends ServiceImpl<AffairLogMapper, AffairLog> implements AffairLogService {
}
