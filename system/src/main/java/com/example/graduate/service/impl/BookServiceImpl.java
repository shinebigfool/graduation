package com.example.graduate.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.graduate.mappers.BookMapper;
import com.example.graduate.pojo.Book;
import com.example.graduate.service.BookService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class BookServiceImpl extends ServiceImpl<BookMapper, Book> implements BookService {
    @Override
    public int qryTotalRow(Map<String, Object> params) {
        return this.baseMapper.qryTotalRow(params);
    }

    @Override
    public List<Book> qryBookByPage(Page<Book> page,String title,String author,int cid,
                                    int examineState,int availableState,String examinePerson,
                                    String uploadPerson) {
        return this.baseMapper.qryBookByPage(page,title,author,cid,examineState,
                availableState,examinePerson,uploadPerson);
    }
}
