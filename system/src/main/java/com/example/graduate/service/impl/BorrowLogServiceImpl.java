package com.example.graduate.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.graduate.mappers.BorrowLogMapper;
import com.example.graduate.pojo.BookLendCount;
import com.example.graduate.pojo.BorrowLog;
import com.example.graduate.pojo.BorrowLogDetail;
import com.example.graduate.service.BorrowLogService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class BorrowLogServiceImpl extends ServiceImpl<BorrowLogMapper, BorrowLog> implements BorrowLogService {
    @Override
    public List<BorrowLogDetail> qryBorrowLogDetail(Map<String, Object> params) {
        return this.baseMapper.qryBorrowLogDetail(params);
    }

    @Override
    public int qryTotalRow(Map<String, Object> params) {
        return this.baseMapper.qryTotalRow(params);
    }

    @Override
    public List<BorrowLogDetail> qryLogPage(Page<BorrowLogDetail> page, String title, String author,
                                      int cid, int borrowState,
                                      String uploadPerson, String name) {
        return this.baseMapper.qryLogPage(page,title,author,cid,borrowState,uploadPerson,name);
    }

    @Override
    public List<BookLendCount> lendCount() {
        return this.baseMapper.lendCount();
    }
}
