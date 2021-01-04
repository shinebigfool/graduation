package com.example.graduate.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.graduate.mappers.BookOperationLogMapper;
import com.example.graduate.pojo.BookOperationLog;
import com.example.graduate.service.BookOperationLogService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class BookOperationLogServiceImpl extends ServiceImpl<BookOperationLogMapper, BookOperationLog> implements BookOperationLogService {
    @Override
    public int qryTotalRow(Map<String, Object> params) {
        return this.baseMapper.qryTotalRow(params);
    }

    @Override
    public List<BookOperationLog> qryLogPage(Page<BookOperationLog> page, String opName, int state, int type, String date, int bid) {
        return this.baseMapper.qryLogPage(page, opName, state, type, date, bid);
    }
}
