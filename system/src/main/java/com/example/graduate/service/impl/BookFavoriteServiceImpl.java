package com.example.graduate.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.graduate.mappers.BookFavoriteMapper;
import com.example.graduate.pojo.BookFavorite;
import com.example.graduate.service.BookFavoriteService;
import org.springframework.stereotype.Service;

@Service
public class BookFavoriteServiceImpl extends ServiceImpl<BookFavoriteMapper, BookFavorite> implements BookFavoriteService {
}
