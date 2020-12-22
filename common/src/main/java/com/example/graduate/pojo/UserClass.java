package com.example.graduate.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class UserClass {
    @TableId(type = IdType.AUTO)
    private int id;
    private int cid;
    private int uid;
}
