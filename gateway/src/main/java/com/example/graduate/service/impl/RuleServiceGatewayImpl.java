package com.example.graduate.service.impl;

import com.example.graduate.cache.GroovyScriptCache;
import com.example.graduate.cache.RuleCache;
import com.example.graduate.config.GroovyDynamicLoader;
import com.example.graduate.pojo.CalculateRule;
import com.example.graduate.service.CalculateRuleService;
import com.example.graduate.service.RuleServiceGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class RuleServiceGatewayImpl implements RuleServiceGateway {

    @Autowired
    private CalculateRuleService calculateRuleService;
    @Resource
    private GroovyDynamicLoader groovyDynamicLoader;
    @Override
    public void deleteRule(CalculateRule calculateRule) {
        //先删缓存后删数据库
        deleteRuleFromCache(calculateRule);
        calculateRuleService.removeById(calculateRule.getId());
    }

    @Override
    public void updateRule(CalculateRule calculateRule) {
        //先删缓存后更新
        deleteRuleFromCache(calculateRule);
        calculateRuleService.updateById(calculateRule);
    }
    /**
     * 删缓存
     *
     * @param calculateRule 指定脚本
     */
    @Override
    public void deleteRuleFromCache(CalculateRule calculateRule) {
        //bean缓存删
        groovyDynamicLoader.destroyBeanDefinition(calculateRule);
        //invoke缓存删
        GroovyScriptCache.removeMap(calculateRule.getInterfaceId());
        //GroovyClassLoader缓存删
        RuleCache.removeMap(calculateRule.getInterfaceId());
    }
}
