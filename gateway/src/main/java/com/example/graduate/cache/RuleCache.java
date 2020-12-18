package com.example.graduate.cache;

import com.example.graduate.service.CalculateParser;
import groovy.lang.Script;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RuleCache {
    private static Map<String, CalculateParser> ruleCache = new ConcurrentHashMap<>();
    public static void put2Map(String key, CalculateParser script){
        ruleCache.put(key,script);
    }
    public static void removeMap(String beanId){
        ruleCache.remove(beanId);
    }
    public static void listCache(){
        System.out.println(ruleCache.keySet());
    }
    public static CalculateParser getById(String id){
        return ruleCache.get(id);
    }
    public static void clear(){
        ruleCache.clear();
    }
}
