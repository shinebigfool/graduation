package com.example.graduate.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.graduate.pojo.BookOperationLog;

import java.util.List;
import java.util.Map;

public interface BookOperationLogService extends IService<BookOperationLog> {
    int qryTotalRow(Map<String,Object> params);
    List<BookOperationLog> qryLogPage(Page<BookOperationLog> page, String opName,
                                      int state, int type, String date,int bid);
}
