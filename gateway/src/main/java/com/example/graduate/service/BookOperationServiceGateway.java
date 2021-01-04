package com.example.graduate.service;

import com.example.graduate.dto.PageDTO;
import com.example.graduate.pojo.BookOperationLog;

import java.util.Map;

public interface BookOperationServiceGateway {
    PageDTO<BookOperationLog> qryLog(Map<String,Object> params);
}
