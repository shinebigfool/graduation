package com.example.graduate.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
@TableName("affair_log")
public class AffairLog {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer affairId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date openTime;
    private String operateAccount;
    private Integer operateType;
    private String operateDetail;
}
