package com.example.graduate.service;

import com.example.graduate.pojo.CalculateRule;

public interface RuleServiceGateway {

    void deleteRule(CalculateRule calculateRule);
    void updateRule(CalculateRule calculateRule);
    void deleteRuleFromCache(CalculateRule calculateRule);
}
