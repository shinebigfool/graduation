package com.example.graduate.service;

import com.example.graduate.dto.*;
import com.example.graduate.pojo.BookLendCount;
import com.example.graduate.pojo.BorrowLog;
import com.example.graduate.pojo.BorrowLogDetail;

import java.util.Map;

public interface BorrowLogServiceGateway {
    DTO lendBook(int bid);

    DTO returnBook(int bid,String name);

    PageDTO<BorrowLogDetail> qryBorrowLog(Map<String,Object> params);

    PageDTO<BorrowLogDetail> qryWholeLog(Map<String,Object> params);

    PersonalBorrowInfoDTO qryPersonalBorrowInfo();

    BookDTO qryBorrowLogDetail(int id);

    Boolean isInHand(int bid,String userAccount);

    ListDTO<BookLendCount> lendCount();

    BorrowLog qryCurrLog(int bid,String name);
}
