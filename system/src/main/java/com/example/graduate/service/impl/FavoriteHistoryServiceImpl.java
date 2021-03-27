package com.example.graduate.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.graduate.mappers.FavoriteHistoryMapper;
import com.example.graduate.pojo.FavoriteHistory;
import com.example.graduate.service.FavoriteHistoryService;
import org.springframework.stereotype.Service;

@Service
public class FavoriteHistoryServiceImpl extends ServiceImpl<FavoriteHistoryMapper, FavoriteHistory> implements FavoriteHistoryService {
}
