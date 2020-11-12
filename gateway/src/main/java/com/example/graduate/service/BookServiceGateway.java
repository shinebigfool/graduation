package com.example.graduate.service;

import com.example.graduate.dto.DTO;
import com.example.graduate.dto.PageDTO;
import com.example.graduate.pojo.Book;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

public interface BookServiceGateway {
    PageDTO<Book> qryBook(Map<String,Object> params);
    DTO saveBook(Book book);
    DTO delBookById(List<Integer> bids);
    DTO examineBook(Book book);

}
