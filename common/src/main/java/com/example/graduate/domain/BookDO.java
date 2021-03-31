package com.example.graduate.domain;

import com.example.graduate.pojo.Book;
import lombok.Data;

@Data
public class BookDO extends Book {
    private int aviPoint;
    private int readNum;
    private int favoriteNum;
}
