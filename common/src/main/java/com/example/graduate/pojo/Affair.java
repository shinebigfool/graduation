package com.example.graduate.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
@TableName("affair")
public class Affair {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String applyUser;
    private String operateUser;
    private Integer affairType;
    private Integer affairDetail;
    private String bookInfo;
    private Integer state;
    private Integer bid;
    private String brokenPic;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date openTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    private String note;
}
