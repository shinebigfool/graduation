package com.example.graduate.cache;

import com.example.graduate.bean.BeanName;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
public class BeanNameCache {
    /**
     * 脚本列表
     */
    private static ConcurrentMap<String, String> beanNameMap = new ConcurrentHashMap<>();
    /**
     * 缓存脚本id和脚本名称
     * 缓存每次更新都是先把原来的全清了再重新put
     * 带有查重功能，
     * @param beanNameList
     */
    public static void put2map(List<BeanName> beanNameList) {
        // 先清空
        if (!beanNameMap.isEmpty()) {
            beanNameMap.clear();
        }
        for (BeanName beanName : beanNameList) {
            if (!beanNameMap.containsKey(beanName.getInterfaceId())) {
                beanNameMap.put(beanName.getInterfaceId(), beanName.getBeanName());
            } else {
                log.warn("found duplication interfaceId:" + beanName.getInterfaceId());
            }
        }
    }
        public static void removeMap(String key){
        beanNameMap.remove(key);
    }
    public static String getByInterfaceId(String interfaceId) {
        return beanNameMap.get(interfaceId);
    }
    public static void listMap(){
        System.out.println(beanNameMap.keySet());
    }
}
