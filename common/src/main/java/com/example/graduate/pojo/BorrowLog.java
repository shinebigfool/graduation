package com.example.graduate.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Data
@TableName("borrow_log")
public class BorrowLog {
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

    public void setLog(Date borrowDate,String userAccount,int bookId,String bookTitle,Date needReturnDate){
        this.borrowDate = borrowDate;
        this.userAccount = userAccount;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.state = 2;
        this.needReturnDate = needReturnDate;
    }
}
