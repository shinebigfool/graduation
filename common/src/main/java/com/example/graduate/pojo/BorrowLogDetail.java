package com.example.graduate.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
@Data
public class BorrowLogDetail {
    private int id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date borrowDate;
    private String userAccount;
    private int bookId;
    private int totalTime;
    private String bookTitle;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date needReturnDate;
    private String note;
    private int state;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date returnDate;
    private String author;
    private int cid;
    private String uploadPerson;
}
