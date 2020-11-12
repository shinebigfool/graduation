package com.example.graduate.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.graduate.mappers.BorrowLogMapper;
import com.example.graduate.pojo.BorrowLog;
import com.example.graduate.service.BorrowLogService;
import org.springframework.stereotype.Service;

@Service
public class BorrowLogServiceImpl extends ServiceImpl<BorrowLogMapper, BorrowLog> implements BorrowLogService {
}
