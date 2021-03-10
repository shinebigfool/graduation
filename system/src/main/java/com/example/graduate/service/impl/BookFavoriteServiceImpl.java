package com.example.graduate.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.graduate.mappers.BookFavoriteMapper;
import com.example.graduate.pojo.Book;
import com.example.graduate.pojo.BookFavorite;
import com.example.graduate.pojo.BookFavoriteCount;
import com.example.graduate.service.BookFavoriteService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class BookFavoriteServiceImpl extends ServiceImpl<BookFavoriteMapper, BookFavorite> implements BookFavoriteService {
    @Override
    public List<Book> qryFavoriteBook(Map<String, Object> params) {
        return this.baseMapper.qryFavoriteBook(params);
    }

    @Override
    public List<BookFavoriteCount> favoriteCount() {
        return this.baseMapper.favoriteCount();
    }

    @Override
    public List<Book> qryFavoriteByPage(Page<Book> page, String title, String author, int cid, String uploadPerson, String name) {
        return this.baseMapper.qryFavoriteByPage(page, title, author, cid, uploadPerson, name);
    }

    @Override
    public int countFavorite(Map<String, Object> params) {
        return this.baseMapper.countFavorite(params);
    }
}
