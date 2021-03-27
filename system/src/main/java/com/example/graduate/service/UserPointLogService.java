package com.example.graduate.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.graduate.pojo.UserPointLog;

import java.util.List;
import java.util.Map;

public interface UserPointLogService extends IService<UserPointLog> {
    int qryTotal(Map<String,Object> params);
    List<UserPointLog> qryLogList(Page<UserPointLog> page,String name);
}
