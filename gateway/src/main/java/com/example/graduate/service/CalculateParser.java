package com.example.graduate.service;

import com.example.graduate.dto.DTO;

import java.util.Map;

public interface CalculateParser {
    DTO parse2DTO(Map<String,Object> params);
}
