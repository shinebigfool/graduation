package com.example.graduate.service;

import com.example.graduate.domain.BookDO;
import com.example.graduate.dto.BookDTO;
import com.example.graduate.dto.DTO;
import com.example.graduate.dto.ListDTO;
import com.example.graduate.dto.PageDTO;
import com.example.graduate.exception.NxyException;
import com.example.graduate.pojo.Book;
import com.example.graduate.pojo.BookFavoriteCount;

import java.util.Map;

public interface BookServiceGateway {
    PageDTO<Book> qryBook(Map<String,Object> params);
    PageDTO<BookDO> qryMyBook(Map<String,Object> params);
    DTO saveBook(Book book) throws NxyException;
    DTO delBookById(int bid) throws NxyException;
    DTO examineBook(Book book) throws NxyException;
    DTO modifyBook(Book book) throws NxyException;
    ListDTO<Book> suggestBook(Map<String,Object> params);
    BookDTO qryBookDetail(int id);
    ListDTO<Book> qryFavoriteBook();
    DTO addFavoriteBook(Book book);
    DTO removeFavoriteBook(Book book);
    Boolean isFavorite(Book book);
    PageDTO<Book> qryFavoriteBook(Map<String,Object> params);
    ListDTO<BookFavoriteCount> favoriteCount();
    int qryFavoriteNum(int bid);
    int borrowNum(int bid);
}
