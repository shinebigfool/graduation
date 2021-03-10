package com.example.graduate.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.graduate.pojo.Book;
import com.example.graduate.pojo.BookFavorite;
import com.example.graduate.pojo.BookFavoriteCount;

import java.util.List;
import java.util.Map;

public interface BookFavoriteService extends IService<BookFavorite> {
    List<Book> qryFavoriteBook(Map<String,Object> params);
    List<BookFavoriteCount> favoriteCount();
    List<Book> qryFavoriteByPage(Page<Book> page,
                                 String title,
                                 String author,
                                 int cid,
                                 String uploadPerson,
                                 String name);
    int countFavorite(Map<String,Object> params);
}
