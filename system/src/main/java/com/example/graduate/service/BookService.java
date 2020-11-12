package com.example.graduate.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.graduate.pojo.Book;

import java.util.List;
import java.util.Map;

public interface BookService extends IService<Book> {
    int qryTotalRow(Map<String,Object> params);
    List<Book> qryBookByPage(Page<Book> page, String title,String author,int cid,
                             int examineState,int availableState,String examinePerson,
                             String uploadPerson);
}
