package com.example.graduate.utils;

import com.example.graduate.cache.GroovyScriptCache;
import com.example.graduate.cache.RuleCache;
import com.example.graduate.codeEnum.RetCodeEnum;
import com.example.graduate.dto.DTO;
import com.example.graduate.pojo.CalculateRule;
import com.example.graduate.service.CalculateParser;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

import java.util.Map;

public class GroovyScriptUtil {
    private static final GroovyShell groovyShell = new GroovyShell();

    //读取脚本运行并缓存
    public static DTO invoke(CalculateRule calculateRule, Map<String, Object> params) throws Exception {
        Script script;
        String scriptText = calculateRule.getCalculateRule();
        String cacheKey = calculateRule.getInterfaceId();
        script = GroovyScriptCache.getById(cacheKey);

        if (script == null) {
            script = groovyShell.parse(scriptText);

            DTO dto = invoke(script, params);
            if(dto!=null){
                GroovyScriptCache.put2Map(cacheKey, script);
            }
            return dto;
        }
//        Binding binding = new Binding();
//        binding.setVariable("params",params);
//        binding.setVariable("newConcat",script);
//        script.setBinding(binding);
        return invoke(script,params);
    }

    public static DTO invoke(Script script, Map<String, Object> params) {
        return (DTO) script.invokeMethod("parse2DTO", params);
    }
    public static DTO invokeByGroovyClassLoader(CalculateRule calculateRule,Map<String,Object> params) throws IllegalAccessException, InstantiationException {
        CalculateParser groovyScript=RuleCache.getById(calculateRule.getInterfaceId());
        if(groovyScript!=null){
            return groovyScript.parse2DTO(params);
        }
        GroovyClassLoader groovyLoader = new GroovyClassLoader();
        Class<CalculateParser> groovyClass = (Class<CalculateParser>) groovyLoader.parseClass(calculateRule.getCalculateRule());
        groovyScript = groovyClass.newInstance();
        RuleCache.put2Map(calculateRule.getInterfaceId(),groovyScript);
//        GroovyScriptCache.put2Map(calculateRule.getInterfaceId(),groovyScript);
        return groovyScript.parse2DTO(params);
    }
    public static DTO invokeByGroovyClassLoader(CalculateParser script,Map<String,Object> params){
        return script.parse2DTO(params);
    }
}
