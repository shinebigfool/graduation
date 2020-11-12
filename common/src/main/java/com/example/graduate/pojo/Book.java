package com.example.graduate.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Data
@TableName("book")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String cover;
    private String title;
    private String author;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date publishDate;
    private String press;
    private String abs;
    private int cid;
    private int examineState;
    private int availableState;
    private String examinePerson;
    private String examineNote;
    private String uploadPerson;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date insertDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateDate;

}
