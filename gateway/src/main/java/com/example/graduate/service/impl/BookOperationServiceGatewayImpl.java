package com.example.graduate.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.graduate.codeEnum.RetCodeEnum;
import com.example.graduate.dto.PageDTO;
import com.example.graduate.pojo.BookOperationLog;
import com.example.graduate.service.BookOperationLogService;
import com.example.graduate.service.BookOperationServiceGateway;
import com.example.graduate.utils.PageUtil;
import com.example.graduate.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class BookOperationServiceGatewayImpl implements BookOperationServiceGateway {
    @Autowired
    private BookOperationLogService bookOperationLogService;
    @Override
    public PageDTO<BookOperationLog> qryLog(Map<String, Object> params) {
        PageDTO<BookOperationLog> pageDTO = new PageDTO<>(RetCodeEnum.SUCCEED);
        int totalRow = bookOperationLogService.qryTotalRow(params);
        params.put("totalRow",totalRow);
        int size = PageUtil.transParam2Page(params,pageDTO);
        int current = StringUtil.objectToInt(params.get("current"));
        Page<BookOperationLog> page = new Page<>(current,size);
        String opName=StringUtil.parseString(params.get("opName"));
        int state = StringUtil.objectToInt(params.get("state"));
        int type = StringUtil.objectToInt(params.get("type"));
        int bid = StringUtil.objectToInt(params.get("bid"));
        String date = StringUtil.parseString(params.get("date"));
        List<BookOperationLog> list = bookOperationLogService.qryLogPage(page,opName,state,type,date,bid);
        if(list.size()==0){
            return new PageDTO<>(RetCodeEnum.RESULT_EMPTY);
        }
        pageDTO.setRetList(list);
        return pageDTO;
    }
}
