package com.example.graduate.service;

import com.example.graduate.dto.DTO;
import com.example.graduate.dto.ListDTO;
import com.example.graduate.pojo.AffairLog;

public interface AffairLogServiceGateway {
    DTO delByAffairId(Integer affairId);
    DTO addAffairLog(AffairLog affairLog);
    ListDTO<AffairLog> qryAffairLogByAffairId(Integer affairId);
}
