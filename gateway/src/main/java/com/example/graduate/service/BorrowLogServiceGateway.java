package com.example.graduate.service;

import com.example.graduate.dto.DTO;
import com.example.graduate.dto.ListDTO;

import java.util.Map;

public interface BorrowLogServiceGateway {
    DTO lendBook(int bid);

    DTO returnBook(int bid);

    ListDTO qryBorrowLog(Map<String,Object> params);
}
