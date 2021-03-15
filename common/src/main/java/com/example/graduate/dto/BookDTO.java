package com.example.graduate.dto;

import com.example.graduate.codeEnum.RetCodeEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class BookDTO extends DTO{
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
    //当前登录用户是否收藏了此书
    private int favorite;
    //借书人
    private String borrowPerson;
    //是否未归还
    private int isInHand;
    private String onlineUrl;
    public BookDTO(){
        super();
    }
    public BookDTO(String code,String msg){
        super(code,msg);
    }
    public BookDTO(RetCodeEnum e){
        super(e);
    }

}
