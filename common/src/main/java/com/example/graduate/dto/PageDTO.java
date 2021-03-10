package com.example.graduate.dto;


import com.example.graduate.codeEnum.RetCodeEnum;
import com.example.graduate.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class  PageDTO<E> extends DTO {

    private static final long serialVersionUID = 1L;

    private int totalRow;
    private int totalPage;
    private int curPage;
    private int pageSize;
    private List<E> retList = new ArrayList<E>();

    public PageDTO(RetCodeEnum e) {
        super(e);
    }

    public PageDTO() {
        super();
    }
    public PageDTO(String retCode, String retMsg, int pageSize) {
        super(retCode, retMsg);
        this.pageSize = pageSize;
    }
    public PageDTO(String code,String msg){
        super(code,msg);
    }
    @SuppressWarnings("unchecked")
    public void add(Map<String,Object> retMap) {
        this.setRetList((List<E>) retMap.get("list"));
        this.setCurPage(StringUtil.ch2Int(String.valueOf(retMap.get("curPage"))));
        this.setTotalRow(StringUtil.ch2Int(String.valueOf(retMap.get("totalRow"))));
        this.setTotalPage(StringUtil.ch2Int(String.valueOf(retMap.get("totalPage"))));
    }

    public int getTotalRow() {
        return totalRow;
    }

    public void setTotalRow(int totalRow) {
        this.totalRow = totalRow;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getCurPage() {
        return curPage;
    }

    public void setCurPage(int curPage) {
        this.curPage = curPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<E> getRetList() {
        return retList;
    }

    public void setRetList(List<E> list) {
        for(E e : list) {
            this.addItem(e);
        }
    }

    public void setRetListForNew(List<E> list) {
        retList.clear();
        for(E e : list) {
            this.addItem(e);
        }
    }

    private void addItem(E e) {
        if(retList.size() == pageSize) {
            throw new RuntimeException("result list size greater than page size");
        }
        retList.add(e);
    }

}
