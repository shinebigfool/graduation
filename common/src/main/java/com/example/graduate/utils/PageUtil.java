package com.example.graduate.utils;

import com.example.graduate.dto.PageDTO;

import java.util.Map;

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

    /**
     * 分页查询参数模板
     *
     * @param params 分页参数
     * @param pageDTO  pageDTO
     * @return pageSize 页面记录条数
     */
    public static int transParam2Page(Map<String, Object> params, PageDTO pageDTO) {
        int current = StringUtil.objectToInt(params.get("current"));
        int size = StringUtil.objectToInt(params.get("size"));
        int totalRow = StringUtil.objectToInt(params.get("totalRow"));
        pageDTO.setCurPage(current);
        pageDTO.setPageSize(size);

        pageDTO.setTotalRow(totalRow);
        pageDTO.setTotalPage(PageUtil.computeTotalPage(size,totalRow));
        return size;
    }

}
