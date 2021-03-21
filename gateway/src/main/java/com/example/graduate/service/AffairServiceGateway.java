package com.example.graduate.service;

import com.example.graduate.dto.DTO;
import com.example.graduate.dto.PageDTO;
import com.example.graduate.pojo.Affair;

import java.util.Map;

public interface AffairServiceGateway {
    DTO addAffair(Affair affair);
    DTO modAffair(Affair affair);
    DTO delAffair(int id);
    PageDTO<Affair> qryAffair(Map<String,Object> params);
}
