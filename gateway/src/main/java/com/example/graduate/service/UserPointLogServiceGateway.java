package com.example.graduate.service;

import com.example.graduate.dto.DTO;
import com.example.graduate.dto.ListDTO;
import com.example.graduate.dto.PageDTO;
import com.example.graduate.pojo.UserPointLog;

import java.util.Map;

public interface UserPointLogServiceGateway {
    ListDTO<UserPointLog> qryLog(String name);
    DTO addLog(UserPointLog userPointLog);
    PageDTO<UserPointLog> qryLogByPage(Map<String,Object> params);
}
