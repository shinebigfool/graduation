package com.example.graduate.service;

import com.example.graduate.dto.BookDTO;
import com.example.graduate.dto.DTO;
import com.example.graduate.dto.ListDTO;
import com.example.graduate.dto.PageDTO;
import com.example.graduate.exception.NxyException;
import com.example.graduate.pojo.Book;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

public interface BookServiceGateway {
    PageDTO<Book> qryBook(Map<String,Object> params);
    DTO saveBook(Book book) throws NxyException;
    DTO delBookById(List<Integer> bids) throws NxyException;
    DTO examineBook(Book book) throws NxyException;
    DTO modifyBook(Book book) throws NxyException;
    ListDTO<Book> suggestBook(Map<String,Object> params);
    BookDTO qryBookDetail(int id);
    ListDTO<Book> qryFavoriteBook();
    DTO addFavoriteBook(Book book);
    DTO removeFavoriteBook(Book book);
    Boolean isFavorite(Book book);
}
