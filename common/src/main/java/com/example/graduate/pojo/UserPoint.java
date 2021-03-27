package com.example.graduate.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class UserPoint {
    @TableId
    private String name;
    private Integer point;
}
