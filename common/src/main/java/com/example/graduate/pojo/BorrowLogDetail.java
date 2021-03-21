package com.example.graduate.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
@Data
public class BorrowLogDetail {
    @TableId(type = IdType.AUTO)
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
    private int overDue;
    private int point;
}
