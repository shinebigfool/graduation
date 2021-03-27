package com.example.graduate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.graduate.annotation.PointLogAnnotation;
import com.example.graduate.pojo.FavoriteHistory;
import com.example.graduate.service.FavoriteHistoryGateway;
import com.example.graduate.service.FavoriteHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FavoriteHistoryGatewayImpl implements FavoriteHistoryGateway {
    @Autowired
    private FavoriteHistoryService favoriteHistoryService;
    @Override
    public boolean isExists(String name,int bid){
        LambdaQueryWrapper<FavoriteHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FavoriteHistory::getName,name).eq(FavoriteHistory::getBid,bid);
        return favoriteHistoryService.count(wrapper) == 1;
    }

    @Override
    public boolean add(FavoriteHistory favoriteHistory) {
        return favoriteHistoryService.save(favoriteHistory);
    }

}
