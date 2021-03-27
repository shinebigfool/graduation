package com.example.graduate.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.graduate.mappers.UserPointLogMapper;
import com.example.graduate.pojo.UserPointLog;
import com.example.graduate.service.UserPointLogService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UserPointLogServiceImpl extends ServiceImpl<UserPointLogMapper, UserPointLog> implements UserPointLogService {
    @Override
    public int qryTotal(Map<String, Object> params) {
        return this.baseMapper.qryTotal(params);
    }

    @Override
    public List<UserPointLog> qryLogList(Page<UserPointLog> page, String name) {
        return this.baseMapper.qryLogList(page, name);
    }
}
