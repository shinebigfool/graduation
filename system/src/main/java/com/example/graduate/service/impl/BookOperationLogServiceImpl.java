package com.example.graduate.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.graduate.mappers.BookOperationLogMapper;
import com.example.graduate.pojo.BookOperationLog;
import com.example.graduate.service.BookOperationLogService;
import org.springframework.stereotype.Service;

@Service
public class BookOperationLogServiceImpl extends ServiceImpl<BookOperationLogMapper, BookOperationLog> implements BookOperationLogService {
}
