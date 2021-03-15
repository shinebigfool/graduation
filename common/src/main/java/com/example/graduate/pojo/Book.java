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
@TableName("book")
public class Book {
    @TableId(type = IdType.AUTO)
    protected int id;
    protected String cover;
    protected String title;
    protected String author;
    @JsonFormat(pattern = "yyyy-MM-dd")
    protected Date publishDate;
    protected String press;
    protected String abs;
    protected int cid;
    protected int examineState;
    protected int availableState;
    protected String examinePerson;
    protected String examineNote;
    protected String uploadPerson;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    protected Date insertDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    protected Date updateDate;
    protected String onlineUrl;

}
