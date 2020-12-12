package com.example.graduate.service;

import com.example.graduate.bean.RuleEngineRequest;
import com.example.graduate.dto.DTO;

public interface GroovyParserEngine {
    DTO parse2DTO(RuleEngineRequest request);
}
