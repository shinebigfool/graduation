package com.example.graduate.dto;

import com.example.graduate.codeEnum.RetCodeEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class SchoolClassDTO extends DTO{
    private int id;
    private String classId;
    private String className;
    private String classGrade;
    private int userAmount;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createTime;
    public SchoolClassDTO(){
        super();
    }
    public SchoolClassDTO(RetCodeEnum e){
        super(e);
    }
    public SchoolClassDTO(String code,String msg){
        super(code,msg);
    }
}
