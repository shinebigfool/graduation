package com.example.graduate.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.graduate.pojo.BorrowLog;
import com.example.graduate.pojo.BorrowLogDetail;

import java.util.List;
import java.util.Map;

public interface BorrowLogService extends IService<BorrowLog> {
    List<BorrowLogDetail> qryBorrowLogDetail(Map<String,Object> params);

    int qryTotalRow(Map<String,Object> params);

    List<BorrowLogDetail> qryLogPage(Page<BorrowLogDetail> page,String title,String author,
                               int cid,int borrowState,
                               String uploadPerson,String name);
}
