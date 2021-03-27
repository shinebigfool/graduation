package com.example.graduate.service;

import com.example.graduate.pojo.FavoriteHistory;

public interface FavoriteHistoryGateway {
    boolean isExists(String name,int bid);
    boolean add(FavoriteHistory favoriteHistory);
}
