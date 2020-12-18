package com.example.graduate.cache;

import groovy.lang.Script;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GroovyScriptCache {
    private static Map<String, Script> scriptCache = new ConcurrentHashMap<>();
    public static void put2Map(String key,Script script){
        scriptCache.put(key,script);
    }
    public static void removeMap(String beanId){
        scriptCache.remove(beanId);
    }
    public static void listCache(){
        System.out.println(scriptCache);
    }
    public static Script getById(String id){
        return scriptCache.get(id);
    }
    public static void clear(){
        scriptCache.clear();
    }
}
