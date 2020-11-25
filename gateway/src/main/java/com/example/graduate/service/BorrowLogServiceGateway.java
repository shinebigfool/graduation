package com.example.graduate.service;

import com.example.graduate.dto.DTO;
import com.example.graduate.dto.ListDTO;
import com.example.graduate.dto.PageDTO;
import com.example.graduate.dto.PersonalBorrowInfoDTO;
import com.example.graduate.pojo.BorrowLog;
import com.example.graduate.pojo.BorrowLogDetail;

import java.util.Map;

public interface BorrowLogServiceGateway {
    DTO lendBook(int bid);

    DTO returnBook(int bid);

    ListDTO qryBorrowLog(Map<String,Object> params);

    PageDTO<BorrowLogDetail> qryWholeLog(Map<String,Object> params);

    PersonalBorrowInfoDTO qryPersonalBorrowInfo();
}
