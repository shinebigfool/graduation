package com.example.graduate.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Data
@TableName("book_operation_log")
public class BookOperationLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;
    private String operationPerson;
    private int status;
    private int type;
    private int bid;
    private String description;

    public void setSuccessLog(String operationPerson,int status,int type,int bid,String description){
        this.operationPerson = operationPerson;
        this.status = status;
        this.type = type;
        this.bid = bid;
        this.description = description;
    }
}
