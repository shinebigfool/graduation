package com.example.graduate.bean;

import lombok.Data;

import java.util.Map;

@Data
public class RuleEngineRequest {
    private String interfaceId;
    private Map<String,Object> params;
}
