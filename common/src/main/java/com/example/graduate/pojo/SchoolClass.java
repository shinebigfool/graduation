package com.example.graduate.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class SchoolClass {
    @TableId(type = IdType.AUTO)
    private int id;
    private String classId;
    private String className;
    private String classGrade;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createTime;
}
