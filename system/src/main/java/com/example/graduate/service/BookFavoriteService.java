package com.example.graduate.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.graduate.pojo.Book;
import com.example.graduate.pojo.BookFavorite;
import com.example.graduate.pojo.BookFavoriteCount;

import java.util.List;
import java.util.Map;

public interface BookFavoriteService extends IService<BookFavorite> {
    List<Book> qryFavoriteBook(Map<String,Object> params);
    List<BookFavoriteCount> favoriteCount();
}
