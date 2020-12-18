package com.example.graduate.cache;

import com.example.graduate.bean.GroovyInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
/**
 * @author 倪鑫彦
 * @description GroovyInfo的缓存
 * @since 9:21 2020/12/12
 */
@Slf4j
public class GroovyInnerCache {
    /**
     * 脚本列表
     */
    private static ConcurrentMap<String, GroovyInfo> groovyMap = new ConcurrentHashMap<>();
    /**
     * 把脚本缓存一下
     *
     * @param groovyList
     */
    public static void put2map(List<GroovyInfo> groovyList) {

        if (!groovyMap.isEmpty()) {
            groovyMap.clear();
        }
        for (GroovyInfo groovyInfo : groovyList) {
            String scriptName = groovyInfo.getClassName();
            if (!groovyMap.containsKey(scriptName)) {
                groovyMap.put(scriptName, groovyInfo);
            } else {
                // 发现重名groovy脚本
                log.warn("found duplication groovy script:" + groovyInfo);
            }
        }
    }

    /**
     * 更新map
     *
     * @param groovyInfos
     */
    public static void update2map(List<GroovyInfo>[] groovyInfos) {
        List<GroovyInfo> addedGroovyInfos = groovyInfos[0];
        List<GroovyInfo> updatedGroovyInfos = groovyInfos[1];
        List<GroovyInfo> deletedGroovyInfos = groovyInfos[2];
        addMap(addedGroovyInfos);
        addMap(updatedGroovyInfos);
        removeMap(deletedGroovyInfos);
    }

    /**
     * 新增
     *
     * @param groovyList
     */
    private static void addMap(List<GroovyInfo> groovyList) {
        for (GroovyInfo groovyInfo : groovyList) {
            groovyMap.put(groovyInfo.getClassName(), groovyInfo);
        }
    }

    /**
     * 删除
     *
     * @param groovyList
     */
    public static void removeMap(List<GroovyInfo> groovyList) {
        for (GroovyInfo groovyInfo : groovyList) {
            groovyMap.remove(groovyInfo.getClassName());
        }
    }

    public static void removeMap(String key){
        groovyMap.remove(key);
    }
    /**
     * 根据名称获取脚本信息
     *
     * @param scriptName
     * @return
     */
    public static GroovyInfo getByName(String scriptName) {
        return groovyMap.get(scriptName);
    }

    /**
     * 获取已经加载的全部脚本类名
     *
     * @return
     */
    public static Map<String, GroovyInfo> getGroovyInfos() {
        return groovyMap;
    }

    public static void listMap(){
        System.out.println(groovyMap.keySet());
    }
}
