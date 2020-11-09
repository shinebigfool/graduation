package com.example.graduate.utils;

public class PageUtil {
    /**
     * 计算总页数
     *
     * @param pageSize 页面记录条数
     * @param totalRow 总记录条数
     * @return totalPage 总页数
     */
    public static int computeTotalPage(int pageSize, int totalRow) {
        return totalRow % pageSize == 0 ? totalRow / pageSize : totalRow / pageSize + 1;
    }
}
